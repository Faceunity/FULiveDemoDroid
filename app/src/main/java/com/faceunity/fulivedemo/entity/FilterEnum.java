package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;

/**
 * Created by tujh on 2018/1/30.
 */
public enum FilterEnum {

    /**
     * 滤镜资源
     */
    nature(Filter.Key.ORIGIN, R.drawable.nature, R.string.origin),
    bailiang(Filter.Key.BAILIANG_2, R.drawable.bailiang2, R.string.bailiang),
    fennen(Filter.Key.FENNEN_1, R.drawable.fennen1, R.string.fennen),
    xiaoqingxin(Filter.Key.XIAOQINGXIN_6, R.drawable.xiaoqingxin6, R.string.qingxin),
    lengsediao(Filter.Key.LENGSEDIAO_1, R.drawable.lengsediao1, R.string.lengsediao),
    nuansediao(Filter.Key.NUANSEDIAO_1, R.drawable.nuansediao1, R.string.nuansediao);

    private String filterName;
    private int resId;
    private int description;

    FilterEnum(String name, int resId, int description) {
        this.filterName = name;
        this.resId = resId;
        this.description = description;
    }

    public String filterName() {
        return filterName;
    }

    public int resId() {
        return resId;
    }

    public int description() {
        return description;
    }

    public static ArrayList<Filter> getFiltersByFilterType() {
        FilterEnum[] values = FilterEnum.values();
        ArrayList<Filter> filters = new ArrayList<>(values.length);
        for (FilterEnum f : values) {
            filters.add(f.filter());
        }
        return filters;
    }

    public Filter filter() {
        return new Filter(filterName, resId, description);
    }
}
