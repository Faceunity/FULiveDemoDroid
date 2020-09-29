package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;

/**
 * 选择颜色的锚点，取色器
 *
 * @author Richie on 2020.08.19
 */
public class ColorPickerView extends FrameLayout {
    private ImageView mImageView;
    private GradientDrawable mPickedColorDrawable;
    private LayerDrawable mLayerTransparentDrawable;

    public ColorPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void setPickedColor(int color) {
        if (color == Color.TRANSPARENT) {
            mImageView.setImageDrawable(mLayerTransparentDrawable);
        } else {
            mPickedColorDrawable.setColor(color);
            mImageView.setImageDrawable(mPickedColorDrawable);
        }
    }

    public void postSetPickedColor(final int color) {
        post(new Runnable() {
            @Override
            public void run() {
                setPickedColor(color);
            }
        });
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bsg_anchor, this);
        mImageView = view.findViewById(R.id.iv_bsg_picked_color);
        mPickedColorDrawable = (GradientDrawable) mImageView.getDrawable();
        mLayerTransparentDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.layer_bsg_pick_color_transparent);
    }

}