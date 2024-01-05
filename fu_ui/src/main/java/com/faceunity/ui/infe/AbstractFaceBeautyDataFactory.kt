package com.faceunity.ui.infe

import com.faceunity.ui.entity.FaceBeautyBean
import com.faceunity.ui.entity.FaceBeautyFilterBean
import com.faceunity.ui.entity.ModelAttributeData


/**
 *
 * DESC：数据构造工厂接口类
 * Created on 2020/12/23
 *
 */
abstract class AbstractFaceBeautyDataFactory {

    /*美肤底部菜单数据*/
    abstract val skinBeauty: ArrayList<FaceBeautyBean>

    /*美型底部菜单数据*/
    abstract val shapeBeauty: ArrayList<FaceBeautyBean>

    /*美型脸型子项数据*/
    abstract val shapeBeautySubItem: ArrayList<FaceBeautyBean>

    /* 滤镜底部菜单数据*/
    abstract val beautyFilters: ArrayList<FaceBeautyFilterBean>

    /*系统推荐配置滤镜对应下标*/
    abstract var currentFilterIndex: Int

    /* 美颜项目数据扩展模型 */
    abstract val modelAttributeRange: HashMap<String, ModelAttributeData>

    /**
     * 切换滤镜
     * @param name String
     * @param intensity Double
     */
    abstract fun onFilterSelected(name: String, intensity: Double, resDes: Int)

    /**
     * 更改滤镜强度
     * @param intensity Double
     */
    abstract fun updateFilterIntensity(intensity: Double)

    /**
     * 美颜开关
     * @param enable Boolean
     */
    abstract fun enableFaceBeauty(enable: Boolean)

    /**
     * 获取单项强度
     * @param key String
     * @return Double
     */
    abstract fun getParamIntensity(key: String): Double

    /**
     * 获取one hot的脸型
     */
    abstract fun getCurrentOneHotFaceShape(): String

    /**
     * 设置当前one hot的脸型
     */
    abstract fun setCurrentOneHotFaceShape(faceShape: String)

    /**
     * 设置当前脸型的UI值
     */
    abstract fun setCurrentFaceShapeUIValue(hashMap: HashMap<String, Double>)

    /**
     * 获取当前脸型的UI值
     */
    abstract fun getCurrentFaceShapeUIValue(): HashMap<String, Double>

    /**
     * 设置单项强度
     * @param key String
     * @param value Double
     */
    abstract fun updateParamIntensity(key: String, value: Double)

    /**
     * 将所有效果置空 -> 变成标准值
     */
    abstract fun resetParamIntensity()

    /**
     * 获取关联的radioButton选择项
     */
    abstract fun getParamRelevanceSelectedType(key: String): Int

    /**
     * 更新关联的radioButton选择项
     */
    abstract fun updateParamRelevanceType(key: String, type: Int)
}