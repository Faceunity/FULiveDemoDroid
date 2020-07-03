package com.faceunity.fulivedemo.entity;

import com.faceunity.fulivedemo.R;
import com.faceunity.param.CartoonFilterParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 动漫滤镜枚举类
 *
 * @author Richie on 2018.11.14
 */
public enum CartoonFilterEnum {

    NO_FILTER(R.drawable.ic_delete_all, CartoonFilterParam.NO_FILTER),

    COMIC_FILTER(R.drawable.icon_animefilter, CartoonFilterParam.COMIC_FILTER),
    PORTRAIT_EFFECT(R.drawable.icon_portrait_dynamiceffect, CartoonFilterParam.PORTRAIT_EFFECT),
    SKETCH_FILTER(R.drawable.icon_sketchfilter, CartoonFilterParam.SKETCH_FILTER),
    OIL_PAINT_FILTER(R.drawable.icon_oilpainting, CartoonFilterParam.OIL_PAINTING),
    SAND_PAINT_FILTER(R.drawable.icon_sandlpainting, CartoonFilterParam.SAND_PAINTING),
    PEN_PAINT_FILTER(R.drawable.icon_penpainting, CartoonFilterParam.PEN_PAINTING),
    PENCIL_PAINT_FILTER(R.drawable.icon_pencilpainting, CartoonFilterParam.PENCIL_PAINTING),
    GRAFFITI_FILTER(R.drawable.icon_graffiti, CartoonFilterParam.GRAFFITI);

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
