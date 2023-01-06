package com.faceunity.app.data.source;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import com.faceunity.app.DemoApplication;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.utils.FuDeviceUtils;
import com.faceunity.core.controller.makeup.MakeupParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.core.model.makeup.Makeup;
import com.faceunity.core.utils.FileUtils;
import com.faceunity.ui.entity.MakeupCombinationBean;
import com.faceunity.ui.entity.MakeupCombinationBean.TypeEnum;
import com.faceunity.ui.entity.MakeupCustomBean;
import com.faceunity.ui.entity.MakeupCustomClassBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DESC：美妆数据构造
 * Created on 2021/3/28
 */
public class MakeupSource {


    //region 组合妆容

    /**
     * 构造美妆组合妆容配置
     *
     * @return ArrayList<MakeupCombinationBean>
     */
    public static ArrayList<MakeupCombinationBean> buildCombinations() {
        ArrayList<MakeupCombinationBean> combinations = new ArrayList<MakeupCombinationBean>();
        String jsonDir = DemoConfig.MAKEUP_RESOURCE_JSON_DIR;
        String bundleDir = DemoConfig.MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR;
        combinations.add(new MakeupCombinationBean("origin", TypeEnum.TYPE_NONE, R.mipmap.icon_control_none, R.string.makeup_radio_remove, null, "", FaceBeautyFilterEnum.ZIRAN_2, 1.0, 0.0));
        combinations.add(new MakeupCombinationBean("diadiatu", TypeEnum.TYPE_THEME_MAIN, R.mipmap.icon_makeup_combination_diadiatu, R.string.makeup_combination_diadiatu, bundleDir + "diadiatu.bundle", jsonDir + "diadiatu.json", FaceBeautyFilterEnum.ORIGIN, 0.68));
        combinations.add(new MakeupCombinationBean("dongling", TypeEnum.TYPE_THEME_MAIN, R.mipmap.icon_makeup_combination_freezing_age, R.string.makeup_combination_dongling, bundleDir + "dongling.bundle", jsonDir + "dongling.json", FaceBeautyFilterEnum.ORIGIN, 0.68));
        combinations.add(new MakeupCombinationBean("guofeng", TypeEnum.TYPE_THEME_MAIN, R.mipmap.icon_makeup_combination_guo_feng, R.string.makeup_combination_guofeng, bundleDir + "guofeng.bundle", jsonDir + "guofeng.json", FaceBeautyFilterEnum.ORIGIN, 0.6));
        combinations.add(new MakeupCombinationBean("hunxie", TypeEnum.TYPE_THEME_MAIN, R.mipmap.icon_makeup_combination_mixed_race, R.string.makeup_combination_hunxie, bundleDir + "hunxie.bundle", jsonDir + "hunxie.json", FaceBeautyFilterEnum.ORIGIN, 0.6));
        combinations.add(new MakeupCombinationBean("jianling", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_age, R.string.makeup_combination_jianling, bundleDir + "jianling.bundle", jsonDir + "jianling.json", FaceBeautyFilterEnum.ZHIGANHUI_1));
        combinations.add(new MakeupCombinationBean("nuandong", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_warm_winter, R.string.makeup_combination_nuandong, bundleDir + "nuandong.bundle", jsonDir + "nuandong.json", FaceBeautyFilterEnum.ZHIGANHUI_2));
        combinations.add(new MakeupCombinationBean("hongfeng", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_red_maple, R.string.makeup_combination_hongfeng, bundleDir + "hongfeng.bundle", jsonDir + "hongfeng.json", FaceBeautyFilterEnum.ZHIGANHUI_3));
        combinations.add(new MakeupCombinationBean("rose", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_rose, R.string.makeup_combination_rose, bundleDir + "rose.bundle", jsonDir + "rose.json", FaceBeautyFilterEnum.ZHIGANHUI_2));
        combinations.add(new MakeupCombinationBean("shaonv", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_girl, R.string.makeup_combination_shaonv, bundleDir + "shaonv.bundle", jsonDir + "shaonv.json", FaceBeautyFilterEnum.ZHIGANHUI_4));
        combinations.add(new MakeupCombinationBean("ziyun", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_purple_rhyme, R.string.makeup_combination_ziyun, bundleDir + "ziyun.bundle", jsonDir + "ziyun.json", FaceBeautyFilterEnum.ZHIGANHUI_1));
        combinations.add(new MakeupCombinationBean("yanshimao", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_bored_cat, R.string.makeup_combination_yanshimao, bundleDir + "yanshimao.bundle", jsonDir + "yanshimao.json", FaceBeautyFilterEnum.ZHIGANHUI_5));
        combinations.add(new MakeupCombinationBean("renyu", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_mermaid, R.string.makeup_combination_renyu, bundleDir + "renyu.bundle", jsonDir + "renyu.json", FaceBeautyFilterEnum.ZHIGANHUI_1));
        combinations.add(new MakeupCombinationBean("chuqiu", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_early_autumn, R.string.makeup_combination_chuqiu, bundleDir + "chuqiu.bundle", jsonDir + "chuqiu.json", FaceBeautyFilterEnum.ZHIGANHUI_6));
        combinations.add(new MakeupCombinationBean("qianzhihe", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_paper_cranes, R.string.makeup_combination_qianzhihe, bundleDir + "qianzhihe.bundle", jsonDir + "qianzhihe.json", FaceBeautyFilterEnum.ZHIGANHUI_2));
        combinations.add(new MakeupCombinationBean("chaomo", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_supermodel, R.string.makeup_combination_chaomo, bundleDir + "chaomo.bundle", jsonDir + "chaomo.json", FaceBeautyFilterEnum.ZHIGANHUI_7));
        combinations.add(new MakeupCombinationBean("chuju", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_daisy, R.string.makeup_combination_chuju, bundleDir + "chuju.bundle", jsonDir + "chuju.json", FaceBeautyFilterEnum.ZHIGANHUI_8));
        combinations.add(new MakeupCombinationBean("gangfeng", TypeEnum.TYPE_THEME_SUB, R.mipmap.icon_makeup_combination_harbour_wind, R.string.makeup_combination_gangfeng, bundleDir + "gangfeng.bundle", jsonDir + "gangfeng.json", FaceBeautyFilterEnum.ZIRAN_8));
        combinations.add(new MakeupCombinationBean("xinggan", TypeEnum.TYPE_DAILY, R.mipmap.icon_makeup_combination_sexy, R.string.makeup_combination_sexy, bundleDir + "xinggan.bundle", jsonDir + "xinggan.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("tianmei", TypeEnum.TYPE_DAILY, R.mipmap.icon_makeup_combination_sweet, R.string.makeup_combination_sweet, bundleDir + "tianmei.bundle", jsonDir + "tianmei.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("linjia", TypeEnum.TYPE_DAILY, R.mipmap.icon_makeup_combination_neighbor_girl, R.string.makeup_combination_neighbor, bundleDir + "linjia.bundle", jsonDir + "linjia.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("oumei", TypeEnum.TYPE_DAILY, R.mipmap.icon_makeup_combination_occident, R.string.makeup_combination_occident, bundleDir + "oumei.bundle", jsonDir + "oumei.json", FaceBeautyFilterEnum.ZIRAN_4));
        combinations.add(new MakeupCombinationBean("wumei", TypeEnum.TYPE_DAILY, R.mipmap.icon_makeup_combination_charming, R.string.makeup_combination_charming, bundleDir + "wumei.bundle", jsonDir + "wumei.json", FaceBeautyFilterEnum.ZIRAN_4));
        return combinations;
    }


    /**
     * 构造美妆模型
     *
     * @return
     */
    public static Makeup getMakeupModel(MakeupCombinationBean bean) {
        Makeup makeupModel;
        if (TypeEnum.TYPE_THEME_MAIN == bean.getType() && bean.getBundlePath() != null && bean.getBundlePath().trim().length() > 0) {
            makeupModel = new Makeup(new FUBundleData(bean.getBundlePath()));
            //新的组合妆容设置滤镜scale
            makeupModel.setCurrentFilterScale(bean.getFilterScale());
        } else {
            makeupModel = new Makeup(new FUBundleData(DemoConfig.BUNDLE_FACE_MAKEUP));
        }

        if (bean.getKey().equals("origin")) {
            return makeupModel;
        }

        if ((TypeEnum.TYPE_THEME_SUB == bean.getType() || TypeEnum.TYPE_DAILY == bean.getType()) && bean.getBundlePath() != null && bean.getBundlePath().trim().length() > 0)
            makeupModel.setCombinedConfig(new FUBundleData(bean.getBundlePath()));

        makeupModel.setMakeupIntensity(bean.getIntensity());
        makeupModel.setMachineLevel(DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID);//更新设备等级去设置是否开启人脸遮挡

        if (bean.getJsonPathParams() == null) {
            bean.setJsonPathParams(getLocalParams(bean.getJsonPath()));
        }
        LinkedHashMap<String, Object> params = bean.getJsonPathParams();

        //支持自定义，所以需要知道选中了妆容的哪一些项
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();
            if (value instanceof double[] && ((double[]) value).length > 4) {
                int count = ((double[]) value).length / 4;
                for (int i = 0; i < count; i++) {
                    if (i == 0) {
                        if (makeupSetMapping.containsKey(key)) {
                            makeupSetMapping.get(key).setValue(makeupModel, value);
                        }
                    } else {
                        if (makeupSetMapping.containsKey(key + (i + 1))) {
                            makeupSetMapping.get(key + (i + 1)).setValue(makeupModel, value);
                        }
                    }
                }
            } else {
                if (makeupSetMapping.containsKey(key)) {
                    makeupSetMapping.get(key).setValue(makeupModel, value);
                }
            }
        }

        return makeupModel;
    }


    /**
     * 读取本地参数配置
     *
     * @param jsonPath String json文件路径
     * @return LinkedHashMap<String, Any>
     */
    private static LinkedHashMap<String, Object> getLocalParams(String jsonPath) {
        LinkedHashMap<String, Object> map = new LinkedHashMap(32);
        map.put(MakeupParam.LIP_INTENSITY, 0.0);
        map.put(MakeupParam.EYE_LINER_INTENSITY, 0.0);
        map.put(MakeupParam.BLUSHER_INTENSITY, 0.0);
        map.put(MakeupParam.PUPIL_INTENSITY, 0.0);
        map.put(MakeupParam.EYE_BROW_INTENSITY, 0.0);
        map.put(MakeupParam.EYE_SHADOW_INTENSITY, 0.0);
        map.put(MakeupParam.EYELASH_INTENSITY, 0.0);
        map.put(MakeupParam.FOUNDATION_INTENSITY, 0.0);
        map.put(MakeupParam.HIGHLIGHT_INTENSITY, 0.0);
        map.put(MakeupParam.SHADOW_INTENSITY, 0.0);
        LinkedHashMap<String, Object> jsonParam = FileUtils.INSTANCE.loadParamsFromLocal(DemoApplication.mApplication, jsonPath);
        for (Map.Entry<String, Object> entry : jsonParam.entrySet()) {
            if (entry.getKey().startsWith("tex_")) {
                if (entry.getValue() instanceof String && ((String) entry.getValue()).contains(".bundle")) {
                    map.put(entry.getKey(), DemoConfig.MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + entry.getValue());
                }
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    interface MakeupSetParam {
        /**
         * 模型属性赋值
         *
         * @param makeup
         * @param value
         */
        void setValue(Makeup makeup, Object value);

    }

    /*美妆映射模型*/
    public static HashMap<String, MakeupSetParam> makeupSetMapping = new HashMap<String, MakeupSetParam>() {
        {
            put(MakeupParam.LIP_TYPE, (makeup, value) -> makeup.setLipType((int) value));
            put(MakeupParam.IS_TWO_COLOR, (makeup, value) -> makeup.setEnableTwoLipColor((int) value == 1));
            put(MakeupParam.MAKEUP_LIP_HIGH_LIGHT_ENABLE, (makeup, value) -> makeup.setLipHighLightEnable((int) value == 1));
            put(MakeupParam.MAKEUP_LIP_HIGH_LIGHT_STRENGTH, (makeup, value) -> makeup.setLipHighLightStrength((double) value));
            put(MakeupParam.BROW_WARP, (makeup, value) -> makeup.setEnableBrowWarp((double) value == 1.0));
            put(MakeupParam.MAKEUP_MACHINE_LEVEL, (makeup, value) -> makeup.setMachineLevel((double) value == 1.0));
            put(MakeupParam.BROW_WARP_TYPE, (makeup, value) -> makeup.setBrowWarpType((int) value));
            /*强度*/
            put(MakeupParam.MAKEUP_INTENSITY, (makeup, value) -> makeup.setMakeupIntensity((double) value));
            put(MakeupParam.LIP_INTENSITY, (makeup, value) -> makeup.setLipIntensity((double) value));
            put(MakeupParam.EYE_LINER_INTENSITY, (makeup, value) -> makeup.setEyeLineIntensity((double) value));
            put(MakeupParam.BLUSHER_INTENSITY, (makeup, value) -> makeup.setBlusherIntensity((double) value));
            put(MakeupParam.PUPIL_INTENSITY, (makeup, value) -> makeup.setPupilIntensity((double) value));
            put(MakeupParam.EYE_BROW_INTENSITY, (makeup, value) -> makeup.setEyeBrowIntensity((double) value));
            put(MakeupParam.EYE_SHADOW_INTENSITY, (makeup, value) -> makeup.setEyeShadowIntensity((double) value));
            put(MakeupParam.EYELASH_INTENSITY, (makeup, value) -> makeup.setEyeLashIntensity((double) value));
            put(MakeupParam.FOUNDATION_INTENSITY, (makeup, value) -> makeup.setFoundationIntensity((double) value));
            put(MakeupParam.HIGHLIGHT_INTENSITY, (makeup, value) -> makeup.setHeightLightIntensity((double) value));
            put(MakeupParam.SHADOW_INTENSITY, (makeup, value) -> makeup.setShadowIntensity((double) value));
            /*子项妆容贴图*/
            put(MakeupParam.TEX_LIP, (makeup, value) -> makeup.setLipBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_BROW, (makeup, value) -> makeup.setEyeBrowBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW, (makeup, value) -> makeup.setEyeShadowBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW2, (makeup, value) -> makeup.setEyeShadowBundle2(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW3, (makeup, value) -> makeup.setEyeShadowBundle3(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_SHADOW4, (makeup, value) -> makeup.setEyeShadowBundle4(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_PUPIL, (makeup, value) -> makeup.setPupilBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_LASH, (makeup, value) -> makeup.setEyeLashBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_EYE_LINER, (makeup, value) -> makeup.setEyeLinerBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_BLUSHER, (makeup, value) -> makeup.setBlusherBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_BLUSHER2, (makeup, value) -> makeup.setBlusherBundle2(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_FOUNDATION, (makeup, value) -> makeup.setFoundationBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_HIGH_LIGHT, (makeup, value) -> makeup.setHighLightBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            put(MakeupParam.TEX_SHADOW, (makeup, value) -> makeup.setShadowBundle(((String) value).endsWith(".bundle") ? new FUBundleData((String) value) : null));
            /*子项妆容颜色*/
            put(MakeupParam.MAKEUP_LIP_COLOR, (makeup, value) -> makeup.setLipColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_LIP_COLOR_V2, (makeup, value) -> makeup.setLipColorV2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_LIP_COLOR2, (makeup, value) -> makeup.setLipColor2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_LINER_COLOR, (makeup, value) -> makeup.setEyeLinerColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_LASH_COLOR, (makeup, value) -> makeup.setEyeLashColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_BLUSHER_COLOR, (makeup, value) -> makeup.setBlusherColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_BLUSHER_COLOR2, (makeup, value) -> makeup.setBlusherColor2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_FOUNDATION_COLOR, (makeup, value) -> makeup.setFoundationColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_HIGH_LIGHT_COLOR, (makeup, value) -> makeup.setHighLightColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_SHADOW_COLOR, (makeup, value) -> makeup.setShadowColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_BROW_COLOR, (makeup, value) -> makeup.setEyeBrowColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_PUPIL_COLOR, (makeup, value) -> makeup.setPupilColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR, (makeup, value) -> makeup.setEyeShadowColor(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR2, (makeup, value) -> makeup.setEyeShadowColor2(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR3, (makeup, value) -> makeup.setEyeShadowColor3(buildFUColorRGBData(value)));
            put(MakeupParam.MAKEUP_EYE_SHADOW_COLOR4, (makeup, value) -> makeup.setEyeShadowColor4(buildFUColorRGBData(value)));
            /* 图层混合模式 */
            put(MakeupParam.BLEND_TEX_EYE_SHADOW, (makeup, value) -> makeup.setEyeShadowTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_EYE_SHADOW2, (makeup, value) -> makeup.setEyeShadowTexBlend2((int) value));
            put(MakeupParam.BLEND_TEX_EYE_SHADOW3, (makeup, value) -> makeup.setEyeShadowTexBlend3((int) value));
            put(MakeupParam.BLEND_TEX_EYE_SHADOW4, (makeup, value) -> makeup.setEyeShadowTexBlend4((int) value));
            put(MakeupParam.BLEND_TEX_EYE_LASH, (makeup, value) -> makeup.setEyeLashTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_EYE_LINER, (makeup, value) -> makeup.setEyeLinerTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_BLUSHER, (makeup, value) -> makeup.setBlusherTexBlend((int) value));
            put(MakeupParam.BLEND_TEX_BLUSHER2, (makeup, value) -> makeup.setBlusherTexBlend2((int) value));
            put(MakeupParam.BLEND_TEX_PUPIL, (makeup, value) -> makeup.setPupilTexBlend((int) value));
        }
    };


    /**
     * 构造颜色模型
     *
     * @param object
     * @return
     */
    public static FUColorRGBData buildFUColorRGBData(Object object) {
        if (object instanceof double[]) {
            double[] array = (double[]) object;
            if (array.length == 4) {
                return new FUColorRGBData(array[0] * 255, array[1] * 255, array[2] * 255, array[3] * 255);
            }
        }
        return new FUColorRGBData(0.0, 0.0, 0.0, 0.0);
    }
    //endregion 组合妆容

// region 子妆容

    /* 粉底 */
    public static String FACE_MAKEUP_TYPE_FOUNDATION = "FOUNDATION";
    /* 口红 */
    public static String FACE_MAKEUP_TYPE_LIP_STICK = "STICK";
    /* 腮红 */
    public static String FACE_MAKEUP_TYPE_BLUSHER = "BLUSHER";
    /* 眉毛 */
    public static String FACE_MAKEUP_TYPE_EYE_BROW = "EYE_BROW";
    /* 眼影 */
    public static String FACE_MAKEUP_TYPE_EYE_SHADOW = "EYE_SHADOW";
    /* 眼线 */
    public static String FACE_MAKEUP_TYPE_EYE_LINER = "EYE_LINER";
    /* 睫毛 */
    public static String FACE_MAKEUP_TYPE_EYE_LASH = "EYE_LASH";
    /* 高光 */
    public static String FACE_MAKEUP_TYPE_HIGH_LIGHT = "HIGHLIGHT";
    /* 阴影 */
    public static String FACE_MAKEUP_TYPE_SHADOW = "SHADOW";
    /* 美瞳 */
    public static String FACE_MAKEUP_TYPE_EYE_PUPIL = "EYE_PUPIL";

    /**
     * 构造美妆子项类别
     */
    public static ArrayList<MakeupCustomClassBean> buildCustomClasses() {
        ArrayList<MakeupCustomClassBean> classes = new ArrayList();
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_foundation, FACE_MAKEUP_TYPE_FOUNDATION));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_lipstick, FACE_MAKEUP_TYPE_LIP_STICK));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_blusher, FACE_MAKEUP_TYPE_BLUSHER));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_eyebrow, FACE_MAKEUP_TYPE_EYE_BROW));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_eye_shadow, FACE_MAKEUP_TYPE_EYE_SHADOW));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_eye_liner, FACE_MAKEUP_TYPE_EYE_LINER));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_eyelash, FACE_MAKEUP_TYPE_EYE_LASH));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_highlight, FACE_MAKEUP_TYPE_HIGH_LIGHT));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_shadow, FACE_MAKEUP_TYPE_SHADOW));
        classes.add(new MakeupCustomClassBean(R.string.makeup_radio_contact_lens, FACE_MAKEUP_TYPE_EYE_PUPIL));
        return classes;
    }


    /**
     * 美妆单项妆容配置参数
     *
     * @return LinkedHashMap<String, ArrayList < MakeupCustomBean>>
     */
    public static LinkedHashMap<String, ArrayList<MakeupCustomBean>> buildCustomItemParams(LinkedHashMap<String, ArrayList<double[]>> colorMap) {
        LinkedHashMap<String, ArrayList<MakeupCustomBean>> mCustomItems = new LinkedHashMap<>();
        /*粉底*/
        ArrayList<MakeupCustomBean> makeupItems = new ArrayList(6);
        makeupItems.add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
        ArrayList<double[]> list = colorMap.get("color_mu_style_foundation_01");
        for (int i = 3; i < 8; i++) {
            double[] colors = list.get(i);
            ColorDrawable drawable = new ColorDrawable(Color.argb((int) (colors[3] * 255), (int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255)));
            makeupItems.add(new MakeupCustomBean(0, drawable));
        }
        mCustomItems.put(FACE_MAKEUP_TYPE_FOUNDATION, makeupItems);


        /*口红*/
        mCustomItems.put(FACE_MAKEUP_TYPE_LIP_STICK, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_lip_fog, getDrawable(R.mipmap.icon_makeup_lip_fog), colorMap.get("color_mu_style_lip_01")));
                add(new MakeupCustomBean(R.string.makeup_lip_moist1, getDrawable(R.mipmap.icon_makeup_lip_moist), colorMap.get("color_mu_style_lip_01")));
                add(new MakeupCustomBean(R.string.makeup_lip_moist2, getDrawable(R.mipmap.icon_makeup_lip_water), colorMap.get("color_mu_style_lip_01")));
                add(new MakeupCustomBean(R.string.makeup_lip_pearl, getDrawable(R.mipmap.icon_makeup_lip_pearl), colorMap.get("color_mu_style_lip_01")));
                add(new MakeupCustomBean(R.string.makeup_lip_bitelip, getDrawable(R.mipmap.icon_makeup_lip_beitelip), colorMap.get("color_mu_style_lip_01")));
            }
        });

        /*腮红*/
        mCustomItems.put(FACE_MAKEUP_TYPE_BLUSHER, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_blusher_apple, getDrawable(R.mipmap.icon_makeup_blush_01), colorMap.get("color_mu_style_blush_01")));
                add(new MakeupCustomBean(R.string.makeup_blusher_fan, getDrawable(R.mipmap.icon_makeup_blush_02), colorMap.get("color_mu_style_blush_02")));
                add(new MakeupCustomBean(R.string.makeup_blusher_eye_corner, getDrawable(R.mipmap.icon_makeup_blush_03), colorMap.get("color_mu_style_blush_03")));
                add(new MakeupCustomBean(R.string.makeup_blusher_slight_drunk, getDrawable(R.mipmap.icon_makeup_blush_04), colorMap.get("color_mu_style_blush_04")));
            }
        });
        /*眉毛*/
        mCustomItems.put(FACE_MAKEUP_TYPE_EYE_BROW, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_eyebrow_willow, getDrawable(R.mipmap.icon_makeup_eyebrow_01), colorMap.get("color_mu_style_eyebrow_01")));
                add(new MakeupCustomBean(R.string.makeup_eyebrow_wild, getDrawable(R.mipmap.icon_makeup_eyebrow_02), colorMap.get("color_mu_style_eyebrow_01")));
                add(new MakeupCustomBean(R.string.makeup_eyebrow_classical, getDrawable(R.mipmap.icon_makeup_eyebrow_03), colorMap.get("color_mu_style_eyebrow_01")));
                add(new MakeupCustomBean(R.string.makeup_eyebrow_standard, getDrawable(R.mipmap.icon_makeup_eyebrow_04), colorMap.get("color_mu_style_eyebrow_01")));
            }
        });

        /*眼影*/
        mCustomItems.put(FACE_MAKEUP_TYPE_EYE_SHADOW, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_eye_shadow_single, getDrawable(R.mipmap.icon_makeup_eyeshadow_01), colorMap.get("color_mu_style_eyeshadow_01")));
                add(new MakeupCustomBean(R.string.makeup_eye_shadow_double1, getDrawable(R.mipmap.icon_makeup_eyeshadow_02), colorMap.get("color_mu_style_eyeshadow_02")));
                add(new MakeupCustomBean(R.string.makeup_eye_shadow_double2, getDrawable(R.mipmap.icon_makeup_eyeshadow_03), colorMap.get("color_mu_style_eyeshadow_03")));
                add(new MakeupCustomBean(R.string.makeup_eye_shadow_double3, getDrawable(R.mipmap.icon_makeup_eyeshadow_04), colorMap.get("color_mu_style_eyeshadow_04")));
                add(new MakeupCustomBean(R.string.makeup_eye_shadow_triple1, getDrawable(R.mipmap.icon_makeup_eyeshadow_05), colorMap.get("color_mu_style_eyeshadow_05")));
                add(new MakeupCustomBean(R.string.makeup_eye_shadow_triple2, getDrawable(R.mipmap.icon_makeup_eyeshadow_06), colorMap.get("color_mu_style_eyeshadow_06")));
            }
        });

        /*眼线*/
        mCustomItems.put(FACE_MAKEUP_TYPE_EYE_LINER, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_eye_linear_cat, getDrawable(R.mipmap.icon_makeup_eyeliner_01), colorMap.get("color_mu_style_eyeliner_01")));
                add(new MakeupCustomBean(R.string.makeup_eye_linear_drooping, getDrawable(R.mipmap.icon_makeup_eyeliner_02), colorMap.get("color_mu_style_eyeliner_02")));
                add(new MakeupCustomBean(R.string.makeup_eye_linear_pull_open, getDrawable(R.mipmap.icon_makeup_eyeliner_03), colorMap.get("color_mu_style_eyeliner_03")));
                add(new MakeupCustomBean(R.string.makeup_eye_linear_pull_close, getDrawable(R.mipmap.icon_makeup_eyeliner_04), colorMap.get("color_mu_style_eyeliner_04")));
                add(new MakeupCustomBean(R.string.makeup_eye_linear_long, getDrawable(R.mipmap.icon_makeup_eyeliner_05), colorMap.get("color_mu_style_eyeliner_05")));
                add(new MakeupCustomBean(R.string.makeup_eye_linear_circular, getDrawable(R.mipmap.icon_makeup_eyeliner_06), colorMap.get("color_mu_style_eyeliner_06")));
            }
        });
        /*睫毛*/
        mCustomItems.put(FACE_MAKEUP_TYPE_EYE_LASH, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_eyelash_natural1, getDrawable(R.mipmap.icon_makeup_eyelash_01), colorMap.get("color_mu_style_eyelash_01")));
                add(new MakeupCustomBean(R.string.makeup_eyelash_natural2, getDrawable(R.mipmap.icon_makeup_eyelash_02), colorMap.get("color_mu_style_eyelash_02")));
                add(new MakeupCustomBean(R.string.makeup_eyelash_thick1, getDrawable(R.mipmap.icon_makeup_eyelash_03), colorMap.get("color_mu_style_eyelash_03")));
                add(new MakeupCustomBean(R.string.makeup_eyelash_thick2, getDrawable(R.mipmap.icon_makeup_eyelash_04), colorMap.get("color_mu_style_eyelash_04")));
                add(new MakeupCustomBean(R.string.makeup_eyelash_exaggerate1, getDrawable(R.mipmap.icon_makeup_eyelash_05), colorMap.get("color_mu_style_eyelash_05")));
                add(new MakeupCustomBean(R.string.makeup_eyelash_exaggerate2, getDrawable(R.mipmap.icon_makeup_eyelash_06), colorMap.get("color_mu_style_eyelash_06")));
            }
        });

        /*高光*/
        mCustomItems.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_highlight_one, getDrawable(R.mipmap.icon_makeup_highlight_01), colorMap.get("color_mu_style_highlight_01")));
                add(new MakeupCustomBean(R.string.makeup_highlight_two, getDrawable(R.mipmap.icon_makeup_highlight_02), colorMap.get("color_mu_style_highlight_02")));
            }
        });
        /*阴影*/
        mCustomItems.put(FACE_MAKEUP_TYPE_SHADOW, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_shadow_one, getDrawable(R.mipmap.icon_makeup_contour_01), colorMap.get("color_mu_style_contour_01")));
            }
        });
        /*美瞳*/
        mCustomItems.put(FACE_MAKEUP_TYPE_EYE_PUPIL, new ArrayList<MakeupCustomBean>() {
            {
                add(new MakeupCustomBean(R.string.makeup_radio_remove, getDrawable(R.mipmap.icon_control_none)));
                add(new MakeupCustomBean(R.string.makeup_pupil_1, getDrawable(R.mipmap.icon_makeup_eyepupil_01), colorMap.get("color_mu_style_eyepupil_01")));
                add(new MakeupCustomBean(R.string.makeup_pupil_2, getDrawable(R.mipmap.icon_makeup_eyepupil_03), null));
                add(new MakeupCustomBean(R.string.makeup_pupil_3, getDrawable(R.mipmap.icon_makeup_eyepupil_04), null));
                add(new MakeupCustomBean(R.string.makeup_pupil_4, getDrawable(R.mipmap.icon_makeup_eyepupil_05), null));
                add(new MakeupCustomBean(R.string.makeup_pupil_5, getDrawable(R.mipmap.icon_makeup_eyepupil_06), null));
                add(new MakeupCustomBean(R.string.makeup_pupil_6, getDrawable(R.mipmap.icon_makeup_eyepupil_07), null));
                add(new MakeupCustomBean(R.string.makeup_pupil_7, getDrawable(R.mipmap.icon_makeup_eyepupil_08), null));
                add(new MakeupCustomBean(R.string.makeup_pupil_8, getDrawable(R.mipmap.icon_makeup_eyepupil_09), null));
            }
        });
        return mCustomItems;
    }


    //endregion 子妆容


    //region 其他

    /**
     * 获取颜色值配置
     *
     * @return LinkedHashMap<String, ArrayList < DoubleArray>>
     */
    public static LinkedHashMap<String, ArrayList<double[]>> buildMakeUpColorMap() {
        LinkedHashMap<String, ArrayList<double[]>> makeupColorMap = new LinkedHashMap<>(32);
        String colorJson = FileUtils.INSTANCE.loadStringFromLocal(DemoApplication.mApplication, DemoConfig.MAKEUP_RESOURCE_COLOR_SETUP_JSON);
        if (colorJson != null && colorJson.trim().length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(colorJson);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    ArrayList<double[]> colorList = new ArrayList(12);
                    // add additional transparent to fit ui
                    //增加透明色，兼容ColorRecycleView展示
                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    JSONObject colorObject = jsonObject.optJSONObject(key);
                    for (int i = 1; i < 6; i++) {
                        JSONArray jsonArray = colorObject.optJSONArray("color" + i);
                        int length = jsonArray.length();
                        double[] colors = new double[length];
                        for (int j = 0; j < length; j++) {
                            colors[j] = jsonArray.optDouble(j, 0.0);
                        }
                        colorList.add(colors);
                    }
                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    colorList.add(new double[]{0.0, 0.0, 0.0, 0.0});
                    makeupColorMap.put(key, colorList);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return makeupColorMap;
    }


    /**
     * 获取Drawable对象
     *
     * @param res Int
     * @return Drawable
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private static Drawable getDrawable(int res) {
        return DemoApplication.mApplication.getResources().getDrawable(res);
    }
    //endregion

    /**
     * 获取日常妆的选中项
     *
     * @param key
     * @return
     */
    public static HashMap<String, Integer> getDailyCombinationSelectItem(String key) {
        HashMap<String, Integer> mCustomIndexMap = new HashMap<>();
        if ("xinggan".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 1);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 2);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 1);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 2);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 1);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 4);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 2);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 1);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("tianmei".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 2);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 4);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 4);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 1);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 2);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 2);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 1);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 1);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("linjia".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 3);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 1);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 2);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 1);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 6);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 1);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 0);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 0);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("oumei".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 2);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 2);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 1);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 4);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 5);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 5);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 2);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 1);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        } else if ("wumei".equals(key)) {
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, 4);//粉底
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, 1);//口红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, 3);//腮红
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, 1);//眉毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, 2);//眼影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, 3);//眼线
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, 3);//睫毛
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, 1);//高光
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, 0);//阴影
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, 0);//美瞳
        }
        return mCustomIndexMap;
    }

    /**
     * 获取日常妆选中项的强度
     *
     * @param key
     * @return
     */
    public static HashMap<String, Double> getDailyCombinationSelectItemValue(String key) {
        HashMap<String, Double> mCustomIntensityMap = new HashMap<>();
        if ("xinggan".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_1", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.800000011920929);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0.4000000059604645);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 0.8999999761581421);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_1", 0.6000000238418579);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_4", 0.699999988079071);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 1.0);//高光
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 1.0);//阴影
        } else if ("tianmei".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_2", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.5);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_4", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_4", 0.5);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 0.699999988079071);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_2", 0.5);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_2", 0.5);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 1.0);//高光
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 1.0);//阴影
        } else if ("linjia".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_3", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.6000000238418579);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_1", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_2", 0.4);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 0.8999999761581421);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_6", 0.699999988079071);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_1", 0.699999988079071);//睫毛
        } else if ("oumei".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_2", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.8600000143051148);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0.5);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_4", 0.800000011920929);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_5", 0.4000000059604645);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_5", 0.6000000238418579);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 1.0);//高光
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 1.0);//阴影
        } else if ("wumei".equals(key)) {
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_4", 1.0);//粉底
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 0.699999988079071);//口红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_3", 1.0);//腮红
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 0.6000000238418579);//眉毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 0.699999988079071);//眼影
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_3", 0.6000000238418579);//眼线
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_3", 0.6000000238418579);//睫毛
            mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 1.0);//高光
        }
        return mCustomIntensityMap;
    }

    /**
     * 获取日常妆选中项的颜色
     *
     * @param key
     * @return
     */
    public static HashMap<String, Integer> getDailyCombinationSelectItemColor(String key) {
        HashMap<String, Integer> mCustomColorIndexMap = new HashMap<>();
        if ("xinggan".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 3);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 3);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 3);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 3);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_1", 3);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_4", 3);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 3);//高光
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 3);//阴影
        } else if ("tianmei".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 4);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_4", 4);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_4", 3);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 3);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_2", 4);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_2", 3);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 4);//高光
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 3);//阴影
        } else if ("linjia".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 5);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_1", 5);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_2", 3);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_1", 3);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_6", 5);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_1", 3);//睫毛
        } else if ("oumei".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 6);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_2", 6);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 3);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_4", 3);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_5", 6);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_5", 3);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_2", 6);//高光
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_1", 6);//阴影
        } else if ("wumei".equals(key)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_1", 7);//口红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_3", 7);//腮红
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_1", 3);//眉毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_2", 4);//眼影
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_3", 5);//眼线
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_3", 3);//睫毛
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_1", 7);//高光
        }
        return mCustomColorIndexMap;
    }
}
