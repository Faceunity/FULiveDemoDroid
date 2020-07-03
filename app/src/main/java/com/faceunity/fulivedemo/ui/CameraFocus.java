package com.faceunity.fulivedemo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.faceunity.fulivedemo.R;

/**
 * Created by tujh on 2018/8/14.
 */
public class CameraFocus extends AppCompatImageView {
    private static final String TAG = CameraFocus.class.getSimpleName();

    private ValueAnimator mSizeAnimator;
    private int mWidth;
    private int mHeight;
    private float mScale;

    private float mRawX;
    private float mRawY;

    public CameraFocus(Context context) {
        this(context, null);
    }

    public CameraFocus(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraFocus(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.camera_focus);
        mWidth = typedArray.getDimensionPixelSize(R.styleable.camera_focus_focus_width, context.getResources().getDimensionPixelSize(R.dimen.x150));
        mHeight = typedArray.getDimensionPixelSize(R.styleable.camera_focus_focus_height, context.getResources().getDimensionPixelSize(R.dimen.x150));
        mScale = typedArray.getFloat(R.styleable.camera_focus_focus_scale, 0.666f);
        typedArray.recycle();
    }

    public void showCameraFocus(float x, float y) {
        if (mSizeAnimator == null) {
            mSizeAnimator = ValueAnimator.ofFloat(1, mScale).setDuration(300);
            mSizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    showCameraFocusLayout((float) animation.getAnimatedValue());
                }
            });
        } else if (mSizeAnimator.isRunning()) {
            mSizeAnimator.end();
        }
        mRawX = x;
        mRawY = y;
        mSizeAnimator.start();
    }

    private void showCameraFocusLayout(float scale) {
        int w = (int) (mWidth * scale);
        int h = (int) (mHeight * scale);
        int left = (int) (mRawX - w / 2);
        int top = (int) (mRawY - h / 2);
        layout(left, top, left + w, top + h);
    }
}
