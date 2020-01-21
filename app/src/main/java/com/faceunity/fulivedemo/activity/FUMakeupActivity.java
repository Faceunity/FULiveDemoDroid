package com.faceunity.fulivedemo.activity;

import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.control.MakeupControlView;

/**
 * 美妆界面
 * Created by tujh on 2018/1/31.
 */
public class FUMakeupActivity extends FUBaseActivity {
    public final static String TAG = FUMakeupActivity.class.getSimpleName();
    private MakeupControlView mMakeupControlView;
    private boolean mIsHideBottomView;

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .setLoadAiFaceLandmark75(false)
                .setLoadAiFaceLandmark239(true)
                .inputImageOrientation(mFrontCameraOrientation)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onCreate() {
        // 美妆采用单输入
        mInputTypeRadioGroup.setVisibility(View.GONE);
        mIsDualInput = false;

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_makeup);
        mBottomViewStub.inflate();
        mMakeupControlView = findViewById(R.id.makeup_control);
        mMakeupControlView.setOnFUControlListener(mFURenderer);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x304);
        int size = getResources().getDimensionPixelSize(R.dimen.x122);
        mTakePicBtn.setLayoutParams(params);
        mTakePicBtn.setDrawWidth(size);
        mTakePicBtn.bringToFront();
        mCameraFocus.bringToFront();
        mMakeupControlView.setOnBottomAnimatorChangeListener(new MakeupControlView.OnBottomAnimatorChangeListener() {
            private int px166 = getResources().getDimensionPixelSize(R.dimen.x166);
            private int px402 = getResources().getDimensionPixelSize(R.dimen.x402);
            private int px156 = getResources().getDimensionPixelSize(R.dimen.x156);
            private int px304 = getResources().getDimensionPixelSize(R.dimen.x304);
            private int firstDif = px402 - px304;
            private int secondDif = px402 - px156;

            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                // 收起 1--0，弹起 0--1
                mIsHideBottomView = showRate == 0;
                double v = px166 * (1 - showRate * 0.265);
                mTakePicBtn.setDrawWidth((int) v);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
                params.bottomMargin = (int) (px156 + secondDif * showRate);
                mTakePicBtn.setLayoutParams(params);
                if (showRate == 0) {
                    mMakeupControlView.setColorListVisible(false);
                    mMakeupControlView.setTitleSelection(false);
                } else {
                    mMakeupControlView.setColorListVisible(true);
                    mMakeupControlView.setTitleSelection(true);
                }
            }

            @Override
            public void onFirstMakeupAnimatorChangeListener(float hideRate) {
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
                params.bottomMargin = (int) (px304 + firstDif * hideRate);
                mTakePicBtn.setLayoutParams(params);
            }
        });
    }

    @Override
    protected void onLightFocusVisibilityChanged(boolean visible) {
        super.onLightFocusVisibilityChanged(visible);
        if (!mIsHideBottomView) {
            mMakeupControlView.setColorListVisible(!visible);
            if (visible) {
                mMakeupControlView.touchScreen();
            }
        }
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        mMakeupControlView.selectDefault();
    }

    @Override
    public void onCameraChanged(int cameraFacing, int cameraOrientation) {
        super.onCameraChanged(cameraFacing, cameraOrientation);
        mMakeupControlView.onCameraChange(cameraFacing);
    }

    @Override
    protected int getLandmarksType() {
        return FURenderer.FACE_LANDMARKS_239;
    }

}
