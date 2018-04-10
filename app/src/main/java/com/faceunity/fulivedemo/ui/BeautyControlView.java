package com.faceunity.fulivedemo.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.core.OnFaceUnityControlListener;
import com.faceunity.fulivedemo.entity.FaceBeautyModel;
import com.faceunity.fulivedemo.entity.Filter;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tujh on 2017/8/15.
 */

public class BeautyControlView extends FrameLayout {
    private static final String TAG = BeautyControlView.class.getSimpleName();

    private Context mContext;

    private OnFaceUnityControlListener mOnFaceUnityControlListener;

    public void setOnFaceUnityControlListener(OnFaceUnityControlListener onFaceUnityControlListener) {
        mOnFaceUnityControlListener = onFaceUnityControlListener;
    }

    private int mSelectBeautyRadioId = 0;
    private TextView mSkinBeautyRadio;
    private TextView mFaceShapeRadio;
    private TextView mBeautyFilterRadio;
    private TextView mFilterRadio;
    private FrameLayout mBeautyMidLayout;

    private int mSelectSkinBeautyId = 0;
    private HorizontalScrollView mSkinBeautySelect;
    private LinearLayout mSkinBeautyAllBlurLinear;
    private ImageView mSkinBeautyAllBlurImg;
    private TextView mSkinBeautyAllBlurTxt;
    private LinearLayout mBeautyTypeLinear;
    private ImageView mBeautyTypeImg;
    private TextView mBeautyTypeTxt;
    private LinearLayout mSkinBeautyBlurLinear;
    private ImageView mSkinBeautyBlurImg;
    private TextView mSkinBeautyBlurTxt;
    private LinearLayout mSkinBeautyColorLinear;
    private ImageView mSkinBeautyColorImg;
    private TextView mSkinBeautyColorTxt;
    private LinearLayout mSkinBeautyRedLinear;
    private ImageView mSkinBeautyRedImg;
    private TextView mSkinBeautyRedTxt;
    private LinearLayout mBrightEyesLinear;
    private ImageView mBrightEyesImg;
    private TextView mBrightEyesTxt;
    private LinearLayout mBeautyTeethLinear;
    private ImageView mBeautyTeethImg;
    private TextView mBeautyTeethTxt;

    private int mSelectTypeBeautyId = 0;
    private HorizontalScrollView mFaceShapeSelect;
    private LinearLayout mFaceShapeLinear;
    private ImageView mFaceShapeImg;
    private TextView mFaceShapeTxt;
    private LinearLayout mEnlargeEyeLevelLinear;
    private ImageView mEnlargeEyeLevelImg;
    private TextView mEnlargeEyeLevelTxt;
    private LinearLayout mCheekthinLevelLinear;
    private ImageView mCheekthinLevelImg;
    private TextView mCheekthinLevelTxt;
    private LinearLayout mChinLevelLinear;
    private ImageView mChinLevelImg;
    private TextView mChinLevelTxt;
    private LinearLayout mForeheadLevelLinear;
    private ImageView mForeheadLevelImg;
    private TextView mForeheadLevelTxt;
    private LinearLayout mThinNoseLevelLinear;
    private ImageView mThinNoseLevelImg;
    private TextView mThinNoseLevelTxt;
    private LinearLayout mMouthShapeLinear;
    private ImageView mMouthShapeImg;
    private TextView mMouthShapeTxt;

    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private List<Filter> mBeautyFilters;
    private List<Filter> mFilters;

    private FrameLayout mBeautySeekBarLayout;
    private DiscreteSeekBar mBeautySeekBar;
    private static final List<Integer> FaceShapeIdList = Arrays.asList(R.id.face_shape_0_nvshen, R.id.face_shape_1_wanghong, R.id.face_shape_2_ziran, R.id.face_shape_3_default);
    private RadioGroup mFaceShapeRadioGroup;

    private FaceBeautyModel mFaceBeautyModel;
    private float mFaceBeautyALLBlurLevel = 1.0f;//精准磨皮
    private float mFaceBeautyType = 0.0f;//美肤类型
    private float mFaceBeautyBlurLevel = 0.7f;//磨皮
    private float mFaceBeautyColorLevel = 0.5f;//美白
    private float mFaceBeautyRedLevel = 0.5f;//红润
    private float mBrightEyesLevel = 1000.7f;//亮眼
    private float mBeautyTeethLevel = 1000.7f;//美牙

