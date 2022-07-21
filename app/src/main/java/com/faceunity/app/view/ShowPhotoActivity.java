package com.faceunity.app.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.R;
import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.data.AnimojiDataFactory;
import com.faceunity.app.data.AvatarDataFactory;
import com.faceunity.app.data.BgSegGreenDataFactory;
import com.faceunity.app.data.BodyBeautyDataFactory;
import com.faceunity.app.data.FaceBeautyDataFactory;
import com.faceunity.app.data.FineStickerDataFactory;
import com.faceunity.app.data.HairBeautyDataFactory;
import com.faceunity.app.data.MakeupDataFactory;
import com.faceunity.app.data.MakeupDataFactory;
import com.faceunity.app.data.MakeupDataFactory;
import com.faceunity.app.data.MusicFilterDataFactory;
import com.faceunity.app.data.PortraitSegmentDataFactory;
import com.faceunity.app.data.PropDataFactory;
import com.faceunity.app.data.source.BgSegGreenSource;
import com.faceunity.app.data.source.PortraitSegmentSource;
import com.faceunity.app.entity.FunctionEnum;
import com.faceunity.app.utils.FileUtils;
import com.faceunity.app.utils.FuDeviceUtils;
import com.faceunity.core.callback.OnColorReadCallback;
import com.faceunity.core.entity.FUCoordinate2DData;
import com.faceunity.core.entity.FURenderFrameData;
import com.faceunity.core.entity.FURenderInputData;
import com.faceunity.core.entity.FURenderOutputData;
import com.faceunity.core.enumeration.FUAIProcessorEnum;
import com.faceunity.core.enumeration.FUAITypeEnum;
import com.faceunity.core.enumeration.FUFaceProcessorDetectModeEnum;
import com.faceunity.core.enumeration.FUHumanProcessorDetectModeEnum;
import com.faceunity.core.faceunity.FUAIKit;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.listener.OnGlRendererListener;
import com.faceunity.core.media.midea.MediaPlayerHelper;
import com.faceunity.core.media.photo.OnPhotoRecordingListener;
import com.faceunity.core.media.photo.PhotoRecordHelper;
import com.faceunity.core.media.rgba.RGBAPicker;
import com.faceunity.core.media.video.VideoPlayHelper;
import com.faceunity.core.model.bgSegGreen.BgSegGreen;
import com.faceunity.core.model.facebeauty.FaceBeautyBlurTypeEnum;
import com.faceunity.core.model.musicFilter.MusicFilter;
import com.faceunity.core.model.prop.Prop;
import com.faceunity.core.model.prop.bgSegCustom.BgSegCustom;
import com.faceunity.core.renderer.PhotoRenderer;
import com.faceunity.core.utils.GestureTouchHandler;
import com.faceunity.core.utils.GlUtil;
import com.faceunity.ui.control.AnimojiControlView;
import com.faceunity.ui.control.AvatarControlView;
import com.faceunity.ui.control.BgSegGreenControlView;
import com.faceunity.ui.control.BodyBeautyControlView;
import com.faceunity.ui.control.FaceBeautyControlView;
import com.faceunity.ui.control.FineStickerView;
import com.faceunity.ui.control.HairBeautyControlView;
import com.faceunity.ui.control.MakeupControlView;
import com.faceunity.ui.control.MusicFilterControlView;
import com.faceunity.ui.control.PropControlView;
import com.faceunity.ui.control.PropCustomControlView;
import com.faceunity.ui.dialog.ToastHelper;
import com.faceunity.ui.entity.BgSegGreenBackgroundBean;
import com.faceunity.ui.entity.BgSegGreenSafeAreaBean;
import com.faceunity.ui.entity.PropCustomBean;
import com.faceunity.ui.widget.ColorPickerView;

import org.jetbrains.annotations.NotNull;

/**
 * DESC：
 * Created on 2021/3/2
 */
public class ShowPhotoActivity extends BaseActivity {
    public static final String TYPE = "type";
    public static final String PATH = "path";
    private static final int REQUEST_CODE_PHOTO = 1000;

    public static void startActivity(Context context, int type, String path) {
        context.startActivity(new Intent(context, ShowPhotoActivity.class).putExtra(TYPE, type).putExtra(PATH, path));
    }


    private VideoPlayHelper mVideoPlayHelper;

    //region 生命周期绑定
    @Override
    public void onResume() {
        super.onResume();
        if (mFunctionType == FunctionEnum.FINE_STICKER)
            mFineStickerDataFactory.acceptEvent();
        mPhotoRenderer.onResume();
    }

