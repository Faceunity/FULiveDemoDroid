package com.faceunity.fulivedemo.ui.colorfulcircle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;

/**
 * 带有阴影的白色圆环
 *
 * @author Richie on 2019.05.30
 */
public class ShadowCircleRingView extends View {
    private Paint mBorderPaint;
    private int mBorderWidth;
    private int mDefaultSize;

    public ShadowCircleRingView(Context context) {
        super(context);
        init();
    }

    public ShadowCircleRingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShadowCircleRingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.WHITE);
        mBorderWidth = getResources().getDimensionPixelSize(R.dimen.x8);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        int shaRadius = getResources().getDimensionPixelSize(R.dimen.x4);
        mBorderPaint.setShadowLayer(shaRadius, 0, 0, Color.parseColor("#80000000"));
        mDefaultSize = getResources().getDimensionPixelSize(R.dimen.x96);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            measuredWidth = mDefaultSize;
            measuredHeight = mDefaultSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            measuredWidth = mDefaultSize;
            measuredHeight = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            measuredHeight = mDefaultSize;
            measuredWidth = widthSize;
        }
        int minSize = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(minSize, minSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth() - getPaddingLeft() - getPaddingRight();
        float cx = (float) w / 2;
        int h = getHeight() - getPaddingTop() - getPaddingBottom();
        float cy = (float) h / 2;
        float radius = (float) Math.min(w, h) / 2 - mBorderWidth;
        canvas.drawCircle(cx, cy, radius, mBorderPaint);
    }

}
