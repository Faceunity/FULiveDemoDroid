package com.faceunity.fulivedemo.utils;

/**
 * 数值工具类
 *
 * @author Richie on 2019.07.05
 */
public class DecimalUtils {
    /**
     * 两个浮点数的差值小于 0.000001 认为相等
     */
    private static final float THRESHOLD = 1e-6f;

    private DecimalUtils() {
    }

    public static boolean floatEquals(float a, float b) {
        return Math.abs(a - b) < THRESHOLD;
    }

}
