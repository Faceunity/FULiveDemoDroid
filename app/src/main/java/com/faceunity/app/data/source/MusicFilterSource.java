package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.ui.entity.MusicFilterBean;

import java.util.ArrayList;

/**
 * DESC：音乐滤镜数据构造
 * Created on 2021/3/28
 */
public class MusicFilterSource {

    /**
     * 构造音乐滤镜队列
     * @return
     */
    public static ArrayList<MusicFilterBean> buildMusicFilters() {
        ArrayList<MusicFilterBean> filters = new ArrayList<>();
        filters.add(new MusicFilterBean(R.mipmap.icon_control_delete_all, null));
        filters.add(new MusicFilterBean(R.mipmap.icon_music_filter, "effect/musicfilter/douyin_01.bundle", "effect/musicfilter/douyin_01.mp3"));
        filters.add(new MusicFilterBean(R.mipmap.icon_music_filter, "effect/musicfilter/douyin_02.bundle", "effect/musicfilter/douyin_02.mp3"));
        return filters;

    }

}
