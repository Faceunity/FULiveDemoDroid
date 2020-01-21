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
    EffectNone("none", R.drawable.ic_delete_all, "none", 1, Effect.EFFECT_TYPE_NONE, 0),
    /**
     * 道具贴纸
     */
    Effect_sdlu("sdlu", R.drawable.sdlu, "effect/normal/sdlu.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    Effect_daisypig("daisypig", R.drawable.daisypig, "effect/normal/daisypig.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    Effect_fashi("fashi", R.drawable.fashi, "effect/normal/fashi.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    Effect_chri1("chri1", R.drawable.chri1, "effect/normal/chri1.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    Effect_xueqiu_lm_fu("xueqiu_lm_fu", R.drawable.xueqiu_lm_fu, "effect/normal/xueqiu_lm_fu.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    Effect_wobushi("wobushi", R.drawable.wobushi, "effect/normal/wobushi.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    Effect_gaoshiqing("gaoshiqing", R.drawable.gaoshiqing, "effect/normal/gaoshiqing.bundle", 4, Effect.EFFECT_TYPE_NORMAL, 0),
    /**
     * AR面具
     */
    Effect_bluebird("bluebird", R.drawable.bluebird, "effect/ar/bluebird.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_lanhudie("lanhudie", R.drawable.lanhudie, "effect/ar/lanhudie.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_fenhudie("fenhudie", R.drawable.fenhudie, "effect/ar/fenhudie.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_tiger_huang("tiger_huang", R.drawable.tiger_huang, "effect/ar/tiger_huang.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_tiger_bai("tiger_bai", R.drawable.tiger_bai, "effect/ar/tiger_bai.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_afd("afd", R.drawable.afd, "effect/ar/afd.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_baozi("baozi", R.drawable.baozi, "effect/ar/baozi.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_tiger("tiger", R.drawable.tiger, "effect/ar/tiger.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    Effect_xiongmao("xiongmao", R.drawable.xiongmao, "effect/ar/xiongmao.bundle", 4, Effect.EFFECT_TYPE_AR, 0),
    /**
     * 表情识别
     */
    Effect_future_warrior("future_warrior", R.drawable.future_warrior, "effect/expression/future_warrior.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.future_warrior),
    Effect_jet_mask("jet_mask", R.drawable.jet_mask, "effect/expression/jet_mask.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.jet_mask),
    Effect_sdx2("sdx2", R.drawable.sdx2, "effect/expression/sdx2.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.sdx2),
    Effect_luhantongkuan_ztt_fu("luhantongkuan_ztt_fu", R.drawable.luhantongkuan_ztt_fu, "effect/expression/luhantongkuan_ztt_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.luhantongkuan_ztt_fu),
    Effect_qingqing_ztt_fu("qingqing_ztt_fu", R.drawable.qingqing_ztt_fu, "effect/expression/qingqing_ztt_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.qingqing_ztt_fu),
    Effect_xiaobianzi_zh_fu("xiaobianzi_zh_fu", R.drawable.xiaobianzi_zh_fu, "effect/expression/xiaobianzi_zh_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.xiaobianzi_zh_fu),
    Effect_xiaoxueshen_ztt_fu("xiaoxueshen_ztt_fu", R.drawable.xiaoxueshen_ztt_fu, "effect/expression/xiaoxueshen_ztt_fu.bundle", 4, Effect.EFFECT_TYPE_EXPRESSION, R.string.xiaoxueshen_ztt_fu),
    /**
     * 背景分割
     */
    Effect_hez_ztt_fu("hez_ztt_fu", R.drawable.hez_ztt_fu, "effect/background/hez_ztt_fu.bundle", 1, Effect.EFFECT_TYPE_BACKGROUND, R.string.hez_ztt_fu),
    Effect_gufeng_zh_fu("gufeng_zh_fu", R.drawable.gufeng_zh_fu, "effect/background/gufeng_zh_fu.bundle", 1, Effect.EFFECT_TYPE_BACKGROUND, 0),
    Effect_xiandai_ztt_fu("xiandai_ztt_fu", R.drawable.xiandai_ztt_fu, "effect/background/xiandai_ztt_fu.bundle", 1, Effect.EFFECT_TYPE_BACKGROUND, 0),
    Effect_sea_lm_fu("sea_lm_fu", R.drawable.sea_lm_fu, "effect/background/sea_lm_fu.bundle", 1, Effect.EFFECT_TYPE_BACKGROUND, 0),
    Effect_ice_lm_fu("ice_lm_fu", R.drawable.ice_lm_fu, "effect/background/ice_lm_fu.bundle", 1, Effect.EFFECT_TYPE_BACKGROUND, 0),
    /**
     * 手势识别
     */
    Effect_ctrl_rain("ctrl_rain", R.drawable.ctrl_rain, "effect/gesture/ctrl_rain.bundle", 4, Effect.EFFECT_TYPE_GESTURE, R.string.push_hand),
    Effect_ctrl_snow("ctrl_snow", R.drawable.ctrl_snow, "effect/gesture/ctrl_snow.bundle", 4, Effect.EFFECT_TYPE_GESTURE, R.string.push_hand),
    Effect_ctrl_flower("ctrl_flower", R.drawable.ctrl_flower, "effect/gesture/ctrl_flower.bundle", 4, Effect.EFFECT_TYPE_GESTURE, R.string.push_hand),
    Effect_fu_lm_koreaheart("fu_lm_koreaheart", R.drawable.fu_lm_koreaheart, "effect/gesture/ssd_thread_korheart.bundle", 4, Effect.EFFECT_TYPE_GESTURE, R.string.fu_lm_koreaheart),
    Effect_ssd_thread_six("ssd_thread_six", R.drawable.ssd_thread_six, "effect/gesture/ssd_thread_six.bundle", 4, Effect.EFFECT_TYPE_GESTURE, R.string.ssd_thread_six),
    Effect_ssd_thread_cute("ssd_thread_cute", R.drawable.ssd_thread_cute, "effect/gesture/ssd_thread_cute.bundle", 4, Effect.EFFECT_TYPE_GESTURE, R.string.ssd_thread_cute),
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
     * 人像驱动
     */
    Effect_picasso_e1("picasso_e1", R.drawable.picasso_e1, "effect/portrait_drive/picasso_e1.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_DRIVE, 0),
    Effect_picasso_e2("picasso_e2", R.drawable.picasso_e2, "effect/portrait_drive/picasso_e2.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_DRIVE, 0),
    Effect_picasso_e3("picasso_e3", R.drawable.picasso_e3, "effect/portrait_drive/picasso_e3.bundle", 1, Effect.EFFECT_TYPE_PORTRAIT_DRIVE, 0),
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
     * Avatar 捏脸头部
     */
    AVATAR_HEAD("avatar_head", 0, "avatar/avatar_head.bundle", 1, Effect.EFFECT_TYPE_AVATAR, 0);

    private String bundleName;
    private int resId;
    private String path;
    private int maxFace;
    private int effectType;
    private int description;

    EffectEnum(String name, int resId, String path, int maxFace, int effectType, int description) {
        this.bundleName = name;
        this.resId = resId;
        this.path = path;
        this.maxFace = maxFace;
        this.effectType = effectType;
        this.description = description;
    }

    public String bundleName() {
        return bundleName;
    }

    public int resId() {
        return resId;
    }

    public String path() {
        return path;
    }

    public int maxFace() {
        return maxFace;
    }

    public int effectType() {
        return effectType;
    }

    public int description() {
        return description;
    }

    public Effect effect() {
        return new Effect(bundleName, resId, path, maxFace, effectType, description);
    }

    public static ArrayList<Effect> getEffectsByEffectType(int effectType) {
        ArrayList<Effect> effects = new ArrayList<>(16);
        effects.add(EffectNone.effect());
        for (EffectEnum e : EffectEnum.values()) {
            if (e.effectType == effectType) {
                effects.add(e.effect());
            }
        }
        return effects;
    }
}
