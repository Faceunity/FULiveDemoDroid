package com.faceunity.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.faceunity.ui.R
import kotlinx.android.synthetic.main.layout_bsg_anchor.view.*
import java.lang.Exception

/**
 * 选择颜色的锚点，取色器
 *
 */
class ColorPickerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var mPickedColorDrawable: GradientDrawable


    init {
        LayoutInflater.from(context).inflate(R.layout.layout_bsg_anchor, this)
        mPickedColorDrawable = iv_bsg_picked_color.drawable as GradientDrawable
    }


    fun updatePickerColor(color: Int) {
        if (color == Color.TRANSPARENT) {
            iv_bsg_picked_color.setImageResource(R.drawable.icon_bsg_pick_color_transparent)
        } else {
            mPickedColorDrawable.setColor(color)
            iv_bsg_picked_color!!.setImageDrawable(mPickedColorDrawable)
        }
    }


}