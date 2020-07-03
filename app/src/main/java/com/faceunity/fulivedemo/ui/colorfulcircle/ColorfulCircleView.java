package com.faceunity.fulivedemo.ui.colorfulcircle;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;

/**
 * 多彩圆形 View，支持四种颜色值
 *
 * @author Richie on 2019.05.25
 */
public class ColorfulCircleView extends View {
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint4;
    private Paint mPaint3;
    private RectF mBoundRectF;
    private CircleFilledColor mCircleFillColor;
    private int mDefaultSize;

    public ColorfulCircleView(Context context) {
        super(context);
        init();
    }

    public ColorfulCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public ColorfulCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorfulCircleView);
        int fillColor1 = typedArray.getColor(R.styleable.ColorfulCircleView_fill_color_1, DEFAULT_COLOR);
        int fillColor2 = typedArray.getColor(R.styleable.ColorfulCircleView_fill_color_2, DEFAULT_COLOR);
        int fillColor3 = typedArray.getColor(R.styleable.ColorfulCircleView_fill_color_3, DEFAULT_COLOR);
        int fillColor4 = typedArray.getColor(R.styleable.ColorfulCircleView_fill_color_4, DEFAULT_COLOR);
        int fillMode = typedArray.getInt(R.styleable.ColorfulCircleView_fill_mode, CircleFilledColor.FillMode.SINGLE.getValue());
        mCircleFillColor = new CircleFilledColor(fillColor1, fillColor2, fillColor3, fillColor4, CircleFilledColor.FillMode.ofValue(fillMode));
        typedArray.recycle();

        init();
    }

    private void init() {
        mPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint1.setStyle(Paint.Style.FILL);
        mPaint1.setColor(mCircleFillColor.getFillColor1());
        mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint2.setStyle(Paint.Style.FILL);
        mPaint2.setColor(mCircleFillColor.getFillColor2());
        mPaint3 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint3.setStyle(Paint.Style.FILL);
        mPaint3.setColor(mCircleFillColor.getFillColor3());
        mPaint4 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint4.setStyle(Paint.Style.FILL);
        mPaint4.setColor(mCircleFillColor.getFillColor4());
        mBoundRectF = new RectF();
        mDefaultSize = getResources().getDimensionPixelSize(R.dimen.x80);
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
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int maxPadding = Math.max(Math.max(paddingLeft, paddingTop), Math.max(paddingRight, paddingBottom));
        paddingLeft = maxPadding;
        paddingRight = maxPadding;
        paddingTop = maxPadding;
        paddingBottom = maxPadding;

        final int left = paddingLeft;
        final int top = paddingTop;
        final int right = width - paddingRight;
        final int bottom = height - paddingBottom;
        mBoundRectF.set(paddingLeft, paddingTop, right, bottom);
        // draw shadow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setOval(left, top, right, bottom);
                }
            };
            setOutlineProvider(viewOutlineProvider);
        }

        switch (mCircleFillColor.getFillMode()) {
            case SINGLE: {
                canvas.drawArc(mBoundRectF, 0, 360, true, mPaint1);
            }
            break;
            case DOUBLE: {
                canvas.drawArc(mBoundRectF, 0, 180, true, mPaint2);
                canvas.drawArc(mBoundRectF, 180, 180, true, mPaint1);
            }
            break;
            case TRIPLE: {
                canvas.drawArc(mBoundRectF, 0, 360, true, mPaint2);
                canvas.drawArc(mBoundRectF, 20, 140, false, mPaint3);
                canvas.drawArc(mBoundRectF, 200, 140, false, mPaint1);
            }
            break;
            case QUADRUPLE: {
                canvas.drawArc(mBoundRectF, 0, 90, true, mPaint4);
                canvas.drawArc(mBoundRectF, 90, 90, true, mPaint3);
                canvas.drawArc(mBoundRectF, 180, 90, true, mPaint2);
                canvas.drawArc(mBoundRectF, 270, 90, true, mPaint1);
            }
            break;
            default:
        }
    }

    public void setCircleFillColor(CircleFilledColor circleFillColor) {
        mCircleFillColor = circleFillColor;
        mPaint1.setColor(circleFillColor.getFillColor1());
        mPaint2.setColor(circleFillColor.getFillColor2());
        mPaint3.setColor(circleFillColor.getFillColor3());
        mPaint4.setColor(circleFillColor.getFillColor4());

        invalidate();
    }

    public CircleFilledColor getCircleFillColor() {
        return mCircleFillColor;
    }
}
