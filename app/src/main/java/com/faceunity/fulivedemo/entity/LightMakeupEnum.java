package com.faceunity.fulivedemo.entity;

import androidx.core.util.Pair;

import com.faceunity.entity.Filter;
import com.faceunity.entity.LightMakeupItem;
import com.faceunity.fulivedemo.R;
import com.faceunity.param.BeautificationParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 轻美妆列表，口红用 JSON 表示，其他都是图片
 *
 * @author Richie on 2018.11.12
 */
public enum LightMakeupEnum {
    MAKEUP_NONE("卸妆", "", LightMakeupItem.FACE_MAKEUP_TYPE_NONE, R.drawable.makeup_none_normal, R.string.makeup_radio_remove, true),

    // 腮红
    MAKEUP_BLUSHER_01("MAKEUP_BLUSHER_01", "light_makeup/blusher/mu_blush_01.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_01, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_02("MAKEUP_BLUSHER_02", "light_makeup/blusher/mu_blush_02.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_02, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_03("MAKEUP_BLUSHER_03", "light_makeup/blusher/mu_blush_03.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_03, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_04("MAKEUP_BLUSHER_04", "light_makeup/blusher/mu_blush_04.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_04, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_05("MAKEUP_BLUSHER_05", "light_makeup/blusher/mu_blush_05.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_05, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_06("MAKEUP_BLUSHER_06", "light_makeup/blusher/mu_blush_06.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_06, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_07("MAKEUP_BLUSHER_07", "light_makeup/blusher/mu_blush_07.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_07, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_08("MAKEUP_BLUSHER_08", "light_makeup/blusher/mu_blush_08.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_08, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_09("MAKEUP_BLUSHER_09", "light_makeup/blusher/mu_blush_09.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_09, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_10("MAKEUP_BLUSHER_10", "light_makeup/blusher/mu_blush_10.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_10, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_11("MAKEUP_BLUSHER_11", "light_makeup/blusher/mu_blush_11.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_11, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_12("MAKEUP_BLUSHER_12", "light_makeup/blusher/mu_blush_12.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_12, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_13("MAKEUP_BLUSHER_13", "light_makeup/blusher/mu_blush_13.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_13, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_14("MAKEUP_BLUSHER_14", "light_makeup/blusher/mu_blush_14.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_14, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_15("MAKEUP_BLUSHER_15", "light_makeup/blusher/mu_blush_15.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_15, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_16("MAKEUP_BLUSHER_16", "light_makeup/blusher/mu_blush_16.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_16, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_17("MAKEUP_BLUSHER_17", "light_makeup/blusher/mu_blush_17.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_17, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_18("MAKEUP_BLUSHER_18", "light_makeup/blusher/mu_blush_18.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_18, R.string.makeup_radio_blusher, true),
    MAKEUP_BLUSHER_19("MAKEUP_BLUSHER_19", "light_makeup/blusher/mu_blush_19.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_19, R.string.makeup_radio_blusher, true),
    MAKEUP_BLUSHER_20("MAKEUP_BLUSHER_20", "light_makeup/blusher/mu_blush_20.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_20, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_21("MAKEUP_BLUSHER_21", "light_makeup/blusher/mu_blush_21.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_21, R.string.makeup_radio_blusher, true),
    MAKEUP_BLUSHER_22("MAKEUP_BLUSHER_22", "light_makeup/blusher/mu_blush_22.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_22, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_23("MAKEUP_BLUSHER_23", "light_makeup/blusher/mu_blush_23.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_23, R.string.makeup_radio_blusher, false),
    MAKEUP_BLUSHER_24("MAKEUP_BLUSHER_24", "light_makeup/blusher/mu_blush_24.png", LightMakeupItem.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.demo_blush_24, R.string.makeup_radio_blusher, false),
    // 眉毛
    MAKEUP_EYEBROW_01("MAKEUP_EYEBROW_01", "light_makeup/eyebrow/mu_eyebrow_01.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_01, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_02("MAKEUP_EYEBROW_02", "light_makeup/eyebrow/mu_eyebrow_02.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_02, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_03("MAKEUP_EYEBROW_03", "light_makeup/eyebrow/mu_eyebrow_03.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_03, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_04("MAKEUP_EYEBROW_04", "light_makeup/eyebrow/mu_eyebrow_04.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_04, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_05("MAKEUP_EYEBROW_05", "light_makeup/eyebrow/mu_eyebrow_05.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_05, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_06("MAKEUP_EYEBROW_06", "light_makeup/eyebrow/mu_eyebrow_06.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_06, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_07("MAKEUP_EYEBROW_07", "light_makeup/eyebrow/mu_eyebrow_07.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_07, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_08("MAKEUP_EYEBROW_08", "light_makeup/eyebrow/mu_eyebrow_08.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_08, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_09("MAKEUP_EYEBROW_09", "light_makeup/eyebrow/mu_eyebrow_09.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_09, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_10("MAKEUP_EYEBROW_10", "light_makeup/eyebrow/mu_eyebrow_10.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_10, R.string.makeup_radio_eyebrow, true),
    MAKEUP_EYEBROW_11("MAKEUP_EYEBROW_11", "light_makeup/eyebrow/mu_eyebrow_11.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_11, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_12("MAKEUP_EYEBROW_12", "light_makeup/eyebrow/mu_eyebrow_12.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_12, R.string.makeup_radio_eyebrow, true),
    MAKEUP_EYEBROW_13("MAKEUP_EYEBROW_13", "light_makeup/eyebrow/mu_eyebrow_13.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_13, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_14("MAKEUP_EYEBROW_14", "light_makeup/eyebrow/mu_eyebrow_14.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_14, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_15("MAKEUP_EYEBROW_15", "light_makeup/eyebrow/mu_eyebrow_15.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_15, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_16("MAKEUP_EYEBROW_16", "light_makeup/eyebrow/mu_eyebrow_16.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_16, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_17("MAKEUP_EYEBROW_17", "light_makeup/eyebrow/mu_eyebrow_17.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_17, R.string.makeup_radio_eyebrow, true),
    MAKEUP_EYEBROW_18("MAKEUP_EYEBROW_18", "light_makeup/eyebrow/mu_eyebrow_18.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_18, R.string.makeup_radio_eyebrow, false),
    MAKEUP_EYEBROW_19("MAKEUP_EYEBROW_19", "light_makeup/eyebrow/mu_eyebrow_19.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.demo_eyebrow_19, R.string.makeup_radio_eyebrow, false),

    // 睫毛
    MAKEUP_EYELASH_01("MAKEUP_EYELASH_01", "light_makeup/eyelash/mu_eyelash_01.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_01, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_02("MAKEUP_EYELASH_02", "light_makeup/eyelash/mu_eyelash_02.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_02, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_03("MAKEUP_EYELASH_03", "light_makeup/eyelash/mu_eyelash_03.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_03, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_04("MAKEUP_EYELASH_04", "light_makeup/eyelash/mu_eyelash_04.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_04, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_05("MAKEUP_EYELASH_05", "light_makeup/eyelash/mu_eyelash_05.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_05, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_06("MAKEUP_EYELASH_06", "light_makeup/eyelash/mu_eyelash_06.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_06, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_07("MAKEUP_EYELASH_07", "light_makeup/eyelash/mu_eyelash_07.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_07, R.string.makeup_radio_eyelash, false),
    MAKEUP_EYELASH_08("MAKEUP_EYELASH_08", "light_makeup/eyelash/mu_eyelash_08.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYELASH, R.drawable.demo_eyelash_08, R.string.makeup_radio_eyelash, false),

    // 眼线
    MAKEUP_EYELINER_01("MAKEUP_EYELINER_01", "light_makeup/eyeliner/mu_eyeliner_01.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_01, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_02("MAKEUP_EYELINER_02", "light_makeup/eyeliner/mu_eyeliner_02.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_02, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_03("MAKEUP_EYELINER_03", "light_makeup/eyeliner/mu_eyeliner_03.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_03, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_04("MAKEUP_EYELINER_04", "light_makeup/eyeliner/mu_eyeliner_04.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_04, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_05("MAKEUP_EYELINER_05", "light_makeup/eyeliner/mu_eyeliner_05.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_05, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_06("MAKEUP_EYELINER_06", "light_makeup/eyeliner/mu_eyeliner_06.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_06, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_07("MAKEUP_EYELINER_07", "light_makeup/eyeliner/mu_eyeliner_07.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_07, R.string.makeup_radio_eye_liner, false),
    MAKEUP_EYELINER_08("MAKEUP_EYELINER_08", "light_makeup/eyeliner/mu_eyeliner_08.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.demo_eyeliner_08, R.string.makeup_radio_eye_liner, false),

    // 美瞳
    MAKEUP_EYEPUPIL_01("MAKEUP_EYEPUPIL_01", "light_makeup/eyepupil/mu_eyepupil_01.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_01, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_02("MAKEUP_EYEPUPIL_02", "light_makeup/eyepupil/mu_eyepupil_02.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_02, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_03("MAKEUP_EYEPUPIL_03", "light_makeup/eyepupil/mu_eyepupil_03.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_03, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_04("MAKEUP_EYEPUPIL_04", "light_makeup/eyepupil/mu_eyepupil_04.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_04, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_05("MAKEUP_EYEPUPIL_05", "light_makeup/eyepupil/mu_eyepupil_05.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_05, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_06("MAKEUP_EYEPUPIL_06", "light_makeup/eyepupil/mu_eyepupil_06.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_06, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_07("MAKEUP_EYEPUPIL_07", "light_makeup/eyepupil/mu_eyepupil_07.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_07, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_08("MAKEUP_EYEPUPIL_08", "light_makeup/eyepupil/mu_eyepupil_08.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_08, R.string.makeup_radio_contact_lens, false),
    MAKEUP_EYEPUPIL_09("MAKEUP_EYEPUPIL_09", "light_makeup/eyepupil/mu_eyepupil_09.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.demo_eyepupil_09, R.string.makeup_radio_contact_lens, false),

    // 眼影
    MAKEUP_EYE_SHADOW_01("MAKEUP_EYESHADOW_01", "light_makeup/eyeshadow/mu_eyeshadow_01.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_01, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_02("MAKEUP_EYESHADOW_02", "light_makeup/eyeshadow/mu_eyeshadow_02.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_02, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_03("MAKEUP_EYESHADOW_03", "light_makeup/eyeshadow/mu_eyeshadow_03.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_03, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_04("MAKEUP_EYESHADOW_04", "light_makeup/eyeshadow/mu_eyeshadow_04.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_04, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_05("MAKEUP_EYESHADOW_05", "light_makeup/eyeshadow/mu_eyeshadow_05.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_05, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_06("MAKEUP_EYESHADOW_06", "light_makeup/eyeshadow/mu_eyeshadow_06.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_06, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_07("MAKEUP_EYESHADOW_07", "light_makeup/eyeshadow/mu_eyeshadow_07.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_07, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_08("MAKEUP_EYESHADOW_08", "light_makeup/eyeshadow/mu_eyeshadow_08.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_08, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_09("MAKEUP_EYESHADOW_09", "light_makeup/eyeshadow/mu_eyeshadow_09.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_09, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_10("MAKEUP_EYESHADOW_10", "light_makeup/eyeshadow/mu_eyeshadow_10.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_10, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_11("MAKEUP_EYESHADOW_11", "light_makeup/eyeshadow/mu_eyeshadow_11.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_11, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_12("MAKEUP_EYESHADOW_12", "light_makeup/eyeshadow/mu_eyeshadow_12.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_12, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_13("MAKEUP_EYESHADOW_13", "light_makeup/eyeshadow/mu_eyeshadow_13.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_13, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_14("MAKEUP_EYESHADOW_14", "light_makeup/eyeshadow/mu_eyeshadow_14.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_14, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_15("MAKEUP_EYESHADOW_15", "light_makeup/eyeshadow/mu_eyeshadow_15.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_15, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_16("MAKEUP_EYESHADOW_16", "light_makeup/eyeshadow/mu_eyeshadow_16.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_16, R.string.makeup_radio_eye_shadow, true),
    MAKEUP_EYE_SHADOW_17("MAKEUP_EYESHADOW_17", "light_makeup/eyeshadow/mu_eyeshadow_17.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_17, R.string.makeup_radio_eye_shadow, true),
    MAKEUP_EYE_SHADOW_18("MAKEUP_EYESHADOW_18", "light_makeup/eyeshadow/mu_eyeshadow_18.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_18, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_19("MAKEUP_EYESHADOW_19", "light_makeup/eyeshadow/mu_eyeshadow_19.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_19, R.string.makeup_radio_eye_shadow, true),
    MAKEUP_EYE_SHADOW_20("MAKEUP_EYESHADOW_20", "light_makeup/eyeshadow/mu_eyeshadow_20.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_20, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_21("MAKEUP_EYESHADOW_21", "light_makeup/eyeshadow/mu_eyeshadow_21.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_21, R.string.makeup_radio_eye_shadow, false),
    MAKEUP_EYE_SHADOW_22("MAKEUP_EYESHADOW_22", "light_makeup/eyeshadow/mu_eyeshadow_22.png", LightMakeupItem.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.demo_eyeshadow_22, R.string.makeup_radio_eye_shadow, false),

    // 口红
    MAKEUP_LIPSTICK_01("MAKEUP_LIPSTICK_01", "light_makeup/lipstick/mu_lip_01.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_01, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_02("MAKEUP_LIPSTICK_02", "light_makeup/lipstick/mu_lip_02.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_02, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_03("MAKEUP_LIPSTICK_03", "light_makeup/lipstick/mu_lip_03.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_03, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_10("MAKEUP_LIPSTICK_10", "light_makeup/lipstick/mu_lip_10.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_10, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_11("MAKEUP_LIPSTICK_11", "light_makeup/lipstick/mu_lip_11.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_12, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_12("MAKEUP_LIPSTICK_12", "light_makeup/lipstick/mu_lip_12.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_12, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_13("MAKEUP_LIPSTICK_13", "light_makeup/lipstick/mu_lip_13.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_13, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_14("MAKEUP_LIPSTICK_14", "light_makeup/lipstick/mu_lip_14.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_14, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_15("MAKEUP_LIPSTICK_15", "light_makeup/lipstick/mu_lip_15.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_15, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_16("MAKEUP_LIPSTICK_16", "light_makeup/lipstick/mu_lip_16.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_16, R.string.makeup_radio_lipstick, true),
    MAKEUP_LIPSTICK_17("MAKEUP_LIPSTICK_17", "light_makeup/lipstick/mu_lip_17.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_17, R.string.makeup_radio_lipstick, true),
    MAKEUP_LIPSTICK_18("MAKEUP_LIPSTICK_18", "light_makeup/lipstick/mu_lip_18.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_18, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_19("MAKEUP_LIPSTICK_19", "light_makeup/lipstick/mu_lip_19.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_19, R.string.makeup_radio_lipstick, true),
    MAKEUP_LIPSTICK_20("MAKEUP_LIPSTICK_20", "light_makeup/lipstick/mu_lip_20.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_20, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_21("MAKEUP_LIPSTICK_21", "light_makeup/lipstick/mu_lip_21.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_21, R.string.makeup_radio_lipstick, false),
    MAKEUP_LIPSTICK_22("MAKEUP_LIPSTICK_22", "light_makeup/lipstick/mu_lip_22.json", LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.demo_lip_22, R.string.makeup_radio_lipstick, false);

    private String name;
    private String path;
    private int type;
    private int iconId;
    private int strId;
    /**
     * 轻美妆妆容组合的整体强度
     */
    public final static Map<Integer, Float> LIGHT_MAKEUP_OVERALL_INTENSITIES = new HashMap<>(16);
    /**
     * 轻美妆妆容和滤镜的组合，http://confluence.faceunity.com/pages/viewpage.action?pageId=20332259
     */
    public static final HashMap<Integer, Pair<Filter, Float>> LIGHT_MAKEUP_FILTERS = new HashMap<>(16);

    // 桃花、西柚、清透、男友, 赤茶妆、冬日妆、奶油妆
    static {
        LIGHT_MAKEUP_OVERALL_INTENSITIES.put(R.string.makeup_peach_blossom, 0.9f);
        LIGHT_MAKEUP_OVERALL_INTENSITIES.put(R.string.makeup_grapefruit, 1.0f);
        LIGHT_MAKEUP_OVERALL_INTENSITIES.put(R.string.makeup_clear, 0.9f);
        LIGHT_MAKEUP_OVERALL_INTENSITIES.put(R.string.makeup_boyfriend, 1.0f);
        /*
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_red_tea, 1.0f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_winter, 0.9f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_cream, 1.0f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_punk, 0.85f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_maple_leaf, 1.0f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_brocade_carp, 0.9f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_plum, 0.85f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_tipsy, 1.0f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_classical, 1.0f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_disgusting, 1.0f);
        MAKEUP_OVERALL_LEVEL.put(R.string.makeup_black_white, 1.0f);
        */
    }

    // 桃花、西柚、清透、男友, 赤茶妆、冬日妆、奶油妆
    static {
        LIGHT_MAKEUP_FILTERS.put(R.string.makeup_peach_blossom, Pair.create(Filter.create(BeautificationParam.FENNEN_3), 1.0f));
        LIGHT_MAKEUP_FILTERS.put(R.string.makeup_grapefruit, Pair.create(Filter.create(BeautificationParam.LENGSEDIAO_4), 0.7f));
        LIGHT_MAKEUP_FILTERS.put(R.string.makeup_clear, Pair.create(Filter.create(BeautificationParam.XIAOQINGXIN_1), 0.8f));
        LIGHT_MAKEUP_FILTERS.put(R.string.makeup_boyfriend, Pair.create(Filter.create(BeautificationParam.XIAOQINGXIN_3), 0.9f));

//        MAKEUP_FILTERS.put(R.string.makeup_red_tea, Pair.create(Filter.create(BeautificationParam.XIAOQINGXIN_2), 0.75f));
//        MAKEUP_FILTERS.put(R.string.makeup_winter, Pair.create(Filter.create(BeautificationParam.NUANSEDIAO_1), 0.8f));
//        MAKEUP_FILTERS.put(R.string.makeup_cream, Pair.create(Filter.create(BeautificationParam.BAILIANG_1), 0.75f));
//        MAKEUP_FILTERS.put(R.string.makeup_punk, Pair.create(FilterEnum.dry.filter(), 0.5f));
//        MAKEUP_FILTERS.put(R.string.makeup_maple_leaf, Pair.create(FilterEnum.delta.filter(), 0.8f));
//        MAKEUP_FILTERS.put(R.string.makeup_brocade_carp, Pair.create(FilterEnum.linjia.filter(), 0.7f));
//        MAKEUP_FILTERS.put(R.string.makeup_classical, Pair.create(FilterEnum.hongkong.filter(), 0.85f));
//        MAKEUP_FILTERS.put(R.string.makeup_plum, Pair.create(FilterEnum.red_tea.filter(), 0.8f));
//        MAKEUP_FILTERS.put(R.string.makeup_tipsy, Pair.create(FilterEnum.hongrun.filter(), 0.55f));
//        MAKEUP_FILTERS.put(R.string.makeup_freckles, Pair.create(FilterEnum.warm.filter(), 0.4f));
    }

    LightMakeupEnum(String name, String path, int type, int iconId, int strId, boolean showInMakeup) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.iconId = iconId;
        this.strId = strId;
    }

    /**
     * 轻美妆组合，顺序为：桃花、西柚、清透、男友
     *
     * @return
     */
    public static List<LightMakeupCombination> getLightMakeupCombinations() {
        List<LightMakeupCombination> faceMakeups = new ArrayList<>();
        LightMakeupCombination none = new LightMakeupCombination(null, R.string.makeup_radio_remove, R.drawable.makeup_none_normal);
        faceMakeups.add(none);

        // 桃花
        List<LightMakeupItem> peachBlossomMakeups = new ArrayList<>(4);
        peachBlossomMakeups.add(MAKEUP_BLUSHER_01.lightMakeup(0.9f));
        peachBlossomMakeups.add(MAKEUP_EYE_SHADOW_01.lightMakeup(0.9f));
        peachBlossomMakeups.add(MAKEUP_EYEBROW_01.lightMakeup(0.5f));
        peachBlossomMakeups.add(MAKEUP_LIPSTICK_01.lightMakeup(0.9f));
        LightMakeupCombination peachBlossom = new LightMakeupCombination(peachBlossomMakeups, R.string.makeup_peach_blossom, R.drawable.demo_makeup_peachblossom);
        faceMakeups.add(peachBlossom);

        // 西柚
        List<LightMakeupItem> grapeMakeups = new ArrayList<>(4);
        grapeMakeups.add(MAKEUP_BLUSHER_23.lightMakeup(1.0f));
        grapeMakeups.add(MAKEUP_EYE_SHADOW_21.lightMakeup(0.75f));
        grapeMakeups.add(MAKEUP_EYEBROW_19.lightMakeup(0.6f));
        grapeMakeups.add(MAKEUP_LIPSTICK_21.lightMakeup(0.8f));
        LightMakeupCombination grape = new LightMakeupCombination(grapeMakeups, R.string.makeup_grapefruit, R.drawable.demo_makeup_grapefruit);
        faceMakeups.add(grape);

        // 清透
        List<LightMakeupItem> clearMakeups = new ArrayList<>(4);
        clearMakeups.add(MAKEUP_BLUSHER_22.lightMakeup(0.9f));
        clearMakeups.add(MAKEUP_EYE_SHADOW_20.lightMakeup(0.65f));
        clearMakeups.add(MAKEUP_EYEBROW_18.lightMakeup(0.45f));
        clearMakeups.add(MAKEUP_LIPSTICK_20.lightMakeup(0.8f));
        LightMakeupCombination clear = new LightMakeupCombination(clearMakeups, R.string.makeup_clear, R.drawable.demo_makeup_clear);
        faceMakeups.add(clear);

        // 男友
        List<LightMakeupItem> boyFriendMakeups = new ArrayList<>(4);
        boyFriendMakeups.add(MAKEUP_BLUSHER_20.lightMakeup(0.8f));
        boyFriendMakeups.add(MAKEUP_EYE_SHADOW_18.lightMakeup(0.9f));
        boyFriendMakeups.add(MAKEUP_EYEBROW_16.lightMakeup(0.65f));
        boyFriendMakeups.add(MAKEUP_LIPSTICK_18.lightMakeup(1.0f));
        LightMakeupCombination boyFriend = new LightMakeupCombination(boyFriendMakeups, R.string.makeup_boyfriend, R.drawable.demo_makeup_boyfriend);
        faceMakeups.add(boyFriend);
        return faceMakeups;
    }

    public LightMakeupItem lightMakeup(float level) {
        return new LightMakeupItem(name, path, type, strId, iconId, level);
    }
}