package com.faceunity.fulivedemo.utils;

import android.util.Log;

/**
 * FPS工具类
 * Created by tujh on 2018/5/24.
 */
public class FPSUtil {
    private static final String TAG = FPSUtil.class.getSimpleName();
    private static final int NANO_IN_ONE_MILLI_SECOND = 1000000;

    private static long mLastFrameTimeStamp = 0;

    /**
     * 每帧都计算
     *
     * @return
     */
    public static double fps() {
        double fps = 0;
        long tmp = System.nanoTime();
        fps = 1000.0f * NANO_IN_ONE_MILLI_SECOND / (tmp - mLastFrameTimeStamp);
        mLastFrameTimeStamp = tmp;
        Log.e(TAG, "FPS : " + fps);
        return fps;
    }

    private static long mStartTime = 0;
    private static int mFPSFrameRate = 0;

    /**
     * 平均值
     *
     * @return
     */
    public static double fpsAVG(int time) {
        if (mStartTime == 0) {
            mFPSFrameRate = 0;
            mStartTime = System.nanoTime();
            return 0;
        }
        mFPSFrameRate += time;
        double fps = 0;
        fps = 1000.0f * NANO_IN_ONE_MILLI_SECOND * mFPSFrameRate / (System.nanoTime() - mStartTime);
//        Log.e(TAG, "FPS : " + fps);
        return fps;
    }

    public static void resetAVG() {
        mFPSFrameRate = 0;
        mStartTime = 0;
    }

    private long mLimitMinTime = 33333333;
    private long mLimitStartTime;
    private int mLimitFrameRate;

    public void setLimitMinTime(long limitMinTime) {
        mLimitMinTime = limitMinTime;
    }

    public void limit() {
        try {
            if (mLimitFrameRate == 0 || mLimitFrameRate > 600000) {
                mLimitStartTime = System.nanoTime();
                mLimitFrameRate = 0;
            }
            long sleepTime = mLimitMinTime * mLimitFrameRate++ - (System.nanoTime() - mLimitStartTime);
            if (sleepTime > 0) {
                Thread.sleep(sleepTime / NANO_IN_ONE_MILLI_SECOND, (int) (sleepTime % NANO_IN_ONE_MILLI_SECOND));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resetLimit() {
        mLimitStartTime = 0;
        mLimitFrameRate = 0;
    }
}
