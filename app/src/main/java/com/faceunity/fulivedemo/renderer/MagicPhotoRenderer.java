package com.faceunity.fulivedemo.renderer;

import android.animation.ValueAnimator;
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
 * Created by tujh on 2018/3/2.
 * 异图
 */
public class MagicPhotoRenderer implements GLSurfaceView.Renderer {
    public final static String TAG = MagicPhotoRenderer.class.getSimpleName();
    private static final int DEFAULT_WIDTH = 720;
    private static final int DEFAULT_HEIGHT = 1280;
    private static final float AREA_SCALE = 2f;
    private static final float[] IMG_DATA_MATRIX = {0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};
    private static final int SHOW_MODE_NORMAL = 0;
    private static final int SHOW_MODE_AREA = 1;
    private static final float[] MVP_MATRIX = new float[16];

    static {
        Matrix.setIdentityM(MVP_MATRIX, 0);
        Matrix.rotateM(MVP_MATRIX, 0, 90, 0, 0, 1);
    }

    private final int CLICK_POINT_SIZE_HALF;
    private final int CLICK_POINT_SIZE;
    private final int CLICK_POINT_AREA_SIZE_HALF;
    private final int CLICK_POINT_AREA_SIZE;
    private final int AREA_SIZE;
    private final int AREA_MARGIN;
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
    private volatile boolean isShowLandmarks = false;
    private float[] mvpAreaMatrix = new float[16];
    private float[] mvpAreaMidFianlMatrix = new float[16];
    private float[] mvpAreaMidMatrix = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;
    private MakeupProgramLandmarks mProgramLandmarks;
    // landmark 坐标系的坐标
    private volatile float[] mFinalLandmarks = null;
    private float mViewPortScaleFinal = 1F;
    private float mViewPortScale = 1F;
    private int mViewPortX;
    private int mViewPortY;
    private FPSUtil mFPSUtil;
    private volatile int clickPointIndex = -1;
    private PointF mTouchPointF = new PointF();
    private PointF mTouchPointDownF = new PointF();
    private PointF mTouchPointShowF = new PointF();
    private ValueAnimator animator;

    public MagicPhotoRenderer(String photoPath, GLSurfaceView glSurfaceView, OnRendererStatusListener onPhotoRendererStatusListener) {
        mPhotoPath = photoPath;
        mGLSurfaceView = glSurfaceView;
        mOnPhotoRendererStatusListener = onPhotoRendererStatusListener;
        mFPSUtil = new FPSUtil();
        CLICK_POINT_SIZE_HALF = glSurfaceView.getResources().getDimensionPixelSize(R.dimen.x50);
        CLICK_POINT_SIZE = CLICK_POINT_SIZE_HALF * 2;
        CLICK_POINT_AREA_SIZE_HALF = glSurfaceView.getResources().getDimensionPixelSize(R.dimen.x40);
        CLICK_POINT_AREA_SIZE = CLICK_POINT_AREA_SIZE_HALF * 2;
        AREA_SIZE = glSurfaceView.getResources().getDimensionPixelSize(R.dimen.x234);
        AREA_MARGIN = glSurfaceView.getResources().getDimensionPixelSize(R.dimen.x20);
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
        Log.i(TAG, "onSurfaceCreated: ");
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mProgramLandmarks = new MakeupProgramLandmarks();
        loadImgData(mPhotoPath);
        if (mFinalLandmarks != null) {
            refreshLandmarks();
        }
        mOnPhotoRendererStatusListener.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "onSurfaceChanged: width:" + width + ", height:" + height);
        mViewWidth = width;
        mViewHeight = height;
        mShowAreaX = AREA_MARGIN;
        mShowAreaRightX = mViewWidth - AREA_SIZE - AREA_MARGIN;
        mShowAreaY = mViewHeight - AREA_SIZE - AREA_MARGIN;

