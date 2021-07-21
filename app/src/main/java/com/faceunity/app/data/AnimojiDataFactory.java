package com.faceunity.app.data;

import com.faceunity.app.data.source.AnimojiSource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.animationFilter.AnimationFilter;
import com.faceunity.core.model.animationFilter.AnimationFilterTypeEnum;
import com.faceunity.core.model.antialiasing.Antialiasing;
import com.faceunity.core.model.facebeauty.FaceBeauty;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.PropContainer;
import com.faceunity.core.model.prop.animoji.Animoji;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.ui.entity.AnimationFilterBean;
import com.faceunity.ui.entity.AnimojiBean;
import com.faceunity.ui.infe.AbstractAnimojiDataFactory;

import java.util.ArrayList;

/**
 * DESC：Animoji业务工厂
 * Created on 2021/3/3
 */
public class AnimojiDataFactory extends AbstractAnimojiDataFactory {

    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    private FUAIKit mFUAIKit = FUAIKit.getInstance();
    /*3D抗锯齿*/
    public final Antialiasing antialiasing;
    /*动漫滤镜模型*/
    public final AnimationFilter animationFilter;
    /*当前选中贴图模型*/
    private Prop currentAnimoji;
    /*当前选中下标*/
    private int currentAnimojiIndex;
    /*当前滤镜下标*/
    private int currentFilterIndex;
    /*Animoji数据*/
    private ArrayList<AnimojiBean> animojiBeans;
    /*Animoji滤镜数据*/
    private ArrayList<AnimationFilterBean> animationFilterBeans;


    /**
     * 构造 AnimojiDataFactory
     *
     * @param animojiIndex 贴图下标
     * @param filterIndex  滤镜下标
     */
    public AnimojiDataFactory(int animojiIndex, int filterIndex) {
        antialiasing = new Antialiasing(new FUBundleData(DemoConfig.BUNDLE_ANTI_ALIASING));
        animationFilter = new AnimationFilter(new FUBundleData(DemoConfig.BUNDLE_ANIMATION_FILTER));
        currentAnimojiIndex = animojiIndex;
        currentFilterIndex = filterIndex;
        animojiBeans = AnimojiSource.buildAnimojis();
        animationFilterBeans = AnimojiSource.buildFilters();
    }


    /**
     * 动漫贴图列表
     *
     * @return
     */
    @Override
    public ArrayList<AnimojiBean> getAnimojis() {
        return animojiBeans;
    }

    /**
     * 动漫滤镜列表
     *
     * @return
     */
    @Override
    public ArrayList<AnimationFilterBean> getFilters() {
        return animationFilterBeans;
    }

    /**
     * 当前选中动漫贴图下标
     *
     * @return
     */
    @Override
    public int getCurrentAnimojiIndex() {
        return currentAnimojiIndex;
    }

    /**
     * 设置当前选中动漫贴图下标
     *
     * @return
     */
    @Override
    public void setCurrentAnimojiIndex(int currentAnimojiIndex) {
        this.currentAnimojiIndex = currentAnimojiIndex;
    }

    /**
     * 当前选中滤镜下标
     *
     * @return
     */
    @Override
    public int getCurrentFilterIndex() {
        return currentFilterIndex;
    }

    /**
     * 设置当前选中动漫贴图下标
     *
     * @return
     */
    @Override
    public void setCurrentFilterIndex(int currentFilterIndex) {
        this.currentFilterIndex = currentFilterIndex;
    }

    /**
     * 设置选中贴图
     *
     * @param bean
     */
    @Override
    public void onAnimojiSelected(AnimojiBean bean) {
        PropContainer propContainer = mFURenderKit.getPropContainer();
        String path = bean.getPath();
        Prop prop = null;
        if (path != null && path.trim().length() > 0) {
            prop = new Animoji(new FUBundleData(path));
        }
        propContainer.replaceProp(currentAnimoji, prop);
        currentAnimoji = prop;
    }

    /**
     * 设置选中滤镜
     *
     * @param data
     */
    @Override
    public void onFilterSelected(AnimationFilterBean data) {
        animationFilter.setStyle(data.getStyle());
    }

    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_TONGUE, FUAITypeEnum.FUAITYPE_TONGUETRACKING);
        mFUAIKit.setMaxFaces(4);
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mFURenderKit.setAntialiasing(antialiasing);
        mFURenderKit.setAnimationFilter(animationFilter);
        animationFilter.setStyle(animationFilterBeans.get(currentFilterIndex).getStyle());
        onAnimojiSelected(animojiBeans.get(currentAnimojiIndex));
    }

    /**
     * 结束需要释放AI驱动
     */
    public void releaseAIProcessor() {
        mFUAIKit.releaseAIProcessor(FUAITypeEnum.FUAITYPE_TONGUETRACKING);
    }
}
