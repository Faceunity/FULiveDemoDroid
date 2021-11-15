package com.faceunity.app.view;

import android.view.MotionEvent;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.ui.control.FaceBeautyControlView;

/**
 * DESC：美颜
 * Created on 2021/3/1
 */
public class FaceBeautyActivity extends BaseFaceUnityActivity {


    private FaceBeautyControlView mFaceBeautyControlView;
    private FaceBeautyDataFactory mFaceBeautyDataFactory;


    public static boolean needBindDataFactory = false;

    @Override
    public void onResume() {
        if (needBindDataFactory) {
            mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
            needBindDataFactory = false;
        }
        super.onResume();
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
        mFaceBeautyControlView = (FaceBeautyControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x156), getResources().getDimensionPixelSize(R.dimen.x166));
    }

    @Override
    public void bindListener() {
        super.bindListener();
        mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
        mFaceBeautyControlView.setOnBottomAnimatorChangeListener(showRate -> {
            // 收起 1-->0，弹出 0-->1
            updateTakePicButton(getResources().getDimensionPixelSize(R.dimen.x166), showRate,
                    getResources().getDimensionPixelSize(R.dimen.x156), getResources().getDimensionPixelSize(R.dimen.x256), true);
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