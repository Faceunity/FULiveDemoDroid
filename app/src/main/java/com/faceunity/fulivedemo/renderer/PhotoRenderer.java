package com.faceunity.fulivedemo.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.faceunity.fulivedemo.utils.BitmapUtil;
import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by tujh on 2018/3/2.
 */

public class PhotoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = PhotoRenderer.class.getSimpleName();

    public static final float[] imgDataMatrix = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    public static final float[] ROTATE_90 = {0.0F, 1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F};
    private GLSurfaceView mGLSurfaceView;

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight);

        void onSurfaceDestroy();
    }

    private OnRendererStatusListener mOnPhotoRendererStatusListener;

    private int mViewWidth = 1280;
    private int mViewHeight = 720;

    private String mPhotoPath;

    private byte[] mPhotoBytes;
    private int mImgTextureId;
    private int mPhotoWidth = 720;
    private int mPhotoHeight = 1280;

    private int mFuTextureId;
    private float[] mvp = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;

    private FPSUtil mFPSUtil;

    public PhotoRenderer(String photoPath, GLSurfaceView GLSurfaceView, OnRendererStatusListener onPhotoRendererStatusListener) {
        mPhotoPath = photoPath;
        mGLSurfaceView = GLSurfaceView;
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
            e.printStackTrace();
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
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);
        mvp = GlUtil.changeMVPMatrix(ROTATE_90, mViewWidth, mViewHeight, mPhotoWidth, mPhotoHeight);
        mOnPhotoRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) return;
        mFuTextureId = mOnPhotoRendererStatusListener.onDrawFrame(mPhotoBytes, mImgTextureId, mPhotoWidth, mPhotoHeight);
        mFullFrameRectTexture2D.drawFrame(mFuTextureId, imgDataMatrix, mvp);

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

    private void loadImgData(String path) {
        Log.e(TAG, "loadImgData");
        try {
            Bitmap src = BitmapUtil.loadBitmap(path, 720);
            mImgTextureId = GlUtil.createImageTexture(src);
            mPhotoBytes = BitmapUtil.getNV21(mPhotoWidth = src.getWidth() / 2 * 2, mPhotoHeight = src.getHeight() / 2 * 2, src);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
