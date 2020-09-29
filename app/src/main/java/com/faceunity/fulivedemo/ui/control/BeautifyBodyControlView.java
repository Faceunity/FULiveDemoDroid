package com.faceunity.fulivedemo.ui.control;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.faceunity.OnFUControlListener;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.beautybox.BeautyBox;
import com.faceunity.fulivedemo.ui.beautybox.BeautyBoxGroup;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.DecimalUtils;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

/**
 * 美体 操作栏
 *
 * @author Richie on 2019.07.31
 */
public class BeautifyBodyControlView extends FrameLayout {
    private BeautyBoxGroup mBeautyBoxGroup;
    private BeautyBox mBbBodySlim;
    private BeautyBox mBbLegSlim;
    private BeautyBox mBbThinWaist;
    private BeautyBox mBbHipSlim;
    private BeautyBox mBbShoulder;
    private BeautyBox mBbHead;
    private BeautyBox mBbLegThin;
    private DiscreteSeekBar mSeekBar;
    private ImageView mIvRecover;
    private TextView mTvRecover;
    // 当前进度值
    private SparseArray<Float> mIntensitys = new SparseArray<>();
    // 进度值阈值
    private SparseArray<Float> mThreshHold = new SparseArray<>();
    // 进度值默认值
    private SparseArray<Float> mDefaultValues = new SparseArray<>();
    private OnFUControlListener mOnFUControlListener;

    public BeautifyBodyControlView(Context context) {
        this(context, null);
    }

