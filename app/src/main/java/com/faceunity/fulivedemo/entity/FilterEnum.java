package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;

/**
 * Created by tujh on 2018/1/30.
 */

public enum FilterEnum {

    nature("origin", R.drawable.nature, R.string.origin, Filter.FILTER_TYPE_FILTER),
    delta("delta", R.drawable.delta, R.string.delta, Filter.FILTER_TYPE_FILTER),
    electric("electric", R.drawable.electric, R.string.electric, Filter.FILTER_TYPE_FILTER),
    slowlived("slowlived", R.drawable.slowlived, R.string.slowlived, Filter.FILTER_TYPE_FILTER),
    tokyo("tokyo", R.drawable.tokyo, R.string.tokyo, Filter.FILTER_TYPE_FILTER),
    warm("warm", R.drawable.warm, R.string.warm, Filter.FILTER_TYPE_FILTER),

    nature_beauty("origin", R.drawable.nature, R.string.origin_beauty, Filter.FILTER_TYPE_BEAUTY_FILTER),
    ziran("ziran", R.drawable.origin, R.string.ziran, Filter.FILTER_TYPE_BEAUTY_FILTER),
    danya("danya", R.drawable.qingxin, R.string.danya, Filter.FILTER_TYPE_BEAUTY_FILTER),
    fennen("fennen", R.drawable.shaonv, R.string.fennen, Filter.FILTER_TYPE_BEAUTY_FILTER),
    qingxin("qingxin", R.drawable.ziran, R.string.qingxin, Filter.FILTER_TYPE_BEAUTY_FILTER),
    hongrun("hongrun", R.drawable.hongrun, R.string.hongrun, Filter.FILTER_TYPE_BEAUTY_FILTER);

    private String filterName;
    private int resId;
    private int description;
    private int filterType;

    FilterEnum(String name, int resId, int description, int filterType) {
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

    public int description() {
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
