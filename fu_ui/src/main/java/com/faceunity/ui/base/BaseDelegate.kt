package com.faceunity.ui.base

import android.view.View


/**
 *
 * DESC：RecycleView 通用业务调用
 * Created on 2020/11/17
 *
 */
abstract class BaseDelegate<T> {
    /**
     * 根据页面以及数据内容返回Item的布局index,默认返回第一个布局
     *
     * @param data
     * @param position
     * @return
     */
    open fun getItemViewType(data: T, position: Int): Int {
        return 0
    }


    /**
     * 为ViewHolder绑定数据item
     *
     * @param viewType
     * @param helper
     * @param data
     * @param position
     * @return
     */
    abstract fun convert(viewType: Int, helper: BaseViewHolder, data: T, position: Int)

    /**
     * 绑定单击事件
     *
     * @param view
     * @param data
     * @param position
     */
    open fun onItemClickListener(view: View, data: T, position: Int) {

    }


    /**
     * 绑定长按事件
     *
     * @param view
     * @param data
     * @param position
     */
    open fun onItemLongClickListener(view: View, data: T, position: Int): Boolean {
        return false
    }
}