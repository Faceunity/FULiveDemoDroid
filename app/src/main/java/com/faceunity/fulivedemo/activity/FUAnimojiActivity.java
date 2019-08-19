package com.faceunity.fulivedemo.activity;

import android.view.MotionEvent;
import android.view.View;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.control.AnimControlView;

/**
 * Animoji 和动漫滤镜效果
 * 关闭默认美颜效果
 *
 * @author Richie on 2018.11.13
 */
public class FUAnimojiActivity extends FUBaseActivity {

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_animoji);
        View view = mBottomViewStub.inflate();
        final AnimControlView animControlView = view.findViewById(R.id.fu_anim_control);
        animControlView.setOnFUControlListener(mFURenderer);
        animControlView.setOnBottomAnimatorChangeListener(new AnimControlView.OnBottomAnimatorChangeListener() {
            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - showRate * 0.265)));
            }
        });
        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                animControlView.hideBottomLayoutAnimator();
                return false;
            }
        });
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setNeedAnimoji3D(true)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }
}
