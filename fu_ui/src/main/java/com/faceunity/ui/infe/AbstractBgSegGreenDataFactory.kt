package com.faceunity.ui.infe

import com.faceunity.ui.entity.BgSegGreenBackgroundBean
import com.faceunity.ui.entity.BgSegGreenBean
import com.faceunity.ui.entity.BgSegGreenSafeAreaBean
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

    /* 绿幕抠图安全区域功能列表*/
    abstract val bgSegGreenSafeAreas: ArrayList<BgSegGreenSafeAreaBean>

    /* 安全区域下标 */
    abstract var bgSafeAreaIndex: Int

    /* 刷新安全区域UI */
    abstract fun updateSafeAreaBeansAndIndex() :Boolean

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
     * 自定义安全区域
     */
    abstract fun onSafeAreaAdd()

    /**
     * 安全区域变更
     * @param data BgSegGreenSafeAreaBean
     */
    abstract fun onSafeAreaSelected(data: BgSegGreenSafeAreaBean?)

    /**
     * 是否开启安全区域总开关
     */
    abstract fun isUseTemplate():Boolean

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