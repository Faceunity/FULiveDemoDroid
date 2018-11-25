package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.FaceMakeup;
import com.faceunity.entity.MakeupItem;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LiuQiang on 2018.11.12
 * 口红用 JSON 表示，其他都是图片
 */
public enum FaceMakeupEnum {

    /**
     * 美妆项，前几项是预置的效果
     * 排在列表最前方，顺序为桃花妆、雀斑妆、朋克妆（其中朋克没有腮红，3个妆容的眼线、眼睫毛共用1个的）
     */
    MAKEUP_NONE("卸妆", "", FaceMakeup.FACE_MAKEUP_TYPE_NONE, R.drawable.makeup_none_normal, R.string.makeup_radio_remove),

    // 腮红
    MAKEUP_BLUSHER_11("MAKEUP_BLUSHER_11", "blusher/blusher_11.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_11, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_12("MAKEUP_BLUSHER_12", "blusher/blusher_12.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_12, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_01("MAKEUP_BLUSHER_01", "blusher/blusher_01.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_01, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_02("MAKEUP_BLUSHER_02", "blusher/blusher_02.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_02, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_03("MAKEUP_BLUSHER_03", "blusher/blusher_03.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_03, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_04("MAKEUP_BLUSHER_04", "blusher/blusher_04.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_04, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_05("MAKEUP_BLUSHER_05", "blusher/blusher_05.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_05, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_06("MAKEUP_BLUSHER_06", "blusher/blusher_06.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_06, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_07("MAKEUP_BLUSHER_07", "blusher/blusher_07.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_07, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_08("MAKEUP_BLUSHER_08", "blusher/blusher_08.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_08, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_09("MAKEUP_BLUSHER_09", "blusher/blusher_09.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_09, R.string.makeup_radio_blusher),
    MAKEUP_BLUSHER_10("MAKEUP_BLUSHER_10", "blusher/blusher_10.png", FaceMakeup.FACE_MAKEUP_TYPE_BLUSHER, R.drawable.blusher_10, R.string.makeup_radio_blusher),

    // 眉毛
    MAKEUP_EYEBORW_07("MAKEUP_EYEBORW_07", "eyebrow/eyebrow_07.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_07, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_08("MAKEUP_EYEBORW_08", "eyebrow/eyebrow_08.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_08, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_09("MAKEUP_EYEBORW_09", "eyebrow/eyebrow_09.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_09, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_01("MAKEUP_EYEBORW_01", "eyebrow/eyebrow_01.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_01, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_02("MAKEUP_EYEBORW_02", "eyebrow/eyebrow_02.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_02, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_03("MAKEUP_EYEBORW_03", "eyebrow/eyebrow_03.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_03, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_04("MAKEUP_EYEBORW_04", "eyebrow/eyebrow_04.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_04, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_05("MAKEUP_EYEBORW_05", "eyebrow/eyebrow_05.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_05, R.string.makeup_radio_eyebrow),
    MAKEUP_EYEBORW_06("MAKEUP_EYEBORW_06", "eyebrow/eyebrow_06.png", FaceMakeup.FACE_MAKEUP_TYPE_EYEBROW, R.drawable.eyebrow_06, R.string.makeup_radio_eyebrow),

    // 睫毛
    MAKEUP_EYELASH_07("MAKEUP_EYELASH_07", "eyelash/eyelash_07.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_07, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_08("MAKEUP_EYELASH_08", "eyelash/eyelash_08.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_08, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_01("MAKEUP_EYELASH_01", "eyelash/eyelash_01.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_01, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_02("MAKEUP_EYELASH_02", "eyelash/eyelash_02.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_02, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_03("MAKEUP_EYELASH_03", "eyelash/eyelash_03.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_03, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_04("MAKEUP_EYELASH_04", "eyelash/eyelash_04.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_04, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_05("MAKEUP_EYELASH_05", "eyelash/eyelash_05.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_05, R.string.makeup_radio_eyelash),
    MAKEUP_EYELASH_06("MAKEUP_EYELASH_06", "eyelash/eyelash_06.png", FaceMakeup.FACE_MAKEUP_TYPE_EYELASH, R.drawable.eyelash_06, R.string.makeup_radio_eyelash),

    // 眼线
    MAKEUP_EYELINER_07("MAKEUP_EYELINER_07", "eyeliner/eyeliner_07.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_07, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_08("MAKEUP_EYELINER_08", "eyeliner/eyeliner_08.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_08, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_01("MAKEUP_EYELINER_01", "eyeliner/eyeliner_01.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_01, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_02("MAKEUP_EYELINER_02", "eyeliner/eyeliner_02.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_02, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_03("MAKEUP_EYELINER_03", "eyeliner/eyeliner_03.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_03, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_04("MAKEUP_EYELINER_04", "eyeliner/eyeliner_04.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_04, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_05("MAKEUP_EYELINER_05", "eyeliner/eyeliner_05.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_05, R.string.makeup_radio_eye_liner),
    MAKEUP_EYELINER_06("MAKEUP_EYELINER_06", "eyeliner/eyeliner_06.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_LINER, R.drawable.eye_liner_06, R.string.makeup_radio_eye_liner),

    // 美瞳
    MAKEUP_EYEPUPIL_10("MAKEUP_EYEPUPIL_10", "eyepupil/eyepupil_10.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_10, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_11("MAKEUP_EYEPUPIL_11", "eyepupil/eyepupil_11.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_11, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_12("MAKEUP_EYEPUPIL_12", "eyepupil/eyepupil_12.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_12, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_04("MAKEUP_EYEPUPIL_04", "eyepupil/eyepupil_04.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_04, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_05("MAKEUP_EYEPUPIL_05", "eyepupil/eyepupil_05.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_05, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_06("MAKEUP_EYEPUPIL_06", "eyepupil/eyepupil_06.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_06, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_07("MAKEUP_EYEPUPIL_07", "eyepupil/eyepupil_07.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_07, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_08("MAKEUP_EYEPUPIL_08", "eyepupil/eyepupil_08.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_08, R.string.makeup_radio_contact_lens),
    MAKEUP_EYEPUPIL_09("MAKEUP_EYEPUPIL_09", "eyepupil/eyepupil_09.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_PUPIL, R.drawable.contact_lens_09, R.string.makeup_radio_contact_lens),

    // 眼影
    MAKEUP_EYE_SHADOW_07("MAKEUP_EYE_SHADOW_07", "eyeshadow/eyeshadow_07.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_07, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_08("MAKEUP_EYE_SHADOW_08", "eyeshadow/eyeshadow_08.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_08, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_09("MAKEUP_EYE_SHADOW_09", "eyeshadow/eyeshadow_09.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_09, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_01("MAKEUP_EYE_SHADOW_01", "eyeshadow/eyeshadow_01.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_01, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_02("MAKEUP_EYE_SHADOW_02", "eyeshadow/eyeshadow_02.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_02, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_03("MAKEUP_EYE_SHADOW_03", "eyeshadow/eyeshadow_03.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_03, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_04("MAKEUP_EYE_SHADOW_04", "eyeshadow/eyeshadow_04.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_04, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_05("MAKEUP_EYE_SHADOW_05", "eyeshadow/eyeshadow_05.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_05, R.string.makeup_radio_eye_shadow),
    MAKEUP_EYE_SHADOW_06("MAKEUP_EYE_SHADOW_06", "eyeshadow/eyeshadow_06.png", FaceMakeup.FACE_MAKEUP_TYPE_EYE_SHADOW, R.drawable.eye_shadow_06, R.string.makeup_radio_eye_shadow),

    // 口红
    MAKEUP_LIPSTICK_07("MAKEUP_LIPSTICK_07", "lipstick/lip_07.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_07, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_08("MAKEUP_LIPSTICK_08", "lipstick/lip_08.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_08, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_09("MAKEUP_LIPSTICK_09", "lipstick/lip_09.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_09, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_01("MAKEUP_LIPSTICK_01", "lipstick/lip_01.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_01, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_02("MAKEUP_LIPSTICK_02", "lipstick/lip_02.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_02, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_03("MAKEUP_LIPSTICK_03", "lipstick/lip_03.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_03, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_04("MAKEUP_LIPSTICK_04", "lipstick/lip_04.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_04, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_05("MAKEUP_LIPSTICK_05", "lipstick/lip_05.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_05, R.string.makeup_radio_lipstick),
    MAKEUP_LIPSTICK_06("MAKEUP_LIPSTICK_06", "lipstick/lip_06.json", FaceMakeup.FACE_MAKEUP_TYPE_LIPSTICK, R.drawable.lipstick_06, R.string.makeup_radio_lipstick);

    public static final float DEFAULT_BATCH_MAKEUP_LEVEL = 0.7f;

    private String name;
    private String path;
    private int type;
    private int iconId;
    private int strId;

    FaceMakeupEnum(String name, String path, int type, int iconId, int strId) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.iconId = iconId;
        this.strId = strId;
    }

    /**
     * 根据类型查询美妆
     */
    public static List<MakeupItem> getFaceMakeupByType(int type) {
        FaceMakeupEnum[] values = values();
        List<MakeupItem> makeups = new ArrayList<>(16);
        MakeupItem none = MAKEUP_NONE.faceMakeup();
        none.setType(type);
        makeups.add(none);
        for (FaceMakeupEnum value : values) {
            if (value.type == type) {
                makeups.add(value.faceMakeup());
            }
        }
        return makeups;
    }

    /**
     * 预置的美妆
     *
     * @return
     */
    public static List<FaceMakeup> getDefaultMakeups() {
        List<FaceMakeup> faceMakeups = new ArrayList<>(4);
        FaceMakeup none = new FaceMakeup(null, R.string.makeup_radio_remove, R.drawable.makeup_none_normal);
        faceMakeups.add(none);
        List<MakeupItem> peachMakeups = new ArrayList<>(8);
        peachMakeups.add(MAKEUP_BLUSHER_11.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        peachMakeups.add(MAKEUP_EYE_SHADOW_07.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        peachMakeups.add(MAKEUP_EYEBORW_07.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        peachMakeups.add(MAKEUP_LIPSTICK_07.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        peachMakeups.add(MAKEUP_EYELINER_07.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        peachMakeups.add(MAKEUP_EYELASH_07.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        peachMakeups.add(MAKEUP_EYEPUPIL_10.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        FaceMakeup peach = new FaceMakeup(peachMakeups, R.string.makeup_peach, R.drawable.icon_peachblossom_make_up);
        faceMakeups.add(peach);
        List<MakeupItem> freckles = new ArrayList<>(8);
        freckles.add(MAKEUP_BLUSHER_12.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        freckles.add(MAKEUP_EYE_SHADOW_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        freckles.add(MAKEUP_EYEBORW_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        freckles.add(MAKEUP_EYEPUPIL_11.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        freckles.add(MAKEUP_LIPSTICK_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        freckles.add(MAKEUP_EYELASH_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        freckles.add(MAKEUP_EYELINER_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        FaceMakeup faceMakeup = new FaceMakeup(freckles, R.string.makeup_freckles, R.drawable.icon_freckles_make_up);
        faceMakeups.add(faceMakeup);
        List<MakeupItem> punkMakeups = new ArrayList<>(8);
        punkMakeups.add(MAKEUP_EYE_SHADOW_09.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        punkMakeups.add(MAKEUP_EYEBORW_09.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        punkMakeups.add(MAKEUP_EYEPUPIL_12.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        punkMakeups.add(MAKEUP_LIPSTICK_09.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        punkMakeups.add(MAKEUP_EYELASH_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        punkMakeups.add(MAKEUP_EYELINER_08.faceMakeup(DEFAULT_BATCH_MAKEUP_LEVEL));
        FaceMakeup punk = new FaceMakeup(punkMakeups, R.string.makeup_punk, R.drawable.icon_punk_make_up);
        faceMakeups.add(punk);
        return faceMakeups;
    }

    public MakeupItem faceMakeup() {
        return new MakeupItem(name, path, type, strId, iconId);
    }

    public MakeupItem faceMakeup(float level) {
        return new MakeupItem(name, path, type, strId, iconId, level);
    }
}