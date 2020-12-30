package com.faceunity.utils;

import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.EGLContext;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.util.Log;
import android.view.Surface;

import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.EglCore;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.gles.core.OffscreenSurface;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;


/**
 * @author Richie on 2020.08.21
 */
public class VideoDecoder implements SurfaceTexture.OnFrameAvailableListener {
    private static final String TAG = "VideoDecoder";
    private int mVideoWidth = 1;
    private int mVideoHeight = 1;
    private String mVideoPath;
    private SurfaceTexture mSurfaceTexture;
    private ProgramTextureOES mProgramTextureOes;
    private OffscreenSurface mOffscreenSurface;
    private EglCore mEglCore;
    private MediaPlayer mMediaPlayer;
    private int mVideoTexId;
    private final float[] mMvpMatrix = new float[16];
    private final int[] mTextures = new int[1];
    private final int[] mFrameBuffers = new int[1];
    private final int[] mViewport = new int[4];
    private final int[] mFboBackup = new int[1];
    private boolean mIsStopRunning;
    private Handler mDecodeHandler;
    private Surface mSurface;
    private boolean mIsFrontCam = true;
    private OnPlayerStartListener mOnPlayerStartListener;
    private boolean mUseTexture = true;
    private ByteBuffer mRgbaBuffer;
    private byte[] mRgbaByte;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        try {
            surfaceTexture.updateTexImage();
        } catch (Exception e) {
            return;
        }
        int drawWidth = mVideoWidth;
        int drawHeight = mVideoHeight;
        int[] viewport = mViewport;
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
        GLES20.glGetIntegerv(GLES20.GL_FRAMEBUFFER_BINDING, mFboBackup, 0);
        GLES20.glViewport(0, 0, drawWidth, drawHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        if (mProgramTextureOes != null) {
            mProgramTextureOes.drawFrame(mVideoTexId, GlUtil.IDENTITY_MATRIX, mMvpMatrix);
        }

        if (!mUseTexture) {
            ByteBuffer rgbaBuffer = mRgbaBuffer;
            rgbaBuffer.rewind();
            GLES20.glReadPixels(0, 0, drawWidth, drawHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, rgbaBuffer);
            rgbaBuffer.rewind();
            rgbaBuffer.get(mRgbaByte);
            if (mOnPlayerStartListener != null) {
                mOnPlayerStartListener.onReadPixel(drawWidth, drawHeight, mRgbaByte);
            }
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboBackup[0]);
        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    public void create(final EGLContext sharedContext) {
        Log.d(TAG, "create sharedContext " + sharedContext);
        HandlerThread handlerThread = new HandlerThread("video_decoder", Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        Handler decodeHandler = new Handler(handlerThread.getLooper());
        decodeHandler.post(new Runnable() {
            @Override
            public void run() {
                createSurface(sharedContext);
            }
        });
        mDecodeHandler = decodeHandler;
        computeDrawParams();
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

    public void setUseTexture(boolean useTexture) {
        mUseTexture = useTexture;
    }

    public void setOnPlayerStartListener(OnPlayerStartListener onPlayerStartListener) {
        mOnPlayerStartListener = onPlayerStartListener;
    }

    public void start(String videoPath) {
        Log.d(TAG, "start videoPath " + videoPath);
        mIsStopRunning = false;
        mVideoPath = videoPath;
        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                int videoWidth = mVideoWidth;
                int videoHeight = mVideoHeight;
                retrieveVideoInfo();
                if (videoWidth != mVideoWidth || videoHeight != mVideoHeight) {
                    Log.i(TAG, "recreate offscreen surface");
                    int capacity = mVideoWidth * mVideoHeight * 4;
                    ByteBuffer rgbaBuffer = ByteBuffer.allocateDirect(capacity);
                    rgbaBuffer.order(ByteOrder.LITTLE_ENDIAN);
                    mRgbaBuffer = rgbaBuffer;
                    mRgbaByte = new byte[capacity];
                    releaseOffScreenSurface();
                    createOffScreenSurface();
                }
                createMediaPlayer();
            }
        });
    }

