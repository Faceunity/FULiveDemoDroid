package com.faceunity.app.view;

import android.view.MotionEvent;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.disksource.FUUtils;
import com.faceunity.app.data.disksource.FaceBeautyData;
import com.faceunity.app.data.source.FaceBeautySource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.ui.control.FaceBeautyControlView;

/**
 * DESC：美颜
 * Created on 2021/3/1
 */
public class FaceBeautyActivity extends BaseFaceUnityActivity {


    private FaceBeautyControlView mFaceBeautyControlView;
    private FaceBeautyDataFactory mFaceBeautyDataFactory;
    private FaceBeautyData faceBeautyData;


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
    public void onDestroy() {
        super.onDestroy();
        //在美颜退出的时候序列化数据到磁盘，从真正需要的FaceBeauty数据中缓存到磁盘
        if (DemoConfig.OPEN_FACE_BEAUTY_TO_FILE) {
            FUUtils.saveFaceBeautyData2File(faceBeautyData,FaceBeautyDataFactory.defaultFaceBeauty,FaceBeautySource.buildFilters(),mFaceBeautyDataFactory.getCurrentStyleIndex());
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
        faceBeautyData = new FaceBeautyData();
        mFaceBeautyControlView = (FaceBeautyControlView) mStubView;
        mFaceBeautyControlView.setResetButton(DemoConfig.IS_SHOW_RESET_BUTTON);
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