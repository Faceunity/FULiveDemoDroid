package com.faceunity.fulivedemo.ui.control;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.FaceMakeup;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.entity.FaceMakeupEnum;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.seekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tujh on 2017/8/15.
 */

public class MakeupControlView extends FrameLayout {
    private static final String TAG = MakeupControlView.class.getSimpleName();

    private Context mContext;

    private OnFUControlListener mOnFUControlListener;

    public void setOnFUControlListener(@NonNull OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    private ConstraintLayout mClMakeupItem;
    private ConstraintLayout mClFaceMakeup;
    private MakeupItemAdapter mMakeupItemAdapter;
    private DiscreteSeekBar mMakeupSeekBar;
    // 所有二级美妆
    private SparseArray<List<MakeupItem>> mMakeupItemMap = new SparseArray<>(16);
    private DiscreteSeekBar mBeautySeekBar;
    private ValueAnimator mFirstMpAnimator;
    private ValueAnimator mSecondMpAnimator;
    private RecyclerView mMakeupMidRecycler;
    private MakeupItemTitleAdapter mMakeupItemTitleAdapter;
    private OnFaceMakeupClickListener mOnMpItemClickListener;
    private FaceMakeupAdapter mFaceMakeupAdapter;
    // 选中的二级美妆
    private SparseArray<MakeupItem> mSelectedItems = new SparseArray<>(8);

    public MakeupControlView(Context context) {
        this(context, null);
    }

    public MakeupControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MakeupControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        LayoutInflater.from(context).inflate(R.layout.layout_makeup_control, this);
        initView();
    }

