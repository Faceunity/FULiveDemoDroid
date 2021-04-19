package com.faceunity.ui.control

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.dialog.ToastHelper
import com.faceunity.ui.entity.FaceBeautyBean
import com.faceunity.ui.entity.FaceBeautyFilterBean
import com.faceunity.ui.entity.FaceBeautyStyleBean
import com.faceunity.ui.entity.ModelAttributeData
import com.faceunity.ui.infe.AbstractFaceBeautyDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlinx.android.synthetic.main.layout_face_beauty_control.view.*


/**
 *
 * DESC：美颜
 * Created on 2020/11/17
 *
 */
class FaceBeautyControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractFaceBeautyDataFactory

    /*  美颜、美型 */
    private lateinit var mModelAttributeRange: HashMap<String, ModelAttributeData>
    private var mSkinBeauty = ArrayList<FaceBeautyBean>()
    private var mShapeBeauty = ArrayList<FaceBeautyBean>()
    private var mSkinIndex = 0
    private var mShapeIndex = 1
    private lateinit var mBeautyAdapter: BaseListAdapter<FaceBeautyBean>

    /* 滤镜 */
    private var mFilters = ArrayList<FaceBeautyFilterBean>()
    private lateinit var mFiltersAdapter: BaseListAdapter<FaceBeautyFilterBean>

    /* 风格 */
    private var mStyles = ArrayList<FaceBeautyStyleBean>()
    private lateinit var mStylesAdapter: BaseListAdapter<FaceBeautyStyleBean>
    private var mEnableBottomRationClick = true
    // region  init

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_face_beauty_control, this)
        initView()
        initAdapter()
        bindListener()
    }


    /**
     * 给控制绑定FaceBeautyController，数据工厂
     * @param dataFactory IFaceBeautyDataFactory
     */
    fun bindDataFactory(dataFactory: AbstractFaceBeautyDataFactory) {
        mDataFactory = dataFactory
        mModelAttributeRange = dataFactory.modelAttributeRange
        mSkinBeauty = dataFactory.skinBeauty
        mShapeBeauty = dataFactory.shapeBeauty
        mFilters = dataFactory.beautyFilters
        mStyles = dataFactory.beautyStyles
        mFiltersAdapter.setData(mFilters)
        mStylesAdapter.setData(mStyles)
        if (dataFactory.currentStyleIndex > -1) {
            lyt_style_recover.isSelected = false
            setBottomCheckRatioEnable(false)
        } else {
            lyt_style_recover.isSelected = true
            setBottomCheckRatioEnable(true)
        }
        beauty_radio_group.check(View.NO_ID)
    }

    /**
     * 收回菜单栏
     */
    fun hideControlView() {
        beauty_radio_group.check(View.NO_ID)
    }


    /**
     *  View初始化
     */
    private fun initView() {
        initHorizontalRecycleView(recycler_view)
    }


    /**
     *  构造Adapter
     */
    private fun initAdapter() {
        mStylesAdapter = BaseListAdapter(
            ArrayList(), object : BaseDelegate<FaceBeautyStyleBean>() {
                override fun convert(viewType: Int, helper: BaseViewHolder, data: FaceBeautyStyleBean, position: Int) {
                    helper.setText(R.id.tv_control, data.desRes)
                    helper.setImageResource(R.id.iv_control, data.imageRes)
                    helper.itemView.isSelected = mDataFactory.currentStyleIndex == position
                }

                override fun onItemClickListener(view: View, data: FaceBeautyStyleBean, position: Int) {
                    super.onItemClickListener(view, data, position)
                    if (mDataFactory.currentStyleIndex != position) {
                        lyt_style_recover.isSelected = false
                        setBottomCheckRatioEnable(false)
                        changeAdapterSelected(mStylesAdapter, mDataFactory.currentStyleIndex, position)
                        mDataFactory.currentStyleIndex = position
                        mDataFactory.onStyleSelected(data.key)
                    }
                }
            }, R.layout.list_item_control_title_image_circle
        )



        mFiltersAdapter = BaseListAdapter(
            ArrayList(), object : BaseDelegate<FaceBeautyFilterBean>() {
                override fun convert(viewType: Int, helper: BaseViewHolder, data: FaceBeautyFilterBean, position: Int) {
                    helper.setText(R.id.tv_control, data.desRes)
                    helper.setImageResource(R.id.iv_control, data.imageRes)
                    helper.itemView.isSelected = mDataFactory.currentFilterIndex == position
                }

                override fun onItemClickListener(view: View, data: FaceBeautyFilterBean, position: Int) {
                    super.onItemClickListener(view, data, position)
                    if (mDataFactory.currentFilterIndex != position) {
                        changeAdapterSelected(mFiltersAdapter, mDataFactory.currentFilterIndex, position)
                        mDataFactory.currentFilterIndex = position
                        mDataFactory.onFilterSelected(data.key, data.intensity, data.desRes)
                        if (position == 0) {
                            beauty_seek_bar.visibility = View.INVISIBLE
                        } else {
                            seekToSeekBar(data.intensity, 0.0, 1.0)
                        }
                    }
                }
            }, R.layout.list_item_control_title_image_square
        )



        mBeautyAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<FaceBeautyBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: FaceBeautyBean, position: Int) {
                helper.setText(R.id.tv_control, data.desRes)
                val value = mDataFactory.getParamIntensity(data.key)
                val stand = mModelAttributeRange[data.key]!!.stand
                if (DecimalUtils.doubleEquals(value, stand)) {
                    helper.setImageResource(R.id.iv_control, data.closeRes)
                } else {
                    helper.setImageResource(R.id.iv_control, data.openRes)
                }
                val isShinSelected = beauty_radio_group.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                helper.itemView.isSelected = if (isShinSelected) mSkinIndex == position else mShapeIndex == position
            }

            override fun onItemClickListener(view: View, data: FaceBeautyBean, position: Int) {
                val isShinSelected = beauty_radio_group.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                if ((isShinSelected && position == mSkinIndex) || (!isShinSelected && position == mShapeIndex)) {
                    return
                }
                if (isShinSelected) {
                    changeAdapterSelected(mBeautyAdapter, mSkinIndex, position)
                    mSkinIndex = position
                } else {
                    changeAdapterSelected(mBeautyAdapter, mShapeIndex, position)
                    mShapeIndex = position
                }
                val value = mDataFactory.getParamIntensity(data.key)
                val stand = mModelAttributeRange[data.key]!!.stand
                val maxRange = mModelAttributeRange[data.key]!!.maxRange
                seekToSeekBar(value, stand, maxRange)
            }
        }, R.layout.list_item_control_title_image_circle)


    }


    /**
     * 绑定监听事件
     */
    private fun bindListener() {
        /*拦截触碰事件*/
        fyt_bottom_view.setOnTouchListener { _, _ -> true }
        /*菜单控制*/
        bindBottomRadioListener()
        /*滑动条控制*/
        bindSeekBarListener()
        /*比对开关*/
        iv_compare.setOnTouchStateListener { v, event ->
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                v.alpha = 0.7f
                mDataFactory.enableFaceBeauty(false)
            } else if (action == MotionEvent.ACTION_UP) {
                v.alpha = 1f
                mDataFactory.enableFaceBeauty(true)
            }
            true
        }
        /*还原数据*/
        lyt_beauty_recover.setOnClickListener {
            showDialog(mContext.getString(R.string.dialog_reset_avatar_model)) { recoverData() }

        }
        lyt_style_recover.setOnClickListener {
            if (it.isSelected) {
                return@setOnClickListener
            }
            changeAdapterSelected(mStylesAdapter, mDataFactory.currentStyleIndex, -1)
            mDataFactory.currentStyleIndex = -1
            it.isSelected = true
            setBottomCheckRatioEnable(true)
            mDataFactory.onStyleSelected(null)
        }
    }


    /**
     * 滚动条绑定事件
     */
    private fun bindSeekBarListener() {
        beauty_seek_bar.setOnProgressChangeListener(object : DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0 * (value - seekBar!!.min) / 100
                when (beauty_radio_group.checkedCheckBoxId) {
                    R.id.beauty_radio_skin_beauty -> {
                        val bean = mSkinBeauty[mSkinIndex]
                        val range = mModelAttributeRange[bean.key]!!.maxRange
                        val res = valueF * range
                        val value = mDataFactory.getParamIntensity(bean.key)
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateParamIntensity(bean.key, res)
                            setRecoverFaceSkinEnable(checkFaceSkinChanged())
                            updateBeautyItemUI(mBeautyAdapter.getViewHolderByPosition(mSkinIndex), bean)
                        }
                    }
                    R.id.beauty_radio_face_shape -> {
                        val bean = mShapeBeauty[mShapeIndex]
                        val range = mModelAttributeRange[bean.key]!!.maxRange
                        val res = valueF * range
                        val value = mDataFactory.getParamIntensity(bean.key)
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateParamIntensity(bean.key, res)
                            setRecoverFaceSkinEnable(checkFaceShapeChanged())
                            updateBeautyItemUI(mBeautyAdapter.getViewHolderByPosition(mShapeIndex), bean)
                        }
                    }
                    R.id.beauty_radio_filter -> {
                        val bean = mFilters[mDataFactory.currentFilterIndex]
                        if (!DecimalUtils.doubleEquals(bean.intensity, valueF)) {
                            bean.intensity = valueF
                            mDataFactory.updateFilterIntensity(valueF)
                        }
                    }
                }
            }
        })
    }


    /**
     * 底部导航栏绑定监听事件，处理RecycleView等相关布局变更
     */
    private fun bindBottomRadioListener() {

        beauty_radio_group.setOnDispatchActionUpListener { x ->
            if (!mEnableBottomRationClick) {
                val width = beauty_radio_group.measuredWidth
                when {
                    x < width * 0.25 -> {
                        val toastStr = mContext.getString(R.string.beauty_face_style_toast, mContext.getString(R.string.beauty_radio_skin_beauty))
                        ToastHelper.showNormalToast(mContext, toastStr)
                    }
                    x < width * 0.5 -> {
                        val toastStr = mContext.getString(R.string.beauty_face_style_toast, mContext.getString(R.string.beauty_radio_face_shape))
                        ToastHelper.showNormalToast(mContext, toastStr)
                    }
                    x < width * 0.75f -> {
                        val toastStr = mContext.getString(R.string.beauty_face_style_toast, mContext.getString(R.string.beauty_radio_filter))
                        ToastHelper.showNormalToast(mContext, toastStr)
                    }
                }
            }
        }



        beauty_radio_group.setOnCheckedChangeListener { _, checkedId ->

            //视图变化
            when (checkedId) {
                R.id.beauty_radio_skin_beauty, R.id.beauty_radio_face_shape -> {
                    iv_compare.visibility = View.VISIBLE
                    beauty_seek_bar.visibility = View.VISIBLE
                    lyt_style_recover.visibility = View.GONE
                    lyt_beauty_recover.visibility = View.VISIBLE
                    iv_line.visibility = View.VISIBLE
                }
                R.id.beauty_radio_filter -> {
                    iv_compare.visibility = View.VISIBLE
                    beauty_seek_bar.visibility = if (mDataFactory.currentFilterIndex == 0) View.INVISIBLE else View.VISIBLE
                    lyt_style_recover.visibility = View.GONE
                    lyt_beauty_recover.visibility = View.GONE
                    iv_line.visibility = View.GONE
                }
                R.id.beauty_radio_style -> {
                    iv_compare.visibility = View.VISIBLE
                    beauty_seek_bar.visibility = View.INVISIBLE
                    lyt_beauty_recover.visibility = View.GONE
                    lyt_style_recover.visibility = View.VISIBLE
                    iv_line.visibility = View.VISIBLE
                }
                View.NO_ID -> {
                    iv_compare.visibility = View.INVISIBLE
                    mDataFactory.enableFaceBeauty(true)
                }

            }

            //数据变化
            when (checkedId) {
                R.id.beauty_radio_skin_beauty -> {
                    mBeautyAdapter.setData(mSkinBeauty)
                    recycler_view.adapter = mBeautyAdapter
                    val item = mSkinBeauty[mSkinIndex]
                    val value = mDataFactory.getParamIntensity(item.key)
                    val stand = mModelAttributeRange[item.key]!!.stand
                    val maxRange = mModelAttributeRange[item.key]!!.maxRange
                    seekToSeekBar(value, stand, maxRange)
                    setRecoverFaceSkinEnable(checkFaceSkinChanged())
                    changeBottomLayoutAnimator(true)
                }
                R.id.beauty_radio_face_shape -> {
                    mBeautyAdapter.setData(mShapeBeauty)
                    recycler_view.adapter = mBeautyAdapter
                    val item = mShapeBeauty[mShapeIndex]
                    val value = mDataFactory.getParamIntensity(item.key)
                    val stand = mModelAttributeRange[item.key]!!.stand
                    val maxRange = mModelAttributeRange[item.key]!!.maxRange
                    seekToSeekBar(value, stand, maxRange)
                    setRecoverFaceSkinEnable(checkFaceShapeChanged())
                    changeBottomLayoutAnimator(true)
                }
                R.id.beauty_radio_filter -> {
                    recycler_view.adapter = mFiltersAdapter
                    recycler_view.scrollToPosition(mDataFactory.currentFilterIndex)
                    if (mDataFactory.currentFilterIndex == 0) {
                        beauty_seek_bar.visibility = View.INVISIBLE
                    } else {
                        seekToSeekBar(mFilters[mDataFactory.currentFilterIndex].intensity, 0.0, 1.0)
                    }
                    changeBottomLayoutAnimator(true)
                }
                R.id.beauty_radio_style -> {
                    recycler_view.adapter = mStylesAdapter
                    if (mDataFactory.currentStyleIndex > -1) {
                        recycler_view.scrollToPosition(mDataFactory.currentStyleIndex)
                        lyt_style_recover.isSelected = false
                    } else {
                        lyt_style_recover.isSelected = true
                    }
                    changeBottomLayoutAnimator(true)
                }
                View.NO_ID -> {
                    changeBottomLayoutAnimator(false)
                    iv_compare.visibility = View.INVISIBLE
                    mDataFactory.enableFaceBeauty(true)
                }

            }
        }
    }


    private fun setBottomCheckRatioEnable(enable: Boolean) {
        mEnableBottomRationClick = enable
        var i = 0
        val childCount: Int = beauty_radio_group.getChildCount()
        while (i < childCount) {
            val view: View = beauty_radio_group.getChildAt(i)
            if (view.id != R.id.beauty_radio_style) {
                view.alpha = if (enable) 1f else 0.6f
                view.isEnabled = enable
            }
            i++
        }
    }

    // endregion
    // region  业务处理


    /**
     * 设置滚动条数值
     * @param value Double 结果值
     * @param stand Double 标准值
     * @param range Double 范围区间
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

    /**
     * 更新单项是否为基准值显示
     */
    private fun updateBeautyItemUI(viewHolder: BaseViewHolder?, item: FaceBeautyBean) {
        val value = mDataFactory.getParamIntensity(item.key)
        val stand = mModelAttributeRange[item.key]!!.stand
        if (DecimalUtils.doubleEquals(value, stand)) {
            viewHolder?.setImageResource(R.id.iv_control, item.closeRes)
        } else {
            viewHolder?.setImageResource(R.id.iv_control, item.openRes)
        }
    }

    /**
     * 重置还原按钮状态
     */
    private fun setRecoverFaceSkinEnable(enable: Boolean) {
        if (enable) {
            tv_beauty_recover.alpha = 1f
            iv_beauty_recover.alpha = 1f
        } else {
            tv_beauty_recover.alpha = 0.6f
            iv_beauty_recover.alpha = 0.6f
        }
        lyt_beauty_recover.isEnabled = enable
    }


    /**
     * 遍历美肤数据确认还原按钮是否可以点击
     */
    private fun checkFaceSkinChanged(): Boolean {
        val item = mSkinBeauty[mSkinIndex]
        var value = mDataFactory.getParamIntensity(item.key)
        var default = mModelAttributeRange[item.key]!!.default
        if (!DecimalUtils.doubleEquals(value, default)) {
            return true
        }
        mSkinBeauty.forEach {
            value = mDataFactory.getParamIntensity(it.key)
            default = mModelAttributeRange[it.key]!!.default
            if (!DecimalUtils.doubleEquals(value, default)) {
                return true
            }
        }
        return false
    }

    /**
     * 遍历美型数据确认还原按钮是否可以点击
     */
    private fun checkFaceShapeChanged(): Boolean {
        var item = mShapeBeauty[mShapeIndex]
        var value = mDataFactory.getParamIntensity(item.key)
        var default = mModelAttributeRange[item.key]!!.default
        if (!DecimalUtils.doubleEquals(value, default)) {
            return true
        }
        mShapeBeauty.forEach {
            value = mDataFactory.getParamIntensity(it.key)
            default = mModelAttributeRange[it.key]!!.default
            if (!DecimalUtils.doubleEquals(value, default)) {
                return true
            }
        }
        return false
    }

    /**
     * 还原 美型、美肤数据
     */
    private fun recoverData() {
        when (beauty_radio_group.checkedCheckBoxId) {
            R.id.beauty_radio_skin_beauty -> {
                mSkinBeauty.forEach {
                    val default = mModelAttributeRange[it.key]!!.default
                    mDataFactory.updateParamIntensity(it.key, default)
                }
                val item = mSkinBeauty[mSkinIndex]
                val value = mDataFactory.getParamIntensity(item.key)
                val stand = mModelAttributeRange[item.key]!!.stand
                val maxRange = mModelAttributeRange[item.key]!!.maxRange
                seekToSeekBar(value, stand, maxRange)
                mBeautyAdapter.notifyDataSetChanged()
                setRecoverFaceSkinEnable(false)
            }
            R.id.beauty_radio_face_shape -> {
                mShapeBeauty.forEach {
                    val default = mModelAttributeRange[it.key]!!.default
                    mDataFactory.updateParamIntensity(it.key, default)
                }
                val item = mShapeBeauty[mShapeIndex]
                val value = mDataFactory.getParamIntensity(item.key)
                val stand = mModelAttributeRange[item.key]!!.stand
                val maxRange = mModelAttributeRange[item.key]!!.maxRange
                seekToSeekBar(value, stand, maxRange)
                mBeautyAdapter.notifyDataSetChanged()
                setRecoverFaceSkinEnable(false)
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
        val start = if (isOpen) resources.getDimension(R.dimen.x1).toInt() else resources.getDimension(R.dimen.x268).toInt()
        val end = if (isOpen) resources.getDimension(R.dimen.x268).toInt() else resources.getDimension(R.dimen.x1).toInt()

        if (bottomLayoutAnimator != null && bottomLayoutAnimator!!.isRunning) {
            bottomLayoutAnimator!!.end()
        }
        bottomLayoutAnimator = ValueAnimator.ofInt(start, end).setDuration(150)
        bottomLayoutAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val params = fyt_bottom_view.layoutParams as LinearLayout.LayoutParams
            params.height = height
            fyt_bottom_view.layoutParams = params
            if (onBottomAnimatorChangeListener != null) {
                val showRate = 1.0f * (height - start) / (end - start)
                onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (!isOpen) 1 - showRate else showRate)
            }
            if (DecimalUtils.floatEquals(animation.animatedFraction, 1.0f) && isOpen) {
                iv_compare.visibility = View.VISIBLE
            }
        }
        bottomLayoutAnimator!!.start()
        isBottomShow = isOpen

    }


}
