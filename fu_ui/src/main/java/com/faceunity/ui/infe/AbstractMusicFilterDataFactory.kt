package com.faceunity.ui.infe

import com.faceunity.ui.entity.MusicFilterBean


/**
 *
 * DESC：
 * Created on 2020/12/23
 *
 */
abstract class AbstractMusicFilterDataFactory {

    /**
     * 当前音乐滤镜下标
     */
    abstract var currentFilterIndex: Int

    /**
     * 音乐滤镜队列
     */
    abstract val musicFilters: ArrayList<MusicFilterBean>


    abstract fun onMusicFilterSelected(data: MusicFilterBean)

}