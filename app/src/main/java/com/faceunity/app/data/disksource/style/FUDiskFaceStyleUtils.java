package com.faceunity.app.data.disksource.style;

import android.content.Context;
import android.content.SharedPreferences;

import com.faceunity.app.DemoApplication;
import com.faceunity.app.data.disksource.facebeauty.FUDiskFaceBeautyData;
import com.faceunity.app.data.source.StyleSource;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.makeup.SimpleMakeup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 将FaceBeauty StyleData 互相设置
 */
public class FUDiskFaceStyleUtils {
    private static final Type type = new TypeToken<HashMap<String, FUDiskStyleData>>() {
    }.getType();
    private static final String SP_NAME = "style";//sp表名
    private static final String SP_KEY_NAME = "styleData";//sp参数名

    /**
     * 将FaceBeauty styleType simpleMakeup 转 FUDiskStyleData
     */
    private static FUDiskStyleData buildStyleData(FaceBeauty faceBeauty, boolean styleType, SimpleMakeup simpleMakeup, boolean faceBeautySkinEnable, boolean faceBeautyShapeEnable) {
        FUDiskStyleData fuDiskStyleData = new FUDiskStyleData();
        if (faceBeauty != null) {
            /* 美肤 */
            /* 是否开启美肤效果 */
            fuDiskStyleData.faceBeautySkinEnable = faceBeautySkinEnable;
            /* 磨皮类型 */
            fuDiskStyleData.blurType = faceBeauty.getBlurType();
            /* 磨皮程度 */
            fuDiskStyleData.blurIntensity = faceBeauty.getBlurIntensity();
            /* 祛斑痘程度 */
            fuDiskStyleData.delspotIntensity = faceBeauty.getDelspotIntensity();
            /* 美白程度 */
            fuDiskStyleData.colorIntensity = faceBeauty.getColorIntensity();
            /* 红润程度 */
            fuDiskStyleData.redIntensity = faceBeauty.getRedIntensity();
            /* 清晰程度 */
            fuDiskStyleData.clarityIntensity = faceBeauty.getClarityIntensity();
            /* 锐化程度 */
            fuDiskStyleData.sharpenIntensity = faceBeauty.getSharpenIntensity();
            /* 亮眼程度 */
            fuDiskStyleData.eyeBrightIntensity = faceBeauty.getEyeBrightIntensity();
            /* 美牙程度 */
            fuDiskStyleData.toothIntensity = faceBeauty.getToothIntensity();
            /* 去黑眼圈强度*/
            fuDiskStyleData.removePouchIntensity = faceBeauty.getRemovePouchIntensity();
            /* 去法令纹强度*/
            fuDiskStyleData.removeLawPatternIntensity = faceBeauty.getRemoveLawPatternIntensity();

            /* 美型 */
            /* 是否开启美肤效果 */
            fuDiskStyleData.faceBeautyShapeEnable = faceBeautyShapeEnable;
            /* 瘦脸程度 */
            fuDiskStyleData.cheekThinningIntensity = faceBeauty.getCheekThinningIntensity();
            /* V脸程度 */
            fuDiskStyleData.cheekVIntensity = faceBeauty.getCheekVIntensity();
            /* 窄脸程度 */
            fuDiskStyleData.cheekNarrowIntensity = faceBeauty.getCheekNarrowIntensity();
            /* 短脸程度 */
            fuDiskStyleData.cheekShortIntensity = faceBeauty.getCheekShortIntensity();
            /* 小脸程度 */
            fuDiskStyleData.cheekSmallIntensity = faceBeauty.getCheekSmallIntensity();
            /* 瘦颧骨 */
            fuDiskStyleData.cheekBonesIntensity = faceBeauty.getCheekBonesIntensity();
            /* 瘦下颌骨 */
            fuDiskStyleData.lowerJawIntensity = faceBeauty.getLowerJawIntensity();
            /* 大眼程度 */
            fuDiskStyleData.eyeEnlargingIntensity = faceBeauty.getEyeEnlargingIntensity();
            /* 圆眼程度 */
            fuDiskStyleData.eyeCircleIntensity = faceBeauty.getEyeCircleIntensity();
            /* 下巴调整程度 */
            fuDiskStyleData.chinIntensity = faceBeauty.getChinIntensity();
            /* 额头调整程度 */
            fuDiskStyleData.forHeadIntensity = faceBeauty.getForHeadIntensity();
            /* 瘦鼻程度 */
            fuDiskStyleData.noseIntensity = faceBeauty.getNoseIntensity();
            /* 嘴巴调整程度 */
            fuDiskStyleData.mouthIntensity = faceBeauty.getMouthIntensity();
            /* 开眼角强度 */
            fuDiskStyleData.canthusIntensity = faceBeauty.getCanthusIntensity();
            /* 眼睛间距 */
            fuDiskStyleData.eyeSpaceIntensity = faceBeauty.getEyeSpaceIntensity();
            /* 眼睛角度 */
            fuDiskStyleData.eyeRotateIntensity = faceBeauty.getEyeRotateIntensity();
            /* 鼻子长度 */
            fuDiskStyleData.longNoseIntensity = faceBeauty.getLongNoseIntensity();
            /* 调节人中 */
            fuDiskStyleData.philtrumIntensity = faceBeauty.getPhiltrumIntensity();
            /* 微笑嘴角强度 */
            fuDiskStyleData.smileIntensity = faceBeauty.getSmileIntensity();
            /* 眉毛上下 */
            fuDiskStyleData.browHeightIntensity = faceBeauty.getBrowHeightIntensity();
            /* 眉毛间距 */
            fuDiskStyleData.browSpaceIntensity = faceBeauty.getBrowSpaceIntensity();
            /* 眼睑 */
            fuDiskStyleData.eyeLidIntensity = faceBeauty.getEyeLidIntensity();
            /* 眼睛高度 */
            fuDiskStyleData.eyeHeightIntensity = faceBeauty.getEyeHeightIntensity();
            /* 眉毛粗细 */
            fuDiskStyleData.browThickIntensity = faceBeauty.getBrowThickIntensity();
            /* 嘴巴厚度 */
            fuDiskStyleData.lipThickIntensity = faceBeauty.getLipThickIntensity();
            /* 五官立体 */
            fuDiskStyleData.faceThreeIntensity = faceBeauty.getFaceThreeIntensity();

            /* 风格滤镜强度 */
            fuDiskStyleData.filterIntensity = simpleMakeup.getFilterIntensity();

            //风格美妆
            fuDiskStyleData.makeupPath = simpleMakeup.getControlBundle().getPath();
            fuDiskStyleData.makeupIntensity = simpleMakeup.getMakeupIntensity();

            /* 风格默认选中原生*/
            fuDiskStyleData.isSelect = styleType;
        }
        return fuDiskStyleData;
    }

