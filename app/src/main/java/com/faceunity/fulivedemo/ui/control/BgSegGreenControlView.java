package com.faceunity.fulivedemo.ui.control;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.OnFUControlListener;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.CheckGroup;
import com.faceunity.fulivedemo.ui.RingCircleView;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.beautybox.BeautyBox;
import com.faceunity.fulivedemo.ui.beautybox.BeautyBoxGroup;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;
import com.faceunity.fulivedemo.utils.DecimalUtils;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 绿幕抠像 操作栏
 *
 * @author Richie on 2020.08.13
 */
public class BgSegGreenControlView extends FrameLayout {
    private static final String TAG = "BgSegGreenControlView";
    private static final double[] COLOR_GREEN = new double[]{0, 255, 0};
    private static final double[] COLOR_BLUE = new double[]{0, 0, 255};
    private static final int DEFAULT_BACKGROUND_INDEX = 3;
    private OnFUControlListener mOnFUControlListener;
    private BeautyBox mBbSimilarity;
    private BeautyBox mBbSmooth;
    private BeautyBox mBbAlpha;
    private DiscreteSeekBar mSeekBar;
    private BeautyBoxGroup mBeautyBoxGroup;
    // 当前进度值
    private SparseArray<Float> mIntensitys = new SparseArray<>();
    // 进度值默认值
    private SparseArray<Float> mDefaultValues = new SparseArray<>();
    private View mClGraphic;
    private View mClBackground;
    private View mClPalette;
    private CheckGroup mCheckGroup;
    private ValueAnimator mBottomLayoutAnimator;
    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;
    private View mClRecover;
    private RingCircleView mIvPalettePick;
    private RingCircleView mIvPaletteGreen;
    private RingCircleView mIvPaletteBlue;
    private boolean mIsShown = true;
    private OnColorPickerStateChangedListener mOnColorPickerStateChangedListener;
    private BackgroundAdapter mBackgroundAdapter;

    public BgSegGreenControlView(@NonNull Context context) {
        this(context, null);
    }

