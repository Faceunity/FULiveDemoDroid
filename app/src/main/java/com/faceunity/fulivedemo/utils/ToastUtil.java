package com.faceunity.fulivedemo.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.faceunity.fulivedemo.R;

/**
 * Created by tujh on 2018/6/28.
 */
public final class ToastUtil {
    private static Toast sNormalToast;
    private static Toast sFineToast;
    private static Toast sWhiteTextToast;

    public static Toast makeFineToast(Context context, String text, int iconId) {
        View view;
        if (sFineToast == null) {
            FrameLayout frameLayout = new FrameLayout(context);
            view = LayoutInflater.from(context).inflate(R.layout.toast_live_photo, frameLayout, true);
            sFineToast = new Toast(context);
            TextView textView = view.findViewById(R.id.tv_toast_text);
            textView.setText(text);
            ImageView imageView = view.findViewById(R.id.iv_toast_icon);
            imageView.setImageResource(iconId);
            sFineToast.setView(frameLayout);
            sFineToast.setDuration(Toast.LENGTH_SHORT);
        } else {
            view = sFineToast.getView();
        }
        TextView textView = view.findViewById(R.id.tv_toast_text);
        ImageView imageView = view.findViewById(R.id.iv_toast_icon);
        imageView.setImageResource(iconId);
        textView.setText(text);
        int yOffset = context.getResources().getDimensionPixelSize(R.dimen.x570);
        sFineToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, yOffset);
        return sFineToast;
    }

    public static void showToast(Context context, String str) {
        Toast toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showToast(Context context, @StringRes int strId) {
        showToast(context, context.getString(strId));
    }

    public static void showWhiteTextToast(Context context, @StringRes int strId) {
        showWhiteTextToast(context, context.getString(strId));
    }

    public static void showWhiteTextToast(Context context, String text) {
        if (sWhiteTextToast == null) {
            Resources resources = context.getResources();
            TextView textView = new TextView(context);
            textView.setTextColor(resources.getColor(R.color.colorWhite));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.x64));
            textView.setText(text);
            sWhiteTextToast = new Toast(context);
            sWhiteTextToast.setView(textView);
            sWhiteTextToast.setDuration(Toast.LENGTH_SHORT);
            int yOffset = context.getResources().getDimensionPixelSize(R.dimen.x560);
            sWhiteTextToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, yOffset);
            sWhiteTextToast.show();
        } else {
            TextView view = (TextView) sWhiteTextToast.getView();
            view.setText(text);
            if (!view.isShown()) {
                sWhiteTextToast.show();
            }
        }
    }

    public static void showNormalToast(Context context, @StringRes int strId) {
        showNormalToast(context, context.getString(strId));
    }

    public static void showNormalToast(Context context, String text) {
        if (sNormalToast == null) {
            context = context.getApplicationContext();
            Resources resources = context.getResources();
            TextView textView = new TextView(context);
            textView.setTextColor(resources.getColor(R.color.colorWhite));
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.x26));
            textView.setBackgroundResource(R.drawable.more_toast_background);
            int hPadding = resources.getDimensionPixelSize(R.dimen.x28);
            int vPadding = resources.getDimensionPixelSize(R.dimen.x16);
            textView.setPadding(hPadding, vPadding, hPadding, vPadding);
            textView.setText(text);
            sNormalToast = new Toast(context);
            sNormalToast.setView(textView);
            sNormalToast.setDuration(Toast.LENGTH_SHORT);
            int yOffset = context.getResources().getDimensionPixelSize(R.dimen.x582);
            sNormalToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, yOffset);
            sNormalToast.show();
        } else {
            TextView textView = (TextView) sNormalToast.getView();
            textView.setText(text);
            if (!textView.isShown()) {
                sNormalToast.show();
            }
        }
    }

}
