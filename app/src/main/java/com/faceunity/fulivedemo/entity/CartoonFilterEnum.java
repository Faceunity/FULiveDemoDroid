package com.faceunity.fulivedemo.entity;

import com.faceunity.FURenderer;
import com.faceunity.entity.CartoonFilter;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuQiang on 2018.11.14
 */
public enum CartoonFilterEnum {
    /**
     * 风格滤镜
     */
    NO_FILTER(R.drawable.ic_delete_all, "无效果", FURenderer.NO_FILTER),
    COMIC_FILTER(R.drawable.icon_animefilter, "漫画滤镜", FURenderer.COMIC_FILTER),
    PORTRAIT_EFFECT(R.drawable.icon_portrait_dynamiceffect, "人像动效", FURenderer.PORTRAIT_EFFECT),
    SKETCH_FILTER(R.drawable.icon_sketchfilter, "素描滤镜", FURenderer.SKETCH_FILTER);

    private int imageResId;
    private String name;
    private int style;

    CartoonFilterEnum(int imageResId, String name, int style) {
        this.imageResId = imageResId;
        this.name = name;
        this.style = style;
    }

    public static List<CartoonFilter> getAllCartoonFilters() {
        CartoonFilterEnum[] values = values();
        List<CartoonFilter> cartoonFilters = new ArrayList<>(values.length);
        for (CartoonFilterEnum value : values) {
            cartoonFilters.add(new CartoonFilter(value.imageResId, value.name, value.style));
        }
        return cartoonFilters;
    }
}
