package com.faceunity.app.entity;

import static com.faceunity.app.entity.FunctionEnum.ACTION_RECOGNITION;
import static com.faceunity.app.entity.FunctionEnum.ANIMOJI;
import static com.faceunity.app.entity.FunctionEnum.AR_MASK;
import static com.faceunity.app.entity.FunctionEnum.AVATAR;
import static com.faceunity.app.entity.FunctionEnum.BG_SEG_GREEN;
import static com.faceunity.app.entity.FunctionEnum.BIG_HEAD;
import static com.faceunity.app.entity.FunctionEnum.BODY_BEAUTY;
import static com.faceunity.app.entity.FunctionEnum.EXPRESSION_RECOGNITION;
import static com.faceunity.app.entity.FunctionEnum.FACE_BEAUTY;
import static com.faceunity.app.entity.FunctionEnum.FACE_WARP;
import static com.faceunity.app.entity.FunctionEnum.FINE_STICKER;
import static com.faceunity.app.entity.FunctionEnum.GESTURE_RECOGNITION;
import static com.faceunity.app.entity.FunctionEnum.HAIR_BEAUTY;
import static com.faceunity.app.entity.FunctionEnum.LIGHT_MAKEUP;
import static com.faceunity.app.entity.FunctionEnum.MAKE_UP;
import static com.faceunity.app.entity.FunctionEnum.MUSIC_FILTER;
import static com.faceunity.app.entity.FunctionEnum.PORTRAIT_SEGMENT;
import static com.faceunity.app.entity.FunctionEnum.POSTER_CHANGE;
import static com.faceunity.app.entity.FunctionEnum.STICKER;
import static com.faceunity.app.entity.FunctionEnum.STYLE;

import java.util.HashMap;

/**
 * DESC：更多控制菜单
 * Created on 2021/3/19
 */
public class FunctionConfigModel {
    public boolean isOpenResolutionChange = false;//是否配置分辨率设置窗口
    public boolean isOpenFURenderInput = false;//是否配置FURender输入形式选择窗口
    public boolean isOpenPhotoVideo = false;//是否配置输入源选择窗口
    public boolean isShowAutoFocus = true;//是否自动对焦

    public FunctionConfigModel(boolean isOpenFURenderInput, boolean isOpenResolutionChange, boolean isOpenPhotoVideo, boolean isShowAutoFocus) {
        this.isOpenFURenderInput = isOpenFURenderInput;
        this.isOpenResolutionChange = isOpenResolutionChange;
        this.isOpenPhotoVideo = isOpenPhotoVideo;
        this.isShowAutoFocus = isShowAutoFocus;
    }

    public static HashMap<Integer, FunctionConfigModel> functionSwitchMap = new HashMap<Integer, FunctionConfigModel>() {
        {
            this.put(FACE_BEAUTY, new FunctionConfigModel(true, true, true, true));
            this.put(MAKE_UP, new FunctionConfigModel(false, false, false, true));
            this.put(STYLE, new FunctionConfigModel(false, true, true, true));
            this.put(STICKER, new FunctionConfigModel(true, false, true, true));
            this.put(ANIMOJI, new FunctionConfigModel(true, false, false, true));
            this.put(HAIR_BEAUTY, new FunctionConfigModel(true, false, false, true));
            this.put(LIGHT_MAKEUP, new FunctionConfigModel(true, false, false, true));
            this.put(AR_MASK, new FunctionConfigModel(true, false, false, true));
            this.put(BIG_HEAD, new FunctionConfigModel(true, false, false, true));
            this.put(POSTER_CHANGE, new FunctionConfigModel(true, false, false, true));
            this.put(EXPRESSION_RECOGNITION, new FunctionConfigModel(true, false, false, true));
            this.put(MUSIC_FILTER, new FunctionConfigModel(true, false, false, true));
            this.put(FACE_WARP, new FunctionConfigModel(true, false, false, true));
            this.put(ACTION_RECOGNITION, new FunctionConfigModel(true, false, false, true));
            this.put(BODY_BEAUTY, new FunctionConfigModel(true, false, false, true));
            this.put(PORTRAIT_SEGMENT, new FunctionConfigModel(false, false, false, true));
            this.put(GESTURE_RECOGNITION, new FunctionConfigModel(true, false, false, true));
            this.put(BG_SEG_GREEN, new FunctionConfigModel(true, true, true, false));
            this.put(FINE_STICKER, new FunctionConfigModel(true, true, false, false));
            this.put(AVATAR, new FunctionConfigModel(false, false, false, false));
        }
    };

}
