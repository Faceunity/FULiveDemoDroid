package com.faceunity.fulivedemo.utils;

/**
 * @author LiuQiang on 2019.01.02
 */
public class ViewUtils {
    // 两次点击按钮之间的点击间隔不能少于500毫秒
    public static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    private ViewUtils() {
    }

    /**
     * 判断按钮是否频繁快速点击
     *
     * @return
     */
    public static boolean isNormalClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            lastClickTime = curClickTime;
            flag = true;
        }
        return flag;
    }
}
