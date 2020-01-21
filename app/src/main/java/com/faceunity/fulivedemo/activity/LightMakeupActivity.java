package com.faceunity.fulivedemo.activity;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.control.LightMakeupControlView;

/**
 * 轻美妆页面
 *
 * @author Richie on 2019.11.25
 */
public class LightMakeupActivity extends FUBaseActivity {

    private LightMakeupControlView mLightMakeupControlView;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_light_makeup);
        mLightMakeupControlView = (LightMakeupControlView) mBottomViewStub.inflate();
        mLightMakeupControlView.setOnFUControlListener(mFURenderer);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputImageOrientation(mFrontCameraOrientation)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        mLightMakeupControlView.selectDefault();
    }

}