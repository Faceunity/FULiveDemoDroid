package com.faceunity.fulivedemo;

import android.content.Intent;
import android.content.res.Resources;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.utils.CameraUtils;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 道具界面
 * Created by tujh on 2018/1/31.
 */

public class FUEffectActivity extends FUBaseActivity {
    public final static String TAG = FUEffectActivity.class.getSimpleName();

    private EffectRecyclerAdapter mEffectRecyclerAdapter;

    private int mEffectType;
    private boolean mIsAnimFilterOpen;
    private ImageView mImageView;

    @Override
    protected void onCreate() {
        // Animoji 增加滤镜开关
        if (mEffectType == Effect.EFFECT_TYPE_ANIMOJI) {
            mTopBackground.setVisibility(View.VISIBLE);
            mImageView = new ImageView(this);
            Resources resources = getResources();
            int ivWidth = resources.getDimensionPixelSize(R.dimen.x172);
            int ivHeight = resources.getDimensionPixelSize(R.dimen.x60);
            ConstraintLayout.LayoutParams ivParams = new ConstraintLayout.LayoutParams(ivWidth, ivHeight);
            mImageView.setImageResource(R.drawable.btn_automaticl_nor);
            ivParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            ivParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            ivParams.leftMargin = resources.getDimensionPixelSize(R.dimen.x34);
            ivParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.x202);
            mClOperationView.addView(mImageView, ivParams);
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIsAnimFilterOpen = !mIsAnimFilterOpen;
                    mImageView.setImageResource(mIsAnimFilterOpen ? R.drawable.btn_automaticl_sel : R.drawable.btn_automaticl_nor);
                    mFURenderer.onLoadAnimFilter(mIsAnimFilterOpen);
                }
            });
        }

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
        mBottomViewStub.inflate();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fu_effect_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, mEffectType, mFURenderer));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mEffectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
            @Override
            public void onDescriptionChangeListener(int description) {
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
    protected FURenderer initFURenderer() {
        mEffectType = getIntent().getIntExtra("EffectType", 0);
        ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(mEffectType);
        int frontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        Log.d(TAG, "front camera orientation:" + frontCameraOrientation);
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(effects.size() > 1 ? effects.get(1) : null)
                .setOnFUDebugListener(this)
                .inputImageOrientation(frontCameraOrientation)
                .setOnTrackingStatusChangedListener(this)
                .setNeedAnimoji3D(mEffectType == Effect.EFFECT_TYPE_ANIMOJI)
                .setNeedFaceBeauty(mEffectType != Effect.EFFECT_TYPE_ANIMOJI && mEffectType != Effect.EFFECT_TYPE_PORTRAIT_DRIVE)
                .build();
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
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDescription(mEffectRecyclerAdapter.getSelectEffect().description(), 1500);
            }
        });
    }

}
