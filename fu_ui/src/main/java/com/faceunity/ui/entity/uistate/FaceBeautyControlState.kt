package com.faceunity.ui.entity.uistate

import android.view.View
import androidx.annotation.IdRes

/**
 * 用于同步ui状态
 * @property skinIndex Int 美颜选中项
 * @property shapeIndex Int 美形选中项
 * @constructor
 */
data class FaceBeautyControlState @JvmOverloads constructor(
    val skinIndex: Int = -1,
    val shapeIndex: Int = -1,
    @IdRes val rbRes: Int = View.NO_ID
)