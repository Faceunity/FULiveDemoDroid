package com.faceunity.fulivedemo.renderer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;

import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramLandmarks;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.GlUtil;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2019.08.23
 */
public class BaseCameraRenderer implements GLSurfaceView.Renderer {
    /**
     * 显示 landmarks 点位开关
     */
    public static final boolean ENABLE_DRAW_LANDMARKS = false;
    private static final String TAG = "BaseCameraRenderer";
    public static final int FACE_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final int FACE_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;
    public static final int FRONT_CAMERA_ORIENTATION = 270;
    public static final int BACK_CAMERA_ORIENTATION = 90;
    public static final int DEFAULT_PREVIEW_WIDTH = 1280;
    public static final int DEFAULT_PREVIEW_HEIGHT = 720;
    public static final int PREVIEW_BUFFER_SIZE = 3;
    private static final float[] TEXTURE_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f};
    protected int mViewWidth;
    protected int mViewHeight;
    protected volatile boolean mIsStopPreview;
    protected volatile boolean mIsSwitchCamera;
    protected int mCameraFacing = FACE_FRONT;
    protected int mCameraWidth = DEFAULT_PREVIEW_WIDTH;
    protected int mCameraHeight = DEFAULT_PREVIEW_HEIGHT;
    protected int mCameraTexId;
    protected int mBackCameraOrientation = BACK_CAMERA_ORIENTATION;
    protected int mFrontCameraOrientation = FRONT_CAMERA_ORIENTATION;
    protected int mCameraOrientation = FRONT_CAMERA_ORIENTATION;
    protected float[] mMvpMatrix;
    protected float[] mTexMatrix = Arrays.copyOf(TEXTURE_MATRIX, TEXTURE_MATRIX.length);
    protected byte[] mCameraNV21Byte;
    protected SurfaceTexture mSurfaceTexture;
    protected GLSurfaceView mGlSurfaceView;
    protected Activity mActivity;
    protected Handler mBackgroundHandler;
    protected boolean mIsPreviewing;
    protected Bitmap mShotBitmap;
    private ProgramTextureOES mProgramTextureOES;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramLandmarks mProgramLandmarks;
    private float[] mLandmarksData;
    protected int m2DTexId;
    private FPSUtil mFPSUtil = new FPSUtil();
    protected OnRendererStatusListener mOnRendererStatusListener;
    protected final Object mLock = new Object();

    protected BaseCameraRenderer(Activity activity, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mGlSurfaceView = glSurfaceView;
        mActivity = activity;
        mOnRendererStatusListener = onRendererStatusListener;
        initCameraInfo();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated. thread:" + Thread.currentThread().getName());
        mProgramTexture2d = new ProgramTexture2d();
        mProgramTextureOES = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mCameraTexId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                startPreview();
            }
        });
        mOnRendererStatusListener.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        if (mViewWidth != width || mViewHeight != height) {
            mMvpMatrix = GlUtil.changeMVPMatrixCrop(width, height, mCameraHeight, mCameraWidth);
        }
        Log.d(TAG, "onSurfaceChanged. viewWidth:" + width + ", viewHeight:" + height
                + ". cameraOrientation:" + mCameraOrientation + ", cameraWidth:" + mCameraWidth
                + ", cameraHeight:" + mCameraHeight + ", cameraTexId:" + mCameraTexId);
        mViewWidth = width;
        mViewHeight = height;
        mFPSUtil.resetLimit();
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                startPreview();
            }
        });
        mOnRendererStatusListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mProgramTexture2d == null || mProgramTextureOES == null || mSurfaceTexture == null) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mShotBitmap == null) {
            try {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mTexMatrix);
            } catch (Exception e) {
                Log.e(TAG, "onDrawFrame: ", e);
            }
        }

        if (!mIsStopPreview) {
            synchronized (mLock) {
                if (mCameraNV21Byte != null) {
                    m2DTexId = mOnRendererStatusListener.onDrawFrame(mCameraNV21Byte, mCameraTexId,
                            mCameraWidth, mCameraHeight, mMvpMatrix, mTexMatrix, mSurfaceTexture.getTimestamp());
                }
            }
        }

        if (!mIsSwitchCamera) {
            if (m2DTexId > 0) {
                mProgramTexture2d.drawFrame(m2DTexId, mTexMatrix, mMvpMatrix);
            } else if (mCameraTexId > 0) {
                mProgramTextureOES.drawFrame(mCameraTexId, mTexMatrix, mMvpMatrix);
            }

            if (ENABLE_DRAW_LANDMARKS && mLandmarksData != null) {
                mProgramLandmarks.refresh(mLandmarksData, mCameraWidth, mCameraHeight, mCameraOrientation, mCameraFacing, mMvpMatrix);
                mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
            }
        }

        if (!mIsStopPreview) {
            mGlSurfaceView.requestRender();
        }
        mFPSUtil.limit();
    }

    public void onResume() {
        startBackgroundThread();
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                openCamera(mCameraFacing);
                startPreview();
            }
        });
        mGlSurfaceView.onResume();
    }

    public void onPause() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                destroyGlSurface();
                countDownLatch.countDown();
            }
        });
        try {
            countDownLatch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignored
        }
        mGlSurfaceView.onPause();
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                closeCamera();
            }
        });
        stopBackgroundThread();
    }

    public void changeResolution(int cameraWidth, int cameraHeight) {
    }

    public void switchCamera() {
        if (mBackgroundHandler == null) {
            return;
        }
        mBackgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsStopPreview = true;
                mIsSwitchCamera = true;
                boolean isFront = mCameraFacing == FACE_FRONT;
                mCameraFacing = isFront ? FACE_BACK : FACE_FRONT;
                mCameraOrientation = isFront ? mBackCameraOrientation : mFrontCameraOrientation;
                closeCamera();
                openCamera(mCameraFacing);
                startPreview();
                mIsSwitchCamera = false;
                mIsStopPreview = false;
                mOnRendererStatusListener.onCameraChanged(mCameraFacing, mCameraOrientation);
            }
        });
    }

    public void hideImageTexture() {
        mShotBitmap = null;
        mIsStopPreview = false;
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mMvpMatrix = GlUtil.changeMVPMatrixCrop(mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
            }
        });
        mGlSurfaceView.requestRender();
    }

    public void showImageTexture(final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        mIsStopPreview = true;
        mShotBitmap = bitmap;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                m2DTexId = GlUtil.createImageTexture(bitmap);
                float[] mvpMatrix = GlUtil.changeMVPMatrixCrop(mViewWidth, mViewHeight, bitmap.getWidth(), bitmap.getHeight());
                float[] scaleMatrix = Arrays.copyOf(GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX.length);
                Matrix.scaleM(scaleMatrix, 0, -1F, 1F, 1F);
                Matrix.multiplyMM(mMvpMatrix, 0, scaleMatrix, 0, mvpMatrix, 0);
                if (mCameraOrientation == BACK_CAMERA_ORIENTATION) {
                    Matrix.rotateM(mMvpMatrix, 0, mCameraFacing == FACE_FRONT ? FRONT_CAMERA_ORIENTATION : BACK_CAMERA_ORIENTATION, 0F, 0F, 1F);
                } else if (mCameraOrientation == FRONT_CAMERA_ORIENTATION) {
                    Matrix.rotateM(mMvpMatrix, 0, mCameraFacing == FACE_FRONT ? BACK_CAMERA_ORIENTATION : FRONT_CAMERA_ORIENTATION, 0F, 0F, 1F);
                }
                mTexMatrix = Arrays.copyOf(TEXTURE_MATRIX, TEXTURE_MATRIX.length);
            }
        };
        mGlSurfaceView.queueEvent(runnable);
        mGlSurfaceView.requestRender();
    }

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }

    public int getViewWidth() {
        return mViewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public int getHeight4Video() {
        int h = mViewHeight * DEFAULT_PREVIEW_HEIGHT / mViewWidth;
        return h;
    }

    public int getWidth4Video() {
        int w = mViewWidth * DEFAULT_PREVIEW_WIDTH / mViewHeight;
        return w;
    }

    public void handleFocus(float rawX, float rawY, int areaSize) {

    }

    public float getExposureCompensation() {
        return 0;
    }

    public void setExposureCompensation(float value) {

    }

    public void setLandmarksDataArray(float[][] landmarksDataArray) {
        if (mLandmarksData == null || mLandmarksData.length != landmarksDataArray[0].length * landmarksDataArray.length) {
            mLandmarksData = new float[landmarksDataArray[0].length * landmarksDataArray.length];
        }
        for (int i = 0; i < landmarksDataArray.length; i++) {
            System.arraycopy(landmarksDataArray[i], 0, mLandmarksData, i * landmarksDataArray[i].length, landmarksDataArray[i].length);
        }
    }

    protected void initCameraInfo() {
    }

    protected void openCamera(int cameraFacing) {
    }

    protected void startPreview() {
    }

    protected void closeCamera() {
        mCameraNV21Byte = null;
    }

    private void destroyGlSurface() {
        if (mCameraTexId != 0) {
            GLES20.glDeleteTextures(1, new int[]{mCameraTexId}, 0);
            mCameraTexId = 0;
        }
        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
        if (mProgramTextureOES != null) {
            mProgramTextureOES.release();
            mProgramTextureOES = null;
        }
        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        mOnRendererStatusListener.onSurfaceDestroy();
    }

    private void startBackgroundThread() {
        HandlerThread backgroundThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        mBackgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (mBackgroundHandler != null) {
            mBackgroundHandler.getLooper().quitSafely();
            mBackgroundHandler = null;
        }
    }

}
