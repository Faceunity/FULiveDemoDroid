@file:Suppress("DEPRECATION")

package com.faceunity.ui.dialog

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.faceunity.ui.R
import java.lang.ref.WeakReference

/**
 *
 * DESC：自定义Toast弹窗
 * Created on 2020/11/10
 *
 */
object ToastHelper {


    @JvmStatic
    fun showWhiteTextToast(context: Context, @StringRes strId: Int) {
        showWhiteTextToast(context.applicationContext, context.getString(strId))
    }

    @JvmStatic
    fun showNormalToast(context: Context, @StringRes strId: Int) {
        showNormalToast(context.applicationContext, context.getString(strId))
    }

    @JvmStatic
    fun dismissToast() {
        dismissWhiteTextToast()
        dismissNormalToast()
    }


    @JvmStatic
    fun dismissWhiteTextToast() {
        mWhiteTextToast?.let {
            it.cancel()
        }
    }

    @JvmStatic
    fun dismissNormalToast() {
        mNormalToast?.let {
            it.cancel()
        }
    }


    private var mNormalToast: Toast? = null
    private var mWhiteTextToast: Toast? = null
    private var mWeakContext: WeakReference<Context>? = null

    @JvmStatic
    fun showWhiteTextToast(context: Context, text: String?) {
        if (mWeakContext?.get() == context) {
            if (mWhiteTextToast != null) {
                val view = mWhiteTextToast!!.view as TextView
                view.text = text
                if (!view.isShown) {
                    mWhiteTextToast!!.show()
                }
                return
            }
        }
        mWeakContext = WeakReference(context)
        val resources = context.resources
        val textView = TextView(mWeakContext!!.get())
        textView.setTextColor(resources.getColor(R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.x64))
        textView.text = text
        mWhiteTextToast = Toast(mWeakContext!!.get())
        mWhiteTextToast!!.view = textView
        mWhiteTextToast!!.duration = Toast.LENGTH_SHORT
        val yOffset = resources.getDimensionPixelSize(R.dimen.x560);
        mWhiteTextToast!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, yOffset)
        mWhiteTextToast!!.show()
    }


    @JvmStatic
    fun showNormalToast(context: Context, text: String?) {
        if (mWeakContext?.get() == context) {
            if (mNormalToast != null) {
                val view = mNormalToast!!.view as TextView
                view.text = text
                if (!view.isShown) {
                    mNormalToast!!.show()
                }
                return
            }
        }
        mWeakContext = WeakReference(context)
        val resources = context.resources
        val textView = TextView(mWeakContext!!.get())
        textView.setTextColor(resources.getColor(R.color.colorWhite))
        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.x26))
        textView.setBackgroundResource(R.drawable.bg_toast_more)
        val hPadding = resources.getDimensionPixelSize(R.dimen.x28)
        val vPadding = resources.getDimensionPixelSize(R.dimen.x16)
        textView.setPadding(hPadding, vPadding, hPadding, vPadding)
        textView.text = text
        mNormalToast = Toast(mWeakContext!!.get())
        mNormalToast!!.view = textView
        mNormalToast!!.duration = Toast.LENGTH_SHORT
        val yOffset = resources.getDimensionPixelSize(R.dimen.x182)
        mNormalToast!!.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.TOP, 0, yOffset)
        mNormalToast!!.show()
    }


}