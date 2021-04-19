package com.faceunity.ui.infe

import com.faceunity.ui.entity.PosterBean


/**
 *
 * DESC：
 * Created on 2020/12/28
 *
 */
abstract class AbstractPosterChangeFaceDataFactory {

    abstract var currentPosterIndex: Int

    abstract fun onItemSelectedChange(data: PosterBean)

    abstract val posters: ArrayList<PosterBean>
}