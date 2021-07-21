package com.faceunity.app.view;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.BgSegGreenDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.callback.OnColorReadCallback;
import com.faceunity.core.entity.FUCoordinate2DData;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.core.media.rgba.RGBAPicker;
import com.faceunity.core.media.video.VideoPlayHelper;
import com.faceunity.core.model.bgSegGreen.BgSegGreen;
import com.faceunity.core.utils.GestureTouchHandler;
import com.faceunity.ui.control.BgSegGreenControlView;
import com.faceunity.ui.dialog.PromptDialogFragment;
import com.faceunity.ui.entity.BgSegGreenBackgroundBean;
import com.faceunity.ui.widget.ColorPickerView;

/**
 * DESC：
 * Created on 2021/3/4
 */
public class BgSegGreenActivity extends BaseFaceUnityActivity {

    private BgSegGreenControlView mBgSegGreenControlView;
    private BgSegGreenDataFactory mBgSegGreenDataFactory;
    private VideoPlayHelper mVideoPlayHelper;


    /*手势动作*/
    private GestureTouchHandler mGestureTouchHandler;

    private boolean isReadRGBA = false;
    private boolean mIsShowColorPicker = false;
    private int anchorX = 0;
    private int anchorY = 0;
    private int argbA = 0;
    private int argbR = 0;
    private int argbG = 0;
    private int argbB = 0;

    private int viewTopOffset;//取色器触碰向上偏移一个手指


    //region   onCreate初始化

    private ColorPickerView mColorPickerView;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_bsg;
    }

    @Override
    public void initData() {
        super.initData();
        mBgSegGreenDataFactory = new BgSegGreenDataFactory(mBgSegGreenListener, 3);
        viewTopOffset = getResources().getDimensionPixelSize(R.dimen.x64);
    }

    @Override
    public void initView() {
        super.initView();
        mBgSegGreenControlView = (BgSegGreenControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x397), getResources().getDimensionPixelSize(R.dimen.x122));
        mColorPickerView = new ColorPickerView(this);
        addColorPickerView();
        PromptDialogFragment promptDialogFragment = PromptDialogFragment.newInstance(this, R.string.dialog_guide_bg_seg_green);
        promptDialogFragment.show(getSupportFragmentManager(), "PromptDialogFragment");

    }

    @Override
    public void bindListener() {
        super.bindListener();
        /*VideoDecoderHelper 需要在Surface初始化之后*/
        mVideoPlayHelper = new VideoPlayHelper(mVideoDecoderListener, mSurfaceView, false);
        mBgSegGreenControlView.bindDataFactory(mBgSegGreenDataFactory);
        mBgSegGreenControlView.setOnBottomAnimatorChangeListener(showRate -> {
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x166), showRate, getResources().getDimensionPixelSize(R.dimen.x128),
                    getResources().getDimensionPixelSize(R.dimen.x269), true);
        });
        mGestureTouchHandler = new GestureTouchHandler(this);
        mGestureTouchHandler.setOnTouchResultListener(mOnTouchResultListener);
    }


    /**
     * 绑定取色器视图
     */
    private void addColorPickerView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mColorPickerView.setVisibility(View.GONE);
        mCustomView.addView(mColorPickerView, layoutParams);
    }

    //endregion


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        mVideoPlayHelper.pausePlay();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mVideoPlayHelper.release();
        super.onDestroy();
    }


    //region 重写

    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mBgSegGreenDataFactory.bindCurrentRenderer();
    }

    @Override
    protected void onSurfaceChanged(int width, int height) {
        mGestureTouchHandler.setViewSize(width, height);
    }

    @Override
    protected void onTrackStatusChanged(FUAIProcessorEnum fuaiProcessorEnum, int status) {

    }


    @Override
    protected void onDrawFrameAfter() {
        if (isReadRGBA) {
            RGBAPicker.readRgba(anchorX, anchorY, mOnReadRgbaListener);
        }
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.BG_SEG_GREEN;
    }

    //endregion 重写
    //region 回调

    private VideoPlayHelper.VideoDecoderListener mVideoDecoderListener = (bytes, width, height) -> {
        BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
        if (bgSegGreen == null) {
            return;
        }
        bgSegGreen.createBgSegment(bytes, width, height);
    };


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
     * GlSurfaceRenderer 取色结果实时回调
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
            mColorPickerView.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            mColorPickerView.updatePickerColor(color);
        }

        @Override
        public void onBackgroundSelected(BgSegGreenBackgroundBean bean) {
            if (bean.getFilePath() != null) {
                mVideoPlayHelper.playAssetsVideo(BgSegGreenActivity.this, bean.getFilePath());
            } else {
                mVideoPlayHelper.pausePlay();
                BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
                if (bgSegGreen != null) {
                    bgSegGreen.removeBgSegment();
                }
            }
        }
    };


    //endregion 回调
    //region触碰处理

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                mColorPickerView.setVisibility(View.GONE);
                mBgSegGreenControlView.setPaletteColorPicked(argbR, argbG, argbB);
                return true;
            }
            return false;
        } else {
            return mGestureTouchHandler.onTouchEvent(event);
        }


    }
    //endregion


}
