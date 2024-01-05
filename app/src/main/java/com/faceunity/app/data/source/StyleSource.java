package com.faceunity.app.data.source;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.data.disksource.style.FUDiskFaceStyleUtils;
import com.faceunity.app.data.disksource.style.FUDiskStyleData;
import com.faceunity.app.utils.FuDeviceUtils;
import com.faceunity.core.controller.facebeauty.FaceBeautyParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUFaceBeautyMultiModePropertyEnum;
import com.faceunity.core.enumeration.FUFaceBeautyPropertyModeEnum;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.makeup.SimpleMakeup;
import com.faceunity.ui.entity.FaceBeautyBean;
import com.faceunity.ui.entity.ModelAttributeData;
import com.faceunity.ui.entity.StyleBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * DESC：风格数据
 * Created on 2022/11/08
 */
public class StyleSource {
    public static final String CONFIG_TEMPERAMENT = "temperament";//气质
    public static final String CONFIG_SENIOR_SISTER = "senior_sister";//学姐
    public static final String CONFIG_ZYFZ = "zyfz";//智雅
    public static final String CONFIG_PALE_COMPLEXION = "pale_complexion";//淡颜
    public static final String CONFIG_TEXTURE = "texture";//质感
    public static final String CONFIG_KOREAN_SCHOOLGIRLS = "korean_schoolgirls";//学妹
    public static final String CONFIG_ALZ = "alz";//爱凌
    public static final String CONFIG_PRIMITIVE = "primitive";//原生
    public static final String CONFIG_CLASSIC = "classic";//经典
    public static final String CONFIG_GODDESS = "goddess";//女神
    public static final String CONFIG_MALE_DEITY = "male_deity";//男神

    public static final String CONFIG_HNADSOME = "handsome";
    public static final String CONFIG_NATURA = "natura";
    public static final String CONFIG_PURE_DESIRE = "pure_desire";
    public static final String CONFIG_MILK_FEROCIOUS = "milk_ferocious";
    public static final String makeupBundleDir = DemoConfig.MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR;

    //当前选中的风格
    public static final String defaultStyle = CONFIG_TEMPERAMENT;
    public static String currentStyle = defaultStyle;

    private static FaceBeauty defaultFaceBeauty;

