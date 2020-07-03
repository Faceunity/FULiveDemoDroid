package com.faceunity.fulivedemo.entity;

/**
 * 动漫滤镜
 *
 * @author Richie on 2018.11.14
 */
public class CartoonFilter {
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
