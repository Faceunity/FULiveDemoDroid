package com.faceunity.app.data.disksource;

import android.content.Context;
import android.content.SharedPreferences;

import com.faceunity.app.DemoApplication;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.ui.entity.FaceBeautyFilterBean;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * 将FaceBeauty FaceBeautyData 互相设置
 */
public class FUUtils {
    private static final String SP_NAME = "FaceBeauty";
    private static final String SP_KEY_NAME = "faceBeautyData";

    /**
     * 将FaceBeauty 转 FaceBeautyData
     *
     * @param faceBeautyData
     */
    public static void buildFaceBeautyData(FaceBeautyData faceBeautyData,FaceBeauty faceBeauty,ArrayList<FaceBeautyFilterBean> filterList, int styleTypeIndex) {
        if (faceBeauty != null) {
            /* 美肤 */
            /* 磨皮类型 */
            faceBeautyData.blurType = faceBeauty.getBlurType();
            /* 磨皮程度 */
            faceBeautyData.blurIntensity = faceBeauty.getBlurIntensity();
            /* 美白程度 */
            faceBeautyData.colorIntensity = faceBeauty.getColorIntensity();
            /* 红润程度 */
            faceBeautyData.redIntensity = faceBeauty.getRedIntensity();
            /* 锐化程度 */
            faceBeautyData.sharpenIntensity = faceBeauty.getSharpenIntensity();
            /* 亮眼程度 */
            faceBeautyData.eyeBrightIntensity = faceBeauty.getEyeBrightIntensity();
            /* 美牙程度 */
            faceBeautyData.toothIntensity = faceBeauty.getToothIntensity();
            /* 去黑眼圈强度*/
            faceBeautyData.removePouchIntensity = faceBeauty.getRemovePouchIntensity();
            /* 去法令纹强度*/
            faceBeautyData.removeLawPatternIntensity = faceBeauty.getRemoveLawPatternIntensity();

            /* 美型 */
            /* 瘦脸程度 */
            faceBeautyData.cheekThinningIntensity = faceBeauty.getCheekThinningIntensity();
            /* V脸程度 */
            faceBeautyData.cheekVIntensity = faceBeauty.getCheekVIntensity();
            /* 窄脸程度 */
            faceBeautyData.cheekNarrowIntensity = faceBeauty.getCheekNarrowIntensity();
            /* 短脸程度 */
            faceBeautyData.cheekShortIntensity = faceBeauty.getCheekShortIntensity();
            /* 小脸程度 */
            faceBeautyData.cheekSmallIntensity = faceBeauty.getCheekSmallIntensity();
            /* 瘦颧骨 */
            faceBeautyData.cheekBonesIntensity = faceBeauty.getCheekBonesIntensity();
            /* 瘦下颌骨 */
            faceBeautyData.lowerJawIntensity = faceBeauty.getLowerJawIntensity();
            /* 大眼程度 */
            faceBeautyData.eyeEnlargingIntensity = faceBeauty.getEyeEnlargingIntensity();
            /* 圆眼程度 */
            faceBeautyData.eyeCircleIntensity = faceBeauty.getEyeCircleIntensity();
            /* 下巴调整程度 */
            faceBeautyData.chinIntensity = faceBeauty.getChinIntensity();
            /* 额头调整程度 */
            faceBeautyData.forHeadIntensity = faceBeauty.getForHeadIntensity();
            /* 瘦鼻程度 */
            faceBeautyData.noseIntensity = faceBeauty.getNoseIntensity();
            /* 嘴巴调整程度 */
            faceBeautyData.mouthIntensity = faceBeauty.getMouthIntensity();
            /* 开眼角强度 */
            faceBeautyData.canthusIntensity = faceBeauty.getCanthusIntensity();
            /* 眼睛间距 */
            faceBeautyData.eyeSpaceIntensity = faceBeauty.getEyeSpaceIntensity();
            /* 眼睛角度 */
            faceBeautyData.eyeRotateIntensity = faceBeauty.getEyeRotateIntensity();
            /* 鼻子长度 */
            faceBeautyData.longNoseIntensity = faceBeauty.getLongNoseIntensity();
            /* 调节人中 */
            faceBeautyData.philtrumIntensity = faceBeauty.getPhiltrumIntensity();
            /* 微笑嘴角强度 */
            faceBeautyData.smileIntensity = faceBeauty.getSmileIntensity();
            /* 眉毛上下 */
            faceBeautyData.browHeightIntensity = faceBeauty.getBrowHeightIntensity();
            /* 眉毛间距 */
            faceBeautyData.browSpaceIntensity = faceBeauty.getBrowSpaceIntensity();

            /* 滤镜相关 */
            if (filterList != null) {
                for (FaceBeautyFilterBean filterBean:filterList) {
                    faceBeautyData.filterMap.put(filterBean.getKey(),filterBean.getIntensity());
                }
            }
            /* 滤镜名称 */
            faceBeautyData.filterName = faceBeauty.getFilterName();
            /* 滤镜强度 */
            faceBeautyData.filterIntensity = faceBeauty.getFilterIntensity();

            /* 是否开启风格 */
            faceBeautyData.styleTypeIndex = styleTypeIndex;
        }
    }