    private float mOpenFaceShape = 1.0f;
    private float mFaceBeautyFaceShape = 3.0f;//脸型
    private float mFaceBeautyEnlargeEye = 0.4f;//大眼
    private float mFaceBeautyCheekThin = 0.4f;//瘦脸
    private float mFaceBeautyEnlargeEye_old = 0.4f;//大眼
    private float mFaceBeautyCheekThin_old = 0.4f;//瘦脸
    private float mChinLevel = 0.3f;//下巴
    private float mForeheadLevel = 0.3f;//额头
    private float mThinNoseLevel = 0.5f;//瘦鼻
    private float mMouthShape = 0.4f;//嘴形

    private StringBuilder mToastStr;

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

        mFaceBeautyModel = mFaceBeautyModel.getInstance(context);
        mFaceBeautyALLBlurLevel = mFaceBeautyModel.getFaceBeautyALLBlurLevel();
        mFaceBeautyType = mFaceBeautyModel.getFaceBeautyType();
        mFaceBeautyBlurLevel = mFaceBeautyModel.getFaceBeautyBlurLevel();
        mFaceBeautyColorLevel = mFaceBeautyModel.getFaceBeautyColorLevel();
        mFaceBeautyRedLevel = mFaceBeautyModel.getFaceBeautyRedLevel();
        mBrightEyesLevel = mFaceBeautyModel.getBrightEyesLevel();
        mBeautyTeethLevel = mFaceBeautyModel.getBeautyTeethLevel();

