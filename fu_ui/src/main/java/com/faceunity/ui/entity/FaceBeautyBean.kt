package com.faceunity.ui.entity


/**
 *
 * @property key String 名称标识
 * @property desRes Int  描述
 * @property closeRes Int 图片
 * @property openRes Int  图片
 * @constructor
 */
data class FaceBeautyBean(
    val key: String,
    val desRes: Int,
    val closeRes: Int,
    val openRes: Int
)