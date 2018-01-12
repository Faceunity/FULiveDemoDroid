package com.faceunity.fulivedemo.utils;

import android.util.Log;

/**
 * Created by tujh on 2017/12/19.
 */

public abstract class FPSUtil {

    private static long lastOneHundredFrameTimeStamp = 0;
    private static int currentFrameCnt = 0;

    public static void fps(String TAG) {
        if (++currentFrameCnt == 100) {
            currentFrameCnt = 0;
            long tmp = System.nanoTime();
            Log.e(TAG, "FPS TAG : " + (1000.0f * 1000000.0f / ((tmp - lastOneHundredFrameTimeStamp) / 100.0f)));
            lastOneHundredFrameTimeStamp = tmp;
        }
    }

    public static void reset() {
        lastOneHundredFrameTimeStamp = 0;
        currentFrameCnt = 0;
    }
}
