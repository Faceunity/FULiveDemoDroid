package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faceunity.fulivedemo.R;

/**
 * Created by tujh on 2018/4/17.
 */
public class BeautyBox extends LinearLayout implements Checkable {

    private boolean isSelect;
    private boolean mChecked;

    private boolean mBroadcasting;
    private OnCheckedChangeListener mOnCheckedChangeListener;

    private int checkedModel;
    private Drawable drawableNormal;
    private Drawable drawableChecked;

    private String textNormalStr;
    private String textCheckedStr;

    private int textNormalColor;
    private int textCheckedColor;

    private ImageView boxImg;
    private TextView boxText;

    public BeautyBox(Context context) {
        this(context, null);
    }

    public BeautyBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_beauty_box, this);

        boxImg = (ImageView) findViewById(R.id.beauty_box_img);
        boxText = (TextView) findViewById(R.id.beauty_box_text);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.BeautyBox, defStyleAttr, 0);

        drawableNormal = a.getDrawable(R.styleable.BeautyBox_drawable_normal);
        drawableChecked = a.getDrawable(R.styleable.BeautyBox_drawable_checked);

        textNormalStr = a.getString(R.styleable.BeautyBox_text_normal);
        textCheckedStr = a.getString(R.styleable.BeautyBox_text_checked);
        if (TextUtils.isEmpty(textCheckedStr))
            textCheckedStr = textNormalStr;

        textNormalColor = a.getColor(R.styleable.BeautyBox_textColor_normal, getResources().getColor(R.color.main_color_c5c5c5));
        textCheckedColor = a.getColor(R.styleable.BeautyBox_textColor_checked, getResources().getColor(R.color.main_color));

        final boolean checked = a.getBoolean(R.styleable.BeautyBox_checked, false);

        checkedModel = a.getInt(R.styleable.BeautyBox_checked_model, 1);

        boxText.setText(textNormalStr);
        boxText.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));
        boxImg.setImageDrawable(drawableNormal);

        setChecked(checked);

        a.recycle();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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

    @Override
    public void setChecked(boolean checked) {
        updateView(mChecked = checked);

        // Avoid infinite recursions if setChecked() is called from a listener
        if (mBroadcasting) {
            return;
        }

        mBroadcasting = true;
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mChecked);
        }

        mBroadcasting = false;
    }

    public void updateView(boolean checked) {
        boxImg.setImageDrawable(checked ? drawableChecked : drawableNormal);
        boxText.setText(checked ? textCheckedStr : textNormalStr);
        boxText.setTextColor(checked ? textCheckedColor : textNormalColor);
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        if (checkedModel == 1) setChecked(!isSelect && mChecked ? mChecked : !mChecked);
        else if (checkedModel == 2) setChecked(!mChecked);
        else if (checkedModel == 3) setChecked(mChecked);
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public void setBackgroundImg(int resId) {
        boxImg.setBackgroundResource(resId);
    }

    public void clearBackgroundImg() {
        boxImg.setBackground(null);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener mOnCheckedChangeListener) {
        this.mOnCheckedChangeListener = mOnCheckedChangeListener;
    }

    /**
     * Interface definition for a callback to be invoked when the checked state
     * of a BeautyBox changed.
     */
    public static interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param beautyBox The BeautyBox view whose state has changed.
         * @param isChecked The new checked state of buttonView.
         */
        void onCheckedChanged(BeautyBox beautyBox, boolean isChecked);
    }
}
