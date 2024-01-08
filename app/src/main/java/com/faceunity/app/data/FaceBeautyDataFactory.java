package com.faceunity.app.data;

import androidx.annotation.NonNull;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.FaceBeautySource;
import com.faceunity.core.controller.facebeauty.FaceBeautyParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.core.model.prop.expression.ExpressionRecognition;
import com.faceunity.ui.entity.FaceBeautyBean;
import com.faceunity.ui.entity.FaceBeautyFilterBean;
import com.faceunity.ui.entity.ModelAttributeData;
import com.faceunity.ui.infe.AbstractFaceBeautyDataFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * DESC：美颜业务工厂
 * Created on 2021/3/1
 */
public class FaceBeautyDataFactory extends AbstractFaceBeautyDataFactory {

    public interface FaceBeautyListener {
        /**
         * 风格切换
         *
         * @param res
         */
        void onFilterSelected(int res);

        /**
         * 美颜开关
         *
         * @param enable
         */
        void onFaceBeautyEnable(boolean enable);
    }

    interface FaceBeautySetParamInterface {
        /**
         * 设置属性值
         *
         * @param value
         */
        void setValue(double value);
    }

    interface FaceBeautySetParamRelevanceInterface {
        /**
         * 设置属性值
         *
         * @param value
         */
        void setValue(boolean value);
    }

    interface FaceBeautyGetParamRelevanceInterface {
        /**
         * 获取属性值
         */
        boolean getValue();
    }

    interface FaceBeautyGetParamInterface {
        /**
         * 获取属性值
         *
         * @return
         */
        double getValue();
    }


    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();

    /*美颜缓存数据模型 用于普通美颜*/
    public static final FaceBeauty defaultFaceBeauty = FaceBeautySource.getDefaultFaceBeauty();

    /*当前生效美颜数据模型 普通 or 风格的*/
    public static FaceBeauty faceBeauty = defaultFaceBeauty;


    /*默认滤镜选中下标*/
    private int currentFilterIndex = 0;
    /*业务回调*/
    private final FaceBeautyListener mFaceBeautyListener;


    public FaceBeautyDataFactory(FaceBeautyListener listener) {
        mFaceBeautyListener = listener;
    }


    /**
     * 获取美肤参数列表
     *
     * @return
     */
    @NonNull
    @Override
    public ArrayList<FaceBeautyBean> getSkinBeauty() {
        return FaceBeautySource.buildSkinParams();
    }

    /**
     * 获取美型参数列表
     *
     * @return
     */
    @NonNull
    @Override
    public ArrayList<FaceBeautyBean> getShapeBeauty() {
        return FaceBeautySource.buildShapeParams();
    }

    /**
     * 获取美型参数列表
     *
     * @return
     */
    @NonNull
    @Override
    public ArrayList<FaceBeautyBean> getShapeBeautySubItem() {
        return FaceBeautySource.buildFaceShapeSubItemParams();
    }


    /**
     * 获取美肤、美型扩展参数
     *
     * @return
     */
    @NonNull
    @Override
    public HashMap<String, ModelAttributeData> getModelAttributeRange() {
        return FaceBeautySource.buildModelAttributeRange();
    }


    /**
     * 获取滤镜参数列表
     *
     * @return
     */
    @NonNull
    @Override
    public ArrayList<FaceBeautyFilterBean> getBeautyFilters() {
        ArrayList<FaceBeautyFilterBean> filterBeans = FaceBeautySource.buildFilters();
        for (int i = 0; i < filterBeans.size(); i++) {
            if (filterBeans.get(i).getKey().equals(defaultFaceBeauty.getFilterName())) {
                filterBeans.get(i).setIntensity(defaultFaceBeauty.getFilterIntensity());
                currentFilterIndex = i;
            }
        }
        return filterBeans;
    }

    /**
     * 获取当前滤镜下标
     *
     * @return
     */
    @Override
    public int getCurrentFilterIndex() {
        return currentFilterIndex;
    }

    /**
     * 设置当前滤镜下标
     *
     * @param currentFilterIndex
     */
    @Override
    public void setCurrentFilterIndex(int currentFilterIndex) {
        this.currentFilterIndex = currentFilterIndex;
    }

