package com.faceunity.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.faceunity.ui.R

/**
 * 没有检测到人脸的提示框
 */
class NoTrackFaceDialogFragment(private val mContext: Context, private val message: String) : BaseDialogFragment() {

    constructor(mContext: Context, messageRes: Int) : this(mContext, mContext.resources.getString(messageRes))

    private var mOnDismissListener: OnDismissListener? = null


    override fun createDialogView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.dialog_not_track_face, container, false)
        val textView = view.findViewById<View>(R.id.tv_tip_message) as TextView
        if (!message.isNullOrBlank()) {
            textView.text = message
        }
        view.findViewById<View>(R.id.btn_done).setOnClickListener {
            dismiss()
            mOnDismissListener?.onDismiss()
        }
        isCancelable = false
        return view
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        mOnDismissListener = onDismissListener
    }


    override fun getDialogWidth(): Int {
        return resources.getDimensionPixelSize(R.dimen.x490)
    }

    override fun getDialogHeight(): Int {
        return resources.getDimensionPixelSize(R.dimen.x450)
    }


}