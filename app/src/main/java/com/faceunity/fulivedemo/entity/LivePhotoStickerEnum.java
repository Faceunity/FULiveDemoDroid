package com.faceunity.fulivedemo.entity;

import android.util.SparseArray;

import com.faceunity.fulivedemo.FUApplication;
import com.faceunity.fulivedemo.R;
import com.faceunity.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 表情动图，不同人像的五官类型
 *
 * @author Richie on 2019.04.12
 */
public enum LivePhotoStickerEnum {
    /**
     * 眉毛：全部通用
     * 眼睛：全部不一样
     * 鼻子：写实1种，漫画男女萌通用1种
     * 嘴：写实1种，漫画男女萌通用1种
     * <p>
     * 写实
     */
    REALISTIC_LEFT_EYE(R.drawable.icon_left_eye, R.string.livephoto_left_eye, LivePhotoSticker.ORGAN_TYPE_LEFT_EYE, "live_photo/resource_01_reality/reality_leye.png", LivePhotoSticker.POINTS_KEY_LEFT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_REALISTIC, "live_photo/resource_01_reality/xieshi_point.json"),
    REALISTIC_RIGHT_EYE(R.drawable.icon_right_eye, R.string.livephoto_right_eye, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYE, "live_photo/resource_01_reality/reality_reye.png", LivePhotoSticker.POINTS_KEY_RIGHT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_REALISTIC, "live_photo/resource_01_reality/xieshi_point.json"),
    REALISTIC_MOUTH(R.drawable.icon_mouth, R.string.livephoto_mouth, LivePhotoSticker.ORGAN_TYPE_MOUTH, "live_photo/resource_01_reality/reality_mouth.png", LivePhotoSticker.POINTS_KEY_MOUTH, LivePhotoPortraitType.PORTRAIT_TYPE_REALISTIC, "live_photo/resource_01_reality/xieshi_point.json"),
    REALISTIC_NOSE(R.drawable.icon_nose, R.string.livephoto_nose, LivePhotoSticker.ORGAN_TYPE_NOSE, "live_photo/resource_01_reality/reality_nose.png", LivePhotoSticker.POINTS_KEY_NOSE, LivePhotoPortraitType.PORTRAIT_TYPE_REALISTIC, "live_photo/resource_01_reality/xieshi_point.json"),
    REALISTIC_LEFT_BROW(R.drawable.demo_icon_lbrow, R.string.livephoto_left_eyebrow, LivePhotoSticker.ORGAN_TYPE_LEFT_EYEBROW, "live_photo/resource_01_reality/lbrow.png", LivePhotoSticker.POINTS_KEY_LEFT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_REALISTIC, "live_photo/resource_01_reality/xieshi_point.json"),
    REALISTIC_RIGHT_BROW(R.drawable.demo_icon_rbrow, R.string.livephoto_right_eyebrow, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYEBROW, "live_photo/resource_01_reality/rbrow.png", LivePhotoSticker.POINTS_KEY_RIGHT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_REALISTIC, "live_photo/resource_01_reality/xieshi_point.json"),
    /**
     * 漫画男
     */
    COMIC_BOY_LEFT_EYE(R.drawable.demo_icon_comic_boy_leyes, R.string.livephoto_left_eye, LivePhotoSticker.ORGAN_TYPE_LEFT_EYE, "live_photo/resource_02_comic_boy/comic_boy_leyes.png", LivePhotoSticker.POINTS_KEY_LEFT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_BOY, "live_photo/resource_02_comic_boy/manhuanan_point.json"),
    COMIC_BOY_RIGHT_EYE(R.drawable.demo_icon_comic_boy_reyes, R.string.livephoto_right_eye, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYE, "live_photo/resource_02_comic_boy/comic_boy_reyes.png", LivePhotoSticker.POINTS_KEY_RIGHT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_BOY, "live_photo/resource_02_comic_boy/manhuanan_point.json"),
    COMIC_BOY_MOUTH(R.drawable.demo_icon_comic_mouth, R.string.livephoto_mouth, LivePhotoSticker.ORGAN_TYPE_MOUTH, "live_photo/resource_02_comic_boy/mouth.png", LivePhotoSticker.POINTS_KEY_MOUTH, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_BOY, "live_photo/resource_02_comic_boy/manhuanan_point.json"),
    COMIC_BOY_NOSE(R.drawable.demo_icon_comic_nose, R.string.livephoto_nose, LivePhotoSticker.ORGAN_TYPE_NOSE, "live_photo/resource_02_comic_boy/nose.png", LivePhotoSticker.POINTS_KEY_NOSE, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_BOY, "live_photo/resource_02_comic_boy/manhuanan_point.json"),
    COMIC_BOY_LEFT_BROW(R.drawable.demo_icon_lbrow, R.string.livephoto_left_eyebrow, LivePhotoSticker.ORGAN_TYPE_LEFT_EYEBROW, "live_photo/resource_02_comic_boy/lbrow.png", LivePhotoSticker.POINTS_KEY_LEFT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_BOY, "live_photo/resource_02_comic_boy/manhuanan_point.json"),
    COMIC_BOY_RIGHT_BROW(R.drawable.demo_icon_rbrow, R.string.livephoto_right_eyebrow, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYEBROW, "live_photo/resource_02_comic_boy/rbrow.png", LivePhotoSticker.POINTS_KEY_RIGHT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_BOY, "live_photo/resource_02_comic_boy/manhuanan_point.json"),
    /**
     * 漫画女
     */
    COMIC_GIRL_LEFT_EYE(R.drawable.demo_icon_comic_girl_leyes, R.string.livephoto_left_eye, LivePhotoSticker.ORGAN_TYPE_LEFT_EYE, "live_photo/resource_03_comic_girl/comic_girl_leyes.png", LivePhotoSticker.POINTS_KEY_LEFT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_GIRL, "live_photo/resource_03_comic_girl/manhuanv_point.json"),
    COMIC_GIRL_RIGHT_EYE(R.drawable.demo_icon_comic_girl_reyes, R.string.livephoto_right_eye, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYE, "live_photo/resource_03_comic_girl/comic_girl_reyes.png", LivePhotoSticker.POINTS_KEY_RIGHT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_GIRL, "live_photo/resource_03_comic_girl/manhuanv_point.json"),
    COMIC_GIRL_MOUTH(R.drawable.demo_icon_comic_mouth, R.string.livephoto_mouth, LivePhotoSticker.ORGAN_TYPE_MOUTH, "live_photo/resource_03_comic_girl/mouth.png", LivePhotoSticker.POINTS_KEY_MOUTH, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_GIRL, "live_photo/resource_03_comic_girl/manhuanv_point.json"),
    COMIC_GIRL_NOSE(R.drawable.demo_icon_comic_nose, R.string.livephoto_nose, LivePhotoSticker.ORGAN_TYPE_NOSE, "live_photo/resource_03_comic_girl/nose.png", LivePhotoSticker.POINTS_KEY_NOSE, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_GIRL, "live_photo/resource_03_comic_girl/manhuanv_point.json"),
    COMIC_GIRL_LEFT_BROW(R.drawable.demo_icon_lbrow, R.string.livephoto_left_eyebrow, LivePhotoSticker.ORGAN_TYPE_LEFT_EYEBROW, "live_photo/resource_03_comic_girl/lbrow.png", LivePhotoSticker.POINTS_KEY_LEFT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_GIRL, "live_photo/resource_03_comic_girl/manhuanv_point.json"),
    COMIC_GIRL_RIGHT_BROW(R.drawable.demo_icon_rbrow, R.string.livephoto_right_eyebrow, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYEBROW, "live_photo/resource_03_comic_girl/rbrow.png", LivePhotoSticker.POINTS_KEY_RIGHT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_COMIC_GIRL, "live_photo/resource_03_comic_girl/manhuanv_point.json"),
    /**
     * 萌版
     */
    CUTE_LEFT_EYE(R.drawable.demo_icon_cute_leyes, R.string.livephoto_left_eye, LivePhotoSticker.ORGAN_TYPE_LEFT_EYE, "live_photo/resource_04_cute/cute_leye.png", LivePhotoSticker.POINTS_KEY_LEFT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_CUTE, "live_photo/resource_04_cute/mengban_point.json"),
    CUTE_RIGHT_EYE(R.drawable.demo_icon_cute_reyes, R.string.livephoto_right_eye, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYE, "live_photo/resource_04_cute/cute_reye.png", LivePhotoSticker.POINTS_KEY_RIGHT_EYE, LivePhotoPortraitType.PORTRAIT_TYPE_CUTE, "live_photo/resource_04_cute/mengban_point.json"),
    CUTE_MOUTH(R.drawable.demo_icon_comic_mouth, R.string.livephoto_mouth, LivePhotoSticker.ORGAN_TYPE_MOUTH, "live_photo/resource_04_cute/cute_mouth.png", LivePhotoSticker.POINTS_KEY_MOUTH, LivePhotoPortraitType.PORTRAIT_TYPE_CUTE, "live_photo/resource_04_cute/mengban_point.json"),
    CUTE_NOSE(R.drawable.demo_icon_comic_nose, R.string.livephoto_nose, LivePhotoSticker.ORGAN_TYPE_NOSE, "live_photo/resource_04_cute/cute_nose.png", LivePhotoSticker.POINTS_KEY_NOSE, LivePhotoPortraitType.PORTRAIT_TYPE_CUTE, "live_photo/resource_04_cute/mengban_point.json"),
    CUTE_LEFT_BROW(R.drawable.demo_icon_lbrow, R.string.livephoto_left_eyebrow, LivePhotoSticker.ORGAN_TYPE_LEFT_EYEBROW, "live_photo/resource_04_cute/cute_lbrow.png", LivePhotoSticker.POINTS_KEY_LEFT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_CUTE, "live_photo/resource_04_cute/mengban_point.json"),
    CUTE_RIGHT_BROW(R.drawable.demo_icon_rbrow, R.string.livephoto_right_eyebrow, LivePhotoSticker.ORGAN_TYPE_RIGHT_EYEBROW, "live_photo/resource_04_cute/cute_rbrow.png", LivePhotoSticker.POINTS_KEY_RIGHT_BROW, LivePhotoPortraitType.PORTRAIT_TYPE_CUTE, "live_photo/resource_04_cute/mengban_point.json");

