package com.faceunity.ui.dialog

import android.view.*
import com.faceunity.ui.R

/**
 * 分割模式选择框
 */
class PortraitSegmentModeChooseDialogFragment: BaseDialogFragment() {
    private var mOnChooseListener: OnChooseListener? = null

    override fun createDialogView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.dialog_portrait_segment_mode_choose, container, false)
        view.findViewById<View>(R.id.tv_portrait_segment_mode1).setOnClickListener {
            dismiss()
            mOnChooseListener?.onPortraitSegmentMode(PortraitSegmentModeEnum.PortraitSegmentMode1)
        }

        view.findViewById<View>(R.id.tv_portrait_segment_mode2).setOnClickListener {
            dismiss()
            mOnChooseListener?.onPortraitSegmentMode(PortraitSegmentModeEnum.PortraitSegmentMode2)
        }

        view.findViewById<View>(R.id.iv_back).setOnClickListener {
            dismiss()
            mOnChooseListener?.onBack()
        }

        view.findViewById<View>(R.id.btn_camera_change).setOnClickListener {
            mOnChooseListener?.onCameraChange()
        }

        view.findViewById<View>(R.id.btn_debug).setOnClickListener {
            mOnChooseListener?.onDebug()
        }

        isCancelable = false
        return view
    }

    override fun setKeyListener() {
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            //点击返回键不消失
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                dismiss()
                mOnChooseListener?.onBack()
                true
            } else false
        }
    }

    override fun getDialogWidth(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    override fun getDialogHeight(): Int {
        return WindowManager.LayoutParams.MATCH_PARENT
    }

    interface OnChooseListener {
        fun onPortraitSegmentMode(choose:PortraitSegmentModeEnum)

        fun onBack()

        fun onCameraChange()

        fun onDebug()
    }

    enum class PortraitSegmentModeEnum {
        PortraitSegmentMode1,
        PortraitSegmentMode2
    }

    fun setOnChooseListener(onChooseListener: OnChooseListener?) {
        mOnChooseListener = onChooseListener
    }
}