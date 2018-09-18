package com.faceunity.fulivedemo;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.RadioGroup;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.utils.Constant;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 道具界面
 * Created by tujh on 2018/1/31.
 */

public class FUEffectActivity extends FUBaseUIActivity
        implements FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = FUEffectActivity.class.getSimpleName();

    private EffectRecyclerAdapter mEffectRecyclerAdapter;
    private RecyclerView mRecyclerView;

    private int mEffectType;
    private ArrayList<Effect> mEffects;

    private byte[] mFuNV21Byte;

    private FURenderer mFURenderer;

    @Override
    protected void onCreate() {
        mEffectType = getIntent().getIntExtra("EffectType", 0);
        mEffects = EffectEnum.getEffectsByEffectType(mEffectType);

        if (mEffectType == Effect.EFFECT_TYPE_ANIMOJI) {
            mTopBackground.setVisibility(View.VISIBLE);
        }

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
        mBottomViewStub.inflate();

        mInputTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.fu_base_input_type_double:
                        isDoubleInputType = true;
                        break;
                    case R.id.fu_base_input_type_single:
                        isDoubleInputType = false;
                        break;
                }
                mFURenderer.changeInputType();
            }
        });

        //初始化FU相关 authpack 为证书文件
        mFURenderer = new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(mEffects.size() > 1 ? mEffects.get(1) : null)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setNeedAnimoji3D(mEffectType == Effect.EFFECT_TYPE_ANIMOJI)
                .setNeedFaceBeauty(mEffectType != Effect.EFFECT_TYPE_ANIMOJI && mEffectType != Effect.EFFECT_TYPE_PORTRAIT_DRIVE)
                .build();

        mRecyclerView = (RecyclerView) findViewById(R.id.fu_effect_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, mEffectType, mFURenderer));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mEffectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
            @Override
            public void onDescriptionChangeListener(String description) {
                showDescription(description, 1500);
            }
        });

        if (mEffectType == Effect.EFFECT_TYPE_NORMAL) {
            mSelectDataBtn.setVisibility(View.VISIBLE);
            mSelectDataBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(FUEffectActivity.this, SelectDataActivity.class);
                    intent.putExtra("SelectData", TAG);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEffectRecyclerAdapter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mEffectRecyclerAdapter.onPause();
    }

    @Override
    protected void onSensorChanged(int rotation) {
        mFURenderer.setTrackOrientation(rotation);
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        mFURenderer.onCameraChange(currentCameraType, cameraOrientation);
    }


    @Override
    public void onFpsChange(final double fps, final double renderTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDebugText.setText(String.format("resolution:\n\t%dX%d\nfps:%d\nrender time:\n\t\t\t\t\t%dms", mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight(), (int) fps, (int) renderTime));
            }
        });
    }

    @Override
    public void onTrackingStatusChanged(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsTrackingText.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDescription(mEffectRecyclerAdapter.getSelectEffect().description(), 1500);
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mtx, long timeStamp) {
        int fuTextureId;
        if (isDoubleInputType) {
            fuTextureId = mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else {
            if (mFuNV21Byte == null) {
                mFuNV21Byte = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
            fuTextureId = mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
        }
        sendRecordingData(fuTextureId, mtx, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
        checkPic(fuTextureId, mtx, cameraHeight, cameraWidth);
        return fuTextureId;
    }

    @Override
    public void onSurfaceDestroy() {
        //通知FU销毁
        mFURenderer.onSurfaceDestroyed();
    }

}
