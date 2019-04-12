package com.faceunity.fulivedemo.ui.control;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
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
import com.faceunity.entity.FaceMakeup;
import com.faceunity.entity.Filter;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.entity.FaceMakeupEnum;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.BeautyBox;
import com.faceunity.fulivedemo.ui.BeautyBoxGroup;
import com.faceunity.fulivedemo.ui.CheckGroup;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

import java.util.Arrays;
import java.util.List;

import static com.faceunity.fulivedemo.entity.BeautyParameterModel.STR_FILTER_LEVEL;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.getValue;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isHeightPerformance;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.isOpen;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sFilterLevel;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sFilterName;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sHeavyBlur;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.sSkinDetect;
import static com.faceunity.fulivedemo.entity.BeautyParameterModel.setValue;

// TODO: 2019/3/25 0025 fix static import!

/**
 * Created by tujh on 2017/8/15.
 */
public class BeautyControlView extends FrameLayout {
    private static final String TAG = "BeautyControlView";
    private Context mContext;

    private OnFUControlListener mOnFUControlListener;
    private static final List<Integer> FACE_SHAPE_ID_LIST = Arrays.asList(R.id.face_shape_0_nvshen, R.id.face_shape_1_wanghong, R.id.face_shape_2_ziran, R.id.face_shape_3_default, R.id.face_shape_4);
    private RecyclerView mRvMakeupItems;

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private CheckGroup mBottomCheckGroup;

    private HorizontalScrollView mSkinBeautySelect;
    private BeautyBoxGroup mSkinBeautyBoxGroup;
    private BeautyBox mBoxSkinDetect;
    private BeautyBox mBoxHeavyBlur;
    private BeautyBox mBoxBlurLevel;
    private BeautyBox mBoxEyeBright;
    private BeautyBox mBoxToothWhiten;

    private HorizontalScrollView mFaceShapeSelect;
    private BeautyBoxGroup mFaceShapeBeautyBoxGroup;
    private BeautyBox mBoxIntensityChin;
    private BeautyBox mBoxIntensityForehead;
    private BeautyBox mBoxIntensityNose;
    private BeautyBox mBoxIntensityMouth;

    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private List<Filter> mFilters;

