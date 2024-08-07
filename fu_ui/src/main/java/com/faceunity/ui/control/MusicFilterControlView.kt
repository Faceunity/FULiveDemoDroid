package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.databinding.LayoutEffectControlBinding
import com.faceunity.ui.entity.MusicFilterBean
import com.faceunity.ui.infe.AbstractMusicFilterDataFactory


/**
 *
 * DESC：音乐滤镜
 * Created on 2020/12/10
 *
 */

class MusicFilterControlView @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractMusicFilterDataFactory

    private lateinit var mMusicFilterAdapter: BaseListAdapter<MusicFilterBean>

    private val mBinding: LayoutEffectControlBinding by lazy {
        LayoutEffectControlBinding.inflate(LayoutInflater.from(context), this, true)
    }

    // region  init
    init {
        initView()
        initAdapter()
    }

    /**
     * 给控制绑定 EffectController，数据工厂
     * @param dataFactory IMusicFilterFactory
     */
    fun bindDataFactory(dataFactory: AbstractMusicFilterDataFactory) {
        mDataFactory = dataFactory
        mMusicFilterAdapter.setData(dataFactory.musicFilters)
    }


    /**
     *  View初始化
     */
    private fun initView() {
        initHorizontalRecycleView(mBinding.recyclerView)
    }

    /**
     *  Adapter初始化
     */
    private fun initAdapter() {
        mMusicFilterAdapter =
            BaseListAdapter(ArrayList(), object : BaseDelegate<MusicFilterBean>() {
                override fun convert(
                    viewType: Int,
                    helper: BaseViewHolder,
                    item: MusicFilterBean,
                    position: Int
                ) {
                    helper.setImageResource(R.id.iv_control, item.iconId)
                    helper.itemView.isSelected = position == mDataFactory.currentFilterIndex
                }

                override fun onItemClickListener(view: View, data: MusicFilterBean, position: Int) {
                    if (mDataFactory.currentFilterIndex != position) {
                        changeAdapterSelected(
                            mMusicFilterAdapter,
                            mDataFactory.currentFilterIndex,
                            position
                        )
                        mDataFactory.currentFilterIndex = position
                        mDataFactory.onMusicFilterSelected(data)
                    }

                }
            }, R.layout.list_item_control_image_circle)
        mBinding.recyclerView.adapter = mMusicFilterAdapter
    }


    // endregion

}