package com.faceunity.ui.entity


/**
 * 模型单项补充模型
 * @property default Double 默认值
 * @property stand Double 无变化时候的基准值
 * @property minRange Double 范围最小值
 * @property maxRange Double 范围最大值
 * @constructor
 */
data class ModelAttributeData(val default: Double=0.0, val stand: Double = 0.0, val minRange: Double = 0.0, val maxRange: Double = 1.0)