package com.faceunity.ui.control

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.circle.RingCircleView
import com.faceunity.ui.dialog.BaseDialogFragment
import com.faceunity.ui.dialog.ConfirmDialogFragment
import com.faceunity.ui.entity.BgSegGreenBackgroundBean
import com.faceunity.ui.entity.BgSegGreenBean
import com.faceunity.ui.entity.ModelAttributeData
import com.faceunity.ui.infe.AbstractBgSegGreenDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlinx.android.synthetic.main.layout_bg_seg_green_control.view.*


/**
 *
 * DESC：
 * Created on 2020/12/4
 *
 */
class BgSegGreenControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {


    private lateinit var mDataFactory: AbstractBgSegGreenDataFactory
    private lateinit var mModelAttributeRange: HashMap<String, ModelAttributeData>


    private lateinit var mActionAdapter: BaseListAdapter<BgSegGreenBean>
    private var mActionIndex = 0

    private lateinit var mBackgroundAdapter: BaseListAdapter<BgSegGreenBackgroundBean>


    //region 初始化

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_bg_seg_green_control, this)
        initView()
        initAdapter()
        bindListener()
    }

    fun bindDataFactory(dataFactory: AbstractBgSegGreenDataFactory) {
        mDataFactory = dataFactory
        mModelAttributeRange = dataFactory.modelAttributeRange
        mActionAdapter.setData(mDataFactory.bgSegGreenActions)
        mBackgroundAdapter.setData(mDataFactory.bgSegGreenBackgrounds)
        showSeekBar()
        setPaletteSelected(1)
        setRecoverEnable(checkActionChanged())
    }

    /**
     * 收回菜单栏
     */
    fun hideControlView() {
        check_group.check(View.NO_ID)
    }

    private fun initView() {
        isBottomShow = true
        initHorizontalRecycleView(recycler_view)
        initHorizontalRecycleView(recycler_view_background)
        iv_palette_green.setFillColor(Color.parseColor("#FF00FF00"))
        iv_palette_blue.setFillColor(Color.parseColor("#FF0000FF"))
        iv_palette_pick.setDrawType(RingCircleView.TYPE_PICK_TRANSPARENT)
    }


    private fun initAdapter() {
        mActionAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<BgSegGreenBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: BgSegGreenBean, position: Int) {
                helper.setText(R.id.tv_control, data.desRes)
                if (mModelAttributeRange.containsKey(data.key)) {
                    val value = mDataFactory.getParamIntensity(data.key)
                    val stand = mModelAttributeRange[data.key]!!.stand
                    if (DecimalUtils.doubleEquals(value, stand)) {
                        helper.setImageResource(R.id.iv_control, data.closeRes)
                    } else {
                        helper.setImageResource(R.id.iv_control, data.openRes)
                    }
                } else {
                    setPaletteSelected(1)
                    helper.setImageResource(R.id.iv_control, data.openRes)
                }
                helper.itemView.isSelected = position == mActionIndex
            }

            override fun onItemClickListener(view: View, data: BgSegGreenBean, position: Int) {
                if (position != mActionIndex) {
                    changeAdapterSelected(mActionAdapter, mActionIndex, position)
                    mActionIndex = position
                    showSeekBar()
                }
            }
        }, R.layout.list_item_control_title_image_circle)
        recycler_view.adapter = mActionAdapter

        mBackgroundAdapter = BaseListAdapter(
            ArrayList(), object : BaseDelegate<BgSegGreenBackgroundBean>() {
                override fun convert(viewType: Int, helper: BaseViewHolder, item: BgSegGreenBackgroundBean, position: Int) {
                    helper.setText(R.id.tv_control, item.desRes)
                    helper.setImageResource(R.id.iv_control, item.iconRes)
                    helper.itemView.isSelected = position == mDataFactory.backgroundIndex
                }

                override fun onItemClickListener(view: View, data: BgSegGreenBackgroundBean, position: Int) {
                    super.onItemClickListener(view, data, position)
                    if (position != mDataFactory.backgroundIndex) {
                        changeAdapterSelected(mBackgroundAdapter, mDataFactory.backgroundIndex, position)
                        mDataFactory.backgroundIndex = position
                        mDataFactory.onBackgroundSelected(data)
                    }
                }
            }, R.layout.list_item_control_title_image_square
        )
        recycler_view_background.adapter = mBackgroundAdapter

    }

    private fun bindListener() {
        lyt_bottom_view.setOnTouchListener { _, _ -> true }
        bindBottomRadio()
        bindSeekBarListener()
        lyt_recover.setOnClickListener {
            val confirmDialogFragment =
                ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_reset_avatar_model),
                    object : BaseDialogFragment.OnClickListener {
                        override fun onConfirm() { // recover params
                            recoverData()
                        }

                        override fun onCancel() {}
                    })
            confirmDialogFragment.show((mContext as FragmentActivity).supportFragmentManager, "ConfirmDialogFragmentReset")
        }


        iv_palette_green.setOnClickListener {
            if (!iv_palette_green.isSelected) {
                mDataFactory.onColorPickerStateChanged(false, Color.TRANSPARENT)
                setPaletteSelected(1)
                mDataFactory.onColorRGBChanged(doubleArrayOf(0.0, 255.0, 0.0))
                setRecoverEnable(checkActionChanged())
            }

        }
        iv_palette_blue.setOnClickListener {
            if (!iv_palette_blue.isSelected) {
                mDataFactory.onColorPickerStateChanged(false, Color.TRANSPARENT)
                setPaletteSelected(2)
                mDataFactory.onColorRGBChanged(doubleArrayOf(0.0, 0.0, 255.0))
                setRecoverEnable(checkActionChanged())
            }
        }
        iv_palette_pick.setOnClickListener {
            mDataFactory.onBgSegGreenEnableChanged(false)
            setPaletteSelected(0)
            iv_palette_pick.setDrawType(RingCircleView.TYPE_TRANSPARENT)
            setRecoverEnable(true)
            mDataFactory.onColorPickerStateChanged(true, Color.TRANSPARENT)
        }
    }


    // region 业务调用


    fun dismissBottomLayout() {
        if (isBottomShow) {
            check_group.clearCheck()
            changeBottomLayoutAnimator(false)
        }
    }


    /**
     * 滑动条业务绑定
     */
    private fun bindSeekBarListener() {
        seek_bar.setOnProgressChangeListener(object : DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0 * (value - seekBar!!.min) / 100
                val data = mDataFactory.bgSegGreenActions[mActionIndex]
                val value = mDataFactory.getParamIntensity(data.key)
                val range = mModelAttributeRange[data.key]!!.maxRange
                val res = valueF * range
                if (value != res) {
                    mDataFactory.updateParamIntensity(data.key, res)
                    setRecoverEnable(checkActionChanged())
                    updateActionItem(mActionAdapter.getViewHolderByPosition(mActionIndex), data)
                }
            }
        })


    }


    /**
     * 遍历数据确认还原按钮是否可以点击
     * @return Boolean
     */
    private fun checkActionChanged(): Boolean {
        mDataFactory.bgSegGreenActions.forEach {
            if (it.type == 0) {
                if (!iv_palette_green.isSelected) {
                    return true
                }
            } else {
                var value = mDataFactory.getParamIntensity(it.key)
                var default = mModelAttributeRange[it.key]!!.default
                if (!DecimalUtils.doubleEquals(value, default)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 恢复数据
     */
    private fun recoverData() {
        mDataFactory.bgSegGreenActions.forEach {
            if (it.type == 0) {
                mDataFactory.onColorRGBChanged(doubleArrayOf(0.0, 255.0, 0.0))
            } else {
                val default = mModelAttributeRange[it.key]!!.default
                mDataFactory.updateParamIntensity(it.key, default)
            }
        }
        mDataFactory.onColorPickerStateChanged(false, Color.TRANSPARENT)
        mActionAdapter.notifyDataSetChanged()
        showSeekBar()
        setRecoverEnable(false)
    }

    /**
     * 更新单项是否为基准值显示
     */
    private fun updateActionItem(viewHolder: BaseViewHolder?, data: BgSegGreenBean) {
        val value = mDataFactory.getParamIntensity(data.key)
        val stand = mModelAttributeRange[data.key]!!.stand
        if (DecimalUtils.doubleEquals(value, stand)) {
            viewHolder?.setImageResource(R.id.iv_control, data.closeRes)
        } else {
            viewHolder?.setImageResource(R.id.iv_control, data.openRes)
        }
    }
    //endregion

    //region 视图变更

    /**
     * 关键颜色选择状态变更
     * @param index Int 0：选择器 1绿色 2蓝色
     */
    private fun setPaletteSelected(index: Int) {
        iv_palette_green.isSelected = false
        iv_palette_blue.isSelected = false
        iv_palette_pick.isSelected = false
        iv_palette_pick.setDrawType(RingCircleView.TYPE_PICK_TRANSPARENT)
        when (index) {
            0 -> iv_palette_pick.isSelected = true
            1 -> iv_palette_green.isSelected = true
            2 -> iv_palette_blue.isSelected = true

        }
    }


    /**
     * 抠像-单项控制器视图变更
     */
    private fun showSeekBar() {
        if (mActionIndex > 0) {
            val data = mDataFactory.bgSegGreenActions[mActionIndex]
            lyt_palette.visibility = View.GONE
            seek_bar.visibility = View.VISIBLE
            val value = mDataFactory.getParamIntensity(data.key)
            seek_bar.progress = (value * 100).toInt()
        } else {
            lyt_palette.visibility = View.VISIBLE
            seek_bar.visibility = View.INVISIBLE
        }
    }

    /**
     * 重置还原按钮状态
     * @param enable Boolean
     */
    private fun setRecoverEnable(enable: Boolean) {
        if (enable) {
            tv_recover.alpha = 1f
            iv_recover.alpha = 1f
        } else {
            tv_recover.alpha = 0.6f
            iv_recover.alpha = 0.6f
        }
        lyt_recover.isEnabled = enable
    }

    /**
     * 底部导航栏绑定监听事件，处理RecycleView等相关布局变更
     */
    private fun bindBottomRadio() {
        check_group.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.checkbox_graphic -> {
                    ryt_action.visibility = View.VISIBLE
                    ryt_background.visibility = View.GONE
                    changeBottomLayoutAnimator(true)
                }
                R.id.checkbox_background -> {
                    ryt_action.visibility = View.GONE
                    ryt_background.visibility = View.VISIBLE
                    changeBottomLayoutAnimator(true)
                }
                View.NO_ID -> {
                    changeBottomLayoutAnimator(false)
                }

            }
        }
    }

    /**
     * 底部动画处理
     * @param isOpen Boolean
     */
    private fun changeBottomLayoutAnimator(isOpen: Boolean) {

        if (isBottomShow == isOpen) {
            return
        }
        val start = if (isOpen) resources.getDimension(R.dimen.x1).toInt() else resources.getDimension(R.dimen.x269).toInt()
        val end = if (isOpen) resources.getDimension(R.dimen.x269).toInt() else resources.getDimension(R.dimen.x1).toInt()

        if (bottomLayoutAnimator != null && bottomLayoutAnimator!!.isRunning) {
            bottomLayoutAnimator!!.end()
        }
        bottomLayoutAnimator = ValueAnimator.ofInt(start, end).setDuration(150)
        bottomLayoutAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val params = lyt_bottom_view.layoutParams as LinearLayout.LayoutParams
            params.height = height
            lyt_bottom_view.layoutParams = params
            if (onBottomAnimatorChangeListener != null) {
                val showRate = 1.0f * (height - start) / (end - start)
                onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (!isOpen) 1 - showRate else showRate)
            }
        }
        bottomLayoutAnimator!!.start()
        isBottomShow = isOpen

    }

    //endregion


    fun setPalettePickColor(color: Int) {
        val transparent = color == Color.TRANSPARENT
        if (transparent) {
            iv_palette_pick.setDrawType(RingCircleView.TYPE_PICK_TRANSPARENT)
        } else {
            iv_palette_pick.setDrawType(RingCircleView.TYPE_PICK_COLOR)
            iv_palette_pick.setFillColor(color)
        }
    }

    fun setPaletteColorPicked(r: Int, g: Int, b: Int) {
        mDataFactory.onColorRGBChanged(doubleArrayOf(r.toDouble(), g.toDouble(), b.toDouble()))
    }


}