package com.faceunity.fulivedemo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.core.OnFaceUnityControlListener;
import com.faceunity.fulivedemo.entity.Filter;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;
import java.util.List;

import static com.faceunity.fulivedemo.entity.BeautyParameterModel.getValue;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isHeightPerformance;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpen;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenBlurLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenCheekThinning;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenCheekThinning_old;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenColorLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenEyeBright;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenEyeEnlarge;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenEyeEnlarge_old;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenIntensityChin;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenIntensityForehead;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenIntensityMouth;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenIntensityNose;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenRedLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpenToothWhiten;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sFaceShape;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sFilterLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sHeavyBlur;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sSkinDetect;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sStrFilterLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.setValue;

/**
 * Created by tujh on 2017/8/15.
 */

public class BeautyControlView extends FrameLayout {
    private static final String TAG = BeautyControlView.class.getSimpleName();

    private Context mContext;

    private OnFaceUnityControlListener mOnFaceUnityControlListener;

    public void setOnFaceUnityControlListener(@NonNull OnFaceUnityControlListener onFaceUnityControlListener) {
        mOnFaceUnityControlListener = onFaceUnityControlListener;
    }

    private CheckGroup mBottomCheckGroup;
    private FrameLayout mBeautyMidLayout;

    private HorizontalScrollView mSkinBeautySelect;
    private BeautyBoxGroup mSkinBeautyBoxGroup;
    private BeautyBox mBoxSkinDetect;
    private BeautyBox mBoxHeavyBlur;
    private BeautyBox mBoxBlurLevel;
    private BeautyBox mBoxColorLevel;
    private BeautyBox mBoxRedLevel;
    private BeautyBox mBoxEyeBright;
    private BeautyBox mBoxToothWhiten;

    private HorizontalScrollView mFaceShapeSelect;
    private BeautyBoxGroup mFaceShapeBeautyBoxGroup;
    private BeautyBox mBoxFaceShape;
    private BeautyBox mBoxEyeEnlarge;
    private BeautyBox mBoxCheekThinning;
    private BeautyBox mBoxIntensityChin;
    private BeautyBox mBoxIntensityForehead;
    private BeautyBox mBoxIntensityNose;
    private BeautyBox mBoxIntensityMouth;

    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private List<Filter> mBeautyFilters;
    private List<Filter> mFilters;

