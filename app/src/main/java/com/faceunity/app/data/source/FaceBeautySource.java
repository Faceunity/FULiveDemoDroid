package com.faceunity.app.data.source;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.disksource.FUUtils;
import com.faceunity.app.data.disksource.FaceBeautyData;
import com.faceunity.app.utils.FuDeviceUtils;
import com.faceunity.core.controller.facebeauty.FaceBeautyParam;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUFaceBeautyMultiModePropertyEnum;
import com.faceunity.core.enumeration.FUFaceBeautyPropertyModeEnum;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.facebeauty.FaceBeautyFilterEnum;
import com.faceunity.ui.entity.FaceBeautyBean;
import com.faceunity.ui.entity.FaceBeautyFilterBean;
import com.faceunity.ui.entity.FaceBeautyStyleBean;
import com.faceunity.ui.entity.ModelAttributeData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * DESC：美颜数据构造
 * Created on 2021/3/27
 */
public class FaceBeautySource {

    private static ArrayList<FaceBeautyFilterBean> filters = new ArrayList<>();
    private static FaceBeautyData faceBeautyData;

    /**
     * 获取默认推荐美颜模型
     * 一个app生命周期请求一次
     *
     * @return
     */
    public static FaceBeauty getDefaultFaceBeauty() {
        FaceBeauty recommendFaceBeauty = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
        if (DemoConfig.OPEN_FACE_BEAUTY_TO_FILE)
            faceBeautyData = FUUtils.loadFaceBeautyData();
        if (faceBeautyData != null) {
            //有本地缓存
            FaceBeautyDataFactory.setDiskCurrentStyleIndex(faceBeautyData.styleTypeIndex);
            FUUtils.setFaceBeauty(faceBeautyData, recommendFaceBeauty);
        } else {
            //没有本地缓存
            recommendFaceBeauty.setFilterName(FaceBeautyFilterEnum.ZIRAN_2);
            recommendFaceBeauty.setFilterIntensity(0.4);
            /*美肤*/
            recommendFaceBeauty.setSharpenIntensity(0.2);
            recommendFaceBeauty.setColorIntensity(0.3);
            recommendFaceBeauty.setRedIntensity(0.3);
            recommendFaceBeauty.setBlurIntensity(4.2);
            /*美型*/
            recommendFaceBeauty.setFaceShapeIntensity(1.0);
            recommendFaceBeauty.setEyeEnlargingIntensity(0.4);
            recommendFaceBeauty.setCheekVIntensity(0.5);
            recommendFaceBeauty.setNoseIntensity(0.5);
            recommendFaceBeauty.setForHeadIntensity(0.3);
            recommendFaceBeauty.setMouthIntensity(0.4);
            recommendFaceBeauty.setChinIntensity(0.3);
            //性能最优策略
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID) {
                setFaceBeautyPropertyMode(recommendFaceBeauty);
            }
        }

