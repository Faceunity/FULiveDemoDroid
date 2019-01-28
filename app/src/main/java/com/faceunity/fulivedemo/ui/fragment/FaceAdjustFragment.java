package com.faceunity.fulivedemo.ui.fragment;

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

import com.faceunity.fulivedemo.FUMagicGenActivity;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.renderer.MagicPhotoRenderer;

/**
 * @author LiuQiang on 2018.12.17
 * 五官调整页面
 */
public class FaceAdjustFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "FaceAdjustFragment";
    private float[] mPoints;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable longPressRunnable;
    private GestureDetectorCompat mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private MagicPhotoRenderer mMagicPhotoRenderer;
    private OnBackClickListener mOnBackClickListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMagicPhotoRenderer = ((FUMagicGenActivity) getActivity()).getMagicPhotoRenderer();
        if (mPoints != null) {
            mMagicPhotoRenderer.setShowLandmarks(true);
            mMagicPhotoRenderer.setViewPoints(mPoints);
        }
        View view = inflater.inflate(R.layout.fragment_face_adjust, container, false);
        view.findViewById(R.id.fl_back).setOnClickListener(this);
        view.setOnTouchListener(new TouchListener());
        longPressRunnable = new Runnable() {
            @Override
            public void run() {
                mMagicPhotoRenderer.clickLong();
            }
        };
        mGestureDetector = new GestureDetectorCompat(getActivity(), new GestureDetector.SimpleOnGestureListener() {
            private final int delayMillis = ViewConfiguration.getLongPressTimeout() + ViewConfiguration.getTapTimeout();

            @Override
            public boolean onDown(MotionEvent e) {
                if (mMagicPhotoRenderer.clickDown(e)) {
                    mHandler.postDelayed(longPressRunnable, delayMillis);
                }
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                mHandler.removeCallbacks(longPressRunnable);
                mMagicPhotoRenderer.translateM(distanceX, distanceY);
                return true;
            }
        });
        mGestureDetector.setIsLongpressEnabled(false);
        mScaleGestureDetector = new ScaleGestureDetector(getActivity(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mMagicPhotoRenderer.scaleM(detector.getFocusX(), detector.getFocusY(), detector.getScaleFactor());
                return true;
            }
        });
        return view;
    }

    public void setViewPoints(float[] points) {
        if (mMagicPhotoRenderer != null) {
            mMagicPhotoRenderer.setShowLandmarks(true);
            mMagicPhotoRenderer.setViewPoints(points);
        } else {
            mPoints = points;
        }
    }

    @Override
    public void onClick(View v) {
        // 返回贴纸页面
        mMagicPhotoRenderer.setShowLandmarks(false);
        ((FUMagicGenActivity) getActivity()).showFragment(FaceMarkFragment.TAG);
        if (mOnBackClickListener != null) {
            mOnBackClickListener.onClick(mMagicPhotoRenderer.getLandmarks());
        }
    }

    public void setOnBackClickListener(OnBackClickListener onBackClickListener) {
        mOnBackClickListener = onBackClickListener;
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
                mMagicPhotoRenderer.clickUp();
            }
            return true;
        }
    }

    public interface OnBackClickListener {
        /**
         * 精细调整点击返回
         *
         * @param landmarkPoints landmark 坐标系的点位
         */
        void onClick(float[] landmarkPoints);
    }

}
