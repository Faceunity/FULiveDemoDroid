package com.faceunity.fulivedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.faceunity.FURenderer;
import com.faceunity.encoder.MediaAudioFileEncoder;
import com.faceunity.encoder.MediaEncoder;
import com.faceunity.encoder.MediaMuxerWrapper;
import com.faceunity.encoder.MediaVideoEncoder;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.renderer.BaseCameraRenderer;
import com.faceunity.fulivedemo.renderer.VideoRenderer;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.ui.control.AnimControlView;
import com.faceunity.fulivedemo.ui.control.BeautifyBodyControlView;
import com.faceunity.fulivedemo.ui.control.BeautyControlView;
import com.faceunity.fulivedemo.ui.control.BeautyHairControlView;
import com.faceunity.fulivedemo.ui.control.LightMakeupControlView;
import com.faceunity.fulivedemo.ui.control.MakeupControlView;
import com.faceunity.fulivedemo.utils.AudioObserver;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;


public class ShowVideoActivity extends AppCompatActivity implements VideoRenderer.OnRendererStatusListener,
        SensorEventListener, FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = ShowVideoActivity.class.getSimpleName();

    private GLSurfaceView mGlSurfaceView;
    private VideoRenderer mVideoRenderer;
    private float[] mLandmarksData;
    private boolean mIsBeautyFace;
    private boolean mIsMakeup;
    private TextView mEffectDescription;
    private ImageView mPlayImageView;
    private ImageView mSaveImageView;
    private TextView mIsTrackingText;
    private BeautyControlView mBeautyControlView;
    private FURenderer mFURenderer;
    private String mVideoFilePath;
    private MakeupControlView mMakeupControlView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private volatile MediaVideoEncoder mVideoEncoder;

    @Override
    protected void onResume() {
        super.onResume();
        mVideoRenderer.onResume();
        if (mBeautyControlView != null) {
            mBeautyControlView.onResume();
        }
        mPlayImageView.setVisibility(View.VISIBLE);
        mPlayImageView.setImageResource(R.drawable.show_video_play);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVideoRenderer.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopRecording();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_show_video);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Uri uri = getIntent().getData();
        String selectDataType = getIntent().getStringExtra(SelectDataActivity.SELECT_DATA_KEY);
        int selectEffectType = getIntent().getIntExtra(FUEffectActivity.SELECT_EFFECT_KEY, -1);
        if (uri == null) {
            onBackPressed();
            return;
        }
        mVideoFilePath = MiscUtil.getFileAbsolutePath(this, uri);

        mGlSurfaceView = findViewById(R.id.show_gl_surface);
        mGlSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
        mVideoRenderer = new VideoRenderer(mVideoFilePath, mGlSurfaceView, this);
        mGlSurfaceView.setRenderer(mVideoRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mVideoRenderer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopRecording();
                mPlayImageView.setVisibility(View.VISIBLE);
                mPlayImageView.setImageResource(R.drawable.show_video_replay);
                mSaveImageView.setVisibility(View.VISIBLE);
            }
        });

        //初始化FU相关 authpack 为证书文件
        mIsBeautyFace = FUBeautyActivity.TAG.equals(selectDataType);
        mIsMakeup = FUMakeupActivity.TAG.equals(selectDataType);
        boolean isLightMakeup = LightMakeupActivity.TAG.equals(selectDataType);
        boolean isBodySlim = BeautifyBodyActivity.TAG.equals(selectDataType);
        boolean isHairSeg = FUHairActivity.TAG.equals(selectDataType);
        mFURenderer = new FURenderer
                .Builder(this)
                .maxFaces(4)
                .setExternalInputType(FURenderer.EXTERNAL_INPUT_TYPE_VIDEO)
                .inputImageOrientation(0)
                .setLoadAiHumanPose(isBodySlim)
                .setLoadAiHairSeg(isHairSeg)
                .setLoadAiBgSeg(selectEffectType == Effect.EFFECT_TYPE_BACKGROUND)
                .setLoadAiGesture(selectEffectType == Effect.EFFECT_TYPE_GESTURE)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setUseBeautifyBody(isBodySlim)
                .setNeedBeautyHair(isHairSeg)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setOnTrackingStatusChangedListener(this)
                .build();

        if (mIsMakeup) {
            mLandmarksData = new float[239 * 2];
        } else {
            mLandmarksData = new float[75 * 2];
        }
        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mIsTrackingText = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mPlayImageView = (ImageView) findViewById(R.id.show_play_btn);
        mSaveImageView = (ImageView) findViewById(R.id.show_save_btn);
        if (mIsBeautyFace) {
            mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
            mBeautyControlView.setVisibility(View.VISIBLE);
            mBeautyControlView.setOnFUControlListener(mFURenderer);
            mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
            mGlSurfaceView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    mBeautyControlView.hideBottomLayoutAnimator();
                }
            });
        } else if (mIsMakeup) {
            mMakeupControlView = findViewById(R.id.fu_makeup_control);
            mMakeupControlView.setVisibility(View.VISIBLE);
            mMakeupControlView.setOnFUControlListener(mFURenderer);
            mMakeupControlView.setOnBottomAnimatorChangeListener(new MakeupControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }

                @Override
                public void onFirstMakeupAnimatorChangeListener(float hideRate) {

                }
            });
        } else if (FUAnimojiActivity.TAG.equals(selectDataType)) {
            AnimControlView animControlView = findViewById(R.id.fu_anim_control);
            animControlView.setVisibility(View.VISIBLE);
            animControlView.setOnFUControlListener(mFURenderer);
            animControlView.setOnBottomAnimatorChangeListener(new AnimControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
        } else if (isHairSeg) {
            BeautyHairControlView beautyHairControlView = findViewById(R.id.fu_beauty_hair);
            beautyHairControlView.setVisibility(View.VISIBLE);
            beautyHairControlView.setOnFUControlListener(mFURenderer);
        } else if (isBodySlim) {
            BeautifyBodyControlView beautifyBodyControlView = findViewById(R.id.fu_beautify_body);
            beautifyBodyControlView.setVisibility(View.VISIBLE);
            beautifyBodyControlView.setOnFUControlListener(mFURenderer);
        } else if (isLightMakeup) {
            LightMakeupControlView lightMakeupControlView = findViewById(R.id.fu_light_makeup);
            lightMakeupControlView.setVisibility(View.VISIBLE);
            lightMakeupControlView.setOnFUControlListener(mFURenderer);
        } else {
            RecyclerView effectRecyclerView = findViewById(R.id.fu_effect_recycler);
            effectRecyclerView.setVisibility(View.VISIBLE);
            effectRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            EffectRecyclerAdapter effectRecyclerAdapter;
            effectRecyclerView.setAdapter(effectRecyclerAdapter = new EffectRecyclerAdapter(this, selectEffectType, mFURenderer));
            ((SimpleItemAnimator) effectRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            if (selectEffectType != Effect.EFFECT_TYPE_HAIR_GRADIENT) {
                mFURenderer.setDefaultEffect(EffectEnum.getEffectsByEffectType(selectEffectType).get(1));
            }
            effectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
                @Override
                public void onDescriptionChangeListener(int description) {
                    showDescription(description, 1500);
                }
            });
        }

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mSaveImageView.getLayoutParams();
        params.bottomMargin = (int) getResources().getDimension(mIsBeautyFace ? R.dimen.x151 : R.dimen.x199);
        mSaveImageView.setLayoutParams(params);

        AudioObserver audioObserver = new AudioObserver(this);
        getLifecycle().addObserver(audioObserver);
    }

    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
        if (mMakeupControlView != null) {
            mMakeupControlView.selectDefault();
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height, int videoWidth, int videoHeight, int videoRotation, boolean isSystemCameraRecord) {
        mFURenderer.setVideoParams(videoRotation, isSystemCameraRecord);
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public int onDrawFrame(int videoTextureId, int videoWidth, int videoHeight, float[] mvpMatrix, long timeStamp) {
        int fuTextureId = mFURenderer.onDrawFrame(videoTextureId, videoWidth, videoHeight);
        sendRecordingData(fuTextureId, mvpMatrix);
        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS) {
            mFURenderer.getLandmarksData(0, mLandmarksData);
            mVideoRenderer.setLandmarksData(mLandmarksData);
        }
        return fuTextureId;
    }

    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                mGlSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        mVideoEncoder = videoEncoder;
                    }
                });
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
        }
    };

    private File mOutVideoFile;

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
                break;
            case R.id.show_save_btn:
                if (mOutVideoFile != null && mOutVideoFile.exists()) {
                    ThreadHelper.getInstance().enqueueOnUiThread(new Callable<File>() {
                        @Override
                        public File call() throws Exception {
                            File dcimFile = new File(Constant.VIDEO_FILE_PATH, mOutVideoFile.getName());
                            FileUtils.copyFile(mOutVideoFile, dcimFile);
                            FileUtils.deleteFile(mOutVideoFile);
                            return dcimFile;
                        }
                    }, new ThreadHelper.Callback<File>() {
                        @Override
                        protected void onSuccess(File result) {
                            super.onSuccess(result);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(result)));
                            ToastUtil.showToast(ShowVideoActivity.this, R.string.save_video_success);
                        }
                    });
                }
                break;
            default:
        }
    }

    @Override
    public void onLoadVideoError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ShowVideoActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void sendRecordingData(int texId, final float[] texMatrix) {
        if (mVideoEncoder != null) {
            mVideoEncoder.frameAvailableSoon(texId, texMatrix, GlUtil.IDENTITY_MATRIX);
        }
    }

    private MediaMuxerWrapper mMuxer;

    private void startRecording() {
        try {
            String videoFileName = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".mp4";
            mOutVideoFile = new File(FileUtils.getExternalCacheDir(this), videoFileName);
            mMuxer = new MediaMuxerWrapper(mOutVideoFile.getAbsolutePath());

            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mVideoRenderer.getVideoWidth(), mVideoRenderer.getVideoHeight());
            new MediaAudioFileEncoder(mMuxer, mMediaEncoderListener, mVideoFilePath);

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (IOException e) {
            Log.e(TAG, "startRecording:", e);
        }
    }

    private void stopRecording() {
        if (mMuxer != null) {
            mVideoEncoder = null;
            mMuxer.stopRecording();
            mMuxer = null;
        }
    }

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mEffectDescription.setText("");
            mEffectDescription.setVisibility(View.INVISIBLE);
        }
    };

    protected void showDescription(int str, int time) {
        if (0 == str) {
            return;
        }
        mEffectDescription.removeCallbacks(effectDescriptionHide);
        mEffectDescription.setVisibility(View.VISIBLE);
        mEffectDescription.setText(str);
        mEffectDescription.postDelayed(effectDescriptionHide, time);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    mFURenderer.setTrackOrientation(x > 0 ? 0 : 180);
                } else {
                    mFURenderer.setTrackOrientation(y > 0 ? 90 : 270);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onTrackingStatusChanged(int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsTrackingText.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }
}
