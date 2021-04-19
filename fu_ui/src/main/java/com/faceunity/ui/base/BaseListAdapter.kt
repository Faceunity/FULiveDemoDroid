package com.faceunity.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.faceunity.ui.listener.OnMultiClickListener
import org.jetbrains.annotations.NotNull


/**
 *
 * DESC：RecycleView 通用适配器
 * Created on 2020/11/17
 *
 */
class BaseListAdapter<T>(private val data: ArrayList<T>, private val viewHolderDelegate: BaseDelegate<T>, @NotNull vararg resLayouts: Int) :
    RecyclerView.Adapter<BaseViewHolder>() {
    private val mLayouts = resLayouts
    private val mViewHolder = HashMap<Int, BaseViewHolder>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(getLayoutId(viewType), parent, false)
        return BaseViewHolder(view)

    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        mViewHolder[position] = holder
        viewHolderDelegate.convert(getItemViewType(position), holder, data[position], position)
        bindViewClickListener(holder, position)
    }


    fun setData(items: ArrayList<T>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun getData(position: Int) = data[position]

    fun getViewHolderByPosition(position: Int)  = mViewHolder[position]

    fun getViewByPosition(position: Int)  = mViewHolder[position]?.itemView

    private fun bindViewClickListener(holder: BaseViewHolder, position: Int) {
        val view = holder.itemView
        view.setOnClickListener(object : OnMultiClickListener() {
            override fun onMultiClick(v: View?) {
                viewHolderDelegate.onItemClickListener(view, data[position], position)
            }
        })
        view.setOnLongClickListener {
            viewHolderDelegate.onItemLongClickListener(view, data[position], position)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return viewHolderDelegate.getItemViewType(data[position], position)
    }

    private fun getLayoutId(viewType: Int): Int {
        return mLayouts[viewType]
    }


}