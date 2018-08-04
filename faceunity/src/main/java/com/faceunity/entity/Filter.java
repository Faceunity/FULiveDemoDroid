package com.faceunity.entity;

import android.text.TextUtils;

/**
 * 本demo中滤镜的实体类
 * Created by tujh on 2018/2/7.
 */

public class Filter {

    public static final int FILTER_TYPE_FILTER = 0;
    public static final int FILTER_TYPE_BEAUTY_FILTER = 1;

    private String filterName;
    private int resId;
    private int description;
    private int filterType;

    public Filter(String filterName) {
        this.filterName = filterName;
    }

    public Filter(String filterName, int resId, int description, int filterType) {
        this.filterName = filterName;
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

    public int filterType() {
        return filterType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filter filter = (Filter) o;
        return !TextUtils.isEmpty(filterName) && filterName.equals(filter.filterName());
    }

    @Override
    public int hashCode() {
        return !TextUtils.isEmpty(filterName) ? filterName.hashCode() : 0;
    }
}
