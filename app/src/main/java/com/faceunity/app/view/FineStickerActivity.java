package com.faceunity.app.view;

import android.view.MotionEvent;
import android.view.View;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FineStickerDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.ui.control.FineStickerView;
import com.faceunity.ui.radio.XfermodeRadioButton;

public class FineStickerActivity extends BaseFaceUnityActivity {
    private FUAIProcessorEnum mFUAIProcessorEnum = FUAIProcessorEnum.FACE_PROCESSOR;

    private FineStickerView fineStickerView;
    private FineStickerDataFactory mFineStickerDataFactory;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_fine_sticker;
    }

    @Override
    public void initData() {
        super.initData();
        mFineStickerDataFactory = new FineStickerDataFactory();
    }

    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mFineStickerDataFactory.bindCurrentRenderer();
    }

    @Override
    public void initView() {
        super.initView();
        fineStickerView = (FineStickerView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x462), getResources().getDimensionPixelSize(R.dimen.x166));
    }

    @Override
    public void bindListener() {
        super.bindListener();
        fineStickerView.bindDataFactory(mFineStickerDataFactory);
        mFineStickerDataFactory.bindView(fineStickerView);
        fineStickerView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x166), showRate,
                    getResources().getDimensionPixelSize(R.dimen.x156), getResources().getDimensionPixelSize(R.dimen.x364), true);
        });

        mFineStickerDataFactory.setBundleTypeListener(bundleType -> {
            if (bundleType != null) {
                if (bundleType == FineStickerDataFactory.BundleType.AVATAR_BUNDLE) {
                    runOnUiThread(()-> mTrackingView.setText(R.string.toast_not_detect_body));
                    mFUAIProcessorEnum = FUAIProcessorEnum.HUMAN_PROCESSOR;
                } else {
                    runOnUiThread(()-> mTrackingView.setText(R.string.fu_base_is_tracking_text));
                    mFUAIProcessorEnum = FUAIProcessorEnum.FACE_PROCESSOR;
                }
            } else {
                runOnUiThread(()-> mTrackingView.setText(R.string.fu_base_is_tracking_text));
                mFUAIProcessorEnum = FUAIProcessorEnum.FACE_PROCESSOR;
            }
        });
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.FINE_STICKER;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        fineStickerView.hideControlView();
        mFineStickerDataFactory.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        mFineStickerDataFactory.acceptEvent();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFineStickerDataFactory.refuseEvent();
    }

    @Override
    public void onDestroy() {
        mFineStickerDataFactory.releaseAIProcessor();
        super.onDestroy();
    }

    @Override
    public void showHideMoreWindowView(View view) {
        if (view != null) {
            XfermodeRadioButton rbResolution480p = view.findViewById(R.id.rb_resolution_480p);
            rbResolution480p.setVisibility(View.GONE);
        }
    }

    @Override
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        return mFUAIProcessorEnum;
    }
}