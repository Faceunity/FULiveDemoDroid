package com.faceunity.app.entity;

import com.faceunity.app.R;

import java.util.ArrayList;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class HomeFunctionModuleData {
    public FunctionType type;
    public int iconRes;
    public int titleRes;


    HomeFunctionModuleData(int iconRes, int titleRes) {
        this.iconRes = iconRes;
        this.titleRes = titleRes;
        this.type = FunctionType.Model;
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
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_beauty, R.string.home_function_name_beauty));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_makeup, R.string.home_function_name_makeup));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_sticker, R.string.home_function_name_sticker));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_animoji, R.string.home_function_name_animoji));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_hair, R.string.home_function_name_hair));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_light_beauty, R.string.home_function_name_light_makeup));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_ar_mask, R.string.home_function_name_ar));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_big_head, R.string.home_function_name_big_head));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_poster_face, R.string.home_function_name_poster_face));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_expression, R.string.home_function_name_expression));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_music_fiter, R.string.home_function_name_music_filter));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_face_warp, R.string.home_function_name_face_warp));
        data.add(new HomeFunctionModuleData(FunctionType.Title, R.string.main_classification_human));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_beauty_body, R.string.home_function_name_beauty_body));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_human_avatar, R.string.home_function_name_human_avatar));
//        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_action_recognition, R.string.home_function_name_action_recognition));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_portrait_segment, R.string.home_function_name_portrait_segment));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_gesture, R.string.home_function_name_gesture));
        data.add(new HomeFunctionModuleData(R.mipmap.ico_home_green_curtain, R.string.home_function_name_green_curtain));
        data.add(new HomeFunctionModuleData(FunctionType.Title, R.string.main_content_service));
        data.add(new HomeFunctionModuleData(FunctionType.ModelLottie, R.string.home_function_name_fine_sticker));
        return data;
    }


}