package com.faceunity.fulivedemo.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;

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

    private static float getCross(PointF p1, PointF p2, PointF p) {
        return (p2.x - p1.x) * (p.y - p1.y) - (p.x - p1.x) * (p2.y - p1.y);
    }

    /**
     * 点是否在矩形内
     *
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @param p
     * @return
     */
    public static boolean IsPointInMatrix(PointF p1, PointF p2, PointF p3, PointF p4, PointF p) {
        return getCross(p1, p2, p) * getCross(p3, p4, p) >= 0 && getCross(p2, p3, p) * getCross(p4, p1, p) >= 0;
    }

}
