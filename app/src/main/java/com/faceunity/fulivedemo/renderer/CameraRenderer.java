package com.faceunity.fulivedemo.renderer;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramLandmarks;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.GlUtil;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * 打开相机，使用 GLSurfaceView 渲染图像
 *
 * Camera.PreviewCallback camera数据回调
 * GLSurfaceView.Renderer GLSurfaceView相应的创建销毁与绘制回调
 * <p>
 * Created by tujh on 2018/3/2.
 */
public class CameraRenderer implements Camera.PreviewCallback, GLSurfaceView.Renderer {
    public final static String TAG = CameraRenderer.class.getSimpleName();
    /**
     * 显示 Landmark 点位
     */
    public static final boolean DRAW_LANDMARK = false;
    private static final int PREVIEW_BUFFER_COUNT = 3;
    private float[] mTexMatrix = {0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f};

    private Activity mActivity;
    private GLSurfaceView mGlSurfaceView;
    private OnRendererStatusListener mOnRendererStatusListener;

    private int mViewWidth = 720;
    private int mViewHeight = 1280;

    private final Object mCameraLock = new Object();
    private Camera mCamera;
    private byte[][] previewCallbackBuffer;
    private int mCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraWidth = 1280;
    private int mCameraHeight = 720;
    private int mCameraOrientation;
    private volatile int mCameraTextureId;
    private volatile byte[] mCameraNV21Byte;
    private volatile boolean mIsDrawing = false;
    private SurfaceTexture mSurfaceTexture;

    private int mFuTextureId;
    private boolean mIsCameraOpen;
    private volatile boolean mIsNeedStopDraw;
    private volatile float[] mMvpMatrix;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramTextureOES mTextureOES;
    private ProgramLandmarks mProgramLandmarks;
    private float[] mLandmarksData;
    private FPSUtil mFPSUtil;

    public CameraRenderer(Activity activity, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mActivity = activity;
        mGlSurfaceView = glSurfaceView;
        mOnRendererStatusListener = onRendererStatusListener;
        mFPSUtil = new FPSUtil();
//        setGlThreadLogEnable();
    }

