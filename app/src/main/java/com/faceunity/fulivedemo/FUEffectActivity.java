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

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
        mBottomViewStub.inflate();

        RecyclerView recyclerView = findViewById(R.id.fu_effect_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
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
        int frontCameraOrientation = 270;
        if (mEffectType == Effect.EFFECT_TYPE_GESTURE) {
            // nexus 手机方向倒置问题
            frontCameraOrientation = CameraUtils.getFrontCameraOrientation();
        }
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .defaultEffect(effects.size() > 1 ? effects.get(1) : null)
                .inputImageOrientation(frontCameraOrientation)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
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
