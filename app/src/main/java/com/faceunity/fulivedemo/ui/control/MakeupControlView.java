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
import com.faceunity.entity.Filter;
import com.faceunity.entity.MakeupEntity;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.MakeupCombination;
import com.faceunity.fulivedemo.entity.MakeupConfig;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.SpaceItemDecoration;
import com.faceunity.fulivedemo.ui.colorfulcircle.CircleFilledColor;
import com.faceunity.fulivedemo.ui.colorfulcircle.ColorfulCircleView;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.param.MakeupParamHelper;
import com.wuyr.pathlayoutmanager.PathLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 美妆交互栏
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
    private SparseArray<Double> mSelectedCombinationIntensitys = new SparseArray<>(16);
    // 组合滤镜的强度，<nameId, intensity>
    private Map<Integer, Double> mSelectedFilterIntensitys = new HashMap<>(16);
    private int mSelectedColorPosition = 3;
    private int mTitleSelection;
    private boolean mFirstSetup;
    private boolean mIsMakeupItemViewShown = true;
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;

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
        MakeupConfig.initConfigs(mContext);
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
        rvMakeupCombination.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.x15), 0));
        mMakeupCombinationAdapter = new MakeupCombinationAdapter(MakeupConfig.createMakeupCombinations());
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
                SparseArray<MakeupCombination> selectedItems = mMakeupCombinationAdapter.getSelectedItems();
                double intensity = (double) value / 100;
                // 组合妆容调节
                if (selectedItems.size() > 0) {
                    // 预置妆
                    MakeupCombination makeupCombination = selectedItems.valueAt(0);
                    int nameId = makeupCombination.getNameId();
                    mSelectedCombinationIntensitys.put(nameId, intensity);
                    Map<String, Object> paramMap = makeupCombination.getParamMap();
                    if (paramMap != null) {
                        Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
                        for (Map.Entry<String, Object> entry : entries) {
                            String key = entry.getKey();
                            if (key.startsWith(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PREFIX)) {
                                mOnFUControlListener.setMakeupItemIntensity(key, intensity * ((Double) entry.getValue()));
                            }
                        }
                    }

                    Filter filter = MakeupConfig.MAKEUP_COMBINATION_FILTER_MAP.get(nameId);
                    if (filter != null) {
                        mOnFUControlListener.onFilterLevelSelected((float) intensity);
                        mSelectedFilterIntensitys.put(nameId, intensity);
                    }
                } else {
                    // 自定义
                    for (int i = 0; i < mSelectedItems.size(); i++) {
                        SelectedMakeupItem selectedMakeupItem = mSelectedItems.valueAt(i);
                        MakeupItem makeupItem = selectedMakeupItem.makeupItem;
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
        mMakeupItemAdapter = new MakeupItemAdapter(new ArrayList<>(8));
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
                SparseArray<MakeupItem> selectedItems = mMakeupItemAdapter.getSelectedItems();
                if (selectedItems.size() > 0) {
                    MakeupItem makeupItem = selectedItems.valueAt(0);
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
        replaceMakeupItem(MakeupItem.FACE_MAKEUP_TYPE_LIPSTICK);
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
                SparseArray<MakeupItem> selectedItems = mMakeupItemAdapter.getSelectedItems();
                if (selectedItems.size() > 0) {
                    MakeupItem makeupItem = selectedItems.valueAt(0);
                    double[] colors = mColorBallAdapter.getItem(position);
                    String colorName = makeupItem.getColorName();
                    if (colors != null && colorName != null) {
                        MakeupItem newMakeupItem = mMakeupItemAdapter.getSelectedItems().valueAt(0);
                        int pos = mMakeupItemAdapter.indexOf(newMakeupItem);
                        if (pos >= 0) {
                            String key = "" + mSubTitleAdapter.getSelectedIndex() + pos;
                            mSelectedColors.put(key, new SelectedMakeupItem(position, makeupItem));
                            // 对于多种颜色叠加，设置多层颜色值，比如 makeup_eye_color
                            setMakeupColor(colorName, colors);
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
        final int fmpHeight = getResources().getDimensionPixelSize(R.dimen.x290);
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

    private void setMakeupColor(String colorName, double[] colorArray) {
        double[] colorRgba;
        for (int i = 0, j = colorArray.length / 4; i < j; i++) {
            colorRgba = new double[4];
            System.arraycopy(colorArray, i * 4, colorRgba, 0, colorRgba.length);
            String colorKey = colorName;
            if (i > 0) {
                colorKey = colorName + (i + 1);
            }
            mOnFUControlListener.setMakeupItemColor(colorKey, colorRgba);
        }
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
        List<MakeupItem> makeupItems = MakeupConfig.MAKEUP_ITEM_MAP.get(type);
        if (makeupItems != null) {
            mMakeupItemAdapter.replaceAll(makeupItems);
        }
        if (mSelectedItems.size() == 0) {
            for (int i = 0; i <= MakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL; i++) {
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
            MakeupItem makeupItem = selectedMakeupItem.makeupItem;
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
            MakeupItem makeupItem = selectedMakeupItem.makeupItem;
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
    }

    public void setColorListVisible(boolean visible) {
        if (Math.abs(mClFaceMakeup.getHeight()) < 2 && mClMakeupColorList != null) {
            SparseArray<TitleEntity> selectedItems = mSubTitleAdapter.getSelectedItems();
            if (selectedItems.size() > 0) {
                TitleEntity titleEntity = selectedItems.valueAt(0);
                if (mSubTitleAdapter.indexOf(titleEntity) == 0) {
                    // 粉底
                    mClMakeupColorList.setVisibility(INVISIBLE);
                } else {
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

    public void onCameraChange(int cameraFacing) {
        mCameraFacing = cameraFacing;
        SparseArray<MakeupCombination> selectedItems = mMakeupCombinationAdapter.getSelectedItems();
        if (selectedItems.size() > 0) {
            MakeupCombination makeupCombination = selectedItems.valueAt(0);
            boolean needFlipPoints = makeupCombination.getMakeupEntity().isNeedFlipPoints();
            setIsFlipPoints(needFlipPoints, true);
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
                mMakeupCombinationAdapter.getOnItemClickListener().onItemClick(mMakeupCombinationAdapter, null, 1);
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
                float s = (float) (height - startHeight) / (endHeight - startHeight);
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
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_foundation), MakeupItem.FACE_MAKEUP_TYPE_FOUNDATION, 0));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_lipstick), MakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, 1));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_blusher), MakeupItem.FACE_MAKEUP_TYPE_BLUSHER, 2));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eyebrow), MakeupItem.FACE_MAKEUP_TYPE_EYEBROW, 3));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eye_shadow), MakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, 4));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eye_liner), MakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, 5));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eyelash), MakeupItem.FACE_MAKEUP_TYPE_EYELASH, 6));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_highlight), MakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT, 7));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_shadow), MakeupItem.FACE_MAKEUP_TYPE_SHADOW, 8));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_contact_lens), MakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, 9));
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
        MakeupItem makeupItem;

        public SelectedMakeupItem(int position, MakeupItem makeupItem) {
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
            SparseArray<TitleEntity> selectedItems = getSelectedItems();
            if (selectedItems.size() > 0) {
                TitleEntity titleEntity = selectedItems.valueAt(0);
                if (titleEntity != null) {
                    titleEntity.hasSelectedItem = selected;
                    notifyItemChanged(indexOf(titleEntity));
                }
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
    private class MakeupItemAdapter extends BaseRecyclerAdapter<MakeupItem> {

        MakeupItemAdapter(@NonNull List<MakeupItem> data) {
            super(data, R.layout.layout_makeup_recycler);
        }

        public int getSelectedIndex() {
            SparseArray<MakeupItem> selectedItems = getSelectedItems();
            if (selectedItems.size() > 0) {
                return indexOf(selectedItems.valueAt(0));
            }
            return -1;
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, MakeupItem item) {
            viewHolder.setImageDrawable(R.id.makeup_recycler_img, item.getIconDrawable());
            ImageView imageView = viewHolder.getViewById(R.id.makeup_recycler_img);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(getResources().getDimensionPixelSize(R.dimen.x5)));
            Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(item.getIconDrawable()).into(imageView);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, MakeupItem data, boolean selected) {
            viewHolder.setBackground(R.id.makeup_recycler_img, selected ? R.drawable.control_filter_select : 0);
        }

        @Override
        public int indexOf(@NonNull MakeupItem data) {
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
    private class MakeupItemClickListener implements BaseRecyclerAdapter.OnItemClickListener<MakeupItem> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<MakeupItem> adapter, View view, int position) {
            MakeupItem makeupItem = adapter.getItem(position);
            if (position == 0) {
                // 卸妆
                setColorListVisible(false);
                mMakeupItemSeekBar.setVisibility(View.INVISIBLE);
                mSubTitleAdapter.setPositionHighlight(false);
                mOnFUControlListener.setMakeupItemParam(makeupItem.getParamMap());
            } else {
                int titlePos = mSubTitleAdapter.getSelectedIndex();
                String key = "" + titlePos + position;
                if (makeupItem.getNameId() > 0) {
                    ToastUtil.showWhiteTextToast(mContext, makeupItem.getNameId());
                }
                mMakeupItemSeekBar.setVisibility(View.VISIBLE);
                String colorName = makeupItem.getColorName();
                double[] colors = null;
                if (titlePos == 0) {
                    // 粉底没有颜色滑条
                    setColorListVisible(false);
                    List<double[]> colorList = makeupItem.getColorList();
                    colors = colorList.get(position + 2);
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
                        colors = colorList.get(selectedMakeupItem.position);
                    } else {
                        setColorListVisible(false);
                    }
                }
                // 必须先绑定子妆 bundle，再设置颜色等参数
                mOnFUControlListener.setMakeupItemParam(makeupItem.getParamMap());
                if (colors != null) {
                    setMakeupColor(colorName, colors);
                }
                double level;
                if (mSelectedItemIntensitys.containsKey(key)) {
                    level = mSelectedItemIntensitys.get(key);
                } else {
                    level = 1.0F;
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

    private void setCustomEnable(boolean enable) {
        mIvCustom.setEnabled(enable);
        float alpha = enable ? 1.0f : 0.6f;
        mIvCustom.setAlpha(alpha);
        mTvCustom.setAlpha(alpha);
    }

    private void setIsFlipPoints(boolean isNeedFlipPoints, boolean isSetImmediately) {
        boolean isBackCamera = false;
        if (isNeedFlipPoints) {
            isBackCamera = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        ((FURenderer) mOnFUControlListener).setIsMakeupFlipPoints(isBackCamera, isSetImmediately);
    }

    // 一级组合妆容点击事件
    private class MakeupCombinationClickListener implements BaseRecyclerAdapter.OnItemClickListener<MakeupCombination> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<MakeupCombination> adapter, View view, int position) {
            MakeupCombination makeupCombination = adapter.getItem(position);
            Map<String, Object> paramMap = makeupCombination.getParamMap();
            // 自定义妆容的时候需要使用详细配置，这里采用懒加载的方式。如果不需要自定义，完全可以忽略
            if (paramMap == null || paramMap.size() == 0) {
                paramMap = MakeupConfig.loadMakeupParamsFromJson(mContext, makeupCombination.getJsonPath());
                makeupCombination.setParamMap(paramMap);
                SparseArray<MakeupCombination.SubItem> makeupSubItems = MakeupConfig.createMakeupSubItems(paramMap);
                makeupCombination.setSubItems(makeupSubItems);
            }

            MakeupEntity makeupEntity = makeupCombination.getMakeupEntity();
            if (position == 0) {
                // 卸妆
                setCustomEnable(true);
                mMakeupCombinationSeekBar.setVisibility(View.INVISIBLE);
                mOnFUControlListener.selectMakeup(makeupEntity, paramMap);
                mOnFUControlListener.onFilterNameSelected(Filter.Key.ZIRAN_2);
                mOnFUControlListener.onFilterLevelSelected(0.4F);
                clearSelectedItems();
            } else {
                // 切换
                boolean isDaily = makeupCombination.getType() == MakeupCombination.TYPE_DAILY;
                setCustomEnable(isDaily);
                int nameId = makeupCombination.getNameId();
                double intensity = mSelectedCombinationIntensitys.get(nameId, (double) MakeupConfig.DEFAULT_INTENSITY);
                mMakeupCombinationSeekBar.setVisibility(View.VISIBLE);
                mMakeupCombinationSeekBar.setProgress((int) (intensity * 100));
                // 仅组合妆需要自定义
                Map<String, Object> paramMapCopy = new HashMap<>(32);
                if (isDaily) {
                    paramMapCopy.putAll(paramMap);
                }
                Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
                for (Map.Entry<String, Object> entry : entries) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (key.startsWith(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PREFIX)) {
                        double v = ((Double) value) * intensity;
                        paramMapCopy.put(key, v);
                    }
                }
                mOnFUControlListener.selectMakeup(makeupEntity, paramMapCopy);
                boolean needFlipPoints = makeupCombination.getMakeupEntity().isNeedFlipPoints();
                setIsFlipPoints(needFlipPoints, false);

                // filter
                Filter filter = MakeupConfig.MAKEUP_COMBINATION_FILTER_MAP.get(nameId);
                if (filter != null) {
                    mOnFUControlListener.onFilterNameSelected(filter.getName());
                    double filterLevel;
                    if (mSelectedFilterIntensitys.containsKey(nameId)) {
                        filterLevel = mSelectedFilterIntensitys.get(nameId);
                    } else {
                        filterLevel = MakeupConfig.FILTER_INTENSITY;
                        mSelectedFilterIntensitys.put(nameId, filterLevel);
                    }
                    mOnFUControlListener.onFilterLevelSelected((float) filterLevel);
                } else {
                    mOnFUControlListener.onFilterNameSelected(Filter.Key.ZIRAN_2);
                    mOnFUControlListener.onFilterLevelSelected(0.4F);
                }
            }
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
                            Double overallDensity = mSelectedCombinationIntensitys.get(makeupCombination.getNameId(),
                                    (double) MakeupConfig.DEFAULT_INTENSITY);
                            for (int i = 0, size = subItems.size(); i < size; i++) {
                                MakeupCombination.SubItem subItem = subItems.valueAt(i);
                                int selType = subItem.getType();
                                // 类型列表
                                int pos = subItem.getItemPosition();
                                double intensity = subItem.getIntensity();
                                mSubTitleAdapter.setPositionHighlight(selType, pos > 0 && intensity > 0);
                                List<MakeupItem> makeupItems = MakeupConfig.MAKEUP_ITEM_MAP.get(selType);
                                MakeupItem makeupItem = makeupItems.get(pos);
                                mSelectedItems.put(selType, new SelectedMakeupItem(pos, makeupItem));
                                // 颜色列表
                                String key = "" + selType + pos; // regard type as title position
                                mSelectedItemIntensitys.put(key, intensity * overallDensity);
                                if (selType > 0) {
                                    // 非粉底，记录右侧颜色条
                                    mSelectedColors.put(key, new SelectedMakeupItem(subItem.getColorPosition(), makeupItem));
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
                                        if (type == MakeupItem.FACE_MAKEUP_TYPE_FOUNDATION) {
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
                                    Double overallIntensity = mSelectedCombinationIntensitys.get(makeupCombination.getNameId(),
                                            (double) MakeupConfig.DEFAULT_INTENSITY);
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