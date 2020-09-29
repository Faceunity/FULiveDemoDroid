package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;
import com.faceunity.utils.BitmapUtil;

/**
 * @author Richie on 2020.09.14
 */
public class RingCircleView extends View {
    private static final String TAG = "RingCircleView";
    private int mDefaultSize;
    private Paint mPaintOuter;
    private Paint mPaintInner;
    private Paint mPaintBitmap;
    private int mFillColor = Color.GREEN;
    private int mInnerPadding;
    private Bitmap mBitmapPick;
    private Bitmap mBitmapBg;
    private Rect mBitmapRect = new Rect();
    private int mDrawType = TYPE_NORMAL;

    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_TRANSPARENT = 2;
    public static final int TYPE_PICK_TRANSPARENT = 3;
    public static final int TYPE_PICK_COLOR = 4;

    public RingCircleView(@NonNull Context context) {
        this(context, null);
    }

    public RingCircleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingCircleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RingCircleView);
            mInnerPadding = (int) typedArray.getDimension(R.styleable.RingCircleView_inner_padding, getResources().getDimensionPixelSize(R.dimen.x20));
            typedArray.recycle();
        }
        mDefaultSize = getResources().getDimensionPixelSize(R.dimen.x88);
        mPaintOuter = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOuter.setStyle(Paint.Style.STROKE);
        mPaintOuter.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.x4));
        mPaintOuter.setColor(Color.WHITE);
        mPaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintInner.setStyle(Paint.Style.FILL);
        mPaintInner.setColor(mFillColor);
        mPaintBitmap = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
        mPaintInner.setColor(mFillColor);
        invalidate();
    }

    public void setDrawType(int drawType) {
        mDrawType = drawType;
        invalidate();
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
        int centerX = width / 2;
        int height = getMeasuredHeight();
        int centerY = height / 2;
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int maxPadding = Math.max(Math.max(paddingLeft, paddingTop), Math.max(paddingRight, paddingBottom));
        paddingLeft = maxPadding;
        paddingRight = maxPadding;
        paddingTop = maxPadding;
        paddingBottom = maxPadding;

        boolean selected = isSelected();
        int diameter = Math.min(width - paddingLeft - paddingRight, height - paddingTop - paddingBottom);
        float radius = (float) diameter / 2;

        switch (mDrawType) {
            case TYPE_NORMAL: {
                if (selected) {
                    float innerRadius = ((float) diameter - mInnerPadding) / 2;
                    canvas.drawCircle(centerX, centerY, radius, mPaintOuter);
                    canvas.drawCircle(centerX, centerX, innerRadius, mPaintInner);
                } else {
                    canvas.drawCircle(centerX, centerY, radius, mPaintInner);
                }
            }
            break;
            case TYPE_PICK_COLOR: {
                float innerRadius = ((float) diameter - mInnerPadding) / 2;
                canvas.drawCircle(centerX, centerY, radius, mPaintOuter);
                canvas.drawCircle(centerX, centerX, innerRadius, mPaintInner);
            }
            break;
            case TYPE_PICK_TRANSPARENT: {
                if (mBitmapPick == null) {
                    mBitmapPick = BitmapUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.demo_icon_straw, diameter, diameter);
                }
                if (mBitmapBg == null) {
                    mBitmapBg = BitmapUtil.decodeSampledBitmapFromResource(getResources(), R.drawable.demo_bg_transparent, diameter, diameter);
                }
                mBitmapRect.left = paddingLeft;
                mBitmapRect.top = paddingTop;
                mBitmapRect.right = width - paddingRight;
                mBitmapRect.bottom = height - paddingBottom;
                canvas.drawBitmap(mBitmapBg, null, mBitmapRect, mPaintBitmap);
                canvas.drawBitmap(mBitmapPick, null, mBitmapRect, mPaintBitmap);
            }
            break;
            case TYPE_TRANSPARENT: {
                canvas.drawCircle(centerX, centerY, radius, mPaintOuter);
                int padding = mInnerPadding / 2;
                mBitmapRect.left = paddingLeft + padding;
                mBitmapRect.top = paddingTop + padding;
                mBitmapRect.right = width - paddingRight - padding;
                mBitmapRect.bottom = height - paddingBottom - padding;
                canvas.drawBitmap(mBitmapBg, null, mBitmapRect, mPaintBitmap);
            }
            break;
            default:
        }
    }

}
