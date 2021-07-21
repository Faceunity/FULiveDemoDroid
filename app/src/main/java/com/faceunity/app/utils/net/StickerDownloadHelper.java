package com.faceunity.app.utils.net;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.ArrayMap;
import android.util.Log;

import com.faceunity.app.DemoApplication;
import com.faceunity.ui.entity.net.FineStickerEntity;
import com.faceunity.app.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

/**
 * 道具下载 缓存
 *
 * @author Richie on 2021.03.03
 */
public final class StickerDownloadHelper {
    private static final String TAG = "StickerDownloadHelper";
    private static final String URL_HOST = "https://items.faceunity.com:4006";
    private static final String URL_TAGS = URL_HOST + "/api/guest/tags?platform=mobile";
    private static final String URL_TOOLS = URL_HOST + "/api/guest/tools";
    private static final String URL_DOWNLOAD = URL_HOST + "/api/guest/download?id=";
    public static final String STICKER_DIR_PATH = FileUtils.getExternalFileDir(DemoApplication.mApplication) + "/fine_sticker";

    private Gson gson;
    private String[] mTags;
    private Map<String, FineStickerEntity> mFineStickerEntityMap = new ArrayMap<>();

    static {
        File file = new File(STICKER_DIR_PATH);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    private StickerDownloadHelper() {
        gson = new Gson();
        String tagArray = loadFormSp(TAG);
        JsonArray array = gson.fromJson(tagArray, JsonArray.class);
        if (null != array) {
            mTags = new String[array.size()];
            for (int i = 0; i < array.size(); i++) {
                mTags[i] = array.get(i).toString();
                mTags[i] = mTags[i].substring(1, mTags[i].length() - 1); // 后台把引号放进来了
            }
        } else {
            mTags = new String[0];
        }
        for (int i = 0; i < mTags.length; i++) {
            String json = loadFormSp(mTags[i]);
            FineStickerEntity fineSticker = gson.fromJson(json, FineStickerEntity.class);
            if (null != fineSticker) {
                mFineStickerEntityMap.put(mTags[i], fineSticker);
            }
        }
    }

    private static class StickerDownloadHelperHolder {
        private static StickerDownloadHelper INSTANCE = new StickerDownloadHelper();
    }

    public static StickerDownloadHelper getInstance() {
        return StickerDownloadHelperHolder.INSTANCE;
    }

    public interface Callback {
        void onGetTags(String[] tags);

        void onGetList(String tag, FineStickerEntity fineSticker);

        void onDownload(FineStickerEntity.DocsBean entity);

        void onDownloadError(FineStickerEntity.DocsBean entity, String msg);
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public String[] tags() {
        // {"code":2,"message":"ok","data":["中级道具","高级道具","专业道具"]}
        OkHttpUtils.getInstance().getAsEntity(URL_TAGS, new OkHttpUtils.OkHttpCallback<JsonObject>() {
            @Override
            protected void onSuccess(JsonObject result) {
                JsonArray array = null;
                try {
                    array = result.getAsJsonArray("data");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (array == null) {
                    return;
                }
                mTags = new String[array.size()];
                for (int i = 0; i < array.size(); i++) {
                    mTags[i] = array.get(i).toString();
                    mTags[i] = mTags[i].substring(1, mTags[i].length() - 1); // 后台把引号放进来了
                }
                if (null != mCallback) {
                    mCallback.onGetTags(mTags);
                }
                saveToSp(TAG, array.toString());
            }

            @Override
            protected void onFailure(String errorMsg) {
                Log.e(TAG, URL_TAGS + "  " + errorMsg);
            }
        });
        return mTags;
    }

    /**
     * 根据tag获取道具列表
     */
    public FineStickerEntity tools(String tag) {
        if (mFineStickerEntityMap.get(tag) == null) {
            FineStickerEntity entity = new FineStickerEntity();
            entity.setDocs(new ArrayList<>());
            mFineStickerEntityMap.put(tag, entity);
        }
        if (!mFineStickerEntityMap.get(tag).isOnline()) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("platform", "mobile");
            jsonObject.addProperty("tag", tag);
            OkHttpUtils.getInstance().postJsonAsEntity(URL_TOOLS, jsonObject.toString(), new OkHttpUtils.OkHttpCallback<JsonObject>() {
                @Override
                protected void onSuccess(JsonObject result) {
                    FineStickerEntity entity = gson.fromJson(result.get("data"), FineStickerEntity.class);
                    entity.setOnline(true);
                    mFineStickerEntityMap.put(tag, entity);
                    checkDownloaded(entity);
                    if (null != mCallback) {
                        mCallback.onGetList(tag, entity);
                    }
                    saveToSp(tag, result.get("data").toString());
                }

                @Override
                protected void onFailure(String errorMsg) {
                    Log.e(TAG, URL_TOOLS + "  " + errorMsg);
                }
            });
        }
        checkDownloaded(mFineStickerEntityMap.get(tag));
        return mFineStickerEntityMap.get(tag);
    }

    /**
     * 获取道具下载地址
     * 获取完就直接开始下载了
     */
    public void download(FineStickerEntity.DocsBean entity) {
        String url = URL_DOWNLOAD + entity.getTool().get_id() + "&platform=mobile";
        OkHttpUtils.getInstance().getAsEntity(url, new OkHttpUtils.OkHttpCallback<JsonObject>() {
            /**  {
             *     "code": 2,
             *     "message": "ok",
             *     "data": { // 请自行下载url
             *         "url": "http://tools-manage.oss-cn-hangzhou.aliyuncs.com/0a176380-b33e-11e9-8e6a-35012f229fc1-enc.bundle?OSSAccessKeyId=LTAIZvD3ylHAD1vH&Expires=1615967910&Signature=2%2B0YwGKKidAWNqLHEXf0BtByQoI%3D"
             *     }
             * }
             */
            @Override
            protected void onSuccess(JsonObject result) {
                realDownload(result.getAsJsonObject("data").get("url").getAsString(), entity);
            }

            @Override
            protected void onFailure(String errorMsg) {
                Log.e(TAG, url + "  " + errorMsg);
                if (null != mCallback) {
                    mCallback.onDownloadError(entity,errorMsg);
                }
            }
        });
    }

    /**
     * 下载道具
     */
    private void realDownload(String url, FineStickerEntity.DocsBean entity) {
        File dest = new File(STICKER_DIR_PATH, entity.getTool().getBundle().getUid());
        OkHttpUtils.getInstance().downloadFile(url, dest, new OkHttpUtils.OkHttpCallback<File>() {
            @Override
            protected void onSuccess(File result) {
                entity.setFilePath(result.getAbsolutePath());
                if (null != mCallback) {
                    mCallback.onDownload(entity);
                }
            }

            @Override
            protected void onFailure(String errorMsg) {
                Log.e(TAG, url + "  " + errorMsg);
                if (null != mCallback) {
                    mCallback.onDownloadError(entity,errorMsg);
                }
            }
        });
    }

    /************************** 缓存相关 **************************/

    private void saveToSp(String key, String value) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(DemoApplication.mApplication.getPackageName(), Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    private String loadFormSp(String key) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(DemoApplication.mApplication.getPackageName(), Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    private void checkDownloaded(FineStickerEntity fineStickerEntity) {
        if (null == fineStickerEntity.getDocs()) {
            return;
        }
        String[] bundles = new File(STICKER_DIR_PATH).list();
        HashSet<String> bundleSet = new HashSet<>();
        if (bundles != null && bundles.length > 0) {
            Collections.addAll(bundleSet, bundles);
        }
        for (FineStickerEntity.DocsBean fineSticker : fineStickerEntity.getDocs()) {
            if (bundleSet.contains(fineSticker.getTool().getBundle().getUid())) {
                fineSticker.setFilePath(STICKER_DIR_PATH + "/" + fineSticker.getTool().getBundle().getUid());
            }
        }
    }
}
