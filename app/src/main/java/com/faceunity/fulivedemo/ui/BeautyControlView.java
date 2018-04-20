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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.core.OnFaceUnityControlListener;
import com.faceunity.fulivedemo.entity.Filter;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tujh on 2017/8/15.
 */

public class BeautyControlView extends FrameLayout {
    private static final String TAG = BeautyControlView.class.getSimpleName();

    public static final float FINAL_CHANE = 1000;

    private Context mContext;

    private OnFaceUnityControlListener mOnFaceUnityControlListener;

    public void setOnFaceUnityControlListener(@NonNull OnFaceUnityControlListener onFaceUnityControlListener) {
        mOnFaceUnityControlListener = onFaceUnityControlListener;
    }

    private CheckGroup mBottomCheckGroup;
    private FrameLayout mBeautyMidLayout;

    private HorizontalScrollView mSkinBeautySelect;
    private BeautyBoxGroup mSkinBeautyBoxGroup;

    private HorizontalScrollView mFaceShapeSelect;
    private BeautyBoxGroup mFaceShapeBeautyBoxGroup;
    private BeautyBox mFaceShapeBox;
    private BeautyBox mChinLevelBox;
    private BeautyBox mForeheadLevelBox;
    private BeautyBox mThinNoseLevelBox;
    private BeautyBox mMouthShapeBox;

    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private List<Filter> mBeautyFilters;
    private List<Filter> mFilters;

    private FrameLayout mBeautySeekBarLayout;
    private DiscreteSeekBar mBeautySeekBar;
    private static final List<Integer> FaceShapeIdList = Arrays.asList(R.id.face_shape_0_nvshen, R.id.face_shape_1_wanghong, R.id.face_shape_2_ziran, R.id.face_shape_3_default, R.id.face_shape_4);
    private RadioGroup mFaceShapeRadioGroup;

    private static final String FaceBeautyFilterLevel = "FaceBeautyFilterLevel_";
    private Map<String, Float> mFilterLevelIntegerMap = new HashMap<>();

    private float mFaceBeautyALLBlurLevel = 1.0f;//精准磨皮
    private float mFaceBeautyType = 0.0f;//美肤类型
    private float mFaceBeautyBlurLevel = 0.7f;//磨皮
    private float mFaceBeautyColorLevel = 0.5f;//美白
    private float mFaceBeautyRedLevel = 0.5f;//红润
    private float mBrightEyesLevel = 1000.7f;//亮眼
    private float mBeautyTeethLevel = 1000.7f;//美牙

    private float mFaceBeautyFaceShape = 4.0f;//脸型
    private float mFaceBeautyEnlargeEye = 0.4f;//大眼
    private float mFaceBeautyCheekThin = 0.4f;//瘦脸
    private float mFaceBeautyEnlargeEye_old = 0.4f;//大眼
    private float mFaceBeautyCheekThin_old = 0.4f;//瘦脸
    private float mChinLevel = 0.3f;//下巴
    private float mForeheadLevel = 0.3f;//额头
    private float mThinNoseLevel = 0.5f;//瘦鼻
    private float mMouthShape = 0.4f;//嘴形

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
        mSkinBeautyBoxGroup.check(View.NO_ID);
        mFaceShapeBeautyBoxGroup.check(View.NO_ID);
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

