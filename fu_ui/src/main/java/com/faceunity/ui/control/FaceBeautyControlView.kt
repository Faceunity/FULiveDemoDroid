package com.faceunity.ui.control

import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.databinding.LayoutFaceBeautyControlBinding
import com.faceunity.ui.dialog.ToastHelper
import com.faceunity.ui.entity.FaceBeautyBean
import com.faceunity.ui.entity.FaceBeautyFilterBean
import com.faceunity.ui.entity.ModelAttributeData
import com.faceunity.ui.entity.uistate.FaceBeautyControlState
import com.faceunity.ui.infe.AbstractFaceBeautyDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlin.collections.set


/**
 *
 * DESC：美颜
 * Created on 2020/11/17
 *
 */
class FaceBeautyControlView @JvmOverloads constructor(
    private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : BaseControlView(mContext, attrs, defStyleAttr) {

    private lateinit var mDataFactory: AbstractFaceBeautyDataFactory

    /*  美颜、美型 */
    private lateinit var mModelAttributeRange: HashMap<String, ModelAttributeData>
    private var mSkinBeauty = ArrayList<FaceBeautyBean>()
    private var mShapeBeauty = ArrayList<FaceBeautyBean>()
    private var mShapeBeautySubItem = ArrayList<FaceBeautyBean>()
    private var mSubItemUIValueCache: HashMap<String, Double> = HashMap()//美型子项脸型的UI缓存值
    private var mSkinIndex = -1
    private var mShapeIndex = -1
    private var mIsOnBeautyShapeMain = true//美型是否在主项上

    private lateinit var mBeautyAdapter: BaseListAdapter<FaceBeautyBean>

    /* 滤镜 */
    private var mFilters = ArrayList<FaceBeautyFilterBean>()
    private lateinit var mFiltersAdapter: BaseListAdapter<FaceBeautyFilterBean>
    private val mBinding: LayoutFaceBeautyControlBinding by lazy {
        LayoutFaceBeautyControlBinding.inflate(LayoutInflater.from(context), this, true)
    }
    // region  init

    init {
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
        mShapeBeautySubItem = dataFactory.shapeBeautySubItem
        mFilters = dataFactory.beautyFilters
        mFiltersAdapter.setData(mFilters)
        hideControlView()

        //恢复上一次脸型选项的UI值
        mSubItemUIValueCache = mDataFactory.getCurrentFaceShapeUIValue()
    }

    /**
     * 收回菜单栏
     */
    fun hideControlView() {
        mBinding.beautyRadioGroup.check(View.NO_ID)
    }


    /**
     *  View初始化
     */
    private fun initView() {
        initHorizontalRecycleView(mBinding.recyclerView)
    }


    /**
     *  构造Adapter
     */
    private fun initAdapter() {
        mFiltersAdapter = BaseListAdapter(
            ArrayList(), object : BaseDelegate<FaceBeautyFilterBean>() {
                override fun convert(
                    viewType: Int, helper: BaseViewHolder, data: FaceBeautyFilterBean, position: Int
                ) {
                    helper.setText(R.id.tv_control, data.desRes)
                    helper.setImageResource(R.id.iv_control, data.imageRes)
                    helper.itemView.isSelected = mDataFactory.currentFilterIndex == position
                }

                override fun onItemClickListener(
                    view: View, data: FaceBeautyFilterBean, position: Int
                ) {
                    super.onItemClickListener(view, data, position)
                    if (mDataFactory.currentFilterIndex != position) {
                        changeAdapterSelected(
                            mFiltersAdapter, mDataFactory.currentFilterIndex, position
                        )
                        mDataFactory.currentFilterIndex = position
                        mDataFactory.onFilterSelected(data.key, data.intensity, data.desRes)
                        if (position == 0) {
                            mBinding.beautySeekBar.visibility = View.INVISIBLE
                        } else {
                            seekToSeekBar(data.intensity, 0.0, 1.0)
                        }
                    }
                }
            }, R.layout.list_item_control_title_image_square
        )

        mBeautyAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<FaceBeautyBean>() {
            override fun convert(
                viewType: Int, helper: BaseViewHolder, data: FaceBeautyBean, position: Int
            ) {
                val isShinSelected =
                    mBinding.beautyRadioGroup.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                helper.itemView.isSelected =
                    if (isShinSelected) mSkinIndex == position else mShapeIndex == position
                helper.setText(R.id.tv_control, data.desRes)
                //主项的时候才需要显示
//                helper.setVisible(R.id.iv_oval_spot, !isShinSelected && mIsOnBeautyShapeMain && position ==0)
                val value = mDataFactory.getParamIntensity(data.key)
                when (data.buttonType) {
                    FaceBeautyBean.ButtonType.BACK_BUTTON -> {
                        helper.setImageResource(R.id.iv_control, data.closeRes)
                    }

                    FaceBeautyBean.ButtonType.SUB_ITEM_BUTTON -> {
                        //判断当前UI值 和 真实值是否全是空的如果是空的则不选中
                        //先过UI值
                        var choose = false
                        run outside@{
                            mSubItemUIValueCache.forEach {
                                if (it.value > 0) {
                                    choose = true
                                }
                            }

                            mShapeBeautySubItem.forEach {
                                if (it.buttonType == FaceBeautyBean.ButtonType.NORMAL_BUTTON) {
                                    if (mDataFactory.getParamIntensity(it.key) > 0) {
                                        choose = true
                                    }
                                }
                            }
                        }

                        helper.setImageResource(
                            R.id.iv_control, if (choose) data.openRes else data.closeRes
                        )
                    }
                    //普通按钮
                    else -> {
                        val stand = mModelAttributeRange[data.key]!!.stand
                        if (DecimalUtils.doubleEquals(value, stand)) {
                            helper.setImageResource(R.id.iv_control, data.closeRes)
                        } else {
                            helper.setImageResource(R.id.iv_control, data.openRes)
                        }
                        if (!data.canUseFunction) {
                            val ivControl = helper.getView<ImageView>(R.id.iv_control)
                            ivControl?.imageAlpha = 154
                        } else {
                            val ivControl = helper.getView<ImageView>(R.id.iv_control)
                            ivControl?.imageAlpha = 255
                        }

                        if (openEnterAnimation && needEnterAnimation && position != 0) {
                            enterAnimation(helper.itemView)
                            if (position >= 4 || (mBeautyAdapter.itemCount < 5 && position == mBeautyAdapter.itemCount - 1)) {
                                needEnterAnimation = false
                            }
                        }
                    }
                }
            }

            override fun onItemClickListener(view: View, data: FaceBeautyBean, position: Int) {
                val isShinSelected =
                    mBinding.beautyRadioGroup.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                if ((isShinSelected && position == mSkinIndex) || (!isShinSelected && position == mShapeIndex)) {
                    return
                }
                if (!data.canUseFunction) {
                    ToastHelper.showNormalToast(
                        mContext, mContext.getString(
                            R.string.face_beauty_function_tips, mContext.getString(data.desRes)
                        )
                    )
                    return
                }
                checkRelevanceRadioGroup(data)
                if (isShinSelected) {
                    changeAdapterSelected(mBeautyAdapter, mSkinIndex, position)
                    val value = mDataFactory.getParamIntensity(data.key)
                    val stand = mModelAttributeRange[data.key]!!.stand
                    val maxRange = mModelAttributeRange[data.key]!!.maxRange
                    seekToSeekBar(value, stand, maxRange)
                    mSkinIndex = position
                } else {
                    when (data.buttonType) {
                        FaceBeautyBean.ButtonType.BACK_BUTTON -> {
                            //返回按钮
                            mBeautyAdapter.setData(mShapeBeauty)
                            //没有按钮被选中，需要隐藏进度条
                            mBinding.beautySeekBar.visibility = View.INVISIBLE
                            changeAdapterSelected(mBeautyAdapter, mShapeIndex, -1)
                            mShapeIndex = -1
                            mIsOnBeautyShapeMain = true
                            needEnterAnimation = true
                        }

                        FaceBeautyBean.ButtonType.SUB_ITEM_BUTTON -> {
                            //子项按钮
                            //选中的是哪一个子项 -> 在FaceBeauty中有记录
                            run outside@{
                                mShapeBeautySubItem.forEachIndexed { index, value ->
                                    if (mDataFactory.getCurrentOneHotFaceShape() == value.key) {//获取当前作用的脸型
                                        mShapeIndex = index
                                        return@outside
                                    }
                                }
                            }

                            mBeautyAdapter.setData(mShapeBeautySubItem)
                            if (mShapeBeautySubItem.size >= 1) {
                                mBinding.beautySeekBar.visibility = View.VISIBLE
                                val faceBeautyBean = mShapeBeautySubItem[mShapeIndex]
                                if (faceBeautyBean.buttonType == FaceBeautyBean.ButtonType.NORMAL_BUTTON) {
                                    val value = mDataFactory.getParamIntensity(faceBeautyBean.key)
                                    val stand = mModelAttributeRange[faceBeautyBean.key]!!.stand
                                    val maxRange =
                                        mModelAttributeRange[faceBeautyBean.key]!!.maxRange
                                    seekToSeekBar(value, stand, maxRange)
                                }
                            }
                            mIsOnBeautyShapeMain = false
                            needEnterAnimation = true
                        }
                        //普通按钮
                        else -> {
                            //点击的是子项普通按钮还是主项普通按钮1.子项普通之间按钮效果One Hot 2.主项普通按钮之间效果可叠加
                            mBinding.beautySeekBar.visibility = View.VISIBLE
                            changeAdapterSelected(mBeautyAdapter, mShapeIndex, position)
                            //判断一下是子项还是主项，1.主项直接设置改点击对象的值，2.子项需要获取之前的缓存如果存在缓存设置回之前的值，然后还要将上一个点击过的子项sdk值设置为零，UI值进行缓存
                            var value: Double
                            if (mIsOnBeautyShapeMain) {
                                value = mDataFactory.getParamIntensity(data.key)
                            } else {
                                //子项 one hot + 如果有UI值显示UI值 -> 并将UI缓存值设置到SDK
                                value = if (mSubItemUIValueCache.contains(data.key)) {
                                    var value = mSubItemUIValueCache[data.key]!!
                                    mDataFactory.updateParamIntensity(data.key, value)
                                    mSubItemUIValueCache.remove(data.key)!!
                                } else mDataFactory.getParamIntensity(data.key)
                                //将上一个点击的有效项设置为空
                                if (mShapeIndex >= 0) {
                                    var beforeData = mBeautyAdapter.getData(mShapeIndex)
                                    when (beforeData.buttonType) {
                                        FaceBeautyBean.ButtonType.NORMAL_BUTTON -> {
                                            //算法值要设置为零，但是点击回来的时候需要根据UI值(缓存值)重新设置回算法值
                                            mSubItemUIValueCache[beforeData.key] =
                                                mDataFactory.getParamIntensity(beforeData.key)
                                            mDataFactory.updateParamIntensity(beforeData.key, 0.0)
                                        }
                                    }
                                }
                                mDataFactory.setCurrentOneHotFaceShape(data.key)
                                mBeautyAdapter.notifyItemChanged(mShapeIndex)
                                mBeautyAdapter.notifyItemChanged(position)
                            }

                            //设置当前点击bean的进度值
                            val stand = mModelAttributeRange[data.key]!!.stand
                            val maxRange = mModelAttributeRange[data.key]!!.maxRange
                            seekToSeekBar(value, stand, maxRange)
                            mShapeIndex = position
                        }
                    }
                }

            }
        }, R.layout.list_item_control_title_image_circle)
    }

    private fun checkRelevanceRadioGroup(data: FaceBeautyBean) {
        mBinding.linBeautyRelevance.visibility = if (data.showRadioButton) VISIBLE else GONE
        if (data.showRadioButton) {
            (mBinding.beautySeekBar.layoutParams as LinearLayout.LayoutParams).weight = 1.0f
            mBinding.beautySeekBar.requestLayout()
            mBinding.rbLeftBeautyRelevance.text = resources.getString(data.leftRadioButtonDesRes)
            mBinding.rbRightBeautyRelevance.text = resources.getString(data.rightRadioButtonDesRes)
            if (mDataFactory.getParamRelevanceSelectedType(data.relevanceKey) == 0) {
                mBinding.rbLeftBeautyRelevance.isSelected = true
                mBinding.rbRightBeautyRelevance.isSelected = false
            } else {
                mBinding.rbLeftBeautyRelevance.isSelected = false
                mBinding.rbRightBeautyRelevance.isSelected = true
            }
            if (!data.enableRadioButton) {
                if (mBinding.rbLeftBeautyRelevance.isSelected) {
                    mBinding.rbRightBeautyRelevance.setOnClickListener {
                        ToastHelper.showNormalToast(
                            mContext, mContext.getString(
                                R.string.face_beauty_function_tips,
                                mContext.getString(data.enableRadioButtonDesRes)
                            )
                        )
                    }
                }
                if (mBinding.rbRightBeautyRelevance.isSelected) {
                    mBinding.rbLeftBeautyRelevance.setOnClickListener {
                        ToastHelper.showNormalToast(
                            mContext, mContext.getString(
                                R.string.face_beauty_function_tips,
                                mContext.getString(data.enableRadioButtonDesRes)
                            )
                        )
                    }
                }
            } else {
                mBinding.rbLeftBeautyRelevance.setOnClickListener {
                    mDataFactory.updateParamRelevanceType(data.relevanceKey, 0)
                    setRecoverFaceSkinEnable(checkFaceSkinChanged())
                    mBinding.rbLeftBeautyRelevance.isSelected = true
                    mBinding.rbRightBeautyRelevance.isSelected = false
                }
                mBinding.rbRightBeautyRelevance.setOnClickListener {
                    mDataFactory.updateParamRelevanceType(data.relevanceKey, 1)
                    setRecoverFaceSkinEnable(checkFaceSkinChanged())
                    mBinding.rbLeftBeautyRelevance.isSelected = false
                    mBinding.rbRightBeautyRelevance.isSelected = true
                }
            }
        } else {
            (mBinding.beautySeekBar.layoutParams as LinearLayout.LayoutParams).weight = 0f
            mBinding.beautySeekBar.layoutParams.width = resources.getDimension(R.dimen.x528).toInt()
            mBinding.beautySeekBar.requestLayout()
        }

    }


    /**
     * 绑定监听事件
     */
    private fun bindListener() {
        /*拦截触碰事件*/
        mBinding.fytBottomView.setOnTouchListener { _, _ -> true }
        /*菜单控制*/
        bindBottomRadioListener()
        /*滑动条控制*/
        bindSeekBarListener()
        /*比对开关*/
        mBinding.ivCompare.setOnTouchStateListener { v, event ->
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
        mBinding.lytBeautyRecover.setOnClickListener {
            showDialog(mContext.getString(R.string.dialog_reset_avatar_model)) { recoverData() }
        }

        mBinding.ivReset.setOnClickListener {
            mDataFactory.resetParamIntensity()
            var item: FaceBeautyBean? = null
            when (mBinding.beautyRadioGroup.checkedCheckBoxId) {
                R.id.beauty_radio_skin_beauty -> {
                    mBeautyAdapter.notifyDataSetChanged()
                    if (mSkinIndex >= 0) item = mSkinBeauty[mSkinIndex]
                    setRecoverFaceSkinEnable(true)
                }

                R.id.beauty_radio_face_shape -> {
                    mBeautyAdapter.notifyDataSetChanged()
                    if (mShapeIndex >= 0) item = mShapeBeauty[mShapeIndex]
                    setRecoverFaceSkinEnable(true)
                }

                R.id.beauty_radio_filter -> {
                    mFiltersAdapter.notifyDataSetChanged()
                    mBinding.beautySeekBar.visibility = View.INVISIBLE
                }
            }
            item?.let {
                val value = mDataFactory.getParamIntensity(it.key)
                val stand = mModelAttributeRange[it.key]!!.stand
                val maxRange = mModelAttributeRange[it.key]!!.maxRange
                seekToSeekBar(value, stand, maxRange)
            }
        }
    }


    /**
     * 滚动条绑定事件
     */
    private fun bindSeekBarListener() {
        mBinding.beautySeekBar.setOnProgressChangeListener(object :
            DiscreteSeekBar.OnSimpleProgressChangeListener() {
            override fun onProgressChanged(
                seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0 * (value - seekBar!!.min) / 100
                when (mBinding.beautyRadioGroup.checkedCheckBoxId) {
                    R.id.beauty_radio_skin_beauty -> {
                        if (mSkinIndex < 0) return
                        val bean = mSkinBeauty[mSkinIndex]
                        val range = mModelAttributeRange[bean.key]!!.maxRange
                        val res = valueF * range
                        val value = mDataFactory.getParamIntensity(bean.key)
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateParamIntensity(bean.key, res)
                            setRecoverFaceSkinEnable(checkFaceSkinChanged())
                            updateBeautyItemUI(
                                mBeautyAdapter.getViewHolderByPosition(mSkinIndex), bean
                            )
                        }
                    }

                    R.id.beauty_radio_face_shape -> {
                        if (mShapeIndex < 0) return
                        val bean = mBeautyAdapter.getData(mShapeIndex)
                        when (bean.buttonType) {
                            FaceBeautyBean.ButtonType.NORMAL_BUTTON -> {
                                //判断一下这个按钮的类型
                                val range = mModelAttributeRange[bean.key]!!.maxRange
                                val res = valueF * range
                                val value = mDataFactory.getParamIntensity(bean.key)
                                if (!DecimalUtils.doubleEquals(res, value)) {
                                    mDataFactory.updateParamIntensity(bean.key, res)
                                    setRecoverFaceSkinEnable(checkFaceShapeChanged())
                                    updateBeautyItemUI(
                                        mBeautyAdapter.getViewHolderByPosition(
                                            mShapeIndex
                                        ), bean
                                    )
                                }
                            }
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
        mBinding.beautyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            //视图变化
            when (checkedId) {
                R.id.beauty_radio_skin_beauty -> {
                    showRelevanceRadioButton(false)
                    mBinding.ivCompare.visibility = View.VISIBLE
                    mBinding.beautySeekBar.visibility = View.VISIBLE
                    mBinding.lytBeautyRecover.visibility = View.VISIBLE
                    mBinding.ivLine.visibility = View.VISIBLE
                }

                R.id.beauty_radio_face_shape -> {
                    showRelevanceRadioButton(false)
                    mBinding.ivCompare.visibility = View.VISIBLE
                    mBinding.beautySeekBar.visibility = View.VISIBLE
                    mBinding.lytBeautyRecover.visibility = View.VISIBLE
                    mBinding.ivLine.visibility = View.VISIBLE
                }

                R.id.beauty_radio_filter -> {
                    showRelevanceRadioButton(false)
                    mBinding.ivCompare.visibility = View.VISIBLE
                    mBinding.beautySeekBar.visibility =
                        if (mDataFactory.currentFilterIndex == 0) View.INVISIBLE else View.VISIBLE
                    mBinding.lytBeautyRecover.visibility = View.GONE
                    mBinding.ivLine.visibility = View.GONE
                }

                View.NO_ID -> {
                    mBinding.ivCompare.visibility = View.INVISIBLE
                    mDataFactory.enableFaceBeauty(true)
                }
            }

            //数据变化
            when (checkedId) {
                R.id.beauty_radio_skin_beauty -> {
                    mBeautyAdapter.setData(mSkinBeauty)
                    mBinding.recyclerView.adapter = mBeautyAdapter
                    if (mSkinIndex >= 0) {
                        val item = mSkinBeauty[mSkinIndex]
                        val value = mDataFactory.getParamIntensity(item.key)
                        val stand = mModelAttributeRange[item.key]!!.stand
                        val maxRange = mModelAttributeRange[item.key]!!.maxRange
                        checkRelevanceRadioGroup(item)
                        seekToSeekBar(value, stand, maxRange)
                    } else {
                        mBinding.linBeautyRelevance.visibility = View.GONE
                        mBinding.beautySeekBar.visibility = View.INVISIBLE
                    }
                    setRecoverFaceSkinEnable(checkFaceSkinChanged())
                    changeBottomLayoutAnimator(true)
                }

                R.id.beauty_radio_face_shape -> {
                    mBeautyAdapter.setData(mShapeBeauty)
                    mBinding.recyclerView.adapter = mBeautyAdapter
                    if (mShapeIndex >= 0) {
                        val item = mShapeBeauty[mShapeIndex]
                        val value = mDataFactory.getParamIntensity(item.key)
                        val stand = mModelAttributeRange[item.key]!!.stand
                        val maxRange = mModelAttributeRange[item.key]!!.maxRange
                        checkRelevanceRadioGroup(item)
                        seekToSeekBar(value, stand, maxRange)
                    } else {
                        mBinding.beautySeekBar.visibility = View.INVISIBLE
                    }
                    setRecoverFaceSkinEnable(checkFaceShapeChanged())
                    changeBottomLayoutAnimator(true)
                }

                R.id.beauty_radio_filter -> {
                    mBinding.recyclerView.adapter = mFiltersAdapter
                    mBinding.recyclerView.scrollToPosition(mDataFactory.currentFilterIndex)
                    if (mDataFactory.currentFilterIndex == 0) {
                        mBinding.beautySeekBar.visibility = View.INVISIBLE
                    } else {
                        seekToSeekBar(mFilters[mDataFactory.currentFilterIndex].intensity, 0.0, 1.0)
                    }
                    changeBottomLayoutAnimator(true)
                }

                View.NO_ID -> {
                    changeBottomLayoutAnimator(false)
                    mDataFactory.enableFaceBeauty(true)
                }

            }
        }
    }

    private fun showRelevanceRadioButton(show: Boolean) {
        mBinding.linBeautyRelevance.visibility = if (show) View.VISIBLE else View.GONE
        (mBinding.beautySeekBar.layoutParams as LinearLayout.LayoutParams).weight =
            if (show) 1.0f else 0f
        if (!show) {
            mBinding.beautySeekBar.layoutParams.width = resources.getDimension(R.dimen.x528).toInt()
        }
        mBinding.beautySeekBar.requestLayout()
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
            mBinding.beautySeekBar.apply {
                min = -50
                max = 50
                progress = (value * 100 / range - 50).toInt()
            }

        } else {
            mBinding.beautySeekBar.apply {
                min = 0
                max = 100
                progress = (value * 100 / range).toInt()
            }
        }
        mBinding.beautySeekBar.visibility = View.VISIBLE
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
        mBeautyAdapter.notifyDataSetChanged()
    }

    /**
     * 重置还原按钮状态
     */
    private fun setRecoverFaceSkinEnable(enable: Boolean) {
        if (enable) {
            mBinding.tvBeautyRecover.alpha = 1f
            mBinding.ivBeautyRecover.alpha = 1f
        } else {
            mBinding.tvBeautyRecover.alpha = 0.6f
            mBinding.ivBeautyRecover.alpha = 0.6f
        }
        mBinding.lytBeautyRecover.isEnabled = enable
    }


    /**
     * 遍历美肤数据确认还原按钮是否可以点击
     */
    private fun checkFaceSkinChanged(): Boolean {
        if (mSkinBeauty.size > mSkinIndex && mSkinIndex > 0) {
            val item = mSkinBeauty[mSkinIndex]
            val value = mDataFactory.getParamIntensity(item.key)
            val default = mModelAttributeRange[item.key]!!.default
            if (!DecimalUtils.doubleEquals(value, default)) {
                return true
            }
            if (!TextUtils.isEmpty(item.relevanceKey)) {
                item.relevanceKey.let {
                    val relevanceValue = mDataFactory.getParamRelevanceSelectedType(it)
                    val relevanceDefault = mModelAttributeRange[it]?.default?.toInt()!!
                    if (relevanceValue != relevanceDefault) {
                        return true
                    }
                }
            }
        }

        mSkinBeauty.forEach {
            val value = mDataFactory.getParamIntensity(it.key)
            val default = mModelAttributeRange[it.key]!!.default
            if (!DecimalUtils.doubleEquals(value, default)) {
                return true
            }
            if (!TextUtils.isEmpty(it.relevanceKey)) {
                it.relevanceKey.apply {
                    val relevanceValue = mDataFactory.getParamRelevanceSelectedType(this)
                    val relevanceDefault = mModelAttributeRange[this]?.default?.toInt()!!
                    if (relevanceValue != relevanceDefault) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 遍历美型数据确认还原按钮是否可以点击
     */
    private fun checkFaceShapeChanged(): Boolean {
        if (checkFaceShapeChanged(mShapeBeauty) || checkFaceShapeChanged(mShapeBeautySubItem)) return true
        return false
    }

    /**
     * 遍历美型数据确认还原按钮是否可以点击
     */
    private fun checkFaceShapeChanged(shapeBeauty: ArrayList<FaceBeautyBean>): Boolean {
        var value: Double
        var default: Double
        if (shapeBeauty.size > mShapeIndex && mShapeIndex > 0) {
            val item = shapeBeauty[mShapeIndex]
            item.let {
                value = mDataFactory.getParamIntensity(item.key)
                default = mModelAttributeRange[item.key]!!.default
                if (!DecimalUtils.doubleEquals(value, default)) {
                    return true
                }
            }
        }

        shapeBeauty.forEach {
            when (it.buttonType) {
                FaceBeautyBean.ButtonType.NORMAL_BUTTON -> {
                    value = mDataFactory.getParamIntensity(it.key)
                    default = mModelAttributeRange[it.key]!!.default
                    if (!DecimalUtils.doubleEquals(value, default)) {
                        return true
                    }
                }
            }
        }

        return false
    }


    /**
     * 还原 美型、美肤数据
     */
    private fun recoverData() {
        when (mBinding.beautyRadioGroup.checkedCheckBoxId) {
            R.id.beauty_radio_skin_beauty -> {
                mSkinBeauty.forEach {
                    val default = mModelAttributeRange[it.key]!!.default
                    mDataFactory.updateParamIntensity(it.key, default)
                    if (!TextUtils.isEmpty(it.relevanceKey)) {
                        val relevanceDefault = mModelAttributeRange[it.relevanceKey]?.default!!
                        mDataFactory.updateParamRelevanceType(
                            it.relevanceKey,
                            relevanceDefault.toInt()
                        )
                    }

                }
                if (mSkinIndex >= 0) {
                    val item = mSkinBeauty[mSkinIndex]
                    val value = mDataFactory.getParamIntensity(item.key)
                    val stand = mModelAttributeRange[item.key]!!.stand
                    val maxRange = mModelAttributeRange[item.key]!!.maxRange
                    checkRelevanceRadioGroup(item)
                    seekToSeekBar(value, stand, maxRange)
                }
                mBeautyAdapter.notifyDataSetChanged()
                setRecoverFaceSkinEnable(false)
            }

            R.id.beauty_radio_face_shape -> {
                //还原主项和子项
                mShapeBeauty.forEach {
                    when (it.buttonType) {
                        FaceBeautyBean.ButtonType.NORMAL_BUTTON -> {
                            val default = mModelAttributeRange[it.key]!!.default
                            mDataFactory.updateParamIntensity(it.key, default)
                        }
                    }
                }

                //还原子项
                mShapeBeautySubItem.forEach {
                    when (it.buttonType) {
                        FaceBeautyBean.ButtonType.NORMAL_BUTTON -> {
                            val default = mModelAttributeRange[it.key]!!.default
                            mDataFactory.updateParamIntensity(it.key, default)
                        }
                    }
                }

                //获取当前shapeAdapter中被选中的项
                var value = 0.0
                var stand = 0.0
                var maxRange = 1.0
                if (mShapeIndex > 0) {
                    val item = mBeautyAdapter.getData(mShapeIndex)
                    when (item.buttonType) {
                        FaceBeautyBean.ButtonType.NORMAL_BUTTON -> {
                            value = mDataFactory.getParamIntensity(item.key)
                            stand = mModelAttributeRange[item.key]!!.stand
                            maxRange = mModelAttributeRange[item.key]!!.maxRange
                        }
                    }
                }
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
        val start = if (isOpen) resources.getDimension(R.dimen.x1)
            .toInt() else resources.getDimension(R.dimen.x268).toInt()
        val end = if (isOpen) resources.getDimension(R.dimen.x268)
            .toInt() else resources.getDimension(R.dimen.x1).toInt()

        if (bottomLayoutAnimator != null && bottomLayoutAnimator!!.isRunning) {
            bottomLayoutAnimator!!.end()
        }
        bottomLayoutAnimator = ValueAnimator.ofInt(start, end).setDuration(150)
        bottomLayoutAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val params = mBinding.fytBottomView.layoutParams as LinearLayout.LayoutParams
            params.height = height
            mBinding.fytBottomView.layoutParams = params
            if (onBottomAnimatorChangeListener != null) {
                val showRate = 1.0f * (height - start) / (end - start)
                onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (!isOpen) 1 - showRate else showRate)
            }
            if (DecimalUtils.floatEquals(animation.animatedFraction, 1.0f) && isOpen) {
                mBinding.ivCompare.visibility = View.VISIBLE
            }
        }
        bottomLayoutAnimator!!.start()
        isBottomShow = isOpen
    }

    override fun onDetachedFromWindow() {
        //被销毁前，记录此时的UI值，用于下一次启动的时候恢复
        mSubItemUIValueCache.forEach {
            //应该要将UI值保存用于后续恢复

        }
        super.onDetachedFromWindow()
    }

    /* item入场动画总开关 */
    val openEnterAnimation = false

    /* 是否需要对该子项进行入场动画 */
    var needEnterAnimation = false

    /**
     * recycleView ITEM 子项入场动画
     */
    fun enterAnimation(view: View) {
        //动画集合
        val animSet = AnimationSet(false)
        //位移动画
        val translateAnim = TranslateAnimation(
            Animation.ABSOLUTE,
            0f,
            Animation.ABSOLUTE,
            0f,
            Animation.ABSOLUTE,
            100f,
            Animation.ABSOLUTE,
            0f
        )
        //渐变动画
        val alphaAnim = AlphaAnimation(0.0f, 1.0f)
        animSet.addAnimation(translateAnim)
        animSet.addAnimation(alphaAnim)
        animSet.duration = 300
        animSet.fillAfter = true
        animSet.addAnimation(alphaAnim)
        view.startAnimation(animSet)
    }

    /**
     * 是否展示还原按钮
     */
    fun setResetButton(isVisible: Boolean) {
        if (isVisible) mBinding.ivReset.visibility = View.VISIBLE
        else mBinding.ivReset.visibility = View.GONE
    }

    fun getUIStates(): FaceBeautyControlState {
        return FaceBeautyControlState(
            mSkinIndex, mShapeIndex, mBinding.beautyRadioGroup.checkedCheckBoxId
        )
    }

    fun updateUIStates(faceBeautyControlState: FaceBeautyControlState?) {
        if (faceBeautyControlState != null) {
            mSkinIndex = faceBeautyControlState.skinIndex
            mShapeIndex = faceBeautyControlState.shapeIndex
            mBinding.beautyRadioGroup.check(faceBeautyControlState.rbRes)
            mBeautyAdapter.notifyDataSetChanged()
            mFiltersAdapter.notifyDataSetChanged()
        }
    }
}
