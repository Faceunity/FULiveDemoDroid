package com.faceunity.entity;

/**
 * @author LiuQiang on 2018.11.14
 * 动漫滤镜
 */
public class CartoonFilter {
    private int imageResId;
    private String name;
    private int style;

    public CartoonFilter(int imageResId, String name, int style) {
        this.imageResId = imageResId;
        this.name = name;
        this.style = style;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "imageResId=" + imageResId +
                ", name='" + name + '\'' +
                ", style=" + style +
                '}';
    }
}
