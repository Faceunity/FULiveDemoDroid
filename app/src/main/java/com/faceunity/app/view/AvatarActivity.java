package com.faceunity.app.view;

import android.view.MotionEvent;
import android.view.View;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.AvatarDataFactory;
import com.faceunity.app.data.source.AvatarSource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FUCameraConfig;
import com.faceunity.core.enumeration.CameraFacingEnum;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.ui.control.AvatarControlView;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class AvatarActivity extends BaseFaceUnityActivity {

    private AvatarDataFactory mAvatarDataFactory;
    private AvatarControlView mAvatarControlView;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_avatar;
    }

    @Override
    public void initData() {
        super.initData();
        mAvatarDataFactory = new AvatarDataFactory(0, true);
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mAvatarDataFactory.bindCurrentRenderer();
    }


    @Override
    public void initView() {
        super.initView();
        mAvatarControlView = (AvatarControlView) mStubView;
        mTakePicView.setVisibility(View.GONE);
    }


    @Override
    public void bindListener() {
        super.bindListener();
        mAvatarControlView.bindDataFactory(mAvatarDataFactory);
        mCameraRenderer.drawSmallViewport(true);
    }

    @Override
    protected FUCameraConfig getCameraConfig() {
        FUCameraConfig cameraConfig = super.getCameraConfig();
        cameraConfig.setCameraFacing(CameraFacingEnum.CAMERA_BACK);
        return cameraConfig;
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.AVATAR;
    }

    @Override
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        return FUAIProcessorEnum.HUMAN_PROCESSOR;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCameraRenderer.onTouchEvent((int) event.getX(), (int) event.getY(), event.getAction());
        return true;
    }
}