        mShowAreaPointX = AREA_SIZE / 2 - CLICK_POINT_AREA_SIZE_HALF;
        mShowAreaPointY = mViewHeight - AREA_SIZE / 2 - AREA_MARGIN - CLICK_POINT_AREA_SIZE_HALF;

        calculateViewport();

        mOnPhotoRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    private void calculateViewport() {
        float scale = 1.0f * mViewWidth * mPhotoHeight / mViewHeight / mPhotoWidth;
        if (scale > 1) {
            mViewPortY = 0;
            mViewPortScaleFinal = mViewPortScale = (float) mViewHeight / mPhotoHeight;
            mViewPortX = (int) ((mViewWidth - mViewPortScale * mPhotoWidth) / 2);
        } else if (scale < 1) {
            mViewPortX = 0;
            mViewPortScaleFinal = mViewPortScale = (float) mViewWidth / mPhotoWidth;
            mViewPortY = (int) ((mViewHeight - mViewPortScale * mPhotoHeight) / 2);
        } else {
            mViewPortX = 0;
            mViewPortY = 0;
            mViewPortScaleFinal = mViewPortScale = (float) mViewWidth / mPhotoWidth;
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.01f, 0f, 0.06f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (mFullFrameRectTexture2D == null) {
            return;
        }

        GLES20.glViewport(mViewPortX, mViewPortY, (int) (mPhotoWidth * mViewPortScale), (int) (mPhotoHeight * mViewPortScale));
        int fuTextureId = mOnPhotoRendererStatusListener.onDrawFrame(mPhotoBytes, mImgTextureId, mPhotoWidth, mPhotoHeight);
        mFullFrameRectTexture2D.drawFrame(fuTextureId, IMG_DATA_MATRIX, MVP_MATRIX);
        if (isShowLandmarks) {
            mProgramLandmarks.drawFrame(mViewPortX, mViewPortY, (int) (mPhotoWidth * mViewPortScale), (int) (mPhotoHeight * mViewPortScale), null);
        }

        if (mShowMode == SHOW_MODE_AREA) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            GLES20.glViewport((int) (mTouchPointShowF.x - CLICK_POINT_SIZE_HALF), (int) (mTouchPointShowF.y - CLICK_POINT_SIZE_HALF), CLICK_POINT_SIZE, CLICK_POINT_SIZE);
            mFullFrameRectTexture2D.drawFrame(mClickPointTextureId, GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX);

            if (mShowAreaX == AREA_MARGIN && mTouchPointShowF.x < AREA_SIZE + AREA_MARGIN && mTouchPointShowF.y > mViewHeight - AREA_SIZE - AREA_MARGIN) {
                mShowAreaX = mShowAreaRightX;
            } else if (mShowAreaX == mShowAreaRightX && mTouchPointShowF.x > mShowAreaRightX && mTouchPointShowF.y > mViewHeight - AREA_SIZE - AREA_MARGIN) {
                mShowAreaX = AREA_MARGIN;
            }

            GLES20.glViewport(mShowAreaX, mShowAreaY, AREA_SIZE, AREA_SIZE);
            Matrix.multiplyMM(mvpAreaMatrix, 0, mvpAreaMidMatrix, 0, MVP_MATRIX, 0);
            mFullFrameRectTexture2D.drawFrame(fuTextureId, IMG_DATA_MATRIX, mvpAreaMatrix);
            mProgramLandmarks.drawFrame(mShowAreaX, mShowAreaY, AREA_SIZE, AREA_SIZE, mvpAreaMidMatrix);

            GLES20.glViewport(mShowAreaPointX + mShowAreaX, mShowAreaPointY, CLICK_POINT_AREA_SIZE, CLICK_POINT_AREA_SIZE);
            mFullFrameRectTexture2D.drawFrame(mClickPointAreaTextureId, GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX);
        }

        mFPSUtil.limit();
        mGLSurfaceView.requestRender();
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

    private void deleteImageTexture(int texId) {
        if (texId != 0) {
            int[] textures = new int[]{texId};
            GLES20.glDeleteTextures(1, textures, 0);
        }
    }

    private void loadImgData(String path) {
        Log.i(TAG, "loadImgData: " + path);
        Bitmap src = BitmapUtil.loadBitmap(path, 720, 1280);
        // src maybe null
        if (src == null) {
            mOnPhotoRendererStatusListener.onLoadImageError();
            return;
        }
        mImgTextureId = GlUtil.createImageTexture(src);
        try {
            mPhotoBytes = BitmapUtil.getNV21(mPhotoWidth = src.getWidth() / 2 * 2, mPhotoHeight = src.getHeight() / 2 * 2, src);
            mClickPointTextureId = GlUtil.createImageTexture(BitmapFactory.decodeStream(mGLSurfaceView.getContext().getAssets().open("image/advanced_click_point.png")));
            mClickPointAreaTextureId = GlUtil.createImageTexture(BitmapFactory.decodeStream(mGLSurfaceView.getContext().getAssets().open("image/advanced_click_point_area.png")));
        } catch (Throwable e) {
            Log.e(TAG, "loadImgData: ", e);
            mClickPointTextureId = 0;
            mClickPointAreaTextureId = 0;
        }

        Log.i(TAG, "loadImgData: pW:" + mPhotoWidth + ", pH:" + mPhotoHeight);
    }

    // 使用 landmark 坐标系
    public void setViewPoints(float[] points) {
        int length = points.length;
        mFinalLandmarks = new float[length];
        for (int i = 0; i < length; i += 2) {
            PointF pointFLand = changeViewToLandmark(points[i], points[i + 1]);
            mFinalLandmarks[i] = pointFLand.x;
            mFinalLandmarks[i + 1] = pointFLand.y;
        }
        refreshLandmarks();
    }

    public void setLandmarkPoints(float[] points) {
        if (mFinalLandmarks == null) {
            mFinalLandmarks = new float[points.length];
        }
        System.arraycopy(points, 0, mFinalLandmarks, 0, points.length);
        refreshLandmarks();
    }

    public void refreshLandmarks() {
        mProgramLandmarks.refresh(mFinalLandmarks, mPhotoWidth, mPhotoHeight);
    }

    public float[] getLandmarks() {
        return Arrays.copyOf(mFinalLandmarks, mFinalLandmarks.length);
    }

    public float[] convertFaceRect(float[] faceRect) {
        float[] newFaceRect = new float[4];
        // 以右下角为顶点计算
        newFaceRect[2] = (mPhotoWidth - faceRect[0]) * mViewPortScale + mViewPortX;
        newFaceRect[3] = mViewHeight - faceRect[1] * mViewPortScale - mViewPortY;
        newFaceRect[0] = (mPhotoWidth - faceRect[2]) * mViewPortScale + mViewPortX;
        newFaceRect[1] = mViewHeight - faceRect[3] * mViewPortScale - mViewPortY;
        Log.i(TAG, "convertFaceRect: mViewPortScale:" + mViewPortScale + ", mViewPort:" + mViewPortX + "," + mViewPortY);
        return newFaceRect;
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
            }
        });
    }

    public void translateM(final float distanceX, final float distanceY) {
        if (mShowMode == SHOW_MODE_AREA) {
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mTouchPointF.x -= distanceX;
                    mTouchPointF.y += distanceY;

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

                    Matrix.translateM(mvpAreaMidMatrix, 0, mvpAreaMidFianlMatrix, 0,
                            2 * (mTouchPointDownF.x - mTouchPointShowF.x) / mPhotoWidth / mViewPortScale,
                            -2 * (mTouchPointShowF.y - mTouchPointDownF.y) / mPhotoHeight / mViewPortScale, 0);
                }
            });
        } else {
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mViewPortX -= distanceX;
                    mViewPortY += distanceY;
                }
            });
        }

    }

    public boolean clickDown(MotionEvent e) {
        if (mFinalLandmarks == null) {
            return false;
        }
        if (animator != null) {
            animator.cancel();
            animator = null;
        }

        clickPointIndex = -1;
        mTouchPointF = changeViewToGL(e.getX(), e.getY());
        mTouchPointShowF = new PointF(mTouchPointF.x, mTouchPointF.y);
        mTouchPointDownF = new PointF(mTouchPointF.x, mTouchPointF.y);
        PointF photoPoint = changeGLToPhoto(mTouchPointF.x, mTouchPointF.y);

        int min = Math.max((int) (30 / mViewPortScale * AREA_SCALE), 3);
        for (int i = 0; i < mFinalLandmarks.length; i += 2) {
            if (Math.abs(mFinalLandmarks[i] - photoPoint.x) < min && Math.abs(mFinalLandmarks[i + 1] - photoPoint.y) < min) {
                clickPointIndex = i;
                mvpAreaMidFianlMatrix = GlUtil.changeMVPMatrixCrop(AREA_SIZE, AREA_SIZE, mPhotoWidth, mPhotoHeight);
                Matrix.scaleM(mvpAreaMidFianlMatrix, 0, mViewPortScale * AREA_SCALE, mViewPortScale * AREA_SCALE, 1);
                Matrix.translateM(mvpAreaMidFianlMatrix, 0, 1 - 2 * photoPoint.x / mPhotoWidth, 1 - 2 * (mPhotoHeight - photoPoint.y) / mPhotoHeight, 0);
                mvpAreaMidMatrix = Arrays.copyOf(mvpAreaMidFianlMatrix, mvpAreaMidFianlMatrix.length);
                return true;
            }
        }
        return false;
    }

    public void clickLong() {
        mFinalLandmarks[clickPointIndex] = -1;
        mFinalLandmarks[clickPointIndex + 1] = -1;
        refreshLandmarks();
        mShowMode = SHOW_MODE_AREA;
    }

    public void clickUp() {
        if (mShowMode == SHOW_MODE_AREA) {
            PointF photoPoint = changeGLToPhoto(mTouchPointShowF.x, mTouchPointShowF.y);
            mFinalLandmarks[clickPointIndex] = photoPoint.x;
            mFinalLandmarks[clickPointIndex + 1] = photoPoint.y;
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

    public void setShowLandmarks(boolean showLandmarks) {
        isShowLandmarks = showLandmarks;
    }

    public void setMaxScale(float[] ld) {
        this.mMaxScale = mViewWidth / Math.abs(ld[104] - ld[92]) / mViewPortScaleFinal;
    }

    /**
     * android view 坐标系的点转换成 opengl 坐标系
     * <p>
     * android view 坐标系 ： 左上角为原点
     * opengl 坐标系 ： 左下角为原点
     *
     * @param x
     * @param y
     * @return
     */
    public PointF changeViewToGL(float x, float y) {
        return new PointF(x, mViewHeight - y);
    }

    /**
     * opengl 坐标系的点转换成 landmarks 中相应点
     * <p>
     * opengl 坐标系 ： 左下角为原点
     * landmarks 坐标系 ： 图片左上角为原点
     *
     * @param x
     * @param y
     * @return
     */
    public PointF changeGLToPhoto(float x, float y) {
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
    public PointF changeLandmarkToView(float x, float y) {
        return new PointF(x * mViewPortScale + mViewPortX, mViewPortScale * (y - mPhotoHeight) + mViewHeight - mViewPortY);
    }

    public int getViewWidth() {
        return mViewWidth;
    }

    public int getViewHeight() {
        return mViewHeight;
    }

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight);

        void onSurfaceDestroy();

        void onLoadImageError();
    }

    public interface OnRenderFinishListener {
        void onRenderFinish();
    }
}
