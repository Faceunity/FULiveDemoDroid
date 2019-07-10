package com.faceunity.fulivedemo.ui.control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.FURenderer;
import com.faceunity.OnFUControlListener;
import com.faceunity.entity.NewMakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.FaceMakeupConfig;
import com.faceunity.fulivedemo.entity.MakeupCombination;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.colorfulcircle.CircleFilledColor;
import com.faceunity.fulivedemo.ui.colorfulcircle.ColorfulCircleView;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.MakeupParamHelper;
import com.wuyr.pathlayoutmanager.PathLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tujh on 2017/8/15.
 */

public class MakeupControlView extends FrameLayout {
    private static final String TAG = "MakeupControlView";

    private static final int COLOR_LIST_ANIMATION_TIME = 250;
    private Context mContext;

    private OnFUControlListener mOnFUControlListener;
    private RecyclerView mRvMakeupColor;
    private ColorBallAdapter mColorBallAdapter;
    private static final int DEFAULT_COLOR_POSITION = 3;
    private PathLayoutManager mPathLayoutManager;
    private ValueAnimator mBottomLayoutAnimator;
    private ConstraintLayout mClMakeupItem;
    private View mClMakeupColorList;
    private View mIvCustom;
    private View mTvCustom;
    private ConstraintLayout mClFaceMakeup;
    private MakeupCombinationAdapter mMakeupCombinationAdapter;
    private MakeupItemAdapter mMakeupItemAdapter;
    private DiscreteSeekBar mMakeupCombinationSeekBar;
    private ValueAnimator mFirstMpAnimator;
    private ValueAnimator mSecondMpAnimator;
    private RecyclerView mMakeupItemRecycler;
    private DiscreteSeekBar mMakeupItemSeekBar;
    private SubTitleAdapter mSubTitleAdapter;
    // 选中的二级美妆，<type, <position, item>>
    private SparseArray<SelectedMakeupItem> mSelectedItems = new SparseArray<>(10);
    // 选中的二级颜色条，<titlePos+itemPos, item>
    private Map<String, SelectedMakeupItem> mSelectedColors = new HashMap<>(32);
    // 选中的二级美妆的强度，<titlePos+itemPos, intensity>
    private Map<String, Double> mSelectedItemIntensitys = new HashMap<>(32);
    // 组合妆容的强度，<nameId, intensity>
    private SparseArray<Double> mSelectedCombinationIntensitys = new SparseArray<>(6);
    private int mSelectedColorPosition = 3;
    private int mTitleSelection = 0;
    private boolean mFirstSetup;
    private boolean mIsMakeupItemViewShown = true;
    private int mCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    public MakeupControlView(Context context) {
        this(context, null);
    }

