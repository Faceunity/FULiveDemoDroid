package com.faceunity.app.data;

import android.view.MotionEvent;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.utils.net.StickerDownloadHelper;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.prop.sticker.FineSticker;
import com.faceunity.ui.control.FineStickerView;
import com.faceunity.ui.entity.net.FineStickerEntity;
import com.faceunity.ui.entity.net.FineStickerTagEntity;
import com.faceunity.ui.infe.AbstractFineStickerDataFactory;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/3/31 0031 15:28.
 * Author: xloger
 * Email:phoenix@xloger.com
 */
public class FineStickerDataFactory extends AbstractFineStickerDataFactory {

    /*渲染控制器*/
    private final FURenderKit mFURenderKit = FURenderKit.getInstance();
    /*当前选中道具模型*/
    private FineSticker currentProp;
    /*菜单视图*/
    private FineStickerView view;
    /*当前选中道具*/
    private FineStickerEntity.DocsBean currentSticker;

    public FineStickerDataFactory() {
        StickerDownloadHelper.getInstance().setCallback(downloadHelper);
    }

    /**
     * 绑定菜单视图
     *
     * @param view
     */
    public void bindView(FineStickerView view) {
        this.view = view;
    }

    public void acceptEvent() {
        StickerDownloadHelper.getInstance().setCallback(downloadHelper);

    }

    public void refuseEvent() {
        StickerDownloadHelper.getInstance().setCallback(null);
    }

    /**
     * 切换道具
     *
     * @param bean
     */
    @Override
    public void onItemSelected(FineStickerEntity.DocsBean bean) {
        mFURenderKit.getPropContainer().removeAllProp();
        currentProp = null;
        currentSticker = bean;
        if (bean!=null&&bean.getFilePath()!=null&&bean.getFilePath().trim().length()>0){
            adapterMaxFace();
            FineSticker prop = adapterBean(bean.getFilePath());
            mFURenderKit.getPropContainer().addProp(prop);
            currentProp = prop;
        }
    }


    /**
     * 根据adpater 调整业务模型
     * 0：维持竖屏
     * 1：仅限一人
     * 2：美妆道具
     * 3：点击事件
     * 4：翻转
     *
     * @param path
     * @return
     */
    private FineSticker adapterBean(String path) {
        String adapter = currentSticker.getTool().getAdapter();
        if (adapter == null || !adapter.contains("1")) {
            mFURenderKit.getFUAIController().setMaxFaces(4);
        } else {
            mFURenderKit.getFUAIController().setMaxFaces(1);
        }
        if (adapter != null && adapter.trim().length() > 0) {
            Boolean isFlipPoints = adapter.contains("2");
            Boolean is3DFlipH = adapter.contains("4");
            Boolean isClick = adapter.contains("3");
            return new FineSticker(new FUBundleData(path), isFlipPoints, is3DFlipH, isClick);
        } else {
            return new FineSticker(new FUBundleData(path));
        }
    }


    /**
     * 网络回调
     */
    private StickerDownloadHelper.Callback downloadHelper = new StickerDownloadHelper.Callback() {
        @Override
        public void onGetTags(String[] tags) {
            view.onGetTags(formatTag(tags));
        }

        @Override
        public void onGetList(String tag, FineStickerEntity fineSticker) {
            view.onGetToolList(new FineStickerTagEntity(tag), fineSticker);
        }

        @Override
        public void onDownload(FineStickerEntity.DocsBean entity) {
            view.onDownload(entity);
        }

        @Override
        public void onDownloadError(FineStickerEntity.DocsBean entity, String msg) {
            view.onDownloadError(entity, msg);
        }
    };


    /**
     * 获取标签列表
     *
     * @return
     */
    @NotNull
    @Override
    public List<FineStickerTagEntity> loadTagList() {
        String[] tags = StickerDownloadHelper.getInstance().tags();
        return formatTag(tags);
    }

    /**
     * 获取标签对应道具列表
     *
     * @param tag
     * @return
     */
    @NotNull
    @Override
    public FineStickerEntity loadStickerList(@NotNull FineStickerTagEntity tag) {
        return StickerDownloadHelper.getInstance().tools(tag.getTag());
    }

    /**
     * 下载道具
     *
     * @param docsBean
     */
    @Override
    public void downloadSticker(@NotNull FineStickerEntity.DocsBean docsBean) {
        StickerDownloadHelper.getInstance().download(docsBean);
    }


    /**
     * 数据转换
     *
     * @param tags
     * @return
     */
    private List<FineStickerTagEntity> formatTag(String[] tags) {
        List<FineStickerTagEntity> tagEntityList = new ArrayList<>(tags.length);
        for (int i = 0; i < tags.length; i++) {
            tagEntityList.add(new FineStickerTagEntity(tags[i]));
        }
        return tagEntityList;
    }

    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.getFUAIController().loadAIProcessor(DemoConfig.BUNDLE_AI_HUMAN, FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFURenderKit.getFUAIController().loadAIProcessor(DemoConfig.BUNDLE_AI_HAND, FUAITypeEnum.FUAITYPE_HANDGESTURE);
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mFURenderKit.getFUAIController().setMaxFaces(1);
        onItemSelected(currentSticker);
    }


    /**
     * 结束需要释放AI驱动
     */
    public void releaseAIProcessor() {
        mFURenderKit.getFUAIController().releaseAIProcessor(FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFURenderKit.getFUAIController().releaseAIProcessor(FUAITypeEnum.FUAITYPE_HANDGESTURE);
    }


    private void adapterMaxFace() {
        if (currentSticker == null) return;
        String adapter = currentSticker.getTool().getAdapter();
        if (adapter == null || !adapter.contains("1")) {
            mFURenderKit.getFUAIController().setMaxFaces(4);
        } else {
            mFURenderKit.getFUAIController().setMaxFaces(1);
        }
    }

    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && currentProp != null&&currentProp.isClick()) {
            currentProp.onClick();
        }
    }


}
