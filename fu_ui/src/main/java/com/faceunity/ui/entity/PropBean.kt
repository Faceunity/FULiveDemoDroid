package com.faceunity.ui.entity

/**
 * 道具
 * @property iconId Int 图标
 * @property path String 道具路径
 * @property descId Int 道具提示
 * @constructor
 */
data class PropBean @JvmOverloads constructor(
    val iconId: Int,
    val path: String?,
    val descId: Int = 0
)