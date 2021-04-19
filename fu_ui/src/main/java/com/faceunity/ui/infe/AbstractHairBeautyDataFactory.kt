package com.faceunity.ui.infe

import com.faceunity.ui.entity.HairBeautyBean


/**
 *
 * DESC：
 * Created on 2020/12/23
 *
 */
abstract class AbstractHairBeautyDataFactory {
    /**
     * 美发列表
     */
    abstract val hairBeautyBeans: ArrayList<HairBeautyBean>

    /**
     * 当前选中美发下标
     */
    abstract var currentHairIndex: Int

    /**
     * 美发强度变更
     * @param intensity Double
     */
    abstract fun onHairIntensityChanged(intensity: Double)


    /**
     * 美发类型变更
     * @param data HairBeautyBean
     */
    abstract fun onHairSelected(data: HairBeautyBean)


}