    /**
     * 将FaceBeauty 转 FaceBeautyData
     *
     * @param faceBeautyData
     */
    public static boolean setFaceBeauty(FaceBeautyData faceBeautyData ,FaceBeauty faceBeauty) {
        if (faceBeautyData == null) {
            return false;
        }
        if (faceBeauty != null) {
            /* 如果用户开启了风格推荐 */
            //下面是否则
            /* 美肤 */
            /* 磨皮类型 */
            if (faceBeautyData.blurType != faceBeauty.getBlurType())
                faceBeauty.setBlurType(faceBeautyData.blurType);
            /* 磨皮程度 */
            if (faceBeautyData.blurIntensity != faceBeauty.getBlurIntensity())
                faceBeauty.setBlurIntensity(faceBeautyData.blurIntensity);
            /* 美白程度 */
            if (faceBeautyData.colorIntensity != faceBeauty.getColorIntensity())
                faceBeauty.setColorIntensity(faceBeautyData.colorIntensity);
            /* 红润程度 */
            if (faceBeautyData.redIntensity != faceBeauty.getRedIntensity())
                faceBeauty.setRedIntensity(faceBeautyData.redIntensity);
            /* 锐化程度 */
            if (faceBeautyData.sharpenIntensity != faceBeauty.getSharpenIntensity())
                faceBeauty.setSharpenIntensity(faceBeautyData.sharpenIntensity);
            /* 亮眼程度 */
            if (faceBeautyData.eyeBrightIntensity != faceBeauty.getEyeBrightIntensity())
                faceBeauty.setEyeBrightIntensity(faceBeautyData.eyeBrightIntensity);
            /* 美牙程度 */
            if (faceBeautyData.toothIntensity != faceBeauty.getToothIntensity())
                faceBeauty.setToothIntensity(faceBeautyData.toothIntensity);
            /* 去黑眼圈强度*/
            if (faceBeautyData.removePouchIntensity != faceBeauty.getRemovePouchIntensity())
                faceBeauty.setRemovePouchIntensity(faceBeautyData.removePouchIntensity);
            /* 去法令纹强度*/
            if (faceBeautyData.removeLawPatternIntensity != faceBeauty.getRemoveLawPatternIntensity())
                faceBeauty.setRemoveLawPatternIntensity(faceBeautyData.removeLawPatternIntensity);

            /* 美型 */
            /* 瘦脸程度 */
            if (faceBeautyData.cheekThinningIntensity != faceBeauty.getCheekThinningIntensity())
                faceBeauty.setCheekThinningIntensity(faceBeautyData.cheekThinningIntensity);
            /* V脸程度 */
            if (faceBeautyData.cheekVIntensity != faceBeauty.getCheekVIntensity())
                faceBeauty.setCheekVIntensity(faceBeautyData.cheekVIntensity);
            /* 窄脸程度 */
            /* V脸程度 */
            if (faceBeautyData.cheekNarrowIntensity != faceBeauty.getCheekNarrowIntensity())
                faceBeauty.setCheekNarrowIntensity(faceBeautyData.cheekNarrowIntensity);
            /* 短脸程度 */
            if (faceBeautyData.cheekShortIntensity != faceBeauty.getCheekShortIntensity())
                faceBeauty.setCheekShortIntensity(faceBeautyData.cheekShortIntensity);
            /* 小脸程度 */
            if (faceBeautyData.cheekSmallIntensity != faceBeauty.getCheekSmallIntensity())
                faceBeauty.setCheekSmallIntensity(faceBeautyData.cheekSmallIntensity);
            /* 瘦颧骨 */
            if (faceBeautyData.cheekBonesIntensity != faceBeauty.getCheekBonesIntensity())
                faceBeauty.setCheekBonesIntensity(faceBeautyData.cheekBonesIntensity);
            /* 瘦下颌骨 */
            if (faceBeautyData.lowerJawIntensity != faceBeauty.getLowerJawIntensity())
                faceBeauty.setLowerJawIntensity(faceBeautyData.lowerJawIntensity);
            /* 大眼程度 */
            if (faceBeautyData.eyeEnlargingIntensity != faceBeauty.getEyeEnlargingIntensity())
                faceBeauty.setEyeEnlargingIntensity(faceBeautyData.eyeEnlargingIntensity);
            /* 圆眼程度 */
            if (faceBeautyData.eyeCircleIntensity != faceBeauty.getEyeCircleIntensity())
                faceBeauty.setEyeCircleIntensity(faceBeautyData.eyeCircleIntensity);
            /* 下巴调整程度 */
            if (faceBeautyData.chinIntensity != faceBeauty.getChinIntensity())
                faceBeauty.setChinIntensity(faceBeautyData.chinIntensity);
            /* 额头调整程度 */
            if (faceBeautyData.forHeadIntensity != faceBeauty.getForHeadIntensity())
                faceBeauty.setForHeadIntensity(faceBeautyData.forHeadIntensity);
            /* 瘦鼻程度 */
            if (faceBeautyData.noseIntensity != faceBeauty.getNoseIntensity())
                faceBeauty.setNoseIntensity(faceBeautyData.noseIntensity);
            /* 嘴巴调整程度 */
            if (faceBeautyData.mouthIntensity != faceBeauty.getMouthIntensity())
                faceBeauty.setMouthIntensity(faceBeautyData.mouthIntensity);
            /* 开眼角强度 */
            if (faceBeautyData.canthusIntensity != faceBeauty.getCanthusIntensity())
                faceBeauty.setCanthusIntensity(faceBeautyData.canthusIntensity);
            /* 眼睛间距 */
            if (faceBeautyData.eyeSpaceIntensity != faceBeauty.getEyeSpaceIntensity())
                faceBeauty.setEyeSpaceIntensity(faceBeautyData.eyeSpaceIntensity);
            /* 眼睛角度 */
            if (faceBeautyData.eyeRotateIntensity != faceBeauty.getEyeRotateIntensity())
                faceBeauty.setEyeRotateIntensity(faceBeautyData.eyeRotateIntensity);
            /* 鼻子长度 */
            if (faceBeautyData.longNoseIntensity != faceBeauty.getLongNoseIntensity())
                faceBeauty.setLongNoseIntensity(faceBeautyData.longNoseIntensity);
            /* 调节人中 */
            if (faceBeautyData.philtrumIntensity != faceBeauty.getPhiltrumIntensity())
                faceBeauty.setPhiltrumIntensity(faceBeautyData.philtrumIntensity);
            /* 微笑嘴角强度 */
            if (faceBeautyData.smileIntensity != faceBeauty.getSmileIntensity())
                faceBeauty.setSmileIntensity(faceBeautyData.smileIntensity);
            /* 眉毛上下 */
            if (faceBeautyData.browHeightIntensity != faceBeauty.getBrowHeightIntensity())
                faceBeauty.setBrowHeightIntensity(faceBeautyData.browHeightIntensity);
            /* 眉毛间距 */
            if (faceBeautyData.browSpaceIntensity != faceBeauty.getBrowSpaceIntensity())
                faceBeauty.setBrowSpaceIntensity(faceBeautyData.browSpaceIntensity);

            /* 滤镜相关 */
            /* 滤镜名称 */
            if (!faceBeautyData.filterName.equals(faceBeauty.getFilterName()))
                faceBeauty.setFilterName(faceBeautyData.filterName);
            if (faceBeautyData.filterIntensity != faceBeauty.getFilterIntensity())
                faceBeauty.setFilterIntensity(faceBeautyData.filterIntensity);
            return true;
        }

        return false;
    }

