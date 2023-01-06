package com.faceunity.app.data.disksource.style;

import com.faceunity.core.model.facebeauty.FaceBeautyBlurTypeEnum;

import java.io.Serializable;

/**
 * 保存到磁盘的风格对象
 * 该例只保存特效demo展示出来的风格功能
 */
public class FUDiskStyleData implements Serializable {
    /* 美肤 */
    /*是否开启美肤效果*/
    public boolean faceBeautySkinEnable = true;
    /* 磨皮类型 */
    public int blurType = FaceBeautyBlurTypeEnum.FineSkin;
    /* 磨皮程度 */
    public double blurIntensity = 0.0;
    /* 美白程度 */
    public double colorIntensity = 0.0;
    /* 红润程度 */
    public double redIntensity = 0.0;
    /* 锐化程度 */
    public double sharpenIntensity = 0.0;
    /* 亮眼程度 */
    public double eyeBrightIntensity = 0.0;
    /* 美牙程度 */
    public double toothIntensity = 0.0;
    /* 去黑眼圈强度*/
    public double removePouchIntensity = 0.0;
    /* 去法令纹强度*/
    public double removeLawPatternIntensity = 0.0;

    /*美型*/
    /*是否开启美型效果*/
    public boolean faceBeautyShapeEnable = true;
    /* 瘦脸程度 */
    public double cheekThinningIntensity = 0.0;
    /* V脸程度 */
    public double cheekVIntensity = 0.0;
    /* 窄脸程度 */
    public double cheekNarrowIntensity = 0.0;
    /* 短脸程度 */
    public double cheekShortIntensity = 0.0;
    /* 小脸程度 */
    public double cheekSmallIntensity = 0.0;
    /* 瘦颧骨 */
    public double cheekBonesIntensity = 0.0;
    /* 瘦下颌骨 */
    public double lowerJawIntensity = 0.0;
    /* 大眼程度 */
    public double eyeEnlargingIntensity = 0.0;
    /* 圆眼程度 */
    public double eyeCircleIntensity = 0.0;
    /* 下巴调整程度 */
    public double chinIntensity = 0.5;
    /* 额头调整程度 */
    public double forHeadIntensity = 0.5;
    /* 瘦鼻程度 */
    public double noseIntensity = 0.0;
    /* 嘴巴调整程度 */
    public double mouthIntensity = 0.5;
    /* 开眼角强度 */
    public double canthusIntensity = 0.0;
    /* 眼睛间距 */
    public double eyeSpaceIntensity = 0.5;
    /* 眼睛角度 */
    public double eyeRotateIntensity = 0.5;
    /* 鼻子长度 */
    public double longNoseIntensity = 0.5;
    /* 调节人中 */
    public double philtrumIntensity = 0.5;
    /* 微笑嘴角强度 */
    public double smileIntensity = 0.0;
    /* 眉毛上下 */
    public double browHeightIntensity = 0.5;
    /* 眉毛间距 */
    public double browSpaceIntensity = 0.5;
    /* 眼睑 */
    public double eyeLidIntensity = 0.0;
    /* 眼睛高度 */
    public double eyeHeightIntensity = 0.5;
    /* 眉毛粗细 */
    public double browThickIntensity = 0.5;
    /* 嘴巴厚度 */
    public double lipThickIntensity = 0.5;
    /* 五官立体 */
    public double faceThreeIntensity = 0.5;

    /* 风格滤镜程度 */
    public double filterIntensity = 0.0f;

    /*风格美妆道具路径*/
    public String makeupPath = "";
    /*风格美妆强度*/
    public double makeupIntensity = 0.0f;

    /*选中的风格类型，默认选中原生*/
    public boolean isSelect = false;
}
