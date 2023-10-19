package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.databinding.LayoutAvatarControlBinding
import com.faceunity.ui.entity.AvatarBean
import com.faceunity.ui.infe.AbstractAvatarDataFactory


/**
 *
 * DESC：
 * Created on 2020/12/18
 *
 */
class AvatarControlView @JvmOverloads constructor(
    private val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mAvatarDataFactory: AbstractAvatarDataFactory

    private lateinit var mAvatarBeanAdapter: BaseListAdapter<AvatarBean>

    private val mBinding: LayoutAvatarControlBinding by lazy {
        LayoutAvatarControlBinding.inflate(LayoutInflater.from(context), this, true)
    }

    // region  init
    init {
        initView()
        initAdapter()
        bindListener()
    }


    fun bindDataFactory(dataFactory: AbstractAvatarDataFactory) {
        mAvatarDataFactory = dataFactory
        mAvatarBeanAdapter.setData(dataFactory.members)
//        btn_switch_pta.visibility = View.GONE
        mBinding.btnSwitchPta.isChecked = dataFactory.isHumanTrackSceneFull
    }


    private fun initView() {
        initHorizontalRecycleView(mBinding.recyclerView)
        val margin = mContext.resources.getDimensionPixelSize(R.dimen.x4)
        mBinding.btnSwitchPta.setThumbMargin(
            -margin * 2.toFloat(),
            -margin.toFloat(),
            -margin * 2.toFloat(),
            -margin * 4.toFloat()
        )
    }


    private fun initAdapter() {
        mAvatarBeanAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<AvatarBean>() {
            override fun convert(
                viewType: Int,
                helper: BaseViewHolder,
                item: AvatarBean,
                position: Int
            ) {
                helper.setImageResource(R.id.iv_control, item.iconId)
                helper.itemView.isSelected = position == mAvatarDataFactory.currentMemberIndex
            }

            override fun onItemClickListener(view: View, data: AvatarBean, position: Int) {
                if (mAvatarDataFactory.currentMemberIndex != position) {
                    changeAdapterSelected(
                        mAvatarBeanAdapter,
                        mAvatarDataFactory.currentMemberIndex,
                        position
                    )
                    mAvatarDataFactory.currentMemberIndex = position
                    mAvatarDataFactory.onMemberSelected(data)

//                    if (position == 0) {
//                        btn_switch_pta.visibility = View.GONE
//                    } else {
//                        btn_switch_pta.visibility = View.VISIBLE
//                    }
                }
            }
        }, R.layout.list_item_control_image_circle)
        mBinding.recyclerView.adapter = mAvatarBeanAdapter
    }

    private fun bindListener() {
        mBinding.btnSwitchPta.setOnCheckedChangeListener { _, isChecked ->
            mAvatarDataFactory.isHumanTrackSceneFull = isChecked

        }
    }


}