    private FrameLayout mBeautySeekBarLayout;
    private DiscreteSeekBar mBeautySeekBar;
    private static final List<Integer> FaceShapeIdList = Arrays.asList(R.id.face_shape_0_nvshen, R.id.face_shape_1_wanghong, R.id.face_shape_2_ziran, R.id.face_shape_3_default, R.id.face_shape_4);
    private RadioGroup mFaceShapeRadioGroup;
    private RadioButton mFaceShape4Radio;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mBeautyFilters = FilterEnum.getFiltersByFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
        mFilters = FilterEnum.getFiltersByFilterType(Filter.FILTER_TYPE_FILTER);

        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);

        initView();

        updateViewSkinBeauty();
        updateViewFaceShape();
    }

    private void initView() {
        initViewBottomRadio();

        mBeautyMidLayout = (FrameLayout) findViewById(R.id.beauty_mid_layout);
        initViewSkinBeauty();
        initViewFaceShape();
        initViewRecyclerView();

        initViewTop();
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = (CheckGroup) findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedId_old = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                changeBottomLayoutAnimator(checkedId_old == checkedId || checkedId_old == View.NO_ID || checkedId == View.NO_ID);
                checkedId_old = checkedId;
            }
        });
    }

    private void initViewSkinBeauty() {
        mSkinBeautySelect = (HorizontalScrollView) findViewById(R.id.skin_beauty_select_block);

        mSkinBeautyBoxGroup = (BeautyBoxGroup) findViewById(R.id.beauty_group_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId, boolean isChecked) {
                mFaceShapeRadioGroup.setVisibility(GONE);
                mBeautySeekBarLayout.setVisibility(GONE);
                changeParameterStatus(checkedId, isChecked);
                changeBottomLayoutAnimator(false);
            }
        });
        mBoxSkinDetect = (BeautyBox) findViewById(R.id.beauty_box_skin_detect);
        mBoxHeavyBlur = (BeautyBox) findViewById(R.id.beauty_box_heavy_blur);
        mBoxBlurLevel = (BeautyBox) findViewById(R.id.beauty_box_blur_level);
        mBoxColorLevel = (BeautyBox) findViewById(R.id.beauty_box_color_level);
        mBoxRedLevel = (BeautyBox) findViewById(R.id.beauty_box_red_level);
        mBoxEyeBright = (BeautyBox) findViewById(R.id.beauty_box_eye_bright);
        mBoxToothWhiten = (BeautyBox) findViewById(R.id.beauty_box_tooth_whiten);
    }

    private void updateViewSkinBeauty() {
        mBoxSkinDetect.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxSkinDetect.updateView(isOpen(R.id.beauty_box_skin_detect));
        onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect, getValue(R.id.beauty_box_skin_detect));

        mBoxHeavyBlur.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxHeavyBlur.updateView(isOpen(R.id.beauty_box_heavy_blur));
        onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur, getValue(R.id.beauty_box_heavy_blur));

        mBoxBlurLevel.updateView(isOpen(R.id.beauty_box_blur_level));
        onChangeFaceBeautyLevel(R.id.beauty_box_blur_level, getValue(R.id.beauty_box_blur_level));

        mBoxColorLevel.updateView(isOpen(R.id.beauty_box_color_level));
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level, getValue(R.id.beauty_box_color_level));

        mBoxRedLevel.updateView(isOpen(R.id.beauty_box_red_level));
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level, getValue(R.id.beauty_box_red_level));

        mBoxEyeBright.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxEyeBright.updateView(isOpen(R.id.beauty_box_eye_bright));
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright, getValue(R.id.beauty_box_eye_bright));

        mBoxToothWhiten.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxToothWhiten.updateView(isOpen(R.id.beauty_box_tooth_whiten));
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten, getValue(R.id.beauty_box_tooth_whiten));
    }

    private void initViewFaceShape() {
        mFaceShapeSelect = (HorizontalScrollView) findViewById(R.id.face_shape_select_block);

        mFaceShapeBeautyBoxGroup = (BeautyBoxGroup) findViewById(R.id.beauty_group_face_shape);
        mFaceShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId, boolean isChecked) {
                mFaceShapeRadioGroup.setVisibility(GONE);
                mBeautySeekBarLayout.setVisibility(GONE);
                if (checkedId == R.id.beauty_box_face_shape) {
                    mFaceShapeRadioGroup.setVisibility(VISIBLE);
                    float faceShape = getValue(R.id.beauty_box_face_shape);
                    mFaceShapeRadioGroup.check(FaceShapeIdList.get((int) faceShape));
                } else {
                    changeParameterStatus(checkedId, isChecked);
                }
                changeBottomLayoutAnimator(false);
            }
        });
        mBoxFaceShape = (BeautyBox) findViewById(R.id.beauty_box_face_shape);
        mBoxEyeEnlarge = (BeautyBox) findViewById(R.id.beauty_box_eye_enlarge);
        mBoxCheekThinning = (BeautyBox) findViewById(R.id.beauty_box_cheek_thinning);
        mBoxIntensityChin = (BeautyBox) findViewById(R.id.beauty_box_intensity_chin);
        mBoxIntensityForehead = (BeautyBox) findViewById(R.id.beauty_box_intensity_forehead);
        mBoxIntensityNose = (BeautyBox) findViewById(R.id.beauty_box_intensity_nose);
        mBoxIntensityMouth = (BeautyBox) findViewById(R.id.beauty_box_intensity_mouth);
    }

    private void updateViewFaceShape() {
        float faceShape = getValue(R.id.beauty_box_face_shape);
        mBoxFaceShape.updateView(faceShape != 3);
        mBoxEyeEnlarge.updateView(isOpen(R.id.beauty_box_eye_enlarge));
        mBoxCheekThinning.updateView(isOpen(R.id.beauty_box_cheek_thinning));

        mBoxIntensityChin.updateView(isOpen(R.id.beauty_box_intensity_chin));
        mBoxIntensityForehead.updateView(isOpen(R.id.beauty_box_intensity_forehead));
        mBoxIntensityNose.updateView(isOpen(R.id.beauty_box_intensity_nose));
        mBoxIntensityMouth.updateView(isOpen(R.id.beauty_box_intensity_mouth));

        mBoxIntensityChin.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mBoxIntensityForehead.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mBoxIntensityNose.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mBoxIntensityMouth.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mFaceShape4Radio.setVisibility(isHeightPerformance ? GONE : VISIBLE);

        onChangeFaceBeautyLevel(R.id.beauty_box_face_shape, getValue(R.id.beauty_box_face_shape));
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge, getValue(R.id.beauty_box_eye_enlarge));
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning, getValue(R.id.beauty_box_cheek_thinning));
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_chin, getValue(R.id.beauty_box_intensity_chin));
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_forehead, getValue(R.id.beauty_box_intensity_forehead));
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_nose, getValue(R.id.beauty_box_intensity_nose));
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_mouth, getValue(R.id.beauty_box_intensity_mouth));
    }

    private void initViewRecyclerView() {
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
    }

    private void initViewTop() {
        mFaceShapeRadioGroup = (RadioGroup) findViewById(R.id.face_shape_radio_group);
        mFaceShapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.face_shape_4) {
                    mBoxIntensityChin.setVisibility(VISIBLE);
                    mBoxIntensityForehead.setVisibility(VISIBLE);
                    mBoxIntensityNose.setVisibility(VISIBLE);
                    mBoxIntensityMouth.setVisibility(VISIBLE);
                } else {
                    mBoxIntensityChin.setVisibility(GONE);
                    mBoxIntensityForehead.setVisibility(GONE);
                    mBoxIntensityNose.setVisibility(GONE);
                    mBoxIntensityMouth.setVisibility(GONE);
                }
                float value = FaceShapeIdList.indexOf(checkedId);
                setValue(R.id.beauty_box_face_shape, value);
                onChangeFaceBeautyLevel(R.id.beauty_box_face_shape, value);
                onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge, getValue(R.id.beauty_box_eye_enlarge));
                mBoxEyeEnlarge.updateView(isOpen(R.id.beauty_box_eye_enlarge));
                onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning, getValue(R.id.beauty_box_cheek_thinning));
                mBoxCheekThinning.updateView(isOpen(R.id.beauty_box_cheek_thinning));
                mBoxFaceShape.setChecked(checkedId != R.id.face_shape_3_default);
            }
        });
        mFaceShape4Radio = (RadioButton) findViewById(R.id.face_shape_4);

        mBeautySeekBarLayout = (FrameLayout) findViewById(R.id.beauty_seek_bar_layout);
        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_skin_beauty) {
                    setValue(mSkinBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mSkinBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                } else if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_face_shape) {
                    setValue(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                } else if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_beauty_filter || mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar SeekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar SeekBar) {

            }
        });
    }

    private void onChangeFaceBeautyLevel(int viewId, float value) {
        if (mOnFaceUnityControlListener == null) return;
        switch (viewId) {
            case R.id.beauty_box_skin_detect:
                mOnFaceUnityControlListener.onSkinDetectSelected(value);
                break;
            case R.id.beauty_box_heavy_blur:
                mOnFaceUnityControlListener.onHeavyBlurSelected(value);
                break;
            case R.id.beauty_box_blur_level:
                mOnFaceUnityControlListener.onBlurLevelSelected(value);
                break;
            case R.id.beauty_box_color_level:
                mOnFaceUnityControlListener.onColorLevelSelected(value);
                break;
            case R.id.beauty_box_red_level:
                mOnFaceUnityControlListener.onRedLevelSelected(value);
                break;
            case R.id.beauty_box_eye_bright:
                mOnFaceUnityControlListener.onEyeBrightSelected(value);
                break;
            case R.id.beauty_box_tooth_whiten:
                mOnFaceUnityControlListener.onToothWhitenSelected(value);
                break;
            case R.id.beauty_box_face_shape:
                mOnFaceUnityControlListener.onFaceShapeSelected(value);
                break;
            case R.id.beauty_box_eye_enlarge:
                mOnFaceUnityControlListener.onEyeEnlargeSelected(value);
                break;
            case R.id.beauty_box_cheek_thinning:
                mOnFaceUnityControlListener.onCheekThinningSelected(value);
                break;
            case R.id.beauty_box_intensity_chin:
                mOnFaceUnityControlListener.onIntensityChinSelected(value);
                break;
            case R.id.beauty_box_intensity_forehead:
                mOnFaceUnityControlListener.onIntensityForeheadSelected(value);
                break;
            case R.id.beauty_box_intensity_nose:
                mOnFaceUnityControlListener.onIntensityNoseSelected(value);
                break;
            case R.id.beauty_box_intensity_mouth:
                mOnFaceUnityControlListener.onIntensityMouthSelected(value);
                break;
        }
    }

    private void clickViewBottomRadio(int viewId) {
        mBeautyMidLayout.setVisibility(GONE);
        mSkinBeautySelect.setVisibility(GONE);
        mFaceShapeSelect.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);

        mFaceShapeRadioGroup.setVisibility(GONE);
        mBeautySeekBarLayout.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mBeautyMidLayout.setVisibility(VISIBLE);
            mSkinBeautySelect.setVisibility(VISIBLE);
            int id = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id != R.id.beauty_box_skin_detect && id != R.id.beauty_box_heavy_blur)
                seekToSeekBar(id);
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mBeautyMidLayout.setVisibility(VISIBLE);
            mFaceShapeSelect.setVisibility(VISIBLE);
            int id = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id == R.id.beauty_box_face_shape) {
                mFaceShapeRadioGroup.setVisibility(VISIBLE);
            } else {
                seekToSeekBar(id);
            }
        } else if (viewId == R.id.beauty_radio_beauty_filter) {
            mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
            mBeautyMidLayout.setVisibility(VISIBLE);
            mFilterRecyclerView.setVisibility(VISIBLE);
            if (mFilterTypeSelect == Filter.FILTER_TYPE_BEAUTY_FILTER) {
                mFilterRecyclerAdapter.setFilterProgress();
            }
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_FILTER);
            mBeautyMidLayout.setVisibility(VISIBLE);
            mFilterRecyclerView.setVisibility(VISIBLE);
            if (mFilterTypeSelect == Filter.FILTER_TYPE_FILTER) {
                mFilterRecyclerAdapter.setFilterProgress();
            }
        }
    }

    private void seekToSeekBar(int checkedId) {
        if (checkedId == View.NO_ID || !isOpen(checkedId)) return;
        float value = getValue(checkedId);
        int min = 0;
        int max = 100;
        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead || checkedId == R.id.beauty_box_intensity_mouth) {
            min = -50;
            max = 50;
        }
        seekToSeekBar(value, min, max);
    }

    private void seekToSeekBar(float value) {
        seekToSeekBar(value, 0, 100);
    }

    private void seekToSeekBar(float value, int min, int max) {
        mBeautySeekBarLayout.setVisibility(VISIBLE);
        mBeautySeekBar.setMin(min);
        mBeautySeekBar.setMax(max);
        mBeautySeekBar.setProgress((int) (value * (max - min) + min));
    }

    private int mFilterPositionSelect = 0;
    private int mFilterTypeSelect = Filter.FILTER_TYPE_BEAUTY_FILTER;

    class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {

        int filterType;

        @Override
        public FilterRecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FilterRecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(FilterRecyclerAdapter.HomeRecyclerHolder holder, final int position) {
            final List<Filter> filters = getItems(filterType);
            holder.filterImg.setBackgroundResource(filters.get(position).resId());
            holder.filterName.setText(filters.get(position).description());
            if (mFilterPositionSelect == position && filterType == mFilterTypeSelect) {
                holder.filterImg.setImageResource(R.drawable.control_filter_select);
            } else {
                holder.filterImg.setImageResource(0);
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterPositionSelect = position;
                    mFilterTypeSelect = filterType;
                    setFilterProgress();
                    notifyDataSetChanged();
                    mBeautySeekBarLayout.setVisibility(VISIBLE);
                    changeBottomLayoutAnimator(false);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onFilterNameSelected(filters.get(mFilterPositionSelect));
                }
            });
        }

        @Override
        public int getItemCount() {
            return getItems(filterType).size();
        }

        public void setFilterType(int filterType) {
            this.filterType = filterType;
            notifyDataSetChanged();
        }


        public void setFilterLevels(float filterLevels) {
            setFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName(), filterLevels);
        }

        public void setFilterProgress() {
            seekToSeekBar(getFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName()));
        }

        public List<Filter> getItems(int type) {
            switch (type) {
                case Filter.FILTER_TYPE_BEAUTY_FILTER:
                    return mBeautyFilters;
                case Filter.FILTER_TYPE_FILTER:
                    return mFilters;
            }
            return mFilters;
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView filterImg;
            TextView filterName;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                filterImg = (ImageView) itemView.findViewById(R.id.control_recycler_img);
                filterName = (TextView) itemView.findViewById(R.id.control_recycler_text);
            }
        }
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private ValueAnimator mBottomLayoutAnimator;

    private void changeBottomLayoutAnimator(final boolean isNeedBottomAnimator) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        final int startHeight = getHeight();
        measure(0, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        final int endHeight = getMeasuredHeight();
        if (startHeight == endHeight) {
            return;
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(50);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) return;
                params.height = height;
                setLayoutParams(params);
                if (isNeedBottomAnimator && mOnBottomAnimatorChangeListener != null) {
                    float showRate = 1.0f * (height - startHeight) / (endHeight - startHeight);
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(startHeight > endHeight ? 1 - showRate : showRate);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    public void hideBottomLayoutAnimator() {
        mBottomCheckGroup.check(View.NO_ID);
    }

    public interface OnDescriptionShowListener {
        void onDescriptionShowListener(String str);

    }

    public void setOnDescriptionShowListener(OnDescriptionShowListener onDescriptionShowListener) {
        mOnDescriptionShowListener = onDescriptionShowListener;
    }

    private OnDescriptionShowListener mOnDescriptionShowListener;

    private void setDescriptionShowStr(String str) {
        if (mOnDescriptionShowListener != null)
            mOnDescriptionShowListener.onDescriptionShowListener(str);
    }

    public void setHeightPerformance(boolean isHP) {
        if (isHeightPerformance == isHP) return;
        isHeightPerformance = isHP;
        mFaceShapeRadioGroup.setVisibility(GONE);
        mBeautySeekBarLayout.setVisibility(GONE);
        updateViewSkinBeauty();
        updateViewFaceShape();
    }

    public void changeParameterStatus(int checkId, boolean isChecked) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                sSkinDetect = isChecked ? 1 : 0;
                setDescriptionShowStr(sSkinDetect == 0 ? "精准美肤 关闭" : "精准美肤 开启");
                break;
            case R.id.beauty_box_heavy_blur:
                sHeavyBlur = isChecked ? 1 : 0;
                setDescriptionShowStr(sHeavyBlur == 0 ? "当前为 清晰磨皮 模式" : "当前为 朦胧磨皮 模式");
                break;
            case R.id.beauty_box_blur_level:
                if (isChecked && !isOpenBlurLevel) {
                    isOpenBlurLevel = true;
                    setDescriptionShowStr("磨皮 开启");
                } else if (!isChecked && isOpenBlurLevel) {
                    isOpenBlurLevel = false;
                    setDescriptionShowStr("磨皮 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_color_level:
                if (isChecked && !isOpenColorLevel) {
                    isOpenColorLevel = true;
                    setDescriptionShowStr("美白 开启");
                } else if (!isChecked && isOpenColorLevel) {
                    isOpenColorLevel = false;
                    setDescriptionShowStr("美白 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_red_level:
                if (isChecked && !isOpenRedLevel) {
                    isOpenRedLevel = true;
                    setDescriptionShowStr("红润 开启");
                } else if (!isChecked && isOpenRedLevel) {
                    isOpenRedLevel = false;
                    setDescriptionShowStr("红润 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_eye_bright:
                if (isChecked && !isOpenEyeBright) {
                    isOpenEyeBright = true;
                    setDescriptionShowStr("亮眼 开启");
                } else if (!isChecked && isOpenEyeBright) {
                    isOpenEyeBright = false;
                    setDescriptionShowStr("亮眼 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_tooth_whiten:
                if (isChecked && !isOpenToothWhiten) {
                    isOpenToothWhiten = true;
                    setDescriptionShowStr("美牙 开启");
                } else if (!isChecked && isOpenToothWhiten) {
                    isOpenToothWhiten = false;
                    setDescriptionShowStr("美牙 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_eye_enlarge:
                if (sFaceShape == 4) {
                    if (isChecked && !isOpenEyeEnlarge) {
                        isOpenEyeEnlarge = true;
                        setDescriptionShowStr("大眼 开启");
                    } else if (!isChecked && isOpenEyeEnlarge) {
                        isOpenEyeEnlarge = false;
                        setDescriptionShowStr("大眼 关闭");
                    }
                } else {
                    if (isChecked && !isOpenEyeEnlarge_old) {
                        isOpenEyeEnlarge_old = true;
                        setDescriptionShowStr("大眼 开启");
                    } else if (!isChecked && isOpenEyeEnlarge_old) {
                        isOpenEyeEnlarge_old = false;
                        setDescriptionShowStr("大眼 关闭");
                    }
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_cheek_thinning:
                if (sFaceShape == 4) {
                    if (isChecked && !isOpenCheekThinning) {
                        isOpenCheekThinning = true;
                        setDescriptionShowStr("瘦脸 开启");
                    } else if (!isChecked && isOpenCheekThinning) {
                        isOpenCheekThinning = false;
                        setDescriptionShowStr("瘦脸 关闭");
                    }
                } else {
                    if (isChecked && !isOpenCheekThinning_old) {
                        isOpenCheekThinning_old = true;
                        setDescriptionShowStr("瘦脸 开启");
                    } else if (!isChecked && isOpenCheekThinning_old) {
                        isOpenCheekThinning_old = false;
                        setDescriptionShowStr("瘦脸 关闭");
                    }
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_intensity_chin:
                if (isChecked && !isOpenIntensityChin) {
                    isOpenIntensityChin = true;
                    setDescriptionShowStr("下巴 开启");
                } else if (!isChecked && isOpenIntensityChin) {
                    isOpenIntensityChin = false;
                    setDescriptionShowStr("下巴 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_intensity_forehead:
                if (isChecked && !isOpenIntensityForehead) {
                    isOpenIntensityForehead = true;
                    setDescriptionShowStr("额头 开启");
                } else if (!isChecked && isOpenIntensityForehead) {
                    isOpenIntensityForehead = false;
                    setDescriptionShowStr("额头 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_intensity_nose:
                if (isChecked && !isOpenIntensityNose) {
                    isOpenIntensityNose = true;
                    setDescriptionShowStr("鼻子 开启");
                } else if (!isChecked && isOpenIntensityNose) {
                    isOpenIntensityNose = false;
                    setDescriptionShowStr("鼻子 关闭");
                }
                seekToSeekBar(checkId);
                break;
            case R.id.beauty_box_intensity_mouth:
                if (isChecked && !isOpenIntensityMouth) {
                    isOpenIntensityMouth = true;
                    setDescriptionShowStr("嘴形 开启");
                } else if (!isChecked && isOpenIntensityMouth) {
                    isOpenIntensityMouth = false;
                    setDescriptionShowStr("嘴形 关闭");
                }
                seekToSeekBar(checkId);
                break;
        }
        onChangeFaceBeautyLevel(checkId, getValue(checkId));
    }

    public float getFilterLevel(String filterName) {
        Float level = sFilterLevel.get(sStrFilterLevel + filterName);
        float l = level == null ? 1.0f : level;
        setFilterLevel(filterName, l);
        return l;
    }

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        sFilterLevel.put(sStrFilterLevel + filterName, faceBeautyFilterLevel);
        if (mOnFaceUnityControlListener != null)
            mOnFaceUnityControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
    }
}