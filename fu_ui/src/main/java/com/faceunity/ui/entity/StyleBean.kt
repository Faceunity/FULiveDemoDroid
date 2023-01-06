package com.faceunity.ui.entity


/**
 *
 * @property key String 名称标识
 * @property strId Int 名字
 * @property iconId Int 图片
 * @property desRes Int 描述
 * @property canUseFunction Boolean  是否可以使用该项功能
 * @constructor
 */
data class StyleBean(
    val key: String?,
    val strId: Int,
    val iconId: Int,
    val desRes: Int,
    val canUseFunction:Boolean
) {
    constructor(key: String?, strId: Int, desRes: Int, iconId: Int) : this(
        key,
        strId,
        desRes,
        iconId,
        true
    )
}