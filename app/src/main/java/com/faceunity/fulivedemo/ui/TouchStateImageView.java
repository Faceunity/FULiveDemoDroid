package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Touch 事件时支持 state 变换
 *
 * @author Richie on 2018.09.20
 */
public class TouchStateImageView extends AppCompatImageView {
    private OnTouchStateListener mOnTouchStateListener;

    public TouchStateImageView(Context context) {
        super(context);
    }

    public TouchStateImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchStateImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnTouchStateListener(OnTouchStateListener onTouchStateListener) {
        mOnTouchStateListener = onTouchStateListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener l) {
        OnTouchListener onTouchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setState(event);
                boolean ret = l.onTouch(v, event);
                if (!ret && mOnTouchStateListener != null) {
                    return mOnTouchStateListener.onTouch(v, event);
                } else {
                    return ret;
                }
            }
        };
        super.setOnTouchListener(onTouchListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setState(event);
        if (mOnTouchStateListener != null) {
            return mOnTouchStateListener.onTouch(this, event);
        } else {
            return super.onTouchEvent(event);
        }
    }

    private void setState(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            setSelected(true);
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            setSelected(false);
        }
    }

    /**
     * Interface definition for a callback to be invoked when a touch event is
     * dispatched to this view. The callback will be invoked before the touch
     * event is given to the view.
     */
    public interface OnTouchStateListener {
        /**
         * Called when a touch event is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v     The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *              the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onTouch(View v, MotionEvent event);
    }
}
