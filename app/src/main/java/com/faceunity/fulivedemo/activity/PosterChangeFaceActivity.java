package com.faceunity.fulivedemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.entity.PosterChangeFaceTemplate;
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
import com.faceunity.fulivedemo.renderer.PosterPhotoRenderer;
import com.faceunity.fulivedemo.ui.FaceMaskView;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.SpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.NoTrackFaceDialogFragment;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.fulivedemo.utils.OnMultiClickListener;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 海报换脸效果界面
 * @author Richie
 */
public class PosterChangeFaceActivity extends AppCompatActivity implements PosterPhotoRenderer.OnRendererStatusListener,
        BaseRecyclerAdapter.OnItemClickListener<PosterChangeFaceTemplate> {
    private static final String TAG = "PosterChangeFace";
    public static final String PHOTO_PATH = "photo_path";
    public static final String TEMPLATE_PATH = "template_path";
    public static final int REQ_TRACK_FACE = 702;
    public static final int RESULT_NO_TRACK_FACE = -1;
    protected GLSurfaceView mGlSurfaceView;
    private FURenderer mFURenderer;
    private PosterPhotoRenderer mPosterPhotoRenderer;
    private volatile boolean mIsNeedSavePhoto;
    private volatile boolean mIsSavingPhoto;
    private boolean mIsTrackedTemplate;
    private boolean mIsTrackedPhoto;
    private volatile boolean mIsFirstDraw = true;
    private volatile boolean mIsNeedReInput = true;
    private int mTexId;
    private float[] mTemplateLandmarks;
    private float[] mPhotoLandmarks;
    private View mLoadingView;
    private volatile boolean mIsNotSelectedMultiFace;
    private ConstraintLayout mClContainerView;
    // 模板路径
    private String mTemplatePath;
    // 混合后的海报不合法
    private boolean mMixTexIdInvalid;
    // 是否已经保存海报
    private boolean mIsSavedPhoto;
    // 矫正生成的口型参数
    private volatile float mFixParams = 0.5f;
    // 生成图保存路径
    private String mMixedPhotoPath;
    private int mLastPosition = -1;
    private volatile int mFaceIndex;
    // 生成成功
    private static final int STATE_SUCCEED = 123;
    // 生成失败
    private static final int STATE_FAILED = 144;
    // 生成中
    private static final int STATE_CHANGING = 837;
    // 检测到多人脸
    private volatile boolean mIsMultiFace;
    // 一旦生成海报，就临时保存，Home 键退出再进入，不需要重新生成。用户点击保存时，把生成的复制到相册即可。
    private volatile int mState;
    private FaceMaskView mFaceMaskView;

    public static void startSelfActivity(Activity activity, String templatePath, String photoPath) {
        Intent intent = new Intent(activity, PosterChangeFaceActivity.class);
        intent.putExtra(TEMPLATE_PATH, templatePath);
        intent.putExtra(PHOTO_PATH, photoPath);
        activity.startActivityForResult(intent, REQ_TRACK_FACE);
    }

    private volatile boolean mPaused;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_fuposter_face);

        String photoPath = getIntent().getStringExtra(PHOTO_PATH);
        mTemplatePath = getIntent().getStringExtra(TEMPLATE_PATH);
        Log.i(TAG, "onCreate: photo:" + photoPath + ", template:" + mTemplatePath);
        mGlSurfaceView = findViewById(R.id.gl_surface);
        mGlSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
        mPosterPhotoRenderer = new PosterPhotoRenderer(photoPath, mGlSurfaceView, this);
        mGlSurfaceView.setRenderer(mPosterPhotoRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        ViewClickListener viewClickListener = new ViewClickListener();
        findViewById(R.id.iv_poster_back).setOnClickListener(viewClickListener);
        findViewById(R.id.iv_poster_save).setOnClickListener(viewClickListener);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_poster_template);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        int verticalPadding = getResources().getDimensionPixelSize(R.dimen.x24);
        int horizonPadding = getResources().getDimensionPixelSize(R.dimen.x10);
        int extraPadding = getResources().getDimensionPixelSize(R.dimen.x24);
        RecyclerView.ItemDecoration itemDecoration = new SpaceItemDecoration(horizonPadding, verticalPadding, extraPadding, extraPadding);
        recyclerView.addItemDecoration(itemDecoration);
        List<PosterChangeFaceTemplate> listPosterTemplates = PosterChangeFaceTemplate.getPosterTemplates(this);
        PosterTempListAdapter adapter = new PosterTempListAdapter(listPosterTemplates);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mLastPosition = PosterChangeFaceTemplate.findSelectedIndex(listPosterTemplates, mTemplatePath);
        adapter.setItemSelected(mLastPosition);
        // 只给毕业照模板设置，第 5 个。
        if (mLastPosition == 5) {
            mFixParams = 0.2f;
        }

        mClContainerView = (ConstraintLayout) findViewById(R.id.cl_container);
        ImageView ivLoading = (ImageView) findViewById(R.id.iv_loading);
        mLoadingView = findViewById(R.id.fl_loading_view);
        // 处理海报时，拦截其他点击
        mLoadingView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        Glide.with(this).load(R.drawable.loading_gif).into(ivLoading);
        showLoadingView(true);

        mFURenderer = new FURenderer
                .Builder(this)
                .maxFaces(4)
                .setNeedPosterFace(true)
                .setNeedFaceBeauty(false)
                .inputTextureType(2)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPaused = false;
        if (mState == STATE_SUCCEED) {
            mPosterPhotoRenderer.setMixedPhotoPath(mMixedPhotoPath);
        }
        mPosterPhotoRenderer.onCreate();
    }

    private void setSavePhotoFlag() {
        if (mIsSavingPhoto) {
            return;
        }
        mIsNeedSavePhoto = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        mPosterPhotoRenderer.onDestroy();
        if (mFaceMaskView != null) {
            mFaceMaskView.dismissPopWindow();
            mClContainerView.removeView(mFaceMaskView);
        }
    }

    @Override
    public void onSurfaceChanged(int viewWidth, int viewHeight) {

    }

    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
        mIsFirstDraw = true;
        mIsNeedReInput = true;
        mIsTrackedTemplate = false;
        mIsTrackedPhoto = false;
        mMixTexIdInvalid = false;
    }

    @Override
    public int onDrawFrame(int photoTextureId, int photoWidth, int photoHeight) {
        int texId = mTexId;
        if (mIsNeedReInput) {
            Log.i(TAG, "onDrawFrame: input params. isTrackTemplate:" + mIsTrackedTemplate +
                    ", isTrackPhoto:" + mIsTrackedPhoto + ", multiFace:" + mIsNotSelectedMultiFace);
            if (!mMixTexIdInvalid && !mIsNotSelectedMultiFace) {
                if (mIsTrackedTemplate) {
                    showLoadingView(true);
                    mFURenderer.fixPosterFaceParam(mFixParams);
                    mFURenderer.onPosterTemplateSelected(mPosterPhotoRenderer.getTemplateWidth(),
                            mPosterPhotoRenderer.getTemplateHeight(), mPosterPhotoRenderer.getTemplateRGBABytes(), mTemplateLandmarks);
                }

                if (mIsTrackedPhoto) {
                    showLoadingView(true);
                    if (mPhotoLandmarks != null) {
                        mFURenderer.onPosterInputPhoto(photoWidth, photoHeight, mPosterPhotoRenderer.getPhotoRGBABytes(),
                                mPhotoLandmarks);
                        mPosterPhotoRenderer.setDrawPhoto(false);
                    }
                }

                if (mIsTrackedPhoto || mIsTrackedTemplate) {
                    showLoadingView(false);
                }
            }

            if (!mIsNotSelectedMultiFace && !mIsFirstDraw) {
                byte[] templateBytes = mPosterPhotoRenderer.getTemplateBytes();
                texId = mFURenderer.onDrawFrame(templateBytes, mPosterPhotoRenderer.getTemplateWidth(),
                        mPosterPhotoRenderer.getTemplateHeight());
                if (texId == 0) {
                    mIsNeedReInput = true;
                    mState = STATE_FAILED;
                    if (templateBytes != null) {
                        mMixTexIdInvalid = true;
                    }
                } else {
                    mIsNeedReInput = false;
                    mMixTexIdInvalid = false;
                    mState = STATE_SUCCEED;
                    setSavePhotoFlag();
                }
                mTexId = texId = texId > 0 ? texId : mTexId;
                Log.i(TAG, "onDrawFrame: draw template:" + texId);
            } else {
                mTexId = texId = photoTextureId;
                mPosterPhotoRenderer.setDrawPhoto(true);
                mIsFirstDraw = false;
                Log.i(TAG, "onDrawFrame: draw photo:" + texId);
            }
        }
        savePhoto(texId, 720, 1280);
//        Log.i(TAG, "onDrawFrame: final texId " + texId);
        return texId;
    }

    private void showLoadingView(final boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show && mLoadingView.getVisibility() == View.INVISIBLE) {
                    mLoadingView.setVisibility(View.VISIBLE);
                } else if (!show && mLoadingView.getVisibility() == View.VISIBLE) {
                    mLoadingView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
        mTexId = 0;
        mIsTrackedTemplate = false;
        mIsTrackedPhoto = false;
        mIsFirstDraw = true;
        mIsSavedPhoto = false;
    }

    @Override
    public void onTemplateLoaded(byte[] img, int width, int height) {
        Log.d(TAG, "onTemplateLoaded() called width = [" + width + "], height = [" + height + "]");
        if (mFURenderer.trackFace(img, width, height) > 0) {
            mTemplateLandmarks = getCopyOfLandmark(0);
            mIsTrackedTemplate = true;
            mIsTrackedPhoto = true;
            mIsNeedReInput = true;
            Log.i(TAG, "onTemplateLoaded: tracking template > 0--------- ");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PosterChangeFaceActivity.this, "未识别模板的人脸，请重新选择模板", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onPhotoLoaded(byte[] img, int width, int height) {
        Log.d(TAG, "onPhotoLoaded() called width = [" + width + "], height = [" + height + "]");
        // 多人脸弹框过程中
        if (mIsNotSelectedMultiFace) {
            mIsMultiFace = false;
            if (mFaceMaskView != null) {
                mFaceMaskView.post(new Runnable() {
                    @Override
                    public void run() {
                        mFaceMaskView.dismissPopWindow();
                        mClContainerView.removeView(mFaceMaskView);
                    }
                });
                mFaceMaskView = null;
            }
        }

        // Home 键再次返回后
        if (mIsMultiFace) {
            showLoadingView(true);
            mIsTrackedPhoto = true;
            mIsNeedReInput = true;
            mFURenderer.trackFace(img, width, height);
            mPhotoLandmarks = getCopyOfLandmark(mFaceIndex);
            mPosterPhotoRenderer.reloadTemplateData(mTemplatePath);
            return;
        }
        final int trackFace = mFURenderer.trackFace(img, width, height);
        if (trackFace > 0) {
            if (trackFace > 1) {
                // 多人脸
                mIsNotSelectedMultiFace = true;
                mIsMultiFace = true;
                mFaceIndex = 0;
                mFaceMaskView = new FaceMaskView(this);
                mFaceMaskView.setOnFaceSelectedListener(new FaceMaskView.OnFaceSelectedListener() {
                    @Override
                    public void onFaceSelected(final View view, int index) {
                        Log.i(TAG, "onFaceSelected: " + index);
                        mFaceIndex = index;
                        mIsNotSelectedMultiFace = false;
                        mIsFirstDraw = true;
                        mGlSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                mPhotoLandmarks = getCopyOfLandmark(mFaceIndex);
                                mIsNeedReInput = true;
                                mPosterPhotoRenderer.reloadTemplateData(mTemplatePath);
                            }
                        });
                        if (!mPaused) {
                            view.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mFaceMaskView.dismissPopWindow();
                                    mClContainerView.removeView(mFaceMaskView);
                                }
                            }, 500);
                        }
                    }
                });
                mPosterPhotoRenderer.setDrawPhoto(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < trackFace; i++) {
                            float[] faceRectData = mFURenderer.getFaceRectData(i);
                            float[] newFaceRect = mPosterPhotoRenderer.convertFaceRect(faceRectData);
                            mFaceMaskView.addFaceRect(newFaceRect);
                        }
                        showLoadingView(false);
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                        if (!mPaused) {
                            mClContainerView.addView(mFaceMaskView, params);
                        }
                    }
                });
            } else {
                mFaceIndex = 0;
                mIsMultiFace = false;
                boolean checked = false;
                if (mMixedPhotoPath == null) {
                    // 只要生成过，就不需要再次 check
                    boolean ret = checkRotation();
                    if (ret) {
                        checked = true;
                        mIsNeedReInput = false;
                        mPosterPhotoRenderer.setDrawPhoto(true);
                        showPromptFragment(R.string.dialog_face_rotation_not_valid);
                    }
                }
                if (!checked) {
                    mIsTrackedPhoto = true;
                    mIsNeedReInput = true;
                    mPhotoLandmarks = getCopyOfLandmark(mFaceIndex);
                    mPosterPhotoRenderer.reloadTemplateData(mTemplatePath);
                }
            }
            Log.i(TAG, "onPhotoLoaded: tracking photo > 0--------- ");
        } else {
            mIsNeedReInput = false;
            mPosterPhotoRenderer.setDrawPhoto(true);
            showPromptFragment(R.string.dialog_no_track_face);
        }
    }

    private float[] getCopyOfLandmark(int faceId) {
        float[] landmarksData = mFURenderer.getLandmarksData(faceId);
        return Arrays.copyOf(landmarksData, landmarksData.length);
    }

    @Override
    public void onLoadPhotoError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.makeFineToast(PosterChangeFaceActivity.this, error, R.drawable.icon_fail).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PosterChangeFaceActivity.this.onBackPressed();
                    }
                }, 1500);
            }
        });
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter<PosterChangeFaceTemplate> adapter, View view, final int position) {
        if (mLastPosition == position) {
            return;
        }
        mLastPosition = position;
        mIsTrackedTemplate = false;
        mIsSavedPhoto = false;
        showLoadingView(true);
        mState = STATE_CHANGING;
        PosterChangeFaceTemplate posterTemplate = adapter.getItem(position);
        if (position == 5) {
            mFixParams = 0.2f;
        } else {
            mFixParams = 0.5f;
        }
        mTemplatePath = posterTemplate.getPath();
        mGlSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mPosterPhotoRenderer.reloadTemplateData(mTemplatePath);
            }
        });
    }

    private void showPromptFragment(final int strId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NoTrackFaceDialogFragment noTrackFaceDialogFragment = NoTrackFaceDialogFragment.newInstance(strId);
                noTrackFaceDialogFragment.setOnDismissListener(new NoTrackFaceDialogFragment.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setResult(RESULT_NO_TRACK_FACE);
                        PosterChangeFaceActivity.this.finish();
                    }
                });
                noTrackFaceDialogFragment.show(PosterChangeFaceActivity.this.getSupportFragmentManager(), "NoTrackFaceDialogFragment");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFURenderer = null;
        mPhotoLandmarks = null;
        mTemplateLandmarks = null;
    }

    private void savePhoto(int textureId, final int texWidth, final int texHeight) {
        if (!mIsNeedSavePhoto || mIsSavingPhoto) {
            return;
        }
        Log.d(TAG, "savePhoto: " + textureId + ", width:" + texWidth + ", height:" + texHeight);
        mIsSavingPhoto = true;
        BitmapUtil.glReadBitmap(textureId, PosterPhotoRenderer.IMG_DATA_MATRIX, PhotoRenderer.ROTATE_90, texWidth, texHeight,
                new BitmapUtil.OnReadBitmapListener() {
                    @Override
                    public void onReadBitmapListener(final Bitmap bitmap) {
                        mMixedPhotoPath = FileUtils.getSavePath(PosterChangeFaceActivity.this);
                        try {
                            FileUtils.saveTempBitmap(bitmap, new File(mMixedPhotoPath));
                        } catch (IOException e) {
                            Log.e(TAG, "onReadBitmapListener: ", e);
                        }
                        mIsSavingPhoto = false;
                        mIsNeedSavePhoto = false;
                    }
                }, false);
    }

    private boolean checkRotation() {
        float[] rotations = mFURenderer.getRotationData();
        double x = rotations[0];
        double y = rotations[1];
        double z = rotations[2];
        double w = rotations[3];
        double yaw = Math.atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y)) / Math.PI * 180;
        double pitch = Math.asin(2 * (w * y - z * x)) / Math.PI * 180;
        double roll = Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z)) / Math.PI * 180;
        // 左右 pitch, 俯仰 yaw，摇摆 roll
        Log.i(TAG, "checkRotation: yaw:" + yaw + ". pitch:" + pitch + ". roll:" + roll);
        return yaw > 30 || yaw < -30 || pitch > 15 || pitch < -15;
    }

    private class ViewClickListener extends OnMultiClickListener {

        @Override
        protected void onMultiClick(View v) {
            int id = v.getId();
            if (id == R.id.iv_poster_save) {
                if (!mIsSavedPhoto) {
                    Toast.makeText(PosterChangeFaceActivity.this, getString(R.string.save_photo_success), Toast.LENGTH_SHORT).show();
                    String name = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".jpg";
                    try {
                        File resultFile = new File(Constant.photoFilePath, name);
                        if (mMixedPhotoPath != null) {
                            FileUtils.copyFile(new File(mMixedPhotoPath), resultFile);
                            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFile)));
                            mIsSavedPhoto = true;
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "onClick: copyFile", e);
                    }
                } else {
                    Toast.makeText(PosterChangeFaceActivity.this, getString(R.string.save_photo_success), Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.iv_poster_back) {
                onBackPressed();
            }
        }
    }

    public class PosterTempListAdapter extends BaseRecyclerAdapter<PosterChangeFaceTemplate> {

        PosterTempListAdapter(@NonNull List<PosterChangeFaceTemplate> data) {
            super(data, R.layout.layout_poster_template_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, PosterChangeFaceTemplate item) {
            ImageView imageView = viewHolder.getViewById(R.id.iv_poster_temp);
            Glide.with(viewHolder.itemView.getContext())
                    .load(item.getListIconPath())
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .into(imageView);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, PosterChangeFaceTemplate data, boolean selected) {
            viewHolder.setVisibility(R.id.iv_poster_bg, selected ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