    /**
     * 美颜开关设置
     *
     * @param enable
     */
    @Override
    public void enableFaceBeauty(boolean enable) {
        mFaceBeautyListener.onFaceBeautyEnable(enable);
    }

    /**
     * 获取模型参数
     *
     * @param key 名称标识
     * @return 属性值
     */
    @Override
    public double getParamIntensity(@NonNull String key) {
        if (faceBeautyGetMapping.containsKey(key)) {
            return faceBeautyGetMapping.get(key).getValue();
        }
        return 0.0;
    }

    /**
     * 设置模型参数
     *
     * @param key   名称标识
     * @param value 属性值
     */
    @Override
    public void updateParamIntensity(@NonNull String key, double value) {
        if (faceBeautySetMapping.containsKey(key)) {
            faceBeautySetMapping.get(key).setValue(value);
        }
        if (faceBeautyRelevanceSetMapping.containsKey(key)) {
            faceBeautyRelevanceSetMapping.get(key).setValue(value == 1);
        }
    }

    /**
     * 将所有效果制空
     */
    @Override
    public void resetParamIntensity() {
        if (faceBeauty != defaultFaceBeauty) {
            faceBeauty = defaultFaceBeauty;
            FURenderKit.getInstance().setFaceBeauty(faceBeauty);
        }

        ArrayList<FaceBeautyBean> skinBeauty = getSkinBeauty();
        ArrayList<FaceBeautyBean> shapeBeauty = getShapeBeauty();
        HashMap<String, ModelAttributeData> modelAttributeRange = getModelAttributeRange();

        //还原美肤
        for (FaceBeautyBean faceBeautyBean : skinBeauty) {
            String key = faceBeautyBean.getKey();
            ModelAttributeData modelAttributeData = modelAttributeRange.get(key);
            updateParamIntensity(key, modelAttributeData.getStand());
        }

        //还原美型
        for (FaceBeautyBean faceBeautyBean : shapeBeauty) {
            String key = faceBeautyBean.getKey();
            ModelAttributeData modelAttributeData = modelAttributeRange.get(key);
            updateParamIntensity(key, modelAttributeData.getStand());
        }

        //还原滤镜
        defaultFaceBeauty.setFilterName(FaceBeautyFilterEnum.ORIGIN);
        defaultFaceBeauty.setFilterIntensity(0.0);
        setCurrentFilterIndex(0);
    }

    @Override
    public String getCurrentOneHotFaceShape() {
        return CurrentFaceShapeUIValue.currentFaceShape == null ? FaceBeautyParam.CHEEK_V_INTENSITY : CurrentFaceShapeUIValue.currentFaceShape;
    }

    @Override
    public void setCurrentOneHotFaceShape(String faceShape) {
        CurrentFaceShapeUIValue.currentFaceShape = faceShape;
    }


    /**
     * 设置当前脸型的UI值
     */
    public void setCurrentFaceShapeUIValue(HashMap<String, Double> hashMap) {
        CurrentFaceShapeUIValue.currentFaceShapeValue.clear();
        CurrentFaceShapeUIValue.currentFaceShapeValue.putAll(hashMap);
    }

    /**
     * 获取当前脸型的UI值
     */
    public HashMap<String, Double> getCurrentFaceShapeUIValue() {
        return CurrentFaceShapeUIValue.currentFaceShapeValue;
    }

    /**
     * 切换滤镜
     *
     * @param name      滤镜名称标识
     * @param intensity 滤镜强度
     * @param resID     滤镜名称
     */
    @Override
    public void onFilterSelected(@NonNull String name, double intensity, int resID) {
        defaultFaceBeauty.setFilterName(name);
        defaultFaceBeauty.setFilterIntensity(intensity);
        mFaceBeautyListener.onFilterSelected(resID);
    }

    /**
     * 更换滤镜强度
     *
     * @param intensity 滤镜强度
     */
    @Override
    public void updateFilterIntensity(double intensity) {
        defaultFaceBeauty.setFilterIntensity(intensity);
    }

