package com.faceunity.ui.entity


/**
 *
 * @property key String 名称标识
 * @property desRes Int  描述
 * @property closeRes Int 图片
 * @property openRes Int  图片
 * @property buttonType 按钮类型
 * @property showRadioButton  boolean 展示RadioButton
 * @constructor
 */
data class FaceBeautyBean(
    val key: String,
    val desRes: Int,
    val closeRes: Int,
    val openRes: Int,
    val canUseFunction: Boolean = true,
    val buttonType: ButtonType = ButtonType.NORMAL_BUTTON//定义一项按钮功能 普通按钮 返回按钮 子项按钮
) {
    constructor(key: String, desRes: Int, closeRes: Int, openRes: Int) : this(
        key,
        desRes,
        closeRes,
        openRes,
        true,
        ButtonType.NORMAL_BUTTON
    )

    constructor(
        key: String,
        desRes: Int,
        closeRes: Int,
        openRes: Int,
        canUseFunction: Boolean
    ) : this(
        key,
        desRes,
        closeRes,
        openRes,
        canUseFunction,
        ButtonType.NORMAL_BUTTON
    )


    enum class ButtonType {
        NORMAL_BUTTON,
        BACK_BUTTON,
        SUB_ITEM_BUTTON
    }
}