package com.faceunity.ui.entity

import android.graphics.drawable.Drawable


/**
 *
 * @property nameRes Int  名称
 * @property drawable Drawable  图片资源
 * @property doubleArray ArrayList<DoubleArray>?  颜色数组
 * @constructor
 */
data class MakeupCustomBean @JvmOverloads constructor(
    val nameRes: Int,
    val drawable: Drawable,
    val doubleArray: ArrayList<DoubleArray>? = null
)

