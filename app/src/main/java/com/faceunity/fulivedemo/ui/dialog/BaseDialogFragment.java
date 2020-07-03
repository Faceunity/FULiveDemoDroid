package com.faceunity.fulivedemo.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.faceunity.fulivedemo.R;

/**
 * @author Richie on 2018.09.19
 */
public abstract class BaseDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View dialogView = createDialogView(inflater, container);
        initWindowParams();
        return dialogView;
    }

    /**
     * 创建 dialog view
     *
     * @param inflater
     * @param container
     * @return
     */
    protected abstract View createDialogView(LayoutInflater inflater, @Nullable ViewGroup container);

    protected int getDialogWidth() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getDialogHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    private void initWindowParams() {
        Dialog dialog = getDialog();
        if (dialog != null) {

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            if (window != null) {
                window.getDecorView().setPadding(0, 0, 0, 0);
                window.setBackgroundDrawableResource(R.color.transparent);
                WindowManager.LayoutParams windowAttributes = window.getAttributes();
                windowAttributes.gravity = Gravity.CENTER;
                windowAttributes.width = getDialogWidth();
                windowAttributes.height = getDialogHeight();
                window.setAttributes(windowAttributes);
            }
        }
    }

    public interface OnClickListener {
        /**
         * 确认
         */
        void onConfirm();

        /**
         * 取消
         */
        void onCancel();
    }

    public interface OnDismissListener {
        /**
         * 消失
         */
        void onDismiss();
    }
}
