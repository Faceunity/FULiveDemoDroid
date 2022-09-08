package com.faceunity.app.data;

import android.util.Log;
import android.view.MotionEvent;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.data.source.AvatarSource;
import com.faceunity.app.utils.net.StickerDownloadHelper;
import com.faceunity.core.avatar.model.Avatar;
import com.faceunity.core.avatar.model.Scene;
import com.faceunity.core.avatar.scene.ProcessorConfig;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUTranslationScale;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.faceunity.FUSceneKit;
import com.faceunity.core.model.antialiasing.Antialiasing;
import com.faceunity.core.model.prop.sticker.FineSticker;
import com.faceunity.core.utils.FileUtils;
import com.faceunity.ui.control.FineStickerView;
import com.faceunity.ui.entity.net.FineStickerEntity;
import com.faceunity.ui.entity.net.FineStickerTagEntity;
import com.faceunity.ui.infe.AbstractFineStickerDataFactory;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2021/3/31 0031 15:28.
 * Author: xloger
 * Email:phoenix@xloger.com
 */
public class FineStickerDataFactory extends AbstractFineStickerDataFactory {
    private static final String TAG = "FineStickerDataFactory";

    /*渲染控制器*/
    private final FURenderKit mFURenderKit = FURenderKit.getInstance();
    private FUAIKit mFUAIKit = FUAIKit.getInstance();
    /*当前选中道具模型*/
    private FineSticker currentProp;
    /*菜单视图*/
    private FineStickerView view;
    /*当前选中道具*/
    private FineStickerEntity.DocsBean currentSticker;
    /*当前道具的类型 普通单bundle，avatar 默认为普通单bundle*/
    private BundleType mCurrentBundleType = BundleType.NORMAL_SINGLE_BUNDLE;

    //avatar相关
    /* 场景  */
    private Scene mSceneModel;
    /* 对象  */
    private Avatar mCurrentAvatarModel;
    /*3D抗锯齿*/
    public Antialiasing antialiasing;

    //avatar用于区分bundle类型的关键字
    private final String COMPONENTS_STR = "components";
    private final String ANIM_STR = "anim";
    private final String INFO = "info.json";
    private final String ZIP = ".zip";
    private final String AVATAR = "avatar";

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
        currentSticker = bean;
        if (bean != null && bean.getTool().getBundle().getUid().endsWith(ZIP)) {
            //复合道具
            if (AVATAR.equals(bean.getTool().getCategory())) {
                //avatar道具
                if (mCurrentBundleType == BundleType.NORMAL_SINGLE_BUNDLE) {
                    //移除旧的道具
                    mFURenderKit.getPropContainer().removeAllProp();
                    currentProp = null;
                    adapterMaxFace();
                }
                if (bean.getUnZipFilePaths() != null) {
                    buildAvatarModel(bean);
                    //当前为 avatar bundle
                    mCurrentBundleType = BundleType.AVATAR_BUNDLE;
                }
            }
        } else {
            //普通道具
            if (mCurrentBundleType == BundleType.NORMAL_SINGLE_BUNDLE) {
                mFURenderKit.getPropContainer().removeAllProp();
                currentProp = null;
                if (bean != null && bean.getFilePath() != null && bean.getFilePath().trim().length() > 0) {
                    adapterMaxFace();
                    FineSticker prop = adapterBean(bean.getFilePath());
                    mFURenderKit.getPropContainer().addProp(prop);
                    currentProp = prop;
                }
            } else if (mCurrentBundleType == BundleType.AVATAR_BUNDLE) {
                //关闭avatar 相关的东西
                if (mSceneModel != null && mCurrentAvatarModel != null) {
                    mSceneModel.removeAvatar(mCurrentAvatarModel);
                    FUSceneKit.getInstance().removeScene(mSceneModel);
                    mSceneModel = null;
                    mCurrentAvatarModel = null;
                }

                //设置道具
                if (bean != null && bean.getFilePath() != null && bean.getFilePath().trim().length() > 0) {
                    adapterMaxFace();
                    FineSticker prop = adapterBean(bean.getFilePath());
                    mFURenderKit.getPropContainer().addProp(prop);
                    currentProp = prop;
                }
            }

            //当前为普通bundle
            mCurrentBundleType = BundleType.NORMAL_SINGLE_BUNDLE;
        }

