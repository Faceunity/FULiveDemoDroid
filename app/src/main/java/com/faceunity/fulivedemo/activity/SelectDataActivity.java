package com.faceunity.fulivedemo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.faceunity.entity.Effect;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.util.Iterator;
import java.util.List;

public class SelectDataActivity extends AppCompatActivity {
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
            case IMAGE_REQUEST_CODE_PHOTO: {
                if (data == null) {
                    return;
                }
                String fileImgPath = MiscUtil.getFileAbsolutePath(this, data.getData());
                if (!new File(fileImgPath).exists()) {
                    ToastUtil.showToast(this, R.string.image_file_does_not_exist);
                    return;
                }
                boolean b = checkIsImage(fileImgPath);
                if (!b) {
                    Toast.makeText(this, "请选择图片文件", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentPhoto = new Intent(SelectDataActivity.this, ShowPhotoActivity.class);
                intentPhoto.setData(data.getData());
                intentPhoto.putExtra(SELECT_DATA_KEY, mSelectDataType);
                intentPhoto.putExtra(FUEffectActivity.SELECT_EFFECT_KEY, mSelectEffectType);
                startActivityForResult(intentPhoto, IMAGE_REQUEST_CODE_SHOW);
            }
            break;
            case IMAGE_REQUEST_CODE_TAKE_PHOTO: {
                Intent intentTakePhoto = new Intent(SelectDataActivity.this, ShowPhotoActivity.class);
                intentTakePhoto.setData(mImageUri);
                intentTakePhoto.putExtra(SELECT_DATA_KEY, mSelectDataType);
                intentTakePhoto.putExtra(FUEffectActivity.SELECT_EFFECT_KEY, mSelectEffectType);
                startActivityForResult(intentTakePhoto, IMAGE_REQUEST_CODE_SHOW);
            }
            break;
            case IMAGE_REQUEST_CODE_VIDEO: {
                if (data == null) {
                    return;
                }
                String fileVideoPath = MiscUtil.getFileAbsolutePath(this, data.getData());
                if (!new File(fileVideoPath).exists()) {
                    ToastUtil.showToast(this, R.string.video_file_does_not_exist);
                    return;
                }
                boolean b = checkIsVideo(SelectDataActivity.this, fileVideoPath);
                if (!b) {
                    Toast.makeText(this, "请选择视频文件", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intentVideo = new Intent(SelectDataActivity.this, ShowVideoActivity.class);
                intentVideo.setData(data.getData());
                intentVideo.putExtra(SELECT_DATA_KEY, mSelectDataType);
                intentVideo.putExtra(FUEffectActivity.SELECT_EFFECT_KEY, mSelectEffectType);
                startActivityForResult(intentVideo, IMAGE_REQUEST_CODE_SHOW);
            }
            break;
            case IMAGE_REQUEST_CODE_SHOW: {
                if (resultCode == IMAGE_RESULT_CODE) {
                    onBackPressed();
                }
            }
            break;
            default:
        }
    }

    private boolean checkIsImage(String path) {
        String name = new File(path).getName().toLowerCase();
        boolean isImage = name.endsWith(MiscUtil.IMAGE_FORMAT_PNG) || name.endsWith(MiscUtil.IMAGE_FORMAT_JPG)
                || name.endsWith(MiscUtil.IMAGE_FORMAT_JPEG);
        return isImage;
    }

    // 检查是否是 video
    private boolean checkIsVideo(Context context, String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, Uri.fromFile(new File(path)));
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            return "yes".equals(hasVideo);
        } catch (Exception e) {
            return false;
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.select_data_photo_layout: {
                Intent intentPhoto = new Intent();
                intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                intentPhoto.setType("image/*");
                intentPhoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
                ResolveInfo resolveInfo = getPackageManager().resolveActivity(intentPhoto, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo == null) {
                    intentPhoto.setAction(Intent.ACTION_GET_CONTENT);
                    intentPhoto.removeCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(intentPhoto, IMAGE_REQUEST_CODE_PHOTO);
            }
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
            case R.id.select_data_video_layout: {
                Intent intentVideo = new Intent();
                intentVideo.addCategory(Intent.CATEGORY_OPENABLE);
                intentVideo.setType("video/*");
                intentVideo.setAction(Intent.ACTION_OPEN_DOCUMENT);
                ResolveInfo resolveInfo = getPackageManager().resolveActivity(intentVideo, PackageManager.MATCH_DEFAULT_ONLY);
                if (resolveInfo == null) {
                    intentVideo.setAction(Intent.ACTION_GET_CONTENT);
                    intentVideo.removeCategory(Intent.CATEGORY_OPENABLE);
                }
                startActivityForResult(intentVideo, IMAGE_REQUEST_CODE_VIDEO);
            }
            break;
            default:
        }
    }

    public static void filterEffectList(List<Effect> effects) {
        for (Iterator<Effect> iterator = effects.iterator(); iterator.hasNext(); ) {
            Effect effect = iterator.next();
            String bundleName = effect.getBundleName();
            // 导入图片和视频不用这两个，通过 bundleName 过滤，注意 hard code！
            if ("expression_shooting".equals(bundleName) || "zhenxinhua_damaoxian".equals(bundleName)) {
                iterator.remove();
            }
        }
    }
}
