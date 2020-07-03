package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;


/**
 * Created by tujh on 2017/6/14.
 */
public class RecordBtn extends View implements View.OnTouchListener {
    private static final String TAG = "RecordBtn";

    /**
     * 画笔对象的引用
     */
    private Paint paint;
    private Paint mPaintFill;
    private RectF mOvalRectF;
    private int mShadowPadding;

    /**
     * btn 需要绘制的宽度
     */
    private int drawWidth;

    /**
     * 圆环的颜色
     */
    private int ringColor = Color.WHITE;

    /**
     * 圆环进度的颜色
     */
    private int ringSecondColor;

    /**
     * 圆环的宽度
     */
    private float ringWidth;

    /**
     * 最大进度
     */
    private long max = SwitchConfig.VIDEO_RECORD_DURATION;

    /**
     * 当前进度
     */
    private long mSecond = 0;

    private long mStartTimestamp;
    private boolean mIsLongClick;
    private int mLongPressTimeout = ViewConfiguration.getLongPressTimeout();

    private OnRecordListener mOnRecordListener;

    public RecordBtn(Context context) {
        this(context, null);
    }

    public RecordBtn(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        ringSecondColor = context.getResources().getColor(R.color.main_color);

        setOnTouchListener(this);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE); //设置空心
        mPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(Color.parseColor("#47FFFFFF"));
        mShadowPadding = getResources().getDimensionPixelSize(R.dimen.x2);
        paint.setShadowLayer(mShadowPadding, 0, 0, Color.parseColor("#802D2D2D"));
        mOvalRectF = new RectF();

        Log.d(TAG, "RecordBtn: mLongPressTimeout " + mLongPressTimeout);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        if (drawWidth <= 0) {
            drawWidth = getWidth();
        }
        ringWidth = 15f * drawWidth / 228;
        int centreX = getWidth() / 2; //获取圆心的x坐标
        int centreY = getHeight() - drawWidth / 2;
        int radius = (int) (drawWidth / 2 - ringWidth / 2) - mShadowPadding; //圆环的半径
        paint.setColor(ringColor); //设置圆环的颜色
        paint.setStrokeWidth(ringWidth); //设置圆环的宽度
        canvas.drawCircle(centreX, centreY, radius, mPaintFill);
        canvas.drawCircle(centreX, centreY, radius, paint); //画出圆环
        /**
         * 画圆弧 ，画圆环的进度
         */
        paint.setStrokeWidth(ringWidth * 0.75f); //设置圆环的宽度
        paint.setColor(ringSecondColor);  //设置进度的颜色
        mOvalRectF.set(centreX - radius, centreY - radius, centreX + radius, centreY + radius);  //用于定义的圆弧的形状和大小的界限
        canvas.drawArc(mOvalRectF, 270, 360 * mSecond / max, false, paint);  //根据进度画圆弧

        if (mSecond >= max && mOnRecordListener != null) {
            mOnRecordListener.stopRecord();
        }
    }

    public synchronized long getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized long getSecond() {
        return mSecond;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param second
     */
    public synchronized void setSecond(long second) {
        if (second < 0) {
            throw new IllegalArgumentException("mSecond not less than 0");
        }
        if (second >= max) {
            mSecond = max;
        }
        if (second < max) {
            mSecond = second;
        }
        postInvalidate();
    }

    public int getCricleColor() {
        return ringColor;
    }

    public void setCricleColor(int cricleColor) {
        this.ringColor = cricleColor;
        invalidate();
    }

    public int getCriclesecondColor() {
        return ringSecondColor;
    }

    public void setCriclesecondColor(int criclesecondColor) {
        this.ringSecondColor = criclesecondColor;
    }

    public float getringWidth() {
        return ringWidth;
    }

    public void setringWidth(float ringWidth) {
        this.ringWidth = ringWidth;
        invalidate();
    }

    public int getDrawWidth() {
        return drawWidth;
    }

    public void setDrawWidth(int drawWidth) {
        this.drawWidth = drawWidth;
        invalidate();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOnRecordListener != null) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                mStartTimestamp = System.currentTimeMillis();
                return true;
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (!mIsLongClick && System.currentTimeMillis() - mStartTimestamp > mLongPressTimeout) {
                    mOnRecordListener.startRecord();
                    mIsLongClick = true;
                }
                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                if (System.currentTimeMillis() - mStartTimestamp < mLongPressTimeout) {
                    mOnRecordListener.takePic();
                } else if (mIsLongClick) {
                    mOnRecordListener.stopRecord();
                }
                mIsLongClick = false;
                mStartTimestamp = 0;
                return true;
            }
        }
        return false;
    }

    public void setOnRecordListener(OnRecordListener onRecordListener) {
        mOnRecordListener = onRecordListener;
    }

    public interface OnRecordListener {
        void takePic();

        void startRecord();

        void stopRecord();
    }
}
