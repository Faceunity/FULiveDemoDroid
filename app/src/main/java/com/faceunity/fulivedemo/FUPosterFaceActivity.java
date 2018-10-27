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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.entity.PosterTemplate;
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
import com.faceunity.fulivedemo.renderer.PosterPhotoRenderer;
import com.faceunity.fulivedemo.ui.FaceMaskView;
import com.faceunity.fulivedemo.ui.adapter.BaseRecyclerAdapter;
import com.faceunity.fulivedemo.ui.adapter.VHSpaceItemDecoration;
import com.faceunity.fulivedemo.ui.dialog.NoTrackFaceDialogFragment;
import com.faceunity.fulivedemo.utils.BitmapUtil;
import com.faceunity.fulivedemo.utils.FileUtils;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.utils.Constant;
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
        PosterPhotoRenderer.OnRendererStatusListener, BaseRecyclerAdapter.OnItemClickListener {
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
    private volatile boolean mFirstDraw = true;
    private boolean mReDrawFrame = true;
    // 是否已经生成海报
    private volatile boolean mIsMixedPhoto;
    private int mTexId;
    private float[] mTemplateLandmarks;
    private float[] mPhotoLandmarks;
    private View mLoadingView;
    private volatile boolean mMultiFace;
    private ConstraintLayout mClContainerView;
    private String mTemplatePath;
    private volatile boolean mActivityStopped;
    private String mMixedPhotoPath;
    // 混合后的海报不合法
    private boolean mMixTexIdInvalid;
    private boolean mSavedPhoto;
    private volatile boolean mLoadFromCache;

    public static void startSelfActivity(Activity activity, String templatePath, String photoPath) {
        Intent intent = new Intent(activity, FUPosterFaceActivity.class);
        intent.putExtra(TEMPLATE_PATH, templatePath);
        intent.putExtra(PHOTO_PATH, photoPath);
        activity.startActivityForResult(intent, REQ_TRACK_FACE);
    }

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
        mGlSurfaceView = (GLSurfaceView) findViewById(R.id.gl_surface);
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
        int selectedIndex = PosterTemplate.findSelectedIndex(listPosterTemplates, mTemplatePath);
        adapter.setItemSelected(selectedIndex);

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
        Glide.with(this).load(R.drawable.loading_gif).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(ivLoading);
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
        Log.d(TAG, "onResume: ");
        mActivityStopped = false;
        if (mIsMixedPhoto) {
            mIsNeedSavePhoto = false;
            mLoadFromCache = true;
            mPosterPhotoRenderer.setMixedPhotoPath(mMixedPhotoPath);
        }
        mPosterPhotoRenderer.onCreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        mActivityStopped = true;
        if (mIsMixedPhoto) {
            setSavePhotoFlag();
        } else {
            mPosterPhotoRenderer.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_poster_save) {
            if (!mSavedPhoto) {
                mSavedPhoto = true;
                setSavePhotoFlag();
            } else {
                Toast.makeText(FUPosterFaceActivity.this, getString(R.string.save_photo_success), Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.iv_poster_back) {
            onBackPressed();
        }
    }

    private void setSavePhotoFlag() {
        if (mIsSavingPhoto) {
            return;
        }
        mIsNeedSavePhoto = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
        mFirstDraw = true;
        mReDrawFrame = true;
        mIsTrackedTemplate = false;
        mIsTrackedPhoto = false;
        mMixTexIdInvalid = false;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight) {
        int texId = mTexId;
        if (mReDrawFrame) {
            Log.i(TAG, "onDrawFrame: input params. isTrackTemplate:" + mIsTrackedTemplate +
                    ", isTrackPhoto:" + mIsTrackedPhoto + ", multiFace:" + mMultiFace);
            if (!mMixTexIdInvalid) {
                if (mIsTrackedTemplate && !mMultiFace) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingView.setVisibility(View.VISIBLE);
                        }
                    });
                    mFURenderer.onPosterTemplateSelected(mPosterPhotoRenderer.getTemplateWidth(), mPosterPhotoRenderer.getTemplateHeight(),
                            mPosterPhotoRenderer.getTemplateRGBABytes(), mTemplateLandmarks);
                }

                if (mIsTrackedPhoto && !mMultiFace) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingView.setVisibility(View.VISIBLE);
                        }
                    });
                    if (mPhotoLandmarks != null) {
                        mFURenderer.onPosterInputPhoto(photoWidth, photoHeight, mPosterPhotoRenderer.getPhotoRGBABytes(), mPhotoLandmarks);
                        mPosterPhotoRenderer.setDrawPhoto(false);
                    }
                }

                if ((mIsTrackedPhoto || mIsTrackedTemplate) && !mMultiFace) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingView.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }

            if (!mFirstDraw && !mMultiFace) {
                texId = mFURenderer.onDrawFrame(mPosterPhotoRenderer.getTemplateBytes(), mPosterPhotoRenderer.getTemplateWidth(),
                        mPosterPhotoRenderer.getTemplateHeight());
                if (texId == 0) {
                    mReDrawFrame = true;
                    if (!mLoadFromCache) {
                        mIsMixedPhoto = false;
                    }
                    if (mPosterPhotoRenderer.getTemplateBytes() != null) {
                        mMixTexIdInvalid = true;
                    }
                } else {
                    mIsMixedPhoto = true;
                    mReDrawFrame = false;
                    mMixTexIdInvalid = false;
                }
                mIsNeedSavePhoto = false;
                mTexId = texId = texId > 0 ? texId : mTexId;
                Log.i(TAG, "onDrawFrame: draw template:" + texId);
            } else {
                mTexId = texId = photoTextureId;
                mPosterPhotoRenderer.setDrawPhoto(true);
                mFirstDraw = false;
                Log.i(TAG, "onDrawFrame: draw photo:" + texId);
            }
        }
        savePhoto(texId, mPosterPhotoRenderer.getTemplateWidth(), mPosterPhotoRenderer.getTemplateHeight());
