package com.faceunity.app.view;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.StyleDataFactory;
import com.faceunity.app.data.source.StyleSource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.ui.control.StyleControlView;
import com.faceunity.ui.entity.uistate.StyleControlState;

/**
 * DESC：风格
 * Created on 2022/11/08
 */
public class StyleActivity extends BaseFaceUnityActivity {
    private StyleControlView mStyleControlView;
    private StyleDataFactory mStyleDataFactory;
    public static StyleControlState mStyleControlState = null;
    private boolean isOncreate = true;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_style;
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mStyleDataFactory.bindCurrentRenderer();
        if (isOncreate) {
            runOnUiThread(()->mStyleControlView.updateUIStates(mStyleControlState));
            isOncreate = false;
        }
    }

    @Override
    public void initData() {
        super.initData();
        mStyleDataFactory = new StyleDataFactory(enable -> mCameraRenderer.setFURenderSwitch(enable));
    }

    @Override
    public void initView() {
        mStyleControlState = null;
        super.initView();
        mStyleControlView = (StyleControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x298));
    }

    @Override
    public void bindListener() {
        super.bindListener();
        mStyleControlView.bindDataFactory(mStyleDataFactory);
        mStyleControlView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x122), showRate, getResources().getDimensionPixelSize(R.dimen.x298), getResources().getDimensionPixelSize(R.dimen.x98), false);
        });
    }

    @Override
    protected void onRenderBefore(FURenderInputData inputData) {
        //风格模块，设置为单纹理输入。
        inputData.setImageBuffer(null);
        inputData.getRenderConfig().setNeedBufferReturn(false);
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.STYLE;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (needUpdateUI) {
            mStyleControlView.selectCurrentStyle();
            mStyleControlView.updateUIStates(mStyleControlState);
            mStyleControlState = null;
            needUpdateUI = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mStyleControlState = mStyleControlView.getUIStates();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        StyleSource.saveStyle2Disk();
        mStyleControlState = null;
    }
}