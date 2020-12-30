package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;

import java.util.ArrayList;

/**
 * Created by tujh on 2018/1/30.
 */
public enum EffectEnum {
    /**
     * 关闭道具
     */
    EffectNone("none", R.drawable.ic_delete_all, "", 1, Effect.EFFECT_TYPE_NONE, 0),
    /**
     * 道具贴纸
     */
    Effect_nihongdeng("nihongdeng", R.drawable.nihongdeng, "effect/normal/nihongdeng.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_cat_sparks("cat_sparks", R.drawable.cat_sparks, "effect/normal/cat_sparks.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_face_half("face_half", R.drawable.face_half, "effect/normal/face_half.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_fu_zh_fenshu("fu_zh_fenshu", R.drawable.fu_zh_fenshu, "effect/normal/fu_zh_fenshu.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_zhenxinhua_damaoxian("zhenxinhua_damaoxian", R.drawable.zhenxinhua_damaoxian, "effect/normal/zhenxinhua_damaoxian.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_sdlr("sdlr", R.drawable.sdlr, "effect/normal/sdlr.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_xlong_zh_fu("xlong_zh_fu", R.drawable.xlong_zh_fu, "effect/normal/xlong_zh_fu.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_expression_shooting("expression_shooting", R.drawable.expression_shooting, "effect/normal/expression_shooting.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_newy1("newy1", R.drawable.newy1, "effect/normal/newy1.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_guangban("guangban", R.drawable.guangban, "effect/normal/guangban.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_redribbt("redribbt", R.drawable.redribbt, "effect/normal/redribbt.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_daisypig("daisypig", R.drawable.daisypig, "effect/normal/daisypig.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),
    Effect_sdlu("sdlu", R.drawable.sdlu, "effect/normal/sdlu.bundle", 4, Effect.EFFECT_TYPE_STICKER, 0),

    /**
     * AR面具
     */
    Effect_bluebird("bluebird", R.drawable.bluebird, "effect/ar/bluebird.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_lanhudie("lanhudie", R.drawable.lanhudie, "effect/ar/lanhudie.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_fenhudie("fenhudie", R.drawable.fenhudie, "effect/ar/fenhudie.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_tiger_huang("tiger_huang", R.drawable.tiger_huang, "effect/ar/tiger_huang.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_tiger_bai("tiger_bai", R.drawable.tiger_bai, "effect/ar/tiger_bai.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_baozi("baozi", R.drawable.baozi, "effect/ar/baozi.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_tiger("tiger", R.drawable.tiger, "effect/ar/tiger.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    Effect_xiongmao("xiongmao", R.drawable.xiongmao, "effect/ar/xiongmao.bundle", 4, Effect.EFFECT_TYPE_AR_MASK, 0),
    /**
     * 表情识别
     */
    Effect_future_warrior("future_warrior", R.drawable.future_warrior, "effect/expression/future_warrior.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.future_warrior),
    Effect_jet_mask("jet_mask", R.drawable.jet_mask, "effect/expression/jet_mask.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.jet_mask),
    Effect_sdx2("sdx2", R.drawable.sdx2, "effect/expression/sdx2.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.sdx2),
    Effect_luhantongkuan_ztt_fu("luhantongkuan_ztt_fu", R.drawable.luhantongkuan_ztt_fu, "effect/expression/luhantongkuan_ztt_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.luhantongkuan_ztt_fu),
    Effect_qingqing_ztt_fu("qingqing_ztt_fu", R.drawable.qingqing_ztt_fu, "effect/expression/qingqing_ztt_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.qingqing_ztt_fu),
    Effect_xiaobianzi_zh_fu("xiaobianzi_zh_fu", R.drawable.xiaobianzi_zh_fu, "effect/expression/xiaobianzi_zh_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.xiaobianzi_zh_fu),
    Effect_xiaoxueshen_ztt_fu("xiaoxueshen_ztt_fu", R.drawable.xiaoxueshen_ztt_fu, "effect/expression/xiaoxueshen_ztt_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION, R.string.xiaoxueshen_ztt_fu),
    /**
     * 人像分割
     */
    Effect_boy_friend1("boy_friend1", R.drawable.demo_icon_boyfriend_01, "effect/segment/boyfriend1.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    Effect_boy_friend3("boy_friend3", R.drawable.demo_icon_boyfriend_02, "effect/segment/boyfriend3.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    Effect_boy_friend2("boy_friend2", R.drawable.demo_icon_boyfriend_03, "effect/segment/boyfriend2.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    Effect_hez_ztt_fu("hez_ztt_fu", R.drawable.hez_ztt_fu, "effect/segment/hez_ztt_fu.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, R.string.hez_ztt_fu),
    Effect_gufeng_zh_fu("gufeng_zh_fu", R.drawable.gufeng_zh_fu, "effect/segment/gufeng_zh_fu.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    Effect_xiandai_ztt_fu("xiandai_ztt_fu", R.drawable.xiandai_ztt_fu, "effect/segment/xiandai_ztt_fu.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    Effect_sea_lm_fu("sea_lm_fu", R.drawable.sea_lm_fu, "effect/segment/sea_lm_fu.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    Effect_ice_lm_fu("ice_lm_fu", R.drawable.ice_lm_fu, "effect/segment/ice_lm_fu.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_SEGMENT, 0),
    /**
     * 手势识别
     */
    Effect_ctrl_rain("ctrl_rain", R.drawable.ctrl_rain, "effect/gesture/ctrl_rain.bundle", 4, Effect.EFFECT_TYPE_GESTURE_RECOGNITION, R.string.push_hand),
    Effect_ctrl_snow("ctrl_snow", R.drawable.ctrl_snow, "effect/gesture/ctrl_snow.bundle", 4, Effect.EFFECT_TYPE_GESTURE_RECOGNITION, R.string.push_hand),
    Effect_ctrl_flower("ctrl_flower", R.drawable.ctrl_flower, "effect/gesture/ctrl_flower.bundle", 4, Effect.EFFECT_TYPE_GESTURE_RECOGNITION, R.string.push_hand),
    Effect_fu_lm_koreaheart("fu_lm_koreaheart", R.drawable.fu_lm_koreaheart, "effect/gesture/ssd_thread_korheart.bundle", 4, Effect.EFFECT_TYPE_GESTURE_RECOGNITION, R.string.fu_lm_koreaheart),
    Effect_ssd_thread_six("ssd_thread_six", R.drawable.ssd_thread_six, "effect/gesture/ssd_thread_six.bundle", 4, Effect.EFFECT_TYPE_GESTURE_RECOGNITION, R.string.ssd_thread_six),
    Effect_ssd_thread_cute("ssd_thread_cute", R.drawable.ssd_thread_cute, "effect/gesture/ssd_thread_cute.bundle", 4, Effect.EFFECT_TYPE_GESTURE_RECOGNITION, R.string.ssd_thread_cute),
    /**
     * Animoji
     */
    Effect_qgirl_Animoji("qgirl_Animoji", R.drawable.qgirl, "effect/animoji/qgirl.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_frog_Animoji("frog_Animoji", R.drawable.frog_st_animoji, "effect/animoji/frog_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_huangya_Animoji("huangya_Animoji", R.drawable.huangya_animoji, "effect/animoji/huangya_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_hetun_Animoji("hetun_Animoji", R.drawable.hetun_animoji, "effect/animoji/hetun_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_douniuquan_Animoji("douniuquan_Animoji", R.drawable.douniuquan_animoji, "effect/animoji/douniuquan_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_hashiqi_Animoji("hashiqi_Animoji", R.drawable.hashiqi_animoji, "effect/animoji/hashiqi_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_baimao_Animoji("baimao_Animoji", R.drawable.baimao_animoji, "effect/animoji/baimao_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    Effect_kuloutou_Animoji("kuloutou_Animoji", R.drawable.kuloutou_animoji, "effect/animoji/kuloutou_Animoji.bundle", 4, Effect.EFFECT_TYPE_ANIMOJI, 0),
    /**
     * 哈哈镜
     */
    Effect_facewarp2("facewarp2", R.drawable.facewarp2, "effect/facewarp/facewarp2.bundle", 4, Effect.EFFECT_TYPE_FACE_WARP, 0),
    Effect_facewarp3("facewarp3", R.drawable.facewarp3, "effect/facewarp/facewarp3.bundle", 4, Effect.EFFECT_TYPE_FACE_WARP, 0),
    Effect_facewarp4("facewarp4", R.drawable.facewarp4, "effect/facewarp/facewarp4.bundle", 4, Effect.EFFECT_TYPE_FACE_WARP, 0),
    Effect_facewarp5("facewarp5", R.drawable.facewarp5, "effect/facewarp/facewarp5.bundle", 4, Effect.EFFECT_TYPE_FACE_WARP, 0),
    Effect_facewarp6("facewarp6", R.drawable.facewarp6, "effect/facewarp/facewarp6.bundle", 4, Effect.EFFECT_TYPE_FACE_WARP, 0),
    /**
     * 音乐滤镜
     */
    Effect_douyin_old("douyin_01", R.drawable.douyin_old, "effect/musicfilter/douyin_01.bundle", 4, Effect.EFFECT_TYPE_MUSIC_FILTER, 0),
    Effect_douyin("douyin_02", R.drawable.douyin, "effect/musicfilter/douyin_02.bundle", 4, Effect.EFFECT_TYPE_MUSIC_FILTER, 0),
    /**
     * 搞笑大头
     */
    EFFECT_BIG_HEAD_FACEWARP_1("big_head_face_warp_1", R.drawable.demo_icon_big_head, "effect/big_head/big_head.bundle", 1, Effect.EFFECT_TYPE_BIG_HEAD, 0),
    EFFECT_BIG_HEAD_FACEWARP_2("big_head_face_warp_2", R.drawable.demo_icon_husky_face, "effect/big_head/big_head_facewarp2.bundle", 1, Effect.EFFECT_TYPE_BIG_HEAD, 0),
    EFFECT_BIG_HEAD_FACEWARP_3("big_head_face_warp_3", R.drawable.demo_icon_sausage_mouth, "effect/big_head/big_head_facewarp4.bundle", 1, Effect.EFFECT_TYPE_BIG_HEAD, 0),
    EFFECT_BIG_HEAD_FACEWARP_4("big_head_face_warp_4", R.drawable.demo_icon_blush, "effect/big_head/big_head_facewarp5.bundle", 1, Effect.EFFECT_TYPE_BIG_HEAD, 0),
    EFFECT_BIG_HEAD_FACEWARP_5("big_head_face_warp_5", R.drawable.demo_icon_dark_circles, "effect/big_head/big_head_facewarp6.bundle", 1, Effect.EFFECT_TYPE_BIG_HEAD, 0),
    EFFECT_BIG_HEAD_FACEWARP_6("big_head_face_warp_6", R.drawable.demo_icon_smiling_head, "effect/big_head/big_head_smile.bundle", 1, Effect.EFFECT_TYPE_BIG_HEAD, R.string.xiaobianzi_zh_fu),
    /**
     * 渐变美发
     */
    Hair_Gradient_01("Gradient_Hair_01", R.drawable.icon_gradualchangehair_01, "", 4, Effect.EFFECT_TYPE_HAIR_GRADIENT, 0),
    Hair_Gradient_02("Gradient_Hair_02", R.drawable.icon_gradualchangehair_02, "", 4, Effect.EFFECT_TYPE_HAIR_GRADIENT, 0),
    Hair_Gradient_04("Gradient_Hair_03", R.drawable.icon_gradualchangehair_03, "", 4, Effect.EFFECT_TYPE_HAIR_GRADIENT, 0),
    Hair_Gradient_05("Gradient_Hair_04", R.drawable.icon_gradualchangehair_04, "", 4, Effect.EFFECT_TYPE_HAIR_GRADIENT, 0),
    Hair_Gradient_06("Gradient_Hair_05", R.drawable.icon_gradualchangehair_05, "", 4, Effect.EFFECT_TYPE_HAIR_GRADIENT, 0),
    /**
     * 普通美发
     */
    Hair_01("Hair_01", R.drawable.icon_beautymakeup_hairsalon_01, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_02("Hair_02", R.drawable.icon_beautymakeup_hairsalon_02, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_03("Hair_03", R.drawable.icon_beautymakeup_hairsalon_03, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_04("Hair_04", R.drawable.icon_beautymakeup_hairsalon_04, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_05("Hair_05", R.drawable.icon_beautymakeup_hairsalon_05, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_06("Hair_06", R.drawable.icon_beautymakeup_hairsalon_06, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_07("Hair_07", R.drawable.icon_beautymakeup_hairsalon_07, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    Hair_08("Hair_08", R.drawable.icon_beautymakeup_hairsalon_08, "", 4, Effect.EFFECT_TYPE_HAIR_NORMAL, 0),
    /**
     * 动作识别
     */
    ACTION_GAME("action_recognition_game", 0, "effect/actiongame_android.bundle", 1, Effect.EFFECT_TYPE_ACTION_RECOGNITION, 0),
    /**
     * PTA
     */
    PTA_GIRL("pta_girl", R.drawable.demo_icon_avatar_female, "girl/", 1, Effect.EFFECT_TYPE_PTA, 0),
    PTA_BOY("pta_boy", R.drawable.demo_icon_avatar_male, "boy/", 1, Effect.EFFECT_TYPE_PTA, 0),
    /**
     * 绿幕抠像
     */
    BG_SEG_GREEN("bg_seg_green", 0, "bg_seg_green/green_screen.bundle", 1, Effect.EFFECT_TYPE_BG_SEG_GREEN, 0);

    private String bundleName;
    private int iconId;
    private String bundlePath;
    private int maxFace;
    private int type;
    private int descId;

    EffectEnum(String name, int iconId, String bundlePath, int maxFace, int type, int descId) {
        this.bundleName = name;
        this.iconId = iconId;
        this.bundlePath = bundlePath;
        this.maxFace = maxFace;
        this.type = type;
        this.descId = descId;
    }

    public Effect effect() {
        return new Effect(bundleName, iconId, bundlePath, maxFace, type, descId);
    }

    public static ArrayList<Effect> getEffectsByEffectType(int effectType) {
        EffectEnum[] values = EffectEnum.values();
        ArrayList<Effect> effects = new ArrayList<>(values.length + 1);
        if (effectType != Effect.EFFECT_TYPE_PTA) {
            effects.add(EffectNone.effect());
        }
        for (EffectEnum e : values) {
            if (e.type == effectType) {
                effects.add(e.effect());
            }
        }
        return effects;
    }
}
