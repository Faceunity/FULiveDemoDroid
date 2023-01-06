package com.faceunity.ui.entity.uistate

/**
 * 用于同步ui状态
 * @property skinIndex Int 美颜选中项
 * @property shapeIndex Int 美形选中项
 * @property isSubOpen Boolean 是否开启二级页面
 * @property isSkin Boolean 是否选中美颜
 * @constructor
 */
data class StyleControlState @JvmOverloads constructor(
    val skinIndex: Int = -1,
    val shapeIndex: Int = -1,
    val isSubOpen: Boolean = false,
    val isSkin: Boolean = true,
    val isFilter: Boolean = false
)