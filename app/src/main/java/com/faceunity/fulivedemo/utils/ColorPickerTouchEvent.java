package com.faceunity.fulivedemo.utils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.ColorPickerView;

/**
 * @author Richie on 2020.08.19
 */
public class ColorPickerTouchEvent {
    private PixelColorReader mPixelColorReader = new PixelColorReader();
    private ColorPickerView mColorPickerView;

    public ColorPickerTouchEvent(Activity context) {
        mColorPickerView = new ColorPickerView(context);
    }

    public ColorPickerView getColorPickerView() {
        return mColorPickerView;
    }

    public boolean handleTouchEvent(MotionEvent event, GLSurfaceView glSurfaceView, int viewWidth, int viewHeight,
                                    float[] texMatrix, float[] mvpMatrix, int texId, OnTouchEventListener onTouchEventListener) {
        int y = (int) event.getY();
        Resources resources = mColorPickerView.getContext().getResources();
        int parentHeight = ((ViewGroup) mColorPickerView.getParent()).getHeight();
        int action = event.getAction();
        boolean isUp = action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL;
        // 366-64=302
        if (y > parentHeight - resources.getDimensionPixelSize(R.dimen.x302) && !isUp) {
            return false;
        }
        if (action == MotionEvent.ACTION_MOVE || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) mColorPickerView.getLayoutParams();
            layoutParams.leftMargin = x - mColorPickerView.getWidth() / 2;
            layoutParams.topMargin = y - mColorPickerView.getHeight() - resources.getDimensionPixelSize(R.dimen.x64);
            mColorPickerView.setLayoutParams(layoutParams);
            int centerX = x;
            int centerY = viewHeight - (layoutParams.topMargin + resources.getDimensionPixelSize(R.dimen.x104));
            mPixelColorReader.setClickPosition(centerX, centerY);
            if (action == MotionEvent.ACTION_MOVE) {
                glSurfaceView.queueEvent(mPixelColorReader.draw());
            } else {
                mColorPickerView.setVisibility(View.VISIBLE);
                mPixelColorReader.setViewSize(viewWidth, viewHeight);
                mPixelColorReader.setDrawMatrix(mvpMatrix, texMatrix);
                mPixelColorReader.setDrawTexture(texId);
                mPixelColorReader.setOnReadRgbaListener(new PixelColorReader.OnReadRgbaListener() {
                    @Override
                    public void onReadRgba(int r, int g, int b, int a) {
                        mColorPickerView.postSetPickedColor(Color.argb(a, r, g, b));
                        if (onTouchEventListener != null) {
                            onTouchEventListener.onReadRgba(r, g, b, a);
                        }
                    }
                });
                glSurfaceView.queueEvent(mPixelColorReader.create());
            }
            return true;
        } else if (isUp) {
            glSurfaceView.queueEvent(mPixelColorReader.destroy());
            mColorPickerView.setVisibility(View.GONE);
            if (onTouchEventListener != null) {
                onTouchEventListener.onActionUp();
            }
            return true;
        }
        return false;
    }

    public interface OnTouchEventListener {
        /**
         * 读到 rgba 值
         *
         * @param r
         * @param g
         * @param b
         * @param a
         */
        void onReadRgba(int r, int g, int b, int a);

        /**
         * 手势抬起，结束取色
         */
        void onActionUp();
    }

}
