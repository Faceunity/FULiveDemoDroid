package com.faceunity.entity;

/**
 * 动漫滤镜
 *
 * @author Richie on 2018.11.14
 */
public class CartoonFilter {
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

    private int iconId;
    private int style;

    public CartoonFilter(int iconId, int style) {
        this.iconId = iconId;
        this.style = style;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return "CartoonFilter{" +
                "iconId=" + iconId +
                ", style=" + style +
                '}';
    }
}
