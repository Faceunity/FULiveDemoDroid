package com.faceunity.fulivedemo.activity;

import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.SwitchConfig;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.utils.AudioObserver;
import com.faceunity.fulivedemo.utils.ScreenUtils;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.ArrayList;

/**
 * 道具界面
 * Created by tujh on 2018/1/31.
 */
public class FUEffectActivity extends FUBaseActivity implements FURenderer.OnBundleLoadCompleteListener {
    public static final String TAG = "FUEffectActivity";
    public static final String SELECT_EFFECT_KEY = "select_effect_key";
    public static final String EFFECT_TYPE = "effect_type";

    protected EffectRecyclerAdapter mEffectRecyclerAdapter;
    protected int mEffectType;

    @Override
    protected void onCreate() {
        if (mEffectType == Effect.EFFECT_TYPE_ACTION_RECOGNITION) {
            findViewById(R.id.cl_custom_view).setVisibility(View.GONE);
            mGlSurfaceView.setOnTouchListener(new View.OnTouchListener() {
                private int mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
                private long mStartTimestamp;
                private int mX = getResources().getDimensionPixelSize(R.dimen.x138);
                private int mY = getResources().getDimensionPixelSize(R.dimen.x150);

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getX() > mX || event.getY() > mY) {
                        return false;
                    }
                    int action = event.getAction();
                    if (action == MotionEvent.ACTION_DOWN) {
                        mStartTimestamp = System.currentTimeMillis();
                    } else if (action == MotionEvent.ACTION_UP) {
                        if (System.currentTimeMillis() - mStartTimestamp < mLongPressTimeout) {
                            onBackPressed();
                        }
                        mStartTimestamp = 0;
                    }
                    return true;
                }
            });
        } else if (mEffectType == Effect.EFFECT_TYPE_PTA) {
            mBottomViewStub.setLayoutResource(R.layout.layout_fu_pta);
            View view = mBottomViewStub.inflate();
            RecyclerView recyclerView = view.findViewById(R.id.fu_effect_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setHasFixedSize(true);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(mEffectType);
            mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, mEffectType, mFURenderer, effects);
            mEffectRecyclerAdapter.setPositionSelect(0);
            recyclerView.setAdapter(mEffectRecyclerAdapter);
            mEffectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
                @Override
                public void onDescriptionChangeListener(int description) {
                    showDescription(description, 1500);
                }
            });
            SwitchButton switchButton = view.findViewById(R.id.btn_switch_pta);
            int pixel6 = getResources().getDimensionPixelSize(R.dimen.x4);
            switchButton.setThumbMargin(-pixel6 * 2, -pixel6, -pixel6 * 2, -pixel6 * 4);
            switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mFURenderer.setHumanTrackScene(isChecked ? FURenderer.HUMAN_TRACK_SCENE_FULL : FURenderer.HUMAN_TRACK_SCENE_HALF);
                }
            });
        } else {
            mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
            RecyclerView recyclerView = (RecyclerView) mBottomViewStub.inflate();
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setHasFixedSize(true);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(mEffectType);
            mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, mEffectType, mFURenderer, effects);
            recyclerView.setAdapter(mEffectRecyclerAdapter);
            mEffectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
                @Override
                public void onDescriptionChangeListener(int description) {
                    showDescription(description, 1500);
                }
            });
            if (mEffectType != Effect.EFFECT_TYPE_PTA) {
                mEffectRecyclerAdapter.setOnEffectSelectedListener(new EffectRecyclerAdapter.OnEffectSelectedListener() {
                    @Override
                    public void onEffectSelected(Effect effect) {
                        mFURenderer.onEffectSelected(effect);
                    }
                });
            }
        }
    }

    @Override
    protected FURenderer initFURenderer() {
        mEffectType = getIntent().getIntExtra(EFFECT_TYPE, Effect.EFFECT_TYPE_NONE);
        if (mEffectType == Effect.EFFECT_TYPE_MUSIC_FILTER) {
            AudioObserver audioObserver = new AudioObserver(this);
            getLifecycle().addObserver(audioObserver);
        }

        ArrayList<Effect> effects = EffectEnum.getEffectsByEffectType(mEffectType);
        boolean isActionRecognition = mEffectType == Effect.EFFECT_TYPE_ACTION_RECOGNITION;
        boolean isPortraitSegment = mEffectType == Effect.EFFECT_TYPE_PORTRAIT_SEGMENT;
        boolean isGestureRecognition = mEffectType == Effect.EFFECT_TYPE_GESTURE_RECOGNITION;
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .defaultEffect(effects.size() > 1 ? effects.get(1) : null)
                .inputImageOrientation(mFrontCameraOrientation)
                .setLoadAiHumanProcessor(isActionRecognition || isPortraitSegment)
                .maxHumans(1)
                .setNeedFaceBeauty(!(isActionRecognition))
                .setLoadAiHandProcessor(isGestureRecognition)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setOnBundleLoadCompleteListener(this)
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
                    showDescription(mEffectRecyclerAdapter.getSelectEffect().getDescId(), 1500);
                }
            }
        });
    }

    @Override
    protected boolean isOpenPhotoVideo() {
        return mEffectType == Effect.EFFECT_TYPE_STICKER || SwitchConfig.ENABLE_LOAD_EXTERNAL_FILE_TO_EFFECT;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intent = new Intent(FUEffectActivity.this, SelectDataActivity.class);
        intent.putExtra(SelectDataActivity.SELECT_DATA_KEY, FUEffectActivity.TAG);
        intent.putExtra(SELECT_EFFECT_KEY, mEffectType);
        startActivity(intent);
    }

    @Override
    protected boolean showAutoFocus() {
        return mEffectType != Effect.EFFECT_TYPE_ACTION_RECOGNITION && mEffectType != Effect.EFFECT_TYPE_PTA;
    }

    @Override
    public void onBundleLoadComplete(int what) {
        if (what == FURenderer.ITEM_ARRAYS_EFFECT_INDEX && mEffectType == Effect.EFFECT_TYPE_ACTION_RECOGNITION) {
            DisplayMetrics screenInfo = ScreenUtils.getScreenInfo(this);
            if ((float) screenInfo.heightPixels / screenInfo.widthPixels > (float) 16 / 9) {
                mFURenderer.setEdgeDistance(0.1F);
            }
        }
    }

    @Override
    public void onTrackStatusChanged(int type, int status) {
        if ((mEffectType == Effect.EFFECT_TYPE_GESTURE_RECOGNITION && type != FURenderer.TRACK_TYPE_GESTURE)
                || (mEffectType == Effect.EFFECT_TYPE_PORTRAIT_SEGMENT && type != FURenderer.TRACK_TYPE_HUMAN)) {
            // do nothing
            return;
        } else {
            super.onTrackStatusChanged(type, status);
        }
    }
}
