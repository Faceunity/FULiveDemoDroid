package com.faceunity.fulivedemo;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
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

        mHeightCheckBox.setVisibility(View.VISIBLE);
        mHeightImg.setVisibility(View.VISIBLE);
        mHeightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHeightImg.setImageResource(isChecked ? R.drawable.performance_checked : R.drawable.performance_normal);
                mBeautyControlView.setHeightPerformance(isChecked);
            }
        });

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty);
        mBottomViewStub.inflate();

        mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
        mBeautyControlView.setOnFUControlListener(mFURenderer);
        mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - showRate * 0.265)));
            }
        });
        mBeautyControlView.setOnDescriptionShowListener(new BeautyControlView.OnDescriptionShowListener() {
            @Override
            public void onDescriptionShowListener(int str) {
                showDescription(str, 1000);
            }
        });

        mHeightCheckBox.setChecked(BeautyParameterModel.isHeightPerformance);
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
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(null)
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
