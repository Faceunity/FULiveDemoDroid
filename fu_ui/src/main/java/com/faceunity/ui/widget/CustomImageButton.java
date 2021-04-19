package com.faceunity.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;


/**
 * DESC：
 * Created on 2021/3/31
 */
public class CustomImageButton extends AppCompatImageButton {
    private boolean isDispatch = false;

    public CustomImageButton(@NonNull Context context) {
        super(context);
    }

    public CustomImageButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDispatch(Boolean isDispatch) {
        this.isDispatch = isDispatch;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (isDispatch) return false;
        return super.dispatchTouchEvent(event);
    }
}
