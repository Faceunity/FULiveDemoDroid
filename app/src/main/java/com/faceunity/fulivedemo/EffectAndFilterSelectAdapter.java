package com.faceunity.fulivedemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lirui on 2017/1/20.
 */

public class EffectAndFilterSelectAdapter extends RecyclerView.Adapter<EffectAndFilterSelectAdapter.ItemViewHolder> {

    public static final int[] EFFECT_ITEM_RES_ARRAY = {
            R.mipmap.ic_delete_all, R.mipmap.yuguan, R.mipmap.lixiaolong, R.mipmap.matianyu, R.mipmap.yazui,
            R.mipmap.gradient, R.mipmap.mood, R.mipmap.item0204,
    };
    public static final String[] EFFECT_ITEM_FILE_NAME = {"none", "yuguan.mp3", "lixiaolong.bundle",
            "mask_matianyu.bundle", "yazui.mp3", "gradient.bundle", "Mood.mp3", "item0204.mp3"};

    public static final int[] FILTER_ITEM_RES_ARRAY = {
            R.mipmap.nature, R.mipmap.delta, R.mipmap.electric, R.mipmap.slowlived, R.mipmap.tokyo, R.mipmap.warm
    };
    public final static String[] FILTERS_NAME = {"nature", "delta", "electric", "slowlived", "tokyo", "warm"};

    public static final int RECYCLEVIEW_TYPE_EFFECT = 0;
    public static final int RECYCLEVIEW_TYPE_FILTER = 1;

    private RecyclerView mOwnerRecyclerView;
    private int mOwnerRecyclerViewType;

    private final int EFFECT_DEFAULT_CLICK_POSITION = 1;
    private final int FILTER_DEFAULT_CLICK_POSITION = 0;
    private EffectAndFilterItemView lastClickItemView = null;
    private int lastClickPosition = EFFECT_DEFAULT_CLICK_POSITION;
    private OnItemSelectedListener mOnItemSelectedListener;

    public EffectAndFilterSelectAdapter(RecyclerView recyclerView, int recyclerViewType) {
        mOwnerRecyclerView = recyclerView;
        mOwnerRecyclerViewType = recyclerViewType;
    }

    @Override
    public int getItemCount() {
        return mOwnerRecyclerViewType == RECYCLEVIEW_TYPE_EFFECT ?
                EFFECT_ITEM_RES_ARRAY.length :
                FILTER_ITEM_RES_ARRAY.length;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        final int adapterPosition = holder.getAdapterPosition();
        if (adapterPosition == lastClickPosition) {
            holder.mItemView.setSelectedBackground();
            lastClickItemView = holder.mItemView;
        } else holder.mItemView.setUnselectedBackground();

        if (mOwnerRecyclerViewType == RECYCLEVIEW_TYPE_EFFECT) {
            holder.mItemView.setItemIcon(EFFECT_ITEM_RES_ARRAY[adapterPosition % EFFECT_ITEM_RES_ARRAY.length]);
        } else {
            holder.mItemView.setItemIcon(FILTER_ITEM_RES_ARRAY[adapterPosition % FILTER_ITEM_RES_ARRAY.length]);
            holder.mItemView.setItemText(FILTERS_NAME[adapterPosition % FILTER_ITEM_RES_ARRAY.length].toUpperCase());
        }

        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastClickItemView != null) lastClickItemView.setUnselectedBackground();
                lastClickItemView = holder.mItemView;
                lastClickPosition = adapterPosition;
                holder.mItemView.setSelectedBackground();
                setClickPosition(adapterPosition);
            }
        });
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(new EffectAndFilterItemView(parent.getContext(), mOwnerRecyclerViewType));
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        EffectAndFilterItemView mItemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mItemView = (EffectAndFilterItemView) itemView;
        }
    }

    private void setClickPosition(int position) {
        if (position < 0) {
            return;
        }
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onItemSelected(position);
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int itemPosition);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
    }
}
