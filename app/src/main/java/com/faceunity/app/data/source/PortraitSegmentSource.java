package com.faceunity.app.data.source;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.faceunity.app.DemoApplication;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.model.prop.humanOutline.HumanOutline;
import com.faceunity.ui.entity.PropCustomBean;
import com.faceunity.ui.infe.AbstractPropCustomDataFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * DESC：人像分割数据构造
 * Created on 2021/3/28
 */
public class PortraitSegmentSource {

    private static final String BG_SEG_CUSTOM_FILEPATH = "bg_seg_custom";

    /**
     * 缓存自定义添加人像
     *
     * @param path String
     */
    public static void saveCachePortraitSegment(String path) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(BG_SEG_CUSTOM_FILEPATH, MODE_PRIVATE);
        sp.edit().putString(BG_SEG_CUSTOM_FILEPATH, path).apply();
    }

    /**
     * 获取自定义添加人像
     *
     * @return String
     */
    public static String getCachePortraitSegment() {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(BG_SEG_CUSTOM_FILEPATH, MODE_PRIVATE);
        return sp.getString(BG_SEG_CUSTOM_FILEPATH, "");
    }


    /**
     * 构造道具队列
     *
     * @return ArrayList<PropCustomBean>
     */
    public static ArrayList<PropCustomBean> buildPropBeans() {
        ArrayList<PropCustomBean> propBeans = new ArrayList<>();
        propBeans.add(new PropCustomBean(R.mipmap.icon_control_delete_all, null, AbstractPropCustomDataFactory.TYPE_NONE));
        propBeans.add(new PropCustomBean(R.mipmap.icon_control_add, null, AbstractPropCustomDataFactory.TYPE_ADD));
        PropCustomBean customBean = buildPropCustomBean(getCachePortraitSegment());
        if (customBean != null) {
            propBeans.add(customBean);
        }
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_human_outline, "effect/segment/human_outline.bundle", FunctionEnum.HUMAN_OUTLINE));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_boyfriend_1, "effect/segment/boyfriend1.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_boyfriend_2, "effect/segment/boyfriend3.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_boyfriend_3, "effect/segment/boyfriend2.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_hez_ztt_fu, "effect/segment/hez_ztt_fu.bundle", FunctionEnum.PORTRAIT_SEGMENT, R.string.future_warrior));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_gufeng_zh_fu, "effect/segment/gufeng_zh_fu.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_xiandai_ztt_fu, "effect/segment/xiandai_ztt_fu.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_sea_lm_fu, "effect/segment/sea_lm_fu.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        propBeans.add(new PropCustomBean(R.mipmap.icon_segment_ice_lm_fu, "effect/segment/ice_lm_fu.bundle", FunctionEnum.PORTRAIT_SEGMENT));
        return propBeans;
    }


    /**
     * 构造自定义人像分割
     *
     * @param path String
     * @return PropCustomBean
     */
    public static PropCustomBean buildPropCustomBean(String path) {
        if (path != null && path.trim().length() > 0 && new File(path).exists()) {
            saveCachePortraitSegment(path);
            return new PropCustomBean(0, DemoConfig.BUNDLE_BG_SEG_CUSTOM, FunctionEnum.BG_SEG_CUSTOM, 0, path);
        }
        return null;

    }


    /**
     * 构造人像分割线模型
     *
     * @param path String
     * @return HumanOutline
     */
    public static HumanOutline getHumanOutline(String path) {
        HumanOutline humanOutline = new HumanOutline(new FUBundleData(path));
        humanOutline.setLineSize(2.8);
        humanOutline.setLineGap(2.8);
        humanOutline.setLineColor(new FUColorRGBData(255.0, 196.0, 0.0));
        return humanOutline;
    }


}
