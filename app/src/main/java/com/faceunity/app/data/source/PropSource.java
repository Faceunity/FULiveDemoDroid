package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.ui.entity.PropBean;

import java.util.ArrayList;

/**
 * DESC：道具数据构造：道具贴图、AR面具、搞笑大头、表情识别、哈哈镜、手势识别
 * Created on 2021/3/28
 */
public class PropSource {

    public static ArrayList<PropBean> buildPropBeans(int propType) {
        ArrayList<PropBean> propBeans = new ArrayList<>();
        switch (propType) {
            case FunctionEnum.STICKER:
                propBeans.add(new PropBean(R.mipmap.icon_control_delete_all, null));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_cat_sparks, "effect/normal/cat_sparks.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_fu_zh_fenshu, "effect/normal/fu_zh_fenshu.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_sdlr, "effect/normal/sdlr.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_xlong_zh_fu, "effect/normal/xlong_zh_fu.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_newy1, "effect/normal/newy1.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_redribbt, "effect/normal/redribbt.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_daisypig, "effect/normal/daisypig.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_sticker_sdlu, "effect/normal/sdlu.bundle"));
                break;
            case FunctionEnum.AR_MASK:
                propBeans.add(new PropBean(R.mipmap.icon_control_delete_all, null));
                propBeans.add(new PropBean(R.mipmap.icon_ar_bluebird, "effect/ar/bluebird.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_lanhudie, "effect/ar/lanhudie.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_fenhudie, "effect/ar/fenhudie.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_tiger_huang, "effect/ar/tiger_huang.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_tiger_bai, "effect/ar/tiger_bai.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_baozi, "effect/ar/baozi.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_tiger, "effect/ar/tiger.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_ar_xiongmao, "effect/ar/xiongmao.bundle"));
                break;
            case FunctionEnum.BIG_HEAD:
                propBeans.add(new PropBean(R.mipmap.icon_control_delete_all, null));
                propBeans.add(new PropBean(R.mipmap.icon_big_head, "effect/big_head/big_head.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_big_head_husky_face, "effect/big_head/big_head_facewarp2.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_big_head_sausage_mouth, "effect/big_head/big_head_facewarp4.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_big_head_blush, "effect/big_head/big_head_facewarp5.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_big_head_dark_circles, "effect/big_head/big_head_facewarp6.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_big_head_smiling_head, "effect/big_head/big_head_smile.bundle", R.string.xiaobianzi_zh_fu));
                break;
            case FunctionEnum.EXPRESSION_RECOGNITION:
                propBeans.add(new PropBean(R.mipmap.icon_control_delete_all, null));
                propBeans.add(new PropBean(R.mipmap.icon_expression_future_warrior, "effect/expression/future_warrior.bundle", R.string.future_warrior));
                propBeans.add(new PropBean(R.mipmap.icon_expression_jet_mask, "effect/expression/jet_mask.bundle", R.string.jet_mask));
                propBeans.add(new PropBean(R.mipmap.icon_expression_sdx2, "effect/expression/sdx2.bundle", R.string.sdx2));
                propBeans.add(new PropBean(R.mipmap.icon_expression_luhantongkuan_ztt_fu, "effect/expression/luhantongkuan_ztt_fu.bundle", R.string.luhantongkuan_ztt_fu));
                propBeans.add(new PropBean(R.mipmap.icon_expression_qingqing_ztt_fu, "effect/expression/qingqing_ztt_fu.bundle", R.string.qingqing_ztt_fu));
                propBeans.add(new PropBean(R.mipmap.icon_expression_xiaobianzi_zh_fu, "effect/expression/xiaobianzi_zh_fu.bundle", R.string.xiaobianzi_zh_fu));
                propBeans.add(new PropBean(R.mipmap.icon_expression_xiaoxueshen_ztt_fu, "effect/expression/xiaoxueshen_ztt_fu.bundle", R.string.xiaoxueshen_ztt_fu));
                break;
            case FunctionEnum.FACE_WARP:
                propBeans.add(new PropBean(R.mipmap.icon_control_delete_all, null));
                propBeans.add(new PropBean(R.mipmap.icon_face_warp_2, "effect/facewarp/facewarp2.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_face_warp_3, "effect/facewarp/facewarp3.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_face_warp_4, "effect/facewarp/facewarp4.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_face_warp_5, "effect/facewarp/facewarp5.bundle"));
                propBeans.add(new PropBean(R.mipmap.icon_face_warp_6, "effect/facewarp/facewarp6.bundle"));
                break;
            case FunctionEnum.GESTURE_RECOGNITION:
                propBeans.add(new PropBean(R.mipmap.icon_control_delete_all, null));
                propBeans.add(new PropBean(R.mipmap.icon_gesture_rain, "effect/gesture/ctrl_rain.bundle", R.string.push_hand));
                propBeans.add(new PropBean(R.mipmap.icon_gesture_snow, "effect/gesture/ctrl_snow.bundle", R.string.push_hand));
                propBeans.add(new PropBean(R.mipmap.icon_gesture_flower, "effect/gesture/ctrl_flower.bundle", R.string.push_hand));
                propBeans.add(new PropBean(R.mipmap.icon_gesture_koreaheart, "effect/gesture/ssd_thread_korheart.bundle", R.string.fu_lm_koreaheart));
                propBeans.add(new PropBean(R.mipmap.icon_gesture_six, "effect/gesture/ssd_thread_six.bundle", R.string.ssd_thread_six));
                propBeans.add(new PropBean(R.mipmap.icon_gesture_cute, "effect/gesture/ssd_thread_cute.bundle", R.string.ssd_thread_cute));
                break;


        }
        return propBeans;
    }
}
