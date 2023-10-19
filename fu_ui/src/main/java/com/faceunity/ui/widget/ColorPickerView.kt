package com.faceunity.ui.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.faceunity.ui.R
import com.faceunity.ui.databinding.LayoutBsgAnchorBinding

/**
 * 选择颜色的锚点，取色器
 *
 */
class ColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mPickedColorDrawable: GradientDrawable

    private val mBinding: LayoutBsgAnchorBinding by lazy {
        LayoutBsgAnchorBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_bsg_anchor, this)
        mPickedColorDrawable = mBinding.ivBsgPickedColor.drawable as GradientDrawable
    }


    fun updatePickerColor(color: Int) {
        if (color == Color.TRANSPARENT) {
            mBinding.ivBsgPickedColor.setImageResource(R.drawable.icon_bsg_pick_color_transparent)
        } else {
            mPickedColorDrawable.setColor(color)
            mBinding.ivBsgPickedColor.setImageDrawable(mPickedColorDrawable)
        }
    }


}