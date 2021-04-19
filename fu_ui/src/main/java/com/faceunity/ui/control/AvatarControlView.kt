package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.AvatarBean
import com.faceunity.ui.infe.AbstractAvatarDataFactory
import kotlinx.android.synthetic.main.layout_avatar_control.view.*


/**
 *
 * DESC：
 * Created on 2020/12/18
 *
 */
class AvatarControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mAvatarDataFactory: AbstractAvatarDataFactory

    private lateinit var mAvatarBeanAdapter: BaseListAdapter<AvatarBean>

    // region  init
    init {
        LayoutInflater.from(mContext).inflate(R.layout.layout_avatar_control, this)
        initView()
        initAdapter()
        bindListener()
    }


    fun bindDataFactory(dataFactory: AbstractAvatarDataFactory) {
        mAvatarDataFactory = dataFactory
        mAvatarBeanAdapter.setData(dataFactory.members)
        btn_switch_pta.isChecked = dataFactory.isHumanTrackSceneFull
    }


    private fun initView() {
        initHorizontalRecycleView(recycler_view)
        val margin = mContext.resources.getDimensionPixelSize(R.dimen.x4)
        btn_switch_pta.setThumbMargin(-margin * 2.toFloat(), -margin.toFloat(), -margin * 2.toFloat(), -margin * 4.toFloat())
    }


    private fun initAdapter() {
        mAvatarBeanAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<AvatarBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, item: AvatarBean, position: Int) {
                helper.setImageResource(R.id.iv_control, item.iconId)
                helper.itemView.isSelected = position == mAvatarDataFactory.currentMemberIndex
            }

            override fun onItemClickListener(view: View, data: AvatarBean, position: Int) {
                if (mAvatarDataFactory.currentMemberIndex != position) {
                    changeAdapterSelected(mAvatarBeanAdapter, mAvatarDataFactory.currentMemberIndex, position)
                    mAvatarDataFactory.currentMemberIndex = position
                    mAvatarDataFactory.onMemberSelected(data)
                }
            }
        }, R.layout.list_item_control_image_circle)
        recycler_view.adapter = mAvatarBeanAdapter
    }

    private fun bindListener() {
        btn_switch_pta.setOnCheckedChangeListener { _, isChecked ->
            mAvatarDataFactory.isHumanTrackSceneFull = isChecked

        }
    }


}