    public BgSegGreenControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BgSegGreenControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        initView();
    }

    public void setOnColorPickerStateChangedListener(OnColorPickerStateChangedListener onColorPickerStateChangedListener) {
        mOnColorPickerStateChangedListener = onColorPickerStateChangedListener;
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
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_bg_seg_green, this);
        mCheckGroup = view.findViewById(R.id.cg_bsg_tab_group);
        mCheckGroup.setOnCheckedChangeListener(new CheckGroupChangeListener());
        mClGraphic = view.findViewById(R.id.cl_bsg_panel_graphic);
        mClBackground = view.findViewById(R.id.cl_bsg_panel_background);
        mClPalette = view.findViewById(R.id.cl_bsg_palette);
        mBeautyBoxGroup = view.findViewById(R.id.bbg_bsg);
        mBeautyBoxGroup.setOnCheckedChangeListener(new BeautyCheckChangeListener());
        mBbSimilarity = view.findViewById(R.id.bg_bgs_similarity);
        mBbSmooth = view.findViewById(R.id.bg_bgs_smooth);
        mBbAlpha = view.findViewById(R.id.bg_bgs_alpha);
        ViewClickListener viewClickListener = new ViewClickListener();
        mClRecover = view.findViewById(R.id.cl_bgs_recover);
        mClRecover.setOnClickListener(viewClickListener);
        mSeekBar = view.findViewById(R.id.seek_bar_bsg_graphic);
        mSeekBar.setOnProgressChangeListener(new SeekBarChangeListener());
        setRecoverEnable(false);
        RecyclerView recyclerView = view.findViewById(R.id.rv_bsg_background);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);

        mIvPalettePick = view.findViewById(R.id.iv_bsg_palette_pick);
        mIvPalettePick.setOnClickListener(viewClickListener);
        mIvPaletteGreen = view.findViewById(R.id.iv_bsg_palette_green);
        mIvPaletteGreen.setOnClickListener(viewClickListener);
        setBackgroundColor(true);
        mIvPaletteBlue = view.findViewById(R.id.iv_bsg_palette_blue);
        mIvPaletteBlue.setOnClickListener(viewClickListener);
        setBackgroundColor(false);
        mIvPaletteGreen.setSelected(true);
        setPalettePickColor(Color.TRANSPARENT);

        List<Background> backgrounds = new ArrayList<>();
        final String sampleDir = "bg_seg_green/";
        backgrounds.add(new Background(R.string.cancel, R.drawable.demo_icon_cancel, null));
        backgrounds.add(new Background(R.string.bg_seg_green_science, R.drawable.demo_bg_science, sampleDir + "science.mp4"));
        backgrounds.add(new Background(R.string.bg_seg_green_beach, R.drawable.demo_bg_beach, sampleDir + "beach.mp4"));
        backgrounds.add(new Background(R.string.bg_seg_green_classroom, R.drawable.demo_bg_classroom, sampleDir + "classroom.mp4"));
        backgrounds.add(new Background(R.string.bg_seg_green_ink, R.drawable.demo_bg_ink_painting, sampleDir + "ink_painting.mp4"));
        backgrounds.add(new Background(R.string.bg_seg_green_forest, R.drawable.demo_bg_forest, sampleDir + "forest.mp4"));
        mBackgroundAdapter = new BackgroundAdapter(new ArrayList<>(backgrounds));
        mBackgroundAdapter.setOnItemClickListener(new BackgroundRecyclerClickListener());
        recyclerView.setAdapter(mBackgroundAdapter);
        mBackgroundAdapter.setItemSelected(DEFAULT_BACKGROUND_INDEX);

        mBeautyBoxGroup.check(R.id.bg_bgs_key_color);
        updateBeautyBoxState();
    }

    private void setBackgroundColor(boolean isGreen) {
        int color = getResources().getColor(isGreen ? R.color.green : R.color.blue);
        if (isGreen) {
            mIvPaletteGreen.setFillColor(color);
        } else {
            mIvPaletteBlue.setFillColor(color);
        }
    }

    public void postSetPalettePickColor(final int color) {
        post(new Runnable() {
            @Override
            public void run() {
                setPalettePickColor(color);
            }
        });
    }

    public void setPalettePickColor(int color) {
        boolean transparent = color == Color.TRANSPARENT;
        if (transparent) {
            mIvPalettePick.setDrawType(RingCircleView.TYPE_PICK_TRANSPARENT);
        } else {
            mIvPalettePick.setDrawType(RingCircleView.TYPE_PICK_COLOR);
            mIvPalettePick.setFillColor(color);
        }
    }

    private void initData() {
        float chromaThres = 0.45f;
        float chromaThresT = 0.3f;
        float alphaL = 0.2f;
        mIntensitys.put(R.id.bg_bgs_similarity, chromaThres);
        mIntensitys.put(R.id.bg_bgs_smooth, chromaThresT);
        mIntensitys.put(R.id.bg_bgs_alpha, alphaL);
        mDefaultValues.put(R.id.bg_bgs_similarity, chromaThres);
        mDefaultValues.put(R.id.bg_bgs_smooth, chromaThresT);
        mDefaultValues.put(R.id.bg_bgs_alpha, alphaL);
    }

    private void updateBeautyBoxState() {
        int childCount = mBeautyBoxGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            BeautyBox box = (BeautyBox) mBeautyBoxGroup.getChildAt(i);
            box.setOpen(isParamOpen(box.getId()));
        }
        setSeekBarProgress(mBeautyBoxGroup.getCheckedBeautyBoxId());
    }

    private boolean isParamOpen(int boxId) {
        Float value = mIntensitys.get(boxId);
        return value != null && value > 0;
    }

    private void setSeekBarProgress(int boxId) {
        if (boxId == R.id.bg_bgs_key_color) {
            mSeekBar.setVisibility(GONE);
        } else if (boxId != View.NO_ID) {
            Float value = mIntensitys.get(boxId);
            seekToSeekBar(value, 0, 100);
        }
    }

    private void seekToSeekBar(float value, int min, int max) {
        mSeekBar.setVisibility(VISIBLE);
        mSeekBar.setMin(min);
        mSeekBar.setMax(max);
        mSeekBar.setProgress((int) (value * (max - min) + min));
    }

    private void setRecoverEnable(boolean enable) {
        mClRecover.setAlpha(enable ? 1f : 0.6f);
        mClRecover.setEnabled(enable);
    }

    private boolean checkIfDefaultIntensity() {
        for (int i = 0, j = mDefaultValues.size(); i < j; i++) {
            if (!DecimalUtils.floatEquals(mDefaultValues.valueAt(i), mIntensitys.get(mDefaultValues.keyAt(i)))) {
                return false;
            }
        }
        return true;
    }

    private void setItemParam(int checkedId, float intensity) {
        switch (checkedId) {
            case R.id.bg_bgs_similarity:
                mOnFUControlListener.setChromaThres(intensity);
                break;
            case R.id.bg_bgs_smooth:
                mOnFUControlListener.setChromaThresT(intensity);
                break;
            case R.id.bg_bgs_alpha:
                mOnFUControlListener.setAlphaL(intensity);
                break;
            default:
        }
    }

    @Override
    public boolean isShown() {
        return mIsShown;
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
    }

    public void hideBottomLayoutAnimator() {
        mCheckGroup.check(View.NO_ID);
    }

    private void changeBottomLayoutAnimator(final int startHeight, final int endHeight) {
        if (mBottomLayoutAnimator != null && mBottomLayoutAnimator.isRunning()) {
            mBottomLayoutAnimator.end();
        }
        mBottomLayoutAnimator = ValueAnimator.ofInt(startHeight, endHeight).setDuration(200);
        mBottomLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams params = getLayoutParams();
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


    private static class Background {
        private int strId;
        private int iconId;
        private String filePath;

        public Background(int strId, int iconId, String filePath) {
            this.strId = strId;
            this.iconId = iconId;
            this.filePath = filePath;
        }

        @Override
        public String toString() {
            return "Background{" +
                    "filePath='" + filePath + '\'' +
                    '}';
        }
    }

    private static class BackgroundAdapter extends BaseRecyclerAdapter<Background> {

        public BackgroundAdapter(@NonNull List<Background> data) {
            super(data, R.layout.layout_beauty_control_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, Background item) {
            viewHolder.setImageResource(R.id.control_recycler_img, item.iconId)
                    .setText(R.id.control_recycler_text, item.strId);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, Background data, boolean selected) {
            super.handleSelectedState(viewHolder, data, selected);
            viewHolder.setBackground(R.id.control_recycler_img, selected ? R.drawable.shape_filter_selected : R.color.transparent);
        }
    }

    private class BackgroundRecyclerClickListener implements BaseRecyclerAdapter.OnItemClickListener<Background> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<Background> adapter, View view, int position) {
            if (position > 0) {
                Background item = adapter.getItem(position);
                mOnFUControlListener.setTexBgSource(item.filePath);
            } else {
                mOnFUControlListener.setTexBgSource(null);
            }
        }
    }

    private class BeautyCheckChangeListener implements BeautyBoxGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(BeautyBoxGroup group, int checkedId) {
            mClPalette.setVisibility(checkedId == R.id.bg_bgs_key_color ? VISIBLE : GONE);
            setSeekBarProgress(checkedId);
        }
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            int paletteColor = Color.TRANSPARENT;
            switch (v.getId()) {
                case R.id.cl_bgs_recover: {
                    ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(getResources().getString(R.string.dialog_reset_avatar_model),
                            new BaseDialogFragment.OnClickListener() {
                                @Override
                                public void onConfirm() {
                                    setRecoverEnable(false);
                                    for (int i = 0, j = mDefaultValues.size(); i < j; i++) {
                                        int key = mDefaultValues.keyAt(i);
                                        Float value = mDefaultValues.valueAt(i);
                                        mIntensitys.put(key, value);
                                        setItemParam(key, value);
                                    }
                                    updateBeautyBoxState();
                                    mIvPaletteGreen.performClick();
                                    setPalettePickColor(Color.TRANSPARENT);
                                    mBeautyBoxGroup.check(R.id.bg_bgs_key_color);
                                    mBackgroundAdapter.setItemSelected(DEFAULT_BACKGROUND_INDEX);
                                    // avoid hard code
                                    mOnFUControlListener.setTexBgSource("bg_seg_green/classroom.mp4");
                                }

                                @Override
                                public void onCancel() {

                                }
                            });
                    confirmDialogFragment.show(((FragmentActivity) getContext()).getSupportFragmentManager(), "ConfirmDialogFragment");
                }
                break;
                case R.id.iv_bsg_palette_pick: {
                    boolean selected = v.isSelected();
                    mIvPaletteGreen.setSelected(false);
                    mIvPaletteBlue.setSelected(false);
                    ((RingCircleView) v).setDrawType(RingCircleView.TYPE_TRANSPARENT);
                    setRecoverEnable(true);
                    if (mOnColorPickerStateChangedListener != null) {
                        mOnColorPickerStateChangedListener.onColorPickerStateChanged(!selected, paletteColor);
                    }
                }
                break;
                case R.id.iv_bsg_palette_green: {
                    if (v.isSelected()) {
                        return;
                    }
                    v.setSelected(true);
                    mIvPalettePick.setDrawType(RingCircleView.TYPE_PICK_TRANSPARENT);
                    mIvPaletteBlue.setSelected(false);
                    mOnFUControlListener.setKeyColor(COLOR_GREEN);
                    boolean isDefault = checkIfDefaultIntensity();
                    setRecoverEnable(!isDefault);
                    if (mOnColorPickerStateChangedListener != null) {
                        mOnColorPickerStateChangedListener.onColorPickerStateChanged(false, paletteColor);
                    }
                }
                break;
                case R.id.iv_bsg_palette_blue: {
                    if (v.isSelected()) {
                        return;
                    }
                    v.setSelected(true);
                    mIvPalettePick.setDrawType(RingCircleView.TYPE_PICK_TRANSPARENT);
                    mIvPaletteGreen.setSelected(false);
                    setRecoverEnable(true);
                    mOnFUControlListener.setKeyColor(COLOR_BLUE);
                    if (mOnColorPickerStateChangedListener != null) {
                        mOnColorPickerStateChangedListener.onColorPickerStateChanged(false, paletteColor);
                    }
                }
                break;
                default:
            }
        }
    }

    private class SeekBarChangeListener implements DiscreteSeekBar.OnProgressChangeListener {

        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            float intensity = (float) value / 100;
            int checkedId = mBeautyBoxGroup.getCheckedBeautyBoxId();
            mIntensitys.put(checkedId, intensity);
            setItemParam(checkedId, intensity);
            boolean isDefault = checkIfDefaultIntensity();
            setRecoverEnable(!isDefault);
            boolean paramOpen = isParamOpen(checkedId);
            switch (checkedId) {
                case R.id.bg_bgs_similarity:
                    mBbSimilarity.setOpen(paramOpen);
                    break;
                case R.id.bg_bgs_smooth:
                    mBbSmooth.setOpen(paramOpen);
                    break;
                case R.id.bg_bgs_alpha:
                    mBbAlpha.setOpen(paramOpen);
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

    private class CheckGroupChangeListener implements CheckGroup.OnCheckedChangeListener {
        private int mLastCheckedId = R.id.cb_bsg_tab_graphic;

        @Override
        public void onCheckedChanged(CheckGroup group, int checkedId) {
            if (checkedId == R.id.cb_bsg_tab_graphic) {
                mClGraphic.setVisibility(VISIBLE);
                mClBackground.setVisibility(GONE);
            } else if (checkedId == R.id.cb_bsg_tab_background) {
                mClBackground.setVisibility(VISIBLE);
                mClGraphic.setVisibility(GONE);
            }
            if ((checkedId == View.NO_ID || checkedId == mLastCheckedId) && mLastCheckedId != View.NO_ID) {
                int endHeight = (int) getResources().getDimension(R.dimen.x98);
                int startHeight = getMeasuredHeight();
                changeBottomLayoutAnimator(startHeight, endHeight);
                mIsShown = false;
            } else if (checkedId != View.NO_ID && mLastCheckedId == View.NO_ID) {
                int startHeight = (int) getResources().getDimension(R.dimen.x98);
                int endHeight = (int) getResources().getDimension(R.dimen.x366);
                changeBottomLayoutAnimator(startHeight, endHeight);
                mIsShown = true;
            }
            mLastCheckedId = checkedId;
        }
    }

    public interface OnColorPickerStateChangedListener {
        /**
         * 取色器是否选中
         *
         * @param selected
         * @param color
         */
        void onColorPickerStateChanged(boolean selected, int color);
    }

}
