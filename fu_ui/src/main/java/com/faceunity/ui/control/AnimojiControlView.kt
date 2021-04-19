package com.faceunity.ui.control

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.AnimationFilterBean
import com.faceunity.ui.entity.AnimojiBean
import com.faceunity.ui.infe.AbstractAnimojiDataFactory
import kotlinx.android.synthetic.main.layout_animo_control.view.*


/**
 *
 * DESC：
 * Created on 2020/12/10
 *
 */
class AnimojiControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {


    private lateinit var mDataFactory: AbstractAnimojiDataFactory

    private lateinit var mAnimojiAdapter: BaseListAdapter<AnimojiBean>
    private lateinit var mAnimationFilterAdapter: BaseListAdapter<AnimationFilterBean>


    // region  init
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_animo_control, this)
        initView()
        initAdapter()
        bindListener()
    }

    /**
     * 给控制绑定AnimojiController，数据工厂
     */
    fun bindDataFactory(dataFactory: AbstractAnimojiDataFactory) {
        mDataFactory = dataFactory
        mAnimojiAdapter.setData(dataFactory.animojis)
        mAnimationFilterAdapter.setData(dataFactory.filters)
    }

    /**
     * 收回菜单栏
     */
    fun hideControlView() {
        rg_anim.check(View.NO_ID)
    }


    private fun initView() {
        isBottomShow = true
        initHorizontalRecycleView(recycler_view)
    }

    private fun initAdapter() {
        mAnimojiAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<AnimojiBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: AnimojiBean, position: Int) {
                helper.setImageResource(R.id.iv_control, data.iconId)
                helper.itemView.isSelected = position == mDataFactory.currentAnimojiIndex
            }

            override fun onItemClickListener(view: View, data: AnimojiBean, position: Int) {
                if (mDataFactory.currentAnimojiIndex != position) {
                    changeAdapterSelected(mAnimojiAdapter, mDataFactory.currentAnimojiIndex, position)
                    mDataFactory.currentAnimojiIndex = position
                    mDataFactory.onAnimojiSelected(data)

                }
            }
        }, R.layout.list_item_control_image_circle)
        recycler_view.adapter = mAnimojiAdapter

        mAnimationFilterAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<AnimationFilterBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: AnimationFilterBean, position: Int) {
                helper.setImageResource(R.id.iv_control, data.iconId)
                helper.itemView.isSelected = position == mDataFactory.currentFilterIndex
            }

            override fun onItemClickListener(view: View, data: AnimationFilterBean, position: Int) {
                if (mDataFactory.currentFilterIndex != position) {
                    changeAdapterSelected(mAnimationFilterAdapter, mDataFactory.currentFilterIndex, position)
                    mDataFactory.currentFilterIndex = position
                    mDataFactory.onFilterSelected(data)
                }
            }
        }, R.layout.list_item_control_image_circle)
    }

    private fun bindListener() {
        rg_anim.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.cb_animoji -> {
                    changeBottomLayoutAnimator(true)
                    recycler_view.adapter = mAnimojiAdapter
                }
                R.id.cb_filter -> {
                    changeBottomLayoutAnimator(true)
                    recycler_view.adapter = mAnimationFilterAdapter
                }
                else -> {
                    changeBottomLayoutAnimator(false)
                }
            }
        }
    }
    // endregion


    /**
     * 底部动画处理
     * @param isOpen Boolean
     */
    private fun changeBottomLayoutAnimator(isOpen: Boolean) {
        if (isOpen == isBottomShow) {
            return
        }

        val start = if (isOpen) resources.getDimensionPixelSize(R.dimen.x98) else resources.getDimensionPixelSize(R.dimen.x266)
        val end = if (isOpen) resources.getDimensionPixelSize(R.dimen.x266) else resources.getDimensionPixelSize(R.dimen.x98)

        if (bottomLayoutAnimator != null && bottomLayoutAnimator!!.isRunning) {
            bottomLayoutAnimator!!.end()
        }
        bottomLayoutAnimator = ValueAnimator.ofInt(start, end).setDuration(150)
        bottomLayoutAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val params = lyt_bottom.layoutParams as ViewGroup.LayoutParams
            params.height = height
            lyt_bottom.layoutParams = params
            if (onBottomAnimatorChangeListener != null) {
                val showRate = 1.0f * (height - start) / (end - start)
                onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (!isOpen) 1 - showRate else showRate)
            }
        }
        bottomLayoutAnimator!!.start()
        isBottomShow = isOpen
    }

}