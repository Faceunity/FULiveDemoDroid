package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;

/**
 * 美颜滤镜枚举类
 * <p>
 * Created by tujh on 2018/1/30.
 */
public enum FilterEnum {

    origin(Filter.Key.ORIGIN, R.drawable.demo_icon_cancel, R.string.origin),

    ziran_1(Filter.Key.ZIRAN_1, R.drawable.demo_icon_natural_1, R.string.ziran_1),
    ziran_2(Filter.Key.ZIRAN_2, R.drawable.demo_icon_natural_2, R.string.ziran_2),
    ziran_3(Filter.Key.ZIRAN_3, R.drawable.demo_icon_natural_3, R.string.ziran_3),
    ziran_4(Filter.Key.ZIRAN_4, R.drawable.demo_icon_natural_4, R.string.ziran_4),
    ziran_5(Filter.Key.ZIRAN_5, R.drawable.demo_icon_natural_5, R.string.ziran_5),
    ziran_6(Filter.Key.ZIRAN_6, R.drawable.demo_icon_natural_6, R.string.ziran_6),
    ziran_7(Filter.Key.ZIRAN_7, R.drawable.demo_icon_natural_7, R.string.ziran_7),
    ziran_8(Filter.Key.ZIRAN_8, R.drawable.demo_icon_natural_8, R.string.ziran_8),

    zhiganhui_1(Filter.Key.ZHIGANHUI_1, R.drawable.demo_icon_texture_gray1, R.string.zhiganhui_1),
    zhiganhui_2(Filter.Key.ZHIGANHUI_2, R.drawable.demo_icon_texture_gray2, R.string.zhiganhui_2),
    zhiganhui_3(Filter.Key.ZHIGANHUI_3, R.drawable.demo_icon_texture_gray3, R.string.zhiganhui_3),
    zhiganhui_4(Filter.Key.ZHIGANHUI_4, R.drawable.demo_icon_texture_gray4, R.string.zhiganhui_4),
    zhiganhui_5(Filter.Key.ZHIGANHUI_5, R.drawable.demo_icon_texture_gray5, R.string.zhiganhui_5),
    zhiganhui_6(Filter.Key.ZHIGANHUI_6, R.drawable.demo_icon_texture_gray6, R.string.zhiganhui_6),
    zhiganhui_7(Filter.Key.ZHIGANHUI_7, R.drawable.demo_icon_texture_gray7, R.string.zhiganhui_7),
    zhiganhui_8(Filter.Key.ZHIGANHUI_8, R.drawable.demo_icon_texture_gray8, R.string.zhiganhui_8),

    bailiang_1(Filter.Key.BAILIANG_1, R.drawable.demo_icon_bailiang1, R.string.bailiang_1),
    bailiang_2(Filter.Key.BAILIANG_2, R.drawable.demo_icon_bailiang2, R.string.bailiang_2),
    bailiang_3(Filter.Key.BAILIANG_3, R.drawable.demo_icon_bailiang3, R.string.bailiang_3),
    bailiang_4(Filter.Key.BAILIANG_4, R.drawable.demo_icon_bailiang4, R.string.bailiang_4),
    bailiang_5(Filter.Key.BAILIANG_5, R.drawable.demo_icon_bailiang5, R.string.bailiang_5),
    bailiang_6(Filter.Key.BAILIANG_6, R.drawable.demo_icon_bailiang6, R.string.bailiang_6),
    bailiang_7(Filter.Key.BAILIANG_7, R.drawable.demo_icon_bailiang7, R.string.bailiang_7),

    fennen_1(Filter.Key.FENNEN_1, R.drawable.demo_icon_fennen1, R.string.fennen_1),
    fennen_2(Filter.Key.FENNEN_2, R.drawable.demo_icon_fennen2, R.string.fennen_2),
    fennen_3(Filter.Key.FENNEN_3, R.drawable.demo_icon_fennen3, R.string.fennen_3),
    //    fennen_4(Filter.Key.FENNEN_4, R.drawable.demo_icon_fennen4, R.string.fennen_4),
    fennen_5(Filter.Key.FENNEN_5, R.drawable.demo_icon_fennen5, R.string.fennen_5),
    fennen_6(Filter.Key.FENNEN_6, R.drawable.demo_icon_fennen6, R.string.fennen_6),
    fennen_7(Filter.Key.FENNEN_7, R.drawable.demo_icon_fennen7, R.string.fennen_7),
    fennen_8(Filter.Key.FENNEN_8, R.drawable.demo_icon_fennen8, R.string.fennen_8),

