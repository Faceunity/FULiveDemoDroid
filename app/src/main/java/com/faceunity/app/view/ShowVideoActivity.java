package com.faceunity.app.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.core.callback.OnColorReadCallback;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.media.rgba.RGBAPicker;
import com.faceunity.core.media.video.OnVideoRecordingListener;
import com.faceunity.core.media.video.VideoPlayHelper;
import com.faceunity.core.media.video.VideoRecordHelper;
import com.faceunity.core.entity.FUCoordinate2DData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.listener.OnVideoPlayListener;
import com.faceunity.core.model.bgSegGreen.BgSegGreen;
import com.faceunity.core.renderer.VideoRenderer;
import com.faceunity.core.utils.GestureTouchHandler;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.data.BgSegGreenDataFactory;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.PropDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.ui.control.BgSegGreenControlView;
import com.faceunity.ui.control.FaceBeautyControlView;
import com.faceunity.ui.control.PropControlView;
import com.faceunity.ui.dialog.ToastHelper;
import com.faceunity.ui.entity.BgSegGreenBackgroundBean;
import com.faceunity.ui.widget.ColorPickerView;
import com.faceunity.ui.widget.CustomImageButton;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * DESC：
 * Created on 2021/3/2
 */
public class ShowVideoActivity extends BaseActivity {
    public static final String TYPE = "type";
    public static final String PATH = "path";

    public static void startActivity(Context context, int type, String path) {
        context.startActivity(new Intent(context, ShowVideoActivity.class).putExtra(TYPE, type).putExtra(PATH, path));
    }

    protected FURenderKit mFURenderKit = FURenderKit.getInstance();
    protected VideoRenderer mVideoRenderer;

    private VideoPlayHelper mVideoPlayHelper;

    //region Activity生命周期绑定
    private Boolean isActivityPause = false;

