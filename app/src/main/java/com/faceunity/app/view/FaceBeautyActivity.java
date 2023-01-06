package com.faceunity.app.view;

import android.view.MotionEvent;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.disksource.facebeauty.FUDiskFaceBeautyUtils;
import com.faceunity.app.data.disksource.facebeauty.FUDiskFaceBeautyData;
import com.faceunity.app.data.source.FaceBeautySource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.ui.control.FaceBeautyControlView;
import com.faceunity.ui.entity.uistate.FaceBeautyControlState;

/**
 * DESC：美颜
 * Created on 2021/3/1
 */
public class FaceBeautyActivity extends BaseFaceUnityActivity {
    private FaceBeautyControlView mFaceBeautyControlView;
    private FaceBeautyDataFactory mFaceBeautyDataFactory;
    private FUDiskFaceBeautyData fuDiskFaceBeautyData;

    public static FaceBeautyControlState mFaceBeautyControlState = null;

    @Override
    public void onResume() {
        if (needUpdateUI) {
            mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
            mFaceBeautyControlView.updateUIStates(mFaceBeautyControlState);
            needUpdateUI = false;
            mFaceBeautyControlState = null;
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        mFaceBeautyControlState = mFaceBeautyControlView.getUIStates();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFaceBeautyControlState = null;
        //在美颜退出的时候序列化数据到磁盘，从真正需要的FaceBeauty数据中缓存到磁盘
        if (DemoConfig.OPEN_FACE_BEAUTY_TO_FILE) {
            FUDiskFaceBeautyUtils.saveFaceBeautyData2File(fuDiskFaceBeautyData,FaceBeautyDataFactory.defaultFaceBeauty,FaceBeautySource.buildFilters());
        }
    }

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_face_beauty;
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mFaceBeautyDataFactory.bindCurrentRenderer();
    }

    @Override
    public void initData() {
        super.initData();
        mFaceBeautyDataFactory = new FaceBeautyDataFactory(mFaceBeautyListener);
    }

    @Override
    public void initView() {
        super.initView();
        mFaceBeautyControlState = null;
        fuDiskFaceBeautyData = new FUDiskFaceBeautyData();
        mFaceBeautyControlView = (FaceBeautyControlView) mStubView;
        mFaceBeautyControlView.setResetButton(DemoConfig.IS_SHOW_RESET_BUTTON);
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x156));
    }

    @Override
    public void bindListener() {
        super.bindListener();
        mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
        mFaceBeautyControlView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x166), showRate,
                    getResources().getDimensionPixelSize(R.dimen.x156), getResources().getDimensionPixelSize(R.dimen.x256), false);
        });
    }


    @Override
    protected int getFunctionType() {
        return FunctionEnum.FACE_BEAUTY;
    }

    FaceBeautyDataFactory.FaceBeautyListener mFaceBeautyListener = new FaceBeautyDataFactory.FaceBeautyListener() {

        @Override
        public void onFilterSelected(int res) {
            showToast(res);
        }

        @Override
        public void onFaceBeautyEnable(boolean enable) {
            mCameraRenderer.setFURenderSwitch(enable);
        }
    };


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mFaceBeautyControlView.hideControlView();
        return super.onTouchEvent(event);
    }
}