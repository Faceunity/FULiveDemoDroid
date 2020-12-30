package com.faceunity.fulivedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.renderer.BaseCameraRenderer;
import com.faceunity.fulivedemo.renderer.VideoRenderer;
import com.faceunity.fulivedemo.ui.ColorPickerView;
import com.faceunity.fulivedemo.ui.GestureTouchHandler;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.ui.control.AnimojiControlView;
import com.faceunity.fulivedemo.ui.control.BeautifyBodyControlView;
import com.faceunity.fulivedemo.ui.control.BeautyControlView;
import com.faceunity.fulivedemo.ui.control.BeautyHairControlView;
import com.faceunity.fulivedemo.ui.control.BgSegGreenControlView;
import com.faceunity.fulivedemo.ui.control.LightMakeupControlView;
import com.faceunity.fulivedemo.ui.control.MakeupControlView;
import com.faceunity.fulivedemo.utils.AudioObserver;
import com.faceunity.fulivedemo.utils.ColorPickerTouchEvent;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.fulivedemo.utils.encoder.MediaAudioFileEncoder;
import com.faceunity.fulivedemo.utils.encoder.MediaEncoder;
import com.faceunity.fulivedemo.utils.encoder.MediaMuxerWrapper;
import com.faceunity.fulivedemo.utils.encoder.MediaVideoEncoder;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;


