package com.faceunity.fulivedemo.entity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;

import com.faceunity.entity.NewMakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MakeupParamHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Richie on 2019.06.14
 */
public class FaceMakeupConfig {
    // makeup list. read only, key is type, value is list.
    public static final Map<Integer, List<NewMakeupItem>> MAKEUP_ITEM_MAP = new HashMap<>(10);
    private static final String TAG = "FaceMakeupConfig";
    private static final String MAKEUP_RESOURCE_DIR = "makeup_resource" + File.separator;
    private static final String MAKEUP_RESOURCE_COMMON_DIR = MAKEUP_RESOURCE_DIR + "common" + File.separator;
    private static final double[] TRANSPARENT = new double[]{0.0, 0.0, 0.0, 0.0};

    public static void initConfigs(Context context) {
        createMakeupItems(context);
    }

    /**
     * 单项妆容，粉底 口红 腮红 眉毛 眼影 眼线 睫毛 高光 阴影 美瞳
     *
     * @param context
     */
    private static void createMakeupItems(Context context) {
        if (!MAKEUP_ITEM_MAP.isEmpty()) {
            return;
        }

        Map<String, List<double[]>> makeupColorMap = readMakeupColor(context);
        Resources resources = context.getResources();
        // 粉底
        int type = NewMakeupItem.FACE_MAKEUP_TYPE_FOUNDATION;
        List<NewMakeupItem> makeupItems = new ArrayList<>(8);
        Map<String, Object> paramMap = new HashMap<>(4);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_FOUNDATION, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_FOUNDATION, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_FOUNDATION, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        NewMakeupItem makeupItem;
        Drawable iconDrawable;
        List<double[]> foundationColorList = makeupColorMap.get("color_mu_style_foundation_01");
        if (foundationColorList != null) {
            // 排除透明色
            for (int i = 3; i < 8; i++) {
                double[] colors = foundationColorList.get(i);
                paramMap = new HashMap<>(4);
                paramMap.put(MakeupParamHelper.MakeupParams.TEX_FOUNDATION, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_foundation_01.png");
                iconDrawable = new ColorDrawable(Color.argb((int) (colors[3] * 255), (int) (colors[0] * 255), (int) (colors[1] * 255), (int) (colors[2] * 255)));
                // 粉底没有名称，这里就用位置替代一下
                makeupItem = new NewMakeupItem(type, -i, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_FOUNDATION,
                        MakeupParamHelper.MakeupParams.MAKEUP_FOUNDATION_COLOR, foundationColorList, iconDrawable, paramMap);
                makeupItems.add(makeupItem);
            }
        }

        // 口红
        type = NewMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK;
        makeupItems = new ArrayList<>(4);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_LIP, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_LIP, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));
        MAKEUP_ITEM_MAP.put(type, makeupItems);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.LIP_TYPE, 0.0);
        paramMap.put(MakeupParamHelper.MakeupParams.IS_TWO_COLOR, 0.0);
        makeupItem = new NewMakeupItem(type, R.string.makeup_lip_fog, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_LIP,
                MakeupParamHelper.MakeupParams.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_lip_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.LIP_TYPE, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.IS_TWO_COLOR, 0.0);
        makeupItem = new NewMakeupItem(type, R.string.makeup_lip_satin, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_LIP,
                MakeupParamHelper.MakeupParams.MAKEUP_LIP_COLOR, makeupColorMap.get("color_mu_style_lip_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 腮红
        type = NewMakeupItem.FACE_MAKEUP_TYPE_BLUSHER;
        makeupItems = new ArrayList<>(8);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));
        MAKEUP_ITEM_MAP.put(type, makeupItems);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BLUSHER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_blush_01.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_blusher_apple, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParams.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BLUSHER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_blush_02.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_blusher_fan, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParams.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BLUSHER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_blush_03.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_blusher_eye_corner, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParams.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_blush_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BLUSHER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_blush_04.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_blusher_slight_drunk, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER,
                MakeupParamHelper.MakeupParams.MAKEUP_BLUSHER_COLOR, makeupColorMap.get("color_mu_style_blush_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 眉毛
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYEBROW;
        makeupItems = new ArrayList<>(4);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW, 0.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 0.0);
        makeupItems.add(new NewMakeupItem(type, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_WILLOW);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_willow, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_STANDARD);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_standard, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_HILL);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_hill, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_ONE_WORD);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_one_word, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_SHAPE);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_shape, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_DAILY);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_daily, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyebrow_07);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_BROW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyebrow_01.png");
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP, 1.0);
        paramMap.put(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_JAPAN);
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyebrow_japan, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR, makeupColorMap.get("color_mu_style_eyebrow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 眼影
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW;
        makeupItems = new ArrayList<>(8);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow1_01.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_shadow_single, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow1_02.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE2, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow2_02.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_shadow_double1, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow1_03.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE2, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow2_03.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_shadow_double2, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow1_04.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE2, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow2_04.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_shadow_double3, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow1_05.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE2, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow2_05.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE3, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow3_05.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_shadow_triple1, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_05"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeshadow_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow1_06.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE2, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow2_06.png");
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE3, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeshadow3_06.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_shadow_triple2, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR, makeupColorMap.get("color_mu_style_eyeshadow_06"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 眼线
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER;
        makeupItems = new ArrayList<>(8);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LINER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeliner_01.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_linear_cat, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LINER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeliner_02.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_linear_drooping, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LINER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeliner_03.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_linear_pull_open, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LINER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeliner_04.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_linear_pull_close, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LINER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeliner_05.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_linear_long, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_05"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyeliner_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LINER, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyeliner_06.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eye_linear_circular, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR, makeupColorMap.get("color_mu_style_eyeliner_06"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 睫毛
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYELASH;
        makeupItems = new ArrayList<>(8);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LASH, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyelash_01.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyelash_natural1, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LASH, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyelash_02.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyelash_natural2, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_03);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LASH, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyelash_03.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyelash_thick1, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_03"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_04);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LASH, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyelash_04.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyelash_thick2, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_04"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_05);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LASH, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyelash_05.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyelash_exaggerate1, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_05"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyelash_06);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_EYE_LASH, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyelash_06.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_eyelash_exaggerate2, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH,
                MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR, makeupColorMap.get("color_mu_style_eyelash_06"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 高光
        type = NewMakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT;
        makeupItems = new ArrayList<>(4);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_HIGHLIGHT, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_HIGHLIGHT, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_highlight_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_HIGHLIGHT, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_highlight_01.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_highlight_one, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_HIGHLIGHT,
                MakeupParamHelper.MakeupParams.MAKEUP_HIGHLIGHT_COLOR, makeupColorMap.get("color_mu_style_highlight_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        iconDrawable = resources.getDrawable(R.drawable.demo_style_highlight_02);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_HIGHLIGHT, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_highlight_02.png");
        makeupItem = new NewMakeupItem(type, R.string.makeup_highlight_two, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_HIGHLIGHT,
                MakeupParamHelper.MakeupParams.MAKEUP_HIGHLIGHT_COLOR, makeupColorMap.get("color_mu_style_highlight_02"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 阴影
        type = NewMakeupItem.FACE_MAKEUP_TYPE_SHADOW;
        makeupItems = new ArrayList<>(2);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_SHADOW, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_SHADOW, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_SHADOW, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_contour_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_SHADOW, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_contour_01.png");
        makeupItem = new NewMakeupItem(type, 0, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_SHADOW,
                MakeupParamHelper.MakeupParams.MAKEUP_SHADOW_COLOR, makeupColorMap.get("color_mu_style_contour_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);

        // 美瞳
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL;
        makeupItems = new ArrayList<>(2);
        MAKEUP_ITEM_MAP.put(type, makeupItems);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PUPIL, 0.0);
        makeupItems.add(new NewMakeupItem(NewMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.string.makeup_radio_remove,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PUPIL, resources.getDrawable(R.drawable.makeup_none_normal), paramMap));

        iconDrawable = resources.getDrawable(R.drawable.demo_style_eyepupil_01);
        paramMap = new HashMap<>(4);
        paramMap.put(MakeupParamHelper.MakeupParams.TEX_PUPIL, MAKEUP_RESOURCE_COMMON_DIR + "mu_style_eyepupil_01.png");
        makeupItem = new NewMakeupItem(type, 0, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PUPIL,
                MakeupParamHelper.MakeupParams.MAKEUP_PUPIL_COLOR, makeupColorMap.get("color_mu_style_eyepupil_01"), iconDrawable, paramMap);
        makeupItems.add(makeupItem);
    }

    private static Map<String, List<double[]>> readMakeupColor(Context context) {
        Map<String, List<double[]>> makeupColorMap = new HashMap<>(32);
        String colorJson = null;
        try {
            colorJson = FileUtils.readStringFromAssetsFile(context, MAKEUP_RESOURCE_DIR + "makeup_color_setup.json");
        } catch (IOException e) {
            Log.e(TAG, "readMakeupColor: ", e);
        }
        if (colorJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(colorJson);
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    List<double[]> colorList = new ArrayList<>(12);
                    colorList.add(TRANSPARENT);
                    colorList.add(TRANSPARENT);
                    colorList.add(TRANSPARENT);
                    JSONObject eyebrowColor = jsonObject.optJSONObject(key);
                    for (int i = 1; i <= 5; i++) {
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
                Log.e(TAG, "readMakeupColor: ", e);
            }
        }
        return makeupColorMap;
    }


    /**
     * 组合妆容
     *
     * @param context
     * @return
     */
    public static List<MakeupCombination> createMakeupCombination(Context context) {
        List<MakeupCombination> makeupCombinations = new ArrayList<>(6);
        Map<String, Object> removeParams = new HashMap<>(16);
        for (String makeupIntensity : MakeupParamHelper.MakeupParams.MAKEUP_INTENSITIES) {
            removeParams.put(makeupIntensity, 0.0);
        }
        removeParams.put(MakeupParamHelper.MakeupParams.BROW_WARP, 0.0);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_radio_remove, R.drawable.makeup_none_normal, removeParams));

        // 日常妆
        String resourcePath = MAKEUP_RESOURCE_DIR + "combination_01_sexy" + File.separator;
        Map<String, Object> paramMap = readCombinationMakeup(context, resourcePath);
        SparseArray<MakeupCombination.SubItem> subItems = new SparseArray<>(10);
        createMakeupSubItems(paramMap, subItems);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_sexy, R.drawable.demo_combination_sexy, paramMap, subItems));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_02_sweet" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        subItems = new SparseArray<>(10);
        createMakeupSubItems(paramMap, subItems);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_sweet, R.drawable.demo_combination_sweet, paramMap, subItems));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_03_neighbor" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        subItems = new SparseArray<>(10);
        createMakeupSubItems(paramMap, subItems);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_neighbor, R.drawable.demo_combination_neighbor_girl, paramMap, subItems));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_04_occident" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        subItems = new SparseArray<>(10);
        createMakeupSubItems(paramMap, subItems);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_occident, R.drawable.demo_combination_occident, paramMap, subItems));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_05_charming" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        subItems = new SparseArray<>(10);
        createMakeupSubItems(paramMap, subItems);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_charming, R.drawable.demo_combination_charming, paramMap, subItems));

        // 主题妆
        resourcePath = MAKEUP_RESOURCE_DIR + "combination_06_flower" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_flower, R.drawable.demo_combination_flower, paramMap));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_10_man" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_man, R.drawable.demo_combination_tough_guy, paramMap));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_07_moon" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_moon, R.drawable.demo_combination_moon, paramMap));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_08_coral" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_coral, R.drawable.demo_combination_coral, paramMap));

        resourcePath = MAKEUP_RESOURCE_DIR + "combination_09_lady" + File.separator;
        paramMap = readCombinationMakeup(context, resourcePath);
        makeupCombinations.add(new MakeupCombination(R.string.makeup_combination_lady, R.drawable.demo_combination_lady, paramMap));

        return makeupCombinations;
    }

    private static void createMakeupSubItems(Map<String, Object> paramMap, SparseArray<MakeupCombination.SubItem> subItems) {
        int type = NewMakeupItem.FACE_MAKEUP_TYPE_EYEBROW;
        MakeupCombination.SubItem subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_BROW,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW, MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_EYE,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE, MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_PUPIL,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PUPIL, MakeupParamHelper.MakeupParams.MAKEUP_PUPIL_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYELASH;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_EYE_LASH,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH, MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_EYE_LINER,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER, MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_BLUSHER;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_BLUSHER,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER, MakeupParamHelper.MakeupParams.MAKEUP_BLUSHER_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_FOUNDATION;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_FOUNDATION,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_FOUNDATION, MakeupParamHelper.MakeupParams.MAKEUP_FOUNDATION_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_HIGHLIGHT;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_HIGHLIGHT,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_HIGHLIGHT, MakeupParamHelper.MakeupParams.MAKEUP_HIGHLIGHT_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_SHADOW;
        subItem = createSubItem(paramMap, type, MakeupParamHelper.MakeupParams.TEX_SHADOW,
                MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_SHADOW, MakeupParamHelper.MakeupParams.MAKEUP_SHADOW_COLOR);
        subItems.put(type, subItem);
        type = NewMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK;
        subItem = createSubItem(paramMap, type, null, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_LIP, MakeupParamHelper.MakeupParams.MAKEUP_LIP_COLOR);
        subItems.put(type, subItem);
    }

    private static MakeupCombination.SubItem createSubItem(Map<String, Object> combinationMap, int type, String texName, String intensityName, String colorName) {
        List<NewMakeupItem> makeupItems = MAKEUP_ITEM_MAP.get(type);
        double intensity = (double) combinationMap.get(intensityName);
        double browType = 0;
        if (combinationMap.containsKey(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE)) {
            browType = (double) combinationMap.get(MakeupParamHelper.MakeupParams.BROW_WARP_TYPE);
        }
        for (NewMakeupItem makeupItem : makeupItems) {
            Map<String, Object> paramMap = makeupItem.getParamMap();
            Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (type == NewMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK) {
                    List<double[]> colors = makeupItem.getColorList();
                    if (colors == null) {
                        continue;
                    }
                    double[] combinationColor = (double[]) combinationMap.get(colorName);
                    int colorPosition = 3;// defalut is 3
                    for (int i = 0; i < colors.size(); i++) {
                        if (Arrays.equals(combinationColor, colors.get(i))) {
                            colorPosition = i;
                            break;
                        }
                    }
                    double lipType = (double) combinationMap.get(MakeupParamHelper.MakeupParams.LIP_TYPE);
                    int itemPosition = (int) lipType + 1;
//                    Log.d(TAG, "createSubItem: type: " + type + ",  intensity: " + intensity + ", itemPos: " + itemPosition + ", colorPos:" + colorPosition);
                    MakeupCombination.SubItem subItem = new MakeupCombination.SubItem(type, intensity, itemPosition, colorPosition);
                    return subItem;
                } else {
                    if (texName.equals(entry.getKey())) {
                        String tex = (String) entry.getValue();
                        String combinationTex = (String) combinationMap.get(texName);
                        if (combinationTex.substring(combinationTex.lastIndexOf(File.separator) + 1)
                                .equals(tex.substring(tex.lastIndexOf(File.separator) + 1))) {
                            int itemPosition = 0;
                            int colorPosition = 3;// default is 3
                            if (intensity != 0.0) {
                                if (type == NewMakeupItem.FACE_MAKEUP_TYPE_EYEBROW) {
                                    itemPosition = getPositionOfEyebrow((int) browType) + 1;
                                } else {
                                    try {
                                        String seq = tex.substring(tex.lastIndexOf("_") + 1, tex.lastIndexOf("."));
                                        itemPosition = Integer.valueOf(seq);
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, "createSubItem: ", e);
                                    }
                                }

                                List<double[]> colors = makeupItem.getColorList();
                                double[] combinationColor = (double[]) combinationMap.get(colorName);
                                for (int i = 0; i < colors.size(); i++) {
                                    if (Arrays.equals(combinationColor, colors.get(i))) {
                                        colorPosition = i;
                                        break;
                                    }
                                }

                                if (type == NewMakeupItem.FACE_MAKEUP_TYPE_FOUNDATION) {
                                    colorPosition -= 2;
                                }
                            }
//                            Log.d(TAG, "createSubItem: type: " + type + ", intensity: " + intensity + ", itemPos: " + itemPosition + ", colorPos:" + colorPosition);
                            MakeupCombination.SubItem subItem = new MakeupCombination.SubItem(type, intensity, itemPosition, colorPosition);
                            return subItem;
                        }
                    }
                }
            }
        }
        return new MakeupCombination.SubItem(type, 1.0, 0, 0);
    }

    private static int getPositionOfEyebrow(int browType) {
        switch (browType) {
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_WILLOW:
                return 0;
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_STANDARD:
                return 1;
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_HILL:
                return 2;
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_ONE_WORD:
                return 3;
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_SHAPE:
                return 4;
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_DAILY:
                return 5;
            case (int) MakeupParamHelper.MakeupParams.BROW_WARP_TYPE_JAPAN:
                return 6;
            default:
                return 0;
        }
    }

    private static Map<String, Object> readCombinationMakeup(Context context, String path) {
        try {
            String content = FileUtils.readStringFromAssetsFile(context, path + "makeup.json");
            Map<String, Object> map = new HashMap<>(32);
            for (String makeupIntensity : MakeupParamHelper.MakeupParams.MAKEUP_INTENSITIES) {
                map.put(makeupIntensity, 0.0);
            }
            map.putAll(readParamsFromJson(content));
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getValue() instanceof String) {
                    entry.setValue(path + entry.getValue());
                }
            }
            return map;
        } catch (IOException e) {
            Log.e(TAG, "readCombinationMakeup: ", e);
        }
        return null;
    }

    private static Map<String, Object> readParamsFromJson(String jsonStr) {
        Map<String, Object> map = new HashMap<>(32);
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_BROW);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_EYE);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_EYE2);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_EYE3);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_PUPIL);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_EYE_LASH);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_EYE_LINER);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_BLUSHER);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_FOUNDATION);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_HIGHLIGHT);
            parseString(map, jsonObject, MakeupParamHelper.MakeupParams.TEX_SHADOW);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.IS_TWO_COLOR);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.LIP_TYPE);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.BROW_WARP);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.BROW_WARP_TYPE);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_LIP);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_PUPIL);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_LINER);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYELASH);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_EYE_BROW);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_BLUSHER);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_FOUNDATION);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_HIGHLIGHT);
            parseDouble(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_INTENSITY_SHADOW);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_EYE_BROW_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_LIP_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_LIP_COLOR2);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_EYE_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_EYE_LINER_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_EYELASH_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_BLUSHER_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_FOUNDATION_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_HIGHLIGHT_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_SHADOW_COLOR);
            parseFloatArray(map, jsonObject, MakeupParamHelper.MakeupParams.MAKEUP_PUPIL_COLOR);
        } catch (JSONException e) {
            Log.e(TAG, "readParamsFromJson: ", e);
        }
        return map;
    }

    private static void parseString(Map<String, Object> map, JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            String string = jsonObject.optString(key);
            map.put(key, string);
        }
    }

    private static void parseDouble(Map<String, Object> map, JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            double d = jsonObject.optDouble(key);
            map.put(key, d);
        }
    }

    private static void parseFloatArray(Map<String, Object> map, JSONObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            JSONArray jsonArray = jsonObject.optJSONArray(key);
            if (jsonArray != null) {
                int length = jsonArray.length();
                double[] values = new double[length];
                for (int i = 0; i < length; i++) {
                    values[i] = jsonArray.optDouble(i);
                }
                map.put(key, values);
            }
        }
    }

}