    public BeautifyBodyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautifyBodyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        initView();
    }

    public void setOnFUControlListener(OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private void initView() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LayoutInflater.from(getContext()).inflate(R.layout.layout_beautify_body, this);

        mSeekBar = findViewById(R.id.seek_bar_beauty_body);
        mSeekBar.setOnProgressChangeListener(new ProgressChangeListener());

        mBeautyBoxGroup = findViewById(R.id.beauty_group_body);
        mBeautyBoxGroup.setOnCheckedChangeListener(new CheckChangeListener());
        mBbShoulder = mBeautyBoxGroup.findViewById(R.id.beauty_box_beauty_shoulder);
        mBbBodySlim = mBeautyBoxGroup.findViewById(R.id.beauty_box_body_slim);
        mBbLegSlim = mBeautyBoxGroup.findViewById(R.id.beauty_box_long_leg);
        mBbThinWaist = mBeautyBoxGroup.findViewById(R.id.beauty_box_thin_waist);
        mBbHipSlim = mBeautyBoxGroup.findViewById(R.id.beauty_box_hip_slim);
        mBbHead = mBeautyBoxGroup.findViewById(R.id.beauty_box_head_slim);
        mBbLegThin = mBeautyBoxGroup.findViewById(R.id.beauty_box_leg_thin_slim);

        mIvRecover = findViewById(R.id.iv_recover_body);
        mTvRecover = findViewById(R.id.tv_recover_body);
        ViewClickListener onClickListener = new ViewClickListener();
        mIvRecover.setOnClickListener(onClickListener);
        mTvRecover.setOnClickListener(onClickListener);
        setRecoverEnable(false);

        updateBeautyBoxState();
    }

    private void initData() {
        mThreshHold.put(R.id.beauty_box_body_slim, 0.0f);
        mThreshHold.put(R.id.beauty_box_long_leg, 0.0f);
        mThreshHold.put(R.id.beauty_box_thin_waist, 0.0f);
        mThreshHold.put(R.id.beauty_box_beauty_shoulder, 0.5f);
        mThreshHold.put(R.id.beauty_box_hip_slim, 0.0f);
        mThreshHold.put(R.id.beauty_box_head_slim, 0.0f);
        mThreshHold.put(R.id.beauty_box_leg_thin_slim, 0.0f);

        mIntensitys.put(R.id.beauty_box_body_slim, 0.0f);
        mIntensitys.put(R.id.beauty_box_long_leg, 0.0f);
        mIntensitys.put(R.id.beauty_box_thin_waist, 0.0f);
        mIntensitys.put(R.id.beauty_box_beauty_shoulder, 0.5f);
        mIntensitys.put(R.id.beauty_box_hip_slim, 0.0f);
        mIntensitys.put(R.id.beauty_box_head_slim, 0.0f);
        mIntensitys.put(R.id.beauty_box_leg_thin_slim, 0.0f);

        mDefaultValues.put(R.id.beauty_box_body_slim, 0.0f);
        mDefaultValues.put(R.id.beauty_box_long_leg, 0.0f);
        mDefaultValues.put(R.id.beauty_box_thin_waist, 0.0f);
        mDefaultValues.put(R.id.beauty_box_beauty_shoulder, 0.5f);
        mDefaultValues.put(R.id.beauty_box_hip_slim, 0.0f);
        mDefaultValues.put(R.id.beauty_box_head_slim, 0.0f);
        mDefaultValues.put(R.id.beauty_box_leg_thin_slim, 0.0f);
    }

    private void updateBeautyBoxState() {
        int childCount = mBeautyBoxGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            BeautyBox box = (BeautyBox) mBeautyBoxGroup.getChildAt(i);
            box.setOpen(isBeautyBodyOpen(box.getId()));
        }
        setSeekBarProgress(mBeautyBoxGroup.getCheckedBeautyBoxId());
    }

    private void setRecoverEnable(boolean enable) {
        if (enable) {
            mIvRecover.setAlpha(1f);
            mTvRecover.setAlpha(1f);
        } else {
            mIvRecover.setAlpha(0.6f);
            mTvRecover.setAlpha(0.6f);
        }
        mIvRecover.setEnabled(enable);
        mTvRecover.setEnabled(enable);
    }

    private boolean isBeautyBodyOpen(int boxId) {
        Float threshold = mThreshHold.get(boxId);
        Float intensity = mIntensitys.get(boxId);
        return !DecimalUtils.floatEquals(intensity, threshold);
    }

    private void setSeekBarProgress(int boxId) {
        Float value = mIntensitys.get(boxId);
        if (boxId != R.id.beauty_box_beauty_shoulder) {
            seekToSeekBar(value, 0, 100);
        } else {
            seekToSeekBar(value, -50, 50);
        }
    }

    private void seekToSeekBar(float value, int min, int max) {
        mSeekBar.setMin(min);
        mSeekBar.setMax(max);
        mSeekBar.setProgress((int) (value * (max - min) + min));
    }

    private boolean checkIfDefaultIntensity() {
        for (int i = 0, j = mDefaultValues.size(); i < j; i++) {
            if (!DecimalUtils.floatEquals(mDefaultValues.valueAt(i), mIntensitys.get(mDefaultValues.keyAt(i)))) {
                return false;
            }
        }
        return true;
    }

    private void setBodyParam(int checkedId, float intensity) {
        switch (checkedId) {
            case R.id.beauty_box_body_slim:
                mOnFUControlListener.setBodySlimIntensity(intensity);
                break;
            case R.id.beauty_box_long_leg:
                mOnFUControlListener.setLegSlimIntensity(intensity);
                break;
            case R.id.beauty_box_thin_waist:
                mOnFUControlListener.setWaistSlimIntensity(intensity);
                break;
            case R.id.beauty_box_beauty_shoulder:
                mOnFUControlListener.setShoulderSlimIntensity(intensity + 0.5f);
                break;
            case R.id.beauty_box_hip_slim:
                mOnFUControlListener.setHipSlimIntensity(intensity);
                break;
            case R.id.beauty_box_head_slim:
                mOnFUControlListener.setHeadSlimIntensity(intensity);
                break;
            case R.id.beauty_box_leg_thin_slim:
                mOnFUControlListener.setLegThinSlimIntensity(intensity);
                break;
            default:
        }
    }


    private class ProgressChangeListener implements DiscreteSeekBar.OnProgressChangeListener {

        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            if (!fromUser) {
                return;
            }

            float intensity = (float) value / 100;
            int checkedId = mBeautyBoxGroup.getCheckedBeautyBoxId();
            mIntensitys.put(checkedId, checkedId == R.id.beauty_box_beauty_shoulder ? intensity + 0.5f : intensity);
            setBodyParam(checkedId, intensity);
            boolean isDefault = checkIfDefaultIntensity();
            setRecoverEnable(!isDefault);
            switch (checkedId) {
                case R.id.beauty_box_body_slim:
                    mBbBodySlim.setOpen(isBeautyBodyOpen(R.id.beauty_box_body_slim));
                    break;
                case R.id.beauty_box_long_leg:
                    mBbLegSlim.setOpen(isBeautyBodyOpen(R.id.beauty_box_long_leg));
                    break;
                case R.id.beauty_box_thin_waist:
                    mBbThinWaist.setOpen(isBeautyBodyOpen(R.id.beauty_box_thin_waist));
                    break;
                case R.id.beauty_box_beauty_shoulder:
                    mBbShoulder.setOpen(isBeautyBodyOpen(R.id.beauty_box_beauty_shoulder));
                    break;
                case R.id.beauty_box_hip_slim:
                    mBbHipSlim.setOpen(isBeautyBodyOpen(R.id.beauty_box_hip_slim));
                    break;
                case R.id.beauty_box_head_slim:
                    mBbHead.setOpen(isBeautyBodyOpen(R.id.beauty_box_head_slim));
                    break;
                case R.id.beauty_box_leg_thin_slim:
                    mBbLegThin.setOpen(isBeautyBodyOpen(R.id.beauty_box_leg_thin_slim));
                    break;
                default:
            }
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

        }
    }

    private class CheckChangeListener implements BeautyBoxGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
            setSeekBarProgress(checkedId);
        }
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_recover_body || id == R.id.tv_recover_body) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getResources().getString(R.string.dialog_reset_avatar_model),
                        new BaseDialogFragment.OnClickListener() {
                            @Override
                            public void onConfirm() {
                                setRecoverEnable(false);
                                for (int i = 0, j = mDefaultValues.size(); i < j; i++) {
                                    int key = mDefaultValues.keyAt(i);
                                    Float value = mDefaultValues.valueAt(i);
                                    mIntensitys.put(key, value);
                                    setBodyParam(key, value);
                                }
                                updateBeautyBoxState();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                confirmDialogFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        }
    }

}
