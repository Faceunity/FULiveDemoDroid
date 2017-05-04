package com.faceunity.fulivedemo;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
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
import com.faceunity.fulivedemo.gles.Texture2dProgram;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 这个Activity演示了如何通过fuRenderToNV21Image
 * 实现在无GL Context的情况下输入nv21的人脸图像，输出添加道具及美颜后的nv21图像
 * 和dual input对应，可以认为single input
 *
 * FU SDK使用者可以将拿到处理后的nv21图像与自己的原有项目对接
 * 请FU SDK使用者直接参考示例放至代码至对应位置
 *
 * FU SDK与camera无耦合，不关心数据的来源，只要图像内容正确且和宽高吻合即可
 *
 * Created by lirui on 2016/12/13.
 */

@SuppressWarnings("deprecation")
public class FURenderToNV21ImageExampleActivity extends FUBaseUIActivity
        implements Camera.PreviewCallback {

    final String TAG = "FURenderToNV21Image";
    Camera mCamera;

    GLSurfaceView glSf;
    GLRenderer glRenderer;

    int cameraWidth;
    int cameraHeight;

    static int mFacebeautyItem = 0;
    static int mEffectItem = 0;
    static int[] itemsArray = {mFacebeautyItem, mEffectItem};

    int mFrameId;

    boolean VERBOSE_LOG = false;

    byte[] mCameraNV21Byte;

    float mFacebeautyColorLevel = 0.2f;
    float mFacebeautyBlurLevel = 6.0f;
    float mFacebeautyCheeckThin = 1.0f;
    float mFacebeautyEnlargeEye = 0.5f;
    float mFacebeautyRedLevel = 0.5f;
    int mFaceShape = 3;
    float mFaceShapeLevel = 0.5f;

    String mFilterName = EffectAndFilterSelectAdapter.FILTERS_NAME[0];

    boolean isNeedEffectItem = true;
    static String mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

    int mCurrentCameraType;

    final boolean DRAW_RETURNED_TEXTURE = true; //直接绘制fuRenderToNV21Image返回的texture或者将返回的nv21 bytes数组load到texture再绘制

    MainHandler mainHandler;

    final int IN_RECORDING = 1;
    final int START_RECORDING = 2;
    final int STOP_RECORDING = 3;
    final int NONE_RECORDING = 4;
    int mRecordingStatus = NONE_RECORDING;

    int currentFrameCnt = 0;
    long lastOneHundredFrameTimeStamp = 0;

    Context mContext;

    HandlerThread mCreateItemThread;
    Handler mCreateItemHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        glSf = (GLSurfaceView) findViewById(R.id.glsv);
        glSf.setEGLContextClientVersion(2);
        glRenderer = new FURenderToNV21ImageExampleActivity.GLRenderer();
        glSf.setRenderer(glRenderer);
        glSf.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mainHandler = new MainHandler(this);

        mCreateItemThread = new HandlerThread("CreateItemThread");
        mCreateItemThread.start();
        mCreateItemHandler = new CreateItemHandler(mCreateItemThread.getLooper(), mContext);
    }

    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mFullScreenCamera;

        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        boolean isFirstOnDrawFrame;

        //Note : this mtx may be not proper for every device, here use it just in a demo
        float[] mtxCameraFront = {0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f};
        float[] mtxCameraBack = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f,0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        int faceTrackingStatus = 0;

        TextureMovieEncoder mTexureMovieEncoder;
        String videoFileName;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.e(TAG, "onSurfaceCreated");

            mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_2D));
            mFullScreenCamera = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_EXT));
            mCameraTextureId = mFullScreenCamera.createTextureObject();
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);
            mainHandler.sendMessage(mainHandler.obtainMessage(
                    MainHandler.HANDLE_CAMERA_START_PREVIEW,
                    mCameraSurfaceTexture));

            try {
                InputStream is = getAssets().open("v3.mp3");
                byte[] v3data = new byte[is.available()];
                is.read(v3data);
                is.close();
                faceunity.fuSetup(v3data, null, authpack.A());
                //faceunity.fuSetMaxFaces(1);
                Log.e(TAG, "fuSetup");

                is = getAssets().open("face_beautification.mp3");
                byte[] itemData = new byte[is.available()];
                is.read(itemData);
                is.close();
                mFacebeautyItem = faceunity.fuCreateItemFromPackage(itemData);
                itemsArray[0] = mFacebeautyItem;
            } catch (IOException e) {
                e.printStackTrace();
            }

            isFirstOnDrawFrame = true;
            //faceunity.disableBoostWithEGLImage();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.e(TAG, "onSurfaceChanged " + width + " " + height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (VERBOSE_LOG) {
                Log.e(TAG, "onDrawFrame");
            }

            /**
             * 获取camera数据, 更新到texture
             */
            float[] mtx = new float[16];
            mCameraSurfaceTexture.updateTexImage();
            mCameraSurfaceTexture.getTransformMatrix(mtx);

            final int isTracking = faceunity.fuIsTracking();
            if (isTracking != faceTrackingStatus) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isTracking == 0) {
                            Log.e(TAG, "detect1 fail");
                            mFaceTrackingStatusImageView.setVisibility(View.VISIBLE);
                        } else {
                            Log.e(TAG, "detect1 success");
                            mFaceTrackingStatusImageView.setVisibility(View.INVISIBLE);
                        }
                    }
                });
                faceTrackingStatus = isTracking;
            }
            if (VERBOSE_LOG) {
                Log.e(TAG, "isTracking " + isTracking);
            }

            if (++currentFrameCnt == 100) {
                currentFrameCnt = 0;
                long tmp = System.currentTimeMillis();
                Log.e(TAG, "renderToNV21Image FPS : " + (1000.0f / ((tmp - lastOneHundredFrameTimeStamp) / 100.0f)));
                lastOneHundredFrameTimeStamp = tmp;
            }

            if (isNeedEffectItem) {
                isNeedEffectItem = false;
                mCreateItemHandler.sendEmptyMessage(CreateItemHandler.HANDLE_CREATE_ITEM);
            }

            faceunity.fuItemSetParam(mFacebeautyItem, "color_level", mFacebeautyColorLevel);
            faceunity.fuItemSetParam(mFacebeautyItem, "blur_level", mFacebeautyBlurLevel);
            faceunity.fuItemSetParam(mFacebeautyItem, "filter_name", mFilterName);
            faceunity.fuItemSetParam(mFacebeautyItem, "cheek_thinning", mFacebeautyCheeckThin);
            faceunity.fuItemSetParam(mFacebeautyItem, "eye_enlarging", mFacebeautyEnlargeEye);
            faceunity.fuItemSetParam(mFacebeautyItem, "face_shape", mFaceShape);
            faceunity.fuItemSetParam(mFacebeautyItem, "face_shape_level", mFaceShapeLevel);
            faceunity.fuItemSetParam(mFacebeautyItem, "red_level", mFacebeautyRedLevel);


            //faceunity.fuItemSetParam(mFacebeautyItem, "is_beauty_on", 0);

            if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
                Log.e(TAG, "camera nv21 bytes null");
                return;
            }
            /**
             * 这个函数执行完成后，入参的nv21 byte数组会被改变
             */
            int fuTex = faceunity.fuRenderToNV21Image(mCameraNV21Byte,
                    cameraWidth, cameraHeight, mFrameId, itemsArray, mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X);

            if (DRAW_RETURNED_TEXTURE) {
                mFullScreenFUDisplay.drawFrame(fuTex, mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ?
                                mtxCameraFront : mtxCameraBack);
            } else {
                //TODO 将nv21 byte数组转换成texture，并通过预览检测正确性
                //int loadNV21ByteTex = faceunity.fuRenderNV21ImageToTexture(mCameraNV21Byte, cameraWidth,
                  //      cameraHeight, mFrameId, new int[]{0});
                //mFullScreenFUDisplay.drawFrame(loadNV21ByteTex, mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ?
                  //      mtxCameraFront : mtxCameraBack);
            }

            if (mRecordingStatus == START_RECORDING) {
                mTexureMovieEncoder = new TextureMovieEncoder();
                videoFileName = MiscUtil.createFileName() + "_camera.mp4";
                File outFile = new File(videoFileName);
                mTexureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                        outFile,cameraHeight, cameraWidth,
                        2000000, EGL14.eglGetCurrentContext()
                ));
                mRecordingStatus = IN_RECORDING;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FURenderToNV21ImageExampleActivity.this, "video file saved to "
                                + videoFileName, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            if (mRecordingStatus == IN_RECORDING) {
                mTexureMovieEncoder.setTextureId(fuTex);
                mTexureMovieEncoder.frameAvailable(mCameraSurfaceTexture);
            }

            if (mRecordingStatus == STOP_RECORDING) {
                mTexureMovieEncoder.stopRecording();
                mRecordingStatus = NONE_RECORDING;
            }
            mFrameId++;
        }

        public void notifyPause() {
            if (mRecordingStatus == IN_RECORDING) {
                mTexureMovieEncoder.stopRecording();
                mRecordingStatus = NONE_RECORDING;
            }

            faceTrackingStatus = 0;
            if (mFullScreenFUDisplay != null) {
                mFullScreenFUDisplay.release(false);
            }

            if (mFullScreenCamera != null) {
                mFullScreenCamera.release(false);
            }

            if (mCameraSurfaceTexture != null) {
                mCameraSurfaceTexture.release();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraWidth = 1280;
        cameraHeight = 720;
        openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT,
                cameraWidth,
                cameraHeight);

        Camera.Size size = mCamera.getParameters().getPreviewSize();
        cameraWidth = size.width;
        cameraHeight = size.height;
        Log.e(TAG, "open camera size " + size.width + " " + size.height);

        //handleCameraStartPreview();

        //fuInit();

        AspectFrameLayout aspectFrameLayout = (AspectFrameLayout) findViewById(R.id.afl);
        aspectFrameLayout.setAspectRatio(1.0f * cameraHeight / cameraWidth);

        glSf.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();

        glSf.onPause();

        mFrameId = 0;

        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);

        glSf.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Note: 切忌使用一个已经destroy的item
                //faceunity.fuDestroyAllItems();
                faceunity.fuDestroyItem(mEffectItem);
                itemsArray[1] = mEffectItem = 0;
                faceunity.fuDestroyItem(mFacebeautyItem);
                itemsArray[0] = mFacebeautyItem = 0;
                faceunity.fuOnDeviceLost();
                isNeedEffectItem = true;
            }
        });

        glRenderer.notifyPause();
    }

    /**
     * 当前未使用，无GL环境时可以使用：初始化GL环境，创建GL Context
     */
    private void fuInitWithoutGLContext() {
        /**
         * 如果当前线程没有GL Context，那么可以使用我们的API创建一个
         * 如果已经有GL Context，如在GLSufaceView对应的Renderer,则无需使用
         *
         * 所有FU API 都需要保证在*同一个*具有*GL Context*的线程被调用
         *
         * 建议使用者在*非主线程*完成fu相关操作，这里就不做演示了
         */
        faceunity.fuCreateEGLContext();

        try {
            InputStream is = getAssets().open("v3.mp3");
            byte[] v3data = new byte[is.available()];
            is.read(v3data);
            is.close();
            faceunity.fuSetup(v3data, null, authpack.A());
            faceunity.fuSetMaxFaces(1);
            Log.e(TAG, "fuSetup");

            is = getAssets().open("face_beautification.mp3");
            byte[] itemData = new byte[is.available()];
            is.read(itemData);
            is.close();
            mFacebeautyItem = faceunity.fuCreateItemFromPackage(itemData);

            is = getAssets().open("YellowEar.mp3");
            itemData = new byte[is.available()];
            is.read(itemData);
            is.close();
            mEffectItem = faceunity.fuCreateItemFromPackage(itemData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (VERBOSE_LOG) {
            Log.d(TAG, "onPreviewFrame");
            int isTracking = faceunity.fuIsTracking();
            if (true) {
                Log.e(TAG, "isTracking " + isTracking);
            }
        }
        mCameraNV21Byte = data;
        glSf.requestRender();
    }

    private void handleCameraStartPreview(SurfaceTexture st) {
        Log.e(TAG, "handleCameraStartPreview");
        mCamera.setPreviewCallback(this);
        try {
            mCamera.setPreviewTexture(st);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*st.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                Log.e(TAG, "onFrameAvailable");
                //
                //glSf.requestRender();
            }
        });*/
        mCamera.startPreview();
    }



    @SuppressWarnings("deprecation")
    private void openCamera(int cameraType, int desiredWidth, int desiredHeight) {
        if (VERBOSE_LOG) {
            Log.d(TAG, "openCamera");
        }
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
            throw new RuntimeException("unable to open camera");
        }

        CameraUtils.setCameraDisplayOrientation(this, cameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setDisplayOrientation(90);
        CameraUtils.choosePreviewSize(parameters, desiredWidth, desiredHeight);
        mCamera.setParameters(parameters);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
            Log.e(TAG, "release camera");
        }
    }

    static class MainHandler extends Handler {

        static final int HANDLE_CAMERA_START_PREVIEW = 1;

        private WeakReference<FURenderToNV21ImageExampleActivity> mActivityWeakReference;

        MainHandler(FURenderToNV21ImageExampleActivity activity) {
            mActivityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FURenderToNV21ImageExampleActivity activity = mActivityWeakReference.get();
            switch (msg.what) {
                case HANDLE_CAMERA_START_PREVIEW:
                    activity.handleCameraStartPreview((SurfaceTexture) msg.obj);
                    break;
            }
        }
    }

    static class CreateItemHandler extends Handler {

        static final int HANDLE_CREATE_ITEM = 1;

        Context mContext;

        CreateItemHandler(Looper looper, Context context) {
            super(looper);
            mContext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_CREATE_ITEM:
                    try {
                        if (mEffectFileName.equals("none")) {
                            itemsArray[1] = mEffectItem = 0;
                        } else {
                            InputStream is = mContext.getAssets().open(mEffectFileName);
                            byte[] itemData = new byte[is.available()];
                            is.read(itemData);
                            is.close();
                            int tmp = itemsArray[1];
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

    @Override
    protected void onBlurLevelSelected(int level) {
        switch (level) {
            case 0:
                mFacebeautyBlurLevel = 0;
                break;
            case 1:
                mFacebeautyBlurLevel = 1.0f;
                break;
            case 2:
                mFacebeautyBlurLevel = 2.0f;
                break;
            case 3:
                mFacebeautyBlurLevel = 3.0f;
                break;
            case 4:
                mFacebeautyBlurLevel = 4.0f;
                break;
            case 5:
                mFacebeautyBlurLevel = 5.0f;
                break;
            case 6:
                mFacebeautyBlurLevel = 6.0f;
                break;
        }
    }

    @Override
    protected void onCheekThinSelected(int progress, int max) {
        mFacebeautyCheeckThin = 1.0f * progress / max;
    }

    @Override
    protected void onColorLevelSelected(int progress, int max) {
        mFacebeautyColorLevel = 1.0f * progress / max;
    }

    @Override
    protected void onEffectItemSelected(String effectItemName) {
        if (effectItemName.equals(mEffectFileName)) {
            return;
        }
        mCreateItemHandler.removeMessages(CreateItemHandler.HANDLE_CREATE_ITEM);
        mEffectFileName = effectItemName;
        isNeedEffectItem = true;
    }

    @Override
    protected void onEnlargeEyeSelected(int progress, int max) {
        mFacebeautyEnlargeEye = 1.0f * progress / max;
    }

    @Override
    protected void onFilterSelected(String filterName) {
        mFilterName = filterName;
    }

    @Override
    protected void onRedLevelSelected(int progress, int max) {
        mFacebeautyRedLevel = 1.0f * progress / max;
    }

    @Override
    protected void onCameraChange() {
        Log.d(TAG, "onCameraChange");
        releaseCamera();
        faceunity.fuOnCameraChange();
        if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, cameraWidth, cameraHeight);
        } else {
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraWidth, cameraHeight);
        }
        handleCameraStartPreview(null);
    }

    @Override
    protected void onFaceShapeLevelSelected(int progress, int max) {
        mFaceShapeLevel = (1.0f * progress) / max;
        Log.e(TAG, "faceshape level " + mFaceShapeLevel);
    }

    @Override
    protected void onFaceShapeSelected(int faceShape) {
        mFaceShape = faceShape;
        Log.e(TAG, "faceshape " + mFaceShape);
    }

    @Override
    protected void onStartRecording() {
        MiscUtil.Logger(TAG, "start recording", false);
        mRecordingStatus = START_RECORDING;
    }

    @Override
    protected void onStopRecording() {
        MiscUtil.Logger(TAG, "stop recording", false);
        mRecordingStatus = STOP_RECORDING;
    }
}
