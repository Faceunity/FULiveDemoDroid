package com.faceunity.fulivedemo.entity;

/**
 * 本demo中滤镜的实体类
 * Created by tujh on 2018/2/7.
 */

public class Filter {

    public static final int FILTER_TYPE_FILTER = 0;
    public static final int FILTER_TYPE_BEAUTY_FILTER = 1;

    private String filterName;
    private int resId;
    private String description;
    private int filterType;

    public Filter(String filterName, int resId, String description, int filterType) {
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

    public String description() {
        return description;
    }
}
