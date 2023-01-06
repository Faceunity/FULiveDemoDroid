package com.faceunity.ui.control

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
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
import com.faceunity.ui.dialog.BaseDialogFragment
import com.faceunity.ui.dialog.ConfirmDialogFragment
import com.faceunity.ui.dialog.ToastHelper
import com.faceunity.ui.entity.FaceBeautyBean
import com.faceunity.ui.entity.StyleBean
import com.faceunity.ui.entity.uistate.StyleControlState
import com.faceunity.ui.infe.AbstractStyleDataFactory
import com.faceunity.ui.seekbar.DiscreteSeekBar
import com.faceunity.ui.utils.DecimalUtils
import kotlinx.android.synthetic.main.layout_style_control.view.*

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

    // region  init
    init {
        LayoutInflater.from(context).inflate(R.layout.layout_style_control, this)
        initView()
        initAdapter()
        bindListener()
    }

    private fun bindListener() {
        cyt_main.setOnTouchListener { _, _ -> true }
        cyt_sub.setOnTouchListener { _, _ -> true }
        /*比对开关*/
        iv_compare.setOnTouchStateListener { v, event ->
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
        iv_recover.setOnClickListener {
            val confirmDialogFragment =
                ConfirmDialogFragment.newInstance(mContext.getString(R.string.dialog_style_reset),
                    object : BaseDialogFragment.OnClickListener {
                        override fun onConfirm() { // recover params
                            mDataFactory.recoverStyleAllParams()
                            mDataFactory.onStyleSelected(mDataFactory.styleBeans[mDataFactory.currentStyleIndex].key)
                            //更新美妆滤镜滑条
                            if (!style_makeup.isChecked) style_makeup.performClick()
                            updateStyleSeekBar()
                            //没有改变
                            iv_recover.alpha = 0.7f
                            iv_recover.isClickable = false
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
        beauty_radio_group.setOnCheckedChangeListener { _, checkedId ->
            //视图变化
            when (checkedId) {
                R.id.beauty_radio_skin_beauty -> {
                    beauty_radio_skin_beauty.isEnabled = false
                    beauty_radio_face_shape.isEnabled = true
                    //选中美颜
                    mBeautyAdapter.setData(mDataFactory.skinBeauty)
                    ll_switch_beauty_skin.visibility = VISIBLE
                    ll_switch_beauty_shape.visibility = GONE
                    if (mSkinIndex >= 0) {
                        val data = mDataFactory.skinBeauty[mSkinIndex]
                        val value = mDataFactory.getParamIntensity(data.key)
                        val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                        val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                        seekToSeekBar(beauty_seek_bar, value, stand, maxRange)
                    } else {
                        beauty_seek_bar.visibility = View.INVISIBLE
                    }
                }
                R.id.beauty_radio_face_shape -> {
                    beauty_radio_skin_beauty.isEnabled = true
                    beauty_radio_face_shape.isEnabled = false
                    //选中美型
                    mBeautyAdapter.setData(mDataFactory.shapeBeauty)
                    ll_switch_beauty_skin.visibility = GONE
                    ll_switch_beauty_shape.visibility = VISIBLE

                    if (mShapeIndex >= 0) {
                        val data = mDataFactory.shapeBeauty[mShapeIndex]
                        val value = mDataFactory.getParamIntensity(data.key)
                        val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                        val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                        seekToSeekBar(beauty_seek_bar, value, stand, maxRange)
                    } else {
                        beauty_seek_bar.visibility = View.INVISIBLE
                    }
                }
            }
        }


        /*返回一级页面*/
        iv_beauty_back.setOnClickListener {
            isOpenSub = false
            openSubBottomAnimator()
            iv_recover.visibility = View.VISIBLE
            //更新风格还原按钮
            checkStyleRecover()
        }

        style_seek_bar.setOnProgressChangeListener(object :
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
                when (style_radio_group.checkedCheckBoxId) {
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
        beauty_seek_bar.setOnProgressChangeListener(object :
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
                when (beauty_radio_group.checkedCheckBoxId) {
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
        switch_beauty_skin.setOnCheckedChangeListener { buttonView, isChecked ->
            //选中美肤
            mDataFactory.enableFaceBeautySkin(isChecked)
            mSkinIndex = -1
            beauty_seek_bar.visibility = View.INVISIBLE
            //刷新数据
            mBeautyAdapter.notifyDataSetChanged()
            tv_switch_beauty_skin.text = if (isChecked) mContext.getString(R.string.open) else mContext.getString(R.string.close)
            if (!isChecked && isOpenSub)  ToastHelper.showNormalToast(mContext,mContext.getString(R.string.close_tip, mContext.getString(R.string.beauty_radio_skin_beauty)))
        }

        /* 美型开关 */
        switch_beauty_shape.setOnCheckedChangeListener { buttonView, isChecked ->
            //选中美型
            mDataFactory.enableFaceBeautyShape(isChecked)
            mShapeIndex = -1
            beauty_seek_bar.visibility = View.INVISIBLE
            //刷新数据
            mBeautyAdapter.notifyDataSetChanged()
            tv_switch_beauty_shape.text = if (isChecked) mContext.getString(R.string.open) else mContext.getString(R.string.close)
            if (!isChecked && isOpenSub) ToastHelper.showNormalToast(mContext,mContext.getString(R.string.close_tip, mContext.getString(R.string.beauty_radio_face_shape)))
        }

        style_radio_group.setOnCheckedChangeListener { _, checkedId ->
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
        initHorizontalRecycleView(recycler_style_view)
        initHorizontalRecycleView(recycler_beauty_view)
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
                    enterSubItem(-1,-1,true)
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
        recycler_style_view.adapter = mStyleAdapter
        initSubAdapter()
    }

    /**
     * 跳入指定子界面
     */
    private fun enterSubItem(skinIndex:Int,shapeIndex:Int,isSkin:Boolean) {
        //跳入辅助页面
        ll_switch_beauty_skin.visibility = VISIBLE
        ll_switch_beauty_shape.visibility = GONE
        switch_beauty_skin.isChecked = mDataFactory.getCurrentStyleSkinEnable()
        tv_switch_beauty_skin.text =
            if (mDataFactory.getCurrentStyleSkinEnable()) mContext.getString(R.string.open) else mContext.getString(
                R.string.close
            )
        switch_beauty_shape.isChecked = mDataFactory.getCurrentStyleShapeEnable()
        tv_switch_beauty_shape.text =
            if (mDataFactory.getCurrentStyleShapeEnable()) mContext.getString(R.string.open) else mContext.getString(
                R.string.close
            )

        isOpenSub = true
        openSubBottomAnimator()
        iv_recover.visibility = GONE
        mSkinIndex = skinIndex
        mShapeIndex = shapeIndex
        beauty_radio_skin_beauty.isChecked = isSkin
        beauty_radio_face_shape.isChecked = !isSkin
        beauty_radio_skin_beauty.isEnabled = !isSkin
        beauty_radio_face_shape.isEnabled = isSkin
        var beauties: java.util.ArrayList<FaceBeautyBean>
        var index: Int
        if (isSkin){
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
            seekToSeekBar(beauty_seek_bar, value, stand, maxRange)
        } else {
            beauty_seek_bar.visibility = INVISIBLE
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
                    beauty_radio_group.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
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
                if (isShinSelected && !switch_beauty_skin.isChecked) {
                    val ivControl = helper.getView<ImageView>(R.id.iv_control)
                    ivControl?.imageAlpha = 154
                } else if (!isShinSelected && !switch_beauty_shape.isChecked) {
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
                    seekToSeekBar(beauty_seek_bar, value, stand, maxRange)
                }
            }

            override fun onItemClickListener(view: View, data: FaceBeautyBean, position: Int) {
                if (!data.canUseFunction) {
                    ToastHelper.showNormalToast(mContext,mContext.getString(R.string.face_beauty_function_tips, mContext.getString(data.desRes)))
                    return
                }
                val isShinSelected =
                    beauty_radio_group.checkedCheckBoxId == R.id.beauty_radio_skin_beauty
                if (isShinSelected && !switch_beauty_skin.isChecked)
                    return
                if (!isShinSelected && !switch_beauty_shape.isChecked)
                    return
                if ((isShinSelected && position == mSkinIndex) || (!isShinSelected && position == mShapeIndex)) {
                    return
                }
                val value = mDataFactory.getParamIntensity(data.key)
                val stand = mDataFactory.modelAttributeRange[data.key]!!.stand
                val maxRange = mDataFactory.modelAttributeRange[data.key]!!.maxRange
                seekToSeekBar(beauty_seek_bar, value, stand, maxRange)
                if (isShinSelected) {
                    changeAdapterSelected(mBeautyAdapter, mSkinIndex, position)
                    mSkinIndex = position
                } else {
                    changeAdapterSelected(mBeautyAdapter, mShapeIndex, position)
                    mShapeIndex = position
                }
            }
        }, R.layout.list_item_control_title_image_circle)
        recycler_beauty_view.adapter = mBeautyAdapter
        isSubInit = true
    }

    /**
     * 更新风格那个seekbar 美妆 滤镜
     */
    private fun updateStyleSeekBar() {
        if (mDataFactory.currentStyleIndex == 0)
            ll_seek_bar.visibility = View.INVISIBLE
        else
            ll_seek_bar.visibility = View.VISIBLE

        val stand = 0.0
        val range = 1.0
        when (style_radio_group.checkedCheckBoxId) {
            R.id.style_makeup -> {
                style_makeup.isEnabled = false
                style_filter.isEnabled = true
                val value = mDataFactory.getMakeupIntensity()
                seekToSeekBar(style_seek_bar, value, stand, range)
            }

            R.id.style_filter -> {
                style_makeup.isEnabled = true
                style_filter.isEnabled = false
                val value = mDataFactory.getFilterIntensity()
                seekToSeekBar(style_seek_bar, value, stand, range)
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
        openSubBottomAnimator(isOpenSub,150)
    }

    /**
     * 菜单切换动画，先收起后弹出
     * @param isOpenSub Boolean 是否开启子菜单
     */
    private fun openSubBottomAnimator(isOpenSub: Boolean,duration :Long) {
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
            val view = if (isOpenSub) cyt_main else cyt_sub
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
            val view = if (!isOpenSub) cyt_main else cyt_sub
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
            iv_recover.alpha = 0.7f
            iv_recover.isClickable = false
        } else {
            //有改变
            iv_recover.alpha = 1.0f
            iv_recover.isClickable = true
        }
    }

    fun getUIStates(): StyleControlState {
        return StyleControlState(mSkinIndex,mShapeIndex,isOpenSub,beauty_radio_skin_beauty.isChecked,style_filter.isChecked)
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
            openSubBottomAnimator(isOpenSub,1)
            iv_recover.visibility = View.VISIBLE
            mStyleAdapter.notifyDataSetChanged()
            style_filter.isChecked = false
            style_filter.isEnabled = true
            style_makeup.isChecked = true
            style_makeup.isEnabled = false
            updateStyleSeekBar()
            checkStyleRecover()
        } else {
            isOpenSub = styleControlState.isSubOpen
            style_filter.isChecked = styleControlState.isFilter
            style_filter.isEnabled = !styleControlState.isFilter
            style_makeup.isChecked = !styleControlState.isFilter
            style_makeup.isEnabled = styleControlState.isFilter
            updateStyleSeekBar()
            checkStyleRecover()
            if (styleControlState.isSubOpen) {
                enterSubItem(styleControlState.skinIndex,styleControlState.shapeIndex,styleControlState.isSkin)
            } else {
                iv_recover.visibility = View.VISIBLE
                openSubBottomAnimator(isOpenSub,1)
            }
            mStyleAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 选中当前应该选中的风格
     */
    fun selectCurrentStyle(){
        mDataFactory.styleTypeIndex()
        mDataFactory.onStyleSelected(mDataFactory.styleBeans[mDataFactory.currentStyleIndex].key,false)
    }
}