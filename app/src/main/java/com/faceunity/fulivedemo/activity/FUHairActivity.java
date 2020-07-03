package com.faceunity.fulivedemo.activity;

import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.SwitchConfig;
import com.faceunity.fulivedemo.ui.control.BeautyHairControlView;

/**
 * 美发界面
 * Created by tujh on 2018/1/31.
 */
public class FUHairActivity extends FUBaseActivity {
    public static final String TAG = "FUHairActivity";

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty_hair);
        mBottomViewStub.inflate();
        BeautyHairControlView beautyHairControlView = findViewById(R.id.fu_beauty_hair);
        beautyHairControlView.setOnFUControlListener(mFURenderer);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x298);
        mTakePicBtn.setLayoutParams(params);
        int size = getResources().getDimensionPixelSize(R.dimen.x122);
        mTakePicBtn.setDrawWidth(size);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .inputImageOrientation(mFrontCameraOrientation)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setNeedBeautyHair(true)
                .build();
    }

    @Override
    protected boolean isOpenPhotoVideo() {
        return SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_HAIR;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intent = new Intent(FUHairActivity.this, SelectDataActivity.class);
        intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, FUHairActivity.TAG);
        startActivity(intent);
    }
}
