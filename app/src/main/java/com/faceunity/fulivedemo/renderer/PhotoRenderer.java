package com.faceunity.fulivedemo.renderer;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.faceunity.fulivedemo.utils.LimitFpsUtil;
import com.faceunity.gles.ProgramLandmarks;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 使用 GLSurfaceView 渲染图像，采用居中裁剪（CenterCrop）的方式显示
 * <p>
 * Created by tujh on 2018/3/2.
 */
public class PhotoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = PhotoRenderer.class.getSimpleName();
    public static final float[] IMG_DATA_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    public static final float[] ROTATE_90 = {0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private static final int REQ_PHOTO_WIDTH = 1080;
    private static final int REQ_PHOTO_HEIGHT = 1920;

    private GLSurfaceView mGlSurfaceView;
    private OnRendererStatusListener mOnRendererStatusListener;
    private String mPhotoPath;
    private int mPhotoTexId;
    private int mPhotoWidth;
    private int mPhotoHeight;
    private int mViewWidth;
    private int mViewHeight;
    private float[] mMvpMatrix;
    private float[] mLandmarksData;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramLandmarks mProgramLandmarks;
    private int m2DTexId;

    public PhotoRenderer(String photoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mPhotoPath = photoPath;
        mGlSurfaceView = glSurfaceView;
        mOnRendererStatusListener = onRendererStatusListener;
    }

    public void onCreate() {
        mGlSurfaceView.onResume();
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
            count.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignored
        }
        mGlSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated. Thread:" + Thread.currentThread().getName());
        mProgramTexture2d = new ProgramTexture2d();
        mProgramLandmarks = new ProgramLandmarks();
        loadPhoto(mPhotoPath);
        LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS);

        mOnRendererStatusListener.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: viewWidth:" + width + ", viewHeight:" + height + ", photoWidth:"
                + mPhotoWidth + ", photoHeight:" + mPhotoHeight + ", textureId:" + mPhotoTexId);
        GLES20.glViewport(0, 0, width, height);
        float[] mvpMatrix = GlUtil.changeMvpMatrixInside(width, height, mPhotoWidth, mPhotoHeight);
        Matrix.rotateM(mvpMatrix, 0, 90, 0, 0, 1);
        mMvpMatrix = mvpMatrix;
        mViewHeight = height;
        mViewWidth = width;

        mOnRendererStatusListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mProgramTexture2d == null) {
            return;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        int fuTexId = mOnRendererStatusListener.onDrawFrame(mPhotoTexId, mPhotoWidth, mPhotoHeight);
        mProgramTexture2d.drawFrame(fuTexId, IMG_DATA_MATRIX, mMvpMatrix);
        m2DTexId = fuTexId;

        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS && mLandmarksData != null) {
            mProgramLandmarks.refresh(mLandmarksData, mPhotoWidth, mPhotoHeight, 90,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mMvpMatrix);
            mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
        }

        LimitFpsUtil.limitFrameRate();
        mGlSurfaceView.requestRender();
    }

    public void setLandmarksData(float[] landmarksData) {
        mLandmarksData = landmarksData;
    }

    private void loadPhoto(String path) {
        Bitmap bitmap = BitmapUtil.loadBitmap(path, REQ_PHOTO_WIDTH, REQ_PHOTO_HEIGHT);
        if (bitmap == null) {
            mOnRendererStatusListener.onLoadPhotoError("图片加载失败:" + path);
            return;
        }

        mPhotoTexId = GlUtil.createImageTexture(bitmap);
        mPhotoWidth = bitmap.getWidth();
        mPhotoHeight = bitmap.getHeight();
        Log.i(TAG, "loadPhoto: path:" + path + ", width:" + mPhotoWidth + ", height:"
                + mPhotoHeight + ", texId:" + mPhotoTexId);
    }

    private void onSurfaceDestroy() {
        Log.d(TAG, "onSurfaceDestroy");
        if (mPhotoTexId != 0) {
            int[] textures = new int[]{mPhotoTexId};
            GLES20.glDeleteTextures(1, textures, 0);
            mPhotoTexId = 0;
        }
        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }
        m2DTexId = -1;

        mOnRendererStatusListener.onSurfaceDestroy();
    }

    public int getViewWidth() {
        return mViewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public float[] getTexMatrix() {
        return IMG_DATA_MATRIX;
    }

    public float[] getMvpMatrix() {
        return mMvpMatrix;
    }

    public int get2dTexture() {
        return m2DTexId;
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
         * Called when drawing current frame.
         *
         * @param photoTexId
         * @param photoWidth
         * @param photoHeight
         * @return
         */
        int onDrawFrame(int photoTexId, int photoWidth, int photoHeight);

        /**
         * Called when surface is destroyed
         */
        void onSurfaceDestroy();

        /**
         * Called when error happened
         *
         * @param error
         */
        void onLoadPhotoError(String error);
    }

}
