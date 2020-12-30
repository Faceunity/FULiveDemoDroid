package com.faceunity.fulivedemo.ui.beautybox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.faceunity.fulivedemo.R;

/**
 * @author Richie on 2019.08.09
 */
public class BaseBeautyBox extends LinearLayout implements Checkable {

    protected boolean mIsOpen = false;
    protected boolean mIsChecked = false;
    protected boolean mIsDouble = false;
    protected OnCheckedChangeListener mOnCheckedChangeListener;
    protected OnOpenChangeListener mOnOpenChangeListener;
    protected OnDoubleChangeListener mOnDoubleChangeListener;
    protected int checkedModel;
    protected Drawable drawableOpenNormal;
    protected Drawable drawableOpenChecked;
    protected Drawable drawableCloseNormal;
    protected Drawable drawableCloseChecked;
    protected ImageView boxImg;
    private boolean mBroadcasting;

    public BaseBeautyBox(Context context) {
        this(context, null);
    }

    public BaseBeautyBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseBeautyBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    protected void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        findViews(context);
        obtainStyle(context, attrs, defStyleAttr);
        setOpen(mIsOpen);
    }

    protected void findViews(Context context) {

    }

    protected void obtainStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BeautyBox, defStyleAttr, 0);
        drawableOpenNormal = a.getDrawable(R.styleable.BeautyBox_drawable_open_normal);
        drawableOpenChecked = a.getDrawable(R.styleable.BeautyBox_drawable_open_checked);
        drawableCloseNormal = a.getDrawable(R.styleable.BeautyBox_drawable_close_normal);
        drawableCloseChecked = a.getDrawable(R.styleable.BeautyBox_drawable_close_checked);
        checkedModel = a.getInt(R.styleable.BeautyBox_checked_model, 1);
        boolean checked = a.getBoolean(R.styleable.BeautyBox_checked, false);
        setChecked(checked);
        a.recycle();
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked == checked) {
            return;
        }
        updateView(mIsChecked = checked);

        // Avoid infinite recursions if setChecked() is called from a listener
        if (mBroadcasting) {
            return;
        }

        mBroadcasting = true;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mIsChecked);
        }
        mBroadcasting = false;
    }

    @Override
    public void toggle() {
        if (checkedModel == 1) {
            setChecked(true);
        } else if (checkedModel == 2) {
            if (mIsChecked) {
                setOpen(!mIsOpen);
                if (mOnOpenChangeListener != null) {
                    mOnOpenChangeListener.onOpenChanged(this, mIsOpen);
                }
            } else {
                setChecked(true);
            }
        } else if (checkedModel == 3) {
            if (mIsChecked) {
                mIsDouble = !mIsDouble;
                updateOtherView();
                if (mOnDoubleChangeListener != null) {
                    mOnDoubleChangeListener.onDoubleChanged(this, mIsDouble);
                }
            } else {
                setChecked(true);
            }
        }
    }

    @Override
    public boolean performClick() {
        toggle();
        final boolean handled = super.performClick();
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK);
        }
        return handled;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    public void setOnOpenChangeListener(OnOpenChangeListener onOpenChangeListener) {
        mOnOpenChangeListener = onOpenChangeListener;
    }

    public void setOnDoubleChangeListener(OnDoubleChangeListener onDoubleChangeListener) {
        mOnDoubleChangeListener = onDoubleChangeListener;
    }

    protected void updateOtherView() {

    }

    protected void updateView(boolean checked) {
        updateImg(checked, mIsOpen);
    }

    public void setOpen(boolean open) {
        updateImg(mIsChecked, mIsOpen = open);
    }

    public void updateImg(boolean checked, boolean isOpen) {
        if (isOpen) {
            boxImg.setImageDrawable(checked ? drawableOpenChecked : drawableOpenNormal);
        } else {
            boxImg.setImageDrawable(checked ? drawableCloseChecked : drawableCloseNormal);
        }
    }

    public void setBackgroundImg(int resId) {
        boxImg.setBackgroundResource(resId);
    }

    public void clearBackgroundImg() {
        boxImg.setBackground(null);
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a BeautyBox changed.
     */
    public interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param beautyBox The BeautyBox view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(BaseBeautyBox beautyBox, boolean isChecked);
    }

    public interface OnOpenChangeListener {
        void onOpenChanged(BaseBeautyBox beautyBox, boolean isOpen);
    }

    public interface OnDoubleChangeListener {
        void onDoubleChanged(BaseBeautyBox beautyBox, boolean isDouble);
    }

}
