package com.faceunity.fulivedemo.ui.sticker;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：ZhouYou
 * 日期：2016/12/2.
 */
public class StickerLayout extends FrameLayout {
    private static final String TAG = "StickerLayout";
    private Context context;
    // 贴纸的集合
    private List<StickerView> stickerViews;
    // 旋转操作图片
    private String rotatePath;
    // 缩放操作图片
    private String increasePath;
    // 缩放操作图片
    private String removePath;
    // 处于编辑操作的View
    private int inEditIndex;
    private static final int MAX_STICKER_COUNT = 3;

    private SparseIntArray mStickerCount;
    private OnChildViewStatusListener mOnChildViewStatusListener;

    public StickerLayout(Context context) {
        this(context, null);
    }

    public StickerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        stickerViews = new ArrayList<>(8);
        mStickerCount = new SparseIntArray(6);
    }

    public void setOnChildViewStatusListener(OnChildViewStatusListener onChildViewStatusListener) {
        mOnChildViewStatusListener = onChildViewStatusListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setUnEditable();
        return super.onTouchEvent(event);
    }

    public void setUnEditable() {
        if (StickerView.DEBUG) {
            Log.d(TAG, "setUnEditable");
        }
        StickerView stickerView = getEditingStickerView();
        if (stickerView != null) {
            stickerView.setEdit(false);
        }
        if (mOnChildViewStatusListener != null) {
            mOnChildViewStatusListener.onViewRemoved(0);
        }
    }

    public StickerView getEditingStickerView() {
        if (inEditIndex >= 0 && inEditIndex < stickerViews.size()) {
            return stickerViews.get(inEditIndex);
        }
        return null;
    }

    /**
     * 新增五官贴纸
     *
     * @param type
     * @param imagePath
     * @param points
     * @param isRandom
     * @param matrixF
     */
    public void addSticker(final int type, final String imagePath, final float[] points, final boolean isRandom,
                           final float[] matrixF) {
        int count = mStickerCount.get(type);
        if (count >= MAX_STICKER_COUNT) {
            StickerView stickerView = getEditingStickerView();
            if (stickerView != null) {
                stickerView.setIncreaseAvailable(false);
                stickerView.setEdit(true);
            }
            ToastUtil.makeNormalToast(getContext(), R.string.live_photo_cant_add_more).show();
            return;
        }
        mStickerCount.put(type, count + 1);
        StickerView stickerView = new StickerView(context);
        stickerView.setStickerParams(type, imagePath, points, isRandom, matrixF);
        LayoutParams stickerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        stickerView.setLayoutParams(stickerParams);
        stickerView.setOnStickerActionListener(new StickerView.OnStickerActionListener() {
            @Override
            public void onDelete(StickerView stickerView) {
                if (StickerView.DEBUG) {
                    Log.d(TAG, "onDelete: " + stickerView);
                }
                int tp = stickerView.getType();
                mStickerCount.put(tp, mStickerCount.get(tp) - 1);
                removeView(stickerView);
                stickerViews.remove(stickerView);
                for (StickerView view : stickerViews) {
                    view.setIncreaseAvailable(true);
                }
                redraw();
                if (mOnChildViewStatusListener != null) {
                    mOnChildViewStatusListener.onViewRemoved(stickerViews.size());
                }
            }

            @Override
            public void onEdit(StickerView stickerView) {
                if (StickerView.DEBUG) {
                    Log.d(TAG, "onEdit: " + stickerView);
                }
                int position = stickerViews.indexOf(stickerView);
                stickerView.bringToFront();
                inEditIndex = position;

                int size = stickerViews.size();
                for (int i = 0; i < size; i++) {
                    StickerView item = stickerViews.get(i);
                    if (position != i) {
                        item.setEdit(false);
                    }
                }
                if (mOnChildViewStatusListener != null) {
                    mOnChildViewStatusListener.onViewAdded(stickerViews.size());
                }
            }

            @Override
            public void onIncrease(StickerView stickerView) {
                if (StickerView.DEBUG) {
                    Log.d(TAG, "onIncrease: " + stickerView);
                }
                inEditIndex = stickerViews.indexOf(stickerView);
                stickerView.setEdit(false);
                addSticker(type, imagePath, points, isRandom, matrixF);
                if (mOnChildViewStatusListener != null) {
                    mOnChildViewStatusListener.onViewAdded(stickerViews.size());
                }
            }
        });
        addView(stickerView);
        stickerViews.add(stickerView);
        inEditIndex = stickerViews.size() - 1;
        redraw();
    }

    /**
     * 重置贴纸的操作列表
     */
    private void redraw() {
        int size = stickerViews.size();
        if (size <= 0) {
            return;
        }

        for (int i = size - 1; i >= 0; i--) {
            StickerView item = stickerViews.get(i);
            item.setIncreasePath(increasePath);
            item.setRotatePath(rotatePath);
            item.setRemovePath(removePath);
            if (i == size - 1) {
                item.setEdit(true);
            } else {
                item.setEdit(false);
            }
        }
    }

    public int getStickerCount() {
        return stickerViews.size();
    }

    public List<Sticker> getStickers() {
        List<Sticker> stickers = new ArrayList<>(stickerViews.size());
        for (StickerView stickerView : stickerViews) {
            stickers.add(stickerView.getSticker());
        }
        return stickers;
    }

    public float[] getMappedPoints() {
        StickerView stickerView = getEditingStickerView();
        if (stickerView != null) {
            return stickerView.getMappedPoints();
        } else {
            return null;
        }
    }

    public float[] getMappedBorders() {
        StickerView stickerView = getEditingStickerView();
        if (stickerView != null) {
            return stickerView.getMappedRectVertex();
        } else {
            return null;
        }
    }

    public void setAdjustPoints(float[] pointsOfLandmark, float[] pointsOfView) {
        StickerView stickerView = getEditingStickerView();
        if (stickerView != null) {
            Sticker sticker = stickerView.getSticker();
            sticker.setLandmarkPoints(pointsOfLandmark);
            Matrix matrix = sticker.getMatrix();
            Matrix inverted = new Matrix();
            // 矩阵逆向，映射修改过的 View 系坐标，然后修改五官的默认点位
            boolean invert = matrix.invert(inverted);
            if (invert) {
                float[] newPoints = new float[pointsOfView.length];
                inverted.mapPoints(newPoints, pointsOfView);
                sticker.setPoints(newPoints);
            }
        }
    }

    public void setRotatePath(String rotatePath) {
        this.rotatePath = rotatePath;
    }

    public void setIncreasePath(String increasePath) {
        this.increasePath = increasePath;
    }

    public void setRemovePath(String removePath) {
        this.removePath = removePath;
    }

    public interface OnChildViewStatusListener {
        /**
         * view 添加
         *
         * @param visibleChildCount
         */
        void onViewAdded(int visibleChildCount);

        /**
         * view 移除
         *
         * @param visibleChildCount
         */
        void onViewRemoved(int visibleChildCount);
    }
}
