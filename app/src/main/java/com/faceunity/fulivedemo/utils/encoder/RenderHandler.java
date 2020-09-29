package com.faceunity.fulivedemo.utils.encoder;

import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.EglCore;
import com.faceunity.gles.core.Program;
import com.faceunity.gles.core.WindowSurface;

public final class RenderHandler implements Runnable {
    private static final String TAG = RenderHandler.class.getSimpleName();
    private static final boolean DEBUG = false;

    private final Object mLock = new Object();
    private EGLContext mShardContext;
    private Surface mSurface;
    private int mTexId;
    private float[] mtx = new float[16];
    private float[] mvp = new float[16];

    private volatile boolean mRequestSetEglContext;
    private volatile boolean mRequestRelease;
    private volatile int mRequestDraw;

    private WindowSurface mInputWindowSurface;
    private EglCore mEglCore;
    private Program mFullScreen;

    public static RenderHandler createHandler(final String name) {
        if (DEBUG)
            Log.v(TAG, "createHandler:");
        final RenderHandler handler = new RenderHandler();
        synchronized (handler.mLock) {
            new Thread(handler, !TextUtils.isEmpty(name) ? name : TAG).start();
            try {
                handler.mLock.wait();
            } catch (final InterruptedException e) {
            }
        }
        return handler;
    }

    public final void setEglContext(final EGLContext sharedContext, final Surface surface, final int texId) {
        if (DEBUG)
            Log.i(TAG, "setEglContext:");
        synchronized (mLock) {
            if (mRequestRelease)
                return;
            mShardContext = sharedContext;
            mTexId = texId;
            mSurface = surface;
            mRequestSetEglContext = true;
            Matrix.setIdentityM(mtx, 0);
            Matrix.setIdentityM(mvp, 0);
            mLock.notifyAll();
            try {
                mLock.wait();
            } catch (final InterruptedException e) {
            }
        }
    }

    public final void draw(final int texId) {
        draw(texId, mtx, mvp);
    }

    public final void draw(final int texId, final float[] texMatrix) {
        draw(texId, texMatrix, mvp);
    }

    public final void draw(final int texId, final float[] texMatrix, final float[] mvpMatrix) {
        synchronized (mLock) {
            if (mRequestRelease)
                return;
            mTexId = texId;
            System.arraycopy(texMatrix, 0, mtx, 0, texMatrix.length);
            System.arraycopy(mvpMatrix, 0, mvp, 0, mvpMatrix.length);
            mRequestDraw++;
            mLock.notifyAll();
/*			try {
				mLock.wait();
			} catch (final InterruptedException e) {
			} */
        }
    }

    public boolean isValid() {
        synchronized (mLock) {
            return mSurface == null || ((Surface) mSurface).isValid();
        }
    }

    public final void release() {
        if (DEBUG)
            Log.i(TAG, "release:");
        synchronized (mLock) {
            if (mRequestRelease)
                return;
            mRequestRelease = true;
            mLock.notifyAll();
            try {
                mLock.wait();
            } catch (final InterruptedException e) {
            }
        }
    }

//********************************************************************************
//********************************************************************************

    @Override
    public final void run() {
        if (DEBUG)
            Log.i(TAG, "RenderHandler thread started:");
        synchronized (mLock) {
            mRequestSetEglContext = mRequestRelease = false;
            mRequestDraw = 0;
            mLock.notifyAll();
        }
        boolean localRequestDraw;
        for (; ; ) {
            synchronized (mLock) {
                if (mRequestRelease)
                    break;
                if (mRequestSetEglContext) {
                    mRequestSetEglContext = false;
                    internalPrepare();
                }
                localRequestDraw = mRequestDraw > 0;
                if (localRequestDraw) {
                    mRequestDraw--;
//                mLock.notifyAll();
                }
            }
            if (localRequestDraw) {
                if ((mEglCore != null) && mTexId >= 0) {
                    mInputWindowSurface.makeCurrent();
                    // clear screen with yellow color so that you can see rendering rectangle
                    GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                    mFullScreen.drawFrame(mTexId, mtx, mvp);
                    mInputWindowSurface.swapBuffers();
                }
            } else {
                synchronized (mLock) {
                    try {
                        mLock.wait();
                    } catch (final InterruptedException e) {
                        break;
                    }
                }
            }
        }
        synchronized (mLock) {
            mRequestRelease = true;
            internalRelease();
            mLock.notifyAll();
        }
        if (DEBUG)
            Log.i(TAG, "RenderHandler thread finished:");
    }

    private void internalPrepare() {
        if (DEBUG)
            Log.i(TAG, "internalPrepare:");
        internalRelease();
        mEglCore = new EglCore(mShardContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mSurface, true);
        mInputWindowSurface.makeCurrent();
        mFullScreen = new ProgramTexture2d();
        mSurface = null;
        mLock.notifyAll();
    }

    private void internalRelease() {
        if (DEBUG)
            Log.i(TAG, "internalRelease:");
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
        if (mFullScreen != null) {
            mFullScreen.release();
            mFullScreen = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
    }

}
