package com.faceunity.fulivedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
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
import com.faceunity.fulivedemo.utils.ColorPickerTouchEvent;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.MiscUtil;

import java.io.File;

public class ShowPhotoActivity extends AppCompatActivity implements PhotoRenderer.OnRendererStatusListener,
        FURenderer.OnTrackingStatusChangedListener, SensorEventListener, ColorPickerTouchEvent.OnTouchEventListener {
    public final static String TAG = ShowPhotoActivity.class.getSimpleName();

    private PhotoRenderer mPhotoRenderer;
    private TextView mTvTrackStatus;
    private TextView mEffectDescription;
    private ImageView mSaveImageView;
    private BeautyControlView mBeautyControlView;
    private FURenderer mFURenderer;
    private float[] mLandmarksData;

    private volatile boolean mTakePicing = false;
    private volatile boolean mIsNeedTakePic = false;

    private MakeupControlView mMakeupControlView;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private GLSurfaceView mGlSurfaceView;
    private ColorPickerTouchEvent mColorPickerTouchEvent;
    private int mPickedColor;
    private boolean mIsShowColorPicker;
    private BgSegGreenControlView mBgSegGreenControlView;
    private GestureTouchHandler mGestureTouchHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_show_photo);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        Uri uri = getIntent().getData();
        String selectDataType = getIntent().getStringExtra(SelectDataActivity.SELECT_DATA_KEY);
        int selectEffectType = getIntent().getIntExtra(FUEffectActivity.SELECT_EFFECT_KEY, -1);
        if (uri == null) {
            onBackPressed();
            return;
        }
        String filePath = MiscUtil.getFileAbsolutePath(this, uri);

        boolean isBgSegGreen = BgSegGreenActivity.TAG.equals(selectDataType);
        mGlSurfaceView = findViewById(R.id.show_gl_surface);
        mGlSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGlVersion(this));
        mPhotoRenderer = new PhotoRenderer(filePath, mGlSurfaceView, this);
        mGlSurfaceView.setRenderer(mPhotoRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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
                    mFURenderer.setTransform(x1, y1, x2, y2);
                }

                @Override
                public void onClick() {
                    if (mBgSegGreenControlView.isShown()) {
                        mBgSegGreenControlView.hideBottomLayoutAnimator();
                    }
                }
            });
        }

        boolean isBeautyFace = FUBeautyActivity.TAG.equals(selectDataType);
        boolean isMakeup = FUMakeupActivity.TAG.equals(selectDataType);
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
                .setExternalInputType(FURenderer.EXTERNAL_INPUT_TYPE_IMAGE)
                .inputImageOrientation(0)
                .setLoadAiHumanProcessor(loadAiHumanProcessor)
                .setNeedBeautyHair(isHairSeg)
                .setNeedBodySlim(isBodySlim)
                .setLoadAiGesture(isGestureRecognition)
                .defaultEffect(isBgSegGreen ? EffectEnum.BG_SEG_GREEN.effect() : null)
                .setNeedFaceBeauty(!isBodySlim)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setOnTrackingStatusChangedListener(this)
                .build();

        if (isMakeup) {
            mLandmarksData = new float[239 * 2];
        } else {
            mLandmarksData = new float[75 * 2];
        }
        mTvTrackStatus = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mTvTrackStatus.setVisibility(View.GONE);
        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mSaveImageView = (ImageView) findViewById(R.id.show_save_btn);
        if (isBeautyFace) {
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
        } else if (isMakeup) {
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
            final ConstraintLayout constraintLayout = (ConstraintLayout) mGlSurfaceView.getParent();
            mBgSegGreenControlView = findViewById(R.id.fu_bg_seg_green);
            mBgSegGreenControlView.setVisibility(View.VISIBLE);
            mBgSegGreenControlView.setOnFUControlListener(mFURenderer);
            mBgSegGreenControlView.setOnBottomAnimatorChangeListener(new BgSegGreenControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
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
            mSaveImageView.setAlpha(0f);
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
            effectRecyclerAdapter.setOnEffectSelectedListener(new EffectRecyclerAdapter.OnEffectSelectedListener() {
                @Override
                public void onEffectSelected(Effect effect) {
                    mFURenderer.onEffectSelected(effect);
                }
            });
        }

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mSaveImageView.getLayoutParams();
        params.bottomMargin = (int) getResources().getDimension(isBeautyFace ? R.dimen.x151 : R.dimen.x199);
        mSaveImageView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoRenderer.onCreate();
        if (mBeautyControlView != null) {
            mBeautyControlView.onResume();
        }
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPhotoRenderer.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }
        return mIsShowColorPicker && mColorPickerTouchEvent.handleTouchEvent(event, mGlSurfaceView,
                mPhotoRenderer.getViewWidth(), mPhotoRenderer.getViewHeight(),
                mPhotoRenderer.getTexMatrix(), mPhotoRenderer.getMvpMatrix(),
                mPhotoRenderer.get2dTexture(), this);
    }

    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
        if (mMakeupControlView != null) {
            mMakeupControlView.selectDefault();
        }
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {
        if (mGestureTouchHandler != null) {
            mGestureTouchHandler.setViewSize(viewWidth, viewHeight);
        }
    }

    @Override
    public int onDrawFrame(int photoTexId, int photoWidth, int photoHeight) {
        int fuTexId = mFURenderer.onDrawFrame(photoTexId, photoWidth, photoHeight);
        checkPic(fuTexId, photoWidth, photoHeight);
        if (BaseCameraRenderer.ENABLE_DRAW_LANDMARKS) {
            mFURenderer.getLandmarksData(0, mLandmarksData);
            mPhotoRenderer.setLandmarksData(mLandmarksData);
        }
        return fuTexId;
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
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
    public void onLoadPhotoError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.makeFineToast(ShowPhotoActivity.this, error, R.drawable.icon_fail).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowPhotoActivity.this.onBackPressed();
                    }
                }, 1500);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.show_save_btn:
                takePic();
                break;
            default:
        }
    }

    public void takePic() {
        if (mTakePicing) {
            return;
        }
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    public void checkPic(int textureId, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(textureId, PhotoRenderer.IMG_DATA_MATRIX, PhotoRenderer.ROTATE_90, texWidth, texHeight, new BitmapUtil.OnReadBitmapListener() {
            @Override
            public void onReadBitmapListener(Bitmap bitmap) {
                final String filePath = MiscUtil.saveBitmap(bitmap, Constant.PHOTO_FILE_PATH, MiscUtil.getCurrentPhotoName());
                Log.d(TAG, "onReadBitmapListener: filePath: " + filePath);
                if (filePath != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(ShowPhotoActivity.this, R.string.save_photo_success);
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath)));
                            sendBroadcast(intent);
                        }
                    });
                }
                mTakePicing = false;
            }
        }, false);
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
