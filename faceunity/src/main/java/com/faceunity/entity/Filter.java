package com.faceunity.entity;

import java.util.Objects;

/**
 * 美颜滤镜
 * Created by tujh on 2018/2/7.
 */
public class Filter {
    private String name;
    private int iconId;
    private int nameId;

    public static Filter create(String name) {
        return new Filter(name);
    }

    private Filter(String name) {
        this.name = name;
    }

    public Filter(String name, int iconId, int nameId) {
        this.name = name;
        this.iconId = iconId;
        this.nameId = nameId;
    }

    public String getName() {
        return name;
    }

    public int getIconId() {
        return iconId;
    }

    public int getNameId() {
        return nameId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Filter filter = (Filter) o;
        return Objects.equals(name, filter.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "name='" + name + '\'' +
                ", iconId=" + iconId +
                ", nameId=" + nameId +
                '}';
    }
}