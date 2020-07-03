package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;

import java.util.Arrays;

/**
 * 多人模式下的选择蒙版
 *
 * @author Richie on 2018.09.01
 */
public class FaceMaskView extends View {
    public static final int MAX_FACE = 4;
    private float[] left = new float[MAX_FACE];
    private float[] right = new float[MAX_FACE];
    private float[] top = new float[MAX_FACE];
    private float[] bottom = new float[MAX_FACE];
    private Paint mFacePaint;
    private Paint mBgPaint;
    private Paint mDotLinePaint;
    private Paint mSelectedFacePaint;
    private int mCurrentFace;
    private RectF mFaceRectF;
    private int mSelectedFace = -1;
    private OnFaceSelectedListener mOnFaceSelectedListener;
    private PopupWindow mPopupWindow;
    private boolean mShowPopup = true;

    public FaceMaskView(Context context) {
        super(context);
        init();
    }

    public FaceMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //对单独的View在运行时阶段禁用硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mFacePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int paintWidth = getResources().getDimensionPixelSize(R.dimen.x8);
        mFacePaint.setStrokeWidth(paintWidth);
        mFacePaint.setStyle(Paint.Style.FILL);
        mFacePaint.setColor(Color.TRANSPARENT);
        mFacePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(Color.parseColor("#B3000000"));
        mBgPaint.setStyle(Paint.Style.FILL);

        mDotLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDotLinePaint.setColor(Color.parseColor("#B3FFFFFF"));
        mDotLinePaint.setStyle(Paint.Style.STROKE);
        int dotLineWidth = getResources().getDimensionPixelSize(R.dimen.x3);
        mDotLinePaint.setStrokeWidth(dotLineWidth);
        int rectDotLineGap1 = getResources().getDimensionPixelSize(R.dimen.x8);
        int rectDotLineGap2 = getResources().getDimensionPixelSize(R.dimen.x4);
        PathEffect peDotLine = new DashPathEffect(new float[]{rectDotLineGap1, rectDotLineGap2}, 0);
        mDotLinePaint.setPathEffect(peDotLine);

        mSelectedFacePaint = new Paint(mDotLinePaint);
        mSelectedFacePaint.setPathEffect(null);
        int shaRadius = getResources().getDimensionPixelSize(R.dimen.x4);
        mSelectedFacePaint.setShadowLayer(shaRadius, 0, 0, Color.parseColor("#27A5F4"));

        mFaceRectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getWidth(), getHeight(), mBgPaint);
        for (int i = 0; i < mCurrentFace; i++) {
            mFaceRectF.set(left[i], top[i], right[i], bottom[i]);
            canvas.drawOval(mFaceRectF, mFacePaint);
            if (mSelectedFace == i) {
                canvas.drawOval(mFaceRectF, mSelectedFacePaint);
            } else {
                canvas.drawOval(mFaceRectF, mDotLinePaint);
            }
        }
        if (mShowPopup) {
            showPopupWindow();
            mShowPopup = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dismissPopWindow();
        float x = event.getX();
        float y = event.getY();
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            return true;
        } else if (action == MotionEvent.ACTION_UP) {
            for (int i = 0; i < mCurrentFace; i++) {
                if (x >= left[i] && x <= right[i] && y >= top[i] && y <= bottom[i]) {
                    mSelectedFace = i;
                    invalidate();
                    if (mOnFaceSelectedListener != null) {
                        mOnFaceSelectedListener.onFaceSelected(FaceMaskView.this, mSelectedFace);
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void showPopupWindow() {
        int width = getResources().getDimensionPixelSize(R.dimen.x456);
        int height = getResources().getDimensionPixelSize(R.dimen.x76);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_dual_face, null);
        mPopupWindow = new PopupWindow(view, width, height, true);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setTouchable(false);
        mPopupWindow.setAnimationStyle(R.style.photo_tip_popup_anim_style);
        float maxBottom = findMaxBottom();
        int yOffset = (int) (maxBottom - getHeight() / 2) + getResources().getDimensionPixelSize(R.dimen.x8);
        mPopupWindow.showAtLocation(this, Gravity.CENTER_HORIZONTAL, 0, yOffset);
    }

    private float findMaxBottom() {
        float[] bo = Arrays.copyOf(bottom, bottom.length);
        Arrays.sort(bo);
        return bo[bo.length - 1];
    }

    public void addFaceRect(float[] faceRect) {
        left[mCurrentFace] = faceRect[0] < faceRect[2] ? faceRect[0] : faceRect[2];
        top[mCurrentFace] = faceRect[1] < faceRect[3] ? faceRect[1] : faceRect[3];
        right[mCurrentFace] = faceRect[0] > faceRect[2] ? faceRect[0] : faceRect[2];
        bottom[mCurrentFace] = faceRect[1] > faceRect[3] ? faceRect[1] : faceRect[3];
        mCurrentFace++;
    }

    public void setShowPopup(boolean showPopup) {
        mShowPopup = showPopup;
    }

    public void dismissPopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }

    public void setOnFaceSelectedListener(OnFaceSelectedListener onFaceSelectedListener) {
        mOnFaceSelectedListener = onFaceSelectedListener;
    }

    public interface OnFaceSelectedListener {
        /**
         * 选中某张脸
         *
         * @param view
         * @param index
         */
        void onFaceSelected(View view, int index);
    }
}
