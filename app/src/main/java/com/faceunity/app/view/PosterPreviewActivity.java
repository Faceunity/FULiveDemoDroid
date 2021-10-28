package com.faceunity.app.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.data.PosterChangeFaceDataFactory;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.core.callback.OnPosterRenderCallback;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.enumeration.FUFaceProcessorDetectModeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FUPosterKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.media.photo.OnPhotoRecordingListener;
import com.faceunity.core.media.photo.PhotoRecordHelper;
import com.faceunity.core.program.ProgramTexture2d;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.ui.control.PosterChangeFaceControlView;
import com.faceunity.ui.dialog.NoTrackFaceDialogFragment;
import com.faceunity.ui.dialog.ToastHelper;
import com.faceunity.ui.entity.PosterBean;
import com.faceunity.ui.widget.FaceMaskView;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * DESC：海报换脸结果预览
 * Created on 2021/3/3
 */
public class PosterPreviewActivity extends BaseActivity {
    public static String TEMPLATE = "template";
    public static String PHOTO = "photo";
    public static String INTENSITY = "intensity";

    public static void startActivity(Activity activity, String photo, String template, double intensity) {
        activity.startActivityForResult(
                new Intent(activity, PosterPreviewActivity.class)
                        .putExtra(TEMPLATE, template).putExtra(PHOTO, photo).putExtra(INTENSITY, intensity), PosterFaceAcquisitionActivity.REQ_PREVIEW
        );
    }

    /**
     * 照片、蒙版参数
     */
    private String mPhoto;
    private String mTemplate;
    private double mIntensity;


    private PosterChangeFaceDataFactory mPosterChangeFaceDataFactory;
    private FUPosterKit mFUPosterKit;
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    private FUAIKit mFUAIKit = FUAIKit.getInstance();

    private boolean isActivityPaused = true; // Activity生命周期，用于异步回调返回时候切换UI线程控制
    private boolean isFaceMaskFirst = true;
    private Bitmap mSavedBitmap;

    private PhotoRecordHelper mPhotoRecordHelper;

    //region onCreate

    /**
     * UI视图
     */
    private GLSurfaceView mGlSurfaceView;
    private PosterChangeFaceControlView mPosterChangeFaceControlView;
    private View mLoadingViewRoot;
    private ImageView mLoadingView;
    private FaceMaskView mFaceMaskView;
    private NoTrackFaceDialogFragment mDialog;
    private ImageView mSaveView;