    lengsediao_1(Filter.Key.LENGSEDIAO_1, R.drawable.demo_icon_lengsediao1, R.string.lengsediao_1),
    lengsediao_2(Filter.Key.LENGSEDIAO_2, R.drawable.demo_icon_lengsediao2, R.string.lengsediao_2),
    lengsediao_3(Filter.Key.LENGSEDIAO_3, R.drawable.demo_icon_lengsediao3, R.string.lengsediao_3),
    lengsediao_4(Filter.Key.LENGSEDIAO_4, R.drawable.demo_icon_lengsediao4, R.string.lengsediao_4),
    //    lengsediao_5(Filter.Key.LENGSEDIAO_5, R.drawable.demo_icon_lengsediao5, R.string.lengsediao_5),
    //    lengsediao_6(Filter.Key.LENGSEDIAO_6, R.drawable.demo_icon_lengsediao6, R.string.lengsediao_6),
    lengsediao_7(Filter.Key.LENGSEDIAO_7, R.drawable.demo_icon_lengsediao7, R.string.lengsediao_7),
    lengsediao_8(Filter.Key.LENGSEDIAO_8, R.drawable.demo_icon_lengsediao8, R.string.lengsediao_8),
    //    lengsediao_9(Filter.Key.LENGSEDIAO_9, R.drawable.demo_icon_lengsediao9, R.string.lengsediao_9),
    //    lengsediao_10(Filter.Key.LENGSEDIAO_10, R.drawable.demo_icon_lengsediao10, R.string.lengsediao_10),
    lengsediao_11(Filter.Key.LENGSEDIAO_11, R.drawable.demo_icon_lengsediao11, R.string.lengsediao_11),

    nuansediao_1(Filter.Key.NUANSEDIAO_1, R.drawable.demo_icon_nuansediao1, R.string.nuansediao_1),
    nuansediao_2(Filter.Key.NUANSEDIAO_2, R.drawable.demo_icon_nuansediao2, R.string.nuansediao_2),
    //    nuansediao_3(Filter.Key.NUANSEDIAO_3, R.drawable.demo_icon_nuansediao3, R.string.nuansediao_3),

    gexing_1(Filter.Key.GEXING_1, R.drawable.demo_icon_gexing1, R.string.gexing_1),
    gexing_2(Filter.Key.GEXING_2, R.drawable.demo_icon_gexing2, R.string.gexing_2),
    gexing_3(Filter.Key.GEXING_3, R.drawable.demo_icon_gexing3, R.string.gexing_3),
    gexing_4(Filter.Key.GEXING_4, R.drawable.demo_icon_gexing4, R.string.gexing_4),
    gexing_5(Filter.Key.GEXING_5, R.drawable.demo_icon_gexing5, R.string.gexing_5),
    //    gexing_6(Filter.Key.GEXING_6, R.drawable.demo_icon_gexing6, R.string.gexing_6),
    gexing_7(Filter.Key.GEXING_7, R.drawable.demo_icon_gexing7, R.string.gexing_7),
    //    gexing_8(Filter.Key.GEXING_8, R.drawable.demo_icon_gexing8, R.string.gexing_8),
    //    gexing_9(Filter.Key.GEXING_9, R.drawable.demo_icon_gexing9, R.string.gexing_9),
    gexing_10(Filter.Key.GEXING_10, R.drawable.demo_icon_gexing10, R.string.gexing_10),
    gexing_11(Filter.Key.GEXING_11, R.drawable.demo_icon_gexing11, R.string.gexing_11),

    xiaoqingxin_1(Filter.Key.XIAOQINGXIN_1, R.drawable.demo_icon_xiaoqingxin1, R.string.xiaoqingxin_1),
    //    xiaoqingxin_2(Filter.Key.XIAOQINGXIN_2, R.drawable.demo_icon_xiaoqingxin2, R.string.xiaoqingxin_2),
    xiaoqingxin_3(Filter.Key.XIAOQINGXIN_3, R.drawable.demo_icon_xiaoqingxin3, R.string.xiaoqingxin_3),
    xiaoqingxin_4(Filter.Key.XIAOQINGXIN_4, R.drawable.demo_icon_xiaoqingxin4, R.string.xiaoqingxin_4),
    //    xiaoqingxin_5(Filter.Key.XIAOQINGXIN_5, R.drawable.demo_icon_xiaoqingxin5, R.string.xiaoqingxin_5),
    xiaoqingxin_6(Filter.Key.XIAOQINGXIN_6, R.drawable.demo_icon_xiaoqingxin6, R.string.xiaoqingxin_6),

    heibai_1(Filter.Key.HEIBAI_1, R.drawable.demo_icon_heibai1, R.string.heibai_1),
    heibai_2(Filter.Key.HEIBAI_2, R.drawable.demo_icon_heibai2, R.string.heibai_2),
    heibai_3(Filter.Key.HEIBAI_3, R.drawable.demo_icon_heibai3, R.string.heibai_3),
    heibai_4(Filter.Key.HEIBAI_4, R.drawable.demo_icon_heibai4, R.string.heibai_4);
    //    heibai_5(Filter.Key.HEIBAI_5, R.drawable.demo_icon_heibai5, R.string.heibai_5);

    private String name;
    private int iconId;
    private int nameId;

    FilterEnum(String name, int iconId, int nameId) {
        this.name = name;
        this.iconId = iconId;
        this.nameId = nameId;
    }

    public static ArrayList<Filter> getFiltersByFilterType() {
        FilterEnum[] values = FilterEnum.values();
        ArrayList<Filter> filters = new ArrayList<>(values.length);
        for (FilterEnum value : values) {
            filters.add(value.create());
        }
        return filters;
    }

    public Filter create() {
        return new Filter(name, iconId, nameId);
    }
}
