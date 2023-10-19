package com.faceunity.app.data.source;

import android.content.Context;

import com.faceunity.app.DemoApplication;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.core.model.littleMakeup.LightMakeup;
import com.faceunity.ui.entity.LightMakeupBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * DESC：轻美妆数据构造
 * Created on 2021/3/27
 */
public class LightMakeupSource {

    /**
     * 构造轻美妆队列
     *
     * @return ArrayList<LightMakeupBean>
     */
    public static ArrayList<LightMakeupBean> buildLightMakeup() {
        ArrayList<LightMakeupBean> makeups = new ArrayList<>();
        makeups.add(new LightMakeupBean(R.string.makeup_radio_remove, R.mipmap.icon_control_none, null, 0.0, FaceBeautyFilterEnum.ZIRAN_2, 0.4));
        /*桃花*/
        makeups.add(new LightMakeupBean(R.string.makeup_peach_blossom, R.mipmap.icon_light_makeup_peachblossom, "taohua", 0.9, FaceBeautyFilterEnum.FENNEN_3, 0.9));
        /*西柚*/
        makeups.add(new LightMakeupBean(R.string.makeup_grapefruit, R.mipmap.icon_light_makeup_grapefruit, "xiyou", 1.0, FaceBeautyFilterEnum.LENGSEDIAO_4, 1.0));
        /*清透*/
        makeups.add(new LightMakeupBean(R.string.makeup_clear, R.mipmap.icon_light_makeup_clear, "qingtou", 0.9, FaceBeautyFilterEnum.XIAOQINGXIN_1, 0.9));
        /*男友*/
        makeups.add(new LightMakeupBean(R.string.makeup_boyfriend, R.mipmap.icon_light_makeup_boyfriend, "nanyou", 1.0, FaceBeautyFilterEnum.XIAOQINGXIN_3, 1.0));
        return makeups;
    }


