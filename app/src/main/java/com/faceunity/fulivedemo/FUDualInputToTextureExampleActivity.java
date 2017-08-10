package com.faceunity.fulivedemo;

import android.content.Context;
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

import com.faceunity.fulivedemo.gles.CameraClipFrameRect;
import com.faceunity.fulivedemo.gles.FullFrameRect;
import com.faceunity.fulivedemo.gles.LandmarksPoints;
import com.faceunity.fulivedemo.gles.Texture2dProgram;
import com.faceunity.fulivedemo.encoder.TextureMovieEncoder;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.faceunity.fulivedemo.encoder.TextureMovieEncoder.IN_RECORDING;
import static com.faceunity.fulivedemo.encoder.TextureMovieEncoder.START_RECORDING;


/**
 * 这个Activity演示了从Camera取数据,用fuDualInputToTexure处理并预览展示
 * 所谓dual input，指从cpu和gpu同时拿数据，
 * cpu拿到的是nv21的byte数组，gpu拿到的是对应的texture
 * <p>
 * Created by lirui on 2016/12/13.
 */

@SuppressWarnings("deprecation")
public class FUDualInputToTextureExampleActivity extends FUBaseUIActivity
        implements Camera.PreviewCallback,
        SurfaceTexture.OnFrameAvailableListener {

    final static String TAG = "FUDualInputToTextureEg";

    Camera mCamera;

    GLSurfaceView glSf;
    GLRenderer glRenderer;

    Handler mMainHandler;

    int cameraWidth = 1280;
    int cameraHeight = 720;

    byte[] mCameraNV21Byte;
    byte[] fuImgNV21Bytes;

    int mFrameId = 0;

    static int mFaceBeautyItem = 0; //美颜道具
    static int mEffectItem = 0; //贴纸道具
    static int mGestureItem = 0; //手势道具
    static int[] itemsArray = {mFaceBeautyItem, mEffectItem, mGestureItem};

    long resumeTimeStamp;
    boolean isFirstOnFrameAvailable;
    long frameAvailableTimeStamp;

    boolean VERBOSE_LOG = false;

    float mFaceBeautyColorLevel = 0.2f;
    float mFaceBeautyBlurLevel = 6.0f;
    float mFaceBeautyCheekThin = 1.0f;
    float mFaceBeautyEnlargeEye = 0.5f;
    float mFaceBeautyRedLevel = 0.5f;
    int mFaceShape = 3;
    float mFaceShapeLevel = 0.5f;

    String mFilterName = EffectAndFilterSelectAdapter.FILTERS_NAME[0];

    boolean isNeedEffectItem = true;
    static String mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

    int mCurrentCameraType;

    boolean mUseBeauty = true;

    int cameraDataAlreadyCount = 0;
    final Object prepareCameraDataLock = new Object();
    boolean isNeedSwitchCameraSurfaceTexture = true;

    TextureMovieEncoder mTextureMovieEncoder;
    String videoFileName;

    boolean mUseGesture = false;

    HandlerThread mCreateItemThread;
    Handler mCreateItemHandler;

    Context mContext;

    boolean isBenchmarkFPS = true;
    boolean isBenchmarkTime = false;

    boolean isInPause = false;

    boolean isInAvatarMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        mContext = this;

        mMainHandler = new MainHandler(this);

        glSf = (GLSurfaceView) findViewById(R.id.glsv);
        glSf.setEGLContextClientVersion(2);
        glRenderer = new GLRenderer();
        glSf.setRenderer(glRenderer);
        glSf.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mCreateItemThread = new HandlerThread("CreateItemThread");
        mCreateItemThread.start();
        mCreateItemHandler = new CreateItemHandler(mCreateItemThread.getLooper(), mContext);
    }

    final int PREVIEW_BUFFER_COUNT = 3;
    byte[][] previewCallbackBuffer;

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume");

        resumeTimeStamp = System.nanoTime();
        isFirstOnFrameAvailable = true;

        isInPause = false;

        super.onResume();

        openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT,
                cameraWidth,
                cameraHeight);

        /**
         * 请注意这个地方, camera返回的图像并不一定是设置的大小（因为可能并不支持）
         */
        Camera.Size size = mCamera.getParameters().getPreviewSize();
        cameraWidth = size.width;
        cameraHeight = size.height;
        Log.e(TAG, "open camera size width : " + size.width + " height : " + size.height);

        AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
        aspectFrameLayout.setAspectRatio(1.0f * cameraHeight / cameraWidth);

        glSf.onResume();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");

        isInPause = true;

        super.onPause();

        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);

        releaseCamera();

        glSf.queueEvent(new Runnable() {
            @Override
            public void run() {
                glRenderer.notifyPause();
                glRenderer.destroySurfaceTexture();

                itemsArray[1] = mEffectItem = 0;
                itemsArray[0] = mFaceBeautyItem = 0;
                //Note: 切忌使用一个已经destroy的item
                faceunity.fuDestroyAllItems();
                //faceunity.fuDestroyItem(mEffectItem);
                //faceunity.fuDestroyItem(mFaceBeautyItem);
                isNeedEffectItem = true;
                faceunity.fuOnDeviceLost();
                // faceunity.fuClearReadbackRelated();
                mFrameId = 0;
            }
        });

        glSf.onPause();

        lastOneHundredFrameTimeStamp = 0;
        oneHundredFrameFUTime = 0;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (VERBOSE_LOG) {
            Log.e(TAG, "onPreviewFrame len " + data.length);
            Log.e(TAG, "onPreviewThread " + Thread.currentThread());
        }
        mCameraNV21Byte = isInPause ? null : data;
        mCamera.addCallbackBuffer(data);
        synchronized (prepareCameraDataLock) {
            cameraDataAlreadyCount++;
            prepareCameraDataLock.notify();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (isFirstOnFrameAvailable) {
            frameAvailableTimeStamp = System.nanoTime();
            isFirstOnFrameAvailable = false;
            Log.e(TAG, "first frame available time cost " +
                    (frameAvailableTimeStamp - resumeTimeStamp) / MiscUtil.NANO_IN_ONE_MILLI_SECOND);
        }
        if (VERBOSE_LOG) {
            Log.e(TAG, "onFrameAvailable");
        }
        synchronized (prepareCameraDataLock) {
            cameraDataAlreadyCount++;
            prepareCameraDataLock.notify();
        }
    }

    /**
     * set preview and start preview after the surface created
     */
    private void handleCameraStartPreview(SurfaceTexture surfaceTexture) {
        Log.e(TAG, "handleCameraStartPreview");

        if (previewCallbackBuffer == null) {
            Log.e(TAG, "allocate preview callback buffer");
            previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][cameraWidth * cameraHeight * 3 / 2];
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
            mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        surfaceTexture.setOnFrameAvailableListener(this);
        mCamera.startPreview();
    }

    long lastOneHundredFrameTimeStamp = 0;
    int currentFrameCnt = 0;
    long oneHundredFrameFUTime = 0;

    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mFullScreenCamera;

        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        boolean isFirstCameraOnDrawFrame;

        int faceTrackingStatus = 0;

        CameraClipFrameRect cameraClipFrameRect;

        LandmarksPoints landmarksPoints;
        float[] landmarksData = new float[150];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.e(TAG, "onSurfaceCreated fu version " + faceunity.fuGetVersion());

            mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_2D));
            mFullScreenCamera = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_EXT));
            mCameraTextureId = mFullScreenCamera.createTextureObject();

            cameraClipFrameRect = new CameraClipFrameRect(0.4f, 0.4f * 0.8f); //clip 20% vertical
            landmarksPoints = new LandmarksPoints();//如果有证书权限可以获取到的话，绘制人脸特征点

            switchCameraSurfaceTexture();

            try {
                InputStream is = getAssets().open("v3.mp3");
                byte[] v3data = new byte[is.available()];
                int len = is.read(v3data);
                is.close();
                faceunity.fuSetup(v3data, null, authpack.A());
                //faceunity.fuSetMaxFaces(1);//设置最大识别人脸数目
                Log.e(TAG, "fuSetup v3 len " + len);

                if (mUseBeauty) {
                    is = getAssets().open("face_beautification.mp3");
                    byte[] itemData = new byte[is.available()];
                    len = is.read(itemData);
                    Log.e(TAG, "beautification len " + len);
                    is.close();
                    mFaceBeautyItem = faceunity.fuCreateItemFromPackage(itemData);
                    itemsArray[0] = mFaceBeautyItem;
                }

                if (mUseGesture) {
                    is = getAssets().open("heart.mp3");
                    byte[] itemData = new byte[is.available()];
                    len = is.read(itemData);
                    Log.e(TAG, "heart len " + len);
                    is.close();
                    mGestureItem = faceunity.fuCreateItemFromPackage(itemData);
                    itemsArray[2] = mGestureItem;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            isFirstCameraOnDrawFrame = true;
        }

        public void switchCameraSurfaceTexture() {
            Log.e(TAG, "switchCameraSurfaceTexture");
            isNeedSwitchCameraSurfaceTexture = false;
            if (mCameraSurfaceTexture != null) {
                faceunity.fuOnCameraChange();
                destroySurfaceTexture();
            }
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            Log.e(TAG, "send start camera message");
            mMainHandler.sendMessage(mMainHandler.obtainMessage(
                    MainHandler.HANDLE_CAMERA_START_PREVIEW,
                    mCameraSurfaceTexture));
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.e(TAG, "onSurfaceChanged " + width + " " + height);
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (VERBOSE_LOG) {
                Log.e(TAG, "onDrawFrame");
            }

            if (isInPause) {
                //glSf.requestRender();
                return;
            }

            if (isNeedSwitchCameraSurfaceTexture) {
                switchCameraSurfaceTexture();
            }

            if (VERBOSE_LOG) {
                Log.e(TAG, "after switchCameraSurfaceTexture");
            }

            /**
             * If camera texture data not ready there will be low possibility in meizu note3 which maybe causing black screen.
             */
            while (cameraDataAlreadyCount < 2) {
                if (VERBOSE_LOG) {
                    Log.e(TAG, "while cameraDataAlreadyCount < 2");
                }
                if (isFirstCameraOnDrawFrame) {
                    glSf.requestRender();
                    return;
                }
                synchronized (prepareCameraDataLock) {
                    //block until new camera frame comes.
                    try {
                        prepareCameraDataLock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            isFirstCameraOnDrawFrame = false;

            if (++currentFrameCnt == 100) {
                currentFrameCnt = 0;
                long tmp = System.nanoTime();
                if (isBenchmarkFPS)
                    Log.e(TAG, "dualInput FPS : " + (1000.0f * MiscUtil.NANO_IN_ONE_MILLI_SECOND / ((tmp - lastOneHundredFrameTimeStamp) / 100.0f)));
                lastOneHundredFrameTimeStamp = tmp;
                if (isBenchmarkTime)
                    Log.e(TAG, "dualInput cost time avg : " + oneHundredFrameFUTime / 100.f / MiscUtil.NANO_IN_ONE_MILLI_SECOND);
                oneHundredFrameFUTime = 0;
            }

            /**
             * 获取camera数据, 更新到texture
             */
            float[] mtx = new float[16];
            if (mCameraSurfaceTexture != null) {
                try {
                    mCameraSurfaceTexture.updateTexImage();
                    mCameraSurfaceTexture.getTransformMatrix(mtx);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else throw new RuntimeException("HOW COULD IT HAPPEN!!! mCameraSurfaceTexture is null!!!");

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
            if (VERBOSE_LOG) {
                Log.e(TAG, "isTracking " + isTracking);
            }

            if (isNeedEffectItem) {
                isNeedEffectItem = false;
                mCreateItemHandler.sendEmptyMessage(CreateItemHandler.HANDLE_CREATE_ITEM);
            }

            faceunity.fuItemSetParam(mFaceBeautyItem, "color_level", mFaceBeautyColorLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "blur_level", mFaceBeautyBlurLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "filter_name", mFilterName);
            faceunity.fuItemSetParam(mFaceBeautyItem, "cheek_thinning", mFaceBeautyCheekThin);
            faceunity.fuItemSetParam(mFaceBeautyItem, "eye_enlarging", mFaceBeautyEnlargeEye);
            faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape", mFaceShape);
            faceunity.fuItemSetParam(mFaceBeautyItem, "face_shape_level", mFaceShapeLevel);
            faceunity.fuItemSetParam(mFaceBeautyItem, "red_level", mFaceBeautyRedLevel);

            faceunity.fuItemSetParam(mEffectItem, "rotationAngle", mCurrentCameraType== Camera.CameraInfo.CAMERA_FACING_FRONT ? 90 : 270);

            //faceunity.fuItemSetParam(mFaceBeautyItem, "use_old_blur", 1);

            if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
                Log.e(TAG, "camera nv21 bytes null");
                glSf.requestRender();
                glSf.requestRender();
                return;
            }

            boolean isOESTexture = true; //Tip: camera texture类型是默认的是OES的，和texture 2D不同
            int flags = isOESTexture ? faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE : 0;
            boolean isNeedReadBack = false; //是否需要写回，如果是，则入参的byte[]会被修改为带有fu特效的；支持写回自定义大小的内存数组中，即readback custom img
            flags = isNeedReadBack ? flags | faceunity.FU_ADM_FLAG_ENABLE_READBACK : flags;
            if (isNeedReadBack) {
                if (fuImgNV21Bytes == null) {
                    fuImgNV21Bytes = new byte[mCameraNV21Byte.length];
                }
                System.arraycopy(mCameraNV21Byte, 0, fuImgNV21Bytes, 0, mCameraNV21Byte.length);
            } else {
                fuImgNV21Bytes = mCameraNV21Byte;
            }
            flags |= mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X;

            long fuStartTime = System.nanoTime();
            /*
             * 这里拿到fu处理过后的texture，可以对这个texture做后续操作，如硬编、预览。
             */
            int fuTex = faceunity.fuDualInputToTexture(fuImgNV21Bytes, mCameraTextureId, flags,
                    cameraWidth, cameraHeight, mFrameId++, itemsArray);
            long fuEndTime = System.nanoTime();
            oneHundredFrameFUTime += fuEndTime - fuStartTime;

            //int fuTex = faceunity.fuBeautifyImage(mCameraTextureId, flags,
            //            cameraWidth, cameraHeight, mFrameId++, new int[] {mEffectItem, mFaceBeautyItem});
            //mFullScreenCamera.drawFrame(mCameraTextureId, mtx);
            if (mFullScreenFUDisplay != null) mFullScreenFUDisplay.drawFrame(fuTex, mtx);
            else throw new RuntimeException("HOW COULD IT HAPPEN!!! mFullScreenFUDisplay is null!!!");

            /**
             * 绘制Avatar模式下的镜头内容以及landmarks
             **/
            if (isInAvatarMode) {
                cameraClipFrameRect.drawFrame(mCameraTextureId, mtx);
                faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);
                landmarksPoints.refresh(landmarksData, cameraWidth, cameraHeight, 0.1f, 0.8f, mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT);
                landmarksPoints.draw();
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(START_RECORDING)) {
                videoFileName = MiscUtil.createFileName() + "_camera.mp4";
                File outFile = new File(videoFileName);
                mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile, cameraHeight, cameraWidth,
                        3000000, EGL14.eglGetCurrentContext(), mCameraSurfaceTexture.getTimestamp()
                ));

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
                        Toast.makeText(FUDualInputToTextureExampleActivity.this, "video file saved to "
                                + videoFileName, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
                mTextureMovieEncoder.setTextureId(fuTex);
                mTextureMovieEncoder.frameAvailable(mCameraSurfaceTexture);
            }

            if (!isInPause) glSf.requestRender();
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

            if (mFullScreenCamera != null) {
                mFullScreenCamera.release(false);
                mFullScreenCamera = null;
            }
        }

        public void destroySurfaceTexture() {
            if (mCameraSurfaceTexture != null) {
                mCameraSurfaceTexture.release();
                mCameraSurfaceTexture = null;
            }
        }
    }

    static class MainHandler extends Handler {

        static final int HANDLE_CAMERA_START_PREVIEW = 1;

        private WeakReference<FUDualInputToTextureExampleActivity> mActivityWeakReference;

        MainHandler(FUDualInputToTextureExampleActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FUDualInputToTextureExampleActivity activity = mActivityWeakReference.get();
            switch (msg.what) {
                case HANDLE_CAMERA_START_PREVIEW:
                    Log.e(TAG, "HANDLE_CAMERA_START_PREVIEW");
                    activity.handleCameraStartPreview((SurfaceTexture) msg.obj);
                    break;
            }
        }
    }

    static class CreateItemHandler extends Handler {

        static final int HANDLE_CREATE_ITEM = 1;

        WeakReference<Context> mContext;

        CreateItemHandler(Looper looper, Context context) {
            super(looper);
            mContext = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            Context context = mContext.get();
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_CREATE_ITEM:
                    try {
                        if (mEffectFileName.equals("none")) {
                            itemsArray[1] = mEffectItem = 0;
                        } else {
                            InputStream is = context.getAssets().open(mEffectFileName);
                            byte[] itemData = new byte[is.available()];
                            int len = is.read(itemData);
                            Log.e("FU", "effect len " + len);
                            is.close();
                            final int tmp = itemsArray[1];
                            itemsArray[1] = mEffectItem = faceunity.fuCreateItemFromPackage(itemData);
                            faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);
                            if (tmp != 0) {
                                faceunity.fuDestroyItem(tmp);
                            }
                        }
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

        cameraDataAlreadyCount = 0;

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
                    Toast.makeText(FUDualInputToTextureExampleActivity.this,
                            "Open Camera Failed! Make sure it is not locked!", Toast.LENGTH_SHORT)
                            .show();
                }
            });
            throw new RuntimeException("unable to open camera");
        }

        CameraUtils.setCameraDisplayOrientation(this, cameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();

        /**
         * 设置对焦，会影响camera吞吐速率
         */
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        /**
         * 设置fps
         * */
        int[] closetFramerate = CameraUtils.closetFramerate(parameters, 30);
        Log.e(TAG, "closet framerate min " + closetFramerate[0] + " max " + closetFramerate[1]);
        parameters.setPreviewFpsRange(closetFramerate[0], closetFramerate[1]);

        CameraUtils.choosePreviewSize(parameters, desiredWidth, desiredHeight);
        mCamera.setParameters(parameters);
    }

    private void releaseCamera() {
        Log.e(TAG, "release camera");
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
    }

    @Override
    protected void onBlurLevelSelected(int level) {
        switch (level) {
            case 0:
                mFaceBeautyBlurLevel = 0;
                break;
            case 1:
                mFaceBeautyBlurLevel = 1.0f;
                break;
            case 2:
                mFaceBeautyBlurLevel = 2.0f;
                break;
            case 3:
                mFaceBeautyBlurLevel = 3.0f;
                break;
            case 4:
                mFaceBeautyBlurLevel = 4.0f;
                break;
            case 5:
                mFaceBeautyBlurLevel = 5.0f;
                break;
            case 6:
                mFaceBeautyBlurLevel = 6.0f;
                break;
        }
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
    protected void onEffectItemSelected(String effectItemName) {
        if (effectItemName.equals(mEffectFileName)) {
            return;
        }
        isInAvatarMode = effectItemName.equals("lixiaolong.bundle");
        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);
        mEffectFileName = effectItemName;
        isNeedEffectItem = true;
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
    protected void onCameraChange() {
        Log.e(TAG, "onCameraChange");
        synchronized (prepareCameraDataLock) {

            isNeedSwitchCameraSurfaceTexture = true;

            cameraDataAlreadyCount = 0;

            releaseCamera();

            mCameraNV21Byte = null;
            mFrameId = 0;

            if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, cameraWidth, cameraHeight);
            } else {
                openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraWidth, cameraHeight);
            }
        }
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
            mTextureMovieEncoder.stopRecording();
        }
    }

    @Override
    protected void onFaceShapeLevelSelected(int progress, int max) {
        mFaceShapeLevel = (1.0f * progress) / max;
    }

    @Override
    protected void onFaceShapeSelected(int faceShape) {
        mFaceShape = faceShape;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

        mCreateItemThread.quit();
        mCreateItemThread = null;
        mCreateItemHandler = null;
    }
}
