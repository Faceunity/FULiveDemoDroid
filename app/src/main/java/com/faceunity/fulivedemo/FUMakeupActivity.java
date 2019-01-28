package com.faceunity.fulivedemo;

import com.faceunity.FURenderer;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.ui.control.MakeupControlView;

import java.util.Map;
import java.util.Set;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 美妆界面
 * Created by tujh on 2018/1/31.
 */

public class FUMakeupActivity extends FUBaseActivity {
    public final static String TAG = FUMakeupActivity.class.getSimpleName();

    private MakeupControlView mMakeupControlView;


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
    protected void onCreate() {
        initDefaultValue();
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_makeup);
        mBottomViewStub.inflate();
        mMakeupControlView = findViewById(R.id.makeup_control);
        mMakeupControlView.setOnFUControlListener(mFURenderer);
        mMakeupControlView.setOnBottomAnimatorChangeListener(new MakeupControlView.OnBottomAnimatorChangeListener() {
            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - showRate * 0.265)));
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        mMakeupControlView.initData();
    }

    private void initDefaultValue() {
        Set<Map.Entry<String, Float>> batchEntries = BeautyParameterModel.sBatchMakeupLevel.entrySet();
        for (Map.Entry<String, Float> entry : batchEntries) {
            entry.setValue(1.0f);
        }
        Set<Map.Entry<String, Float>> mpEntries = BeautyParameterModel.sMakeupLevel.entrySet();
        for (Map.Entry<String, Float> mpEntry : mpEntries) {
            mpEntry.setValue(MakeupItem.DEFAULT_MAKEUP_LEVEL);
        }
    }
}
