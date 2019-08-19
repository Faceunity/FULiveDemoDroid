package com.faceunity.fulivedemo.activity;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.control.BeautyHairControlView;

/**
 * 美发界面
 * Created by tujh on 2018/1/31.
 */
public class FUHairActivity extends FUBaseActivity {
    public final static String TAG = FUHairActivity.class.getSimpleName();

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty_hair);
        mBottomViewStub.inflate();
        BeautyHairControlView beautyHairControlView = findViewById(R.id.fu_beauty_hair);
        beautyHairControlView.setOnFUControlListener(mFURenderer);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setNeedBeautyHair(true)
                .build();
    }
}
