package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.Makeup;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;

/**
 * Created by tujh on 2018/1/30.
 */

public enum MakeupEnum {
    /**
     *
     */
    MakeupNone("", 0, "", Makeup.MAKEUP_TYPE_NONE, 0),

    Makeup_blusher_01_923_sh("blusher_01_923_sh", R.drawable.blusher_01_923_sh, "blusher/MU_Blush_01.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_02_927_sh("blusher_02_927_sh", R.drawable.blusher_02_927_sh, "blusher/MU_Blush_02.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_03_958_sh("blusher_03_958_sh", R.drawable.blusher_03_958_sh, "blusher/MU_Blush_03.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_04_960_sh("blusher_04_960_sh", R.drawable.blusher_04_960_sh, "blusher/MU_Blush_04.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_05_980_sh("blusher_05_980_sh", R.drawable.blusher_05_980_sh, "blusher/MU_Blush_05.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_06_1967_sh("blusher_06_1967_sh", R.drawable.blusher_06_1967_sh, "blusher/MU_Blush_06.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_07_2005_sh("blusher_07_2005_sh", R.drawable.blusher_07_2005_sh, "blusher/MU_Blush_07.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_08_2047_sh("blusher_08_2047_sh", R.drawable.blusher_08_2047_sh, "blusher/MU_Blush_08.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_09_2048_sh("blusher_09_2048_sh", R.drawable.blusher_09_2048_sh, "blusher/MU_Blush_09.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),
    Makeup_blusher_10_2054_sh("blusher_10_2054_sh", R.drawable.blusher_10_2054_sh, "blusher/MU_Blush_10.bundle", Makeup.MAKEUP_TYPE_BLUSHER, 0),

    Makeup_eyebrow_01_09("eyebrow_01_09", R.drawable.eyebrow_01_09, "eyebrow/MU_Eyebrow_01.bundle", Makeup.MAKEUP_TYPE_EYEBROW, 0),
    Makeup_eyebrow_02_927("eyebrow_02_927", R.drawable.eyebrow_02_927, "eyebrow/MU_Eyebrow_02.bundle", Makeup.MAKEUP_TYPE_EYEBROW, 0),
    Makeup_eyebrow_03_928("eyebrow_03_928", R.drawable.eyebrow_03_928, "eyebrow/MU_Eyebrow_03.bundle", Makeup.MAKEUP_TYPE_EYEBROW, 0),
    Makeup_eyebrow_04_940("eyebrow_04_940", R.drawable.eyebrow_04_940, "eyebrow/MU_Eyebrow_04.bundle", Makeup.MAKEUP_TYPE_EYEBROW, 0),
    Makeup_eyebrow_05_960("eyebrow_05_960", R.drawable.eyebrow_05_960, "eyebrow/MU_Eyebrow_05.bundle", Makeup.MAKEUP_TYPE_EYEBROW, 0),
    Makeup_eyebrow_06_966("eyebrow_06_966", R.drawable.eyebrow_06_966, "eyebrow/MU_Eyebrow_06.bundle", Makeup.MAKEUP_TYPE_EYEBROW, 0),

    Makeup_eyelash_01_794("eyelash_01_794", R.drawable.eyelash_01_794, "eyelash/MU_Eyelash_01.bundle", Makeup.MAKEUP_TYPE_EYELASH, 0),
    Makeup_eyelash_02_927("eyelash_02_927", R.drawable.eyelash_02_927, "eyelash/MU_Eyelash_02.bundle", Makeup.MAKEUP_TYPE_EYELASH, 0),
    Makeup_eyelash_03_928("eyelash_03_928", R.drawable.eyelash_03_928, "eyelash/MU_Eyelash_03.bundle", Makeup.MAKEUP_TYPE_EYELASH, 0),
    Makeup_eyelash_04_940("eyelash_04_940", R.drawable.eyelash_04_940, "eyelash/MU_Eyelash_04.bundle", Makeup.MAKEUP_TYPE_EYELASH, 0),
    Makeup_eyelash_05_951("eyelash_05_951", R.drawable.eyelash_05_951, "eyelash/MU_Eyelash_05.bundle", Makeup.MAKEUP_TYPE_EYELASH, 0),
    Makeup_eyelash_06_958("eyelash_06_958", R.drawable.eyelash_06_958, "eyelash/MU_Eyelash_06.bundle", Makeup.MAKEUP_TYPE_EYELASH, 0),

    Makeup_eye_liner_01("eye_liner_01", R.drawable.eye_liner_01, "eye_liner/MU_EyeLiner_01.bundle", Makeup.MAKEUP_TYPE_EYE_LINER, 0),
    Makeup_eye_liner_02("eye_liner_02", R.drawable.eye_liner_02, "eye_liner/MU_EyeLiner_02.bundle", Makeup.MAKEUP_TYPE_EYE_LINER, 0),
    Makeup_eye_liner_03("eye_liner_03", R.drawable.eye_liner_03, "eye_liner/MU_EyeLiner_03.bundle", Makeup.MAKEUP_TYPE_EYE_LINER, 0),
    Makeup_eye_liner_04("eye_liner_04", R.drawable.eye_liner_04, "eye_liner/MU_EyeLiner_04.bundle", Makeup.MAKEUP_TYPE_EYE_LINER, 0),
    Makeup_eye_liner_05("eye_liner_05", R.drawable.eye_liner_05, "eye_liner/MU_EyeLiner_05.bundle", Makeup.MAKEUP_TYPE_EYE_LINER, 0),
    Makeup_eye_liner_06("eye_liner_06", R.drawable.eye_liner_06, "eye_liner/MU_EyeLiner_06.bundle", Makeup.MAKEUP_TYPE_EYE_LINER, 0),

    Makeup_lipstick_01("lipstick_01", R.drawable.lipstick_01, "lipstick/MU_Lipstick_01.bundle", Makeup.MAKEUP_TYPE_LIPSTICK, 0),
    Makeup_lipstick_02("lipstick_02", R.drawable.lipstick_02, "lipstick/MU_Lipstick_02.bundle", Makeup.MAKEUP_TYPE_LIPSTICK, 0),
    Makeup_lipstick_03("lipstick_03", R.drawable.lipstick_03, "lipstick/MU_Lipstick_03.bundle", Makeup.MAKEUP_TYPE_LIPSTICK, 0),
    Makeup_lipstick_04("lipstick_04", R.drawable.lipstick_04, "lipstick/MU_Lipstick_04.bundle", Makeup.MAKEUP_TYPE_LIPSTICK, 0),
    Makeup_lipstick_05("lipstick_05", R.drawable.lipstick_05, "lipstick/MU_Lipstick_05.bundle", Makeup.MAKEUP_TYPE_LIPSTICK, 0),
    Makeup_lipstick_06("lipstick_06", R.drawable.lipstick_06, "lipstick/MU_Lipstick_06.bundle", Makeup.MAKEUP_TYPE_LIPSTICK, 0),

    Makeup_contact_lens_01("contact_lens_01", R.drawable.contact_lens_01, "contact_lens/MU_ContactLenses_01.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_02("contact_lens_02", R.drawable.contact_lens_02, "contact_lens/MU_ContactLenses_02.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_03("contact_lens_03", R.drawable.contact_lens_03, "contact_lens/MU_ContactLenses_03.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_04("contact_lens_04", R.drawable.contact_lens_04, "contact_lens/MU_ContactLenses_04.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_05("contact_lens_05", R.drawable.contact_lens_05, "contact_lens/MU_ContactLenses_05.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_06("contact_lens_06", R.drawable.contact_lens_06, "contact_lens/MU_ContactLenses_06.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_07("contact_lens_07", R.drawable.contact_lens_07, "contact_lens/MU_ContactLenses_07.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_08("contact_lens_08", R.drawable.contact_lens_08, "contact_lens/MU_ContactLenses_08.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),
    Makeup_contact_lens_09("contact_lens_09", R.drawable.contact_lens_09, "contact_lens/MU_ContactLenses_09.bundle", Makeup.MAKEUP_TYPE_CONTACT_LENS, 0),

    Makeup_eye_shadow_01("eye_shadow_01", R.drawable.eye_shadow_01, "eye_shadow/MU_EyeShadow_01.bundle", Makeup.MAKEUP_TYPE_EYE_SHADOW, 0),
    Makeup_eye_shadow_02("eye_shadow_02", R.drawable.eye_shadow_02, "eye_shadow/MU_EyeShadow_02.bundle", Makeup.MAKEUP_TYPE_EYE_SHADOW, 0),
    Makeup_eye_shadow_03("eye_shadow_03", R.drawable.eye_shadow_03, "eye_shadow/MU_EyeShadow_03.bundle", Makeup.MAKEUP_TYPE_EYE_SHADOW, 0),
    Makeup_eye_shadow_04("eye_shadow_04", R.drawable.eye_shadow_04, "eye_shadow/MU_EyeShadow_04.bundle", Makeup.MAKEUP_TYPE_EYE_SHADOW, 0),
    Makeup_eye_shadow_05("eye_shadow_05", R.drawable.eye_shadow_05, "eye_shadow/MU_EyeShadow_05.bundle", Makeup.MAKEUP_TYPE_EYE_SHADOW, 0),
    Makeup_eye_shadow_06("eye_shadow_06", R.drawable.eye_shadow_06, "eye_shadow/MU_EyeShadow_06.bundle", Makeup.MAKEUP_TYPE_EYE_SHADOW, 0),;

    private String bundleName;
    private int resId;
    private String path;
    private int makeupType;
    private int description;

    MakeupEnum(String name, int resId, String path, int makeupType, int description) {
        this.bundleName = name;
        this.resId = resId;
        this.path = path;
        this.makeupType = makeupType;
        this.description = description;
    }

    public String bundleName() {
        return bundleName;
    }

    public int resId() {
        return resId;
    }

    public String path() {
        return path;
    }

    public int makeupType() {
        return makeupType;
    }

    public int description() {
        return description;
    }

    public Makeup makeup() {
        return new Makeup(bundleName, resId, path, makeupType, description);
    }

    public static ArrayList<Makeup> getMakeupsByMakeupType(int makeupType) {
        ArrayList<Makeup> Makeups = new ArrayList<>();
        for (MakeupEnum e : MakeupEnum.values()) {
            if (e.makeupType == makeupType) {
                Makeups.add(e.makeup());
            }
        }
        return Makeups;
    }
}
