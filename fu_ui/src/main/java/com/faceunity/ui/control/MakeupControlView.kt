package com.faceunity.ui.control

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.circle.CircleFilledColor
import com.faceunity.ui.circle.ColorfulCircleView
import com.faceunity.ui.dialog.ToastHelper
import com.faceunity.ui.entity.MakeupCombinationBean
import com.faceunity.ui.entity.MakeupCustomBean
import com.faceunity.ui.entity.MakeupCustomClassBean
import com.faceunity.ui.infe.AbstractMakeupDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import com.wuyr.pathlayoutmanager.PathLayoutManager
import kotlinx.android.synthetic.main.layout_make_up_control.view.*
import kotlin.math.abs


/**
 *
 * DESC：美妆
 * Created on 2020/12/9
 *
 */
class MakeupControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    BaseControlView(mContext, attrs, defStyleAttr) {


    private lateinit var mDataFactory: AbstractMakeupDataFactory

    /*  组合妆容 */
    private lateinit var mCombinationAdapter: BaseListAdapter<MakeupCombinationBean>

    /*  自定义妆容类别 */
    private lateinit var mCustomClassAdapter: BaseListAdapter<MakeupCustomClassBean>
    private var mCustomClassIndex: Int = 0
    private lateinit var mCurrentCustomClassKey: String// 当前组合妆容选中类别

    /* 自定义妆容单项 */
    private lateinit var mCustomAdapter: BaseListAdapter<MakeupCustomBean>

    /* 自定义妆容单项-颜色 */
    private lateinit var mCustomColorAdapter: BaseListAdapter<DoubleArray>

    private var needUpdateView = true

