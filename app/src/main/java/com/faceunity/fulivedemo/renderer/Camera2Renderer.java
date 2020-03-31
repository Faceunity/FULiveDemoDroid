package com.faceunity.fulivedemo.renderer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.MeteringRectangle;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;

import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.gles.core.GlUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * 针对 Camera2 API 的渲染封装
 * refers https://github.com/Shouheng88/CameraX
 *
 * @author Richie on 2019.08.19
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Renderer extends BaseCameraRenderer implements ImageReader.OnImageAvailableListener {
    private static final String TAG = "Camera2Renderer";
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;
    private String mFrontCameraId;
    private String mBackCameraId;
    private CameraCharacteristics mFrontCameraCharacteristics;
    private CameraCharacteristics mBackCameraCharacteristics;
    private CaptureRequest.Builder mCaptureRequestBuilder;
    private CameraManager mCameraManager;
    private ImageReader mImageReader;
    private byte[] mYDataBuffer;
    private byte[] mYuvDataBuffer;
    private byte[][] mYuvDataBufferArray;
    private int mYuvDataBufferPosition;
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureSequenceCompleted(CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
            Log.v(TAG, "onCaptureSequenceCompleted() called with: session = [" + session + "], sequenceId = [" + sequenceId + "], frameNumber = [" + frameNumber + "]");
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, null);
        }
    };

    public Camera2Renderer(Activity activity, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        super(activity, glSurfaceView, onRendererStatusListener);
    }

    // this method may cost a lot of time
    @Override
    protected void initCameraInfo() {
        mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] ids = mCameraManager.getCameraIdList();
            if (ids.length <= 0) {
                throw new RuntimeException("No camera");
            }

            for (String id : ids) {
                CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null) {
                    if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
                        mFrontCameraId = id;
                        Integer iFrontCameraOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        mFrontCameraOrientation = iFrontCameraOrientation == null ? FRONT_CAMERA_ORIENTATION : iFrontCameraOrientation;
                        mFrontCameraCharacteristics = characteristics;
                    } else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
                        mBackCameraId = id;
                        Integer iBackCameraOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        mBackCameraOrientation = iBackCameraOrientation == null ? BACK_CAMERA_ORIENTATION : iBackCameraOrientation;
                        mBackCameraCharacteristics = characteristics;
                    }
                }
            }
        } catch (CameraAccessException | IllegalArgumentException e) {
            Log.e(TAG, "initCameraInfo: ", e);
        }
        mCameraOrientation = mCameraFacing == FACE_FRONT ? mFrontCameraOrientation : mBackCameraOrientation;
        Log.i(TAG, "initCameraInfo. frontCameraId:" + mFrontCameraId + ", frontCameraOrientation:"
                + mFrontCameraOrientation + ", backCameraId:" + mBackCameraId + ", mBackCameraOrientation:"
                + mBackCameraOrientation);
    }

    @Override
    protected void openCamera(int cameraFacing) {
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Camera Permission Denied");
        }

        try {
            String cameraId = cameraFacing == FACE_FRONT ? mFrontCameraId : mBackCameraId;
            CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (streamConfigurationMap != null) {
                Size[] outputSizes = streamConfigurationMap.getOutputSizes(SurfaceTexture.class);
                Size size = CameraUtils.chooseOptimalSize(outputSizes, mCameraWidth, mCameraHeight,
                        1920, 1080, new Size(mCameraWidth, mCameraHeight));
                mCameraWidth = size.getWidth();
                mCameraHeight = size.getHeight();
            }
            Log.i(TAG, "openCamera. facing:" + (mCameraFacing == FACE_FRONT ? "front" : "back")
                    + ", orientation:" + mCameraOrientation + ", previewWidth:" + mCameraWidth
                    + ", previewHeight:" + mCameraHeight + ", thread:" + Thread.currentThread().getName());
            mYuvDataBufferArray = new byte[PREVIEW_BUFFER_SIZE][mCameraWidth * mCameraHeight * ImageFormat.getBitsPerPixel(ImageFormat.YUV_420_888) / 8];
            mImageReader = ImageReader.newInstance(mCameraWidth, mCameraHeight, ImageFormat.YUV_420_888, PREVIEW_BUFFER_SIZE);
            mImageReader.setOnImageAvailableListener(this, mBackgroundHandler);
            mCameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    Log.d(TAG, "onCameraOpened: " + camera + ", thread:" + Thread.currentThread().getName());
                    mCameraDevice = camera;
                    if (mViewWidth > 0 && mViewHeight > 0) {
                        mMvpMatrix = GlUtil.changeMVPMatrixCrop(mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
                    }
                    startPreview();
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    Log.d(TAG, "onCameraDisconnected: " + camera);
                    camera.close();
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    Log.e(TAG, "onOpenCameraError: " + error);
                    camera.close();
                    mCameraDevice = null;
                }
            }, mBackgroundHandler);
        } catch (Exception e) {
            Log.e(TAG, "openCamera: ", e);
        }
    }

    @Override
    protected void closeCamera() {
        Log.d(TAG, "closeCamera. thread:" + Thread.currentThread().getName());
        if (mCameraCaptureSession != null) {
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        mIsPreviewing = false;
        super.closeCamera();
    }

    @Override
    public void changeResolution(final int cameraWidth, final int cameraHeight) {
        super.changeResolution(cameraWidth, cameraHeight);
        Log.d(TAG, "changeResolution() cameraWidth = [" + cameraWidth + "], cameraHeight = [" + cameraHeight + "]");
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsStopPreview = true;
                mIsSwitchCamera = true;
                mCameraWidth = cameraWidth;
                mCameraHeight = cameraHeight;
                mYDataBuffer = null;
                mYuvDataBuffer = null;
                mYuvDataBufferArray = null;
                closeCamera();
                openCamera(mCameraFacing);
                startPreview();
                mIsSwitchCamera = false;
                mIsStopPreview = false;
                mOnRendererStatusListener.onCameraChanged(mCameraFacing, mCameraOrientation);
            }
        });
    }

    @Override
    protected void startPreview() {
        if (mCameraTexId <= 0 || mCameraDevice == null || mIsPreviewing) {
            return;
        }
        mIsPreviewing = true;
        Log.i(TAG, "startPreview. cameraTexId:" + mCameraTexId + ", cameraDevice:" + mCameraDevice);

        mSurfaceTexture = new SurfaceTexture(mCameraTexId);
        mSurfaceTexture.setDefaultBufferSize(mCameraWidth, mCameraHeight);
        showImageTexture(mShotBitmap);
        try {
            Range<Integer> rangeFps = getBestRange();
            Log.d(TAG, "startPreview. rangeFPS: " + rangeFps);
            CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            if (rangeFps != null) {
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, rangeFps);
            }
            captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_LOCK, false);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CaptureRequest.CONTROL_AF_TRIGGER_IDLE);
            Surface previewSurface = new Surface(mSurfaceTexture);
            captureRequestBuilder.addTarget(previewSurface);
            Surface imageReaderSurface = mImageReader.getSurface();
            captureRequestBuilder.addTarget(imageReaderSurface);
            mCaptureRequestBuilder = captureRequestBuilder;
            List<Surface> surfaceList = Arrays.asList(previewSurface, imageReaderSurface);
            mCameraDevice.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    Log.d(TAG, "onConfigured: " + session + ", thread:" + Thread.currentThread().getName());
                    mCameraCaptureSession = session;
                    CaptureRequest captureRequest = mCaptureRequestBuilder.build();
                    try {
                        session.setRepeatingRequest(captureRequest, mCaptureCallback, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "setRepeatingRequest: ", e);
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    Log.w(TAG, "onConfigureFailed: " + session);
                    mIsPreviewing = false;
                }
            }, mBackgroundHandler);
        } catch (Exception e) {
            Log.e(TAG, "startPreview: ", e);
        }
    }

    // see https://stackoverflow.com/questions/41649691/android-camera2-api-touch-to-focus-example
    @Override
    public void handleFocus(float rawX, float rawY, int areaSize) {
        if (mCameraCaptureSession == null) {
            return;
        }
        if (!isMeteringAreaAFSupported()) {
            Log.e(TAG, "handleFocus not supported");
            return;
        }

        CameraCharacteristics cameraCharacteristics = getCurrentCameraInfo();
        final Rect sensorArraySize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        //here I just flip x,y, but this needs to correspond with the sensor orientation (via SENSOR_ORIENTATION)
        final int y = (int) ((rawX / mViewWidth) * (float) sensorArraySize.height());
        final int x = (int) ((rawY / mViewHeight) * (float) sensorArraySize.width());
        final int halfTouchWidth = areaSize / 2;
        final int halfTouchHeight = areaSize / 2;
        MeteringRectangle focusAreaTouch = new MeteringRectangle(Math.max(x - halfTouchWidth, 0),
                Math.max(y - halfTouchHeight, 0),
                halfTouchWidth * 2,
                halfTouchHeight * 2,
                MeteringRectangle.METERING_WEIGHT_MAX - 1);

        try {
            mCameraCaptureSession.stopRepeating();
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_IDLE);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_OFF);
            MeteringRectangle[] meteringRectangles = new MeteringRectangle[]{focusAreaTouch};
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, meteringRectangles);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_AUTO);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
            mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "setExposureCompensation: ", e);
        }
    }

    @Override
    public float getExposureCompensation() {
        CameraCharacteristics cameraCharacteristics = getCurrentCameraInfo();
        Range<Integer> range = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        int min = -1;
        int max = 1;
        if (range != null) {
            min = range.getLower();
            max = range.getUpper();
        }
        Integer progressI = mCaptureRequestBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION);
        int progress = 0;
        if (progressI != null) {
            progress = progressI;
        }
        return (float) (progress - min) / (max - min);
    }

    @Override
    public void setExposureCompensation(float value) {
        if (mCameraCaptureSession == null) {
            return;
        }
        CameraCharacteristics cameraCharacteristics = getCurrentCameraInfo();
        Range<Integer> range = cameraCharacteristics.get(CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE);
        if (range != null) {
            int min = range.getLower();
            int max = range.getUpper();
            int val = (int) (value * (max - min) + min);
            mCaptureRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, val);
            try {
                mCameraCaptureSession.setRepeatingRequest(mCaptureRequestBuilder.build(), mCaptureCallback, mBackgroundHandler);
            } catch (CameraAccessException e) {
                Log.e(TAG, "setExposureCompensation: ", e);
            }
        }
    }

    @Override
    public void onImageAvailable(ImageReader reader) {
        // called on CameraRenderer thread
        try (Image image = reader.acquireLatestImage()) {
            if (image == null) {
                return;
            }
            if (!mIsStopPreview) {
                mYuvDataBuffer = mYuvDataBufferArray[mYuvDataBufferPosition];
                mYuvDataBufferPosition = ++mYuvDataBufferPosition % mYuvDataBufferArray.length;
                YUV420ToNV21(image);
                synchronized (mLock) {
                    mCameraNV21Byte = mYuvDataBuffer;
                }
                mGlSurfaceView.requestRender();
            }
        } catch (Exception e) {
            Log.e(TAG, "onImageAvailable: ", e);
        }
    }

    private boolean isMeteringAreaAFSupported() {
        Integer masRegionsAF = getCurrentCameraInfo().get(CameraCharacteristics.CONTROL_MAX_REGIONS_AF);
        if (masRegionsAF != null) {
            return masRegionsAF >= 1;
        } else {
            return false;
        }
    }

    private CameraCharacteristics getCurrentCameraInfo() {
        return mCameraFacing == FACE_FRONT ? mFrontCameraCharacteristics : mBackCameraCharacteristics;
    }

    // call this method may cost 5+ms
    private void YUV420ToNV21(Image image) {
        Rect crop = image.getCropRect();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        if (mYDataBuffer == null) {
            mYDataBuffer = new byte[planes[0].getRowStride()];
        }
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    channelOffset = width * height + 1;
                    outputStride = 2;
                    break;
                case 2:
                    channelOffset = width * height;
                    outputStride = 2;
                    break;
                default:
            }

            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(mYuvDataBuffer, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(mYDataBuffer, 0, length);
                    for (int col = 0; col < w; col++) {
                        mYuvDataBuffer[channelOffset] = mYDataBuffer[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
        }
    }

    private Range<Integer> getBestRange() {
        Range<Integer> result = null;
        try {
            String cameraId = mCameraFacing == FACE_FRONT ? mFrontCameraId : mBackCameraId;
            CameraCharacteristics chars = mCameraManager.getCameraCharacteristics(cameraId);
            Range<Integer>[] ranges = chars.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
            if (ranges != null) {
                for (Range<Integer> range : ranges) {
                    //帧率不能太低，大于10
                    if (range.getLower() < 10) {
                        continue;
                    }
                    if (result == null) {
                        result = range;
                    }
                    //FPS下限小于15，弱光时能保证足够曝光时间，提高亮度。range范围跨度越大越好，光源足够时FPS较高，预览更流畅，光源不够时FPS较低，亮度更好。
                    else if (range.getLower() <= 15 && (range.getUpper() - range.getLower()) > (result.getUpper() - result.getLower())) {
                        result = range;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getBestRange: ", e);
        }
        return result;
    }

}