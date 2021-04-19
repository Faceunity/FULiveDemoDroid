package com.faceunity.ui.control

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View 
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.entity.BodyBeautyBean
import com.faceunity.ui.entity.ModelAttributeData
import com.faceunity.ui.infe.AbstractBodyBeautyDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlinx.android.synthetic.main.layout_body_beauty_control.view.* 


/**
 *
 * DESC：
 * Created on 2020/12/11
 *
 */
class BodyBeautyControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {
    private lateinit var mDataFactory: AbstractBodyBeautyDataFactory
    private lateinit var mModelAttributeRange: HashMap<String, ModelAttributeData>
    private lateinit var mBodyBeautyBeans: ArrayList<BodyBeautyBean>
    private lateinit var mBodyAdapter: BaseListAdapter<BodyBeautyBean>
    private var mBodyIndex = 0

    // region  init

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_body_beauty_control, this)
        initView()
        initAdapter()
        bindListener()
    }


    fun bindDataFactory(dataFactory: AbstractBodyBeautyDataFactory) {
        mDataFactory = dataFactory
        mBodyBeautyBeans = mDataFactory.bodyBeautyParam
        mBodyAdapter.setData(mBodyBeautyBeans)
        mModelAttributeRange = mDataFactory.modelAttributeRange
        val data = mBodyBeautyBeans[mBodyIndex]
        val value = mDataFactory.getParamIntensity(data.key)
        val stand = mModelAttributeRange[data.key]!!.stand
        val maxRange = mModelAttributeRange[data.key]!!.maxRange
        seekToSeekBar(value, stand, maxRange)
        setRecoverEnable(checkParamsChanged())

    }

    private fun initView() {
        initHorizontalRecycleView(recycler_view)
    }


    private fun initAdapter() {
        mBodyAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<BodyBeautyBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: BodyBeautyBean, position: Int) {
                helper.setText(R.id.tv_control, data.desRes)
                val value = mDataFactory.getParamIntensity(data.key)
                val stand = mModelAttributeRange[data.key]!!.stand
                if (DecimalUtils.doubleEquals(value, stand)) {
                    helper.setImageResource(R.id.iv_control, data.closeRes)
                } else {
                    helper.setImageResource(R.id.iv_control, data.openRes)
                }
                helper.itemView.isSelected = position == mBodyIndex
            }

            override fun onItemClickListener(view: View, data: BodyBeautyBean, position: Int) {
                if (mBodyIndex != position) {
                    changeAdapterSelected(mBodyAdapter, mBodyIndex, position)
                    mBodyIndex = position
                    val value = mDataFactory.getParamIntensity(data.key)
                    val stand = mModelAttributeRange[data.key]!!.stand
                    val maxRange = mModelAttributeRange[data.key]!!.maxRange
                    seekToSeekBar(value, stand, maxRange)
                }
            }

        }, R.layout.list_item_control_title_image_circle)
        recycler_view.adapter = mBodyAdapter
    }

    private fun bindListener() {
        cyt_main.setOnTouchListener { _, _ -> true }
        beauty_seek_bar.setOnProgressChangeListener(object : DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0 * (value - seekBar!!.min) / 100
                val data = mBodyBeautyBeans[mBodyIndex]
                val value = mDataFactory.getParamIntensity(data.key)
                val range = mModelAttributeRange[data.key]!!.maxRange
                val res = valueF * range
                if (!DecimalUtils.doubleEquals(res, value)) {
                    mDataFactory.updateParamIntensity(data.key, res)
                    setRecoverEnable(checkParamsChanged())
                    updateBeautyItemUI(mBodyAdapter.getViewHolderByPosition(mBodyIndex), data)
                }
            }
        })
        lyt_beauty_recover.setOnClickListener {
            showDialog(mContext.getString(R.string.dialog_reset_avatar_model)) {
                recoverData()
            }
        }
    }


    /**
     * 设置滚动条数值
     */
    private fun seekToSeekBar(value: Double, stand: Double, range: Double) {
        if (stand == 0.5) {
            beauty_seek_bar.min = -50
            beauty_seek_bar.max = 50
            beauty_seek_bar.progress = (value * 100 / range - 50).toInt()
        } else {
            beauty_seek_bar.min = 0
            beauty_seek_bar.max = 100
            beauty_seek_bar.progress = (value * 100 / range).toInt()
        }
        beauty_seek_bar.visibility = View.VISIBLE
    }


    // endregion


    /**
     * 更新单项是否为基准值显示
     */
    private fun updateBeautyItemUI(viewHolder: BaseViewHolder?, data: BodyBeautyBean) {
        val value = mDataFactory.getParamIntensity(data.key)
        val stand = mModelAttributeRange[data.key]!!.stand
        if (DecimalUtils.doubleEquals(value, stand)) {
            viewHolder?.setImageResource(R.id.iv_control, data.closeRes)
        } else {
            viewHolder?.setImageResource(R.id.iv_control, data.openRes)
        }
    }


    private fun recoverData() {
        mBodyBeautyBeans.forEach {
            val default = mModelAttributeRange[it.key]!!.default
            mDataFactory.updateParamIntensity(it.key, default)
        }
        val data = mBodyBeautyBeans[mBodyIndex]
        val value = mDataFactory.getParamIntensity(data.key)
        val stand = mModelAttributeRange[data.key]!!.stand
        val maxRange = mModelAttributeRange[data.key]!!.maxRange
        seekToSeekBar(value, stand, maxRange)
        mBodyAdapter.notifyDataSetChanged()
        setRecoverEnable(false)
    }

    /**
     * 遍历数据确认还原按钮是否可以点击
     * @return Boolean
     */
    private fun checkParamsChanged(): Boolean {
        var item = mBodyBeautyBeans[mBodyIndex]
        var value = mDataFactory.getParamIntensity(item.key)
        var default = mModelAttributeRange[item.key]!!.default
        if (!DecimalUtils.doubleEquals(value, default)) {
            return true
        }
        mBodyBeautyBeans.forEach {
            value = mDataFactory.getParamIntensity(it.key)
            default = mModelAttributeRange[it.key]!!.default
            if (!DecimalUtils.doubleEquals(value, default)) {
                return true
            }
        }
        return false
    }


    /**
     * 重置还原按钮状态
     * @param enable Boolean
     */
    private fun setRecoverEnable(enable: Boolean) {
        if (enable) {
            tv_beauty_recover.alpha = 1f
            iv_beauty_recover.alpha = 1f
        } else {
            tv_beauty_recover.alpha = 0.6f
            iv_beauty_recover.alpha = 0.6f
        }
        lyt_beauty_recover.isEnabled = enable
    }


}