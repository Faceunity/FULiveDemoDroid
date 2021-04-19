package com.faceunity.ui.entity

/**
 * 滤镜
 * @property key String 名称标识
 * @property imageRes Int 图片
 * @property desRes Int 描述
 * @property intensity Double 强度
 * @constructor
 */
data class FaceBeautyFilterBean @JvmOverloads constructor(val key: String, val imageRes: Int, val desRes: Int, var intensity: Double = 0.4)