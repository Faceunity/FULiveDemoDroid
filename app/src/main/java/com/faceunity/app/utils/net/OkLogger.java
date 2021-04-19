package com.faceunity.app.utils.net;

import android.util.Log;

/**
 * OkHttp 日志的工具类
 *
 * @author Richie on 2018.12.31
 */
public final class OkLogger {
    private static boolean isEnable = false;
    private static String tag = "OkHttpUtils";

    public static void debug(boolean isEnable) {
        debug(tag, isEnable);
    }

    public static void debug(String tag, boolean isEnable) {
        OkLogger.tag = tag;
        OkLogger.isEnable = isEnable;
    }

    public static void v(String msg) {
        v(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isEnable) {
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        d(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isEnable) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isEnable) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        w(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isEnable) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        e(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isEnable) {
            Log.e(tag, msg);
        }
    }

    public static void printStackTrace(Throwable t) {
        if (isEnable && t != null) {
            Log.e(tag, "OkHttp error", t);
        }
    }

    public static void log(int level, String message) {
        switch (level) {
            case Log.VERBOSE:
                v(message);
                break;
            case Log.DEBUG:
                d(message);
                break;
            case Log.INFO:
                i(message);
                break;
            case Log.WARN:
                w(message);
                break;
            case Log.ERROR:
                e(message);
                break;
            default:
                v(message);
        }
    }
}