        if (mBundleTypeListener != null) {
            mBundleTypeListener.bundleType(mCurrentBundleType);
        }
    }

    /**
     * 构建avatar -> scene
     * @param bean
     */
    private void buildAvatarModel(FineStickerEntity.DocsBean bean) {
        //启动avatar基本逻辑
        if (antialiasing == null)
            antialiasing = new Antialiasing(new FUBundleData(DemoConfig.BUNDLE_ANTI_ALIASING));
        //3d抗锯齿
        mFURenderKit.setAntialiasing(antialiasing);
        //判断是avatar 控件 -> 查看是否有json文件
        ArrayList<String> unZipFilePaths = bean.getUnZipFilePaths();
        int hasJson = unZipFilePaths.indexOf(INFO);

        ArrayList<String> strComponents = new ArrayList<>();//组件Bundle
        ArrayList<String> strAnimations = new ArrayList<>();//动画Bundle
        if (hasJson >= 0) {
            //用json的描述赋予每一个bundle自己的职责，还可以赋予其他参数
            String jsonPath = bean.getFilePath().substring(0,bean.getFilePath().lastIndexOf(".")) + "/" + unZipFilePaths.get(hasJson);
            //解析json文件 ->
            String json = FileUtils.loadStringFromExternal(jsonPath);
            //解析avatar的json文件
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray components = jsonObject.getJSONArray("components");
                for(int i =0 ;i < components.length();i++) {
                    String path = bean.getFilePath().substring(0,bean.getFilePath().lastIndexOf(".")) + "/" + components.get(i);
                    strComponents.add(path);
                }

                JSONArray anims = jsonObject.getJSONArray("anims");
                for(int i =0 ;i < anims.length();i++) {
                    String path = bean.getFilePath().substring(0,bean.getFilePath().lastIndexOf(".")) + "/" + anims.get(i);
                    strAnimations.add(path);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //根据bundle的命名赋予每一个bundle自己的职责
            for (String str:unZipFilePaths) {
                if (str.startsWith(COMPONENTS_STR)) {
                    String path = bean.getFilePath().substring(0,bean.getFilePath().lastIndexOf(".")) + "/" + str;
                    strComponents.add(path);
                } else if (str.startsWith(ANIM_STR)){
                    String path = bean.getFilePath().substring(0,bean.getFilePath().lastIndexOf(".")) + "/" + str;
                    strAnimations.add(path);
                }
            }
        }

        if (mSceneModel == null) {
            mCurrentAvatarModel = AvatarSource.buildAvatarData(strComponents,strAnimations);
            mSceneModel = AvatarSource.buildSceneModel(mCurrentAvatarModel);
            mSceneModel.processorConfig.setTrackScene(ProcessorConfig.TrackScene.SceneFull);
            mCurrentAvatarModel.transForm.setTranslationScale(new FUTranslationScale(0.5f, 0f, 0.1f));
        } else {
            if (mCurrentAvatarModel != null)
                mSceneModel.removeAvatar(mCurrentAvatarModel);
            mCurrentAvatarModel = AvatarSource.buildAvatarData(strComponents,strAnimations);
            mSceneModel.addAvatar(mCurrentAvatarModel);
        }
       FUSceneKit.getInstance().addScene(mSceneModel);
       FUSceneKit.getInstance().setCurrentScene(mSceneModel);
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
            mFUAIKit.setMaxFaces(4);
        } else {
            mFUAIKit.setMaxFaces(1);
        }
        if (adapter != null && adapter.trim().length() > 0) {
            boolean isFlipPoints = adapter.contains("2");
            boolean is3DFlipH = adapter.contains("4");
            boolean isClick = adapter.contains("3");
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
        try{
            StickerDownloadHelper.getInstance().download(docsBean);
        } catch (Exception e) {
            Log.e(TAG,"downloadSticker",e);
            e.printStackTrace();
        }
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
        mFUAIKit.loadAIProcessor(DemoConfig.getAIHumanBundle(), FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_HAND, FUAITypeEnum.FUAITYPE_HANDGESTURE);
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        mFUAIKit.setMaxFaces(1);
        onItemSelected(currentSticker);
    }


    /**
     * 结束需要释放AI驱动
     */
    public void releaseAIProcessor() {
        mFUAIKit.releaseAIProcessor(FUAITypeEnum.FUAITYPE_HUMAN_PROCESSOR);
        mFUAIKit.releaseAIProcessor(FUAITypeEnum.FUAITYPE_HANDGESTURE);
    }


    private void adapterMaxFace() {
        if (currentSticker == null) return;
        String adapter = currentSticker.getTool().getAdapter();
        if (adapter == null || !adapter.contains("1")) {
            mFUAIKit.setMaxFaces(4);
        } else {
            mFUAIKit.setMaxFaces(1);
        }
    }

    public void onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && currentProp != null && currentProp.isClick()) {
            currentProp.onClick();
        }
    }

    //通过枚举记录每一个bundle的类型
    public enum BundleType {
        NORMAL_SINGLE_BUNDLE,//普通的单bundle道具
        AVATAR_BUNDLE//avatar bundle 一般为多bundle
    }

    private BundleTypeListener mBundleTypeListener;
    public interface BundleTypeListener {
        void bundleType(BundleType bundleType);
    }
    public void setBundleTypeListener (BundleTypeListener bundleTypeListener){
        mBundleTypeListener = bundleTypeListener;
    }
}
