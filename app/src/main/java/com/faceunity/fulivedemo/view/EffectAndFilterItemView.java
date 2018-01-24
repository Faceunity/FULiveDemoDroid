package com.faceunity.fulivedemo.view;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.fulivedemo.R;

import static com.faceunity.fulivedemo.view.EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT;

/**
 * Created by lirui on 2017/1/20.
 */

public class EffectAndFilterItemView extends LinearLayout {

    private ImageView mItemIcon;
    private TextView mItemText;

    private int mItemType = EffectAndFilterSelectAdapter.RECYCLEVIEW_TYPE_EFFECT;//effect or filter

    public EffectAndFilterItemView(Context context) {
        super(context);
        LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        View viewRoot = LayoutInflater.from(context).inflate(R.layout.effect_and_filter_item_view,
                this, true);
        mItemIcon = (ImageView) viewRoot.findViewById(R.id.item_icon);
        mItemText = (TextView) viewRoot.findViewById(R.id.item_text);
        init();
    }

    private void init() {
        if (mItemType == RECYCLEVIEW_TYPE_EFFECT) {
            mItemText.setVisibility(GONE);
        } else {
            mItemText.setVisibility(VISIBLE);
        }
    }

    public void setUnselectedBackground() {
        if (mItemType == RECYCLEVIEW_TYPE_EFFECT) {
            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_circle_unselected));
        } else {
            mItemIcon.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    public void setSelectedBackground() {
        if (mItemType == RECYCLEVIEW_TYPE_EFFECT) {
            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_circle_selected));
        } else {
            mItemIcon.setBackground(getResources().getDrawable(R.drawable.effect_item_square_selected));
        }
    }

    public void setItemIcon(int resourceId) {
        mItemIcon.setImageDrawable(getResources().getDrawable(resourceId));
    }

    public void setItemText(String text) {
        mItemText.setText(text);
    }

    public void setItemType(int itemType) {
        mItemType = itemType;
        init();
    }
}
