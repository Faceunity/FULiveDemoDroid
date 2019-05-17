package com.faceunity.fulivedemo.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public final class PointUtils {

    /**
     * 将matrix的点映射成坐标点
     *
     * @return
     */
    public static void getBitmapPoints(Bitmap bitmap, Matrix matrix, float[] dest) {
        float[] src = new float[]{
                0, 0,
                bitmap.getWidth(), 0,
                0, bitmap.getHeight(),
                bitmap.getWidth(), bitmap.getHeight()
        };
        matrix.mapPoints(dest, src);
    }

    /**
     * 获取映射的点位
     *
     * @param src
     * @param dest
     * @param matrix
     */
    public static void getMappedPoints(float[] src, float[] dest, Matrix matrix) {
        matrix.mapPoints(dest, src);
    }
}
