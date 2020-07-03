package com.faceunity.param;

/**
 * 卡通滤镜参数
 *
 * @author Richie on 2020.05.07
 */
public final class CartoonFilterParam {
    /**
     * 滤镜样式，0-7，见下方枚举
     */
    public static final String STYLE = "style";
    /**
     * OpenGLES 版本
     */
    public static final String GLVER = "glVer";

    /**
     * 无
     */
    public static final int NO_FILTER = -1;
    /**
     * 0. 动漫
     */
    public static final int COMIC_FILTER = 0;
    /**
     * 1. 素描
     */
    public static final int SKETCH_FILTER = 1;
    /**
     * 2. 人像
     */
    public static final int PORTRAIT_EFFECT = 2;
    /**
     * 3. 油画
     */
    public static final int OIL_PAINTING = 3;
    /**
     * 4. 沙画
     */
    public static final int SAND_PAINTING = 4;
    /**
     * 5. 钢笔画
     */
    public static final int PEN_PAINTING = 5;
    /**
     * 6. 铅笔画
     */
    public static final int PENCIL_PAINTING = 6;
    /**
     * 7. 涂鸦
     */
    public static final int GRAFFITI = 7;
}
