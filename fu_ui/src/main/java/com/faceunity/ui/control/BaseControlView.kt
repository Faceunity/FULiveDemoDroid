package com.faceunity.ui.control

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.dialog.BaseDialogFragment
import com.faceunity.ui.dialog.ConfirmDialogFragment
import com.faceunity.ui.listener.OnBottomAnimatorChangeListener


/**
 *
 * DESC：
 * Created on 2020/12/8
 *
 */

abstract class BaseControlView @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(mContext, attrs, defStyleAttr) {


    /**
     * adapter单项点击，选中状态变更
     * @param adapter BaseListAdapter<T>
     * @param old Int
     * @param new Int
     */
    protected fun <T> changeAdapterSelected(adapter: BaseListAdapter<T>, old: Int, new: Int) {
        if (old >= 0) {
            adapter.getViewByPosition(old)?.isSelected = false
        }
        if (new >= 0) {
            adapter.getViewByPosition(new)?.isSelected = true
        }
    }


    protected fun showDialog(tip: String, unit: () -> Unit) {
        val confirmDialogFragment =
            ConfirmDialogFragment.newInstance(tip,
                object : BaseDialogFragment.OnClickListener {
                    override fun onConfirm() { // recover params
                        unit.invoke()
                    }

                    override fun onCancel() {}
                })
        confirmDialogFragment.show((mContext as FragmentActivity).supportFragmentManager, "ConfirmDialogFragmentReset")
    }


    protected fun initHorizontalRecycleView(recyclerView: RecyclerView) {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }


    /****************************************菜单动画*****************************************************/


    var isBottomShow = false
    var bottomLayoutAnimator: ValueAnimator? = null
    var onBottomAnimatorChangeListener: OnBottomAnimatorChangeListener? = null


}