    /**
     * 将FaceBeautyData保存到文件
     *
     * @param faceBeautyData
     */
    public static void saveFaceBeautyData2File(FaceBeautyData faceBeautyData, FaceBeauty faceBeauty, ArrayList<FaceBeautyFilterBean> filterList, int styleTypeIndex) {
        buildFaceBeautyData(faceBeautyData,faceBeauty,filterList,styleTypeIndex);
        saveFaceBeautyData2File(faceBeautyData);
    }

    /**
     * 将FaceBeautyData保存到文件
     *
     * @param faceBeautyData
     */
    public static void saveFaceBeautyData2File(FaceBeautyData faceBeautyData) {
        Gson gson = new Gson();
        String faceBeautyString = gson.toJson(faceBeautyData);
        saveToSp(SP_KEY_NAME, faceBeautyString);
    }

    /**
     * 获取bean对象
     *
     * @return
     */
    public static FaceBeautyData loadFaceBeautyData() {
        String faceBeautyData = loadFormSp(SP_KEY_NAME);
        if (faceBeautyData != null && !faceBeautyData.isEmpty()) {
            Gson gson = new Gson();
            FaceBeautyData faceBeautyDataBean = gson.fromJson(faceBeautyData, FaceBeautyData.class);
            return faceBeautyDataBean;
        }
        return null;
    }

    private static void saveToSp(String key, String value) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private static String loadFormSp(String key) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }
}
