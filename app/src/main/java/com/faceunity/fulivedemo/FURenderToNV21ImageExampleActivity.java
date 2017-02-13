package com.faceunity.fulivedemo;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.faceunity.fulivedemo.gles.FullFrameRect;
import com.faceunity.fulivedemo.gles.Texture2dProgram;
import com.faceunity.wrapper.faceunity;

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

    int cameraWidth;
    int cameraHeight;

    int mFacebeautyItem = 0;
    int mEffectItem = 0;

    int mFrameId;

    boolean VERBOSE_LOG = true;

    byte[] mCameraNV21Byte;

    float mFacebeautyColorLevel = 0.5f;
    float mFacebeautyBlurLevel = 5.0f;
    float mFacebeautyCheeckThin = 1.0f;
    float mFacebeautyEnlargeEye = 1.0f;
    String mFilterName = EffectAndFilterSelectAdapter.FILTERS_NAME[0];
    String mEffectFileName = EffectAndFilterSelectAdapter.EFFECT_ITEM_FILE_NAME[1];

    int mCurrentCameraType;

    final boolean DRAW_RETURNED_TEXTURE = true; //直接绘制fuRenderToNV21Image返回的texture或者将返回的nv21 bytes数组load到texture再绘制

    MainHandler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSf = (GLSurfaceView) findViewById(R.id.glsv);
        glSf.setEGLContextClientVersion(2);
        glSf.setRenderer(new FURenderToNV21ImageExampleActivity.GLRenderer());
        glSf.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mainHandler = new MainHandler(this);
    }

    class GLRenderer implements GLSurfaceView.Renderer {

        FullFrameRect mFullScreenFUDisplay;
        FullFrameRect mFullScreenCamera;

        /**
         * 测试表明，有些手机无相关surface的话onPreviewFrame会造成无法正确回调，所以这里设置了一下
         * 但是本Activity目的只是演示如果通过只输入nv21 byte数组来实现对应的效果
         */
        int mCameraTextureId;
        SurfaceTexture mCameraSurfaceTexture;

        boolean isFirstOnDrawFrame;

        //Note : this mtx may be not proper for every device, here use it just in a demo
        float[] mtxCameraFront = {0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f};
        float[] mtxCameraBack = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f,0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
        int faceTrackingStatus = 0;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.e(TAG, "onSurfaceCreated");

            mFullScreenFUDisplay = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_2D));

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
            } catch (IOException e) {
                e.printStackTrace();
            }

            isFirstOnDrawFrame = true;

            mFullScreenCamera = new FullFrameRect(new Texture2dProgram(
                    Texture2dProgram.ProgramType.TEXTURE_EXT));
            mCameraTextureId = mFullScreenCamera.createTextureObject();
            mCameraSurfaceTexture = new SurfaceTexture(mCameraTextureId);

            mainHandler.sendMessage(mainHandler.obtainMessage(
                    MainHandler.HANDLE_CAMERA_START_PREVIEW,
                    mCameraSurfaceTexture));

            faceunity.disableBoostWithEGLImage();
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

            if (mEffectItem == 0) {
                try {
                    InputStream is = getAssets().open(mEffectFileName);
                    byte[] itemData = new byte[is.available()];
                    is.read(itemData);
                    is.close();
                    mEffectItem = faceunity.fuCreateItemFromPackage(itemData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            faceunity.fuItemSetParam(mEffectItem, "isAndroid", 1.0);

            faceunity.fuItemSetParam(mFacebeautyItem, "color_level", mFacebeautyColorLevel);
            faceunity.fuItemSetParam(mFacebeautyItem, "blur_level", mFacebeautyBlurLevel);
            faceunity.fuItemSetParam(mFacebeautyItem, "filter_name", mFilterName);
            faceunity.fuItemSetParam(mFacebeautyItem, "cheek_thinning", mFacebeautyCheeckThin);
            faceunity.fuItemSetParam(mFacebeautyItem, "eye_enlarging", mFacebeautyEnlargeEye);

            if (mCameraNV21Byte == null || mCameraNV21Byte.length == 0) {
                Log.e(TAG, "camera nv21 bytes null");
                return;
            }
            /**
             * 这个函数执行完成后，入参的nv21 byte数组会被改变
             */
            int fuTex = faceunity.fuRenderToNV21Image(mCameraNV21Byte,
                    cameraWidth, cameraHeight, mFrameId, new int[] {mEffectItem, mFacebeautyItem});

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
            mFrameId++;
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

        glSf.queueEvent(new Runnable() {
            @Override
            public void run() {
                //Note: 切忌使用一个已经destroy的item
                faceunity.fuDestroyItem(mEffectItem);
                mEffectItem = 0;
                faceunity.fuDestroyItem(mFacebeautyItem);
                mFacebeautyItem = 0;
                faceunity.fuOnDeviceLost();
            }
        });

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
        Log.d(TAG, "onPreviewFrame");
        int isTracking = faceunity.fuIsTracking();
        if (true) {
            Log.e(TAG, "isTracking " + isTracking);
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
        mCamera.startPreview();
    }

    @SuppressWarnings("deprecation")
    private void openCamera(int cameraType, int desiredWidth, int desiredHeight) {
        Log.d(TAG, "openCamera");
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
        }
    }

    @Override
    protected void onCheekThinSelected(int progress, int max) {
        mFacebeautyCheeckThin = 2.0f * progress / max;
    }

    @Override
    protected void onColorLevelSelected(int progress, int max) {
        mFacebeautyColorLevel = 1.0f * progress / max;
    }

    @Override
    protected void onEffectItemSelected(String effectItemName) {
        mEffectFileName = effectItemName;
        if (mEffectItem != 0) {
            glSf.queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuDestroyItem(mEffectItem);
                    mEffectItem = 0;
                }
            });
        }
    }

    @Override
    protected void onEnlargeEyeSelected(int progress, int max) {
        mFacebeautyEnlargeEye = 4.0f * progress / max;
    }

    @Override
    protected void onFilterSelected(String filterName) {
        mFilterName = filterName;
    }

    @Override
    protected void onCameraChange() {
        Log.d(TAG, "onCameraChange");
        releaseCamera();
        if (mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            openCamera(Camera.CameraInfo.CAMERA_FACING_BACK, cameraWidth, cameraHeight);
        } else {
            openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, cameraWidth, cameraHeight);
        }
        handleCameraStartPreview(null);
    }
}