    /**
     * 将FaceBeauty 转 FaceBeautyData
     *
     * @param FUDiskFaceBeautyData
     */
    public static boolean setFaceBeauty(FUDiskFaceBeautyData FUDiskFaceBeautyData, FaceBeauty faceBeauty) {
        if (FUDiskFaceBeautyData == null) {
            return false;
        }
        if (faceBeauty != null) {
            /* 如果用户开启了风格推荐 */
            //下面是否则
            /* 美肤 */
            /* 磨皮类型 */
            if (FUDiskFaceBeautyData.blurType != faceBeauty.getBlurType())
                faceBeauty.setBlurType(FUDiskFaceBeautyData.blurType);
            /* 磨皮程度 */
            if (FUDiskFaceBeautyData.blurIntensity != faceBeauty.getBlurIntensity())
                faceBeauty.setBlurIntensity(FUDiskFaceBeautyData.blurIntensity);
            /* 祛斑痘程度 */
            if (FUDiskFaceBeautyData.delspotIntensity != faceBeauty.getDelspotIntensity())
                faceBeauty.setDelspotIntensity(FUDiskFaceBeautyData.delspotIntensity);
            /* 美白程度 */
            if (FUDiskFaceBeautyData.colorIntensity != faceBeauty.getColorIntensity())
                faceBeauty.setColorIntensity(FUDiskFaceBeautyData.colorIntensity);
            /* 红润程度 */
            if (FUDiskFaceBeautyData.redIntensity != faceBeauty.getRedIntensity())
                faceBeauty.setRedIntensity(FUDiskFaceBeautyData.redIntensity);
            /* 清晰程度 */
            if (FUDiskFaceBeautyData.clarityIntensity != faceBeauty.getClarityIntensity())
                faceBeauty.setClarityIntensity(FUDiskFaceBeautyData.clarityIntensity);
            /* 锐化程度 */
            if (FUDiskFaceBeautyData.sharpenIntensity != faceBeauty.getSharpenIntensity())
                faceBeauty.setSharpenIntensity(FUDiskFaceBeautyData.sharpenIntensity);
            /* 亮眼程度 */
            if (FUDiskFaceBeautyData.eyeBrightIntensity != faceBeauty.getEyeBrightIntensity())
                faceBeauty.setEyeBrightIntensity(FUDiskFaceBeautyData.eyeBrightIntensity);
            /* 美牙程度 */
            if (FUDiskFaceBeautyData.toothIntensity != faceBeauty.getToothIntensity())
                faceBeauty.setToothIntensity(FUDiskFaceBeautyData.toothIntensity);
            /* 去黑眼圈强度*/
            if (FUDiskFaceBeautyData.removePouchIntensity != faceBeauty.getRemovePouchIntensity())
                faceBeauty.setRemovePouchIntensity(FUDiskFaceBeautyData.removePouchIntensity);
            /* 去法令纹强度*/
            if (FUDiskFaceBeautyData.removeLawPatternIntensity != faceBeauty.getRemoveLawPatternIntensity())
                faceBeauty.setRemoveLawPatternIntensity(FUDiskFaceBeautyData.removeLawPatternIntensity);

            /* 美型 */
            /* 瘦脸程度 */
            if (FUDiskFaceBeautyData.cheekThinningIntensity != faceBeauty.getCheekThinningIntensity())
                faceBeauty.setCheekThinningIntensity(FUDiskFaceBeautyData.cheekThinningIntensity);
            /* V脸程度 */
            if (FUDiskFaceBeautyData.cheekVIntensity != faceBeauty.getCheekVIntensity())
                faceBeauty.setCheekVIntensity(FUDiskFaceBeautyData.cheekVIntensity);
            /* 窄脸程度 */
            /* V脸程度 */
            if (FUDiskFaceBeautyData.cheekNarrowIntensity != faceBeauty.getCheekNarrowIntensity())
                faceBeauty.setCheekNarrowIntensity(FUDiskFaceBeautyData.cheekNarrowIntensity);
            /* 短脸程度 */
            if (FUDiskFaceBeautyData.cheekShortIntensity != faceBeauty.getCheekShortIntensity())
                faceBeauty.setCheekShortIntensity(FUDiskFaceBeautyData.cheekShortIntensity);
            /* 小脸程度 */
            if (FUDiskFaceBeautyData.cheekSmallIntensity != faceBeauty.getCheekSmallIntensity())
                faceBeauty.setCheekSmallIntensity(FUDiskFaceBeautyData.cheekSmallIntensity);
            /* 瘦颧骨 */
            if (FUDiskFaceBeautyData.cheekBonesIntensity != faceBeauty.getCheekBonesIntensity())
                faceBeauty.setCheekBonesIntensity(FUDiskFaceBeautyData.cheekBonesIntensity);
            /* 瘦下颌骨 */
            if (FUDiskFaceBeautyData.lowerJawIntensity != faceBeauty.getLowerJawIntensity())
                faceBeauty.setLowerJawIntensity(FUDiskFaceBeautyData.lowerJawIntensity);
            /* 大眼程度 */
            if (FUDiskFaceBeautyData.eyeEnlargingIntensity != faceBeauty.getEyeEnlargingIntensity())
                faceBeauty.setEyeEnlargingIntensity(FUDiskFaceBeautyData.eyeEnlargingIntensity);
            /* 圆眼程度 */
            if (FUDiskFaceBeautyData.eyeCircleIntensity != faceBeauty.getEyeCircleIntensity())
                faceBeauty.setEyeCircleIntensity(FUDiskFaceBeautyData.eyeCircleIntensity);
            /* 下巴调整程度 */
            if (FUDiskFaceBeautyData.chinIntensity != faceBeauty.getChinIntensity())
                faceBeauty.setChinIntensity(FUDiskFaceBeautyData.chinIntensity);
            /* 额头调整程度 */
            if (FUDiskFaceBeautyData.forHeadIntensity != faceBeauty.getForHeadIntensity())
                faceBeauty.setForHeadIntensity(FUDiskFaceBeautyData.forHeadIntensity);
            /* 瘦鼻程度 */
            if (FUDiskFaceBeautyData.noseIntensity != faceBeauty.getNoseIntensity())
                faceBeauty.setNoseIntensity(FUDiskFaceBeautyData.noseIntensity);
            /* 嘴巴调整程度 */
            if (FUDiskFaceBeautyData.mouthIntensity != faceBeauty.getMouthIntensity())
                faceBeauty.setMouthIntensity(FUDiskFaceBeautyData.mouthIntensity);
            /* 开眼角强度 */
            if (FUDiskFaceBeautyData.canthusIntensity != faceBeauty.getCanthusIntensity())
                faceBeauty.setCanthusIntensity(FUDiskFaceBeautyData.canthusIntensity);
            /* 眼睛间距 */
            if (FUDiskFaceBeautyData.eyeSpaceIntensity != faceBeauty.getEyeSpaceIntensity())
                faceBeauty.setEyeSpaceIntensity(FUDiskFaceBeautyData.eyeSpaceIntensity);
            /* 眼睛角度 */
            if (FUDiskFaceBeautyData.eyeRotateIntensity != faceBeauty.getEyeRotateIntensity())
                faceBeauty.setEyeRotateIntensity(FUDiskFaceBeautyData.eyeRotateIntensity);
            /* 鼻子长度 */
            if (FUDiskFaceBeautyData.longNoseIntensity != faceBeauty.getLongNoseIntensity())
                faceBeauty.setLongNoseIntensity(FUDiskFaceBeautyData.longNoseIntensity);
            /* 调节人中 */
            if (FUDiskFaceBeautyData.philtrumIntensity != faceBeauty.getPhiltrumIntensity())
                faceBeauty.setPhiltrumIntensity(FUDiskFaceBeautyData.philtrumIntensity);
            /* 微笑嘴角强度 */
            if (FUDiskFaceBeautyData.smileIntensity != faceBeauty.getSmileIntensity())
                faceBeauty.setSmileIntensity(FUDiskFaceBeautyData.smileIntensity);
            /* 眉毛上下 */
            if (FUDiskFaceBeautyData.browHeightIntensity != faceBeauty.getBrowHeightIntensity())
                faceBeauty.setBrowHeightIntensity(FUDiskFaceBeautyData.browHeightIntensity);
            /* 眉毛间距 */
            if (FUDiskFaceBeautyData.browSpaceIntensity != faceBeauty.getBrowSpaceIntensity())
                faceBeauty.setBrowSpaceIntensity(FUDiskFaceBeautyData.browSpaceIntensity);
            /* 眼睑 */
            if (FUDiskFaceBeautyData.eyeLidIntensity != faceBeauty.getEyeLidIntensity())
                faceBeauty.setEyeLidIntensity(FUDiskFaceBeautyData.eyeLidIntensity);
            /* 眼睛高度 */
            if (FUDiskFaceBeautyData.eyeHeightIntensity != faceBeauty.getEyeHeightIntensity())
                faceBeauty.setEyeHeightIntensity(FUDiskFaceBeautyData.eyeHeightIntensity);
            /* 眉毛粗细 */
            if (FUDiskFaceBeautyData.browThickIntensity != faceBeauty.getBrowThickIntensity())
                faceBeauty.setBrowThickIntensity(FUDiskFaceBeautyData.browThickIntensity);
            /* 嘴巴厚度 */
            if (FUDiskFaceBeautyData.lipThickIntensity != faceBeauty.getLipThickIntensity())
                faceBeauty.setLipThickIntensity(FUDiskFaceBeautyData.lipThickIntensity);
            /* 五官立体 */
            if (FUDiskFaceBeautyData.faceThreeIntensity != faceBeauty.getFaceThreeIntensity())
                faceBeauty.setFaceThreeIntensity(FUDiskFaceBeautyData.faceThreeIntensity);

            /* 滤镜相关 */
            /* 滤镜名称 */
            if (!FUDiskFaceBeautyData.filterName.equals(faceBeauty.getFilterName()))
                faceBeauty.setFilterName(FUDiskFaceBeautyData.filterName);
            if (FUDiskFaceBeautyData.filterIntensity != faceBeauty.getFilterIntensity())
                faceBeauty.setFilterIntensity(FUDiskFaceBeautyData.filterIntensity);
            return true;
        }

        return false;
    }

