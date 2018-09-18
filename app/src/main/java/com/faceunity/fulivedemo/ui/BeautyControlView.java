package com.faceunity.fulivedemo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;
import java.util.List;

import static com.faceunity.fulivedemo.entity.BeautyParameterModel.getValue;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isHeightPerformance;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpen;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sFilterLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sFilterName;
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

    private OnFUControlListener mOnFUControlListener;

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private CheckGroup mBottomCheckGroup;

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

    private DiscreteSeekBar mBeautySeekBar;
    private static final List<Integer> FaceShapeIdList = Arrays.asList(R.id.face_shape_0_nvshen, R.id.face_shape_1_wanghong, R.id.face_shape_2_ziran, R.id.face_shape_3_default, R.id.face_shape_4);
    private RelativeLayout mFaceShapeLayout;
    private View mFaceShapeCheckedLine;
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
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mBeautyFilters = FilterEnum.getFiltersByFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
        mFilters = FilterEnum.getFiltersByFilterType(Filter.FILTER_TYPE_FILTER);

        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);

        initView();
    }

    private void initView() {
        initViewBottomRadio();

        initViewSkinBeauty();
        initViewFaceShape();
        initViewFilterRecycler();

        initViewTop();
    }

    public void onResume() {
        updateViewSkinBeauty();
        updateViewFaceShape();
        updateViewFilterRecycler();
        hideBottomLayoutAnimator();
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = (CheckGroup) findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedId_old = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if ((checkedId == View.NO_ID || checkedId == checkedId_old) && checkedId_old != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x98);
                    int startHeight = getHeight();
                    changeBottomLayoutAnimator(startHeight, endHeight);
                } else if (checkedId != View.NO_ID && checkedId_old == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x98);
                    int endHeight = (int) getResources().getDimension(R.dimen.x366);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                }
                checkedId_old = checkedId;
            }
        });
    }

    private void initViewSkinBeauty() {
        mSkinBeautySelect = (HorizontalScrollView) findViewById(R.id.skin_beauty_select_block);

        mSkinBeautyBoxGroup = (BeautyBoxGroup) findViewById(R.id.beauty_group_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mFaceShapeLayout.setVisibility(GONE);
                mBeautySeekBar.setVisibility(GONE);
                if (checkedId != R.id.beauty_box_skin_detect) {
                    seekToSeekBar(checkedId);
                    onChangeFaceBeautyLevel(checkedId);
                }
            }
        });
        mBoxSkinDetect = (BeautyBox) findViewById(R.id.beauty_box_skin_detect);
        mBoxSkinDetect.setOnOpenChangeListener(new BeautyBox.OnOpenChangeListener() {
            @Override
            public void onOpenChanged(BeautyBox beautyBox, boolean isOpen) {
                sSkinDetect = isOpen ? 1 : 0;
                setDescriptionShowStr(sSkinDetect == 0 ? "精准美肤 关闭" : "精准美肤 开启");
                onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
            }
        });
        mBoxHeavyBlur = (BeautyBox) findViewById(R.id.beauty_box_heavy_blur);
        mBoxHeavyBlur.setOnDoubleChangeListener(new BeautyBox.OnDoubleChangeListener() {
            @Override
            public void onDoubleChanged(BeautyBox beautyBox, boolean isDouble) {
                sHeavyBlur = isDouble ? 1 : 0;
                setDescriptionShowStr(sHeavyBlur == 0 ? "当前为 清晰磨皮 模式" : "当前为 朦胧磨皮 模式");
                seekToSeekBar(R.id.beauty_box_heavy_blur);
                onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
                if (mOnFUControlListener != null)
                    mOnFUControlListener.onHeavyBlurSelected(sHeavyBlur);
            }
        });
        mBoxBlurLevel = (BeautyBox) findViewById(R.id.beauty_box_blur_level);
        mBoxColorLevel = (BeautyBox) findViewById(R.id.beauty_box_color_level);
        mBoxRedLevel = (BeautyBox) findViewById(R.id.beauty_box_red_level);
        mBoxEyeBright = (BeautyBox) findViewById(R.id.beauty_box_eye_bright);
        mBoxToothWhiten = (BeautyBox) findViewById(R.id.beauty_box_tooth_whiten);
    }

    private void updateViewSkinBeauty() {
        mBoxSkinDetect.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxHeavyBlur.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxBlurLevel.setVisibility(isHeightPerformance ? VISIBLE : GONE);
        mBoxEyeBright.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxToothWhiten.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        if (mOnFUControlListener != null)
            mOnFUControlListener.onHeavyBlurSelected(isHeightPerformance ? 1 : sHeavyBlur);
        onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
        onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
        onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright);
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten);
    }

    private void initViewFaceShape() {
        mFaceShapeSelect = (HorizontalScrollView) findViewById(R.id.face_shape_select_block);

        mFaceShapeBeautyBoxGroup = (BeautyBoxGroup) findViewById(R.id.beauty_group_face_shape);
        mFaceShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mFaceShapeLayout.setVisibility(GONE);
                mBeautySeekBar.setVisibility(GONE);
                if (checkedId == R.id.beauty_box_face_shape) {
                    mFaceShapeLayout.setVisibility(VISIBLE);
                    float faceShape = getValue(R.id.beauty_box_face_shape);
                    updateFaceShapeCheckedLine(FaceShapeIdList.get((int) faceShape));
                    mFaceShapeRadioGroup.check(FaceShapeIdList.get((int) faceShape));
                } else {
                    seekToSeekBar(checkedId);
                }
                onChangeFaceBeautyLevel(checkedId);
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

        mBoxIntensityChin.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mBoxIntensityForehead.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mBoxIntensityNose.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mBoxIntensityMouth.setVisibility(faceShape != 4 ? GONE : VISIBLE);
        mFaceShape4Radio.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        if (isHeightPerformance && mFaceShapeRadioGroup.getCheckedRadioButtonId() == R.id.face_shape_4) {
            mFaceShapeRadioGroup.check(R.id.face_shape_3_default);
        }
        onChangeFaceBeautyLevel(R.id.beauty_box_face_shape);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_chin);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_forehead);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_mouth);
    }

    private void initViewFilterRecycler() {
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
        ((SimpleItemAnimator) mFilterRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void updateViewFilterRecycler() {
        mFilterRecyclerAdapter.setFilter(sFilterName);
    }

    private void initViewTop() {
        mFaceShapeLayout = (RelativeLayout) findViewById(R.id.face_shape_radio_layout);
        mFaceShapeCheckedLine = findViewById(R.id.beauty_face_shape_checked_line);
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
                onChangeFaceBeautyLevel(R.id.beauty_box_face_shape);
                onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
                onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
                updateFaceShapeCheckedLine(checkedId);
            }
        });
        mFaceShape4Radio = (RadioButton) findViewById(R.id.face_shape_4);

        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_skin_beauty) {
                    setValue(mSkinBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
                } else if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_face_shape) {
                    setValue(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId());
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

    private void updateFaceShapeCheckedLine(final int checkedId) {
        mFaceShapeCheckedLine.post(new Runnable() {
            @Override
            public void run() {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mFaceShapeCheckedLine.getLayoutParams();
                int textWidth = radioButton == null || radioButton.getVisibility() == GONE ? 0 : (int) radioButton.getPaint().measureText(radioButton.getText().toString());
                params.width = textWidth;
                params.leftMargin = radioButton == null || radioButton.getVisibility() == GONE ? 0 : (radioButton.getLeft() + (radioButton.getWidth() - textWidth) / 2);
                mFaceShapeCheckedLine.setLayoutParams(params);
            }
        });
    }

    private void onChangeFaceBeautyLevel(int viewId) {
        if (viewId == View.NO_ID) return;
        ((BeautyBox) findViewById(viewId)).setOpen(isOpen(viewId));
        if (mOnFUControlListener == null) return;
        switch (viewId) {
            case R.id.beauty_box_skin_detect:
                mOnFUControlListener.onSkinDetectSelected(getValue(viewId));
                break;
            case R.id.beauty_box_heavy_blur:
                mOnFUControlListener.onBlurLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_blur_level:
                mOnFUControlListener.onBlurLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_color_level:
                mOnFUControlListener.onColorLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_red_level:
                mOnFUControlListener.onRedLevelSelected(getValue(viewId));
                break;
            case R.id.beauty_box_eye_bright:
                mOnFUControlListener.onEyeBrightSelected(getValue(viewId));
                break;
            case R.id.beauty_box_tooth_whiten:
                mOnFUControlListener.onToothWhitenSelected(getValue(viewId));
                break;
            case R.id.beauty_box_face_shape:
                mOnFUControlListener.onFaceShapeSelected(getValue(viewId));
                break;
            case R.id.beauty_box_eye_enlarge:
                mOnFUControlListener.onEyeEnlargeSelected(getValue(viewId));
                break;
            case R.id.beauty_box_cheek_thinning:
                mOnFUControlListener.onCheekThinningSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_chin:
                mOnFUControlListener.onIntensityChinSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_forehead:
                mOnFUControlListener.onIntensityForeheadSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_nose:
                mOnFUControlListener.onIntensityNoseSelected(getValue(viewId));
                break;
            case R.id.beauty_box_intensity_mouth:
                mOnFUControlListener.onIntensityMouthSelected(getValue(viewId));
                break;
        }
    }

    private void clickViewBottomRadio(int viewId) {
        mSkinBeautySelect.setVisibility(GONE);
        mFaceShapeSelect.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);

        mFaceShapeLayout.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mSkinBeautySelect.setVisibility(VISIBLE);
            int id = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id != R.id.beauty_box_skin_detect)
                seekToSeekBar(id);
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mFaceShapeSelect.setVisibility(VISIBLE);
            int id = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id == R.id.beauty_box_face_shape) {
                mFaceShapeLayout.setVisibility(VISIBLE);
            } else {
                seekToSeekBar(id);
            }
        } else if (viewId == R.id.beauty_radio_beauty_filter) {
            mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
            mFilterRecyclerView.setVisibility(VISIBLE);
            if (mFilterTypeSelect == Filter.FILTER_TYPE_BEAUTY_FILTER) {
                mFilterRecyclerAdapter.setFilterProgress();
            }
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_FILTER);
            mFilterRecyclerView.setVisibility(VISIBLE);
            if (mFilterTypeSelect == Filter.FILTER_TYPE_FILTER) {
                mFilterRecyclerAdapter.setFilterProgress();
            }
        }
    }

    private void seekToSeekBar(int checkedId) {
        if (checkedId == View.NO_ID) return;
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
        mBeautySeekBar.setVisibility(VISIBLE);
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
            holder.filterImg.setImageResource(filters.get(position).resId());
            holder.filterName.setText(filters.get(position).description());
            if (mFilterPositionSelect == position && filterType == mFilterTypeSelect) {
                holder.filterImg.setBackgroundResource(R.drawable.control_filter_select);
            } else {
                holder.filterImg.setBackgroundResource(0);
            }
            holder.itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFilterPositionSelect = position;
                    mFilterTypeSelect = filterType;
                    setFilterProgress();
                    notifyDataSetChanged();
                    mBeautySeekBar.setVisibility(VISIBLE);
                    if (mOnFUControlListener != null)
                        mOnFUControlListener.onFilterNameSelected(sFilterName = filters.get(mFilterPositionSelect));
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

        public void setFilter(Filter filter) {
            mFilterTypeSelect = filter.filterType();
            mFilterPositionSelect = getItems(mFilterTypeSelect).indexOf(filter);
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

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params == null) return;
                params.height = height;
                setLayoutParams(params);
                if (mOnBottomAnimatorChangeListener != null) {
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
        isHeightPerformance = isHP;
        updateViewSkinBeauty();
        updateViewFaceShape();
        mSkinBeautyBoxGroup.check(View.NO_ID);
        mFaceShapeBeautyBoxGroup.check(View.NO_ID);
    }

    public float getFilterLevel(String filterName) {
        Float level = sFilterLevel.get(sStrFilterLevel + filterName);
        float l = level == null ? 1.0f : level;
        setFilterLevel(filterName, l);
        return l;
    }

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        sFilterLevel.put(sStrFilterLevel + filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null)
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
    }
}