    @Override
    public void onResume() {
        if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            BgSegGreenBackgroundBean bean = mBgSegGreenDataFactory.getBgSegGreenBackgrounds().get(mBgSegGreenDataFactory.getBackgroundIndex());
            mVideoPlayHelper.playAssetsVideo(this, bean.getFilePath());
        }
        super.onResume();
        mVideoRenderer.onResume();
        if (isActivityPause) {
            mPlayView.setVisibility(View.VISIBLE);
        }
        isActivityPause = false;
    }

    @Override
    public void onPause() {
        isActivityPause = true;
        if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mVideoPlayHelper.pausePlay();
        }
        super.onPause();
        if (isSendRecordingData) {
            mVideoRecordHelper.stopRecording();
        }
        mVideoRenderer.onPause();
    }


    @Override
    public void onDestroy() {
        if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mVideoPlayHelper.release();
        }
        mVideoRenderer.onDestroy();
        super.onDestroy();
    }


    //endregion 生命周期绑定


    //region OnCreate
    protected int mVideoHeight;
    protected int mVideoWidth;

    private int mFunctionType;
    private String mVideoPath;

    private View mStubView;
    ;
    private CustomImageButton mPlayView;
    protected GLSurfaceView mSurfaceView;
    protected TextView mTrackingView;
    protected TextView mEffectDescription;
    private ImageButton mSaveView;
    protected RelativeLayout mCustomView;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_show_video;
    }

    @Override
    public void initData() {
        mFunctionType = getIntent().getIntExtra(TYPE, 0);
        mVideoPath = getIntent().getStringExtra(PATH);
        mVideoRecordHelper = new VideoRecordHelper(this, mOnVideoRecordingListener);
        viewTopOffset = getResources().getDimensionPixelSize(R.dimen.x64);
    }

    @Override
    public void initView() {
        ViewStub viewStub = findViewById(R.id.stub_bottom);
        viewStub.setLayoutResource(getBottomLayout());
        mStubView = viewStub.inflate();
        mPlayView = findViewById(R.id.btn_play);
        mSurfaceView = findViewById(R.id.gl_surface);
        mTrackingView = findViewById(R.id.tv_tracking);
        mEffectDescription = findViewById(R.id.tv_effect_description);
        mSaveView = findViewById(R.id.btn_save);
        mCustomView = findViewById(R.id.ryt_custom_view);
        changeTakePicButtonMargin(mFunctionType == FunctionEnum.FACE_BEAUTY ? getResources().getDimensionPixelSize(R.dimen.x156) : getResources().getDimensionPixelSize(R.dimen.x200));

    }


    @Override
    public void bindListener() {
        mVideoRenderer = new VideoRenderer(mSurfaceView, mVideoPath, mOnGlRendererListener);
        mVideoPlayHelper = new VideoPlayHelper(mVideoDecoderListener, mSurfaceView, false);
        /* 播放*/
        mPlayView.setOnClickListener((view) -> {
            if (mSaveView.isSelected()) {
                ToastHelper.showNormalToast(ShowVideoActivity.this, R.string.save_video_wait);
                return;
            }
            mSaveView.setVisibility(View.GONE);
            mPlayView.setVisibility(View.GONE);
            mVideoRecordHelper.startRecording(mSurfaceView, mVideoWidth, mVideoHeight, mVideoPath);

        });
        /* 保存*/
        mSaveView.setOnClickListener((view) -> {
            if (mSaveView.isSelected()) {
                ToastHelper.showNormalToast(ShowVideoActivity.this, R.string.save_video_wait);
                return;
            }
            mSaveView.setVisibility(View.GONE);
            new Thread(() -> {
                String filePath = FileUtils.addVideoToAlbum(ShowVideoActivity.this, recordFile);
                if (recordFile.exists()) {
                    recordFile.delete();
                    recordFile = null;
                }
                if (filePath == null || filePath.trim().length() == 0) {
                    runOnUiThread(() -> ToastHelper.showNormalToast(ShowVideoActivity.this, R.string.save_video_failed));
                } else {
                    runOnUiThread(() -> ToastHelper.showNormalToast(ShowVideoActivity.this, R.string.save_video_success));
                }
            }).start();
        });
        /* 返回 */
        findViewById(R.id.btn_back).setOnClickListener(view -> onBackPressed());
        bindDataFactory();
    }

    //endregion OnCreate
    //region 业务扩展
    private FaceBeautyDataFactory mFaceBeautyDataFactory;//美颜
    private FaceBeautyControlView mFaceBeautyControlView;//美颜

    private BgSegGreenDataFactory mBgSegGreenDataFactory;//绿幕抠像
    private BgSegGreenControlView mBgSegGreenControlView;//绿幕抠像

    private PropDataFactory mPropDataFactory; //道具


    private void bindDataFactory() {
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            mFaceBeautyDataFactory = new FaceBeautyDataFactory(mFaceBeautyListener);
            mFaceBeautyControlView = ((FaceBeautyControlView) mStubView);
            mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
            mFaceBeautyControlView.setOnBottomAnimatorChangeListener(showRate -> {
                // 收起 1-->0，弹出 0-->1
                mSaveView.setAlpha(1 - showRate);
            });
        } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mBgSegGreenDataFactory = new BgSegGreenDataFactory(mBgSegGreenListener, 3);
            mBgSegGreenControlView = ((BgSegGreenControlView) mStubView);
            mBgSegGreenControlView.bindDataFactory(mBgSegGreenDataFactory);
            mBgSegGreenControlView.setOnBottomAnimatorChangeListener(showRate -> {
                // 收起 1-->0，弹出 0-->1
                mSaveView.setAlpha(1 - showRate);
            });
            mSaveView.setAlpha(0f);
            mColorPickerView = new ColorPickerView(this);
            mGestureTouchHandler = new GestureTouchHandler(this);
            mGestureTouchHandler.setOnTouchResultListener(mOnTouchResultListener);
            addColorPickerView();
        } else if (mFunctionType == FunctionEnum.STICKER) {
            mPropDataFactory = new PropDataFactory(mPropListener, mFunctionType, 1);
            ((PropControlView) mStubView).bindDataFactory(mPropDataFactory);
        }
    }

    private void configureFURenderKit() {
        mFURenderKit.getFUAIController().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            mFaceBeautyDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mBgSegGreenDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.STICKER) {
            mPropDataFactory.bindCurrentRenderer();
        }
    }

    private int getBottomLayout() {
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            return R.layout.layout_control_face_beauty;
        } else if (mFunctionType == FunctionEnum.STICKER) {
            return R.layout.layout_control_prop;
        } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            return R.layout.layout_control_bsg;
        }
        return 0;
    }


    /**
     * 调整拍照按钮对齐方式
     *
     * @param margin Int
     */
    protected void changeTakePicButtonMargin(int margin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSaveView.getLayoutParams();
        params.bottomMargin = margin;
        mSaveView.setLayoutParams(params);
    }

    /**
     * 返回拦截
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onStopRecord();
    }

    //endregion 业务扩展
    //region 回调
    private int mTrackStatus = 1;


    /*GLRender 回调  */
    private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {
        private int currentFrame = 0;

        @Override
        public void onRenderAfter(@NotNull FURenderOutputData outputData, @NotNull FURenderFrameData frameData) {
            mVideoWidth = outputData.getTexture().getWidth();
            mVideoHeight = outputData.getTexture().getHeight();
            if (isSendRecordingData) {
                mVideoRecordHelper.frameAvailableSoon(outputData.getTexture().getTexId(), frameData.getTexMatrix(), GlUtil.IDENTITY_MATRIX);
            }
            if (currentFrame++ == 5) {
                mVideoRecordHelper.startRecording(mSurfaceView, mVideoWidth, mVideoHeight, mVideoPath);
            }
        }


        @Override
        public void onDrawFrameAfter() {
            trackStatus();
            if (isReadRGBA) {
                RGBAPicker.readRgba(anchorX, anchorY, mOnReadRgbaListener);
            }
        }


        @Override
        public void onRenderBefore(FURenderInputData inputData) {

        }

        @Override
        public void onSurfaceCreated() {
            configureFURenderKit();
        }

        @Override
        public void onSurfaceChanged(int width, int height) {
            if (mGestureTouchHandler != null) {
                mGestureTouchHandler.setViewSize(width, height);
            }
        }

        @Override
        public void onSurfaceDestroy() {
            mFURenderKit.release();
        }

        private void trackStatus() {
            int trackStatus;
            FUAIProcessorEnum fuaiProcessorEnum = getFURenderKitTrackingType();
            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
                trackStatus = mFURenderKit.getFUAIController().handProcessorGetNumResults();
            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
                trackStatus = mFURenderKit.getFUAIController().humanProcessorGetNumResults();
            } else {
                trackStatus = mFURenderKit.getFUAIController().isTracking();
            }
            if (mTrackStatus != trackStatus) {
                mTrackStatus = trackStatus;
                final int status = trackStatus;
                runOnUiThread(() -> onTrackStatusChanged(fuaiProcessorEnum, status));
            }
        }

    };

    /**
     * 检测类型
     *
     * @return
     */
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            return FUAIProcessorEnum.HUMAN_PROCESSOR;
        } else if (mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
            return FUAIProcessorEnum.HAND_GESTURE_PROCESSOR;
        } else return FUAIProcessorEnum.FACE_PROCESSOR;
    }

    protected void onTrackStatusChanged(FUAIProcessorEnum fuaiProcessorEnum, int status) {
        if (mFunctionType == FunctionEnum.GESTURE_RECOGNITION || mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            return;
        }
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

    private VideoPlayHelper.VideoDecoderListener mVideoDecoderListener = (bytes, width, height) -> {
        BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
        if (bgSegGreen == null) {
            return;
        }
        bgSegGreen.createBgSegment(bytes, width, height);
    };

    //endregion 回调

    //region 视频录制
    private VideoRecordHelper mVideoRecordHelper;
    private volatile Boolean isSendRecordingData = false;
    private File recordFile;


    protected void onStopRecord() {
        mVideoRecordHelper.stopRecording();
    }

    private OnVideoRecordingListener mOnVideoRecordingListener = new OnVideoRecordingListener() {
        @Override
        public void onPrepared() {
            isSendRecordingData = true;
            mVideoRenderer.startMediaPlayer(mOnVideoPlayListener);
        }

        @Override
        public void onProcess(Long time) {

        }

        @Override
        public void onFinish(File file) {
            isSendRecordingData = false;
            recordFile = file;
            runOnUiThread(() -> mSaveView.setSelected(false));
        }
    };


    /**
     * 视频回调
     */
    private OnVideoPlayListener mOnVideoPlayListener = new OnVideoPlayListener() {
        @Override
        public void onError(String error) {

        }

        @Override
        public void onPlayFinish() {
            onStopRecord();
            runOnUiThread(() -> {
                mPlayView.setVisibility(View.VISIBLE);
                mSaveView.setVisibility(View.VISIBLE);
                mSaveView.setSelected(true);
            });
        }
    };


    //endregion 视频录制


    //region 绿幕抠像


    private ColorPickerView mColorPickerView;
    private GestureTouchHandler mGestureTouchHandler;
    private boolean mIsShowColorPicker = false;
    private boolean isReadRGBA = false;
    private int anchorX = 0;
    private int anchorY = 0;
    private int argbA = 0;
    private int argbR = 0;
    private int argbG = 0;
    private int argbB = 0;


    /**
     * 绑定取色器视图
     */
    private void addColorPickerView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mColorPickerView.setVisibility(View.GONE);
        mCustomView.addView(mColorPickerView, layoutParams);
    }

    /**
     * GestureTouchHandler 触碰回调
     */
    private GestureTouchHandler.OnTouchResultListener mOnTouchResultListener = new GestureTouchHandler.OnTouchResultListener() {
        @Override
        public void onTransform(float x1, float y1, float x2, float y2) {
            FUCoordinate2DData fuCoordinate2DData = new FUCoordinate2DData((x1 + x2) / 2, (y1 + y2) / 2);
            BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
            if (bgSegGreen != null) {
                bgSegGreen.setCenterPoint(fuCoordinate2DData);
                bgSegGreen.setZoom((double) (x2 - x1) * (x2 - x1));
            }
        }

        @Override
        public void onClick() {
            mBgSegGreenControlView.dismissBottomLayout();
        }
    };


    /**
     * 取色状态回调
     */
    private BgSegGreenDataFactory.BgSegGreenListener mBgSegGreenListener = new BgSegGreenDataFactory.BgSegGreenListener() {

        @Override
        public void onColorPickerStateChanged(boolean isSelected, int color) {
            if (isSelected) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mColorPickerView.getLayoutParams();
                layoutParams.leftMargin = (mCustomView.getWidth() - mColorPickerView.getWidth()) / 2;
                layoutParams.topMargin = (mCustomView.getHeight() - mColorPickerView.getHeight()) / 2;
                mColorPickerView.setLayoutParams(layoutParams);
                /*纹理坐标左下角为(0,0) 需要进行转换*/
                anchorX = mCustomView.getWidth() / 2;
                anchorY = mCustomView.getHeight() - (layoutParams.topMargin + getResources().getDimensionPixelSize(R.dimen.x104));
            }
            mIsShowColorPicker = isSelected;
            mPlayView.setDispatch(isSelected);

            mColorPickerView.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            mColorPickerView.updatePickerColor(color);
        }

        @Override
        public void onBackgroundSelected(BgSegGreenBackgroundBean bean) {
            if (bean.getFilePath() != null) {
                mVideoPlayHelper.playAssetsVideo(ShowVideoActivity.this, bean.getFilePath());
            } else {
                mVideoPlayHelper.pausePlay();
                BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
                if (bgSegGreen != null) {
                    bgSegGreen.removeBgSegment();
                }
            }
        }
    };

    /**
     * GlSurfaceRenderer 取色回调
     */
    private OnColorReadCallback mOnReadRgbaListener = new OnColorReadCallback() {
        @Override
        public void onReadRgba(int r, int g, int b, int a) {
            argbA = a;
            argbR = r;
            argbG = g;
            argbB = b;
            runOnUiThread(() -> {
                int color = Color.argb(a, r, g, b);
                mColorPickerView.updatePickerColor(color);
                mBgSegGreenControlView.setPalettePickColor(color);
            });

        }
    };


    private int viewTopOffset;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            mFaceBeautyControlView.hideControlView();
        }
        if (super.onTouchEvent(event)) {
            return true;
        } else if (mIsShowColorPicker) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();
            int parentHeight = ((ViewGroup) mColorPickerView.getParent()).getHeight();
            if (touchY > parentHeight - (getResources().getDimensionPixelSize(R.dimen.x367) - viewTopOffset)
                    && !(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mColorPickerView.getLayoutParams();
                layoutParams.leftMargin = touchX - mColorPickerView.getWidth() / 2;
                layoutParams.topMargin = touchY - mColorPickerView.getHeight() - viewTopOffset;
                mColorPickerView.setLayoutParams(layoutParams);
                /*纹理坐标左下角为(0,0) 需要进行转换*/
                int pickY = mCustomView.getHeight() - (layoutParams.topMargin + getResources().getDimensionPixelSize(R.dimen.x104));
                anchorX = touchX;
                anchorY = pickY;
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isReadRGBA = true;
                }
                return false;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                isReadRGBA = false;
                mIsShowColorPicker = false;
                mPlayView.setDispatch(false);
                mColorPickerView.setVisibility(View.GONE);
                mBgSegGreenControlView.setPaletteColorPicked(argbR, argbG, argbB);
                return true;
            }
            return false;
        } else if (mGestureTouchHandler != null) {
            return mGestureTouchHandler.onTouchEvent(event);
        } else {
            return false;
        }


    }

    //endregion 绿幕抠像

    //region页面交互回调
    private FaceBeautyDataFactory.FaceBeautyListener mFaceBeautyListener = new FaceBeautyDataFactory.FaceBeautyListener() {

        @Override
        public void onFilterSelected(int res) {
            ToastHelper.showNormalToast(ShowVideoActivity.this, res);
        }

        @Override
        public void onFaceBeautyEnable(boolean enable) {
            mVideoRenderer.setFURenderSwitch(enable);
        }
    };


    private PropDataFactory.PropListener mPropListener = bean -> {

    };
    //endregion
}