    private Handler mMainHandler;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_poster_result;
    }

    @Override
    public void initData() {
        mPhoto = getIntent().getStringExtra(PHOTO);
        mTemplate = getIntent().getStringExtra(TEMPLATE);
        mIntensity = getIntent().getDoubleExtra(INTENSITY, 0.0);
        mPosterChangeFaceDataFactory = new PosterChangeFaceDataFactory(mTemplate, mPosterChangeFaceListener);
        mFUPosterKit = FUPosterKit.getInstance(new FUBundleData(DemoConfig.BUNDLE_POSTER_CHANGE_FACE), mOnPosterRenderCallback);
        mMainHandler = new Handler();
        mPhotoRecordHelper = new PhotoRecordHelper(mOnPhotoRecordingListener);
    }

    @Override
    public void initView() {
        mGlSurfaceView = findViewById(R.id.gl_surface);
        mPosterChangeFaceControlView = findViewById(R.id.control_poster);
        mLoadingViewRoot = findViewById(R.id.fyt_loading_view);
        mLoadingView = findViewById(R.id.iv_loading);
        Glide.with(this).load(R.drawable.bg_loading_gif).into(mLoadingView);
        mFaceMaskView = findViewById(R.id.face_mask);
        mSaveView = findViewById(R.id.iv_save);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void bindListener() {
        mFUAIKit.loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
        mFUAIKit.faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.IMAGE);
        mPosterChangeFaceControlView.bindDataFactory(mPosterChangeFaceDataFactory);
        mLoadingViewRoot.setOnTouchListener((view, event) -> true);
        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.iv_save).setOnClickListener(view -> {
            if (mSavedBitmap != null) {
                FileUtils.addBitmapToAlbum(this, mSavedBitmap);
                ToastHelper.showNormalToast(PosterPreviewActivity.this, R.string.save_photo_success);
            }
        });

        mFaceMaskView.setOnFaceSelectedListener((view, index) -> {
            if (!isActivityPaused) {
                mFaceMaskView.setVisibility(View.GONE);
                showLoadingView(true);
            }
            mGlSurfaceView.queueEvent(() -> mFUPosterKit.bindPhotoData(index));
        });
        mGlSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGlVersion(this));
        mGlSurfaceView.setRenderer(posterRenderer);
        mGlSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        showLoadingView(true);
    }

    //endregion onCreate
    //region 生命周期
    @Override
    protected void onResume() {
        super.onResume();
        isActivityPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityPaused = true;
    }

    @Override
    public void onBackPressed() {
        release();
        super.onBackPressed();
    }


    private void release() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        mGlSurfaceView.queueEvent(() -> {
            mFUPosterKit.onDestroy();
            mFURenderKit.release();
            destroyGlSurface();
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //endregion 生命周期
    //region 业务回调


    private PosterChangeFaceDataFactory.PosterChangeFaceListener mPosterChangeFaceListener = new PosterChangeFaceDataFactory.PosterChangeFaceListener() {
        /**
         * 蒙版切换调用
         */
        @Override
        public void onItemSelectedChange(PosterBean data) {
            showLoadingView(true);
            mSavedBitmap = null;
            mSaveView.setVisibility(View.GONE);
            mGlSurfaceView.queueEvent(() -> mFUPosterKit.updateTemplate(data.getPath(), data.getWarpIntensity()));
        }
    };


    /**
     * FUPosterKit回調
     */
    private OnPosterRenderCallback mOnPosterRenderCallback = new OnPosterRenderCallback() {

        /**
         * 合成结果返回
         * @param isSuccess
         * @param texId
         */
        @Override
        public void onMergeResult(boolean isSuccess, int texId) {
            runOnUiThread(() -> {
                showLoadingView(false);
                if (!isSuccess) {
                    ToastHelper.showNormalToast(PosterPreviewActivity.this, R.string.poster_change_face_error);

                }
                mergedTexId = texId;
                mGlSurfaceView.requestRender();
            });
        }

        /**
         * 蒙版加载回调
         * @param trackFace
         */
        @Override
        public void onTemplateLoaded(int trackFace) {
            if (trackFace <= 0) {
                runOnUiThread(() -> {
                    showLoadingView(false);
                    ToastHelper.showNormalToast(PosterPreviewActivity.this, R.string.poster_template_face_none);
                });
            }
        }

        /**
         * 照片加载回调
         * @param trackFace
         * @param array
         */
        @Override
        public void onPhotoLoaded(int trackFace, ArrayList<float[]> array) {
            switch (trackFace) {
                case -1:
                    showPromptFragment(R.string.dialog_face_rotation_not_valid);
                    break;
                case 0:
                    showPromptFragment(R.string.dialog_no_track_face);
                    break;
                default:
                    showFaceMaskView(array);
            }
        }
    };
//endregion
//region 业务调用

    /*是否显示加载动画 */
    private void showLoadingView(Boolean isShow) {
        mLoadingViewRoot.setVisibility(isShow ? View.VISIBLE : View.GONE);

    }

    /*提示弹框*/
    private void showPromptFragment(int strId) {
        if (!isActivityPaused) {
            runOnUiThread(() -> {
                        showLoadingView(false);
                        mDialog = new NoTrackFaceDialogFragment(this, strId);
                        mDialog.setOnDismissListener(() -> {
                            setResult(RESULT_OK);
                            release();
                            this.finish();
                        });
                        mDialog.show(getSupportFragmentManager(), "NoTrackFaceDialogFragment");
                    }
            );
        }
    }

    /* 头像选择蒙版 */
    private void showFaceMaskView(ArrayList<float[]> array) {
        if (!isActivityPaused && isFaceMaskFirst) {
            runOnUiThread(() -> {
                mFaceMaskView.clean();
                for (float[] face : array) {
                    mFaceMaskView.addFaceRect(face);
                }
                showLoadingView(false);
                mFaceMaskView.invalidate();
                mFaceMaskView.setVisibility(View.VISIBLE);
                isFaceMaskFirst = false;
            });
        }
    }
    //endregion 业务调用

    //region surfaceRender


    /**
     * 纹理
     **/
    private int mergedTexId = 0;
    private int mPhotoTextureId = 0;
    private ProgramTexture2d mProgramTexture2d;

    /**
     * 换算矩阵
     **/
    private float[] mvpRenderMatrix;
    private float[] mvpPhotoMatrix;

    /**
     * 照片相关参数
     **/
    private Bitmap mPhotoBitmap;
    private int mPhotoWidth = 720;
    private int mPhotoHeight = 1280;
    private boolean isFirstRender = true;
    /**
     * Suface尺寸
     **/
    private int mViewWidth;
    private int mViewHeight;

    /**
     * 渲染次数
     */
    private int currentFrame = 0;


    private final GLSurfaceView.Renderer posterRenderer = new GLSurfaceView.Renderer() {


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mProgramTexture2d = new ProgramTexture2d();
            mPhotoBitmap = FileUtils.loadBitmapFromExternal(mPhoto, 1080);
            mPhotoTextureId = GlUtil.createImageTexture(mPhotoBitmap);
            mPhotoWidth = mPhotoBitmap.getWidth();
            mPhotoHeight = mPhotoBitmap.getHeight();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            if (mViewWidth != width || mViewHeight != height) {
                mFUPosterKit.bindSurfaceSize(width, height);
                mViewWidth = width;
                mViewHeight = height;
                mvpPhotoMatrix = GlUtil.changeMvpMatrixInside((float) width, (float) height, (float) mPhotoWidth, (float) mPhotoHeight);
                Matrix.scaleM(mvpPhotoMatrix, 0, 1f, -1f, 1f);
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (mProgramTexture2d == null) {
                mProgramTexture2d = new ProgramTexture2d();
            }
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if (mergedTexId <= 0) {
                mProgramTexture2d.drawFrame(mPhotoTextureId, GlUtil.IDENTITY_MATRIX, mvpPhotoMatrix);
            } else {
                if (mvpRenderMatrix == null) {
                    mvpRenderMatrix = GlUtil.changeMvpMatrixInside((float) mViewWidth, (float) mViewHeight, (float) mFUPosterKit.getTemplateWidth(), (float) mFUPosterKit.getTemplateHeight());
                }
                mProgramTexture2d.drawFrame(mergedTexId, GlUtil.IDENTITY_MATRIX, mvpRenderMatrix);
                mPhotoRecordHelper.sendRecordingData(mergedTexId, GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX, mFUPosterKit.getTemplateWidth(), mFUPosterKit.getTemplateHeight());
            }
            if (currentFrame++ <= 1) {
                mMainHandler.postDelayed(() -> mGlSurfaceView.requestRender(), 30);
                return;
            }
            if (isFirstRender) {
                isFirstRender = false;
                mFUPosterKit.renderPoster(mPhotoBitmap, mPhotoTextureId, mTemplate, mIntensity);
            }

        }
    };

    /**
     * 釋放GL线程相关资源
     */
    private void destroyGlSurface() {
        if (mergedTexId != 0) {
            int[] textures = new int[]{mergedTexId};
            GLES20.glDeleteTextures(1, textures, 0);
            mergedTexId = 0;
        }
        if (mPhotoTextureId != 0) {
            int[] textures = new int[]{mPhotoTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mPhotoTextureId = 0;
        }
        if (mProgramTexture2d != null) {
            mProgramTexture2d.release();
            mProgramTexture2d = null;
        }
    }

    /**
     * 获取拍摄的照片
     */
    private final OnPhotoRecordingListener mOnPhotoRecordingListener = bitmap -> {
        mSavedBitmap = bitmap;
        runOnUiThread(() -> mSaveView.setVisibility(View.VISIBLE));
    };


}
