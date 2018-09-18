package com.faceunity.fulivedemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.faceunity.encoder.MediaAudioEncoder;
import com.faceunity.encoder.MediaEncoder;
import com.faceunity.encoder.MediaMuxerWrapper;
import com.faceunity.encoder.MediaVideoEncoder;
import com.faceunity.fulivedemo.renderer.CameraRenderer;
import com.faceunity.fulivedemo.ui.RecordBtn;
import com.faceunity.fulivedemo.utils.BitmapUtil;
import com.faceunity.fulivedemo.utils.NotchInScreenUtil;
import com.faceunity.fulivedemo.utils.ToastUtil;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.utils.Constant;
import com.faceunity.utils.MiscUtil;

import java.io.File;
import java.io.IOException;

/**
 * Base Activity, 主要封装FUBeautyActivity与FUEffectActivity的公用界面与方法
 * CameraRenderer相关回调实现
 * Created by tujh on 2018/1/31.
 */
public abstract class FUBaseUIActivity extends AppCompatActivity
        implements View.OnClickListener,
        CameraRenderer.OnRendererStatusListener,
        SensorEventListener {
    public final static String TAG = FUBaseUIActivity.class.getSimpleName();

    protected ImageView mTopBackground;
    protected GLSurfaceView mGLSurfaceView;
    protected CameraRenderer mCameraRenderer;
    protected boolean isDoubleInputType = true;
    protected RadioGroup mInputTypeRadioGroup;
    private CheckBox mDebugBox;
    protected TextView mDebugText;
    protected TextView mIsTrackingText;
    protected TextView mEffectDescription;
    protected RecordBtn mTakePicBtn;
    protected ViewStub mBottomViewStub;
    protected CheckBox mHeightCheckBox;
    protected ImageView mHeightImg;
    protected ImageView mSelectDataBtn;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mTakePicing = false;
    private boolean mIsNeedTakePic = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (NotchInScreenUtil.hasNotch(this)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fu_base);

        mTopBackground = (ImageView) findViewById(R.id.fu_base_top_background);

        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.fu_base_gl_surface);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mCameraRenderer = new CameraRenderer(this, mGLSurfaceView, this);
        mGLSurfaceView.setRenderer(mCameraRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mInputTypeRadioGroup = (RadioGroup) findViewById(R.id.fu_base_input_type_radio_group);

        mDebugBox = (CheckBox) findViewById(R.id.fu_base_debug);
        mDebugText = (TextView) findViewById(R.id.fu_base_debug_text);
        mDebugBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mDebugText.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        mIsTrackingText = (TextView) findViewById(R.id.fu_base_is_tracking_text);
        mEffectDescription = (TextView) findViewById(R.id.fu_base_effect_description);
        mTakePicBtn = (RecordBtn) findViewById(R.id.fu_base_take_pic);
        mTakePicBtn.setOnRecordListener(new RecordBtn.OnRecordListener() {
            @Override
            public void takePic() {
                FUBaseUIActivity.this.takePic();
            }

            @Override
            public void startRecord() {
                startRecording();
            }

            @Override
            public void stopRecord() {
                stopRecording();
            }
        });
        mHeightCheckBox = (CheckBox) findViewById(R.id.fu_base_height);
        mHeightImg = (ImageView) findViewById(R.id.fu_base_height_img);
        mHeightImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeightCheckBox.setChecked(!mHeightCheckBox.isChecked());
            }
        });
        mSelectDataBtn = (ImageView) findViewById(R.id.fu_base_select_data);
        mBottomViewStub = (ViewStub) findViewById(R.id.fu_base_bottom);
        mBottomViewStub.setInflatedId(R.id.fu_base_bottom);
        onCreate();
    }

    protected abstract void onCreate();

    @Override
    protected void onResume() {
        super.onResume();
        mCameraRenderer.onCreate();
        mCameraRenderer.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mCameraRenderer.onPause();
        mCameraRenderer.onDestroy();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fu_base_back:
                onBackPressed();
                break;
            case R.id.fu_base_camera_change:
                mCameraRenderer.changeCamera();
                break;
        }
    }

    private Runnable effectDescriptionHide = new Runnable() {
        @Override
        public void run() {
            mEffectDescription.setText("");
            mEffectDescription.setVisibility(View.INVISIBLE);
        }
    };

    protected void showDescription(String str, int time) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        mEffectDescription.removeCallbacks(effectDescriptionHide);
        mEffectDescription.setVisibility(View.VISIBLE);
        mEffectDescription.setText(str);
        mEffectDescription.postDelayed(effectDescriptionHide, time);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                if (Math.abs(x) > Math.abs(y)) {
                    onSensorChanged(x > 0 ? 0 : 180);
                } else {
                    onSensorChanged(y > 0 ? 90 : 270);
                }
            }
        }
    }

    protected abstract void onSensorChanged(int rotation);

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void takePic() {
        if (mTakePicing) {
            return;
        }
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    public void checkPic(int textureId, float[] mtx, final int texWidth, final int texHeight) {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        BitmapUtil.glReadBitmap(textureId, mtx, GlUtil.IDENTITY_MATRIX, texWidth, texHeight, new BitmapUtil.OnReadBitmapListener() {
            @Override
            public void onReadBitmapListener(Bitmap bitmap) {
                String name = "FULiveDemo_" + MiscUtil.getCurrentDate() + ".jpg";
                String result = MiscUtil.saveBitmap(bitmap, Constant.photoFilePath, name);
                if (result != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(FUBaseUIActivity.this, "保存照片成功！");
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

    private long mStartTime = 0;

    protected void sendRecordingData(int texId, final float[] tex_matrix, final long timeStamp) {
        if (mVideoEncoder != null) {
            mVideoEncoder.frameAvailableSoon(texId, tex_matrix);
            if (mStartTime == 0) mStartTime = timeStamp;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTakePicBtn.setSecond(timeStamp - mStartTime);
                }
            });
        }
    }

    private File mOutFile;
    private MediaVideoEncoder mVideoEncoder;
    /**
     * callback methods from encoder
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {
        @Override
        public void onPrepared(final MediaEncoder encoder) {
            if (encoder instanceof MediaVideoEncoder) {
                final MediaVideoEncoder videoEncoder = (MediaVideoEncoder) encoder;
                mGLSurfaceView.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        videoEncoder.setEglContext(EGL14.eglGetCurrentContext());
                        mVideoEncoder = videoEncoder;
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTakePicBtn.setSecond(0);
                    }
                });
            }

        }

        @Override
        public void onStopped(final MediaEncoder encoder) {
            mVideoEncoder = null;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "stop encoder success");
                    ToastUtil.showToast(FUBaseUIActivity.this, "保存视频成功！");
                    mTakePicBtn.setSecond(mStartTime = 0);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(mOutFile)));
                }
            });
        }
    };

    private MediaMuxerWrapper mMuxer;

    private void startRecording() {
        try {
            String videoFileName = "FULiveDemo_" + MiscUtil.getCurrentDate() + ".mp4";
            mOutFile = new File(Constant.cameraFilePath, videoFileName);
            mMuxer = new MediaMuxerWrapper(mOutFile.getAbsolutePath());

            // for video capturing
            new MediaVideoEncoder(mMuxer, mMediaEncoderListener, mCameraRenderer.getCameraHeight(), mCameraRenderer.getCameraWidth());
            new MediaAudioEncoder(mMuxer, mMediaEncoderListener);

            mMuxer.prepare();
            mMuxer.startRecording();
        } catch (final IOException e) {
            Log.e(TAG, "startCapture:", e);
        }
    }

    private void stopRecording() {
        if (mMuxer != null) {
            mMuxer.stopRecording();
        }
        System.gc();
    }
}
