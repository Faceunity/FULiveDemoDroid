package com.faceunity.fulivedemo.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by tujh on 2018/3/2.
 */

public class PhotoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = PhotoRenderer.class.getSimpleName();

    public static final float[] IMG_DATA_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    public static final float[] ROTATE_90 = {0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private GLSurfaceView mGLSurfaceView;

    private void loadImgData(String path) {
        Log.e(TAG, "loadImgData");
        Bitmap src = BitmapUtil.loadBitmap(path, 720);
        if (src == null) {
            mOnPhotoRendererStatusListener.onLoadPhotoError("图片加载失败");
            return;
        }
        mImgTextureId = GlUtil.createImageTexture(src);
        mPhotoBytes = BitmapUtil.getNV21(mPhotoWidth = src.getWidth() / 2 * 2, mPhotoHeight = src.getHeight() / 2 * 2, src);
    }

    private OnRendererStatusListener mOnPhotoRendererStatusListener;

    private String mPhotoPath;
    private byte[] mPhotoBytes;
    private int mImgTextureId;
    private int mPhotoWidth = 720;
    private int mPhotoHeight = 1280;

    private float[] mvp = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;

    private FPSUtil mFPSUtil;

    public PhotoRenderer(String photoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onPhotoRendererStatusListener) {
        mPhotoPath = photoPath;
        mGLSurfaceView = glSurfaceView;
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
        mFullFrameRectTexture2D = new ProgramTexture2d();
        loadImgData(mPhotoPath);
        mOnPhotoRendererStatusListener.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mvp = GlUtil.changeMVPMatrixCrop(ROTATE_90, width, height, mPhotoWidth, mPhotoHeight);
        mOnPhotoRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) {
            return;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        int fuTextureId = mOnPhotoRendererStatusListener.onDrawFrame(mPhotoBytes, mImgTextureId, mPhotoWidth, mPhotoHeight);
        mFullFrameRectTexture2D.drawFrame(fuTextureId, IMG_DATA_MATRIX, mvp);

        mFPSUtil.limit();
        mGLSurfaceView.requestRender();
    }

    private void onSurfaceDestroy() {
        if (mImgTextureId != 0) {
            int[] textures = new int[]{mImgTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mImgTextureId = 0;
        }

        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }

        mOnPhotoRendererStatusListener.onSurfaceDestroy();
    }

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight);

        void onSurfaceDestroy();

        void onLoadPhotoError(String error);
    }

}
