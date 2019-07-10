package com.faceunity.fulivedemo;

import android.support.constraint.ConstraintLayout;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.ui.control.MakeupControlView;
import com.faceunity.fulivedemo.utils.CameraUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 美妆界面
 * Created by tujh on 2018/1/31.
 */
public class FUMakeupActivity extends FUBaseActivity {
    public final static String TAG = FUMakeupActivity.class.getSimpleName();
    private MakeupControlView mMakeupControlView;
    private boolean mHide;

    @Override
    protected FURenderer initFURenderer() {
        int frontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputImageOrientation(frontCameraOrientation)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onCreate() {
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
                mHide = showRate == 0;
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
        if (!mHide) {
            mMakeupControlView.setColorListVisible(!visible);
            if (visible) {
                mMakeupControlView.touchScreen();
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mMakeupControlView.selectDefault();
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        super.onCameraChange(currentCameraType, cameraOrientation);
        mMakeupControlView.onCameraChange(currentCameraType);
    }

}
