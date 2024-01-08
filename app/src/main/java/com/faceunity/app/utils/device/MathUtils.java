package com.faceunity.app.utils.device;

/**
 * DESC：
 * Created on 2021/3/12
 */
public class MathUtils {
    public static double getScore(double num, double baseNum, double intervalScore, double intervalNum) {
        return (baseNum - num) * intervalScore / intervalNum;
    }
}
