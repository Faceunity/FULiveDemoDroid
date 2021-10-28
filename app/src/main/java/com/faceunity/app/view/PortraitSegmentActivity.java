package com.faceunity.app.view;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import androidx.annotation.Nullable;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.PortraitSegmentDataFactory;
import com.faceunity.app.data.source.PortraitSegmentSource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.core.media.video.VideoPlayHelper;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.bgSegCustom.BgSegCustom;
import com.faceunity.ui.control.PropCustomControlView;
import com.faceunity.ui.entity.PropCustomBean;

/**
 * DESC：道具贴纸
 * Created on 2021/3/2
 */
public class PortraitSegmentActivity extends BaseFaceUnityActivity {

    private PropCustomControlView mPropCustomControlView;
    private PortraitSegmentDataFactory mPortraitSegmentDataFactory;
    private VideoPlayHelper mVideoPlayHelper;

    @Override
    protected int getStubBottomLayoutResID() {
        return R.layout.layout_control_prop_custom;
    }

    @Override
    public void initData() {
        super.initData();
        mPortraitSegmentDataFactory = new PortraitSegmentDataFactory(mPortraitSegmentListener);
    }

    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mPortraitSegmentDataFactory.bindCurrentRenderer();
    }


    @Override
    public void initView() {
        super.initView();
        mPropCustomControlView = (PropCustomControlView) mStubView;
        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x212), getResources().getDimensionPixelSize(R.dimen.x166));
    }


    @Override
    public void bindListener() {
        super.bindListener();
        /*VideoDecoderHelper 需要在Surface初始化之后*/
        mVideoPlayHelper = new VideoPlayHelper(mVideoDecoderListener, mSurfaceView, false);
        mPropCustomControlView.bindDataFactory(mPortraitSegmentDataFactory);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        PropCustomBean bean = mPortraitSegmentDataFactory.getCurrentPropCustomBean();
        if (bean.getType() == FunctionEnum.BG_SEG_CUSTOM) {
            mVideoPlayHelper.pausePlay();
        }
    }

    @Override
    public void onDestroy() {
        mPortraitSegmentDataFactory.releaseAIProcessor();
        mVideoPlayHelper.release();
        super.onDestroy();
    }


    @Override
    protected int getFunctionType() {
        return FunctionEnum.PORTRAIT_SEGMENT;
    }

    @Override
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        return FUAIProcessorEnum.HUMAN_PROCESSOR;
    }


    PortraitSegmentDataFactory.PortraitSegmentListener mPortraitSegmentListener = new PortraitSegmentDataFactory.PortraitSegmentListener() {


        @Override
        public void onItemSelected(PropCustomBean bean) {
            if (bean.getDescId() > 0) {
                showDescription(bean.getDescId(), 1500);
            }
            if (bean.getType() == FunctionEnum.BG_SEG_CUSTOM) {
                mVideoPlayHelper.playVideo(bean.getIconPath());
            } else {
                mVideoPlayHelper.pausePlay();
            }
        }

        @Override
        public void onCustomPropAdd() {
            SelectDataActivity.startActivityForResult(PortraitSegmentActivity.this);
        }

        @Override
        public void onProcessTrackChanged(boolean needShow) {
            if (needShow) {
                isAIProcessTrack = true;
            } else {
                isAIProcessTrack = false;
                mTrackingView.setVisibility(View.INVISIBLE);
                aIProcessTrackStatus = 1;
            }
        }

    };


    private VideoPlayHelper.VideoDecoderListener mVideoDecoderListener = new VideoPlayHelper.VideoDecoderListener() {
        @Override
        public void onReadVideoPixel(byte[] bytes, int width, int height) {
            Prop prop = mPortraitSegmentDataFactory.getCurrentProp();
            if (prop instanceof BgSegCustom) {
                ((BgSegCustom) prop).createBgSegment(bytes, width, height);
            }
        }

        @Override
        public void onReadImagePixel(byte[] bytes, int width, int height) {
            Prop prop = mPortraitSegmentDataFactory.getCurrentProp();
            if (prop instanceof BgSegCustom) {
                ((BgSegCustom) prop).createBgSegment(bytes, width, height);
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        String path = FileUtils.getFilePathByUri(this, uri);
        PropCustomBean customBean = PortraitSegmentSource.buildPropCustomBean(path);
        if (customBean == null) {
            return;
        }
        PropCustomBean bean = mPortraitSegmentDataFactory.getPropCustomBeans().get(2);
        if (bean.getType() == FunctionEnum.BG_SEG_CUSTOM) {
            mPropCustomControlView.replaceProp(customBean, 2);
        } else {
            mPropCustomControlView.addProp(customBean, 2);
        }
        mPortraitSegmentDataFactory.setCurrentPropIndex(2);
        mPortraitSegmentDataFactory.onItemSelected(customBean);
    }


}