    // 打开 GLSurfaceView 的日志
    private void setGlThreadLogEnable() {
        Log.d(TAG, "setGlThreadLogEnable() called");
        mGlSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_LOG_GL_CALLS | GLSurfaceView.DEBUG_CHECK_GL_ERROR);
        setAccess("LOG_ATTACH_DETACH");
        setAccess("LOG_THREADS");
        setAccess("LOG_PAUSE_RESUME");
        setAccess("LOG_SURFACE");
        setAccess("LOG_RENDERER");
        setAccess("LOG_RENDERER_DRAW_FRAME");
        setAccess("LOG_EGL");
    }

    private void setAccess(String fieldName) {
        try {
            Field logField = GLSurfaceView.class.getDeclaredField(fieldName);
            logField.setAccessible(true);
            logField.set(null, true);
        } catch (Exception e) {
            Log.w(TAG, "setAccess: ", e);
        }
    }

    public void onCreate() {
        mGlSurfaceView.onResume();
    }

    public void onResume() {
        openCamera(mCameraType);
    }

    public void onPause() {
        releaseCamera();
    }

    public void onDestroy() {
        final CountDownLatch count = new CountDownLatch(1);
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignored
        }
        mGlSurfaceView.onPause();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        // called on mainThread
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        if (!mIsNeedStopDraw) {
            mGlSurfaceView.requestRender();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated. Thread:" + Thread.currentThread().getName());
        mProgramTexture2d = new ProgramTexture2d();
        mTextureOES = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mCameraTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        cameraStartPreview();

        mOnRendererStatusListener.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mMvpMatrix = GlUtil.changeMVPMatrixCrop(GlUtil.IDENTITY_MATRIX, width, height, mCameraHeight, mCameraWidth);
        Log.d(TAG, "onSurfaceChanged. viewWidth:" + width + ", viewHeight:" + height
                + ". cameraOrientation:" + mCameraOrientation + ", cameraWidth:" + mCameraWidth
                + ", cameraHeight:" + mCameraHeight + ", textureId:" + mCameraTextureId);
        mViewWidth = width;
        mViewHeight = height;
        mFPSUtil.resetLimit();
        mIsDrawing = false;
        mOnRendererStatusListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mTextureOES == null || mProgramTexture2d == null) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (mCameraNV21Byte == null) {
            mProgramTexture2d.drawFrame(mFuTextureId, mTexMatrix, mMvpMatrix);
            return;
        }

        try {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTexMatrix);
        } catch (Exception e) {
            Log.e(TAG, "onDrawFrame: ", e);
        }

        if (!mIsNeedStopDraw) {
            mFuTextureId = mOnRendererStatusListener.onDrawFrame(mCameraNV21Byte, mCameraTextureId,
                    mCameraWidth, mCameraHeight, mTexMatrix, mSurfaceTexture.getTimestamp());
        }
        //用于屏蔽切换调用SDK处理数据方法导致的绿屏（切换SDK处理数据方法是用于展示，实际使用中无需切换，故无需调用做这个判断,直接使用else分支绘制即可）
        if (mFuTextureId <= 0) {
            mTextureOES.drawFrame(mCameraTextureId, mTexMatrix, mMvpMatrix);
        } else {
            mProgramTexture2d.drawFrame(mFuTextureId, mTexMatrix, mMvpMatrix);
        }

        // 绘制 landmark 点位
        if (!mIsNeedStopDraw && mLandmarksData != null) {
            mProgramLandmarks.refresh(mLandmarksData, mCameraWidth, mCameraHeight, mCameraOrientation, mCameraType);
            mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
        }

        mFPSUtil.limit();
        if (!mIsNeedStopDraw) {
            mGlSurfaceView.requestRender();
        }

        mIsDrawing = true;
    }

    public void setNeedStopDraw(boolean needStopDraw) {
        mIsNeedStopDraw = needStopDraw;
    }

    public void dismissImageTexture() {
        setNeedStopDraw(false);
        mMvpMatrix = GlUtil.changeMVPMatrixCrop(mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
    }

    public void showImageTexture(final Bitmap bitmap) {
        if (bitmap != null) {
            mGlSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuTextureId = GlUtil.createImageTexture(bitmap);
                    mMvpMatrix = GlUtil.changeMVPMatrixCrop(mViewWidth, mViewHeight, bitmap.getWidth(), bitmap.getHeight());
                    if (mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        float[] tmp = new float[16];
                        Matrix.setIdentityM(tmp, 0);
                        Matrix.scaleM(tmp, 0, -1F, 1F, 1F);
                        Matrix.multiplyMM(mMvpMatrix, 0, tmp, 0, mMvpMatrix, 0);
                    }
                    if (mCameraOrientation == 90) {
                        if (mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            Matrix.rotateM(mMvpMatrix, 0, 270, 0F, 0F, 1F);
                        } else {
                            Matrix.rotateM(mMvpMatrix, 0, 90, 0F, 0F, 1F);
                        }
                    } else if (mCameraOrientation == 270) {
                        if (mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            Matrix.rotateM(mMvpMatrix, 0, 90, 0F, 0F, 1F);
                        } else {
                            Matrix.rotateM(mMvpMatrix, 0, 270, 0F, 0F, 1F);
                        }
                    }
                    mGlSurfaceView.requestRender();
                }
            });
        }
    }

    private void onSurfaceDestroy() {
        Log.d(TAG, "onSurfaceDestroy: ");
        mIsDrawing = false;
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mCameraTextureId != 0) {
            int[] textures = new int[]{mCameraTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mCameraTextureId = 0;
        }

        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
        if (mTextureOES != null) {
            mTextureOES.release();
            mTextureOES = null;
        }
        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }

        mOnRendererStatusListener.onSurfaceDestroy();
    }

    @SuppressWarnings("Deprecation")
    private void openCamera(final int cameraType) {
        try {
            synchronized (mCameraLock) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                int numCameras = Camera.getNumberOfCameras();
                if (numCameras <= 0) {
                    throw new RuntimeException("No cameras");
                }
                for (int i = 0; i < numCameras; i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == cameraType) {
                        cameraId = i;
                        mCamera = Camera.open(i);
                        mCameraType = cameraType;
                        break;
                    }
                }
                if (mCamera == null) {
                    cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
                    Camera.getCameraInfo(cameraId, info);
                    mCamera = Camera.open(cameraId);
                    mCameraType = cameraId;
                }
                if (mCamera == null) {
                    throw new RuntimeException("No cameras");
                }

                mCameraOrientation = CameraUtils.getCameraOrientation(cameraId);
                CameraUtils.setCameraDisplayOrientation(mActivity, cameraId, mCamera);
                Log.d(TAG, "openCamera. facing: " + (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK
                        ? "back" : "front") + ", orientation:" + mCameraOrientation);

                Camera.Parameters parameters = mCamera.getParameters();
                CameraUtils.setFocusModes(parameters);
                int[] size = CameraUtils.choosePreviewSize(parameters, mCameraWidth, mCameraHeight);
                mCameraWidth = size[0];
                mCameraHeight = size[1];
                mCamera.setParameters(parameters);
                if (mViewWidth != 0 && mViewHeight != 0) {
                    mMvpMatrix = GlUtil.changeMVPMatrixCrop(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
                }
            }

            cameraStartPreview();
            mOnRendererStatusListener.onCameraChange(mCameraType, mCameraOrientation);
            mFPSUtil.resetLimit();
        } catch (Exception e) {
            Log.e(TAG, "openCamera: ", e);
            releaseCamera();
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.camera_dialog_title)
                    .setMessage(R.string.camera_dialog_message)
                    .setNegativeButton(R.string.camera_dialog_open, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openCamera(cameraType);
                        }
                    })
                    .setNeutralButton(R.string.camera_dialog_back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.onBackPressed();
                        }
                    })
                    .show();
        }
    }

    private void cameraStartPreview() {
        if (mCameraTextureId == 0) {
            return;
        }
        try {
            synchronized (mCameraLock) {
                if (mCamera == null || mIsCameraOpen) {
                    return;
                }
                mCamera.stopPreview();

                if (previewCallbackBuffer == null) {
                    previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
                }
                mCamera.setPreviewCallbackWithBuffer(this);
                for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++) {
                    mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
                }
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.release();
                }
                mSurfaceTexture = new SurfaceTexture(mCameraTextureId);
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
                mIsCameraOpen = true;
                Log.d(TAG, "cameraStartPreview: cameraTexId:" + mCameraTextureId);
            }
        } catch (Exception e) {
            Log.e(TAG, "cameraStartPreview: ", e);
        }
    }

    private void releaseCamera() {
        Log.d(TAG, "releaseCamera()");
        mCameraNV21Byte = null;
        try {
            synchronized (mCameraLock) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.setPreviewTexture(null);
                    mCamera.setPreviewCallbackWithBuffer(null);
                    mCamera.release();
                    mCamera = null;
                }
                mIsCameraOpen = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "releaseCamera: ", e);
        }
    }

    public void changeCamera() {
        if (mCameraNV21Byte == null && mIsDrawing) {
            return;
        }
        mIsDrawing = false;
        releaseCamera();
        openCamera(mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }

    public float[] getMvpMatrix() {
        return mMvpMatrix;
    }

    public void handleFocus(float x, float y) {
        CameraUtils.handleFocus(mCamera, x, y, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
    }

    public float getExposureCompensation() {
        return CameraUtils.getExposureCompensation(mCamera);
    }

    public void setExposureCompensation(float v) {
        CameraUtils.setExposureCompensation(mCamera, v);
    }

    public void setLandmarksData(float[] landmarksData) {
        this.mLandmarksData = landmarksData;
    }


    public interface OnRendererStatusListener {
        /**
         * Called when surface is created or recreated.
         */
        void onSurfaceCreated();

        /**
         * Called when surface'size changed.
         *
         * @param viewWidth
         * @param viewHeight
         */
        void onSurfaceChanged(int viewWidth, int viewHeight);

        /**
         * Called when drawing current frame
         *
         * @param cameraNV21Byte
         * @param cameraTextureId
         * @param cameraWidth
         * @param cameraHeight
         * @param mvpMatrix
         * @param timeStamp
         * @return
         */
        int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mvpMatrix, long timeStamp);

        /**
         * Called when surface is destroyed
         */
        void onSurfaceDestroy();

        /**
         * Called when camera changed
         *
         * @param cameraType
         * @param cameraOrientation
         */
        void onCameraChange(int cameraType, int cameraOrientation);
    }

}
