package com.faceunity.fulivedemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.fulivedemo.renderer.PhotoRenderer;
import com.faceunity.fulivedemo.ui.adapter.EffectRecyclerAdapter;
import com.faceunity.fulivedemo.ui.control.AnimControlView;
import com.faceunity.fulivedemo.ui.control.BeautyControlView;
import com.faceunity.fulivedemo.ui.control.MakeupControlView;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.utils.BitmapUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.MiscUtil;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class ShowPhotoActivity extends AppCompatActivity
        implements PhotoRenderer.OnRendererStatusListener,
        View.OnClickListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = ShowPhotoActivity.class.getSimpleName();

    private String mSelectDataType;
    private int mSelectEffectType;

    private GLSurfaceView mGLSurfaceView;
    private PhotoRenderer mPhotoRenderer;

    private TextView mIsTrackingText;
    private TextView mEffectDescription;
    private ImageView mSaveImageView;
    private BeautyControlView mBeautyControlView;
    private MakeupControlView mMakeupControlView;
    private AnimControlView mAnimControlView;
    private RecyclerView mEffectRecyclerView;
    private EffectRecyclerAdapter mEffectRecyclerAdapter;
    private FURenderer mFURenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_show_photo);

        Uri uri = getIntent().getData();
        mSelectDataType = getIntent().getStringExtra(SelectDataActivity.SELECT_DATA_KEY);
        mSelectEffectType = getIntent().getIntExtra(FUEffectActivity.SELECT_EFFECT_KEY, -1);
        if (uri == null) {
            onBackPressed();
            return;
        }
        String filePath = MiscUtil.getFileAbsolutePath(this, uri);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.show_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mPhotoRenderer = new PhotoRenderer(filePath, mGLSurfaceView, this);
        mGLSurfaceView.setRenderer(mPhotoRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //初始化FU相关 authpack 为证书文件
        mFURenderer = new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(0)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(null)
                .inputImageOrientation(360)
                .inputIsImage(1)
                .setCurrentCameraType(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setOnTrackingStatusChangedListener(this)
                .build();

        mIsTrackingText = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mSaveImageView = (ImageView) findViewById(R.id.show_save_btn);
        if (FUBeautyActivity.TAG.equals(mSelectDataType)) {
            mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
            mBeautyControlView.setVisibility(View.VISIBLE);
            mBeautyControlView.setOnFUControlListener(mFURenderer);
            mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
            mBeautyControlView.setOnDescriptionShowListener(new BeautyControlView.OnDescriptionShowListener() {
                @Override
                public void onDescriptionShowListener(int str) {
                    showDescription(str, 1500);
                }
            });
            mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBeautyControlView.hideBottomLayoutAnimator();
                }
            });
        } else if (FUMakeupActivity.TAG.equals(mSelectDataType)) {
            mMakeupControlView = findViewById(R.id.fu_makeup_control);
            mMakeupControlView.setVisibility(View.VISIBLE);
            mMakeupControlView.setOnFUControlListener(mFURenderer);
            mMakeupControlView.setOnBottomAnimatorChangeListener(new MakeupControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
        } else if (FUAnimojiActivity.TAG.equals(mSelectDataType)) {
            mAnimControlView = findViewById(R.id.fu_anim_control);
            mAnimControlView.setVisibility(View.VISIBLE);
            mAnimControlView.setOnFUControlListener(mFURenderer);
            mAnimControlView.setOnBottomAnimatorChangeListener(new AnimControlView.OnBottomAnimatorChangeListener() {
                @Override
                public void onBottomAnimatorChangeListener(float showRate) {
                    mSaveImageView.setAlpha(1 - showRate);
                }
            });
        } else {
            mEffectRecyclerView = (RecyclerView) findViewById(R.id.fu_effect_recycler);
            mEffectRecyclerView.setVisibility(View.VISIBLE);
            mEffectRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            mEffectRecyclerView.setAdapter(mEffectRecyclerAdapter = new EffectRecyclerAdapter(this, mSelectEffectType, mFURenderer));
            ((SimpleItemAnimator) mEffectRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            mFURenderer.setDefaultEffect(EffectEnum.getEffectsByEffectType(mSelectEffectType).get(1));
            mEffectRecyclerAdapter.setOnDescriptionChangeListener(new EffectRecyclerAdapter.OnDescriptionChangeListener() {
                @Override
                public void onDescriptionChangeListener(int description) {
                    showDescription(description, 1500);
                }
            });
        }

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mSaveImageView.getLayoutParams();
        params.bottomMargin = (int) getResources().getDimension(FUBeautyActivity.TAG.equals(mSelectDataType) ? R.dimen.x151 : R.dimen.x199);
        mSaveImageView.setLayoutParams(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPhotoRenderer.onCreate();
        if (mBeautyControlView != null)
            mBeautyControlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPhotoRenderer.onDestroy();
    }

    @Override
    public void onTrackingStatusChanged(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIsTrackingText.setVisibility(status > 0 ? View.INVISIBLE : View.VISIBLE);
            }
        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public int onDrawFrame(byte[] photoBytes, int photoTextureId, int photoWidth, int photoHeight) {
        int fuTextureId = mFURenderer.onDrawFrame(photoBytes, photoTextureId, photoWidth, photoHeight);
        checkPic(fuTextureId, photoWidth, photoHeight);
        return fuTextureId;
    }

    @Override
    public void onSurfaceDestroy() {
        mFURenderer.onSurfaceDestroyed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.show_save_btn:
                takePic();
                break;
        }
    }

    private boolean mTakePicing = false;
    private boolean mIsNeedTakePic = false;

    public void takePic() {
        if (mTakePicing) {
            return;
        }
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    public void checkPic(int textureId, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(textureId, PhotoRenderer.imgDataMatrix, PhotoRenderer.ROTATE_90, texWidth, texHeight, new BitmapUtil.OnReadBitmapListener() {
            @Override
            public void onReadBitmapListener(Bitmap bitmap) {
                String name = Constant.APP_NAME + "_" + MiscUtil.getCurrentDate() + ".jpg";
                String result = MiscUtil.saveBitmap(bitmap, Constant.photoFilePath, name);
                if (result != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(ShowPhotoActivity.this, R.string.save_photo_success);
                        }
                    });
                    File resultFile = new File(result);
                    // 最后通知图库更新
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFile)));
                }
                mTakePicing = false;
            }
        });
    }

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mEffectDescription.setText("");
            mEffectDescription.setVisibility(View.INVISIBLE);
        }
    };

    protected void showDescription(int str, int time) {
        if (0 == str) {
            return;
        }
        mEffectDescription.removeCallbacks(effectDescriptionHide);
        mEffectDescription.setVisibility(View.VISIBLE);
        mEffectDescription.setText(str);
        mEffectDescription.postDelayed(effectDescriptionHide, time);
    }
}
