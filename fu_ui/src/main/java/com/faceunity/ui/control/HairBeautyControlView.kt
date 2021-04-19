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
import com.faceunity.ui.entity.HairBeautyBean
import com.faceunity.ui.infe.AbstractHairBeautyDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlinx.android.synthetic.main.layout_hair_beauty_control.view.*


/**
 *
 * DESC：
 * Created on 2020/12/10
 *
 */
class HairBeautyControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {


    private lateinit var mDataFactory: AbstractHairBeautyDataFactory
    private lateinit var mHairControllerAdapter: BaseListAdapter<HairBeautyBean>


    // region  init
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_hair_beauty_control, this)
        initView()
        initAdapter()
        bindListener()
    }

    /**
     * 给控制绑定FaceBeautyController，数据工厂
     * @param dataFactory IFaceBeautyDataFactory
     */
    fun bindDataFactory(dataFactory: AbstractHairBeautyDataFactory) {
        mDataFactory = dataFactory
        mHairControllerAdapter.setData(dataFactory.hairBeautyBeans)
        val hairBeauty = mDataFactory.hairBeautyBeans[mDataFactory.currentHairIndex]
        if (hairBeauty.type != -1) {
            seek_bar.visibility = View.VISIBLE
            seek_bar.progress = (hairBeauty.intensity * 100).toInt()
        }
    }


    private fun initView() {
        initHorizontalRecycleView(recycler_view)
    }

    private fun initAdapter() {
        mHairControllerAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<HairBeautyBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: HairBeautyBean, position: Int) {
                helper.setImageResource(R.id.iv_control, data.iconId)
                helper.itemView.isSelected = position == mDataFactory.currentHairIndex
            }

            override fun onItemClickListener(view: View, data: HairBeautyBean, position: Int) {
                if (mDataFactory.currentHairIndex != position) {
                    changeAdapterSelected(mHairControllerAdapter, mDataFactory.currentHairIndex, position)
                    mDataFactory.currentHairIndex = position
                    val type = data.type
                    if (type == -1) {
                        seek_bar.visibility = View.INVISIBLE
                    } else {
                        seek_bar.visibility = View.VISIBLE
                        seek_bar.progress = (data.intensity * 100).toInt()
                    }
                    mDataFactory.onHairSelected(data)
                }
            }
        }, R.layout.list_item_control_image_circle)
        recycler_view.adapter = mHairControllerAdapter
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
                val hairBeauty = mDataFactory.hairBeautyBeans[mDataFactory.currentHairIndex]
                if (!DecimalUtils.doubleEquals(hairBeauty.intensity, res)) {
                    hairBeauty.intensity = res
                    mDataFactory.onHairIntensityChanged(res)
                }
            }
        })
    }

    // endregion

}