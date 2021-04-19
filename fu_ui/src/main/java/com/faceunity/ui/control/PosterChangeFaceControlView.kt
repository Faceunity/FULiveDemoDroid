package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.PosterBean
import com.faceunity.ui.infe.AbstractPosterChangeFaceDataFactory
import kotlinx.android.synthetic.main.layout_effect_control.view.*
import java.util.ArrayList


/**
 *
 * DESC：
 * Created on 2020/12/16
 *
 */
class PosterChangeFaceControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {


    private lateinit var mPosterAdapter: BaseListAdapter<PosterBean>
    private lateinit var mDataFactory: AbstractPosterChangeFaceDataFactory

    // region  init

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_effect_control, this)
        initView()
        initAdapter()
    }

    fun bindDataFactory(factoryData: AbstractPosterChangeFaceDataFactory) {
        mDataFactory = factoryData
        mPosterAdapter.setData(factoryData.posters)
        recycler_view.scrollToPosition(mDataFactory.currentPosterIndex)
    }


    private fun initView() {
        initHorizontalRecycleView(recycler_view)
    }

    private fun initAdapter() {
        mPosterAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<PosterBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, item: PosterBean, position: Int) {
                Glide.with(mContext).load(item.listIconPath).apply(RequestOptions().transform(CenterCrop()))
                    .into(helper.getView(R.id.iv_control)!!)
                helper.itemView.isSelected = position == mDataFactory.currentPosterIndex
            }

            override fun onItemClickListener(view: View, data: PosterBean, position: Int) {
                if (mDataFactory.currentPosterIndex != position) {
                    changeAdapterSelected(mPosterAdapter, mDataFactory.currentPosterIndex, position)
                    mDataFactory.currentPosterIndex = position
                    mDataFactory.onItemSelectedChange(data)
                }
            }
        }, R.layout.list_item_control_image_circle)
        recycler_view.adapter = mPosterAdapter
    }

    // endregion

}