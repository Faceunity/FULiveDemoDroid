package com.faceunity.fulivedemo.renderer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.FPSUtil;
import com.faceunity.gles.MakeupProgramLandmarks;
import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 表情动图
 *
 * @author LiuQiang
 */
public class LivePhotoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = LivePhotoRenderer.class.getSimpleName();
    private static final int DEFAULT_WIDTH = 720;
    private static final int DEFAULT_HEIGHT = 1280;
    private static final float AREA_SCALE = 2f;
    private static final float[] IMG_DATA_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static final int SHOW_MODE_NORMAL = 0;
    private static final int SHOW_MODE_AREA = 1;
    private static final float[] MVP_MATRIX = new float[16];
    private static final int OUT_POINT = -20;

    static {
        Matrix.setIdentityM(MVP_MATRIX, 0);
        Matrix.rotateM(MVP_MATRIX, 0, 90, 0, 0, 1);
    }

    private final int mClickPointSizeHalf;
    private final int mClickPointSize;
    private final int mClickPointAreaSizeHalf;
    private final int mClickPointAreaSize;
    private final int mAreaSize;
    private final int mAreaMargin;
    private float mMaxScale = 5;
    private GLSurfaceView mGLSurfaceView;
    private OnRendererStatusListener mOnPhotoRendererStatusListener;
    private int mViewWidth = DEFAULT_WIDTH;
    private int mViewHeight = DEFAULT_HEIGHT;
    private int mShowAreaRightX;
    private int mShowAreaX;
    private int mShowAreaY;
    private int mShowAreaPointX;
    private int mShowAreaPointY;
    private String mPhotoPath;
    private byte[] mPhotoBytes;
    private int mImgTextureId;
    private int mPhotoWidth = DEFAULT_WIDTH;
    private int mPhotoHeight = DEFAULT_HEIGHT;
    private int mClickPointTextureId;
    private int mClickPointAreaTextureId;
    private volatile int mShowMode = SHOW_MODE_NORMAL;
    private volatile boolean isShowPoints = false;
    private float[] mvpAreaMatrix = new float[16];
    private float[] mvpAreaMidFinalMatrix = new float[16];
    private float[] mvpAreaMidMatrix = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;
    private MakeupProgramLandmarks mProgramLandmarks;
    // landmark 坐标系的点位
    private float[] mPointsOfLand;
    private float[] mBordersOfGL = new float[8];
    // final suffix means that values once assigned, never reassigned.
    private float mViewPortScaleFinal = 1F;
    private float mViewPortScale = 1F;
    private int mViewPortX;
    private int mViewPortY;
    private int mViewPortXFinal;
    private int mViewPortYFinal;
    private FPSUtil mFPSUtil;
    private volatile int clickPointIndex = OUT_POINT;
    private PointF mTouchPointF = new PointF();
    private PointF mTouchPointDownF = new PointF();
    private PointF mTouchPointShowF = new PointF();
    private ValueAnimator animator;
    private Context mContext;
    // 检测点位的边界
    private PointF mPoint1 = new PointF();
    private PointF mPoint2 = new PointF();
    private PointF mPoint3 = new PointF();
    private PointF mPoint4 = new PointF();
    private PointF mTransTouchPoint = new PointF();

    public LivePhotoRenderer(String photoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onPhotoRendererStatusListener) {
        mContext = glSurfaceView.getContext();
        mPhotoPath = photoPath;
        mGLSurfaceView = glSurfaceView;
        mOnPhotoRendererStatusListener = onPhotoRendererStatusListener;
        mFPSUtil = new FPSUtil();
        Resources resources = mContext.getResources();
        mClickPointSizeHalf = resources.getDimensionPixelSize(R.dimen.x25);
        mClickPointSize = mClickPointSizeHalf * 2;
        mClickPointAreaSizeHalf = resources.getDimensionPixelSize(R.dimen.x25);
        mClickPointAreaSize = mClickPointAreaSizeHalf * 2;
        mAreaSize = resources.getDimensionPixelSize(R.dimen.x282);
        mAreaMargin = resources.getDimensionPixelSize(R.dimen.x16);
    }

    public void onCreate() {
        mGLSurfaceView.onResume();
    }

    public void onDestroy() {
        final CountDownLatch count = new CountDownLatch(1);
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Log.e(TAG, "onDestroy: ", e);
        }
        mGLSurfaceView.onPause();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mProgramLandmarks = new MakeupProgramLandmarks();
        mProgramLandmarks.setPointFillSize(mContext.getResources().getDimensionPixelSize(R.dimen.x12));
        mProgramLandmarks.setPointBorderSize(mContext.getResources().getDimensionPixelSize(R.dimen.x18));
        loadImgData(mPhotoPath);
        if (mPointsOfLand != null) {
            refreshLandmarks();
        }
        mOnPhotoRendererStatusListener.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: width:" + width + ", height:" + height);
        mViewWidth = width;
        mViewHeight = height;
        mShowAreaX = mAreaMargin;
        mShowAreaRightX = mViewWidth - mAreaSize - mAreaMargin;
        mShowAreaY = mViewHeight - mAreaSize - mAreaMargin;

        mShowAreaPointX = mAreaSize / 2 - mClickPointAreaSizeHalf;
        mShowAreaPointY = mViewHeight - mAreaSize / 2 - mAreaMargin - mClickPointAreaSizeHalf;

        calculateViewport();
        mOnPhotoRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    private void calculateViewport() {
        float scale = 1.0f * mViewWidth * mPhotoHeight / mViewHeight / mPhotoWidth;
        if (scale > 1) {
            mViewPortYFinal = mViewPortY = 0;
            mViewPortScaleFinal = mViewPortScale = (float) mViewHeight / mPhotoHeight;
            mViewPortXFinal = mViewPortX = (int) ((mViewWidth - mViewPortScale * mPhotoWidth) / 2);
        } else if (scale < 1) {
            mViewPortXFinal = mViewPortX = 0;
            mViewPortScaleFinal = mViewPortScale = (float) mViewWidth / mPhotoWidth;
            mViewPortYFinal = mViewPortY = (int) ((mViewHeight - mViewPortScale * mPhotoHeight) / 2);
        } else {
            mViewPortXFinal = mViewPortX = 0;
            mViewPortYFinal = mViewPortY = 0;
            // usually 1.5f
            mViewPortScaleFinal = mViewPortScale = (float) mViewWidth / mPhotoWidth;
        }
        Log.i(TAG, "calculateViewport: scale:" + scale + ", viewPortScale:" + mViewPortScale
                + ", photoWidth:" + mPhotoWidth + ", photoHeight:" + mPhotoHeight);
    }

    private void onSurfaceDestroy() {
        deleteImageTexture(mImgTextureId);
        deleteImageTexture(mClickPointTextureId);
        deleteImageTexture(mClickPointAreaTextureId);
        mImgTextureId = 0;
        mClickPointTextureId = 0;
        mClickPointAreaTextureId = 0;

        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }
        mOnPhotoRendererStatusListener.onSurfaceDestroy();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.01f, 0f, 0.06f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (mFullFrameRectTexture2D == null) {
            return;
        }

        int width = (int) (mPhotoWidth * mViewPortScale);
        int height = (int) (mPhotoHeight * mViewPortScale);
        GLES20.glViewport(mViewPortX, mViewPortY, width, height);
        int fuTextureId = mOnPhotoRendererStatusListener.onDrawFrame(mPhotoBytes, mImgTextureId, mPhotoWidth, mPhotoHeight);
        mFullFrameRectTexture2D.drawFrame(fuTextureId, IMG_DATA_MATRIX, MVP_MATRIX);
        if (isShowPoints) {
            mProgramLandmarks.drawFrame(mViewPortX, mViewPortY, width, height, null);
        }

        if (mShowMode == SHOW_MODE_AREA) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            GLES20.glViewport((int) (mTouchPointShowF.x - mClickPointSizeHalf), (int) (mTouchPointShowF.y - mClickPointSizeHalf), mClickPointSize, mClickPointSize);
            mFullFrameRectTexture2D.drawFrame(mClickPointTextureId, GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX);

            if (mShowAreaX == mAreaMargin && mTouchPointShowF.x < mAreaSize + mAreaMargin && mTouchPointShowF.y > mViewHeight - mAreaSize - mAreaMargin) {
                mShowAreaX = mShowAreaRightX;
            } else if (mShowAreaX == mShowAreaRightX && mTouchPointShowF.x > mShowAreaRightX && mTouchPointShowF.y > mViewHeight - mAreaSize - mAreaMargin) {
                mShowAreaX = mAreaMargin;
            }

            GLES20.glViewport(mShowAreaX, mShowAreaY, mAreaSize, mAreaSize);
            Matrix.multiplyMM(mvpAreaMatrix, 0, mvpAreaMidMatrix, 0, MVP_MATRIX, 0);
            mFullFrameRectTexture2D.drawFrame(fuTextureId, IMG_DATA_MATRIX, mvpAreaMatrix);
            mProgramLandmarks.drawFrame(mShowAreaX, mShowAreaY, mAreaSize, mAreaSize, mvpAreaMidMatrix);

            GLES20.glViewport(mShowAreaPointX + mShowAreaX, mShowAreaPointY, mClickPointAreaSize, mClickPointAreaSize);
            mFullFrameRectTexture2D.drawFrame(mClickPointAreaTextureId, GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX);
        }

        mFPSUtil.limit();
        mGLSurfaceView.requestRender();
    }

    public void resetScale(final OnRenderFinishListener onRenderFinishListener) {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                calculateViewport();
                if (onRenderFinishListener != null) {
                    onRenderFinishListener.onRenderFinish();
                }
            }
        });
    }

    public void scaleM(final float x, final float y, final float scale) {
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                float tmp = mViewPortScale * scale;
                if (tmp < mViewPortScaleFinal || tmp > mViewPortScaleFinal * mMaxScale) {
                    return;
                }
                float focusX = x - mViewPortX;
                float focusY = mViewHeight - y - mViewPortY;
                mViewPortScale = tmp;
                mViewPortX -= focusX * scale - focusX;
                mViewPortY -= focusY * scale - focusY;
//                Log.d(TAG, "scaleM() called viewPortScale:" + mViewPortScale + ", viewPortX:" + mViewPortX + ", viewPortY:" + mViewPortY);
            }
        });
    }

    public void translateM(final float distanceX, final float distanceY) {
        if (mShowMode == SHOW_MODE_AREA) {
            // 移动点位
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    float tempX = mTouchPointF.x;
                    float tempY = mTouchPointF.y;
                    tempX -= distanceX;
                    tempY += distanceY;
                    mPoint1.set(mBordersOfGL[0], mBordersOfGL[1]);
                    mPoint2.set(mBordersOfGL[4], mBordersOfGL[5]);
                    mPoint3.set(mBordersOfGL[6], mBordersOfGL[7]);
                    mPoint4.set(mBordersOfGL[2], mBordersOfGL[3]);
                    mTransTouchPoint.set(tempX, tempY);

//                    boolean touchInSide = PointUtils.IsPointInMatrix(mPoint1, mPoint2, mPoint3, mPoint4, mTransTouchPoint);
//                    Log.d(TAG, "run: inSide " + touchInSide + " touchPoint " + mTouchPointF
//                            + ", border " + Arrays.toString(mBordersOfGL));
                    // TODO: 2019/5/21 0021 cant drag points?
//                    if (!touchInSide) {
//                        return;
//                    }

                    mTouchPointF.x = tempX;
                    mTouchPointF.y = tempY;
                    if (mTouchPointF.x < mViewPortX) {
                        mTouchPointShowF.x = mViewPortX;
                    } else if (mTouchPointF.x > mViewPortX + mPhotoWidth * mViewPortScale) {
                        mTouchPointShowF.x = mViewPortX + mPhotoWidth * mViewPortScale;
                    } else {
                        mTouchPointShowF.x = mTouchPointF.x;
                    }
                    if (mTouchPointF.y < mViewPortY) {
                        mTouchPointShowF.y = mViewPortY;
                    } else if (mTouchPointF.y > mViewPortY + mPhotoHeight * mViewPortScale) {
                        mTouchPointShowF.y = mViewPortY + mPhotoHeight * mViewPortScale;
                    } else {
                        mTouchPointShowF.y = mTouchPointF.y;
                    }

                    Matrix.translateM(mvpAreaMidMatrix, 0, mvpAreaMidFinalMatrix, 0,
                            2 * (mTouchPointDownF.x - mTouchPointShowF.x) / mPhotoWidth / mViewPortScale,
                            -2 * (mTouchPointShowF.y - mTouchPointDownF.y) / mPhotoHeight / mViewPortScale, 0);
                }
            });
        } else {
            // 移动图像
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mViewPortX -= distanceX;
                    mViewPortY += distanceY;
                    // 滑向左上正数，右下负数
//                    Log.d(TAG, "translate: disX: " + distanceX + ", disY: " + distanceY + ", viewportX:" + mViewPortX + ", viewportY:" + mViewPortY);
                }
            });
        }
    }

    /**
     * View 坐标系下的 Points 点位
     *
     * @return
     */
    public float[] getPointsOfView() {
        float[] viewPoints = new float[mPointsOfLand.length];
        for (int i = 0; i < mPointsOfLand.length; i += 2) {
            PointF pointF = changeLandmarkToViewNoScale(mPointsOfLand[i], mPointsOfLand[i + 1]);
            viewPoints[i] = pointF.x;
            viewPoints[i + 1] = pointF.y;
        }
        return viewPoints;
    }

    /**
     * 设置 View 坐标系的 points 点位
     *
     * @param points
     * @param border
     */
    public void setViewPoints(float[] points, float[] border) {
        int length = points.length;
        mPointsOfLand = new float[length];
        // 使用 landmark 坐标系
        for (int i = 0; i < length; i += 2) {
            PointF pointFLand = changeViewToLandmark(points[i], points[i + 1]);
            mPointsOfLand[i] = pointFLand.x;
            mPointsOfLand[i + 1] = pointFLand.y;
        }
        refreshLandmarks();

//        length = border.length;
        // 使用 OpenGL 坐标系
//        for (int i = 0; i < length; i += 2) {
//            PointF pointF = changeViewToGL(border[i], border[i + 1]);
//            mBordersOfGL[i] = pointF.x;
//            mBordersOfGL[i + 1] = pointF.y;
//        }
//        Log.i(TAG, "setViewPoints: borderOfLand " + Arrays.toString(mBordersOfGL));

//        translateScaleViewport(mPointsOfLand);
    }

    // 先缩放再平移, points 是 landmark 坐标系
    private void translateScaleViewport(float[] points) {
        float[] tempX = new float[points.length / 2];
        float[] tempY = new float[points.length / 2];
        for (int i = 0, j = 0, k = 0, length = points.length; i < length; i++) {
            if (i % 2 == 0) {
                tempX[j++] = points[i];
            } else {
                tempY[k++] = points[i];
            }
        }
        Arrays.sort(tempX);
        Arrays.sort(tempY);
        float pointsWidth = tempX[tempX.length - 1] - tempX[0];
        float pointsHeight = tempY[tempY.length - 1] - tempY[0];
        Log.d(TAG, "translateScale: tempX:" + Arrays.toString(tempX));
        Log.d(TAG, "translateScale: tempY:" + Arrays.toString(tempY));
        // 如果当前的尺寸大于画布的一半，那么不做处理。
        if (pointsWidth > mPhotoWidth / 2 || pointsHeight > mPhotoHeight / 2) {
            return;
        }

        // 缩至屏幕的 1/2 宽度
        float scale = (float) mPhotoWidth / 2 / pointsWidth;
        Log.i(TAG, " scale:" + scale
                + ", x0 " + tempX[0] + ", x1 " + tempX[tempX.length - 1] + ", y0 " + tempY[0]
                + ", y1 " + tempY[tempY.length - 1] + " pointsWidth:" + pointsWidth + ", pointsHeight:" + pointsHeight);

        mViewPortScale = scale;

        // 滑向左上正数，右下负数
        // 点的坐标没有变，变的是 ViewPort，就像相机的调节远近一样。
        // 左下角，这个按照距离来算，应为原始的坐标顶点是 (0,0)
        mViewPortX = -(int) (mViewPortScale * (tempX[0] - pointsWidth));
        int yHalf = (int) ((mPhotoHeight - pointsHeight) / 2);
        mViewPortY = -(int) (mViewPortScale * (mPhotoHeight - tempY[tempY.length - 1] - yHalf));

        Log.i(TAG, "translateScale: viewportX " + mViewPortX + ", viewportY " + mViewPortY);
        checkViewPort();
    }

    private void refreshLandmarks() {
        mProgramLandmarks.refresh(mPointsOfLand, mPhotoWidth, mPhotoHeight);
    }

    /**
     * Landmark 坐标系下的 Points 点位
     *
     * @return
     */
    public float[] getPointsOfLandmark() {
        return Arrays.copyOf(mPointsOfLand, mPointsOfLand.length);
    }

    public boolean clickDown(MotionEvent e) {
        if (mPointsOfLand == null) {
            return false;
        }
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        clickPointIndex = OUT_POINT;
        mTouchPointF = changeViewToGL(e.getX(), e.getY());
        mTouchPointShowF = new PointF(mTouchPointF.x, mTouchPointF.y);
        mTouchPointDownF = new PointF(mTouchPointF.x, mTouchPointF.y);
        PointF photoPoint = changeGLToLandmark(mTouchPointF.x, mTouchPointF.y);

        int min = Math.max((int) (30 / mViewPortScale * AREA_SCALE), 3);
        for (int i = 0; i < mPointsOfLand.length; i += 2) {
            if (Math.abs(mPointsOfLand[i] - photoPoint.x) < min && Math.abs(mPointsOfLand[i + 1] - photoPoint.y) < min) {
                // 认为是命中了点
                clickPointIndex = i;
                mvpAreaMidFinalMatrix = GlUtil.changeMVPMatrixCrop(mAreaSize, mAreaSize, mPhotoWidth, mPhotoHeight);
                Matrix.scaleM(mvpAreaMidFinalMatrix, 0, mViewPortScale * AREA_SCALE, mViewPortScale * AREA_SCALE, 1);
                Matrix.translateM(mvpAreaMidFinalMatrix, 0, 1 - 2 * photoPoint.x / mPhotoWidth, 1 - 2 * (mPhotoHeight - photoPoint.y) / mPhotoHeight, 0);
                mvpAreaMidMatrix = Arrays.copyOf(mvpAreaMidFinalMatrix, mvpAreaMidFinalMatrix.length);
                return true;
            }
        }
        return false;
    }

    public void clickLong() {
        mPointsOfLand[clickPointIndex] = OUT_POINT;
        mPointsOfLand[clickPointIndex + 1] = OUT_POINT;
        refreshLandmarks();
        mShowMode = SHOW_MODE_AREA;
    }

    public void clickUp() {
        if (mShowMode == SHOW_MODE_AREA) {
            PointF photoPoint = changeGLToLandmark(mTouchPointShowF.x, mTouchPointShowF.y);
            mPointsOfLand[clickPointIndex] = photoPoint.x;
            mPointsOfLand[clickPointIndex + 1] = photoPoint.y;
            refreshLandmarks();
        }
        checkViewPort();
        mShowMode = SHOW_MODE_NORMAL;
    }

    private void checkViewPort() {
        final PointF pointF = new PointF(mViewPortX, mViewPortY);
        if (mPhotoWidth * mViewPortScale < mViewWidth) {
            pointF.x = (mViewWidth - mPhotoWidth * mViewPortScale) / 2;
        } else if (pointF.x > 0) {
            pointF.x = 0;
        } else if (pointF.x + mPhotoWidth * mViewPortScale < mViewWidth) {
            pointF.x = mViewWidth - mPhotoWidth * mViewPortScale;
        }
        if (mPhotoHeight * mViewPortScale < mViewHeight) {
            pointF.y = (mViewHeight - mPhotoHeight * mViewPortScale) / 2;
        } else if (pointF.y > 0) {
            pointF.y = 0;
        } else if (pointF.y + mPhotoHeight * mViewPortScale < mViewHeight) {
            pointF.y = mViewHeight - mPhotoHeight * mViewPortScale;
        }

        if (pointF.x != mViewPortX || pointF.y != mViewPortY) {
            final int viewPortX = mViewPortX;
            final int viewPortY = mViewPortY;
            animator = ValueAnimator.ofFloat(0, 1f).setDuration(300);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final float v = (float) animation.getAnimatedValue();
                    mGLSurfaceView.queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            mViewPortX = (int) (viewPortX + (pointF.x - viewPortX) * v);
                            mViewPortY = (int) (viewPortY + (pointF.y - viewPortY) * v);
                        }
                    });
                }
            });
            animator.start();
        }
    }

    private void deleteImageTexture(int texId) {
        if (texId != 0) {
            int[] textures = new int[]{texId};
            GLES20.glDeleteTextures(1, textures, 0);
        }
    }

    private void loadImgData(String path) {
        Log.d(TAG, "loadImgData: " + path);
        Bitmap src = BitmapUtil.loadBitmap(path, 720, 1280);
        // src maybe null
        if (src == null) {
            mOnPhotoRendererStatusListener.onLoadPhotoError("图片加载出错");
            return;
        }
        mImgTextureId = GlUtil.createImageTexture(src);
        try {
            mPhotoBytes = BitmapUtil.getNV21(mPhotoWidth = src.getWidth() / 2 * 2, mPhotoHeight = src.getHeight() / 2 * 2, src);
            mClickPointTextureId = GlUtil.createImageTexture(BitmapFactory.decodeStream(mGLSurfaceView.getContext().getAssets().open("image/advanced_click_point.png")));
            mClickPointAreaTextureId = GlUtil.createImageTexture(BitmapFactory.decodeStream(mGLSurfaceView.getContext().getAssets().open("image/advanced_click_point_area.png")));
        } catch (Exception e) {
            Log.e(TAG, "loadImgData: ", e);
            mClickPointTextureId = 0;
            mClickPointAreaTextureId = 0;
        }
    }

    public void setShowPoints(boolean showPoints) {
        isShowPoints = showPoints;
    }

    /**
     * android view 坐标系的点转换成 openGL 坐标系
     * <p>
     * android view 坐标系 ： 左上角为原点
     * openGL 坐标系 ： 左下角为原点
     *
     * @param x
     * @param y
     * @return
     */
    private PointF changeViewToGL(float x, float y) {
        return new PointF(x, mViewHeight - y);
    }

    /**
     * openGL 坐标系的点转换成 landmarks 中相应点
     * <p>
     * openGL 坐标系 ： 左下角为原点
     * landmarks 坐标系 ： 图片左上角为原点
     *
     * @param x
     * @param y
     * @return
     */
    private PointF changeGLToLandmark(float x, float y) {
        return new PointF((x - mViewPortX) / mViewPortScale, mPhotoHeight - (y - mViewPortY) / mViewPortScale);
    }

    /**
     * view 坐标系转 landmark 坐标系
     *
     * @param x
     * @param y
     * @return
     */
    public PointF changeViewToLandmark(float x, float y) {
        return new PointF((x - mViewPortX) / mViewPortScale, mPhotoHeight - (mViewHeight - y - mViewPortY) / mViewPortScale);
    }

    /**
     * landmark 坐标系转 view 坐标系
     *
     * @param x
     * @param y
     * @return
     */
    private PointF changeLandmarkToView(float x, float y) {
        return new PointF(x * mViewPortScale + mViewPortX, mViewPortScale * (y - mPhotoHeight) + mViewHeight - mViewPortY);
    }

    /**
     * 在未进行缩放、移动的情况下，进行变换
     *
     * @param x
     * @param y
     * @return
     */
    private PointF changeLandmarkToViewNoScale(float x, float y) {
        return new PointF(x * mViewPortScaleFinal + mViewPortXFinal, mViewPortScaleFinal * (y - mPhotoHeight) + mViewHeight - mViewPortYFinal);
    }

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight);

        void onSurfaceDestroy();

        void onLoadPhotoError(String error);
    }

    public interface OnRenderFinishListener {
        void onRenderFinish();
    }
}
