package com.faceunity.ui.widget

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.faceunity.ui.R

/**
 * Created by tujh on 2018/8/14.
 */
class CameraFocus @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatImageView(context, attrs, defStyleAttr) {
    private var mSizeAnimator: ValueAnimator? = null
    private val mWidth: Int
    private val mHeight: Int
    private val mScale: Float
    private var mRawX = 0f
    private var mRawY = 0f
    fun showCameraFocus(x: Float, y: Float) {
        if (mSizeAnimator == null) {
            mSizeAnimator = ValueAnimator.ofFloat(1f, mScale).setDuration(300)
            mSizeAnimator!!.addUpdateListener(AnimatorUpdateListener { animation -> showCameraFocusLayout(animation.animatedValue as Float) })
        } else if (mSizeAnimator!!.isRunning) {
            mSizeAnimator!!.end()
        }
        mRawX = x
        mRawY = y
        mSizeAnimator!!.start()
    }

    private fun showCameraFocusLayout(scale: Float) {
        val w = (mWidth * scale).toInt()
        val h = (mHeight * scale).toInt()
        val left = (mRawX - w / 2).toInt()
        val top = (mRawY - h / 2).toInt()
        layout(left, top, left + w, top + h)
    }

    companion object {
        private val TAG = CameraFocus::class.java.simpleName
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.camera_focus)
        mWidth = typedArray.getDimensionPixelSize(R.styleable.camera_focus_focus_width, context.resources.getDimensionPixelSize(R.dimen.x150))
        mHeight = typedArray.getDimensionPixelSize(R.styleable.camera_focus_focus_height,context.resources.getDimensionPixelSize(R.dimen.x150))
        mScale = typedArray.getFloat(R.styleable.camera_focus_focus_scale, 0.666f)
        typedArray.recycle()
    }
}