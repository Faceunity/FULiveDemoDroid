package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.faceunity.fulivedemo.R;

/**
 * Created by tujh on 2018/3/2.
 */

public class XORTextRadioButton extends android.support.v7.widget.AppCompatRadioButton {

    private String textXOR;
    private int textSizeXOR;
    private int textColorXOR;
    private int textXORWidth;
    private int baseLineY;

    private Paint mMyRadioButtonPaint;

    public XORTextRadioButton(Context context) {
        this(context, null);
    }

    public XORTextRadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public XORTextRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.xor_radio_btn, defStyleAttr, 0);

        textXOR = typedArray.getString(R.styleable.xor_radio_btn_text_xor);
        textSizeXOR = typedArray.getDimensionPixelSize(R.styleable.xor_radio_btn_text_size_xor, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, context.getResources().getDisplayMetrics()));
        textColorXOR = typedArray.getColor(R.styleable.xor_radio_btn_text_color_xor, getResources().getColor(R.color.colorWhite));

        mMyRadioButtonPaint = new Paint();
        mMyRadioButtonPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        mMyRadioButtonPaint.setColor(textColorXOR);
        mMyRadioButtonPaint.setTextSize(textSizeXOR);
        mMyRadioButtonPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        textXORWidth = (int) mMyRadioButtonPaint.measureText(textXOR);
        Paint.FontMetrics fontMetrics = mMyRadioButtonPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        baseLineY = (int) (getMeasuredHeight() - top - bottom) / 2;
        setMeasuredDimension(40 + textXORWidth, getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(textXOR, (getMeasuredWidth() - textXORWidth) / 2, baseLineY, mMyRadioButtonPaint);
    }
}
