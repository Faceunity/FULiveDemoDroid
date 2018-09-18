package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;

/**
 * Created by tujh on 2018/1/30.
 */

public enum FilterEnum {

    nature("origin", R.drawable.nature, "origin", Filter.FILTER_TYPE_FILTER),
    delta("delta", R.drawable.delta, "delta", Filter.FILTER_TYPE_FILTER),
    electric("electric", R.drawable.electric, "electric", Filter.FILTER_TYPE_FILTER),
    slowlived("slowlived", R.drawable.slowlived, "slowlived", Filter.FILTER_TYPE_FILTER),
    tokyo("tokyo", R.drawable.tokyo, "tokyo", Filter.FILTER_TYPE_FILTER),
    warm("warm", R.drawable.warm, "warm", Filter.FILTER_TYPE_FILTER),

    nature_beauty("origin", R.drawable.nature, "原图", Filter.FILTER_TYPE_BEAUTY_FILTER),
    ziran("ziran", R.drawable.origin, "自然", Filter.FILTER_TYPE_BEAUTY_FILTER),
    danya("danya", R.drawable.qingxin, "淡雅", Filter.FILTER_TYPE_BEAUTY_FILTER),
    fennen("fennen", R.drawable.shaonv, "粉嫩", Filter.FILTER_TYPE_BEAUTY_FILTER),
    qingxin("qingxin", R.drawable.ziran, "清新", Filter.FILTER_TYPE_BEAUTY_FILTER),
    hongrun("hongrun", R.drawable.hongrun, "红润", Filter.FILTER_TYPE_BEAUTY_FILTER);

    private String filterName;
    private int resId;
    private String description;
    private int filterType;

    FilterEnum(String name, int resId, String description, int filterType) {
        this.filterName = name;
        this.resId = resId;
        this.description = description;
        this.filterType = filterType;
    }

    public String filterName() {
        return filterName;
    }

    public int resId() {
        return resId;
    }

    public String description() {
        return description;
    }

    public Filter filter() {
        return new Filter(filterName, resId, description, filterType);
    }

    public static ArrayList<Filter> getFiltersByFilterType(int filterType) {
        ArrayList<Filter> filters = new ArrayList<>();
        for (FilterEnum f : FilterEnum.values()) {
            if (f.filterType == filterType) {
                filters.add(f.filter());
            }
        }
        return filters;
    }
}