public class ShowVideoActivity extends AppCompatActivity implements VideoRenderer.OnRendererStatusListener,
        SensorEventListener, FURenderer.OnTrackingStatusChangedListener, ColorPickerTouchEvent.OnTouchEventListener {
    public final static String TAG = ShowVideoActivity.class.getSimpleName();

    private GLSurfaceView mGlSurfaceView;
    private VideoRenderer mVideoRenderer;
    private float[] mLandmarksData;
    private boolean mIsBeautyFace;
    private boolean mIsMakeup;
    private TextView mEffectDescription;
    private ImageView mPlayImageView;
    private ImageView mSaveImageView;
    private TextView mTvTrackStatus;
    private BeautyControlView mBeautyControlView;
    private FURenderer mFURenderer;
    private String mVideoFilePath;
    private MakeupControlView mMakeupControlView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private volatile MediaVideoEncoder mVideoEncoder;
    private ColorPickerTouchEvent mColorPickerTouchEvent;
    private int mPickedColor;
    private boolean mIsShowColorPicker;
    private BgSegGreenControlView mBgSegGreenControlView;
    private GestureTouchHandler mGestureTouchHandler;

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

        boolean isBgSegGreen = BgSegGreenActivity.TAG.equals(selectDataType);
        mGlSurfaceView = findViewById(R.id.show_gl_surface);
        mGlSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGlVersion(this));
        if (isBgSegGreen) {
            mGlSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return !mIsShowColorPicker && mGestureTouchHandler.onTouchEvent(event);
                }
            });
            mGestureTouchHandler = new GestureTouchHandler(this);
            mGestureTouchHandler.setOnTouchResultListener(new GestureTouchHandler.OnTouchResultListener() {

                @Override
                public void onTransform(float x1, float y1, float x2, float y2) {
                    int videoRotation = mVideoRenderer.getVideoRotation();
                    if (videoRotation == 0) {
                        mFURenderer.setTransform(x1, y1, x2, y2);
                    } else if (videoRotation == 90) {
                        mFURenderer.setTransform(y1, 1 - x2, y2, 1 - x1);
                    } else if (videoRotation == 270) {
                        mFURenderer.setTransform(1 - y2, x1, 1 - y1, x2);
                    } else if (videoRotation == 180) {
                        mFURenderer.setTransform(1 - x2, 1 - y2, 1 - x1, 1 - y1);
                    }
                }

                @Override
                public void onClick() {
                    if (mBgSegGreenControlView.isShown()) {
                        mBgSegGreenControlView.hideBottomLayoutAnimator();
                    }
                }
            });
        }
        mVideoRenderer = new VideoRenderer(mVideoFilePath, mGlSurfaceView, this);
        mGlSurfaceView.setRenderer(mVideoRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mVideoRenderer.setOnMediaEventListener(new VideoRenderer.OnMediaEventListener() {
            @Override
            public void onCompletion() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopRecording();
                        mPlayImageView.setVisibility(View.VISIBLE);
                        mPlayImageView.setImageResource(R.drawable.show_video_replay);
                        mSaveImageView.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onLoadError(final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ShowVideoActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mIsBeautyFace = FUBeautyActivity.TAG.equals(selectDataType);
        mIsMakeup = FUMakeupActivity.TAG.equals(selectDataType);
        boolean isLightMakeup = LightMakeupActivity.TAG.equals(selectDataType);
        boolean isBodySlim = BeautifyBodyActivity.TAG.equals(selectDataType);
        boolean isHairSeg = FUHairActivity.TAG.equals(selectDataType);
        boolean isPortraitSegment = selectEffectType == Effect.EFFECT_TYPE_PORTRAIT_SEGMENT;
        boolean loadAiHumanProcessor = isBodySlim || isPortraitSegment;
        boolean isGestureRecognition = selectEffectType == Effect.EFFECT_TYPE_GESTURE_RECOGNITION;
        mFURenderer = new FURenderer
                .Builder(this)
                .maxFaces(loadAiHumanProcessor ? 1 : 4)
                .maxHumans(1)
                .setExternalInputType(FURenderer.EXTERNAL_INPUT_TYPE_VIDEO)
                .inputImageOrientation(0)
                .setLoadAiHumanProcessor(loadAiHumanProcessor)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setNeedBeautyHair(isHairSeg)
                .setNeedBodySlim(isBodySlim)
                .setLoadAiHandProcessor(isGestureRecognition)
                .defaultEffect(isBgSegGreen ? EffectEnum.BG_SEG_GREEN.effect() : null)
                .setNeedFaceBeauty(!isBodySlim)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setOnTrackingStatusChangedListener(this)
                .build();

        if (mIsMakeup) {
            mLandmarksData = new float[239 * 2];
        } else {
            mLandmarksData = new float[75 * 2];
        }
        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mTvTrackStatus = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mTvTrackStatus.setVisibility(View.GONE);
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
            AnimojiControlView animControlView = findViewById(R.id.fu_anim_control);
            animControlView.setVisibility(View.VISIBLE);
            animControlView.setOnFUControlListener(mFURenderer);
            animControlView.setOnBottomAnimatorChangeListener(new AnimojiControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
        } else if (isBgSegGreen) {
            ConstraintLayout constraintLayout = (ConstraintLayout) mGlSurfaceView.getParent();
            mBgSegGreenControlView = findViewById(R.id.fu_bg_seg_green);
            mBgSegGreenControlView.setVisibility(View.VISIBLE);
            mBgSegGreenControlView.setOnFUControlListener(mFURenderer);
            mBgSegGreenControlView.setOnColorPickerStateChangedListener(new BgSegGreenControlView.OnColorPickerStateChangedListener() {
                @Override
                public void onColorPickerStateChanged(boolean selected, int color) {
                    mFURenderer.setRunBgSegGreen(!selected);
                    mIsShowColorPicker = selected;
                    ColorPickerView colorPickerView = mColorPickerTouchEvent.getColorPickerView();
                    colorPickerView.setPickedColor(color);
                    colorPickerView.setVisibility(selected ? View.VISIBLE : View.GONE);
                    if (selected) {
                        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) colorPickerView.getLayoutParams();
                        layoutParams.leftMargin = (constraintLayout.getWidth() - colorPickerView.getWidth()) / 2;
                        layoutParams.topMargin = (constraintLayout.getHeight() - colorPickerView.getHeight()) / 2;
                        colorPickerView.setLayoutParams(layoutParams);
                    }
                }
            });
            mColorPickerTouchEvent = new ColorPickerTouchEvent(this);
            constraintLayout.post(new Runnable() {
                @Override
                public void run() {
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
                    layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
                    layoutParams.leftMargin = constraintLayout.getWidth() / 2;
                    layoutParams.topMargin = constraintLayout.getHeight() / 2;
                    ColorPickerView colorPickerView = mColorPickerTouchEvent.getColorPickerView();
                    constraintLayout.addView(colorPickerView, layoutParams);
                    colorPickerView.setVisibility(View.GONE);
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
            effectRecyclerView.setHasFixedSize(true);
            EffectRecyclerAdapter effectRecyclerAdapter;
            ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(selectEffectType);
            SelectDataActivity.filterEffectList(effects);
            effectRecyclerAdapter = new EffectRecyclerAdapter(this, selectEffectType, mFURenderer, effects);
            effectRecyclerView.setAdapter(effectRecyclerAdapter);
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
            effectRecyclerAdapter.setOnEffectSelectedListener(new EffectRecyclerAdapter.OnEffectSelectedListener() {
                @Override
                public void onEffectSelected(Effect effect) {
                    mFURenderer.onEffectSelected(effect);
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
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }
        return mIsShowColorPicker && mColorPickerTouchEvent.handleTouchEvent(event, mGlSurfaceView,
                mVideoRenderer.getViewWidth(), mVideoRenderer.getViewHeight(),
                mVideoRenderer.getTexMatrix(), mVideoRenderer.getMvpMatrix(), mVideoRenderer.get2dTexture(), this);
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
        if (mGestureTouchHandler != null) {
            mGestureTouchHandler.setViewSize(width, height);
        }
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

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
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
                mVideoRenderer.startMediaPlayer();
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
    public void onTrackStatusChanged(int type, int status) {
        if (mBgSegGreenControlView == null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTvTrackStatus.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
                    if (status <= 0) {
                        int strId = 0;
                        if (type == FURenderer.TRACK_TYPE_FACE) {
                            strId = R.string.fu_base_is_tracking_text;
                        } else if (type == FURenderer.TRACK_TYPE_HUMAN) {
                            strId = R.string.toast_not_detect_body;
                        }
                        if (strId > 0) {
                            mTvTrackStatus.setText(strId);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onReadRgba(int r, int g, int b, int a) {
        int argb = Color.argb(a, r, g, b);
        mBgSegGreenControlView.postSetPalettePickColor(argb);
        mPickedColor = argb;
    }

    @Override
    public void onActionUp() {
        mFURenderer.setRunBgSegGreen(true);
        int pickedColor = mPickedColor;
        setKeyColor(pickedColor);
        mIsShowColorPicker = false;
    }

    private void setKeyColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        mFURenderer.setKeyColor(new double[]{red, green, blue});
    }

}
