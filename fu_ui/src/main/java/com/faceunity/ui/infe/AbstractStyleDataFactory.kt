package com.faceunity.ui.infe

import com.faceunity.ui.entity.FaceBeautyBean
import com.faceunity.ui.entity.ModelAttributeData
import com.faceunity.ui.entity.StyleBean


/**
 *
 * DESC：
 * Created on 2020/12/23
 *
 */
abstract class AbstractStyleDataFactory {
    /**
     * 美肤底部菜单数据
     */
    abstract val skinBeauty: ArrayList<FaceBeautyBean>

    /**
     * 美型底部菜单数据
     */
    abstract val shapeBeauty: ArrayList<FaceBeautyBean>

    /* 美颜项目数据扩展模型 */
    abstract val modelAttributeRange: HashMap<String, ModelAttributeData>

    /**
     * 风格列表
     */
    abstract val styleBeans: ArrayList<StyleBean>

    /**
     * 当前选中风格下标
     */
    abstract var currentStyleIndex: Int

    /**
     * 风格类型变更
     * @param name 风格key
     */
    abstract fun onStyleSelected(name: String?)

    /**
     * 风格类型变更
     * @param name 风格key
     * @param cacheAction 是否缓存动作同一帧触发
     */
    abstract fun onStyleSelected(name: String?, cacheAction: Boolean)

    /**
     * 风格开关
     * @param enable Boolean
     */
    abstract fun enableStyle(enable: Boolean)

    /**
     * 恢复风格所以参数
     */
    abstract fun recoverStyleAllParams()

    //美颜相关
    /**
     * 获取单项强度
     * @param key String
     * @return Double
     */
    abstract fun getParamIntensity(key: String): Double

    /**
     * 设置单项强度
     * @param key String
     * @param value Double
     */
    abstract fun updateParamIntensity(key: String, value: Double)

    /**
     * 设置所有美肤效果到无
     */
    abstract fun enableFaceBeautySkin(faceBeautySkinEnable: Boolean)

    /**
     * 设置所有美肤效果到无
     */
    abstract fun enableFaceBeautyShape(faceBeautyShapeEnable: Boolean)

    /**
     * 获取当前风格美肤是否开启
     */
    abstract fun getCurrentStyleSkinEnable(): Boolean

    /**
     * 获取当前风格美型是否开启
     */
    abstract fun getCurrentStyleShapeEnable(): Boolean

    //美妆相关
    /**
     * 获取美妆强度
     * @param key String
     * @return Double
     */
    abstract fun getMakeupIntensity(): Double

    /**
     * 设置美妆强度
     * @param key String
     * @param value Double
     */
    abstract fun updateMakeupParamIntensity(value: Double)

    /**
     * 获取滤镜强度
     * @param key String
     * @return Double
     */
    abstract fun getFilterIntensity(): Double

    /**
     * 设置滤镜强度
     * @param key String
     * @param value Double
     */
    abstract fun updateFilterParamIntensity(value: Double)

    /**
     * 检查是否与原来风格一样
     * @return ture和原风格一样 false和原风格不一样
     */
    abstract fun checkStyleRecover(): Boolean

    /**
     * 将真正生效的风格转换成角标
     */
    abstract fun styleTypeIndex()

    /**
     * 获取关联的radioButton选择项
     */
    abstract fun getParamRelevanceSelectedType(key: String): Int

    /**
     * 更新关联的radioButton选择项
     */
    abstract fun updateParamRelevanceType(key: String, type: Int)
}