    // region  init
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_make_up_control, this)
        initView()
        initAdapter()
        bindListener()
    }


    /**
     * 给控制绑定FaceBeautyController，MakeupController 数据工厂
     * @param dataFactory IFaceBeautyDataFactory
     */
    fun bindDataFactory(dataFactory: AbstractMakeupDataFactory) {
        mDataFactory = dataFactory
        mCustomClassAdapter.setData(dataFactory.makeupCustomClass)
        mCombinationAdapter.setData(dataFactory.makeupCombinations)
        showCombinationSeekBar(mCombinationAdapter.getData(dataFactory.currentCombinationIndex))
        mCurrentCustomClassKey = dataFactory.makeupCustomClass[mCustomClassIndex].key
    }


    /**
     *  View初始化
     */
    private fun initView() {
        initHorizontalRecycleView(recycler_combination)//组合妆容
        initHorizontalRecycleView(recycler_custom_class)//自定义妆容类别
        initHorizontalRecycleView(recycler_custom)//自定义妆容项目
        initColorRecycleView()//颜色
    }


    /**
     * 构造Adapter
     */
    private fun initAdapter() {
        initCombinationAdapter()
        initCustomClassAdapter()
        initCustomBeanAdapter()
        initCustomColorAdapter()
    }


    /**
     * 绑定监听事件
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        /*拦截触碰事件*/
        cyt_combination_makeup.setOnTouchListener { _, _ -> true }
        /*拦截触碰事件*/
        cyt_custom_makeup.setOnTouchListener { _, _ -> true }
        /*滑动条控制*/
        bindSeekBarListener()
        /*开启自定义美妆*/
        iv_combination_makeup.setOnClickListener {
            mDataFactory.enterCustomMakeup()
            if (needUpdateView) {
                mCustomClassIndex = 0
                mCurrentCustomClassKey = mDataFactory.makeupCustomClass[mCustomClassIndex].key
                val beans = mDataFactory.makeupCustomItemParams[mCurrentCustomClassKey]!!
                val current = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
                mCustomClassAdapter.notifyDataSetChanged()
                recycler_custom_class.scrollToPosition(mCustomClassIndex)
                mCustomAdapter.setData(beans)
                recycler_custom.scrollToPosition(current)
                val data = beans[current]
                val doubleList = data.doubleArray
                val intensity = mDataFactory.getCurrentCustomIntensity(mCurrentCustomClassKey, current)
                showCustomSeekBar(current, intensity)
                showColorRecycleView(doubleList)
                needUpdateView = false
            } else if (mCustomColorAdapter.itemCount > 0) {
                cyt_makeup_color.visibility = View.VISIBLE
            }
            changeAdapterSelected(mCombinationAdapter, mDataFactory.currentCombinationIndex, -1)
            mDataFactory.currentCombinationIndex = -1
            seek_bar_combination.visibility = View.INVISIBLE
            openCustomBottomAnimator(true)
        }
        iv_custom_back.setOnClickListener {
            cyt_makeup_color.visibility = View.GONE
            openCustomBottomAnimator(false)
        }
    }


    private fun initColorRecycleView() {
        (recycler_makeup_color.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        val xOffset = resources.getDimensionPixelSize(R.dimen.x100);
        val path = Path()
        val xAxis = resources.getDimensionPixelSize(R.dimen.x40);
        path.moveTo(xAxis.toFloat(), 0f)
        path.lineTo(xAxis.toFloat(), resources.getDimensionPixelSize(R.dimen.x540).toFloat())
        val mPathLayoutManager = PathLayoutManager(path, xOffset, RecyclerView.VERTICAL)
        mPathLayoutManager.setScrollMode(PathLayoutManager.SCROLL_MODE_NORMAL)
        mPathLayoutManager.setItemDirectionFixed(true)
        mPathLayoutManager.setFlingEnable(true)
        mPathLayoutManager.setCacheCount(5)
        mPathLayoutManager.setAutoSelect(true)
        mPathLayoutManager.setAutoSelectFraction(0.5f)
        mPathLayoutManager.setFixingAnimationDuration(250L)
        mPathLayoutManager.setItemScaleRatio(1f, 0f, 1.5f, 0.25f, 2f, 0.5f, 1.5f, 0.75f, 1f, 1f)
        mPathLayoutManager.setOnItemSelectedListener { position ->
            val layoutManager = recycler_makeup_color.layoutManager as PathLayoutManager
            layoutManager.setFixingAnimationDuration(250L)
            mDataFactory.updateCustomColor(mCurrentCustomClassKey, position)
        }
        recycler_makeup_color.layoutManager = mPathLayoutManager
    }

    //endregion  init


    // region  Adapter
    /**
     * 组合妆容Adapter
     */
    private fun initCombinationAdapter() {
        mCombinationAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<MakeupCombinationBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: MakeupCombinationBean, position: Int) {
                helper.setText(R.id.tv_control, data.desRes)
                helper.setImageResource(R.id.iv_control, data.imageRes)
                helper.itemView.isSelected = (position == mDataFactory.currentCombinationIndex)
            }

            override fun onItemClickListener(view: View, data: MakeupCombinationBean, position: Int) {
                if (position != mDataFactory.currentCombinationIndex) {
                    needUpdateView = true
                    changeAdapterSelected(mCombinationAdapter, mDataFactory.currentCombinationIndex, position)
                    mDataFactory.currentCombinationIndex = position
                    mDataFactory.onMakeupCombinationSelected(data)
                    showCombinationSeekBar(data)

                }
            }
        }, R.layout.list_item_control_title_image_square)
        recycler_combination.adapter = mCombinationAdapter
    }

    /**
     * 自定义妆容一级类别Adapter
     */
    private fun initCustomClassAdapter() {
        mCustomClassAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<MakeupCustomClassBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, data: MakeupCustomClassBean, position: Int) {
                helper.setText(R.id.tv_control, data.nameRes)
                val current = mDataFactory.getCurrentCustomItemIndex(data.key)
                helper.setVisible(R.id.iv_indicator, current > 0)
                helper.itemView.isSelected = (position == mCustomClassIndex)
            }

            override fun onItemClickListener(view: View, data: MakeupCustomClassBean, position: Int) {
                if (mCustomClassIndex != position) {
                    changeAdapterSelected(mCustomClassAdapter, mCustomClassIndex, position)
                    mCustomClassIndex = position
                    mCurrentCustomClassKey = data.key
                    val makeupCustomBeans = mDataFactory.makeupCustomItemParams[mCurrentCustomClassKey]!!
                    mCustomAdapter.setData(makeupCustomBeans)
                    val current = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
                    val intensity = mDataFactory.getCurrentCustomIntensity(mCurrentCustomClassKey, current)
                    showCustomSeekBar(current, intensity)
                    recycler_custom.scrollToPosition(current)
                    showColorRecycleView(makeupCustomBeans[current].doubleArray)
                }
            }
        }, R.layout.list_item_control_title)
        recycler_custom_class.adapter = mCustomClassAdapter
    }

    /**
     * 自定义妆容二级项目Adapter
     */
    private fun initCustomBeanAdapter() {
        mCustomAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<MakeupCustomBean>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, item: MakeupCustomBean, position: Int) {
                val requestOptions = RequestOptions().transform(CenterCrop(), RoundedCorners(resources.getDimensionPixelSize(R.dimen.x5)))
                Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(item.drawable).into(helper.getView(R.id.iv_control)!!)
                val current = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
                helper.itemView.isSelected = position == current
            }

            override fun onItemClickListener(view: View, data: MakeupCustomBean, position: Int) {
                if (data.nameRes > 0 && position > 0) {
                    ToastHelper.showWhiteTextToast(context, data.nameRes)
                }
                val oldIndex = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
                if (position != oldIndex) {
                    changeAdapterSelected(mCustomAdapter, oldIndex, position)
                    mDataFactory.onCustomBeanSelected(mCurrentCustomClassKey, position)
                    val intensity = mDataFactory.getCurrentCustomIntensity(mCurrentCustomClassKey, position)
                    showCustomSeekBar(position, intensity)
                    showColorRecycleView(data.doubleArray)
                    mCustomClassAdapter.getViewByPosition(mCustomClassIndex)?.findViewById<View>(R.id.iv_indicator)
                        ?.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
                }
            }
        }, R.layout.list_item_control_image_square)
        recycler_custom.adapter = mCustomAdapter
    }


    private fun initCustomColorAdapter() {
        mCustomColorAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<DoubleArray>() {
            override fun convert(viewType: Int, helper: BaseViewHolder, item: DoubleArray, position: Int) {
                val view = helper.getView<ColorfulCircleView>(R.id.iv_colorful)
                val circleFillColor = view?.circleFillColor
                val count = item.size / 4
                var fillMode = CircleFilledColor.FillMode.SINGLE
                for (i in 0 until count) {
                    val color = Color.argb(
                        (item[i * 4 + 3] * 255).toInt(), (item[i * 4] * 255).toInt(),
                        (item[i * 4 + 1] * 255).toInt(), (item[i * 4 + 2] * 255).toInt()
                    )
                    if (i == 0) {
                        circleFillColor!!.fillColor1 = color
                        fillMode = CircleFilledColor.FillMode.SINGLE
                    } else if (i == 1) {
                        circleFillColor!!.fillColor2 = color
                        if (item[7] == 1.0) {
                            fillMode = CircleFilledColor.FillMode.DOUBLE
                        }
                    } else if (i == 2) {
                        circleFillColor!!.fillColor3 = color
                        if (item[7] == 1.0) {
                            fillMode = CircleFilledColor.FillMode.DOUBLE
                        }
                        if (item[11] == 1.0) {
                            fillMode = CircleFilledColor.FillMode.TRIPLE
                        }
                    } else if (i == 3) {
                        circleFillColor!!.fillColor4 = color
                        if (item[15] == 1.0) {
                            fillMode = CircleFilledColor.FillMode.QUADRUPLE
                        }
                        if (item[11] == 1.0) {
                            fillMode = CircleFilledColor.FillMode.TRIPLE
                        }
                        if (item[7] == 1.0) {
                            fillMode = CircleFilledColor.FillMode.DOUBLE
                        }
                    }
                }
                circleFillColor!!.fillMode = fillMode
                view.circleFillColor = circleFillColor
            }

            override fun onItemClickListener(view: View, data: DoubleArray, position: Int) {
                val current = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
                val oldIndex = mDataFactory.getCurrentCustomColorIndex(mCurrentCustomClassKey, current)
                if (position < 3 || position >= 8 || oldIndex == position) {
                    return
                }
                val layoutManager = recycler_makeup_color.layoutManager as PathLayoutManager
                if (abs(oldIndex - position) > 1) {
                    layoutManager.setFixingAnimationDuration(250L)
                } else {
                    layoutManager.setFixingAnimationDuration(250L / 2.toLong())
                }
                layoutManager.smoothScrollToPosition(position)
            }

        }, R.layout.list_item_control_colorful_circle)

        recycler_makeup_color.adapter = mCustomColorAdapter
    }


