package com.faceunity.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.ui.dialog.ToastHelper;

/**
 * DESC：
 * Created on 2021/3/2
 */
public class SelectDataActivity extends BaseActivity {
    private static final String TYPE = "type";
    private static final int REQUEST_CODE_PHOTO = 1000;
    private static final int REQUEST_CODE_VIDEO = 1001;

    public static void startActivity(Context context, int type) {
        context.startActivity(new Intent(context, SelectDataActivity.class).putExtra(TYPE, type));
    }

    public static void startActivityForResult(Activity activity, int type,int requestCode) {
        activity.startActivityForResult(new Intent(activity, SelectDataActivity.class).putExtra(TYPE, type), requestCode);
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
        Uri uri = data.getData();
        String path = FileUtils.getFilePathByUri(this, uri);
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (!FileUtils.checkIsImage(path)) {
                ToastHelper.showNormalToast(this, getString(R.string.please_select_the_correct_picture_file));
                return;
            }
        } else if (requestCode == REQUEST_CODE_VIDEO) {
            if (!FileUtils.checkIsVideo(this,path)) {
                ToastHelper.showNormalToast(this, getString(R.string.please_select_the_correct_video_file));
                return;
            }
        }
        if (mFunctionType == -1) {
            setResult(resultCode, data);
            finish();
            return;
        }
        if (requestCode == REQUEST_CODE_PHOTO) {
            if (mFunctionType == FunctionEnum.FACE_BEAUTY)
                BaseFaceUnityActivity.needUpdateUI = true;
            else if (mFunctionType == FunctionEnum.STICKER)
                BaseFaceUnityActivity.needUpdateUI = true;
            else if (mFunctionType == FunctionEnum.BG_SEG_GREEN)
                BaseFaceUnityActivity.needUpdateUI = true;
            else if (mFunctionType == FunctionEnum.STYLE)
                BaseFaceUnityActivity.needUpdateUI = true;
            ShowPhotoActivity.startActivity(this, mFunctionType, path);
        } else if (requestCode == REQUEST_CODE_VIDEO) {
            if (mFunctionType == FunctionEnum.FACE_BEAUTY)
                BaseFaceUnityActivity.needUpdateUI = true;
            else if (mFunctionType == FunctionEnum.STICKER)
                BaseFaceUnityActivity.needUpdateUI = true;
            else if (mFunctionType == FunctionEnum.BG_SEG_GREEN)
                BaseFaceUnityActivity.needUpdateUI = true;
            else if (mFunctionType == FunctionEnum.STYLE)
                BaseFaceUnityActivity.needUpdateUI = true;
            ShowVideoActivity.startActivity(this, mFunctionType, path);
        }
    }
}
