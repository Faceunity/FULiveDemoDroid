package com.faceunity.app.data.disksource.facebeauty;

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
public class FUDiskFaceBeautyUtils {
    private static final String SP_NAME = "FaceBeauty";
    private static final String SP_KEY_NAME = "faceBeautyData";

    /**
     * 将FaceBeauty 转 FaceBeautyData
     *
     * @param fuDiskFaceBeautyData
     */
    public static void buildFaceBeautyData(FUDiskFaceBeautyData fuDiskFaceBeautyData, FaceBeauty faceBeauty, ArrayList<FaceBeautyFilterBean> filterList) {
        if (faceBeauty != null) {
            /* 美肤 */
            /* 磨皮类型 */
            fuDiskFaceBeautyData.blurType = faceBeauty.getBlurType();
            /* 磨皮程度 */
            fuDiskFaceBeautyData.blurIntensity = faceBeauty.getBlurIntensity();
            /* 美白程度 */
            fuDiskFaceBeautyData.colorIntensity = faceBeauty.getColorIntensity();
            /* 红润程度 */
            fuDiskFaceBeautyData.redIntensity = faceBeauty.getRedIntensity();
            /* 锐化程度 */
            fuDiskFaceBeautyData.sharpenIntensity = faceBeauty.getSharpenIntensity();
            /* 亮眼程度 */
            fuDiskFaceBeautyData.eyeBrightIntensity = faceBeauty.getEyeBrightIntensity();
            /* 美牙程度 */
            fuDiskFaceBeautyData.toothIntensity = faceBeauty.getToothIntensity();
            /* 去黑眼圈强度*/
            fuDiskFaceBeautyData.removePouchIntensity = faceBeauty.getRemovePouchIntensity();
            /* 去法令纹强度*/
            fuDiskFaceBeautyData.removeLawPatternIntensity = faceBeauty.getRemoveLawPatternIntensity();

            /* 美型 */
            /* 瘦脸程度 */
            fuDiskFaceBeautyData.cheekThinningIntensity = faceBeauty.getCheekThinningIntensity();
            /* V脸程度 */
            fuDiskFaceBeautyData.cheekVIntensity = faceBeauty.getCheekVIntensity();
            /* 窄脸程度 */
            fuDiskFaceBeautyData.cheekNarrowIntensity = faceBeauty.getCheekNarrowIntensity();
            /* 短脸程度 */
            fuDiskFaceBeautyData.cheekShortIntensity = faceBeauty.getCheekShortIntensity();
            /* 小脸程度 */
            fuDiskFaceBeautyData.cheekSmallIntensity = faceBeauty.getCheekSmallIntensity();
            /* 瘦颧骨 */
            fuDiskFaceBeautyData.cheekBonesIntensity = faceBeauty.getCheekBonesIntensity();
            /* 瘦下颌骨 */
            fuDiskFaceBeautyData.lowerJawIntensity = faceBeauty.getLowerJawIntensity();
            /* 大眼程度 */
            fuDiskFaceBeautyData.eyeEnlargingIntensity = faceBeauty.getEyeEnlargingIntensity();
            /* 圆眼程度 */
            fuDiskFaceBeautyData.eyeCircleIntensity = faceBeauty.getEyeCircleIntensity();
            /* 下巴调整程度 */
            fuDiskFaceBeautyData.chinIntensity = faceBeauty.getChinIntensity();
            /* 额头调整程度 */
            fuDiskFaceBeautyData.forHeadIntensity = faceBeauty.getForHeadIntensity();
            /* 瘦鼻程度 */
            fuDiskFaceBeautyData.noseIntensity = faceBeauty.getNoseIntensity();
            /* 嘴巴调整程度 */
            fuDiskFaceBeautyData.mouthIntensity = faceBeauty.getMouthIntensity();
            /* 开眼角强度 */
            fuDiskFaceBeautyData.canthusIntensity = faceBeauty.getCanthusIntensity();
            /* 眼睛间距 */
            fuDiskFaceBeautyData.eyeSpaceIntensity = faceBeauty.getEyeSpaceIntensity();
            /* 眼睛角度 */
            fuDiskFaceBeautyData.eyeRotateIntensity = faceBeauty.getEyeRotateIntensity();
            /* 鼻子长度 */
            fuDiskFaceBeautyData.longNoseIntensity = faceBeauty.getLongNoseIntensity();
            /* 调节人中 */
            fuDiskFaceBeautyData.philtrumIntensity = faceBeauty.getPhiltrumIntensity();
            /* 微笑嘴角强度 */
            fuDiskFaceBeautyData.smileIntensity = faceBeauty.getSmileIntensity();
            /* 眉毛上下 */
            fuDiskFaceBeautyData.browHeightIntensity = faceBeauty.getBrowHeightIntensity();
            /* 眉毛间距 */
            fuDiskFaceBeautyData.browSpaceIntensity = faceBeauty.getBrowSpaceIntensity();
            /* 眼睑 */
            fuDiskFaceBeautyData.eyeLidIntensity = faceBeauty.getEyeLidIntensity();
            /* 眼睛高度 */
            fuDiskFaceBeautyData.eyeHeightIntensity = faceBeauty.getEyeHeightIntensity();
            /* 眉毛粗细 */
            fuDiskFaceBeautyData.browThickIntensity = faceBeauty.getBrowThickIntensity();
            /* 嘴巴厚度 */
            fuDiskFaceBeautyData.lipThickIntensity = faceBeauty.getLipThickIntensity();
            /* 五官立体 */
            fuDiskFaceBeautyData.faceThreeIntensity = faceBeauty.getFaceThreeIntensity();

            /* 滤镜相关 */
            if (filterList != null) {
                for (FaceBeautyFilterBean filterBean:filterList) {
                    fuDiskFaceBeautyData.filterMap.put(filterBean.getKey(),filterBean.getIntensity());
                }
            }
            /* 滤镜名称 */
            fuDiskFaceBeautyData.filterName = faceBeauty.getFilterName();
            /* 滤镜强度 */
            fuDiskFaceBeautyData.filterIntensity = faceBeauty.getFilterIntensity();
        }
    }

