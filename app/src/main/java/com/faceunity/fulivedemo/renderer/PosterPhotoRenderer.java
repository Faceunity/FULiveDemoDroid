package com.faceunity.fulivedemo.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author LiuQiang on 2018.10.09
 * 海报换脸 渲染器
 */
public class PosterPhotoRenderer implements GLSurfaceView.Renderer {
    public static final float[] IMG_DATA_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static final String TAG = "PosterPhotoRenderer";

    private GLSurfaceView mGLSurfaceView;
    private OnRendererStatusListener mOnPhotoRendererStatusListener;
    private int mViewWidth = 1280;
    private int mViewHeight = 720;
    private String mPhotoPath;
    private byte[] mTemplateBytes;
    private byte[] mTemplateRGBABytes;
    private byte[] mPhotoBytes;
    private byte[] mPhotoRGBABytes;
    private int mImgTextureId;
    private int mTemplateWidth = 720;
    private int mTemplateHeight = 1280;
    private int mPhotoWidth = 720;
    private int mPhotoHeight = 1280;
    private volatile boolean mFirstDrawPhoto = true;
    private boolean mDrawPhoto = true;
    private boolean mReRenderTemplate;
    private float[] mMvpPhotoMatrix;
    private float[] mMvpTemplateMatrix = Arrays.copyOf(GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX.length);
    private ProgramTexture2d mFullFrameRectTexture2D;
    private FPSUtil mFPSUtil;
    private int mViewPortX;
    private int mViewPortY;
    private float mViewPortScale = 1F;
    private volatile String mMixedPhotoPath;
    private int mFrameCount;

    public PosterPhotoRenderer(String photoPath, GLSurfaceView glSurfaceview, OnRendererStatusListener onPhotoRendererStatusListener) {
        mPhotoPath = photoPath;
        mGLSurfaceView = glSurfaceview;
        mOnPhotoRendererStatusListener = onPhotoRendererStatusListener;
        mFPSUtil = new FPSUtil();
    }

    public void onCreate() {
        mGLSurfaceView.onResume();
    }

