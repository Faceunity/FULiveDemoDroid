package com.faceunity.fulivedemo.ui.fragment;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.FURenderer;
import com.faceunity.entity.AvatarModel;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.activity.AvatarDriveActivity;
import com.faceunity.fulivedemo.entity.AvatarComponent;
import com.faceunity.fulivedemo.entity.AvatarFaceAspect;
import com.faceunity.fulivedemo.entity.AvatarFaceAspectCustomEnum;
import com.faceunity.fulivedemo.entity.AvatarFaceHelper;
import com.faceunity.fulivedemo.entity.AvatarFaceType;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.SpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.ColorConstant;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Richie on 2019.03.21
 * Avatar 选择五官模型和自定义页面
 */
public class AvatarMakeFragment extends Fragment {
    public static final String TAG = "AvatarMakeFragment";
    public static final int MAKE_TRANSLATION_Y = 140;
    public static final int CUSTOM_TRANSLATION_Y = 60;
    public static final float NORMAL_SCALE = 1f;
    public static final float LARGE_SCALE = 1.2f;
    private static final int ANIMATION_DURATION = 300;
    // 保存进度条数值 -1 --> 1
    private final Map<String, Float> mFaceShapeLevelMap = new HashMap<>(16);
    // 自定义保存后的参数，key: type, value: levelMap
    private final Map<Integer, Map<String, Float>> mCustomedLevelMaps = new HashMap<>(8);
    // 每个五官下的预置的调整维度，只读
    private final SparseArray<List<AvatarFaceAspect>> mAvatarFaceAspects = new SparseArray<>(16);
    private AvatarComponentAdapter mAvatarComponentAdapter;
    private AvatarColorAdapter mAvatarColorAdapter;
    private AvatarTypeAdapter mAvatarTypeAdapter;
    private RecyclerView mRvAvatarColor;
    private AvatarDriveActivity mActivity;
    private boolean mInCustom;
    private TextView mTvTitle;
    private TextView mTvItemName;
    private View mLoadingView;
    private View mMakeView;
    private DiscreteSeekBar mDiscreteSeekBar;
    private View mSeekBarContainer;
    private AvatarAspectAdapter mAvatarAspectAdapter;
    private RecyclerView mRvAvatarComponent;
    private AvatarTypeClickListener mAvatarTypeClickListener;
    private AvatarComponentClickListener mAvatarComponentClickListener;
    private Handler mMainHandler = new Handler();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AvatarDriveActivity) getActivity();
        initRecyclerData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_avatar_make, container, false);
        RecyclerView rvAvatarType = view.findViewById(R.id.rv_avatar_type);
        rvAvatarType.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        rvAvatarType.setHasFixedSize(true);
        ((SimpleItemAnimator) rvAvatarType.getItemAnimator()).setSupportsChangeAnimations(false);
        mAvatarTypeAdapter = new AvatarTypeAdapter(AvatarFaceHelper.getAvatarFaceTypes(mActivity));
        mAvatarTypeAdapter.setItemSelected(0);// 默认选择头发类型
        mAvatarTypeClickListener = new AvatarTypeClickListener();
        mAvatarTypeAdapter.setOnItemClickListener(mAvatarTypeClickListener);
        rvAvatarType.setAdapter(mAvatarTypeAdapter);

        mRvAvatarComponent = view.findViewById(R.id.rv_avatar_component);
        mRvAvatarComponent.setLayoutManager(new GridLayoutManager(mActivity, 4, GridLayoutManager.VERTICAL, false));
        mRvAvatarComponent.setHasFixedSize(true);
        mRvAvatarComponent.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x2),
                getResources().getDimensionPixelSize(R.dimen.x2), getResources().getDimensionPixelSize(R.dimen.x15),
                getResources().getDimensionPixelSize(R.dimen.x15)));
        ((SimpleItemAnimator) mRvAvatarComponent.getItemAnimator()).setSupportsChangeAnimations(false);
        List<AvatarComponent> avatarComponents = AvatarFaceHelper.getAvatarComponents(AvatarFaceType.AVATAR_FACE_HAIR);
        mAvatarComponentAdapter = new AvatarComponentAdapter(new ArrayList<>(avatarComponents));
        int pos = AvatarFaceHelper.FACE_ASPECT_SELECTED_POSITION.get(AvatarFaceType.AVATAR_FACE_HAIR, 0);
        mAvatarComponentAdapter.setItemSelected(pos);
        mAvatarComponentClickListener = new AvatarComponentClickListener();
        mAvatarComponentAdapter.setOnItemClickListener(mAvatarComponentClickListener);
        mRvAvatarComponent.setAdapter(mAvatarComponentAdapter);
        mRvAvatarComponent.scrollToPosition(pos);

        mRvAvatarColor = view.findViewById(R.id.rv_avatar_color);
        mRvAvatarColor.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        mRvAvatarColor.setHasFixedSize(true);
        mRvAvatarColor.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x15),
                getResources().getDimensionPixelSize(R.dimen.x20), getResources().getDimensionPixelSize(R.dimen.x19),
                getResources().getDimensionPixelSize(R.dimen.x19)));
        ((SimpleItemAnimator) mRvAvatarColor.getItemAnimator()).setSupportsChangeAnimations(false);
        mAvatarColorAdapter = new AvatarColorAdapter(new ArrayList<>(Arrays.asList(mAvatarTypeAdapter.getSelectedItems().valueAt(0).getColors())));
        AvatarCompColorClickListener onItemClickListener = new AvatarCompColorClickListener();
        mAvatarColorAdapter.setOnItemClickListener(onItemClickListener);
        pos = AvatarFaceHelper.FACE_ASPECT_COLOR_SELECTED_POSITION.get(AvatarFaceType.AVATAR_FACE_HAIR, 0);
        mRvAvatarColor.setAdapter(mAvatarColorAdapter);
        mAvatarColorAdapter.setItemSelected(pos);
        mRvAvatarColor.scrollToPosition(pos);
        double[] colors = ColorConstant.hair_color[pos];
        AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.put(AvatarFaceAspect.COLOR_HAIR + colors.length, colors);

        ViewClickListener viewClickListener = new ViewClickListener();
        view.findViewById(R.id.iv_avatar_back).setOnClickListener(viewClickListener);
        view.findViewById(R.id.iv_avatar_save).setOnClickListener(viewClickListener);
        view.findViewById(R.id.iv_avatar_reset).setOnClickListener(viewClickListener);
        mMakeView = view.findViewById(R.id.cl_make_avatar_view);

        mTvTitle = view.findViewById(R.id.tv_avatar_custom_name);
        mTvItemName = view.findViewById(R.id.tv_avatar_face_name);
        mSeekBarContainer = view.findViewById(R.id.ll_avatar_face_seekbar);
        mDiscreteSeekBar = mSeekBarContainer.findViewById(R.id.seek_bar_avatar_face);
        mDiscreteSeekBar.setOnProgressChangeListener(new SeekBarChangedListener());
        RecyclerView rvCustom = view.findViewById(R.id.rv_avatar_custom);
        rvCustom.setHasFixedSize(true);
        rvCustom.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) rvCustom.getItemAnimator()).setSupportsChangeAnimations(false);
        rvCustom.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x22), 0));
        mAvatarAspectAdapter = new AvatarAspectAdapter(new ArrayList<AvatarFaceAspect>());
        mAvatarAspectAdapter.setOnItemClickListener(new AvatarAspectClickListener());
        rvCustom.setAdapter(mAvatarAspectAdapter);
        mAvatarTypeClickListener.showColorList();

        ImageView ivLoading = (ImageView) view.findViewById(R.id.iv_loading);
        mLoadingView = view.findViewById(R.id.fl_loading);
        // intercept quick click when loading hair bundle
        mLoadingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        Glide.with(this).load(R.drawable.loading_gif).into(ivLoading);
        Log.i(TAG, "onCreateView finish");
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Pass avatar to making mode
     *
     * @param avatarModel
     */
    public void setAvatarModel(AvatarModel avatarModel) {
        Log.i(TAG, "setAvatarModel: " + avatarModel);
        if (avatarModel == null) {
            return;
        }

        mInCustom = false;
        AvatarFaceHelper.array2UiConfig(avatarModel.getUiJson());
        fillParams(avatarModel);

        int type = AvatarFaceType.AVATAR_FACE_HAIR;
        if (mAvatarComponentAdapter != null) {
            mAvatarTypeAdapter.setItemSelected(0);
            mAvatarTypeClickListener.onItemClick(mAvatarTypeAdapter, null, 0);
            int pos = AvatarFaceHelper.FACE_ASPECT_SELECTED_POSITION.get(type, 0);
            if (pos >= 0) {
                mAvatarComponentAdapter.setItemSelected(pos);
                mRvAvatarComponent.scrollToPosition(pos);
            }
        }
        if (mAvatarAspectAdapter != null) {
            mAvatarAspectAdapter.clearSingleItemSelected();
        }
        if (mAvatarColorAdapter != null) {
            int pos = AvatarFaceHelper.FACE_ASPECT_COLOR_SELECTED_POSITION.get(type, 0);
            if (pos >= 0) {
                mAvatarColorAdapter.setItemSelected(pos);
                mRvAvatarColor.scrollToPosition(pos);
            }
        }
    }

    private void fillParams(AvatarModel avatarModel) {
        List<AvatarFaceAspect> avatarFaceAspects = AvatarFaceHelper.config2Array(avatarModel.getConfigJson());
        mCustomedLevelMaps.clear();
        if (avatarFaceAspects == null) {
            return;
        }

        Set<Map.Entry<Integer, Set<String>>> faceTypeEntries = AvatarFaceHelper.FACE_ASPECT_TYPE_MAP.entrySet();
        for (AvatarFaceAspect avatarFaceAspect : avatarFaceAspects) {
            String name = avatarFaceAspect.getName();
            int type = AvatarFaceType.AVATAR_FACE_HAIR;
            for (Map.Entry<Integer, Set<String>> faceTypeEntry : faceTypeEntries) {
                if (faceTypeEntry.getValue().contains(name)) {
                    type = faceTypeEntry.getKey();
                    break;
                }
            }
            Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
            if (paramsMap == null) {
                paramsMap = new HashMap<>(8);
                mCustomedLevelMaps.put(type, paramsMap);
            }

            float level = avatarFaceAspect.getLevel();
            if (level != 0) {
                paramsMap.put(name, level);
                AvatarFaceHelper.FACE_ASPECT_MAP.put(name, level);
                AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.put(name, level);
            }
            if (avatarFaceAspect.getBundlePath() != null) {
                AvatarFaceHelper.sFaceHairBundlePath = avatarFaceAspect.getBundlePath();
            }
            if (avatarFaceAspect.getColor() != null) {
                AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.put(name + avatarFaceAspect.getColor().length,
                        avatarFaceAspect.getColor());
            }
        }
    }

    public void transformModelHead() {
        FURenderer fuRenderer = mActivity.getFURenderer();
        if (mInCustom) {
            double[] translationValue = new double[]{0, CUSTOM_TRANSLATION_Y, 0};
            fuRenderer.setAvatarTranslate(translationValue);
            fuRenderer.setAvatarScale(LARGE_SCALE);
        } else {
            double[] translationValue = new double[]{0, MAKE_TRANSLATION_Y, 0};
            fuRenderer.setAvatarTranslate(translationValue);
            fuRenderer.setAvatarScale(NORMAL_SCALE);
        }
    }

    public void transformModelHair() {
        FURenderer fuRenderer = mActivity.getFURenderer();
        if (mInCustom) {
            double[] translationValue = new double[]{0, CUSTOM_TRANSLATION_Y, 0};
            fuRenderer.setAvatarHairTranslate(translationValue);
            fuRenderer.setAvatarScale(LARGE_SCALE);
            fuRenderer.setAvatarHairScale(LARGE_SCALE);
        } else {
            double[] translationValue = new double[]{0, MAKE_TRANSLATION_Y, 0};
            fuRenderer.setAvatarHairTranslate(translationValue);
            fuRenderer.setAvatarHairScale(NORMAL_SCALE);
        }
    }

    public void startMakeAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(CUSTOM_TRANSLATION_Y, MAKE_TRANSLATION_Y).setDuration(ANIMATION_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private double[] translationValue = new double[]{0, CUSTOM_TRANSLATION_Y, 0};

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mActivity == null) {
                    return;
                }
                float val = (float) animation.getAnimatedValue();
                translationValue[1] = val;
                mActivity.getFURenderer().setAvatarTranslate(translationValue);
                mActivity.getFURenderer().setAvatarHairTranslate(translationValue);
                float animatedFraction = animation.getAnimatedFraction();
                float scale = LARGE_SCALE - animatedFraction * (LARGE_SCALE - NORMAL_SCALE);
                mActivity.getFURenderer().setAvatarScale(scale);
                mActivity.getFURenderer().setAvatarHairScale(scale);
                performMakeAnimation(animatedFraction, true);
            }
        });
        valueAnimator.start();
        mInCustom = false;
    }

    public void startCustomAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(MAKE_TRANSLATION_Y, CUSTOM_TRANSLATION_Y).setDuration(ANIMATION_DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private double[] translationValue = new double[]{0, MAKE_TRANSLATION_Y, 0};

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (mActivity == null) {
                    return;
                }
                float val = (float) animation.getAnimatedValue();
                translationValue[1] = val;
                mActivity.getFURenderer().setAvatarTranslate(translationValue);
                mActivity.getFURenderer().setAvatarHairTranslate(translationValue);
                float animatedFraction = animation.getAnimatedFraction();
                float scale = NORMAL_SCALE + animatedFraction * (LARGE_SCALE - NORMAL_SCALE);
                mActivity.getFURenderer().setAvatarScale(scale);
                mActivity.getFURenderer().setAvatarHairScale(scale);
                performMakeAnimation(animatedFraction, false);
            }
        });
        valueAnimator.start();
        mInCustom = true;
    }

    public void performMakeAnimation(float fraction, boolean show) {
        if (mMakeView == null) {
            return;
        }

        ViewGroup.LayoutParams layoutParams = mMakeView.getLayoutParams();
        int showHeight = getResources().getDimensionPixelSize(R.dimen.x634);
        if (show) {
            int height = (int) (showHeight * fraction);
            if (height > 0) {
                layoutParams.height = height;
            }
        } else {
            int height = (int) (showHeight * (1 - fraction));
            if (height > 0) {
                layoutParams.height = height;
            }
        }
        mMakeView.setLayoutParams(layoutParams);
    }

    private void reSelectLastComponent() {
        int type = mAvatarTypeAdapter.getSelectedItems().valueAt(0).getType();
        int pos = AvatarFaceHelper.FACE_ASPECT_SELECTED_POSITION.get(type, -1);
        if (pos >= 0) {
            mAvatarComponentAdapter.setItemSelected(pos);
            if (pos > 0) {
                mAvatarComponentClickListener.onItemClick(mAvatarComponentAdapter, null, pos);
            }
        }
    }

    private void initRecyclerData() {
        mAvatarFaceAspects.put(AvatarFaceType.AVATAR_FACE_SHAPE, AvatarFaceAspectCustomEnum.getAvatarAspectsByType(AvatarFaceType.AVATAR_FACE_SHAPE));
        mAvatarFaceAspects.put(AvatarFaceType.AVATAR_FACE_EYE, AvatarFaceAspectCustomEnum.getAvatarAspectsByType(AvatarFaceType.AVATAR_FACE_EYE));
        mAvatarFaceAspects.put(AvatarFaceType.AVATAR_FACE_NOSE, AvatarFaceAspectCustomEnum.getAvatarAspectsByType(AvatarFaceType.AVATAR_FACE_NOSE));
        mAvatarFaceAspects.put(AvatarFaceType.AVATAR_FACE_LIP, AvatarFaceAspectCustomEnum.getAvatarAspectsByType(AvatarFaceType.AVATAR_FACE_LIP));
    }

    public void showLoadingView(boolean show) {
        if (mLoadingView == null) {
            return;
        }

        if (show && mLoadingView.getVisibility() == View.INVISIBLE) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else if (!show && mLoadingView.getVisibility() == View.VISIBLE) {
            mLoadingView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean checkIfLevelChanged() {
        Set<Map.Entry<String, Float>> faceEntries = AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.entrySet();
        for (Map.Entry<String, Float> faceEntry : faceEntries) {
            if (Float.compare(faceEntry.getValue(), 0f) != 0) {
                return true;
            }
        }
        return false;
    }

    public void backPressed() {
        if (mInCustom) {
            // 自定义页面
            boolean levelChanged = checkIfLevelChanged();
            if (levelChanged) {
                ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.live_photo_back_not_save), new BaseDialogFragment.OnClickListener() {
                    @Override
                    public void onConfirm() {
                        Set<Map.Entry<String, Float>> entries = AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.entrySet();
                        for (Map.Entry<String, Float> entry : entries) {
                            mActivity.getFURenderer().fuItemSetParamFaceup(entry.getKey(), 0f);
                        }
                        int type = mAvatarTypeAdapter.getSelectedItems().valueAt(0).getType();
                        Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
                        if (paramsMap != null) {
                            entries = paramsMap.entrySet();
                            for (Map.Entry<String, Float> entry : entries) {
                                mActivity.getFURenderer().fuItemSetParamFaceup(entry.getKey(), entry.getValue());
                            }
                        }
                        AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.clear();
                        startMakeAnimation();
                        reSelectLastComponent();
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                confirmDialogFragment.show(mActivity.getSupportFragmentManager(), "ConfirmDialogFragment");
            } else {
                startMakeAnimation();
                reSelectLastComponent();
            }
        } else {
            // 效果选择页面
            ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.live_photo_back_not_save), new BaseDialogFragment.OnClickListener() {
                @Override
                public void onConfirm() {
                    int type = AvatarFaceType.AVATAR_FACE_HAIR;
                    SparseArray<AvatarFaceType> selectedItems = mAvatarTypeAdapter.getSelectedItems();
                    if (selectedItems.size() > 0) {
                        type = selectedItems.valueAt(0).getType();
                    }
                    Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
                    if (paramsMap != null) {
                        paramsMap.clear();
                    }
                    mActivity.getFURenderer().clearFaceShape();
                    mActivity.getFURenderer().quitFaceup();
                    mActivity.showDrivePage();
                }

                @Override
                public void onCancel() {
                }
            });
            confirmDialogFragment.show(mActivity.getSupportFragmentManager(), "ConfirmDialogFragment");
        }
    }

    // 自定义五官列表
    private static class AvatarAspectAdapter extends BaseRecyclerAdapter<AvatarFaceAspect> {

        AvatarAspectAdapter(@NonNull List<AvatarFaceAspect> data) {
            super(data, R.layout.recycler_avatar_aspect);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, AvatarFaceAspect item) {
            viewHolder.setImageResource(R.id.iv_avatar_aspect, item.getIconId());
        }
    }

    // 顶部颜色列表
    private class AvatarColorAdapter extends BaseRecyclerAdapter<double[]> {

        public AvatarColorAdapter(@NonNull List<double[]> data) {
            super(data, R.layout.recycler_avatar_color);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, double[] data, boolean selected) {
            viewHolder.setVisibility(R.id.iv_avatar_color_flag, selected ? View.VISIBLE : View.GONE);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, double[] item) {
            viewHolder.setImageDrawable(R.id.iv_avatar_color, getRoundColorDrawable(item));
        }

        private Drawable getRoundColorDrawable(double[] rgb) {
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setShape(GradientDrawable.OVAL);
            gradientDrawable.setColor(Color.argb(255, (int) rgb[0],
                    (int) rgb[1], (int) rgb[2]));
            return gradientDrawable;
        }
    }

    // 中部组件列表
    private class AvatarComponentAdapter extends BaseRecyclerAdapter<AvatarComponent> {

        public AvatarComponentAdapter(@NonNull List<AvatarComponent> data) {
            super(data, R.layout.recycler_avatar_item);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            BaseViewHolder viewHolder;
            if (viewType == AvatarComponent.CUSTOM) {
                viewHolder = BaseViewHolder.createViewHolder(parent, R.layout.layout_avatar_component_custom);
            } else {
                viewHolder = BaseViewHolder.createViewHolder(parent, R.layout.recycler_avatar_item);
            }
            View itemView = viewHolder.getItemView();
            itemView.setOnClickListener(new InnerItemViewClickListener(viewHolder));
            itemView.setOnLongClickListener(new InnerItemLongClickListener(viewHolder));
            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            return mData.get(position).isCustom() ? AvatarComponent.CUSTOM : AvatarComponent.NOT_CUSTOM;
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, AvatarComponent item) {
            if (!item.isCustom()) {
                ImageView imageView = viewHolder.getViewById(R.id.iv_avatar_item);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x16)));
                Glide.with(mActivity).applyDefaultRequestOptions(requestOptions).load(item.getIconId()).into(imageView);
            }
        }
    }

    // 底部五官列表
    private class AvatarTypeAdapter extends BaseRecyclerAdapter<AvatarFaceType> {

        public AvatarTypeAdapter(@NonNull List<AvatarFaceType> data) {
            super(data, R.layout.recycler_avatar_type);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, AvatarFaceType item) {
            viewHolder.setText(R.id.tv_avatar_type, item.getName());
        }
    }

    // 颜色点击事件
    private class AvatarCompColorClickListener implements BaseRecyclerAdapter.OnItemClickListener<double[]> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<double[]> adapter, View view, int position) {
            SparseArray<AvatarFaceType> selectedItems = mAvatarTypeAdapter.getSelectedItems();
            int type = AvatarFaceType.AVATAR_FACE_HAIR;
            if (selectedItems.size() > 0) {
                type = selectedItems.valueAt(0).getType();
            }
            AvatarFaceHelper.FACE_ASPECT_COLOR_SELECTED_POSITION.put(type, position);
            double[] item = adapter.getItem(position);
            if (type == AvatarFaceType.AVATAR_FACE_SHAPE) {
                mActivity.getFURenderer().fuItemSetParamFaceColor(AvatarFaceAspect.COLOR_FACE, item);
                AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.put(AvatarFaceAspect.COLOR_FACE + item.length, item);
            } else if (type == AvatarFaceType.AVATAR_FACE_LIP) {
                mActivity.getFURenderer().fuItemSetParamFaceColor(AvatarFaceAspect.COLOR_LIP, item);
                AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.put(AvatarFaceAspect.COLOR_LIP + item.length, item);
            } else if (type == AvatarFaceType.AVATAR_FACE_EYE) {
                mActivity.getFURenderer().fuItemSetParamFaceColor(AvatarFaceAspect.COLOR_EYE, item);
                AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.put(AvatarFaceAspect.COLOR_EYE + item.length, item);
            } else if (type == AvatarFaceType.AVATAR_FACE_HAIR) {
                mActivity.getFURenderer().fuItemSetParamFaceColor(AvatarFaceAspect.COLOR_HAIR, item);
                AvatarFaceHelper.FACE_ASPECT_COLOR_MAP.put(AvatarFaceAspect.COLOR_HAIR + item.length, item);
            }
        }
    }

    // 中间组件点击事件
    private class AvatarComponentClickListener implements BaseRecyclerAdapter.OnItemClickListener<AvatarComponent> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<AvatarComponent> adapter, View view, int position) {
            AvatarComponent item = adapter.getItem(position);
            int type = item.getType();
            AvatarFaceHelper.FACE_ASPECT_SELECTED_POSITION.put(type, position);
            boolean needCustomize = AvatarFaceHelper.isNeedCustomize(type);
            if (position == 0 && needCustomize) {
                // 点击自定义
                showFaceInfo();
            } else {
                if (type == AvatarFaceType.AVATAR_FACE_HAIR) {
                    // 头发
                    String bundlePath = item.getBundlePath();
                    AvatarFaceHelper.sFaceHairBundlePath = bundlePath;
                    mActivity.getFURenderer().loadAvatarHair(bundlePath);
                    if (position == 0) {
                        mAvatarTypeClickListener.hideColorList();
                    } else {
                        showLoadingView(true);
                        SparseArray<AvatarFaceType> selectedItems = mAvatarTypeAdapter.getSelectedItems();
                        if (selectedItems.size() > 0) {
                            double[][] colors = selectedItems.valueAt(0).getColors();
                            if (colors != null) {
                                mAvatarColorAdapter.replaceAll(Arrays.asList(colors));
                            }
                        }
                        if (position > 0) {
                            int colorPos = AvatarFaceHelper.FACE_ASPECT_COLOR_SELECTED_POSITION.get(type, 0);
                            if (colorPos >= 0) {
                                mAvatarColorAdapter.setItemSelected(colorPos);
                                mRvAvatarColor.scrollToPosition(colorPos);
                            }
                            mAvatarTypeClickListener.showColorList();
                        } else {
                            mAvatarTypeClickListener.hideColorList();
                        }
                    }
                } else {
                    // 其他
                    if (position > 0) {
                        Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
                        if (paramsMap != null) {
                            paramsMap.clear();
                        }
                    }
                    double[][] colors = mAvatarTypeAdapter.getSelectedItems().valueAt(0).getColors();
                    if (colors != null) {
                        mAvatarColorAdapter.replaceAll(Arrays.asList(colors));
                        int colorPos = AvatarFaceHelper.FACE_ASPECT_COLOR_SELECTED_POSITION.get(type, 0);
                        if (colorPos >= 0) {
                            mAvatarColorAdapter.setItemSelected(colorPos);
                            mRvAvatarColor.scrollToPosition(colorPos);
                        }
                        mAvatarTypeClickListener.showColorList();
                    } else {
                        mAvatarTypeClickListener.hideColorList();
                    }
                    List<AvatarFaceAspect> avatarFaceAspects = item.getAvatarFaceAspects();
                    Set<String> faceNamesByType = AvatarFaceHelper.getFaceNamesByType(type);
                    // 先重置，然后再设置
                    mActivity.getFURenderer().enterFaceShape();
                    for (String s : faceNamesByType) {
                        AvatarFaceHelper.FACE_ASPECT_MAP.put(s, 0f);
                        mActivity.getFURenderer().fuItemSetParamFaceup(s, 0f);
                    }
                    for (AvatarFaceAspect avatarFaceAspect : avatarFaceAspects) {
                        String name = avatarFaceAspect.getName();
                        if (name != null) {
                            float level = avatarFaceAspect.getLevel();
                            AvatarFaceHelper.FACE_ASPECT_MAP.put(name, level);
                            mActivity.getFURenderer().fuItemSetParamFaceup(name, level);
                        }
                    }
                }
            }
        }

        // 自定义界面
        private void showFaceInfo() {
            AvatarFaceType avatarFaceType = mAvatarTypeAdapter.getSelectedItems().valueAt(0);
            int type = avatarFaceType.getType();
            List<AvatarFaceAspect> avatarFaceAspects = mAvatarFaceAspects.get(type);
            if (avatarFaceAspects == null) {
                return;
            }

            startCustomAnimation();

            mDiscreteSeekBar.setProgress(0);
            mSeekBarContainer.setVisibility(View.INVISIBLE);
            mTvTitle.setText(avatarFaceType.getName());
            mAvatarAspectAdapter.replaceAll(avatarFaceAspects);

            mFaceShapeLevelMap.clear();
            AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.clear();
            Set<String> faceAspects = AvatarFaceHelper.FACE_ASPECT_TYPE_MAP.get(type);
            for (String faceAspect : faceAspects) {
                AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.put(faceAspect, 0f);
                mActivity.getFURenderer().fuItemSetParamFaceup(faceAspect, 0f);
            }
            Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
            if (paramsMap != null) {
                Set<Map.Entry<String, Float>> entries = paramsMap.entrySet();
                for (Map.Entry<String, Float> entry : entries) {
                    Float value = entry.getValue();
                    if (value > 0) {
                        mActivity.getFURenderer().fuItemSetParamFaceup(entry.getKey(), value);
                    } else {
                        value = entry.getValue();
                        if (value != null && value > 0) {
                            mActivity.getFURenderer().fuItemSetParamFaceup(AvatarFaceHelper.oppositeOf(entry.getKey()), value);
                        }
                    }
                }
            }
        }
    }

    // 控件点击事件
    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.iv_avatar_back: {
                    backPressed();
                }
                break;
                case R.id.iv_avatar_save: {
                    if (mInCustom) {
                        // 保存调节的效果
                        Set<Map.Entry<String, Float>> entries = AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.entrySet();
                        int type = mAvatarTypeAdapter.getSelectedItems().valueAt(0).getType();
                        Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
                        if (paramsMap == null) {
                            paramsMap = new HashMap<>(8);
                            mCustomedLevelMaps.put(type, paramsMap);
                        }
                        for (Map.Entry<String, Float> entry : entries) {
                            Float value = entry.getValue();
                            AvatarFaceHelper.FACE_ASPECT_MAP.put(entry.getKey(), value);
                            if (value > 0) {
                                paramsMap.put(entry.getKey(), value);
                            }
                        }
                        AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.clear();
                        mFaceShapeLevelMap.clear();
                        startMakeAnimation();
                    } else {
                        if (!TextUtils.isEmpty(AvatarFaceHelper.sFaceHairBundlePath)) {
                            // 有头发
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    // avoid bundle not loaded or avatar hair queue is empty
                                    if (mActivity.getFURenderer().isAvatarMakeupItemLoaded()) {
                                        mActivity.setSnapShot();
                                    } else {
                                        mMainHandler.postDelayed(this, 500);
                                    }
                                }
                            };
                            runnable.run();
                        } else {
                            // 没头发
                            mActivity.setSnapShot();
                        }
                    }
                }
                break;
                case R.id.iv_avatar_reset: {
                    // 重置参数
                    ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getString(R.string.dialog_reset_avatar_model), new BaseDialogFragment.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            AvatarFaceType avatarFaceType = mAvatarTypeAdapter.getSelectedItems().valueAt(0);
                            int type = avatarFaceType.getType();
                            Set<String> keys = AvatarFaceHelper.FACE_ASPECT_TYPE_MAP.get(type);
                            Map<String, Float> paramMap = mCustomedLevelMaps.get(type);
                            if (paramMap == null) {
                                paramMap = new HashMap<>();
                            }
                            Set<Map.Entry<String, Float>> entries = paramMap.entrySet();
                            for (Map.Entry<String, Float> entry : entries) {
                                entry.setValue(0F);
                            }
                            for (String key : keys) {
                                mActivity.getFURenderer().fuItemSetParamFaceup(key, 0f);
                            }
                            mFaceShapeLevelMap.clear();
                            AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.clear();
                            mDiscreteSeekBar.setProgress(0);
                        }

                        @Override
                        public void onCancel() {

                        }
                    });
                    confirmDialogFragment.show(mActivity.getSupportFragmentManager(), "ConfirmDialogFragmentReset");
                }
                break;
                default:
            }
        }
    }

    // 自定义点击事件
    private class AvatarAspectClickListener implements BaseRecyclerAdapter.OnItemClickListener<AvatarFaceAspect> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<AvatarFaceAspect> adapter, View view, int position) {
            if (mSeekBarContainer.getVisibility() != View.VISIBLE) {
                mSeekBarContainer.setVisibility(View.VISIBLE);
            }
            AvatarFaceAspect item = adapter.getItem(position);
            String name = item.getName();
            boolean isEmpty = mFaceShapeLevelMap.isEmpty();
            Float level = mFaceShapeLevelMap.get(name);
            float finalLevel = 0;
            String oppoName = AvatarFaceHelper.oppositeOf(name);
            if (level == null || level == 0) {
                Float oppoLevel = mFaceShapeLevelMap.get(oppoName);
                if (oppoLevel != null && oppoLevel != 0) {
                    finalLevel = -oppoLevel;
                }
            } else {
                finalLevel = level;
            }

            if (isEmpty) {
                int type = mAvatarTypeAdapter.getSelectedItems().valueAt(0).getType();
                Map<String, Float> paramsMap = mCustomedLevelMaps.get(type);
                if (paramsMap != null) {
                    Float customLevel = paramsMap.get(name);
                    if (customLevel == null || customLevel == 0) {
                        customLevel = paramsMap.get(oppoName);
                        if (customLevel != null) {
                            finalLevel = -customLevel;
                        }
                    } else {
                        finalLevel = customLevel;
                    }
                }
            }

            mDiscreteSeekBar.setProgress((int) (100 * finalLevel));
            mTvItemName.setText(item.getDescriptionId());
            mActivity.getFURenderer().enterFaceShape();
        }
    }

    // 底部五官类型点击事件
    private class AvatarTypeClickListener implements BaseRecyclerAdapter.OnItemClickListener<AvatarFaceType> {
        private ValueAnimator mShowAnimator;
        private ValueAnimator mHideAnimator;
        private boolean mLastHasColor;

        @Override
        public void onItemClick(BaseRecyclerAdapter<AvatarFaceType> adapter, View view, int position) {
            AvatarFaceType item = adapter.getItem(position);
            int type = item.getType();
            List<AvatarComponent> avatarComponents = AvatarFaceHelper.getAvatarComponents(type);
            mAvatarComponentAdapter.replaceAll(avatarComponents);
            // 默认选中自定义，头发和鼻子选中第一个
            int selPos = AvatarFaceHelper.FACE_ASPECT_SELECTED_POSITION.get(type, -1);
            AvatarComponent selAvatarComp = null;
            if (selPos >= 0) {
                selAvatarComp = mAvatarComponentAdapter.getItem(selPos);
                mAvatarComponentAdapter.setItemSelected(selPos);
            }

            double[][] colors = item.getColors();
            if (colors != null) {
                mAvatarColorAdapter.replaceAll(Arrays.asList(colors));
                int colorPos = AvatarFaceHelper.FACE_ASPECT_COLOR_SELECTED_POSITION.get(type, 0);
                if (colorPos >= 0) {
                    if (position > 0) {
                        // 头发之外的类型
                        if (selPos >= 0) {
                            showColorList();
                            mAvatarColorAdapter.setItemSelected(colorPos);
                            mRvAvatarColor.scrollToPosition(colorPos);
                        } else {
                            hideColorList();
                        }
                    } else {
                        // 头发类型
                        if (selAvatarComp != null) {
                            if (!TextUtils.isEmpty(selAvatarComp.getBundlePath())) {
                                showColorList();
                                mAvatarColorAdapter.setItemSelected(colorPos);
                                mRvAvatarColor.scrollToPosition(colorPos);
                            } else {
                                hideColorList();
                            }
                        } else {
                            hideColorList();
                        }
                    }
                } else {
                    if (selPos >= 0) {
                        showColorList();
                    } else {
                        hideColorList();
                    }
                }
            } else {
                hideColorList();
            }
        }

        public void showColorList() {
            if (mLastHasColor) {
                return;
            }

            mLastHasColor = true;
            cancelAnim();
            mShowAnimator = ValueAnimator.ofInt(0, getResources().getDimensionPixelSize(R.dimen.x120)).setDuration(ANIMATION_DURATION);
            mShowAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int baseHeight = getResources().getDimensionPixelSize(R.dimen.x536);

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    if (animatedValue > 0) {
                        ConstraintLayout.LayoutParams colorParams = (ConstraintLayout.LayoutParams) mRvAvatarColor.getLayoutParams();
                        colorParams.height = animatedValue;
                        mRvAvatarColor.setLayoutParams(colorParams);
                    }

                    ConstraintLayout.LayoutParams modelParams = (ConstraintLayout.LayoutParams) mRvAvatarComponent.getLayoutParams();
                    modelParams.height = baseHeight - animatedValue;
                    mRvAvatarComponent.setLayoutParams(modelParams);
                }
            });
            mShowAnimator.start();
        }

        private void hideColorList() {
            if (!mLastHasColor) {
                return;
            }

            mLastHasColor = false;
            final int colorHeight = getResources().getDimensionPixelSize(R.dimen.x120);
            mHideAnimator = ValueAnimator.ofInt(colorHeight, 0).setDuration(ANIMATION_DURATION);
            mHideAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                private int baseHeight = getResources().getDimensionPixelSize(R.dimen.x416);

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int animatedValue = (int) animation.getAnimatedValue();
                    if (animatedValue > 0) {
                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mRvAvatarColor.getLayoutParams();
                        params.height = animatedValue;
                        mRvAvatarColor.setLayoutParams(params);
                    }

                    ConstraintLayout.LayoutParams modelParams = (ConstraintLayout.LayoutParams) mRvAvatarComponent.getLayoutParams();
                    modelParams.height = baseHeight + (colorHeight - animatedValue);
                    mRvAvatarComponent.setLayoutParams(modelParams);
                }
            });
            mHideAnimator.start();
        }

        private void cancelAnim() {
            if (mShowAnimator != null && mShowAnimator.isRunning()) {
                mShowAnimator.cancel();
            }
            if (mHideAnimator != null && mHideAnimator.isRunning()) {
                mHideAnimator.cancel();
            }
        }
    }

    // 进度条拖动事件
    private class SeekBarChangedListener extends DiscreteSeekBar.OnSimpleProgressChangeListener {

        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            super.onProgressChanged(seekBar, value, fromUser);
            // value: -100 --> 100, we need 0 --> 1
            if (!fromUser) {
                return;
            }

            AvatarFaceAspect avatarFaceAspect = mAvatarAspectAdapter.getSelectedItems().valueAt(0);
            float fixedValue = 1.0f * value / 100;
            String name = avatarFaceAspect.getName();
            String opposite = AvatarFaceHelper.oppositeOf(name);
            // 为了解决 home 键带来的调节无效问题，这里每次都进入捏脸模式，如果不销毁的话，大可不必这么做
            mActivity.getFURenderer().enterFaceShape();

            if (value > 0) {
                mActivity.getFURenderer().fuItemSetParamFaceup(name, fixedValue);
                mActivity.getFURenderer().fuItemSetParamFaceup(opposite, 0);
                AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.put(name, fixedValue);
                AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.put(opposite, 0f);
            } else {
                mActivity.getFURenderer().fuItemSetParamFaceup(opposite, -fixedValue);
                mActivity.getFURenderer().fuItemSetParamFaceup(name, 0);
                AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.put(opposite, -fixedValue);
                AvatarFaceHelper.CUSTOM_FACE_ASPECT_MAP.put(name, 0f);
            }
            mFaceShapeLevelMap.put(name, fixedValue);
        }
    }
}