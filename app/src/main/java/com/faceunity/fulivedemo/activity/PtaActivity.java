package com.faceunity.fulivedemo.activity;

import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.View;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.renderer.BaseCameraRenderer;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.utils.CameraUtils;

/**
 * 全身 Avatar
 */
public class PtaActivity extends FUEffectActivity {
    public static final String TAG = "PtaActivity";
    private boolean mLoadedController;

    @Override
    protected void onCreate() {
        super.onCreate();
        mInputTypeRadioGroup.setVisibility(View.GONE);
        mTakePicBtn.setVisibility(View.INVISIBLE);
        mCameraRenderer.setCameraFacing(BaseCameraRenderer.FACE_BACK);
        mEffectRecyclerAdapter.setOnEffectSelectedListener(new EffectRecyclerAdapter.OnEffectSelectedListener() {
            @Override
            public void onEffectSelected(Effect effect) {
                mFURenderer.selectPtaItem(effect.getBundlePath(), null);
            }
        });
    }

    @Override
    protected FURenderer initFURenderer() {
        mEffectType = getIntent().getIntExtra(EFFECT_TYPE, Effect.EFFECT_TYPE_NONE);
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                .inputImageOrientation(CameraUtils.getCameraOrientation(Camera.CameraInfo.CAMERA_FACING_BACK))
                .setLoadAiHumanProcessor(true)
                .maxHumans(1)
                .maxFaces(1)
                .setNeedFaceBeauty(false)
                .setOnTrackingStatusChangedListener(this)
                .setOnFUDebugListener(this)
                .build();
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        Effect effect = mEffectRecyclerAdapter.getSelectEffect();
        mFURenderer.selectPtaItem(effect.getBundlePath(), new Runnable() {
            @Override
            public void run() {
                mLoadedController = true;
                mCameraRenderer.setRenderRotatedImage(true);
            }
        });
    }

    @Override
    public int onDrawFrame(byte[] cameraNv21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mvpMatrix, float[] texMatrix, long timeStamp) {
        int texId;
        if (mLoadedController) {
            texId = mFURenderer.onDrawFramePta(cameraNv21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else {
            texId = mFURenderer.onDrawFrame(cameraNv21Byte, cameraTextureId, cameraWidth, cameraHeight);
        }
        return texId;
    }

    @Override
    public void onSurfaceDestroy() {
        super.onSurfaceDestroy();
        mLoadedController = false;
        mCameraRenderer.setRenderRotatedImage(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mCameraRenderer.onTouchEvent((int) event.getX(), (int) event.getY(), event.getAction());
        return true;
    }

}
