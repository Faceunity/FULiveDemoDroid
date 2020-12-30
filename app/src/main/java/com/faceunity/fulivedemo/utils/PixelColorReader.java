package com.faceunity.fulivedemo.utils;

import android.opengl.GLES20;

import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * 读取像素颜色
 *
 * @author Richie on 2020.08.19
 */
public class PixelColorReader {
    private int mViewWidth;
    private int mViewHeight;
    private ProgramTexture2d mProgramTexture2d;
    private final ByteBuffer mByteBuffer;
    private final byte[] mByteArray = new byte[4];
    private final int[] mTexId = new int[1];
    private final int[] mFboId = new int[1];
    private final int[] mViewport = new int[4];
    private float[] mMvpMatrix;
    private float[] mTexMatrix;
    private int mCenterX;
    private int mCenterY;
    private int mTexture;
    private OnReadRgbaListener mOnReadRgbaListener;

    public PixelColorReader() {
        mByteBuffer = ByteBuffer.allocateDirect(4);
        mByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void setViewSize(int viewWidth, int viewHeight) {
        mViewWidth = viewWidth;
        mViewHeight = viewHeight;
    }

    public void setClickPosition(int centerX, int centerY) {
        mCenterX = centerX;
        mCenterY = centerY;
    }

    public void setDrawMatrix(float[] mvpMatrix, float[] texMatrix) {
        mMvpMatrix = Arrays.copyOf(mvpMatrix, mvpMatrix.length);
        mTexMatrix = Arrays.copyOf(texMatrix, texMatrix.length);
    }

    public void setDrawTexture(int texture) {
        mTexture = texture;
    }

    public void setOnReadRgbaListener(OnReadRgbaListener onReadRgbaListener) {
        mOnReadRgbaListener = onReadRgbaListener;
    }

    private final Runnable mCreateRunnable = new Runnable() {
        @Override
        public void run() {
            GlUtil.createFrameBuffers(mTexId, mFboId, mViewWidth, mViewHeight);
            mProgramTexture2d = new ProgramTexture2d();
        }
    };

    public Runnable create() {
        return mCreateRunnable;
    }

    private final Runnable mDrawRunnable = new Runnable() {
        @Override
        public void run() {
            int[] viewport = mViewport;
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[0]);
            GLES20.glViewport(0, 0, mViewWidth, mViewHeight);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            mProgramTexture2d.drawFrame(mTexture, mTexMatrix, mMvpMatrix);
            ByteBuffer byteBuffer = mByteBuffer;
            byteBuffer.clear();
            GLES20.glReadPixels(mCenterX, mCenterY, 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, byteBuffer);
            GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, GLES20.GL_NONE);
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_NONE);
            byteBuffer.rewind();
            byte[] byteArray = mByteArray;
            byteBuffer.get(byteArray);
            int r = ((int) byteArray[0]) & 0xFF;
            int g = ((int) byteArray[1]) & 0xFF;
            int b = ((int) byteArray[2]) & 0xFF;
            int a = ((int) byteArray[3]) & 0xFF;
            if (mOnReadRgbaListener != null) {
                mOnReadRgbaListener.onReadRgba(r, g, b, a);
            }
        }
    };

    public Runnable draw() {
        return mDrawRunnable;
    }

    private final Runnable mDestroyRunnable = new Runnable() {
        @Override
        public void run() {
            mTexture = -1;
            GlUtil.deleteFrameBuffers(mFboId);
            mFboId[0] = -1;
            GlUtil.deleteTextures(mTexId);
            mTexId[0] = -1;
            if (mProgramTexture2d != null) {
                mProgramTexture2d.release();
                mProgramTexture2d = null;
            }
        }
    };

    public Runnable destroy() {
        return mDestroyRunnable;
    }

    public interface OnReadRgbaListener {
        /**
         * 读到 rgba 值
         *
         * @param r
         * @param g
         * @param b
         * @param a
         */
        void onReadRgba(int r, int g, int b, int a);
    }
}
