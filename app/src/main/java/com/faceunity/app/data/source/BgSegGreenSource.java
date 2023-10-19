package com.faceunity.app.data.source;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.faceunity.app.DemoApplication;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.core.controller.bgSegGreen.BgSegGreenParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.model.bgSegGreen.BgSegGreen;
import com.faceunity.ui.entity.BgSegGreenBackgroundBean;
import com.faceunity.ui.entity.BgSegGreenBean;
import com.faceunity.ui.entity.BgSegGreenSafeAreaBean;
import com.faceunity.ui.entity.ModelAttributeData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DESC：绿幕抠像数据构造
 * Created on 2021/3/25
 */
public class BgSegGreenSource {
    private static final String GREEN_SAFE_AREA_CUSTOM = "green_safe_area_custom";

    private static double SIMILARITY = 0.5;//相似度
    private static double SMOOTHNESS = 0.3;//平滑度
    private static double TRANSPARENCY = 0.66;//透明度

    /**
     * 构造绿幕抠像对象
     *
     * @return BgSegGreen
     */
    public static BgSegGreen buildBgSegGreen() {
        BgSegGreen bgSegGreen = new BgSegGreen(new FUBundleData(DemoConfig.BUNDLE_BG_SEG_GREEN));
        bgSegGreen.setSimilarity(BgSegGreenSource.SIMILARITY);
        bgSegGreen.setSmoothness(BgSegGreenSource.SMOOTHNESS);
        bgSegGreen.setTransparency(BgSegGreenSource.TRANSPARENCY);
        return bgSegGreen;
    }

    /**
     * 获取模型属性扩展数据
     *
     * @return HashMap<String, ModelAttributeData>
     */
    public static HashMap<String, ModelAttributeData> buildModelAttributeRange() {
        HashMap<String, ModelAttributeData> params = new HashMap<String, ModelAttributeData>();
        params.put(BgSegGreenParam.SIMILARITY, new ModelAttributeData(SIMILARITY, 0.0, 0.0, 1.0));
        params.put(BgSegGreenParam.SMOOTHNESS, new ModelAttributeData(SMOOTHNESS, 0.0, 0.0, 1.0));
        params.put(BgSegGreenParam.TRANSPARENCY, new ModelAttributeData(TRANSPARENCY, 0.0, 0.0, 1.0));
        return params;
    }

    /**
     * 绿幕抠像功能列表
     *
     * @return ArrayList<BgSegGreenBean>
     */
    public static ArrayList<BgSegGreenBean> buildBgSegGreenAction() {
        ArrayList<BgSegGreenBean> actions = new ArrayList<BgSegGreenBean>();
        actions.add(new BgSegGreenBean(BgSegGreenParam.RGB_COLOR, R.string.bg_seg_green_key_color, R.drawable.icon_green_color_selector, R.drawable.icon_green_color_selector, BgSegGreenBean.ButtonType.NORMAL2_BUTTON));
        actions.add(new BgSegGreenBean(BgSegGreenParam.SIMILARITY, R.string.bg_seg_green_similarity, R.drawable.icon_green_similarityr_close_selector, R.drawable.icon_green_similarityr_open_selector, BgSegGreenBean.ButtonType.NORMAL1_BUTTON));
        actions.add(new BgSegGreenBean(BgSegGreenParam.SMOOTHNESS, R.string.bg_seg_green_smooth, R.drawable.icon_green_smooth_close_selector, R.drawable.icon_green_smooth_open_selector, BgSegGreenBean.ButtonType.NORMAL1_BUTTON));
        actions.add(new BgSegGreenBean(BgSegGreenParam.TRANSPARENCY, R.string.bg_seg_green_alpha, R.drawable.icon_green_transparency_close_selector, R.drawable.icon_green_transparency_open_selector, BgSegGreenBean.ButtonType.NORMAL1_BUTTON));
        actions.add(new BgSegGreenBean("", R.string.bg_seg_green_safe_area, R.drawable.icon_green_safe_area_close_selector, R.drawable.icon_green_safe_area_open_selector, BgSegGreenBean.ButtonType.SWITCH_BUTTON));
        return actions;
    }

