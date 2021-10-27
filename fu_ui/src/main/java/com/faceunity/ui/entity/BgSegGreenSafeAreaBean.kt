package com.faceunity.ui.entity


/**
 *
 * DESC：
 * Created on 2020/12/4
 *
 */

data class BgSegGreenSafeAreaBean(val iconRes: Int, val type: ButtonType, val filePath: String? = null,val isAssetFile: Boolean = true) {

    constructor(iconRes: Int, type: ButtonType) : this(
        iconRes,
        type,
        null,
        true
    )

    constructor(iconRes: Int, type: ButtonType, filePath :String) : this(
        iconRes,
        type,
        filePath,
        true
    )
    enum class ButtonType{
        NORMAL1_BUTTON,//普通一号按钮，普通安全区域按钮
        NORMAL2_BUTTON,//普通二号按钮，用于自定义按钮
        BACK_BUTTON,//返回按钮
        NONE_BUTTON,//不选择按钮
    }
}