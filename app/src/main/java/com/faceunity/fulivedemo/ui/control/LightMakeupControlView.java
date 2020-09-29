package com.faceunity.fulivedemo.ui.control;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Filter;
import com.faceunity.entity.LightMakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.entity.LightMakeupCombination;
import com.faceunity.fulivedemo.entity.LightMakeupEnum;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.SpaceItemDecoration;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.List;

/**
 * 轻美妆交互栏
 *
 * @author Richie on 2019.11.25
 */
public class LightMakeupControlView extends FrameLayout {
    private LightMakeupAdapter mLightMakeupAdapter;
    private OnFUControlListener mOnFUControlListener;
    private DiscreteSeekBar mDiscreteSeekBar;
    private boolean mFirstSetup;

    public LightMakeupControlView(@NonNull Context context) {
        super(context);
        initView();
    }

    public LightMakeupControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public LightMakeupControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setOnFUControlListener(OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_light_makeup, this);
        RecyclerView rvLightMakeup = findViewById(R.id.rv_light_makeup);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvLightMakeup.setLayoutManager(layoutManager);
        SpaceItemDecoration itemDecoration = new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x15), 0);
        rvLightMakeup.addItemDecoration(itemDecoration);
        ((SimpleItemAnimator) rvLightMakeup.getItemAnimator()).setSupportsChangeAnimations(false);
        rvLightMakeup.setHasFixedSize(true);

        mLightMakeupAdapter = new LightMakeupAdapter(LightMakeupEnum.getLightMakeupCombinations());
        rvLightMakeup.setAdapter(mLightMakeupAdapter);
        mLightMakeupAdapter.setOnItemClickListener(new OnLightMakeupClickListener());
        mLightMakeupAdapter.setItemSelected(1); // 默认选中第一个桃花妆

        mDiscreteSeekBar = findViewById(R.id.seek_bar_light_makeup);
        mDiscreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                // 整体妆容调节
                float level = 1.0f * value / 100;
                LightMakeupCombination lightMakeupCombination = mLightMakeupAdapter.getSelectedItems().valueAt(0);
                int nameId = lightMakeupCombination.getNameId();
                BeautyParameterModel.sLightMakeupCombinationLevels.put(nameId, level);
                List<LightMakeupItem> makeupItems = lightMakeupCombination.getMakeupItems();
                /* 按比例调节强度
                 * 0.4        0.7
                 * strength  level
                 * --> strength = 0.4 * level / 0.7
                 *   if level = 1.0, then strength = 0.57
                 *   if level = 0.2, then strength = 0.11
                 *   so, float strength = item.defaultLevel * level / DEFAULT_BATCH_MAKEUP_LEVEL
                 * */
                if (makeupItems != null) {
                    for (LightMakeupItem makeupItem : makeupItems) {
                        float fixLevel = makeupItem.getDefaultLevel() * level / LightMakeupEnum.LIGHT_MAKEUP_OVERALL_INTENSITIES.get(nameId);
                        makeupItem.setLevel(fixLevel);
                        mOnFUControlListener.onLightMakeupItemLevelChanged(makeupItem);
                    }
                }
                mOnFUControlListener.onFilterLevelSelected(level);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    // 默认选中第一个桃花妆
    public void selectDefault() {
        if (mFirstSetup) {
            return;
        }
        mFirstSetup = true;
        mDiscreteSeekBar.post(new Runnable() {
            @Override
            public void run() {
                mLightMakeupAdapter.getOnItemClickListener().onItemClick(mLightMakeupAdapter, null, 1);
            }
        });
    }

    private class LightMakeupAdapter extends BaseRecyclerAdapter<LightMakeupCombination> {

        LightMakeupAdapter(@NonNull List<LightMakeupCombination> data) {
            super(data, R.layout.layout_rv_makeup);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, LightMakeupCombination item) {
            viewHolder.setText(R.id.tv_makeup, getResources().getString(item.getNameId()))
                    .setImageResource(R.id.iv_makeup, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, LightMakeupCombination data, boolean selected) {
            ((TextView) viewHolder.getViewById(R.id.tv_makeup)).setTextColor(selected ?
                    getResources().getColor(R.color.main_color) : getResources().getColor(R.color.colorWhite));
            viewHolder.setBackground(R.id.iv_makeup, selected ? R.drawable.shape_filter_selected : 0);
        }
    }

    private class OnLightMakeupClickListener implements BaseRecyclerAdapter.OnItemClickListener<LightMakeupCombination> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<LightMakeupCombination> adapter, View view, int position) {
            LightMakeupCombination lightMakeupCombination = adapter.getItem(position);
            List<LightMakeupItem> makeupItems = lightMakeupCombination.getMakeupItems();
            mOnFUControlListener.onLightMakeupCombinationSelected(makeupItems);
            if (position == 0) {
                mDiscreteSeekBar.setVisibility(View.INVISIBLE);
            } else {
                int nameId = lightMakeupCombination.getNameId();
                Object levelObj = BeautyParameterModel.sLightMakeupCombinationLevels.get(nameId);
                float level;
                if (levelObj == null) {
                    level = LightMakeupEnum.LIGHT_MAKEUP_OVERALL_INTENSITIES.get(nameId);
                    BeautyParameterModel.sLightMakeupCombinationLevels.put(nameId, level);
                } else {
                    level = (float) levelObj;
                }

                if (makeupItems != null) {
                    for (LightMakeupItem makeupItem : makeupItems) {
                        float fixLevel = makeupItem.getDefaultLevel() * level / LightMakeupEnum.LIGHT_MAKEUP_OVERALL_INTENSITIES.get(nameId);
                        makeupItem.setLevel(fixLevel);
                        mOnFUControlListener.onLightMakeupItemLevelChanged(makeupItem);
                    }
                }

                Pair<Filter, Float> filterFloatPair = LightMakeupEnum.LIGHT_MAKEUP_FILTERS.get(nameId);
                mOnFUControlListener.onFilterNameSelected(filterFloatPair.first.getName());

                mDiscreteSeekBar.setVisibility(View.VISIBLE);
                mDiscreteSeekBar.setProgress((int) (level * 100));
            }
        }
    }

}