        mOpenFaceShape = mFaceBeautyModel.getOpenFaceShape();
        mFaceBeautyFaceShape = mFaceBeautyModel.getFaceBeautyFaceShape();
        mFaceBeautyEnlargeEye = mFaceBeautyModel.getFaceBeautyEnlargeEye();
        mFaceBeautyCheekThin = mFaceBeautyModel.getFaceBeautyCheekThin();
        mFaceBeautyEnlargeEye_old = mFaceBeautyModel.getFaceBeautyEnlargeEye_old();
        mFaceBeautyCheekThin_old = mFaceBeautyModel.getFaceBeautyCheekThin_old();
        mChinLevel = mFaceBeautyModel.getChinLevel();
        mForeheadLevel = mFaceBeautyModel.getForeheadLevel();
        mThinNoseLevel = mFaceBeautyModel.getThinNoseLevel();
        mMouthShape = mFaceBeautyModel.getMouthShape();

        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);

        initView();
    }

    private void initView() {
        initViewBottomRadio();

        mBeautyMidLayout = (FrameLayout) findViewById(R.id.beauty_mid_layout);
        initViewSkinBeauty();
        initViewFaceShape();
        initViewFilter();

        initViewTop();
    }

    private void initViewBottomRadio() {
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isNeedBottomAnimator = mSelectBeautyRadioId == v.getId() || mSelectBeautyRadioId == 0 || v.getId() == 0;
                clickViewBottomRadio(v.getId());
                changeBottomLayoutAnimator(isNeedBottomAnimator);
            }
        };

        mFaceShapeRadio = (TextView) findViewById(R.id.beauty_radio_face_shape);
        mSkinBeautyRadio = (TextView) findViewById(R.id.beauty_radio_skin_beauty);
        mBeautyFilterRadio = (TextView) findViewById(R.id.beauty_radio_beauty_filter);
        mFilterRadio = (TextView) findViewById(R.id.beauty_radio_filter);
        mFaceShapeRadio.setOnClickListener(onClickListener);
        mSkinBeautyRadio.setOnClickListener(onClickListener);
        mBeautyFilterRadio.setOnClickListener(onClickListener);
        mFilterRadio.setOnClickListener(onClickListener);
    }

    private void initViewSkinBeauty() {
        mSkinBeautySelect = (HorizontalScrollView) findViewById(R.id.skin_beauty_select_block);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId = v.getId();
                boolean needChange = selectId == mSelectSkinBeautyId;
                mSelectSkinBeautyId = selectId;
                clickSkinBeautyBtn(needChange);
                changeBottomLayoutAnimator(false);
            }
        };
        mSkinBeautyAllBlurLinear = (LinearLayout) findViewById(R.id.beauty_all_blur_linear);
        mSkinBeautyAllBlurImg = (ImageView) findViewById(R.id.beauty_all_blur_linear_img);
        mSkinBeautyAllBlurTxt = (TextView) findViewById(R.id.beauty_all_blur_linear_txt);
        mSkinBeautyAllBlurLinear.setOnClickListener(onClickListener);
        mSkinBeautyAllBlurImg.setImageResource(mFaceBeautyALLBlurLevel == 0 ? R.drawable.beauty_skin_control_all_blur_normal : R.drawable.beauty_skin_control_all_blur_checked);
        mSkinBeautyAllBlurTxt.setTextColor(mFaceBeautyALLBlurLevel == 0 ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));

        mBeautyTypeLinear = (LinearLayout) findViewById(R.id.beauty_type_linear);
        mBeautyTypeImg = (ImageView) findViewById(R.id.beauty_type_linear_img);
        mBeautyTypeTxt = (TextView) findViewById(R.id.beauty_type_linear_txt);
        mBeautyTypeLinear.setOnClickListener(onClickListener);
        mBeautyTypeTxt.setText(mFaceBeautyType == 0 ? "清晰磨皮" : "朦胧磨皮");

        mSkinBeautyBlurLinear = (LinearLayout) findViewById(R.id.beauty_blur_linear);
        mSkinBeautyBlurImg = (ImageView) findViewById(R.id.beauty_blur_linear_img);
        mSkinBeautyBlurTxt = (TextView) findViewById(R.id.beauty_blur_linear_txt);
        mSkinBeautyBlurLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.beauty_blur_linear, mFaceBeautyBlurLevel);

        mSkinBeautyColorLinear = (LinearLayout) findViewById(R.id.beauty_color_linear);
        mSkinBeautyColorImg = (ImageView) findViewById(R.id.beauty_color_linear_img);
        mSkinBeautyColorTxt = (TextView) findViewById(R.id.beauty_color_linear_txt);
        mSkinBeautyColorLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.beauty_color_linear, mFaceBeautyColorLevel);

        mSkinBeautyRedLinear = (LinearLayout) findViewById(R.id.beauty_red_linear);
        mSkinBeautyRedImg = (ImageView) findViewById(R.id.beauty_red_linear_img);
        mSkinBeautyRedTxt = (TextView) findViewById(R.id.beauty_red_linear_txt);
        mSkinBeautyRedLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.beauty_red_linear, mFaceBeautyRedLevel);

        mBrightEyesLinear = (LinearLayout) findViewById(R.id.bright_eyes_linear);
        mBrightEyesImg = (ImageView) findViewById(R.id.bright_eyes_linear_img);
        mBrightEyesTxt = (TextView) findViewById(R.id.bright_eyes_linear_txt);
        mBrightEyesLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.bright_eyes_linear, mBrightEyesLevel);

        mBeautyTeethLinear = (LinearLayout) findViewById(R.id.beauty_teeth_linear);
        mBeautyTeethImg = (ImageView) findViewById(R.id.beauty_teeth_linear_img);
        mBeautyTeethTxt = (TextView) findViewById(R.id.beauty_teeth_linear_txt);
        mBeautyTeethLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.beauty_teeth_linear, mBeautyTeethLevel);
    }

    private void initViewFaceShape() {
        mFaceShapeSelect = (HorizontalScrollView) findViewById(R.id.face_shape_select_block);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectId = v.getId();
                boolean needChange = selectId == mSelectTypeBeautyId;
                mSelectTypeBeautyId = selectId;
                clickFaceShapeBtn(needChange);
                changeBottomLayoutAnimator(false);
            }
        };

        mFaceShapeLinear = (LinearLayout) findViewById(R.id.face_shape_linear);
        mFaceShapeImg = (ImageView) findViewById(R.id.face_shape_linear_img);
        mFaceShapeTxt = (TextView) findViewById(R.id.face_shape_linear_txt);
        mFaceShapeLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.face_shape_linear, mFaceBeautyFaceShape);

        mEnlargeEyeLevelLinear = (LinearLayout) findViewById(R.id.enlarge_eye_level_linear);
        mEnlargeEyeLevelImg = (ImageView) findViewById(R.id.enlarge_eye_level_linear_img);
        mEnlargeEyeLevelTxt = (TextView) findViewById(R.id.enlarge_eye_level_linear_txt);
        mEnlargeEyeLevelLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.enlarge_eye_level_linear, mOpenFaceShape == 1.0 ? mFaceBeautyEnlargeEye : mFaceBeautyEnlargeEye_old);

        mCheekthinLevelLinear = (LinearLayout) findViewById(R.id.cheekthin_level_linear);
        mCheekthinLevelImg = (ImageView) findViewById(R.id.cheekthin_level_linear_img);
        mCheekthinLevelTxt = (TextView) findViewById(R.id.cheekthin_level_linear_txt);
        mCheekthinLevelLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.cheekthin_level_linear, mOpenFaceShape == 1.0 ? mFaceBeautyCheekThin : mFaceBeautyCheekThin_old);

        mChinLevelLinear = (LinearLayout) findViewById(R.id.chin_level_linear);
        mChinLevelImg = (ImageView) findViewById(R.id.chin_level_linear_img);
        mChinLevelTxt = (TextView) findViewById(R.id.chin_level_linear_txt);
        mChinLevelLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.chin_level_linear, mChinLevel);

        mForeheadLevelLinear = (LinearLayout) findViewById(R.id.forehead_level_linear);
        mForeheadLevelImg = (ImageView) findViewById(R.id.forehead_level_linear_img);
        mForeheadLevelTxt = (TextView) findViewById(R.id.forehead_level_linear_txt);
        mForeheadLevelLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.forehead_level_linear, mForeheadLevel);

        mThinNoseLevelLinear = (LinearLayout) findViewById(R.id.thin_nose_level_linear);
        mThinNoseLevelImg = (ImageView) findViewById(R.id.thin_nose_level_linear_img);
        mThinNoseLevelTxt = (TextView) findViewById(R.id.thin_nose_level_linear_txt);
        mThinNoseLevelLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.thin_nose_level_linear, mThinNoseLevel);

        mMouthShapeLinear = (LinearLayout) findViewById(R.id.mouth_shape_linear);
        mMouthShapeImg = (ImageView) findViewById(R.id.mouth_shape_linear_img);
        mMouthShapeTxt = (TextView) findViewById(R.id.mouth_shape_linear_txt);
        mMouthShapeLinear.setOnClickListener(onClickListener);
        updateSeekBarBeautyView(false, R.id.mouth_shape_linear, mMouthShape);
    }

    private void initViewFilter() {
        mFilterRecyclerView = (RecyclerView) findViewById(R.id.effect_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
    }

    private void initViewTop() {
        mFaceShapeRadioGroup = (RadioGroup) findViewById(R.id.face_shape_radio_group);
        mFaceShapeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.face_shape_4) {
                    mChinLevelLinear.setVisibility(VISIBLE);
                    mForeheadLevelLinear.setVisibility(VISIBLE);
                    mThinNoseLevelLinear.setVisibility(VISIBLE);
                    mMouthShapeLinear.setVisibility(VISIBLE);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onOpenNewFaceShapeSelected(mOpenFaceShape = 1);
                } else {
                    mChinLevelLinear.setVisibility(GONE);
                    mForeheadLevelLinear.setVisibility(GONE);
                    mThinNoseLevelLinear.setVisibility(GONE);
                    mMouthShapeLinear.setVisibility(GONE);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onOpenNewFaceShapeSelected(mOpenFaceShape = 0);
                    float faceShape = FaceShapeIdList.indexOf(checkedId);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onFaceShapeSelected(faceShape);
                    mFaceBeautyModel.setFaceBeautyFaceShape(faceShape);
                }
                mFaceBeautyModel.setOpenFaceShape(mOpenFaceShape);
                if (checkedId == R.id.face_shape_3_default) {
                    mFaceShapeImg.setImageResource(R.drawable.beauty_type_control_face_shape_normal);
                    mFaceShapeTxt.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));
                } else {
                    mFaceShapeImg.setImageResource(R.drawable.beauty_type_control_face_shape_checked);
                    mFaceShapeTxt.setTextColor(getResources().getColor(R.color.main_color));
                }
            }
        });
        if (mOpenFaceShape == 0) {
            mFaceShapeRadioGroup.check(FaceShapeIdList.get((int) mFaceBeautyFaceShape));
            mChinLevelLinear.setVisibility(GONE);
            mForeheadLevelLinear.setVisibility(GONE);
            mThinNoseLevelLinear.setVisibility(GONE);
            mMouthShapeLinear.setVisibility(GONE);
        } else {
            mFaceShapeRadioGroup.check(R.id.face_shape_4);
            mChinLevelLinear.setVisibility(VISIBLE);
            mForeheadLevelLinear.setVisibility(VISIBLE);
            mThinNoseLevelLinear.setVisibility(VISIBLE);
            mMouthShapeLinear.setVisibility(VISIBLE);
        }

        mBeautySeekBarLayout = (FrameLayout) findViewById(R.id.beauty_seek_bar_layout);
        mBeautySeekBar = (DiscreteSeekBar) findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar SeekBar, int value, boolean fromUser) {
                float valueF = 1.0f * value / 100;
                if (mSelectBeautyRadioId == R.id.beauty_radio_skin_beauty) {
                    updateSeekBarBeautyView(false, mSelectSkinBeautyId, valueF);
                } else if (mSelectBeautyRadioId == R.id.beauty_radio_face_shape) {
                    if (mSelectTypeBeautyId == R.id.chin_level_linear || mSelectTypeBeautyId == R.id.forehead_level_linear || mSelectTypeBeautyId == R.id.mouth_shape_linear) {
                        valueF += 0.5f;
                    }
                    updateSeekBarBeautyView(false, mSelectTypeBeautyId, valueF);
                } else if (mSelectBeautyRadioId == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onFilterLevelSelected(valueF);
                } else if (mSelectBeautyRadioId == R.id.beauty_radio_beauty_filter) {
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

    private void seekBarBeautyView(boolean needChange, int viewId, float value) {
        boolean isNeedShowToast = false;
        mToastStr = new StringBuilder();
        mToastStr.reverse();
        if (needChange && value < 1000) {
            value = value + 1000;
            isNeedShowToast = true;
        } else if (value >= 1000) {
            value = value - 1000;
            isNeedShowToast = true;
        }

        updateSeekBarBeautyView(isNeedShowToast, viewId, value);

        int progress = (int) (value * 100);
        if (viewId == R.id.chin_level_linear || viewId == R.id.forehead_level_linear || viewId == R.id.mouth_shape_linear) {
            progress = (int) (value * 100 - 50);
        }
        if (value < 1000) {
            mBeautySeekBarLayout.setVisibility(VISIBLE);
            mBeautySeekBar.setProgress(progress);
            mToastStr.append(" 开启");
        } else {
            mToastStr.append(" 关闭");
        }
        if (isNeedShowToast) {
            mOnDescriptionShowListener.onDescriptionShowListener(mToastStr.toString());
        }
    }

    private void updateSeekBarBeautyView(boolean isNeedShowToast, int viewId, float value) {
        boolean isClose = value >= 1000;
        switch (viewId) {
            case R.id.beauty_blur_linear:
                mSkinBeautyBlurImg.setImageResource(isClose ? R.drawable.beauty_skin_control_blur_normal : R.drawable.beauty_skin_control_blur_checked);
                mSkinBeautyBlurTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setFaceBeautyBlurLevel(mFaceBeautyBlurLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onBlurLevelSelected(isClose ? 0 : mFaceBeautyBlurLevel);
                }
                if (isNeedShowToast) mToastStr.append("磨皮");
                break;
            case R.id.beauty_color_linear:
                mSkinBeautyColorImg.setImageResource(isClose ? R.drawable.beauty_skin_control_color_normal : R.drawable.beauty_skin_control_color_checked);
                mSkinBeautyColorTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setFaceBeautyColorLevel(mFaceBeautyColorLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onColorLevelSelected(isClose ? 0 : mFaceBeautyColorLevel);
                }
                if (isNeedShowToast) mToastStr.append("美白");
                break;
            case R.id.beauty_red_linear:
                mSkinBeautyRedImg.setImageResource(isClose ? R.drawable.beauty_skin_control_red_normal : R.drawable.beauty_skin_control_red_checked);
                mSkinBeautyRedTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setFaceBeautyRedLevel(mFaceBeautyRedLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onRedLevelSelected(isClose ? 0 : mFaceBeautyRedLevel);
                }
                if (isNeedShowToast) mToastStr.append("红润");
                break;
            case R.id.bright_eyes_linear:
                mBrightEyesImg.setImageResource(isClose ? R.drawable.beauty_skin_control_bright_eyes_normal : R.drawable.beauty_skin_control_bright_eyes_checked);
                mBrightEyesTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setBrightEyesLevel(mBrightEyesLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onBrightEyesSelected(isClose ? 0 : mBrightEyesLevel);
                }
                if (isNeedShowToast) mToastStr.append("亮眼");
                break;
            case R.id.beauty_teeth_linear:
                mBeautyTeethImg.setImageResource(isClose ? R.drawable.beauty_skin_control_teeth_normal : R.drawable.beauty_skin_control_teeth_checked);
                mBeautyTeethTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setBeautyTeethLevel(mBeautyTeethLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onBeautyTeethSelected(isClose ? 0 : mBeautyTeethLevel);
                }
                if (isNeedShowToast) mToastStr.append("美牙");
                break;
            case R.id.enlarge_eye_level_linear:
                mEnlargeEyeLevelImg.setImageResource(isClose ? R.drawable.beauty_type_control_enlarge_eye_level_normal : R.drawable.beauty_type_control_enlarge_eye_level_checked);
                mEnlargeEyeLevelTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                if (mOnFaceUnityControlListener != null) {
                    if (mOpenFaceShape == 1.0) {
                        mFaceBeautyModel.setFaceBeautyEnlargeEye(mFaceBeautyEnlargeEye = value);
                        mOnFaceUnityControlListener.onEnlargeEyeSelected(isClose ? 0 : mFaceBeautyEnlargeEye);
                    } else {
                        mFaceBeautyModel.setFaceBeautyEnlargeEye_old(mFaceBeautyEnlargeEye_old = value);
                        mOnFaceUnityControlListener.onEnlargeEyeSelected(isClose ? 0 : mFaceBeautyEnlargeEye_old);
                    }
                }
                if (isNeedShowToast) mToastStr.append("大眼");
                break;
            case R.id.cheekthin_level_linear:
                mCheekthinLevelImg.setImageResource(isClose ? R.drawable.beauty_type_control_cheekthin_level_normal : R.drawable.beauty_type_control_cheekthin_level_checked);
                mCheekthinLevelTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                if (mOnFaceUnityControlListener != null) {
                    if (mOpenFaceShape == 1.0) {
                        mFaceBeautyModel.setFaceBeautyCheekThin(mFaceBeautyCheekThin = value);
                        mOnFaceUnityControlListener.onCheekThinSelected(isClose ? 0 : mFaceBeautyCheekThin);
                    } else {
                        mFaceBeautyModel.setFaceBeautyCheekThin_old(mFaceBeautyCheekThin_old = value);
                        mOnFaceUnityControlListener.onCheekThinSelected(isClose ? 0 : mFaceBeautyCheekThin_old);
                    }
                }
                if (isNeedShowToast) mToastStr.append("瘦脸");
                break;
            case R.id.chin_level_linear:
                mChinLevelImg.setImageResource(isClose ? R.drawable.beauty_type_control_chin_level_normal : R.drawable.beauty_type_control_chin_level_checked);
                mChinLevelTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setChinLevel(mChinLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onChinLevelSelected(isClose ? 0.5f : mChinLevel);
                }
                if (isNeedShowToast) mToastStr.append("下巴");
                break;
            case R.id.forehead_level_linear:
                mForeheadLevelImg.setImageResource(isClose ? R.drawable.beauty_type_control_forehead_level_normal : R.drawable.beauty_type_control_forehead_level_checked);
                mForeheadLevelTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setForeheadLevel(mForeheadLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onForeheadLevelSelected(isClose ? 0.5f : mForeheadLevel);
                }
                if (isNeedShowToast) mToastStr.append("额头");
                break;
            case R.id.thin_nose_level_linear:
                mThinNoseLevelImg.setImageResource(isClose ? R.drawable.beauty_type_control_thin_nose_level_normal : R.drawable.beauty_type_control_thin_nose_level_checked);
                mThinNoseLevelTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setThinNoseLevel(mThinNoseLevel = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onThinNoseLevelSelected(isClose ? 0 : mThinNoseLevel);
                }
                if (isNeedShowToast) mToastStr.append("瘦鼻");
                break;
            case R.id.mouth_shape_linear:
                mMouthShapeImg.setImageResource(isClose ? R.drawable.beauty_type_control_mouth_shape_normal : R.drawable.beauty_type_control_mouth_shape_checked);
                mMouthShapeTxt.setTextColor(isClose ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mFaceBeautyModel.setMouthShape(mMouthShape = value);
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onMouthShapeSelected(isClose ? 0.5f : mMouthShape);
                }
                if (isNeedShowToast) mToastStr.append("嘴型");
                break;
        }
    }

    private void clickViewBottomRadio(int viewId) {
        if (mSelectBeautyRadioId == viewId) {
            mSelectBeautyRadioId = 0;
        } else {
            mSelectBeautyRadioId = viewId;
        }
        mFaceShapeRadio.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));
        mSkinBeautyRadio.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));
        mBeautyFilterRadio.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));
        mFilterRadio.setTextColor(getResources().getColor(R.color.main_color_c5c5c5));

        mBeautyMidLayout.setVisibility(GONE);
        mSkinBeautySelect.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);
        mFaceShapeSelect.setVisibility(GONE);

        mFaceShapeRadioGroup.setVisibility(GONE);
        mBeautySeekBarLayout.setVisibility(GONE);
        switch (mSelectBeautyRadioId) {
            case R.id.beauty_radio_skin_beauty:
                mBeautyMidLayout.setVisibility(VISIBLE);
                mSkinBeautySelect.setVisibility(VISIBLE);
                clickSkinBeautyBtn(false);
                mSkinBeautyRadio.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case R.id.beauty_radio_face_shape:
                mBeautyMidLayout.setVisibility(VISIBLE);
                mFaceShapeSelect.setVisibility(VISIBLE);
                clickFaceShapeBtn(false);
                mFaceShapeRadio.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case R.id.beauty_radio_beauty_filter:
                mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_BEAUTY_FILTER);
                mBeautyMidLayout.setVisibility(VISIBLE);
                mFilterRecyclerView.setVisibility(VISIBLE);
                if (mFilterTypeSelect == Filter.FILTER_TYPE_BEAUTY_FILTER) {
                    mBeautySeekBarLayout.setVisibility(VISIBLE);
                    mBeautySeekBar.setMin(0);
                    mBeautySeekBar.setMax(100);
                }
                mFilterRecyclerAdapter.setFilterProgress();
                mBeautyFilterRadio.setTextColor(getResources().getColor(R.color.main_color));
                break;
            case R.id.beauty_radio_filter:
                mFilterRecyclerAdapter.setFilterType(Filter.FILTER_TYPE_FILTER);
                mBeautyMidLayout.setVisibility(VISIBLE);
                mFilterRecyclerView.setVisibility(VISIBLE);
                if (mFilterTypeSelect == Filter.FILTER_TYPE_FILTER) {
                    mBeautySeekBarLayout.setVisibility(VISIBLE);
                    mBeautySeekBar.setMin(0);
                    mBeautySeekBar.setMax(100);
                }
                mFilterRecyclerAdapter.setFilterProgress();
                mFilterRadio.setTextColor(getResources().getColor(R.color.main_color));
                break;
        }
    }

    private void clickSkinBeautyBtn(boolean needChange) {
        mSkinBeautyAllBlurImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mBeautyTypeImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mSkinBeautyBlurImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mSkinBeautyColorImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mSkinBeautyRedImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mBrightEyesImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mBeautyTeethImg.setBackgroundColor(getResources().getColor(R.color.Transparent));

        mFaceShapeRadioGroup.setVisibility(GONE);
        mBeautySeekBarLayout.setVisibility(GONE);
        mBeautySeekBar.setMin(0);
        mBeautySeekBar.setMax(100);
        switch (mSelectSkinBeautyId) {
            case R.id.beauty_all_blur_linear:
                mSkinBeautyAllBlurImg.setBackgroundResource(R.drawable.control_beauty_select);
                mSkinBeautyAllBlurImg.setImageResource(mFaceBeautyALLBlurLevel == 1 ? R.drawable.beauty_skin_control_all_blur_normal : R.drawable.beauty_skin_control_all_blur_checked);
                mSkinBeautyAllBlurTxt.setTextColor(mFaceBeautyALLBlurLevel == 1 ? getResources().getColor(R.color.main_color_c5c5c5) : getResources().getColor(R.color.main_color));
                mOnDescriptionShowListener.onDescriptionShowListener(mFaceBeautyALLBlurLevel == 1 ? "精准美肤 关闭" : "精准美肤 开启");
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onALLBlurLevelSelected(mFaceBeautyALLBlurLevel = (mFaceBeautyALLBlurLevel == 1 ? 0 : 1));
                }
                mFaceBeautyModel.setFaceBeautyALLBlurLevel(mFaceBeautyALLBlurLevel);
                break;
            case R.id.beauty_type_linear:
                mBeautyTypeImg.setBackgroundResource(R.drawable.control_beauty_select);
                mBeautyTypeTxt.setText(mFaceBeautyType == 1 ? "清晰磨皮" : "朦胧磨皮");
                mOnDescriptionShowListener.onDescriptionShowListener(mFaceBeautyType == 1 ? "当前为 清晰磨皮 模式" : "当前为 朦胧磨皮 模式");
                if (mOnFaceUnityControlListener != null) {
                    mOnFaceUnityControlListener.onBeautySkinTypeSelected(mFaceBeautyType = (mFaceBeautyType == 1 ? 0 : 1));
                }
                mFaceBeautyModel.setFaceBeautyType(mFaceBeautyType);
                break;
            case R.id.beauty_blur_linear:
                mSkinBeautyBlurImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectSkinBeautyId, mFaceBeautyBlurLevel);
                break;
            case R.id.beauty_color_linear:
                mSkinBeautyColorImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectSkinBeautyId, mFaceBeautyColorLevel);
                break;
            case R.id.beauty_red_linear:
                mSkinBeautyRedImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectSkinBeautyId, mFaceBeautyRedLevel);
                break;
            case R.id.bright_eyes_linear:
                mBrightEyesImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectSkinBeautyId, mBrightEyesLevel);
                break;
            case R.id.beauty_teeth_linear:
                mBeautyTeethImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectSkinBeautyId, mBeautyTeethLevel);
                break;
        }
    }

    private void clickFaceShapeBtn(boolean needChange) {
        mFaceShapeImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mEnlargeEyeLevelImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mCheekthinLevelImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mChinLevelImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mForeheadLevelImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mThinNoseLevelImg.setBackgroundColor(getResources().getColor(R.color.Transparent));
        mMouthShapeImg.setBackgroundColor(getResources().getColor(R.color.Transparent));

        mFaceShapeRadioGroup.setVisibility(GONE);
        mBeautySeekBarLayout.setVisibility(GONE);
        if (mSelectTypeBeautyId == R.id.chin_level_linear || mSelectTypeBeautyId == R.id.forehead_level_linear || mSelectTypeBeautyId == R.id.mouth_shape_linear) {
            mBeautySeekBar.setMin(-50);
            mBeautySeekBar.setMax(50);
        } else {
            mBeautySeekBar.setMin(0);
            mBeautySeekBar.setMax(100);
        }

        switch (mSelectTypeBeautyId) {
            case R.id.face_shape_linear:
                mFaceShapeImg.setBackgroundResource(R.drawable.control_beauty_select);
                mFaceShapeRadioGroup.setVisibility(VISIBLE);
                break;
            case R.id.enlarge_eye_level_linear:
                mEnlargeEyeLevelImg.setBackgroundResource(R.drawable.control_beauty_select);
                if (mOpenFaceShape == 1.0) {
                    seekBarBeautyView(needChange, mSelectTypeBeautyId, mFaceBeautyEnlargeEye);
                } else {
                    seekBarBeautyView(needChange, mSelectTypeBeautyId, mFaceBeautyEnlargeEye_old);
                }
                break;
            case R.id.cheekthin_level_linear:
                mCheekthinLevelImg.setBackgroundResource(R.drawable.control_beauty_select);
                if (mOpenFaceShape == 1.0) {
                    seekBarBeautyView(needChange, mSelectTypeBeautyId, mFaceBeautyCheekThin);
                } else {
                    seekBarBeautyView(needChange, mSelectTypeBeautyId, mFaceBeautyCheekThin_old);
                }
                break;
            case R.id.chin_level_linear:
                mChinLevelImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectTypeBeautyId, mChinLevel);
                break;
            case R.id.forehead_level_linear:
                mForeheadLevelImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectTypeBeautyId, mForeheadLevel);
                break;
            case R.id.thin_nose_level_linear:
                mThinNoseLevelImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectTypeBeautyId, mThinNoseLevel);
                break;
            case R.id.mouth_shape_linear:
                mMouthShapeImg.setBackgroundResource(R.drawable.control_beauty_select);
                seekBarBeautyView(needChange, mSelectTypeBeautyId, mMouthShape);
                break;
        }
    }

    private int mPositionSelect = 0;
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
            if (mPositionSelect == position && filterType == mFilterTypeSelect) {
                holder.filterImg.setImageResource(R.drawable.control_filter_select);
            } else {
                holder.filterImg.setImageResource(0);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPositionSelect = position;
                    mFilterTypeSelect = filterType;
                    setFilterProgress();
                    notifyDataSetChanged();
                    mBeautySeekBarLayout.setVisibility(VISIBLE);
                    changeBottomLayoutAnimator(false);
                    if (mOnFaceUnityControlListener != null)
                        mOnFaceUnityControlListener.onFilterSelected(filters.get(mPositionSelect));
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
            mFaceBeautyModel.setFaceBeautyFilterLevel(getItems(mFilterTypeSelect).get(mPositionSelect).filterName(), filterLevels);
        }

        public void setFilterProgress() {
            mBeautySeekBar.setProgress((int) (100 * mFaceBeautyModel.getFaceBeautyFilterLevel(getItems(mFilterTypeSelect).get(mPositionSelect).filterName())));
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
        clickViewBottomRadio(0);
        changeBottomLayoutAnimator(true);
    }

    public interface OnDescriptionShowListener {
        void onDescriptionShowListener(String str);
    }

    public void setOnDescriptionShowListener(OnDescriptionShowListener onDescriptionShowListener) {
        mOnDescriptionShowListener = onDescriptionShowListener;
    }

    private OnDescriptionShowListener mOnDescriptionShowListener;
}