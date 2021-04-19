package com.faceunity.ui.radio

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatRadioButton
import com.faceunity.ui.R

/**
 * Created by tujh on 2018/3/2.
 */
class XfermodeRadioButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.radioButtonStyle
) : AppCompatRadioButton(context, attrs, defStyleAttr) {
    private val textXfermode: String?
    private val textSizeXfermode: Int
    private val textColorXfermodeNormal = -0x1
    private val textColorXfermodeChecked = -0x66faf0ec
    private var textXfermodeWidth = 0
    private var baseLineY = 0
    private val mMyRadioButtonPaint: Paint?
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        textXfermodeWidth = mMyRadioButtonPaint!!.measureText(textXfermode).toInt()
        val fontMetrics = mMyRadioButtonPaint.fontMetrics
        val top = fontMetrics.top //为基线到字体上边框的距离,即上图中的top
        val bottom = fontMetrics.bottom //为基线到字体下边框的距离,即上图中的bottom
        baseLineY = (measuredHeight - top - bottom).toInt() / 2
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun setChecked(checked: Boolean) {
        super.setChecked(checked)
        if (mMyRadioButtonPaint != null) {
            mMyRadioButtonPaint.color = if (checked) textColorXfermodeChecked else textColorXfermodeNormal
            mMyRadioButtonPaint.xfermode = PorterDuffXfermode(if (checked) PorterDuff.Mode.SRC_ATOP else PorterDuff.Mode.XOR)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val x = (measuredWidth - textXfermodeWidth) / 2
        canvas.drawText(textXfermode!!, x.toFloat(), baseLineY.toFloat(), mMyRadioButtonPaint!!)
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.xfermode_radio_btn, defStyleAttr, 0)
        textXfermode = typedArray.getString(R.styleable.xfermode_radio_btn_text_xfermode)
        textSizeXfermode = typedArray.getDimensionPixelSize(
            R.styleable.xfermode_radio_btn_text_size_xfermode,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, context.resources.displayMetrics).toInt()
        )
        typedArray.recycle()
        mMyRadioButtonPaint = Paint()
        mMyRadioButtonPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
        mMyRadioButtonPaint.color = textColorXfermodeNormal
        mMyRadioButtonPaint.textSize = textSizeXfermode.toFloat()
        mMyRadioButtonPaint.isAntiAlias = true
        buttonDrawable = StateListDrawable()
    }
}