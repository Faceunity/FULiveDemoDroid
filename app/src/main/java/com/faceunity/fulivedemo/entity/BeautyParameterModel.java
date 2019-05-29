package com.faceunity.fulivedemo.entity;


import com.faceunity.entity.Filter;
import com.faceunity.fulivedemo.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录,目前仅以保存数据，可改造为以SharedPreferences保存数据
 * Created by tujh on 2018/3/7.
 */
public abstract class BeautyParameterModel {
    public static final String TAG = BeautyParameterModel.class.getSimpleName();

    public static final String STR_FILTER_LEVEL = "FilterLevel_";
    public static Map<String, Float> sFilterLevel = new HashMap<>(16);
    public static Filter sFilter = FilterEnum.fennen.filter();
    /**
     * key: name, value: level
     */
    public static Map<String, Float> sMakeupLevel = new HashMap<>(64);
    /**
     * key: name，value: level
     */
    public static Map<String, Float> sBatchMakeupLevel = new HashMap<>(8);

    public static float[] sHairLevel = new float[14];

    static {
        Arrays.fill(sHairLevel, 0.6f);
    }

    public static float sSkinDetect = 1.0f;//精准磨皮
    public static float sHeavyBlur = 0.0f;//美肤类型
    public static float sHeavyBlurLevel = 0.7f;//磨皮
    public static float sBlurLevel = 0.7f;//磨皮
    public static float sColorLevel = 0.3f;//美白
    public static float sRedLevel = 0.3f;//红润
    public static float sEyeBright = 0.0f;//亮眼
    public static float sToothWhiten = 0.0f;//美牙

    // 脸型默认的参数
    private static final Map<Integer, Float> FACE_SHAPE_DEFAULT_PARAMS = new HashMap<>(16);
    public static float sCheekThinning = 0f;//瘦脸
    public static float sCheekV = 0.5f;//V脸
    public static float sCheekNarrow = 0f;//窄脸
    public static float sCheekSmall = 0f;//小脸
    public static float sEyeEnlarging = 0.4f;//大眼
    public static float sIntensityChin = 0.3f;//下巴
    public static float sIntensityForehead = 0.3f;//额头
    public static float sIntensityNose = 0.5f;//瘦鼻
    public static float sIntensityMouth = 0.4f;//嘴形

