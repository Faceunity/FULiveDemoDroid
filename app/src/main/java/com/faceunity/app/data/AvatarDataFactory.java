package com.faceunity.app.data;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.AvatarSource;
import com.faceunity.core.avatar.model.PTAAvatar;
import com.faceunity.core.avatar.model.PTAScene;
import com.faceunity.core.avatar.scene.SceneHumanProcessor;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUCoordinate3DData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.antialiasing.Antialiasing;
import com.faceunity.ui.entity.AvatarBean;
import com.faceunity.ui.infe.AbstractAvatarDataFactory;

import java.util.ArrayList;

/**
 * DESC：
 * Created on 2021/3/30
 */
public class AvatarDataFactory extends AbstractAvatarDataFactory {

    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    /*3D抗锯齿*/
    public final Antialiasing antialiasing;

    /* 人物队列  */
    private ArrayList<AvatarBean> members;
    /* 当前选中人物下标  */
    private int currentMemberIndex;
    /* 驱动类型是否为全身  */
    private Boolean isHumanTrackSceneFull;

    /* 场景  */
    private PTAScene sceneModel;
    /* 男孩对象  */
    private PTAAvatar boyAvatarModel;
    /* 女孩对象  */
    private PTAAvatar girlAvatarModel;


    public AvatarDataFactory(int index, boolean isFull) {
        isHumanTrackSceneFull = isFull;
        currentMemberIndex = index;
        members = AvatarSource.buildMembers();
        antialiasing = new Antialiasing(new FUBundleData(DemoConfig.BUNDLE_ANTI_ALIASING));
        boyAvatarModel = AvatarSource.buildBoyData(isFull);
        girlAvatarModel = AvatarSource.buildGirlData(isFull);
        sceneModel = AvatarSource.buildSceneModel(index == 0 ? girlAvatarModel : boyAvatarModel);
    }


    /**
     * 获取人物队列
     *
     * @return
     */
    @Override
    public ArrayList<AvatarBean> getMembers() {
        return members;
    }

    /**
     * 获取当前选中人物下标
     *
     * @return
     */
    @Override
    public int getCurrentMemberIndex() {
        return currentMemberIndex;
    }

    /**
     * 设置当前人物选中下标
     *
     * @param index
     */
    @Override
    public void setCurrentMemberIndex(int index) {
        currentMemberIndex = index;
    }

    /**
     * 获取当前驱动类型
     *
     * @return
     */
    @Override
    public boolean isHumanTrackSceneFull() {
        return isHumanTrackSceneFull;
    }

    /**
     * 设置当前驱动类型
     *
     * @param isFull
     */
    @Override
    public void setHumanTrackSceneFull(boolean isFull) {
        isHumanTrackSceneFull = isFull;
        sceneModel.getMSceneHumanProcessor().setTrackScene(isFull ? SceneHumanProcessor.TrackScene.SceneFull : SceneHumanProcessor.TrackScene.SceneHalf);
        if (isFull) {
            boyAvatarModel.getMAvatarTransForm().setPosition(new FUCoordinate3DData(0.0, 58.14, -618.94));
            girlAvatarModel.getMAvatarTransForm().setPosition(new FUCoordinate3DData(0.0, 58.14, -618.94));
        } else {
            boyAvatarModel.getMAvatarTransForm().setPosition(new FUCoordinate3DData(0.0, 11.76, -183.89));
            girlAvatarModel.getMAvatarTransForm().setPosition(new FUCoordinate3DData(0.0, 11.76, -183.89));
        }
    }

    /**
     * 人物切换
     *
     * @param bean
     */
    @Override
    public void onMemberSelected(AvatarBean bean) {
        if (bean.getDes().equals(AvatarSource.GIRL)) {
            sceneModel.replaceAvatar(boyAvatarModel, girlAvatarModel);
        } else {
            sceneModel.replaceAvatar(girlAvatarModel, boyAvatarModel);
        }
    }


    public void bindCurrentRenderer() {
        mFURenderKit.getFUAIController().loadAIProcessor(DemoConfig.BUNDLE_AI_HUMAN, FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFURenderKit.getFUAIController().setMaxFaces(1);
        mFURenderKit.setAntialiasing(antialiasing);
        mFURenderKit.getAvatarContainer().addScene(sceneModel);
        sceneModel.getMSceneHumanProcessor().setEnableHumanProcessor(true);
        setHumanTrackSceneFull(isHumanTrackSceneFull);
    }

}
