package com.faceunity.utils;

import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;

import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.EglCore;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.gles.core.OffscreenSurface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author Richie on 2020.08.21
 */
public class VideoDecoder implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "VideoDecoder";
    private int mVideoWidth = 1280;
    private int mVideoHeight = 720;
    private String mVideoPath;
    private SurfaceTexture mSurfaceTexture;
    private EGLContext mSharedContext = EGL14.EGL_NO_CONTEXT;
    private ProgramTextureOES mProgramTextureOES;
    private OffscreenSurface mOffscreenSurface;
    private EglCore mEglCore;
    private MediaPlayer mMediaPlayer;
    private ByteBuffer mRgbaBuffer;
    private int mVideoTexId;
    private float[] mMvpMatrix;
    private float[] mTexMatrix = new float[16];
    private int[] mTextures = new int[1];
    private int[] mFrameBuffers = new int[1];
    private byte[] mRgbaByte;
    private volatile boolean mIsStopDecode;
    private Handler mDecodeHandler;
    private Surface mSurface;
    private OnReadPixelListener mOnReadPixelListener;
    private boolean mIsFrontCam;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        try {
            surfaceTexture.updateTexImage();
        } catch (Exception e) {
            return;
        }
        surfaceTexture.getTransformMatrix(mTexMatrix);
        int drawWidth = mVideoWidth;
        int drawHeight = mVideoHeight;
        GLES20.glViewport(0, 0, drawWidth, drawHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        if (mProgramTextureOES != null) {
            mProgramTextureOES.drawFrame(mVideoTexId, mTexMatrix, mMvpMatrix);
        }
        ByteBuffer rgbaBuffer = mRgbaBuffer;
        rgbaBuffer.rewind();
        GLES20.glReadPixels(0, 0, drawWidth, drawHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, rgbaBuffer);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        rgbaBuffer.rewind();
        rgbaBuffer.get(mRgbaByte);
        if (mOnReadPixelListener != null && !mIsStopDecode) {
            mOnReadPixelListener.onReadPixel(drawWidth, drawHeight, mRgbaByte);
        }
    }

    public void setOnReadPixelListener(OnReadPixelListener onReadPixelListener) {
        mOnReadPixelListener = onReadPixelListener;
    }

    public void create(EGLContext sharedContext, boolean isFrontCam) {
        Log.d(TAG, "create() called with: sharedContext = [" + sharedContext + "], isFrontCam = [" + isFrontCam + "]");
        mSharedContext = sharedContext;
        mIsFrontCam = isFrontCam;
        HandlerThread handlerThread = new HandlerThread("video_decoder");
        handlerThread.start();
        mDecodeHandler = new Handler(handlerThread.getLooper());
    }

    public void setFrontCam(final boolean frontCam) {
        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                mIsFrontCam = frontCam;
                computeDrawParams();
            }
        });
    }

    private void computeDrawParams() {
        mMvpMatrix = Arrays.copyOf(GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX.length);
        Matrix.rotateM(mMvpMatrix, 0, 180, 0, 0, 1);
        Matrix.scaleM(mMvpMatrix, 0, mIsFrontCam ? 1 : -1, 1, 1);
    }

    public void start(String videoPath) {
        Log.d(TAG, "start: ");
        mVideoPath = videoPath;
        mIsStopDecode = false;
        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                retrieveVideoInfo();
                createSurface();
                createMediaPlayer();
            }
        });
    }

    public void stop() {
        Log.d(TAG, "stop: ");
        if (mIsStopDecode) {
            return;
        }
        mIsStopDecode = true;
        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                releaseMediaPlayer();
                releaseSurface();
            }
        });
    }

    public void release() {
        Log.d(TAG, "release");
        stop();
        mDecodeHandler.getLooper().quitSafely();
    }

    private void retrieveVideoInfo() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(mVideoPath);
            String sVideoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            mVideoWidth = Integer.parseInt(sVideoWidth);
            String sVideoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            mVideoHeight = Integer.parseInt(sVideoHeight);
        } catch (Exception e) {
            Log.e(TAG, "MediaMetadataRetriever extractMetadata: ", e);
        } finally {
            mediaMetadataRetriever.release();
        }
        int capacity = mVideoWidth * mVideoHeight * 4;
        mRgbaBuffer = ByteBuffer.allocate(capacity);
        mRgbaBuffer.order(ByteOrder.LITTLE_ENDIAN);
        mRgbaByte = new byte[capacity];
        computeDrawParams();
        Log.d(TAG, "retrieveVideoInfo DecodeVideoTask path:" + mVideoPath + ", width:" + mVideoWidth + ", height:" + mVideoHeight);
    }

    private void createSurface() {
        Log.d(TAG, "createSurface");
        releaseSurface();
        mEglCore = new EglCore(mSharedContext, 0);
        mOffscreenSurface = new OffscreenSurface(mEglCore, mVideoWidth, mVideoHeight);
        mOffscreenSurface.makeCurrent();
        mVideoTexId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mVideoTexId);
        mProgramTextureOES = new ProgramTextureOES();
        GlUtil.createFrameBuffers(mTextures, mFrameBuffers, mVideoWidth, mVideoHeight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSurfaceTexture.setOnFrameAvailableListener(this, mDecodeHandler);
        } else {
            mSurfaceTexture.setOnFrameAvailableListener(this);
        }
    }

    private void createMediaPlayer() {
        Log.d(TAG, "createMediaPlayer");
        releaseMediaPlayer();
        try {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setVolume(0F, 0F);
            mMediaPlayer.setLooping(true);
            mSurface = new Surface(mSurfaceTexture);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mMediaPlayer.start();
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
                    createMediaPlayer();
                    return true;
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "createMediaPlayer: ", e);
        }
    }

    private void releaseSurface() {
        Log.d(TAG, "releaseSurface");
        if (mSurfaceTexture != null) {
            mSurfaceTexture.setOnFrameAvailableListener(null);
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mProgramTextureOES != null) {
            mProgramTextureOES.release();
            mProgramTextureOES = null;
        }
        if (mFrameBuffers[0] > 0) {
            GlUtil.deleteFrameBuffers(mFrameBuffers);
            mFrameBuffers[0] = -1;
        }
        if (mTextures[0] > 0) {
            GlUtil.deleteTextures(mTextures);
            mTextures[0] = -1;
        }
        if (mVideoTexId > 0) {
            int[] textures = new int[]{mVideoTexId};
            GlUtil.deleteTextures(textures);
            mVideoTexId = -1;
        }
        if (mOffscreenSurface != null) {
            mOffscreenSurface.release();
            mOffscreenSurface = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        mSharedContext = EGL14.EGL_NO_CONTEXT;
    }

    private void releaseMediaPlayer() {
        Log.d(TAG, "releaseMediaPlayer");
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "releaseMediaPlayer: ", e);
            }
            mMediaPlayer = null;
        }
    }

    public interface OnReadPixelListener {
        /**
         * 读到 rgba 数据
         *
         * @param width
         * @param height
         * @param rgba
         */
        void onReadPixel(int width, int height, byte[] rgba);
    }

}
