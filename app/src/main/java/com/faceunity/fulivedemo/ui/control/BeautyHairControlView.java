package com.faceunity.fulivedemo.ui.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.FURenderer;
import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 美发道具列表
 *
 * @author Richie on 2019.07.24
 */
public class BeautyHairControlView extends FrameLayout {
    private OnFUControlListener mOnFUControlListener;
    private List<Effect> mEffects;
    private HairAdapter mHairAdapter;
    private DiscreteSeekBar mDiscreteSeekBar;
    private int mHairGradientCount;

    public BeautyHairControlView(Context context) {
        this(context, null);
    }

    public BeautyHairControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyHairControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initData();
        initView();
    }

    public void setOnFUControlListener(OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_fu_hair, this);

        RecyclerView recyclerView = view.findViewById(R.id.fu_hair_recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        mHairAdapter = new HairAdapter();
        recyclerView.setAdapter(mHairAdapter);

        mDiscreteSeekBar = view.findViewById(R.id.fu_hair_recycler_seek_bar);
        mDiscreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                if (mHairAdapter.mPositionSelect <= mHairGradientCount) {
                    int hairIndex = mHairAdapter.mPositionSelect - 1;
                    mOnFUControlListener.onHairStrengthSelected(hairIndex, hairIndex < 0 ? 0 : (BeautyParameterModel.sHairLevel[mHairAdapter.mPositionSelect - 1] = 1.0f * value / 100));
                } else {
                    int hairIndex = mHairAdapter.mPositionSelect - mHairGradientCount - 1;
                    mOnFUControlListener.onHairStrengthSelected(hairIndex, BeautyParameterModel.sHairLevel[mHairAdapter.mPositionSelect - 1] = 1.0f * value / 100);
                }
            }
        });
        Arrays.fill(BeautyParameterModel.sHairLevel, BeautyParameterModel.HAIR_COLOR_INTENSITY);
        mDiscreteSeekBar.setProgress((int) (BeautyParameterModel.HAIR_COLOR_INTENSITY * 100));
    }

    private void initData() {
        ArrayList<Effect> hairEffects = EffectEnum.getEffectsByEffectType(Effect.EFFECT_TYPE_HAIR_NORMAL);
        ArrayList<Effect> hairGradientEffects = EffectEnum.getEffectsByEffectType(Effect.EFFECT_TYPE_HAIR_GRADIENT);
        mHairGradientCount = hairGradientEffects.size() - 1;
        mEffects = new ArrayList<>(mHairGradientCount + hairEffects.size());
        mEffects.addAll(hairGradientEffects);
        hairEffects.remove(0);
        mEffects.addAll(hairEffects);
    }

    class HairAdapter extends RecyclerView.Adapter<HairAdapter.HomeRecyclerHolder> {
        private int mPositionSelect = 1;

        @Override
        @NonNull
        public HairAdapter.HomeRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_effect_recycler, parent, false);
            return new HairAdapter.HomeRecyclerHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull HairAdapter.HomeRecyclerHolder holder, int position) {
            holder.effectImg.setImageResource(mEffects.get(position).getIconId());
            final int pos = position;
            holder.effectImg.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    if (mPositionSelect == pos) {
                        return;
                    }
                    int lastPos = mPositionSelect;
                    mPositionSelect = pos;
                    int hairIndex;
                    float hairLevel;
                    if (mPositionSelect <= 0) {
                        hairIndex = mPositionSelect;
                        hairLevel = 0;
                    } else if (mPositionSelect > mHairGradientCount) {
                        // 正常
                        hairIndex = mPositionSelect - mHairGradientCount - 1;
                        hairLevel = BeautyParameterModel.sHairLevel[mPositionSelect - 1];
                    } else {
                        // 渐变
                        hairIndex = mPositionSelect - 1;
                        hairLevel = BeautyParameterModel.sHairLevel[mPositionSelect - 1];
                    }
                    if (mPositionSelect == 0) {
                        if (lastPos <= mHairGradientCount) {
                            mOnFUControlListener.onHairSelected(FURenderer.HAIR_GRADIENT, hairIndex, 0.0f);
                        } else {
                            mOnFUControlListener.onHairSelected(FURenderer.HAIR_NORMAL, hairIndex, 0.0f);
                        }
                    } else {
                        if (mPositionSelect <= mHairGradientCount) {
                            mOnFUControlListener.onHairSelected(FURenderer.HAIR_GRADIENT, hairIndex, hairLevel);
                        } else {
                            mOnFUControlListener.onHairSelected(FURenderer.HAIR_NORMAL, hairIndex, hairLevel);
                        }
                    }
                    if (mPositionSelect == 0) {
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

            HomeRecyclerHolder(View itemView) {
                super(itemView);
                effectImg = itemView.findViewById(R.id.effect_recycler_img);
            }
        }
    }

}
