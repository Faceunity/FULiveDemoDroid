package com.faceunity.fulivedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
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
import com.faceunity.fulivedemo.entity.PosterTemplate;
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
import com.faceunity.fulivedemo.renderer.PosterPhotoRenderer;
import com.faceunity.fulivedemo.ui.FaceMaskView;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.NoTrackFaceDialogFragment;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 海报换脸效果界面
 */
public class FUPosterFaceActivity extends AppCompatActivity implements View.OnClickListener,
        PosterPhotoRenderer.OnRendererStatusListener, BaseRecyclerAdapter.OnItemClickListener<PosterTemplate> {
    public static final String PHOTO_PATH = "photo_path";
    public static final String TEMPLATE_PATH = "template_path";
    public static final int REQ_TRACK_FACE = 702;
    public static final int RESULT_NO_TRACK_FACE = -1;
    private static final String TAG = "FUPosterFaceActivity";
    private static final int FACE_HORIZONTAL_ROTATION = 15;
    private static final int FACE_VERTICAL_ROTATION_MIN = -15;
    private static final int FACE_VERTICAL_ROTATION_MAX = 5;
    protected GLSurfaceView mGlSurfaceView;
    private FURenderer mFURenderer;
    private PosterPhotoRenderer mPosterPhotoRenderer;
    private volatile boolean mIsNeedSavePhoto;
    private volatile boolean mIsSavingPhoto;
    private boolean mIsTrackedTemplate;
    private boolean mIsTrackedPhoto;
    private volatile boolean mIsFirstDraw = true;
    private boolean mIsNeedReInput = true;
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
        Intent intent = new Intent(activity, FUPosterFaceActivity.class);
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
//        int supportGLVersion = GlUtil.getSupportGLVersion(this);
        mGlSurfaceView.setEGLContextClientVersion(2);
        mPosterPhotoRenderer = new PosterPhotoRenderer(photoPath, mGlSurfaceView, this);
        mGlSurfaceView.setRenderer(mPosterPhotoRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        findViewById(R.id.iv_poster_back).setOnClickListener(this);
        findViewById(R.id.iv_poster_save).setOnClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_poster_template);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        int verticalPadding = getResources().getDimensionPixelSize(R.dimen.x24);
        int horizonPadding = getResources().getDimensionPixelSize(R.dimen.x10);
        VHSpaceItemDecoration itemDecoration = new VHSpaceItemDecoration(verticalPadding, horizonPadding) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int adapterPosition = parent.getChildAdapterPosition(view);
                if (adapterPosition == 0) {
                    outRect.left += getResources().getDimensionPixelSize(R.dimen.x24);
                }
            }
        };
        recyclerView.addItemDecoration(itemDecoration);
        List<PosterTemplate> listPosterTemplates = PosterTemplate.getPosterTemplates(this);
        PosterTempListAdapter adapter = new PosterTempListAdapter(listPosterTemplates);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
        mLastPosition = PosterTemplate.findSelectedIndex(listPosterTemplates, mTemplatePath);
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
        mLoadingView.setVisibility(View.VISIBLE);

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
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
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
                mLoadingView.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
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
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_poster_save) {
            if (!mIsSavedPhoto) {
                Toast.makeText(FUPosterFaceActivity.this, getString(R.string.save_photo_success), Toast.LENGTH_SHORT).show();
                String name = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".jpg";
                try {
                    File resultFile = new File(Constant.photoFilePath, name);
                    FileUtils.copyFile(new File(mMixedPhotoPath), resultFile);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFile)));
                    mIsSavedPhoto = true;
                } catch (IOException e) {
                    Log.e(TAG, "onClick: copyFile", e);
                }
            } else {
                Toast.makeText(FUPosterFaceActivity.this, getString(R.string.save_photo_success), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_poster_back) {
            onBackPressed();
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
            mPhotoLandmarks = mFURenderer.getLandmarksData(mFaceIndex);
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
                                mPhotoLandmarks = mFURenderer.getLandmarksData(mFaceIndex);
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
                                    mLoadingView.setVisibility(View.VISIBLE);
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
                        mLoadingView.setVisibility(View.INVISIBLE);
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
                    mPhotoLandmarks = mFURenderer.getLandmarksData(mFaceIndex);
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

    @Override
    public void onTemplateLoaded(byte[] img, int width, int height) {
        Log.d(TAG, "onTemplateLoaded() called width = [" + width + "], height = [" + height + "]");
        if (mFURenderer.trackFace(img, width, height) > 0) {
            mTemplateLandmarks = mFURenderer.getLandmarksData(0);
            mIsTrackedTemplate = true;
            mIsTrackedPhoto = true;
            mIsNeedReInput = true;
            Log.i(TAG, "onTemplateLoaded: tracking template > 0--------- ");
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FUPosterFaceActivity.this, "未识别模板的人脸，请重新选择模板", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter<PosterTemplate> adapter, View view, final int position) {
        if (mLastPosition == position) {
            return;
        }
        mLastPosition = position;
        mIsTrackedTemplate = false;
        mIsSavedPhoto = false;
        mLoadingView.setVisibility(View.VISIBLE);
        mState = STATE_CHANGING;
        PosterTemplate posterTemplate = adapter.getItem(position);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFURenderer = null;
        mPhotoLandmarks = null;
        mTemplateLandmarks = null;
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
                        FUPosterFaceActivity.this.finish();
                    }
                });
                noTrackFaceDialogFragment.show(FUPosterFaceActivity.this.getSupportFragmentManager(), "NoTrackFaceDialogFragment");
            }
        });
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
        Log.i(TAG, "checkRotation: yaw:" + yaw + ". pitch:" + pitch + ". roll:" + roll);
        return yaw > FACE_VERTICAL_ROTATION_MAX || yaw < FACE_VERTICAL_ROTATION_MIN || pitch >
                FACE_HORIZONTAL_ROTATION || pitch < -FACE_HORIZONTAL_ROTATION || roll > FACE_HORIZONTAL_ROTATION || roll < -FACE_HORIZONTAL_ROTATION;
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
                        mMixedPhotoPath = FileUtils.getSavePath(FUPosterFaceActivity.this);
                        try {
                            FileUtils.saveTempBitmap(bitmap, new File(mMixedPhotoPath));
                        } catch (IOException e) {
                            Log.e(TAG, "onReadBitmapListener: ", e);
                        }
                        mIsSavingPhoto = false;
                        mIsNeedSavePhoto = false;
                    }
                });
    }

    public class PosterTempListAdapter extends BaseRecyclerAdapter<PosterTemplate> {

        PosterTempListAdapter(@NonNull List<PosterTemplate> data) {
            super(data, R.layout.layout_poster_template_recycler);
        }

        @Override
        protected void bindViewHolder(BaseViewHolder viewHolder, PosterTemplate item) {
            ImageView imageView = viewHolder.getViewById(R.id.iv_poster_temp);
            Glide.with(viewHolder.itemView.getContext())
                    .load(item.getListIconPath())
                    .apply(new RequestOptions().transform(new CenterCrop()))
                    .into(imageView);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, PosterTemplate data, boolean selected) {
            viewHolder.setVisibility(R.id.iv_poster_bg, selected ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
