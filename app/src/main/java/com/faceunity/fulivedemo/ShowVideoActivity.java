package com.faceunity.fulivedemo;

import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.FURenderer;
import com.faceunity.encoder.MediaAudioFileEncoder;
import com.faceunity.encoder.MediaEncoder;
import com.faceunity.encoder.MediaMuxerWrapper;
import com.faceunity.encoder.MediaVideoEncoder;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.renderer.VideoRenderer;
import com.faceunity.fulivedemo.ui.BeautyControlView;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class ShowVideoActivity extends AppCompatActivity
        implements VideoRenderer.OnRendererStatusListener,
        View.OnClickListener {
    public final static String TAG = ShowVideoActivity.class.getSimpleName();

    private String mSelectDataType;

    private GLSurfaceView mGLSurfaceView;
    private VideoRenderer mVideoRenderer;

    private TextView mEffectDescription;
    private ImageView mPlayImageView;
    private ImageView mSaveImageView;
    private BeautyControlView mBeautyControlView;
    private RecyclerView mEffectRecyclerView;
    private EffectRecyclerAdapter mEffectRecyclerAdapter;
    private FURenderer mFURenderer;
    private String mVideoFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_show_video);

        Uri uri = getIntent().getData();
        mSelectDataType = getIntent().getStringExtra("SelectData");
        if (uri == null) {
            onBackPressed();
            return;
        }
        mVideoFilePath = MiscUtil.getFileAbsolutePath(this, uri);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.show_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mVideoRenderer = new VideoRenderer(mVideoFilePath, mGLSurfaceView, this);
        mGLSurfaceView.setRenderer(mVideoRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mVideoRenderer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopRecording();
                mPlayImageView.setVisibility(View.VISIBLE);
                mSaveImageView.setVisibility(View.VISIBLE);
            }
        });

        //初始化FU相关 authpack 为证书文件
        mFURenderer = new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .setCurrentCameraType(Camera.CameraInfo.CAMERA_FACING_BACK)
                .build();

        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mPlayImageView = (ImageView) findViewById(R.id.show_play_btn);
        mSaveImageView = (ImageView) findViewById(R.id.show_save_btn);
        if (FUBeautyActivity.TAG.equals(mSelectDataType)) {
            mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
            mBeautyControlView.setVisibility(View.VISIBLE);
            mBeautyControlView.setOnFUControlListener(mFURenderer);
            mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
            mBeautyControlView.setOnDescriptionShowListener(new BeautyControlView.OnDescriptionShowListener() {
                @Override
                public void onDescriptionShowListener(String str) {
                    showDescription(str, 1500);
                }
            });
            mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBeautyControlView.hideBottomLayoutAnimator();
                }
            });
        } else {
            mEffectRecyclerView = (RecyclerView) findViewById(R.id.fu_effect_recycler);
            mEffectRecyclerView.setVisibility(View.VISIBLE);
            mEffectRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mEffectRecyclerView.setAdapter(mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, Effect.EFFECT_TYPE_NORMAL, mFURenderer));
            ((SimpleItemAnimator) mEffectRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            mFURenderer.setDefaultEffect(EffectEnum.getEffectsByEffectType(Effect.EFFECT_TYPE_NORMAL).get(1));
            mEffectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
                @Override
                public void onDescriptionChangeListener(String description) {
                    showDescription(description, 1500);
                }
            });
        }

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mSaveImageView.getLayoutParams();
        params.bottomMargin = (int) getResources().getDimension(FUBeautyActivity.TAG.equals(mSelectDataType) ? R.dimen.x151 : R.dimen.x199);
        mSaveImageView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoRenderer.onResume();
        if (mBeautyControlView != null)
            mBeautyControlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoRenderer.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOutVideoFile != null && mOutVideoFile.exists()) {
            mOutVideoFile.delete();
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public int onDrawFrame(int videoTextureId, int videoWidth, int videoHeight, float[] mtx, long timeStamp) {
        int fuTextureId = mFURenderer.onDrawFrame(videoTextureId, videoWidth, videoHeight);
        sendRecordingData(fuTextureId, mtx);
        return fuTextureId;
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.show_play_btn:
                if (mOutVideoFile != null && mOutVideoFile.exists()) {
                    mOutVideoFile.delete();
                }
                startRecording();
                mVideoRenderer.playMedia();
                mSaveImageView.setVisibility(View.GONE);
                mPlayImageView.setVisibility(View.GONE);
                mPlayImageView.setImageResource(R.drawable.show_video_replay);
                break;
            case R.id.show_save_btn:
                if (mOutVideoFile != null && mOutVideoFile.exists()) {
                    ToastUtil.showToast(ShowVideoActivity.this, "保存视频成功！");
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mOutVideoFile)));
                    mOutVideoFile = null;
                }
                break;
        }
    }

    private File mOutVideoFile;
    protected MediaVideoEncoder mVideoEncoder;
    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                final MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        mVideoEncoder = videoEncoder;
                    }
                });
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            mVideoEncoder = null;
        }
    };

    protected void sendRecordingData(int texId, final float[] tex_matrix) {
        if (mVideoEncoder != null) {
            mVideoEncoder.frameAvailableSoon(texId, tex_matrix);
        }
    }

    private MediaMuxerWrapper mMuxer;

    private void startRecording() {
        try {
            String videoFileName = "FULiveDemo_" + MiscUtil.getCurrentDate() + ".mp4";
            mOutVideoFile = new File(Constant.cameraFilePath, videoFileName);
            mMuxer = new MediaMuxerWrapper(mOutVideoFile.getAbsolutePath());

            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mVideoRenderer.getVideoWidth(), mVideoRenderer.getVideoHeight());
            new MediaAudioFileEncoder(mMuxer, mMediaEncoderListener, mVideoFilePath);

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    private void stopRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
        }
        System.gc();
    }

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mEffectDescription.setText("");
            mEffectDescription.setVisibility(View.INVISIBLE);
        }
    };

    protected void showDescription(String str, int time) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        mEffectDescription.removeCallbacks(effectDescriptionHide);
        mEffectDescription.setVisibility(View.VISIBLE);
        mEffectDescription.setText(str);
        mEffectDescription.postDelayed(effectDescriptionHide, time);
    }
}
