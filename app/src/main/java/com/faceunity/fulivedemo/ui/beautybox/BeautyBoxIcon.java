package com.faceunity.fulivedemo.ui.beautybox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;

/**
 * 仅有 icon 的单选多状态 View
 * <p>
 * Created by tujh on 2018/4/17.
 */
public class BeautyBoxIcon extends BaseBeautyBox {

    public BeautyBoxIcon(Context context) {
        this(context, null);
    }

    public BeautyBoxIcon(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyBoxIcon(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void findViews(Context context) {
        super.findViews(context);
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_box_icon, this);
        boxImg = findViewById(R.id.beauty_box_img);
    }

}
