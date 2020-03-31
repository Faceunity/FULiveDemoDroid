package com.faceunity.param;

/**
 * 美颜道具参数，包含红润、美白、磨皮、滤镜、变形、亮眼、美牙等功能。
 *
 * @author Richie on 2019.07.18
 */
public class BeautificationParam {
    /**
     * 美颜参数全局开关，0 关，1 开，默认 1
     */
    public static final String IS_BEAUTY_ON = "is_beauty_on";
    /**
     * 滤镜名称，默认 origin
     */
    public static final String FILTER_NAME = "filter_name";
    /**
     * 滤镜程度，范围 [0-1]，默认 1
     */
    public static final String FILTER_LEVEL = "filter_level";
    /**
     * 美白程度，范围 [0-2]，默认 0.2
     */
    public static final String COLOR_LEVEL = "color_level";
    /**
     * 红润程度，范围 [0-2]，默认 0.5
     */
    public static final String RED_LEVEL = "red_level";
    /**
     * 磨皮程度，范围 [0-6]，默认 6
     */
    public static final String BLUR_LEVEL = "blur_level";
    /**
     * 磨皮类型，0 清晰磨皮，1 朦胧磨皮，2 精细磨皮。当 heavy_blur 为 0 时生效
     */
    public static final String BLUR_TYPE = "blur_type";
    /**
     * 肤色检测开关，0 关，1 开，默认 0
     */
    public static final String SKIN_DETECT = "skin_detect";
    /**
     * 肤色检测开启后，非肤色区域的融合程度，范围 [0-1]，默认 0.45
     */
    public static final String NONSKIN_BLUR_SCALE = "nonskin_blur_scale";
    /**
     * 磨皮类型，0 清晰磨皮，1 重度磨皮，默认 1
     */
    public static final String HEAVY_BLUR = "heavy_blur";
    /**
     * 亮眼程度，范围 [0-1]，默认 1
     */
    public static final String EYE_BRIGHT = "eye_bright";
    /**
     * 美牙程度，范围 [0-1]，默认 1
     */
    public static final String TOOTH_WHITEN = "tooth_whiten";
    /**
     * 变形选择，0 女神，1 网红，2 自然，3 默认，4 精细变形，默认 3
     */
    public static final String FACE_SHAPE = "face_shape";
    /**
     * 变形程度，0-1，默认 1
     */
    public static final String FACE_SHAPE_LEVEL = "face_shape_level";
    /**
     * 大眼程度，范围 [0-1]，默认 0.5
     */
    public static final String EYE_ENLARGING = "eye_enlarging";
    /**
     * 瘦脸程度，范围 [0-1]，默认 0
     */
    public static final String CHEEK_THINNING = "cheek_thinning";
    /**
     * 窄脸程度，范围 [0-1]，默认 0
     */
    public static final String CHEEK_NARROW = "cheek_narrow";
    /**
     * 小脸程度，范围 [0-1]，默认 0
     */
    public static final String CHEEK_SMALL = "cheek_small";
    /**
     * V脸程度，范围 [0-1]，默认 0
     */
    public static final String CHEEK_V = "cheek_v";
    /**
     * 瘦鼻程度，范围 [0-1]，默认 0
     */
    public static final String INTENSITY_NOSE = "intensity_nose";
    /**
     * 嘴巴调整程度，范围 [0-1]，默认 0.5
     */
    public static final String INTENSITY_MOUTH = "intensity_mouth";
    /**
     * 额头调整程度，范围 [0-1]，默认 0.5
     */
    public static final String INTENSITY_FOREHEAD = "intensity_forehead";
    /**
     * 下巴调整程度，范围 [0-1]，默认 0.5
     */
    public static final String INTENSITY_CHIN = "intensity_chin";
    /**
     * 变形渐变调整参数，0 渐变关闭，大于 0 渐变开启，值为渐变需要的帧数
     */
    public static final String CHANGE_FRAMES = "change_frames";
    /**
     * 去黑眼圈强度，0.0 到 1.0 变强
     */
    public static final String REMOVE_POUCH_STRENGTH = "remove_pouch_strength";
    /**
     * 去法令纹强度，0.0 到 1.0 变强
     */
    public static final String REMOVE_NASOLABIAL_FOLDS_STRENGTH = "remove_nasolabial_folds_strength";
    /**
     * 微笑嘴角强度，0.0 到 1.0 变强
     */
    public static final String INTENSITY_SMILE = "intensity_smile";
    /**
     * 开眼角强度，0.0 到 1.0 变强
     */
    public static final String INTENSITY_CANTHUS = "intensity_canthus";
    /**
     * 调节人中，0.5 到 1.0 是逐渐缩短，0.5 到 0.0 是逐渐增长
     */
    public static final String INTENSITY_PHILTRUM = "intensity_philtrum";
    /**
     * 鼻子长度，0.5 到 1.0 是逐渐缩短，0.5 到 0.0 是逐渐增长
     */
    public static final String INTENSITY_LONG_NOSE = "intensity_long_nose";
    /**
     * 眼睛间距，0.5 到 1.0 是逐渐缩短，0.5 到 0.0 是逐渐增长
     */
    public static final String INTENSITY_EYE_SPACE = "intensity_eye_space";
    /**
     * 眼睛角度，0.5 到 1.0 眼角向下旋转，0.5 到 0.0 眼角向上旋转
     */
    public static final String INTENSITY_EYE_ROTATE = "intensity_eye_rotate";
}
