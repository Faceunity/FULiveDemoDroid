package com.faceunity.fulivedemo.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Window;
import android.view.WindowInsets;

import java.lang.reflect.Method;

/**
 * 刘海屏适配
 *
 * @author Richie on 2019/7/12.
 */
public final class NotchInScreenUtil {
    private static final String TAG = NotchInScreenUtil.class.getSimpleName();

    public static boolean hasNotch(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return hasNotchInHW(activity) || hasNotchInOppo(activity) || hasNotchInVivo(activity) || hasNotchInXiaomi(activity);
        } else {
            return false;
        }
    }

    /**
     * 判断 Android 9 手机是否为刘海屏手机，官方文档
     *
     * @param window
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public static boolean isNotchScreenInAndroidP(Window window) {
        WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();
        if (windowInsets == null) {
            return false;
        }

        DisplayCutout displayCutout = windowInsets.getDisplayCutout();
        if (displayCutout == null || displayCutout.getBoundingRects() == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断华为手机是否为刘海屏手机
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean hasNotchInHW(Context context) {
        boolean isNotchScreen = false;
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getDeclaredMethod("hasNotchInScreen");
            get.setAccessible(true);
            isNotchScreen = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInHW error");
        }
        return isNotchScreen;
    }

    /**
     * 判断小米手机是否为刘海屏手机
     *
     * @param
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean hasNotchInXiaomi(Context context) {
        return isXiaomi() && 1 == getIntFromSystemProperties("ro.miui.notch", context);
    }

    /**
     * 判断 Oppo 手机是否为刘海屏手机
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean hasNotchInOppo(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }

    /**
     * 判断 Vivo 手机是否为刘海屏手机
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean hasNotchInVivo(Context context) {
        boolean isNotchScreen = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> ftFeature = classLoader.loadClass("android.util.FtFeature");
            Method isFeatureSupport = ftFeature.getMethod("isFeatureSupport", Integer.TYPE);
            isFeatureSupport.setAccessible(true);
            isNotchScreen = (boolean) isFeatureSupport.invoke(ftFeature, 0x00000020);
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInVivo error");
        }
        return isNotchScreen;
    }

    private static int getIntFromSystemProperties(String key, Context context) {
        int result = 0;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class<?> systemProperties = classLoader.loadClass("android.os.SystemProperties");
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = systemProperties.getDeclaredMethod("getInt", paramTypes);
            getInt.setAccessible(true);
            Object[] params = new Object[2];
            params[0] = key;
            params[1] = 0;
            result = (Integer) getInt.invoke(systemProperties, params);
        } catch (Exception e) {
            Log.e(TAG, "getIntFromSystemProperties: ", e);
        }
        return result;
    }

    private static boolean isXiaomi() {
        return "Xiaomi".equalsIgnoreCase(Build.MANUFACTURER);
    }

}