    /**
     * 获取默认的美颜beauty
     *
     * @return FaceBeauty
     */
    public static FaceBeauty getDefaultFaceBeauty() {
        if (defaultFaceBeauty != null) {
            return defaultFaceBeauty;
        }
        defaultFaceBeauty = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
        if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
            setFaceBeautyPropertyMode(defaultFaceBeauty);
        }
        return defaultFaceBeauty;
    }

    /**
     * 获取默认的美颜beauty
     *
     * @return FaceBeauty
     */
    public static FaceBeauty getNewFaceBeauty() {
        FaceBeauty newFaceBeauty = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
        if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
            setFaceBeautyPropertyMode(newFaceBeauty);
        }
        return newFaceBeauty;
    }

    /**
     * 风格数据构造
     *
     * @return ArrayList<StyleBean>
     */
    public static ArrayList<StyleBean> buildStyleBeans() {
        ArrayList<StyleBean> styleBeans = new ArrayList<>();
        styleBeans.add(new StyleBean(null, R.string.beauty_face_style_none, R.mipmap.icon_control_none, R.string.beauty_face_style_none));
        styleBeans.add(new StyleBean(CONFIG_TEMPERAMENT, R.string.style_temperament, R.mipmap.icon_style_temperament, R.string.style_temperament));//气质
        styleBeans.add(new StyleBean(CONFIG_SENIOR_SISTER, R.string.style_senior_sister, R.mipmap.icon_style_senior_sister, R.string.style_senior_sister));//学姐
        styleBeans.add(new StyleBean(CONFIG_ZYFZ, R.string.style_zyfz, R.mipmap.icon_style_zyfz, R.string.style_zyfz));//智雅
        styleBeans.add(new StyleBean(CONFIG_PALE_COMPLEXION, R.string.style_pale_complexion, R.mipmap.icon_style_pale_complexion, R.string.style_pale_complexion));//淡颜
        styleBeans.add(new StyleBean(CONFIG_TEXTURE, R.string.style_texture, R.mipmap.icon_style_texture, R.string.style_texture));//质感
        styleBeans.add(new StyleBean(CONFIG_KOREAN_SCHOOLGIRLS, R.string.style_korean_schoolgirls, R.mipmap.icon_style_korean_schoolgirls, R.string.style_korean_schoolgirls));//韩国学妹
        styleBeans.add(new StyleBean(CONFIG_ALZ, R.string.style_alz, R.mipmap.icon_style_alz, R.string.style_alz));//爱凌
        styleBeans.add(new StyleBean(CONFIG_PRIMITIVE, R.string.style_primitive, R.mipmap.icon_style_primitive, R.string.style_primitive));//原生
        styleBeans.add(new StyleBean(CONFIG_CLASSIC, R.string.style_classic, R.mipmap.icon_style_classic, R.string.style_classic));//经典
        styleBeans.add(new StyleBean(CONFIG_GODDESS, R.string.style_goddess, R.mipmap.icon_style_goddess, R.string.style_goddess));//女神
        styleBeans.add(new StyleBean(CONFIG_MALE_DEITY, R.string.style_male_deity, R.mipmap.icon_style_male_deity, R.string.style_male_deity));//男神

//        styleBeans.add(new StyleBean(CONFIG_HNADSOME, R.string.style_handsome, R.mipmap.icon_style_handsome, R.string.style_handsome));
//        styleBeans.add(new StyleBean(CONFIG_NATURA, R.string.style_natura, R.mipmap.icon_style_natura, R.string.style_natura));
//        styleBeans.add(new StyleBean(CONFIG_PURE_DESIRE, R.string.style_pure_desire, R.mipmap.icon_style_pure_desire, R.string.style_pure_desire));
//        styleBeans.add(new StyleBean(CONFIG_MILK_FEROCIOUS, R.string.style_milk_feroucious, R.mipmap.icon_style_milk_ferocious, R.string.style_milk_feroucious));

        return styleBeans;
    }

    /**
     * 根据风格选中一个角标
     *
     * @return int
     */
    public static int styleTypeIndex() {
        int currentStyleIndex;
        if (StyleSource.CONFIG_TEMPERAMENT.equals(StyleSource.currentStyle)) {//气质
            currentStyleIndex = 1;
        } else if (StyleSource.CONFIG_SENIOR_SISTER.equals(StyleSource.currentStyle)) {//学姐
            currentStyleIndex = 2;
        } else if (StyleSource.CONFIG_ZYFZ.equals(StyleSource.currentStyle)) {//智雅
            currentStyleIndex = 3;
        } else if (StyleSource.CONFIG_PALE_COMPLEXION.equals(StyleSource.currentStyle)) {//淡颜
            currentStyleIndex = 4;
        } else if (StyleSource.CONFIG_TEXTURE.equals(StyleSource.currentStyle)) {//质感
            currentStyleIndex = 5;
        } else if (StyleSource.CONFIG_KOREAN_SCHOOLGIRLS.equals(StyleSource.currentStyle)) {//韩国学妹
            currentStyleIndex = 6;
        } else if (StyleSource.CONFIG_ALZ.equals(StyleSource.currentStyle)) {//爱凌
            currentStyleIndex = 7;
        } else if (StyleSource.CONFIG_PRIMITIVE.equals(StyleSource.currentStyle)) {//原生
            currentStyleIndex = 8;
        } else if (StyleSource.CONFIG_CLASSIC.equals(StyleSource.currentStyle)) {//经典
            currentStyleIndex = 9;
        } else if (StyleSource.CONFIG_GODDESS.equals(StyleSource.currentStyle)) {//女神
            currentStyleIndex = 10;
        } else if (StyleSource.CONFIG_MALE_DEITY.equals(StyleSource.currentStyle)) {//男神
            currentStyleIndex = 11;
        } else {
            currentStyleIndex = 0;
        }
        return currentStyleIndex;
    }

    /**
     * 当前所有的风格配置
     */
    public static HashMap<String, StyleData> styleType = new HashMap<String, StyleData>() {
        {
            put(CONFIG_TEMPERAMENT, buildStyleFaceBeauty(CONFIG_TEMPERAMENT));//气质
            put(CONFIG_SENIOR_SISTER, buildStyleFaceBeauty(CONFIG_SENIOR_SISTER));//学姐
            put(CONFIG_ZYFZ, buildStyleFaceBeauty(CONFIG_ZYFZ));//智雅
            put(CONFIG_PALE_COMPLEXION, buildStyleFaceBeauty(CONFIG_PALE_COMPLEXION));//淡颜
            put(CONFIG_TEXTURE, buildStyleFaceBeauty(CONFIG_TEXTURE));//质感
            put(CONFIG_KOREAN_SCHOOLGIRLS, buildStyleFaceBeauty(CONFIG_KOREAN_SCHOOLGIRLS));//韩国学妹
            put(CONFIG_ALZ, buildStyleFaceBeauty(CONFIG_ALZ));//爱凌
            put(CONFIG_PRIMITIVE, buildStyleFaceBeauty(CONFIG_PRIMITIVE));//原生
            put(CONFIG_CLASSIC, buildStyleFaceBeauty(CONFIG_CLASSIC));//经典
            put(CONFIG_GODDESS, buildStyleFaceBeauty(CONFIG_GODDESS));//女神
            put(CONFIG_MALE_DEITY, buildStyleFaceBeauty(CONFIG_MALE_DEITY));//男神
        }
    };

    /**
     * 还原所有风格参数
     */
    public static void resetAllStyleFaceBeauty() {
//        FUDiskFaceStyleUtils.removeStyleData();
        Iterator<Map.Entry<String, StyleData>> iterator = styleType.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, StyleData> entry = iterator.next();
            StyleData styleData = resetStyleFaceBeauty(entry.getValue().faceBeauty, entry.getKey());
            entry.setValue(styleData);
        }
        currentStyle = defaultStyle;
    }

    /**
     * 还原单个风格参数
     *
     * @param faceBeauty FaceBeauty
     * @param styleName  String
     * @return StyleData
     */
    public static StyleData resetStyleFaceBeauty(FaceBeauty faceBeauty, String styleName) {
        //先将所有参数项置标准
        setFaceBeauty(getDefaultFaceBeauty(), faceBeauty);
        //再将所有参数项置该风格默认值
        StyleData styleData = buildDefaultStyleParams(styleName, faceBeauty);
        styleData.faceBeautySkinEnable = true;
        styleData.faceBeautyShapeEnable = true;
        return styleData;
    }

    /**
     * 构建推荐的风格配置
     *
     * @param styleName String
     * @return StyleData
     */
    public static StyleData buildStyleFaceBeauty(String styleName) {
        StyleData styleData = new StyleData();
        FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
        //设置美颜各属性模式
        if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
            setFaceBeautyPropertyMode(model);
        }
        SimpleMakeup simpleMakeup;
        //查询有没有本地缓存
        HashMap<String, FUDiskStyleData> fuDiskStyleDataArrayList = loadStyleFromDisk();
        if (fuDiskStyleDataArrayList != null && fuDiskStyleDataArrayList.get(styleName) != null) {
            FUDiskStyleData fuDiskStyleData = fuDiskStyleDataArrayList.get(styleName);
            //美肤
            model.setBlurIntensity(fuDiskStyleData.blurIntensity);
            model.setDelspotIntensity(fuDiskStyleData.delspotIntensity);
            model.setColorIntensity(fuDiskStyleData.colorIntensity);
            model.setRedIntensity(fuDiskStyleData.redIntensity);
            model.setClarityIntensity(fuDiskStyleData.clarityIntensity);
            model.setSharpenIntensity(fuDiskStyleData.sharpenIntensity);
            model.setEyeBrightIntensity(fuDiskStyleData.eyeBrightIntensity);
            model.setToothIntensity(fuDiskStyleData.toothIntensity);
            model.setRemovePouchIntensity(fuDiskStyleData.removePouchIntensity);
            model.setRemoveLawPatternIntensity(fuDiskStyleData.removeLawPatternIntensity);
            //美型
            model.setCheekThinningIntensity(fuDiskStyleData.cheekThinningIntensity);
            model.setCheekVIntensity(fuDiskStyleData.cheekVIntensity);
            model.setCheekNarrowIntensity(fuDiskStyleData.cheekNarrowIntensity);
            model.setCheekShortIntensity(fuDiskStyleData.cheekShortIntensity);
            model.setCheekSmallIntensity(fuDiskStyleData.cheekSmallIntensity);
            model.setCheekBonesIntensity(fuDiskStyleData.cheekBonesIntensity);
            model.setLowerJawIntensity(fuDiskStyleData.lowerJawIntensity);
            model.setEyeEnlargingIntensity(fuDiskStyleData.eyeEnlargingIntensity);
            model.setEyeCircleIntensity(fuDiskStyleData.eyeCircleIntensity);
            model.setChinIntensity(fuDiskStyleData.chinIntensity);
            model.setForHeadIntensity(fuDiskStyleData.forHeadIntensity);
            model.setNoseIntensity(fuDiskStyleData.noseIntensity);
            model.setMouthIntensity(fuDiskStyleData.mouthIntensity);
            model.setCanthusIntensity(fuDiskStyleData.canthusIntensity);
            model.setEyeSpaceIntensity(fuDiskStyleData.eyeSpaceIntensity);
            model.setEyeRotateIntensity(fuDiskStyleData.eyeRotateIntensity);
            model.setLongNoseIntensity(fuDiskStyleData.longNoseIntensity);
            model.setPhiltrumIntensity(fuDiskStyleData.philtrumIntensity);
            model.setSmileIntensity(fuDiskStyleData.smileIntensity);
            model.setBrowHeightIntensity(fuDiskStyleData.browHeightIntensity);
            model.setBrowSpaceIntensity(fuDiskStyleData.browSpaceIntensity);
            model.setEyeLidIntensity(fuDiskStyleData.eyeLidIntensity);
            model.setEyeHeightIntensity(fuDiskStyleData.eyeHeightIntensity);
            model.setBrowThickIntensity(fuDiskStyleData.browThickIntensity);
            model.setLipThickIntensity(fuDiskStyleData.lipThickIntensity);
            model.setFaceThreeIntensity(fuDiskStyleData.faceThreeIntensity);
            //美妆 + 滤镜
            simpleMakeup = new SimpleMakeup(new FUBundleData(fuDiskStyleData.makeupPath));
            simpleMakeup.setMachineLevel(DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE);//更新设备等级去设置是否开启人脸遮挡
            simpleMakeup.setFilterIntensity(fuDiskStyleData.filterIntensity);
            simpleMakeup.setMakeupIntensity(fuDiskStyleData.makeupIntensity);
            styleData.faceBeauty = model;
            styleData.simpleMakeup = simpleMakeup;
            styleData.faceBeautySkinEnable = fuDiskStyleData.faceBeautySkinEnable;
            styleData.faceBeautyShapeEnable = fuDiskStyleData.faceBeautyShapeEnable;
        } else {
            styleData = buildDefaultStyleParams(styleName, model);
        }

        return styleData;
    }

    /**
     * 构建默认的风格参数
     * 嘴唇厚度 眼睛位置 眼睑下至 眉毛上下 眉间距 眉毛粗细
     *
     * @param styleName String
     * @param model     FaceBeauty
     * @return StyleData
     */
    public static StyleData buildDefaultStyleParams(String styleName, FaceBeauty model) {
        StyleData styleData = new StyleData();
        SimpleMakeup simpleMakeup;
        if (CONFIG_TEMPERAMENT.equals(styleName)) {//气质
            model.setBlurIntensity(3.0);
            model.setEyeBrightIntensity(0.2);
            model.setColorIntensity(0.3);
            model.setRemoveLawPatternIntensity(0.3);
            model.setRemovePouchIntensity(0.5);
            model.setFaceThreeIntensity(1.0);
            model.setCheekVIntensity(0.5);
            model.setNoseIntensity(0.25);
            model.setCanthusIntensity(0.45);
            model.setEyeEnlargingIntensity(0.35);
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "qizhi.bundle"));
            simpleMakeup.setFilterIntensity(0.8);
            simpleMakeup.setMakeupIntensity(0.8);
        } else if (CONFIG_SENIOR_SISTER.equals(styleName)) {//学姐
            model.setBlurIntensity(2.7);
            model.setEyeBrightIntensity(0.3);
            model.setRemovePouchIntensity(0.7);
            model.setRemoveLawPatternIntensity(0.5);
            model.setFaceThreeIntensity(0.6);
            model.setCheekSmallIntensity(0.5);
            model.setCheekBonesIntensity(0.2);
            model.setEyeEnlargingIntensity(0.25);
            model.setNoseIntensity(0.3);
            model.setCheekVIntensity(0.3);
            model.setChinIntensity(0.6);
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
                model.setLipThickIntensity(0.75);
            }
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "xuejie.bundle"));
            simpleMakeup.setFilterIntensity(0.7);
            simpleMakeup.setMakeupIntensity(0.8);
        } else if (CONFIG_ZYFZ.equals(styleName)) {//智雅仿妆
            model.setColorIntensity(0.6);
            model.setBlurIntensity(3.9);
            model.setEyeBrightIntensity(0.3);
            model.setRemoveLawPatternIntensity(0.3);
            model.setRemovePouchIntensity(0.7);
            model.setFaceThreeIntensity(0.5);
            model.setCheekSmallIntensity(0.5);
            model.setCheekShortIntensity(0.3);
            model.setNoseIntensity(0.3);
            model.setForHeadIntensity(0.15);
            model.setEyeEnlargingIntensity(0.3);
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
                model.setLipThickIntensity(0.65);
                model.setEyeLidIntensity(0.75);
            }
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "zhiya.bundle"));
            simpleMakeup.setFilterIntensity(0.7);
            simpleMakeup.setMakeupIntensity(0.85);
        } else if (CONFIG_PALE_COMPLEXION.equals(styleName)) {//淡颜
            model.setColorIntensity(0.4);
            model.setBlurIntensity(1.8);
            model.setRemovePouchIntensity(0.5);
            model.setEyeBrightIntensity(0.3);
            model.setRemoveLawPatternIntensity(0.3);
            model.setCheekThinningIntensity(0.4);
            model.setEyeEnlargingIntensity(0.3);
            model.setCheekSmallIntensity(0.2);
            model.setNoseIntensity(0.2);
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
                model.setLipThickIntensity(0.7);
            }
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "danyan.bundle"));
            simpleMakeup.setFilterIntensity(0.8);
            simpleMakeup.setMakeupIntensity(0.8);
        } else if (CONFIG_TEXTURE.equals(styleName)) {//质感
            model.setBlurIntensity(3.6);
            model.setFaceThreeIntensity(0.4);
            model.setColorIntensity(0.25);
            model.setEyeBrightIntensity(0.25);
            model.setRemoveLawPatternIntensity(0.4);
            model.setRemovePouchIntensity(0.6);
            model.setCheekThinningIntensity(0.3);
            model.setCheekVIntensity(0.3);
            model.setEyeEnlargingIntensity(0.4);
            model.setCanthusIntensity(0.5);
            model.setNoseIntensity(0.35);
            model.setForHeadIntensity(0.15);
            model.setSmileIntensity(0.4);
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
                model.setLipThickIntensity(0.65);
            }
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "zhigan.bundle"));
            simpleMakeup.setFilterIntensity(0.8);
            simpleMakeup.setMakeupIntensity(0.9);
        } else if (CONFIG_KOREAN_SCHOOLGIRLS.equals(styleName)) {//韩国学妹
            model.setColorIntensity(0.1);
            model.setBlurIntensity(3.0);
            model.setEyeBrightIntensity(0.4);
            model.setRemovePouchIntensity(0.7);
            model.setRemoveLawPatternIntensity(0.3);
            model.setFaceThreeIntensity(0.6);
            model.setEyeEnlargingIntensity(0.3);
            model.setCheekSmallIntensity(0.3);
            model.setNoseIntensity(0.25);
            model.setForHeadIntensity(0.2);
            model.setCheekNarrowIntensity(0.3);
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
                model.setLipThickIntensity(0.65);
            }
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "hanguoxuemei.bundle"));
            simpleMakeup.setFilterIntensity(0.8);
            simpleMakeup.setMakeupIntensity(0.8);
        } else if (CONFIG_ALZ.equals(styleName)) {//爱凌妆
            model.setColorIntensity(0.2);
            model.setBlurIntensity(1.8);
            model.setEyeBrightIntensity(0.3);
            model.setRemoveLawPatternIntensity(0.3);
            model.setRemovePouchIntensity(0.6);
            model.setFaceThreeIntensity(0.6);
            model.setCheekSmallIntensity(0.45);
            model.setNoseIntensity(0.3);
            model.setEyeEnlargingIntensity(0.3);
            model.setCheekNarrowIntensity(0.1);
            model.setCheekThinningIntensity(0.35);
            model.setChinIntensity(0.8);
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE) {
                model.setLipThickIntensity(0.65);
            }
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "ailing.bundle"));
            simpleMakeup.setFilterIntensity(0.8);
            simpleMakeup.setMakeupIntensity(0.8);
        } else if (CONFIG_PRIMITIVE.equals(styleName)) {//原生
            model.setBlurIntensity(1.8);
            model.setSharpenIntensity(0.2);
            model.setToothIntensity(0.2);
            model.setRemovePouchIntensity(0.45);
            model.setRemoveLawPatternIntensity(0.35);
            model.setFaceThreeIntensity(0.5);
            model.setEyeBrightIntensity(0.3);
            model.setCheekThinningIntensity(0.3);
            model.setEyeEnlargingIntensity(0.2);
            model.setCheekBonesIntensity(0.3);
            model.setNoseIntensity(0.2);
            model.setForHeadIntensity(0.4);
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "yuansheng.bundle"));
            simpleMakeup.setFilterIntensity(1.0);
            simpleMakeup.setMakeupIntensity(0.8);
        } else if (CONFIG_CLASSIC.equals(styleName)) {//经典
            model.setBlurIntensity(3.3);
            model.setSharpenIntensity(0.2);
            model.setFaceThreeIntensity(0.5);
            model.setToothIntensity(0.2);
            model.setRemovePouchIntensity(0.5);
            model.setRemoveLawPatternIntensity(0.3);
            model.setCheekThinningIntensity(0.6);
            model.setEyeEnlargingIntensity(0.3);
            model.setCheekBonesIntensity(0.2);
            model.setNoseIntensity(0.4);
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "jingdian.bundle"));
            simpleMakeup.setFilterIntensity(1.0);
            simpleMakeup.setMakeupIntensity(0.4);
        } else if (CONFIG_GODDESS.equals(styleName)) {//女神
            model.setBlurIntensity(3.9);
            model.setSharpenIntensity(0.2);
            model.setColorIntensity(0.1);
            model.setFaceThreeIntensity(0.5);
            model.setToothIntensity(0.2);
            model.setRemovePouchIntensity(0.55);
            model.setRemoveLawPatternIntensity(0.3);
            model.setCheekThinningIntensity(0.4);
            model.setEyeEnlargingIntensity(0.35);
            model.setCheekBonesIntensity(0.25);
            model.setNoseIntensity(0.35);
            model.setForHeadIntensity(0.35);
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "nvshen.bundle"));
            simpleMakeup.setFilterIntensity(1.0);
            simpleMakeup.setMakeupIntensity(0.5);
        } else if (CONFIG_MALE_DEITY.equals(styleName)) {//男神
            model.setBlurIntensity(2.7);
            model.setEyeBrightIntensity(0.3);
            model.setSharpenIntensity(0.3);
            model.setColorIntensity(0.1);
            model.setToothIntensity(0.15);
            model.setRemovePouchIntensity(0.4);
            model.setRemoveLawPatternIntensity(0.5);
            model.setFaceThreeIntensity(0.6);
            model.setCheekThinningIntensity(0.2);
            model.setCheekNarrowIntensity(0.3);
            model.setEyeEnlargingIntensity(0.25);
            model.setCheekBonesIntensity(0.1);
            model.setNoseIntensity(0.35);
            model.setMouthIntensity(0.55);
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "nanshen.bundle"));
            simpleMakeup.setFilterIntensity(0.7);
            simpleMakeup.setMakeupIntensity(0.6);
        } else {//默认气质
            model.setBlurIntensity(3.0);
            model.setEyeBrightIntensity(0.2);
            model.setColorIntensity(0.3);
            model.setRemoveLawPatternIntensity(0.3);
            model.setRemovePouchIntensity(0.5);
            model.setFaceThreeIntensity(1.0);
            model.setCheekVIntensity(0.5);
            model.setNoseIntensity(0.25);
            model.setCanthusIntensity(0.45);
            model.setEyeEnlargingIntensity(0.35);
            simpleMakeup = new SimpleMakeup(new FUBundleData(makeupBundleDir + "qizhi.bundle"));
            simpleMakeup.setFilterIntensity(0.8);
            simpleMakeup.setMakeupIntensity(0.8);
        }
        simpleMakeup.setMachineLevel(DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE);//更新设备等级去设置是否开启人脸遮挡
        styleData.faceBeauty = model;
        styleData.simpleMakeup = simpleMakeup;
        return styleData;
    }

    /**
     * 获取某项最新的风格配置
     *
     * @param styleName String
     * @return StyleData
     */
    public static StyleData getStyleType(String styleName) {
        currentStyle = styleName;
        return styleType.get(styleName);
    }

    /**
     * 初始化美肤参数
     *
     * @return ArrayList<FaceBeautyBean>
     */
    public static ArrayList<FaceBeautyBean> buildSkinParams() {
        ArrayList<FaceBeautyBean> params = new ArrayList<>();
        params.add(new FaceBeautyBean(
                        FaceBeautyParam.BLUR_INTENSITY, R.string.beauty_box_heavy_blur_fine,
                        R.drawable.icon_beauty_skin_buffing_close_selector, R.drawable.icon_beauty_skin_buffing_open_selector
                )
        );
        params.add(new FaceBeautyBean(
                        FaceBeautyParam.DELSPOT, R.string.beauty_box_delspot,
                        R.drawable.icon_beauty_skin_delspot_close_selector, R.drawable.icon_beauty_skin_delspot_open_selector, DemoConfig.DEVICE_LEVEL >= FuDeviceUtils.DEVICE_LEVEL_THREE && !FuDeviceUtils.judgeFunctionInBlackList(FaceBeautyParam.DELSPOT)
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.COLOR_INTENSITY, R.string.beauty_box_color_level,
                        R.drawable.icon_beauty_skin_color_close_selector, R.drawable.icon_beauty_skin_color_open_selector, true,
                        FaceBeautyParam.ENABLE_SKIN_SEG, true, DemoConfig.DEVICE_LEVEL >= FuDeviceUtils.DEVICE_LEVEL_FOUR,
                        R.string.beauty_skin_seg_type_overall, R.string.beauty_skin_seg_type_skin, R.string.beauty_skin_seg_type_tips
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.RED_INTENSITY, R.string.beauty_box_red_level,
                        R.drawable.icon_beauty_skin_red_close_selector, R.drawable.icon_beauty_skin_red_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CLARITY, R.string.beauty_box_clarity,
                        R.drawable.icon_beauty_skin_clarity_close_selector, R.drawable.icon_beauty_skin_clarity_open_selector)
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.SHARPEN_INTENSITY, R.string.beauty_box_sharpen,
                        R.drawable.icon_beauty_skin_sharpen_close_selector, R.drawable.icon_beauty_skin_sharpen_open_selector
                )
        );
        //五官立体
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.FACE_THREED, R.string.beauty_face_three,
                        R.drawable.icon_beauty_skin_face_three_close_selector, R.drawable.icon_beauty_skin_face_three_open_selector)
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_BRIGHT_INTENSITY, R.string.beauty_box_eye_bright,
                        R.drawable.icon_beauty_skin_eyes_bright_close_selector, R.drawable.icon_beauty_skin_eyes_bright_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.TOOTH_WHITEN_INTENSITY, R.string.beauty_box_tooth_whiten,
                        R.drawable.icon_beauty_skin_teeth_close_selector, R.drawable.icon_beauty_skin_teeth_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.REMOVE_POUCH_INTENSITY, R.string.beauty_micro_pouch,
                        R.drawable.icon_beauty_skin_dark_circles_close_selector, R.drawable.icon_beauty_skin_dark_circles_open_selector)
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, R.string.beauty_micro_nasolabial,
                        R.drawable.icon_beauty_skin_wrinkle_close_selector, R.drawable.icon_beauty_skin_wrinkle_open_selector)
        );

        return params;
    }

    /**
     * 初始化美型参数
     *
     * @return ArrayList<FaceBeautyBean>
     */
    public static ArrayList<FaceBeautyBean> buildShapeParams() {
        ArrayList<FaceBeautyBean> params = new ArrayList<>();
        //瘦脸
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_THINNING_INTENSITY, R.string.beauty_box_cheek_thinning,
                        R.drawable.icon_beauty_shape_face_cheekthin_close_selector, R.drawable.icon_beauty_shape_face_cheekthin_open_selector
                )
        );

        //V脸
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_V_INTENSITY, R.string.beauty_box_cheek_v,
                        R.drawable.icon_beauty_shape_face_v_close_selector, R.drawable.icon_beauty_shape_face_v_open_selector
                )
        );

        //窄脸
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_NARROW_INTENSITY, R.string.beauty_box_cheek_narrow,
                        R.drawable.icon_beauty_shape_face_narrow_close_selector, R.drawable.icon_beauty_shape_face_narrow_open_selector
                )
        );

        //小脸 -> 短脸  --使用的参数是以前小脸的
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_SHORT_INTENSITY, R.string.beauty_box_cheek_short,
                        R.drawable.icon_beauty_shape_face_short_close_selector, R.drawable.icon_beauty_shape_face_short_open_selector
                )
        );

        //小脸 -> 新增
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_SMALL_INTENSITY, R.string.beauty_box_cheek_small,
                        R.drawable.icon_beauty_shape_face_little_close_selector, R.drawable.icon_beauty_shape_face_little_open_selector
                )
        );
        //瘦颧骨
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, R.string.beauty_box_cheekbones,
                        R.drawable.icon_beauty_shape_cheek_bones_close_selector, R.drawable.icon_beauty_shape_cheek_bones_open_selector
                )
        );

        //瘦下颌骨
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, R.string.beauty_box_lower_jaw,
                        R.drawable.icon_beauty_shape_lower_jaw_close_selector, R.drawable.icon_beauty_shape_lower_jaw_open_selector
                )
        );

        //大眼
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_ENLARGING_INTENSITY, R.string.beauty_box_eye_enlarge,
                        R.drawable.icon_beauty_shape_enlarge_eye_close_selector, R.drawable.icon_beauty_shape_enlarge_eye_open_selector
                )
        );

        //圆眼
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_CIRCLE_INTENSITY, R.string.beauty_box_eye_circle,
                        R.drawable.icon_beauty_shape_round_eye_close_selector, R.drawable.icon_beauty_shape_round_eye_open_selector
                )
        );

        //下巴
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHIN_INTENSITY, R.string.beauty_box_intensity_chin,
                        R.drawable.icon_beauty_shape_chin_close_selector, R.drawable.icon_beauty_shape_chin_open_selector
                )
        );

        //额头
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.FOREHEAD_INTENSITY, R.string.beauty_box_intensity_forehead,
                        R.drawable.icon_beauty_shape_forehead_close_selector, R.drawable.icon_beauty_shape_forehead_open_selector
                )
        );

        //瘦鼻
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.NOSE_INTENSITY, R.string.beauty_box_intensity_nose,
                        R.drawable.icon_beauty_shape_thin_nose_close_selector, R.drawable.icon_beauty_shape_thin_nose_open_selector
                )
        );

        //嘴型
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.MOUTH_INTENSITY, R.string.beauty_box_intensity_mouth,
                        R.drawable.icon_beauty_shape_mouth_close_selector, R.drawable.icon_beauty_shape_mouth_open_selector
                )
        );

        //嘴唇厚度
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_LIP_THICK, R.string.beauty_lip_thick,
                        R.drawable.icon_beauty_shape_lip_thick_close_selector, R.drawable.icon_beauty_shape_lip_thick_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE)
        );

        //眼睛位置
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_EYE_HEIGHT, R.string.beauty_eye_height,
                        R.drawable.icon_beauty_shape_eye_height_close_selector, R.drawable.icon_beauty_shape_eye_height_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE)
        );

        //开眼角
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CANTHUS_INTENSITY, R.string.beauty_micro_canthus,
                        R.drawable.icon_beauty_shape_open_eyes_close_selector, R.drawable.icon_beauty_shape_open_eyes_open_selector
                )
        );

        //眼睑下至
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_EYE_LID, R.string.beauty_eye_lid,
                        R.drawable.icon_beauty_shape_eye_lid_close_selector, R.drawable.icon_beauty_shape_eye_lid_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE)
        );

        //眼距
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_SPACE_INTENSITY, R.string.beauty_micro_eye_space,
                        R.drawable.icon_beauty_shape_distance_close_selector, R.drawable.icon_beauty_shape_distance_open_selector
                )
        );

        //眼睛角度
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_ROTATE_INTENSITY, R.string.beauty_micro_eye_rotate,
                        R.drawable.icon_beauty_shape_angle_close_selector, R.drawable.icon_beauty_shape_angle_open_selector
                )
        );

        //长鼻
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.LONG_NOSE_INTENSITY, R.string.beauty_micro_long_nose,
                        R.drawable.icon_beauty_shape_proboscis_close_selector, R.drawable.icon_beauty_shape_proboscis_open_selector
                )
        );

        //缩人中
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.PHILTRUM_INTENSITY, R.string.beauty_micro_philtrum,
                        R.drawable.icon_beauty_shape_shrinking_close_selector, R.drawable.icon_beauty_shape_shrinking_open_selector
                )
        );

        //微笑嘴角
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.SMILE_INTENSITY, R.string.beauty_micro_smile,
                        R.drawable.icon_beauty_shape_smile_close_selector, R.drawable.icon_beauty_shape_smile_open_selector
                )
        );

        //眉毛上下
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.BROW_HEIGHT_INTENSITY, R.string.beauty_brow_height,
                        R.drawable.icon_beauty_shape_brow_height_close_selector, R.drawable.icon_beauty_shape_brow_height_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE
                )
        );

        //眉间距
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.BROW_SPACE_INTENSITY, R.string.beauty_brow_space,
                        R.drawable.icon_beauty_shape_brow_space_close_selector, R.drawable.icon_beauty_shape_brow_space_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE
                )
        );

        //眉毛粗细
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_BROW_THICK, R.string.beauty_brow_thick,
                        R.drawable.icon_beauty_shape_brow_thick_close_selector, R.drawable.icon_beauty_shape_brow_thick_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_ONE)
        );
        return params;
    }

    /**
     * 初始化参数扩展列表
     *
     * @return HashMap<String, ModelAttributeData>
     */
    public static HashMap<String, ModelAttributeData> buildModelAttributeRange() {
        HashMap<String, ModelAttributeData> params = new HashMap<>();
        /*美肤*/
        params.put(FaceBeautyParam.COLOR_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.BLUR_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 6.0));
        params.put(FaceBeautyParam.DELSPOT, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.RED_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CLARITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.SHARPEN_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_BRIGHT_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.TOOTH_WHITEN_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.REMOVE_POUCH_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.FACE_THREED, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        /*美型*/
        params.put(FaceBeautyParam.FACE_SHAPE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_THINNING_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_LONG_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_CIRCLE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_V_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_NARROW_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_SHORT_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_SMALL_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_ENLARGING_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_CIRCLE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHIN_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.FOREHEAD_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.NOSE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.MOUTH_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.CANTHUS_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_SPACE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_ROTATE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.LONG_NOSE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.PHILTRUM_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.SMILE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.BROW_HEIGHT_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.BROW_SPACE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_EYE_LID, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_EYE_HEIGHT, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_BROW_THICK, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_LIP_THICK, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        return params;
    }

    public static class StyleData {
        public FaceBeauty faceBeauty;
        public SimpleMakeup simpleMakeup;
        /*是否开启美肤效果*/
        public boolean faceBeautySkinEnable = true;
        /*是否开启美型效果*/
        public boolean faceBeautyShapeEnable = true;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            StyleData styleData = (StyleData) o;
            //是否开启美服美型
            if (faceBeautySkinEnable != styleData.faceBeautySkinEnable || faceBeautyShapeEnable != styleData.faceBeautyShapeEnable)
                return false;
            //美妆
            if (!simpleMakeup.getControlBundle().getPath().equals(styleData.simpleMakeup.getControlBundle().getPath()))
                return false;
            if (simpleMakeup.getMakeupIntensity() != styleData.simpleMakeup.getMakeupIntensity())
                return false;
            if (simpleMakeup.getFilterIntensity() != styleData.simpleMakeup.getFilterIntensity())
                return false;
            //美颜
            if (!faceBeauty.getControlBundle().getPath().equals(styleData.faceBeauty.getControlBundle().getPath()))
                return false;
            //美肤
            if (faceBeauty.getBlurIntensity() != styleData.faceBeauty.getBlurIntensity())
                return false;
            if (faceBeauty.getDelspotIntensity() != styleData.faceBeauty.getDelspotIntensity())
                return false;
            if (faceBeauty.getColorIntensity() != styleData.faceBeauty.getColorIntensity())
                return false;
            if (faceBeauty.getRedIntensity() != styleData.faceBeauty.getRedIntensity())
                return false;
            if (faceBeauty.getClarityIntensity() != styleData.faceBeauty.getClarityIntensity())
                return false;
            if (faceBeauty.getSharpenIntensity() != styleData.faceBeauty.getSharpenIntensity())
                return false;
            if (faceBeauty.getEyeBrightIntensity() != styleData.faceBeauty.getEyeBrightIntensity())
                return false;
            if (faceBeauty.getToothIntensity() != styleData.faceBeauty.getToothIntensity())
                return false;
            if (faceBeauty.getRemovePouchIntensity() != styleData.faceBeauty.getRemovePouchIntensity())
                return false;
            if (faceBeauty.getRemoveLawPatternIntensity() != styleData.faceBeauty.getRemoveLawPatternIntensity())
                return false;
            //美型
            if (faceBeauty.getCheekThinningIntensity() != styleData.faceBeauty.getCheekThinningIntensity())
                return false;
            if (faceBeauty.getCheekVIntensity() != styleData.faceBeauty.getCheekVIntensity())
                return false;
            if (faceBeauty.getCheekNarrowIntensity() != styleData.faceBeauty.getCheekNarrowIntensity())
                return false;
            if (faceBeauty.getCheekShortIntensity() != styleData.faceBeauty.getCheekShortIntensity())
                return false;
            if (faceBeauty.getCheekSmallIntensity() != styleData.faceBeauty.getCheekSmallIntensity())
                return false;
            if (faceBeauty.getCheekBonesIntensity() != styleData.faceBeauty.getCheekBonesIntensity())
                return false;
            if (faceBeauty.getLowerJawIntensity() != styleData.faceBeauty.getLowerJawIntensity())
                return false;
            if (faceBeauty.getEyeEnlargingIntensity() != styleData.faceBeauty.getEyeEnlargingIntensity())
                return false;
            if (faceBeauty.getEyeCircleIntensity() != styleData.faceBeauty.getEyeCircleIntensity())
                return false;
            if (faceBeauty.getChinIntensity() != styleData.faceBeauty.getChinIntensity())
                return false;
            if (faceBeauty.getForHeadIntensity() != styleData.faceBeauty.getForHeadIntensity())
                return false;
            if (faceBeauty.getNoseIntensity() != styleData.faceBeauty.getNoseIntensity())
                return false;
            if (faceBeauty.getMouthIntensity() != styleData.faceBeauty.getMouthIntensity())
                return false;
            if (faceBeauty.getCanthusIntensity() != styleData.faceBeauty.getCanthusIntensity())
                return false;
            if (faceBeauty.getEyeSpaceIntensity() != styleData.faceBeauty.getEyeSpaceIntensity())
                return false;
            if (faceBeauty.getEyeRotateIntensity() != styleData.faceBeauty.getEyeRotateIntensity())
                return false;
            if (faceBeauty.getLongNoseIntensity() != styleData.faceBeauty.getLongNoseIntensity())
                return false;
            if (faceBeauty.getPhiltrumIntensity() != styleData.faceBeauty.getPhiltrumIntensity())
                return false;
            if (faceBeauty.getSmileIntensity() != styleData.faceBeauty.getSmileIntensity())
                return false;
            if (faceBeauty.getBrowHeightIntensity() != styleData.faceBeauty.getBrowHeightIntensity())
                return false;
            if (faceBeauty.getBrowSpaceIntensity() != styleData.faceBeauty.getBrowSpaceIntensity())
                return false;
            if (faceBeauty.getEyeLidIntensity() != styleData.faceBeauty.getEyeLidIntensity())
                return false;
            if (faceBeauty.getEyeHeightIntensity() != styleData.faceBeauty.getEyeHeightIntensity())
                return false;
            if (faceBeauty.getBrowThickIntensity() != styleData.faceBeauty.getBrowThickIntensity())
                return false;
            if (faceBeauty.getLipThickIntensity() != styleData.faceBeauty.getLipThickIntensity())
                return false;
            if (faceBeauty.getFaceThreeIntensity() != styleData.faceBeauty.getFaceThreeIntensity())
                return false;
            return true;
        }
    }

    /**
     * 高端机的时候，开启4个相对吃性能的模式
     * 1.祛黑眼圈 MODE2
     * 2.祛法令纹 MODE2
     * 3.大眼 MODE3
     * 4.嘴型 MODE3
     */
    private static void setFaceBeautyPropertyMode(FaceBeauty faceBeauty) {
        /*
         * 多模式属性
         * 属性名称|支持模式|默认模式|最早支持版本
         * 美白 colorIntensity|MODE1 MODE2|MODE2|MODE2 8.2.0;
         * 祛黑眼圈 removePouchIntensity|MODE1 MODE2|MODE2|MODE2 8.2.0;
         * 祛法令纹 removeLawPatternIntensity|MODE1 MODE1|MODE2|MODE2 8.2.0;
         * 窄脸程度 cheekNarrowIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 小脸程度 cheekSmallIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 大眼程度 eyeEnlargingIntensity|MODE1 MODE2 MODE3|MODE3|MODE2 8.0.0;MODE3 8.2.0;
         * 额头调整程度 forHeadIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 瘦鼻程度 noseIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 嘴巴调整程度 mouthIntensity|MODE1 MODE2 MODE3|MODE3|MODE2 8.0.0;MODE3 8.2.0;
         */
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.REMOVE_POUCH_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE2);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.REMOVE_NASOLABIAL_FOLDS_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE2);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.EYE_ENLARGING_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE3);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.MOUTH_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE3);
    }

    /**
     * 退出页面的时候进行缓存
     */
    public static void saveStyle2Disk() {
        //硬盘缓存所有风格参数修改
        FUDiskFaceStyleUtils.saveStyleTypeMap2File(styleType, currentStyle);
    }

    private static HashMap<String, FUDiskStyleData> fuDiskStyleDataHashMap;

    /**
     * 加载类的时候读取一下硬盘缓存数据
     *
     * @return HashMap<String, FUDiskStyleData>
     */
    public static HashMap<String, FUDiskStyleData> loadStyleFromDisk() {
        if (fuDiskStyleDataHashMap == null || fuDiskStyleDataHashMap.isEmpty()) {
            fuDiskStyleDataHashMap = FUDiskFaceStyleUtils.loadStyleData();
            if (fuDiskStyleDataHashMap != null) {
                //还原默认选中项目
                Iterator<Map.Entry<String, FUDiskStyleData>> iterator = fuDiskStyleDataHashMap.entrySet().iterator();
                boolean hasChooseStyleType = false;
                while (iterator.hasNext()) {
                    Map.Entry<String, FUDiskStyleData> entry = iterator.next();
                    if (entry.getValue().isSelect) {
                        currentStyle = entry.getKey();
                        hasChooseStyleType = true;
                        break;
                    }
                }
                if (!hasChooseStyleType)
                    currentStyle = "";
            }
        }
        return fuDiskStyleDataHashMap;
    }

    /**
     * 将一个sourceFaceBeauty FaceBeauty设置给 targetBeauty FaceBeauty
     *
     * @param sourceFaceBeauty FaceBeauty
     * @param targetBeauty     FaceBeauty
     */
    public static void setFaceBeauty(FaceBeauty sourceFaceBeauty, FaceBeauty targetBeauty) {
        setFaceBeautySkin(sourceFaceBeauty, targetBeauty);
        setFaceBeautyShape(sourceFaceBeauty, targetBeauty);
    }

    /**
     * 将一个sourceFaceBeauty FaceBeauty设置给 targetBeauty FaceBeauty 只设置美肤项目
     *
     * @param sourceFaceBeauty FaceBeauty
     * @param targetBeauty     FaceBeauty
     */
    public static void setFaceBeautySkin(FaceBeauty sourceFaceBeauty, FaceBeauty targetBeauty) {
        //美肤
        if (targetBeauty.getBlurIntensity() != sourceFaceBeauty.getBlurIntensity())
            targetBeauty.setBlurIntensity(sourceFaceBeauty.getBlurIntensity());
        if (targetBeauty.getDelspotIntensity() != sourceFaceBeauty.getDelspotIntensity())
            targetBeauty.setDelspotIntensity(sourceFaceBeauty.getDelspotIntensity());
        if (targetBeauty.getColorIntensity() != sourceFaceBeauty.getColorIntensity())
            targetBeauty.setColorIntensity(sourceFaceBeauty.getColorIntensity());
        if (targetBeauty.getRedIntensity() != sourceFaceBeauty.getRedIntensity())
            targetBeauty.setRedIntensity(sourceFaceBeauty.getRedIntensity());
        if (targetBeauty.getClarityIntensity() != sourceFaceBeauty.getClarityIntensity())
            targetBeauty.setClarityIntensity(sourceFaceBeauty.getClarityIntensity());
        if (targetBeauty.getSharpenIntensity() != sourceFaceBeauty.getSharpenIntensity())
            targetBeauty.setSharpenIntensity(sourceFaceBeauty.getSharpenIntensity());
        if (targetBeauty.getEyeBrightIntensity() != sourceFaceBeauty.getEyeBrightIntensity())
            targetBeauty.setEyeBrightIntensity(sourceFaceBeauty.getEyeBrightIntensity());
        if (targetBeauty.getToothIntensity() != sourceFaceBeauty.getToothIntensity())
            targetBeauty.setToothIntensity(sourceFaceBeauty.getToothIntensity());
        if (targetBeauty.getRemovePouchIntensity() != sourceFaceBeauty.getRemovePouchIntensity())
            targetBeauty.setRemovePouchIntensity(sourceFaceBeauty.getRemovePouchIntensity());
        if (targetBeauty.getRemoveLawPatternIntensity() != sourceFaceBeauty.getRemoveLawPatternIntensity())
            targetBeauty.setRemoveLawPatternIntensity(sourceFaceBeauty.getRemoveLawPatternIntensity());
        if (targetBeauty.getFaceThreeIntensity() != sourceFaceBeauty.getFaceThreeIntensity())
            targetBeauty.setFaceThreeIntensity(sourceFaceBeauty.getFaceThreeIntensity());
    }

    /**
     * 将一个sourceFaceBeauty FaceBeauty设置给 targetBeauty FaceBeauty 只设置美型项目
     *
     * @param sourceFaceBeauty FaceBeauty
     * @param targetBeauty     FaceBeauty
     */
    public static void setFaceBeautyShape(FaceBeauty sourceFaceBeauty, FaceBeauty targetBeauty) {
        //美型
        if (targetBeauty.getCheekThinningIntensity() != sourceFaceBeauty.getCheekThinningIntensity())
            targetBeauty.setCheekThinningIntensity(sourceFaceBeauty.getCheekThinningIntensity());
        if (targetBeauty.getCheekVIntensity() != sourceFaceBeauty.getCheekVIntensity())
            targetBeauty.setCheekVIntensity(sourceFaceBeauty.getCheekVIntensity());
        if (targetBeauty.getCheekNarrowIntensity() != sourceFaceBeauty.getCheekNarrowIntensity())
            targetBeauty.setCheekNarrowIntensity(sourceFaceBeauty.getCheekNarrowIntensity());
        if (targetBeauty.getCheekShortIntensity() != sourceFaceBeauty.getCheekShortIntensity())
            targetBeauty.setCheekShortIntensity(sourceFaceBeauty.getCheekShortIntensity());
        if (targetBeauty.getCheekSmallIntensity() != sourceFaceBeauty.getCheekSmallIntensity())
            targetBeauty.setCheekSmallIntensity(sourceFaceBeauty.getCheekSmallIntensity());
        if (targetBeauty.getCheekBonesIntensity() != sourceFaceBeauty.getCheekBonesIntensity())
            targetBeauty.setCheekBonesIntensity(sourceFaceBeauty.getCheekBonesIntensity());
        if (targetBeauty.getLowerJawIntensity() != sourceFaceBeauty.getLowerJawIntensity())
            targetBeauty.setLowerJawIntensity(sourceFaceBeauty.getLowerJawIntensity());
        if (targetBeauty.getEyeEnlargingIntensity() != sourceFaceBeauty.getEyeEnlargingIntensity())
            targetBeauty.setEyeEnlargingIntensity(sourceFaceBeauty.getEyeEnlargingIntensity());
        if (targetBeauty.getEyeCircleIntensity() != sourceFaceBeauty.getEyeCircleIntensity())
            targetBeauty.setEyeCircleIntensity(sourceFaceBeauty.getEyeCircleIntensity());
        if (targetBeauty.getChinIntensity() != sourceFaceBeauty.getChinIntensity())
            targetBeauty.setChinIntensity(sourceFaceBeauty.getChinIntensity());
        if (targetBeauty.getForHeadIntensity() != sourceFaceBeauty.getForHeadIntensity())
            targetBeauty.setForHeadIntensity(sourceFaceBeauty.getForHeadIntensity());
        if (targetBeauty.getNoseIntensity() != sourceFaceBeauty.getNoseIntensity())
            targetBeauty.setNoseIntensity(sourceFaceBeauty.getNoseIntensity());
        if (targetBeauty.getMouthIntensity() != sourceFaceBeauty.getMouthIntensity())
            targetBeauty.setMouthIntensity(sourceFaceBeauty.getMouthIntensity());
        if (targetBeauty.getCanthusIntensity() != sourceFaceBeauty.getCanthusIntensity())
            targetBeauty.setCanthusIntensity(sourceFaceBeauty.getCanthusIntensity());
        if (targetBeauty.getEyeSpaceIntensity() != sourceFaceBeauty.getEyeSpaceIntensity())
            targetBeauty.setEyeSpaceIntensity(sourceFaceBeauty.getEyeSpaceIntensity());
        if (targetBeauty.getEyeRotateIntensity() != sourceFaceBeauty.getEyeRotateIntensity())
            targetBeauty.setEyeRotateIntensity(sourceFaceBeauty.getEyeRotateIntensity());
        if (targetBeauty.getLongNoseIntensity() != sourceFaceBeauty.getLongNoseIntensity())
            targetBeauty.setLongNoseIntensity(sourceFaceBeauty.getLongNoseIntensity());
        if (targetBeauty.getPhiltrumIntensity() != sourceFaceBeauty.getPhiltrumIntensity())
            targetBeauty.setPhiltrumIntensity(sourceFaceBeauty.getPhiltrumIntensity());
        if (targetBeauty.getSmileIntensity() != sourceFaceBeauty.getSmileIntensity())
            targetBeauty.setSmileIntensity(sourceFaceBeauty.getSmileIntensity());
        if (targetBeauty.getBrowHeightIntensity() != sourceFaceBeauty.getBrowHeightIntensity())
            targetBeauty.setBrowHeightIntensity(sourceFaceBeauty.getBrowHeightIntensity());
        if (targetBeauty.getBrowSpaceIntensity() != sourceFaceBeauty.getBrowSpaceIntensity())
            targetBeauty.setBrowSpaceIntensity(sourceFaceBeauty.getBrowSpaceIntensity());
        if (targetBeauty.getEyeLidIntensity() != sourceFaceBeauty.getEyeLidIntensity())
            targetBeauty.setEyeLidIntensity(sourceFaceBeauty.getEyeLidIntensity());
        if (targetBeauty.getEyeHeightIntensity() != sourceFaceBeauty.getEyeHeightIntensity())
            targetBeauty.setEyeHeightIntensity(sourceFaceBeauty.getEyeHeightIntensity());
        if (targetBeauty.getBrowThickIntensity() != sourceFaceBeauty.getBrowThickIntensity())
            targetBeauty.setBrowThickIntensity(sourceFaceBeauty.getBrowThickIntensity());
        if (targetBeauty.getLipThickIntensity() != sourceFaceBeauty.getLipThickIntensity())
            targetBeauty.setLipThickIntensity(sourceFaceBeauty.getLipThickIntensity());
    }
}
