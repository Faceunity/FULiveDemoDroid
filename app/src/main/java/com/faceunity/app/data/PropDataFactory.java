package com.faceunity.app.data;


import androidx.annotation.NonNull;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.PropSource;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.arMask.ARMask;
import com.faceunity.core.model.prop.bigHead.BigHead;
import com.faceunity.core.model.prop.expression.ExpressionRecognition;
import com.faceunity.core.model.prop.faceWarp.FaceWarp;
import com.faceunity.core.model.prop.gesture.GestureRecognition;
import com.faceunity.core.model.prop.sticker.Sticker;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.ui.entity.PropBean;
import com.faceunity.ui.infe.AbstractPropDataFactory;

import java.util.ArrayList;

/**
 * DESC：道具业务工厂：道具贴图、AR面具、搞笑大头、表情识别、哈哈镜、手势识别
 * Created on 2021/3/2
 */
public class PropDataFactory extends AbstractPropDataFactory {

    public interface PropListener {

        void onItemSelected(PropBean bean);

    }

    /*渲染控制器*/
    private final FURenderKit mFURenderKit = FURenderKit.getInstance();
    /*道具列表*/
    private final ArrayList<PropBean> propBeans;

    /*默认选中下标*/
    private int currentPropIndex;
    /*当前道具*/
    public Prop currentProp;
    /*回调接口*/
    private final PropListener mPropListener;
    /*道具类型*/
    private final int propType;


    public PropDataFactory(PropListener listener, int type, int index) {
        mPropListener = listener;
        propType = type;
        currentPropIndex = index;
        propBeans = PropSource.buildPropBeans(type);
    }


    /**
     * 获取当前选中下标
     *
     * @return
     */
    @Override
    public int getCurrentPropIndex() {
        return currentPropIndex;
    }

    /**
     * 设置当前选中下标
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
    @NonNull
    public ArrayList<PropBean> getPropBeans() {
        return propBeans;
    }


    @Override
    public void onItemSelected(PropBean bean) {
        onPropSelected(bean);
        mPropListener.onItemSelected(bean);
    }


    /**
     * 其他道具
     *
     * @param bean
     */
    private void onPropSelected(PropBean bean) {
        String path = bean.getPath();
        if (path == null || path.trim().length() == 0) {
            mFURenderKit.getPropContainer().removeAllProp();
            currentProp = null;
            return;
        }
        Prop prop = null;
        switch (propType) {
            case FunctionEnum.STICKER:
                prop = new Sticker(new FUBundleData(path));
                break;
            case FunctionEnum.AR_MASK:
                prop = new ARMask(new FUBundleData(path));
                break;
            case FunctionEnum.BIG_HEAD:
                prop = new BigHead(new FUBundleData(path));
                break;
            case FunctionEnum.EXPRESSION_RECOGNITION:
                prop = new ExpressionRecognition(new FUBundleData(path));
                break;
            case FunctionEnum.FACE_WARP:
                prop = new FaceWarp(new FUBundleData(path));
                break;
            case FunctionEnum.GESTURE_RECOGNITION:
                prop = new GestureRecognition(new FUBundleData(path));
                break;

        }
        mFURenderKit.getPropContainer().replaceProp(currentProp, prop);
        currentProp = prop;
    }


    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        if (propType == FunctionEnum.GESTURE_RECOGNITION) {
            mFURenderKit.getFUAIController().loadAIProcessor(DemoConfig.BUNDLE_AI_HAND, FUAITypeEnum.FUAITYPE_HANDGESTURE);
        }
        if (propType == FunctionEnum.BIG_HEAD) {
            mFURenderKit.getFUAIController().setMaxFaces(1);
        } else {
            mFURenderKit.getFUAIController().setMaxFaces(4);
        }
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        PropBean propBean = propBeans.get(currentPropIndex);
        onItemSelected(propBean);
    }

    /**
     * 结束需要释放AI驱动
     */
    public void releaseAIProcessor() {
        if (propType == FunctionEnum.GESTURE_RECOGNITION) {
            mFURenderKit.getFUAIController().releaseAIProcessor(FUAITypeEnum.FUAITYPE_HANDGESTURE);
        }
    }
}
