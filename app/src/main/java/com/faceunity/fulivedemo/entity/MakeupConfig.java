package com.faceunity.fulivedemo.entity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;

import com.faceunity.entity.Filter;
import com.faceunity.entity.MakeupEntity;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.DecimalUtils;
import com.faceunity.param.BeautificationParam;
import com.faceunity.param.MakeupParamHelper;
import com.faceunity.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 新版美妆配置
 *
 * @author Richie on 2019.06.14
 */
public class MakeupConfig {
    // 默认美妆强度
    public static final float DEFAULT_INTENSITY = 0.7F;
    // 默认滤镜强度
    public static final float FILTER_INTENSITY = 0.7F;
    // makeup list. read only, key is type, value is list.
    public static final Map<Integer, List<MakeupItem>> MAKEUP_ITEM_MAP = new HashMap<>(16);
    private static final String TAG = "MakeupConfig";
    private static final String MAKEUP_RESOURCE_DIR = "makeup" + File.separator;
    /* 美妆组合妆配置文件 */
    private static final String MAKEUP_RESOURCE_JSON_DIR = MAKEUP_RESOURCE_DIR + "config_json" + File.separator;
    /* 美妆组合妆bundle文件 */
    private static final String MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR = MAKEUP_RESOURCE_DIR + "combination_bundle" + File.separator;
    /* 美妆妆容单项bundle文件 */
    private static final String MAKEUP_RESOURCE_ITEM_BUNDLE_DIR = MAKEUP_RESOURCE_DIR + "item_bundle" + File.separator;
    private static final double[] TRANSPARENT = new double[]{0.0, 0.0, 0.0, 0.0};
    public static final Map<Integer, Filter> MAKEUP_COMBINATION_FILTER_MAP = new HashMap<>(16);

    public static void initConfigs(Context context) {
        initMakeupItem(context);
        initMakeupFilter();
    }