// region 视图控制

    /**
     *  选中组合妆容，控制强度调节器以及自定义按钮状态变更
     */
    private fun showCombinationSeekBar(data: MakeupCombinationBean) {
        seek_bar_combination.visibility = if (data.type == MakeupCombinationBean.TypeEnum.TYPE_NONE) View.INVISIBLE else View.VISIBLE
        seek_bar_combination.progress = (data.intensity * 100).toInt()
        setCustomEnable(data.type == MakeupCombinationBean.TypeEnum.TYPE_DAILY || data.type == MakeupCombinationBean.TypeEnum.TYPE_NONE)
    }

    /**
     *  自定义按钮是否可点击，暂不支持主题妆
     */
    private fun setCustomEnable(enable: Boolean) {
        iv_combination_makeup.isEnabled = enable
        val alpha = if (enable) 1.0f else 0.6f
        tv_combination_makeup.alpha = alpha
        iv_combination_makeup.alpha = alpha
    }

    /**
     * 自定义妆容-变更调节器状态以及数值
     */
    private fun showCustomSeekBar(current: Int, intensity: Double) {
        seek_bar_custom.visibility = if (current == 0) View.INVISIBLE else View.VISIBLE
        seek_bar_custom.progress = (intensity * 100).toInt()
    }


    /**
     * 自定义妆容-控制颜色选择器状态以及默认值
     */
    private fun showColorRecycleView(doubleList: ArrayList<DoubleArray>?) {
        if (doubleList.isNullOrEmpty()) {
            mCustomColorAdapter.setData(ArrayList())
            cyt_makeup_color.visibility = View.GONE
        } else {
            mCustomColorAdapter.setData(doubleList)
            cyt_makeup_color.visibility = View.VISIBLE
            val current = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
            val colorIndex = mDataFactory.getCurrentCustomColorIndex(mCurrentCustomClassKey, current)
            (recycler_makeup_color.layoutManager as PathLayoutManager).scrollToPosition(colorIndex)
        }
    }

    private fun bindSeekBarListener() {
        /*组合妆容强度变更回调*/
        seek_bar_combination.setOnProgressChangeListener(object : DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0f * (value - seekBar!!.min) / 100
                val combination = mCombinationAdapter.getData(mDataFactory.currentCombinationIndex)
                if (!DecimalUtils.doubleEquals(valueF.toDouble(), combination.intensity)) {
                    combination.intensity = valueF.toDouble()
                    combination.filterIntensity = valueF.toDouble()
                    mDataFactory.updateCombinationIntensity(valueF.toDouble())
                }
            }
        })


        /**
         * 自定义妆容子项强度变更回调
         */
        seek_bar_custom.setOnProgressChangeListener(object : DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0f * (value - seekBar!!.min) / 100
                val current = mDataFactory.getCurrentCustomItemIndex(mCurrentCustomClassKey)
                val intensity = mDataFactory.getCurrentCustomIntensity(mCurrentCustomClassKey, current)
                if (!DecimalUtils.doubleEquals(intensity, valueF.toDouble())) {
                    mDataFactory.updateCustomItemIntensity(mCurrentCustomClassKey, current, valueF.toDouble())
                }
            }
        })
    }


    // endregion

