package com.faceunity.ui.infe

import com.faceunity.ui.entity.PropBean


/**
 *
 * DESC：贴图道具
 * Created on 2020/12/23
 *
 */
abstract class AbstractPropDataFactory {

    /**
     * 默认选中道具下标
     */
    abstract  var currentPropIndex: Int


    /**
     * 道具队列
     */
    abstract  val propBeans: ArrayList<PropBean>


    /**
     * 道具选中
     * @param bean StickerBean
     */
    abstract  fun onItemSelected(bean: PropBean)


}