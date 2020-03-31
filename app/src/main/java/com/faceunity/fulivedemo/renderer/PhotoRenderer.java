package com.faceunity.fulivedemo.renderer;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.faceunity.fulivedemo.utils.FPSUtil;
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

    private GLSurfaceView mGlSurfaceView;
    private OnRendererStatusListener mOnRendererStatusListener;
    private String mPhotoPath;
    private byte[] mPhotoNV21Bytes;
    private int mPhotoTextureId;
    private int mPhotoWidth = 720;
    private int mPhotoHeight = 1280;
    private int mViewWidth;
    private int mViewHeight;
    private float[] mMvpMatrix;
    private float[] mLandmarksData;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramLandmarks mProgramLandmarks;
    private FPSUtil mFPSUtil;

    public PhotoRenderer(String photoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mPhotoPath = photoPath;
        mGlSurfaceView = glSurfaceView;
        mOnRendererStatusListener = onRendererStatusListener;
        mFPSUtil = new FPSUtil();
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
            count.await(1, TimeUnit.SECONDS);
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
        mOnRendererStatusListener.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged: viewWidth:" + width + ", viewHeight:" + height + ", photoWidth:"
                + mPhotoWidth + ", photoHeight:" + mPhotoHeight + ", textureId:" + mPhotoTextureId);
        mViewHeight = height;
        mViewWidth = width;
        GLES20.glViewport(0, 0, width, height);
        mMvpMatrix = GlUtil.changeMVPMatrixInside(width, height, mPhotoWidth, mPhotoHeight);
        Matrix.rotateM(mMvpMatrix, 0, 90, 0, 0, 1);
        mFPSUtil.resetLimit();

        mOnRendererStatusListener.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mProgramTexture2d == null || mPhotoNV21Bytes == null) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        int fuTextureId = mOnRendererStatusListener.onDrawFrame(mPhotoNV21Bytes, mPhotoTextureId, mPhotoWidth, mPhotoHeight);
        mProgramTexture2d.drawFrame(fuTextureId, IMG_DATA_MATRIX, mMvpMatrix);

        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS && mLandmarksData != null) {
            mProgramLandmarks.refresh(mLandmarksData, mPhotoWidth, mPhotoHeight, 90,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mMvpMatrix);
            mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
        }

        mFPSUtil.limit();
        mGlSurfaceView.requestRender();
    }

    public void setLandmarksData(float[] landmarksData) {
        mLandmarksData = landmarksData;
    }

    private void loadPhoto(String path) {
        Log.d(TAG, "loadPhoto() path:" + path);
        Bitmap bitmap = BitmapUtil.loadBitmap(path, mPhotoWidth, mPhotoHeight);
        if (bitmap == null) {
            mOnRendererStatusListener.onLoadPhotoError("图片加载失败:" + path);
            return;
        }

        mPhotoTextureId = GlUtil.createImageTexture(bitmap);
        mPhotoWidth = bitmap.getWidth() / 2 * 2;
        mPhotoHeight = bitmap.getHeight() / 2 * 2;
        mPhotoNV21Bytes = BitmapUtil.getNV21(mPhotoWidth, mPhotoHeight, bitmap);
    }

    private void onSurfaceDestroy() {
        Log.d(TAG, "onSurfaceDestroy");
        if (mPhotoTextureId != 0) {
            int[] textures = new int[]{mPhotoTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mPhotoTextureId = 0;
        }
        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }

        mOnRendererStatusListener.onSurfaceDestroy();
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
         * @param photoNV21Bytes
         * @param photoTextureId
         * @param photoWidth
         * @param photoHeight
         * @return
         */
        int onDrawFrame(byte[] photoNV21Bytes, int photoTextureId, int photoWidth, int photoHeight);

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
