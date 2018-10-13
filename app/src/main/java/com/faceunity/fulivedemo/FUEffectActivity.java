package com.faceunity.fulivedemo;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;

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
    private RecyclerView mRecyclerView;

    private int mEffectType;
    private ArrayList<Effect> mEffects;

    @Override
    protected void onCreate() {
        if (mEffectType == Effect.EFFECT_TYPE_ANIMOJI) {
            mTopBackground.setVisibility(View.VISIBLE);
        }

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
        mBottomViewStub.inflate();

        mRecyclerView = (RecyclerView) findViewById(R.id.fu_effect_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, mEffectType, mFURenderer));
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
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
        mEffects = EffectEnum.getEffectsByEffectType(mEffectType);
        return new FURenderer
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
