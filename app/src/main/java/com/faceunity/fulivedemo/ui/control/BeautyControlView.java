package com.faceunity.fulivedemo.ui.control;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.entity.FilterEnum;
import com.faceunity.fulivedemo.ui.CheckGroup;
import com.faceunity.fulivedemo.ui.TouchStateImageView;
import com.faceunity.fulivedemo.ui.beautybox.BaseBeautyBox;
import com.faceunity.fulivedemo.ui.beautybox.BeautyBoxGroup;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.DecimalUtils;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.param.BeautificationParam;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 美颜
 * Created by tujh on 2017/8/15.
 */
public class BeautyControlView extends FrameLayout implements TouchStateImageView.OnTouchStateListener {
    private static final String TAG = "BeautyControlView";
    private Context mContext;
    private OnFUControlListener mOnFUControlListener;
    private BeautyBoxGroup mBbgStyle;
    private View mClStyleNone;
    private BeautyBoxGroup.OnCheckedChangeListener mOnFaceStyleCheckedChangeListener;

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private CheckGroup mBottomCheckGroup;
    private FrameLayout mFlFaceSkinItems;
    private BeautyBoxGroup mSkinBeautyBoxGroup;
    private BeautyBoxGroup mShapeBeautyBoxGroup;
    private FrameLayout mFlFaceShapeItems;
    private FrameLayout mFlFaceStyleItems;
    private ImageView mIvRecoverFaceShape;
    private TextView mTvRecoverFaceShape;
    private ImageView mIvRecoverFaceSkin;
    private TextView mTvRecoverFaceSkin;
    private View mBottomView;
    private RecyclerView mFilterRecyclerView;
    private FilterRecyclerAdapter mFilterRecyclerAdapter;
    private DiscreteSeekBar mBeautySeekBar;
    private TouchStateImageView mIvCompare;
    private boolean isShown;
    private final List<Filter> mFilters;
    // 默认选中第三个粉嫩
    private int mFilterPositionSelect = 2;
    private boolean mEnableBottomRationClick = true;

    public BeautyControlView(Context context) {
        this(context, null);
    }

