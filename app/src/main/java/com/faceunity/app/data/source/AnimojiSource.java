package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.core.model.animationFilter.AnimationFilterTypeEnum;
import com.faceunity.ui.entity.AnimationFilterBean;
import com.faceunity.ui.entity.AnimojiBean;

import java.util.ArrayList;

/**
 * DESC：Animoji数据构造
 * Created on 2021/3/25
 */
public class AnimojiSource {


    /**
     * 构造贴图数据
     *
     * @return
     */
    public static ArrayList<AnimojiBean> buildAnimojis() {
        ArrayList<AnimojiBean> array = new ArrayList<>();
        array.add(new AnimojiBean(R.mipmap.icon_control_delete_all, null));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_cartoon_princess, "animoji/cartoon_princess_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_qgirl, "animoji/qgirl_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_kaola, "animoji/kaola_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_wuxia, "animoji/wuxia_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_baihu, "animoji/baihu_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_frog_st, "animoji/frog_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_huangya, "animoji/huangya_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_hetun, "animoji/hetun_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_douniuquan, "animoji/douniuquan_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_hashiqi, "animoji/hashiqi_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_baimao, "animoji/baimao_Animoji.bundle"));
        array.add(new AnimojiBean(R.mipmap.icon_animoji_kuloutou, "animoji/kuloutou_Animoji.bundle"));
        return array;
    }

    /**
     * 构造滤镜数据
     *
     * @return
     */
    public static ArrayList<AnimationFilterBean> buildFilters() {
        ArrayList<AnimationFilterBean> filters = new ArrayList<>();
        filters.add(new AnimationFilterBean(R.mipmap.icon_control_delete_all, AnimationFilterTypeEnum.Origin));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_anime, AnimationFilterTypeEnum.Comic));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_portrait_dynamiceffect, AnimationFilterTypeEnum.Portrait));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_sketch, AnimationFilterTypeEnum.Sketch));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_oilpainting, AnimationFilterTypeEnum.Oil));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_sandlpainting, AnimationFilterTypeEnum.Sand));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_penpainting, AnimationFilterTypeEnum.Pen));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_pencilpainting, AnimationFilterTypeEnum.Pencil));
        filters.add(new AnimationFilterBean(R.mipmap.icon_cartoon_graffiti, AnimationFilterTypeEnum.Granffiti));
        return filters;
    }

}
