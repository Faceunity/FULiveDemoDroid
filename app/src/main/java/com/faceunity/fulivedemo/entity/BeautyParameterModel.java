package com.faceunity.fulivedemo.entity;

import com.faceunity.fulivedemo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录,目前仅以保存数据，可改造为以SharedPreferences保存数据
 * Created by tujh on 2018/3/7.
 */

public abstract class BeautyParameterModel {
    public static final String TAG = BeautyParameterModel.class.getSimpleName();


    public static boolean isHeightPerformance = false;

    public static final String sStrFilterLevel = "FilterLevel_";
    public static Map<String, Float> sFilterLevel = new HashMap<>();
    public static Filter sFilterName = FilterEnum.ziran.filter();

    public static final String sNameSkinDetect = "精准磨皮";//精准磨皮
    public static final String sStrSkinDetect = "SkinDetect_";//精准磨皮
    public static float sSkinDetect = 1.0f;//精准磨皮

    public static final String sNameHeavyBlur = "美肤类型";//美肤类型
    public static final String sStrHeavyBlur = "HeavyBlur_";//美肤类型
    public static float sHeavyBlur = 0.0f;//美肤类型

    public static final String sNameBlurLevel = "磨皮";//磨皮
    public static final String sStrBlurLevel = "BlurLevel_";//磨皮
    public static boolean isOpenBlurLevel = true;//磨皮
    public static float sBlurLevel = 0.7f;//磨皮

    public static final String sNameColorLevel = "美白";//美白
    public static final String sStrColorLevel = "ColorLevel_";//美白
    public static boolean isOpenColorLevel = true;//美白
    public static float sColorLevel = 0.5f;//美白

    public static final String sNameRedLevel = "红润";//红润
    public static final String sStrRedLevel = "RedLevel_";//红润
    public static boolean isOpenRedLevel = true;//红润
    public static float sRedLevel = 0.5f;//红润

    public static final String sNameEyeBright = "亮眼";//亮眼
    public static final String sStrEyeBright = "EyeBright_";//亮眼
    public static boolean isOpenEyeBright = false;//亮眼
    public static float sEyeBright = 0.7f;//亮眼

    public static final String sNameToothWhiten = "美牙";//美牙
    public static final String sStrToothWhiten = "ToothWhiten_";//美牙
    public static boolean isOpenToothWhiten = false;//美牙
    public static float sToothWhiten = 0.7f;//美牙

    public static final String sNameFaceShape = "脸型";//脸型
    public static final String sStrFaceShape = "FaceShape_";//脸型
    public static float sFaceShape = 4.0f;//脸型

    public static final String sNameEyeEnlarge = "大眼";//大眼
    public static final String sStrEyeEnlarge = "EyeEnlarge_";//大眼
    public static boolean isOpenEyeEnlarge = true;//大眼
    public static float sEyeEnlarge = 0.4f;//大眼
    public static boolean isOpenEyeEnlarge_old = true;//大眼
    public static float sEyeEnlarge_old = 0.4f;//大眼

    public static final String sNameCheekThinning = "瘦脸";//瘦脸
    public static final String sStrCheekThinning = "CheekThinning_";//瘦脸
    public static boolean isOpenCheekThinning = true;//瘦脸
    public static float sCheekThinning = 0.4f;//瘦脸
    public static boolean isOpenCheekThinning_old = true;//瘦脸
    public static float sCheekThinning_old = 0.4f;//瘦脸

    public static final String sNameIntensityChin = "下巴";//下巴
    public static final String sStrIntensityChin = "IntensityChin_";//下巴
    public static boolean isOpenIntensityChin = true;//下巴
    public static float sIntensityChin = 0.3f;//下巴

    public static final String sNameIntensityForehead = "额头";//额头
    public static final String sStrIntensityForehead = "IntensityForehead_";//额头
    public static boolean isOpenIntensityForehead = true;//额头
    public static float sIntensityForehead = 0.3f;//额头

    public static final String sNameIntensityNose = "瘦鼻";//瘦鼻
    public static final String sStrIntensityNose = "IntensityNose_";//瘦鼻
    public static boolean isOpenIntensityNose = true;//瘦鼻
    public static float sIntensityNose = 0.5f;//瘦鼻

    public static final String sNameIntensityMouth = "嘴形";//嘴形
    public static final String sStrIntensityMouth = "IntensityMouth_";//嘴形
    public static boolean isOpenIntensityMouth = true;//嘴形
    public static float sIntensityMouth = 0.4f;//嘴形