    public void onDestroy() {
        final CountDownLatch count = new CountDownLatch(1);
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "onDestroy: ", e);
        }
        mGLSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated() called");
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mDrawPhoto = true;
        mOnPhotoRendererStatusListener.onSurfaceCreated(gl, config);
        if (!TextUtils.isEmpty(mMixedPhotoPath)) {
            mFirstDrawPhoto = false;
            loadMixedPhoto(mMixedPhotoPath);
        } else {
            mFirstDrawPhoto = true;
            loadPhotoData(mPhotoPath, true);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d(TAG, "onSurfaceChanged() called  viewWidth = [" + width + "], viewHeight = [" + height + "], photoWidth:"
                + mPhotoWidth + ", photoHeight:" + mPhotoHeight);
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);
        mMvpPhotoMatrix = GlUtil.changeMVPMatrixInside(mViewWidth, mViewHeight, mPhotoWidth, mPhotoHeight);
        Matrix.rotateM(mMvpPhotoMatrix, 0, 90, 0, 0, 1);
        float scale = (float) mViewWidth * mPhotoHeight / mViewHeight / mPhotoWidth;
        if (scale > 1) {
            mViewPortY = 0;
            mViewPortScale = (float) mViewHeight / mPhotoHeight;
            mViewPortX = (int) ((mViewWidth - mViewPortScale * mPhotoWidth) / 2);
        } else if (scale < 1) {
            mViewPortX = 0;
            mViewPortScale = (float) mViewWidth / mPhotoWidth;
            mViewPortY = (int) ((mViewHeight - mViewPortScale * mPhotoHeight) / 2);
        } else {
            mViewPortX = 0;
            mViewPortY = 0;
            mViewPortScale = (float) mViewWidth / mPhotoWidth;
        }

        mOnPhotoRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) {
            return;
        }

        float[] matrix;
        if (mDrawPhoto) {
            matrix = mMvpPhotoMatrix;
        } else {
            matrix = mMvpTemplateMatrix;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        int fuTextureId = mOnPhotoRendererStatusListener.onDrawFrame(mImgTextureId, mPhotoWidth, mPhotoHeight);
        mFullFrameRectTexture2D.drawFrame(fuTextureId, IMG_DATA_MATRIX, matrix);
        // 解决前几帧黑屏问题
        if (mFrameCount++ >= 2) {
            if (mFirstDrawPhoto) {
                mFirstDrawPhoto = false;
                mDrawPhoto = false;
                mOnPhotoRendererStatusListener.onPhotoLoaded(mPhotoBytes, mPhotoWidth, mPhotoHeight);
            }
            if (mReRenderTemplate) {
                mReRenderTemplate = false;
                mOnPhotoRendererStatusListener.onTemplateLoaded(mTemplateBytes, mTemplateWidth, mTemplateHeight);
            }
        }

        mFPSUtil.limit();
        mGLSurfaceView.requestRender();
    }

    public void setMixedPhotoPath(String mixedPhotoPath) {
        mMixedPhotoPath = mixedPhotoPath;
    }

    private void loadMixedPhoto(String path) {
        Log.i(TAG, "loadMixedPhoto: path:" + path);
        destroyImageTexture();
        Bitmap src = BitmapUtil.loadBitmap(path, 720);
        mImgTextureId = GlUtil.createImageTexture(src);
        mPhotoWidth = src.getWidth() / 2 * 2;
        mPhotoHeight = src.getHeight() / 2 * 2;
    }

    private void onSurfaceDestroy() {
        destroyImageTexture();
        mFrameCount = 0;
        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }
        mFirstDrawPhoto = true;
        mReRenderTemplate = false;
        mDrawPhoto = true;
        mPhotoRGBABytes = null;
        mPhotoBytes = null;
        mTemplateRGBABytes = null;
        mTemplateBytes = null;
        mMixedPhotoPath = null;

        mOnPhotoRendererStatusListener.onSurfaceDestroy();
    }

    private void destroyImageTexture() {
        if (mImgTextureId != 0) {
            int[] textures = new int[]{mImgTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mImgTextureId = 0;
        }
    }

    private void loadTemplateData(String path) {
        Log.d(TAG, "loadTemplateData: path:" + path);
        Bitmap src = BitmapUtil.loadBitmap(path, 720);
        mTemplateRGBABytes = new byte[src.getByteCount()];
        ByteBuffer rgbaBuffer = ByteBuffer.wrap(mTemplateRGBABytes);
        src.copyPixelsToBuffer(rgbaBuffer);
        mTemplateWidth = src.getWidth();
        mTemplateHeight = src.getHeight();
        mTemplateBytes = BitmapUtil.getNV21(mTemplateWidth, mTemplateHeight, src);
        mMvpTemplateMatrix = GlUtil.changeMVPMatrixInside(mViewWidth, mViewHeight, mTemplateWidth, mTemplateHeight);
        Matrix.rotateM(mMvpTemplateMatrix, 0, 90, 0, 0, 1);
    }

    private void loadPhotoData(String path, boolean createTexture) {
        Log.d(TAG, "loadPhotoData: path:" + path + ", createTexture:" + createTexture);
        Bitmap src = BitmapUtil.loadBitmap(path, 720);
        if (src != null) {
            if (createTexture) {
                mImgTextureId = GlUtil.createImageTexture(src);
            }
            mPhotoRGBABytes = new byte[src.getByteCount()];
            ByteBuffer rgbaBuffer = ByteBuffer.wrap(mPhotoRGBABytes);
            src.copyPixelsToBuffer(rgbaBuffer);
            mPhotoWidth = src.getWidth();
            mPhotoHeight = src.getHeight();
            mPhotoBytes = BitmapUtil.getNV21(mPhotoWidth, mPhotoHeight, src);
        } else {
            mOnPhotoRendererStatusListener.onLoadPhotoError("图片加载失败");
        }
    }

    public void reloadTemplateData(String path) {
        Log.d(TAG, "reloadTemplateData: " + path);
        if (mPhotoBytes == null) {
            loadPhotoData(mPhotoPath, false);
            mMvpPhotoMatrix = GlUtil.changeMVPMatrixInside(mViewWidth, mViewHeight, mPhotoWidth, mPhotoHeight);
            Matrix.rotateM(mMvpPhotoMatrix, 0, 90, 0, 0, 1);
            mFirstDrawPhoto = true;
            mDrawPhoto = false;
        } else {
            loadTemplateData(path);
        }
        mReRenderTemplate = true;
    }

    public float[] convertFaceRect(float[] faceRect) {
        float[] newFaceRect = new float[4];
        // 以右下角为顶点计算
        newFaceRect[2] = (mPhotoWidth - faceRect[0]) * mViewPortScale + mViewPortX;
        newFaceRect[3] = mViewHeight - faceRect[1] * mViewPortScale - mViewPortY;
        newFaceRect[0] = (mPhotoWidth - faceRect[2]) * mViewPortScale + mViewPortX;
        newFaceRect[1] = mViewHeight - faceRect[3] * mViewPortScale - mViewPortY;
        return newFaceRect;
    }

    public void setDrawPhoto(boolean drawPhoto) {
        mDrawPhoto = drawPhoto;
    }

    public byte[] getPhotoRGBABytes() {
        return mPhotoRGBABytes;
    }

    public byte[] getTemplateRGBABytes() {
        return mTemplateRGBABytes;
    }

    public byte[] getTemplateBytes() {
        return mTemplateBytes;
    }

    public int getTemplateWidth() {
        return mTemplateWidth;
    }

    public int getTemplateHeight() {
        return mTemplateHeight;
    }

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(int photoTextureId, int photoWidth, int photoHeight);

        void onSurfaceDestroy();

        /**
         * 图片加载完成
         *
         * @param img
         * @param width
         * @param height
         */
        void onPhotoLoaded(byte[] img, int width, int height);

        /**
         * 模板加载完成
         *
         * @param img
         * @param width
         * @param height
         */
        void onTemplateLoaded(byte[] img, int width, int height);

        /**
         * 图片加载出现错误
         *
         * @param error
         */
        void onLoadPhotoError(String error);
    }
}
