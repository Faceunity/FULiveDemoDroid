package com.faceunity.ui.entity


/**
 *
 * DESC：
 * Created on 2020/12/4
 *
 */

data class BgSegGreenBean(val key: String, val desRes: Int, val closeRes: Int, val openRes: Int, val type: ButtonType) {
    enum class ButtonType{
        NORMAL1_BUTTON,//普通一号按钮
        NORMAL2_BUTTON,//普通二号按钮
        BACK_BUTTON,//返回按钮
        SWITCH_BUTTON//切换按钮，切换整个按钮功能
    }
}