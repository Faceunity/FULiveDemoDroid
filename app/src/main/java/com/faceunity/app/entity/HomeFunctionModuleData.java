package com.faceunity.app.entity;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.utils.FuDeviceUtils;

import java.util.ArrayList;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class HomeFunctionModuleData {
    public FunctionType type;
    public int iconRes;
    public int titleRes;
    public int errorRes;
    public String authCode;
    public boolean enable;

    HomeFunctionModuleData(int iconRes, int titleRes, String authCode) {
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.type = FunctionType.Model;
        this.authCode = authCode;
        this.errorRes = R.string.sorry_no_permission;
        this.enable = true;
    }

    HomeFunctionModuleData(int iconRes, int titleRes, String authCode, boolean enable, int errorRes) {
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.type = FunctionType.Model;
        this.authCode = authCode;
        this.errorRes = errorRes;
        this.enable = enable;
    }

    HomeFunctionModuleData(FunctionType type, int res) {
        this.type = type;
        if (type == FunctionType.Banner) {
            this.iconRes = res;
        } else if (type == FunctionType.Title) {
            this.titleRes = res;
        } else if (type == FunctionType.ModelLottie) {
            this.titleRes = res;
        }
    }


    public static ArrayList<HomeFunctionModuleData> buildData() {
        ArrayList<HomeFunctionModuleData> data = new ArrayList<>();
        data.add(new HomeFunctionModuleData(FunctionType.Banner, R.mipmap.banner_home_top));
        data.add(new HomeFunctionModuleData(FunctionType.Title, R.string.main_classification_face));

        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_beauty, R.string.home_function_name_beauty,"1-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_makeup, R.string.home_function_name_makeup, "524288-0", DemoConfig.DEVICE_LEVEL >= FuDeviceUtils.DEVICE_LEVEL_ONE, R.string.device_level_function_tip));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_style, R.string.home_function_name_style, "0-1048576", DemoConfig.DEVICE_LEVEL >= FuDeviceUtils.DEVICE_LEVEL_ONE, R.string.device_level_function_tip));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_sticker, R.string.home_function_name_sticker,"110-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_animoji, R.string.home_function_name_animoji,"16-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_hair, R.string.home_function_name_hair,"1048576-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_light_beauty, R.string.home_function_name_light_makeup,"0-8", DemoConfig.DEVICE_LEVEL >= FuDeviceUtils.DEVICE_LEVEL_ONE, R.string.device_level_function_tip));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_ar_mask, R.string.home_function_name_ar,"96-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_big_head, R.string.home_function_name_big_head,"0-32768"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_poster_face, R.string.home_function_name_poster_face,"8388608-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_expression, R.string.home_function_name_expression,"2058-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_music_fiter, R.string.home_function_name_music_filter,"131072-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_face_warp, R.string.home_function_name_face_warp,"65536-0"));
        data.add(new HomeFunctionModuleData(FunctionType.Title, R.string.main_classification_human));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_beauty_body, R.string.home_function_name_beauty_body,"0-32"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_human_avatar, R.string.home_function_name_human_avatar,"0-448"));
//        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_action_recognition, R.string.home_function_name_action_recognition));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_portrait_segment, R.string.home_function_name_portrait_segment,"256-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_gesture, R.string.home_function_name_gesture,"512-0"));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_green_curtain, R.string.home_function_name_green_curtain,"0-512"));
        data.add(new HomeFunctionModuleData(FunctionType.Title, R.string.main_content_service));
        data.add(new HomeFunctionModuleData(FunctionType.ModelLottie, R.string.home_function_name_fine_sticker));
        return data;
    }


}