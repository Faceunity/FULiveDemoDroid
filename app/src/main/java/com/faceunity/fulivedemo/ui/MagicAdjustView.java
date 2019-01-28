package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.faceunity.fulivedemo.R;

/**
 * @author LiuQiang on 2018.12.17
 */
public class MagicAdjustView extends AppCompatImageView {
    private static final String TAG = "MagicAdjustView";
    private Paint mPaint;
    private float[] mViewPortPoints;
    private float[] mViewPoints;
    private int mRadius;
    private boolean mDrawViewPort;
    private int mViewPortSize;
    private int mViewPortPadding;
    private int mRawEventX;
    private int mRawEventY;

    public MagicAdjustView(Context context) {
        super(context);
        init();
    }

    public MagicAdjustView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MagicAdjustView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mRadius = getResources().getDimensionPixelSize(R.dimen.x4);
        mViewPortSize = getResources().getDimensionPixelSize(R.dimen.x234);
        mViewPortPadding = getResources().getDimensionPixelSize(R.dimen.x16);
    }

    public void setViewPoints(float[] points) {
        mViewPoints = points;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mViewPoints != null) {
            for (int i = 0, j = mViewPoints.length; i < j; i += 2) {
                canvas.drawCircle(mViewPoints[i], mViewPoints[i + 1], mRadius, mPaint);
            }
        }
        if (mDrawViewPort) {
            if (mViewPortPoints != null) {
                for (int i = 0, j = mViewPortPoints.length; i < j; i += 2) {
                    canvas.drawCircle(mViewPortPoints[i], mViewPortPoints[i + 1], mRadius, mPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
