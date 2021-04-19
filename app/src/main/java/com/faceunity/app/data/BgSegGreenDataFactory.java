package com.faceunity.app.data;


import androidx.annotation.NonNull;

import com.faceunity.app.data.source.BgSegGreenSource;
import com.faceunity.core.controller.bgSegGreen.BgSegGreenParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.bgSegGreen.BgSegGreen;
import com.faceunity.app.DemoConfig;
import com.faceunity.ui.entity.BgSegGreenBackgroundBean;
import com.faceunity.ui.entity.BgSegGreenBean;
import com.faceunity.ui.entity.ModelAttributeData;
import com.faceunity.ui.infe.AbstractBgSegGreenDataFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * DESC：绿幕抠像业务工厂
 * Created on 2021/3/4
 */
public class BgSegGreenDataFactory extends AbstractBgSegGreenDataFactory {


    public interface BgSegGreenListener {
        /**
         * 取色状态回调
         *
         * @param isSelected 是否选中
         * @param color      默认颜色
         */
        void onColorPickerStateChanged(boolean isSelected, int color);

        /**
         * 切换背景道具
         *
         * @param bean
         */
        void onBackgroundSelected(BgSegGreenBackgroundBean bean);
    }


    /*渲染控制器*/
    private final FURenderKit mFURenderKit = FURenderKit.getInstance();

    /*绿幕抠像特效模型*/
    private final BgSegGreen bgSegGreen;

    /*绿幕抠像背景列表*/
    private final ArrayList<BgSegGreenBackgroundBean> bgSegGreenBackgroundBeans;
    /* 绿幕抠像当前背景下标 */
    private int currentBackgroundIndex;

    /* 回调 */
    private final BgSegGreenListener mBgSegGreenListener;


    /**
     * 构造绿幕抠像
     *
     * @param listener 回调
     * @param index    背景下标
     */
    public BgSegGreenDataFactory(BgSegGreenListener listener, int index) {
        mBgSegGreenListener = listener;
        bgSegGreen = BgSegGreenSource.buildBgSegGreen();
        bgSegGreenBackgroundBeans = BgSegGreenSource.buildBgSegGreenBackground();
        currentBackgroundIndex = index;
    }


    /**
     * 获取绿幕抠像当前背景下标
     *
     * @return
     */
    @Override

    public int getBackgroundIndex() {
        return currentBackgroundIndex;
    }

    /**
     * 设置绿幕抠像当前背景下标
     *
     * @param backgroundIndex
     */
    @Override
    public void setBackgroundIndex(int backgroundIndex) {
        this.currentBackgroundIndex = backgroundIndex;
    }


    /**
     * 获取绿幕抠像项目数据扩展模型
     *
     * @return
     */
    @Override
    public HashMap<String, ModelAttributeData> getModelAttributeRange() {
        return BgSegGreenSource.buildModelAttributeRange();
    }

    /**
     * 获取绿幕抠像功能列表
     *
     * @return
     */
    @Override
    public ArrayList<BgSegGreenBean> getBgSegGreenActions() {
        return BgSegGreenSource.buildBgSegGreenAction();
    }

    /**
     * 获取绿幕抠像背景列表
     *
     * @return
     */
    @Override
    public ArrayList<BgSegGreenBackgroundBean> getBgSegGreenBackgrounds() {
        return bgSegGreenBackgroundBeans;
    }

    /**
     * 背景图片变更
     *
     * @param data BgSegGreenBackgroundBean
     */
    @Override
    public void onBackgroundSelected(BgSegGreenBackgroundBean data) {
        mBgSegGreenListener.onBackgroundSelected(data);
    }

    /**
     * 取色锚点颜色变更
     *
     * @param array DoubleArray
     */
    @Override
    public void onColorRGBChanged(double[] array) {
        bgSegGreen.setColorRGB(new FUColorRGBData(array[0], array[1], array[2]));
        bgSegGreen.setEnable(true);
    }

    /**
     * 绿幕开关
     *
     * @param enable Boolean
     */
    @Override
    public void onBgSegGreenEnableChanged(boolean enable) {
        bgSegGreen.setEnable(enable);
    }


    /**
     * 根据名称标识获取对应的值
     *
     * @param key String  标识
     * @return Double  值
     */
    @Override
    public double getParamIntensity(@NonNull String key) {
        if (bgSegGreenGetMapping.containsKey(key)) {
            return bgSegGreenGetMapping.get(key).getValue();
        }
        return 0.0;
    }

    /**
     * 根据名称标识更新对应的值
     *
     * @param key String  标识
     * @return Double  值
     */
    @Override
    public void updateParamIntensity(@NonNull String key, double value) {
        if (bgSegGreenSetMapping.containsKey(key)) {
            Objects.requireNonNull(bgSegGreenSetMapping.get(key)).setValue(value);
        }
    }

    /**
     * 调用取色器功能状态变更
     *
     * @param selected
     * @param color
     */
    @Override
    public void onColorPickerStateChanged(boolean selected, int color) {
        mBgSegGreenListener.onColorPickerStateChanged(selected, color);
    }

    //region 业务映射

    /**
     * 参数设置
     */
    interface BgSegGreenSetParam {
        void setValue(double value);
    }

    /**
     * 模型参数获取
     */
    interface BgSegGreenGetParam {
        double getValue();
    }

    /**
     * 获取当前绿幕对象
     *
     * @return
     */
    private BgSegGreen getCurrentBgSegGreenModel() {
        return bgSegGreen;
    }


    /* 模型映射 */
    private final HashMap<String, BgSegGreenSetParam> bgSegGreenSetMapping = new HashMap<String, BgSegGreenSetParam>() {
        {
            put(BgSegGreenParam.SIMILARITY, value -> getCurrentBgSegGreenModel().setSimilarity(value));
            put(BgSegGreenParam.SMOOTHNESS, value -> getCurrentBgSegGreenModel().setSmoothness(value));
            put(BgSegGreenParam.TRANSPARENCY, value -> getCurrentBgSegGreenModel().setTransparency(value));
        }
    };

    /*模型映射获取模型值*/
    private final HashMap<String, BgSegGreenGetParam> bgSegGreenGetMapping = new HashMap<String, BgSegGreenGetParam>() {
        {
            put(BgSegGreenParam.SIMILARITY, () -> getCurrentBgSegGreenModel().getSimilarity());
            put(BgSegGreenParam.SMOOTHNESS, () -> getCurrentBgSegGreenModel().getSmoothness());
            put(BgSegGreenParam.TRANSPARENCY, () -> getCurrentBgSegGreenModel().getTransparency());
        }
    };

    //endregion 业务映射


    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.getFUAIController().setMaxFaces(1);
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mFURenderKit.setBgSegGreen(bgSegGreen);
    }

}
