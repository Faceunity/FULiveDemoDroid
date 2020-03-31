package com.faceunity;


import com.faceunity.entity.Effect;
import com.faceunity.entity.LightMakeupItem;
import com.faceunity.entity.LivePhoto;
import com.faceunity.entity.MakeupEntity;

import java.util.List;
import java.util.Map;

/**
 * FURenderer与界面之间的交互接口
 */
public interface OnFUControlListener {
    /**
     * 美颜道具全局开关
     *
     * @param isOn
     */
    void setBeautificationOn(boolean isOn);

    /**
     * 音乐滤镜时间
     *
     * @param time
     */
    void onMusicFilterTime(long time);

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
    void onFilterNameSelected(String filterName);

    /**
     * 美发颜色
     *
     * @param type
     * @param hairColorIndex 美发颜色
     * @param hairColorLevel 美发颜色强度
     */
    void onHairSelected(int type, int hairColorIndex, float hairColorLevel);

    /**
     * 调整美发强度
     *
     * @param type
     * @param hairColorIndex
     * @param hairColorLevel
     */
    void onHairLevelSelected(int type, int hairColorIndex, float hairColorLevel);

    /**
     * 磨皮类型
     *
     * @param blurType 0 清晰磨皮，1 重度磨皮，2 精细磨皮
     */
    void onBlurTypeSelected(float blurType);

    /**
     * 磨皮程度
     *
     * @param level 磨皮程度 [0, 6]，默认 6.0
     */
    void onBlurLevelSelected(float level);

    /**
     * 美白选择
     *
     * @param level 美白
     */
    void onColorLevelSelected(float level);

    /**
     * 红润
     */
    void onRedLevelSelected(float level);

    /**
     * 亮眼
     */
    void onEyeBrightSelected(float level);

    /**
     * 美牙
     */
    void onToothWhitenSelected(float level);

    /**
     * 大眼选择
     *
     * @param level 大眼
     */
    void onEyeEnlargeSelected(float level);

    /**
     * 瘦脸选择
     *
     * @param level 瘦脸
     */
    void onCheekThinningSelected(float level);

    /**
     * 下巴
     */
    void onIntensityChinSelected(float level);

    /**
     * 额头
     */
    void onIntensityForeheadSelected(float level);

    /**
     * 瘦鼻
     */
    void onIntensityNoseSelected(float level);


    /**
     * 嘴形
     */
    void onIntensityMouthSelected(float level);

    /**
     * 窄脸选择
     *
     * @param level
     */
    void onCheekNarrowSelected(float level);

    /**
     * 小脸选择
     *
     * @param level
     */
    void onCheekSmallSelected(float level);

    /**
     * V脸选择
     *
     * @param level
     */
    void onCheekVSelected(float level);

    /**
     * 切换海报模板
     *
     * @param tempWidth
     * @param tempHeight
     * @param temp
     * @param landmark
     */
    void onPosterTemplateSelected(int tempWidth, int tempHeight, byte[] temp, float[] landmark);

    /**
     * 海报换脸输入照片
     *
     * @param inputWidth
     * @param inputHeight
     * @param input
     * @param landmark
     */
    void onPosterInputPhoto(int inputWidth, int inputHeight, byte[] input, float[] landmark);

    /**
     * 设置风格滤镜
     *
     * @param style
     */
    void setCartoonFilter(int style);

    /**
     * 调节多个轻美妆的妆容
     *
     * @param makeupItems
     */
    void onLightMakeupCombinationSelected(List<LightMakeupItem> makeupItems);

    /**
     * 轻美妆妆容调节强度
     *
     * @param makeupItem
     */
    void onLightMakeupItemLevelChanged(LightMakeupItem makeupItem);

    /**
     * 设置表情动图的点位和图像数据，用来驱动图像
     *
     * @param livePhoto
     */
    void setLivePhoto(LivePhoto livePhoto);

    /**
     * 设置美妆组合妆容
     *
     * @param makeupEntity
     * @param paramMap
     */
    void selectMakeup(MakeupEntity makeupEntity, Map<String, Object> paramMap);

    /**
     * 设置美妆妆容参数
     *
     * @param paramMap
     */
    void setMakeupItemParam(Map<String, Object> paramMap);

    /**
     * 调节美妆妆容强度
     *
     * @param name
     * @param density
     */
    void setMakeupItemIntensity(String name, double density);

    /**
     * 设置美妆妆容颜色
     *
     * @param name
     * @param colors RGBA color
     */
    void setMakeupItemColor(String name, double[] colors);

    /**
     * 设置瘦身程度
     *
     * @param intensity
     */
    void setBodySlimIntensity(float intensity);

    /**
     * 设置长腿程度
     *
     * @param intensity
     */
    void setLegSlimIntensity(float intensity);

    /**
     * 设置细腰程度
     *
     * @param intensity
     */
    void setWaistSlimIntensity(float intensity);

    /**
     * 设置美肩程度
     *
     * @param intensity
     */
    void setShoulderSlimIntensity(float intensity);

    /**
     * 设置美臀程度
     *
     * @param intensity
     */
    void setHipSlimIntensity(float intensity);

    /**
     * 设置去黑眼圈强度
     *
     * @param strength
     */
    void setRemovePouchStrength(float strength);

    /**
     * 设置去法令纹强度
     *
     * @param strength
     */
    void setRemoveNasolabialFoldsStrength(float strength);

    /**
     * 设置微笑嘴角强度
     *
     * @param intensity
     */
    void setSmileIntensity(float intensity);

    /**
     * 设置开眼角强度
     *
     * @param intensity
     */
    void setCanthusIntensity(float intensity);

    /**
     * 设置人中长度
     *
     * @param intensity
     */
    void setPhiltrumIntensity(float intensity);

    /**
     * 设置鼻子长度
     *
     * @param intensity
     */
    void setLongNoseIntensity(float intensity);

    /**
     * 设置眼睛间距
     *
     * @param intensity
     */
    void setEyeSpaceIntensity(float intensity);

    /**
     * 设置眼睛角度
     *
     * @param intensity
     */
    void setEyeRotateIntensity(float intensity);
}
