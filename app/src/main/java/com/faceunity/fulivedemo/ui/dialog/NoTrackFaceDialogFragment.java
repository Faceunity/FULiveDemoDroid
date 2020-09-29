package com.faceunity.fulivedemo.ui.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.faceunity.fulivedemo.FUApplication;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;


/**
 * 没有检测到人脸的提示框
 *
 * @author Richie on 2018.08.28
 */
public class NoTrackFaceDialogFragment extends BaseDialogFragment {
    private static final String MESSAGE = "message";
    private OnDismissListener mOnDismissListener;

    public static NoTrackFaceDialogFragment newInstance(@StringRes int res) {
        return newInstance(FUApplication.getContext().getResources().getString(res));
    }

    public static NoTrackFaceDialogFragment newInstance(String message) {
        NoTrackFaceDialogFragment fragment = new NoTrackFaceDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    protected View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_not_track_face, container, false);
        TextView textView = (TextView) view.findViewById(R.id.tv_tip_message);
        String message = getArguments().getString(MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            textView.setText(message);
        }
        view.findViewById(R.id.btn_done).setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                dismiss();
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss();
                }
            }
        });
        setCancelable(false);
        return view;
    }

    @Override
    protected int getDialogWidth() {
        return getResources().getDimensionPixelSize(R.dimen.x490);
    }

    @Override
    protected int getDialogHeight() {
        return getResources().getDimensionPixelSize(R.dimen.x450);
    }

}
