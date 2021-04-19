package com.faceunity.ui.infe

import com.faceunity.ui.entity.AvatarBean


/**
 *
 * DESC：
 * Created on 2021/1/6
 *
 */
abstract class AbstractAvatarDataFactory {

    /**
     * 人物队列
     */
    abstract val members: ArrayList<AvatarBean>

    /**
     * 默认选中人物下标
     */
    abstract var currentMemberIndex: Int

    /**
     * true 全身、  false 半身     驱动切换
     */
    abstract var isHumanTrackSceneFull: Boolean

    /**
     * 当前人物选中
     * @param bean AvatarBean
     */
    abstract fun onMemberSelected(bean:AvatarBean)

}