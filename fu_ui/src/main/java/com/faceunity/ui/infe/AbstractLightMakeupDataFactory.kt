package com.faceunity.ui.infe

import com.faceunity.ui.entity.LightMakeupBean


/**
 *
 * DESC：
 * Created on 2020/12/25
 *
 */
abstract class AbstractLightMakeupDataFactory {

    /* 当前选中轻美妆下标  */
    abstract var currentLightMakeupIndex: Int

    /* 轻美妆列表 */
    abstract val lightMakeUpBeans: ArrayList<LightMakeupBean>


    abstract fun onLightMakeupSelected(data: LightMakeupBean)

    abstract fun onLightMakeupIntensityChanged(intensity: Double)


}