package com.faceunity.fulivedemo.ui.beautybox;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;

/**
 * 带有文字和 icon 的单选多状态 View
 * <p>
 * Created by tujh on 2018/4/17.
 */
public class BeautyBox extends BaseBeautyBox {
    private String textNormalStr;
    private String textDoubleStr;
    private int textNormalColor;
    private int textCheckedColor;
    private TextView boxText;

    public BeautyBox(Context context) {
        this(context, null);
    }

    public BeautyBox(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyBox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void findViews(Context context) {
        super.findViews(context);
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_box, this);
        boxImg = findViewById(R.id.beauty_box_img);
        boxText = findViewById(R.id.beauty_box_text);
    }

    @Override
    protected void obtainStyle(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BeautyBox, defStyleAttr, 0);
        textNormalStr = a.getString(R.styleable.BeautyBox_text_normal);
        textDoubleStr = a.getString(R.styleable.BeautyBox_text_double);
        if (TextUtils.isEmpty(textDoubleStr)) {
            textDoubleStr = textNormalStr;
        }
        textNormalColor = a.getColor(R.styleable.BeautyBox_textColor_normal, getResources().getColor(R.color.main_color_c5c5c5));
        textCheckedColor = a.getColor(R.styleable.BeautyBox_textColor_checked, getResources().getColor(R.color.main_color));
        boxText.setText(textNormalStr);
        boxText.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));
        a.recycle();
        super.obtainStyle(context, attrs, defStyleAttr);
    }

    @Override
    protected void updateOtherView() {
        super.updateOtherView();
        boxText.setText(mIsDouble ? textDoubleStr : textNormalStr);
    }

    @Override
    protected void updateView(boolean checked) {
        super.updateView(checked);
        boxText.setTextColor(checked ? textCheckedColor : textNormalColor);
    }
}
