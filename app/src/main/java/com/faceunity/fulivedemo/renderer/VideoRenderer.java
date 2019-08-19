package com.faceunity.fulivedemo.renderer;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 使用 GLSurfaceView 渲染视频，采用居中裁剪（CenterCrop）的方式显示
 *
 * Created by tujh on 2018/3/2.
 */
public class VideoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = VideoRenderer.class.getSimpleName();
    private GLSurfaceView mGlSurfaceView;
    private OnRendererStatusListener mOnVideoRendererStatusListener;
    private String mVideoPath;
    private boolean isNeedPlay = false;
    private MediaPlayer mMediaPlayer;
    private SurfaceTexture mVideoSurfaceTexture;
    private int mVideoTextureId;
    private int mVideoWidth = 720;
    private int mVideoHeight = 1280;
    private int mVideoRotation = 0;
    private float[] mTexMatrix = new float[16];
    private float[] mMvpMatrix;
    private ProgramTexture2d mProgramTexture2d;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private FPSUtil mFPSUtil;

    public VideoRenderer(String videoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mVideoPath = videoPath;
        mGlSurfaceView = glSurfaceView;
        mOnVideoRendererStatusListener = onRendererStatusListener;
        mFPSUtil = new FPSUtil();
    }

    public void onResume() {
        mGlSurfaceView.onResume();
    }

    public void onPause() {
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
        Log.d(TAG, "onSurfaceCreated");
        mProgramTexture2d = new ProgramTexture2d();
        mVideoTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mOnVideoRendererStatusListener.onSurfaceCreated();
        createMedia();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mOnVideoRendererStatusListener.onSurfaceChanged(width, height);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        try {
            mediaMetadataRetriever.setDataSource(mVideoPath);
            mVideoWidth = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            mVideoRotation = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        } catch (Exception e) {
            Log.e(TAG, "MediaMetadataRetriever extractMetadata: ", e);
        }
        Log.d(TAG, "onSurfaceChanged() width:" + width + ", height:" + height + ", videoWidth:"
                + mVideoWidth + ", videoHeight:" + mVideoHeight + ", videoRotation:" + mVideoRotation);
        mMvpMatrix = GlUtil.changeMVPMatrixCrop(GlUtil.IDENTITY_MATRIX, width, height,
                mVideoRotation % 180 == 0 ? mVideoWidth : mVideoHeight, mVideoRotation % 180 == 0 ? mVideoHeight : mVideoWidth);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mProgramTexture2d == null) {
            return;
        }

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        try {
            mVideoSurfaceTexture.updateTexImage();
            mVideoSurfaceTexture.getTransformMatrix(mTexMatrix);
        } catch (Exception e) {
            Log.e(TAG, "onDrawFrame: ", e);
            return;
        }

        int fuTextureId = mOnVideoRendererStatusListener.onDrawFrame(mVideoTextureId, mVideoWidth,
                mVideoHeight, mTexMatrix, mVideoSurfaceTexture.getTimestamp());
        mProgramTexture2d.drawFrame(fuTextureId, mTexMatrix, mMvpMatrix);
        mFPSUtil.limit();
        mGlSurfaceView.requestRender();
    }

    private void onSurfaceDestroy() {
        Log.d(TAG, "onSurfaceDestroy");
        releaseMedia();
        if (mVideoTextureId != 0) {
            int[] textures = new int[]{mVideoTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mVideoTextureId = 0;
        }
        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
        mOnVideoRendererStatusListener.onSurfaceDestroy();
    }

    private void createMedia() {
        releaseMedia();
        try {
            mVideoSurfaceTexture = new SurfaceTexture(mVideoTextureId);
            mVideoSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mGlSurfaceView.requestRender();
                    if (!isNeedPlay && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                }
            });

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setVolume(0, 0);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setSurface(new Surface(mVideoSurfaceTexture));
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    Log.d(TAG, "onPrepared");
                    mMediaPlayer.start();
                    mGlSurfaceView.requestRender();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion");
                    isNeedPlay = false;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mp);
                    }
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "createMedia: ", e);
            mOnVideoRendererStatusListener.onLoadVideoError(e.getMessage());
        }
    }

    public void playMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mMediaPlayer.setVolume(1, 1);
            isNeedPlay = true;
        }
    }

    private void releaseMedia() {
        if (mMediaPlayer != null) {
            isNeedPlay = false;
            mMediaPlayer.stop();
            mMediaPlayer.setSurface(null);
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mVideoSurfaceTexture != null) {
            mVideoSurfaceTexture.release();
            mVideoSurfaceTexture = null;
        }
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    public int getVideoWidth() {
        return mVideoRotation % 180 == 0 ? mVideoWidth : mVideoHeight;
    }

    public int getVideoHeight() {
        return mVideoRotation % 180 == 0 ? mVideoHeight : mVideoWidth;
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
         * Called when drawing current frame
         *
         * @param videoTextureId
         * @param videoWidth
         * @param videoHeight
         * @param mvpMatrix
         * @param timeStamp
         * @return
         */
        int onDrawFrame(int videoTextureId, int videoWidth, int videoHeight, float[] mvpMatrix, long timeStamp);

        /**
         * Called when surface is destroyed
         */
        void onSurfaceDestroy();

        /**
         * Called when error happened
         *
         * @param error
         */
        void onLoadVideoError(String error);
    }
}
