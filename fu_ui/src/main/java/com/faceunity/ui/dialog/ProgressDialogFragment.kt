package com.faceunity.ui.dialog

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.faceunity.ui.R

/**
 * 保存视频进度
 */
class ProgressDialogFragment(val duration:Long) : BaseDialogFragment() {

    private var mOnDismissListener: OnDismissListener? = null

    override fun createDialogView(inflater: LayoutInflater, container: ViewGroup?): View {
        val view = inflater.inflate(R.layout.dialog_progress, container, false)
        val textView = view.findViewById<View>(R.id.tv_cancel) as TextView
        val progressBar = view.findViewById<View>(R.id.cnpb_progress) as CircleNumberProgressBar
        textView.setOnClickListener {
            dismiss()
        }
        isCancelable = false

        val start = 0
        val end = 100

        val progressAnimator = ValueAnimator.ofInt(start, end).setDuration(duration)
        progressAnimator!!.addUpdateListener { animation ->
            val progress = animation.animatedValue as Int
            progressBar.progress = progress
        }
        progressAnimator.start()
        return view
    }

    fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        mOnDismissListener = onDismissListener
    }

    override fun dismiss(){
        super.dismiss()
        mOnDismissListener?.onDismiss()
    }
}