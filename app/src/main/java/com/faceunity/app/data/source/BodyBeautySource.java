package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.core.controller.bodyBeauty.BodyBeautyParam;
import com.faceunity.ui.entity.BodyBeautyBean;
import com.faceunity.ui.entity.ModelAttributeData;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * DESC：美体数据构造
 * Created on 2021/3/26
 */
public class BodyBeautySource {


    /**
     * 构造美体
     *
     * @return
     */
    public static ArrayList<BodyBeautyBean> buildBodyBeauty() {
        ArrayList<BodyBeautyBean> params = new ArrayList<>();
        params.add(new BodyBeautyBean(BodyBeautyParam.BODY_SLIM_INTENSITY, R.string.slimming, R.drawable.icon_body_slimming_close_selector, R.drawable.icon_body_slimming_open_selector));
        params.add(new BodyBeautyBean(BodyBeautyParam.LEG_STRETCH_INTENSITY, R.string.long_legs, R.drawable.icon_body_stovepipe_close_selector, R.drawable.icon_body_stovepipe_open_selector));
        params.add(new BodyBeautyBean(BodyBeautyParam.WAIST_SLIM_INTENSITY, R.string.thin_waist, R.drawable.icon_body_waist_close_selector, R.drawable.icon_body_waist_open_selector));
        params.add(new BodyBeautyBean(BodyBeautyParam.SHOULDER_SLIM_INTENSITY, R.string.beautify_shoulder, R.drawable.icon_body_shoulder_close_selector, R.drawable.icon_body_shoulder_open_selector));
        params.add(new BodyBeautyBean(BodyBeautyParam.HIP_SLIM_INTENSITY, R.string.beautify_hip_slim, R.drawable.icon_body_hip_close_selector, R.drawable.icon_body_hip_open_selector));
        params.add(new BodyBeautyBean(BodyBeautyParam.HEAD_SLIM_INTENSITY, R.string.beautify_head_slim, R.drawable.icon_body_little_head_close_selector, R.drawable.icon_body_little_head_open_selector));
        params.add(new BodyBeautyBean(BodyBeautyParam.LEG_SLIM_INTENSITY, R.string.beautify_leg_thin_slim, R.drawable.icon_body_thin_leg_close_selector, R.drawable.icon_body_thin_leg_open_selector));
        return params;
    }

    /**
     * 获取模型属性扩展数据
     *
     * @return HashMap<String, ModelAttributeData>
     */
    public static HashMap<String, ModelAttributeData> buildModelAttributeRange() {
        HashMap<String, ModelAttributeData> params = new HashMap<>();
        params.put(BodyBeautyParam.BODY_SLIM_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(BodyBeautyParam.LEG_STRETCH_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(BodyBeautyParam.WAIST_SLIM_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(BodyBeautyParam.SHOULDER_SLIM_INTENSITY, new ModelAttributeData(0.5, 0.5, 0.0, 1.0));
        params.put(BodyBeautyParam.HIP_SLIM_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(BodyBeautyParam.HEAD_SLIM_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        params.put(BodyBeautyParam.LEG_SLIM_INTENSITY, new ModelAttributeData(0.0, 0.0, 0.0, 1.0));
        return params;
    }
}
