package com.faceunity.fulivedemo;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.entity.BeautyParameterModel;
import com.faceunity.fulivedemo.ui.BeautyControlView;
import com.faceunity.utils.Constant;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 美颜界面
 * Created by tujh on 2018/1/31.
 */

public class FUBeautyActivity extends FUBaseUIActivity
        implements FURenderer.OnFUDebugListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = FUBeautyActivity.class.getSimpleName();

    private byte[] mFuNV21Byte;

    private BeautyControlView mBeautyControlView;
    private FURenderer mFURenderer;

    @Override
    protected void onCreate() {

        mHeightCheckBox.setVisibility(View.VISIBLE);
        mHeightImg.setVisibility(View.VISIBLE);
        mHeightCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHeightImg.setImageResource(isChecked ? R.drawable.performance_checked : R.drawable.performance_normal);
                mBeautyControlView.setHeightPerformance(isChecked);
            }
        });
        //初始化FU相关 authpack 为证书文件
        mFURenderer = new FURenderer
                .Builder(this)
                .maxFaces(4)
                .inputTextureType(FURenderer.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(null)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this)
                .build();

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_beauty);
        mBottomViewStub.inflate();

        mBeautyControlView = (BeautyControlView) findViewById(R.id.fu_beauty_control);
        mBeautyControlView.setOnFUControlListener(mFURenderer);
        mBeautyControlView.setOnBottomAnimatorChangeListener(new BeautyControlView.OnBottomAnimatorChangeListener() {
            @Override
            public void onBottomAnimatorChangeListener(float showRate) {
                mTakePicBtn.setDrawWidth((int) (getResources().getDimensionPixelSize(R.dimen.x166) * (1 - showRate * 0.265)));
            }
        });
        mBeautyControlView.setOnDescriptionShowListener(new BeautyControlView.OnDescriptionShowListener() {
            @Override
            public void onDescriptionShowListener(int str) {
                showDescription(str, 1000);
            }
        });
        mGLSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBeautyControlView.hideBottomLayoutAnimator();
            }
        });

        mInputTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.fu_base_input_type_double:
                        isDoubleInputType = true;
                        break;
                    case R.id.fu_base_input_type_single:
                        isDoubleInputType = false;
                        break;
                }
                mFURenderer.changeInputType();
            }
        });

        mHeightCheckBox.setChecked(BeautyParameterModel.isHeightPerformance);
        mSelectDataBtn.setVisibility(View.VISIBLE);
        mSelectDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FUBeautyActivity.this, SelectDataActivity.class);
                intent.putExtra("SelectData", TAG);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBeautyControlView != null)
            mBeautyControlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onSensorChanged(int rotation) {
        mFURenderer.setTrackOrientation(rotation);
    }

    @Override
    public void onCameraChange(int currentCameraType, int cameraOrientation) {
        mFURenderer.onCameraChange(currentCameraType, cameraOrientation);
    }

    @Override
    public void onFpsChange(final double fps, final double renderTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDebugText.setText(String.format(getString(R.string.fu_base_debug), mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight(), (int) fps, (int) renderTime));
            }
        });
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
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mtx, long timeStamp) {
        int fuTextureId = 0;
        if (isDoubleInputType) {
            fuTextureId = mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else if (cameraNV21Byte != null) {
            if (mFuNV21Byte == null || mFuNV21Byte.length != cameraNV21Byte.length) {
                mFuNV21Byte = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
            fuTextureId = mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
        }
        sendRecordingData(fuTextureId, mtx, timeStamp / Constant.NANO_IN_ONE_MILLI_SECOND);
        checkPic(fuTextureId, mtx, cameraHeight, cameraWidth);
        return fuTextureId;
    }

    @Override
    public void onSurfaceDestroy() {
        //通知FU销毁
        mFURenderer.onSurfaceDestroyed();
    }
}
