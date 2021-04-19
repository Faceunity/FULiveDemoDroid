package com.faceunity.ui.entity


/**
 * 音乐滤镜
 * @property iconId Int 图片资源
 * @property model MusicFilterModel 音乐文件路径
 * @constructor
 */
data class MusicFilterBean @JvmOverloads constructor(val iconId: Int, val path: String? = null, val music: String? = null)