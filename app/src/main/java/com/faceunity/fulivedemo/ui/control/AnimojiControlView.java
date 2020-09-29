package com.faceunity.fulivedemo.ui.control;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.faceunity.OnFUControlListener;
import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.CartoonFilter;
import com.faceunity.fulivedemo.entity.CartoonFilterEnum;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.ui.CheckGroup;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;

import java.util.List;

/**
 * Animoji 和动漫滤镜效果
 *
 * @author Richie on 2018.11.13
 */
public class AnimojiControlView extends FrameLayout implements CheckGroup.OnCheckedChangeListener {
    private static final int DEFAULT_FILTER_INDEX = 0;
    private static final int DEFAULT_ANIMOJI_INDEX = 0;
    private RecyclerView mRvAnim;
    private RecyclerView mRvFilter;
    private OnFUControlListener mOnFUControlListener;
    private boolean mIsShown;
    private CheckGroup mCheckGroup;
    private int mLastCheckedId = View.NO_ID;
    private OnBottomAnimatorChangeListener mOnBottomAnimatorChangeListener;
    private ValueAnimator mBottomLayoutAnimator;

    public AnimojiControlView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public AnimojiControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AnimojiControlView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_animoji_filter, this);
        mCheckGroup = view.findViewById(R.id.rg_anim);
        mCheckGroup.setOnCheckedChangeListener(this);
        mRvAnim = view.findViewById(R.id.rv_animoji);
        mRvAnim.setHasFixedSize(true);
        mRvAnim.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) mRvAnim.getItemAnimator()).setSupportsChangeAnimations(false);
        AnimojiAdapter animojiAdapter = new AnimojiAdapter(EffectEnum.getEffectsByEffectType(Effect.EFFECT_TYPE_ANIMOJI));
        animojiAdapter.setOnItemClickListener(new OnAnimojiItemClickListener());
        animojiAdapter.setItemSelected(DEFAULT_ANIMOJI_INDEX);
        mRvAnim.setAdapter(animojiAdapter);
        mRvFilter = view.findViewById(R.id.rv_filter);
        mRvFilter.setHasFixedSize(true);
        mRvFilter.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        ((SimpleItemAnimator) mRvFilter.getItemAnimator()).setSupportsChangeAnimations(false);
        FilterAdapter filterAdapter = new FilterAdapter(CartoonFilterEnum.getAllCartoonFilters());
        filterAdapter.setOnItemClickListener(new OnFilterItemClickListener());
        filterAdapter.setItemSelected(DEFAULT_FILTER_INDEX);
        mRvFilter.setAdapter(filterAdapter);
        // 默认选中 Animoji 页面无选中，开启第一个动漫滤镜
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                onCheckedChanged(mCheckGroup, R.id.cb_animoji);
            }
        });
    }

    public void setOnFUControlListener(OnFUControlListener onFUControlListener) {
        mOnFUControlListener = onFUControlListener;
    }

    @Override
    public void onCheckedChanged(CheckGroup group, int checkedId) {
        mRvFilter.setVisibility(GONE);
        mRvAnim.setVisibility(GONE);
        if (checkedId == R.id.cb_animoji) {
            mRvAnim.setVisibility(VISIBLE);
        } else if (checkedId == R.id.cb_filter) {
            mRvFilter.setVisibility(VISIBLE);
        }
        if ((checkedId == View.NO_ID || checkedId == mLastCheckedId) && mLastCheckedId != View.NO_ID) {
            int endHeight = (int) getResources().getDimension(R.dimen.x98);
            int startHeight = getMeasuredHeight();
            changeBottomLayoutAnimator(startHeight, endHeight);
            mIsShown = false;
        } else if (checkedId != View.NO_ID && mLastCheckedId == View.NO_ID) {
            int startHeight = (int) getResources().getDimension(R.dimen.x98);
            int endHeight = (int) getResources().getDimension(R.dimen.x266);
            changeBottomLayoutAnimator(startHeight, endHeight);
            mIsShown = true;
        }
        mLastCheckedId = checkedId;
    }

    @Override
    public boolean isShown() {
        return mIsShown;
    }

    public void setOnBottomAnimatorChangeListener(OnBottomAnimatorChangeListener onBottomAnimatorChangeListener) {
        mOnBottomAnimatorChangeListener = onBottomAnimatorChangeListener;
    }

    public void hideBottomLayoutAnimator() {
        mCheckGroup.check(View.NO_ID);
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

    private class OnFilterItemClickListener implements BaseRecyclerAdapter.OnItemClickListener<CartoonFilter> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<CartoonFilter> adapter, View view, int position) {
            CartoonFilter cartoonFilter = adapter.getItem(position);
            if (mOnFUControlListener != null && cartoonFilter != null) {
                mOnFUControlListener.setCartoonFilter(cartoonFilter.getStyle());
            }
        }
    }

    private class OnAnimojiItemClickListener implements BaseRecyclerAdapter.OnItemClickListener<Effect> {

        @Override
        public void onItemClick(BaseRecyclerAdapter<Effect> adapter, View view, int position) {
            Effect effect = adapter.getItem(position);
            if (mOnFUControlListener != null) {
                mOnFUControlListener.onEffectSelected(effect);
            }
        }
    }

    private static class FilterAdapter extends BaseRecyclerAdapter<CartoonFilter> {

        public FilterAdapter(@NonNull List<CartoonFilter> data) {
            super(data, R.layout.layout_animoji_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, CartoonFilter item) {
            viewHolder.setImageResource(R.id.iv_anim_item, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, CartoonFilter data, boolean selected) {
            viewHolder.setBackground(R.id.iv_anim_item, selected ? R.drawable.effect_select : 0);
        }
    }

    private static class AnimojiAdapter extends BaseRecyclerAdapter<Effect> {

        public AnimojiAdapter(@NonNull List<Effect> data) {
            super(data, R.layout.layout_animoji_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, Effect item) {
            viewHolder.setImageResource(R.id.iv_anim_item, item.getIconId());
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, Effect data, boolean selected) {
            viewHolder.setBackground(R.id.iv_anim_item, selected ? R.drawable.effect_select : 0);
        }
    }

}