    static {
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_thinning, sCheekThinning);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_narrow, sCheekNarrow);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_small, sCheekSmall);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_cheek_v, sCheekV);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_eye_enlarge, sEyeEnlarging);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_chin, sIntensityChin);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_forehead, sIntensityForehead);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_nose, sIntensityNose);
        FACE_SHAPE_DEFAULT_PARAMS.put(R.id.beauty_box_intensity_mouth, sIntensityMouth);
    }

    /**
     * 美颜效果是否打开
     *
     * @param checkId
     * @return
     */
    public static boolean isOpen(int checkId) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                return sSkinDetect == 1;
            case R.id.beauty_box_heavy_blur:
                return sHeavyBlur == 1 ? sHeavyBlurLevel > 0 : sBlurLevel > 0;
            case R.id.beauty_box_blur_level:
                return sHeavyBlurLevel > 0;
            case R.id.beauty_box_color_level:
                return sColorLevel > 0;
            case R.id.beauty_box_red_level:
                return sRedLevel > 0;
            case R.id.beauty_box_eye_bright:
                return sEyeBright > 0;
            case R.id.beauty_box_tooth_whiten:
                return sToothWhiten != 0;
            case R.id.beauty_box_eye_enlarge:
                return sEyeEnlarging > 0;
            case R.id.beauty_box_cheek_thinning:
                return sCheekThinning > 0;
            case R.id.beauty_box_cheek_narrow:
                return sCheekNarrow > 0;
            case R.id.beauty_box_cheek_v:
                return sCheekV > 0;
            case R.id.beauty_box_cheek_small:
                return sCheekSmall > 0;
            case R.id.beauty_box_intensity_chin:
                return sIntensityChin != 0.5;
            case R.id.beauty_box_intensity_forehead:
                return sIntensityForehead != 0.5;
            case R.id.beauty_box_intensity_nose:
                return sIntensityNose > 0;
            case R.id.beauty_box_intensity_mouth:
                return sIntensityMouth != 0.5;
            default:
                return true;
        }
    }

    /**
     * 获取美颜的参数值
     *
     * @param checkId
     * @return
     */
    public static float getValue(int checkId) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                return sSkinDetect;
            case R.id.beauty_box_heavy_blur:
                return sHeavyBlur == 1 ? sHeavyBlurLevel : sBlurLevel;
            case R.id.beauty_box_blur_level:
                return sHeavyBlurLevel;
            case R.id.beauty_box_color_level:
                return sColorLevel;
            case R.id.beauty_box_red_level:
                return sRedLevel;
            case R.id.beauty_box_eye_bright:
                return sEyeBright;
            case R.id.beauty_box_tooth_whiten:
                return sToothWhiten;
            case R.id.beauty_box_eye_enlarge:
                return sEyeEnlarging;
            case R.id.beauty_box_cheek_thinning:
                return sCheekThinning;
            case R.id.beauty_box_cheek_narrow:
                return sCheekNarrow;
            case R.id.beauty_box_cheek_v:
                return sCheekV;
            case R.id.beauty_box_cheek_small:
                return sCheekSmall;
            case R.id.beauty_box_intensity_chin:
                return sIntensityChin;
            case R.id.beauty_box_intensity_forehead:
                return sIntensityForehead;
            case R.id.beauty_box_intensity_nose:
                return sIntensityNose;
            case R.id.beauty_box_intensity_mouth:
                return sIntensityMouth;
            default:
                return 0;
        }
    }

    /**
     * 设置美颜的参数值
     *
     * @param checkId
     * @param value
     */
    public static void setValue(int checkId, float value) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                sSkinDetect = value;
                break;
            case R.id.beauty_box_heavy_blur:
                if (sHeavyBlur == 1) {
                    sHeavyBlurLevel = value;
                } else {
                    sBlurLevel = value;
                }
                break;
            case R.id.beauty_box_blur_level:
                sHeavyBlurLevel = value;
                break;
            case R.id.beauty_box_color_level:
                sColorLevel = value;
                break;
            case R.id.beauty_box_red_level:
                sRedLevel = value;
                break;
            case R.id.beauty_box_eye_bright:
                sEyeBright = value;
                break;
            case R.id.beauty_box_tooth_whiten:
                sToothWhiten = value;
                break;
            case R.id.beauty_box_eye_enlarge:
                sEyeEnlarging = value;
                break;
            case R.id.beauty_box_cheek_thinning:
                sCheekThinning = value;
                break;
            case R.id.beauty_box_cheek_v:
                sCheekV = value;
                break;
            case R.id.beauty_box_cheek_narrow:
                sCheekNarrow = value;
                break;
            case R.id.beauty_box_cheek_small:
                sCheekSmall = value;
                break;
            case R.id.beauty_box_intensity_chin:
                sIntensityChin = value;
                break;
            case R.id.beauty_box_intensity_forehead:
                sIntensityForehead = value;
                break;
            case R.id.beauty_box_intensity_nose:
                sIntensityNose = value;
                break;
            case R.id.beauty_box_intensity_mouth:
                sIntensityMouth = value;
                break;
            default:
        }
    }

    /**
     * 默认的美型参数是否被修改过
     *
     * @return
     */
    public static boolean checkIfFaceShapeChanged() {
        if (Float.compare(sCheekNarrow, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow)) != 0) {
            return true;
        }
        if (Float.compare(sCheekSmall, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small)) != 0) {
            return true;
        }
        if (Float.compare(sCheekV, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v)) != 0) {
            return true;
        }
        if (Float.compare(sCheekThinning, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning)) != 0) {
            return true;
        }
        if (Float.compare(sEyeEnlarging, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityNose, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityChin, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityMouth, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth)) != 0) {
            return true;
        }
        if (Float.compare(sIntensityForehead, FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead)) != 0) {
            return true;
        }
        return false;
    }

    /**
     * 恢复美型的默认值
     */
    public static void recoverToDefValue() {
        sCheekNarrow = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_narrow);
        sCheekSmall = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_small);
        sCheekV = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_v);
        sCheekThinning = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_cheek_thinning);
        sEyeEnlarging = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_eye_enlarge);
        sIntensityNose = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_nose);
        sIntensityMouth = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_mouth);
        sIntensityForehead = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_forehead);
        sIntensityChin = FACE_SHAPE_DEFAULT_PARAMS.get(R.id.beauty_box_intensity_chin);
    }

}
