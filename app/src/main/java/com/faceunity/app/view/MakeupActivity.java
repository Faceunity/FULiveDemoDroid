package com.faceunity.app.view;


import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.MakeupDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.ui.control.MakeupControlView;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class MakeupActivity extends BaseFaceUnityActivity {


    private MakeupControlView mMakeupControlView;
    private MakeupDataFactory mMakeupDataFactory;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_makeup;
    }

    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mMakeupDataFactory.bindCurrentRenderer();
    }

    @Override
    public void initData() {
        super.initData();
        mMakeupDataFactory = new MakeupDataFactory(1);
    }

    @Override
    public void initView() {
        super.initView();
        mMakeupControlView = (MakeupControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x304), getResources().getDimensionPixelSize(R.dimen.x122));

    }

    @Override
    public void bindListener() {
        super.bindListener();
        mMakeupControlView.bindDataFactory(mMakeupDataFactory);
        mMakeupControlView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x122), showRate, getResources().getDimensionPixelSize(R.dimen.x304), getResources().getDimensionPixelSize(R.dimen.x98), false);
        });
    }

    @Override
    protected void onRenderBefore(FURenderInputData inputData) {
        //美妆模块，设置为单纹理输入。
        inputData.setImageBuffer(null);
        inputData.getRenderConfig().setNeedBufferReturn(false);
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.MAKE_UP;
    }
}