    public void stop() {
        Log.d(TAG, "stop");
        if (mIsStopRunning) {
            return;
        }
        mIsStopRunning = true;
        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mMediaPlayer != null) {
                    mMediaPlayer.reset();
                }
                releaseOffScreenSurface();
                mVideoWidth = 1;
                mVideoHeight = 1;
            }
        });
    }

    public void release() {
        Log.d(TAG, "release");
        stop();
        mDecodeHandler.post(new Runnable() {
            @Override
            public void run() {
                releaseMediaPlayer();
                releaseSurface();
            }
        });
        mDecodeHandler.getLooper().quitSafely();
    }

    private void computeDrawParams() {
        Matrix.setIdentityM(mMvpMatrix, 0);
        Matrix.scaleM(mMvpMatrix, 0, mIsFrontCam ? -1 : 1, 1, 1);
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
        Log.d(TAG, "retrieveVideoInfo DecodeVideoTask path:" + mVideoPath + ", width:" + mVideoWidth + ", height:" + mVideoHeight);
    }

    private void createSurface(EGLContext sharedContext) {
        Log.d(TAG, "createSurface");
        mEglCore = new EglCore(sharedContext, 0);
        createOffScreenSurface();
        mVideoTexId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mSurfaceTexture = new SurfaceTexture(mVideoTexId);
        mSurface = new Surface(mSurfaceTexture);
        mProgramTextureOes = new ProgramTextureOES();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSurfaceTexture.setOnFrameAvailableListener(this, mDecodeHandler);
        } else {
            mSurfaceTexture.setOnFrameAvailableListener(this);
        }
    }

    private void createMediaPlayer() {
        Log.d(TAG, "createMediaPlayer");
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
            } else {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setVolume(0F, 0F);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mMediaPlayer.start();
                    if (mOnPlayerStartListener != null) {
                        mOnPlayerStartListener.onStart(mTextures[0]);
                    }
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.reset();
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
        if (mProgramTextureOes != null) {
            mProgramTextureOes.release();
            mProgramTextureOes = null;
        }
        if (mVideoTexId > 0) {
            int[] textures = new int[]{mVideoTexId};
            GlUtil.deleteTextures(textures);
            mVideoTexId = -1;
        }
        releaseOffScreenSurface();
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }
        mVideoWidth = 1;
        mVideoHeight = 1;
    }

    private void createOffScreenSurface() {
        Log.d(TAG, "createOffScreenSurface");
        mOffscreenSurface = new OffscreenSurface(mEglCore, mVideoWidth, mVideoHeight);
        mOffscreenSurface.makeCurrent();
        GlUtil.createFrameBuffers(mTextures, mFrameBuffers, mVideoWidth, mVideoHeight);
    }

    private void releaseOffScreenSurface() {
        Log.d(TAG, "releaseOffScreenSurface");
        GlUtil.deleteFrameBuffers(mFrameBuffers);
        if (mFrameBuffers[0] > 0) {
            mFrameBuffers[0] = -1;
        }
        GlUtil.deleteTextures(mTextures);
        if (mTextures[0] > 0) {
            mTextures[0] = -1;
        }
        mFboBackup[0] = -1;
        if (mOffscreenSurface != null) {
            mOffscreenSurface.release();
            mOffscreenSurface = null;
        }
    }

    private void releaseMediaPlayer() {
        Log.d(TAG, "releaseMediaPlayer");
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.stop();
                mMediaPlayer.setSurface(null);
                mMediaPlayer.setOnPreparedListener(null);
                mMediaPlayer.setOnErrorListener(null);
                mMediaPlayer.release();
            } catch (Exception e) {
                Log.e(TAG, "releaseMediaPlayer: ", e);
            }
            mMediaPlayer = null;
        }
    }

    public interface OnPlayerStartListener {
        /**
         * 视频 2D 纹理
         *
         * @param texId
         */
        void onStart(int texId);

        /**
         * 视频 RGBA 数据
         *
         * @param width
         * @param height
         * @param rgba
         */
        void onReadPixel(int width, int height, byte[] rgba);
    }
}
