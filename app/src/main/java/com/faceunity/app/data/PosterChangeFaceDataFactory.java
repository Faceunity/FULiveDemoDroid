package com.faceunity.app.data;

import com.faceunity.app.data.source.PosterChangeFaceSource;
import com.faceunity.app.view.PosterPreviewActivity;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.app.DemoApplication;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.view.PosterPreviewActivity;
import com.faceunity.ui.entity.PosterBean;
import com.faceunity.ui.infe.AbstractPosterChangeFaceDataFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class PosterChangeFaceDataFactory extends AbstractPosterChangeFaceDataFactory {


    public interface PosterChangeFaceListener {
        /**
         * 切换海报
         *
         * @param data
         */
        void onItemSelectedChange(PosterBean data);
    }

    /*海报队列*/
    private ArrayList<PosterBean> posterBeans;
    /*当前海报对应下标*/
    private int mCurrentPosterIndex;
    /*回调接口*/
    private PosterChangeFaceListener mPosterChangeFaceListener;


    public PosterChangeFaceDataFactory(String template, PosterChangeFaceListener listener) {
        mPosterChangeFaceListener = listener;
        posterBeans = PosterChangeFaceSource.buildPoster();
        for (int i = 0; i < posterBeans.size(); i++) {
            if (posterBeans.get(i).getPath().equals(template)) {
                mCurrentPosterIndex = i;
                break;
            }
        }
    }


    /**
     * 获取当前海报下标
     *
     * @return
     */
    @Override
    public int getCurrentPosterIndex() {
        return mCurrentPosterIndex;
    }

    /**
     * 设置当前海报下标
     *
     * @param currentPosterIndex
     */
    @Override
    public void setCurrentPosterIndex(int currentPosterIndex) {
        this.mCurrentPosterIndex = currentPosterIndex;
    }


    @Override
    public ArrayList<PosterBean> getPosters() {
        return posterBeans;
    }


    @Override
    public void onItemSelectedChange(PosterBean data) {
        mPosterChangeFaceListener.onItemSelectedChange(data);
    }


}