    /**
     * 美妆与滤镜组合
     */
    private static void initMakeupFilter() {
        if (!MAKEUP_COMBINATION_FILTER_MAP.isEmpty()) {
            return;
        }

        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_jianling, Filter.create(BeautificationParam.ZHIGANHUI_1));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_nuandong, Filter.create(BeautificationParam.ZHIGANHUI_2));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_hongfeng, Filter.create(BeautificationParam.ZHIGANHUI_3));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_shaonv, Filter.create(BeautificationParam.ZHIGANHUI_4));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_ziyun, Filter.create(BeautificationParam.ZHIGANHUI_1));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_yanshimao, Filter.create(BeautificationParam.ZHIGANHUI_5));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_renyu, Filter.create(BeautificationParam.ZHIGANHUI_1));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_chuqiu, Filter.create(BeautificationParam.ZHIGANHUI_6));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_qianzhihe, Filter.create(BeautificationParam.ZHIGANHUI_2));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_chaomo, Filter.create(BeautificationParam.ZHIGANHUI_7));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_chuju, Filter.create(BeautificationParam.ZHIGANHUI_8));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_gangfeng, Filter.create(BeautificationParam.ZIRAN_8));
        MAKEUP_COMBINATION_FILTER_MAP.put(R.string.makeup_combination_rose, Filter.create(BeautificationParam.ZHIGANHUI_2));
    }

    /**
     * 单项妆容，粉底 口红 腮红 眉毛 眼影 眼线 睫毛 高光 阴影 美瞳
     *
     * @param context
     */
    private static void initMakeupItem(Context context) {
        if (!MAKEUP_ITEM_MAP.isEmpty()) {
            return;
        }

        Map<String, List<double[]>> makeupColorMap = loadInitialMakeupColor(context);
        Resources resources = context.getResources();
        // 粉底
        int type = MakeupItem.FACE_MAKEUP_TYPE_FOUNDATION;
        List<MakeupItem> makeupItems = new ArrayList<>(8);
        Map<String, Object> paramMap = new HashMap<>(4);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_FOUNDATION, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_FOUNDATION, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_FOUNDATION, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        MakeupItem makeupItem;
        Drawable iconDrawable;
        List<double[]> foundationColorList = makeupColorMap.get("color_mu_style_foundation_01");
        if (foundationColorList != null) {
            // 排除透明色
            for (int i = 3; i < 8; i++) {
                double[] colors = foundationColorList.get(i);
                paramMap = new HashMap<>(4);
                paramMap.put(MakeupParamHelper.MakeupParam.TEX_FOUNDATION, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_foundation_01.bundle");
                iconDrawable = new ColorDrawable(Color.argb((int) (colors[3] * 255), (int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255)));
                // 由于粉底没有名称，这里构造参数就用位置取负
                makeupItem = new MakeupItem(type, -i, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_FOUNDATION,
                        MakeupParamHelper.MakeupParam.MAKEUP_FOUNDATION_COLOR, foundationColorList, iconDrawable, paramMap);
                makeupItems.add(makeupItem);
            }
        }

        // 口红
        type = MakeupItem.FACE_MAKEUP_TYPE_LIPSTICK;
        makeupItems = new ArrayList<>(4);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));
        MAKEUP_ITEM_MAP.put(type, makeupItems);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.LIP_TYPE, 0.0);
        paramMap.put(MakeupParamHelper.MakeupParam.IS_TWO_COLOR, 0.0);
        makeupItem = new MakeupItem(type, R.string.makeup_lip_fog, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP,
                MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        //        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_02);
        //        paramMap = new HashMap<>(4);
        //        paramMap.put(MakeupParamHelper.MakeupParam.LIP_TYPE, 1.0);
        //        paramMap.put(MakeupParamHelper.MakeupParam.IS_TWO_COLOR, 0.0);
        //        makeupItem = new MakeupItem(type, R.string.makeup_lip_satin, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP,
        //                MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        //        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.LIP_TYPE, 2.0);
        paramMap.put(MakeupParamHelper.MakeupParam.IS_TWO_COLOR, 0.0);
        makeupItem = new MakeupItem(type, R.string.makeup_lip_moist, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP,
                MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.LIP_TYPE, 3.0);
        paramMap.put(MakeupParamHelper.MakeupParam.IS_TWO_COLOR, 0.0);
        makeupItem = new MakeupItem(type, R.string.makeup_lip_pearl, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP,
                MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR2, new double[]{0.0, 0.0, 0.0, 0.0});
        paramMap.put(MakeupParamHelper.MakeupParam.LIP_TYPE, 0.0);
        paramMap.put(MakeupParamHelper.MakeupParam.IS_TWO_COLOR, 1.0);
        makeupItem = new MakeupItem(type, R.string.makeup_lip_bitelip, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP,
                MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 腮红
        type = MakeupItem.FACE_MAKEUP_TYPE_BLUSHER;
        makeupItems = new ArrayList<>(8);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));
        MAKEUP_ITEM_MAP.put(type, makeupItems);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BLUSHER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_blush_01.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_blusher_apple, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParam.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BLUSHER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_blush_02.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_blusher_fan, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParam.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BLUSHER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_blush_03.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_blusher_eye_corner, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParam.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BLUSHER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_blush_04.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_blusher_slight_drunk, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParam.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