        return recommendFaceBeauty;
    }

    /**
     * 高端机的时候，开启4个相对吃性能的模式
     * 1.祛黑眼圈 MODE2
     * 2.祛法令纹 MODE2
     * 3.大眼 MODE3
     * 4.嘴型 MODE3
     */
    private static void setFaceBeautyPropertyMode(FaceBeauty faceBeauty) {
        /*
         * 多模式属性
         * 属性名称|支持模式|默认模式|最早支持版本
         * 美白 colorIntensity|MODE1 MODE2|MODE2|MODE2 8.2.0;
         * 祛黑眼圈 removePouchIntensity|MODE1 MODE2|MODE2|MODE2 8.2.0;
         * 祛法令纹 removeLawPatternIntensity|MODE1 MODE1|MODE2|MODE2 8.2.0;
         * 窄脸程度 cheekNarrowIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 小脸程度 cheekSmallIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 大眼程度 eyeEnlargingIntensity|MODE1 MODE2 MODE3|MODE3|MODE2 8.0.0;MODE3 8.2.0;
         * 额头调整程度 forHeadIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 瘦鼻程度 noseIntensity|MODE1 MODE2|MODE2|MODE2 8.0.0;
         * 嘴巴调整程度 mouthIntensity|MODE1 MODE2 MODE3|MODE3|MODE2 8.0.0;MODE3 8.2.0;
         */
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.REMOVE_POUCH_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE2);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.REMOVE_NASOLABIAL_FOLDS_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE2);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.EYE_ENLARGING_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE3);
        faceBeauty.addPropertyMode(FUFaceBeautyMultiModePropertyEnum.MOUTH_INTENSITY, FUFaceBeautyPropertyModeEnum.MODE3);
    }


    /**
     * 初始化美肤参数
     *
     * @return ArrayList<FaceBeautyBean>
     */
    public static ArrayList<FaceBeautyBean> buildSkinParams() {
        ArrayList<FaceBeautyBean> params = new ArrayList<>();
        params.add(new FaceBeautyBean(
                        FaceBeautyParam.BLUR_INTENSITY, R.string.beauty_box_heavy_blur_fine,
                        R.drawable.icon_beauty_skin_buffing_close_selector, R.drawable.icon_beauty_skin_buffing_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.COLOR_INTENSITY, R.string.beauty_box_color_level,
                        R.drawable.icon_beauty_skin_color_close_selector, R.drawable.icon_beauty_skin_color_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.RED_INTENSITY, R.string.beauty_box_red_level,
                        R.drawable.icon_beauty_skin_red_close_selector, R.drawable.icon_beauty_skin_red_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.SHARPEN_INTENSITY, R.string.beauty_box_sharpen,
                        R.drawable.icon_beauty_skin_sharpen_close_selector, R.drawable.icon_beauty_skin_sharpen_open_selector
                )
        );
        //五官立体
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.FACE_THREED, R.string.beauty_face_three,
                        R.drawable.icon_beauty_skin_face_three_close_selector, R.drawable.icon_beauty_skin_face_three_open_selector)
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_BRIGHT_INTENSITY, R.string.beauty_box_eye_bright,
                        R.drawable.icon_beauty_skin_eyes_bright_close_selector, R.drawable.icon_beauty_skin_eyes_bright_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.TOOTH_WHITEN_INTENSITY, R.string.beauty_box_tooth_whiten,
                        R.drawable.icon_beauty_skin_teeth_close_selector, R.drawable.icon_beauty_skin_teeth_open_selector
                )
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.REMOVE_POUCH_INTENSITY, R.string.beauty_micro_pouch,
                        R.drawable.icon_beauty_skin_dark_circles_close_selector, R.drawable.icon_beauty_skin_dark_circles_open_selector)
        );
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, R.string.beauty_micro_nasolabial,
                        R.drawable.icon_beauty_skin_wrinkle_close_selector, R.drawable.icon_beauty_skin_wrinkle_open_selector)
        );

        return params;
    }

    /**
     * 初始化美型参数
     *
     * @return ArrayList<FaceBeautyBean>
     */
    public static ArrayList<FaceBeautyBean> buildShapeParams() {
        ArrayList<FaceBeautyBean> params = new ArrayList<>();
//        params.add(
//                new FaceBeautyBean(
//                        "", R.string.avatar_face_face,
//                        R.drawable.icon_beauty_shape_face_shape_close_selector, R.drawable.icon_beauty_shape_face_shape_open_selector, FaceBeautyBean.ButtonType.SUB_ITEM_BUTTON
//                )
//        );

        //瘦脸
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_THINNING_INTENSITY, R.string.beauty_box_cheek_thinning,
                        R.drawable.icon_beauty_shape_face_cheekthin_close_selector, R.drawable.icon_beauty_shape_face_cheekthin_open_selector
                )
        );

        //V脸
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_V_INTENSITY, R.string.beauty_box_cheek_v,
                        R.drawable.icon_beauty_shape_face_v_close_selector, R.drawable.icon_beauty_shape_face_v_open_selector
                )
        );

        //窄脸
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_NARROW_INTENSITY, R.string.beauty_box_cheek_narrow,
                        R.drawable.icon_beauty_shape_face_narrow_close_selector, R.drawable.icon_beauty_shape_face_narrow_open_selector
                )
        );

        //小脸 -> 短脸  --使用的参数是以前小脸的
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_SHORT_INTENSITY, R.string.beauty_box_cheek_short,
                        R.drawable.icon_beauty_shape_face_short_close_selector, R.drawable.icon_beauty_shape_face_short_open_selector
                )
        );

        //小脸 -> 新增
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHEEK_SMALL_INTENSITY, R.string.beauty_box_cheek_small,
                        R.drawable.icon_beauty_shape_face_little_close_selector, R.drawable.icon_beauty_shape_face_little_open_selector
                )
        );
        //瘦颧骨
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, R.string.beauty_box_cheekbones,
                        R.drawable.icon_beauty_shape_cheek_bones_close_selector, R.drawable.icon_beauty_shape_cheek_bones_open_selector
                )
        );

        //瘦下颌骨
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, R.string.beauty_box_lower_jaw,
                        R.drawable.icon_beauty_shape_lower_jaw_close_selector, R.drawable.icon_beauty_shape_lower_jaw_open_selector
                )
        );

        //大眼
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_ENLARGING_INTENSITY, R.string.beauty_box_eye_enlarge,
                        R.drawable.icon_beauty_shape_enlarge_eye_close_selector, R.drawable.icon_beauty_shape_enlarge_eye_open_selector
                )
        );

        //圆眼
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_CIRCLE_INTENSITY, R.string.beauty_box_eye_circle,
                        R.drawable.icon_beauty_shape_round_eye_close_selector, R.drawable.icon_beauty_shape_round_eye_open_selector
                )
        );

        //下巴
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CHIN_INTENSITY, R.string.beauty_box_intensity_chin,
                        R.drawable.icon_beauty_shape_chin_close_selector, R.drawable.icon_beauty_shape_chin_open_selector
                )
        );

        //额头
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.FOREHEAD_INTENSITY, R.string.beauty_box_intensity_forehead,
                        R.drawable.icon_beauty_shape_forehead_close_selector, R.drawable.icon_beauty_shape_forehead_open_selector
                )
        );

        //瘦鼻
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.NOSE_INTENSITY, R.string.beauty_box_intensity_nose,
                        R.drawable.icon_beauty_shape_thin_nose_close_selector, R.drawable.icon_beauty_shape_thin_nose_open_selector
                )
        );

        //嘴型
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.MOUTH_INTENSITY, R.string.beauty_box_intensity_mouth,
                        R.drawable.icon_beauty_shape_mouth_close_selector, R.drawable.icon_beauty_shape_mouth_open_selector
                )
        );

        //嘴唇厚度
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_LIP_THICK, R.string.beauty_lip_thick,
                        R.drawable.icon_beauty_shape_lip_thick_close_selector, R.drawable.icon_beauty_shape_lip_thick_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID)
        );

        //眼睛位置
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_EYE_HEIGHT, R.string.beauty_eye_height,
                        R.drawable.icon_beauty_shape_eye_height_close_selector, R.drawable.icon_beauty_shape_eye_height_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID)
        );

        //开眼角
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.CANTHUS_INTENSITY, R.string.beauty_micro_canthus,
                        R.drawable.icon_beauty_shape_open_eyes_close_selector, R.drawable.icon_beauty_shape_open_eyes_open_selector
                )
        );

        //眼睑下至
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_EYE_LID, R.string.beauty_eye_lid,
                        R.drawable.icon_beauty_shape_eye_lid_close_selector, R.drawable.icon_beauty_shape_eye_lid_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID)
        );

        //眼距
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_SPACE_INTENSITY, R.string.beauty_micro_eye_space,
                        R.drawable.icon_beauty_shape_distance_close_selector, R.drawable.icon_beauty_shape_distance_open_selector
                )
        );

        //眼睛角度
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.EYE_ROTATE_INTENSITY, R.string.beauty_micro_eye_rotate,
                        R.drawable.icon_beauty_shape_angle_close_selector, R.drawable.icon_beauty_shape_angle_open_selector
                )
        );

        //长鼻
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.LONG_NOSE_INTENSITY, R.string.beauty_micro_long_nose,
                        R.drawable.icon_beauty_shape_proboscis_close_selector, R.drawable.icon_beauty_shape_proboscis_open_selector
                )
        );

        //缩人中
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.PHILTRUM_INTENSITY, R.string.beauty_micro_philtrum,
                        R.drawable.icon_beauty_shape_shrinking_close_selector, R.drawable.icon_beauty_shape_shrinking_open_selector
                )
        );

        //微笑嘴角
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.SMILE_INTENSITY, R.string.beauty_micro_smile,
                        R.drawable.icon_beauty_shape_smile_close_selector, R.drawable.icon_beauty_shape_smile_open_selector
                )
        );

        //眉毛上下
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.BROW_HEIGHT_INTENSITY, R.string.beauty_brow_height,
                        R.drawable.icon_beauty_shape_brow_height_close_selector, R.drawable.icon_beauty_shape_brow_height_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID
                )
        );

        //眉间距
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.BROW_SPACE_INTENSITY, R.string.beauty_brow_space,
                        R.drawable.icon_beauty_shape_brow_space_close_selector, R.drawable.icon_beauty_shape_brow_space_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID
                )
        );

        //眉毛粗细
        params.add(
                new FaceBeautyBean(
                        FaceBeautyParam.INTENSITY_BROW_THICK, R.string.beauty_brow_thick,
                        R.drawable.icon_beauty_shape_brow_thick_close_selector, R.drawable.icon_beauty_shape_brow_thick_open_selector,
                        DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID)
        );
        return params;
    }

    /**
     * 加载脸型子项
     *
     * @return
     */
    public static ArrayList<FaceBeautyBean> buildFaceShapeSubItemParams() {
        return buildSubItemParams(FaceBeautyParam.FACE_SHAPE);
    }

    public static ArrayList<FaceBeautyBean> buildSubItemParams(String key) {
        ArrayList<FaceBeautyBean> params = new ArrayList<>();
//        if (key != null && !key.isEmpty()) {
//            if (key.equals(FaceBeautyParam.FACE_SHAPE)) {
//                //返回
//                params.add(
//                        new FaceBeautyBean(
//                                "", R.string.back,
//                                R.mipmap.icon_beauty_back, R.mipmap.icon_beauty_back, FaceBeautyBean.ButtonType.BACK_BUTTON
//                        )
//                );
//
//                //自然 V脸 -> 自然脸
//                params.add(
//                        new FaceBeautyBean(
//                                FaceBeautyParam.CHEEK_V_INTENSITY, R.string.beauty_box_cheek_natural,
//                                R.drawable.icon_beauty_shape_face_natural_close_selector, R.drawable.icon_beauty_shape_face_natural_open_selector
//                        )
//                );
//
//                //女神 瘦脸 -> 女神脸
//                params.add(
//                        new FaceBeautyBean(
//                                FaceBeautyParam.CHEEK_THINNING_INTENSITY, R.string.beauty_box_cheek_goddess,
//                                R.drawable.icon_beauty_shape_face_goddess_close_selector, R.drawable.icon_beauty_shape_face_goddess_open_selector
//                        )
//                );
//
//                //长脸
//                params.add(
//                        new FaceBeautyBean(
//                                FaceBeautyParam.CHEEK_LONG_INTENSITY, R.string.beauty_box_cheek_long_face,
//                                R.drawable.icon_beauty_shape_face_long_close_selector, R.drawable.icon_beauty_shape_face_long_open_selector
//                        )
//                );
//
//                //圆脸
//                params.add(
//                        new FaceBeautyBean(
//                                FaceBeautyParam.CHEEK_CIRCLE_INTENSITY, R.string.beauty_box_cheek_round_face,
//                                R.drawable.icon_beauty_shape_face_round_close_selector, R.drawable.icon_beauty_shape_face_round_open_selector
//                        )
//                );
//            }
//        }

        return params;
    }

    /**
     * 初始化参数扩展列表
     *
     * @return HashMap<String, ModelAttributeData>
     */
    public static HashMap<String, ModelAttributeData> buildModelAttributeRange() {
        HashMap<String, ModelAttributeData> params = new HashMap<>();
        /*美肤*/
        params.put(FaceBeautyParam.COLOR_INTENSITY, new ModelAttributeData(0.3, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.BLUR_INTENSITY, new ModelAttributeData(4.2, 0.0, 0.0, 6.0));
        params.put(FaceBeautyParam.RED_INTENSITY, new ModelAttributeData(0.3, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.SHARPEN_INTENSITY, new ModelAttributeData(0.2, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_BRIGHT_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.TOOTH_WHITEN_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.REMOVE_POUCH_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.REMOVE_NASOLABIAL_FOLDS_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.FACE_THREED, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        /*美型*/
        params.put(FaceBeautyParam.FACE_SHAPE_INTENSITY, new ModelAttributeData(1.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_THINNING_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_LONG_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_CIRCLE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_V_INTENSITY, new ModelAttributeData(0.5, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_NARROW_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_SHORT_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHEEK_SMALL_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_CHEEKBONES_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_LOW_JAW_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_ENLARGING_INTENSITY, new ModelAttributeData(0.4, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_CIRCLE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.CHIN_INTENSITY, new ModelAttributeData(0.3, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.FOREHEAD_INTENSITY, new ModelAttributeData(0.3, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.NOSE_INTENSITY, new ModelAttributeData(0.5, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.MOUTH_INTENSITY, new ModelAttributeData(0.4, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.CANTHUS_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_SPACE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.EYE_ROTATE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.LONG_NOSE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.PHILTRUM_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.SMILE_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.BROW_HEIGHT_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.BROW_SPACE_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_EYE_LID, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_EYE_HEIGHT, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_BROW_THICK, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(FaceBeautyParam.INTENSITY_LIP_THICK, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        return params;
    }


    /**
     * 初始化滤镜参数
     *
     * @return ArrayList<FaceBeautyFilterBean>
     */
    public static ArrayList<FaceBeautyFilterBean> buildFilters() {
        if (!filters.isEmpty()) {
            return filters;
        }
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ORIGIN, R.mipmap.icon_beauty_filter_cancel, R.string.origin, 0.0));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_1, R.mipmap.icon_beauty_filter_natural_1, R.string.ziran_1, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_2, R.mipmap.icon_beauty_filter_natural_2, R.string.ziran_2, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_3, R.mipmap.icon_beauty_filter_natural_3, R.string.ziran_3, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_4, R.mipmap.icon_beauty_filter_natural_4, R.string.ziran_4, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_5, R.mipmap.icon_beauty_filter_natural_5, R.string.ziran_5, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_5)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_6, R.mipmap.icon_beauty_filter_natural_6, R.string.ziran_6, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_6)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_7, R.mipmap.icon_beauty_filter_natural_7, R.string.ziran_7, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_7)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZIRAN_8, R.mipmap.icon_beauty_filter_natural_8, R.string.ziran_8, getDiskFilterValue(FaceBeautyFilterEnum.ZIRAN_8)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_1, R.mipmap.icon_beauty_filter_texture_gray_1, R.string.zhiganhui_1, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_2, R.mipmap.icon_beauty_filter_texture_gray_2, R.string.zhiganhui_2, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_3, R.mipmap.icon_beauty_filter_texture_gray_3, R.string.zhiganhui_3, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_4, R.mipmap.icon_beauty_filter_texture_gray_4, R.string.zhiganhui_4, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_5, R.mipmap.icon_beauty_filter_texture_gray_5, R.string.zhiganhui_5, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_5)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_6, R.mipmap.icon_beauty_filter_texture_gray_6, R.string.zhiganhui_6, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_6)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_7, R.mipmap.icon_beauty_filter_texture_gray_7, R.string.zhiganhui_7, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_7)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.ZHIGANHUI_8, R.mipmap.icon_beauty_filter_texture_gray_8, R.string.zhiganhui_8, getDiskFilterValue(FaceBeautyFilterEnum.ZHIGANHUI_8)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_1, R.mipmap.icon_beauty_filter_peach_1, R.string.mitao_1, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_2, R.mipmap.icon_beauty_filter_peach_2, R.string.mitao_2, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_3, R.mipmap.icon_beauty_filter_peach_3, R.string.mitao_3, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_4, R.mipmap.icon_beauty_filter_peach_4, R.string.mitao_4, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_5, R.mipmap.icon_beauty_filter_peach_5, R.string.mitao_5, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_5)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_6, R.mipmap.icon_beauty_filter_peach_6, R.string.mitao_6, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_6)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_7, R.mipmap.icon_beauty_filter_peach_7, R.string.mitao_7, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_7)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.MITAO_8, R.mipmap.icon_beauty_filter_peach_8, R.string.mitao_8, getDiskFilterValue(FaceBeautyFilterEnum.MITAO_8)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_1, R.mipmap.icon_beauty_filter_bailiang_1, R.string.bailiang_1, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_2, R.mipmap.icon_beauty_filter_bailiang_2, R.string.bailiang_2, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_3, R.mipmap.icon_beauty_filter_bailiang_3, R.string.bailiang_3, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_4, R.mipmap.icon_beauty_filter_bailiang_4, R.string.bailiang_4, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_5, R.mipmap.icon_beauty_filter_bailiang_5, R.string.bailiang_5, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_5)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_6, R.mipmap.icon_beauty_filter_bailiang_6, R.string.bailiang_6, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_6)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.BAILIANG_7, R.mipmap.icon_beauty_filter_bailiang_7, R.string.bailiang_7, getDiskFilterValue(FaceBeautyFilterEnum.BAILIANG_7)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_1, R.mipmap.icon_beauty_filter_fennen_1, R.string.fennen_1, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_2, R.mipmap.icon_beauty_filter_fennen_2, R.string.fennen_2, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_3, R.mipmap.icon_beauty_filter_fennen_3, R.string.fennen_3, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_5, R.mipmap.icon_beauty_filter_fennen_5, R.string.fennen_5, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_5)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_6, R.mipmap.icon_beauty_filter_fennen_6, R.string.fennen_6, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_6)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_7, R.mipmap.icon_beauty_filter_fennen_7, R.string.fennen_7, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_7)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.FENNEN_8, R.mipmap.icon_beauty_filter_fennen_8, R.string.fennen_8, getDiskFilterValue(FaceBeautyFilterEnum.FENNEN_8)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_1, R.mipmap.icon_beauty_filter_lengsediao_1, R.string.lengsediao_1, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_2, R.mipmap.icon_beauty_filter_lengsediao_2, R.string.lengsediao_2, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_3, R.mipmap.icon_beauty_filter_lengsediao_3, R.string.lengsediao_3, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_4, R.mipmap.icon_beauty_filter_lengsediao_4, R.string.lengsediao_4, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_7, R.mipmap.icon_beauty_filter_lengsediao_7, R.string.lengsediao_7, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_7)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_8, R.mipmap.icon_beauty_filter_lengsediao_8, R.string.lengsediao_8, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_8)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.LENGSEDIAO_11, R.mipmap.icon_beauty_filter_lengsediao_11, R.string.lengsediao_11, getDiskFilterValue(FaceBeautyFilterEnum.LENGSEDIAO_11)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.NUANSEDIAO_1, R.mipmap.icon_beauty_filter_nuansediao_1, R.string.nuansediao_1, getDiskFilterValue(FaceBeautyFilterEnum.NUANSEDIAO_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.NUANSEDIAO_2, R.mipmap.icon_beauty_filter_nuansediao_2, R.string.nuansediao_2, getDiskFilterValue(FaceBeautyFilterEnum.NUANSEDIAO_2)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_1, R.mipmap.icon_beauty_filter_gexing_1, R.string.gexing_1, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_2, R.mipmap.icon_beauty_filter_gexing_2, R.string.gexing_2, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_3, R.mipmap.icon_beauty_filter_gexing_3, R.string.gexing_3, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_4, R.mipmap.icon_beauty_filter_gexing_4, R.string.gexing_4, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_5, R.mipmap.icon_beauty_filter_gexing_5, R.string.gexing_5, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_5)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_7, R.mipmap.icon_beauty_filter_gexing_7, R.string.gexing_7, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_7)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_10, R.mipmap.icon_beauty_filter_gexing_10, R.string.gexing_10, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_10)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.GEXING_11, R.mipmap.icon_beauty_filter_gexing_11, R.string.gexing_11, getDiskFilterValue(FaceBeautyFilterEnum.GEXING_11)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.XIAOQINGXIN_1, R.mipmap.icon_beauty_filter_xiaoqingxin_1, R.string.xiaoqingxin_1, getDiskFilterValue(FaceBeautyFilterEnum.XIAOQINGXIN_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.XIAOQINGXIN_3, R.mipmap.icon_beauty_filter_xiaoqingxin_3, R.string.xiaoqingxin_3, getDiskFilterValue(FaceBeautyFilterEnum.XIAOQINGXIN_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.XIAOQINGXIN_4, R.mipmap.icon_beauty_filter_xiaoqingxin_4, R.string.xiaoqingxin_4, getDiskFilterValue(FaceBeautyFilterEnum.XIAOQINGXIN_4)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.XIAOQINGXIN_6, R.mipmap.icon_beauty_filter_xiaoqingxin_6, R.string.xiaoqingxin_6, getDiskFilterValue(FaceBeautyFilterEnum.XIAOQINGXIN_6)));

        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.HEIBAI_1, R.mipmap.icon_beauty_filter_heibai_1, R.string.heibai_1, getDiskFilterValue(FaceBeautyFilterEnum.HEIBAI_1)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.HEIBAI_2, R.mipmap.icon_beauty_filter_heibai_2, R.string.heibai_2, getDiskFilterValue(FaceBeautyFilterEnum.HEIBAI_2)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.HEIBAI_3, R.mipmap.icon_beauty_filter_heibai_3, R.string.heibai_3, getDiskFilterValue(FaceBeautyFilterEnum.HEIBAI_3)));
        filters.add(new FaceBeautyFilterBean(FaceBeautyFilterEnum.HEIBAI_4, R.mipmap.icon_beauty_filter_heibai_4, R.string.heibai_4, getDiskFilterValue(FaceBeautyFilterEnum.HEIBAI_4)));

        return filters;
    }

    /**
     * 从磁盘获取所有项滤镜强度
     *
     * @param key
     * @return
     */
    private static double getDiskFilterValue(String key) {
        if (faceBeautyData != null) {
            return faceBeautyData.filterMap.get(key);
        } else {
            return 0.4;
        }
    }

    private static final String CONFIG_BIAOZHUN = "biaozhun";
    private static final String CONFIG_HUAJIAO = "huajiao";
    private static final String CONFIG_KUAISHOU = "kuaishou";
    private static final String CONFIG_QINGYAN = "qingyan";
    private static final String CONFIG_SHANGTANG = "shangtang";
    private static final String CONFIG_YINGKE = "yingke";
    private static final String CONFIG_ZIJIETIAODONG = "zijietiaodong";


    /**
     * 初始化风格推荐
     *
     * @return ArrayList<FaceBeautyBean>
     */
    public static ArrayList<FaceBeautyStyleBean> buildStylesParams() {
        ArrayList<FaceBeautyStyleBean> params = new ArrayList<>();
        params.add(new FaceBeautyStyleBean(CONFIG_KUAISHOU, R.drawable.icon_beauty_style_1_selector, R.string.beauty_face_style_1));
        params.add(new FaceBeautyStyleBean(CONFIG_QINGYAN, R.drawable.icon_beauty_style_2_selector, R.string.beauty_face_style_2));
        params.add(new FaceBeautyStyleBean(CONFIG_ZIJIETIAODONG, R.drawable.icon_beauty_style_3_selector, R.string.beauty_face_style_3));
        params.add(new FaceBeautyStyleBean(CONFIG_HUAJIAO, R.drawable.icon_beauty_style_4_selector, R.string.beauty_face_style_4));
        params.add(new FaceBeautyStyleBean(CONFIG_YINGKE, R.drawable.icon_beauty_style_5_selector, R.string.beauty_face_style_5));
        params.add(new FaceBeautyStyleBean(CONFIG_SHANGTANG, R.drawable.icon_beauty_style_6_selector, R.string.beauty_face_style_6));
        params.add(new FaceBeautyStyleBean(CONFIG_BIAOZHUN, R.drawable.icon_beauty_style_7_selector, R.string.beauty_face_style_7));
        return params;
    }

    /**
     * 风格对应参数配置
     */
    public static HashMap<String, Runnable> styleParams = new HashMap<String, Runnable>() {
        {
            put(CONFIG_KUAISHOU, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setColorIntensity(0.5);
                model.setBlurIntensity(3.6);
                model.setEyeBrightIntensity(0.35);
                model.setToothIntensity(0.25);
                model.setCheekThinningIntensity(0.45);
                model.setCheekVIntensity(0.08);
                model.setCheekSmallIntensity(0.05);
                model.setEyeEnlargingIntensity(0.3);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);

            });
            put(CONFIG_QINGYAN, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setFilterName(FaceBeautyFilterEnum.ZIRAN_3);
                model.setFilterIntensity(0.3);
                model.setColorIntensity(0.4);
                model.setRedIntensity(0.2);
                model.setBlurIntensity(3.6);
                model.setEyeBrightIntensity(0.5);
                model.setToothIntensity(0.4);
                model.setCheekThinningIntensity(0.3);
                model.setNoseIntensity(0.5);
                model.setEyeEnlargingIntensity(0.25);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
            });
            put(CONFIG_ZIJIETIAODONG, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setColorIntensity(0.4);
                model.setRedIntensity(0.3);
                model.setBlurIntensity(2.4);
                model.setCheekThinningIntensity(0.3);
                model.setCheekSmallIntensity(0.15);
                model.setEyeEnlargingIntensity(0.65);
                model.setNoseIntensity(0.3);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
            });
            put(CONFIG_HUAJIAO, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setColorIntensity(0.7);
                model.setBlurIntensity(3.9);
                model.setCheekThinningIntensity(0.3);
                model.setCheekSmallIntensity(0.05);
                model.setEyeEnlargingIntensity(0.65);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
            });
            put(CONFIG_YINGKE, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setFilterName(FaceBeautyFilterEnum.FENNEN_2);
                model.setFilterIntensity(0.5);
                model.setColorIntensity(0.6);
                model.setBlurIntensity(3.0);
                model.setCheekThinningIntensity(0.5);
                model.setEyeEnlargingIntensity(0.65);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
            });
            put(CONFIG_SHANGTANG, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setFilterName(FaceBeautyFilterEnum.FENNEN_2);
                model.setFilterIntensity(0.8);
                model.setColorIntensity(0.7);
                model.setBlurIntensity(4.2);
                model.setEyeEnlargingIntensity(0.6);
                model.setCheekThinningIntensity(0.3);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
            });
            put(CONFIG_BIAOZHUN, () -> {
                FaceBeauty model = new FaceBeauty(new FUBundleData(DemoConfig.BUNDLE_FACE_BEAUTIFICATION));
                model.setFaceShapeIntensity(1.0);
                model.setFilterName(FaceBeautyFilterEnum.ZIRAN_5);
                model.setFilterIntensity(0.55);
                model.setColorIntensity(0.2);
                model.setRedIntensity(0.65);
                model.setBlurIntensity(3.3);
                model.setCheekSmallIntensity(0.05);
                model.setCheekThinningIntensity(0.1);
                FaceBeautyDataFactory.faceBeauty = model;
                FURenderKit.getInstance().setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
            });
        }

    };

    /**
     * 克隆模型
     *
     * @param faceBeauty
     * @return
     */
    public static FaceBeauty clone(FaceBeauty faceBeauty) {
        FaceBeauty cloneFaceBeauty = new FaceBeauty(new FUBundleData(faceBeauty.getControlBundle().getPath()));
        /*滤镜*/
        cloneFaceBeauty.setFilterName(faceBeauty.getFilterName());
        cloneFaceBeauty.setFilterIntensity(faceBeauty.getFilterIntensity());
        /*美肤*/
        cloneFaceBeauty.setBlurIntensity(faceBeauty.getBlurIntensity());
        cloneFaceBeauty.setEnableHeavyBlur(faceBeauty.getEnableHeavyBlur());
        cloneFaceBeauty.setEnableSkinDetect(faceBeauty.getEnableSkinDetect());
        cloneFaceBeauty.setNonSkinBlurIntensity(faceBeauty.getNonSkinBlurIntensity());
        cloneFaceBeauty.setBlurType(faceBeauty.getBlurType());
        cloneFaceBeauty.setEnableBlurUseMask(faceBeauty.getEnableBlurUseMask());
        cloneFaceBeauty.setColorIntensity(faceBeauty.getColorIntensity());
        cloneFaceBeauty.setRedIntensity(faceBeauty.getRedIntensity());
        cloneFaceBeauty.setSharpenIntensity(faceBeauty.getSharpenIntensity());
        cloneFaceBeauty.setEyeBrightIntensity(faceBeauty.getEyeBrightIntensity());
        cloneFaceBeauty.setToothIntensity(faceBeauty.getToothIntensity());
        cloneFaceBeauty.setRemovePouchIntensity(faceBeauty.getRemovePouchIntensity());
        cloneFaceBeauty.setRemoveLawPatternIntensity(faceBeauty.getRemoveLawPatternIntensity());
        /*美型*/
        cloneFaceBeauty.setFaceShape(faceBeauty.getFaceShape());
        cloneFaceBeauty.setFaceShapeIntensity(faceBeauty.getFaceShapeIntensity());
        cloneFaceBeauty.setCheekThinningIntensity(faceBeauty.getCheekThinningIntensity());
        cloneFaceBeauty.setCheekVIntensity(faceBeauty.getCheekVIntensity());
        cloneFaceBeauty.setCheekLongIntensity(faceBeauty.getCheekLongIntensity());
        cloneFaceBeauty.setCheekCircleIntensity(faceBeauty.getCheekCircleIntensity());
        cloneFaceBeauty.setCheekNarrowIntensity(faceBeauty.getCheekNarrowIntensity());
        cloneFaceBeauty.setCheekShortIntensity(faceBeauty.getCheekShortIntensity());
        cloneFaceBeauty.setCheekSmallIntensity(faceBeauty.getCheekSmallIntensity());
        cloneFaceBeauty.setCheekBonesIntensity(faceBeauty.getCheekBonesIntensity());
        cloneFaceBeauty.setLowerJawIntensity(faceBeauty.getLowerJawIntensity());
        cloneFaceBeauty.setEyeEnlargingIntensity(faceBeauty.getEyeEnlargingIntensity());
        cloneFaceBeauty.setChinIntensity(faceBeauty.getChinIntensity());
        cloneFaceBeauty.setForHeadIntensity(faceBeauty.getForHeadIntensity());
        cloneFaceBeauty.setNoseIntensity(faceBeauty.getNoseIntensity());
        cloneFaceBeauty.setMouthIntensity(faceBeauty.getMouthIntensity());
        cloneFaceBeauty.setCanthusIntensity(faceBeauty.getCanthusIntensity());
        cloneFaceBeauty.setEyeSpaceIntensity(faceBeauty.getEyeSpaceIntensity());
        cloneFaceBeauty.setEyeRotateIntensity(faceBeauty.getEyeRotateIntensity());
        cloneFaceBeauty.setLongNoseIntensity(faceBeauty.getLongNoseIntensity());
        cloneFaceBeauty.setPhiltrumIntensity(faceBeauty.getPhiltrumIntensity());
        cloneFaceBeauty.setSmileIntensity(faceBeauty.getSmileIntensity());
        cloneFaceBeauty.setEyeCircleIntensity(faceBeauty.getEyeCircleIntensity());
        cloneFaceBeauty.setBrowHeightIntensity(faceBeauty.getBrowHeightIntensity());
        cloneFaceBeauty.setBrowSpaceIntensity(faceBeauty.getBrowSpaceIntensity());
        cloneFaceBeauty.setEyeLidIntensity(faceBeauty.getEyeLidIntensity());
        cloneFaceBeauty.setEyeHeightIntensity(faceBeauty.getEyeHeightIntensity());
        cloneFaceBeauty.setBrowThickIntensity(faceBeauty.getBrowThickIntensity());
        cloneFaceBeauty.setLipThickIntensity(faceBeauty.getLipThickIntensity());
        cloneFaceBeauty.setFaceThreeIntensity(faceBeauty.getFaceThreeIntensity());
        cloneFaceBeauty.setChangeFramesIntensity(faceBeauty.getChangeFramesIntensity());
        return cloneFaceBeauty;
    }

}