    @Override
    public int getParamRelevanceSelectedType(@NonNull String key) {
        if (faceBeautyRelevanceGetMapping != null && faceBeautyRelevanceGetMapping.containsKey(key)) {
            return faceBeautyRelevanceGetMapping.get(key).getValue() ? 1 : 0;
        }
        return 0;
    }

    @Override
    public void updateParamRelevanceType(@NonNull String key, int type) {
        if (faceBeautyRelevanceSetMapping.containsKey(key)) {
            faceBeautyRelevanceSetMapping.get(key).setValue(type == 1);
        }
    }

    /*模型映射设置模型值*/
    private final HashMap<String, FaceBeautySetParamInterface> faceBeautySetMapping = new HashMap<String, FaceBeautySetParamInterface>() {{
        put(FaceBeautyParam.COLOR_INTENSITY, defaultFaceBeauty::setColorIntensity);
        put(FaceBeautyParam.BLUR_INTENSITY, defaultFaceBeauty::setBlurIntensity);
        put(FaceBeautyParam.RED_INTENSITY, defaultFaceBeauty::setRedIntensity);
        put(FaceBeautyParam.SHARPEN_INTENSITY, defaultFaceBeauty::setSharpenIntensity);
        put(FaceBeautyParam.EYE_BRIGHT_INTENSITY, defaultFaceBeauty::setEyeBrightIntensity);
        put(FaceBeautyParam.TOOTH_WHITEN_INTENSITY, defaultFaceBeauty::setToothIntensity);
        put(FaceBeautyParam.REMOVE_POUCH_INTENSITY, defaultFaceBeauty::setRemovePouchIntensity);
        put(FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, defaultFaceBeauty::setRemoveLawPatternIntensity);
        put(FaceBeautyParam.FACE_THREED, defaultFaceBeauty::setFaceThreeIntensity);
        put(FaceBeautyParam.CLARITY, defaultFaceBeauty::setClarityIntensity);
        put(FaceBeautyParam.DELSPOT, defaultFaceBeauty::setDelspotIntensity);
        /*美型*/
        put(FaceBeautyParam.FACE_SHAPE_INTENSITY, defaultFaceBeauty::setFaceShapeIntensity);
        put(FaceBeautyParam.CHEEK_THINNING_INTENSITY, defaultFaceBeauty::setCheekThinningIntensity);
        put(FaceBeautyParam.CHEEK_V_INTENSITY, defaultFaceBeauty::setCheekVIntensity);
        put(FaceBeautyParam.CHEEK_LONG_INTENSITY, defaultFaceBeauty::setCheekLongIntensity);
        put(FaceBeautyParam.CHEEK_CIRCLE_INTENSITY, defaultFaceBeauty::setCheekCircleIntensity);
        put(FaceBeautyParam.CHEEK_NARROW_INTENSITY, defaultFaceBeauty::setCheekNarrowIntensity);
        put(FaceBeautyParam.CHEEK_SHORT_INTENSITY, defaultFaceBeauty::setCheekShortIntensity);
        put(FaceBeautyParam.CHEEK_SMALL_INTENSITY, defaultFaceBeauty::setCheekSmallIntensity);
        put(FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, defaultFaceBeauty::setCheekBonesIntensity);
        put(FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, defaultFaceBeauty::setLowerJawIntensity);
        put(FaceBeautyParam.EYE_ENLARGING_INTENSITY, defaultFaceBeauty::setEyeEnlargingIntensity);
        put(FaceBeautyParam.EYE_CIRCLE_INTENSITY, defaultFaceBeauty::setEyeCircleIntensity);
        put(FaceBeautyParam.BROW_HEIGHT_INTENSITY, defaultFaceBeauty::setBrowHeightIntensity);
        put(FaceBeautyParam.BROW_SPACE_INTENSITY, defaultFaceBeauty::setBrowSpaceIntensity);
        put(FaceBeautyParam.INTENSITY_EYE_LID, defaultFaceBeauty::setEyeLidIntensity);
        put(FaceBeautyParam.INTENSITY_EYE_HEIGHT, defaultFaceBeauty::setEyeHeightIntensity);
        put(FaceBeautyParam.INTENSITY_BROW_THICK, defaultFaceBeauty::setBrowThickIntensity);
        put(FaceBeautyParam.INTENSITY_LIP_THICK, defaultFaceBeauty::setLipThickIntensity);
        put(FaceBeautyParam.CHIN_INTENSITY, defaultFaceBeauty::setChinIntensity);
        put(FaceBeautyParam.FOREHEAD_INTENSITY, defaultFaceBeauty::setForHeadIntensity);
        put(FaceBeautyParam.NOSE_INTENSITY, defaultFaceBeauty::setNoseIntensity);
        put(FaceBeautyParam.MOUTH_INTENSITY, defaultFaceBeauty::setMouthIntensity);
        put(FaceBeautyParam.CANTHUS_INTENSITY, defaultFaceBeauty::setCanthusIntensity);
        put(FaceBeautyParam.EYE_SPACE_INTENSITY, defaultFaceBeauty::setEyeSpaceIntensity);
        put(FaceBeautyParam.EYE_ROTATE_INTENSITY, defaultFaceBeauty::setEyeRotateIntensity);
        put(FaceBeautyParam.LONG_NOSE_INTENSITY, defaultFaceBeauty::setLongNoseIntensity);
        put(FaceBeautyParam.PHILTRUM_INTENSITY, defaultFaceBeauty::setPhiltrumIntensity);
        put(FaceBeautyParam.SMILE_INTENSITY, defaultFaceBeauty::setSmileIntensity);
    }};

