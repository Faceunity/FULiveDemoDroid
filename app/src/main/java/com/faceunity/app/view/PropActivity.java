package com.faceunity.app.view;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.PropDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.ui.control.PropControlView;
import com.faceunity.ui.entity.PropBean;
import com.faceunity.ui.entity.uistate.PropControlState;

/**
 * DESC：道具贴纸
 * Created on 2021/3/2
 */
public class PropActivity extends BaseFaceUnityActivity {
    private static final String TYPE = "type";

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, PropActivity.class).putExtra(TYPE, type));
    }

    private PropControlView mPropControlView;
    private PropDataFactory mPropDataFactory;

    private int mFunctionType;

    public static PropControlState propControlState = null;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_prop;
    }

    @Override
    public void initData() {
        mFunctionType = getIntent().getIntExtra(TYPE, 0);
        super.initData();
        mPropDataFactory = new PropDataFactory(mPropListener, mFunctionType, 1);
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mPropDataFactory.bindCurrentRenderer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needUpdateUI) {
            mPropControlView.updateUIStates(propControlState);
            propControlState = null;
            needUpdateUI = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        propControlState = mPropControlView.getUIStates();
    }

    @Override
    public void initView() {
        super.initView();
        mPropControlView = (PropControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x212));
    }

    @Override
    public void bindListener() {
        super.bindListener();
        mPropControlView.bindDataFactory(mPropDataFactory);
    }

    @Override
    protected int getFunctionType() {
        return mFunctionType;
    }


    @Override
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            return FUAIProcessorEnum.HUMAN_PROCESSOR;
        } else if (mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
            mFUAIKit.setHandDetectEveryNFramesWhenNoHand(DemoConfig.HAND_DETECT_WHEN_NO_HAND_NUM);
            return FUAIProcessorEnum.HAND_GESTURE_PROCESSOR;
        } else return FUAIProcessorEnum.FACE_PROCESSOR;
    }

    @Override
    public void onDestroy() {
        mPropDataFactory.releaseAIProcessor();
        super.onDestroy();
    }

    private PropDataFactory.PropListener mPropListener = new PropDataFactory.PropListener() {
        @Override
        public void onItemSelected(PropBean bean) {
            if (mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
                if (bean.getPath() == null) {
                    mMainHandler.post(() -> {
                        isAIProcessTrack = false;
                        mTrackingView.setVisibility(View.INVISIBLE);
                        aIProcessTrackStatus = 1;
                    });
                } else {
                    isAIProcessTrack = true;
                }
            }
            if (bean.getDescId() > 0) {
                mMainHandler.post(() -> showDescription(bean.getDescId(), 1500));
            }
        }
    };
}
