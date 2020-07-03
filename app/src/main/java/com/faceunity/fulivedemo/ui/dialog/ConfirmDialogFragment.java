package com.faceunity.fulivedemo.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.faceunity.fulivedemo.R;


/**
 * 确认对话框
 *
 * @author Richie on 2018.08.28
 */
public class ConfirmDialogFragment extends BaseDialogFragment {
    private static final String TITLE = "content";
    private static final String CONFIRM = "confirm";
    private static final String CANCEL = "cancel";
    private OnClickListener mOnClickListener;

    /**
     * 创建对话框
     *
     * @param title
     * @param onClickListener
     * @return
     */
    public static ConfirmDialogFragment newInstance(@NonNull String title, @NonNull OnClickListener onClickListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.mOnClickListener = onClickListener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ConfirmDialogFragment newInstance(@NonNull String title, @NonNull String confirmText,
                                                    @NonNull String cancelText, @NonNull OnClickListener onClickListener) {
        ConfirmDialogFragment fragment = new ConfirmDialogFragment();
        fragment.mOnClickListener = onClickListener;
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, title);
        bundle.putString(CONFIRM, confirmText);
        bundle.putString(CANCEL, cancelText);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_confirm, container, false);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                int id = v.getId();
                if (id == R.id.tv_confirm) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onConfirm();
                    }
                } else if (id == R.id.tv_cancel) {
                    if (mOnClickListener != null) {
                        mOnClickListener.onCancel();
                    }
                }
            }
        };
        String confirmTxt = getArguments().getString(CONFIRM);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        if (!TextUtils.isEmpty(confirmTxt)) {
            tvConfirm.setText(confirmTxt);
        }
        String cancelTxt = getArguments().getString(CANCEL);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        if (!TextUtils.isEmpty(cancelTxt)) {
            tvCancel.setText(cancelTxt);
        }
        tvConfirm.setOnClickListener(onClickListener);
        tvCancel.setOnClickListener(onClickListener);
        String title = getArguments().getString(TITLE);
        ((TextView) view.findViewById(R.id.tv_content)).setText(title);
        return view;
    }

    @Override
    protected int getDialogWidth() {
        return getResources().getDimensionPixelSize(R.dimen.x562);
    }

    @Override
    protected int getDialogHeight() {
        return getResources().getDimensionPixelSize(R.dimen.x294);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

}