    /**
     * 将FaceBeauty 转 FaceBeautyData
     *
     * @param fuDiskFaceBeautyData
     */
    public static boolean setFaceBeauty(FUDiskFaceBeautyData fuDiskFaceBeautyData, FaceBeauty faceBeauty) {
        if (fuDiskFaceBeautyData == null) {
            return false;
        }
        if (faceBeauty != null) {
            /* 如果用户开启了风格推荐 */
            //下面是否则
            /* 美肤 */
            /* 磨皮类型 */
            if (fuDiskFaceBeautyData.blurType != faceBeauty.getBlurType())
                faceBeauty.setBlurType(fuDiskFaceBeautyData.blurType);
            /* 磨皮程度 */
            if (fuDiskFaceBeautyData.blurIntensity != faceBeauty.getBlurIntensity())
                faceBeauty.setBlurIntensity(fuDiskFaceBeautyData.blurIntensity);
            /* 美白程度 */
            if (fuDiskFaceBeautyData.colorIntensity != faceBeauty.getColorIntensity())
                faceBeauty.setColorIntensity(fuDiskFaceBeautyData.colorIntensity);
            /* 红润程度 */
            if (fuDiskFaceBeautyData.redIntensity != faceBeauty.getRedIntensity())
                faceBeauty.setRedIntensity(fuDiskFaceBeautyData.redIntensity);
            /* 锐化程度 */
            if (fuDiskFaceBeautyData.sharpenIntensity != faceBeauty.getSharpenIntensity())
                faceBeauty.setSharpenIntensity(fuDiskFaceBeautyData.sharpenIntensity);
            /* 亮眼程度 */
            if (fuDiskFaceBeautyData.eyeBrightIntensity != faceBeauty.getEyeBrightIntensity())
                faceBeauty.setEyeBrightIntensity(fuDiskFaceBeautyData.eyeBrightIntensity);
            /* 美牙程度 */
            if (fuDiskFaceBeautyData.toothIntensity != faceBeauty.getToothIntensity())
                faceBeauty.setToothIntensity(fuDiskFaceBeautyData.toothIntensity);
            /* 去黑眼圈强度*/
            if (fuDiskFaceBeautyData.removePouchIntensity != faceBeauty.getRemovePouchIntensity())
                faceBeauty.setRemovePouchIntensity(fuDiskFaceBeautyData.removePouchIntensity);
            /* 去法令纹强度*/
            if (fuDiskFaceBeautyData.removeLawPatternIntensity != faceBeauty.getRemoveLawPatternIntensity())
                faceBeauty.setRemoveLawPatternIntensity(fuDiskFaceBeautyData.removeLawPatternIntensity);

            /* 美型 */
            /* 瘦脸程度 */
            if (fuDiskFaceBeautyData.cheekThinningIntensity != faceBeauty.getCheekThinningIntensity())
                faceBeauty.setCheekThinningIntensity(fuDiskFaceBeautyData.cheekThinningIntensity);
            /* V脸程度 */
            if (fuDiskFaceBeautyData.cheekVIntensity != faceBeauty.getCheekVIntensity())
                faceBeauty.setCheekVIntensity(fuDiskFaceBeautyData.cheekVIntensity);
            /* 窄脸程度 */
            /* V脸程度 */
            if (fuDiskFaceBeautyData.cheekNarrowIntensity != faceBeauty.getCheekNarrowIntensity())
                faceBeauty.setCheekNarrowIntensity(fuDiskFaceBeautyData.cheekNarrowIntensity);
            /* 短脸程度 */
            if (fuDiskFaceBeautyData.cheekShortIntensity != faceBeauty.getCheekShortIntensity())
                faceBeauty.setCheekShortIntensity(fuDiskFaceBeautyData.cheekShortIntensity);
            /* 小脸程度 */
            if (fuDiskFaceBeautyData.cheekSmallIntensity != faceBeauty.getCheekSmallIntensity())
                faceBeauty.setCheekSmallIntensity(fuDiskFaceBeautyData.cheekSmallIntensity);
            /* 瘦颧骨 */
            if (fuDiskFaceBeautyData.cheekBonesIntensity != faceBeauty.getCheekBonesIntensity())
                faceBeauty.setCheekBonesIntensity(fuDiskFaceBeautyData.cheekBonesIntensity);
            /* 瘦下颌骨 */
            if (fuDiskFaceBeautyData.lowerJawIntensity != faceBeauty.getLowerJawIntensity())
                faceBeauty.setLowerJawIntensity(fuDiskFaceBeautyData.lowerJawIntensity);
            /* 大眼程度 */
            if (fuDiskFaceBeautyData.eyeEnlargingIntensity != faceBeauty.getEyeEnlargingIntensity())
                faceBeauty.setEyeEnlargingIntensity(fuDiskFaceBeautyData.eyeEnlargingIntensity);
            /* 圆眼程度 */
            if (fuDiskFaceBeautyData.eyeCircleIntensity != faceBeauty.getEyeCircleIntensity())
                faceBeauty.setEyeCircleIntensity(fuDiskFaceBeautyData.eyeCircleIntensity);
            /* 下巴调整程度 */
            if (fuDiskFaceBeautyData.chinIntensity != faceBeauty.getChinIntensity())
                faceBeauty.setChinIntensity(fuDiskFaceBeautyData.chinIntensity);
            /* 额头调整程度 */
            if (fuDiskFaceBeautyData.forHeadIntensity != faceBeauty.getForHeadIntensity())
                faceBeauty.setForHeadIntensity(fuDiskFaceBeautyData.forHeadIntensity);
            /* 瘦鼻程度 */
            if (fuDiskFaceBeautyData.noseIntensity != faceBeauty.getNoseIntensity())
                faceBeauty.setNoseIntensity(fuDiskFaceBeautyData.noseIntensity);
            /* 嘴巴调整程度 */
            if (fuDiskFaceBeautyData.mouthIntensity != faceBeauty.getMouthIntensity())
                faceBeauty.setMouthIntensity(fuDiskFaceBeautyData.mouthIntensity);
            /* 开眼角强度 */
            if (fuDiskFaceBeautyData.canthusIntensity != faceBeauty.getCanthusIntensity())
                faceBeauty.setCanthusIntensity(fuDiskFaceBeautyData.canthusIntensity);
            /* 眼睛间距 */
            if (fuDiskFaceBeautyData.eyeSpaceIntensity != faceBeauty.getEyeSpaceIntensity())
                faceBeauty.setEyeSpaceIntensity(fuDiskFaceBeautyData.eyeSpaceIntensity);
            /* 眼睛角度 */
            if (fuDiskFaceBeautyData.eyeRotateIntensity != faceBeauty.getEyeRotateIntensity())
                faceBeauty.setEyeRotateIntensity(fuDiskFaceBeautyData.eyeRotateIntensity);
            /* 鼻子长度 */
            if (fuDiskFaceBeautyData.longNoseIntensity != faceBeauty.getLongNoseIntensity())
                faceBeauty.setLongNoseIntensity(fuDiskFaceBeautyData.longNoseIntensity);
            /* 调节人中 */
            if (fuDiskFaceBeautyData.philtrumIntensity != faceBeauty.getPhiltrumIntensity())
                faceBeauty.setPhiltrumIntensity(fuDiskFaceBeautyData.philtrumIntensity);
            /* 微笑嘴角强度 */
            if (fuDiskFaceBeautyData.smileIntensity != faceBeauty.getSmileIntensity())
                faceBeauty.setSmileIntensity(fuDiskFaceBeautyData.smileIntensity);
            /* 眉毛上下 */
            if (fuDiskFaceBeautyData.browHeightIntensity != faceBeauty.getBrowHeightIntensity())
                faceBeauty.setBrowHeightIntensity(fuDiskFaceBeautyData.browHeightIntensity);
            /* 眉毛间距 */
            if (fuDiskFaceBeautyData.browSpaceIntensity != faceBeauty.getBrowSpaceIntensity())
                faceBeauty.setBrowSpaceIntensity(fuDiskFaceBeautyData.browSpaceIntensity);
            /* 眼睑 */
            if (fuDiskFaceBeautyData.eyeLidIntensity != faceBeauty.getEyeLidIntensity())
                faceBeauty.setEyeLidIntensity(fuDiskFaceBeautyData.eyeLidIntensity);
            /* 眼睛高度 */
            if (fuDiskFaceBeautyData.eyeHeightIntensity != faceBeauty.getEyeHeightIntensity())
                faceBeauty.setEyeHeightIntensity(fuDiskFaceBeautyData.eyeHeightIntensity);
            /* 眉毛粗细 */
            if (fuDiskFaceBeautyData.browThickIntensity != faceBeauty.getBrowThickIntensity())
                faceBeauty.setBrowThickIntensity(fuDiskFaceBeautyData.browThickIntensity);
            /* 嘴巴厚度 */
            if (fuDiskFaceBeautyData.lipThickIntensity != faceBeauty.getLipThickIntensity())
                faceBeauty.setLipThickIntensity(fuDiskFaceBeautyData.lipThickIntensity);
            /* 五官立体 */
            if (fuDiskFaceBeautyData.faceThreeIntensity != faceBeauty.getFaceThreeIntensity())
                faceBeauty.setFaceThreeIntensity(fuDiskFaceBeautyData.faceThreeIntensity);

            /* 滤镜相关 */
            /* 滤镜名称 */
            if (!fuDiskFaceBeautyData.filterName.equals(faceBeauty.getFilterName()))
                faceBeauty.setFilterName(fuDiskFaceBeautyData.filterName);
            if (fuDiskFaceBeautyData.filterIntensity != faceBeauty.getFilterIntensity())
                faceBeauty.setFilterIntensity(fuDiskFaceBeautyData.filterIntensity);
            return true;
        }

        return false;
    }

