package com.faceunity.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.Nullable;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.ui.dialog.ToastHelper;

import java.io.File;

/**
 * DESC：
 * Created on 2021/3/2
 */
public class SelectDataActivity extends BaseActivity {

    private static final String TYPE = "type";
    private static final int REQUEST_CODE_PHOTO = 1000;
    private static final int REQUEST_CODE_VIDEO = 1001;

    private static final String IMAGE_FORMAT_JPG = ".jpg";
    private static final String IMAGE_FORMAT_JPEG = ".jpeg";
    private static final String IMAGE_FORMAT_PNG = ".png";

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, SelectDataActivity.class).putExtra(TYPE, type));
    }

    public static void startActivityForResult(Activity activity) {
        activity.startActivityForResult(new Intent(activity, SelectDataActivity.class).putExtra(TYPE, -1), 1000);
    }

    private int mFunctionType;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_select_data;
    }

    @Override
    public void initData() {
        mFunctionType = getIntent().getIntExtra(TYPE, 0);
    }

    @Override
    public void initView() {

    }

    @Override
    public void bindListener() {
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.lyt_select_data_photo).setOnClickListener(view -> FileUtils.pickImageFile(this, REQUEST_CODE_PHOTO));
        findViewById(R.id.lyt_select_data_video).setOnClickListener(view -> FileUtils.pickVideoFile(this, REQUEST_CODE_VIDEO));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null) return;
        if (mFunctionType == -1) {
            setResult(resultCode, data);
            finish();
            return;
        }
        Uri uri = data.getData();
        String path = FileUtils.getFilePathByUri(this, uri);
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (!checkIsImage(path)) {
                ToastHelper.showNormalToast(this, "请选择正确的图片文件");
                return;
            }
            FaceBeautyActivity.needBindDataFactory = true;
            ShowPhotoActivity.startActivity(this, mFunctionType, path);

        } else if (requestCode == REQUEST_CODE_VIDEO) {
            if (!checkIsVideo(path)) {
                ToastHelper.showNormalToast(this, "请选择正确的视频文件");
                return;
            }
            FaceBeautyActivity.needBindDataFactory = true;
            ShowVideoActivity.startActivity(this, mFunctionType, path);
        }


    }


    /**
     * 校验文件是否是图片
     *
     * @param path String
     * @return Boolean
     */
    private Boolean checkIsImage(String path) {
        String name = new File(path).getName().toLowerCase();
        return (name.endsWith(IMAGE_FORMAT_PNG) || name.endsWith(IMAGE_FORMAT_JPG)
                || name.endsWith(IMAGE_FORMAT_JPEG));
    }

    /**
     * 校验文件是否是视频
     *
     * @param path String
     * @return Boolean
     */
    private Boolean checkIsVideo(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(this, Uri.fromFile(new File(path)));
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            return "yes".equals(hasVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }


}
