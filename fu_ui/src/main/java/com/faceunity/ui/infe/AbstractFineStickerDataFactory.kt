package com.faceunity.ui.infe

import com.faceunity.ui.entity.net.FineStickerEntity
import com.faceunity.ui.entity.net.FineStickerTagEntity

/**
 * Created on 2021/3/31 0031 15:27.
 * Author: xloger
 * Email:phoenix@xloger.com
 */
abstract class AbstractFineStickerDataFactory {
    /*切换道具*/
    abstract fun onItemSelected(bean: FineStickerEntity.DocsBean?)
    /*获取标签列表*/
    abstract fun loadTagList(): List<FineStickerTagEntity>
    /*获取道具列表*/
    abstract fun loadStickerList(tag: FineStickerTagEntity): FineStickerEntity?
    /*下载道具*/
    abstract fun downloadSticker(docsBean: FineStickerEntity.DocsBean)

}