package com.faceunity.fulivedemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.ui.RecordBtn;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;

/**
 * 海报换脸拍照界面
 *
 * @author Richie
 */
public class PosterChangeFaceCameraActivity extends FUBaseActivity {
    private static final String TAG = "PosterChangeFaceCamera";
    private static final int REQ_PHOTO = 310;
    private View mFaceRectView;
    private View mTakeOptionView;
    private Bitmap mShotBitmap;
    private String mPhotoPath;

    public static void startSelfActivity(Context context, String templatePath) {
        Intent intent = new Intent(context, PosterChangeFaceCameraActivity.class);
        intent.putExtra(PosterChangeFaceActivity.TEMPLATE_PATH, templatePath);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate() {
        mTakeOptionView = LayoutInflater.from(this).inflate(R.layout.layout_poster_take_bottom, null);
        ConstraintLayout.LayoutParams paramsOptionView = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.x238));
        paramsOptionView.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        mRootView.addView(mTakeOptionView, paramsOptionView);
        mTakeOptionView.setVisibility(View.INVISIBLE);
        OnOptionViewClickListener onOptionViewClickListener = new OnOptionViewClickListener();
        mTakeOptionView.findViewById(R.id.iv_poster_take_back).setOnClickListener(onOptionViewClickListener);
        mTakeOptionView.findViewById(R.id.iv_poster_take_confirm).setOnClickListener(onOptionViewClickListener);

        mFaceRectView = LayoutInflater.from(this).inflate(R.layout.layout_poster_take_photo, null);
        ConstraintLayout.LayoutParams paramsRectView = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);
        mRootView.addView(mFaceRectView, paramsRectView);

        ImageView ivBack = (ImageView) findViewById(R.id.fu_base_back);
        ivBack.setImageResource(R.drawable.back_show);
        ivBack.setOnClickListener(new OnMultiClickListener() {
            @Override
            protected void onMultiClick(View v) {
                onBackPressed();
            }
        });
        mTakePicBtn.setOnRecordListener(new RecordBtn.OnRecordListener() {
            @Override
            public void takePic() {
                PosterChangeFaceCameraActivity.this.takePic();
            }

            @Override
            public void startRecord() {
            }

            @Override
            public void stopRecord() {
            }
        });
        mOnReadBitmapListener = new BitmapUtil.OnReadBitmapListener() {
            @Override
            public void onReadBitmapListener(Bitmap bitmap) {
                mShotBitmap = bitmap;
                mCameraRenderer.showImageTexture(bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTakeViewVisible(false);
                    }
                });
                mIsTakingPic = false;
            }
        };
    }

    @Override
    public void onSurfaceCreated() {
        super.onSurfaceCreated();
        mCameraRenderer.showImageTexture(mShotBitmap);
    }

    @Override
    protected FURenderer initFURenderer() {
        return new FURenderer
                .Builder(this)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .inputImageOrientation(mFrontCameraOrientation)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mPhotoPath)) {
            String templatePath = getIntent().getStringExtra(PosterChangeFaceActivity.TEMPLATE_PATH);
            PosterChangeFaceActivity.startSelfActivity(this, templatePath, mPhotoPath);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mPhotoPath = null;
    }

    // onActivityResult 的调用先于 OnRestart, 所以在 onResume 的时候才去跳转，防止出现黑屏。

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PHOTO && resultCode == Activity.RESULT_OK) {
            mPhotoPath = MiscUtil.getFileAbsolutePath(this, data.getData());
        } else if (requestCode == PosterChangeFaceActivity.REQ_TRACK_FACE) {
            if (resultCode != PosterChangeFaceActivity.RESULT_NO_TRACK_FACE) {
                onBackPressed();
            } else {
                mShotBitmap = null;
                mCameraRenderer.hideImageTexture();
                setTakeViewVisible(true);
            }
        }
    }

    @Override
    protected void takePicture(int texId, float[] mvpMatrix, float[] texMatrix, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(texId, texMatrix, mvpMatrix, texWidth, texHeight, mOnReadBitmapListener, false);
    }


    @Override
    protected boolean isOpenPhotoVideo() {
        return true;
    }

    @Override
    protected void onSelectPhotoVideoClick() {
        super.onSelectPhotoVideoClick();
        Intent intentPhoto = new Intent();
        intentPhoto.addCategory(Intent.CATEGORY_OPENABLE);
        intentPhoto.setType("image/*");
        intentPhoto.setAction(Intent.ACTION_OPEN_DOCUMENT);
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(intentPhoto, PackageManager.MATCH_DEFAULT_ONLY);
        if (resolveInfo == null) {
            intentPhoto.setAction(Intent.ACTION_GET_CONTENT);
            intentPhoto.removeCategory(Intent.CATEGORY_OPENABLE);
        }
        startActivityForResult(intentPhoto, REQ_PHOTO);
    }

    private void setTakeViewVisible(boolean visible) {
        if (visible) {
            mClOperationView.setVisibility(View.VISIBLE);
            mFaceRectView.setVisibility(View.VISIBLE);
            mTakeOptionView.setVisibility(View.GONE);
        } else {
            mClOperationView.setVisibility(View.GONE);
            mFaceRectView.setVisibility(View.GONE);
            mTakeOptionView.setVisibility(View.VISIBLE);
        }
    }

    private class OnOptionViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_poster_take_confirm) {
                ThreadHelper.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        String filePath = MiscUtil.saveBitmap(mShotBitmap, Constant.PHOTO_FILE_PATH, MiscUtil.getCurrentPhotoName());
                        if (filePath != null) {
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath)));
                            sendBroadcast(intent);
                        }
                    }
                });
                try {
                    String photoPath = FileUtils.saveTempBitmap(mShotBitmap, FileUtils.getSavePathFile(PosterChangeFaceCameraActivity.this));
                    String templatePath = getIntent().getStringExtra(PosterChangeFaceActivity.TEMPLATE_PATH);
                    PosterChangeFaceActivity.startSelfActivity(PosterChangeFaceCameraActivity.this, templatePath, photoPath);
                } catch (IOException e) {
                    Log.e(TAG, "saveTempBitmap: ", e);
                    Toast.makeText(PosterChangeFaceCameraActivity.this, "图片保存失败", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.iv_poster_take_back) {
                setTakeViewVisible(true);
                mCameraRenderer.hideImageTexture();
                mShotBitmap = null;
            }
        }
    }
}
