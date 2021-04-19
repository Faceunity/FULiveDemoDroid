package com.faceunity.app.view;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.LightMakeupDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.ui.control.LightMakeupControlView;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class LightMakeupActivity extends BaseFaceUnityActivity {


    private LightMakeupControlView mLightMakeupControlView;
    private LightMakeupDataFactory mLightMakeupDataFactory;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_light_makeup;
    }

    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mLightMakeupDataFactory.bindCurrentRenderer();
    }

    @Override
    public void initData() {
        super.initData();
        mLightMakeupDataFactory = new LightMakeupDataFactory(1);
    }

    @Override
    public void initView() {
        super.initView();
        mLightMakeupControlView = (LightMakeupControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x298), getResources().getDimensionPixelSize(R.dimen.x122));
    }


    @Override
    public void bindListener() {
        super.bindListener();
        mLightMakeupControlView.bindDataFactory(mLightMakeupDataFactory);
    }


    @Override
    protected int getFunctionType() {
        return FunctionEnum.LIGHT_MAKEUP;
    }

}