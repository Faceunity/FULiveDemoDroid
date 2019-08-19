package com.faceunity.fulivedemo.activity;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.control.BeautyControlView;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

/**
 * 美颜界面
 * Created by tujh on 2018/1/31.
 */

public class FUBeautyActivity extends FUBaseActivity {
    public final static String TAG = FUBeautyActivity.class.getSimpleName();
    private BeautyControlView mBeautyControlView;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty);
        mBeautyControlView = (BeautyControlView) mBottomViewStub.inflate();
        mBeautyControlView.setOnFUControlListener(mFURenderer);
        mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
            private int px166 = getResources().getDimensionPixelSize(R.dimen.x160);
            private int px156 = getResources().getDimensionPixelSize(R.dimen.x156);
            private int px402 = getResources().getDimensionPixelSize(R.dimen.x402);
            private int diff = px402 - px156;

            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                // 收起 1-->0，弹出 0-->1
                double v = px166 * (1 - showRate * 0.265);
                mTakePicBtn.setDrawWidth((int) v);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
                params.bottomMargin = (int) (px156 + diff * showRate);
                mTakePicBtn.setLayoutParams(params);
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166)
                        * (1 - showRate * 0.265)));
            }
        });
        mBeautyControlView.setOnDescriptionShowListener(new BeautyControlView.OnDescriptionShowListener() {
            @Override
            public void onDescriptionShowListener(int str) {
                showDescription(str, 1000);
            }
        });

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mTakePicBtn.getLayoutParams();
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x156);
        int size = getResources().getDimensionPixelSize(R.dimen.x160);
        mTakePicBtn.setLayoutParams(params);
        mTakePicBtn.setDrawWidth(size);
        mTakePicBtn.bringToFront();

        mSelectDataBtn.setVisibility(View.VISIBLE);
        mSelectDataBtn.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                Intent intent = new Intent(FUBeautyActivity.this, SelectDataActivity.class);
                intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, TAG);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBeautyControlView.isShown()) {
            mBeautyControlView.hideBottomLayoutAnimator();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeautyControlView != null) {
            mBeautyControlView.onResume();
        }
    }

}
