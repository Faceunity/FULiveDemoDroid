package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import java.util.Arrays;

/**
 * @author Richie on 2020.08.21
 */
public class GestureTouchHandler {
    private static final String TAG = "GestureTouchHandler";
    // 最大和最小的缩放范围
    private static final float MIN_SCALE = 0.25f;
    private static final float MAX_SCALE = 4.0f;
    private static final boolean DEBUG = false;
    private float downX;
    private float downY;
    // 手指按下时图片的矩阵
    private Matrix downMatrix = new Matrix();
    // 手指移动时图片的矩阵
    private Matrix moveMatrix = new Matrix();
    // 多点触屏时的中心点
    private PointF mMiddlePoint = new PointF();
    // 图像顶点坐标
    private float[] mResultPoints = new float[8];
    private Matrix mResultMatrix;
    private int mTouchSlop;
    private int mMode;
    private int mWidth;
    private int mHeight;
    private OnTouchResultListener mOnTouchResultListener;
    private float[] mIdentityPoints;
    // 手指之间的初始距离
    private float mTouchDistance;
    // 原始图像的像素尺寸
    private float mIdentitySize;

    public GestureTouchHandler(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mResultMatrix = new Matrix();
    }

    public void setOnTouchResultListener(OnTouchResultListener onTouchResultListener) {
        mOnTouchResultListener = onTouchResultListener;
    }

    public void setViewSize(int width, int height) {
        Log.d(TAG, "setViewSize() width = [" + width + "], height = [" + height + "]");
        mWidth = width;
        mHeight = height;
        mIdentityPoints = new float[]{
                0, 0,
                width, 0,
                0, height,
                width, height
        };
        mIdentitySize = getScaledRectSize();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        boolean handleResult = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                downMatrix.set(mResultMatrix);
                mMode = ActionMode.TRANS;
                handleResult = true;
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                // 多点触控
                mMode = ActionMode.ZOOM_MULTI;
                mTouchDistance = getMultiTouchDistance(event);
                mMiddlePoint = getMiddleTouchPoint(event);
                downMatrix.set(mResultMatrix);
                handleResult = true;
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                // 检测是否为点击，根据 touchSlop 判断
                float x = event.getX() - downX;
                float y = event.getY() - downY;
                float distance = (float) Math.sqrt(x * x + y * y);
                if (distance < mTouchSlop) {
                    // 认为是点击
                    if (DEBUG) {
                        Log.i(TAG, "onTouchEvent: 点击操作 " + mTouchSlop);
                    }
                    if (mOnTouchResultListener != null) {
                        mOnTouchResultListener.onClick();
                    }
                } else {
                    // 平移
                    if (mMode == ActionMode.TRANS) {
                        moveMatrix.set(downMatrix);
                        float dx = event.getX() - downX;
                        float dy = event.getY() - downY;
                        moveMatrix.postTranslate(dx, dy);
                        mResultMatrix.set(moveMatrix);
                        mapPoints(moveMatrix, mResultPoints);
                        float x1 = mResultPoints[0] / mWidth;
                        float y1 = mResultPoints[1] / mHeight;
                        float x2 = mResultPoints[2] / mWidth;
                        float y2 = mResultPoints[5] / mHeight;
                        if (mOnTouchResultListener != null) {
                            mOnTouchResultListener.onTransform(x1, y1, x2, y2);
                        }
                        if (DEBUG) {
                            Log.d(TAG, "平移操作 dx:" + (int) dx + ", dy:" + (int) dy
                                    + ", move mtx:" + moveMatrix + ". points:" + Arrays.toString(mResultPoints));
                            Log.i(TAG, "onTouchEvent: x1 " + x1 + ", y1 " + y1 + ", x2:" + x2
                                    + ", y2:" + y2);
                        }
                    } else if (mMode == ActionMode.ZOOM_MULTI) {
                        // 多点缩放
                        moveMatrix.set(downMatrix);
                        float scale = getMultiTouchDistance(event) / mTouchDistance;
                        float scaledBitmapSize = getScaledRectSize();
                        float currScale = scaledBitmapSize / mIdentitySize;
                        // 限制缩放比例
                        if (currScale > MAX_SCALE && scale > 1 || currScale < MIN_SCALE && scale < 1) {
                            return false;
                        }
                        moveMatrix.postScale(scale, scale, mMiddlePoint.x, mMiddlePoint.y);
                        mResultMatrix.set(moveMatrix);

                        float w = mWidth;
                        float h = mHeight;
                        float x1 = mResultPoints[0] / w;
                        float y1 = mResultPoints[1] / h;
                        float x2 = mResultPoints[2] / w;
                        float y2 = mResultPoints[5] / h;
                        if (mOnTouchResultListener != null) {
                            mOnTouchResultListener.onTransform(x1, y1, x2, y2);
                        }
                        if (DEBUG) {
                            Log.d(TAG, "多点缩放操作 " + scale);
                            Log.i(TAG, "onTouchEvent: x1 " + x1 + ", y1 " + y1 + ", x2:" + x2 + ", y2:" + y2);
                        }
                    }
                }
                handleResult = true;
            }
            break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
                mMode = ActionMode.NONE;
                mMiddlePoint = null;
                handleResult = true;
            }
            break;
            default:
        }
        return handleResult;
    }

    /**
     * 缩放后图片顶点坐标
     *
     * @return
     */
    private float getScaledRectSize() {
        mResultMatrix.mapPoints(mResultPoints, mIdentityPoints);
        float x1 = mResultPoints[0];
        float y1 = mResultPoints[1];
        float x2 = mResultPoints[2];
        float y2 = mResultPoints[3];
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 将 matrix 的点映射成坐标点
     *
     * @return
     */
    private void mapPoints(Matrix matrix, float[] dest) {
        matrix.mapPoints(dest, mIdentityPoints);
    }

    /**
     * 多点缩放，获取手指间的距离
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
     * 获取手势中心点
     *
     * @param event
     */
    public static PointF getMiddleTouchPoint(MotionEvent event) {
        PointF point = new PointF();
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
        return point;
    }

    public static class ActionMode {
        public static final int NONE = 0; // 无模式
        public static final int TRANS = 1; // 拖拽模式
        public static final int ROTATE_AND_ZOOM = 2; // 单点旋转加缩放模式
        public static final int ZOOM_MULTI = 3; // 多点缩放模式
    }

    public interface OnTouchResultListener {
        /**
         * 点位变换
         *
         * @param x1
         * @param y1
         * @param x2
         * @param y2
         */
        void onTransform(float x1, float y1, float x2, float y2);

        /**
         * 单击事件
         */
        void onClick();
    }

}
