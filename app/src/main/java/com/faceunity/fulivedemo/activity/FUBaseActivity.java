package com.faceunity.fulivedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.faceunity.FURenderer;
import com.faceunity.encoder.MediaAudioEncoder;
import com.faceunity.encoder.MediaEncoder;
import com.faceunity.encoder.MediaMuxerWrapper;
import com.faceunity.encoder.MediaVideoEncoder;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.renderer.BaseCameraRenderer;
import com.faceunity.fulivedemo.renderer.Camera1Renderer;
import com.faceunity.fulivedemo.renderer.OnRendererStatusListener;
import com.faceunity.fulivedemo.ui.CameraFocus;
import com.faceunity.fulivedemo.ui.RecordBtn;
import com.faceunity.fulivedemo.ui.VerticalSeekBar;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * Base Activity, 主要封装FUBeautyActivity与FUEffectActivity的公用界面与方法
 * CameraRenderer相关回调实现
 * Created by tujh on 2018/1/31.
 */
public abstract class FUBaseActivity extends AppCompatActivity
        implements OnRendererStatusListener,
        SensorEventListener,
        FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = FUBaseActivity.class.getSimpleName();

    protected ImageView mTopBackground;
    protected GLSurfaceView mGLSurfaceView;
    protected BaseCameraRenderer mCameraRenderer;
    protected volatile boolean mIsDualInput = true;
    private TextView mDebugText;
    protected TextView mIsTrackingText;
    private TextView mEffectDescription;
    protected RecordBtn mTakePicBtn;
    protected ViewStub mBottomViewStub;
    private LinearLayout mLlLight;
    private VerticalSeekBar mVerticalSeekBar;
    protected CameraFocus mCameraFocus;
    protected ConstraintLayout mClOperationView;
    protected ConstraintLayout mRootView;
    private PopupWindow mPopupWindow;
    protected RadioGroup mInputTypeRadioGroup;
    private ImageView mIvShowMore;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private final Runnable mCameraFocusDismiss = new Runnable() {
        @Override
        public void run() {
            mCameraFocus.layout(0, 0, 0, 0);
            mLlLight.setVisibility(View.INVISIBLE);
            onLightFocusVisibilityChanged(false);
        }
    };

    protected FURenderer mFURenderer;
    protected byte[] mFuNV21Byte;
    protected int mFrontCameraOrientation;
    private float[][] mLandmarksDataArray;
    private float[] mLandmarksDataNew;
    private int mTrackedFaceCount;
    protected Handler mMainHandler = new Handler(Looper.getMainLooper());
    protected volatile boolean mIsTakingPic = false;

    protected abstract void onCreate();

    protected abstract FURenderer initFURenderer();

    // 默认全部使用对焦
    protected boolean showAutoFocus() {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }

        if (!showAutoFocus()) {
            return false;
        }
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {
            mCameraRenderer.handleFocus(event.getRawX(), event.getRawY(), getResources().getDimensionPixelSize(R.dimen.x150));
            mCameraFocus.showCameraFocus(event.getRawX(), event.getRawY());
            mLlLight.setVisibility(View.VISIBLE);
            onLightFocusVisibilityChanged(true);
            mVerticalSeekBar.setProgress((int) (100 * mCameraRenderer.getExposureCompensation()));

            mMainHandler.removeCallbacks(mCameraFocusDismiss);
            mMainHandler.postDelayed(mCameraFocusDismiss, 1300);
            return true;
        }
        return false;
    }

    protected void onLightFocusVisibilityChanged(boolean visible) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraRenderer.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.onPause();
    }

    protected volatile boolean mIsNeedTakePic = false;
    private volatile long mStartTime = 0;

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mEffectDescription.setText("");
            mEffectDescription.setVisibility(View.INVISIBLE);
        }
    };

    protected void showDescription(int str, int time) {
        if (str == 0) {
            return;
        }
        mEffectDescription.removeCallbacks(effectDescriptionHide);
        mEffectDescription.setVisibility(View.VISIBLE);
        mEffectDescription.setText(str);
        mEffectDescription.postDelayed(effectDescriptionHide, time);
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~Sensor部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FURenderer信息回调~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onFpsChange(final double fps, final double renderTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDebugText.setText(String.format(getString(R.string.fu_base_debug), mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight(), (int) fps, (int) renderTime));
            }
        });
    }

    @Override
    public void onTrackingStatusChanged(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsTrackingText.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~FURenderer调用部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
        mFURenderer.setBeautificationOn(true);
        mFURenderer.setFaceBeautyLandmarksType(getLandmarksType());
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight,
                           float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        int fuTexId = 0;
        if (mIsDualInput) {
            fuTexId = mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else if (cameraNV21Byte != null) {
            if (mFuNV21Byte == null || mFuNV21Byte.length != cameraNV21Byte.length) {
                mFuNV21Byte = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
            fuTexId = mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
        }
        showLandmarks();
        sendRecordingData(fuTexId, mvpMatrix, texMatrix, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
        takePicture(fuTexId, mvpMatrix, texMatrix, mCameraRenderer.getViewWidth(), mCameraRenderer.getViewHeight());
        return fuTexId;
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {
        mFURenderer.onCameraChange(cameraFacing, cameraOrientation);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVerticalSeekBar.setProgress((int) (100 * mCameraRenderer.getExposureCompensation()));
            }
        });
    }

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~拍照录制部分~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    protected BitmapUtil.OnReadBitmapListener mOnReadBitmapListener = new BitmapUtil.OnReadBitmapListener() {

        @Override
        public void onReadBitmapListener(Bitmap bitmap) {
            // Call on async thread
            final String filePath = MiscUtil.saveBitmap(bitmap, Constant.photoFilePath, MiscUtil.getCurrentPhotoName());
            if (filePath != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(FUBaseActivity.this, R.string.save_photo_success);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath)));
                        sendBroadcast(intent);
                    }
                });
            }
            mIsTakingPic = false;
        }
    };

    public void takePic() {
        if (mIsTakingPic) {
            return;
        }
        mIsNeedTakePic = true;
        mIsTakingPic = true;
    }

    /**
     * 拍照
     *
     * @param texId
     * @param texMatrix
     * @param texWidth
     * @param texHeight
     */
    protected void takePicture(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, mOnReadBitmapListener, false);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fu_base_back:
                onBackPressed();
                break;
            case R.id.fu_base_camera_change:
                mCameraRenderer.switchCamera();
                break;
            default:
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fu_base);
        MiscUtil.checkPermission(this);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.fu_base_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
