package com.faceunity.ui.infe

import com.faceunity.ui.entity.AnimationFilterBean
import com.faceunity.ui.entity.AnimojiBean


/**
 *
 * DESC：
 * Created on 2020/12/24
 *
 */
abstract class AbstractAnimojiDataFactory {

    /* 当前选中动漫贴图下标  */
    abstract var currentAnimojiIndex: Int

    /*  动漫贴图队列   */
    abstract val animojis: ArrayList<AnimojiBean>

    /* 当前选中滤镜下标  */
    abstract var currentFilterIndex: Int

    /* 滤镜队列  */
    abstract val filters: ArrayList<AnimationFilterBean>

    abstract fun onAnimojiSelected(data: AnimojiBean)

    abstract fun onFilterSelected(data: AnimationFilterBean)

}