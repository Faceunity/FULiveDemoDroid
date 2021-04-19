package com.faceunity.ui.infe

import com.faceunity.ui.entity.BodyBeautyBean
import com.faceunity.ui.entity.ModelAttributeData


/**
 *
 * DESC：
 * Created on 2020/12/28
 *
 */
abstract class AbstractBodyBeautyDataFactory {


    /*  美体参数集合   */
    abstract val bodyBeautyParam: ArrayList<BodyBeautyBean>

    /* 美体项目数据扩展模型  */
    abstract val modelAttributeRange: HashMap<String, ModelAttributeData>


    /**
     * 根据名称标识获取对应的值
     * @param key String  标识
     * @return Double  值
     */
    abstract fun getParamIntensity(key: String): Double

    /**
     * 根据名称标识更新对应的值
     * @param key String  标识
     * @return Double  值
     */
    abstract fun updateParamIntensity(key: String, value: Double)
}