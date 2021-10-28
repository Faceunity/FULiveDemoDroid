package com.faceunity.app.data;

import com.faceunity.app.data.source.MusicFilterSource;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.musicFilter.MusicFilter;
import com.faceunity.ui.entity.MusicFilterBean;
import com.faceunity.ui.infe.AbstractMusicFilterDataFactory;

import java.util.ArrayList;

/**
 * DESC：音乐滤镜业务工厂
 * Created on 2021/3/3
 */
public class MusicFilterDataFactory extends AbstractMusicFilterDataFactory {

    public interface MusicFilterListener {
        /**
         * 音乐滤镜变更回调
         *
         * @param data
         */
        void onMusicFilterSelected(MusicFilterBean data);

    }


    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    /*滤镜队列*/
    private ArrayList<MusicFilterBean> musicFilterBeans;
    /*当前滤镜选中下标*/
    private int currentFilterIndex;
    /*回调*/
    private MusicFilterListener mMusicFilterListener;

    public MusicFilterDataFactory(int index, MusicFilterListener listener) {
        mMusicFilterListener = listener;
        currentFilterIndex = index;
        musicFilterBeans = MusicFilterSource.buildMusicFilters();
    }

    /**
     * 获取当前滤镜队列下标
     *
     * @return
     */
    @Override
    public int getCurrentFilterIndex() {
        return currentFilterIndex;
    }

    /**
     * 设置当前滤镜队列下标
     *
     * @param currentFilterIndex
     */
    @Override
    public void setCurrentFilterIndex(int currentFilterIndex) {
        this.currentFilterIndex = currentFilterIndex;
    }

    /**
     * 获取滤镜队列
     *
     * @return
     */
    @Override
    public ArrayList<MusicFilterBean> getMusicFilters() {
        return musicFilterBeans;
    }

    /**
     * 切换滤镜
     *
     * @param data
     */
    @Override
    public void onMusicFilterSelected(MusicFilterBean data) {
        mMusicFilterListener.onMusicFilterSelected(data);
        if (data.getPath() != null && data.getPath().trim().length() > 0 && data.getMusic() != null && data.getMusic().trim().length() > 0) {
            MusicFilter musicFilter = new MusicFilter(new FUBundleData(data.getPath()));
            mFURenderKit.setMusicFilter(musicFilter);
        } else {
            mFURenderKit.setMusicFilter(null);
        }
    }


    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        FUAIKit.getInstance().setMaxFaces(4);
        MusicFilterBean musicFilterBean = musicFilterBeans.get(currentFilterIndex);
        onMusicFilterSelected(musicFilterBean);
    }


}
