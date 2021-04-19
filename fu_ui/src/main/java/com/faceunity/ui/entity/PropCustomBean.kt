package com.faceunity.ui.entity

/**
 * 道具
 * @property iconId Int 图标
 * @property path String 道具路径
 * @property descId Int 道具提示
 * @property descId Int 类型  -2 为添加事件  -1为空  其他多道具类型
 * @property iconPath 图标路径
 * @constructor
 */
data class PropCustomBean @JvmOverloads constructor(
    val iconId: Int,
    val path: String?,
    val type: Int = -1,
    val descId: Int = 0,
    val iconPath: String? = null
)