    /**
     * 将FaceBeautyData保存到文件
     *
     * @param fuDiskFaceBeautyData
     */
    public static void saveFaceBeautyData2File(FUDiskFaceBeautyData fuDiskFaceBeautyData, FaceBeauty faceBeauty, ArrayList<FaceBeautyFilterBean> filterList) {
        buildFaceBeautyData(fuDiskFaceBeautyData,faceBeauty,filterList);
        saveFaceBeautyData2File(fuDiskFaceBeautyData);
    }

    /**
     * 将FaceBeautyData保存到文件
     *
     * @param fuDiskFaceBeautyData
     */
    public static void saveFaceBeautyData2File(FUDiskFaceBeautyData fuDiskFaceBeautyData) {
        Gson gson = new Gson();
        String faceBeautyString = gson.toJson(fuDiskFaceBeautyData);
        saveToSp(SP_KEY_NAME, faceBeautyString);
    }

    /**
     * 获取bean对象
     *
     * @return
     */
    public static FUDiskFaceBeautyData loadFaceBeautyData() {
        String faceBeautyData = loadFormSp(SP_KEY_NAME);
        if (faceBeautyData != null && !faceBeautyData.isEmpty()) {
            Gson gson = new Gson();
            FUDiskFaceBeautyData FUDiskFaceBeautyDataBean = gson.fromJson(faceBeautyData, FUDiskFaceBeautyData.class);
            return FUDiskFaceBeautyDataBean;
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
