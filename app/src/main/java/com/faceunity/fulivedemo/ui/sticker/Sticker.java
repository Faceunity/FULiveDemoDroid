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
    private String imagePath;
    // 图片的边界点
    private float[] borderVertex;
    // 五官点位，左上角为顶点
    private float[] points;
    // 修正后的 landmark 点位
    private float[] landmarkPoints;
    // 类型
    private int type;
    private float[] mBitmapX;
    private float[] mBitmapY;

    Sticker(String imagePath, Bitmap bitmap, float[] points, int type, float[] matrixF) {
        this.imagePath = imagePath;
        this.srcImage = bitmap;
        this.matrix = new Matrix();
        if (matrixF != null) {
            this.matrix.setValues(matrixF);
        }
        this.points = points;
        this.borderVertex = new float[]{0, 0,
                bitmap.getWidth(), 0,
                0, bitmap.getHeight(),
                bitmap.getWidth(), bitmap.getHeight()};
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
     * 获取图片中心点
     */
    PointF getImageMidPoint(Matrix matrix) {
        PointF point = new PointF();
        float[] border = new float[8];
        PointUtils.getBitmapPoints(srcImage, matrix, border);
        for (int i = 0, p = 0, q = 0, j = border.length; i < j; i++) {
            if (i % 2 == 0) {
                mBitmapX[p++] = border[i];
            } else {
                mBitmapY[q++] = border[i];
            }
        }
        Arrays.sort(mBitmapX);
        Arrays.sort(mBitmapY);
        point.set((mBitmapX[0] + mBitmapX[mBitmapX.length - 1]) / 2, (mBitmapY[0] + mBitmapY[mBitmapY.length - 1]) / 2);
        return point;
    }

    /**
     * 获取手势中心点
     *
     * @param event
     */
    public static PointF getMidPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    /**
     * 获取手指的旋转角度
     *
     * @param event
     * @return
     */
    public static float getSpaceRotation(MotionEvent event, PointF imageMidPoint) {
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
    public static float getMultiTouchDistance(MotionEvent event) {
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
    public static float getSingleTouchDistance(MotionEvent event, PointF imageMidPoint) {
        float x = event.getX(0) - imageMidPoint.x;
        float y = event.getY(0) - imageMidPoint.y;
        return (float) Math.sqrt(x * x + y * y);
    }

    RectF getSrcImageBound(int halfSize) {
        RectF dst = new RectF();
        matrix.mapRect(dst, new RectF(0 + halfSize, 0 + halfSize, getStickerWidth() - halfSize,
                getStickerHeight() - halfSize));
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

    public void setPoints(float[] points) {
        this.points = points;
    }

    public int getType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float[] getBorderVertex() {
        return borderVertex;
    }

    public float[] getLandmarkPoints() {
        return landmarkPoints;
    }

    public void setLandmarkPoints(float[] landmarkPoints) {
        this.landmarkPoints = landmarkPoints;
    }
}
