package com.faceunity.ui.listener

import android.view.View

/**
 * 防止控件快速点击
 *
 */
abstract class OnMultiClickListener : View.OnClickListener {

    companion object {
        private const val MIN_CLICK_DELAY_TIME = 500
    }

    private var mLastClickTime: Long = 0
    private var mViewId = View.NO_ID
    /**
     * 处理后的点击事件
     *
     * @param v
     */
    protected abstract fun onMultiClick(v: View?)

    override fun onClick(v: View) {
        val curClickTime = System.currentTimeMillis()
        val viewId = v.id
        if (mViewId == viewId) {
            if (curClickTime - mLastClickTime >= MIN_CLICK_DELAY_TIME) {
                mLastClickTime = curClickTime
                onMultiClick(v)
            }
        } else {
            mViewId = viewId
            mLastClickTime = curClickTime
            onMultiClick(v)
        }
    }


}