package com.faceunity.app.data;


import androidx.annotation.NonNull;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.StyleSource;
import com.faceunity.core.controller.facebeauty.FaceBeautyParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.makeup.SimpleMakeup;
import com.faceunity.ui.entity.FaceBeautyBean;
import com.faceunity.ui.entity.ModelAttributeData;
import com.faceunity.ui.entity.StyleBean;
import com.faceunity.ui.infe.AbstractStyleDataFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DESC：风格工厂
 */
public class StyleDataFactory extends AbstractStyleDataFactory {
    //    private FaceBeauty mFaceBeauty;
    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    private FaceBeauty mFaceBeauty = StyleSource.getNewFaceBeauty();
    private FaceBeauty mDefaultFaceBeauty = StyleSource.getDefaultFaceBeauty();
    private SimpleMakeup mSimpleMakeup;
    private StyleSource.StyleData mStyleType;

    public interface StyleListener {
        /**
         * 风格开关
         *
         * @param enable
         */
        void onStyleEnable(boolean enable);
    }

    /*业务回调*/
    private final StyleListener mStyleListener;

    public StyleDataFactory(StyleListener listener) {
        mStyleListener = listener;
        styleTypeIndex();
    }

    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        //设置最大效果人数4
        FUAIKit.getInstance().setMaxFaces(4);
        mFURenderKit.setFaceBeauty(mFaceBeauty);
        styleTypeIndex();
        onStyleSelected(getStyleBeans().get(currentStyleIndex).getKey(), false);
    }

    /**
     * 当前选中风格下标
     */
    private int currentStyleIndex;

    @NonNull
    @Override
    public ArrayList<StyleBean> getStyleBeans() {
        return StyleSource.buildStyleBeans();
    }

    @Override
    public int getCurrentStyleIndex() {
        return currentStyleIndex;
    }

    @Override
    public void setCurrentStyleIndex(int currentStyleIndex) {
        this.currentStyleIndex = currentStyleIndex;
    }

    @Override
    public void onStyleSelected(String name) {
        onStyleSelected(name, true);
    }

    public void onStyleSelected(String name, boolean syncAction) {
        if (name == null) {
            StyleSource.currentStyle = "";
            mStyleType = null;
            mSimpleMakeup = null;
            currentStyleIndex = 0;
            mFaceBeauty.beginCacheAction();
            StyleSource.setFaceBeauty(mDefaultFaceBeauty, mFaceBeauty);
            mFURenderKit.doGLThreadAction(() -> mFaceBeauty.doingUnitCache());
            mFURenderKit.setMakeup(mSimpleMakeup);
        } else {
            mStyleType = StyleSource.getStyleType(name);
            //设置美妆参数
            mSimpleMakeup = mStyleType.simpleMakeup;
            //确保美妆bundle load 完毕，然后同一帧将美妆美颜效果上上去，会有一个问题faceBeauty对应的UI无法及时响应。
            if (syncAction) {
                mFaceBeauty.beginCacheAction();
                //真正上效果由某一阵gl决定
                mFURenderKit.addMakeupLoadListener(() -> mFaceBeauty.doingUnitCache());
            }
            StyleSource.setFaceBeauty(mDefaultFaceBeauty, mFaceBeauty);
            if (mStyleType.faceBeautySkinEnable)
                StyleSource.setFaceBeautySkin(mStyleType.faceBeauty, mFaceBeauty);
            if (mStyleType.faceBeautyShapeEnable)
                StyleSource.setFaceBeautyShape(mStyleType.faceBeauty, mFaceBeauty);
            mFURenderKit.setMakeup(mSimpleMakeup);
        }
    }

    @Override
    public void enableStyle(boolean enable) {
        mStyleListener.onStyleEnable(enable);
    }

    @Override
    public void recoverStyleAllParams() {
        StyleSource.resetAllStyleFaceBeauty();
        styleTypeIndex();
    }

    /**
     * 获取模型参数
     *
     * @param key 名称标识
     * @return 属性值
     */
    @Override
    public double getParamIntensity(@NonNull String key) {
        //理论上调节的时候mFaceBeauty 肯定部位空因为已经选择了风格
        double value = 0.0;
        //美颜
        if (FaceBeautyParam.COLOR_INTENSITY.equals(key))
            value = mFaceBeauty.getColorIntensity();
        else if (FaceBeautyParam.BLUR_INTENSITY.equals(key))
            value = mFaceBeauty.getBlurIntensity();
        else if (FaceBeautyParam.DELSPOT.equals(key))
            value = mFaceBeauty.getDelspotIntensity();
        else if (FaceBeautyParam.RED_INTENSITY.equals(key))
            value = mFaceBeauty.getRedIntensity();
        else if (FaceBeautyParam.CLARITY.equals(key))
            value = mFaceBeauty.getClarityIntensity();
        else if (FaceBeautyParam.SHARPEN_INTENSITY.equals(key))
            value = mFaceBeauty.getSharpenIntensity();
        else if (FaceBeautyParam.EYE_BRIGHT_INTENSITY.equals(key))
            value = mFaceBeauty.getEyeBrightIntensity();
        else if (FaceBeautyParam.TOOTH_WHITEN_INTENSITY.equals(key))
            value = mFaceBeauty.getToothIntensity();
        else if (FaceBeautyParam.REMOVE_POUCH_INTENSITY.equals(key))
            value = mFaceBeauty.getRemovePouchIntensity();
        else if (FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY.equals(key))
            value = mFaceBeauty.getRemoveLawPatternIntensity();
            //美型
        else if (FaceBeautyParam.FACE_SHAPE_INTENSITY.equals(key))
            value = mFaceBeauty.getSharpenIntensity();
        else if (FaceBeautyParam.CHEEK_THINNING_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekThinningIntensity();
        else if (FaceBeautyParam.CHEEK_V_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekVIntensity();
        else if (FaceBeautyParam.CHEEK_LONG_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekLongIntensity();
        else if (FaceBeautyParam.CHEEK_CIRCLE_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekCircleIntensity();
        else if (FaceBeautyParam.CHEEK_NARROW_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekNarrowIntensity();
        else if (FaceBeautyParam.CHEEK_SHORT_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekShortIntensity();
        else if (FaceBeautyParam.CHEEK_SMALL_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekSmallIntensity();
        else if (FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY.equals(key))
            value = mFaceBeauty.getCheekBonesIntensity();
        else if (FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY.equals(key))
            value = mFaceBeauty.getLowerJawIntensity();
        else if (FaceBeautyParam.EYE_ENLARGING_INTENSITY.equals(key))
            value = mFaceBeauty.getEyeEnlargingIntensity();
        else if (FaceBeautyParam.EYE_CIRCLE_INTENSITY.equals(key))
            value = mFaceBeauty.getEyeCircleIntensity();
        else if (FaceBeautyParam.BROW_HEIGHT_INTENSITY.equals(key))
            value = mFaceBeauty.getBrowHeightIntensity();
        else if (FaceBeautyParam.BROW_SPACE_INTENSITY.equals(key))
            value = mFaceBeauty.getBrowSpaceIntensity();
        else if (FaceBeautyParam.INTENSITY_EYE_LID.equals(key))
            value = mFaceBeauty.getEyeLidIntensity();
        else if (FaceBeautyParam.INTENSITY_EYE_HEIGHT.equals(key))
            value = mFaceBeauty.getEyeHeightIntensity();
        else if (FaceBeautyParam.INTENSITY_BROW_THICK.equals(key))
            value = mFaceBeauty.getBrowThickIntensity();
        else if (FaceBeautyParam.INTENSITY_LIP_THICK.equals(key))
            value = mFaceBeauty.getLipThickIntensity();
        else if (FaceBeautyParam.FACE_THREED.equals(key))
            value = mFaceBeauty.getFaceThreeIntensity();
        else if (FaceBeautyParam.CHIN_INTENSITY.equals(key))
            value = mFaceBeauty.getChinIntensity();
        else if (FaceBeautyParam.FOREHEAD_INTENSITY.equals(key))
            value = mFaceBeauty.getForHeadIntensity();
        else if (FaceBeautyParam.NOSE_INTENSITY.equals(key))
            value = mFaceBeauty.getNoseIntensity();
        else if (FaceBeautyParam.MOUTH_INTENSITY.equals(key))
            value = mFaceBeauty.getMouthIntensity();
        else if (FaceBeautyParam.CANTHUS_INTENSITY.equals(key))
            value = mFaceBeauty.getCanthusIntensity();
        else if (FaceBeautyParam.EYE_SPACE_INTENSITY.equals(key))
            value = mFaceBeauty.getEyeSpaceIntensity();
        else if (FaceBeautyParam.EYE_ROTATE_INTENSITY.equals(key))
            value = mFaceBeauty.getEyeRotateIntensity();
        else if (FaceBeautyParam.LONG_NOSE_INTENSITY.equals(key))
            value = mFaceBeauty.getLongNoseIntensity();
        else if (FaceBeautyParam.PHILTRUM_INTENSITY.equals(key))
            value = mFaceBeauty.getPhiltrumIntensity();
        else if (FaceBeautyParam.SMILE_INTENSITY.equals(key))
            value = mFaceBeauty.getSmileIntensity();
        return value;
    }

    /**
     * 设置模型参数
     *
     * @param key   名称标识
     * @param value 属性值
     */
    @Override
    public void updateParamIntensity(@NonNull String key, double value) {
        //美颜
        if (FaceBeautyParam.COLOR_INTENSITY.equals(key))
            mFaceBeauty.setColorIntensity(value);
        else if (FaceBeautyParam.BLUR_INTENSITY.equals(key))
            mFaceBeauty.setBlurIntensity(value);
        else if (FaceBeautyParam.DELSPOT.equals(key))
            mFaceBeauty.setDelspotIntensity(value);
        else if (FaceBeautyParam.RED_INTENSITY.equals(key))
            mFaceBeauty.setRedIntensity(value);
        else if (FaceBeautyParam.CLARITY.equals(key))
            mFaceBeauty.setClarityIntensity(value);
        else if (FaceBeautyParam.SHARPEN_INTENSITY.equals(key))
            mFaceBeauty.setSharpenIntensity(value);
        else if (FaceBeautyParam.EYE_BRIGHT_INTENSITY.equals(key))
            mFaceBeauty.setEyeBrightIntensity(value);
        else if (FaceBeautyParam.TOOTH_WHITEN_INTENSITY.equals(key))
            mFaceBeauty.setToothIntensity(value);
        else if (FaceBeautyParam.REMOVE_POUCH_INTENSITY.equals(key))
            mFaceBeauty.setRemovePouchIntensity(value);
        else if (FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY.equals(key))
            mFaceBeauty.setRemoveLawPatternIntensity(value);
            //美型
        else if (FaceBeautyParam.FACE_SHAPE_INTENSITY.equals(key))
            mFaceBeauty.setSharpenIntensity(value);
        else if (FaceBeautyParam.CHEEK_THINNING_INTENSITY.equals(key))
            mFaceBeauty.setCheekThinningIntensity(value);
        else if (FaceBeautyParam.CHEEK_V_INTENSITY.equals(key))
            mFaceBeauty.setCheekVIntensity(value);
        else if (FaceBeautyParam.CHEEK_LONG_INTENSITY.equals(key))
            mFaceBeauty.setCheekLongIntensity(value);
        else if (FaceBeautyParam.CHEEK_CIRCLE_INTENSITY.equals(key))
            mFaceBeauty.setCheekCircleIntensity(value);
        else if (FaceBeautyParam.CHEEK_NARROW_INTENSITY.equals(key))
            mFaceBeauty.setCheekNarrowIntensity(value);
        else if (FaceBeautyParam.CHEEK_SHORT_INTENSITY.equals(key))
            mFaceBeauty.setCheekShortIntensity(value);
        else if (FaceBeautyParam.CHEEK_SMALL_INTENSITY.equals(key))
            mFaceBeauty.setCheekSmallIntensity(value);
        else if (FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY.equals(key))
            mFaceBeauty.setCheekBonesIntensity(value);
        else if (FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY.equals(key))
            mFaceBeauty.setLowerJawIntensity(value);
        else if (FaceBeautyParam.EYE_ENLARGING_INTENSITY.equals(key))
            mFaceBeauty.setEyeEnlargingIntensity(value);
        else if (FaceBeautyParam.EYE_CIRCLE_INTENSITY.equals(key))
            mFaceBeauty.setEyeCircleIntensity(value);
        else if (FaceBeautyParam.BROW_HEIGHT_INTENSITY.equals(key))
            mFaceBeauty.setBrowHeightIntensity(value);
        else if (FaceBeautyParam.BROW_SPACE_INTENSITY.equals(key))
            mFaceBeauty.setBrowSpaceIntensity(value);
        else if (FaceBeautyParam.INTENSITY_EYE_LID.equals(key))
            mFaceBeauty.setEyeLidIntensity(value);
        else if (FaceBeautyParam.INTENSITY_EYE_HEIGHT.equals(key))
            mFaceBeauty.setEyeHeightIntensity(value);
        else if (FaceBeautyParam.INTENSITY_BROW_THICK.equals(key))
            mFaceBeauty.setBrowThickIntensity(value);
        else if (FaceBeautyParam.INTENSITY_LIP_THICK.equals(key))
            mFaceBeauty.setLipThickIntensity(value);
        else if (FaceBeautyParam.FACE_THREED.equals(key))
            mFaceBeauty.setFaceThreeIntensity(value);
        else if (FaceBeautyParam.CHIN_INTENSITY.equals(key))
            mFaceBeauty.setChinIntensity(value);
        else if (FaceBeautyParam.FOREHEAD_INTENSITY.equals(key))
            mFaceBeauty.setForHeadIntensity(value);
        else if (FaceBeautyParam.NOSE_INTENSITY.equals(key))
            mFaceBeauty.setNoseIntensity(value);
        else if (FaceBeautyParam.MOUTH_INTENSITY.equals(key))
            mFaceBeauty.setMouthIntensity(value);
        else if (FaceBeautyParam.CANTHUS_INTENSITY.equals(key))
            mFaceBeauty.setCanthusIntensity(value);
        else if (FaceBeautyParam.EYE_SPACE_INTENSITY.equals(key))
            mFaceBeauty.setEyeSpaceIntensity(value);
        else if (FaceBeautyParam.EYE_ROTATE_INTENSITY.equals(key))
            mFaceBeauty.setEyeRotateIntensity(value);
        else if (FaceBeautyParam.LONG_NOSE_INTENSITY.equals(key))
            mFaceBeauty.setLongNoseIntensity(value);
        else if (FaceBeautyParam.PHILTRUM_INTENSITY.equals(key))
            mFaceBeauty.setPhiltrumIntensity(value);
        else if (FaceBeautyParam.SMILE_INTENSITY.equals(key))
            mFaceBeauty.setSmileIntensity(value);

        //将这些参数设置到对应的style持有的faceBeauty
        if (mStyleType != null && mStyleType.faceBeauty != null)
            StyleSource.setFaceBeauty(mFaceBeauty, mStyleType.faceBeauty);
    }

    /**
     * 获取美妆强度
     *
     * @return
     */
    @Override
    public double getMakeupIntensity() {
        double makeIntensity = 0.0;
        if (mSimpleMakeup != null)
            makeIntensity = mSimpleMakeup.getMakeupIntensity();
        return makeIntensity;
    }

    /**
     * 更新美妆强度
     *
     * @param value
     */
    @Override
    public void updateMakeupParamIntensity(double value) {
        if (mSimpleMakeup != null)
            mSimpleMakeup.setMakeupIntensity(value);
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

    /*模型映射获取模型值*/
    HashMap<String, FaceBeautyDataFactory.FaceBeautyGetParamRelevanceInterface> faceBeautyRelevanceGetMapping = new HashMap<String, FaceBeautyDataFactory.FaceBeautyGetParamRelevanceInterface>() {
        {
            put(FaceBeautyParam.ENABLE_SKIN_SEG, mFaceBeauty::getEnableSkinSeg);
        }
    };

    HashMap<String, FaceBeautyDataFactory.FaceBeautySetParamRelevanceInterface> faceBeautyRelevanceSetMapping = new HashMap<String, FaceBeautyDataFactory.FaceBeautySetParamRelevanceInterface>() {
        {
            put(FaceBeautyParam.ENABLE_SKIN_SEG, mFaceBeauty::setEnableSkinSeg);
        }
    };

    /**
     * 获取滤镜强度
     *
     * @return
     */
    @Override
    public double getFilterIntensity() {
        double filterIntensity = 0.0;
        if (mSimpleMakeup != null)
            filterIntensity = mSimpleMakeup.getFilterIntensity();
        return filterIntensity;
    }

    /**
     * 设置滤镜强度
     *
     * @param value
     */
    @Override
    public void updateFilterParamIntensity(double value) {
        if (mSimpleMakeup != null) {
            mSimpleMakeup.setFilterIntensity(value);
        }
    }

    @Override
    public ArrayList<FaceBeautyBean> getSkinBeauty() {
        return StyleSource.buildSkinParams();
    }

    @Override
    public ArrayList<FaceBeautyBean> getShapeBeauty() {
        return StyleSource.buildShapeParams();
    }

    /**
     * 获取美肤、美型扩展参数
     *
     * @return
     */
    @NonNull
    @Override
    public HashMap<String, ModelAttributeData> getModelAttributeRange() {
        return StyleSource.buildModelAttributeRange();
    }

    /**
     * 是否打开美肤
     */
    @Override
    public void enableFaceBeautySkin(boolean faceBeautySkinEnable) {
        mStyleType.faceBeautySkinEnable = faceBeautySkinEnable;
        enableFaceBeautySkinOrShape();
    }

    /**
     * 是否打开美型
     */
    @Override
    public void enableFaceBeautyShape(boolean faceBeautyShapeEnable) {
        mStyleType.faceBeautyShapeEnable = faceBeautyShapeEnable;
        enableFaceBeautySkinOrShape();
    }

    /**
     * 设置美肤or美型无效过
     */
    private void enableFaceBeautySkinOrShape() {
        mFaceBeauty.beginCacheAction();
        if (!mStyleType.faceBeautySkinEnable) {
            //美肤无效
            StyleSource.setFaceBeautySkin(mDefaultFaceBeauty, mFaceBeauty);
        } else {
            //美肤生效
            StyleSource.setFaceBeautySkin(mStyleType.faceBeauty, mFaceBeauty);
        }
        if (!mStyleType.faceBeautyShapeEnable) {
            //美型无效
            StyleSource.setFaceBeautyShape(mDefaultFaceBeauty, mFaceBeauty);
        } else {
            //美型生效
            StyleSource.setFaceBeautyShape(mStyleType.faceBeauty, mFaceBeauty);
        }
        mFURenderKit.doGLThreadAction(() -> {
            mFaceBeauty.doingUnitCache();
        });
    }

    /**
     * 获取当前美肤功能是否开启
     *
     * @return
     */
    public boolean getCurrentStyleSkinEnable() {
        boolean skinEnable = true;
        if (mStyleType != null) {
            skinEnable = mStyleType.faceBeautySkinEnable;
        }
        return skinEnable;
    }

    /**
     * 获取当前美型功能是否开启
     *
     * @return
     */
    public boolean getCurrentStyleShapeEnable() {
        boolean shapeEnable = true;
        if (mStyleType != null) {
            shapeEnable = mStyleType.faceBeautyShapeEnable;
        }
        return shapeEnable;
    }

    @Override
    public boolean checkStyleRecover() {
        if (currentStyleIndex != 1) {
            return false;
        } else {
            Iterator<Map.Entry<String, StyleSource.StyleData>> iterator = StyleSource.styleType.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, StyleSource.StyleData> entry = iterator.next();
                //判断是否开启美颜美型按钮
                StyleSource.StyleData currentStyleData = entry.getValue();
                //判断美妆滤镜值
                StyleSource.StyleData styleData = StyleSource.buildDefaultStyleParams(entry.getKey(), new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION)));
                if (!currentStyleData.equals(styleData))
                    return false;
            }
        }

        return true;
    }

    /**
     * 根据风格选中一个角标
     */
    public void styleTypeIndex() {
        currentStyleIndex = StyleSource.styleTypeIndex();
    }
}