    private static SparseArray<List<LivePhotoSticker>> sLivePhotoStickerMap = new SparseArray<>(8);
    private int iconId;
    private int nameId;
    private int organType;
    private int portraitType;
    private String imagePath;
    private String pointsJsonKey;
    private String pointsJsonPath;

    LivePhotoStickerEnum(int iconId, int nameId, int organType, String imagePath, String pointsJsonKey, int portraitType, String pointsJsonPath) {
        this.iconId = iconId;
        this.nameId = nameId;
        this.organType = organType;
        this.imagePath = imagePath;
        this.portraitType = portraitType;
        this.pointsJsonKey = pointsJsonKey;
        this.pointsJsonPath = pointsJsonPath;
    }

    public static List<LivePhotoSticker> getStickerByPortraitType(int portraitType) {
        List<LivePhotoSticker> livePhotoStickers = sLivePhotoStickerMap.get(portraitType);
        if (livePhotoStickers != null) {
            return livePhotoStickers;
        }
        livePhotoStickers = new ArrayList<>(8);
        LivePhotoStickerEnum[] values = values();
        for (LivePhotoStickerEnum value : values) {
            if (value.portraitType == portraitType) {
                float[] points = getArrayFromJson(value.pointsJsonKey, value.pointsJsonPath);
                livePhotoStickers.add(new LivePhotoSticker(value.iconId, value.imagePath, value.nameId, value.organType, points));
            }
        }
        sLivePhotoStickerMap.put(portraitType, livePhotoStickers);
        return livePhotoStickers;
    }

    private static float[] getArrayFromJson(String key, String configPath) {
        try {
            String jsonContent = FileUtils.readStringFromAssetsFile(FUApplication.getContext(), configPath);
            JSONObject jsonObject = new JSONObject(jsonContent);
            JSONArray jsonArray = jsonObject.optJSONArray(key);
            if (jsonArray != null) {
                int length = jsonArray.length();
                float[] values = new float[length];
                for (int i = 0; i < length; i++) {
                    values[i] = jsonArray.optInt(i);
                }
                return values;
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