        mSkinBeautyBoxGroup = (BeautyBoxGroup) findViewById(R.id.beauty_box_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId, boolean isChecked) {
                mFaceShapeRadioGroup.setVisibility(GONE);
                mBeautySeekBarLayout.setVisibility(GONE);
                if (checkedId == R.id.beauty_all_blur_box) {
                    mFaceBeautyALLBlurLevel = isChecked ? 1 : 0;
                    setDescriptionShowStr(mFaceBeautyALLBlurLevel == 0 ? "精准美肤 关闭" : "精准美肤 开启");
                    onChangeFaceBeautyLevel(checkedId, mFaceBeautyALLBlurLevel);

                } else if (checkedId == R.id.beauty_type_box) {
                    mFaceBeautyType = isChecked ? 1 : 0;
                    setDescriptionShowStr(mFaceBeautyType == 0 ? "当前为 清晰磨皮 模式" : "当前为 朦胧磨皮 模式");
                    onChangeFaceBeautyLevel(checkedId, mFaceBeautyType);

                } else if (checkedId == R.id.beauty_blur_box) {
                    if (isChecked && mFaceBeautyBlurLevel >= FINAL_CHANE) {
                        mFaceBeautyBlurLevel -= FINAL_CHANE;
                        setDescriptionShowStr("磨皮 开启");
                    } else if (!isChecked && mFaceBeautyBlurLevel < FINAL_CHANE) {
                        mFaceBeautyBlurLevel += FINAL_CHANE;
                        setDescriptionShowStr("磨皮 关闭");
                    }
                    seekToSeekBar(mFaceBeautyBlurLevel);
                    onChangeFaceBeautyLevel(checkedId, mFaceBeautyBlurLevel);

                } else if (checkedId == R.id.beauty_color_box) {
                    if (isChecked && mFaceBeautyColorLevel >= FINAL_CHANE) {
                        mFaceBeautyColorLevel -= FINAL_CHANE;
                        setDescriptionShowStr("美白 开启");
                    } else if (!isChecked && mFaceBeautyColorLevel < FINAL_CHANE) {
                        mFaceBeautyColorLevel += FINAL_CHANE;
                        setDescriptionShowStr("美白 关闭");
                    }
                    seekToSeekBar(mFaceBeautyColorLevel);
                    onChangeFaceBeautyLevel(checkedId, mFaceBeautyColorLevel);

                } else if (checkedId == R.id.beauty_red_box) {
                    if (isChecked && mFaceBeautyRedLevel >= FINAL_CHANE) {
                        mFaceBeautyRedLevel -= FINAL_CHANE;
                        setDescriptionShowStr("红润 开启");
                    } else if (!isChecked && mFaceBeautyRedLevel < FINAL_CHANE) {
                        mFaceBeautyRedLevel += FINAL_CHANE;
                        setDescriptionShowStr("红润 关闭");
                    }
                    seekToSeekBar(mFaceBeautyRedLevel);
                    onChangeFaceBeautyLevel(checkedId, mFaceBeautyRedLevel);

                } else if (checkedId == R.id.beauty_bright_eyes_box) {
                    if (isChecked && mBrightEyesLevel >= FINAL_CHANE) {
                        mBrightEyesLevel -= FINAL_CHANE;
                        setDescriptionShowStr("亮眼 开启");
                    } else if (!isChecked && mBrightEyesLevel < FINAL_CHANE) {
                        mBrightEyesLevel += FINAL_CHANE;
                        setDescriptionShowStr("亮眼 关闭");
                    }
                    seekToSeekBar(mBrightEyesLevel);
                    onChangeFaceBeautyLevel(checkedId, mBrightEyesLevel);

                } else if (checkedId == R.id.beauty_teeth_box) {
                    if (isChecked && mBeautyTeethLevel >= FINAL_CHANE) {
                        mBeautyTeethLevel -= FINAL_CHANE;
                        setDescriptionShowStr("美牙 开启");
                    } else if (!isChecked && mBeautyTeethLevel < FINAL_CHANE) {
                        mBeautyTeethLevel += FINAL_CHANE;
                        setDescriptionShowStr("美牙 关闭");
                    }
                    seekToSeekBar(mBeautyTeethLevel);
                    onChangeFaceBeautyLevel(checkedId, mBeautyTeethLevel);

                }
                changeBottomLayoutAnimator(false);
            }
        });
    }

    private void updateViewSkinBeauty() {
        ((BeautyBox) findViewById(R.id.beauty_all_blur_box)).setChecked(mFaceBeautyALLBlurLevel == 1);
        ((BeautyBox) findViewById(R.id.beauty_type_box)).setChecked(mFaceBeautyType == 1);
        ((BeautyBox) findViewById(R.id.beauty_blur_box)).setChecked(mFaceBeautyBlurLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.beauty_color_box)).setChecked(mFaceBeautyColorLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.beauty_red_box)).setChecked(mFaceBeautyRedLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.beauty_bright_eyes_box)).setChecked(mBrightEyesLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.beauty_teeth_box)).setChecked(mBeautyTeethLevel < FINAL_CHANE);
    }

    private void initViewFaceShape() {
        mFaceShapeSelect = (HorizontalScrollView) findViewById(R.id.face_shape_select_block);

        mFaceShapeBeautyBoxGroup = (BeautyBoxGroup) findViewById(R.id.beauty_box_face_shape);
        mFaceShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId, boolean isChecked) {
                mFaceShapeRadioGroup.setVisibility(GONE);
                mBeautySeekBarLayout.setVisibility(GONE);
                if (checkedId == R.id.face_shape_box) {
                    mFaceShapeRadioGroup.setVisibility(VISIBLE);
                } else if (checkedId == R.id.enlarge_eye_level_box) {
                    if (mFaceBeautyFaceShape == 4) {
                        if (isChecked && mFaceBeautyEnlargeEye >= FINAL_CHANE) {
                            mFaceBeautyEnlargeEye -= FINAL_CHANE;
                            setDescriptionShowStr("大眼 开启");
                        } else if (!isChecked && mFaceBeautyEnlargeEye < FINAL_CHANE) {
                            mFaceBeautyEnlargeEye += FINAL_CHANE;
                            setDescriptionShowStr("大眼 关闭");
                        }
                        seekToSeekBar(mFaceBeautyEnlargeEye);
                        onChangeFaceBeautyLevel(checkedId, mFaceBeautyEnlargeEye);
                    } else {
                        if (isChecked && mFaceBeautyEnlargeEye_old >= FINAL_CHANE) {
                            mFaceBeautyEnlargeEye_old -= FINAL_CHANE;
                            setDescriptionShowStr("大眼 开启");
                        } else if (!isChecked && mFaceBeautyEnlargeEye_old < FINAL_CHANE) {
                            mFaceBeautyEnlargeEye_old += FINAL_CHANE;
                            setDescriptionShowStr("大眼 关闭");
                        }
                        seekToSeekBar(mFaceBeautyEnlargeEye_old);
                        onChangeFaceBeautyLevel(checkedId, mFaceBeautyEnlargeEye_old);
                    }
                } else if (checkedId == R.id.cheek_thin_level_box) {
                    if (mFaceBeautyFaceShape == 4) {
                        if (isChecked && mFaceBeautyCheekThin >= FINAL_CHANE) {
                            mFaceBeautyCheekThin -= FINAL_CHANE;
                            setDescriptionShowStr("瘦脸 开启");
                        } else if (!isChecked && mFaceBeautyCheekThin < FINAL_CHANE) {
                            mFaceBeautyCheekThin += FINAL_CHANE;
                            setDescriptionShowStr("瘦脸 关闭");
                        }
                        seekToSeekBar(mFaceBeautyCheekThin);
                        onChangeFaceBeautyLevel(checkedId, mFaceBeautyCheekThin);
                    } else {
                        if (isChecked && mFaceBeautyCheekThin_old >= FINAL_CHANE) {
                            mFaceBeautyCheekThin_old -= FINAL_CHANE;
                            setDescriptionShowStr("瘦脸 开启");
                        } else if (!isChecked && mFaceBeautyCheekThin_old < FINAL_CHANE) {
                            mFaceBeautyCheekThin_old += FINAL_CHANE;
                            setDescriptionShowStr("瘦脸 关闭");
                        }
                        seekToSeekBar(mFaceBeautyCheekThin_old);
                        onChangeFaceBeautyLevel(checkedId, mFaceBeautyCheekThin_old);
                    }
                } else if (checkedId == R.id.chin_level_box) {
                    if (isChecked && mChinLevel >= FINAL_CHANE) {
                        mChinLevel -= FINAL_CHANE;
                        setDescriptionShowStr("下巴 开启");
                    } else if (!isChecked && mChinLevel < FINAL_CHANE) {
                        mChinLevel += FINAL_CHANE;
                        setDescriptionShowStr("下巴 关闭");
                    }
                    seekToSeekBar(mChinLevel, -50, 50);
                    onChangeFaceBeautyLevel(checkedId, mChinLevel);

                } else if (checkedId == R.id.forehead_level_box) {
                    if (isChecked && mForeheadLevel >= FINAL_CHANE) {
                        mForeheadLevel -= FINAL_CHANE;
                        setDescriptionShowStr("额头 开启");
                    } else if (!isChecked && mForeheadLevel < FINAL_CHANE) {
                        mForeheadLevel += FINAL_CHANE;
                        setDescriptionShowStr("额头 关闭");
                    }
                    seekToSeekBar(mForeheadLevel, -50, 50);
                    onChangeFaceBeautyLevel(checkedId, mForeheadLevel);

                } else if (checkedId == R.id.thin_nose_level_box) {
                    if (isChecked && mThinNoseLevel >= FINAL_CHANE) {
                        mThinNoseLevel -= FINAL_CHANE;
                        setDescriptionShowStr("瘦鼻 开启");
                    } else if (!isChecked && mThinNoseLevel < FINAL_CHANE) {
                        mThinNoseLevel += FINAL_CHANE;
                        setDescriptionShowStr("瘦鼻 关闭");
                    }
                    seekToSeekBar(mThinNoseLevel);
                    onChangeFaceBeautyLevel(checkedId, mThinNoseLevel);

                } else if (checkedId == R.id.mouth_shape_box) {
                    if (isChecked && mMouthShape >= FINAL_CHANE) {
                        mMouthShape -= FINAL_CHANE;
                        setDescriptionShowStr("嘴形 开启");
                    } else if (!isChecked && mMouthShape < FINAL_CHANE) {
                        mMouthShape += FINAL_CHANE;
                        setDescriptionShowStr("嘴形 关闭");
                    }
                    seekToSeekBar(mMouthShape, -50, 50);
                    onChangeFaceBeautyLevel(checkedId, mMouthShape);

                }
                changeBottomLayoutAnimator(false);
            }
        });
        mFaceShapeBox = (BeautyBox) findViewById(R.id.face_shape_box);
        mChinLevelBox = (BeautyBox) findViewById(R.id.chin_level_box);
        mForeheadLevelBox = (BeautyBox) findViewById(R.id.forehead_level_box);
        mThinNoseLevelBox = (BeautyBox) findViewById(R.id.thin_nose_level_box);
        mMouthShapeBox = (BeautyBox) findViewById(R.id.mouth_shape_box);
    }

    private void updateViewFaceShape() {
        if (mFaceBeautyFaceShape == 4) {
            ((BeautyBox) findViewById(R.id.enlarge_eye_level_box)).setChecked(mFaceBeautyEnlargeEye < FINAL_CHANE);
            ((BeautyBox) findViewById(R.id.cheek_thin_level_box)).setChecked(mFaceBeautyCheekThin < FINAL_CHANE);
        } else {
            ((BeautyBox) findViewById(R.id.enlarge_eye_level_box)).setChecked(mFaceBeautyEnlargeEye_old < FINAL_CHANE);
            ((BeautyBox) findViewById(R.id.cheek_thin_level_box)).setChecked(mFaceBeautyCheekThin_old < FINAL_CHANE);
        }
        ((BeautyBox) findViewById(R.id.chin_level_box)).setChecked(mChinLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.forehead_level_box)).setChecked(mForeheadLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.thin_nose_level_box)).setChecked(mThinNoseLevel < FINAL_CHANE);
        ((BeautyBox) findViewById(R.id.mouth_shape_box)).setChecked(mMouthShape < FINAL_CHANE);

        if (mFaceBeautyFaceShape != 4) {
            mFaceShapeRadioGroup.check(FaceShapeIdList.get((int) mFaceBeautyFaceShape));
            mChinLevelBox.setVisibility(GONE);
            mForeheadLevelBox.setVisibility(GONE);
            mThinNoseLevelBox.setVisibility(GONE);
            mMouthShapeBox.setVisibility(GONE);
        } else {
            mFaceShapeRadioGroup.check(R.id.face_shape_4);
            mChinLevelBox.setVisibility(VISIBLE);
            mForeheadLevelBox.setVisibility(VISIBLE);
            mThinNoseLevelBox.setVisibility(VISIBLE);
            mMouthShapeBox.setVisibility(VISIBLE);
        }
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
                    mChinLevelBox.setVisibility(VISIBLE);
                    mForeheadLevelBox.setVisibility(VISIBLE);
                    mThinNoseLevelBox.setVisibility(VISIBLE);
                    mMouthShapeBox.setVisibility(VISIBLE);
                } else {
                    mChinLevelBox.setVisibility(GONE);
                    mForeheadLevelBox.setVisibility(GONE);
                    mThinNoseLevelBox.setVisibility(GONE);
                    mMouthShapeBox.setVisibility(GONE);
                }
                mFaceBeautyFaceShape = FaceShapeIdList.indexOf(checkedId);
                if (mOnFaceUnityControlListener != null)
                    mOnFaceUnityControlListener.onFaceShapeSelected(mFaceBeautyFaceShape);
                mFaceShapeBox.setChecked(checkedId != R.id.face_shape_3_default);
            }
        });

        mBeautySeekBarLayout = (FrameLayout) findViewById(R.id.beauty_seek_bar_layout);
        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                if (!fromUser) return;
                float valueF = 1.0f * (value - SeekBar.getMin()) / 100;
                if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_skin_beauty) {
                    onChangeFaceBeautyLevel(mSkinBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                } else if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_face_shape) {
                    onChangeFaceBeautyLevel(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                } else if (mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_beauty_filter || mBottomCheckGroup.getCheckedCheckBoxId() == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onFilterLevelSelected(valueF);
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

    private void updateTopView(int viewId) {
        mFaceShapeRadioGroup.setVisibility(GONE);
        mBeautySeekBarLayout.setVisibility(GONE);
        if (viewId == R.id.beauty_blur_box) {
            seekToSeekBar(mFaceBeautyBlurLevel);
        } else if (viewId == R.id.beauty_color_box) {
            seekToSeekBar(mFaceBeautyColorLevel);
        } else if (viewId == R.id.beauty_red_box) {
            seekToSeekBar(mFaceBeautyRedLevel);
        } else if (viewId == R.id.beauty_bright_eyes_box) {
            seekToSeekBar(mBrightEyesLevel);
        } else if (viewId == R.id.beauty_teeth_box) {
            seekToSeekBar(mBeautyTeethLevel);
        } else if (viewId == R.id.face_shape_box) {
            mFaceShapeRadioGroup.setVisibility(VISIBLE);
        } else if (viewId == R.id.enlarge_eye_level_box) {
            if (mFaceBeautyFaceShape == 4) seekToSeekBar(mFaceBeautyEnlargeEye);
            else seekToSeekBar(mFaceBeautyEnlargeEye_old);
        } else if (viewId == R.id.cheek_thin_level_box) {
            if (mFaceBeautyFaceShape == 4) seekToSeekBar(mFaceBeautyCheekThin);
            else seekToSeekBar(mFaceBeautyCheekThin_old);
        } else if (viewId == R.id.chin_level_box) {
            seekToSeekBar(mChinLevel, -50, 50);
        } else if (viewId == R.id.forehead_level_box) {
            seekToSeekBar(mForeheadLevel, -50, 50);
        } else if (viewId == R.id.thin_nose_level_box) {
            seekToSeekBar(mThinNoseLevel);
        } else if (viewId == R.id.mouth_shape_box) {
            seekToSeekBar(mMouthShape, -50, 50);
        }
    }

    private void onChangeFaceBeautyLevel(int viewId, float value) {
        boolean isClose = value >= 1000;
        if (viewId == R.id.beauty_all_blur_box) {
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onALLBlurLevelSelected(value);
        } else if (viewId == R.id.beauty_type_box) {
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onBeautySkinTypeSelected(value);
        } else if (viewId == R.id.beauty_blur_box) {
            mFaceBeautyBlurLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onBlurLevelSelected(isClose ? 0 : mFaceBeautyBlurLevel);
        } else if (viewId == R.id.beauty_color_box) {
            mFaceBeautyColorLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onColorLevelSelected(isClose ? 0 : mFaceBeautyColorLevel);
        } else if (viewId == R.id.beauty_red_box) {
            mFaceBeautyRedLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onRedLevelSelected(isClose ? 0 : mFaceBeautyRedLevel);
        } else if (viewId == R.id.beauty_bright_eyes_box) {
            mBrightEyesLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onBrightEyesSelected(isClose ? 0 : mBrightEyesLevel);
        } else if (viewId == R.id.beauty_teeth_box) {
            mBeautyTeethLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onBeautyTeethSelected(isClose ? 0 : mBeautyTeethLevel);
        } else if (viewId == R.id.enlarge_eye_level_box) {
            if (mFaceBeautyFaceShape == 4) {
                mFaceBeautyEnlargeEye = value;
                if (mOnFaceUnityControlListener != null)
                    mOnFaceUnityControlListener.onEnlargeEyeSelected(isClose ? 0 : mFaceBeautyEnlargeEye);
            } else {
                mFaceBeautyEnlargeEye_old = value;
                if (mOnFaceUnityControlListener != null)
                    mOnFaceUnityControlListener.onEnlargeEyeSelected(isClose ? 0 : mFaceBeautyEnlargeEye_old);
            }
        } else if (viewId == R.id.cheek_thin_level_box) {
            if (mFaceBeautyFaceShape == 4) {
                mFaceBeautyCheekThin = value;
                if (mOnFaceUnityControlListener != null)
                    mOnFaceUnityControlListener.onCheekThinSelected(isClose ? 0 : mFaceBeautyCheekThin);
            } else {
                mFaceBeautyCheekThin_old = value;
                if (mOnFaceUnityControlListener != null)
                    mOnFaceUnityControlListener.onCheekThinSelected(isClose ? 0 : mFaceBeautyCheekThin_old);
            }
        } else if (viewId == R.id.chin_level_box) {
            mChinLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onChinLevelSelected(isClose ? 0.5f : mChinLevel);

        } else if (viewId == R.id.forehead_level_box) {
            mForeheadLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onForeheadLevelSelected(isClose ? 0.5f : mForeheadLevel);

        } else if (viewId == R.id.thin_nose_level_box) {
            mThinNoseLevel = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onThinNoseLevelSelected(isClose ? 0 : mThinNoseLevel);

        } else if (viewId == R.id.mouth_shape_box) {
            mMouthShape = value;
            if (mOnFaceUnityControlListener != null)
                mOnFaceUnityControlListener.onMouthShapeSelected(isClose ? 0.5f : mMouthShape);

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
            updateTopView(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mBeautyMidLayout.setVisibility(VISIBLE);
            mFaceShapeSelect.setVisibility(VISIBLE);
            updateTopView(mFaceShapeBeautyBoxGroup.getCheckedBeautyBoxId());
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

    private void seekToSeekBar(float value) {
        seekToSeekBar(value, 0, 100);
    }

    private void seekToSeekBar(float value, int min, int max) {
        if (value < FINAL_CHANE) {
            mBeautySeekBarLayout.setVisibility(VISIBLE);
            mBeautySeekBar.setMin(min);
            mBeautySeekBar.setMax(max);
            mBeautySeekBar.setProgress((int) (value * (max - min) + min));
        }
    }

    public float getFaceBeautyFilterLevel(String filterName) {
        Float level = mFilterLevelIntegerMap.get(FaceBeautyFilterLevel + filterName);
        float l = level == null ? 1.0f : level;
        setFaceBeautyFilterLevel(filterName, l);
        return l;
    }

    public void setFaceBeautyFilterLevel(String filterName, float faceBeautyFilterLevel) {
        mFilterLevelIntegerMap.put(FaceBeautyFilterLevel + filterName, faceBeautyFilterLevel);
        if (mOnFaceUnityControlListener != null)
            mOnFaceUnityControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
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
                        mOnFaceUnityControlListener.onFilterSelected(filters.get(mFilterPositionSelect));
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
            setFaceBeautyFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName(), filterLevels);
        }

        public void setFilterProgress() {
            seekToSeekBar(getFaceBeautyFilterLevel(getItems(mFilterTypeSelect).get(mFilterPositionSelect).filterName()));
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
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning())
            mBottomLayoutAnimator.end();
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
}