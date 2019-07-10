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
 * Created by tujh on 2018/3/2.
 */

public class VideoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = VideoRenderer.class.getSimpleName();

    private GLSurfaceView mGLSurfaceView;

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(int videoTextureId, int videoWidth, int videoHeight, float[] mtx, long timeStamp);

        void onSurfaceDestroy();
    }

    private OnRendererStatusListener mOnVideoRendererStatusListener;

    private int mViewWidth = 1280;
    private int mViewHeight = 720;

    private String mVideoPath;

    private boolean isNeedPlay = false;
    private MediaPlayer mMediaPlayer;
    private SurfaceTexture mVideoSurfaceTexture;
    private int mVideoTextureId;
    private int mVideoWidth = 1280;
    private int mVideoHeight = 720;
    private int mVideoRotation = 0;

    private final float[] mtx = new float[16];
    private float[] mvp = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;

    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private FPSUtil mFPSUtil;

    public VideoRenderer(String filePath, GLSurfaceView GLSurfaceView, OnRendererStatusListener onVideoRendererStatusListener) {
        mVideoPath = filePath;
        mGLSurfaceView = GLSurfaceView;
        mOnVideoRendererStatusListener = onVideoRendererStatusListener;
        mFPSUtil = new FPSUtil();
        Log.d(TAG, "VideoRenderer: path:" + mVideoPath);
    }

    public void onResume() {
        mGLSurfaceView.onResume();
    }

    public void onPause() {
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
            Log.e(TAG, "onPause: ", e);
        }
        mGLSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mVideoTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mOnVideoRendererStatusListener.onSurfaceCreated(gl, config);
        createMedia();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);
        mOnVideoRendererStatusListener.onSurfaceChanged(gl, width, height);
        MediaMetadataRetriever retr = new MediaMetadataRetriever();
        try {
            retr.setDataSource(mVideoPath);
            mVideoWidth = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            mVideoRotation = Integer.parseInt(retr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
        } catch (Exception e) {
            Log.e(TAG, "onSurfaceChanged: ", e);
            mVideoWidth = 720;
            mVideoHeight = 1280;
            mVideoRotation = 0;
        }
        Log.d(TAG, "onSurfaceChanged() called with: width = [" + width + "], height = [" + height + "]"
                + ", videoWidth:" + mVideoWidth + ", videoHeight:" + mVideoHeight + ", videoRotation:" + mVideoRotation);
        mvp = GlUtil.changeMVPMatrixCrop(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mVideoRotation % 180 == 0 ? mVideoWidth : mVideoHeight, mVideoRotation % 180 == 0 ? mVideoHeight : mVideoWidth);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mFullFrameRectTexture2D == null) {
            return;
        }
        try {
            mVideoSurfaceTexture.updateTexImage();
            mVideoSurfaceTexture.getTransformMatrix(mtx);
        } catch (Exception e) {
            return;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        int fuTextureId = mOnVideoRendererStatusListener.onDrawFrame(mVideoTextureId, mVideoWidth, mVideoHeight, mtx, mVideoSurfaceTexture.getTimestamp());
        mFullFrameRectTexture2D.drawFrame(fuTextureId, mtx, mvp);
        mFPSUtil.limit();
    }

    private void onSurfaceDestroy() {
        releaseMedia();
        if (mVideoTextureId != 0) {
            int[] textures = new int[]{mVideoTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mVideoTextureId = 0;
        }
        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
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
                    mGLSurfaceView.requestRender();
                    if (!isNeedPlay && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                        mMediaPlayer.pause();
                    }
                }
            });

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mVideoPath);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setSurface(new Surface(mVideoSurfaceTexture));
            mMediaPlayer.setVolume(0, 0);
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(final MediaPlayer mp) {
                    mMediaPlayer.start();
                    mGLSurfaceView.requestRender();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isNeedPlay = false;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mp);
                    }
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "createMedia: ", e);
        }
    }

    public void playMedia() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
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
}
