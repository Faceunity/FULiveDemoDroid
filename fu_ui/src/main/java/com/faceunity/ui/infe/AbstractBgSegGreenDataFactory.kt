package com.faceunity.ui.infe

import com.faceunity.ui.entity.BgSegGreenBackgroundBean
import com.faceunity.ui.entity.BgSegGreenBean
import com.faceunity.ui.entity.ModelAttributeData


/**
 *
 * DESC：
 * Created on 2020/12/29
 *
 */
abstract class AbstractBgSegGreenDataFactory {

    /* 绿幕抠像项目数据扩展模型 */
    abstract val modelAttributeRange: HashMap<String, ModelAttributeData>

    /* 绿幕抠像功能列表 */
    abstract val bgSegGreenActions: ArrayList<BgSegGreenBean>

    /* 绿幕抠像背景列表  */
    abstract val bgSegGreenBackgrounds: ArrayList<BgSegGreenBackgroundBean>

    /* 绿幕抠像当前背景下标 */
    abstract var backgroundIndex: Int


    /**
     * 背景图片变更
     * @param data BgSegGreenBackgroundBean
     */
    abstract fun onBackgroundSelected(data: BgSegGreenBackgroundBean)

    /**
     * 取色锚点颜色变更
     * @param array DoubleArray
     */
    abstract fun onColorRGBChanged(array: DoubleArray)

    /**
     * 绿幕开关
     * @param enable Boolean
     */
    abstract fun onBgSegGreenEnableChanged(enable: Boolean)


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

    /**
     * 是否调用取色器功能
     *
     * @param selected
     * @param color
     */
    abstract fun onColorPickerStateChanged(selected: Boolean, color: Int)

}