    public MakeupControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MakeupControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.layout_makeup_control, this);
        FaceMakeupConfig.initConfigs(mContext);
        initView();
    }

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private void initView() {
        // 整体妆容
        mClFaceMakeup = findViewById(R.id.cl_face_makeup);
        mClFaceMakeup.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ViewClickListener viewClickListener = new ViewClickListener();
        mIvCustom = mClFaceMakeup.findViewById(R.id.iv_custom_makeup);
        mTvCustom = mClFaceMakeup.findViewById(R.id.tv_custom_makeup);
        mIvCustom.setOnClickListener(viewClickListener);
        RecyclerView rvMakeupCombination = mClFaceMakeup.findViewById(R.id.rv_face_makeup);
        rvMakeupCombination.setHasFixedSize(true);
        ((SimpleItemAnimator) rvMakeupCombination.getItemAnimator()).setSupportsChangeAnimations(false);
        rvMakeupCombination.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvMakeupCombination.addItemDecoration(new VHSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.x15)));
        mMakeupCombinationAdapter = new MakeupCombinationAdapter(FaceMakeupConfig.createMakeupCombination(mContext));
        MakeupCombinationClickListener makeupCombinationClickListener = new MakeupCombinationClickListener();
        mMakeupCombinationAdapter.setOnItemClickListener(makeupCombinationClickListener);
        rvMakeupCombination.setAdapter(mMakeupCombinationAdapter);
        mMakeupCombinationAdapter.setItemSelected(1);

        mMakeupCombinationSeekBar = mClFaceMakeup.findViewById(R.id.seek_bar_makeup);
        mMakeupCombinationSeekBar.setProgress(50);
        mMakeupCombinationSeekBar.setVisibility(View.INVISIBLE);
        mMakeupCombinationSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                // 组合妆容调节
                double intensity = (double) value / 100;
                SparseArray<MakeupCombination> selectedItems = mMakeupCombinationAdapter.getSelectedItems();
                if (selectedItems.size() > 0) {
                    // 预置妆
                    MakeupCombination makeupCombination = selectedItems.valueAt(0);
                    mSelectedCombinationIntensitys.put(makeupCombination.getNameId(), intensity);
                    Set<Map.Entry<String, Object>> entries = makeupCombination.getParamMap().entrySet();
                    for (Map.Entry<String, Object> entry : entries) {
                        String key = entry.getKey();
                        if (key.startsWith(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PREFIX)) {
                            mOnFUControlListener.setMakeupItemIntensity(key, intensity * ((Double) entry.getValue()));
                        }
                    }
                } else {
                    // 自定义
                    for (int i = 0; i < mSelectedItems.size(); i++) {
                        SelectedMakeupItem selectedMakeupItem = mSelectedItems.valueAt(i);
                        NewMakeupItem makeupItem = selectedMakeupItem.makeupItem;
                        mOnFUControlListener.setMakeupItemIntensity(makeupItem.getIntensityName(), intensity);
                        mSelectedCombinationIntensitys.put(R.string.makeup_customize, intensity);
                    }
                }
            }
        });

        // 二级单项妆容
        mClMakeupItem = findViewById(R.id.cl_makeup_item);
        mClMakeupItem.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        mMakeupItemRecycler = mClMakeupItem.findViewById(R.id.makeup_mid_recycler);
        mMakeupItemRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mMakeupItemRecycler.setHasFixedSize(true);
        ((SimpleItemAnimator) mMakeupItemRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        mMakeupItemAdapter = new MakeupItemAdapter(new ArrayList<NewMakeupItem>(8));
        MakeupItemClickListener makeupItemClickListener = new MakeupItemClickListener();
        mMakeupItemAdapter.setOnItemClickListener(makeupItemClickListener);
        mMakeupItemAdapter.setItemSelected(0);
        mMakeupItemRecycler.setAdapter(mMakeupItemAdapter);
        mClMakeupItem.findViewById(R.id.iv_makeup_back).setOnClickListener(viewClickListener);

        RecyclerView rvSubTitle = mClMakeupItem.findViewById(R.id.rv_makeup_item);
        rvSubTitle.setHasFixedSize(true);
        rvSubTitle.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) rvSubTitle.getItemAnimator()).setSupportsChangeAnimations(false);
        mSubTitleAdapter = new SubTitleAdapter(getTitles());
        rvSubTitle.setAdapter(mSubTitleAdapter);
        mSubTitleAdapter.setItemSelected(0);
        SubTitleClickListener subTitleClickListener = new SubTitleClickListener();
        mSubTitleAdapter.setOnItemClickListener(subTitleClickListener);

        mMakeupItemSeekBar = mClMakeupItem.findViewById(R.id.makeup_seek_bar);
        mMakeupItemSeekBar.setProgress(100);
        mMakeupItemSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                // 单项妆容调节
                mSubTitleAdapter.setPositionHighlight(value > 0);
                double level = (double) value / 100;
                SparseArray<NewMakeupItem> selectedItems = mMakeupItemAdapter.getSelectedItems();
                if (selectedItems.size() > 0) {
                    NewMakeupItem makeupItem = selectedItems.valueAt(0);
                    int selectedIndex = mMakeupItemAdapter.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        int titlePos = mSubTitleAdapter.getSelectedIndex();
                        String key = "" + titlePos + selectedIndex;
                        mSelectedItemIntensitys.put(key, level);
                        mOnFUControlListener.setMakeupItemIntensity(makeupItem.getIntensityName(), level);
                    }
                }
            }
        });
        replaceMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK);
    }

    private void initColorListView(final List<double[]> colorList) {
        mClMakeupColorList = findViewById(R.id.cl_makeup_color_list);
        mClMakeupColorList.setVisibility(VISIBLE);
        mRvMakeupColor = mClMakeupColorList.findViewById(R.id.rv_makeup_color);
        ((SimpleItemAnimator) mRvMakeupColor.getItemAnimator()).setSupportsChangeAnimations(false);
        mColorBallAdapter = new ColorBallAdapter(new ArrayList<>(colorList));
        mColorBallAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<double[]>() {
            @Override
            public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
                // 中间点是5
                if (position < 3 || position >= 8 || mSelectedColorPosition == position) {
                    return;
                }
                PathLayoutManager layoutManager = (PathLayoutManager) mRvMakeupColor.getLayoutManager();
                if (Math.abs(mSelectedColorPosition - position) > 1) {
                    layoutManager.setFixingAnimationDuration(COLOR_LIST_ANIMATION_TIME);
                } else {
                    layoutManager.setFixingAnimationDuration(COLOR_LIST_ANIMATION_TIME / 2);
                }
                if (position < adapter.getItemCount() - 2) {
                    layoutManager.smoothScrollToPosition(position);
                }
            }
        });
        mRvMakeupColor.setAdapter(mColorBallAdapter);
        int xOffset = getResources().getDimensionPixelSize(R.dimen.x100);
        // recycler view's height is 200px, item view's height is 100px, max scale is 2.
        // So path y axis is 100px, it's half of recycler's height
        // path is relative to recycler view
        final Path path = new Path();
        int xAxis = getResources().getDimensionPixelSize(R.dimen.x40);
        path.moveTo(xAxis, 0);
        path.lineTo(xAxis, getResources().getDimensionPixelSize(R.dimen.x540));
        // see https://github.com/wuyr/PathLayoutManager
        mPathLayoutManager = new PathLayoutManager(path, xOffset, RecyclerView.VERTICAL);
        mPathLayoutManager.setScrollMode(PathLayoutManager.SCROLL_MODE_NORMAL);
        mPathLayoutManager.setItemDirectionFixed(true);
        mPathLayoutManager.setFlingEnable(true);
        mPathLayoutManager.setCacheCount(5);
        mPathLayoutManager.setAutoSelect(true);
        mPathLayoutManager.setAutoSelectFraction(0.5f);
        mPathLayoutManager.setFixingAnimationDuration(COLOR_LIST_ANIMATION_TIME);
        mPathLayoutManager.setItemScaleRatio(1f, 0f, 1.5f, 0.25f, 2f, 0.5f, 1.5f, 0.75f, 1f, 1f);
        mPathLayoutManager.setOnItemSelectedListener(new PathLayoutManager.OnItemSelectedListener() {
            @Override
            public void onSelected(int position) {
                mSelectedColorPosition = position;
                PathLayoutManager layoutManager = (PathLayoutManager) mRvMakeupColor.getLayoutManager();
                layoutManager.setFixingAnimationDuration(COLOR_LIST_ANIMATION_TIME);
                SparseArray<NewMakeupItem> selectedItems = mMakeupItemAdapter.getSelectedItems();
                if (selectedItems.size() > 0) {
                    NewMakeupItem makeupItem = selectedItems.valueAt(0);
                    double[] colors = mColorBallAdapter.getItem(position);
                    String colorName = makeupItem.getColorName();
                    if (colors != null && colorName != null) {
                        NewMakeupItem newMakeupItem = mMakeupItemAdapter.getSelectedItems().valueAt(0);
                        int pos = mMakeupItemAdapter.indexOf(newMakeupItem);
                        if (pos >= 0) {
                            String key = "" + mSubTitleAdapter.getSelectedIndex() + pos;
                            mSelectedColors.put(key, new SelectedMakeupItem(position, makeupItem));
                            mOnFUControlListener.setMakeupItemColor(colorName, colors);
                        }
                    }
                }
            }
        });
        mRvMakeupColor.setLayoutManager(mPathLayoutManager);
        mPathLayoutManager.scrollToPosition(DEFAULT_COLOR_POSITION);
    }

    private void changeFirstViewWithAnimator(final boolean showFirstView) {
        if (mFirstMpAnimator != null && mFirstMpAnimator.isRunning()) {
            mFirstMpAnimator.cancel();
        }
        final int fmpHeight = getResources().getDimensionPixelSize(R.dimen.x268);
        int start = showFirstView ? 0 : fmpHeight;
        int end = showFirstView ? fmpHeight : 0;
        mFirstMpAnimator = ValueAnimator.ofInt(start, end);
        mFirstMpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                FrameLayout.LayoutParams fmpLayoutParams = (FrameLayout.LayoutParams) mClFaceMakeup.getLayoutParams();
                fmpLayoutParams.height = (int) animation.getAnimatedValue();
                mClFaceMakeup.setLayoutParams(fmpLayoutParams);
                if (mOnBottomAnimatorChangeListener != null) {
                    float animatedFraction = animation.getAnimatedFraction();
                    mOnBottomAnimatorChangeListener.onFirstMakeupAnimatorChangeListener(
                            showFirstView ? 1 - animatedFraction : animatedFraction);
                }
            }
        });
        mFirstMpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!showFirstView) {
                    changeSecondViewWithAnimator(true);
                }
            }
        });
        mFirstMpAnimator.setDuration(150);
        mFirstMpAnimator.start();
    }

    private void changeSecondViewWithAnimator(final boolean showSecondView) {
        if (mSecondMpAnimator != null && mSecondMpAnimator.isRunning()) {
            mSecondMpAnimator.cancel();
        }
        final int mpItemHeight = getResources().getDimensionPixelSize(R.dimen.x366);
        int start = showSecondView ? 0 : mpItemHeight;
        int end = showSecondView ? mpItemHeight : 0;
        mSecondMpAnimator = ValueAnimator.ofInt(start, end);
        mSecondMpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                FrameLayout.LayoutParams mpItemLayoutParams = (FrameLayout.LayoutParams) mClMakeupItem.getLayoutParams();
                mpItemLayoutParams.height = (int) animation.getAnimatedValue();
                mClMakeupItem.setLayoutParams(mpItemLayoutParams);
            }
        });
        mSecondMpAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!showSecondView) {
                    changeFirstViewWithAnimator(true);
                }
            }
        });
        mSecondMpAnimator.setDuration(150);
        mSecondMpAnimator.start();
    }

    // 切换二级单项妆容
    private void replaceMakeupItem(int type) {
        List<NewMakeupItem> makeupItems = FaceMakeupConfig.MAKEUP_ITEM_MAP.get(type);
        if (makeupItems != null) {
            mMakeupItemAdapter.replaceAll(makeupItems);
        }
        if (mSelectedItems.size() == 0) {
            for (int i = 0; i <= NewMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL; i++) {
                mSubTitleAdapter.setPositionHighlight(i, false);
            }
        }
        SelectedMakeupItem selectedMakeupItem = mSelectedItems.get(type);
        int position = 0;
        int titleSelectedIndex = mSubTitleAdapter.getSelectedIndex();
        String key = "" + titleSelectedIndex + position;
        if (selectedMakeupItem != null) {
            position = selectedMakeupItem.position;
            key = "" + titleSelectedIndex + position;
            NewMakeupItem makeupItem = selectedMakeupItem.makeupItem;
            if (mSelectedItemIntensitys.containsKey(key)) {
                double intensity = mSelectedItemIntensitys.get(key);
                mMakeupItemSeekBar.setProgress((int) (intensity * 100));
                mSubTitleAdapter.setPositionHighlight(position > 0 && intensity > 0);
                if (makeupItem != null && position > 0) {
                    mOnFUControlListener.setMakeupItemIntensity(makeupItem.getIntensityName(), intensity);
                }
            }
        }
        mMakeupItemSeekBar.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        mMakeupItemRecycler.scrollToPosition(position);
        mMakeupItemAdapter.setItemSelected(position);

        selectedMakeupItem = mSelectedColors.get(key);
        if (selectedMakeupItem != null) {
            NewMakeupItem makeupItem = selectedMakeupItem.makeupItem;
            if (makeupItem != null) {
                List<double[]> colorList = makeupItem.getColorList();
                if (colorList != null) {
                    if (selectedMakeupItem.position >= 3) {
                        if (mClMakeupColorList != null) {
                            mClMakeupColorList.setVisibility(VISIBLE);
                            mColorBallAdapter.replaceAll(colorList);
                            mPathLayoutManager.scrollToPosition(selectedMakeupItem.position);
                        } else {
                            initColorListView(colorList);
                            mPathLayoutManager.scrollToPosition(selectedMakeupItem.position);
                        }
                    } else {
                        setColorListVisible(false);
                    }
                } else {
                    setColorListVisible(false);
                }
            } else {
                setColorListVisible(false);
            }
        } else {
            setColorListVisible(false);
        }
//        Log.i(TAG, "replaceMakeupItem: key:" + key + ", sel:" + selectedMakeupItem);
    }

    public void setColorListVisible(boolean visible) {
        if (Math.abs(mClFaceMakeup.getHeight()) < 2 && mClMakeupColorList != null) {
            TitleEntity titleEntity = mSubTitleAdapter.getSelectedItems().valueAt(0);
            if (mSubTitleAdapter.indexOf(titleEntity) == 0) {
                // 粉底
                mClMakeupColorList.setVisibility(INVISIBLE);
            } else {
                if (titleEntity != null) {
                    SelectedMakeupItem selectedMakeupItem = mSelectedItems.get(titleEntity.type);
                    if (selectedMakeupItem != null && selectedMakeupItem.position == 0) {
                        mClMakeupColorList.setVisibility(INVISIBLE);
                    } else {
                        mClMakeupColorList.setVisibility(visible ? VISIBLE : INVISIBLE);
                    }
                }
            }
        }
    }

    public void setTitleSelection(boolean select) {
        if (select) {
            mSubTitleAdapter.setItemSelected(mTitleSelection);
        } else {
            mSubTitleAdapter.clearSingleItemSelected();
        }
    }

    public void touchScreen() {
        int startHeight = (int) getResources().getDimension(R.dimen.x366);
        if (Math.abs(mClMakeupItem.getHeight() - startHeight) < 2) {
            mIsMakeupItemViewShown = false;
            int endHeight = (int) getResources().getDimension(R.dimen.x98);
            changeBottomLayoutAnimator(startHeight, endHeight);
        }
    }

    public void onCameraChange(int cameraType) {
        mCameraType = cameraType;
        SparseArray<MakeupCombination> selectedItems = mMakeupCombinationAdapter.getSelectedItems();
        if (selectedItems.size() > 0) {
            MakeupCombination makeupCombination = selectedItems.valueAt(0);
            setIsFlipPoints(makeupCombination);
        }
    }

    public void selectDefault() {
        if (mFirstSetup) {
            return;
        }
        mFirstSetup = true;
        mClMakeupItem.post(new Runnable() {
            @Override
            public void run() {
                MakeupCombination makeupCombination = mMakeupCombinationAdapter.getItem(1);
                mMakeupCombinationSeekBar.setVisibility(View.VISIBLE);
                double intensity = mSelectedCombinationIntensitys.get(makeupCombination.getNameId(), 1.0);
                mMakeupCombinationSeekBar.setProgress((int) (intensity * 100));
                mOnFUControlListener.selectMakeupItem(makeupCombination.getParamMap(), false);
            }
        });
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(150);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = mClMakeupItem.getLayoutParams();
                params.height = height;
                mClMakeupItem.setLayoutParams(params);
                float s = 1.0f * (height - startHeight) / (endHeight - startHeight);
                float showRate = startHeight > endHeight ? 1 - s : s;
                if (mOnBottomAnimatorChangeListener != null) {
                    mOnBottomAnimatorChangeListener.onBottomAnimatorChangeListener(showRate);
                }
            }
        });
        mBottomLayoutAnimator.start();
    }

    // 顺序为：粉底 口红 腮红 眉毛 眼影 眼线 睫毛 高光 阴影 美瞳
    private List<TitleEntity> getTitles() {
        List<TitleEntity> titleEntities = new ArrayList<>();
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_foundation), NewMakeupItem.FACE_MAKEUP_TYPE_FOUNDATION, 0));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_lipstick), NewMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, 1));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_blusher), NewMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, 2));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eyebrow), NewMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, 3));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eye_shadow), NewMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, 4));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eye_liner), NewMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, 5));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eyelash), NewMakeupItem.FACE_MAKEUP_TYPE_EYELASH, 6));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_highlight), NewMakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT, 7));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_shadow), NewMakeupItem.FACE_MAKEUP_TYPE_SHADOW, 8));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_contact_lens), NewMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, 9));
        return titleEntities;
    }

    public interface OnBottomAnimatorChangeListener {
        /**
         * 底部弹起动画
         *
         * @param showRate
         */
        void onBottomAnimatorChangeListener(float showRate);

        /**
         * 一级组合妆容列表收起
         *
         * @param hideRate
         */
        void onFirstMakeupAnimatorChangeListener(float hideRate);
    }

    static class TitleEntity {
        String name;
        int type;
        int position;
        boolean hasSelectedItem;

        TitleEntity(String name, int type, int position) {
            this.name = name;
            this.type = type;
            this.position = position;
        }
    }

    static class SelectedMakeupItem {
        int position;
        NewMakeupItem makeupItem;

        public SelectedMakeupItem(int position, NewMakeupItem makeupItem) {
            this.position = position;
            this.makeupItem = makeupItem;
        }

        @Override
        public String toString() {
            return "SelectedMakeupItem{" +
                    "position=" + position +
                    ", makeupItem=" + makeupItem +
                    '}';
        }
    }

    // 二级单项妆容标题适配器
    private class SubTitleAdapter extends BaseRecyclerAdapter<TitleEntity> {

        SubTitleAdapter(@NonNull List<TitleEntity> data) {
            super(data, R.layout.layout_makeup_recycler_mp);
        }

        public int getSelectedIndex() {
            SparseArray<TitleEntity> selectedItems = getSelectedItems();
            if (selectedItems.size() > 0) {
                return indexOf(selectedItems.valueAt(0));
            }
            return -1;
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, TitleEntity item) {
            viewHolder.setText(R.id.tv_mp_title, item.name)
                    .setVisibility(R.id.iv_mp_indicator, item.hasSelectedItem ? View.VISIBLE : View.INVISIBLE);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, TitleEntity data, boolean selected) {
            viewHolder.setViewSelected(R.id.tv_mp_title, selected);
        }

        public void setPositionHighlight(boolean selected) {
            TitleEntity titleEntity = getSelectedItems().valueAt(0);
            if (titleEntity != null) {
                titleEntity.hasSelectedItem = selected;
                notifyItemChanged(indexOf(titleEntity));
            }
        }

        public void setPositionHighlight(int type, boolean selected) {
            for (int i = 0; i < mData.size(); i++) {
                TitleEntity titleEntity = mData.get(i);
                if (titleEntity.type == type) {
                    titleEntity.hasSelectedItem = selected;
                    notifyItemChanged(i);
                }
            }
        }

    }

    // 一级组合妆容适配器
    private class MakeupCombinationAdapter extends BaseRecyclerAdapter<MakeupCombination> {

        public MakeupCombinationAdapter(@NonNull List<MakeupCombination> data) {
            super(data, R.layout.layout_rv_makeup);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, MakeupCombination item) {
            viewHolder.setText(R.id.tv_makeup, getResources().getString(item.getNameId()))
                    .setImageResource(R.id.iv_makeup, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, MakeupCombination data, boolean selected) {
            ((TextView) viewHolder.getViewById(R.id.tv_makeup)).setTextColor(selected ?
                    getResources().getColor(R.color.main_color) : getResources().getColor(R.color.colorWhite));
            viewHolder.setBackground(R.id.iv_makeup, selected ? R.drawable.control_filter_select : 0);
        }
    }

    // 二级单项妆容标题点击事件
    private class SubTitleClickListener implements BaseRecyclerAdapter.OnItemClickListener<TitleEntity> {
        private int mLastPosition;

        @Override
        public void onItemClick(BaseRecyclerAdapter<TitleEntity> adapter, View view, int position) {
            mTitleSelection = position;
            TitleEntity titleEntity = adapter.getItem(position);
            if (mLastPosition != position) {
                replaceMakeupItem(titleEntity.type);
            }
            // 粉底隐藏颜色滑条
            if (position == 0) {
                setColorListVisible(false);
            }
            if (mIsMakeupItemViewShown) {
                if (mLastPosition == position) {
                    mIsMakeupItemViewShown = false;
                    int startHeight = (int) getResources().getDimension(R.dimen.x366);
                    int endHeight = (int) getResources().getDimension(R.dimen.x98);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                }
            } else {
                mIsMakeupItemViewShown = true;
                int startHeight = (int) getResources().getDimension(R.dimen.x98);
                int endHeight = (int) getResources().getDimension(R.dimen.x366);
                changeBottomLayoutAnimator(startHeight, endHeight);
            }
            mLastPosition = position;
        }
    }


    // 二级单项妆容适配器
    private class MakeupItemAdapter extends BaseRecyclerAdapter<NewMakeupItem> {

        MakeupItemAdapter(@NonNull List<NewMakeupItem> data) {
            super(data, R.layout.layout_makeup_recycler);
        }

        public int getSelectedIndex() {
            SparseArray<NewMakeupItem> selectedItems = getSelectedItems();
            if (selectedItems.size() > 0) {
                return indexOf(selectedItems.valueAt(0));
            }
            return -1;
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, NewMakeupItem item) {
            viewHolder.setImageDrawable(R.id.makeup_recycler_img, item.getIconDrawable());
            ImageView imageView = viewHolder.getViewById(R.id.makeup_recycler_img);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x5)));
            Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(item.getIconDrawable()).into(imageView);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, NewMakeupItem data, boolean selected) {
            viewHolder.setBackground(R.id.makeup_recycler_img, selected ? R.drawable.control_filter_select : 0);
        }

        @Override
        public int indexOf(@NonNull NewMakeupItem data) {
            for (int i = 0, j = mData.size(); i < j; i++) {
                if (data.getNameId() == mData.get(i).getNameId()) {
                    return i;
                }
            }
            return -1;
        }
    }

    // 颜色滑动条适配器
    private class ColorBallAdapter extends BaseRecyclerAdapter<double[]> {

        ColorBallAdapter(@NonNull List<double[]> data) {
            super(data, R.layout.rv_item_colorful_circle);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, double[] item) {
            ColorfulCircleView view = viewHolder.getViewById(R.id.v_colorful);
            CircleFilledColor circleFillColor = view.getCircleFillColor();
            int count = item.length / 4;
            CircleFilledColor.FillMode fillMode = CircleFilledColor.FillMode.SINGLE;
            for (int i = 0; i < count; i++) {
                int color = Color.argb((int) (item[i * 4 + 3] * 255), (int) (item[i * 4] * 255),
                        (int) (item[i * 4 + 1] * 255), (int) (item[i * 4 + 2] * 255));
                if (i == 0) {
                    circleFillColor.setFillColor1(color);
                    fillMode = CircleFilledColor.FillMode.SINGLE;
                } else if (i == 1) {
                    circleFillColor.setFillColor2(color);
                    if (item[7] == 1.0) {
                        fillMode = CircleFilledColor.FillMode.DOUBLE;
                    }
                } else if (i == 2) {
                    circleFillColor.setFillColor3(color);
                    if (item[7] == 1.0) {
                        fillMode = CircleFilledColor.FillMode.DOUBLE;
                    }
                    if (item[11] == 1.0) {
                        fillMode = CircleFilledColor.FillMode.TRIPLE;
                    }
                } else if (i == 3) {
                    circleFillColor.setFillColor4(color);
                    if (item[15] == 1.0) {
                        fillMode = CircleFilledColor.FillMode.QUADRUPLE;
                    }
                    if (item[11] == 1.0) {
                        fillMode = CircleFilledColor.FillMode.TRIPLE;
                    }
                    if (item[7] == 1.0) {
                        fillMode = CircleFilledColor.FillMode.DOUBLE;
                    }
                }
            }
            circleFillColor.setFillMode(fillMode);
            view.setCircleFillColor(circleFillColor);
        }
    }

    private void clearSelectedItems() {
        mSelectedItems.clear();
        mSelectedColors.clear();
        mSelectedItemIntensitys.clear();
    }

    // 二级单项妆容点击事件
    private class MakeupItemClickListener implements BaseRecyclerAdapter.OnItemClickListener<NewMakeupItem> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<NewMakeupItem> adapter, View view, int position) {
            NewMakeupItem makeupItem = adapter.getItem(position);
            if (position == 0) {
                // 卸妆
                setColorListVisible(false);
                mMakeupItemSeekBar.setVisibility(View.INVISIBLE);
                mSubTitleAdapter.setPositionHighlight(false);
                mOnFUControlListener.selectMakeupItem(makeupItem.getParamMap(), false);
            } else {
                int titlePos = mSubTitleAdapter.getSelectedIndex();
                String key = "" + titlePos + position;
                if (makeupItem.getNameId() > 0) {
                    ToastUtil.showWhiteTextToast(mContext, makeupItem.getNameId());
                }
                mMakeupItemSeekBar.setVisibility(View.VISIBLE);
                if (titlePos == 0) {
                    // 粉底没有颜色滑条
                    setColorListVisible(false);
                    List<double[]> colorList = makeupItem.getColorList();
                    mOnFUControlListener.setMakeupItemColor(makeupItem.getColorName(), colorList.get(position + 2));
                } else {
                    // 其他带有颜色滑条
                    List<double[]> colorList = makeupItem.getColorList();
                    if (colorList != null) {
                        if (mClMakeupColorList == null) {
                            initColorListView(colorList);
                            mSelectedColors.put(key, new SelectedMakeupItem(DEFAULT_COLOR_POSITION, makeupItem));
                        } else {
                            setColorListVisible(true);
                            mClMakeupColorList.setVisibility(VISIBLE);
                            mColorBallAdapter.replaceAll(colorList);
                        }
                        SelectedMakeupItem selectedMakeupItem = mSelectedColors.get(key);
                        if (selectedMakeupItem == null) {
                            selectedMakeupItem = new SelectedMakeupItem(DEFAULT_COLOR_POSITION, makeupItem);
                            mSelectedColors.put(key, selectedMakeupItem);
                        }
                        mPathLayoutManager.smoothScrollToPosition(selectedMakeupItem.position);
                        mOnFUControlListener.setMakeupItemColor(makeupItem.getColorName(), colorList.get(selectedMakeupItem.position));
                    } else {
                        setColorListVisible(false);
                    }
                }
                mOnFUControlListener.selectMakeupItem(makeupItem.getParamMap(), false);
                double level;
                if (mSelectedItemIntensitys.containsKey(key)) {
                    level = mSelectedItemIntensitys.get(key);
                } else {
                    // 妆容强度默认值 1.0
                    level = NewMakeupItem.DEFAULT_INTENSITY;
                    mSelectedItemIntensitys.put(key, level);
                }
                mMakeupItemSeekBar.setProgress((int) (level * 100));
                mOnFUControlListener.setMakeupItemIntensity(makeupItem.getIntensityName(), level);
                mSubTitleAdapter.setPositionHighlight(level > 0);
            }
            SelectedMakeupItem selectedMakeupItem = new SelectedMakeupItem(position, makeupItem);
            mSelectedItems.put(makeupItem.getType(), selectedMakeupItem);
        }
    }

    // 花花妆、星月妆、硬汉妆设置点位镜像
    private void setIsFlipPoints(MakeupCombination makeupCombination) {
        boolean isFront = mCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT;
        if (makeupCombination.getNameId() == R.string.makeup_combination_flower
                || makeupCombination.getNameId() == R.string.makeup_combination_moon
                || makeupCombination.getNameId() == R.string.makeup_combination_man) {
            ((FURenderer) mOnFUControlListener).setIsFlipPoints(isFront);
        } else {
            ((FURenderer) mOnFUControlListener).setIsFlipPoints(false);
        }
    }

    // 一级组合妆容点击事件
    private class MakeupCombinationClickListener implements BaseRecyclerAdapter.OnItemClickListener<MakeupCombination> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<MakeupCombination> adapter, View view, int position) {
            MakeupCombination makeupCombination = adapter.getItem(position);
            if (position == 0) {
                // 卸妆
                setCustomEnable(true);
                mMakeupCombinationSeekBar.setVisibility(View.INVISIBLE);
                mOnFUControlListener.selectMakeupItem(makeupCombination.getParamMap(), true);
                clearSelectedItems();
            } else {
                // 预置妆容：5个日常妆，5个主题妆。日常妆支持自定义，主题妆不支持
                if (position > 5) {
                    setCustomEnable(false);
                } else {
                    setCustomEnable(true);
                }
                double intensity = mSelectedCombinationIntensitys.get(makeupCombination.getNameId(), 1.0);
                mMakeupCombinationSeekBar.setVisibility(View.VISIBLE);
                mMakeupCombinationSeekBar.setProgress((int) (intensity * 100));
                Map<String, Object> paramMapCopy = new HashMap<>(makeupCombination.getParamMap());
                Set<Map.Entry<String, Object>> entries = paramMapCopy.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.startsWith(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PREFIX)) {
                        double v = ((Double) value) * intensity;
                        paramMapCopy.put(key, v);
                    }
                }
                mOnFUControlListener.selectMakeupItem(paramMapCopy, true);
                setIsFlipPoints(makeupCombination);
            }
        }

        private void setCustomEnable(boolean enable) {
            mIvCustom.setEnabled(enable);
            float alpha = enable ? 1.0f : 0.6f;
            mIvCustom.setAlpha(alpha);
            mTvCustom.setAlpha(alpha);
        }
    }

    // 控件点击事件
    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            switch (v.getId()) {
                case R.id.iv_custom_makeup: {
                    // 点击自定义
                    changeFirstViewWithAnimator(false);
                    int type = mSubTitleAdapter.getSelectedItems().valueAt(0).type;
                    SparseArray<MakeupCombination> selectedItems = mMakeupCombinationAdapter.getSelectedItems();
                    if (selectedItems.size() > 0) {
                        MakeupCombination makeupCombination = selectedItems.valueAt(0);
                        SparseArray<MakeupCombination.SubItem> subItems = makeupCombination.getSubItems();
                        if (subItems != null) {
                            clearSelectedItems();
                            Double overallDensity = mSelectedCombinationIntensitys.get(makeupCombination.getNameId(), 1.0);
                            for (int i = 0, size = subItems.size(); i < size; i++) {
                                MakeupCombination.SubItem subItem = subItems.valueAt(i);
                                int selType = subItem.getType();
                                // 类型列表
                                int pos = subItem.getItemPosition();
                                double intensity = subItem.getIntensity();
                                mSubTitleAdapter.setPositionHighlight(selType, pos > 0 && intensity > 0);
                                List<NewMakeupItem> makeupItems = FaceMakeupConfig.MAKEUP_ITEM_MAP.get(selType);
                                NewMakeupItem makeupItem = makeupItems.get(pos);
                                SelectedMakeupItem selectedMakeupItem = new SelectedMakeupItem(pos, makeupItem);
                                mSelectedItems.put(selType, selectedMakeupItem);
                                // 颜色列表
                                String key = "" + selType + pos; // regard type as title position
                                int colorPosition = subItem.getColorPosition();
                                selectedMakeupItem = new SelectedMakeupItem(colorPosition, makeupItem);
                                if (selType > 0) {
                                    mSelectedColors.put(key, selectedMakeupItem);
                                    mSelectedItemIntensitys.put(key, intensity * overallDensity);
                                } else {
                                    mSelectedItems.put(selType, selectedMakeupItem);
                                    key = "" + selType + colorPosition;
                                    mSelectedItemIntensitys.put(key, intensity * overallDensity);
                                }
                            }
                        } else {
                            clearSelectedItems();
                        }
                    }
                    replaceMakeupItem(type);
                }
                break;
                case R.id.iv_makeup_back: {
                    // 点击回退
                    changeSecondViewWithAnimator(false);
                    setColorListVisible(false);
                    SparseArray<MakeupCombination> selectedItems = mMakeupCombinationAdapter.getSelectedItems();
                    if (selectedItems.size() > 0) {
                        MakeupCombination makeupCombination = selectedItems.valueAt(0);
                        mMakeupCombinationSeekBar.setVisibility(VISIBLE);
                        if (mMakeupCombinationAdapter.indexOf(makeupCombination) == 0) {
                            // 卸妆
                            if (mSelectedItems.size() > 0) {
                                mMakeupCombinationAdapter.clearSingleItemSelected();
                                mMakeupCombinationSeekBar.setVisibility(INVISIBLE);
                            }
                        } else {
                            // 美妆
                            SparseArray<MakeupCombination.SubItem> subItems = makeupCombination.getSubItems();
                            boolean changed = false;
                            if (mSelectedItems.size() != subItems.size()) {
                                changed = true;
                            } else {
                                // check item
                                for (int i = 0; i < subItems.size(); i++) {
                                    MakeupCombination.SubItem subItem = subItems.valueAt(i);
                                    int type = subItem.getType();
                                    SelectedMakeupItem selectedMakeupItem = mSelectedItems.get(type);
                                    if (selectedMakeupItem != null) {
                                        if (type == NewMakeupItem.FACE_MAKEUP_TYPE_FOUNDATION) {
                                            if (selectedMakeupItem.position != subItem.getColorPosition()) {
                                                Log.d(TAG, "onMultiClick back foundation: changed item");
                                                changed = true;
                                                break;
                                            }
                                        } else {
                                            if (selectedMakeupItem.position != subItem.getItemPosition()) {
                                                Log.d(TAG, "onMultiClick back other: changed item");
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                // check intensity
                                if (!changed) {
                                    Double overallIntensity = mSelectedCombinationIntensitys.get(makeupCombination.getNameId(), 1.0);
                                    for (int i = 0; i < subItems.size(); i++) {
                                        MakeupCombination.SubItem subItem = subItems.valueAt(i);
                                        String key = "" + subItem.getType() + subItem.getItemPosition();
                                        if (mSelectedItemIntensitys.containsKey(key)) {
                                            double intensity = mSelectedItemIntensitys.get(key);
                                            if (intensity != subItem.getIntensity() * overallIntensity) {
                                                Log.d(TAG, "onMultiClick back: changed intensity");
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                                // check color
                                if (!changed) {
                                    for (int i = 0; i < subItems.size(); i++) {
                                        MakeupCombination.SubItem subItem = subItems.valueAt(i);
                                        String key = "" + subItem.getType() + subItem.getItemPosition();
                                        if (mSelectedColors.containsKey(key)) {
                                            SelectedMakeupItem selectedMakeupItem = mSelectedColors.get(key);
                                            if (selectedMakeupItem.position != subItem.getColorPosition()) {
                                                Log.d(TAG, "onMultiClick back: changed color");
                                                changed = true;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (changed) {
                                mMakeupCombinationAdapter.clearSingleItemSelected();
                                mMakeupCombinationSeekBar.setVisibility(INVISIBLE);
                            }
                        }
                    }
                }
                break;
                default:
            }
        }
    }

}