//        boolean hasCamera2 = CameraUtils.hasCamera2(this);
//        Log.i(TAG, "onCreate: hasCamera2:" + hasCamera2);
//        if (hasCamera2) {
//            mCameraRenderer = new Camera2Renderer(this, mGLSurfaceView, this);
//        } else {
        mCameraRenderer = new Camera1Renderer(this, mGLSurfaceView, this);
//        }
        mFrontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        mFURenderer = initFURenderer();
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mTopBackground = (ImageView) findViewById(R.id.fu_base_top_background);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mInputTypeRadioGroup = (RadioGroup) findViewById(R.id.fu_base_input_type_radio_group);
        mInputTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.fu_base_input_type_double:
                        mIsDualInput = true;
                        break;
                    case R.id.fu_base_input_type_single:
                        mIsDualInput = false;
                        break;
                    default:
                }
                mFURenderer.changeInputType();
            }
        });

        CheckBox debugBox = (CheckBox) findViewById(R.id.fu_base_debug);
        mDebugText = (TextView) findViewById(R.id.fu_base_debug_text);
        debugBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDebugText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        mIvShowMore = findViewById(R.id.fu_base_more);
        if (isOpenResolutionChange()) {
            mIvShowMore.setImageResource(R.drawable.demo_icon_more);
        } else if (isOpenPhotoVideo()) {
            mIvShowMore.setImageResource(R.drawable.photo);
        } else {
            mIvShowMore.setVisibility(View.INVISIBLE);
        }
        mIvShowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenResolutionChange()) {
                    showMorePopupWindow();
                } else {
                    onSelectPhotoVideoClick();
                }
            }
        });

        mIsTrackingText = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mTakePicBtn = (RecordBtn) findViewById(R.id.fu_base_take_pic);
        mTakePicBtn.setOnRecordListener(new RecordBtn.OnRecordListener() {
            @Override
            public void takePic() {
                FUBaseActivity.this.takePic();
            }

            @Override
            public void startRecord() {
                startRecording();
            }

            @Override
            public void stopRecord() {
                stopRecording();
            }
        });
        mClOperationView = (ConstraintLayout) findViewById(R.id.cl_custom_view);
        mRootView = (ConstraintLayout) findViewById(R.id.cl_root);
        mBottomViewStub = (ViewStub) findViewById(R.id.fu_base_bottom);
        mBottomViewStub.setInflatedId(R.id.fu_base_bottom);
        mLlLight = (LinearLayout) findViewById(R.id.photograph_light_layout);
        mVerticalSeekBar = (VerticalSeekBar) findViewById(R.id.photograph_light_seek);
        mCameraFocus = (CameraFocus) findViewById(R.id.photograph_focus);
        mVerticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCameraRenderer.setExposureCompensation((float) progress / 100);
                mMainHandler.removeCallbacks(mCameraFocusDismiss);
                mMainHandler.postDelayed(mCameraFocusDismiss, 1300);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        onCreate();
    }

    private void showLandmarks() {
        if (!BaseCameraRenderer.ENABLE_DRAW_LANDMARKS) {
            return;
        }

        int trackedFaceCount = mFURenderer.getTrackedFaceCount();
        int landmarksType = getLandmarksType();
        if (trackedFaceCount > 0) {
            if (FURenderer.FACE_LANDMARKS_DDE == landmarksType) {
                if (mTrackedFaceCount != trackedFaceCount) {
                    mLandmarksDataArray = new float[trackedFaceCount][75 * 2];
                    mTrackedFaceCount = trackedFaceCount;
                }
                for (int i = 0; i < trackedFaceCount; i++) {
                    mFURenderer.getLandmarksData(i, FURenderer.LANDMARKS, mLandmarksDataArray[i]);
                }
                mCameraRenderer.setLandmarksDataArray(mLandmarksDataArray);
            } else {
                if (mTrackedFaceCount != trackedFaceCount) {
                    mTrackedFaceCount = trackedFaceCount;
                    if (FURenderer.FACE_LANDMARKS_75 == landmarksType) {
                        mLandmarksDataNew = new float[trackedFaceCount * 75 * 2];
                    } else if (FURenderer.FACE_LANDMARKS_239 == landmarksType) {
                        mLandmarksDataNew = new float[trackedFaceCount * 239 * 2];
                    }
                }
                mFURenderer.getLandmarksData(0, FURenderer.LANDMARKS_NEW, mLandmarksDataNew);
                mCameraRenderer.setLandmarksDataNew(mLandmarksDataNew);
            }
        } else {
            if (FURenderer.FACE_LANDMARKS_DDE == landmarksType) {
                if (mLandmarksDataArray != null) {
                    for (float[] data : mLandmarksDataArray) {
                        Arrays.fill(data, 0F);
                    }
                    mCameraRenderer.setLandmarksDataArray(mLandmarksDataArray);
                }
            } else {
                if (mLandmarksDataNew != null) {
                    Arrays.fill(mLandmarksDataNew, 0F);
                    mCameraRenderer.setLandmarksDataNew(mLandmarksDataNew);
                }
            }
        }
    }

    /**
     * 发送录制数据
     *
     * @param texId
     * @param texMatrix
     * @param timeStamp
     */
    protected void sendRecordingData(int texId, float[] mvpMatrix, float[] texMatrix, final long timeStamp) {
        if (mVideoEncoder != null) {
            mVideoEncoder.frameAvailableSoon(texId, texMatrix, mvpMatrix);
            if (mStartTime == 0) {
                mStartTime = timeStamp;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTakePicBtn.setSecond(timeStamp - mStartTime);
                }
            });
        }
    }

    private File mVideoOutFile;
    private MediaMuxerWrapper mMuxer;
    private volatile MediaVideoEncoder mVideoEncoder;
    private CountDownLatch mCountDownLatch;

    /**
     * 录制封装回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                Log.d(TAG, "onPrepared: tid:" + Thread.currentThread().getId());
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        mVideoEncoder = videoEncoder;
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTakePicBtn.setSecond(0);
                    }
                });
            }
        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            mCountDownLatch.countDown();
            // Call when MediaVideoEncoder's callback and MediaAudioEncoder's callback both are called.
            if (mCountDownLatch.getCount() == 0) {
                Log.d(TAG, "onStopped: tid:" + Thread.currentThread().getId());
                // onStopped is called on codec thread, it may be interrupted, so we execute following code async.
                ThreadHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final File dcimFile = new File(Constant.cameraFilePath, mVideoOutFile.getName());
                            FileUtils.copyFile(mVideoOutFile, dcimFile);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTakePicBtn.setSecond(mStartTime = 0);
                                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
                                    ToastUtil.showToast(FUBaseActivity.this, R.string.save_video_success);
                                }
                            });
                        } catch (IOException e) {
                            Log.e(TAG, "copyFile: ", e);
                        }
                    }
                });
            }
        }
    };

    /**
     * 开始录制
     */
    private void startRecording() {
        try {
            mCountDownLatch = new CountDownLatch(2);
            String videoFileName = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".mp4";
            mVideoOutFile = new File(FileUtils.getExternalCacheDir(this), videoFileName);
            mMuxer = new MediaMuxerWrapper(mVideoOutFile.getAbsolutePath());

            // for video capturing
            int videoWidth = BaseCameraRenderer.DEFAULT_PREVIEW_HEIGHT;
            int videoHeight = mCameraRenderer.getHeight4Video() / 2 * 2; // 取偶数
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, videoWidth, videoHeight);
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    /**
     * 停止录制
     */
    private void stopRecording() {
        if (mMuxer != null) {
            mVideoEncoder = null;
            mMuxer.stopRecording();
            Log.d(TAG, "stopRecording: ");
            mMuxer = null;
        }
    }

    private void showMorePopupWindow() {
        if (mPopupWindow == null) {
            int width = getResources().getDimensionPixelSize(R.dimen.x682);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_popup_more, null);
            RadioGroup rgSolution = view.findViewById(R.id.rg_resolutions);
            rgSolution.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_resolution_480p:
                            mCameraRenderer.changeResolution(640, 480);
                            break;
                        case R.id.rb_resolution_720p:
                            mCameraRenderer.changeResolution(1280, 720);
                            break;
                        case R.id.rb_resolution_1080p:
                            mCameraRenderer.changeResolution(1920, 1080);
                            break;
                        default:
                    }
                    mFURenderer.cameraChanged();
                }
            });
            if (isOpenPhotoVideo()) {
                ConstraintLayout clSelectPhoto = view.findViewById(R.id.cl_select_photo);
                clSelectPhoto.setVisibility(View.VISIBLE);
                clSelectPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSelectPhotoVideoClick();
                    }
                });
            } else {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rgSolution.getLayoutParams();
                params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x40);
            }

            mPopupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.photo_more_popup_anim_style);
        }

        int xOffset = getResources().getDimensionPixelSize(R.dimen.x386);
        int yOffset = getResources().getDimensionPixelSize(R.dimen.x12);
        mPopupWindow.showAsDropDown(mIvShowMore, -xOffset + mIvShowMore.getWidth() / 2, yOffset);
    }

    protected void onSelectPhotoVideoClick() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    protected boolean isOpenPhotoVideo() {
        return false;
    }

    protected boolean isOpenResolutionChange() {
        return false;
    }

    protected int getLandmarksType() {
        return FURenderer.FACE_LANDMARKS_DDE;
    }

}
