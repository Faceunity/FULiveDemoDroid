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
import android.view.MotionEvent;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.LimitFpsUtil;
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
    public static boolean ENABLE_DRAW_LANDMARKS = false;
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
    private float[] mTexMatrix = Arrays.copyOf(TEXTURE_MATRIX, TEXTURE_MATRIX.length);
    protected byte[] mCameraNv21Byte;
    private byte[] mNv21ByteCopy;
    protected SurfaceTexture mSurfaceTexture;
    protected GLSurfaceView mGlSurfaceView;
    protected Activity mActivity;
    protected Handler mBackgroundHandler;
    protected boolean mIsPreviewing;
    protected Bitmap mShotBitmap;
    protected OnRendererStatusListener mOnRendererStatusListener;
    private ProgramTextureOES mProgramTextureOES;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramLandmarks mProgramLandmarks;
    private float[] mLandmarksData;
    private int m2DTexId;
    private int mBitmap2dTexId;
    /* 全身 avatar 相关 */
    private boolean mRenderRotatedImage;
    private boolean mDrawSmallViewport;
    private int mSmallViewportWidth;
    private int mSmallViewportHeight;
    private int mSmallViewportX;
    private int mSmallViewportY;
    private int mSmallViewportHorizontalPadding;
    private int mSmallViewportTopPadding;
    private int mSmallViewportBottomPadding;
    private int mTouchX;
    private int mTouchY;

    protected BaseCameraRenderer(Activity activity, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mGlSurfaceView = glSurfaceView;
        mActivity = activity;
        mOnRendererStatusListener = onRendererStatusListener;
        initCameraInfo();
        mSmallViewportWidth = activity.getResources().getDimensionPixelSize(R.dimen.x180);
        mSmallViewportHeight = activity.getResources().getDimensionPixelSize(R.dimen.x320);
        mSmallViewportHorizontalPadding = activity.getResources().getDimensionPixelSize(R.dimen.x32);
        mSmallViewportTopPadding = activity.getResources().getDimensionPixelSize(R.dimen.x176);
        mSmallViewportBottomPadding = activity.getResources().getDimensionPixelSize(R.dimen.x200);
    }

    public void setCameraFacing(int cameraFacing) {
        mCameraFacing = cameraFacing;
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
                openCamera(mCameraFacing);
                startPreview();
            }
        });
        LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS);
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
        mSmallViewportX = width - mSmallViewportWidth - mSmallViewportHorizontalPadding;
        mSmallViewportY = mSmallViewportBottomPadding;
        mOnRendererStatusListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mProgramTexture2d == null || mSurfaceTexture == null) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);
        if (mShotBitmap == null) {
            try {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mTexMatrix);
            } catch (Exception e) {
                Log.e(TAG, "onDrawFrame: ", e);
            }
        }

        if (!mIsStopPreview) {
            if (mCameraNv21Byte != null) {
                if (mNv21ByteCopy == null) {
                    mNv21ByteCopy = new byte[mCameraNv21Byte.length];
                }
                System.arraycopy(mCameraNv21Byte, 0, mNv21ByteCopy, 0, mCameraNv21Byte.length);
            }
            if (mNv21ByteCopy != null) {
                m2DTexId = mOnRendererStatusListener.onDrawFrame(mNv21ByteCopy, mCameraTexId,
                        mCameraWidth, mCameraHeight, mMvpMatrix, mTexMatrix, mSurfaceTexture.getTimestamp());
            }
        }

        if (!mIsSwitchCamera) {
            if (m2DTexId > 0) {
                mProgramTexture2d.drawFrame(m2DTexId, mRenderRotatedImage ? GlUtil.IDENTITY_MATRIX : mTexMatrix, mMvpMatrix);
            } else if (mCameraTexId > 0) {
                mProgramTextureOES.drawFrame(mCameraTexId, mTexMatrix, mMvpMatrix);
            }
            if (mDrawSmallViewport) {
                GLES20.glViewport(mSmallViewportX, mSmallViewportY, mSmallViewportWidth, mSmallViewportHeight);
                mProgramTextureOES.drawFrame(mCameraTexId, mTexMatrix, GlUtil.IDENTITY_MATRIX);
                GLES20.glViewport(0, 0, mViewWidth, mViewHeight);
            }

            if (ENABLE_DRAW_LANDMARKS && mLandmarksData != null) {
                mProgramLandmarks.refresh(mLandmarksData, mCameraWidth, mCameraHeight, mCameraOrientation, mCameraFacing, mMvpMatrix);
                mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
            }
        }

        LimitFpsUtil.limitFrameRate();
        if (!mIsStopPreview) {
            mGlSurfaceView.requestRender();
        }
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
            countDownLatch.await(500, TimeUnit.MILLISECONDS);
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

    public void setRenderRotatedImage(boolean renderRotatedImage) {
        mRenderRotatedImage = renderRotatedImage;
        mDrawSmallViewport = renderRotatedImage;
    }

    public void onTouchEvent(int x, int y, int action) {
        if (!mDrawSmallViewport) {
            return;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            if (x < mSmallViewportHorizontalPadding || x > mViewWidth - mSmallViewportHorizontalPadding
                    || y < mSmallViewportTopPadding || y > mViewHeight - mSmallViewportBottomPadding) {
                return;
            }
            int touchX = mTouchX;
            int touchY = mTouchY;
            mTouchX = x;
            mTouchY = y;
            int distanceX = x - touchX;
            int distanceY = y - touchY;
            int viewportX = mSmallViewportX;
            int viewportY = mSmallViewportY;
            viewportX += distanceX;
            viewportY -= distanceY;
            if (viewportX < mSmallViewportHorizontalPadding || viewportX + mSmallViewportWidth > mViewWidth - mSmallViewportHorizontalPadding
                    || mViewHeight - viewportY - mSmallViewportHeight < mSmallViewportTopPadding
                    || viewportY < mSmallViewportBottomPadding) {
                return;
            }
            mSmallViewportX = viewportX;
            mSmallViewportY = viewportY;
        } else if (action == MotionEvent.ACTION_DOWN) {
            mTouchX = x;
            mTouchY = y;
        } else if (action == MotionEvent.ACTION_UP) {
            boolean alignLeft = mSmallViewportX < mViewWidth / 2;
            mSmallViewportX = alignLeft ? mSmallViewportHorizontalPadding : mViewWidth - mSmallViewportHorizontalPadding - mSmallViewportWidth;
            mTouchX = 0;
            mTouchY = 0;
        }
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
        Log.d(TAG, "hideImageTexture() called");
        mShotBitmap = null;
        mIsStopPreview = false;
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mMvpMatrix = GlUtil.changeMVPMatrixCrop(mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
                deleteBitmapTexId();
            }
        });
        mGlSurfaceView.requestRender();
    }

    public void showImageTexture(final Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        Log.d(TAG, "showImageTexture() called with: bitmap = [" + bitmap + "]");
        mIsStopPreview = true;
        mShotBitmap = bitmap;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                deleteBitmapTexId();
                mBitmap2dTexId = GlUtil.createImageTexture(bitmap);
                m2DTexId = mBitmap2dTexId;
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

    private void deleteBitmapTexId() {
        if (mBitmap2dTexId > 0) {
            GlUtil.deleteTextureId(new int[]{mBitmap2dTexId});
            mBitmap2dTexId = 0;
        }
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
        mCameraNv21Byte = null;
        mNv21ByteCopy = null;
    }

    private void destroyGlSurface() {
        deleteBitmapTexId();
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
