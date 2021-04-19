package com.faceunity.ui.control

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.LightMakeupBean
import com.faceunity.ui.infe.AbstractLightMakeupDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlinx.android.synthetic.main.layout_light_makeup.view.*


/**
 *
 * DESC：
 * Created on 2020/12/11
 *
 */
class LightMakeupControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractLightMakeupDataFactory
    private lateinit var mLightMakeUpAdapter: BaseListAdapter<LightMakeupBean>

    // region  init
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_light_makeup, this)
        initView()
        initAdapter()
        bindListener()
    }

    fun bindDataFactory(dataFactory: AbstractLightMakeupDataFactory) {
        mDataFactory = dataFactory
        mLightMakeUpAdapter.setData(dataFactory.lightMakeUpBeans)
        showSeekBar()
    }


    private fun initView() {
        initHorizontalRecycleView(recycler_view)
    }

    private fun initAdapter() {
        mLightMakeUpAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<LightMakeupBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: LightMakeupBean, position: Int) {
                helper.setImageResource(R.id.iv_control, data.iconRes)
                helper.setText(R.id.tv_control, data.nameRes)
                helper.itemView.isSelected = position == mDataFactory.currentLightMakeupIndex
            }

            override fun onItemClickListener(view: View, data: LightMakeupBean, position: Int) {
                if (mDataFactory.currentLightMakeupIndex != position) {
                    changeAdapterSelected(mLightMakeUpAdapter, mDataFactory.currentLightMakeupIndex, position)
                    mDataFactory.currentLightMakeupIndex = position
                    mDataFactory.onLightMakeupSelected(data)
                    showSeekBar()
                }
            }
        }, R.layout.list_item_control_title_image_square)
        recycler_view.adapter = mLightMakeUpAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        cyt_main.setOnTouchListener { _, _ -> true }
        seek_bar.setOnProgressChangeListener(object : DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                val res = 1.0 * (value - seekBar!!.min) / 100
                val data = mDataFactory.lightMakeUpBeans[mDataFactory.currentLightMakeupIndex]
                if (!DecimalUtils.doubleEquals(res, data.intensity)) {
                    data.intensity = res
                    data.filterIntensity=res
                    mDataFactory.onLightMakeupIntensityChanged(res)
                }
            }
        })
    }

    fun showSeekBar() {
        val data = mDataFactory.lightMakeUpBeans[mDataFactory.currentLightMakeupIndex]
        if (data.key == null) {
            seek_bar.visibility = View.INVISIBLE
        } else {
            seek_bar.visibility = View.VISIBLE
            seek_bar.progress = (data.intensity * 100).toInt()
        }
    }

    // endregion

}