// region 菜单切换动画效果

    private var combinationAnimator1: ValueAnimator? = null
    private var combinationAnimator2: ValueAnimator? = null

    /**
     * 菜单切换动画，先收起后弹出
     * @param isOpenCustom Boolean
     */
    private fun openCustomBottomAnimator(isOpenCustom: Boolean) {
        if (combinationAnimator1 != null && combinationAnimator1!!.isRunning) {
            combinationAnimator1!!.cancel()
        }
        if (combinationAnimator2 != null && combinationAnimator2!!.isRunning) {
            combinationAnimator2!!.cancel()
        }
        val start = if (isOpenCustom) resources.getDimensionPixelSize(R.dimen.x290) else resources.getDimensionPixelSize(R.dimen.x366)
        val mid = 1
        val end = if (isOpenCustom) resources.getDimensionPixelSize(R.dimen.x366) else resources.getDimensionPixelSize(R.dimen.x290)
        combinationAnimator1 = ValueAnimator.ofInt(start, mid)
        combinationAnimator1!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val view = if (isOpenCustom) cyt_combination_makeup else cyt_custom_makeup
            val params = view.layoutParams as LayoutParams
            params.height = height
            view.layoutParams = params
            val showRate = 0.5f * (height - start) / (mid - start)
            onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (isOpenCustom) showRate else 1 - showRate)

        }
        combinationAnimator1!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                combinationAnimator2!!.start()
            }
        })
        combinationAnimator1!!.duration = 150
        combinationAnimator2 = ValueAnimator.ofInt(mid, end)
        combinationAnimator2!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val view = if (!isOpenCustom) cyt_combination_makeup else cyt_custom_makeup
            val params = view.layoutParams as LayoutParams
            params.height = height
            view.layoutParams = params
            val showRate = 0.5f * (height - mid) / (end - mid) + 0.5f
            onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (isOpenCustom) showRate else 1 - showRate)
        }
        combinationAnimator2!!.duration = 150
        combinationAnimator1!!.start()
    }
// endregion
}