    private DiscreteSeekBar mBeautySeekBar;
    private FaceMakeupAdapter mFaceMakeupAdapter;
    private RelativeLayout mFaceShapeLayout;
    private View mFaceShapeCheckedLine;
    private RadioGroup mFaceShapeRadioGroup;
    private RadioButton mFaceShape4Radio;
    private boolean isShown;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // 默认选中第三个粉嫩
    private int mFilterPositionSelect = 2;

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mFilters = FilterEnum.getFiltersByFilterType();

        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);

        initView();
    }

    private void initView() {
        initViewBottomRadio();

        initViewSkinBeauty();
        initViewFaceShape();
        initViewFilterRecycler();
        initMakeupView();
        initViewTop();
    }

    public void onResume() {
        updateViewSkinBeauty();
        updateViewFaceShape();
        updateViewFilterRecycler();
        hideBottomLayoutAnimator();
    }

    private void initMakeupView() {
        mRvMakeupItems = findViewById(R.id.rv_face_makeup);
        mRvMakeupItems.setHasFixedSize(true);
        ((SimpleItemAnimator) mRvMakeupItems.getItemAnimator()).setSupportsChangeAnimations(false);
        mRvMakeupItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mRvMakeupItems.addItemDecoration(new VHSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.x15)));
        mFaceMakeupAdapter = new FaceMakeupAdapter(FaceMakeupEnum.getBeautyFaceMakeup());
        OnFaceMakeupClickListener onMpItemClickListener = new OnFaceMakeupClickListener();
        mFaceMakeupAdapter.setOnItemClickListener(onMpItemClickListener);
        mRvMakeupItems.setAdapter(mFaceMakeupAdapter);
        mFaceMakeupAdapter.setItemSelected(0);
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = (CheckGroup) findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            int checkedidOld = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if ((checkedId == View.NO_ID || checkedId == checkedidOld) && checkedidOld != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x98);
                    int startHeight = getHeight();
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedidOld == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x98);
                    int endHeight = (int) getResources().getDimension(R.dimen.x366);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = true;
                }
                checkedidOld = checkedId;
            }
        });
    }

    private void updateViewSkinBeauty() {
        mBoxSkinDetect.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxHeavyBlur.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxBlurLevel.setVisibility(isHeightPerformance ? VISIBLE : GONE);
        mBoxEyeBright.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        mBoxToothWhiten.setVisibility(isHeightPerformance ? GONE : VISIBLE);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onHeavyBlurSelected(isHeightPerformance ? 1 : sHeavyBlur);
        }
        onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
        if (isHeightPerformance) {
            onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
            onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        } else {
            if (BeautyParameterModel.sHeavyBlur == 0) {
                onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
            } else {
                onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
            }
        }
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright);
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten);
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
                setDescriptionShowStr(sSkinDetect == 0 ? R.string.beauty_box_skin_detect_close : R.string.beauty_box_skin_detect_open);
                onChangeFaceBeautyLevel(R.id.beauty_box_skin_detect);
            }
        });
        mBoxHeavyBlur = (BeautyBox) findViewById(R.id.beauty_box_heavy_blur);
        mBoxHeavyBlur.setOnDoubleChangeListener(new BeautyBox.OnDoubleChangeListener() {
            @Override
            public void onDoubleChanged(BeautyBox beautyBox, boolean isDouble) {
                sHeavyBlur = isDouble ? 1 : 0;
                setDescriptionShowStr(sHeavyBlur == 0 ? R.string.beauty_box_heavy_blur_normal_text : R.string.beauty_box_heavy_blur_double_text);
                seekToSeekBar(R.id.beauty_box_heavy_blur);
                onChangeFaceBeautyLevel(R.id.beauty_box_heavy_blur);
                if (mOnFUControlListener != null) {
                    mOnFUControlListener.onHeavyBlurSelected(sHeavyBlur);
                }
            }
        });
        mBoxBlurLevel = (BeautyBox) findViewById(R.id.beauty_box_blur_level);
        BeautyBox boxColorLevel = (BeautyBox) findViewById(R.id.beauty_box_color_level);
        BeautyBox boxRedLevel = (BeautyBox) findViewById(R.id.beauty_box_red_level);
        mBoxEyeBright = (BeautyBox) findViewById(R.id.beauty_box_eye_bright);
        mBoxToothWhiten = (BeautyBox) findViewById(R.id.beauty_box_tooth_whiten);
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
        mOnFUControlListener.onFilterNameSelected(sFilterName);
        float filterLevel = getFilterLevel(sFilterName.filterName());
        mOnFUControlListener.onFilterLevelSelected(filterLevel);
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
                    updateFaceShapeCheckedLine(FACE_SHAPE_ID_LIST.get((int) faceShape));
                    mFaceShapeRadioGroup.check(FACE_SHAPE_ID_LIST.get((int) faceShape));
                } else {
                    seekToSeekBar(checkedId);
                }
                onChangeFaceBeautyLevel(checkedId);
            }
        });
        BeautyBox boxFaceShape = (BeautyBox) findViewById(R.id.beauty_box_face_shape);
        BeautyBox boxEyeEnlarge = (BeautyBox) findViewById(R.id.beauty_box_eye_enlarge);
        BeautyBox boxCheekThinning = (BeautyBox) findViewById(R.id.beauty_box_cheek_thinning);
        mBoxIntensityChin = (BeautyBox) findViewById(R.id.beauty_box_intensity_chin);
        mBoxIntensityForehead = (BeautyBox) findViewById(R.id.beauty_box_intensity_forehead);
        mBoxIntensityNose = (BeautyBox) findViewById(R.id.beauty_box_intensity_nose);
        mBoxIntensityMouth = (BeautyBox) findViewById(R.id.beauty_box_intensity_mouth);
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
        if (viewId == View.NO_ID) {
            return;
        }
        ((BeautyBox) findViewById(viewId)).setOpen(isOpen(viewId));
        if (mOnFUControlListener == null) {
            return;
        }
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
            default:
        }
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
                float value = FACE_SHAPE_ID_LIST.indexOf(checkedId);
                setValue(R.id.beauty_box_face_shape, value);
                onChangeFaceBeautyLevel(R.id.beauty_box_face_shape);
                onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
                onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
                updateFaceShapeCheckedLine(checkedId);
            }
        });
        mFaceShape4Radio = (RadioButton) findViewById(R.id.face_shape_4);

        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                int checkedCheckBoxId = mBottomCheckGroup.getCheckedCheckBoxId();
                if (checkedCheckBoxId == R.id.beauty_radio_skin_beauty) {
                    setValue(mSkinBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
                } else if (checkedCheckBoxId == R.id.beauty_radio_face_shape) {
                    setValue(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                } else if (checkedCheckBoxId == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                } else if (checkedCheckBoxId == R.id.beauty_radio_face_beauty) {
                    // 整体妆容调节
                    float level = 1.0f * value / 100;
                    FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
                    String name = getResources().getString(faceMakeup.getNameId());
                    BeautyParameterModel.sBatchMakeupLevel.put(name, level);
                    List<MakeupItem> makeupItems = faceMakeup.getMakeupItems();
                    /* 数学公式，哈哈
                     * 0.4        0.7
                     * strength  level
                     * --> strength = 0.4 * level / 0.7
                     *   if level = 1.0, then strength = 0.57
                     *   if level = 0.2, then strength = 0.11
                     *   so, float strength = item.defaultLevel * level / DEFAULT_BATCH_MAKEUP_LEVEL
                     * */
                    if (makeupItems != null) {
                        for (MakeupItem makeupItem : makeupItems) {
                            float lev = makeupItem.getDefaultLevel() * level / FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
                            makeupItem.setLevel(lev);
                        }
                    }
                    mOnFUControlListener.onLightMakeupOverallLevelChanged(level);
                    mOnFUControlListener.onFilterLevelSelected(level);
                }
            }
        });
    }

    private void clickViewBottomRadio(int viewId) {
        mSkinBeautySelect.setVisibility(GONE);
        mFaceShapeSelect.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);
        mRvMakeupItems.setVisibility(GONE);

        mFaceShapeLayout.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mSkinBeautySelect.setVisibility(VISIBLE);
            int id = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id != R.id.beauty_box_skin_detect) {
                seekToSeekBar(id);
            }
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mFaceShapeSelect.setVisibility(VISIBLE);
            int id = mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            if (id == R.id.beauty_box_face_shape) {
                mFaceShapeLayout.setVisibility(VISIBLE);
            } else {
                seekToSeekBar(id);
            }
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerView.setVisibility(VISIBLE);
            mFilterRecyclerAdapter.setFilterProgress();
        } else if (viewId == R.id.beauty_radio_face_beauty) {
            mRvMakeupItems.setVisibility(VISIBLE);
            mBeautySeekBar.setVisibility(INVISIBLE);
            FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
            if (faceMakeup != null) {
                String name = getResources().getString(faceMakeup.getNameId());
                Float level = BeautyParameterModel.sBatchMakeupLevel.get(name);
                if (level == null) {
                    level = FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
                    BeautyParameterModel.sBatchMakeupLevel.put(name, level);
                }
                if (level != null) {
                    seekToSeekBar(level);
                }
            }
        }
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

    private void seekToSeekBar(int checkedId) {
        if (checkedId == View.NO_ID) {
            return;
        }

        float value = getValue(checkedId);
        int min = 0;
        int max = 100;
        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead || checkedId == R.id.beauty_box_intensity_mouth) {
            min = -50;
            max = 50;
        }
        seekToSeekBar(value, min, max);
    }

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
                if (params == null) {
                    return;
                }
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

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private ValueAnimator mBottomLayoutAnimator;

    private void setDescriptionShowStr(int str) {
        if (mOnDescriptionShowListener != null) {
            mOnDescriptionShowListener.onDescriptionShowListener(str);
        }
    }

    public void hideBottomLayoutAnimator() {
        mBottomCheckGroup.check(View.NO_ID);
    }

    public interface OnDescriptionShowListener {
        void onDescriptionShowListener(int str);
    }

    public void setOnDescriptionShowListener(OnDescriptionShowListener onDescriptionShowListener) {
        mOnDescriptionShowListener = onDescriptionShowListener;
    }

    private OnDescriptionShowListener mOnDescriptionShowListener;

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        sFilterLevel.put(STR_FILTER_LEVEL + filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
        }
    }

    public void setHeightPerformance(boolean isHP) {
        isHeightPerformance = isHP;
        updateViewSkinBeauty();
        updateViewFaceShape();
        mSkinBeautyBoxGroup.check(View.NO_ID);
        mFaceShapeBeautyBoxGroup.check(View.NO_ID);
    }

    public float getFilterLevel(String filterName) {
        String key = STR_FILTER_LEVEL + filterName;
        Float level = sFilterLevel.get(key);
        if (level == null) {
            level = Filter.DEFAULT_FILTER_LEVEL;
            sFilterLevel.put(key, level);
        }
        setFilterLevel(filterName, level);
        return level;
    }

    class FilterRecyclerAdapter extends RecyclerView.Adapter<FilterRecyclerAdapter.HomeRecyclerHolder> {

        @Override
        public FilterRecyclerAdapter.HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new FilterRecyclerAdapter.HomeRecyclerHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_beauty_control_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(FilterRecyclerAdapter.HomeRecyclerHolder holder, final int position) {
            final List<Filter> filters = mFilters;
            holder.filterImg.setImageResource(filters.get(position).resId());
            holder.filterName.setText(filters.get(position).description());
            if (mFilterPositionSelect == position) {
                holder.filterImg.setBackgroundResource(R.drawable.control_filter_select);
            } else {
                holder.filterImg.setBackgroundResource(0);
            }
            holder.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    mFilterPositionSelect = position;
                    mBeautySeekBar.setVisibility(position == 0 ? INVISIBLE : VISIBLE);
                    setFilterProgress();
                    notifyDataSetChanged();
                    if (mOnFUControlListener != null) {
                        sFilterName = filters.get(mFilterPositionSelect);
                        mOnFUControlListener.onFilterNameSelected(sFilterName);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFilters.size();
        }

        public void setFilterLevels(float filterLevels) {
            if (mFilterPositionSelect >= 0) {
                setFilterLevel(mFilters.get(mFilterPositionSelect).filterName(), filterLevels);
            }
        }

        public void setFilter(Filter filter) {
            mFilterPositionSelect = mFilters.indexOf(filter);
        }

        public int indexOf(Filter filter) {
            for (int i = 0; i < mFilters.size(); i++) {
                if (filter.filterName().equals(mFilters.get(i).filterName())) {
                    return i;
                }
            }
            return -1;
        }

        public void setFilterProgress() {
            if (mFilterPositionSelect >= 0) {
                seekToSeekBar(getFilterLevel(mFilters.get(mFilterPositionSelect).filterName()));
            }
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

    // ----------- 新添加的美妆组合

    // 美妆列表点击事件
    private class OnFaceMakeupClickListener implements BaseRecyclerAdapter.OnItemClickListener<FaceMakeup> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<FaceMakeup> adapter, View view, int position) {
            FaceMakeup faceMakeup = adapter.getItem(position);
            if (position == 0) {
                // 卸妆
                mBeautySeekBar.setVisibility(View.INVISIBLE);
//                Filter origin = mFilters.get(0);
//                mOnFUControlListener.onFilterNameSelected(origin);
//                setFilterLevel(origin.filterName(), 0);
                int old = mFilterPositionSelect;
                mFilterPositionSelect = -1;
                mFilterRecyclerAdapter.notifyItemChanged(old);
            } else {
                // 各个妆容
                mBeautySeekBar.setVisibility(View.VISIBLE);
                String name = getResources().getString(faceMakeup.getNameId());
                Float level = BeautyParameterModel.sBatchMakeupLevel.get(name);
                boolean used = true;
                if (level == null) {
                    used = false;
                    level = FaceMakeupEnum.MAKEUP_OVERALL_LEVEL.get(faceMakeup.getNameId());
                    BeautyParameterModel.sBatchMakeupLevel.put(name, level);
                }
                seekToSeekBar(level);
                mOnFUControlListener.onLightMakeupOverallLevelChanged(level);

                Pair<Filter, Float> filterFloatPair = FaceMakeupEnum.MAKEUP_FILTERS.get(faceMakeup.getNameId());
                if (filterFloatPair != null) {
                    // 滤镜调整到对应的位置，没有就不做
                    Filter filter = filterFloatPair.first;
                    int i = mFilterRecyclerAdapter.indexOf(filter);
                    if (i >= 0) {
                        mFilterPositionSelect = i;
                        mFilterRecyclerAdapter.notifyItemChanged(i);
                        mFilterRecyclerView.scrollToPosition(i);
                    } else {
                        int old = mFilterPositionSelect;
                        mFilterPositionSelect = -1;
                        mFilterRecyclerAdapter.notifyItemChanged(old);
                    }
                    mOnFUControlListener.onFilterNameSelected(filter);
                    Float filterLevel = used ? level : filterFloatPair.second;
                    sFilterName = filter;
                    String filterName = filter.filterName();
                    sFilterLevel.put(STR_FILTER_LEVEL + filterName, filterLevel);
                    setFilterLevel(filterName, filterLevel);
                }
            }
            List<MakeupItem> makeupItems = faceMakeup.getMakeupItems();
            mOnFUControlListener.onLightMakeupBatchSelected(makeupItems);
        }
    }

    // 妆容组合适配器
    private class FaceMakeupAdapter extends BaseRecyclerAdapter<FaceMakeup> {

        FaceMakeupAdapter(@NonNull List<FaceMakeup> data) {
            super(data, R.layout.layout_rv_makeup);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, FaceMakeup item) {
            viewHolder.setText(R.id.tv_makeup, getResources().getString(item.getNameId()))
                    .setImageResource(R.id.iv_makeup, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, FaceMakeup data, boolean selected) {
            ((TextView) viewHolder.getViewById(R.id.tv_makeup)).setTextColor(selected ?
                    getResources().getColor(R.color.main_color) : getResources().getColor(R.color.colorWhite));
            viewHolder.setBackground(R.id.iv_makeup, selected ? R.drawable.control_filter_select : 0);
        }
    }

}