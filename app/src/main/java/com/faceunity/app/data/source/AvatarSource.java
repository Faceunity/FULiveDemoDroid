package com.faceunity.app.data.source;

import com.faceunity.app.R;
import com.faceunity.core.avatar.avatar.Color;
import com.faceunity.core.avatar.avatar.TransForm;
import com.faceunity.core.avatar.model.Avatar;
import com.faceunity.core.avatar.model.Scene;
import com.faceunity.core.entity.FUAnimationData;
import com.faceunity.core.entity.FUAvatarAnimFilterParams;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.entity.FUCoordinate3DData;
import com.faceunity.core.entity.FUTranslationScale;
import com.faceunity.core.entity.FUVisibleBundleData;
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

    /**
     * 构造成员列表
     *
     * @return
     */
    public static ArrayList<AvatarBean> buildMembers() {
        ArrayList<AvatarBean> avatarBeans = new ArrayList<>();
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
    public static Scene buildSceneModel(Avatar avatar) {
        FUBundleData controlBundle = new FUBundleData(BUNDLE_AVATAR_CONTROLLER);
        FUBundleData avatarConfig = new FUBundleData(BUNDLE_AVATAR_CONFIG);
        Scene sceneModel = new Scene(controlBundle, avatarConfig);
        sceneModel.addAvatar(avatar);
        sceneModel.processorConfig.setEnableHumanProcessor(true);
        return sceneModel;
    }

    /**
     * 获取男孩对象
     *
     * @return
     */
    public static Avatar buildBoyData(boolean isFull) {
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
        int[] invisibleList = {2,3,4};
        components.add(new FUVisibleBundleData("",invisibleList,""));
        ArrayList<FUAnimationData> animations = buildAnimations();
        Avatar model = new Avatar(components);
        for (FUAnimationData animationData:animations){
            model.animation.addAnimation(animationData);
        }
        model.color.setColor(Color.Skin,new FUColorRGBData(227.0,158.0,132.0));
        TransForm avatarTransForm = model.transForm;
        avatarTransForm.setInstanceEnableHumanAnimDriver(true);
        avatarTransForm.setPosition(isFull ? new FUCoordinate3DData(0.0, 58.14, -618.94) : new FUCoordinate3DData(0.0, 11.76, -183.89));
        return model;
    }

    /**
     * 获取女孩对象
     *
     * @return
     */
    public static Avatar buildGirlData(boolean isFull) {
        String ptaGirlDir = "pta/girl/";
        ArrayList<FUBundleData> components = new ArrayList();
        components.add(new FUBundleData(ptaGirlDir + "head.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "midBody_female.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "female_hair_23.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "toushi_5.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "taozhuang_12.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "facemakeup_3.bundle"));
        components.add(new FUBundleData(ptaGirlDir + "xiezi_danxie.bundle"));
        int[] invisibleList = {2,3,4};
        components.add(new FUVisibleBundleData("",invisibleList,""));
        ArrayList<FUAnimationData> animations = buildAnimations();
        Avatar model = new Avatar(components );
        for (FUAnimationData animationData:animations){
            model.animation.addAnimation(animationData);
        }
        model.color.setColor(Color.Skin,new FUColorRGBData(255.0,202.0,186.0));
        TransForm avatarTransForm = model.transForm;
        avatarTransForm.setInstanceEnableHumanAnimDriver(true);
        avatarTransForm.setPosition(isFull ? new FUCoordinate3DData(0.0, 58.14, -618.94) : new FUCoordinate3DData(0.0, 11.76, -183.89));
        return model;
    }

    /**
     * 外部传入组件和动画构建
     *
     * @param strComponents 组件bundle
     * @param strAnimations 动画bundle
     * @return
     */
    public static Avatar buildAvatarData(ArrayList<String> strComponents, ArrayList<String> strAnimations) {
        ArrayList<FUBundleData> components = new ArrayList();
        for (String component : strComponents) {
            components.add(new FUBundleData(component));
        }

        Avatar model = new Avatar(components);
        for (String animation : strAnimations) {
            model.animation.addAnimation(new FUAnimationData(new FUBundleData(animation)));
        }
        //位置等avatar基本参数构建
        TransForm avatarTransForm = model.transForm;
        avatarTransForm.setPosition(new FUCoordinate3DData(20, 45, -618.94));
        avatarTransForm.setTranslationScale(new FUTranslationScale(0.0f,0.0f,0.0f));
        avatarTransForm.setInstanceEnableHumanAnimDriver(true);
        model.animation.setHumanProcessorSetAvatarAnimFilterParams(new FUAvatarAnimFilterParams(8, 0.09f, 0.120f));
        return model;
    }

    /**
     * 构造动画参数
     *
     * @return
     */
    public static ArrayList<FUAnimationData> buildAnimations() {
        String animDir = "pta/gesture/";
        ArrayList<FUAnimationData> animations = new ArrayList();
        animations.add(new FUAnimationData(new FUBundleData(animDir + "anim_idle.bundle")));
        return animations;
    }

    public static void setSceneBackGround(Scene sceneModel, boolean hasBackGround) {
        sceneModel.setBackgroundBundle(hasBackGround ? new FUBundleData(BUNDLE_AVATAR_BACKGROUND) : null);
    }
}
