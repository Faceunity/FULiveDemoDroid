package com.faceunity.fulivedemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

/**
 * Created by tujh on 2018/7/18.
 */
public final class ScreenUtils {

    private static final String TAG_FAKE_STATUS_BAR_VIEW = "statusBarView";
    private static final String TAG_MARGIN_ADDED = "marginAdded";

    public static void fullScreen(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            //                window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            //                attributes.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            window.setAttributes(attributes);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // navigation bar
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Return the navigation bar's height.
     *
     * @return the navigation bar's height
     */
    public static int getNavBarHeight() {
        Resources res = Resources.getSystem();
        int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId != 0) {
            return res.getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * Set the navigation bar's visibility.
     *
     * @param activity  The activity.
     * @param isVisible True to set navigation bar visible, false otherwise.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setNavBarVisibility(@NonNull final Activity activity, boolean isVisible) {
        setNavBarVisibility(activity.getWindow(), isVisible);
    }

    /**
     * Set the navigation bar's visibility.
     *
     * @param window    The window.
     * @param isVisible True to set navigation bar visible, false otherwise.
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static void setNavBarVisibility(@NonNull final Window window, boolean isVisible) {
        final int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        final View decorView = window.getDecorView();
        if (isVisible) {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() & ~uiOptions);
        } else {
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | uiOptions);
        }
    }

    /**
     * Return whether the navigation bar visible.
     *
     * @param activity The activity.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(@NonNull final Activity activity) {
        return isNavBarVisible(activity.getWindow());
    }

    /**
     * Return whether the navigation bar visible.
     *
     * @param window The window.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isNavBarVisible(@NonNull final Window window) {
        View decorView = window.getDecorView();
        int visibility = decorView.getSystemUiVisibility();
        return (visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0;
    }

    /**
     * 获取 APP 可用的高度，屏幕高度 - 导航栏高度
     *
     * @param context
     * @return
     */
    public static int getAppScreenHeight(Context context) {
        int navBarHeight = getNavBarHeight();
        int screenHeight = getScreenInfo(context).heightPixels;
        return screenHeight - navBarHeight;
    }

    /**
     * 获取屏幕信息
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getScreenInfo(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * 设置状态栏的颜色
     * reference: https://github.com/niorgai/StatusBarCompat
     *
     * @param activity
     * @param statusColor
     */
    public static void setStatusBarColor(Activity activity, int statusColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColorAbove21(activity, statusColor);
        } else {
            StatusBarCompatKitKat.setStatusBarColor(activity, statusColor);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void setStatusBarColorAbove21(Activity activity, int statusColor) {
        Window window = activity.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusColor);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            mChildView.setFitsSystemWindows(false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    static class StatusBarCompatKitKat {

        private static final String TAG_FAKE_STATUS_BAR_VIEW = "statusBarView";
        private static final String TAG_MARGIN_ADDED = "marginAdded";

        /**
         * return statusBar's Height in pixels
         */
        private static int getStatusBarHeight(Context context) {
            int result = 0;
            int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resId > 0) {
                result = context.getResources().getDimensionPixelOffset(resId);
            }
            return result;
        }

        /**
         * 1. Add fake statusBarView.
         * 2. set tag to statusBarView.
         */
        private static View addFakeStatusBarView(Activity activity, int statusBarColor, int statusBarHeight) {
            Window window = activity.getWindow();
            ViewGroup mDecorView = (ViewGroup) window.getDecorView();

            View mStatusBarView = new View(activity);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, statusBarHeight);
            layoutParams.gravity = Gravity.TOP;
            mStatusBarView.setLayoutParams(layoutParams);
            mStatusBarView.setBackgroundColor(statusBarColor);
            mStatusBarView.setTag(TAG_FAKE_STATUS_BAR_VIEW);

            mDecorView.addView(mStatusBarView);
            return mStatusBarView;
        }

        /**
         * use reserved order to remove is more quickly.
         */
        private static void removeFakeStatusBarViewIfExist(Activity activity) {
            Window window = activity.getWindow();
            ViewGroup mDecorView = (ViewGroup) window.getDecorView();

            View fakeView = mDecorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
            if (fakeView != null) {
                mDecorView.removeView(fakeView);
            }
        }

        /**
         * add marginTop to simulate set FitsSystemWindow true
         */
        private static void addMarginTopToContentChild(View mContentChild, int statusBarHeight) {
            if (mContentChild == null) {
                return;
            }
            if (!TAG_MARGIN_ADDED.equals(mContentChild.getTag())) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
                lp.topMargin += statusBarHeight;
                mContentChild.setLayoutParams(lp);
                mContentChild.setTag(TAG_MARGIN_ADDED);
            }
        }

        /**
         * remove marginTop to simulate set FitsSystemWindow false
         */
        private static void removeMarginTopOfContentChild(View mContentChild, int statusBarHeight) {
            if (mContentChild == null) {
                return;
            }
            if (TAG_MARGIN_ADDED.equals(mContentChild.getTag())) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
                lp.topMargin -= statusBarHeight;
                mContentChild.setLayoutParams(lp);
                mContentChild.setTag(null);
            }
        }

        /**
         * set StatusBarColor
         * <p>
         * 1. set Window Flag : WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
         * 2. removeFakeStatusBarViewIfExist
         * 3. addFakeStatusBarView
         * 4. addMarginTopToContentChild
         * 5. cancel ContentChild's fitsSystemWindow
         */
        static void setStatusBarColor(Activity activity, int statusColor) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mContentChild = mContentView.getChildAt(0);
            int statusBarHeight = getStatusBarHeight(activity);

            removeFakeStatusBarViewIfExist(activity);
            addFakeStatusBarView(activity, statusColor, statusBarHeight);
            addMarginTopToContentChild(mContentChild, statusBarHeight);

            if (mContentChild != null) {
                ViewCompat.setFitsSystemWindows(mContentChild, false);
            }
        }

        /**
         * translucentStatusBar
         * <p>
         * 1. set Window Flag : WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
         * 2. removeFakeStatusBarViewIfExist
         * 3. removeMarginTopOfContentChild
         * 4. cancel ContentChild's fitsSystemWindow
         */
        static void translucentStatusBar(Activity activity) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mContentChild = mContentView.getChildAt(0);

            removeFakeStatusBarViewIfExist(activity);
            removeMarginTopOfContentChild(mContentChild, getStatusBarHeight(activity));
            if (mContentChild != null) {
                ViewCompat.setFitsSystemWindows(mContentChild, false);
            }
        }
    }
}
