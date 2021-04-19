package com.faceunity.app.view;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.model.action.ActionRecognition;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class ActionRecognitionActivity extends BaseFaceUnityActivity {

    private ActionRecognition actionRecognition;
    private long mStartTimestamp = 0L;

    @Override
    protected int getStubBottomLayoutResID() {
        return 0;
    }

    @Override
    public void initData() {
        super.initData();
        actionRecognition = new ActionRecognition(new FUBundleData("effect/action/actiongame_android.bundle"));
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mFURenderKit.getFUAIController().loadAIProcessor(DemoConfig.BUNDLE_AI_HUMAN, FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFURenderKit.getFUAIController().setMaxFaces(1);
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mFURenderKit.setActionRecognition(actionRecognition);
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.ACTION_RECOGNITION;
    }


    @Override
    public void initView() {
        super.initView();
        findViewById(R.id.cyt_custom_view).setVisibility(View.GONE);
    }

    @Override
    public void bindListener() {
        super.bindListener();
        updateOnTouchListener();
    }

    @Override
    public void onDestroy() {
        mFURenderKit.getFUAIController().releaseAIProcessor(FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        super.onDestroy();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateOnTouchListener() {
        long mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        int mX = getResources().getDimensionPixelSize(R.dimen.x138);
        int mY = getResources().getDimensionPixelSize(R.dimen.x150);
        findViewById(R.id.gl_surface).setOnTouchListener((v, event) -> {
            if (event.getX() > mX || event.getY() > mY) {
                return false;
            }
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mStartTimestamp = System.currentTimeMillis();
            } else if (action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - mStartTimestamp < mLongPressTimeout) {
                    onBackPressed();
                }
                mStartTimestamp = 0;
            }
            return true;
        });
    }


}
