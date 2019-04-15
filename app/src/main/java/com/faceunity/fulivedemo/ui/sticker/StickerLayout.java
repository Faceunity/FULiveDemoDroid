package com.faceunity.fulivedemo.ui.sticker;

import android.content.Context;
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
    private static final boolean DEBUG = false;
    private Context context;
    // 贴纸的集合
    private List<StickerView> stickerViews;
    // 贴纸的View参数
    private LayoutParams stickerParams;

    // 旋转操作图片
    private int rotateRes;
    // 缩放操作图片
    private int increaseRes;
    // 缩放操作图片
    private int removeRes;
    // 处于编辑操作的View
    private int inEditIndex;

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
        stickerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
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
        if (DEBUG) {
            Log.d(TAG, "onTouchEvent: ");
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
     * 新增贴纸
     */
    public void addSticker(final int type, final String imagePath, final float[] dots) {
        int count = mStickerCount.get(type);
        if (count >= 3) {
            StickerView stickerView = getEditingStickerView();
            if (stickerView != null) {
                stickerView.setIncreaseAvailable(false);
                stickerView.setEdit(true);
            }
            ToastUtil.makeNormalToast(getContext(), R.string.magic_cant_add_more).show();
            return;
        }
        mStickerCount.put(type, count + 1);
        StickerView sv = new StickerView(context);
        sv.setStickerParams(type, imagePath, dots);
        sv.setLayoutParams(stickerParams);
        sv.setOnStickerActionListener(new StickerView.OnStickerActionListener() {
            @Override
            public void onDelete(StickerView stickerView) {
                if (DEBUG) {
                    Log.d(TAG, "onDelete: ");
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
                if (DEBUG) {
                    Log.d(TAG, "onEdit: ");
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
                if (DEBUG) {
                    Log.d(TAG, "onIncrease: ");
                }
                inEditIndex = stickerViews.indexOf(stickerView);
                stickerView.setEdit(false);
                addSticker(type, imagePath, dots);
                if (mOnChildViewStatusListener != null) {
                    mOnChildViewStatusListener.onViewAdded(stickerViews.size());
                }
            }
        });
        addView(sv);
        stickerViews.add(sv);
        inEditIndex = stickerViews.size() - 1;
        redraw();
    }


    /**
     * 重置贴纸的操作列表
     */
    private void redraw() {
        redraw(true);
    }

    /**
     * 重置贴纸的操作列表
     */
    private void redraw(boolean isNotGenerate) {
        int size = stickerViews.size();
        if (size <= 0) {
            return;
        }
        for (int i = size - 1; i >= 0; i--) {
            StickerView item = stickerViews.get(i);
            item.setIncreaseRes(increaseRes);
            item.setRotateRes(rotateRes);
            item.setRemoveRes(removeRes);
            if (i == size - 1) {
                item.setEdit(isNotGenerate);
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

    public void setFixedLandmarkPoints(float[] points) {
        StickerView stickerView = getEditingStickerView();
        if (stickerView != null) {
            stickerView.getSticker().setLandmarkPoints(points);
        }
    }

    public void setRotateRes(int rotateRes) {
        this.rotateRes = rotateRes;
    }

    public void setIncreaseRes(int increaseRes) {
        this.increaseRes = increaseRes;
    }

    public void setRemoveRes(int removeRes) {
        this.removeRes = removeRes;
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
         * @param visibleChildCount
         */
        void onViewRemoved(int visibleChildCount);
    }
}