    public BeautyControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeautyControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mFilters = FilterEnum.getFiltersByFilterType();
        LayoutInflater.from(context).inflate(R.layout.layout_beauty_control, this);
        initView();
    }

    private void initView() {
        mBottomView = findViewById(R.id.cl_bottom_view);
        mBottomView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mIvCompare = findViewById(R.id.iv_compare);
        mIvCompare.setOnTouchStateListener(this);
        initViewBottomRadio();
        initViewSkinBeauty();
        initViewFaceShape();
        initViewFaceStyle();
        initViewFilterRecycler();
        initViewTop();
        BeautyParameterModel.initConfigMap(mContext);
    }

    public void onResume() {
        updateViewSkinBeauty();
        updateViewFaceShape();
        updateViewFilterRecycler();
        updateViewFaceStyle();
        hideBottomLayoutAnimator();
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    private void initViewBottomRadio() {
        mBottomCheckGroup = findViewById(R.id.beauty_radio_group);
        mBottomCheckGroup.setOnDispatchActionUpListener(new CheckGroup.OnDispatchActionUpListener() {

            @Override
            public void onDispatchActionUp(int x) {
                if (!mEnableBottomRationClick) {
                    int width = mBottomCheckGroup.getMeasuredWidth();
                    if (x < width * 0.25) {
                        String toastStr = mContext.getString(R.string.beauty_face_style_toast, mContext.getString(R.string.beauty_radio_skin_beauty));
                        ToastUtil.showNormalToast(mContext, toastStr);
                    } else if (x < width * 0.5) {
                        String toastStr = mContext.getString(R.string.beauty_face_style_toast, mContext.getString(R.string.beauty_radio_face_shape));
                        ToastUtil.showNormalToast(mContext, toastStr);
                    } else if (x < width * 0.75f) {
                        String toastStr = mContext.getString(R.string.beauty_face_style_toast, mContext.getString(R.string.beauty_radio_filter));
                        ToastUtil.showNormalToast(mContext, toastStr);
                    }
                }
            }
        });
        mBottomCheckGroup.setOnCheckedChangeListener(new CheckGroup.OnCheckedChangeListener() {
            private int checkedIdOld = View.NO_ID;

            @Override
            public void onCheckedChanged(CheckGroup group, int checkedId) {
                clickViewBottomRadio(checkedId);
                if (checkedId != View.NO_ID) {
                    switch (checkedId) {
                        case R.id.beauty_radio_skin_beauty: {
                            BeautyParameterModel.sBackupParams.clear();
                            seekToSeekBar(mSkinBeautyBoxGroup.getCheckedBeautyBoxId());
                        }
                        break;
                        case R.id.beauty_radio_face_shape: {
                            BeautyParameterModel.sBackupParams.clear();
                            seekToSeekBar(mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                        }
                        break;
                        case R.id.beauty_radio_filter: {
                            BeautyParameterModel.sBackupParams.clear();
                            Float valueObj = BeautyParameterModel.sFilterLevel.get(BeautyParameterModel.STR_FILTER_LEVEL + BeautyParameterModel.sFilter.getName());
                            if (valueObj == null) {
                                valueObj = BeautyParameterModel.DEFAULT_FILTER_LEVEL;
                            }
                            if (mFilterPositionSelect > 0) {
                                seekToSeekBar(valueObj);
                            } else {
                                mBeautySeekBar.setVisibility(INVISIBLE);
                            }
                        }
                        break;
                        case R.id.beauty_radio_style: {
                            BeautyParameterModel.backupParams();
                        }
                        break;
                        default:
                    }
                }
                if ((checkedId == View.NO_ID || checkedId == checkedIdOld) && checkedIdOld != View.NO_ID) {
                    int endHeight = (int) getResources().getDimension(R.dimen.x1);
                    int startHeight = (int) getResources().getDimension(R.dimen.x268);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    mIvCompare.setVisibility(INVISIBLE);
                    isShown = false;
                } else if (checkedId != View.NO_ID && checkedIdOld == View.NO_ID) {
                    int startHeight = (int) getResources().getDimension(R.dimen.x1);
                    int endHeight = (int) getResources().getDimension(R.dimen.x268);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                    isShown = true;
                }
                checkedIdOld = checkedId;
            }
        });
    }

    private void updateViewSkinBeauty() {
        onChangeFaceBeautyLevel(R.id.beauty_box_blur_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_color_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_red_level);
        onChangeFaceBeautyLevel(R.id.beauty_box_sharpen);
        onChangeFaceBeautyLevel(R.id.beauty_box_pouch);
        onChangeFaceBeautyLevel(R.id.beauty_box_nasolabial);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_bright);
        onChangeFaceBeautyLevel(R.id.beauty_box_tooth_whiten);
    }

    private void initViewSkinBeauty() {
        mFlFaceSkinItems = findViewById(R.id.fl_face_skin_items);
        mIvRecoverFaceSkin = findViewById(R.id.iv_recover_face_skin);
        mIvRecoverFaceSkin.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        BeautyParameterModel.recoverFaceSkinToDefValue();
                        updateViewSkinBeauty();
                        int checkedId = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
                        seekToSeekBar(checkedId);
                        setRecoverFaceSkinEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceSkin = findViewById(R.id.tv_recover_face_skin);

        mSkinBeautyBoxGroup = findViewById(R.id.beauty_group_skin_beauty);
        mSkinBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mBeautySeekBar.setVisibility(INVISIBLE);
                seekToSeekBar(checkedId);
                onChangeFaceBeautyLevel(checkedId);
            }
        });

        checkFaceSkinChanged();
    }

    private void updateViewFaceShape() {
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_enlarge);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_circle);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_thinning);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_v);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheekbones);
        onChangeFaceBeautyLevel(R.id.beauty_box_lower_jaw);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_narrow);
        onChangeFaceBeautyLevel(R.id.beauty_box_cheek_small);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_chin);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_forehead);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_intensity_mouth);
        onChangeFaceBeautyLevel(R.id.beauty_box_canthus);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_space);
        onChangeFaceBeautyLevel(R.id.beauty_box_eye_rotate);
        onChangeFaceBeautyLevel(R.id.beauty_box_long_nose);
        onChangeFaceBeautyLevel(R.id.beauty_box_philtrum);
        onChangeFaceBeautyLevel(R.id.beauty_box_smile);
    }

    private void initViewFilterRecycler() {
        mFilterRecyclerView = findViewById(R.id.filter_recycle_view);
        mFilterRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mFilterRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) mFilterRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mFilterRecyclerView.setAdapter(mFilterRecyclerAdapter = new FilterRecyclerAdapter());
    }

    private void updateViewFilterRecycler() {
        mFilterRecyclerAdapter.setFilter(BeautyParameterModel.sFilter);
        mOnFUControlListener.onFilterNameSelected(BeautyParameterModel.sFilter.getName());
        float filterLevel = getFilterLevel(BeautyParameterModel.sFilter.getName());
        mOnFUControlListener.onFilterLevelSelected(filterLevel);
    }

    private boolean mReadyForClear;

    private void updateViewFaceStyle() {
        int checkFaceStyleId = BeautyParameterModel.sCheckFaceStyleId;
        if (checkFaceStyleId != View.NO_ID) {
            mBbgStyle.check(checkFaceStyleId);
            mOnFaceStyleCheckedChangeListener.onCheckedChanged(mBbgStyle, checkFaceStyleId);
        } else {
            mReadyForClear = true;
            mBbgStyle.clearCheck();
            mClStyleNone.setSelected(true);
            setBottomCheckRatioEnable(true);
        }
    }

    private void initViewFaceShape() {
        mFlFaceShapeItems = findViewById(R.id.fl_face_shape_items);
        mIvRecoverFaceShape = findViewById(R.id.iv_recover_face_shape);
        mIvRecoverFaceShape.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        // recover params
                        BeautyParameterModel.recoverFaceShapeToDefValue();
                        updateViewFaceShape();
                        seekToSeekBar(mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                        setRecoverFaceShapeEnable(false);
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(((FragmentActivity) mContext).getSupportFragmentManager(), "ConfirmDialogFragmentReset");
            }
        });
        mTvRecoverFaceShape = findViewById(R.id.tv_recover_face_shape);
        mShapeBeautyBoxGroup = findViewById(R.id.beauty_group_face_shape);
        mShapeBeautyBoxGroup.setOnCheckedChangeListener(new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                mBeautySeekBar.setVisibility(GONE);
                seekToSeekBar(checkedId);
                onChangeFaceBeautyLevel(checkedId);
            }
        });
        checkFaceShapeChanged();
    }

    private void initViewFaceStyle() {
        mFlFaceStyleItems = findViewById(R.id.fl_face_style_items);
        mClStyleNone = mFlFaceStyleItems.findViewById(R.id.cl_style_none);
        mClStyleNone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isSelected()) {
                    return;
                }
                mBbgStyle.clearCheck();
                boolean selected = !v.isSelected();
                v.setSelected(selected);
                BeautyParameterModel.sCheckFaceStyleId = View.NO_ID;
                setBottomCheckRatioEnable(selected);
                applyConfigMap(BeautyParameterModel.sBackupParams);
            }
        });
        mBbgStyle = mFlFaceStyleItems.findViewById(R.id.beauty_group_style);
        mOnFaceStyleCheckedChangeListener = new BeautyBoxGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
                if (mReadyForClear) {
                    mReadyForClear = false;
                    return;
                }
                if (checkedId != NO_ID && group.getCheckedBeautyBoxId() == checkedId) {
                    applyConfigMap(BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_NONE));
                    BeautyParameterModel.sCheckFaceStyleId = checkedId;
                    mClStyleNone.setSelected(false);
                    setBottomCheckRatioEnable(false);
                    Map<String, Object> configMap = null;
                    switch (checkedId) {
                        case R.id.beauty_box_style_1:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_KUAISHOU);
                            break;
                        case R.id.beauty_box_style_2:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_QINGYAN);
                            break;
                        case R.id.beauty_box_style_3:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_ZIJIETIAODONG);
                            break;
                        case R.id.beauty_box_style_4:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_HUAJIAO);
                            break;
                        case R.id.beauty_box_style_5:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_YINGKE);
                            break;
                        case R.id.beauty_box_style_6:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_SHANGTANG);
                            break;
                        case R.id.beauty_box_style_7:
                            configMap = BeautyParameterModel.sDefaultConfigMap.get(BeautyParameterModel.CONFIG_BIAOZHUN);
                            break;
                        default:
                    }
                    if (configMap != null) {
                        applyConfigMap(configMap);
                    }
                }
            }
        };
        mBbgStyle.setOnCheckedChangeListener(mOnFaceStyleCheckedChangeListener);
    }

    private void setBottomCheckRatioEnable(boolean enable) {
        mEnableBottomRationClick = enable;
        for (int i = 0, childCount = mBottomCheckGroup.getChildCount(); i < childCount; i++) {
            View view = mBottomCheckGroup.getChildAt(i);
            if (view.getId() != R.id.beauty_radio_style) {
                view.setAlpha(enable ? 1f : 0.6f);
                view.setEnabled(enable);
            }
        }
    }

    private void applyConfigMap(Map<String, Object> map) {
        if (mOnFUControlListener == null) {
            return;
        }
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            float fVaule = 0f;
            if (value instanceof Double) {
                double dValue = (Double) value;
                fVaule = (float) dValue;
            } else if (value instanceof Float) {
                fVaule = (float) value;
            }
            switch (key) {
                case BeautificationParam.FILTER_NAME:
                    mOnFUControlListener.onFilterNameSelected((String) value);
                    break;
                case BeautificationParam.FILTER_LEVEL:
                    mOnFUControlListener.onFilterLevelSelected(fVaule);
                    break;
                case BeautificationParam.BLUR_LEVEL:
                    mOnFUControlListener.onBlurLevelSelected(fVaule);
                    break;
                case BeautificationParam.COLOR_LEVEL:
                    mOnFUControlListener.onColorLevelSelected(fVaule);
                    break;
                case BeautificationParam.RED_LEVEL:
                    mOnFUControlListener.onRedLevelSelected(fVaule);
                    break;
                case BeautificationParam.SHARPEN:
                    mOnFUControlListener.onSharpenLevelSelected(fVaule);
                    break;
                case BeautificationParam.EYE_BRIGHT:
                    mOnFUControlListener.onEyeBrightSelected(fVaule);
                    break;
                case BeautificationParam.TOOTH_WHITEN:
                    mOnFUControlListener.onToothWhitenSelected(fVaule);
                    break;
                case BeautificationParam.REMOVE_POUCH_STRENGTH:
                    mOnFUControlListener.setRemovePouchStrength(fVaule);
                    break;
                case BeautificationParam.REMOVE_NASOLABIAL_FOLDS_STRENGTH:
                    mOnFUControlListener.setRemoveNasolabialFoldsStrength(fVaule);
                    break;
                case BeautificationParam.CHEEK_THINNING:
                    mOnFUControlListener.onCheekThinningSelected(fVaule);
                    break;
                case BeautificationParam.CHEEK_V:
                    mOnFUControlListener.onCheekVSelected(fVaule);
                    break;
                case BeautificationParam.CHEEK_NARROW:
                    mOnFUControlListener.onCheekNarrowSelected(fVaule);
                    break;
                case BeautificationParam.CHEEK_SMALL:
                    mOnFUControlListener.onCheekSmallSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_CHEEKBONES:
                    mOnFUControlListener.setCheekbonesIntensity(fVaule);
                    break;
                case BeautificationParam.INTENSITY_LOW_JAW:
                    mOnFUControlListener.setLowerJawIntensity(fVaule);
                    break;
                case BeautificationParam.EYE_ENLARGING:
                    mOnFUControlListener.onEyeEnlargeSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_EYE_CIRCLE:
                    mOnFUControlListener.onEyeCircleSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_CHIN:
                    mOnFUControlListener.onIntensityChinSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_FOREHEAD:
                    mOnFUControlListener.onIntensityForeheadSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_NOSE:
                    mOnFUControlListener.onIntensityNoseSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_MOUTH:
                    mOnFUControlListener.onIntensityMouthSelected(fVaule);
                    break;
                case BeautificationParam.INTENSITY_EYE_SPACE:
                    mOnFUControlListener.setEyeSpaceIntensity(fVaule);
                    break;
                case BeautificationParam.INTENSITY_CANTHUS:
                    mOnFUControlListener.setCanthusIntensity(fVaule);
                    break;
                case BeautificationParam.INTENSITY_EYE_ROTATE:
                    mOnFUControlListener.setEyeRotateIntensity(fVaule);
                    break;
                case BeautificationParam.INTENSITY_LONG_NOSE:
                    mOnFUControlListener.setLongNoseIntensity(fVaule);
                    break;
                case BeautificationParam.INTENSITY_PHILTRUM:
                    mOnFUControlListener.setPhiltrumIntensity(fVaule);
                    break;
                case BeautificationParam.INTENSITY_SMILE:
                    mOnFUControlListener.setSmileIntensity(fVaule);
                    break;
                default:
            }
        }
    }

    private void setRecoverFaceShapeEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceShape.setAlpha(1f);
            mTvRecoverFaceShape.setAlpha(1f);
        } else {
            mIvRecoverFaceShape.setAlpha(0.6f);
            mTvRecoverFaceShape.setAlpha(0.6f);
        }
        mIvRecoverFaceShape.setEnabled(enable);
        mTvRecoverFaceShape.setEnabled(enable);
    }

    private void setRecoverFaceSkinEnable(boolean enable) {
        if (enable) {
            mIvRecoverFaceSkin.setAlpha(1f);
            mTvRecoverFaceSkin.setAlpha(1f);
        } else {
            mIvRecoverFaceSkin.setAlpha(0.6f);
            mTvRecoverFaceSkin.setAlpha(0.6f);
        }
        mIvRecoverFaceSkin.setEnabled(enable);
        mTvRecoverFaceSkin.setEnabled(enable);
    }

    private void onChangeFaceBeautyLevel(int viewId) {
        if (viewId == View.NO_ID) {
            return;
        }
        View view = findViewById(viewId);
        if (view instanceof BaseBeautyBox) {
            boolean open = BeautyParameterModel.isOpen(viewId);
            ((BaseBeautyBox) view).setOpen(open);
        }
        if (mOnFUControlListener == null) {
            return;
        }
        switch (viewId) {
            case R.id.beauty_box_blur_level:
                mOnFUControlListener.onBlurLevelSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_color_level:
                mOnFUControlListener.onColorLevelSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_red_level:
                mOnFUControlListener.onRedLevelSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_sharpen:
                mOnFUControlListener.onSharpenLevelSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_pouch:
                mOnFUControlListener.setRemovePouchStrength(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_nasolabial:
                mOnFUControlListener.setRemoveNasolabialFoldsStrength(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_eye_bright:
                mOnFUControlListener.onEyeBrightSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_tooth_whiten:
                mOnFUControlListener.onToothWhitenSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_eye_enlarge:
                mOnFUControlListener.onEyeEnlargeSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_eye_circle:
                mOnFUControlListener.onEyeCircleSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_cheek_thinning:
                mOnFUControlListener.onCheekThinningSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_cheek_narrow:
                mOnFUControlListener.onCheekNarrowSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_cheekbones:
                mOnFUControlListener.setCheekbonesIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_lower_jaw:
                mOnFUControlListener.setLowerJawIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_cheek_v:
                mOnFUControlListener.onCheekVSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_cheek_small:
                mOnFUControlListener.onCheekSmallSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_intensity_chin:
                mOnFUControlListener.onIntensityChinSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_intensity_forehead:
                mOnFUControlListener.onIntensityForeheadSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_intensity_nose:
                mOnFUControlListener.onIntensityNoseSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_intensity_mouth:
                mOnFUControlListener.onIntensityMouthSelected(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_canthus:
                mOnFUControlListener.setCanthusIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_eye_space:
                mOnFUControlListener.setEyeSpaceIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_eye_rotate:
                mOnFUControlListener.setEyeRotateIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_long_nose:
                mOnFUControlListener.setLongNoseIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_philtrum:
                mOnFUControlListener.setPhiltrumIntensity(BeautyParameterModel.getValue(viewId));
                break;
            case R.id.beauty_box_smile:
                mOnFUControlListener.setSmileIntensity(BeautyParameterModel.getValue(viewId));
                break;
            default:
        }
    }

    private void initViewTop() {
        mBeautySeekBar = findViewById(R.id.beauty_seek_bar);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                float valueF = 1.0f * (value - seekBar.getMin()) / 100;
                int checkedCheckBoxId = mBottomCheckGroup.getCheckedCheckBoxId();
                if (checkedCheckBoxId == R.id.beauty_radio_skin_beauty) {
                    int skinCheckedId = mSkinBeautyBoxGroup.getCheckedBeautyBoxId();
                    BeautyParameterModel.setValue(skinCheckedId, valueF);
                    onChangeFaceBeautyLevel(skinCheckedId);
                    checkFaceSkinChanged();
                } else if (checkedCheckBoxId == R.id.beauty_radio_face_shape) {
                    BeautyParameterModel.setValue(mShapeBeautyBoxGroup.getCheckedBeautyBoxId(), valueF);
                    onChangeFaceBeautyLevel(mShapeBeautyBoxGroup.getCheckedBeautyBoxId());
                    checkFaceShapeChanged();
                } else if (checkedCheckBoxId == R.id.beauty_radio_filter) {
                    mFilterRecyclerAdapter.setFilterLevels(valueF);
                }
            }
        });
    }

    private void checkFaceShapeChanged() {
        if (BeautyParameterModel.checkIfFaceShapeChanged()) {
            setRecoverFaceShapeEnable(true);
        } else {
            setRecoverFaceShapeEnable(false);
        }
    }

    private void checkFaceSkinChanged() {
        if (BeautyParameterModel.checkIfFaceSkinChanged()) {
            setRecoverFaceSkinEnable(true);
        } else {
            setRecoverFaceSkinEnable(false);
        }
    }

    /**
     * 点击底部 tab
     *
     * @param viewId
     */
    private void clickViewBottomRadio(int viewId) {
        mFlFaceShapeItems.setVisibility(GONE);
        mFlFaceSkinItems.setVisibility(GONE);
        mFilterRecyclerView.setVisibility(GONE);
        mFlFaceStyleItems.setVisibility(GONE);
        mBeautySeekBar.setVisibility(GONE);
        if (viewId == R.id.beauty_radio_skin_beauty) {
            mFlFaceSkinItems.setVisibility(VISIBLE);
            mIvCompare.setVisibility(VISIBLE);
        } else if (viewId == R.id.beauty_radio_face_shape) {
            mFlFaceShapeItems.setVisibility(VISIBLE);
            int id = mShapeBeautyBoxGroup.getCheckedBeautyBoxId();
            seekToSeekBar(id);
            mIvCompare.setVisibility(VISIBLE);
        } else if (viewId == R.id.beauty_radio_filter) {
            mFilterRecyclerView.setVisibility(VISIBLE);
            mFilterRecyclerAdapter.setFilterProgress();
            mIvCompare.setVisibility(VISIBLE);
        } else if (viewId == R.id.beauty_radio_style) {
            mFlFaceStyleItems.setVisibility(VISIBLE);
            mIvCompare.setVisibility(VISIBLE);
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

        float value = BeautyParameterModel.getValue(checkedId);
        int min = 0;
        int max = 100;
        if (checkedId == R.id.beauty_box_intensity_chin || checkedId == R.id.beauty_box_intensity_forehead
                || checkedId == R.id.beauty_box_intensity_mouth || checkedId == R.id.beauty_box_long_nose
                || checkedId == R.id.beauty_box_eye_space || checkedId == R.id.beauty_box_eye_rotate
                || checkedId == R.id.beauty_box_philtrum) {
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
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mBottomView.getLayoutParams();
                params.height = height;
                mBottomView.setLayoutParams(params);
                if (mOnBottomAnimatorChangeListener != null) {
                    float showRate = 1.0f * (height - startHeight) / (endHeight - startHeight);
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(startHeight > endHeight ? 1 - showRate : showRate);
                }
                if (DecimalUtils.floatEquals(animation.getAnimatedFraction(), 1.0f) && startHeight < endHeight) {
                    mIvCompare.setVisibility(VISIBLE);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mOnFUControlListener == null) {
            return false;
        }
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            v.setAlpha(0.7f);
            mOnFUControlListener.setBeautificationOn(false);
        } else if (action == MotionEvent.ACTION_UP) {
            v.setAlpha(1f);
            mOnFUControlListener.setBeautificationOn(true);
        }
        return true;
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private ValueAnimator mBottomLayoutAnimator;

    public void hideBottomLayoutAnimator() {
        mBottomCheckGroup.check(View.NO_ID);
    }

    public void setFilterLevel(String filterName, float faceBeautyFilterLevel) {
        BeautyParameterModel.sFilterLevel.put(BeautyParameterModel.STR_FILTER_LEVEL + filterName, faceBeautyFilterLevel);
        if (mOnFUControlListener != null) {
            mOnFUControlListener.onFilterLevelSelected(faceBeautyFilterLevel);
        }
    }

    public float getFilterLevel(String filterName) {
        String key = BeautyParameterModel.STR_FILTER_LEVEL + filterName;
        Float level = BeautyParameterModel.sFilterLevel.get(key);
        if (level == null) {
            level = BeautyParameterModel.DEFAULT_FILTER_LEVEL;
            BeautyParameterModel.sFilterLevel.put(key, level);
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
            holder.filterImg.setImageResource(filters.get(position).getIconId());
            holder.filterName.setText(filters.get(position).getNameId());
            if (mFilterPositionSelect == position) {
                holder.filterImg.setBackgroundResource(R.drawable.shape_filter_selected);
                holder.filterName.setSelected(true);
            } else {
                holder.filterImg.setBackgroundResource(R.color.transparent);
                holder.filterName.setSelected(false);
            }
            holder.itemView.setOnClickListener(new OnMultiClickListener() {
                @Override
                protected void onMultiClick(View v) {
                    mFilterPositionSelect = position;
                    setFilterProgress();
                    notifyDataSetChanged();
                    if (mOnFUControlListener != null) {
                        BeautyParameterModel.sFilter = filters.get(mFilterPositionSelect);
                        mOnFUControlListener.onFilterNameSelected(BeautyParameterModel.sFilter.getName());
                        ToastUtil.showNormalToast(mContext, BeautyParameterModel.sFilter.getNameId());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mFilters.size();
        }

        void setFilterLevels(float filterLevels) {
            if (mFilterPositionSelect >= 0) {
                setFilterLevel(mFilters.get(mFilterPositionSelect).getName(), filterLevels);
            }
        }

        public void setFilter(Filter filter) {
            mFilterPositionSelect = mFilters.indexOf(filter);
        }

        void setFilterProgress() {
            if (mFilterPositionSelect > 0) {
                seekToSeekBar(getFilterLevel(mFilters.get(mFilterPositionSelect).getName()));
            } else {
                mBeautySeekBar.setVisibility(INVISIBLE);
            }
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            ImageView filterImg;
            TextView filterName;

            HomeRecyclerHolder(View itemView) {
                super(itemView);
                filterImg = itemView.findViewById(R.id.control_recycler_img);
                filterName = itemView.findViewById(R.id.control_recycler_text);
            }
        }
    }

}