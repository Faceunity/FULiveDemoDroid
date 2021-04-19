package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.ui.entity.HairBeautyBean;

import java.util.ArrayList;

/**
 * DESC：美发数据构造
 * Created on 2021/3/27
 */
public class HairBeautySource {

    /**
     * 构造美发列表
     *
     * @return ArrayList<HairBeautyBean>
     */
    public static ArrayList<HairBeautyBean> buildHairBeautyBeans() {
        ArrayList<HairBeautyBean> hairBeans = new ArrayList<>();
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_control_delete_all));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_gradualchange_01, 0, 0));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_gradualchange_02, 0, 1));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_gradualchange_03, 0, 2));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_gradualchange_04, 0, 3));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_gradualchange_05, 0, 4));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_01, 1, 0));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_02, 1, 1));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_03, 1, 2));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_04, 1, 3));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_05, 1, 4));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_06, 1, 5));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_07, 1, 6));
        hairBeans.add(new HairBeautyBean(R.mipmap.icon_hair_hairsalon_08, 1, 7));
        return hairBeans;
    }


}
