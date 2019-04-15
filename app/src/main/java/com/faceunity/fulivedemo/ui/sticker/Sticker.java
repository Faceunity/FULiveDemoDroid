package com.faceunity.fulivedemo.ui.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.faceunity.fulivedemo.utils.PointUtils;

import java.util.Arrays;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class Sticker {
    // 绘制图片的矩阵
    private Matrix matrix;
    // 原图片
    private Bitmap srcImage;
    // 图片的边界点
    private float[] borders;
    // 点位
    private float[] points;
    // 修正后的landMark 点位
    private float[] landmarkPoints;
    // 类型
    private int type;
    private float[] mBitmapX;
    private float[] mBitmapY;

    Sticker(Bitmap bitmap, float[] points, int type) {
        this.srcImage = bitmap;
        this.matrix = new Matrix();
        this.points = points;
        this.borders = new float[8];
        this.type = type;
        this.mBitmapX = new float[4];
        this.mBitmapY = new float[4];
    }

    /**
     * 绘制图片
     *
     * @param canvas
     */
    void draw(Canvas canvas) {
        canvas.drawBitmap(srcImage, matrix, null);
    }

    /**
     * 获取手势中心点
     *
     * @param event
     */
    PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    /**
     * 获取图片中心点
     */
    PointF getImageMidPoint(Matrix matrix) {
        PointF point = new PointF();
        PointUtils.getBitmapPoints(srcImage, matrix, borders);
        for (int i = 0, p = 0, q = 0, j = borders.length; i < j; i++) {
            if (i % 2 == 0) {
                mBitmapX[p++] = borders[i];
            } else {
                mBitmapY[q++] = borders[i];
            }
        }
        Arrays.sort(mBitmapX);
        Arrays.sort(mBitmapY);
        point.set((mBitmapX[0] + mBitmapX[mBitmapX.length - 1]) / 2, (mBitmapY[0] + mBitmapY[mBitmapY.length - 1]) / 2);
        return point;
    }

    /**
     * 获取手指的旋转角度
     *
     * @param event
     * @return
     */
    float getSpaceRotation(MotionEvent event, PointF imageMidPoint) {
        double deltaX = event.getX(0) - imageMidPoint.x;
        double deltaY = event.getY(0) - imageMidPoint.y;
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }

    /**
     * 【多点缩放】获取手指间的距离
     *
     * @param event
     * @return
     */
    float getMultiTouchDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 【单点缩放】获取手指和图片中心点的距离
     *
     * @param event
     * @return
     */
    float getSingleTouchDistance(MotionEvent event, PointF imageMidPoint) {
        float x = event.getX(0) - imageMidPoint.x;
        float y = event.getY(0) - imageMidPoint.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    RectF getSrcImageBound() {
        RectF dst = new RectF();
        matrix.mapRect(dst, new RectF(0, 0, getStickerWidth(), getStickerHeight()));
        return dst;
    }

    int getStickerWidth() {
        return srcImage == null ? 0 : srcImage.getWidth();
    }

    int getStickerHeight() {
        return srcImage == null ? 0 : srcImage.getHeight();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    Bitmap getSrcImage() {
        return srcImage;
    }

    public float[] getPoints() {
        return points;
    }

    public int getType() {
        return type;
    }

    public float[] getLandmarkPoints() {
        return landmarkPoints;
    }

    public void setLandmarkPoints(float[] landmarkPoints) {
        this.landmarkPoints = landmarkPoints;
    }
}
