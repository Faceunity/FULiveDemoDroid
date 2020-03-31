package com.faceunity.fulivedemo.renderer;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.ProgramLandmarks;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.GlUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 使用 GLSurfaceView 渲染视频，采用居中裁剪（CenterCrop）的方式显示
 * <p>
 * Created by tujh on 2018/3/2.
 */
public class VideoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = VideoRenderer.class.getSimpleName();
    private GLSurfaceView mGlSurfaceView;
    private OnRendererStatusListener mOnVideoRendererStatusListener;
    private String mVideoPath;
    private boolean mIsNeedPlay;
    private MediaPlayer mMediaPlayer;
    private SurfaceTexture mVideoSurfaceTexture;
    private int mVideoTextureId;
    private int mVideoWidth = 720;
    private int mVideoHeight = 1280;
    private int mViewWidth;
    private int mViewHeight;
    private int mVideoRotation = 0;
    private float[] mTexMatrix = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mMvpMatrix;
    private float[] mLandmarksData;
    private ProgramLandmarks mProgramLandmarks;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramTextureOES mProgramTextureOes;
    private MediaPlayer.OnCompletionListener mOnCompletionListener;
    private FPSUtil mFPSUtil;
    private boolean mIsPreparing;

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
        mProgramTextureOes = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mVideoTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mOnVideoRendererStatusListener.onSurfaceCreated();
        createMedia();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mViewHeight = height;
        mViewWidth = width;
        GLES20.glViewport(0, 0, width, height);
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        boolean isSystemCameraRecord = true;
        try {
            mediaMetadataRetriever.setDataSource(mVideoPath);
            mVideoWidth = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            mVideoRotation = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            String location = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
            String genre = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            Log.d(TAG, "onSurfaceChanged: location:" + location + ", genre:" + genre);
            isSystemCameraRecord = !(TextUtils.isEmpty(location) || TextUtils.isEmpty(genre));
        } catch (Exception e) {
            Log.e(TAG, "MediaMetadataRetriever extractMetadata: ", e);
        } finally {
            mediaMetadataRetriever.release();
        }
        mOnVideoRendererStatusListener.onSurfaceChanged(width, height, mVideoWidth, mVideoHeight, mVideoRotation, isSystemCameraRecord);
        Log.d(TAG, "onSurfaceChanged() width:" + width + ", height:" + height + ", videoWidth:"
                + mVideoWidth + ", videoHeight:" + mVideoHeight + ", videoRotation:" + mVideoRotation);
        mMvpMatrix = GlUtil.changeMVPMatrixInside(width, height, mVideoRotation % 180 == 0 ? mVideoWidth : mVideoHeight, mVideoRotation % 180 == 0 ? mVideoHeight : mVideoWidth);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mIsPreparing || mProgramTexture2d == null || mProgramTextureOes == null || mVideoSurfaceTexture == null) {
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
        if (fuTextureId > 0) {
            mProgramTexture2d.drawFrame(fuTextureId, mTexMatrix, mMvpMatrix);
        } else {
            mProgramTextureOes.drawFrame(mVideoTextureId, mTexMatrix, mMvpMatrix);
        }
        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS && mLandmarksData != null) {
            mProgramLandmarks.refresh(mLandmarksData, mVideoWidth, mVideoHeight, mVideoRotation,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mMvpMatrix);
            mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
        }

        mFPSUtil.limit();
        mGlSurfaceView.requestRender();
    }

    public void setLandmarksData(float[] landmarksData) {
        mLandmarksData = landmarksData;
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
        if (mProgramTextureOes != null) {
            mProgramTextureOes.release();
            mProgramTextureOes = null;
        }
        if (mProgramLandmarks != null) {
            mProgramLandmarks.release();
            mProgramLandmarks = null;
        }

        mOnVideoRendererStatusListener.onSurfaceDestroy();
    }

    private void createMedia() {
        mIsPreparing = true;
        releaseMedia();
        try {
            mVideoSurfaceTexture = new SurfaceTexture(mVideoTextureId);
            mVideoSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                    mIsPreparing = false;
                    mGlSurfaceView.requestRender();
                    if (!mIsNeedPlay && mMediaPlayer != null && mMediaPlayer.isPlaying()) {
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
                    mIsNeedPlay = false;
                    if (mOnCompletionListener != null) {
                        mOnCompletionListener.onCompletion(mp);
                    }
                    releaseMedia();
                }
            });
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, "createMedia: ", e);
            mOnVideoRendererStatusListener.onLoadVideoError(e.getMessage());
        }
    }

    public void playMedia() {
        mIsNeedPlay = true;
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        } else {
            createMedia();
        }
    }

    private void releaseMedia() {
        mIsNeedPlay = false;
        if (mMediaPlayer != null) {
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
         * @param videoWidth
         * @param videoHeight
         * @param videoRotation
         * @param isSystemCameraRecord
         */
        void onSurfaceChanged(int viewWidth, int viewHeight, int videoWidth, int videoHeight, int videoRotation, boolean isSystemCameraRecord);

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
