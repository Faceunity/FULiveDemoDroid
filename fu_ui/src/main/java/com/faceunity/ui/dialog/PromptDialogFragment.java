package com.faceunity.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.faceunity.ui.R;
import com.faceunity.ui.listener.OnMultiClickListener;


/**
 * @author Richie on 2020.08.17
 */
public class PromptDialogFragment extends BaseDialogFragment {
    private static final String MESSAGE = "message";
    private Context mContext;

    public static PromptDialogFragment newInstance(Context context, @StringRes int res) {
        return newInstance(context, context.getResources().getString(res));
    }

    public static PromptDialogFragment newInstance(Context context, String message) {
        PromptDialogFragment fragment = new PromptDialogFragment();
        fragment.mContext = context;
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_prompt, container, false);
        TextView tvPrompt = view.findViewById(R.id.tv_content);
        String message = getArguments().getString(MESSAGE);
        if (!TextUtils.isEmpty(message)) {
            tvPrompt.setText(message);
        }
        view.findViewById(R.id.tv_confirm).setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                dismiss();
            }
        });
        return view;
    }


    @Override
    protected int getDialogWidth() {
        return getResources().getDimensionPixelSize(R.dimen.x560);
    }

    @Override
    protected int getDialogHeight() {
        return getResources().getDimensionPixelSize(R.dimen.x274);
    }


}
