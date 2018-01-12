package com.faceunity.fulivedemo;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.faceunity.fulivedemo.encoder.TextureMovieEncoder;
import com.faceunity.fulivedemo.gles.FullFrameRect;
import com.faceunity.fulivedemo.gles.LandmarksPoints;
import com.faceunity.fulivedemo.gles.Texture2dProgram;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.fulivedemo.utils.MiscUtil;
import com.faceunity.fulivedemo.view.AspectFrameLayout;
import com.faceunity.fulivedemo.view.EffectAndFilterSelectAdapter;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.faceunity.fulivedemo.encoder.TextureMovieEncoder.IN_RECORDING;
import static com.faceunity.fulivedemo.encoder.TextureMovieEncoder.START_RECORDING;


/**
 * 这个Activity演示了faceunity的使用
 * 本demo中draw方法要求在子类集成实现绘制方法（便于不同绘制方法的展示），实际使用中draw无需在子类实现。
 * <p>
 * Created by lirui on 2016/12/13.
 */

@SuppressWarnings("deprecation")
public abstract class FUExampleActivity extends FUBaseUIActivity
        implements Camera.PreviewCallback {


    protected abstract int draw(byte[] cameraNV21Byte, byte[] fuImgNV21Bytes, int cameraTextureId, int cameraWidth, int cameraHeight, int frameId, int[] ints, int currentCameraType);

    public final static String TAG = FUExampleActivity.class.getSimpleName();

    private FUExampleActivity mContext;

    private GLSurfaceView mGLSurfaceView;
    private GLRenderer mGLRenderer;
    private int mViewWidth;
    private int mViewHeight;

    private Camera mCamera;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraOrientation;
    private int mCameraWidth = 1280;
    private int mCameraHeight = 720;

    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;

    private byte[] mCameraNV21Byte;
    private byte[] mFuImgNV21Bytes;

    private int mFrameId = 0;

    private int mFaceBeautyItem = 0; //美颜道具
    private int mEffectItem = 0; //贴纸道具

    private float mFilterLevel = 1.0f;
    private float mFaceBeautyColorLevel = 0.2f;
    private float mFaceBeautyBlurLevel = 6.0f;
    private float mFaceBeautyALLBlurLevel = 0.0f;
    private float mFaceBeautyCheekThin = 1.0f;
    private float mFaceBeautyEnlargeEye = 0.5f;
    private float mFaceBeautyRedLevel = 0.5f;
    private int mFaceShape = 3;
    private float mFaceShapeLevel = 0.5f;

    private String mFilterName = EffectAndFilterSelectAdapter.FILTERS_NAME[0];

    private boolean isNeedEffectItem = true;
    private String mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

    private TextureMovieEncoder mTextureMovieEncoder;
    private String mVideoFileName;

    private HandlerThread mCreateItemThread;
    private Handler mCreateItemHandler;

    private boolean isInPause = true;

    private boolean isInAvatarMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        mContext = this;
        super.onCreate(savedInstanceState);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.glsv);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLRenderer = new GLRenderer();
        mGLSurfaceView.setRenderer(mGLRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mCreateItemThread = new HandlerThread("CreateItemThread");
        mCreateItemThread.start();
        mCreateItemHandler = new CreateItemHandler(mCreateItemThread.getLooper());
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");

        super.onResume();

        openCamera(mCurrentCameraType, mCameraWidth, mCameraHeight);

        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");

        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);

        releaseCamera();

        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mGLRenderer.notifyPause();
                mGLRenderer.destroySurfaceTexture();

                mEffectItem = 0;
                mFaceBeautyItem = 0;
                //Note: 切忌使用一个已经destroy的item
                faceunity.fuDestroyAllItems();
                isNeedEffectItem = true;
                faceunity.fuOnDeviceLost();
                mFrameId = 0;
            }
        });

        mGLSurfaceView.onPause();

        FPSUtil.reset();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

        mCreateItemThread.quitSafely();
        mCreateItemThread = null;
        mCreateItemHandler = null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        isInPause = false;
    }

    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mCameraDisplay;

        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        int faceTrackingStatus = 0;
        int systemErrorStatus = 0;//success number
        float[] isCalibrating = new float[1];

        LandmarksPoints landmarksPoints;
        float[] landmarksData = new float[150];
        float[] expressionData = new float[46];
        float[] rotationData = new float[4];
        float[] pupilPosData = new float[2];
        float[] rotationModeData = new float[1];

        int fuTex;
        final float[] mtx = new float[16];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.e(TAG, "onSurfaceCreated fu version " + faceunity.fuGetVersion());

            mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_2D));
            mCameraDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_EXT));
            mCameraTextureId = mCameraDisplay.createTextureObject();

            landmarksPoints = new LandmarksPoints();//如果有证书权限可以获取到的话，绘制人脸特征点

            switchCameraSurfaceTexture();

            try {
                InputStream is = getAssets().open("v3.bundle");
                byte[] v3data = new byte[is.available()];
                int len = is.read(v3data);
                is.close();
                faceunity.fuSetup(v3data, null, authpack.A());
                //faceunity.fuSetMaxFaces(1);//设置最大识别人脸数目
                Log.e(TAG, "fuSetup v3 len " + len);

                is = getAssets().open("anim_model.bundle");
                byte[] animModelData = new byte[is.available()];
                is.read(animModelData);
                is.close();
                faceunity.fuLoadAnimModel(animModelData);
                faceunity.fuSetExpressionCalibration(1);

                is = getAssets().open("face_beautification.bundle");
                byte[] itemData = new byte[is.available()];
                len = is.read(itemData);
                Log.e(TAG, "beautification len " + len);
                is.close();
                mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.e(TAG, "onSurfaceChanged " + width + " " + height);
            GLES20.glViewport(0, 0, width, height);
            mViewWidth = width;
            mViewHeight = height;
        }

        @Override
        public void onDrawFrame(GL10 gl) {

            FPSUtil.fps(TAG);

            if (isInPause) {
                mFullScreenFUDisplay.drawFrame(fuTex, mtx);
                mGLSurfaceView.requestRender();
                return;
            }

            /**
             * 获取camera数据, 更新到texture
             */
            try {
                mCameraSurfaceTexture.updateTexImage();
                mCameraSurfaceTexture.getTransformMatrix(mtx);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final int isTracking = faceunity.fuIsTracking();
            if (isTracking != faceTrackingStatus) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTracking == 0) {
                            mFaceTrackingStatusImageView.setVisibility(View.VISIBLE);
                            Arrays.fill(landmarksData, 0);
                        } else {
                            mFaceTrackingStatusImageView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                faceTrackingStatus = isTracking;
            }

            final int systemError = faceunity.fuGetSystemError();
            if (systemError != systemErrorStatus) {
                systemErrorStatus = systemError;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "system error " + systemError + " " + faceunity.fuGetSystemErrorString(systemError));
                        tvSystemError.setText(faceunity.fuGetSystemErrorString(systemError));
                    }
                });
            }

            if (isNeedEffectItem) {
                isNeedEffectItem = false;
                mCreateItemHandler.sendMessage(Message.obtain(mCreateItemHandler, CreateItemHandler.HANDLE_CREATE_ITEM, mEffectFileName));
            }

            faceunity.fuItemSetParam(mFaceBeautyItem, "filter_level", mFilterLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "skin_detect", mFaceBeautyALLBlurLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
            faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
            faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
            faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
            faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);

            if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
                Log.e(TAG, "camera nv21 bytes null");
                mGLSurfaceView.requestRender();
                return;
            }

            if (isInAvatarMode) {
                /**
                 * Avatar道具推荐使用 fuTrackFace 与 fuAvatarToTexture 的api组合
                 */
                fuTex = drawAvatar();
            } else {
                fuTex = draw(mCameraNV21Byte, mFuImgNV21Bytes, mCameraTextureId, mCameraWidth, mCameraHeight, mFrameId++, new int[]{mFaceBeautyItem, mEffectItem}, mCurrentCameraType);
            }

            mFullScreenFUDisplay.drawFrame(fuTex, mtx);

            /**
             * 绘制Avatar模式下的镜头内容以及landmarks
             **/
            if (isInAvatarMode) {
                int[] originalViewport = new int[4];
                GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, originalViewport, 0);
                GLES20.glViewport(0, mViewHeight * 2 / 3, mViewWidth / 3, mViewHeight / 3);
                mCameraDisplay.drawFrame(mCameraTextureId, mtx);
                landmarksPoints.draw();
                GLES20.glViewport(originalViewport[0], originalViewport[1], originalViewport[2], originalViewport[3]);
            }

            final float[] isCalibratingTmp = new float[1];
            faceunity.fuGetFaceInfo(0, "is_calibrating", isCalibratingTmp);
            if (isCalibrating[0] != isCalibratingTmp[0]) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isCalibratingText.setVisibility((isCalibrating[0] = isCalibratingTmp[0]) == 1 ? View.VISIBLE : View.GONE);
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(START_RECORDING)) {
                mVideoFileName = MiscUtil.createFileName() + "_camera.mp4";
                File outFile = new File(mVideoFileName);
                mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile, mCameraHeight, mCameraWidth,
                        3000000, EGL14.eglGetCurrentContext(), mCameraSurfaceTexture.getTimestamp()
                ));
                mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, mtx);
                //forbid click until start or stop success
                mTextureMovieEncoder.setOnEncoderStatusUpdateListener(new TextureMovieEncoder.OnEncoderStatusUpdateListener() {
                    @Override
                    public void onStartSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "start encoder success");
                                mRecordingBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onStopSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e(TAG, "stop encoder success");
                                mRecordingBtn.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "video file saved to "
                                + mVideoFileName, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                mTextureMovieEncoder.setTextureId(mFullScreenFUDisplay, fuTex, mtx);
                mTextureMovieEncoder.frameAvailable(mCameraSurfaceTexture);
            }

            mGLSurfaceView.requestRender();
        }

        int drawAvatar() {
            faceunity.fuTrackFace(mCameraNV21Byte, 0, mCameraWidth, mCameraHeight);

            /**
             * landmarks
             */
            Arrays.fill(landmarksData, 0.0f);
            faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
            if (landmarksPoints != null) {
                landmarksPoints.refresh(landmarksData, mCameraWidth, mCameraHeight, mCameraOrientation, mCurrentCameraType);
            }

            /**
             *rotation
             */
            Arrays.fill(rotationData, 0.0f);
            faceunity.fuGetFaceInfo(0, "rotation", rotationData);
            /**
             * expression
             */
            Arrays.fill(expressionData, 0.0f);
            faceunity.fuGetFaceInfo(0, "expression", expressionData);

            /**
             * pupil pos
             */
            Arrays.fill(pupilPosData, 0.0f);
            faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);

            /**
             * rotation mode
             */
            Arrays.fill(rotationModeData, 0.0f);
            faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);

            int isTracking = faceunity.fuIsTracking();

            //rotation 是一个4元数，如果还没获取到，就使用1,0,0,0
            if (isTracking <= 0) {
                rotationData[3] = 1.0f;
            }

            /**
             * adjust rotation mode
             */
            if (isTracking <= 0) {
                rotationModeData[0] = (360 - mCameraOrientation) / 90;
            }

            return faceunity.fuAvatarToTexture(pupilPosData,
                    expressionData,
                    rotationData,
                    rotationModeData,
                        /*flags*/0,
                    mCameraWidth,
                    mCameraHeight,
                    mFrameId++,
                    new int[]{mEffectItem},
                    isTracking);
        }

        public void switchCameraSurfaceTexture() {
            Log.e(TAG, "switchCameraSurfaceTexture");
            if (mCameraSurfaceTexture != null) {
                faceunity.fuOnCameraChange();
                destroySurfaceTexture();
            }
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleCameraStartPreview(mCameraSurfaceTexture);
                }
            });
        }

        public void notifyPause() {
            faceTrackingStatus = 0;

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecordingBtn.performClick();
                    }
                });
            }

            if (mFullScreenFUDisplay != null) {
                mFullScreenFUDisplay.release(false);
                mFullScreenFUDisplay = null;
            }

            if (mCameraDisplay != null) {
                mCameraDisplay.release(false);
                mCameraDisplay = null;
            }
        }

        public void destroySurfaceTexture() {
            if (mCameraSurfaceTexture != null) {
                mCameraSurfaceTexture.release();
                mCameraSurfaceTexture = null;
            }
        }
    }

    class CreateItemHandler extends Handler {

        static final int HANDLE_CREATE_ITEM = 1;

        CreateItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_CREATE_ITEM:
                    try {
                        final String effectFileName = (String) msg.obj;
                        final int newEffectItem;
                        if (effectFileName.equals("none")) {
                            newEffectItem = 0;
                        } else {
                            InputStream is = mContext.getAssets().open(effectFileName);
                            byte[] itemData = new byte[is.available()];
                            int len = is.read(itemData);
                            Log.e(TAG, "effect len " + len);
                            is.close();
                            newEffectItem = faceunity.fuCreateItemFromPackage(itemData);
                            mGLSurfaceView.queueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    faceunity.fuItemSetParam(newEffectItem, "isAndroid", 1.0);
                                    faceunity.fuItemSetParam(newEffectItem, "rotationAngle", 360 - mCameraOrientation);
                                }
                            });
                        }
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mEffectItem != 0 && mEffectItem != newEffectItem) {
                                    faceunity.fuDestroyItem(mEffectItem);
                                }
                                isInAvatarMode = Arrays.asList(EffectAndFilterSelectAdapter.AVATAR_EFFECT).contains(effectFileName);
                                mEffectItem = newEffectItem;
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void openCamera(int cameraType, int desiredWidth, int desiredHeight) {
        Log.e(TAG, "openCamera");

        if (mCamera != null) {
            throw new RuntimeException("camera already initialized");
        }

        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 0;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraType) {
                cameraId = i;
                mCamera = Camera.open(i);
                mCurrentCameraType = cameraType;
                break;
            }
        }
        if (mCamera == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FUExampleActivity.this,
                            "Open Camera Failed! Make sure it is not locked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
            throw new RuntimeException("unable to open camera");
        }

        mCameraOrientation = CameraUtils.getCameraOrientation(cameraId);
        CameraUtils.setCameraDisplayOrientation(this, cameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();

        CameraUtils.setFocusModes(parameters);

        int[] size = CameraUtils.choosePreviewSize(parameters, desiredWidth, desiredHeight);
        mCameraWidth = size[0];
        mCameraHeight = size[1];

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
                aspectFrameLayout.setAspectRatio(1.0f * mCameraHeight / mCameraWidth);
            }
        });

        mCamera.setParameters(parameters);
    }

    /**
     * set preview and start preview after the surface created
     */
    private void handleCameraStartPreview(SurfaceTexture surfaceTexture) {
        Log.e(TAG, "handleCameraStartPreview");

        if (previewCallbackBuffer == null) {
            Log.e(TAG, "allocate preview callback buffer");
            previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
            mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void releaseCamera() {
        Log.e(TAG, "release camera");
        isInPause = true;

        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        isInPause = true;
    }

    @Override
    protected void onCameraChange() {
        if (isInPause) {
            return;
        }

        Log.e(TAG, "onCameraChange");

        releaseCamera();

        mCameraNV21Byte = null;
        mFrameId = 0;

        if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, mCameraWidth, mCameraHeight);
        } else {
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, mCameraWidth, mCameraHeight);
        }

        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mGLRenderer.switchCameraSurfaceTexture();
                faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);
                faceunity.fuItemSetParam(mEffectItem, "rotationAngle", 360 - mCameraOrientation);
            }
        });
    }

    @Override
    protected void onStartRecording() {
        MiscUtil.Logger(TAG, "start recording", false);
        mTextureMovieEncoder = new TextureMovieEncoder();
    }

    @Override
    protected void onStopRecording() {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
            MiscUtil.Logger(TAG, "stop recording", false);
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mTextureMovieEncoder.stopRecording();
                }
            });
        }
    }

    @Override
    protected void onBlurLevelSelected(int level) {
        mFaceBeautyBlurLevel = level;
    }

    @Override
    protected void onALLBlurLevelSelected(int isAll) {
        mFaceBeautyALLBlurLevel = isAll;
    }

    @Override
    protected void onCheekThinSelected(int progress, int max) {
        mFaceBeautyCheekThin = 1.0f * progress / max;
    }

    @Override
    protected void onColorLevelSelected(int progress, int max) {
        mFaceBeautyColorLevel = 1.0f * progress / max;
    }

    @Override
    protected void onEffectSelected(String effectItemName) {
        if (effectItemName.equals(mEffectFileName)) {
            return;
        }
        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);
        mEffectFileName = effectItemName;
        isNeedEffectItem = true;
    }

    @Override
    protected void onFilterLevelSelected(int progress, int max) {
        mFilterLevel = 1.0f * progress / max;
    }

    @Override
    protected void onEnlargeEyeSelected(int progress, int max) {
        mFaceBeautyEnlargeEye = 1.0f * progress / max;
    }

    @Override
    protected void onFilterSelected(String filterName) {
        mFilterName = filterName;
    }

    @Override
    protected void onRedLevelSelected(int progress, int max) {
        mFaceBeautyRedLevel = 1.0f * progress / max;
    }

    @Override
    protected void onFaceShapeLevelSelected(int progress, int max) {
        mFaceShapeLevel = (1.0f * progress) / max;
    }

    @Override
    protected void onFaceShapeSelected(int faceShape) {
        mFaceShape = faceShape;
    }

}