    /**
     * 绿幕抠像安全区域功能列表
     *
     * @return ArrayList<BgSegGreenSafeAreaBean>
     */
    public static ArrayList<BgSegGreenSafeAreaBean> buildBgSegGreenSafeArea() {
        String fileDir = "bg_seg_green" + File.separator + "sample" + File.separator;
        ArrayList<BgSegGreenSafeAreaBean> safeAreaBeans = new ArrayList<BgSegGreenSafeAreaBean>();
        //返回
        safeAreaBeans.add(new BgSegGreenSafeAreaBean(R.mipmap.icon_control_return, BgSegGreenSafeAreaBean.ButtonType.BACK_BUTTON));
        //不选
        safeAreaBeans.add(new BgSegGreenSafeAreaBean(R.mipmap.icon_control_none, BgSegGreenSafeAreaBean.ButtonType.NONE_BUTTON));
        //自定义
        safeAreaBeans.add(new BgSegGreenSafeAreaBean(R.mipmap.icon_control_square_add, BgSegGreenSafeAreaBean.ButtonType.NORMAL2_BUTTON));
        BgSegGreenSafeAreaBean customerBean = buildSafeAreaCustomBean(getCachePortraitSegment());
        if (customerBean != null) {
            safeAreaBeans.add(customerBean);
        }
        //区域一
        safeAreaBeans.add(new BgSegGreenSafeAreaBean(R.mipmap.icon_green_safe_area1, BgSegGreenSafeAreaBean.ButtonType.NORMAL1_BUTTON, fileDir + "safe_area1.jpg"));
        //区域二
        safeAreaBeans.add(new BgSegGreenSafeAreaBean(R.mipmap.icon_green_safe_area2, BgSegGreenSafeAreaBean.ButtonType.NORMAL1_BUTTON, fileDir + "safe_area2.jpg"));
        return safeAreaBeans;
    }

    /**
     * 构造自定义安全区域图片
     *
     * @param path String
     * @return BgSegGreenSafeAreaBean
     */
    public static BgSegGreenSafeAreaBean buildSafeAreaCustomBean(String path) {
        if (path != null && path.trim().length() > 0 && new File(path).exists()) {
            saveCachePortraitSegment(path);
            return new BgSegGreenSafeAreaBean(0, BgSegGreenSafeAreaBean.ButtonType.NORMAL1_BUTTON, path, false);
        }
        return null;
    }

    /**
     * 绿幕抠像背景列表
     *
     * @return ArrayList<BgSegGreenBackgroundBean>
     */
    public static ArrayList<BgSegGreenBackgroundBean> buildBgSegGreenBackground() {
        ArrayList<BgSegGreenBackgroundBean> backgroundBeans = new ArrayList<>();
        String fileDir = "bg_seg_green" + File.separator + "sample" + File.separator;
        backgroundBeans.add(new BgSegGreenBackgroundBean(R.string.cancel, R.mipmap.icon_control_none, null));
        backgroundBeans.add(new BgSegGreenBackgroundBean(R.string.bg_seg_green_science, R.mipmap.icon_green_science, fileDir + "science.mp4"));
        backgroundBeans.add(new BgSegGreenBackgroundBean(R.string.bg_seg_green_beach, R.mipmap.icon_green_bg_beach, fileDir + "beach.mp4"));
        backgroundBeans.add(new BgSegGreenBackgroundBean(R.string.bg_seg_green_classroom, R.mipmap.icon_green_bg_classroom, fileDir + "classroom.mp4"));
        backgroundBeans.add(new BgSegGreenBackgroundBean(R.string.bg_seg_green_ink, R.mipmap.icon_green_ink_painting, fileDir + "ink_painting.mp4"));
        backgroundBeans.add(new BgSegGreenBackgroundBean(R.string.bg_seg_green_forest, R.mipmap.icon_green_bg_forest, fileDir + "forest.mp4"));
        return backgroundBeans;
    }

    /**
     * 缓存自定义安全区域图片
     *
     * @param path String
     */
    public static void saveCachePortraitSegment(String path) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(GREEN_SAFE_AREA_CUSTOM, MODE_PRIVATE);
        sp.edit().putString(GREEN_SAFE_AREA_CUSTOM, path).apply();
    }

    /**
     * 获取自定义安全区域图片
     *
     * @return String
     */
    public static String getCachePortraitSegment() {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(GREEN_SAFE_AREA_CUSTOM, MODE_PRIVATE);
        return sp.getString(GREEN_SAFE_AREA_CUSTOM, "");
    }
}
