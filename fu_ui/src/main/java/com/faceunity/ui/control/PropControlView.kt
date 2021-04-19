package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.PropBean
import com.faceunity.ui.infe.AbstractPropDataFactory
import kotlinx.android.synthetic.main.layout_effect_control.view.*


/**
 *
 * DESC：道具贴纸
 * Created on 2020/12/10
 *
 */

class PropControlView @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractPropDataFactory
    private lateinit var mPropAdapter: BaseListAdapter<PropBean>

    // region  init
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_effect_control, this)
        initView()
        initAdapter()
    }

    /**
     * 给控制绑定 EffectController，数据工厂
     * @param dataFactory IFaceBeautyDataFactory
     */
    fun bindDataFactory(dataFactory: AbstractPropDataFactory) {
        mDataFactory = dataFactory
        mPropAdapter.setData(dataFactory.propBeans)
    }


    /**
     *  View初始化
     */
    private fun initView() {
        initHorizontalRecycleView(recycler_view)
    }

    /**
     *  Adapter初始化
     */
    private fun initAdapter() {
        mPropAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<PropBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: PropBean, position: Int) {
                helper.setImageResource(R.id.iv_control, data.iconId)
                helper.itemView.isSelected = position == mDataFactory.currentPropIndex
            }

            override fun onItemClickListener(view: View, data: PropBean, position: Int) {
                if (mDataFactory.currentPropIndex != position) {
                    changeAdapterSelected(mPropAdapter, mDataFactory.currentPropIndex, position)
                    mDataFactory.currentPropIndex = position
                    mDataFactory.onItemSelected(data)
                }

            }
        }, R.layout.list_item_control_image_circle)
        recycler_view.adapter = mPropAdapter
    }


    // endregion

}