    /**
     * 风格对应参数配置
     */
    public static HashMap<String, Runnable> LightMakeupParams = new HashMap<String, Runnable>() {
        {
            put("taohua", () -> {
                LightMakeup lightMakeup = new LightMakeup(new FUBundleData(DemoConfig.BUNDLE_LIGHT_MAKEUP));
                lightMakeup.setBlusherTex(LightMakeUpEnum.MAKEUP_BLUSHER_01.path);
                lightMakeup.setBlusherIntensity(0.9);
                lightMakeup.setEyeShadowTex(LightMakeUpEnum.MAKEUP_EYE_SHADOW_01.path);
                lightMakeup.setEyeShadowIntensity(0.9);
                lightMakeup.setEyeBrowTex(LightMakeUpEnum.MAKEUP_EYEBROW_01.path);
                lightMakeup.setEyeBrowIntensity(0.5);
                lightMakeup.setLipColor(LightMakeUpEnum.MAKEUP_LIPSTICK_01.getLipColorRGBData());
                lightMakeup.setLipIntensity(0.9);
                FURenderKit.getInstance().setLightMakeup(lightMakeup);
            });
            put("xiyou", () -> {
                LightMakeup lightMakeup = new LightMakeup(new FUBundleData(DemoConfig.BUNDLE_LIGHT_MAKEUP));
                lightMakeup.setBlusherTex(LightMakeUpEnum.MAKEUP_BLUSHER_23.path);
                lightMakeup.setBlusherIntensity(1.0);
                lightMakeup.setEyeShadowTex(LightMakeUpEnum.MAKEUP_EYE_SHADOW_21.path);
                lightMakeup.setEyeShadowIntensity(0.75);
                lightMakeup.setEyeBrowTex(LightMakeUpEnum.MAKEUP_EYEBROW_19.path);
                lightMakeup.setEyeBrowIntensity(0.6);
                lightMakeup.setLipColor(LightMakeUpEnum.MAKEUP_LIPSTICK_21.getLipColorRGBData());
                lightMakeup.setLipIntensity(0.8);
                FURenderKit.getInstance().setLightMakeup(lightMakeup);
            });
            put("qingtou", () -> {
                LightMakeup lightMakeup = new LightMakeup(new FUBundleData(DemoConfig.BUNDLE_LIGHT_MAKEUP));
                lightMakeup.setBlusherTex(LightMakeUpEnum.MAKEUP_BLUSHER_22.path);
                lightMakeup.setBlusherIntensity(0.9);
                lightMakeup.setEyeShadowTex(LightMakeUpEnum.MAKEUP_EYE_SHADOW_20.path);
                lightMakeup.setEyeShadowIntensity(0.65);
                lightMakeup.setEyeBrowTex(LightMakeUpEnum.MAKEUP_EYEBROW_18.path);
                lightMakeup.setEyeBrowIntensity(0.45);
                lightMakeup.setLipColor(LightMakeUpEnum.MAKEUP_LIPSTICK_20.getLipColorRGBData());
                lightMakeup.setLipIntensity(0.8);
                FURenderKit.getInstance().setLightMakeup(lightMakeup);
            });
            put("nanyou", () -> {
                LightMakeup lightMakeup = new LightMakeup(new FUBundleData(DemoConfig.BUNDLE_LIGHT_MAKEUP));
                lightMakeup.setBlusherTex(LightMakeUpEnum.MAKEUP_BLUSHER_20.path);
                lightMakeup.setBlusherIntensity(0.8);
                lightMakeup.setEyeShadowTex(LightMakeUpEnum.MAKEUP_EYE_SHADOW_18.path);
                lightMakeup.setEyeShadowIntensity(0.9);
                lightMakeup.setEyeBrowTex(LightMakeUpEnum.MAKEUP_EYEBROW_16.path);
                lightMakeup.setEyeBrowIntensity(0.65);
                lightMakeup.setLipColor(LightMakeUpEnum.MAKEUP_LIPSTICK_18.getLipColorRGBData());
                lightMakeup.setLipIntensity(1.0);
                FURenderKit.getInstance().setLightMakeup(lightMakeup);
            });
        }

    };


