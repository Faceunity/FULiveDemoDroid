package com.faceunity.fulivedemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.MiscUtil;

import java.io.File;

public class SelectDataActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = SelectDataActivity.class.getSimpleName();
    public static final String SELECT_DATA_KEY = "select_data_key";

    public static final int IMAGE_RESULT_CODE = 0x200;
    private static final int IMAGE_REQUEST_CODE_PHOTO = 0x101;
    private static final int IMAGE_REQUEST_CODE_TAKE_PHOTO = 0x102;
    private static final int IMAGE_REQUEST_CODE_VIDEO = 0x103;
    private static final int IMAGE_REQUEST_CODE_SHOW = 0x110;
    private String mSelectDataType;
    private int mSelectEffectType;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_data);
        mSelectDataType = getIntent().getStringExtra(SELECT_DATA_KEY);
        mSelectEffectType = getIntent().getIntExtra(FUEffectActivity.SELECT_EFFECT_KEY, -1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK && resultCode != IMAGE_RESULT_CODE) {
            return;
        }
        switch (requestCode) {
            case IMAGE_REQUEST_CODE_PHOTO:
                if (data == null) return;
                String fileImgPath = MiscUtil.getFileAbsolutePath(this, data.getData());
                if (!new File(fileImgPath).exists()) {
                    ToastUtil.showToast(this, R.string.image_file_does_not_exist);
                    return;
                }
                Intent intentPhoto = new Intent(SelectDataActivity.this, ShowPhotoActivity.class);
                intentPhoto.setData(data.getData());
                intentPhoto.putExtra(SELECT_DATA_KEY, mSelectDataType);
                intentPhoto.putExtra(FUEffectActivity.SELECT_EFFECT_KEY, mSelectEffectType);
                startActivityForResult(intentPhoto, IMAGE_REQUEST_CODE_SHOW);
                break;
            case IMAGE_REQUEST_CODE_TAKE_PHOTO:
                Intent intentTakePhoto = new Intent(SelectDataActivity.this, ShowPhotoActivity.class);
                intentTakePhoto.setData(mImageUri);
                intentTakePhoto.putExtra(SELECT_DATA_KEY, mSelectDataType);
                intentTakePhoto.putExtra(FUEffectActivity.SELECT_EFFECT_KEY, mSelectEffectType);
                startActivityForResult(intentTakePhoto, IMAGE_REQUEST_CODE_SHOW);
                break;
            case IMAGE_REQUEST_CODE_VIDEO:
                if (data == null) return;
                String fileVideoPath = MiscUtil.getFileAbsolutePath(this, data.getData());
                if (!new File(fileVideoPath).exists()) {
                    ToastUtil.showToast(this, R.string.video_file_does_not_exist);
                    return;
                }
                Intent intentVideo = new Intent(SelectDataActivity.this, ShowVideoActivity.class);
                intentVideo.setData(data.getData());
                intentVideo.putExtra(SELECT_DATA_KEY, mSelectDataType);
                intentVideo.putExtra(FUEffectActivity.SELECT_EFFECT_KEY, mSelectEffectType);
                startActivityForResult(intentVideo, IMAGE_REQUEST_CODE_SHOW);
                break;
            case IMAGE_REQUEST_CODE_SHOW:
                if (resultCode == IMAGE_RESULT_CODE) {
                    onBackPressed();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.select_data_photo_layout:
                Intent intentPhoto = new Intent();
                intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                intentPhoto.setType("image/*");
                intentPhoto.setAction(Build.VERSION.SDK_INT < 19 ? Intent.ACTION_GET_CONTENT : Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intentPhoto, IMAGE_REQUEST_CODE_PHOTO);
                break;
//                拍摄照片代码
//            case R.id.select_data_take_photo:
//                Intent intentCamera = new Intent();
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
//                }
//                intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
//                String name = Constant.APP_NAME+"_" + MiscUtil.getCurrentDate() + ".jpg";
//                File file = new File(Constant.photoFilePath, name);
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                mImageUri = Uri.fromFile(file);
//                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
//                startActivityForResult(intentCamera, IMAGE_REQUEST_CODE_TAKE_PHOTO);
//                break;
            case R.id.select_data_video_layout:
                Intent intentVideo = new Intent();
                intentVideo.addCategory(Intent.CATEGORY_OPENABLE);
                intentVideo.setType("video/*");
                intentVideo.setAction(Build.VERSION.SDK_INT < 19 ? Intent.ACTION_GET_CONTENT : Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(intentVideo, IMAGE_REQUEST_CODE_VIDEO);
                break;
            default:
        }
    }
}
