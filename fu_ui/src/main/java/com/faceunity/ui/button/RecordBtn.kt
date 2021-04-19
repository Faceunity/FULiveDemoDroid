package com.faceunity.ui.button

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewConfiguration
import com.faceunity.ui.R


/**
 * Created by tujh on 2017/6/14.
 */
@Suppress("DEPRECATION")
class RecordBtn @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), OnTouchListener {
    /**
     * 画笔对象的引用
     */
    private val paint: Paint
    private val mPaintFill: Paint
    private val mOvalRectF: RectF
    private val mShadowPadding: Int

    /**
     * btn 需要绘制的宽度
     */
    private var drawWidth = 0

    /**
     * 圆环的颜色
     */
    private var ringColor = Color.WHITE

    /**
     * 圆环进度的颜色
     */
    var criclesecondColor: Int

    /**
     * 圆环的宽度
     */
    private var ringWidth = 0f

    /**
     * 最大进度
     */
    var max: Long = 10000L

    /**
     * 当前进度
     */
    private var mSecond: Long = 0
    private var mStartTimestamp: Long = 0

    @Volatile
    private var mIsLongClick = false
    private val mLongPressTimeout = ViewConfiguration.getLongPressTimeout()
    private var mOnRecordListener: OnRecordListener? = null
    private val mHandler = Handler()


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /**
         * 画最外层的大圆环
         */
        if (drawWidth <= 0) {
            drawWidth = width
        }
        ringWidth = 15f * drawWidth / 228
        val centreX = width / 2 //获取圆心的x坐标
        val centreY = height - drawWidth / 2
        val radius = (drawWidth / 2 - ringWidth / 2).toInt() - mShadowPadding //圆环的半径
        paint.color = ringColor //设置圆环的颜色
        paint.strokeWidth = ringWidth //设置圆环的宽度
        canvas.drawCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat(), mPaintFill)
        canvas.drawCircle(centreX.toFloat(), centreY.toFloat(), radius.toFloat(), paint) //画出圆环
        /**
         * 画圆弧 ，画圆环的进度
         */
        paint.strokeWidth = ringWidth * 0.75f //设置圆环的宽度
        paint.color = criclesecondColor //设置进度的颜色
        mOvalRectF[centreX - radius.toFloat(), centreY - radius.toFloat(), centreX + radius.toFloat()] =
            centreY + radius.toFloat() //用于定义的圆弧的形状和大小的界限
        canvas.drawArc(mOvalRectF, 270f, 360 * mSecond / max.toFloat(), false, paint) //根据进度画圆弧
        if (mSecond >= max && mOnRecordListener != null) {
            mOnRecordListener!!.stopRecord()
        }
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    @Synchronized
    fun setMax(max: Int) {
        require(max >= 0) { "max not less than 0" }
        this.max = max.toLong()
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param second
     */
    @get:Synchronized
    @set:Synchronized
    var second: Long
        get() = mSecond
        set(second) {
            require(second >= 0) { "mSecond not less than 0" }
            if (second >= max) {
                mSecond = max
            }
            if (second < max) {
                mSecond = second
            }
            postInvalidate()
        }

    var cricleColor: Int
        get() = ringColor
        set(cricleColor) {
            ringColor = cricleColor
            invalidate()
        }

    fun getringWidth(): Float {
        return ringWidth
    }

    fun setringWidth(ringWidth: Float) {
        this.ringWidth = ringWidth
        invalidate()
    }

    fun getDrawWidth(): Int {
        return drawWidth
    }

    fun setDrawWidth(drawWidth: Int) {
        this.drawWidth = drawWidth
        invalidate()
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (mOnRecordListener != null) {
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                mStartTimestamp = System.currentTimeMillis()
                mHandler.postDelayed({
                    mIsLongClick = true
                    mOnRecordListener!!.startRecord()
                }, 500)
                return true
            } else if (action == MotionEvent.ACTION_MOVE) {
                return true
            } else if (action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - mStartTimestamp < 500) {
                    mHandler.removeCallbacksAndMessages(null)
                    mOnRecordListener!!.takePic()
                } else if (mIsLongClick) {
                    mOnRecordListener!!.stopRecord()
                }
                mIsLongClick = false
                mStartTimestamp = 0
                return true
            }
        }
        return false
    }

    fun setOnRecordListener(onRecordListener: OnRecordListener?) {
        mOnRecordListener = onRecordListener
    }

    interface OnRecordListener {
        fun takePic()
        fun startRecord()
        fun stopRecord()
    }

    companion object {
        private const val TAG = "RecordBtn"
    }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        criclesecondColor = context.resources.getColor(R.color.main_color)
        setOnTouchListener(this)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE //设置空心
        mPaintFill = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaintFill.style = Paint.Style.FILL
        mPaintFill.color = Color.parseColor("#47FFFFFF")
        mShadowPadding = (resources.displayMetrics.density * 2 + 0.5f).toInt()
        paint.setShadowLayer(mShadowPadding.toFloat(), 0f, 0f, Color.parseColor("#802D2D2D"))
        mOvalRectF = RectF()
        Log.d(TAG, "RecordBtn: mLongPressTimeout $mLongPressTimeout")
    }
}