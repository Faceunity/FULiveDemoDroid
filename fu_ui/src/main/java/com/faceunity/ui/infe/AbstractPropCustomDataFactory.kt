package com.faceunity.ui.infe

import com.faceunity.ui.entity.PropCustomBean


/**
 *
 * DESC：贴图道具
 * Created on 2020/12/23
 *
 */
abstract class AbstractPropCustomDataFactory {

    /**
     * 默认选中道具下标
     */
    abstract var currentPropIndex: Int


    /**
     * 道具队列
     */
    abstract val propCustomBeans: ArrayList<PropCustomBean>


    /**
     * 道具选中
     * @param bean StickerBean
     */
    abstract fun onItemSelected(bean: PropCustomBean)


    /**
     * 道具选中
     */
    abstract fun onAddPropCustomBeanClick()


    companion object {
        const val TYPE_NONE = -1
        const val TYPE_ADD = -99
    }


}