package com.faceunity.ui.control

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentActivity
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.databinding.LayoutStyleControlBinding
import com.faceunity.ui.dialog.BaseDialogFragment
import com.faceunity.ui.dialog.ConfirmDialogFragment
import com.faceunity.ui.dialog.ToastHelper
import com.faceunity.ui.entity.FaceBeautyBean
import com.faceunity.ui.entity.StyleBean
import com.faceunity.ui.entity.uistate.StyleControlState
import com.faceunity.ui.infe.AbstractStyleDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils

/**
 *
 * DESC：风格
 * Created on 2020/12/10
 *
 */

class StyleControlView @JvmOverloads constructor(
    val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BaseControlView(mContext, attrs, defStyleAttr) {

    private var mSkinIndex = -1
    private var mShapeIndex = -1
    private var isSubInit = false

    private lateinit var mDataFactory: AbstractStyleDataFactory
    private lateinit var mStyleAdapter: BaseListAdapter<StyleBean>
    private lateinit var mBeautyAdapter: BaseListAdapter<FaceBeautyBean>

    //是否打开子页面
    private var isOpenSub = false

    private val mBinding: LayoutStyleControlBinding by lazy {
        LayoutStyleControlBinding.inflate(LayoutInflater.from(context), this, true)
    }

    // region  init
    init {
        initView()
        initAdapter()
        bindListener()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bindListener() {
        mBinding.cytMain.setOnTouchListener { _, _ -> true }
        mBinding.cytSub.setOnTouchListener { _, _ -> true }
        /*比对开关*/
        mBinding.ivCompare.setOnTouchStateListener { v, event ->
            val action = event.action
            if (action == MotionEvent.ACTION_DOWN) {
                v.alpha = 0.7f
                mDataFactory.enableStyle(false)
            } else if (action == MotionEvent.ACTION_UP) {
                v.alpha = 1f
                mDataFactory.enableStyle(true)
            }
            true
        }

        /*还原参数*/
        mBinding.ivRecover.setOnClickListener {
            val confirmDialogFragment =
                ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_style_reset),
                    object : BaseDialogFragment.OnClickListener {
                        override fun onConfirm() { // recover params
                            mDataFactory.recoverStyleAllParams()
                            mDataFactory.onStyleSelected(mDataFactory.styleBeans[mDataFactory.currentStyleIndex].key)
                            //更新美妆滤镜滑条
                            if (!mBinding.styleMakeup.isChecked) mBinding.styleMakeup.performClick()
                            updateStyleSeekBar()
                            //没有改变
                            mBinding.ivRecover.alpha = 0.7f
                            mBinding.ivRecover.isClickable = false
                            //更新列表
                            mStyleAdapter.notifyDataSetChanged()
                        }

                        override fun onCancel() {}
                    })
            confirmDialogFragment.show(
                (mContext as FragmentActivity).supportFragmentManager,
                "StyleConfirmDialogFragmentReset"
            )
        }

        /* 子界面相关逻辑 */
        mBinding.beautyRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            //视图变化
            when (checkedId) {
                R.id.beauty_radio_skin_beauty -> {
                    showRelevanceRadioButton(false)
                    mBinding.beautyRadioSkinBeauty.isEnabled = false
                    mBinding.beautyRadioFaceShape.isEnabled = true
                    //选中美颜
                    mBeautyAdapter.setData(mDataFactory.skinBeauty)
                    mBinding.llSwitchBeautySkin.visibility = VISIBLE
                    mBinding.llSwitchBeautyShape.visibility = GONE
                    if (mSkinIndex >= 0) {
                        val data = mDataFactory.skinBeauty[mSkinIndex]
                        val value = mDataFactory.getParamIntensity(data.key)
                        val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                        val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                        checkRelevanceRadioGroup(data)
                        seekToSeekBar(mBinding.beautySeekBar, value, stand, maxRange)
                    } else {
                        mBinding.beautySeekBar.visibility = View.INVISIBLE
                    }
                }

                R.id.beauty_radio_face_shape -> {
                    showRelevanceRadioButton(false)
                    mBinding.beautyRadioSkinBeauty.isEnabled = true
                    mBinding.beautyRadioFaceShape.isEnabled = false
                    //选中美型
                    mBeautyAdapter.setData(mDataFactory.shapeBeauty)
                    mBinding.llSwitchBeautySkin.visibility = GONE
                    mBinding.llSwitchBeautyShape.visibility = VISIBLE

                    if (mShapeIndex >= 0) {
                        val data = mDataFactory.shapeBeauty[mShapeIndex]
                        val value = mDataFactory.getParamIntensity(data.key)
                        val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                        val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                        checkRelevanceRadioGroup(data)
                        seekToSeekBar(mBinding.beautySeekBar, value, stand, maxRange)
                    } else {
                        mBinding.beautySeekBar.visibility = View.INVISIBLE
                    }
                }
            }
        }


        /*返回一级页面*/
        mBinding.ivBeautyBack.setOnClickListener {
            isOpenSub = false
            openSubBottomAnimator()
            mBinding.ivRecover.visibility = View.VISIBLE
            //更新风格还原按钮
            checkStyleRecover()
        }

        mBinding.styleSeekBar.setOnProgressChangeListener(object :
            DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(
                seekBar: DiscreteSeekBar?,
                value: Int,
                fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0 * (value - seekBar!!.min) / 100
                when (mBinding.styleRadioGroup.checkedCheckBoxId) {
                    R.id.style_makeup -> {
                        val res = valueF * 1.0
                        val value = mDataFactory.getMakeupIntensity()
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateMakeupParamIntensity(res)
                        }
                    }

                    R.id.style_filter -> {
                        val res = valueF * 1.0
                        val value = mDataFactory.getFilterIntensity()
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateFilterParamIntensity(res)
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
                //更新风格还原按钮
                checkStyleRecover()
            }
        })

        /* 滑条控件监听 */
        mBinding.beautySeekBar.setOnProgressChangeListener(object :
            DiscreteSeekBar.OnProgressChangeListener {
            override fun onProgressChanged(
                seekBar: DiscreteSeekBar?,
                value: Int,
                fromUser: Boolean
            ) {
                if (!fromUser) {
                    return
                }
                val valueF = 1.0 * (value - seekBar!!.min) / 100
                when (mBinding.beautyRadioGroup.checkedCheckBoxId) {
                    R.id.beauty_radio_skin_beauty -> {
                        if (mSkinIndex < 0) return
                        val bean = mDataFactory.skinBeauty[mSkinIndex]
                        val range = mDataFactory.modelAttributeRange[bean.key]!!.maxRange
                        val res = valueF * range
                        val value = mDataFactory.getParamIntensity(bean.key)
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateParamIntensity(bean.key, res)
                            updateBeautyItemUI(
                                mBeautyAdapter.getViewHolderByPosition(mSkinIndex),
                                bean
                            )
                        }
                    }

                    R.id.beauty_radio_face_shape -> {
                        if (mShapeIndex < 0) return
                        val bean = mBeautyAdapter.getData(mShapeIndex)
                        val range = mDataFactory.modelAttributeRange[bean.key]!!.maxRange
                        val res = valueF * range
                        val value = mDataFactory.getParamIntensity(bean.key)
                        if (!DecimalUtils.doubleEquals(res, value)) {
                            mDataFactory.updateParamIntensity(bean.key, res)
                            updateBeautyItemUI(
                                mBeautyAdapter.getViewHolderByPosition(mShapeIndex),
                                bean
                            )
                        }
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {

            }
        })

        /* 美肤开关 */
        mBinding.switchBeautySkin.setOnCheckedChangeListener { buttonView, isChecked ->
            //选中美肤
            mDataFactory.enableFaceBeautySkin(isChecked)
            mSkinIndex = -1
            showRelevanceRadioButton(false)
            mBinding.beautySeekBar.visibility = View.INVISIBLE
            //刷新数据
            mBeautyAdapter.notifyDataSetChanged()
            mBinding.tvSwitchBeautySkin.text =
                if (isChecked) mContext.getString(R.string.open) else mContext.getString(R.string.close)
            if (!isChecked && isOpenSub) ToastHelper.showNormalToast(
                mContext,
                mContext.getString(
                    R.string.close_tip,
                    mContext.getString(R.string.beauty_radio_skin_beauty)
                )
            )
        }

        /* 美型开关 */
        mBinding.switchBeautyShape.setOnCheckedChangeListener { buttonView, isChecked ->
            //选中美型
            mDataFactory.enableFaceBeautyShape(isChecked)
            mShapeIndex = -1
            showRelevanceRadioButton(false)
            mBinding.beautySeekBar.visibility = View.INVISIBLE
            //刷新数据
            mBeautyAdapter.notifyDataSetChanged()
            mBinding.tvSwitchBeautyShape.text =
                if (isChecked) mContext.getString(R.string.open) else mContext.getString(R.string.close)
            if (!isChecked && isOpenSub) ToastHelper.showNormalToast(
                mContext,
                mContext.getString(
                    R.string.close_tip,
                    mContext.getString(R.string.beauty_radio_face_shape)
                )
            )
        }

        mBinding.styleRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            //视图变化
            updateStyleSeekBar()
        }
    }

    /**
     * 给控制绑定 EffectController，数据工厂
     * @param dataFactory IFaceBeautyDataFactory
     */
    fun bindDataFactory(dataFactory: AbstractStyleDataFactory) {
        mDataFactory = dataFactory
        mStyleAdapter.setData(dataFactory.styleBeans)
    }

    /**
     *  View初始化
     */
    private fun initView() {
        initHorizontalRecycleView(mBinding.recyclerStyleView)
        initHorizontalRecycleView(mBinding.recyclerBeautyView)
    }

    /**
     *  Adapter初始化
     */
    private fun initAdapter() {
        mStyleAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<StyleBean>() {
            override fun convert(
                viewType: Int,
                helper: BaseViewHolder,
                data: StyleBean,
                position: Int
            ) {
                helper.setImageResource(R.id.iv_control, data.iconId)
                helper.setText(R.id.tv_control, data.strId)
                if (position == mDataFactory.currentStyleIndex && data.key != null)
                    helper.setVisible(R.id.iv_edit, true)
                else
                    helper.setVisible(R.id.iv_edit, false)
                helper.itemView.isSelected = position == mDataFactory.currentStyleIndex
            }

            override fun onItemClickListener(view: View, data: StyleBean, position: Int) {
                if (mDataFactory.currentStyleIndex == position && data.key != null) {
                    enterSubItem(-1, -1, true)
                } else {
                    //选中逻辑
                    changeAdapterSelected(mStyleAdapter, mDataFactory.currentStyleIndex, position)
                    mStyleAdapter.notifyDataSetChanged()
                    mDataFactory.currentStyleIndex = position
                    mDataFactory.onStyleSelected(data.key)
                    //更新妆容 和 美妆 滑竿值
                    updateStyleSeekBar()
                    //更新风格还原按钮
                    checkStyleRecover()
                }
            }
        }, R.layout.list_item_control_title_edit_image_square)
        mBinding.recyclerStyleView.adapter = mStyleAdapter
        initSubAdapter()
    }

    /**
     * 跳入指定子界面
     */
    private fun enterSubItem(skinIndex: Int, shapeIndex: Int, isSkin: Boolean) {
        //跳入辅助页面
        mBinding.llSwitchBeautySkin.visibility = VISIBLE
        mBinding.llSwitchBeautyShape.visibility = GONE
        mBinding.switchBeautySkin.isChecked = mDataFactory.getCurrentStyleSkinEnable()
        mBinding.tvSwitchBeautySkin.text =
            if (mDataFactory.getCurrentStyleSkinEnable()) mContext.getString(R.string.open) else mContext.getString(
                R.string.close
            )
        mBinding.switchBeautyShape.isChecked = mDataFactory.getCurrentStyleShapeEnable()
        mBinding.tvSwitchBeautyShape.text =
            if (mDataFactory.getCurrentStyleShapeEnable()) mContext.getString(R.string.open) else mContext.getString(
                R.string.close
            )

        isOpenSub = true
        openSubBottomAnimator()
        mBinding.ivRecover.visibility = GONE
        mSkinIndex = skinIndex
        mShapeIndex = shapeIndex
        mBinding.beautyRadioSkinBeauty.isChecked = isSkin
        mBinding.beautyRadioFaceShape.isChecked = !isSkin
        mBinding.beautyRadioSkinBeauty.isEnabled = !isSkin
        mBinding.beautyRadioFaceShape.isEnabled = isSkin
        var beauties: java.util.ArrayList<FaceBeautyBean>
        var index: Int
        if (isSkin) {
            beauties = mDataFactory.skinBeauty
            index = mSkinIndex
        } else {
            beauties = mDataFactory.shapeBeauty
            index = mShapeIndex
        }
        if (index >= 0) {
            val data = beauties[index]
            val value = mDataFactory.getParamIntensity(data.key)
            val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
            val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
            checkRelevanceRadioGroup(data)
            seekToSeekBar(mBinding.beautySeekBar, value, stand, maxRange)
        } else {
            showRelevanceRadioButton(false)
            mBinding.beautySeekBar.visibility = INVISIBLE
        }
        if (mBeautyAdapter.itemCount <= 0)
            mBeautyAdapter.setData(beauties)
        else
            mBeautyAdapter.notifyDataSetChanged()
    }

    private fun initSubAdapter() {
        mBeautyAdapter = BaseListAdapter(ArrayList(), object : BaseDelegate<FaceBeautyBean>() {
            override fun convert(
                viewType: Int,
                helper: BaseViewHolder,
                data: FaceBeautyBean,
                position: Int
            ) {
                val isShinSelected =
                    mBinding.beautyRadioGroup.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                helper.itemView.isSelected =
                    if (isShinSelected) mSkinIndex == position else mShapeIndex == position
                helper.setText(R.id.tv_control, data.desRes)
                val value = mDataFactory.getParamIntensity(data.key)
                //普通按钮逻辑
                val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                if (DecimalUtils.doubleEquals(value, stand)) {
                    helper.setImageResource(R.id.iv_control, data.closeRes)
                } else {
                    helper.setImageResource(R.id.iv_control, data.openRes)
                }
                if (isShinSelected && !mBinding.switchBeautySkin.isChecked) {
                    val ivControl = helper.getView<ImageView>(R.id.iv_control)
                    ivControl?.imageAlpha = 154
                } else if (!isShinSelected && !mBinding.switchBeautyShape.isChecked) {
                    val ivControl = helper.getView<ImageView>(R.id.iv_control)
                    ivControl?.imageAlpha = 154
                } else {
                    if (!data.canUseFunction) {
                        val ivControl = helper.getView<ImageView>(R.id.iv_control)
                        ivControl?.imageAlpha = 154
                    } else {
                        val ivControl = helper.getView<ImageView>(R.id.iv_control)
                        ivControl?.imageAlpha = 255
                    }
                }

                if (helper.itemView.isSelected) {
                    val value = mDataFactory.getParamIntensity(data.key)
                    val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                    val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                    seekToSeekBar(mBinding.beautySeekBar, value, stand, maxRange)
                }
            }

            override fun onItemClickListener(view: View, data: FaceBeautyBean, position: Int) {
                if (!data.canUseFunction) {
                    ToastHelper.showNormalToast(
                        mContext,
                        mContext.getString(
                            R.string.face_beauty_function_tips,
                            mContext.getString(data.desRes)
                        )
                    )
                    return
                }
                val isShinSelected =
                    mBinding.beautyRadioGroup.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                if (isShinSelected && !mBinding.switchBeautySkin.isChecked)
                    return
                if (!isShinSelected && !mBinding.switchBeautyShape.isChecked)
                    return
                if ((isShinSelected && position == mSkinIndex) || (!isShinSelected && position == mShapeIndex)) {
                    return
                }
                checkRelevanceRadioGroup(data)
                val value = mDataFactory.getParamIntensity(data.key)
                val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                seekToSeekBar(mBinding.beautySeekBar, value, stand, maxRange)
                if (isShinSelected) {
                    changeAdapterSelected(mBeautyAdapter, mSkinIndex, position)
                    mSkinIndex = position
                } else {
                    changeAdapterSelected(mBeautyAdapter, mShapeIndex, position)
                    mShapeIndex = position
                }
            }
        }, R.layout.list_item_control_title_image_circle)
        mBinding.recyclerBeautyView.adapter = mBeautyAdapter
        isSubInit = true
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
                    mBinding.rbLeftBeautyRelevance.isSelected = true
                    mBinding.rbRightBeautyRelevance.isSelected = false
                }
                mBinding.rbRightBeautyRelevance.setOnClickListener {
                    mDataFactory.updateParamRelevanceType(data.relevanceKey, 1)
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

    private fun showRelevanceRadioButton(show: Boolean) {
        mBinding.linBeautyRelevance.visibility = if (show) View.VISIBLE else View.GONE
        (mBinding.beautySeekBar.layoutParams as LinearLayout.LayoutParams).weight =
            if (show) 1.0f else 0f
        if (!show) {
            mBinding.beautySeekBar.layoutParams.width = resources.getDimension(R.dimen.x528).toInt()
        }
        mBinding.beautySeekBar.requestLayout()
    }

    /**
     * 更新风格那个seekbar 美妆 滤镜
     */
    private fun updateStyleSeekBar() {
        if (mDataFactory.currentStyleIndex == 0)
            mBinding.llSeekBar.visibility = View.INVISIBLE
        else
            mBinding.llSeekBar.visibility = View.VISIBLE

        val stand = 0.0
        val range = 1.0
        when (mBinding.styleRadioGroup.checkedCheckBoxId) {
            R.id.style_makeup -> {
                mBinding.styleMakeup.isEnabled = false
                mBinding.styleFilter.isEnabled = true
                val value = mDataFactory.getMakeupIntensity()
                seekToSeekBar(mBinding.styleSeekBar, value, stand, range)
            }

            R.id.style_filter -> {
                mBinding.styleMakeup.isEnabled = true
                mBinding.styleFilter.isEnabled = false
                val value = mDataFactory.getFilterIntensity()
                seekToSeekBar(mBinding.styleSeekBar, value, stand, range)
            }
        }
    }

    /**
     * 设置滚动条数值
     * @param value Double 结果值
     * @param stand Double 标准值
     * @param range Double 范围区间
     */
    private fun seekToSeekBar(
        seekBar: DiscreteSeekBar,
        value: Double,
        stand: Double,
        range: Double
    ) {
        if (stand == 0.5) {
            seekBar.min = -50
            seekBar.max = 50
            seekBar.progress = (value * 100 / range - 50).toInt()
        } else {
            seekBar.min = 0
            seekBar.max = 100
            seekBar.progress = (value * 100 / range).toInt()
        }
        seekBar.visibility = View.VISIBLE
    }

    /**
     * 更新单项是否为基准值显示
     */
    private fun updateBeautyItemUI(viewHolder: BaseViewHolder?, item: FaceBeautyBean) {
        val value = mDataFactory.getParamIntensity(item.key)
        val stand = mDataFactory.modelAttributeRange[item.key]!!.stand
        if (DecimalUtils.doubleEquals(value, stand)) {
            viewHolder?.setImageResource(R.id.iv_control, item.closeRes)
        } else {
            viewHolder?.setImageResource(R.id.iv_control, item.openRes)
        }
        mBeautyAdapter.notifyDataSetChanged()
    }

    /* 主菜单动画 */
    private var cytMainAnimator: ValueAnimator? = null

    /* 子菜单动画 */
    private var cytSubAnimator: ValueAnimator? = null

    private fun openSubBottomAnimator() {
        openSubBottomAnimator(isOpenSub, 150)
    }

    /**
     * 菜单切换动画，先收起后弹出
     * @param isOpenSub Boolean 是否开启子菜单
     */
    private fun openSubBottomAnimator(isOpenSub: Boolean, duration: Long) {
        if (cytMainAnimator != null && cytMainAnimator!!.isRunning) {
            cytMainAnimator!!.cancel()
        }
        if (cytSubAnimator != null && cytSubAnimator!!.isRunning) {
            cytSubAnimator!!.cancel()
        }
        val start =
            if (isOpenSub) resources.getDimensionPixelSize(R.dimen.x268) else resources.getDimensionPixelSize(
                R.dimen.x366
            )
        val mid = 1
        val end =
            if (isOpenSub) resources.getDimensionPixelSize(R.dimen.x366) else resources.getDimensionPixelSize(
                R.dimen.x268
            )
        cytMainAnimator = ValueAnimator.ofInt(start, mid)
        cytMainAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val view = if (isOpenSub) mBinding.cytMain else mBinding.cytSub
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.height = height
            view.layoutParams = params
            val showRate = 0.5f * (height - start) / (mid - start)
            onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (isOpenSub) showRate else 1 - showRate)
        }
        cytMainAnimator!!.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                cytSubAnimator!!.start()
            }
        })
        cytMainAnimator!!.duration = duration
        cytSubAnimator = ValueAnimator.ofInt(mid, end)
        cytSubAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val view = if (!isOpenSub) mBinding.cytMain else mBinding.cytSub
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.height = height
            view.layoutParams = params
            val showRate = 0.5f * (height - mid) / (end - mid) + 0.5f
            onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (isOpenSub) showRate else 1 - showRate)
        }
        cytSubAnimator!!.duration = duration
        cytMainAnimator!!.start()
    }

    /**
     * 检查所有的风格是否有改变
     */
    fun checkStyleRecover() {
        if (mDataFactory.checkStyleRecover()) {
            //没有改变
            mBinding.ivRecover.alpha = 0.7f
            mBinding.ivRecover.isClickable = false
        } else {
            //有改变
            mBinding.ivRecover.alpha = 1.0f
            mBinding.ivRecover.isClickable = true
        }
    }

    fun getUIStates(): StyleControlState {
        return StyleControlState(
            mSkinIndex,
            mShapeIndex,
            isOpenSub,
            mBinding.beautyRadioSkinBeauty.isChecked,
            mBinding.styleFilter.isChecked
        )
    }

    /**
     * 更新UI数据
     */
    fun updateUIStates() = updateUIStates(null)

    /**
     * 更新UI数据
     */
    fun updateUIStates(styleControlState: StyleControlState?) {
        if (styleControlState == null) {
            mSkinIndex = -1
            mShapeIndex = -1
            isOpenSub = false
            openSubBottomAnimator(isOpenSub, 1)
            mBinding.ivRecover.visibility = View.VISIBLE
            mStyleAdapter.notifyDataSetChanged()
            mBinding.styleFilter.isChecked = false
            mBinding.styleFilter.isEnabled = true
            mBinding.styleMakeup.isChecked = true
            mBinding.styleMakeup.isEnabled = false
            updateStyleSeekBar()
            checkStyleRecover()
        } else {
            isOpenSub = styleControlState.isSubOpen
            mBinding.styleFilter.isChecked = styleControlState.isFilter
            mBinding.styleFilter.isEnabled = !styleControlState.isFilter
            mBinding.styleMakeup.isChecked = !styleControlState.isFilter
            mBinding.styleMakeup.isEnabled = styleControlState.isFilter
            updateStyleSeekBar()
            checkStyleRecover()
            if (styleControlState.isSubOpen) {
                enterSubItem(
                    styleControlState.skinIndex,
                    styleControlState.shapeIndex,
                    styleControlState.isSkin
                )
            } else {
                mBinding.ivRecover.visibility = View.VISIBLE
                openSubBottomAnimator(isOpenSub, 1)
            }
            mStyleAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 选中当前应该选中的风格
     */
    fun selectCurrentStyle() {
        mDataFactory.styleTypeIndex()
        mDataFactory.onStyleSelected(
            mDataFactory.styleBeans[mDataFactory.currentStyleIndex].key,
            false
        )
    }
}