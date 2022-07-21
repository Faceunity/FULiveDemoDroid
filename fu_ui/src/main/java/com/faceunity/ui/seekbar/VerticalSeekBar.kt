package com.faceunity.ui.seekbar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.appcompat.widget.AppCompatSeekBar

/**
 * Created by tujh on 2018/8/15.
 */
class VerticalSeekBar : AppCompatSeekBar {
    /**
     * On touch, this offset plus the scaled value from the position of the
     * touch will form the progress value. Usually 0.
     */
    var mTouchProgressOffset = 0f
    private var mIsDragging = false
    private var mTouchDownY = 0f
    private var mScaledTouchSlop = 0
    var isInScrollingContainer = false

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle) {
        mScaledTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?) : super(context!!) {}

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        canvas.rotate(-90f)
        canvas.translate(-height.toFloat(), 0f)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (isInScrollingContainer) {
                mTouchDownY = event.y
            } else {
                isPressed = true
                invalidate()
                onStartTrackingTouch()
                trackTouchEvent(event)
                attemptClaimDrag()
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                } else {
                    val y = event.y
                    if (Math.abs(y - mTouchDownY) > mScaledTouchSlop) {
                        isPressed = true
                        invalidate()
                        onStartTrackingTouch()
                        trackTouchEvent(event)
                        attemptClaimDrag()
                    }
                }
                onSizeChanged(width, height, 0, 0)
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else { // Touch up when we never crossed the touch slop threshold
// should
// be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                onSizeChanged(width, height, 0, 0)
                // ProgressBar doesn't know to repaint the thumb drawable
// in its inactive state when the touch stops (because the
// value has not apparently changed)
                invalidate()
            }
        }
        return true
    }

    private fun trackTouchEvent(event: MotionEvent) {
        val height = height
        val top = paddingTop
        val bottom = paddingBottom
        val available = height - top - bottom
        val y = event.y.toInt()
        val scale: Float
        var progress = 0f
        // 下面是最小值
        if (y > height - bottom) {
            scale = 0.0f
        } else if (y < top) {
            scale = 1.0f
        } else {
            scale = (available - y + top).toFloat() / available.toFloat()
            progress = mTouchProgressOffset
        }
        val max = max
        progress += scale * max
        setProgress(progress.toInt())
    }

    /**
     * This is called when the user has started touching this widget.
     */
    fun onStartTrackingTouch() {
        mIsDragging = true
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    fun onStopTrackingTouch() {
        mIsDragging = false
    }

    private fun attemptClaimDrag() {
        val p = parent
        p?.requestDisallowInterceptTouchEvent(true)
    }

    @Synchronized
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        onSizeChanged(width, height, 0, 0)
    }
}