package com.faceunity.app.data;

import com.faceunity.app.data.source.PortraitSegmentSource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.bgSegCustom.BgSegCustom;
import com.faceunity.core.model.prop.humanOutline.HumanOutline;
import com.faceunity.core.model.prop.portraitSegment.PortraitSegment;
import com.faceunity.ui.entity.PropCustomBean;
import com.faceunity.ui.infe.AbstractPropCustomDataFactory;

import java.util.ArrayList;

/**
 * DESC：人像分割业务工厂
 * Created on 2021/3/2
 */
public class PortraitSegmentDataFactory extends AbstractPropCustomDataFactory {


    public interface PortraitSegmentListener {
        /**
         * 切换道具
         */
        void onItemSelected(PropCustomBean bean);

        /**
         * 添加自定义道具
         */
        void onCustomPropAdd();

        /**
         * 是否需要关闭检测人体回调提示
         *
         * @param needShow
         */
        void onProcessTrackChanged(boolean needShow);
    }


    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();

    /*道具列表*/
    private ArrayList<PropCustomBean> propCustomBeans;
    /*当前道具选中下标*/
    private int currentPropIndex;
    /*回调接口*/
    private final PortraitSegmentListener mPortraitSegmentListener;

    /*当前道具*/
    private Prop currentProp;

    /*人体描线*/
    private HumanOutline humanOutline;

    /*自定义人像分割*/
    private BgSegCustom bgSegCustom;


    public PortraitSegmentDataFactory(PortraitSegmentListener listener) {
        mPortraitSegmentListener = listener;
        propCustomBeans = PortraitSegmentSource.buildPropBeans();
    }

    public int getHumanOutLineIndex() {
        for (int i = 0; i < propCustomBeans.size(); i++) {
            if (propCustomBeans.get(i).getType() == FunctionEnum.HUMAN_OUTLINE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取当前道具下标
     *
     * @return
     */
    @Override
    public int getCurrentPropIndex() {
        return currentPropIndex;
    }

    /**
     * 设置当前道具下标
     *
     * @param currentPropIndex
     */
    @Override
    public void setCurrentPropIndex(int currentPropIndex) {
        this.currentPropIndex = currentPropIndex;
    }

    /**
     * 获取道具队列
     *
     * @return
     */
    @Override
    public ArrayList<PropCustomBean> getPropCustomBeans() {
        return propCustomBeans;
    }

    /**
     * 切换道具
     *
     * @param bean
     */
    public void onItemSelected(PropCustomBean bean) {
        mPortraitSegmentListener.onProcessTrackChanged(bean.getType() > 0);
        switch (bean.getType()) {
            case AbstractPropCustomDataFactory.TYPE_NONE:
                mFURenderKit.getPropContainer().removeAllProp();
                currentProp = null;
                break;
            case FunctionEnum.HUMAN_OUTLINE:
                if (humanOutline == null) {
                    humanOutline = PortraitSegmentSource.getHumanOutline(bean.getPath());
                }
                mFURenderKit.getPropContainer().replaceProp(currentProp, humanOutline);
                currentProp = humanOutline;
                break;
            case FunctionEnum.BG_SEG_CUSTOM:
                if (bgSegCustom == null) {
                    bgSegCustom = new BgSegCustom(new FUBundleData(bean.getPath()));
                }
                mFURenderKit.getPropContainer().replaceProp(currentProp, bgSegCustom);
                currentProp = bgSegCustom;
                break;
            case FunctionEnum.PORTRAIT_SEGMENT:
                PortraitSegment portraitSegment = new PortraitSegment(new FUBundleData(bean.getPath()));
                mFURenderKit.getPropContainer().replaceProp(currentProp, portraitSegment);
                currentProp = portraitSegment;
            default:
                break;
        }
        mPortraitSegmentListener.onItemSelected(bean);
    }

    /**
     * 添加道具
     */
    @Override
    public void onAddPropCustomBeanClick() {
        mPortraitSegmentListener.onCustomPropAdd();
    }


    /**
     * 获取当前道具模型
     *
     * @return
     */
    public Prop getCurrentProp() {
        return currentProp;
    }

    /**
     * 获取当前道具
     *
     * @return
     */
    public PropCustomBean getCurrentPropCustomBean() {
        return propCustomBeans.get(currentPropIndex);
    }

    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        FUAIKit.getInstance().setMaxFaces(4);
        FUAIKit.getInstance().setMaxHumans(4);
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        PropCustomBean propBean = propCustomBeans.get(currentPropIndex);
        onItemSelected(propBean);
    }

    /**
     * 结束需要释放AI驱动
     */
    public void releaseAIProcessor() {
        FUAIKit.getInstance().releaseAIProcessor(FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
    }
}
