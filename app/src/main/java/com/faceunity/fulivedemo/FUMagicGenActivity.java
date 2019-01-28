package com.faceunity.fulivedemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.renderer.MagicPhotoRenderer;
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
import com.faceunity.fulivedemo.ui.dialog.BaseDialogFragment;
import com.faceunity.fulivedemo.ui.dialog.ConfirmDialogFragment;
import com.faceunity.fulivedemo.ui.fragment.FaceAdjustFragment;
import com.faceunity.fulivedemo.ui.fragment.FaceMarkFragment;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.MiscUtil;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 异图模板生成页
 */
public class FUMagicGenActivity extends AppCompatActivity implements MagicPhotoRenderer.OnRendererStatusListener,
        FaceAdjustFragment.OnBackClickListener {
    public static final String MODEL_PATH = "model_path";
    private FURenderer mFURenderer;
    private MagicPhotoRenderer mMagicPhotoRenderer;
    private boolean mIsSavedModel;
    protected volatile boolean mIsNeedTakePic;
    private OnCheckPicListener mOnCheckPicListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_fumagic_gen);
        GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface);
        glSurfaceView.setEGLContextClientVersion(3);
        String path = getModelPath();
        mMagicPhotoRenderer = new MagicPhotoRenderer(path, glSurfaceView, this);
        glSurfaceView.setRenderer(mMagicPhotoRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        showFragment(FaceMarkFragment.TAG);

        mFURenderer = new FURenderer
                .Builder(this)
                .setNeedFaceBeauty(false)
                .inputTextureType(2)
                .build();
    }

    public Fragment showFragment(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        Fragment markFg = fragmentManager.findFragmentByTag(FaceMarkFragment.TAG);
        Fragment adjustFg = fragmentManager.findFragmentByTag(FaceAdjustFragment.TAG);
        Fragment showFragment = null;
        if (FaceMarkFragment.TAG.equals(tag)) {
            if (adjustFg != null) {
                transaction.hide(adjustFg);
            }
            if (markFg != null) {
                mMagicPhotoRenderer.resetScale(null);
                transaction.show(markFg);
            } else {
                markFg = new FaceMarkFragment();
                transaction.add(R.id.fl_fragment, markFg, tag);
            }
            showFragment = markFg;
        } else if (FaceAdjustFragment.TAG.equals(tag)) {
            if (markFg != null) {
                transaction.hide(markFg);
            }
            if (adjustFg != null) {
                transaction.show(adjustFg);
            } else {
                adjustFg = new FaceAdjustFragment();
                ((FaceAdjustFragment) adjustFg).setOnBackClickListener(this);
                transaction.add(R.id.fl_fragment, adjustFg, tag);
            }
            showFragment = adjustFg;
        }
        transaction.commit();
        return showFragment;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onLoadImageError() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtil.makeFineToast(FUMagicGenActivity.this, "图片加载出错", R.drawable.icon_fail).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FUMagicGenActivity.this.onBackPressed();
                    }
                }, 1500);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMagicPhotoRenderer.onCreate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMagicPhotoRenderer.onDestroy();

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
        BitmapUtil.glReadBitmap(textureId, PhotoRenderer.imgDataMatrix, mvpMatrix,
                texWidth, texHeight, new BitmapUtil.OnReadBitmapListener() {

                    @Override
                    public void onReadBitmapListener(Bitmap bitmap) {
                        final String result = MiscUtil.saveBitmap(bitmap, FileUtils.getMagicPhotoDir(FUMagicGenActivity.this)
                                .getAbsolutePath(), FileUtils.getUUID32() + ".jpg");
                        if (result != null && mOnCheckPicListener != null) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mOnCheckPicListener.onPhotoChecked(result);
                                }
                            });
                        }
                    }
                });
    }

    public void setNeedTakePic(boolean needTakePic, OnCheckPicListener onCheckPicListener) {
        mIsNeedTakePic = needTakePic;
        mOnCheckPicListener = onCheckPicListener;
    }

    @Override
    public void onBackPressed() {
        final FaceMarkFragment faceMarkFragment = (FaceMarkFragment) getSupportFragmentManager().findFragmentByTag(FaceMarkFragment.TAG);
        if (mIsSavedModel) {
            setResult(Activity.RESULT_OK);
            faceMarkFragment.hideStickerLayout();
            super.onBackPressed();
        } else {
            if (faceMarkFragment.isVisible()) {
                if (faceMarkFragment.getStickerCount() > 0) {
                    ConfirmDialogFragment.newInstance(getString(R.string.magic_back_not_save), new BaseDialogFragment.OnClickListener() {
                        @Override
                        public void onConfirm() {
                            setResult(Activity.RESULT_CANCELED);
                            faceMarkFragment.hideStickerLayout();
                            FUMagicGenActivity.super.onBackPressed();
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
                FaceAdjustFragment faceAdjustFragment = (FaceAdjustFragment) getSupportFragmentManager().findFragmentByTag(FaceAdjustFragment.TAG);
                faceAdjustFragment.onClick(null);
            }
        }
    }

    @Override
    public void onClick(float[] landmarkPoints) {
        FaceMarkFragment faceMarkFragment = (FaceMarkFragment) getSupportFragmentManager().findFragmentByTag(FaceMarkFragment.TAG);
        faceMarkFragment.setFixedLandmarkPoints(landmarkPoints);
    }

    public void setSavedModel(boolean savedModel) {
        mIsSavedModel = savedModel;
    }

    public String getModelPath() {
        return getIntent().getStringExtra(MODEL_PATH);
    }

    public MagicPhotoRenderer getMagicPhotoRenderer() {
        return mMagicPhotoRenderer;
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
