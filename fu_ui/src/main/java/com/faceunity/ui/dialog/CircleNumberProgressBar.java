package com.faceunity.ui.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.faceunity.ui.R;

/**
 * Created by ZHT on 2017/6/3.
 * 圆形数字进度条
 */

public class CircleNumberProgressBar extends ProgressBar {
    protected Paint mPaint = new Paint();
    private int mRadius;
    private int mStartAngle;
    private int mBarWidth;
    private int mReachColor;
    private int mUnReachColor;
    private int mTextSize;
    private int mTextColor;
    private int mTextVisibility;
    private String mUnit = "";
    private int mUnitVisibility;
    public static final int VISIBLE = 1;
    public static final int INVISIBLE = 0;
    private RectF rectF;
    private Rect mBound;

    public CircleNumberProgressBar(Context context) {
        this(context, null);
    }

    public CircleNumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleNumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleNumberProgressBar);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CircleNumberProgressBar_cnpb_circle_radius, dp2px(30));
        mStartAngle = typedArray.getInteger(R.styleable.CircleNumberProgressBar_cnpb_start_angle, 0);
        mBarWidth = typedArray.getDimensionPixelSize(R.styleable.CircleNumberProgressBar_cnpb_bar_width, dp2px(8));
        mReachColor = typedArray.getColor(R.styleable.CircleNumberProgressBar_cnpb_reach_color, 0xFF303F9F);
        mUnReachColor = typedArray.getColor(R.styleable.CircleNumberProgressBar_cnpb_unreach_color, 0xFFD3D6DA);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.CircleNumberProgressBar_cnpb_text_size, sp2px(14));
        mTextColor = typedArray.getColor(R.styleable.CircleNumberProgressBar_cnpb_text_color, 0xFF303F9F);
        mTextVisibility = typedArray.getInt(R.styleable.CircleNumberProgressBar_cnpb_text_visibility, VISIBLE);
        mUnit = typedArray.getString(R.styleable.CircleNumberProgressBar_cnpb_unit);
        mUnitVisibility = typedArray.getInt(R.styleable.CircleNumberProgressBar_cnpb_unit_visibility, VISIBLE);

        typedArray.recycle();

        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF(0, 0, mRadius * 2, mRadius * 2);  //绘制圆弧时用于规定圆弧边界

        mBound = new Rect();  //用于获取字体边界
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        switch (widthSpecMode) {
            case MeasureSpec.AT_MOST:
                width = Math.min(mRadius * 2 + getPaddingLeft() + getPaddingRight() + mBarWidth, widthSpecSize);
                break;

            case MeasureSpec.EXACTLY:
                width = widthSpecSize;
                break;

            case MeasureSpec.UNSPECIFIED:
                width = mRadius * 2 + getPaddingRight() + getPaddingLeft() + mBarWidth;
                break;
        }

        switch (heightSpecMode) {
            case MeasureSpec.AT_MOST:
                height = Math.min(mRadius * 2 + getPaddingTop() + getPaddingBottom() + mBarWidth, heightSpecSize);
                break;

            case MeasureSpec.EXACTLY:
                height = heightSpecSize;
                break;

            case MeasureSpec.UNSPECIFIED:
                height = mRadius * 2 + getPaddingTop() + getPaddingBottom() + mBarWidth;
                break;
        }

        int result = Math.min(width, height);

        setMeasuredDimension(result, result);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String text = mUnitVisibility == VISIBLE ? getProgress() + mUnit : getProgress() + "";
        float baseline = getMeasuredHeight() / 2 + mPaint.getTextSize() / 2 - mPaint.getFontMetrics().descent - getPaddingTop();
        canvas.save();
        canvas.translate(getPaddingLeft() + mBarWidth / 2, getPaddingTop() + mBarWidth / 2);
        mPaint.setStyle(Paint.Style.STROKE);

        //先绘制未达到的进度条
        mPaint.setColor(mUnReachColor);
        mPaint.setStrokeWidth(mBarWidth);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);

        //再绘制已经达到的进度条
        mPaint.setColor(mReachColor);
        mPaint.setStrokeWidth(mBarWidth);
        float angle = getProgress() * 1.0f / getMax() * 360;
        canvas.drawArc(rectF, mStartAngle, angle, false, mPaint);

        //绘制文字
        if (mTextVisibility == VISIBLE) {
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds(text, 0, text.length(), mBound);
            canvas.drawText(text, mRadius - mBound.width() / 2, baseline, mPaint);
        }

        canvas.restore();
    }

    /**
     * 设置圆的半径
     *
     * @param radius 半径值(dp为单位)
     */
    public void setRadius(int radius) {
        mRadius = dp2px(radius);
    }

    /**
     * 设置进度条的宽度
     *
     * @param width
     */
    public void setBarWidth(int width) {
        mBarWidth = width;
    }

    /**
     * 设置达到进度的颜色
     *
     * @param color 颜色值
     */
    public void setReachColor(int color) {
        mReachColor = color;
    }

    /**
     * 设置文字颜色值
     *
     * @param color 颜色值
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * 设置文字大小
     *
     * @param size
     */
    public void setTextSize(int size) {
        mTextSize = size;
    }

    /**
     * 设置文字是否显示
     *
     * @param visibility
     */
    public void setTextVisibility(int visibility) {
        mTextVisibility = visibility;
    }

    /**
     * 设置单位
     *
     * @param unit
     */
    public void setUnit(String unit) {
        if (unit == null) {
            mUnit = "";
        } else {
            mUnit = unit;
        }
    }

    /**
     * 设置单位是否显示
     *
     * @param visibility
     */
    public void setUnitVisibility(int visibility) {
        mUnitVisibility = visibility;
    }

    /**
     * 将dp值转换为px值
     *
     * @param dp 需要转换的dp值
     * @return px值
     */
    protected int dp2px(float dp) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return (int) (getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * 将sp值转换为px值
     *
     * @param sp 需要转换的sp值
     * @return px值
     */
    protected int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
