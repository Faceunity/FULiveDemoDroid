package com.faceunity.fulivedemo.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.faceunity.FURenderer;
import com.faceunity.entity.LivePhoto;
import com.faceunity.fulivedemo.R;
import com.faceunity.fulivedemo.renderer.LivePhotoRenderer;
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.fragment.LivePhotoAdjustFragment;
import com.faceunity.fulivedemo.ui.fragment.LivePhotoMarkFragment;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.util.Arrays;

/**
 * 表情动图模板生成页
 *
 * @author Richie on 2019.04.12
 */
public class LivePhotoMakeActivity extends AppCompatActivity implements LivePhotoRenderer.OnRendererStatusListener,
        LivePhotoAdjustFragment.OnBackClickListener {
    public static final String MODEL_PATH = "model_path";
    public static final String EDIT_LIVE_PHOTO = "edit_live_photo";
    public static final int TOAST_DELAY = 1200;
    private FURenderer mFURenderer;
    private LivePhotoRenderer mLivePhotoRenderer;
    private boolean mIsSavedModel;
    protected volatile boolean mIsNeedTakePic;
    private OnCheckPicListener mOnCheckPicListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_livephoto_make);
        GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface);
        glSurfaceView.setEGLContextClientVersion(GlUtil.getSupportGLVersion(this));
        String path = getIntent().getStringExtra(MODEL_PATH);
        mLivePhotoRenderer = new LivePhotoRenderer(path, glSurfaceView, this);
        glSurfaceView.setRenderer(mLivePhotoRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        showFragment(LivePhotoMarkFragment.TAG);

        mFURenderer = new FURenderer
                .Builder(this)
                .setNeedFaceBeauty(false)
                .inputTextureType(2)
                .build();
    }

    public LivePhoto getEditableLivePhoto() {
        return getIntent().getParcelableExtra(EDIT_LIVE_PHOTO);
    }

    public FURenderer getFURenderer() {
        return mFURenderer;
    }

    public Fragment showFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment markFg = fragmentManager.findFragmentByTag(LivePhotoMarkFragment.TAG);
        Fragment adjustFg = fragmentManager.findFragmentByTag(LivePhotoAdjustFragment.TAG);
        Fragment showFragment = null;
        if (LivePhotoMarkFragment.TAG.equals(tag)) {
            if (adjustFg != null) {
                transaction.hide(adjustFg);
            }
            if (markFg != null) {
                mLivePhotoRenderer.resetScale(null);
                transaction.show(markFg);
            } else {
                markFg = new LivePhotoMarkFragment();
                transaction.add(R.id.fl_fragment, markFg, tag);
            }
            showFragment = markFg;
        } else if (LivePhotoAdjustFragment.TAG.equals(tag)) {
            if (markFg != null) {
                transaction.hide(markFg);
            }
            if (adjustFg != null) {
                transaction.show(adjustFg);
            } else {
                adjustFg = new LivePhotoAdjustFragment();
                ((LivePhotoAdjustFragment) adjustFg).setOnBackClickListener(this);
                transaction.add(R.id.fl_fragment, adjustFg, tag);
            }
            showFragment = adjustFg;
        }
        transaction.commit();
        return showFragment;
    }

    @Override
    public void onSurfaceCreated() {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onLoadPhotoError(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.makeFineToast(LivePhotoMakeActivity.this, error, R.drawable.icon_fail).show();
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LivePhotoMakeActivity.this.onBackPressed();
                    }
                }, TOAST_DELAY);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLivePhotoRenderer.onCreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLivePhotoRenderer.onDestroy();

    }

    @Override
    public int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight) {
        int tex = mFURenderer.onDrawFrame(photoBytes, photoTextureId, photoWidth, photoHeight);
        checkPic(tex, photoWidth, photoHeight);
        return tex;
    }

    protected void checkPic(int textureId, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        float[] mvpMatrix = Arrays.copyOf(GlUtil.IDENTITY_MATRIX, GlUtil.IDENTITY_MATRIX.length);
        Matrix.rotateM(mvpMatrix, 0, 270, 0, 0, 1);
        Matrix.scaleM(mvpMatrix, 0, -1, -1, 1);
        BitmapUtil.glReadBitmap(textureId, PhotoRenderer.IMG_DATA_MATRIX, mvpMatrix,
                texWidth, texHeight, new BitmapUtil.OnReadBitmapListener() {

                    @Override
                    public void onReadBitmapListener(Bitmap bitmap) {
                        final String result = MiscUtil.saveBitmap(bitmap, FileUtils.getLivePhotoDir(LivePhotoMakeActivity.this)
                                .getAbsolutePath(), FileUtils.getUUID32() + MiscUtil.IMAGE_FORMAT_JPG);
                        if (result != null && mOnCheckPicListener != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnCheckPicListener.onPhotoChecked(result);
                                }
                            });
                        }
                    }
                }, false);
    }

    public void setNeedTakePic(boolean needTakePic, OnCheckPicListener onCheckPicListener) {
        mIsNeedTakePic = needTakePic;
        mOnCheckPicListener = onCheckPicListener;
    }

    @Override
    public void onBackPressed() {
        final LivePhotoMarkFragment faceMarkFragment = (LivePhotoMarkFragment) getSupportFragmentManager().findFragmentByTag(LivePhotoMarkFragment.TAG);
        if (mIsSavedModel) {
            setResult(Activity.RESULT_OK);
            faceMarkFragment.hideStickerLayout();
            super.onBackPressed();
        } else {
            if (faceMarkFragment.isVisible()) {
                if (faceMarkFragment.shouldShowConfirmDialog()) {
                    ConfirmDialogFragment.newInstance(getString(R.string.live_photo_back_not_save), new BaseDialogFragment.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            setResult(Activity.RESULT_CANCELED);
                            faceMarkFragment.hideStickerLayout();
                            LivePhotoMakeActivity.super.onBackPressed();
                        }

                        @Override
                        public void onCancel() {
                        }
                    }).show(getSupportFragmentManager(), "ConfirmDialogFragment");
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    faceMarkFragment.hideStickerLayout();
                    super.onBackPressed();
                }
            } else {
                LivePhotoAdjustFragment faceAdjustFragment = (LivePhotoAdjustFragment) getSupportFragmentManager().findFragmentByTag(LivePhotoAdjustFragment.TAG);
                faceAdjustFragment.onSaveClick();
            }
        }
    }

    @Override
    public void onClick(float[] pointsOfLandmark, float[] pointsOfView) {
        LivePhotoMarkFragment faceMarkFragment = (LivePhotoMarkFragment) getSupportFragmentManager().findFragmentByTag(LivePhotoMarkFragment.TAG);
        faceMarkFragment.setAdjustPoints(pointsOfLandmark, pointsOfView);
    }

    public void setSavedModel(boolean savedModel) {
        mIsSavedModel = savedModel;
    }

    public LivePhotoRenderer getLivePhotoRenderer() {
        return mLivePhotoRenderer;
    }

    public interface OnCheckPicListener {
        /**
         * 屏幕图片快照
         *
         * @param path
         */
        void onPhotoChecked(String path);
    }

}
