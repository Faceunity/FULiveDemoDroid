package com.faceunity.app.data;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.AvatarSource;
import com.faceunity.core.avatar.model.Avatar;
import com.faceunity.core.avatar.model.Scene;
import com.faceunity.core.avatar.scene.ProcessorConfig;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUCoordinate3DData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.faceunity.FUSceneKit;
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
    private FUAIKit mFUAIKit = FUAIKit.getInstance();
    /*3D抗锯齿*/
    public final Antialiasing antialiasing;

    /* 人物队列  */
    private ArrayList<AvatarBean> members;
    /* 当前选中人物下标  */
    private int currentMemberIndex;
    /* 驱动类型是否为全身  */
    private Boolean isHumanTrackSceneFull;

    /* 场景  */
    private Scene sceneModel;
    /* 男孩对象  */
    private Avatar boyAvatarModel;
    /* 女孩对象  */
    private Avatar girlAvatarModel;
    /*当前对象*/
    private Avatar currentAvatarModel;


    public AvatarDataFactory(int index, boolean isFull) {
        isHumanTrackSceneFull = isFull;
        currentMemberIndex = index;
        members = AvatarSource.buildMembers();
        antialiasing = new Antialiasing(new FUBundleData(DemoConfig.BUNDLE_ANTI_ALIASING));
        boyAvatarModel = AvatarSource.buildBoyData(isFull);
        girlAvatarModel = AvatarSource.buildGirlData(isFull);

        if (index == 0) {
            currentAvatarModel = girlAvatarModel;
        } else if (index == 1) {
            currentAvatarModel = boyAvatarModel;
        }

        sceneModel = AvatarSource.buildSceneModel(currentAvatarModel);
        AvatarSource.setSceneBackGround(sceneModel, true);
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
        sceneModel.processorConfig.setTrackScene(isFull ? ProcessorConfig.TrackScene.SceneFull : ProcessorConfig.TrackScene.SceneHalf);
        if (isFull) {
            boyAvatarModel.transForm.setPosition(new FUCoordinate3DData(0.0, 58.14, -618.94));
            girlAvatarModel.transForm.setPosition(new FUCoordinate3DData(0.0, 58.14, -618.94));
        } else {
            boyAvatarModel.transForm.setPosition(new FUCoordinate3DData(0.0, 11.76, -183.89));
            girlAvatarModel.transForm.setPosition(new FUCoordinate3DData(0.0, 11.76, -183.89));
        }
    }

    /**
     * 人物切换
     *
     * @param bean
     */
    @Override
    public void onMemberSelected(AvatarBean bean) {
        if (mAvatarChoiceListener != null)
            mAvatarChoiceListener.choiceAvatar(bean);

        sceneModel.replaceAvatar(currentAvatarModel, bean.getDes().equals(AvatarSource.GIRL) ? girlAvatarModel : boyAvatarModel);
        currentAvatarModel = bean.getDes().equals(AvatarSource.GIRL) ? girlAvatarModel : boyAvatarModel;
    }

    public void bindCurrentRenderer() {
        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_HUMAN, FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFUAIKit.setMaxFaces(1);
        mFURenderKit.setAntialiasing(antialiasing);
        FUSceneKit.getInstance().addSceneGL(sceneModel);
        FUSceneKit.getInstance().setCurrentSceneGL(sceneModel);
        setHumanTrackSceneFull(isHumanTrackSceneFull);
    }

    public AvatarChoiceListener mAvatarChoiceListener;

    public interface AvatarChoiceListener {
        void choiceAvatar(AvatarBean avatarBean);
    }

    public void setAvatarChoiceListener(AvatarChoiceListener avatarChoiceListener) {
        this.mAvatarChoiceListener = avatarChoiceListener;
    }
}
