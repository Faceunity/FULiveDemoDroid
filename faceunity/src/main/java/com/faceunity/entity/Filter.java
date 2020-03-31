package com.faceunity.entity;

/**
 * 美颜滤镜
 * <p>
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
        return name != null ? name.equals(filter.name) : filter.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "name='" + name + '\'' +
                ", iconId=" + iconId +
                ", nameId=" + nameId +
                '}';
    }

    /**
     * 滤镜使用的 key
     */
    public static class Key {
        public static final String ORIGIN = "origin";
        public static final String FENNEN_1 = "fennen1";
        public static final String FENNEN_2 = "fennen2";
        public static final String FENNEN_3 = "fennen3";
        public static final String FENNEN_4 = "fennen4";
        public static final String FENNEN_5 = "fennen5";
        public static final String FENNEN_6 = "fennen6";
        public static final String FENNEN_7 = "fennen7";
        public static final String FENNEN_8 = "fennen8";
        public static final String XIAOQINGXIN_1 = "xiaoqingxin1";
        public static final String XIAOQINGXIN_2 = "xiaoqingxin2";
        public static final String XIAOQINGXIN_3 = "xiaoqingxin3";
        public static final String XIAOQINGXIN_4 = "xiaoqingxin4";
        public static final String XIAOQINGXIN_5 = "xiaoqingxin5";
        public static final String XIAOQINGXIN_6 = "xiaoqingxin6";
        public static final String BAILIANG_1 = "bailiang1";
        public static final String BAILIANG_2 = "bailiang2";
        public static final String BAILIANG_3 = "bailiang3";
        public static final String BAILIANG_4 = "bailiang4";
        public static final String BAILIANG_5 = "bailiang5";
        public static final String BAILIANG_6 = "bailiang6";
        public static final String BAILIANG_7 = "bailiang7";
        public static final String LENGSEDIAO_1 = "lengsediao1";
        public static final String LENGSEDIAO_2 = "lengsediao2";
        public static final String LENGSEDIAO_3 = "lengsediao3";
        public static final String LENGSEDIAO_4 = "lengsediao4";
        public static final String LENGSEDIAO_5 = "lengsediao5";
        public static final String LENGSEDIAO_6 = "lengsediao6";
        public static final String LENGSEDIAO_7 = "lengsediao7";
        public static final String LENGSEDIAO_8 = "lengsediao8";
        public static final String LENGSEDIAO_9 = "lengsediao9";
        public static final String LENGSEDIAO_10 = "lengsediao10";
        public static final String LENGSEDIAO_11 = "lengsediao11";
        public static final String NUANSEDIAO_1 = "nuansediao1";
        public static final String NUANSEDIAO_2 = "nuansediao2";
        public static final String NUANSEDIAO_3 = "nuansediao3";
        public static final String HEIBAI_1 = "heibai1";
        public static final String HEIBAI_2 = "heibai2";
        public static final String HEIBAI_3 = "heibai3";
        public static final String HEIBAI_4 = "heibai4";
        public static final String HEIBAI_5 = "heibai5";
        public static final String GEXING_1 = "gexing1";
        public static final String GEXING_2 = "gexing2";
        public static final String GEXING_3 = "gexing3";
        public static final String GEXING_4 = "gexing4";
        public static final String GEXING_5 = "gexing5";
        public static final String GEXING_6 = "gexing6";
        public static final String GEXING_7 = "gexing7";
        public static final String GEXING_8 = "gexing8";
        public static final String GEXING_9 = "gexing9";
        public static final String GEXING_10 = "gexing10";
        public static final String GEXING_11 = "gexing11";
        public static final String ZIRAN_1 = "ziran1";
        public static final String ZIRAN_2 = "ziran2";
        public static final String ZIRAN_3 = "ziran3";
        public static final String ZIRAN_4 = "ziran4";
        public static final String ZIRAN_5 = "ziran5";
        public static final String ZIRAN_6 = "ziran6";
        public static final String ZIRAN_7 = "ziran7";
        public static final String ZIRAN_8 = "ziran8";
        public static final String ZHIGANHUI_1 = "zhiganhui1";
        public static final String ZHIGANHUI_2 = "zhiganhui2";
        public static final String ZHIGANHUI_3 = "zhiganhui3";
        public static final String ZHIGANHUI_4 = "zhiganhui4";
        public static final String ZHIGANHUI_5 = "zhiganhui5";
        public static final String ZHIGANHUI_6 = "zhiganhui6";
        public static final String ZHIGANHUI_7 = "zhiganhui7";
        public static final String ZHIGANHUI_8 = "zhiganhui8";
    }

}