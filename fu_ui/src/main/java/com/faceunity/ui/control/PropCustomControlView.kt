package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.Glide
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.PropCustomBean
import com.faceunity.ui.infe.AbstractPropCustomDataFactory
import com.faceunity.ui.infe.AbstractPropCustomDataFactory.Companion.TYPE_ADD
import kotlinx.android.synthetic.main.layout_effect_control.view.*


/**
 *
 * DESC：道具贴纸
 * Created on 2020/12/10
 *
 */

class PropCustomControlView @JvmOverloads constructor(mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractPropCustomDataFactory
    private lateinit var mPropAdapter: BaseListAdapter<PropCustomBean>

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
    fun bindDataFactory(dataFactory: AbstractPropCustomDataFactory) {
        mDataFactory = dataFactory
        mPropAdapter.setData(dataFactory.propCustomBeans)
    }

    /**
     * 添加子项
     * @param prop PropCustomBean
     * @param index Int
     */
    fun addProp(prop: PropCustomBean, index: Int) {
        mDataFactory.propCustomBeans.add(index, prop)
        mPropAdapter.setData(mDataFactory.propCustomBeans)
    }

    /**
     * 替换子项
     * @param prop PropCustomBean
     * @param index Int
     */
    fun replaceProp(prop: PropCustomBean, index: Int) {
        mDataFactory.propCustomBeans.removeAt(index)
        mDataFactory.propCustomBeans.add(index, prop)
        mPropAdapter.setData(mDataFactory.propCustomBeans)
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
        mPropAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<PropCustomBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: PropCustomBean, position: Int) {
                if (data.iconId > 0) {
                    helper.setImageResource(R.id.iv_control, data.iconId)
                } else if (data.iconPath != null) {
                    Glide.with(context).asBitmap().load(data.iconPath).into(helper.getView(R.id.iv_control)!!)
                }

                helper.itemView.isSelected = position == mDataFactory.currentPropIndex
            }

            override fun onItemClickListener(view: View, data: PropCustomBean, position: Int) {
                if (data.type == TYPE_ADD) {
                    mDataFactory.onAddPropCustomBeanClick()
                } else {
                    if (mDataFactory.currentPropIndex != position) {
                        changeAdapterSelected(mPropAdapter, mDataFactory.currentPropIndex, position)
                        mDataFactory.currentPropIndex = position
                        mDataFactory.onItemSelected(data)
                    }
                }
            }
        }, R.layout.list_item_control_image_circle)
        recycler_view.adapter = mPropAdapter
    }


    // endregion

}