    /**
     * 将FaceBeautyData保存到文件
     *
     * @param styleDataHashMap 所有风格记录
     * @param styleType        当前选中风格记录
     */
    public static void saveStyleTypeMap2File(HashMap<String, StyleSource.StyleData> styleDataHashMap, String styleType) {
        Iterator<Map.Entry<String, StyleSource.StyleData>> iterator = styleDataHashMap.entrySet().iterator();
        HashMap<String, FUDiskStyleData> fuDiskStyleDataHashMap = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, StyleSource.StyleData> entry = iterator.next();
            StyleSource.StyleData value = entry.getValue();
            FaceBeauty faceBeauty = value.faceBeauty;
            SimpleMakeup simpleMakeup = value.simpleMakeup;
            boolean faceBeautySkinEnable = value.faceBeautySkinEnable;
            boolean faceBeautyShapeEnable = value.faceBeautyShapeEnable;
            FUDiskStyleData fuDiskStyleData = buildStyleData(faceBeauty, styleType.equals(entry.getKey()), simpleMakeup, faceBeautySkinEnable, faceBeautyShapeEnable);
            fuDiskStyleDataHashMap.put(entry.getKey(), fuDiskStyleData);
        }

        saveStyleData2File(fuDiskStyleDataHashMap);
    }

    /**
     * 将FaceBeautyData保存到文件
     *
     * @param fuDiskStyleDataArrayList
     */
    private static void saveStyleData2File(HashMap<String, FUDiskStyleData> fuDiskStyleDataArrayList) {
        Gson gson = new Gson();
        String faceBeautyString = gson.toJson(fuDiskStyleDataArrayList, type);
        saveToSp(SP_KEY_NAME, faceBeautyString);
    }

    /**
     * 获取bean对象
     *
     * @return
     */
    public static HashMap<String, FUDiskStyleData> loadStyleData() {
        String faceBeautyData = loadFormSp(SP_KEY_NAME);
        if (faceBeautyData != null && !faceBeautyData.isEmpty()) {
            Gson gson = new Gson();
            HashMap<String, FUDiskStyleData> fuDiskStyleDataHashMap;
            fuDiskStyleDataHashMap = gson.fromJson(faceBeautyData, type);
            return fuDiskStyleDataHashMap;
        }
        return null;
    }

    public static void removeStyleData(){
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().remove(SP_KEY_NAME).apply();
    }

    private static void saveToSp(String key, String value) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private static String loadFormSp(String key) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key,null);
    }
}
