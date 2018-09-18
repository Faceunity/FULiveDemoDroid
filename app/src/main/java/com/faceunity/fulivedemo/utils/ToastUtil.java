package com.faceunity.fulivedemo.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by tujh on 2018/6/28.
 */
public abstract class ToastUtil {

    public static void showToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
