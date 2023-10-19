package com.faceunity.ui.control

import android.animation.ValueAnimator
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.faceunity.ui.R
import com.faceunity.ui.base.BaseDelegate
import com.faceunity.ui.base.BaseListAdapter
import com.faceunity.ui.base.BaseViewHolder
import com.faceunity.ui.databinding.LayoutFineStickerBinding
import com.faceunity.ui.dialog.ToastHelper
import com.faceunity.ui.entity.net.DownLoadStatus
import com.faceunity.ui.entity.net.FineStickerEntity
import com.faceunity.ui.entity.net.FineStickerEntity.DocsBean
import com.faceunity.ui.entity.net.FineStickerTagEntity
import com.faceunity.ui.infe.AbstractFineStickerDataFactory
import com.google.android.material.tabs.TabLayout
import java.util.Locale

/**
 * Created on 2021/3/31 0031 15:25.
 * Author: xloger
 * Email:phoenix@xloger.com
 */
class FineStickerView @JvmOverloads constructor(
    val mContext: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    BaseControlView(mContext, attrs, defStyleAttr) {


    private lateinit var dataFactory: AbstractFineStickerDataFactory
    private lateinit var tagPagerAdapter: TagPagerAdapter

    private var currentSticker: DocsBean? = null
    private var currentTag: String? = null
    private var currentPosition: Int = 0

    private val mBinding: LayoutFineStickerBinding by lazy {
        LayoutFineStickerBinding.inflate(LayoutInflater.from(context), this, true)
    }

    // region  init
    init {
        initAdapter()
        bindListener()
    }

    fun bindDataFactory(dataFactory: AbstractFineStickerDataFactory) {
        this.dataFactory = dataFactory
        tagPagerAdapter.setTags(dataFactory.loadTagList())
    }

    private fun initAdapter() {
        tagPagerAdapter = TagPagerAdapter(mContext, mutableListOf())
        mBinding.vpFineSticker.adapter = tagPagerAdapter
    }


    private fun bindListener() {
        mBinding.tlFineSticker.setupWithViewPager(mBinding.vpFineSticker)
        mBinding.tlFineSticker.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeBottomLayoutAnimator(true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                changeBottomLayoutAnimator(!isBottomShow)
            }
        })
        mBinding.ivDeleteAll.setOnClickListener {
            dataFactory.onItemSelected(null)
            updateAdapterView(null, 0, null)
        }
    }
    //endregion init
    //region 接口调用
    /**
     * 返回标签栏
     * @param tags List<FineStickerTagEntity>
     */
    fun onGetTags(tags: List<FineStickerTagEntity>) {
        tagPagerAdapter.setTags(tags)
    }

    /**
     * 加载道具列表
     * @param tag FineStickerTagEntity
     * @param fineStickerEntity FineStickerEntity
     */
    fun onGetToolList(tag: FineStickerTagEntity, fineStickerEntity: FineStickerEntity) {
        if (tag.tag == null) return
        val rv: RecyclerView? = safeFindViewWithTag(tag.tag)
        val adapter = rv?.adapter as BaseListAdapter<DocsBean>?
        adapter?.setData(fineStickerEntity.docs)
    }

    /**
     * 加载完成
     * @param entity DocsBean
     */
    fun onDownload(entity: DocsBean) {
        entity.downloadStatus = DownLoadStatus.DOWN_LOAD_SUCCESS
        val rv: RecyclerView? = safeFindViewWithTag(entity.tag)
        val adapter = rv?.adapter as BaseListAdapter<DocsBean>?
        adapter?.notifyDataSetChanged()
        if (currentSticker == entity) {
            entity.filePath.let {
                dataFactory.onItemSelected(entity)
            }
        }
    }

    /**
     * 加载完成
     * @param entity DocsBean
     */
    fun onDownloadError(entity: DocsBean, msg: String) {
        entity.downloadStatus = DownLoadStatus.DOWN_LOAD_FAILED
        val rv: RecyclerView? = safeFindViewWithTag(entity.tag)
        val adapter = rv?.adapter as BaseListAdapter<DocsBean>?
        adapter?.notifyDataSetChanged()
        ToastHelper.showNormalToast(mContext, R.string.download_error)
    }


    /**
     * 隐藏窗口
     */
    fun hideControlView() {
        changeBottomLayoutAnimator(false)
    }

    //endregion 接口调用

    //region 视图调用


    /**
     * 底部动画处理
     * @param isOpen Boolean
     */
    private fun changeBottomLayoutAnimator(isOpen: Boolean) {
        if (isBottomShow == isOpen) {
            return
        }
        val start = if (isOpen) resources.getDimension(R.dimen.x0)
            .toInt() else resources.getDimension(R.dimen.x364).toInt()
        val end = if (isOpen) resources.getDimension(R.dimen.x364)
            .toInt() else resources.getDimension(R.dimen.x0).toInt()

        if (bottomLayoutAnimator != null && bottomLayoutAnimator!!.isRunning) {
            bottomLayoutAnimator!!.end()
        }
        bottomLayoutAnimator = ValueAnimator.ofInt(start, end).setDuration(150)
        bottomLayoutAnimator!!.addUpdateListener { animation ->
            val height = animation.animatedValue as Int
            val params = mBinding.lytBottomView.layoutParams as LinearLayout.LayoutParams
            params.height = height
            mBinding.lytBottomView.layoutParams = params
            if (onBottomAnimatorChangeListener != null) {
                val showRate = 1.0f * (height - start) / (end - start)
                onBottomAnimatorChangeListener?.onBottomAnimatorChangeListener(if (!isOpen) 1 - showRate else showRate)
            }
        }
        bottomLayoutAnimator!!.start()
        isBottomShow = isOpen
    }
    //endregion 视图调用

    /**
     * 底部适配器
     * @property context Context
     * @property tags MutableList<FineStickerTagEntity>
     * @property gridSpanCount Int
     * @constructor
     */
    private inner class TagPagerAdapter(
        private val context: Context,
        private var tags: MutableList<FineStickerTagEntity>
    ) : PagerAdapter() {

        val gridSpanCount = 5

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val list: ArrayList<DocsBean> =
                dataFactory.loadStickerList(tags[position])?.docs ?: ArrayList()
            val recyclerView = initRecyclerView()
            val adapter = initAdapter(list, tags[position].tag)
            recyclerView.adapter = adapter
            recyclerView.tag = tags[position].tag
            container.addView(recyclerView)
            return recyclerView
        }

        fun setTags(tags: List<FineStickerTagEntity>) {
            this.tags.clear()
            this.tags.addAll(tags)
            notifyDataSetChanged()
        }


        private fun initRecyclerView(): RecyclerView {
            val recyclerView = RecyclerView(context)
            recyclerView.layoutManager = GridLayoutManager(context, gridSpanCount)
            (recyclerView.itemAnimator as SimpleItemAnimator?)!!.supportsChangeAnimations = false
            return recyclerView
        }

        private fun initAdapter(
            items: ArrayList<DocsBean>,
            tag: String
        ): BaseListAdapter<DocsBean> {
            return BaseListAdapter(items, object : BaseDelegate<DocsBean>() {
                override fun convert(
                    viewType: Int,
                    helper: BaseViewHolder,
                    data: DocsBean,
                    position: Int
                ) {
                    val loadingView = helper.getView<ImageView>(R.id.iv_loading)!!
                    val stickerView = helper.getView<ImageView>(R.id.iv_fine_sticker)!!
                    val downloadView = helper.getView<ImageView>(R.id.iv_download)!!
                    if (data.tool.icon != null) {
                        val requestOptions =
                            RequestOptions().placeholder(R.mipmap.icon_control_placeholder)
                                .error(R.mipmap.icon_control_placeholder)
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                        Glide.with(mContext).load(data.tool.icon.url).apply(requestOptions)
                            .into(stickerView)
                    } else {
                        stickerView.setImageResource(R.mipmap.icon_control_placeholder)
                    }
                    data.filePath?.let {
                        downloadView.visibility = GONE
                    }
                    val rotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.anim_rotate)
                    when (data.downloadStatus) {
                        DownLoadStatus.DOWN_LOADING -> {
                            loadingView.startAnimation(rotateAnim)
                            loadingView.visibility = VISIBLE
                            stickerView.alpha = 0.6f
                            downloadView.visibility = GONE
                        }

                        DownLoadStatus.DOWN_LOAD_FAILED -> {
                            stickerView.alpha = 1f
                            loadingView.visibility = GONE
                            loadingView.clearAnimation()
                        }

                        else -> {
                            stickerView.alpha = 1f
                            loadingView.visibility = GONE
                            loadingView.clearAnimation()
                            //手指最后点击的位置
                            helper.itemView.isSelected = currentSticker == data
                        }
                    }
                }

                override fun onItemClickListener(view: View, data: DocsBean, position: Int) {
                    if (currentSticker == data) {
                        if (TextUtils.isEmpty(data.filePath) && data.downloadStatus != DownLoadStatus.DOWN_LOADING) {
                            dataFactory.downloadSticker(data)
                            data.downloadStatus = DownLoadStatus.DOWN_LOADING
                            updateCurrentAdapterView()
                            return
                        }
                        return
                    }
                    if (TextUtils.isEmpty(data.filePath)) {
                        if (data.downloadStatus == DownLoadStatus.DOWN_LOADING) return
                        dataFactory.downloadSticker(data)
                        data.downloadStatus = DownLoadStatus.DOWN_LOADING
                        dataFactory.onItemSelected(null)
                    } else {
                        dataFactory.onItemSelected(data)
                    }
                    updateAdapterView(tag, position, data)
                }
            }, R.layout.list_item_control_fine_sticker)
        }

        override fun getCount(): Int {
            return tags.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return try {
                val tagArray = tags[position].tag.split("/")
                if (Locale.getDefault().language.contains("zh")) {
                    tagArray[0]
                } else {
                    tagArray[1]
                }
            } catch (e: Exception) {
                tags[position].tag
            }
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }

    /**
     * 点击后选中状态变更
     * @param tag String
     * @param position Int
     * @param sticker DocsBean
     */
    private fun updateAdapterView(tag: String?, position: Int, sticker: DocsBean?) {
        currentSticker = sticker
        val rv: RecyclerView? = safeFindViewWithTag(currentTag)
        val adapter = rv?.adapter as BaseListAdapter<DocsBean>?
        adapter?.notifyItemChanged(currentPosition)

        currentPosition = position
        currentTag = tag

        val currentRecyclerView: RecyclerView? = safeFindViewWithTag(tag)
        val currentAdapter = currentRecyclerView?.adapter as BaseListAdapter<DocsBean>?
        currentAdapter?.notifyItemChanged(position)
    }

    /**
     * 状态刷新
     */
    private fun updateCurrentAdapterView() {
        if (currentTag != null) {
            val rv: RecyclerView? = safeFindViewWithTag(currentTag)
            val adapter = rv?.adapter as BaseListAdapter<DocsBean>?
            adapter?.notifyItemChanged(currentPosition)
        }
    }

    /**
     * 安全的findViewWithTag
     */
    private fun safeFindViewWithTag(tag: String?): RecyclerView? {
        try {
            tag?.let {
                return mBinding.vpFineSticker.findViewWithTag(it)
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }
        return null
    }
}