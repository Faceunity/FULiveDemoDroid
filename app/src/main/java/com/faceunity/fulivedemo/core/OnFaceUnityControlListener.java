package com.faceunity.fulivedemo.core;


import com.faceunity.fulivedemo.entity.Effect;
import com.faceunity.fulivedemo.entity.Filter;

/**
 * FURenderer与界面之间的交互接口
 */
public interface OnFaceUnityControlListener {

    /**
     * 道具贴纸选择
     *
     * @param effectItemName 道具贴纸文件名
     */
    void onEffectSelected(Effect effectItemName);

    /**
     * 滤镜强度
     *
     * @param progress 滤镜强度
     */
    void onFilterLevelSelected(float progress);

    /**
     * 滤镜选择
     *
     * @param filterName 滤镜名称
     */
    void onFilterSelected(Filter filterName);

    /**
     * 精准磨皮
     *
     * @param isAll 是否开启精准磨皮（0关闭 1开启）
     */
    void onALLBlurLevelSelected(float isAll);

    /**
     * 美肤类型
     *
     * @param skinType 0:清晰美肤 1:朦胧美肤
     */
    void onBeautySkinTypeSelected(float skinType);

    /**
     * 磨皮选择
     *
     * @param level 磨皮level
     */
    void onBlurLevelSelected(float level);

    /**
     * 美白选择
     *
     * @param progress 美白
     */
    void onColorLevelSelected(float progress);

    /**
     * 红润
     */
    void onRedLevelSelected(float progress);

    /**
     * 亮眼
     */
    void onBrightEyesSelected(float progress);

    /**
     * 美牙
     */
    void onBeautyTeethSelected(float progress);

    /**
     * 是否开启新美型
     */
    void onOpenNewFaceShapeSelected(float isOpen);

    /**
     * 脸型选择
     */
    void onFaceShapeSelected(float faceShape);

    /**
     * 大眼选择
     *
     * @param progress 大眼
     */
    void onEnlargeEyeSelected(float progress);

    /**
     * 瘦脸选择
     *
     * @param progress 瘦脸
     */
    void onCheekThinSelected(float progress);

    /**
     * 下巴
     */
    void onChinLevelSelected(float progress);

    /**
     * 额头
     */
    void onForeheadLevelSelected(float progress);

    /**
     * 瘦鼻
     */
    void onThinNoseLevelSelected(float progress);


    /**
     * 嘴形
     */
    void onMouthShapeSelected(float progress);


}
