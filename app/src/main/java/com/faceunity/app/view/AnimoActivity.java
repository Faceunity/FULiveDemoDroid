package com.faceunity.app.view;

import android.view.MotionEvent;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.AnimojiDataFactory;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.ui.control.AnimojiControlView;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class AnimoActivity extends BaseFaceUnityActivity {

    private AnimojiDataFactory mAnimojiDataFactory;
    private AnimojiControlView mAnimojiControlView;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_animo;
    }

    @Override
    public void initData() {
        super.initData();
        mAnimojiDataFactory = new AnimojiDataFactory(0, 0);
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mAnimojiDataFactory.bindCurrentRenderer();
    }


    @Override
    public void initView() {
        super.initView();
        mAnimojiControlView = (AnimojiControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x306), getResources().getDimensionPixelSize(R.dimen.x122));
    }


    @Override
    public void bindListener() {
        super.bindListener();
        mAnimojiControlView.bindDataFactory(mAnimojiDataFactory);
        mAnimojiControlView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x166), showRate, getResources().getDimensionPixelSize(R.dimen.x138),
                    getResources().getDimensionPixelSize(R.dimen.x168), true);
        });
    }

    @Override
    public void onDestroy() {
        mAnimojiDataFactory.releaseAIProcessor();
        super.onDestroy();
    }


    @Override
    protected int getFunctionType() {
        return FunctionEnum.ANIMOJI;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mAnimojiControlView.hideControlView();
        return super.onTouchEvent(event);
    }
}
