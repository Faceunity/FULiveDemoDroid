package com.faceunity.ui.infe

import com.faceunity.ui.entity.MakeupCombinationBean
import com.faceunity.ui.entity.MakeupCustomBean
import com.faceunity.ui.entity.MakeupCustomClassBean


/**
 *
 * DESC：
 * Created on 2020/12/22
 *
 */
abstract class AbstractMakeupDataFactory {

    /*  默认组合妆容下标  */
    abstract var currentCombinationIndex: Int

    /*  美妆组合妆容配置  */
    abstract val makeupCombinations: ArrayList<MakeupCombinationBean>

    /**
     * 组合妆容选中
     * @param bean MakeupCombinationBean
     */
    abstract fun onMakeupCombinationSelected(bean: MakeupCombinationBean)

    /**
     * 设置美妆整体强度
     * @param intensity Double
     */
    abstract fun updateCombinationIntensity(intensity: Double)

    /**
     * 进入自定义美妆
     */
    abstract fun enterCustomMakeup()

    /**
     * 设置美妆单项强度
     * @param key String 单项key
     * @param current Int 单项下标
     * @param intensity Double
     */
    abstract fun updateCustomItemIntensity(key: String, current: Int, intensity: Double)

    /**
     * 更换类别单项
     * @param key String
     * @param index Int
     */
    abstract fun onCustomBeanSelected(key: String, index: Int)

    /**
     * 设置单项颜色
     * @param key String
     * @param index Int
     */
    abstract fun updateCustomColor(key: String, index: Int)


    /*  美妆功能菜单  */
    abstract val makeupCustomItemParams: LinkedHashMap<String, ArrayList<MakeupCustomBean>>

    /* 美妆子项类别  */

    abstract val makeupCustomClass: ArrayList<MakeupCustomClassBean>


    /**
     * 获取美妆单项当前下标
     * @param key String
     * @return Int
     */
    abstract fun getCurrentCustomItemIndex(key: String): Int

    /**
     * 获取美妆当前选中项颜色下标
     * @param key String
     * @param current Int
     * @return Int
     */
    abstract fun getCurrentCustomColorIndex(key: String, current: Int): Int

    /**
     * 获取美妆当前选中项强度
     * @param key String
     * @param current Int
     * @return Double
     */
    abstract fun getCurrentCustomIntensity(key: String, current: Int): Double
}