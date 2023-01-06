package com.faceunity.ui.entity.uistate

import android.view.View
import androidx.annotation.IdRes

/**
 * 用于同步ui状态
 * @property actionIndex Int 关闭颜色 相似度 祛色度 安全区域的选择
 * @property colorIndex Int 取色器颜色选择
 * @property bgSafeAreaIndex Int 安全区域选择
 * @property backgroundIndex Int 背景选择
 * @property rbIndex Int 底部导航栏选择
 * @constructor
 */
data class BgSegGreenControlState @JvmOverloads constructor(
    val actionIndex: Int = 1,
    val colorIndex: Int = 1,
    val bgSafeAreaIndex: Int = 1,
    val backgroundIndex: Int = 1,
    @IdRes val rbIndex: Int = View.NO_ID
)