package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.core.avatar.avatar.AvatarTransForm;
import com.faceunity.core.avatar.model.PTAAvatar;
import com.faceunity.core.avatar.model.PTAScene;
import com.faceunity.core.entity.FUAvatarAnimFilterParams;
import com.faceunity.core.entity.FUAvatarOffset;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUCoordinate3DData;
import com.faceunity.core.entity.FUTranslationScale;
import com.faceunity.ui.entity.AvatarBean;

import java.io.File;
import java.util.ArrayList;

/**
 * DESC：Avatar数据构造
 * Created on 2021/3/30
 */
public class AvatarSource {
    // Avatar
    private static String BUNDLE_AVATAR_CONTROLLER = "graphics" + File.separator + "controller_cpp.bundle";
    private static String BUNDLE_AVATAR_CONFIG = "pta" + File.separator + "controller_config.bundle";
    private static String BUNDLE_AVATAR_BACKGROUND = "pta" + File.separator + "default_bg.bundle";

    public static String BOY = "boy";
    public static String GIRL = "girl";
    public static String BEAR = "bear";

    /**
     * 构造成员列表
     *
     * @return
     */
    public static ArrayList<AvatarBean> buildMembers() {
        ArrayList<AvatarBean> avatarBeans = new ArrayList<>();
        avatarBeans.add(new AvatarBean(R.mipmap.icon_avatar_bear, BEAR));
        avatarBeans.add(new AvatarBean(R.mipmap.icon_avatar_female, GIRL));
        avatarBeans.add(new AvatarBean(R.mipmap.icon_avatar_male, BOY));
        return avatarBeans;
    }

    /**
     * 构造场景
     *
     * @param avatar
     * @return
     */
    public static PTAScene buildSceneModel(PTAAvatar avatar) {
        FUBundleData controlBundle = new FUBundleData(BUNDLE_AVATAR_CONTROLLER);
        FUBundleData avatarConfig = new FUBundleData(BUNDLE_AVATAR_CONFIG);
        ArrayList<PTAAvatar> avatars = new ArrayList<PTAAvatar>();
        avatars.add(avatar);
        PTAScene sceneModel = new PTAScene(controlBundle, avatarConfig, avatars);
        sceneModel.getMSceneHumanProcessor().setEnableHumanProcessor(true);
        sceneModel.getMSceneHumanProcessor().setHumanProcessorTranslationScale(new FUTranslationScale(0.5f, 0f, 0.1f));
        return sceneModel;
    }

    /**
     * 获取男孩对象
     *
     * @return
     */
    public static PTAAvatar buildBoyData(boolean isFull) {
        String ptaBoyDir = "pta/boy/";
        ArrayList<FUBundleData> components = new ArrayList();
        components.add(new FUBundleData(ptaBoyDir + "head.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "midBody_male.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "male_hair_5.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "toushi_7.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "peishi_erding_2.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "waitao_3.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "kuzi_changku_5.bundle"));
        components.add(new FUBundleData(ptaBoyDir + "xiezi_tuoxie_2.bundle"));
        ArrayList<FUBundleData> animations = buildAnimations();
        PTAAvatar model = new PTAAvatar(components, animations);
        AvatarTransForm avatarTransForm = model.getMAvatarTransForm();
        avatarTransForm.setPosition(isFull ? new FUCoordinate3DData(0.0, 58.14, -618.94) : new FUCoordinate3DData(0.0, 11.76, -183.89));
        return model;
    }

    /**
     * 获取女孩对象
     *
     * @return
     */
    public static PTAAvatar buildGirlData(boolean isFull) {
        String ptaGirlDir = "pta/girl/";
        ArrayList<FUBundleData> components = new ArrayList();
        components.add(new FUBundleData(ptaGirlDir + "head.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "midBody_female.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "female_hair_23.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "toushi_5.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "taozhuang_12.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "facemakeup_3.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "xiezi_danxie.bundle"));
        ArrayList<FUBundleData> animations = buildAnimations();
        PTAAvatar model = new PTAAvatar(components, animations);
        AvatarTransForm avatarTransForm = model.getMAvatarTransForm();
        avatarTransForm.setPosition(isFull ? new FUCoordinate3DData(0.0, 58.14, -618.94) : new FUCoordinate3DData(0.0, 11.76, -183.89));
        return model;
    }

    /**
     * 获取熊对象
     *
     * @return
     */
    public static PTAAvatar buildBearData() {
        String ptaBearDir = "pta/bear/";
        ArrayList<FUBundleData> components = new ArrayList();
        components.add(new FUBundleData(ptaBearDir + "bear.bundle"));
        components.add(new FUBundleData(ptaBearDir + "bear_light.bundle"));
        ArrayList<FUBundleData> animations = buildBearAnimations();
        PTAAvatar model = new PTAAvatar(components, animations);
        AvatarTransForm avatarTransForm = model.getMAvatarTransForm();
        avatarTransForm.setHumanProcessorSetAvatarScale(1.15f);
        avatarTransForm.setHumanProcessorSetAvatarGlobalOffset(new FUAvatarOffset(0, 40, 0));
        avatarTransForm.setHumanProcessorSetAvatarAnimFilterParams(new FUAvatarAnimFilterParams(8, 0.09f, 0.120f));
        avatarTransForm.setPosition(new FUCoordinate3DData(20, 45, -618.94));
        return model;
    }

    /**
     * 构造动画参数
     *
     * @return
     */
    public static ArrayList<FUBundleData> buildAnimations() {
        String animDir = "pta/gesture/";
        ArrayList<FUBundleData> animations = new ArrayList();
        animations.add(new FUBundleData(animDir + "anim_idle.bundle"));
        animations.add(new FUBundleData(animDir + "anim_eight.bundle"));
        animations.add(new FUBundleData(animDir + "anim_fist.bundle"));
        animations.add(new FUBundleData(animDir + "anim_greet.bundle"));
        animations.add(new FUBundleData(animDir + "anim_gun.bundle"));
        animations.add(new FUBundleData(animDir + "anim_heart.bundle"));
        animations.add(new FUBundleData(animDir + "anim_hold.bundle"));
        animations.add(new FUBundleData(animDir + "anim_korheart.bundle"));
        animations.add(new FUBundleData(animDir + "anim_merge.bundle"));
        animations.add(new FUBundleData(animDir + "anim_ok.bundle"));
        animations.add(new FUBundleData(animDir + "anim_one.bundle"));
        animations.add(new FUBundleData(animDir + "anim_palm.bundle"));
        animations.add(new FUBundleData(animDir + "anim_rock.bundle"));
        animations.add(new FUBundleData(animDir + "anim_six.bundle"));
        animations.add(new FUBundleData(animDir + "anim_thumb.bundle"));
        animations.add(new FUBundleData(animDir + "anim_two.bundle"));
        return animations;
    }

    public static void setSceneBackGround(PTAScene sceneModel, boolean hasBackGround) {
        sceneModel.getMSceneBackground().setBackgroundBundle(hasBackGround ? new FUBundleData(BUNDLE_AVATAR_BACKGROUND) : null);
    }

    /**
     * 构造动画参数
     *
     * @return
     */
    public static ArrayList<FUBundleData> buildBearAnimations() {
        String animDir = "pta/gesture/";
        ArrayList<FUBundleData> animations = new ArrayList();
        animations.add(new FUBundleData(animDir + "anim_bear_smile.bundle"));
        return animations;
    }


}
