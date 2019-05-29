package com.faceunity.fulivedemo.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.activity.LivePhotoMakeActivity;
import com.faceunity.fulivedemo.renderer.LivePhotoRenderer;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

/**
 * 表情动图 五官调整页面
 *
 * @author LiuQiang on 2018.12.17
 */
public class LivePhotoAdjustFragment extends Fragment {
    public static final String TAG = "LivePhotoAdjustFragment";
    private float[] mPoints;
    private float[] mBorders;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable longPressRunnable;
    private LivePhotoMakeActivity mActivity;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private LivePhotoRenderer mLivePhotoRenderer;
    private OnBackClickListener mOnBackClickListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (LivePhotoMakeActivity) getActivity();
        mLivePhotoRenderer = mActivity.getLivePhotoRenderer();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mLivePhotoRenderer.setShowPoints(true);
        if (mPoints != null) {
            mLivePhotoRenderer.setViewPoints(mPoints, mBorders);
        }
        View view = inflater.inflate(R.layout.fragment_face_adjust, container, false);
        view.findViewById(R.id.fl_back).setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                LivePhotoAdjustFragment.this.onSaveClick();
            }
        });
        view.setOnTouchListener(new TouchListener());
        longPressRunnable = new Runnable() {
            @Override
            public void run() {
                mLivePhotoRenderer.clickLong();
            }
        };
        mGestureDetector = new GestureDetectorCompat(mActivity, new GestureDetector.SimpleOnGestureListener() {
            private final int delayMillis = ViewConfiguration.getLongPressTimeout();

            @Override
            public boolean onDown(MotionEvent e) {
                if (mLivePhotoRenderer.clickDown(e)) {
                    mHandler.postDelayed(longPressRunnable, delayMillis);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mHandler.removeCallbacks(longPressRunnable);
                mLivePhotoRenderer.translateM(distanceX, distanceY);
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);
        mScaleGestureDetector = new ScaleGestureDetector(mActivity, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mLivePhotoRenderer.scaleM(detector.getFocusX(), detector.getFocusY(), detector.getScaleFactor());
                return true;
            }
        });
        return view;
    }

    public void setViewPoints(float[] points, float[] borders) {
        if (mLivePhotoRenderer != null) {
            mLivePhotoRenderer.setViewPoints(points, borders);
            mLivePhotoRenderer.setShowPoints(true);
        } else {
            mPoints = points;
            mBorders = borders;
        }
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        mOnBackClickListener = onBackClickListener;
    }

    // 返回贴纸页面
    public void onSaveClick() {
        mLivePhotoRenderer.setShowPoints(false);
        mActivity.showFragment(LivePhotoMarkFragment.TAG);
        if (mOnBackClickListener != null) {
            mOnBackClickListener.onClick(mLivePhotoRenderer.getPointsOfLandmark(), mLivePhotoRenderer.getPointsOfView());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHandler.removeCallbacksAndMessages(null);
    }

    public interface OnBackClickListener {
        /**
         * 精细调整保存
         *
         * @param pointsOfLandmark landmark 坐标系的点位
         * @param pointsOfView     view 坐标系的点位
         */
        void onClick(float[] pointsOfLandmark, float[] pointsOfView);
    }

    private class TouchListener implements View.OnTouchListener {
        boolean isTouch = true;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mScaleGestureDetector.onTouchEvent(event);
            if (mScaleGestureDetector.isInProgress()) {
                isTouch = false;
            }
            if (isTouch) {
                mGestureDetector.onTouchEvent(event);
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                mHandler.removeCallbacks(longPressRunnable);
                isTouch = true;
                mLivePhotoRenderer.clickUp();
            }
            return true;
        }
    }

}