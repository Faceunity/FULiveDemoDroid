package com.faceunity.app.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.faceunity.app.R;
import com.faceunity.app.base.BaseFaceUnityActivity;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.enumeration.PosterFaceEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.utils.ThreadHelper;
import com.faceunity.ui.dialog.ToastHelper;

/**
 * DESC：
 * Created on 2021/3/3
 */
public class PosterFaceAcquisitionActivity extends BaseFaceUnityActivity {

    private static String TEMPLATE = "template";
    private static String INTENSITY = "intensity";
    private static int dataSource = 0;

    private static int REQ_PHOTO = 310;
    public static int REQ_PREVIEW = 1000;

    public static void startActivity(Context context, String templatePath, Double intensity) {
        context.startActivity(new Intent(context, PosterFaceAcquisitionActivity.class)
                .putExtra(TEMPLATE, templatePath).putExtra(INTENSITY, intensity));

    }

    private String mTempPath;
    private Double mIntensity;
    //仅用于判断相机视频流的海报换脸人脸情况
    private volatile PosterFaceEnum mPosterFaceEnum = PosterFaceEnum.POSTER_RIGHT_FACE;

    private View mTakeOptionView;

    private Bitmap mShotBitmap;//拍照完成后的Bitmap

    @Override
    public void initData() {
        super.initData();
        mTempPath = getIntent().getStringExtra(TEMPLATE);
        mIntensity = getIntent().getDoubleExtra(INTENSITY, 0.2);
    }

    @Override
    public void initView() {
        super.initView();
        View mFaceRectView = LayoutInflater.from(this).inflate(R.layout.layout_poster_take_photo, null);
        RelativeLayout.LayoutParams paramsRectView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mCustomView.addView(mFaceRectView, 0,paramsRectView);

        mTakeOptionView = LayoutInflater.from(this).inflate(R.layout.layout_poster_take_bottom, null);
        FrameLayout.LayoutParams paramsOptionView = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, getResources().getDimensionPixelSize(R.dimen.x238));
        paramsOptionView.gravity = Gravity.BOTTOM;
        mRootView.addView(mTakeOptionView, paramsOptionView);
        mTakeOptionView.setVisibility(View.GONE);

        mMoreView.setBackgroundResource(R.mipmap.icon_live_photo);
        mMoreView.setVisibility(View.VISIBLE);

        mBackView.setImageResource(R.mipmap.icon_arrow_back);

        changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x40));
    }

    @Override
    public void bindListener() {
        super.bindListener();
        mTakeOptionView.findViewById(R.id.iv_poster_take_back).setOnClickListener(view ->
        {
            updateTakeView(true);
            mCameraRenderer.hideImageTexture();
            mShotBitmap = null;
        });
        mTakeOptionView.findViewById(R.id.iv_poster_take_confirm).setOnClickListener(view ->
        {
            ThreadHelper.getInstance().execute(() -> {
                String photoPath = FileUtils.addBitmapToExternal(this, mShotBitmap);
                if (photoPath == null || photoPath.trim().length() == 0) {
                    runOnUiThread(() -> ToastHelper.showNormalToast(PosterFaceAcquisitionActivity.this, "图片保存失败"));
                } else {
                    mFURenderKit.release();
                    dataSource = 0;
                    PosterPreviewActivity.startActivity(this, photoPath, mTempPath, mIntensity, mPosterFaceEnum,dataSource);
                }
            });
        });
        mMoreView.setOnClickListener((view) -> com.faceunity.app.utils.FileUtils.pickImageFile(this, REQ_PHOTO));
    }


    @Override
    protected void configureFURenderKit() {
        super.configureFURenderKit();
        mFURenderKit.setFaceBeauty(FaceBeautyDataFactory.faceBeauty);
        FUAIKit.getInstance().setMaxFaces(4);
    }

    @Override
    protected void onSurfaceCreated() {
        super.onSurfaceCreated();
        if (mShotBitmap != null)
            mCameraRenderer.showImageTexture(mShotBitmap);
    }

    @Override
    protected int getFunctionType() {
        return FunctionEnum.POSTER_CHANGE;
    }

    @Override
    protected int getStubBottomLayoutResID() {
        return 0;
    }

    @Override
    protected void onReadBitmap(Bitmap bitmap) {
        mShotBitmap = bitmap;
        mCameraRenderer.showImageTexture(bitmap);
        runOnUiThread(() -> updateTakeView(false));
    }

    @Override
    protected void onStartRecord() {
    }

    @Override
    protected void onStopRecord() {
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String photoPath = FileUtils.getFilePathByUri(this, uri);
            mFURenderKit.release();
            dataSource = 1;
            PosterPreviewActivity.startActivity(this, photoPath, mTempPath, mIntensity,PosterFaceEnum.POSTER_RIGHT_FACE,dataSource);
        } else if (requestCode == REQ_PREVIEW) {
            if (resultCode == Activity.RESULT_OK) {
                mShotBitmap = null;
                mCameraRenderer.hideImageTexture();
                updateTakeView(true);
            } else {
                onBackPressed();
            }
        }
    }

    /**
     * 更新视图
     *
     * @param isShowCustom Boolean
     */
    private void updateTakeView(Boolean isShowCustom) {
        mCustomView.setVisibility(isShowCustom ? View.VISIBLE : View.GONE);
        mTakeOptionView.setVisibility(isShowCustom ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onRenderBefore(FURenderInputData inputData) {
        mPosterFaceEnum = PosterFaceEnum.POSTER_RIGHT_FACE;
        int faceCount = FUAIKit.getInstance().isTracking();
        if (faceCount < 1) {
            mPosterFaceEnum = PosterFaceEnum.POSTER_ERROR_NO_FACE;
            runOnUiThread(()->{
                mTrackingView.setVisibility(View.VISIBLE);
                mTrackingView.setText(R.string.fu_base_is_tracking_text);
            });
        } else if (faceCount > 1){} else {
            //一张人脸
            float[] faceArray = new float[4];
            FUAIKit.getInstance().getFaceInfo(0,faceArray);
            boolean canUseFace = true;
            //检测人脸是否完整
            if (faceArray[0] < 0 || faceArray[2] > inputData.getWidth()
                    || faceArray[1] <0 || faceArray[3] > inputData.getHeight()) {
                canUseFace = false;
                mPosterFaceEnum = PosterFaceEnum.POSTER_ERROR_INCOMPLETE_FACE;
            }
            //检测角度
            if (FUAIKit.getInstance().checkRotation()) {
                mPosterFaceEnum = PosterFaceEnum.POSTER_ERROR_ROTATE_FACE;
            }

            boolean finalCanUseFace = canUseFace;
            runOnUiThread(()-> {
                if (finalCanUseFace) {
                    mTrackingView.setVisibility(View.GONE);
                } else {
                    mTrackingView.setVisibility(View.VISIBLE);
                    mTrackingView.setText(R.string.fu_base_incomplete_face_text);
                }
            });
        }
    }
}