    private void initView() {
        mClFaceMakeup = findViewById(R.id.cl_face_makeup);
        mClFaceMakeup.findViewById(R.id.iv_custom_makeup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击自定义
                changeFirstViewWithAnimator(false);
                int type = mMakeupItemTitleAdapter.getSelectedItems().valueAt(0).type;
                FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
                if (faceMakeup != null) {
                    List<MakeupItem> makeupItems = faceMakeup.getMakeupItems();
                    mMakeupItemTitleAdapter.setPositionsSelected(makeupItems);
                    if (makeupItems != null) {
                        for (MakeupItem makeupItem : makeupItems) {
                            mSelectedItems.put(makeupItem.getType(), makeupItem);
                        }
                    }
                }
                replaceMakeupItem(type);
            }
        });
        RecyclerView rvMakeupItems = mClFaceMakeup.findViewById(R.id.rv_face_makeup);
        rvMakeupItems.setHasFixedSize(true);
        ((SimpleItemAnimator) rvMakeupItems.getItemAnimator()).setSupportsChangeAnimations(false);
        rvMakeupItems.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        rvMakeupItems.addItemDecoration(new VHSpaceItemDecoration(0, getResources().getDimensionPixelSize(R.dimen.x15)));
        mFaceMakeupAdapter = new FaceMakeupAdapter(FaceMakeupEnum.getDefaultMakeups());
        mOnMpItemClickListener = new OnFaceMakeupClickListener();
        mFaceMakeupAdapter.setOnItemClickListener(mOnMpItemClickListener);
        rvMakeupItems.setAdapter(mFaceMakeupAdapter);
        // 默认选中桃花妆
        mFaceMakeupAdapter.setItemSelected(1);

        mMakeupSeekBar = mClFaceMakeup.findViewById(R.id.seek_bar_makeup);
        mMakeupSeekBar.setProgress(50);
        mMakeupSeekBar.setVisibility(View.INVISIBLE);
        mMakeupSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                float level = 1.0f * value / 100;
                FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
                BeautyParameterModel.sBatchMakeupLevel.put(getResources().getString(faceMakeup.getNameId()), level);
                mOnFUControlListener.onMakeupOverallLevelChanged(level);
            }
        });

        mClMakeupItem = findViewById(R.id.cl_makeup_item);
        ((View) mClMakeupItem.getParent()).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mMakeupMidRecycler = mClMakeupItem.findViewById(R.id.makeup_mid_recycler);
        mMakeupMidRecycler.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mMakeupMidRecycler.setHasFixedSize(true);
        ((SimpleItemAnimator) mMakeupMidRecycler.getItemAnimator()).setSupportsChangeAnimations(false);
        List<MakeupItem> makeupItems = new ArrayList<>(10);
        mMakeupItemAdapter = new MakeupItemAdapter(makeupItems);
        mMakeupItemAdapter.setOnItemClickListener(new OnMakeupItemClickListener());
        mMakeupItemAdapter.setItemSelected(0);
        mMakeupMidRecycler.setAdapter(mMakeupItemAdapter);
        ImageView ivmMakeupBack = mClMakeupItem.findViewById(R.id.iv_makeup_back);
        ivmMakeupBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击回退
                changeSecondViewWithAnimator(false);
                FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
                if (faceMakeup != null) {
                    List<MakeupItem> makeupItemList = faceMakeup.getMakeupItems();
                    if (makeupItemList != null) {
                        // 普通妆容
                        boolean isChanged = false;
                        out:
                        for (int i = 0, j = mSelectedItems.size(); i < j; i++) {
                            MakeupItem selMp = mSelectedItems.valueAt(i);
                            if (BeautyParameterModel.sMakeupLevel.get(selMp.getName()) /
                                    BeautyParameterModel.sBatchMakeupLevel.get(getResources().getString(faceMakeup.getNameId()))
                                    != FaceMakeupEnum.DEFAULT_BATCH_MAKEUP_LEVEL) {
                                isChanged = true;
                                break;
                            }
                            if (!TextUtils.isEmpty(selMp.getPath())) {
                                for (MakeupItem makeupItem : makeupItemList) {
                                    if (makeupItem.getType() == selMp.getType() && !TextUtils.isEmpty(makeupItem.getPath())) {
                                        if (!makeupItem.getName().equals(selMp.getName())) {
                                            isChanged = true;
                                            break out;
                                        }
                                    }
                                }
                            } else {
                                isChanged = true;
                            }
                        }
                        if (isChanged) {
                            mFaceMakeupAdapter.clearSingleItemSelected();
                            mMakeupSeekBar.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        // 卸妆
                        if (mSelectedItems.size() > 0) {
                            mFaceMakeupAdapter.clearSingleItemSelected();
                            mMakeupSeekBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });

        RecyclerView rvMakeupItem = mClMakeupItem.findViewById(R.id.rv_makeup_item);
        rvMakeupItem.setHasFixedSize(true);
        rvMakeupItem.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) rvMakeupItem.getItemAnimator()).setSupportsChangeAnimations(false);
        mMakeupItemTitleAdapter = new MakeupItemTitleAdapter(getTitles());
        rvMakeupItem.setAdapter(mMakeupItemTitleAdapter);
        mMakeupItemTitleAdapter.setItemSelected(0);
        OnTitleClickListener onTitleClickListener = new OnTitleClickListener();
        mMakeupItemTitleAdapter.setOnItemClickListener(onTitleClickListener);

        mBeautySeekBar = mClMakeupItem.findViewById(R.id.makeup_seek_bar);
        mBeautySeekBar.setProgress(50);
        mBeautySeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnSimpleProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                mMakeupItemTitleAdapter.setPosSelected(value > 0);
                float level = 1.0f * value / 100;
                MakeupItem makeupItem = mMakeupItemAdapter.getSelectedItems().valueAt(0);
                BeautyParameterModel.sMakeupLevel.put(makeupItem.getName(), level);
                makeupItem.setLevel(level);
                mOnFUControlListener.onMakeupLevelChanged(makeupItem.getType(), level);
            }
        });

        replaceMakeupItem(FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK);
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

    private void replaceMakeupItem(int type) {
        List<MakeupItem> makeupItems = mMakeupItemMap.get(type);
        if (makeupItems == null) {
            makeupItems = FaceMakeupEnum.getFaceMakeupByType(type);
            mMakeupItemMap.put(type, makeupItems);
            for (MakeupItem makeupItem : makeupItems) {
                BeautyParameterModel.sMakeupLevel.put(makeupItem.getName(), makeupItem.getLevel());
            }
        }
        mMakeupItemAdapter.replaceAll(makeupItems);
        FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
        if (faceMakeup != null && mFaceMakeupAdapter.indexOf(faceMakeup) > 0) {
            // 具体的组合妆容，二级妆容恢复默认
            List<MakeupItem> makeupItemsList = faceMakeup.getMakeupItems();
            Set<Map.Entry<String, Float>> mpEntries = BeautyParameterModel.sMakeupLevel.entrySet();
            for (Map.Entry<String, Float> mpEntry : mpEntries) {
                mpEntry.setValue(MakeupItem.DEFAULT_MAKEUP_LEVEL);
            }
            for (MakeupItem makeupItem : makeupItemsList) {
                String name = getResources().getString(faceMakeup.getNameId());
                Float lev = BeautyParameterModel.sBatchMakeupLevel.get(name);
                if (lev == null) {
                    lev = 1.0f;
                    BeautyParameterModel.sBatchMakeupLevel.put(name, lev);
                }
                float value = makeupItem.getLevel() * lev;
                BeautyParameterModel.sMakeupLevel.put(makeupItem.getName(), value);
            }
        }
        MakeupItem data = mSelectedItems.get(type);
        int pos = -1;
        if (data != null) {
            pos = mMakeupItemAdapter.indexOf(data);
        }
        if (pos < 0) {
            if (faceMakeup != null) {
                List<MakeupItem> makeupItemList = faceMakeup.getMakeupItems();
                if (makeupItemList != null) {
                    for (int i = 0, j = makeupItemList.size(); i < j; i++) {
                        MakeupItem makeupItem = makeupItemList.get(i);
                        if (makeupItem.getType() == type && !TextUtils.isEmpty(makeupItem.getPath())) {
                            pos = mMakeupItemAdapter.indexOf(makeupItem);
                            break;
                        }
                    }
                }
            } else {
                for (int i = 0; i < mSelectedItems.size(); i++) {
                    MakeupItem makeupItem = mSelectedItems.valueAt(i);
                    if (makeupItem.getType() == type && !TextUtils.isEmpty(makeupItem.getPath())) {
                        pos = mMakeupItemAdapter.indexOf(makeupItem);
                    }
                }
            }
        }
        mMakeupItemAdapter.setItemSelected(pos < 0 ? 0 : pos);
        MakeupItem selectedMp = mMakeupItemAdapter.getItem(pos < 0 ? 0 : pos);
        float level;
        if (selectedMp != null) {
            level = BeautyParameterModel.sMakeupLevel.get(selectedMp.getName());
        } else {
            level = FaceMakeupEnum.DEFAULT_BATCH_MAKEUP_LEVEL;
        }
        mBeautySeekBar.setProgress((int) (1.0f * level * 100));
        mMakeupItemTitleAdapter.setPosSelected(pos > 0);
        mMakeupMidRecycler.scrollToPosition(pos);
        mBeautySeekBar.setVisibility(pos > 0 ? View.VISIBLE : View.INVISIBLE);
    }


    private ValueAnimator mBottomLayoutAnimator;

    public interface OnBottomAnimatorChangeListener {
        void onBottomAnimatorChangeListener(float showRate);
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

    private List<TitleEntity> getTitles() {
        List<TitleEntity> titleEntities = new ArrayList<>();
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_lipstick), FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, 0));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_blusher), FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, 1));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eyebrow), FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, 2));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eye_shadow), FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, 3));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eye_liner), FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, 4));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_eyelash), FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, 5));
        titleEntities.add(new TitleEntity(getResources().getString(R.string.makeup_radio_contact_lens), FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, 6));
        return titleEntities;
    }

    public void initData() {
        FaceMakeup faceMakeup = mFaceMakeupAdapter.getSelectedItems().valueAt(0);
        if (faceMakeup != null) {
            final int pos = mFaceMakeupAdapter.indexOf(faceMakeup);
            if (faceMakeup.getMakeupItems() != null) {
                mClMakeupItem.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnMpItemClickListener.onItemClick(mFaceMakeupAdapter, null, pos);
                    }
                });
            }
        }
    }

    // 美妆标题适配器
    private class MakeupItemTitleAdapter extends BaseRecyclerAdapter<TitleEntity> {

        MakeupItemTitleAdapter(@NonNull List<TitleEntity> data) {
            super(data, R.layout.layout_makeup_recycler_mp);
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

        public void setPosSelected(boolean selected) {
            TitleEntity titleEntity = getSelectedItems().valueAt(0);
            if (titleEntity != null) {
                titleEntity.hasSelectedItem = selected;
                notifyItemChanged(indexOf(titleEntity));
            }
        }

        public void setPositionsSelected(List<MakeupItem> makeupItems) {
            for (TitleEntity datum : mData) {
                if (makeupItems != null) {
                    boolean hint = false;
                    for (MakeupItem makeupItem : makeupItems) {
                        if (datum.type == makeupItem.getType()) {
                            datum.hasSelectedItem = true;
                            hint = true;
                            break;
                        }
                    }
                    if (!hint) {
                        datum.hasSelectedItem = false;
                    }
                } else {
                    datum.hasSelectedItem = false;
                }
            }
            notifyDataSetChanged();
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

    // 妆容单项适配器
    private class MakeupItemAdapter extends BaseRecyclerAdapter<MakeupItem> {

        MakeupItemAdapter(@NonNull List<MakeupItem> data) {
            super(data, R.layout.layout_makeup_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, MakeupItem item) {
            viewHolder.setImageResource(R.id.makeup_recycler_img, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, MakeupItem data, boolean selected) {
            viewHolder.setBackground(R.id.makeup_recycler_img, selected ? R.drawable.control_filter_select : 0);
        }

        @Override
        public int indexOf(@NonNull MakeupItem data) {
            for (int i = 0, j = mData.size(); i < j; i++) {
                if (TextUtils.equals(data.getName(), mData.get(i).getName())) {
                    return i;
                }
            }
            return -1;
        }
    }

    // 妆容标题点击事件
    private class OnTitleClickListener implements BaseRecyclerAdapter.OnItemClickListener<TitleEntity> {
        private int mLastSelectedPos = 0;
        private boolean mIsShown = true;

        @Override
        public void onItemClick(BaseRecyclerAdapter<TitleEntity> adapter, View view, int position) {
            TitleEntity titleEntity = adapter.getItem(position);
            if (mLastSelectedPos != position) {
                replaceMakeupItem(titleEntity.type);
            }
            if (mIsShown) {
                if (mLastSelectedPos == position) {
                    mIsShown = false;
                    int startHeight = (int) getResources().getDimension(R.dimen.x366);
                    int endHeight = (int) getResources().getDimension(R.dimen.x98);
                    changeBottomLayoutAnimator(startHeight, endHeight);
                }
            } else {
                mIsShown = true;
                int startHeight = (int) getResources().getDimension(R.dimen.x98);
                int endHeight = (int) getResources().getDimension(R.dimen.x366);
                changeBottomLayoutAnimator(startHeight, endHeight);
            }
            mLastSelectedPos = position;
        }
    }

    // 一级菜单点击事件
    private class OnFaceMakeupClickListener implements BaseRecyclerAdapter.OnItemClickListener<FaceMakeup> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<FaceMakeup> adapter, View view, int position) {
            FaceMakeup faceMakeup = adapter.getItem(position);
            mSelectedItems.clear();
            if (position == 0) {
                mMakeupSeekBar.setVisibility(View.INVISIBLE);
            } else {
                mMakeupSeekBar.setVisibility(View.VISIBLE);
                String name = getResources().getString(faceMakeup.getNameId());
                Float level = BeautyParameterModel.sBatchMakeupLevel.get(name);
                if (level == null) {
                    level = 1.0f;
                    BeautyParameterModel.sBatchMakeupLevel.put(name, level);
                }
                mMakeupSeekBar.setProgress((int) (1.0f * level * 100));
                mOnFUControlListener.onMakeupOverallLevelChanged(level);
            }
            List<MakeupItem> makeupItems = faceMakeup.getMakeupItems();
            mOnFUControlListener.onBatchMakeupSelected(makeupItems);
        }
    }

    // 二级菜单点击事件
    private class OnMakeupItemClickListener implements BaseRecyclerAdapter.OnItemClickListener<MakeupItem> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<MakeupItem> adapter, View view, int position) {
            MakeupItem makeupItem = adapter.getItem(position);
            mSelectedItems.put(makeupItem.getType(), makeupItem);
            Float level = 0f;
            if (position == 0) {
                mBeautySeekBar.setVisibility(View.INVISIBLE);
                mMakeupItemTitleAdapter.setPosSelected(false);
            } else {
                mBeautySeekBar.setVisibility(View.VISIBLE);
                mMakeupItemTitleAdapter.setPosSelected(true);
                level = BeautyParameterModel.sMakeupLevel.get(makeupItem.getName());
                mBeautySeekBar.setProgress((int) (1.0f * level * 100));
            }
            mOnFUControlListener.onMakeupSelected(makeupItem, level);
        }
    }

    class TitleEntity {
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
}