    public static boolean isOpen(int checkId) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                return !isHeightPerformance && sSkinDetect == 1;
            case R.id.beauty_box_heavy_blur:
                return isHeightPerformance || sHeavyBlur == 1;
            case R.id.beauty_box_blur_level:
                return isOpenBlurLevel;
            case R.id.beauty_box_color_level:
                return isOpenColorLevel;
            case R.id.beauty_box_red_level:
                return isOpenRedLevel;
            case R.id.beauty_box_eye_bright:
                return !isHeightPerformance && isOpenEyeBright;
            case R.id.beauty_box_tooth_whiten:
                return !isHeightPerformance && isOpenToothWhiten;
            case R.id.beauty_box_face_shape:
                return (!isHeightPerformance || sFaceShape != 4) && sFaceShape != 3;
            case R.id.beauty_box_eye_enlarge:
                if (sFaceShape == 4)
                    return isOpenEyeEnlarge;
                else
                    return isOpenEyeEnlarge_old;
            case R.id.beauty_box_cheek_thinning:
                if (sFaceShape == 4)
                    return isOpenCheekThinning;
                else
                    return isOpenCheekThinning_old;
            case R.id.beauty_box_intensity_chin:
                return !isHeightPerformance && isOpenIntensityChin;
            case R.id.beauty_box_intensity_forehead:
                return !isHeightPerformance && isOpenIntensityForehead;
            case R.id.beauty_box_intensity_nose:
                return !isHeightPerformance && isOpenIntensityNose;
            case R.id.beauty_box_intensity_mouth:
                return !isHeightPerformance && isOpenIntensityMouth;
            default:
                return true;
        }
    }

    public static float getValue(int checkId) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                return isHeightPerformance ? 0 : sSkinDetect;
            case R.id.beauty_box_heavy_blur:
                return isHeightPerformance ? 1 : sHeavyBlur;
            case R.id.beauty_box_blur_level:
                return isOpenBlurLevel ? sBlurLevel : 0;
            case R.id.beauty_box_color_level:
                return isOpenColorLevel ? sColorLevel : 0;
            case R.id.beauty_box_red_level:
                return isOpenRedLevel ? sRedLevel : 0;
            case R.id.beauty_box_eye_bright:
                return !isHeightPerformance && isOpenEyeBright ? sEyeBright : 0;
            case R.id.beauty_box_tooth_whiten:
                return !isHeightPerformance && isOpenToothWhiten ? sToothWhiten : 0;
            case R.id.beauty_box_face_shape:
                return isHeightPerformance && sFaceShape == 4 ? 3 : sFaceShape;
            case R.id.beauty_box_eye_enlarge:
                if (!isHeightPerformance && sFaceShape == 4)
                    return isOpenEyeEnlarge ? sEyeEnlarge : 0;
                else
                    return isOpenEyeEnlarge_old ? sEyeEnlarge_old : 0;
            case R.id.beauty_box_cheek_thinning:
                if (!isHeightPerformance && sFaceShape == 4)
                    return isOpenCheekThinning ? sCheekThinning : 0;
                else
                    return isOpenCheekThinning_old ? sCheekThinning_old : 0;
            case R.id.beauty_box_intensity_chin:
                return !isHeightPerformance && isOpenIntensityChin ? sIntensityChin : 0.5f;
            case R.id.beauty_box_intensity_forehead:
                return !isHeightPerformance && isOpenIntensityForehead ? sIntensityForehead : 0.5f;
            case R.id.beauty_box_intensity_nose:
                return !isHeightPerformance && isOpenIntensityNose ? sIntensityNose : 0;
            case R.id.beauty_box_intensity_mouth:
                return !isHeightPerformance && isOpenIntensityMouth ? sIntensityMouth : 0.5f;
            default:
                return 0;
        }
    }

    public static void setValue(int checkId, float value) {
        switch (checkId) {
            case R.id.beauty_box_skin_detect:
                sSkinDetect = value;
                break;
            case R.id.beauty_box_heavy_blur:
                sHeavyBlur = value;
                break;
            case R.id.beauty_box_blur_level:
                sBlurLevel = value;
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
            case R.id.beauty_box_face_shape:
                sFaceShape = value;
                break;
            case R.id.beauty_box_eye_enlarge:
                if (sFaceShape == 4)
                    sEyeEnlarge = value;
                else
                    sEyeEnlarge_old = value;
                break;
            case R.id.beauty_box_cheek_thinning:
                if (sFaceShape == 4)
                    sCheekThinning = value;
                else
                    sCheekThinning_old = value;
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
        }
    }

}
