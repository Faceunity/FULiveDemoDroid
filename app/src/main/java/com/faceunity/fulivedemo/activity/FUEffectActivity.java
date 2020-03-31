package com.faceunity.fulivedemo.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.SwitchConfig;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.utils.AudioObserver;

import java.util.ArrayList;

/**
 * 道具界面
 * Created by tujh on 2018/1/31.
 */
public class FUEffectActivity extends FUBaseActivity {
    public static final String TAG = "FUEffectActivity";
    public static final String SELECT_EFFECT_KEY = "select_effect_key";
    public static final String EFFECT_TYPE = "effect_type";

    private EffectRecyclerAdapter mEffectRecyclerAdapter;
    protected int mEffectType;

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
    }

    @Override
    protected FURenderer initFURenderer() {
        mEffectType = getIntent().getIntExtra(EFFECT_TYPE, Effect.EFFECT_TYPE_NONE);
        if (mEffectType == Effect.EFFECT_TYPE_MUSIC_FILTER) {
            AudioObserver audioObserver = new AudioObserver(this);
            getLifecycle().addObserver(audioObserver);
        }

        ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(mEffectType);
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .defaultEffect(effects.size() > 1 ? effects.get(1) : null)
                .inputImageOrientation(mFrontCameraOrientation)
                .setLoadAiBgSeg(mEffectType == Effect.EFFECT_TYPE_BACKGROUND)
                .setLoadAiGesture(mEffectType == Effect.EFFECT_TYPE_GESTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEffectRecyclerAdapter != null) {
            mEffectRecyclerAdapter.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mEffectRecyclerAdapter != null) {
            mEffectRecyclerAdapter.onPause();
        }
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mEffectRecyclerAdapter != null) {
                    showDescription(mEffectRecyclerAdapter.getSelectEffect().description(), 1500);
                }
            }
        });
    }

    @Override
    protected boolean isOpenPhotoVideo() {
        return mEffectType == Effect.EFFECT_TYPE_NORMAL || SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_EFFECT;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intent = new Intent(FUEffectActivity.this, SelectDataActivity.class);
        intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, FUEffectActivity.TAG);
        intent.putExtra(SELECT_EFFECT_KEY, mEffectType);
        startActivity(intent);
    }
}