//        Log.i(TAG, "onDrawFrame: final texId " + texId);
        return texId;
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
        mTexId = 0;
        mReDrawFrame = true;
        mIsTrackedTemplate = false;
        mIsTrackedPhoto = false;
        mFirstDraw = true;
    }

    @Override
    public void onPhotoLoaded(byte[] img, int width, int height) {
        final int trackFace = mFURenderer.trackFace(img, width, height);
        if (trackFace > 0) {
            if (trackFace > 1) {
                // 多人脸
                mMultiFace = true;
                final FaceMaskView faceMaskView = new FaceMaskView(this);
                faceMaskView.setOnFaceSelectedListener(new FaceMaskView.OnFaceSelectedListener() {
                    @Override
                    public void onFaceSelected(final View view, final int index) {
                        Log.i(TAG, "onFaceSelected: " + index);
                        mMultiFace = false;
                        mFirstDraw = true;
                        mGlSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                mPhotoLandmarks = mFURenderer.getLandmarksData(index);
                                mReDrawFrame = true;
                                mPosterPhotoRenderer.reloadTemplateData(mTemplatePath);
                            }
                        });
                        view.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mClContainerView.removeView(view);
                            }
                        }, 500);
                    }
                });
                mPosterPhotoRenderer.setDrawPhoto(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < trackFace; i++) {
                            float[] faceRectData = mFURenderer.getFaceRectData(i);
                            float[] newFaceRect = mPosterPhotoRenderer.convertFaceRect(faceRectData);
                            faceMaskView.addFaceRect(newFaceRect);
                        }
                        mLoadingView.setVisibility(View.INVISIBLE);
                        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                                ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
                        mClContainerView.addView(faceMaskView, params);
                    }
                });
            } else {
                boolean ret = checkRotation();
                if (ret) {
                    mReDrawFrame = false;
                    mPosterPhotoRenderer.setDrawPhoto(true);
                    showPromptFragment(R.string.dialog_face_rotation_not_valid);
                } else {
                    mPhotoLandmarks = mFURenderer.getLandmarksData(0);
                    mIsTrackedPhoto = true;
                    mReDrawFrame = true;
                    mPosterPhotoRenderer.reloadTemplateData(mTemplatePath);
                }
            }
            Log.i(TAG, "onPhotoLoaded: tracking photo > 0--------- ");
        } else {
            mReDrawFrame = false;
            mPosterPhotoRenderer.setDrawPhoto(true);
            showPromptFragment(R.string.dialog_no_track_face);
        }
    }

    @Override
    public void onTemplateLoaded(byte[] img, int width, int height) {
        if (mFURenderer.trackFace(img, width, height) > 0) {
            mTemplateLandmarks = mFURenderer.getLandmarksData(0);
            mIsTrackedTemplate = true;
            mIsTrackedPhoto = true;
            mReDrawFrame = true;
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
    public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
        mLoadingView.setVisibility(View.VISIBLE);
        PosterTemplate posterTemplate = (PosterTemplate) adapter.getItem(position);
        mIsTrackedTemplate = false;
        mSavedPhoto = false;
        mTemplatePath = posterTemplate.getPath();
        mFURenderer.onPosterTemplateChanged();
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
                noTrackFaceDialogFragment.show(FUPosterFaceActivity.this.getFragmentManager(), "NoTrackFaceDialogFragment");
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
        Log.d(TAG, "savePhoto: " + textureId);
        mIsSavingPhoto = true;
        BitmapUtil.glReadBitmap(textureId, PosterPhotoRenderer.IMG_DATA_MATRIX, PhotoRenderer.ROTATE_90, texWidth, texHeight,
                new BitmapUtil.OnReadBitmapListener() {
                    @Override
                    public void onReadBitmapListener(final Bitmap bitmap) {
                        if (mActivityStopped) {
                            if (mIsMixedPhoto) {
                                try {
                                    mMixedPhotoPath = FileUtils.saveTempBitmap(bitmap, new File(FileUtils.getFileDir(FUPosterFaceActivity.this),
                                            FileUtils.TMP_PHOTO_POSTER_NAME));
                                    Log.i(TAG, "onReadBitmapListener: bmpPath:" + mMixedPhotoPath);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (mActivityStopped) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mPosterPhotoRenderer.onDestroy();
                                        }
                                    });
                                }
                            }
                        } else {
                            String name = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".jpg";
                            final String result = MiscUtil.saveBitmap(bitmap, Constant.photoFilePath, name);
                            if (result != null && !mActivityStopped) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FUPosterFaceActivity.this, getString(R.string.save_photo_success), Toast.LENGTH_SHORT).show();
                                        File resultFile = new File(result);
                                        // 最后通知图库更新
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFile)));
                                    }
                                });
                            }
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
            Glide.with(viewHolder.itemView.getContext()).load(item.getListIconPath()).centerCrop().into(imageView);
        }

        @Override
        protected void handleSelectedState(BaseViewHolder viewHolder, PosterTemplate data, boolean selected) {
            viewHolder.setVisibility(R.id.iv_poster_bg, selected ? View.VISIBLE : View.INVISIBLE);
        }
    }
}
