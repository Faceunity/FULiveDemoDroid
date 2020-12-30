package com.faceunity.fulivedemo.renderer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.faceunity.fulivedemo.utils.LimitFpsUtil;
import com.faceunity.gles.ProgramLandmarks;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.GlUtil;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
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

    private SurfaceTexture mSurfaceTexture;
    private Surface mSurface;
    private int mVideoTextureId;
    private int mVideoWidth = 720;
    private int mVideoHeight = 1280;
    private int mViewWidth;
    private int mViewHeight;
    private int mVideoRotation;
    private final float[] mTexMatrix = {0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private float[] mMvpMatrix;
    private float[] mLandmarksData;
    private ProgramLandmarks mProgramLandmarks;
    private ProgramTexture2d mProgramTexture2d;
    private ProgramTextureOES mProgramTextureOes;
    private OnMediaEventListener mOnMediaEventListener;
    private SimpleExoPlayer mSimpleExoPlayer;
    private Context mContext;
    private Handler mPlayerHandler;
    private boolean mIsSystemCameraRecord;
    private int m2DTexId;

    public VideoRenderer(String videoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onRendererStatusListener) {
        mVideoPath = videoPath;
        mGlSurfaceView = glSurfaceView;
        mContext = glSurfaceView.getContext();
        mOnVideoRendererStatusListener = onRendererStatusListener;
    }

    public void onResume() {
        startPlayerThread();
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                createExoMediaPlayer();
            }
        });
        mGlSurfaceView.onResume();
    }

    public void onPause() {
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                releaseExoMediaPlayer();
                stopPlayerThread();
            }
        });
        final CountDownLatch count = new CountDownLatch(1);
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                releaseSurface();
                count.countDown();
            }
        });
        try {
            count.await(500, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // ignored
        }
        mGlSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d(TAG, "onSurfaceCreated: ");
        mProgramTexture2d = new ProgramTexture2d();
        mProgramTextureOes = new ProgramTextureOES();
        mProgramLandmarks = new ProgramLandmarks();
        mVideoTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        mOnVideoRendererStatusListener.onSurfaceCreated();
        createSurface();
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                mSimpleExoPlayer.setVideoSurface(mSurface);
            }
        });
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mIsSystemCameraRecord = true;
        try {
            mediaMetadataRetriever.setDataSource(mVideoPath);
            mVideoWidth = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            mVideoRotation = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
            String location = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_LOCATION);
            String genre = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
            // 粗略判断是否系统相机录制
            mIsSystemCameraRecord = !(TextUtils.isEmpty(location) || TextUtils.isEmpty(genre));
            Log.d(TAG, "onSurfaceChanged: location:" + location + ", genre:" + genre);
        } catch (Exception e) {
            Log.e(TAG, "MediaMetadataRetriever extractMetadata: ", e);
        } finally {
            mediaMetadataRetriever.release();
        }
        LimitFpsUtil.setTargetFps(LimitFpsUtil.DEFAULT_FPS);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mViewHeight = height;
        mViewWidth = width;
        boolean isLandscape = mVideoRotation % 180 == 0;
        mMvpMatrix = GlUtil.changeMvpMatrixInside(width, height, isLandscape ? mVideoWidth : mVideoHeight,
                isLandscape ? mVideoHeight : mVideoWidth);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (mVideoWidth > mVideoHeight) {
                Matrix.rotateM(mMvpMatrix, 0, 360 - mVideoRotation, 0, 0, 1);
            }
        }
        mOnVideoRendererStatusListener.onSurfaceChanged(width, height, mVideoWidth, mVideoHeight, mVideoRotation, mIsSystemCameraRecord);
        Log.d(TAG, "onSurfaceChanged() viewWidth:" + width + ", viewHeight:" + height + ", videoWidth:"
                + mVideoWidth + ", videoHeight:" + mVideoHeight + ", videoRotation:" + mVideoRotation
                + ", mIsSystemCameraRecord:" + mIsSystemCameraRecord);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mProgramTexture2d == null || mSurfaceTexture == null) {
            return;
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        try {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mTexMatrix);
        } catch (Exception e) {
            Log.e(TAG, "onDrawFrame: ", e);
            return;
        }

        int fuTexId = mOnVideoRendererStatusListener.onDrawFrame(mVideoTextureId, mVideoWidth,
                mVideoHeight, mTexMatrix, mSurfaceTexture.getTimestamp());
        m2DTexId = fuTexId;
        if (fuTexId > 0) {
            mProgramTexture2d.drawFrame(fuTexId, mTexMatrix, mMvpMatrix);
        } else {
            mProgramTextureOes.drawFrame(mVideoTextureId, mTexMatrix, mMvpMatrix);
        }
        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS && mLandmarksData != null) {
            mProgramLandmarks.refresh(mLandmarksData, mVideoWidth, mVideoHeight, mVideoRotation,
                    Camera.CameraInfo.CAMERA_FACING_BACK, mMvpMatrix);
            mProgramLandmarks.drawFrame(0, 0, mViewWidth, mViewHeight);
        }

        LimitFpsUtil.limitFrameRate();
        mGlSurfaceView.requestRender();
    }

    public void setLandmarksData(float[] landmarksData) {
        mLandmarksData = landmarksData;
    }

    private void onSurfaceDestroy() {
        Log.d(TAG, "onSurfaceDestroy");
        if (mVideoTextureId > 0) {
            int[] textures = new int[]{mVideoTextureId};
            GlUtil.deleteTextures(textures);
            mVideoTextureId = -1;
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

    private void createExoMediaPlayer() {
        Log.d(TAG, "createExoMediaPlayer: ");
        mSimpleExoPlayer = new SimpleExoPlayer.Builder(mContext).build();
        MediaEventListener mediaEventListener = new MediaEventListener();
        mSimpleExoPlayer.addListener(mediaEventListener);
        mSimpleExoPlayer.setPlayWhenReady(false);
        String userAgent = Util.getUserAgent(mContext, mContext.getPackageName());
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, userAgent);
        ProgressiveMediaSource.Factory mediaSourceFactory = new ProgressiveMediaSource.Factory(dataSourceFactory);
        Uri uri = Uri.fromFile(new File(mVideoPath));
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(uri);
        mSimpleExoPlayer.prepare(mediaSource);
    }

    private void releaseExoMediaPlayer() {
        Log.d(TAG, "releaseExoMediaPlayer: ");
        if (mSimpleExoPlayer != null) {
            mSimpleExoPlayer.stop(true);
            mSimpleExoPlayer.release();
            mSimpleExoPlayer = null;
        }
    }

    private void createSurface() {
        Log.d(TAG, "createSurface: ");
        mSurfaceTexture = new SurfaceTexture(mVideoTextureId);
        mSurfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                mGlSurfaceView.requestRender();
            }
        });
        mSurface = new Surface(mSurfaceTexture);
    }

    private void releaseSurface() {
        Log.d(TAG, "releaseSurface: ");
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
    }

    public void startMediaPlayer() {
        Log.d(TAG, "startMediaPlayer: ");
        mPlayerHandler.post(new Runnable() {
            @Override
            public void run() {
                mSimpleExoPlayer.seekTo(0);
                mSimpleExoPlayer.setPlayWhenReady(true);
            }
        });
    }

    public void setOnMediaEventListener(OnMediaEventListener onMediaEventListener) {
        mOnMediaEventListener = onMediaEventListener;
    }

    public int getVideoWidth() {
        return mVideoRotation % 180 == 0 ? mVideoWidth : mVideoHeight;
    }

    public int getVideoHeight() {
        return mVideoRotation % 180 == 0 ? mVideoHeight : mVideoWidth;
    }

    public int getViewWidth() {
        return mViewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public float[] getTexMatrix() {
        return mTexMatrix;
    }

    public float[] getMvpMatrix() {
        return mMvpMatrix;
    }

    public int getVideoRotation() {
        return mVideoRotation;
    }

    public int get2dTexture() {
        return m2DTexId;
    }

    private void startPlayerThread() {
        HandlerThread playerThread = new HandlerThread("exo_player", Process.THREAD_PRIORITY_BACKGROUND);
        playerThread.start();
        mPlayerHandler = new Handler(playerThread.getLooper());
    }

    private void stopPlayerThread() {
        mPlayerHandler.getLooper().quitSafely();
        mPlayerHandler = null;
    }

    private class MediaEventListener implements Player.EventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            switch (playbackState) {
//                case Player.STATE_IDLE:
//                    break;
//                case Player.STATE_BUFFERING:
//                    break;
                case Player.STATE_READY:
                    if (playWhenReady) {
                        Log.d(TAG, "onPlayerStateChanged: prepared " + Thread.currentThread().getName());
                        mGlSurfaceView.requestRender();
                    }
                    break;
                case Player.STATE_ENDED:
                    Log.d(TAG, "onPlayerStateChanged: completion " + Thread.currentThread().getName());
                    if (mOnMediaEventListener != null) {
                        mOnMediaEventListener.onCompletion();
                    }
                    break;
                default:
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Log.w(TAG, "onPlayerError: ", error);
            String message;
            switch (error.type) {
                case ExoPlaybackException.TYPE_SOURCE:
                    message = "数据源异常";
                    break;
                case ExoPlaybackException.TYPE_RENDERER:
                    message = "解码异常";
                    break;
                case ExoPlaybackException.TYPE_UNEXPECTED:
                default:
                    message = "其他异常";
            }
            if (mOnMediaEventListener != null) {
                mOnMediaEventListener.onLoadError(message);
            }
        }
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
    }

    public interface OnMediaEventListener {
        /**
         * Called when the end of a media source is reached during playback.
         */
        void onCompletion();

        /**
         * Called when error happened
         *
         * @param error
         */
        void onLoadError(String error);
    }

}