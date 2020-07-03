package com.faceunity.fulivedemo.utils;

import android.os.SystemClock;

/**
 * 帧率限制
 *
 * @author Richie on 2019.04.13
 */
public final class LimitFpsUtil {
    private static final String TAG = "LimitFpsUtil";
    public static final int DEFAULT_FPS = 30;
    private static long frameStartTimeMs;
    private static long startTimeMs;
    private static long expectedFrameTimeMs = 1000 / DEFAULT_FPS;

    private LimitFpsUtil() {
    }

    public static void setTargetFps(int fps) {
        expectedFrameTimeMs = fps > 0 ? 1000 / fps : 0;
        frameStartTimeMs = 0;
        startTimeMs = 0;
    }

    public static void limitFrameRate() {
        long elapsedFrameTimeMs = SystemClock.elapsedRealtime() - frameStartTimeMs;
        long timeToSleepMs = expectedFrameTimeMs - elapsedFrameTimeMs;
        if (timeToSleepMs > 0) {
            SystemClock.sleep(timeToSleepMs);
        }
//        Log.v(TAG, "limitFrameRate: elapsed:" + elapsedFrameTimeMs + ", expected:" + expectedFrameTimeMs + ", sleep:" + timeToSleepMs);
        frameStartTimeMs = SystemClock.elapsedRealtime();
    }

    public static double averageFrameRate(int frames) {
        long elapsedRealtimeMs = SystemClock.elapsedRealtime();
        long elapsedMilliSeconds = elapsedRealtimeMs - startTimeMs;
        double fps = (double) frames * 1000 / elapsedMilliSeconds;
        startTimeMs = SystemClock.elapsedRealtime();
//        Log.d(TAG, "averageFrameRate: " + String.format("%.2f", fps));
        return fps;
    }

}
