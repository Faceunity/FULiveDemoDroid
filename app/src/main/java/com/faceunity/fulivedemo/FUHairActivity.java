package com.faceunity.fulivedemo;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.faceunity.FURenderer;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.CircleImageView;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sHairLevel;

/**
 * 美发界面
 * Created by tujh on 2018/1/31.
 */

public class FUHairActivity extends FUBaseActivity {
    public final static String TAG = FUHairActivity.class.getSimpleName();

    private HairAdapter mHairAdapter;

    private DiscreteSeekBar mDiscreteSeekBar;
    private ArrayList<Effect> mEffects;
    private int mHairEffectCount = 3;

    @Override
    protected void onCreate() {
        mBottomViewStub.setLayoutResource(R.layout.layout_fu_hair);
        mBottomViewStub.inflate();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.fu_hair_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mHairAdapter = new HairAdapter());
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        mDiscreteSeekBar = (DiscreteSeekBar) findViewById(R.id.fu_hair_recycler_seek_bar);
        mDiscreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                if (mHairAdapter.mPositionSelect <= mHairEffectCount) {
                    int hairIndex = mHairAdapter.mPositionSelect - 1 + mHairEffectCount;
                    mFURenderer.onHairLevelSelected(FURenderer.HAIR_GRADIENT, hairIndex,
                            hairIndex < 0 ? 0 : (sHairLevel[hairIndex] = 1.0f * value / 100));
                } else {
                    int hairIndex = mHairAdapter.mPositionSelect - mHairEffectCount - 1;
                    mFURenderer.onHairLevelSelected(FURenderer.HAIR_NORMAL, hairIndex,
                            sHairLevel[mHairAdapter.mPositionSelect - 1] = 1.0f * value / 100);
                }
            }
        });

        Arrays.fill(sHairLevel, 0.6f);
    }

    @Override
    protected FURenderer initFURenderer() {
        ArrayList<Effect> hairEffects = EffectEnum.getEffectsByEffectType(Effect.EFFECT_TYPE_HAIR);
        ArrayList<Effect> hairGradientEffects = EffectEnum.getEffectsByEffectType(Effect.EFFECT_TYPE_HAIR_GRADIENT);
        mHairEffectCount = hairGradientEffects.size() - 1;
        mEffects = new ArrayList<>();
        mEffects.addAll(hairGradientEffects);
        hairEffects.remove(0);
        mEffects.addAll(hairEffects);
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .setNeedBeautyHair(true)
                .build();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        super.onSurfaceCreated(gl, config);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Effect selectEffect = mHairAdapter.getSelectEffect();
                showDescription(selectEffect.description(), 1500);
                mDiscreteSeekBar.setProgress((int) (sHairLevel[mHairAdapter.mPositionSelect - 1] * 100));
            }
        });
    }

    class HairAdapter extends RecyclerView.Adapter<HairAdapter.HomeRecyclerHolder> {
        int mPositionSelect = 1;

        @Override
        public HairAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HairAdapter.HomeRecyclerHolder(LayoutInflater.from(FUHairActivity.this).inflate(R.layout.layout_effect_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(HairAdapter.HomeRecyclerHolder holder, final int position) {

            holder.effectImg.setImageResource(mEffects.get(position).resId());
            holder.effectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPositionSelect == position) {
                        return;
                    }
                    Effect click = mEffects.get(mPositionSelect = position);
                    int hairIndex;
                    float hairLevel;
                    if (mPositionSelect <= 0) {
                        hairIndex = mPositionSelect;
                        hairLevel = 0;
                    } else if (mPositionSelect > mHairEffectCount) {
                        // 正常
                        hairIndex = mPositionSelect - mHairEffectCount - 1;
                        hairLevel = sHairLevel[mPositionSelect - 1];
                    } else {
                        // 渐变
                        hairIndex = mPositionSelect - 1 + mHairEffectCount;
                        hairLevel = sHairLevel[hairIndex];
                    }
                    Log.d(TAG, "onClick: hairIndex:" + hairIndex + ", hairLevel:" + hairLevel +
                            ", pos:" + mPositionSelect + ". lastPos:" + mPositionSelect);
                    if (mPositionSelect <= mHairEffectCount) {
                        mFURenderer.onHairSelected(FURenderer.HAIR_GRADIENT, hairIndex, hairLevel);
                    } else {
                        mFURenderer.onHairSelected(FURenderer.HAIR_NORMAL, hairIndex, hairLevel);
                    }
                    showDescription(click.description(), 1500);
                    if (position == 0) {
                        mDiscreteSeekBar.setVisibility(View.INVISIBLE);
                    } else {
                        mDiscreteSeekBar.setVisibility(View.VISIBLE);
                        mDiscreteSeekBar.setProgress((int) (hairLevel * 100));
                    }
                    notifyDataSetChanged();
                }
            });
            if (mPositionSelect == position) {
                holder.effectImg.setBackgroundResource(R.drawable.effect_select);
            } else {
                holder.effectImg.setBackgroundResource(0);
            }
        }

        @Override
        public int getItemCount() {
            return mEffects.size();
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            CircleImageView effectImg;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                effectImg = (CircleImageView) itemView.findViewById(R.id.effect_recycler_img);
            }
        }

        public Effect getSelectEffect() {
            return mEffects.get(mPositionSelect);
        }
    }
}