    @Override
    public void onPause() {
        if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mVideoPlayHelper.pausePlay();
        } else if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            PropCustomBean bean = mPortraitSegmentFactory.getCurrentPropCustomBean();
            if (bean.getType() == FunctionEnum.BG_SEG_CUSTOM) {
                mVideoPlayHelper.pausePlay();
            }
        } else if (mFunctionType == FunctionEnum.FINE_STICKER) {
            mFineStickerDataFactory.refuseEvent();
        } else if (mFunctionType == FunctionEnum.MUSIC_FILTER) {
            mediaPlayerHelper.pausePlay();
        }
        super.onPause();
        mPhotoRenderer.onPause();

    }

    @Override
    public void onDestroy() {
        if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mVideoPlayHelper.release();
        } else if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            mPortraitSegmentFactory.releaseAIProcessor();
            mVideoPlayHelper.release();
        } else if (mFunctionType == FunctionEnum.FINE_STICKER) {
            mFineStickerDataFactory.releaseAIProcessor();
        } else if (mFunctionType == FunctionEnum.MUSIC_FILTER) {
            mediaPlayerHelper.release();
        }
        mPhotoRenderer.onDestroy();
        super.onDestroy();
    }

    //endregion 生命周期绑定


    //region OnCreate

    private int mFunctionType;
    private String mPhotoPath;

    private View mStubView;
    private ImageButton mSaveView;
    protected GLSurfaceView mSurfaceView;
    protected TextView mTrackingView;
    protected TextView mEffectDescription;
    protected RelativeLayout mCustomView;


    @Override
    public int getLayoutResID() {
        return R.layout.activity_show_photo;
    }

    @Override
    public void initData() {
        mFunctionType = getIntent().getIntExtra(TYPE, 0);
        mPhotoPath = getIntent().getStringExtra(PATH);
        mPhotoRecordHelper = new PhotoRecordHelper(mOnPhotoRecordingListener);
        viewTopOffset = getResources().getDimensionPixelSize(R.dimen.x64);
    }

    @Override
    public void initView() {
        ViewStub viewStub = findViewById(R.id.stub_bottom);
        viewStub.setLayoutResource(getBottomLayout());
        mStubView = viewStub.inflate();
        mSaveView = findViewById(R.id.btn_save);
        mSurfaceView = findViewById(R.id.gl_surface);
        mTrackingView = findViewById(R.id.tv_tracking);
        mEffectDescription = findViewById(R.id.tv_effect_description);
        mCustomView = findViewById(R.id.ryt_custom_view);
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x156));
        } else if (mFunctionType == FunctionEnum.MAKE_UP) {
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x350));
        } else if (mFunctionType == FunctionEnum.FINE_STICKER) {
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x520));
        } else if (mFunctionType == FunctionEnum.HAIR_BEAUTY) {
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x350));
        } else {
            changeTakePicButtonMargin(getResources().getDimensionPixelSize(R.dimen.x200));
        }
    }


    @Override
    public void bindListener() {
        mPhotoRenderer = new PhotoRenderer(mSurfaceView, mPhotoPath, mOnGlRendererListener);
        mVideoPlayHelper = new VideoPlayHelper(mVideoDecoderListener, mSurfaceView, false);
        /* 拍照*/
        mSaveView.setOnClickListener(view -> isTakePhoto = true);
        /* 返回 */
        findViewById(R.id.btn_back).setOnClickListener(view -> onBackPressed());
        bindDataFactory();
    }

    //endregion OnCreate

    //region PhotoRenderer
    private FURenderKit mFURenderKit = FURenderKit.getInstance();
    private PhotoRenderer mPhotoRenderer;

    private FaceBeautyDataFactory mFaceBeautyDataFactory;//美颜a
    private FaceBeautyControlView mFaceBeautyControlView;//美颜

    private com.faceunity.app.data.MakeupDataFactory mMakeupDataFactory;//美妆

    private AnimojiDataFactory mAnimojiFactory;//Animoji
    private AnimojiControlView mAnimojiControlView;//Animoji

    private PortraitSegmentDataFactory mPortraitSegmentFactory;//人像分割
    private PropCustomControlView mPortraitSegmentControlView;//人像分割

    private BgSegGreenDataFactory mBgSegGreenDataFactory;//绿幕抠像
    private BgSegGreenControlView mBgSegGreenControlView;//绿幕抠像

    private PropDataFactory mPropDataFactory;//道具 道具贴纸 AR面具 大头 表情识别 哈哈镜 手势识别

    private BodyBeautyDataFactory mBodyBeautyDataFactory;//美体

    private FineStickerView fineStickerView;//精品贴纸
    private FineStickerDataFactory mFineStickerDataFactory;//精品贴纸

    //美发
    private HairBeautyControlView mHairBeautyControlView;
    private HairBeautyDataFactory mHairBeautyDataFactory;

    //音乐滤镜
    private MusicFilterControlView mMusicFilterControlView;
    private MusicFilterDataFactory mMusicFilterDataFactory;
    private MediaPlayerHelper mediaPlayerHelper;
    private boolean isMusicPlaying = false;
    private Handler mHandler;
    private final Runnable mMusicRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMusicPlaying) {
                MusicFilter musicFilter = mFURenderKit.getMusicFilter();
                if (musicFilter!=null){
                    musicFilter.setMusicTime(mediaPlayerHelper.getMusicCurrentPosition());
                }
                mHandler.postDelayed(this, 50L);
            }
        }
    };

    //Avatar
    private AvatarDataFactory mAvatarDataFactory;
    private AvatarControlView mAvatarControlView;

    private void bindDataFactory() {
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            mFaceBeautyDataFactory = new FaceBeautyDataFactory(mFaceBeautyListener);
            mFaceBeautyControlView = ((FaceBeautyControlView) mStubView);
            mFaceBeautyControlView.bindDataFactory(mFaceBeautyDataFactory);
            mFaceBeautyControlView.setOnBottomAnimatorChangeListener(showRate -> {
                // 收起 1-->0，弹出 0-->1
                mSaveView.setAlpha(1 - showRate);
            });
        } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mBgSegGreenDataFactory = new BgSegGreenDataFactory(mBgSegGreenListener, 3);
            mBgSegGreenControlView = ((BgSegGreenControlView) mStubView);
            mBgSegGreenControlView.bindDataFactory(mBgSegGreenDataFactory);
            mBgSegGreenControlView.setOnBottomAnimatorChangeListener(showRate -> {
                // 收起 1-->0，弹出 0-->1
                mSaveView.setAlpha(1 - showRate);
            });
            mSaveView.setAlpha(0f);
            mColorPickerView = new ColorPickerView(this);
            mGestureTouchHandler = new GestureTouchHandler(this);
            mGestureTouchHandler.setOnTouchResultListener(mOnTouchResultListener);
            addColorPickerView();
        } else if (mFunctionType == FunctionEnum.STICKER || mFunctionType == FunctionEnum.AR_MASK
                || mFunctionType == FunctionEnum.BIG_HEAD || mFunctionType == FunctionEnum.EXPRESSION_RECOGNITION
                || mFunctionType == FunctionEnum.FACE_WARP || mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
            mPropDataFactory = new PropDataFactory(mPropListener, mFunctionType, 1);
            ((PropControlView) mStubView).bindDataFactory(mPropDataFactory);
        } else if (mFunctionType == FunctionEnum.BODY_BEAUTY) {
            mBodyBeautyDataFactory = new BodyBeautyDataFactory();
            ((BodyBeautyControlView) mStubView).bindDataFactory(mBodyBeautyDataFactory);
        } else if (mFunctionType == FunctionEnum.MAKE_UP) {
            mMakeupDataFactory = new com.faceunity.app.data.MakeupDataFactory(1);
            ((MakeupControlView) mStubView).bindDataFactory(mMakeupDataFactory);
        } else if (mFunctionType == FunctionEnum.ANIMOJI) {
            mAnimojiControlView = ((AnimojiControlView) mStubView);
            mAnimojiFactory = new AnimojiDataFactory(0, 0);
            ((AnimojiControlView) mStubView).bindDataFactory(mAnimojiFactory);
            mAnimojiControlView.setOnBottomAnimatorChangeListener(showRate -> {
                // 收起 1-->0，弹出 0-->1
                mSaveView.setAlpha(1 - showRate);
            });
            mSaveView.setAlpha(0f);
        } else if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            mPortraitSegmentControlView = ((PropCustomControlView) mStubView);
            mPortraitSegmentFactory = new PortraitSegmentDataFactory(mPortraitSegmentListener);
            mPortraitSegmentControlView.bindDataFactory(mPortraitSegmentFactory);
        } else if (mFunctionType == FunctionEnum.FINE_STICKER) {
            fineStickerView = ((FineStickerView) mStubView);
            mFineStickerDataFactory = new FineStickerDataFactory();
            fineStickerView.bindDataFactory(mFineStickerDataFactory);
            mFineStickerDataFactory.bindView(fineStickerView);

            mFineStickerDataFactory.setBundleTypeListener(bundleType -> {
                if (bundleType != null) {
                    if (bundleType == FineStickerDataFactory.BundleType.AVATAR_BUNDLE) {
                        runOnUiThread(()-> mTrackingView.setText(R.string.toast_not_detect_body));
                    } else {
                        runOnUiThread(()-> mTrackingView.setText(R.string.fu_base_is_tracking_text));
                    }
                } else {
                    runOnUiThread(()-> mTrackingView.setText(R.string.fu_base_is_tracking_text));
                }
            });
        } else if (mFunctionType == FunctionEnum.HAIR_BEAUTY) {
            mHairBeautyControlView = ((HairBeautyControlView) mStubView);
            mHairBeautyDataFactory = new HairBeautyDataFactory(1);
            mHairBeautyControlView.bindDataFactory(mHairBeautyDataFactory);
        } else if (mFunctionType == FunctionEnum.MUSIC_FILTER) {
            mMusicFilterControlView = ((MusicFilterControlView) mStubView);
            mHandler = new Handler();
            mMusicFilterDataFactory = new MusicFilterDataFactory(1, data -> {
                String path = data.getMusic();
                if (path != null) {
                    mediaPlayerHelper.playMusic(path, true);
                } else {
                    mediaPlayerHelper.stopPlay();
                }
            });
            mMusicFilterControlView.bindDataFactory(mMusicFilterDataFactory);
            mediaPlayerHelper = new MediaPlayerHelper(this, new MediaPlayerHelper.MediaPlayerListener() {
                @Override
                public void onStart() {
                    isMusicPlaying = true;
                    mHandler.post(mMusicRunnable);
                }

                @Override
                public void onPause() {
                    isMusicPlaying = false;
                }

                @Override
                public void onStop() {
                    isMusicPlaying = false;
                }

                @Override
                public void onCompletion() {
                    isMusicPlaying = false;
                }
            });
        } else if (mFunctionType == FunctionEnum.AVATAR) {
            mAvatarControlView = (AvatarControlView) mStubView;
            mAvatarDataFactory = new AvatarDataFactory(0, true);
            mAvatarControlView.bindDataFactory(mAvatarDataFactory);
        }
    }

    private void configureFURenderKit() {
        FUAIKit.getInstance().loadAIProcessor(DemoConfig.BUNDLE_AI_FACE, FUAITypeEnum.FUAITYPE_FACEPROCESSOR);
        FUAIKit.getInstance().faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.IMAGE);
        FUAIKit.getInstance().setHumanProcessorDetectMode(FUHumanProcessorDetectModeEnum.IMAGE);
        FUAIKit.getInstance().faceProcessorSetFaceLandmarkQuality(DemoConfig.DEVICE_LEVEL);
        //高端机开启小脸检测
        if (DemoConfig.DEVICE_LEVEL  > FuDeviceUtils.DEVICE_LEVEL_MID)
            FUAIKit.getInstance().fuFaceProcessorSetDetectSmallFace(true);
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            mFaceBeautyDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            mBgSegGreenDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.STICKER || mFunctionType == FunctionEnum.AR_MASK
                || mFunctionType == FunctionEnum.BIG_HEAD || mFunctionType == FunctionEnum.EXPRESSION_RECOGNITION
                || mFunctionType == FunctionEnum.FACE_WARP || mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
            mPropDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.BODY_BEAUTY) {
            mBodyBeautyDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.ANIMOJI) {
            mAnimojiFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            mPortraitSegmentFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.MAKE_UP) {
            mMakeupDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.FINE_STICKER) {
            mFineStickerDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.HAIR_BEAUTY) {
            mHairBeautyDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.MUSIC_FILTER) {
            mMusicFilterDataFactory.bindCurrentRenderer();
        } else if (mFunctionType == FunctionEnum.AVATAR) {
            mAvatarDataFactory.bindCurrentRenderer();
        }
    }


    private int getBottomLayout() {
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            return R.layout.layout_control_face_beauty;
        } else if (mFunctionType == FunctionEnum.STICKER || mFunctionType == FunctionEnum.AR_MASK
                || mFunctionType == FunctionEnum.BIG_HEAD || mFunctionType == FunctionEnum.EXPRESSION_RECOGNITION
                || mFunctionType == FunctionEnum.FACE_WARP || mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
            return R.layout.layout_control_prop;
        } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            return R.layout.layout_control_bsg;
        } else if (mFunctionType == FunctionEnum.BODY_BEAUTY) {
            return R.layout.layout_control_body_beauty;
        } else if (mFunctionType == FunctionEnum.MAKE_UP) {
            return R.layout.layout_control_makeup;
        } else if (mFunctionType == FunctionEnum.ANIMOJI) {
            return R.layout.layout_control_animo;
        } else if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
            return R.layout.layout_control_prop_custom;
        } else if (mFunctionType == FunctionEnum.FINE_STICKER) {
            return R.layout.layout_control_fine_sticker;
        } else if (mFunctionType == FunctionEnum.HAIR_BEAUTY) {
            return R.layout.layout_control_hair_beauty;
        } else if (mFunctionType == FunctionEnum.MUSIC_FILTER) {
            return R.layout.layout_control_music_filter;
        } else if (mFunctionType == FunctionEnum.AVATAR) {
            return R.layout.layout_control_avatar;
        }
        return 0;
    }

    /**
     * 检测类型
     *
     * @return
     */
    protected FUAIProcessorEnum getFURenderKitTrackingType() {
        if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT || mFunctionType == FunctionEnum.BODY_BEAUTY) {
            return FUAIProcessorEnum.HUMAN_PROCESSOR;
        } else if (mFunctionType == FunctionEnum.GESTURE_RECOGNITION) {
            return FUAIProcessorEnum.HAND_GESTURE_PROCESSOR;
        } else return FUAIProcessorEnum.FACE_PROCESSOR;
    }

    /**
     * 检测结果变更回调
     *
     * @param fuaiProcessorEnum
     * @param status
     */
    protected void onTrackStatusChanged(FUAIProcessorEnum fuaiProcessorEnum, int status) {
        if (mFunctionType == FunctionEnum.GESTURE_RECOGNITION || mFunctionType == FunctionEnum.BG_SEG_GREEN) {
            return;
        }
        mTrackingView.setVisibility((status > 0) ? View.INVISIBLE : View.VISIBLE);
        if (status <= 0) {
            if (fuaiProcessorEnum == FUAIProcessorEnum.FACE_PROCESSOR) {
                mTrackingView.setText(R.string.fu_base_is_tracking_text);
            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
                mTrackingView.setText(R.string.toast_not_detect_body);
            }
            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
                mTrackingView.setText(R.string.toast_not_detect_gesture);
            }
        }
    }


    private final OnGlRendererListener mOnGlRendererListener = new OnGlRendererListener() {


        private int mTrackStatus = 1;/*检测标识*/

        @Override
        public void onSurfaceCreated() {
            configureFURenderKit();
        }


        @Override
        public void onSurfaceChanged(int width, int height) {
            if (mGestureTouchHandler != null) {
                mGestureTouchHandler.setViewSize(width, height);
            }
        }

        @Override
        public void onRenderBefore(FURenderInputData inputData) {
            if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID && getFURenderKitTrackingType() == FUAIProcessorEnum.FACE_PROCESSOR)//高性能设备 并且 人脸场景 -> 才会走磨皮策略
                cheekFaceConfidenceScore();
        }

        @Override
        public void onRenderAfter(@NotNull FURenderOutputData outputData, @NotNull FURenderFrameData frameData) {
            recordingData(outputData, frameData.getTexMatrix());
        }

        @Override
        public void onDrawFrameAfter() {
            trackStatus();
            if (isReadRGBA) {
                RGBAPicker.readRgba(anchorX, anchorY, mOnReadRgbaListener);
            }
        }

        @Override
        public void onSurfaceDestroy() {
            FUAIKit.getInstance().faceProcessorSetDetectMode(FUFaceProcessorDetectModeEnum.VIDEO);
            FUAIKit.getInstance().setHumanProcessorDetectMode(FUHumanProcessorDetectModeEnum.VIDEO);
            mFURenderKit.release();
        }


        private void trackStatus() {
            int trackStatus;
            FUAIProcessorEnum fuaiProcessorEnum = getFURenderKitTrackingType();
            if (fuaiProcessorEnum == FUAIProcessorEnum.HAND_GESTURE_PROCESSOR) {
                trackStatus = FUAIKit.getInstance().handProcessorGetNumResults();
            } else if (fuaiProcessorEnum == FUAIProcessorEnum.HUMAN_PROCESSOR) {
                trackStatus = FUAIKit.getInstance().humanProcessorGetNumResults();
            } else {
                trackStatus = FUAIKit.getInstance().isTracking();
            }
            if (mTrackStatus != trackStatus) {
                mTrackStatus = trackStatus;
                final int status = trackStatus;
                runOnUiThread(() -> onTrackStatusChanged(fuaiProcessorEnum, status));
            }
        }

        /*录制保存*/
        private void recordingData(FURenderOutputData outputData, float[] texMatrix) {
            if (outputData == null || outputData.getTexture() == null || outputData.getTexture().getTexId() <= 0) {
                return;
            }
            if (isTakePhoto) {
                isTakePhoto = false;
                mPhotoRecordHelper.sendRecordingData(outputData.getTexture().getTexId(), texMatrix, GlUtil.IDENTITY_MATRIX, outputData.getTexture().getWidth(), outputData.getTexture().getHeight());
            }
        }

    };

    private VideoPlayHelper.VideoDecoderListener mVideoDecoderListener = new VideoPlayHelper.VideoDecoderListener() {
        @Override
        public void onReadVideoPixel(byte[] bytes, int width, int height) {
            if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
                BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
                if (bgSegGreen == null) {
                    return;
                }

                bgSegGreen.createBgSegment(bytes, width, height);
            }
        }

        @Override
        public void onReadImagePixel(byte[] bytes, int width, int height) {
            if (mFunctionType == FunctionEnum.PORTRAIT_SEGMENT) {
                Prop prop = mPortraitSegmentFactory.getCurrentProp();
                if (prop instanceof BgSegCustom) {
                    ((BgSegCustom) prop).createBgSegment(bytes, width, height);
                }
            } else if (mFunctionType == FunctionEnum.BG_SEG_GREEN) {
                BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
                if (bgSegGreen == null) {
                    return;
                }
                bgSegGreen.createSafeAreaSegment(bytes, width, height);
            }
        }
    };

    //endregion PhotoRenderer


    //region 业务扩展


    /**
     * 调整拍照按钮对齐方式
     *
     * @param margin Int
     */
    protected void changeTakePicButtonMargin(int margin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mSaveView.getLayoutParams();
        params.bottomMargin = margin;
        mSaveView.setLayoutParams(params);
    }

    /**
     * 检查当前人脸置信度
     */
    private void cheekFaceConfidenceScore() {
        //根据有无人脸 + 设备性能 判断开启的磨皮类型
        float faceProcessorGetConfidenceScore = FUAIKit.getInstance().getFaceProcessorGetConfidenceScore(0);
        if (mFURenderKit.getFaceBeauty() != null) {
            if (faceProcessorGetConfidenceScore >= DemoConfig.FACE_CONFIDENCE_SCORE) {
                //高端手机并且检测到人脸开启均匀磨皮
                if (mFURenderKit.getFaceBeauty().getBlurType() != FaceBeautyBlurTypeEnum.EquallySkin) {
                    mFURenderKit.getFaceBeauty().setBlurType(FaceBeautyBlurTypeEnum.EquallySkin);
                    mFURenderKit.getFaceBeauty().setEnableBlurUseMask(true);
                }
            } else {
                if (mFURenderKit.getFaceBeauty().getBlurType() != FaceBeautyBlurTypeEnum.FineSkin) {
                    mFURenderKit.getFaceBeauty().setBlurType(FaceBeautyBlurTypeEnum.FineSkin);
                    mFURenderKit.getFaceBeauty().setEnableBlurUseMask(false);
                }
            }
        }
    }


    //endregion 业务扩展

    //region 拍照

    private PhotoRecordHelper mPhotoRecordHelper;
    private volatile Boolean isTakePhoto = false;

    /**
     * 获取拍摄的照片
     */
    private final OnPhotoRecordingListener mOnPhotoRecordingListener = bitmap -> {
        new Thread(() -> {
            String path = FileUtils.addBitmapToAlbum(this, bitmap);
            if (path == null) return;
            runOnUiThread(() -> ToastHelper.showNormalToast(ShowPhotoActivity.this, R.string.save_photo_success));
        }).start();
    };
    //endregion 拍照


    //region 绿幕抠像


    private ColorPickerView mColorPickerView;
    private GestureTouchHandler mGestureTouchHandler;
    private boolean mIsShowColorPicker = false;
    private boolean isReadRGBA = false;
    private int anchorX = 0;
    private int anchorY = 0;
    private int argbA = 0;
    private int argbR = 0;
    private int argbG = 0;
    private int argbB = 0;


    /**
     * 绑定取色器视图
     */
    private void addColorPickerView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mColorPickerView.setVisibility(View.GONE);
        mCustomView.addView(mColorPickerView, layoutParams);
    }

    /**
     * GestureTouchHandler 触碰回调
     */
    private GestureTouchHandler.OnTouchResultListener mOnTouchResultListener = new GestureTouchHandler.OnTouchResultListener() {
        @Override
        public void onTransform(float x1, float y1, float x2, float y2) {
            FUCoordinate2DData fuCoordinate2DData = new FUCoordinate2DData((x1 + x2) / 2, (y1 + y2) / 2);
            BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
            if (bgSegGreen != null) {
                bgSegGreen.setCenterPoint(fuCoordinate2DData);
                bgSegGreen.setZoom((double) (x2 - x1) * (x2 - x1));
            }
        }

        @Override
        public void onClick() {
            mBgSegGreenControlView.dismissBottomLayout();
        }
    };

    /**
     * 取色状态回调
     */
    private BgSegGreenDataFactory.BgSegGreenListener mBgSegGreenListener = new BgSegGreenDataFactory.BgSegGreenListener() {

        @Override
        public void onColorPickerStateChanged(boolean isSelected, int color) {
            if (isSelected) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mColorPickerView.getLayoutParams();
                layoutParams.leftMargin = (mCustomView.getWidth() - mColorPickerView.getWidth()) / 2;
                layoutParams.topMargin = (mCustomView.getHeight() - mColorPickerView.getHeight()) / 2;
                mColorPickerView.setLayoutParams(layoutParams);
                /*纹理坐标左下角为(0,0) 需要进行转换*/
                anchorX = mCustomView.getWidth() / 2;
                anchorY = mCustomView.getHeight() - (layoutParams.topMargin + getResources().getDimensionPixelSize(R.dimen.x104));
            }
            mIsShowColorPicker = isSelected;
            mColorPickerView.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            mColorPickerView.updatePickerColor(color);
        }

        @Override
        public void onBackgroundSelected(BgSegGreenBackgroundBean bean) {
            if (bean.getFilePath() != null) {
                mVideoPlayHelper.playAssetsVideo(ShowPhotoActivity.this, bean.getFilePath());
            } else {
                mVideoPlayHelper.pausePlay();
                BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
                if (bgSegGreen != null) {
                    bgSegGreen.removeBgSegment();
                }
            }
        }

        @Override
        public void onSafeAreaSelected(BgSegGreenSafeAreaBean bean) {
            if (bean != null && bean.getFilePath() != null) {
                if (bean.isAssetFile())
                    mVideoPlayHelper.playVideo(ShowPhotoActivity.this, bean.getFilePath());
                else
                    mVideoPlayHelper.playVideo(bean.getFilePath());
            } else {
                BgSegGreen bgSegGreen = mFURenderKit.getBgSegGreen();
                if (bgSegGreen != null) {
                    bgSegGreen.removeSafeAreaSegment();
                }
            }
        }

        @Override
        public void onSafeAreaAdd() {
            FileUtils.pickImageFile(ShowPhotoActivity.this, REQUEST_CODE_PHOTO);
        }
    };

    /**
     * GlSurfaceRenderer 取色回调
     */
    private OnColorReadCallback mOnReadRgbaListener = new OnColorReadCallback() {
        @Override
        public void onReadRgba(int r, int g, int b, int a) {
            argbA = a;
            argbR = r;
            argbG = g;
            argbB = b;
            runOnUiThread(() -> {
                int color = Color.argb(a, r, g, b);
                mColorPickerView.updatePickerColor(color);
                mBgSegGreenControlView.setPalettePickColor(color);
            });

        }
    };


    private int viewTopOffset;//取色器触碰向上偏移一个手指

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFunctionType == FunctionEnum.FACE_BEAUTY) {
            mFaceBeautyControlView.hideControlView();
        }
        if (super.onTouchEvent(event)) {
            return true;
        } else if (mIsShowColorPicker) {
            int touchX = (int) event.getX();
            int touchY = (int) event.getY();
            int parentHeight = ((ViewGroup) mColorPickerView.getParent()).getHeight();
            if (touchY > parentHeight - (getResources().getDimensionPixelSize(R.dimen.x367) - viewTopOffset)
                    && !(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mColorPickerView.getLayoutParams();
                layoutParams.leftMargin = touchX - mColorPickerView.getWidth() / 2;
                layoutParams.topMargin = touchY - mColorPickerView.getHeight() - viewTopOffset;
                mColorPickerView.setLayoutParams(layoutParams);
                /*纹理坐标左下角为(0,0) 需要进行转换*/
                int pickY = mCustomView.getHeight() - (layoutParams.topMargin + getResources().getDimensionPixelSize(R.dimen.x104));
                anchorX = touchX;
                anchorY = pickY;
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isReadRGBA = true;
                }
                return false;
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                isReadRGBA = false;
                mIsShowColorPicker = false;
                mColorPickerView.setVisibility(View.GONE);
                mBgSegGreenControlView.setPaletteColorPicked(argbR, argbG, argbB);
                return true;
            }
            return false;
        } else if (mGestureTouchHandler != null) {
            return mGestureTouchHandler.onTouchEvent(event);
        } else {
            return false;
        }


    }


    //endregion 绿幕抠像
    //region页面交互回调
    private FaceBeautyDataFactory.FaceBeautyListener mFaceBeautyListener = new FaceBeautyDataFactory.FaceBeautyListener() {

        @Override
        public void onFilterSelected(int res) {
            ToastHelper.showNormalToast(ShowPhotoActivity.this, res);
        }

        @Override
        public void onFaceBeautyEnable(boolean enable) {
            mPhotoRenderer.setFURenderSwitch(enable);
        }
    };


    private PropDataFactory.PropListener mPropListener = bean -> {

    };
    //endregion

    //region 人像分割
    PortraitSegmentDataFactory.PortraitSegmentListener mPortraitSegmentListener = new PortraitSegmentDataFactory.PortraitSegmentListener() {


        @Override
        public void onItemSelected(PropCustomBean bean) {
            if (bean.getType() == FunctionEnum.BG_SEG_CUSTOM) {
                mVideoPlayHelper.playVideo(bean.getIconPath());
            } else {
                mVideoPlayHelper.pausePlay();
            }
        }

        @Override
        public void onCustomPropAdd() {
            SelectDataActivity.startActivityForResult(ShowPhotoActivity.this);
        }

        @Override
        public void onProcessTrackChanged(boolean needShow) {

        }

    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        Uri uri = data.getData();
        String path = FileUtils.getFilePathByUri(this, uri);
        if (mPortraitSegmentFactory != null) {
            PropCustomBean customBean = PortraitSegmentSource.buildPropCustomBean(path);
            if (customBean == null) {
                return;
            }
            PropCustomBean bean = mPortraitSegmentFactory.getPropCustomBeans().get(2);
            if (bean.getType() == FunctionEnum.BG_SEG_CUSTOM) {
                mPortraitSegmentControlView.replaceProp(customBean, 2);
            } else {
                mPortraitSegmentControlView.addProp(customBean, 2);

            }
            mPortraitSegmentFactory.setCurrentPropIndex(2);
            mPortraitSegmentFactory.onItemSelected(customBean);
        } else if (mBgSegGreenDataFactory != null) {
            if (!FileUtils.checkIsImage(path)) {
                ToastHelper.showNormalToast(this, getString(R.string.please_select_the_correct_picture_file));
                return;
            }
            BgSegGreenSafeAreaBean bean = mBgSegGreenDataFactory.getBgSegGreenSafeAreas().get(3);
            BgSegGreenSafeAreaBean bgSegGreenSafeAreaBean = BgSegGreenSource.buildSafeAreaCustomBean(path);
            if (BgSegGreenSafeAreaBean.ButtonType.NORMAL1_BUTTON == bean.getType() && !bean.isAssetFile()) {
                mBgSegGreenControlView.replaceSegGreenSafeAreaCustom(bgSegGreenSafeAreaBean, 3);
            } else {
                mBgSegGreenControlView.addSegGreenSafeAreaCustom(bgSegGreenSafeAreaBean, 3);
            }
            mBgSegGreenDataFactory.setBgSafeAreaIndex(3);
            mBgSegGreenDataFactory.onSafeAreaSelected(bgSegGreenSafeAreaBean);
        }
    }

    //endregion
}
