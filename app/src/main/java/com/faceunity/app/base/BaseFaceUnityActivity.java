package com.faceunity.app.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.entity.FunctionConfigModel;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.app.utils.SystemUtil;
import com.faceunity.app.view.SelectDataActivity;
import com.faceunity.core.camera.FUCamera;
import com.faceunity.core.entity.FUCameraConfig;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.enumeration.CameraFacingEnum;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.enumeration.FUTransformMatrixEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.media.photo.OnPhotoRecordingListener;
import com.faceunity.core.media.photo.PhotoRecordHelper;
import com.faceunity.core.media.video.OnVideoRecordingListener;
import com.faceunity.core.media.video.VideoRecordHelper;
import com.faceunity.core.renderer.CameraRenderer;
import com.faceunity.core.utils.CameraUtils;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.ui.button.RecordBtn;
import com.faceunity.ui.dialog.ToastHelper;
import com.faceunity.ui.widget.CameraFocus;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * DESC：
 * Created on 2021/3/1
 */
public abstract class BaseFaceUnityActivity extends BaseActivity implements View.OnClickListener {

    //region Activity生命周期绑定

    @Override
    public void onResume() {
        super.onResume();
        mCameraRenderer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isRecording) {
            isRecording = false;
            onStopRecord();
        }
        mCameraRenderer.onPause();
    }


    @Override
    public void onDestroy() {
        mCameraRenderer.onDestroy();
        super.onDestroy();
    }

    //endregion 生命周期绑定


    //region Activity OnCreate
    protected ViewStub mStubBottom;
    protected View mStubView;
    protected GLSurfaceView mSurfaceView;
    protected TextView mTrackingView;
    protected CameraFocus mCameraFocusView;
    protected RecordBtn mTakePicView;
    protected TextView mDebugView;
    protected ImageButton mMoreView;
    protected TextView mEffectDescription;
    protected RelativeLayout mCustomView;
    protected FrameLayout mRootView;
    protected RadioGroup mRenderTypeView;
    protected ImageView mBackView;

    /* 更多弹框*/
    private PopupWindow mPopupWindow;

    protected Handler mMainHandler;
    /* 对焦光标消失*/
    private final Runnable cameraFocusDismiss = () -> {
        mCameraFocusView.layout(0, 0, 0, 0);
        findViewById(R.id.lyt_photograph_light).setVisibility(View.INVISIBLE);
    };

    private FunctionConfigModel mFunctionConfigModel;
    private boolean isSpecialDevice = false;


    @Override
    public int getLayoutResID() {
        return R.layout.activity_live_main;
    }

    @Override
    public void initData() {
        mMainHandler = new Handler();
        mVideoRecordHelper = new VideoRecordHelper(this, mOnVideoRecordingListener);
        mPhotoRecordHelper = new PhotoRecordHelper(mOnPhotoRecordingListener);
        mFunctionConfigModel = FunctionConfigModel.functionSwitchMap.get(getFunctionType());
    }

    @Override
    public void initView() {
        isSpecialDevice = SystemUtil.isSpeDevice();
        mStubBottom = findViewById(R.id.stub_bottom);
        mStubBottom.setInflatedId(R.id.stub_bottom);
        if (getStubBottomLayoutResID() != 0) {
            mStubBottom.setLayoutResource(getStubBottomLayoutResID());
            mStubView = mStubBottom.inflate();
        }
        mRootView = findViewById(R.id.fyt_root);
        mBackView = findViewById(R.id.iv_back);
        mCustomView = findViewById(R.id.cyt_custom_view);
        mSurfaceView = findViewById(R.id.gl_surface);
        mTrackingView = findViewById(R.id.tv_tracking);
        mCameraFocusView = findViewById(R.id.focus);
        mTakePicView = findViewById(R.id.btn_take_pic);
        mEffectDescription = findViewById(R.id.tv_effect_description);
        mDebugView = findViewById(R.id.tv_debug);
        mDebugView.setText(String.format(getString(R.string.fu_base_debug), 0, 0, 0, 0));
        mMoreView = findViewById(R.id.btn_more);
        if (mFunctionConfigModel.isOpenResolutionChange) {
            mMoreView.setBackgroundResource(R.mipmap.icon_live_more);
        } else if (mFunctionConfigModel.isOpenPhotoVideo) {
            mMoreView.setBackgroundResource(R.mipmap.icon_live_photo);
        } else {
            mMoreView.setVisibility(View.INVISIBLE);
        }
        mRenderTypeView = findViewById(R.id.radio_render_input);
        mRenderTypeView.setVisibility(mFunctionConfigModel.isOpenFURenderInput ? View.VISIBLE : View.INVISIBLE);
    }


    @Override
    public void bindListener() {
        mCameraRenderer = new CameraRenderer(mSurfaceView, getCameraConfig(), mOnGlRendererListener);
        /* 亮度调整*/
        ((SeekBar) findViewById(R.id.seek_photograph_light)).setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        /* 切换前后摄像头*/
        findViewById(R.id.btn_camera_change).setOnClickListener(this);
        /* fps日志展示*/
        findViewById(R.id.btn_debug).setOnClickListener(this);
        /* 返回 */
        mBackView.setOnClickListener(this);
        /* 拍照*/
        mTakePicView.setOnRecordListener(mOnRecordListener);
        /* 更多 */
        mMoreView.setOnClickListener(this);
        /* 单双输入切换 */
        mRenderTypeView.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_render_dual:
                    cameraRenderType = 0;
                    break;
                case R.id.rb_render_tex:
                    cameraRenderType = 1;
                    break;
            }
        });
    }

    /**
     * 底部功能栏id
     *
     * @return Int
     */
    protected abstract int getStubBottomLayoutResID();


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_camera_change:
                if (mCameraRenderer.getFUCamera() != null) {
                    mCameraRenderer.getFUCamera().switchCamera();
                }
                break;

            case R.id.btn_debug:
                if (mDebugView.getVisibility() == View.VISIBLE) {
                    isShowBenchmark = false;
                    mDebugView.setVisibility(View.GONE);
                } else {
                    isShowBenchmark = true;
                    mDebugView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_more:
                if (mFunctionConfigModel.isOpenResolutionChange) {
                    showMorePopupWindow();
                } else if (mFunctionConfigModel.isOpenPhotoVideo) {
                    onSelectPhotoVideoClick();
                }
        }
    }


    //endregion Activity OnCreate

    //region CameraRenderer
    protected FURenderKit mFURenderKit = FURenderKit.getInstance();
    protected FUAIKit mFUAIKit = FUAIKit.getInstance();
    protected CameraRenderer mCameraRenderer;
    private int cameraRenderType = 0;

    /*Benchmark 开关*/
    private boolean isShowBenchmark = false;
    /*检测 开关*/
    protected boolean isAIProcessTrack = true;
    /*检测标识*/
    protected int aIProcessTrackStatus = 1;


    /**
     * 特效配置
     */
    protected void configureFURenderKit() {
        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
    }

    /**
     * 配置相机参数
     *
     * @return CameraBuilder
     */
    protected FUCameraConfig getCameraConfig() {
        FUCameraConfig cameraConfig = new FUCameraConfig();
        return cameraConfig;
    }

    /**
     * 检测类型
     *
     * @return
     */
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        return FUAIProcessorEnum.FACE_PROCESSOR;
    }

    /**
     * 检测结果变更回调
     *
     * @param fuaiProcessorEnum
     * @param status
     */
    protected void onTrackStatusChanged(FUAIProcessorEnum fuaiProcessorEnum, int status) {
        mTrackingView.setVisibility((status > 0) ? View.INVISIBLE : View.VISIBLE);
        if (status <= 0) {
            if (fuaiProcessorEnum == FUAIProcessorEnum.FACE_PROCESSOR) {
                mTrackingView.setText(R.string.fu_base_is_tracking_text);
            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
                mTrackingView.setText(R.string.toast_not_detect_body);
            }
            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
                mTrackingView.setText(R.string.toast_not_detect_gesture);
            }
        }
    }

    /**
     * 日志回调
     *
     * @param width
     * @param height
     * @param fps
     * @param renderTime
     */
    protected void onBenchmarkFPSChanged(int width, int height, double fps, double renderTime) {
        mDebugView.setText(String.format(getString(R.string.fu_base_debug), width, height, (int) fps, (int) renderTime));
    }


    /**
     * 获取SurfaceView视图窗口
     *
     * @param width
     * @param height
     */
    protected void onSurfaceChanged(int width, int height) {
    }

    protected void onSurfaceCreated() {
    }

    protected void onDrawFrameAfter() {

    }

    protected void onRenderBefore(FURenderInputData inputData) {

    }

    /* CameraRenderer 回调*/
    private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {


        private int width;//数据宽
        private int height;//数据高
        private long mFuCallStartTime = 0; //渲染前时间锚点（用于计算渲染市场）


        private int mCurrentFrameCnt = 0;
        private int mMaxFrameCnt = 10;
        private long mLastOneHundredFrameTimeStamp = 0;
        private long mOneHundredFrameFUTime = 0;


        @Override
        public void onSurfaceCreated() {
            configureFURenderKit();
            BaseFaceUnityActivity.this.onSurfaceCreated();
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            runOnUiThread(() -> BaseFaceUnityActivity.this.onSurfaceChanged(width, height));
        }

        @Override
        public void onRenderBefore(FURenderInputData inputData) {
            if (isSpecialDevice) {
                //目前这个是Nexus 6P
                if (inputData.getRenderConfig().getCameraFacing() == CameraFacingEnum.CAMERA_FRONT) {
                    inputData.getRenderConfig().setInputTextureMatrix(FUTransformMatrixEnum.CCROT90_FLIPVERTICAL);
                    inputData.getRenderConfig().setInputBufferMatrix(FUTransformMatrixEnum.CCROT90_FLIPVERTICAL);
                }
            }
            width = inputData.getWidth();
            height = inputData.getHeight();
            mFuCallStartTime = System.nanoTime();
            if (cameraRenderType == 1) {
                inputData.setImageBuffer(null);
            }
            BaseFaceUnityActivity.this.onRenderBefore(inputData);
        }


        @Override
        public void onRenderAfter(@NonNull FURenderOutputData outputData, @NotNull FURenderFrameData frameData) {
            recordingData(outputData, frameData.getTexMatrix());
        }

        @Override
        public void onDrawFrameAfter() {
            trackStatus();
            benchmarkFPS();
            BaseFaceUnityActivity.this.onDrawFrameAfter();
        }


        @Override
        public void onSurfaceDestroy() {
            mFURenderKit.release();
        }

        /*AI识别数目检测*/
        private void trackStatus() {
            if (!isAIProcessTrack) {
                return;
            }
            FUAIProcessorEnum fuaiProcessorEnum = getFURenderKitTrackingType();
            int trackCount;
            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
                trackCount = mFUAIKit.handProcessorGetNumResults();
            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
                trackCount = mFUAIKit.humanProcessorGetNumResults();
            } else {
                trackCount = mFUAIKit.isTracking();
            }
            if (aIProcessTrackStatus != trackCount) {
                aIProcessTrackStatus = trackCount;
                runOnUiThread(() -> onTrackStatusChanged(fuaiProcessorEnum, trackCount));
            }
        }

        /*渲染FPS日志*/
        private void benchmarkFPS() {
            if (!isShowBenchmark) {
                return;
            }
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
            if (++mCurrentFrameCnt == mMaxFrameCnt) {
                mCurrentFrameCnt = 0;
                double fps = ((double) mMaxFrameCnt) * 1000000000L / (System.nanoTime() - mLastOneHundredFrameTimeStamp);
                double renderTime = ((double) mOneHundredFrameFUTime) / mMaxFrameCnt / 1000000L;
                mLastOneHundredFrameTimeStamp = System.nanoTime();
                mOneHundredFrameFUTime = 0;
                runOnUiThread(() -> onBenchmarkFPSChanged(width, height, fps, renderTime));
            }
        }

        /*录制保存*/
        private void recordingData(FURenderOutputData outputData, float[] texMatrix) {
            if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
                return;
            }
            if (isRecordingPrepared) {
                mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX);
            }
            if (isTakePhoto) {
                isTakePhoto = false;
                mPhotoRecordHelper.sendRecordingData(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, outputData.getTexture().getWidth(), outputData.getTexture().getHeight());
            }
        }
    };


    //endregion CameraRenderer

    //region  其他业务

    /**
     * 当前功能对应类别 FunctionEnum值
     *
     * @return Boolean
     */
    protected abstract int getFunctionType();


    /**
     * 更多按钮-加载文件
     *
     * @return Boolean
     */
    private void onSelectPhotoVideoClick() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        SelectDataActivity.startActivity(this, getFunctionType());
    }


    /**
     * 调整拍照按钮对齐方式
     *
     * @param margin Int
     */
    protected void changeTakePicButtonMargin(int margin, int width) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTakePicView.getLayoutParams();
        params.bottomMargin = margin;
        mTakePicView.setDrawWidth(width);
        mTakePicView.setLayoutParams(params);
    }

    /**
     * 调整拍照按钮位置大小
     *
     * @param width    Int
     * @param showRate Float
     * @param margin   Int
     * @param diff     Int
     */
    protected void updateTakePicButton(int width, Float showRate, int margin, int diff, Boolean changeSize) {
        int currentWidth = changeSize ? (int) (width * (1 - showRate * 0.265)) : width;
        int currentMargin = margin + (int) (diff * showRate);
        changeTakePicButtonMargin(currentMargin, currentWidth);
    }


    /**
     * 显示提示描述
     *
     * @param strRes Int
     * @param time   Int
     */
    public void showDescription(int strRes, long time) {
        if (strRes == 0) {
            return;
        }
        runOnUiThread(() -> showToast(strRes));
    }


    /**
     * 显示提示描述
     */
    public void showToast(String msg) {
        ToastHelper.showNormalToast(this, msg);
    }

    /**
     * 显示提示描述
     */
    public void showToast(int res) {
        ToastHelper.showWhiteTextToast(this, res);
    }

    /**
     * 更多按钮-点击事件绑定
     */
    private void showMorePopupWindow() {
        if (mPopupWindow == null) {
            int width = getResources().getDimensionPixelSize(R.dimen.x682);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_common_popup_more, null);
            RadioGroup rgSolution = view.findViewById(R.id.rg_resolutions);
            RelativeLayout clSelectPhoto = view.findViewById(R.id.rly_select_photo);
            rgSolution.setOnCheckedChangeListener(mMorePopupWindowCheckedChangeListener);
            clSelectPhoto.setOnClickListener(v -> onSelectPhotoVideoClick());

            rgSolution.setVisibility(mFunctionConfigModel.isOpenResolutionChange ? View.VISIBLE : View.GONE);
            clSelectPhoto.setVisibility(mFunctionConfigModel.isOpenPhotoVideo ? View.VISIBLE : View.GONE);
            mPopupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.photo_more_popup_anim_style);
            showHideMoreWindowView(view);
        }
        int xOffset = getResources().getDimensionPixelSize(R.dimen.x386);
        int yOffset = getResources().getDimensionPixelSize(R.dimen.x12);
        mPopupWindow.showAsDropDown(mMoreView, -xOffset + mMoreView.getWidth() / 2, yOffset);
    }

    public void showHideMoreWindowView(View view) {
    }

    @SuppressLint("NonConstantResourceId")
    private RadioGroup.OnCheckedChangeListener mMorePopupWindowCheckedChangeListener = (group, checkedId) -> {
        FUCamera fuCamera = mCameraRenderer.getFUCamera();
        if (fuCamera == null) return;
        switch (checkedId) {
            case R.id.rb_resolution_480p:
                fuCamera.changeResolution(640, 480);
                break;
            case R.id.rb_resolution_720p:
                fuCamera.changeResolution(1280, 720);
                break;
            case R.id.rb_resolution_1080p:
                fuCamera.changeResolution(1920, 1080);
                break;
        }

    };


    /*亮度调节  */
    private final SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            FUCamera camera = mCameraRenderer.getFUCamera();
            if (camera != null) {
                camera.setExposureCompensation(((float) progress) / 100);
            }
            mMainHandler.removeCallbacks(cameraFocusDismiss);
            mMainHandler.postDelayed(cameraFocusDismiss, CameraUtils.FOCUS_TIME);

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };


    /* 拍照按钮事件回调  */
    private final RecordBtn.OnRecordListener mOnRecordListener = new RecordBtn.OnRecordListener() {
        @Override
        public void stopRecord() {
            if (isRecording) {
                isRecording = false;
                BaseFaceUnityActivity.this.onStopRecord();
            }
        }

        @Override
        public void startRecord() {
            if (!isRecording) {
                isRecording = true;
                BaseFaceUnityActivity.this.onStartRecord();
            }
        }

        @Override
        public void takePic() {
            isTakePhoto = true;
        }
    };

    /**
     * 触碰事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            return true;
        }
        if (!mFunctionConfigModel.isShowAutoFocus) {
            return false;
        }
        if (event.getPointerCount() == 1 && event.getAction() == MotionEvent.ACTION_DOWN) {
            FUCamera fuCamera = mCameraRenderer.getFUCamera();
            findViewById(R.id.lyt_photograph_light).setVisibility(View.VISIBLE);
            int progress = (int) ((fuCamera == null) ? 0f : fuCamera.getExposureCompensation() * 100);
            ((SeekBar) findViewById(R.id.seek_photograph_light)).setProgress(progress);
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            int focusRectSize = getResources().getDimensionPixelSize(R.dimen.x150);
            DisplayMetrics screenInfo = getScreenInfo();
            int screenWidth = screenInfo.widthPixels;
            int marginTop = getResources().getDimensionPixelSize(R.dimen.x280);
            int padding = getResources().getDimensionPixelSize(R.dimen.x44);
            int progressBarHeight = getResources().getDimensionPixelSize(R.dimen.x460);
            if (rawX > screenWidth - focusRectSize && rawY > marginTop - padding && rawY < marginTop + progressBarHeight + padding
            ) {
                return false;
            }
            if (fuCamera != null) {
                fuCamera.handleFocus(mSurfaceView.getWidth(), mSurfaceView.getHeight(), rawX, rawY, focusRectSize);
            }
            mCameraFocusView.showCameraFocus(rawX, rawY);

            mMainHandler.removeCallbacks(cameraFocusDismiss);
            mMainHandler.postDelayed(cameraFocusDismiss, CameraUtils.FOCUS_TIME);
            return true;
        }
        return false;
    }


    /**
     * 获取屏幕信息
     *
     * @return
     */
    private DisplayMetrics getScreenInfo() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics;
    }

    //endregion

    //region 视频录制

    private VideoRecordHelper mVideoRecordHelper;
    private volatile boolean isRecordingPrepared = false;
    private boolean isRecording = false;
    private volatile long recordTime = 0;


    protected void onStartRecord() {
        mVideoRecordHelper.startRecording(mSurfaceView, mCameraRenderer.getFUCamera().getCameraHeight(), mCameraRenderer.getFUCamera().getCameraWidth());
    }

    protected void onStopRecord() {
        mTakePicView.setSecond(0);
        mVideoRecordHelper.stopRecording();
    }

    private OnVideoRecordingListener mOnVideoRecordingListener = new OnVideoRecordingListener() {

        @Override
        public void onPrepared() {
            isRecordingPrepared = true;
        }

        @Override
        public void onProcess(Long time) {
            recordTime = time;
            runOnUiThread(() -> {
                if (isRecording) {
                    mTakePicView.setSecond(time);
                }
            });

        }

        @Override
        public void onFinish(File file) {
            isRecordingPrepared = false;
            if (recordTime < 1100) {
                runOnUiThread(() -> ToastHelper.showNormalToast(BaseFaceUnityActivity.this, R.string.save_video_too_short));
            } else {
                String filePath = FileUtils.addVideoToAlbum(BaseFaceUnityActivity.this, file);
                if (filePath == null || filePath.trim().length() == 0) {
                    runOnUiThread(() -> ToastHelper.showNormalToast(BaseFaceUnityActivity.this, R.string.save_video_failed));
                } else {
                    runOnUiThread(() -> ToastHelper.showNormalToast(BaseFaceUnityActivity.this, R.string.save_video_success));
                }
            }
            if (file.exists()) {
                file.delete();
            }
        }

    };

    //endregion 视频录制
    //region 拍照

    private PhotoRecordHelper mPhotoRecordHelper;
    private volatile Boolean isTakePhoto = false;

    /**
     * 获取拍摄的照片
     */
    private final OnPhotoRecordingListener mOnPhotoRecordingListener = this::onReadBitmap;


    protected void onReadBitmap(Bitmap bitmap) {
        new Thread(() -> {
            String path = FileUtils.addBitmapToAlbum(this, bitmap);
            if (path == null) return;
            runOnUiThread(() -> ToastHelper.showNormalToast(BaseFaceUnityActivity.this, R.string.save_photo_success));
        }).start();
    }


    //endregion 拍照

}
