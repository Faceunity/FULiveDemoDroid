package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.CartoonFilter;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 动漫滤镜枚举类
 *
 * @author Richie on 2018.11.14
 */
public enum CartoonFilterEnum {

    NO_FILTER(R.drawable.ic_delete_all, CartoonFilter.NO_FILTER),
    COMIC_FILTER(R.drawable.icon_animefilter, CartoonFilter.COMIC_FILTER),
    PORTRAIT_EFFECT(R.drawable.icon_portrait_dynamiceffect, CartoonFilter.PORTRAIT_EFFECT),
    SKETCH_FILTER(R.drawable.icon_sketchfilter, CartoonFilter.SKETCH_FILTER),
    OIL_PAINT_FILTER(R.drawable.icon_oilpainting, CartoonFilter.OIL_PAINTING),
    SAND_PAINT_FILTER(R.drawable.icon_sandlpainting, CartoonFilter.SAND_PAINTING),
    PEN_PAINT_FILTER(R.drawable.icon_penpainting, CartoonFilter.PEN_PAINTING),
    PENCIL_PAINT_FILTER(R.drawable.icon_pencilpainting, CartoonFilter.PENCIL_PAINTING),
    GRAFFITI_FILTER(R.drawable.icon_graffiti, CartoonFilter.GRAFFITI);

    private int iconId;
    private int style;

    CartoonFilterEnum(int iconId, int style) {
        this.iconId = iconId;
        this.style = style;
    }

    public static List<CartoonFilter> getAllCartoonFilters() {
        CartoonFilterEnum[] values = values();
        List<CartoonFilter> cartoonFilters = new ArrayList<>(values.length);
        for (CartoonFilterEnum value : values) {
            cartoonFilters.add(value.create());
        }
        return cartoonFilters;
    }

    public CartoonFilter create() {
        return new CartoonFilter(iconId, style);
    }
}
