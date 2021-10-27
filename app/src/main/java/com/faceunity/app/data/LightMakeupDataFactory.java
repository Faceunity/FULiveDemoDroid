package com.faceunity.app.data;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.FaceBeautySource;
import com.faceunity.app.data.source.LightMakeupSource;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.expression.ExpressionRecognition;
import com.faceunity.ui.entity.LightMakeupBean;
import com.faceunity.ui.infe.AbstractLightMakeupDataFactory;

import java.util.ArrayList;


/**
 * DESC：轻美妆业务工厂
 * Created on 2021/3/3
 */
public class LightMakeupDataFactory extends AbstractLightMakeupDataFactory {


    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();


    /* 轻美妆队列 */
    private ArrayList<LightMakeupBean> lightMakeupBeans;
    /* 当前轻美妆选中下标  */
    private int currentLightMakeupIndex;


    public LightMakeupDataFactory(int index) {
        currentLightMakeupIndex = index;
        lightMakeupBeans = LightMakeupSource.buildLightMakeup();
    }

    /**
     * 获取轻美妆队列
     *
     * @return
     */
    @Override
    public ArrayList<LightMakeupBean> getLightMakeUpBeans() {
        return lightMakeupBeans;
    }

    /**
     * 获取轻美妆下标
     *
     * @return
     */
    @Override
    public int getCurrentLightMakeupIndex() {
        return currentLightMakeupIndex;
    }

    /**
     * 设置轻美妆下标
     *
     * @param currentLightMakeupIndex
     */
    @Override
    public void setCurrentLightMakeupIndex(int currentLightMakeupIndex) {
        this.currentLightMakeupIndex = currentLightMakeupIndex;

    }


    /**
     * 切换轻美妆
     *
     * @param data
     */
    @Override
    public void onLightMakeupSelected(LightMakeupBean data) {
        if (data.getKey() == null) {
            mFURenderKit.setLightMakeup(null);
        } else {
            Runnable runnable = LightMakeupSource.LightMakeupParams.get(data.getKey());
            if (runnable != null) {
                runnable.run();
            }
            onLightMakeupIntensityChanged(data.getIntensity());
        }
        if (mFURenderKit.getFaceBeauty() != null) {
            mFURenderKit.getFaceBeauty().setFilterName(data.getFilterName());
            mFURenderKit.getFaceBeauty().setFilterIntensity(data.getFilterIntensity());
        }
    }


    /**
     * 修改强度
     *
     * @param intensity
     */
    @Override
    public void onLightMakeupIntensityChanged(double intensity) {
        if (mFURenderKit.getLightMakeup() != null) {
            mFURenderKit.getLightMakeup().setMakeupIntensity(intensity);
        }
        if (mFURenderKit.getFaceBeauty() != null) {
            mFURenderKit.getFaceBeauty().setFilterIntensity(intensity);
        }
    }


    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.setFaceBeauty(FaceBeautySource.clone(FaceBeautyDataFactory.faceBeauty));
        FUAIKit.getInstance().setMaxFaces(4);
        LightMakeupBean lightMakeupBean = lightMakeupBeans.get(currentLightMakeupIndex);
        onLightMakeupSelected(lightMakeupBean);

        if (DemoConfig.IS_OPEN_LAND_MARK) {
            ExpressionRecognition expressionRecognition =  new ExpressionRecognition(new FUBundleData("others/landmarks.bundle"));
            expressionRecognition.setLandmarksType(FUAITypeEnum.FUAITYPE_FACELANDMARKS239);
            mFURenderKit.getPropContainer().addProp(expressionRecognition);
        }
    }

}