package com.faceunity.fulivedemo.ui.sticker;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.PointUtils;
import com.faceunity.utils.BitmapUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class StickerView extends AppCompatImageView {
    private static final String TAG = "StickerView";
    static final boolean DEBUG = false;
    private static final boolean SHOW_POINTS = false;

    // 最大和最小的缩放范围
    private static final float MIN_SCALE = 0.33f;
    private static final float MAX_SCALE = 3.0f;
    private static final int PREFER_TRANSLATION = 80;
    // 被操作的贴纸对象
    private Sticker sticker;
    // 手指按下时图片的矩阵
    private Matrix downMatrix = new Matrix();
    // 手指移动时图片的矩阵
    private Matrix moveMatrix = new Matrix();
    // 多点触屏时的中心点
    private PointF midPoint = new PointF();
    // 图片的中心点坐标
    private PointF imageMidPoint = new PointF();
    // 旋转操作图片
    private StickerActionIcon rotateIcon;
    // 缩放操作图片
    private StickerActionIcon increaseIcon;
    // 缩放操作图片
    private StickerActionIcon removeIcon;
    // 绘制图片的边框
    private Paint paintEdge;
    private boolean inRotationScale;
    private boolean inTranslation;
    // 绘制点
    private Paint paintPoint;
    //     点的半径
    private int mPointRadius;
    // 触控模式
    private int mode;
    // 是否正在处于编辑
    private boolean isEdit = true;
    // 贴纸的操作监听
    private OnStickerActionListener listener;
    // 映射的点位坐标
    private float[] mappedPoints;
    private float[] mappedRectVertex;
    // 图像顶点坐标
    private float[] bitmapPoints = new float[8];
    // 原始图像的像素尺寸
    private float originBmpSize;
    // 水平校验
    private float horizontalCheck;
    // 手指按下屏幕的X坐标
    private float downX;
    // 手指按下屏幕的Y坐标
    private float downY;
    // 手指之间的初始距离
    private float oldDistance;
    // 手指之间的初始角度
    private float oldRotation;
    // 点击加号或者删除
    private boolean isChangedOption = false;
    // 单点缩放比例
    private float mSingleScale = 1;
    private int mRotationExtraSize;
    private static final int MIN_CLICK_DURATION = 300;
    private int mTouchSlop;
    private long mCurrentClickTime;
    private boolean mIsRandom = true;
    private int mIconHotArea;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setOnStickerActionListener(OnStickerActionListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        setScaleType(ScaleType.MATRIX);
        rotateIcon = new StickerActionIcon(context);
        increaseIcon = new StickerActionIcon(context);
        removeIcon = new StickerActionIcon(context);
        paintEdge = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintEdge.setColor(Color.WHITE);
        paintEdge.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.x4));
        // 60% black
        paintEdge.setShadowLayer(getResources().getDimensionPixelSize(R.dimen.x2), 0, 0, Color.parseColor("#B3000000"));
        horizontalCheck = getResources().getDimensionPixelSize(R.dimen.x2);
        mRotationExtraSize = getResources().getDimensionPixelSize(R.dimen.x10);
        mIconHotArea = getContext().getResources().getDimensionPixelSize(R.dimen.x72) / 2;
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        if (SHOW_POINTS) {
            paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintPoint.setColor(Color.RED);
            paintPoint.setStyle(Paint.Style.FILL);
            mPointRadius = getResources().getDimensionPixelSize(R.dimen.x4);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 随机移动到某个位置
        // TODO: 2019/4/19 0019 某些手机航栏收起，触发 onLayout
        if (changed) {
            if (mIsRandom) {
                int xDis = (getWidth() - sticker.getStickerWidth()) / 2 - PREFER_TRANSLATION;
                int yDis = (getHeight() - sticker.getStickerHeight()) / 2 - PREFER_TRANSLATION;
                int ranX = (int) ((Math.random() * 2 - 1) * xDis);
                int ranY = (int) ((Math.random() * 2 - 1) * yDis);
                sticker.getMatrix().postScale(0.5f, 0.5f);
                sticker.getMatrix().postTranslate((float) (getWidth() - sticker.getStickerWidth()) / 2 + ranX,
                        (float) (getHeight() - sticker.getStickerHeight()) / 2 + ranY);
            }
            originBmpSize = getScaledBitmapSize();
        }
        if (DEBUG) {
            Log.i(TAG, "onLayout: " + left + ":" + top + ":" + right + ":" + bottom + ", changed " + changed);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (DEBUG) {
            Log.i(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "]");
        }
    }

    /**
     * 缩放后图片顶点坐标
     *
     * @return
     */
    private float getScaledBitmapSize() {
        PointUtils.getBitmapPoints(sticker.getSrcImage(), sticker.getMatrix(), bitmapPoints);
        float x1 = bitmapPoints[0];
        float y1 = bitmapPoints[1];
        float x2 = bitmapPoints[2];
        float y2 = bitmapPoints[3];
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (sticker == null) {
            return;
        }

        sticker.draw(canvas);
        PointUtils.getBitmapPoints(sticker.getSrcImage(), sticker.getMatrix(), bitmapPoints);
        float x1 = bitmapPoints[0];
        float y1 = bitmapPoints[1];
        float x2 = bitmapPoints[2];
        float y2 = bitmapPoints[3];
        float x3 = bitmapPoints[4];
        float y3 = bitmapPoints[5];
        float x4 = bitmapPoints[6];
        float y4 = bitmapPoints[7];
        if (isEdit) {
            // 画边框
            canvas.drawLine(x1, y1, x2, y2, paintEdge);
            canvas.drawLine(x2, y2, x4, y4, paintEdge);
            canvas.drawLine(x4, y4, x3, y3, paintEdge);
            canvas.drawLine(x3, y3, x1, y1, paintEdge);
            // 画操作按钮图片
            if (!inTranslation && !inRotationScale) {
                removeIcon.draw(canvas, x1, y1);
                increaseIcon.draw(canvas, x2, y2);
                rotateIcon.setAlpha(255); // 100%
                rotateIcon.setEnlargedSize(0);
                rotateIcon.draw(canvas, x4, y4);
            } else if (inRotationScale) {
                rotateIcon.setAlpha(178); // 70%
                rotateIcon.setEnlargedSize(mRotationExtraSize);
                rotateIcon.draw(canvas, x4, y4);
            }
        }
        // 映射点位坐标
        if (SHOW_POINTS) {
            sticker.getMatrix().mapPoints(mappedPoints, sticker.getPoints());
            for (int i = 0, j = mappedPoints.length; i < j; i += 2) {
                canvas.drawCircle(mappedPoints[i], mappedPoints[i + 1], mPointRadius, paintPoint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        boolean handleResult = true;
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                downY = event.getY();
                if (sticker == null) {
                    return false;
                }
                handleResult = handleDownEvent(event);
            }
            break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                // 多点触控
                mode = ActionMode.ZOOM_MULTI;
                oldDistance = Sticker.getMultiTouchDistance(event);
                midPoint = Sticker.getMidPoint(event);
                downMatrix.set(sticker.getMatrix());
                isEdit = true;
                invalidate();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                // 检测是否为点击，根据 touchSlop 判断
                float x = event.getX() - downX;
                float y = event.getY() - downY;
                float distance = (float) Math.sqrt(x * x + y * y);
                if (distance < mTouchSlop) {
                    // 认为是点击
                    handleResult = handleDownEvent(event);
                } else {
                    // 平移
                    if (mode == ActionMode.TRANS) {
                        moveMatrix.set(downMatrix);
                        float dx = event.getX() - downX;
                        float dy = event.getY() - downY;
                        moveMatrix.postTranslate(dx, dy);
                        PointUtils.getBitmapPoints(sticker.getSrcImage(), moveMatrix, bitmapPoints);
                        imageMidPoint = sticker.getImageMidPoint(moveMatrix);
                        if (imageMidPoint.x < 0 || imageMidPoint.x > getWidth()
                                || imageMidPoint.y < 0 || imageMidPoint.y > getHeight()) {
                            if (DEBUG) {
                                Log.d(TAG, "onTouchEvent: over border");
                            }
                        } else {
                            sticker.getMatrix().set(moveMatrix);
                            invalidate();
                        }
                        if (DEBUG) {
                            Log.d(TAG, "平移操作 dx:" + dx + ", dy:" + dy);
                        }
                    }
                    // 单点旋转缩放
                    else if (mode == ActionMode.ROTATE_AND_ZOOM) {
                        moveMatrix.set(downMatrix);
                        float deltaRotation = Sticker.getSpaceRotation(event, imageMidPoint) - oldRotation;
                        moveMatrix.postRotate(deltaRotation, imageMidPoint.x, imageMidPoint.y);
                        float scale = Sticker.getSingleTouchDistance(event, imageMidPoint) / oldDistance;
                        float scaledBitmapSize = getScaledBitmapSize();
                        float currScale = scaledBitmapSize / originBmpSize;
                        // 限制缩放比例
                        if (currScale > MAX_SCALE && scale > 1 || currScale < MIN_SCALE && scale < 1) {
                            moveMatrix.postScale(mSingleScale, mSingleScale, imageMidPoint.x, imageMidPoint.y);
                        } else {
                            moveMatrix.postScale(scale, scale, imageMidPoint.x, imageMidPoint.y);
                            mSingleScale = scale;
                        }
                        PointUtils.getBitmapPoints(sticker.getSrcImage(), sticker.getMatrix(), bitmapPoints);
                        float y1 = bitmapPoints[1];
                        float y2 = bitmapPoints[3];
                        if (Math.abs(y1 - y2) <= horizontalCheck) {
                            paintEdge.setColor(Color.parseColor("#5EC7FE"));
                            paintEdge.setShadowLayer(2, 0, 0, Color.parseColor("#00000000"));
                        } else {
                            paintEdge.setColor(Color.WHITE);
                            paintEdge.setShadowLayer(2, 0, 0, Color.parseColor("#B3000000"));
                        }
                        sticker.getMatrix().set(moveMatrix);
                        invalidate();
                        if (DEBUG) {
                            Log.d(TAG, "单点旋转缩放操作 scale:" + scale + ", deltaRotation:" + deltaRotation + ", currScale:" + currScale);
                        }
                    }
                    // 多点缩放
                    else if (mode == ActionMode.ZOOM_MULTI) {
                        moveMatrix.set(downMatrix);
                        float scale = Sticker.getMultiTouchDistance(event) / oldDistance;
                        float scaledBitmapSize = getScaledBitmapSize();
                        float currScale = scaledBitmapSize / originBmpSize;
                        // 限制缩放比例
                        if (currScale > MAX_SCALE && scale > 1 || currScale < MIN_SCALE && scale < 1) {
                            return false;
                        }
                        moveMatrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        sticker.getMatrix().set(moveMatrix);
                        invalidate();
                        if (DEBUG) {
                            Log.d(TAG, "多点缩放操作 " + scale);
                        }
                    }
                }
            }
            break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP: {
                isEdit = !isChangedOption;
                mode = ActionMode.NONE;
                midPoint = null;
                imageMidPoint = null;
                inRotationScale = false;
                inTranslation = false;
                invalidate();
            }
            break;
            default:
        }
        return handleResult;
    }

    private boolean handleDownEvent(MotionEvent event) {
        isChangedOption = false;
        boolean handleResult = true;
        boolean isStickerOnEdit = true;
        // 平移手势验证
        if (isInStickerArea(sticker, event)) {
            mode = ActionMode.TRANS;
            inTranslation = true;
            inRotationScale = false;
            downMatrix.set(sticker.getMatrix());
            if (DEBUG) {
                Log.d(TAG, "平移手势 ");
            }
            isEdit = true;
            invalidate();
        }
        // 单点旋转和缩放手势验证
        else if (rotateIcon.isInActionCheck(event)) {
            mode = ActionMode.ROTATE_AND_ZOOM;
            inRotationScale = true;
            inTranslation = false;
            downMatrix.set(sticker.getMatrix());
            imageMidPoint = sticker.getImageMidPoint(downMatrix);
            oldDistance = Sticker.getSingleTouchDistance(event, imageMidPoint);
            oldRotation = Sticker.getSpaceRotation(event, imageMidPoint);
            if (DEBUG) {
                Log.d(TAG, "单点旋转缩放手势");
            }
        }
        // 删除操作
        else if (removeIcon.isInActionCheck(event)) {
            if (!isMultiClick()) {
                if (isEdit && listener != null) {
                    setIncreaseAvailable(true);
                    isChangedOption = true;
                    listener.onDelete(this);
                }
                if (DEBUG) {
                    Log.d(TAG, "删除操作 " + isEdit);
                }
                isStickerOnEdit = false;
            }
        }
        // 新增操作
        else if (increaseIcon.isInActionCheck(event)) {
            if (!isMultiClick()) {
                if (isEdit && listener != null) {
                    isChangedOption = true;
                    listener.onIncrease(this);
                }
                if (DEBUG) {
                    Log.d(TAG, "新增操作 " + isEdit);
                }
                isStickerOnEdit = false;
            }
        } else {
            inRotationScale = false;
            inTranslation = false;
            isStickerOnEdit = false;
            handleResult = false;
        }
        if (isStickerOnEdit && listener != null) {
            listener.onEdit(this);
        }
        return handleResult;
    }

    private boolean isMultiClick() {
        if (System.currentTimeMillis() - mCurrentClickTime < MIN_CLICK_DURATION) {
            return true;
        }
        mCurrentClickTime = System.currentTimeMillis();
        return false;
    }

    /**
     * 判断手指是否在操作区域内
     *
     * @param sticker
     * @param event
     * @return
     */
    private boolean isInStickerArea(Sticker sticker, MotionEvent event) {
        RectF dst = sticker.getSrcImageBound(mIconHotArea);
        return dst.contains(event.getX(), event.getY());
    }

    public float[] getMappedPoints() {
        float[] points = sticker.getPoints();
        sticker.getMatrix().mapPoints(mappedPoints, points);
        return mappedPoints;
    }

    public float[] getMappedRectVertex() {
        float[] borderVertex = sticker.getBorderVertex();
        sticker.getMatrix().mapPoints(mappedRectVertex, borderVertex);
        return mappedRectVertex;
    }

    /**
     * 获取贴纸对象
     *
     * @return
     */
    public Sticker getSticker() {
        return sticker;
    }

    /**
     * 设置贴纸图片
     *
     * @param type
     * @param imagePath
     * @param points
     */
    public void setStickerParams(int type, String imagePath, float[] points, boolean isRandom, float[] matrixF) {
        mIsRandom = isRandom;
        try {
            AssetManager assetManager = getContext().getAssets();
            InputStream isBorder = assetManager.open(imagePath);
            InputStream isData = assetManager.open(imagePath);
            int size = getContext().getResources().getDimensionPixelSize(R.dimen.x250);
            Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromStream(isBorder, isData, size, size);
            sticker = new Sticker(imagePath, bitmap, points, type, matrixF);
            isBorder.close();
            isData.close();
        } catch (IOException e) {
            Log.e(TAG, "setStickerParams: ", e);
        }
        mappedPoints = new float[points.length];
        mappedRectVertex = new float[8];
    }

    public int getType() {
        return sticker.getType();
    }

    /**
     * 设置是否贴纸正在处于编辑状态
     *
     * @param edit
     */
    public void setEdit(boolean edit) {
        isEdit = edit;
        invalidate();
    }

    /**
     * 设置新增按钮可点击
     *
     * @param isAvailable
     */
    public void setIncreaseAvailable(boolean isAvailable) {
        boolean b = increaseIcon.setIconEnable(isAvailable);
        if (b) {
            invalidate();
        }
    }

    /**
     * 设置旋转操作的图片
     *
     * @param rotatePath
     */
    public void setRotatePath(String rotatePath) {
        rotateIcon.setSrcIcon(rotatePath);
    }

    /**
     * 设置新增操作的图片
     *
     * @param zoomPath
     */
    public void setIncreasePath(String zoomPath) {
        increaseIcon.setSrcIcon(zoomPath);
    }

    /**
     * 设置删除操作的图片
     *
     * @param removePath
     */
    public void setRemovePath(String removePath) {
        removeIcon.setSrcIcon(removePath);
    }

    public interface OnStickerActionListener {

        /**
         * 删除贴纸
         *
         * @param stickerView
         */
        void onDelete(StickerView stickerView);

        /**
         * 编辑贴纸
         *
         * @param stickerView
         */
        void onEdit(StickerView stickerView);

        /**
         * 新增贴纸
         *
         * @param stickerView
         */
        void onIncrease(StickerView stickerView);
    }

    public static class ActionMode {
        public static final int NONE = 0; // 无模式
        public static final int TRANS = 1; // 拖拽模式
        public static final int ROTATE_AND_ZOOM = 2; // 单点旋转加缩放模式
        public static final int ZOOM_MULTI = 3; // 多点缩放模式
    }
}