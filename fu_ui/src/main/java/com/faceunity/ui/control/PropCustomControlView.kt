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
import com.faceunity.ui.databinding.LayoutEffectControlBinding
import com.faceunity.ui.entity.PropCustomBean
import com.faceunity.ui.infe.AbstractPropCustomDataFactory
import com.faceunity.ui.infe.AbstractPropCustomDataFactory.Companion.TYPE_ADD


/**
 *
 * DESC：道具贴纸
 * Created on 2020/12/10
 *
 */

class PropCustomControlView @JvmOverloads constructor(
    mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractPropCustomDataFactory
    private lateinit var mPropAdapter: BaseListAdapter<PropCustomBean>
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
        initHorizontalRecycleView(mBinding.recyclerView)
    }

    /**
     *  Adapter初始化
     */
    private fun initAdapter() {
        mPropAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<PropCustomBean>() {
            override fun convert(
                viewType: Int,
                helper: BaseViewHolder,
                data: PropCustomBean,
                position: Int
            ) {
                if (data.iconId > 0) {
                    Glide.with(context).asBitmap().load(data.iconId)
                        .into(helper.getView(R.id.iv_control)!!)
                } else if (data.iconPath != null) {
                    Glide.with(context).asBitmap().load(data.iconPath)
                        .into(helper.getView(R.id.iv_control)!!)
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
        mBinding.recyclerView.adapter = mPropAdapter
    }

    /**
     * 从外部设置选择哪个项
     */
    fun setChooseIndex(position: Int) {
        changeAdapterSelected(mPropAdapter, mDataFactory.currentPropIndex, position)
        mDataFactory.currentPropIndex = position
        mDataFactory.onItemSelected(mDataFactory.propCustomBeans[position])
    }

    // endregion

}