    /*模型映射获取模型值*/
    HashMap<String, FaceBeautyGetParamInterface> faceBeautyGetMapping = new HashMap<String, FaceBeautyGetParamInterface>() {
        {
            put(FaceBeautyParam.COLOR_INTENSITY, defaultFaceBeauty::getColorIntensity);
            put(FaceBeautyParam.BLUR_INTENSITY, defaultFaceBeauty::getBlurIntensity);
            put(FaceBeautyParam.RED_INTENSITY, defaultFaceBeauty::getRedIntensity);
            put(FaceBeautyParam.SHARPEN_INTENSITY, defaultFaceBeauty::getSharpenIntensity);
            put(FaceBeautyParam.EYE_BRIGHT_INTENSITY, defaultFaceBeauty::getEyeBrightIntensity);
            put(FaceBeautyParam.TOOTH_WHITEN_INTENSITY, defaultFaceBeauty::getToothIntensity);
            put(FaceBeautyParam.REMOVE_POUCH_INTENSITY, defaultFaceBeauty::getRemovePouchIntensity);
            put(FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, defaultFaceBeauty::getRemoveLawPatternIntensity);
            put(FaceBeautyParam.FACE_THREED, defaultFaceBeauty::getFaceThreeIntensity);
            put(FaceBeautyParam.CLARITY, defaultFaceBeauty::getClarityIntensity);
            put(FaceBeautyParam.DELSPOT, defaultFaceBeauty::getDelspotIntensity);
            /*美型*/
            put(FaceBeautyParam.FACE_SHAPE_INTENSITY, defaultFaceBeauty::getFaceShapeIntensity);
            put(FaceBeautyParam.CHEEK_THINNING_INTENSITY, defaultFaceBeauty::getCheekThinningIntensity);
            put(FaceBeautyParam.CHEEK_V_INTENSITY, defaultFaceBeauty::getCheekVIntensity);
            put(FaceBeautyParam.CHEEK_LONG_INTENSITY, defaultFaceBeauty::getCheekLongIntensity);
            put(FaceBeautyParam.CHEEK_CIRCLE_INTENSITY, defaultFaceBeauty::getCheekCircleIntensity);
            put(FaceBeautyParam.CHEEK_NARROW_INTENSITY, defaultFaceBeauty::getCheekNarrowIntensity);
            put(FaceBeautyParam.CHEEK_SHORT_INTENSITY, defaultFaceBeauty::getCheekShortIntensity);
            put(FaceBeautyParam.CHEEK_SMALL_INTENSITY, defaultFaceBeauty::getCheekSmallIntensity);
            put(FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, defaultFaceBeauty::getCheekBonesIntensity);
            put(FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, defaultFaceBeauty::getLowerJawIntensity);
            put(FaceBeautyParam.EYE_ENLARGING_INTENSITY, defaultFaceBeauty::getEyeEnlargingIntensity);
            put(FaceBeautyParam.EYE_CIRCLE_INTENSITY, defaultFaceBeauty::getEyeCircleIntensity);
            put(FaceBeautyParam.BROW_HEIGHT_INTENSITY, defaultFaceBeauty::getBrowHeightIntensity);
            put(FaceBeautyParam.BROW_SPACE_INTENSITY, defaultFaceBeauty::getBrowSpaceIntensity);
            put(FaceBeautyParam.INTENSITY_EYE_LID, defaultFaceBeauty::getEyeLidIntensity);
            put(FaceBeautyParam.INTENSITY_EYE_HEIGHT, defaultFaceBeauty::getEyeHeightIntensity);
            put(FaceBeautyParam.INTENSITY_BROW_THICK, defaultFaceBeauty::getBrowThickIntensity);
            put(FaceBeautyParam.INTENSITY_LIP_THICK, defaultFaceBeauty::getLipThickIntensity);
            put(FaceBeautyParam.CHIN_INTENSITY, defaultFaceBeauty::getChinIntensity);
            put(FaceBeautyParam.FOREHEAD_INTENSITY, defaultFaceBeauty::getForHeadIntensity);
            put(FaceBeautyParam.NOSE_INTENSITY, defaultFaceBeauty::getNoseIntensity);
            put(FaceBeautyParam.MOUTH_INTENSITY, defaultFaceBeauty::getMouthIntensity);
            put(FaceBeautyParam.CANTHUS_INTENSITY, defaultFaceBeauty::getCanthusIntensity);
            put(FaceBeautyParam.EYE_SPACE_INTENSITY, defaultFaceBeauty::getEyeSpaceIntensity);
            put(FaceBeautyParam.EYE_ROTATE_INTENSITY, defaultFaceBeauty::getEyeRotateIntensity);
            put(FaceBeautyParam.LONG_NOSE_INTENSITY, defaultFaceBeauty::getLongNoseIntensity);
            put(FaceBeautyParam.PHILTRUM_INTENSITY, defaultFaceBeauty::getPhiltrumIntensity);
            put(FaceBeautyParam.SMILE_INTENSITY, defaultFaceBeauty::getSmileIntensity);

        }
    };


