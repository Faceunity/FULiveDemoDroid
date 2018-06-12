package com.faceunity.fulivedemo;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.faceunity.fulivedemo.core.FURenderer;
import com.faceunity.fulivedemo.entity.Effect;
import com.faceunity.fulivedemo.entity.EffectEnum;
import com.faceunity.wrapper.faceunity;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.faceunity.fulivedemo.entity.BeautyParameterModel.getValue;

/**
 * 道具界面
 * Created by tujh on 2018/1/31.
 */

public class FUEffectActivity extends FUBaseUIActivity
        implements FURenderer.OnFUDebugListener,
        FURenderer.OnCalibratingListener,
        FURenderer.OnTrackingStatusChangedListener {
    public final static String TAG = FUEffectActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private int mEffectType;
    private ArrayList<Effect> mEffects;
    private int mPositionSelect = 1;

    private byte[] mFuNV21Byte;

    private FURenderer mFURenderer;

    @Override
    protected void onCreate() {
        mEffectType = getIntent().getIntExtra("EffectType", 0);
        mEffects = EffectEnum.getEffectsByEffectType(mEffectType);

        mBottomViewStub.setLayoutResource(R.layout.layout_fu_effect);
        mBottomViewStub.inflate();

        mRecyclerView = (RecyclerView) findViewById(R.id.fu_effect_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setAdapter(new EffectRecyclerAdapter());

        mInputTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mFURenderer.changeInputType();
                switch (checkedId) {
                    case R.id.fu_base_input_type_double:
                        isDoubleInputType = true;
                        break;
                    case R.id.fu_base_input_type_single:
                        isDoubleInputType = false;
                        break;
                }
            }
        });

        //初始化FU相关 authpack 为证书文件

        FURenderer.Builder build = new FURenderer
                .Builder(this)
                .inputTextureType(faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE)
                .createEGLContext(false)
                .needReadBackImage(false)
                .defaultEffect(mEffects.size() > 1 ? mEffects.get(1) : null)
                .setOnFUDebugListener(this)
                .setOnTrackingStatusChangedListener(this);

        if (mEffectType == Effect.EFFECT_TYPE_ANIMOJI) {
            build.setOnCalibratingListener(this).setNeedAnimoji3D(true);
        }

        if (mEffectType == Effect.EFFECT_TYPE_ANIMOJI || mEffectType == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            build.setOnCalibratingListener(this).setNeedFaceBeauty(false);
        }

        mFURenderer = build.build();

        mFURenderer.onSkinDetectSelected(getValue(R.id.beauty_box_skin_detect));
        mFURenderer.onHeavyBlurSelected(getValue(R.id.beauty_box_heavy_blur));
        mFURenderer.onBlurLevelSelected(getValue(R.id.beauty_box_blur_level));
        mFURenderer.onColorLevelSelected(getValue(R.id.beauty_box_color_level));
        mFURenderer.onRedLevelSelected(getValue(R.id.beauty_box_red_level));
        mFURenderer.onEyeBrightSelected(getValue(R.id.beauty_box_eye_bright));
        mFURenderer.onToothWhitenSelected(getValue(R.id.beauty_box_tooth_whiten));
        mFURenderer.onFaceShapeSelected(getValue(R.id.beauty_box_face_shape));
        mFURenderer.onEyeEnlargeSelected(getValue(R.id.beauty_box_eye_enlarge));
        mFURenderer.onCheekThinningSelected(getValue(R.id.beauty_box_cheek_thinning));
        mFURenderer.onIntensityChinSelected(getValue(R.id.beauty_box_intensity_chin));
        mFURenderer.onIntensityForeheadSelected(getValue(R.id.beauty_box_intensity_forehead));
        mFURenderer.onIntensityNoseSelected(getValue(R.id.beauty_box_intensity_nose));
        mFURenderer.onIntensityMouthSelected(getValue(R.id.beauty_box_intensity_mouth));
    }

    @Override
    protected void onResume() {
        super.onResume();
        playMusic(mEffects.get(mPositionSelect));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
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
                mDebugText.setText(String.format("resolution:\n\t%dX%d\nfps:%d\nrender time:\n\t\t\t\t\t%dms", mCameraRenderer.getCameraWidth(), mCameraRenderer.getCameraHeight(), (int) fps, (int) renderTime));
            }
        });
    }

    @Override
    public void onTrackingStatusChanged(final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (status > 0) {
                    mIsTrackingText.setVisibility(View.GONE);
                } else {
                    mIsTrackingText.setVisibility(View.VISIBLE);
                    mIsCalibratingText.setVisibility(View.INVISIBLE);
                    mIsCalibratingText.removeCallbacks(mCalibratingRunnable);
                }
            }
        });
    }

    private static final String strCalibrating = "表情校准中";
    private int showNum = 0;
    private final Runnable mCalibratingRunnable = new Runnable() {

        @Override
        public void run() {
            showNum++;
            StringBuilder builder = new StringBuilder();
            builder.append(strCalibrating);
            for (int i = 0; i < showNum; i++) {
                builder.append(".");
            }
            mIsCalibratingText.setText(builder);
            if (showNum < 6) {
                mIsCalibratingText.postDelayed(mCalibratingRunnable, 500);
            } else {
                mIsCalibratingText.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    public void OnCalibrating(final float isCalibrating) {
        if (mEffects.get(mPositionSelect).effectType() == Effect.EFFECT_TYPE_ANIMOJI) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isCalibrating > 0) {
                        mIsCalibratingText.setVisibility(View.VISIBLE);
                        mIsCalibratingText.setText(strCalibrating);
                        showNum = 0;
                        mIsCalibratingText.postDelayed(mCalibratingRunnable, 500);
                    } else {
                        mIsCalibratingText.setVisibility(View.INVISIBLE);
                        mIsCalibratingText.removeCallbacks(mCalibratingRunnable);
                    }
                }
            });
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFURenderer.onSurfaceCreated();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showDescription(mEffects.get(mPositionSelect).description(), 1500);
            }
        });
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight) {
        if (isDoubleInputType) {
            return mFURenderer.onDrawFrame(cameraNV21Byte, cameraTextureId, cameraWidth, cameraHeight);
        } else {
            if (mFuNV21Byte == null) {
                mFuNV21Byte = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, mFuNV21Byte, 0, cameraNV21Byte.length);
            return mFURenderer.onDrawFrame(mFuNV21Byte, cameraWidth, cameraHeight);
        }
    }

    @Override
    public void onSurfaceDestroy() {
        //通知FU销毁
        mFURenderer.onSurfaceDestroyed();
    }


    class EffectRecyclerAdapter extends RecyclerView.Adapter<EffectRecyclerAdapter.HomeRecyclerHolder> {

        @Override
        public HomeRecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HomeRecyclerHolder(LayoutInflater.from(FUEffectActivity.this).inflate(R.layout.layout_effect_recycler, parent, false));
        }

        @Override
        public void onBindViewHolder(HomeRecyclerHolder holder, final int position) {

            holder.effectImg.setImageResource(mEffects.get(position).resId());
            holder.effectImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPositionSelect == position) {
                        return;
                    }
                    Effect click = mEffects.get(mPositionSelect = position);
                    mFURenderer.onEffectSelected(click);
                    playMusic(click);
                    notifyDataSetChanged();
                    showDescription(click.description(), 1500);
                }
            });
            if (mPositionSelect == position) {
                holder.effectImg.setBackgroundResource(R.drawable.effect_select);
            } else {
                holder.effectImg.setBackgroundResource(0);
            }
        }

        @Override
        public int getItemCount() {
            return mEffects.size();
        }

        class HomeRecyclerHolder extends RecyclerView.ViewHolder {

            CircleImageView effectImg;

            public HomeRecyclerHolder(View itemView) {
                super(itemView);
                effectImg = (CircleImageView) itemView.findViewById(R.id.effect_recycler_img);
            }
        }
    }

    private MediaPlayer mediaPlayer;
    private Handler mMusicHandler;
    private static final int MUSIC_TIME = 50;
    Runnable mMusicRunnable = new Runnable() {
        @Override
        public void run() {
            if (mediaPlayer != null && mediaPlayer.isPlaying())
                mFURenderer.setMusicTime(mediaPlayer.getCurrentPosition());

            mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
        }
    };

    void stopMusic() {
        if (mEffectType != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            mMusicHandler.removeCallbacks(mMusicRunnable);

        }
    }

    void playMusic(Effect effect) {
        if (mEffectType != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        stopMusic();

        if (effect.effectType() != Effect.EFFECT_TYPE_MUSIC_FILTER) {
            return;
        }
        mediaPlayer = new MediaPlayer();
        mMusicHandler = new Handler();

        /**
         * mp3
         */
        try {
            AssetFileDescriptor descriptor = getAssets().openFd("musicfilter/" + effect.bundleName() + ".mp3");
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {

                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 装载完毕回调
                    //mediaPlayer.setVolume(1f, 1f);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();

                    mMusicHandler.postDelayed(mMusicRunnable, MUSIC_TIME);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            mediaPlayer = null;
        }
    }
}