//        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_05);
//        paramMap = new HashMap<>(4);
//        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BLUSHER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_blush_05.bundle");
//        makeupItem = new MakeupItem(type, R.string.makeup_blusher_sunburn, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER,
//                MakeupParamHelper.MakeupParam.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_05"), iconDrawable, paramMap);
//        makeupItems.add(makeupItem);

        // 眉毛
        type = MakeupItem.FACE_MAKEUP_TYPE_EYEBROW;
        makeupItems = new ArrayList<>(4);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW, 0.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 0.0);
        makeupItems.add(new MakeupItem(type, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_01);
        paramMap = new LinkedHashMap<>(4);
        // 一定保证有序，先绑定 bundle，再设置参数
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_WILLOW);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_willow, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_02);
        paramMap = new LinkedHashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_STANDARD);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_standard, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_03);
        paramMap = new LinkedHashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_HILL);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_hill, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_04);
        paramMap = new LinkedHashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_ONE_WORD);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_one_word, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_05);
        paramMap = new LinkedHashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_SHAPE);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_shape, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_06);
        paramMap = new LinkedHashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_DAILY);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_daily, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_07);
        paramMap = new LinkedHashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_BROW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyebrow_01.bundle");
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE, MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_JAPAN);
        makeupItem = new MakeupItem(type, R.string.makeup_eyebrow_japan, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 眼影
        type = MakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW;
        makeupItems = new ArrayList<>(8);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_01.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_single, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_02.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_double1, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_03.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_double2, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_04.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_double3, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        //        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_08);
        //        paramMap = new HashMap<>(4);
        //        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_05.bundle");
        //        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_double4, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
        //                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_05"), iconDrawable, paramMap);
        //        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_05.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_triple1, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_05"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_06.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_triple2, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_06"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        //        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_07);
        //        paramMap = new HashMap<>(4);
        //        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeshadow_08.bundle");
        //        makeupItem = new MakeupItem(type, R.string.makeup_eye_shadow_triple3, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE,
        //                MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_06"), iconDrawable, paramMap);
        //        makeupItems.add(makeupItem);

        // 眼线
        type = MakeupItem.FACE_MAKEUP_TYPE_EYE_LINER;
        makeupItems = new ArrayList<>(8);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_01.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_cat, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_02.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_drooping, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_03.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_pull_open, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_04.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_pull_close, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_05.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_long, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_05"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_06.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_circular, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_06"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        //        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_07);
        //        paramMap = new HashMap<>(4);
        //        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LINER, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyeliner_07.bundle");
        //        makeupItem = new MakeupItem(type, R.string.makeup_eye_linear_natural, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER,
        //                MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_06"), iconDrawable, paramMap);
        //        makeupItems.add(makeupItem);

        // 睫毛
        type = MakeupItem.FACE_MAKEUP_TYPE_EYELASH;
        makeupItems = new ArrayList<>(8);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LASH, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyelash_01.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eyelash_natural1, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LASH, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyelash_02.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eyelash_natural2, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LASH, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyelash_03.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eyelash_thick1, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LASH, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyelash_04.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eyelash_thick2, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LASH, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyelash_05.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eyelash_exaggerate1, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_05"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_EYE_LASH, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyelash_06.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_eyelash_exaggerate2, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_06"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 高光
        type = MakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT;
        makeupItems = new ArrayList<>(4);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_HIGHLIGHT, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_HIGHLIGHT, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_highlight_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_HIGHLIGHT, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_highlight_01.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_highlight_one, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_HIGHLIGHT,
                MakeupParamHelper.MakeupParam.MAKEUP_HIGHLIGHT_COLOR, makeupColorMap.get("color_mu_style_highlight_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_highlight_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_HIGHLIGHT, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_highlight_02.bundle");
        makeupItem = new MakeupItem(type, R.string.makeup_highlight_two, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_HIGHLIGHT,
                MakeupParamHelper.MakeupParam.MAKEUP_HIGHLIGHT_COLOR, makeupColorMap.get("color_mu_style_highlight_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 阴影
        type = MakeupItem.FACE_MAKEUP_TYPE_SHADOW;
        makeupItems = new ArrayList<>(2);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_SHADOW, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_SHADOW, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_SHADOW, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_contour_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_SHADOW, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_contour_01.bundle");
        makeupItem = new MakeupItem(type, 0, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_SHADOW,
                MakeupParamHelper.MakeupParam.MAKEUP_SHADOW_COLOR, makeupColorMap.get("color_mu_style_contour_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 美瞳
        type = MakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL;
        makeupItems = new ArrayList<>(2);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PUPIL, 0.0);
        makeupItems.add(new MakeupItem(MakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PUPIL, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyepupil_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParam.TEX_PUPIL, MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + "mu_style_eyepupil_01.bundle");
        makeupItem = new MakeupItem(type, 0, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PUPIL,
                MakeupParamHelper.MakeupParam.MAKEUP_PUPIL_COLOR, makeupColorMap.get("color_mu_style_eyepupil_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);
    }

    private static Map<String, List<double[]>> loadInitialMakeupColor(Context context) {
        Map<String, List<double[]>> makeupColorMap = new HashMap<>(32);
        String colorJson = null;
        try {
            colorJson = FileUtils.readStringFromAssetsFile(context, MAKEUP_RESOURCE_DIR + "color_setup.json");
        } catch (IOException e) {
            Log.e(TAG, "loadInitialMakeupColor: ", e);
        }
        if (colorJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(colorJson);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    List<double[]> colorList = new ArrayList<>(12);
                    // add additional transparent to fit ui
                    colorList.add(TRANSPARENT);
                    colorList.add(TRANSPARENT);
                    colorList.add(TRANSPARENT);
                    JSONObject eyebrowColor = jsonObject.optJSONObject(key);
                    for (int i = 1; i <= 5; i++) { // hard code! color length is 5
                        JSONArray jsonArray = eyebrowColor.optJSONArray("color" + i);
                        int length = jsonArray.length();
                        double[] colors = new double[length];
                        for (int j = 0; j < length; j++) {
                            colors[j] = jsonArray.optDouble(j, 0);
                        }
                        colorList.add(colors);
                    }
                    colorList.add(TRANSPARENT);
                    colorList.add(TRANSPARENT);
                    colorList.add(TRANSPARENT);
                    makeupColorMap.put(key, colorList);
                }
            } catch (JSONException e) {
                Log.e(TAG, "loadInitialMakeupColor: ", e);
            }
        }
        return makeupColorMap;
    }

    /**
     * 创建组合妆容列表
     *
     * @return
     */
    public static List<MakeupCombination> createMakeupCombinations() {
        List<MakeupCombination> makeupCombinations = new ArrayList<>(16);
        // 卸妆
        makeupCombinations.add(new MakeupCombination(R.string.makeup_radio_remove, R.drawable.makeup_none_normal,
                MakeupCombination.TYPE_NONE, MAKEUP_RESOURCE_JSON_DIR + "remove.json",
                new MakeupEntity("")));

        // 主题妆，13 个
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_jianling, R.drawable.demo_combination_age,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "jianling.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "jianling.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_nuandong, R.drawable.demo_combination_warm_winter,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "nuandong.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "nuandong.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_hongfeng, R.drawable.demo_combination_red_maple,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "hongfeng.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "hongfeng.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_rose, R.drawable.demo_combination_rose,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "rose.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "rose.bundle", true)));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_shaonv, R.drawable.demo_combination_girl,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "shaonv.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "shaonv.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_ziyun, R.drawable.demo_combination_purple_rhyme,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "ziyun.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "ziyun.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_yanshimao, R.drawable.demo_combination_bored_cat,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "yanshimao.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "yanshimao.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_renyu, R.drawable.demo_combination_mermaid,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "renyu.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "renyu.bundle", true)));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_chuqiu, R.drawable.demo_combination_early_autumn,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "chuqiu.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "chuqiu.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_qianzhihe, R.drawable.demo_combination_paper_cranes,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "qianzhihe.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "qianzhihe.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_chaomo, R.drawable.demo_combination_supermodel,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "chaomo.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "chaomo.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_chuju, R.drawable.demo_combination_daisy,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "chuju.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "chuju.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_gangfeng, R.drawable.demo_combination_harbour_wind,
                MakeupCombination.TYPE_THEME, MAKEUP_RESOURCE_JSON_DIR + "gangfeng.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "gangfeng.bundle", true)));

        // 日常妆，5个
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_sexy, R.drawable.demo_combination_sexy,
                MakeupCombination.TYPE_DAILY, MAKEUP_RESOURCE_JSON_DIR + "xinggan.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "xinggan.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_sweet, R.drawable.demo_combination_sweet,
                MakeupCombination.TYPE_DAILY, MAKEUP_RESOURCE_JSON_DIR + "tianmei.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "tianmei.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_neighbor, R.drawable.demo_combination_neighbor_girl,
                MakeupCombination.TYPE_DAILY, MAKEUP_RESOURCE_JSON_DIR + "linjia.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "linjia.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_occident, R.drawable.demo_combination_occident,
                MakeupCombination.TYPE_DAILY, MAKEUP_RESOURCE_JSON_DIR + "oumei.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "oumei.bundle")));
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_charming, R.drawable.demo_combination_charming,
                MakeupCombination.TYPE_DAILY, MAKEUP_RESOURCE_JSON_DIR + "wumei.json",
                new MakeupEntity(MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR + "wumei.bundle")));
        return makeupCombinations;
    }

    /**
     * 创建二级妆容单项选择
     *
     * @param paramMap
     * @return
     */
    public static SparseArray<MakeupCombination.SubItem> createMakeupSubItems(Map<String, Object> paramMap) {
        SparseArray<MakeupCombination.SubItem> subItems = new SparseArray<>(10);

        int type = MakeupItem.FACE_MAKEUP_TYPE_EYEBROW;
        MakeupCombination.SubItem subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_BROW,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_BROW, MakeupParamHelper.MakeupParam.MAKEUP_EYE_BROW_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_EYE,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE, MakeupParamHelper.MakeupParam.MAKEUP_EYE_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_PUPIL,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_PUPIL, MakeupParamHelper.MakeupParam.MAKEUP_PUPIL_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_EYELASH;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_EYE_LASH,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYELASH, MakeupParamHelper.MakeupParam.MAKEUP_EYELASH_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_EYE_LINER;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_EYE_LINER,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_EYE_LINER, MakeupParamHelper.MakeupParam.MAKEUP_EYE_LINER_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_BLUSHER;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_BLUSHER,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_BLUSHER, MakeupParamHelper.MakeupParam.MAKEUP_BLUSHER_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_FOUNDATION;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_FOUNDATION,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_FOUNDATION, MakeupParamHelper.MakeupParam.MAKEUP_FOUNDATION_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_HIGHLIGHT,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_HIGHLIGHT, MakeupParamHelper.MakeupParam.MAKEUP_HIGHLIGHT_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_SHADOW;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParam.TEX_SHADOW,
                MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_SHADOW, MakeupParamHelper.MakeupParam.MAKEUP_SHADOW_COLOR);
        subItems.put(type, subItem);

        type = MakeupItem.FACE_MAKEUP_TYPE_LIPSTICK;
        subItem = createSubItem(paramMap, type, null, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP, MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR);
        subItems.put(type, subItem);
        return subItems;
    }

    private static MakeupCombination.SubItem createSubItem(Map<String, Object> combinationMap, int type,
                                                           String texName, String intensityName, String colorName) {
        if (combinationMap == null) {
            return new MakeupCombination.SubItem(type, 1.0, 0, 3);
        }
        double browType = 0.0;
        if (combinationMap.containsKey(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE)) {
            browType = (double) combinationMap.get(MakeupParamHelper.MakeupParam.BROW_WARP_TYPE);
        }
        double intensity = (double) combinationMap.get(intensityName);
        List<MakeupItem> makeupItems = MAKEUP_ITEM_MAP.get(type);
        for (MakeupItem makeupItem : makeupItems) {
            Map<String, Object> paramMap = makeupItem.getParamMap();
            Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (type == MakeupItem.FACE_MAKEUP_TYPE_LIPSTICK) {
                    List<double[]> colors = makeupItem.getColorList();
                    if (colors == null) {
                        continue;
                    }
                    double[] combinationColor = (double[]) combinationMap.get(colorName);
                    int colorPosition = 3; // default color position is 3
                    for (int i = 0; i < colors.size(); i++) {
                        if (DecimalUtils.doubleArrayEquals(combinationColor, colors.get(i))) {
                            colorPosition = i;
                            break;
                        }
                    }
                    int itemPosition = 0;
                    if (combinationMap.containsKey(MakeupParamHelper.MakeupParam.LIP_TYPE)) {
                        double lipType = (double) combinationMap.get(MakeupParamHelper.MakeupParam.LIP_TYPE);
                        itemPosition = (int) lipType + 1; // the first is remove
                    }
                    return new MakeupCombination.SubItem(type, intensity, itemPosition, colorPosition);
                } else {
                    if (texName.equals(entry.getKey())) {
                        String itemTex = (String) entry.getValue();
                        String combinationTex = (String) combinationMap.get(texName);
                        if (combinationTex != null && combinationTex.substring(combinationTex.lastIndexOf(File.separator) + 1)
                                .equals(itemTex.substring(itemTex.lastIndexOf(File.separator) + 1))) {
                            int itemPosition = 0;
                            int colorPosition = 3;
                            if (!DecimalUtils.doubleEquals(intensity, 0.0)) {
                                if (type == MakeupItem.FACE_MAKEUP_TYPE_EYEBROW) {
                                    itemPosition = getPositionOfEyebrow((int) browType) + 1;
                                } else {
                                    try {
                                        String seq = itemTex.substring(itemTex.lastIndexOf("_") + 1, itemTex.lastIndexOf("."));
                                        itemPosition = Integer.valueOf(seq);
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, "createSubItem: ", e);
                                    }
                                }

                                List<double[]> itemColorList = makeupItem.getColorList();
                                if (combinationMap.containsKey(colorName)) {
                                    // item color is [5, 12], or [5, 4]
                                    // combination color is [5, 4]
                                    double[] combinationColor = (double[]) combinationMap.get(colorName);
                                    double[] temp;
                                    if (combinationColor.length != itemColorList.get(3).length) { // exclude transparent
                                        temp = new double[itemColorList.get(3).length];
                                        System.arraycopy(combinationColor, 0, temp, 0, combinationColor.length);
                                        for (int i = 2; i < 4; i++) {
                                            String key = colorName + i;
                                            if (combinationMap.containsKey(key)) {
                                                double[] combinationColorI = (double[]) combinationMap.get(key);
                                                System.arraycopy(combinationColorI, 0, temp, (i - 1) * 4, combinationColorI.length);
                                            }
                                        }
                                    } else {
                                        temp = combinationColor;
                                    }
                                    for (int i = 0; i < itemColorList.size(); i++) {
                                        double[] color = itemColorList.get(i);
                                        if (DecimalUtils.doubleArrayEquals(color, temp)) {
                                            colorPosition = i;
                                            break;
                                        }
                                    }
                                    if (type == MakeupItem.FACE_MAKEUP_TYPE_FOUNDATION) {
                                        colorPosition -= 2;
                                    }
                                }
                            }
                            if (colorPosition < 3) {
                                colorPosition = 3;
                            }
                            return new MakeupCombination.SubItem(type, intensity, itemPosition, colorPosition);
                        }
                    }
                }
            }
        }
        return new MakeupCombination.SubItem(type, 1.0, 0, 3);
    }

    private static int getPositionOfEyebrow(int browType) {
        switch (browType) {
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_WILLOW:
                return 0;
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_STANDARD:
                return 1;
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_HILL:
                return 2;
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_ONE_WORD:
                return 3;
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_SHAPE:
                return 4;
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_DAILY:
                return 5;
            case (int) MakeupParamHelper.MakeupParam.BROW_WARP_TYPE_JAPAN:
                return 6;
            default:
                return 0;
        }
    }

    /**
     * 读取美妆组合的参数
     *
     * @param context
     * @param jsonPath
     * @return
     */
    public static Map<String, Object> loadMakeupParamsFromJson(Context context, String jsonPath) {
        String content = null;
        try {
            content = FileUtils.readStringFromAssetsFile(context, jsonPath);
        } catch (IOException e) {
            Log.e(TAG, "loadMakeupParamsFromJson: ", e);
        }

        Map<String, Object> map = new HashMap<>(32);
        if (content != null) {
            for (String makeupIntensity : MakeupParamHelper.MakeupParam.MAKEUP_INTENSITIES) {
                map.put(makeupIntensity, 0.0);
            }
            map.putAll(readParamFromJson(content));
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                Object value = entry.getValue();
                if (value instanceof String && ((String) value).endsWith(".bundle")) {
                    entry.setValue(MAKEUP_RESOURCE_ITEM_BUNDLE_DIR + value);
                }
            }
        }
        return map;
    }

    /**
     * 从 JSON 中读取配置参数
     *
     * @param jsonStr
     * @return
     */
    private static Map<String, Object> readParamFromJson(String jsonStr) {
        Map<String, Object> map = new HashMap<>(32);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            for (Iterator<String> keys = jsonObject.keys(); keys.hasNext(); ) {
                String key = keys.next();
                Object obj = jsonObject.opt(key);
                if (obj instanceof String || obj instanceof Double) {
                    map.put(key, obj);
                } else if (obj instanceof Integer) {
                    // regard integer as double
                    double value = (int) obj;
                    map.put(key, value);
                } else if (obj instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) obj;
                    int length = jsonArray.length();
                    double[] value = new double[length];
                    for (int i = 0; i < length; i++) {
                        value[i] = jsonArray.optDouble(i);
                    }
                    map.put(key, value);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "readParamFromJson: ", e);
        }
        return map;
    }

}