    /*模型映射获取模型值*/
    HashMap<String, FaceBeautyGetParamRelevanceInterface> faceBeautyRelevanceGetMapping = new HashMap<String, FaceBeautyGetParamRelevanceInterface>() {
        {
            put(FaceBeautyParam.ENABLE_SKIN_SEG, defaultFaceBeauty::getEnableSkinSeg);
        }
    };

    HashMap<String, FaceBeautySetParamRelevanceInterface> faceBeautyRelevanceSetMapping = new HashMap<String, FaceBeautySetParamRelevanceInterface>() {
        {
            put(FaceBeautyParam.ENABLE_SKIN_SEG, defaultFaceBeauty::setEnableSkinSeg);
        }
    };


    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.setFaceBeauty(faceBeauty);
        FUAIKit.getInstance().setMaxFaces(4);
        if (DemoConfig.IS_OPEN_LAND_MARK) {
            ExpressionRecognition expressionRecognition = new ExpressionRecognition(new FUBundleData(DemoConfig.BUNDLE_LANDMARKS));
            expressionRecognition.setLandmarksType(FUAITypeEnum.FUAITYPE_FACELANDMARKS239);
            mFURenderKit.getPropContainer().addProp(expressionRecognition);
        }
    }

    /**
     * 用于记录当前脸型的UI值 -> 用于用户下次点入的时候恢复
     */
    static class CurrentFaceShapeUIValue {
        /* 当前生效的脸型 */
        public static String currentFaceShape = FaceBeautyParam.CHEEK_V_INTENSITY;
        /* 当前脸型的UI值 */
        public static HashMap<String, Double> currentFaceShapeValue = new HashMap<>();
    }
}