    //region 轻美妆效果枚举
    public enum LightMakeUpEnum {
        /**
         * 美妆项，前几项是预置的效果
         * 排在列表最前方，顺序为桃花妆、雀斑妆、朋克妆（其中朋克没有腮红，3个妆容的眼线、眼睫毛共用1个的）
         */
        // 腮红
        MAKEUP_BLUSHER_01("MAKEUP_BLUSHER_01", "light_makeup/blusher/mu_blush_01.png", R.mipmap.icon_light_makeup_blush_01, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_02("MAKEUP_BLUSHER_02", "light_makeup/blusher/mu_blush_02.png", R.mipmap.icon_light_makeup_blush_02, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_03("MAKEUP_BLUSHER_03", "light_makeup/blusher/mu_blush_03.png", R.mipmap.icon_light_makeup_blush_03, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_04("MAKEUP_BLUSHER_04", "light_makeup/blusher/mu_blush_04.png", R.mipmap.icon_light_makeup_blush_04, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_05("MAKEUP_BLUSHER_05", "light_makeup/blusher/mu_blush_05.png", R.mipmap.icon_light_makeup_blush_05, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_06("MAKEUP_BLUSHER_06", "light_makeup/blusher/mu_blush_06.png", R.mipmap.icon_light_makeup_blush_06, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_07("MAKEUP_BLUSHER_07", "light_makeup/blusher/mu_blush_07.png", R.mipmap.icon_light_makeup_blush_07, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_08("MAKEUP_BLUSHER_08", "light_makeup/blusher/mu_blush_08.png", R.mipmap.icon_light_makeup_blush_08, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_09("MAKEUP_BLUSHER_09", "light_makeup/blusher/mu_blush_09.png", R.mipmap.icon_light_makeup_blush_09, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_10("MAKEUP_BLUSHER_10", "light_makeup/blusher/mu_blush_10.png", R.mipmap.icon_light_makeup_blush_10, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_11("MAKEUP_BLUSHER_11", "light_makeup/blusher/mu_blush_11.png", R.mipmap.icon_light_makeup_blush_11, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_12("MAKEUP_BLUSHER_12", "light_makeup/blusher/mu_blush_12.png", R.mipmap.icon_light_makeup_blush_12, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_13("MAKEUP_BLUSHER_13", "light_makeup/blusher/mu_blush_13.png", R.mipmap.icon_light_makeup_blush_13, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_14("MAKEUP_BLUSHER_14", "light_makeup/blusher/mu_blush_14.png", R.mipmap.icon_light_makeup_blush_14, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_15("MAKEUP_BLUSHER_15", "light_makeup/blusher/mu_blush_15.png", R.mipmap.icon_light_makeup_blush_15, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_16("MAKEUP_BLUSHER_16", "light_makeup/blusher/mu_blush_16.png", R.mipmap.icon_light_makeup_blush_16, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_17("MAKEUP_BLUSHER_17", "light_makeup/blusher/mu_blush_17.png", R.mipmap.icon_light_makeup_blush_17, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_18("MAKEUP_BLUSHER_18", "light_makeup/blusher/mu_blush_18.png", R.mipmap.icon_light_makeup_blush_18, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_19("MAKEUP_BLUSHER_19", "light_makeup/blusher/mu_blush_19.png", R.mipmap.icon_light_makeup_blush_19, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_20("MAKEUP_BLUSHER_20", "light_makeup/blusher/mu_blush_20.png", R.mipmap.icon_light_makeup_blush_20, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_21("MAKEUP_BLUSHER_21", "light_makeup/blusher/mu_blush_21.png", R.mipmap.icon_light_makeup_blush_21, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_22("MAKEUP_BLUSHER_22", "light_makeup/blusher/mu_blush_22.png", R.mipmap.icon_light_makeup_blush_22, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_23("MAKEUP_BLUSHER_23", "light_makeup/blusher/mu_blush_23.png", R.mipmap.icon_light_makeup_blush_23, R.string.makeup_radio_blusher),

        MAKEUP_BLUSHER_24("MAKEUP_BLUSHER_24", "light_makeup/blusher/mu_blush_24.png", R.mipmap.icon_light_makeup_blush_24, R.string.makeup_radio_blusher),

        // 眉毛
        MAKEUP_EYEBROW_01("MAKEUP_EYEBROW_01", "light_makeup/eyebrow/mu_eyebrow_01.png", R.mipmap.icon_light_makeup_eyebrow_01, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_02("MAKEUP_EYEBROW_02", "light_makeup/eyebrow/mu_eyebrow_02.png", R.mipmap.icon_light_makeup_eyebrow_02, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_03("MAKEUP_EYEBROW_03", "light_makeup/eyebrow/mu_eyebrow_03.png", R.mipmap.icon_light_makeup_eyebrow_03, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_04("MAKEUP_EYEBROW_04", "light_makeup/eyebrow/mu_eyebrow_04.png", R.mipmap.icon_light_makeup_eyebrow_04, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_05("MAKEUP_EYEBROW_05", "light_makeup/eyebrow/mu_eyebrow_05.png", R.mipmap.icon_light_makeup_eyebrow_05, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_06("MAKEUP_EYEBROW_06", "light_makeup/eyebrow/mu_eyebrow_06.png", R.mipmap.icon_light_makeup_eyebrow_06, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_07("MAKEUP_EYEBROW_07", "light_makeup/eyebrow/mu_eyebrow_07.png", R.mipmap.icon_light_makeup_eyebrow_07, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_08("MAKEUP_EYEBROW_08", "light_makeup/eyebrow/mu_eyebrow_08.png", R.mipmap.icon_light_makeup_eyebrow_08, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_09("MAKEUP_EYEBROW_09", "light_makeup/eyebrow/mu_eyebrow_09.png", R.mipmap.icon_light_makeup_eyebrow_09, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_10("MAKEUP_EYEBROW_10", "light_makeup/eyebrow/mu_eyebrow_10.png", R.mipmap.icon_light_makeup_eyebrow_10, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_11("MAKEUP_EYEBROW_11", "light_makeup/eyebrow/mu_eyebrow_11.png", R.mipmap.icon_light_makeup_eyebrow_11, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_12("MAKEUP_EYEBROW_12", "light_makeup/eyebrow/mu_eyebrow_12.png", R.mipmap.icon_light_makeup_eyebrow_12, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_13("MAKEUP_EYEBROW_13", "light_makeup/eyebrow/mu_eyebrow_13.png", R.mipmap.icon_light_makeup_eyebrow_13, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_14("MAKEUP_EYEBROW_14", "light_makeup/eyebrow/mu_eyebrow_14.png", R.mipmap.icon_light_makeup_eyebrow_14, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_15("MAKEUP_EYEBROW_15", "light_makeup/eyebrow/mu_eyebrow_15.png", R.mipmap.icon_light_makeup_eyebrow_15, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_16("MAKEUP_EYEBROW_16", "light_makeup/eyebrow/mu_eyebrow_16.png", R.mipmap.icon_light_makeup_eyebrow_16, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_17("MAKEUP_EYEBROW_17", "light_makeup/eyebrow/mu_eyebrow_17.png", R.mipmap.icon_light_makeup_eyebrow_17, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_18("MAKEUP_EYEBROW_18", "light_makeup/eyebrow/mu_eyebrow_18.png", R.mipmap.icon_light_makeup_eyebrow_18, R.string.makeup_radio_eyebrow),

        MAKEUP_EYEBROW_19("MAKEUP_EYEBROW_19", "light_makeup/eyebrow/mu_eyebrow_19.png", R.mipmap.icon_light_makeup_eyebrow_19, R.string.makeup_radio_eyebrow),

        // 睫毛
        MAKEUP_EYELASH_01("MAKEUP_EYELASH_01", "light_makeup/eyelash/mu_eyelash_01.png", R.mipmap.icon_light_makeup_eyelash_01, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_02("MAKEUP_EYELASH_02", "light_makeup/eyelash/mu_eyelash_02.png", R.mipmap.icon_light_makeup_eyelash_02, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_03("MAKEUP_EYELASH_03", "light_makeup/eyelash/mu_eyelash_03.png", R.mipmap.icon_light_makeup_eyelash_03, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_04("MAKEUP_EYELASH_04", "light_makeup/eyelash/mu_eyelash_04.png", R.mipmap.icon_light_makeup_eyelash_04, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_05("MAKEUP_EYELASH_05", "light_makeup/eyelash/mu_eyelash_05.png", R.mipmap.icon_light_makeup_eyelash_05, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_06("MAKEUP_EYELASH_06", "light_makeup/eyelash/mu_eyelash_06.png", R.mipmap.icon_light_makeup_eyelash_06, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_07("MAKEUP_EYELASH_07", "light_makeup/eyelash/mu_eyelash_07.png", R.mipmap.icon_light_makeup_eyelash_07, R.string.makeup_radio_eyelash),

        MAKEUP_EYELASH_08("MAKEUP_EYELASH_08", "light_makeup/eyelash/mu_eyelash_08.png", R.mipmap.icon_light_makeup_eyelash_08, R.string.makeup_radio_eyelash),

        // 眼线
        MAKEUP_EYELINER_01("MAKEUP_EYELINER_01", "light_makeup/eyeliner/mu_eyeliner_01.png", R.mipmap.icon_light_makeup_eyeliner_01, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_02("MAKEUP_EYELINER_02", "light_makeup/eyeliner/mu_eyeliner_02.png", R.mipmap.icon_light_makeup_eyeliner_02, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_03("MAKEUP_EYELINER_03", "light_makeup/eyeliner/mu_eyeliner_03.png", R.mipmap.icon_light_makeup_eyeliner_03, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_04("MAKEUP_EYELINER_04", "light_makeup/eyeliner/mu_eyeliner_04.png", R.mipmap.icon_light_makeup_eyeliner_04, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_05("MAKEUP_EYELINER_05", "light_makeup/eyeliner/mu_eyeliner_05.png", R.mipmap.icon_light_makeup_eyeliner_05, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_06("MAKEUP_EYELINER_06", "light_makeup/eyeliner/mu_eyeliner_06.png", R.mipmap.icon_light_makeup_eyeliner_06, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_07("MAKEUP_EYELINER_07", "light_makeup/eyeliner/mu_eyeliner_07.png", R.mipmap.icon_light_makeup_eyeliner_07, R.string.makeup_radio_eye_liner),

        MAKEUP_EYELINER_08("MAKEUP_EYELINER_08", "light_makeup/eyeliner/mu_eyeliner_08.png", R.mipmap.icon_light_makeup_eyeliner_08, R.string.makeup_radio_eye_liner),

        // 美瞳
        MAKEUP_EYEPUPIL_01("MAKEUP_EYEPUPIL_01", "light_makeup/eyepupil/mu_eyepupil_01.png", R.mipmap.icon_light_makeup_eyepupil_01, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_02("MAKEUP_EYEPUPIL_02", "light_makeup/eyepupil/mu_eyepupil_02.png", R.mipmap.icon_light_makeup_eyepupil_02, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_03("MAKEUP_EYEPUPIL_03", "light_makeup/eyepupil/mu_eyepupil_03.png", R.mipmap.icon_light_makeup_eyepupil_03, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_04("MAKEUP_EYEPUPIL_04", "light_makeup/eyepupil/mu_eyepupil_04.png", R.mipmap.icon_light_makeup_eyepupil_04, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_05("MAKEUP_EYEPUPIL_05", "light_makeup/eyepupil/mu_eyepupil_05.png", R.mipmap.icon_light_makeup_eyepupil_05, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_06("MAKEUP_EYEPUPIL_06", "light_makeup/eyepupil/mu_eyepupil_06.png", R.mipmap.icon_light_makeup_eyepupil_06, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_07("MAKEUP_EYEPUPIL_07", "light_makeup/eyepupil/mu_eyepupil_07.png", R.mipmap.icon_light_makeup_eyepupil_07, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_08("MAKEUP_EYEPUPIL_08", "light_makeup/eyepupil/mu_eyepupil_08.png", R.mipmap.icon_light_makeup_eyepupil_08, R.string.makeup_radio_contact_lens),

        MAKEUP_EYEPUPIL_09("MAKEUP_EYEPUPIL_09", "light_makeup/eyepupil/mu_eyepupil_09.png", R.mipmap.icon_light_makeup_eyepupil_09, R.string.makeup_radio_contact_lens),

        // 眼影
        MAKEUP_EYE_SHADOW_01("MAKEUP_EYESHADOW_01", "light_makeup/eyeshadow/mu_eyeshadow_01.png", R.mipmap.icon_light_makeup_eyeshadow_01, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_02("MAKEUP_EYESHADOW_02", "light_makeup/eyeshadow/mu_eyeshadow_02.png", R.mipmap.icon_light_makeup_eyeshadow_02, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_03("MAKEUP_EYESHADOW_03", "light_makeup/eyeshadow/mu_eyeshadow_03.png", R.mipmap.icon_light_makeup_eyeshadow_03, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_04("MAKEUP_EYESHADOW_04", "light_makeup/eyeshadow/mu_eyeshadow_04.png", R.mipmap.icon_light_makeup_eyeshadow_04, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_05("MAKEUP_EYESHADOW_05", "light_makeup/eyeshadow/mu_eyeshadow_05.png", R.mipmap.icon_light_makeup_eyeshadow_05, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_06("MAKEUP_EYESHADOW_06", "light_makeup/eyeshadow/mu_eyeshadow_06.png", R.mipmap.icon_light_makeup_eyeshadow_06, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_07("MAKEUP_EYESHADOW_07", "light_makeup/eyeshadow/mu_eyeshadow_07.png", R.mipmap.icon_light_makeup_eyeshadow_07, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_08("MAKEUP_EYESHADOW_08", "light_makeup/eyeshadow/mu_eyeshadow_08.png", R.mipmap.icon_light_makeup_eyeshadow_08, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_09("MAKEUP_EYESHADOW_09", "light_makeup/eyeshadow/mu_eyeshadow_09.png", R.mipmap.icon_light_makeup_eyeshadow_09, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_10("MAKEUP_EYESHADOW_10", "light_makeup/eyeshadow/mu_eyeshadow_10.png", R.mipmap.icon_light_makeup_eyeshadow_10, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_11("MAKEUP_EYESHADOW_11", "light_makeup/eyeshadow/mu_eyeshadow_11.png", R.mipmap.icon_light_makeup_eyeshadow_11, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_12("MAKEUP_EYESHADOW_12", "light_makeup/eyeshadow/mu_eyeshadow_12.png", R.mipmap.icon_light_makeup_eyeshadow_12, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_13("MAKEUP_EYESHADOW_13", "light_makeup/eyeshadow/mu_eyeshadow_13.png", R.mipmap.icon_light_makeup_eyeshadow_13, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_14("MAKEUP_EYESHADOW_14", "light_makeup/eyeshadow/mu_eyeshadow_14.png", R.mipmap.icon_light_makeup_eyeshadow_14, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_15("MAKEUP_EYESHADOW_15", "light_makeup/eyeshadow/mu_eyeshadow_15.png", R.mipmap.icon_light_makeup_eyeshadow_15, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_16("MAKEUP_EYESHADOW_16", "light_makeup/eyeshadow/mu_eyeshadow_16.png", R.mipmap.icon_light_makeup_eyeshadow_16, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_17("MAKEUP_EYESHADOW_17", "light_makeup/eyeshadow/mu_eyeshadow_17.png", R.mipmap.icon_light_makeup_eyeshadow_17, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_18("MAKEUP_EYESHADOW_18", "light_makeup/eyeshadow/mu_eyeshadow_18.png", R.mipmap.icon_light_makeup_eyeshadow_18, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_19("MAKEUP_EYESHADOW_19", "light_makeup/eyeshadow/mu_eyeshadow_19.png", R.mipmap.icon_light_makeup_eyeshadow_19, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_20("MAKEUP_EYESHADOW_20", "light_makeup/eyeshadow/mu_eyeshadow_20.png", R.mipmap.icon_light_makeup_eyeshadow_20, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_21("MAKEUP_EYESHADOW_21", "light_makeup/eyeshadow/mu_eyeshadow_21.png", R.mipmap.icon_light_makeup_eyeshadow_21, R.string.makeup_radio_eye_shadow),

        MAKEUP_EYE_SHADOW_22("MAKEUP_EYESHADOW_22", "light_makeup/eyeshadow/mu_eyeshadow_22.png", R.mipmap.icon_light_makeup_eyeshadow_22, R.string.makeup_radio_eye_shadow),

        // 口红
        MAKEUP_LIPSTICK_01("MAKEUP_LIPSTICK_01", "light_makeup/lipstick/mu_lip_01.json", R.mipmap.icon_light_makeup_lip_01, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_02("MAKEUP_LIPSTICK_02", "light_makeup/lipstick/mu_lip_02.json", R.mipmap.icon_light_makeup_lip_02, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_03("MAKEUP_LIPSTICK_03", "light_makeup/lipstick/mu_lip_03.json", R.mipmap.icon_light_makeup_lip_03, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_10("MAKEUP_LIPSTICK_10", "light_makeup/lipstick/mu_lip_10.json", R.mipmap.icon_light_makeup_lip_10, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_11("MAKEUP_LIPSTICK_11", "light_makeup/lipstick/mu_lip_11.json", R.mipmap.icon_light_makeup_lip_12, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_12("MAKEUP_LIPSTICK_12", "light_makeup/lipstick/mu_lip_12.json", R.mipmap.icon_light_makeup_lip_12, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_13("MAKEUP_LIPSTICK_13", "light_makeup/lipstick/mu_lip_13.json", R.mipmap.icon_light_makeup_lip_13, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_14("MAKEUP_LIPSTICK_14", "light_makeup/lipstick/mu_lip_14.json", R.mipmap.icon_light_makeup_lip_14, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_15("MAKEUP_LIPSTICK_15", "light_makeup/lipstick/mu_lip_15.json", R.mipmap.icon_light_makeup_lip_15, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_16("MAKEUP_LIPSTICK_16", "light_makeup/lipstick/mu_lip_16.json", R.mipmap.icon_light_makeup_lip_16, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_17("MAKEUP_LIPSTICK_17", "light_makeup/lipstick/mu_lip_17.json", R.mipmap.icon_light_makeup_lip_17, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_18("MAKEUP_LIPSTICK_18", "light_makeup/lipstick/mu_lip_18.json", R.mipmap.icon_light_makeup_lip_18, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_19("MAKEUP_LIPSTICK_19", "light_makeup/lipstick/mu_lip_19.json", R.mipmap.icon_light_makeup_lip_19, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_20("MAKEUP_LIPSTICK_20", "light_makeup/lipstick/mu_lip_20.json", R.mipmap.icon_light_makeup_lip_20, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_21("MAKEUP_LIPSTICK_21", "light_makeup/lipstick/mu_lip_21.json", R.mipmap.icon_light_makeup_lip_21, R.string.makeup_radio_lipstick),

        MAKEUP_LIPSTICK_22("MAKEUP_LIPSTICK_22", "light_makeup/lipstick/mu_lip_22.json", R.mipmap.icon_light_makeup_lip_22, R.string.makeup_radio_lipstick);


        private final String key;
        private final String path;
        private final int iconRes;
        private final int strRes;

        LightMakeUpEnum(String key, String path, int iconRes, int strRes) {
            this.key = key;
            this.path = path;
            this.iconRes = iconRes;
            this.strRes = strRes;
        }

        /**
         * 获取口红颜色
         *
         * @return
         */
        public FUColorRGBData getLipColorRGBData() {
            double[] colorArray = loadRgbaColorFromLocal(DemoApplication.mApplication, path);
            if (colorArray != null && colorArray.length == 4) {
                return new FUColorRGBData(colorArray[0] * 255, colorArray[1] * 255, colorArray[2] * 255, colorArray[3] * 255);
            }
            return new FUColorRGBData(0.0, 0.0, 0.0, 0.0);
        }

    }

    //endregion

    /**
     * 读取 RGBA 颜色数据
     *
     * @param context
     * @param path    path
     * @return
     */
    public static double[] loadRgbaColorFromLocal(Context context, String path) {
        InputStream inputStream = FileUtils.readInputStreamByPath(context, path);
        if (inputStream != null) {
            try {
                byte[] bytes = new byte[inputStream.available()];
                inputStream.read(bytes);
                JSONObject jsonObject = new JSONObject(new String(bytes));
                JSONArray jsonArray = jsonObject.optJSONArray("rgba");
                double[] colorArray = new double[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    colorArray[i] = jsonArray.optDouble(i);
                }
                return colorArray;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
