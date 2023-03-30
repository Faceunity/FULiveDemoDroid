package com.faceunity.app.data;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.HairBeautySource;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.hairBeauty.HairBeautyGradient;
import com.faceunity.core.model.hairBeauty.HairBeautyNormal;
import com.faceunity.ui.entity.HairBeautyBean;
import com.faceunity.ui.infe.AbstractHairBeautyDataFactory;

import java.util.ArrayList;

/**
 * DESC：美发业务工厂
 * Created on 2021/3/3
 */
public class HairBeautyDataFactory extends AbstractHairBeautyDataFactory {

    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    private FUAIKit mFUAIKit = FUAIKit.getInstance();
    /*普通美发数据模型*/
    private HairBeautyNormal normalHair;
    /*渐变美发数据模型*/
    private HairBeautyGradient gradientHair;

    /*美发队列*/
    private ArrayList<HairBeautyBean> hairBeautyBeans;

    /*当前选中美发下标*/
    private int currentHairIndex;


    public HairBeautyDataFactory(int index) {
        currentHairIndex = index;
        normalHair = new HairBeautyNormal(new FUBundleData(DemoConfig.BUNDLE_HAIR_NORMAL));
        gradientHair = new HairBeautyGradient(new FUBundleData(DemoConfig.BUNDLE_HAIR_GRADIENT));
        hairBeautyBeans = HairBeautySource.buildHairBeautyBeans();
    }


    /**
     * 获取美发队列
     *
     * @return
     */
    @Override
    public ArrayList<HairBeautyBean> getHairBeautyBeans() {
        return hairBeautyBeans;
    }

    /**
     * 获取当前美发下标
     *
     * @return
     */
    @Override
    public int getCurrentHairIndex() {
        return currentHairIndex;
    }

    /**
     * 设置当前美发下标
     *
     * @param currentHairIndex
     */
    @Override
    public void setCurrentHairIndex(int currentHairIndex) {
        this.currentHairIndex = currentHairIndex;
    }


    /**
     * 当前美发强度变更
     *
     * @param intensity
     */
    @Override
    public void onHairIntensityChanged(double intensity) {
        if (mFURenderKit.getHairBeauty() != null) {
            mFURenderKit.getHairBeauty().setHairIntensity(intensity);
        }
    }

    /**
     * 切换美发
     *
     * @param data
     */
    @Override
    public void onHairSelected(HairBeautyBean data) {
        if (data.getType() == -1) {
            mFURenderKit.setHairBeauty(null);
        } else if (data.getType() == 0) {
            gradientHair.setHairIndex(data.getIndex());
            gradientHair.setHairIntensity(data.getIntensity());
            if (gradientHair != mFURenderKit.getHairBeauty()) {
                mFURenderKit.setHairBeauty(gradientHair);
            }
        } else {
            normalHair.setHairIndex(data.getIndex());
            normalHair.setHairIntensity(data.getIntensity());
            if (normalHair != mFURenderKit.getHairBeauty()) {
                mFURenderKit.setHairBeauty(normalHair);
            }
        }
    }


    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mFUAIKit.setMaxFaces(4);
        HairBeautyBean hairBeautyBean = hairBeautyBeans.get(currentHairIndex);
        onHairSelected(hairBeautyBean);
    }
}
