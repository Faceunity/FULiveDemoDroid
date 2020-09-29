package com.faceunity;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.entity.Effect;
import com.faceunity.entity.LightMakeupItem;
import com.faceunity.entity.MakeupEntity;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.param.BeautificationParam;
import com.faceunity.param.BodySlimParam;
import com.faceunity.param.CartoonFilterParam;
import com.faceunity.param.HairParam;
import com.faceunity.param.MakeupParamHelper;
import com.faceunity.utils.DeviceUtils;
import com.faceunity.utils.FileUtils;
import com.faceunity.utils.VideoDecoder;
import com.faceunity.wrapper.faceunity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个基于FaceUnity Nama SDK的简单封装，方便简单集成，理论上简单需求的步骤：
 * <p>
 * 1.通过 OnFUControlListener 在UI上进行交互
 * 2.合理调用FURenderer构造函数
 * 3.对应的时机调用onSurfaceCreated和onSurfaceDestroyed
 * 4.处理图像时调用onDrawFrame
 // TODO: 2020/9/21 0021 拆分功能模块，目标是单一职责，高内聚低耦合
 */
public class FURenderer implements OnFUControlListener {
    private static final String TAG = FURenderer.class.getSimpleName();
    public static final int FU_ADM_FLAG_EXTERNAL_OES_TEXTURE = faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE;
    /**
     * 人脸检测点位
     */
    public static final int FACE_LANDMARKS_75 = faceunity.FUAITYPE_FACELANDMARKS75;
    public static final int FACE_LANDMARKS_239 = faceunity.FUAITYPE_FACELANDMARKS239;

    /**
     * 人脸检测模式
     */
    public static final int FACE_PROCESSOR_DETECT_MODE_IMAGE = 0;
    public static final int FACE_PROCESSOR_DETECT_MODE_VIDEO = 1;

    /**
     * 外部输入类型
     */
    public static final int EXTERNAL_INPUT_TYPE_NONE = -1;
    public static final int EXTERNAL_INPUT_TYPE_IMAGE = 0;
    public static final int EXTERNAL_INPUT_TYPE_VIDEO = 1;

    /**
     * 算法检测类型
     */
    public static final int TRACK_TYPE_FACE = faceunity.FUAITYPE_FACEPROCESSOR;
    public static final int TRACK_TYPE_HUMAN = faceunity.FUAITYPE_HUMAN_PROCESSOR;
    public static final int TRACK_TYPE_GESTURE = faceunity.FUAITYPE_HANDGESTURE;

    private Context mContext;

    // 图形道具文件夹
    private static final String GRAPHICS_ASSETS_DIR = "graphics/";
    // fxaa.bundle：3D绘制抗锯齿数据文件，加载后3D绘制效果更加平滑。
    private static final String BUNDLE_FXAA = GRAPHICS_ASSETS_DIR + "fxaa.bundle";
    // 美颜 bundle
    private static final String BUNDLE_FACE_BEAUTIFICATION = GRAPHICS_ASSETS_DIR + "face_beautification.bundle";
    // 美发正常色 bundle
    private static final String BUNDLE_HAIR_NORMAL = "effect/hair_seg/hair_normal.bundle";
    // 美发渐变色 bundle
    private static final String BUNDLE_HAIR_GRADIENT = "effect/hair_seg/hair_gradient.bundle";
    // 海报换脸 bundle
    private static final String BUNDLE_CHANGE_FACE = "change_face/change_face.bundle";
    // 动漫滤镜 bundle
    private static final String BUNDLE_CARTOON_FILTER = GRAPHICS_ASSETS_DIR + "fuzzytoonfilter.bundle";
    // 轻美妆 bundle
    private static final String BUNDLE_LIGHT_MAKEUP = "light_makeup/light_makeup.bundle";
    // 美妆 bundle
    private static final String BUNDLE_FACE_MAKEUP = GRAPHICS_ASSETS_DIR + "face_makeup.bundle";
    // 美体 bundle
    private static final String BUNDLE_BEAUTIFY_BODY = GRAPHICS_ASSETS_DIR + "body_slim.bundle";
    // 算法模型文件夹
    private static final String AI_MODEL_ASSETS_DIR = "model/";
    // 人脸识别算法模型
    private static final String BUNDLE_AI_MODEL_FACE_PROCESSOR = AI_MODEL_ASSETS_DIR + "ai_face_processor.bundle";
    // 舌头识别算法模型
    private static final String BUNDLE_TONGUE = GRAPHICS_ASSETS_DIR + "tongue.bundle";

    private static final String LANDMARKS = "landmarks";

    private static float sIsBeautyOn = 1.0F;
    private static String sFilterName = BeautificationParam.ZIRAN_2;// 滤镜：自然 2
    private static float mFilterLevel = 0.4f;//滤镜强度
    private static float mBlurLevel = 0.7f;//磨皮程度
    private static float mBlurType = 2.0f;//磨皮类型：精细磨皮
    private static float mColorLevel = 0.3f;//美白
    private static float mSharpen = 0.2f;//锐化
    private static float mRedLevel = 0.3f;//红润
    private static float mEyeBright = 0.0f;//亮眼
    private static float mToothWhiten = 0.0f;//美牙
    private static float mFaceShape = 4;//脸型：精细变形
    private static float mFaceShapeLevel = 1.0f;//变形程度
    private static float mIntensityCheekbones = 0f;//颧骨
    private static float mIntensityLowerJaw = 0f;//下颌骨
    private static float mCheekThinning = 0f;//瘦脸
    private static float mCheekV = 0.5f;//V脸
    private static float mCheekNarrow = 0f;//窄脸
    private static float mCheekSmall = 0f;//小脸
    private static float mEyeEnlarging = 0.4f;//大眼
    private static float mIntensityChin = 0.3f;//下巴
    private static float mIntensityForehead = 0.3f;//额头
    private static float mIntensityMouth = 0.4f;//嘴形
    private static float mIntensityNose = 0.5f;//瘦鼻
    private static float sMicroPouch = 0f; // 去黑眼圈
    private static float sMicroNasolabialFolds = 0f; // 去法令纹
    private static float sMicroSmile = 0f; // 微笑嘴角
    private static float sMicroCanthus = 0f; // 眼角
    private static float sMicroPhiltrum = 0.5f; // 人中
    private static float sMicroLongNose = 0.5f; // 鼻子长度
    private static float sMicroEyeSpace = 0.5f; // 眼睛间距
    private static float sMicroEyeRotate = 0.5f; // 眼睛角度

    private int mFrameId = 0;

    // 句柄索引
    private static final int ITEM_ARRAYS_FACE_BEAUTY_INDEX = 0;
    public static final int ITEM_ARRAYS_EFFECT_INDEX = 1;
    private static final int ITEM_ARRAYS_LIGHT_MAKEUP_INDEX = 2;
    private static final int ITEM_ARRAYS_ABIMOJI_3D_INDEX = 3;
    private static final int ITEM_ARRAYS_BEAUTY_HAIR_INDEX = 4;
    private static final int ITEM_ARRAYS_CHANGE_FACE_INDEX = 5;
    private static final int ITEM_ARRAYS_CARTOON_FILTER_INDEX = 6;
    private static final int ITEM_ARRAYS_FACE_MAKEUP_INDEX = 7;
    private static final int ITEM_ARRAYS_BEAUTIFY_BODY = 10;
    // 句柄数量
    private static final int ITEM_ARRAYS_COUNT = 11;

    // 海报换脸 track face 50 次，确保检测成功率
    private static final int MAX_TRACK_COUNT = 50;

    // 美发类型
    public static final int HAIR_NORMAL = 0;
    public static final int HAIR_GRADIENT = 1;

    //美颜和其他道具的handle数组
    private int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    //用于和异步加载道具的线程交互
    private Handler mFuItemHandler;

    private boolean isNeedFaceBeauty = true;
    private boolean isNeedBeautyHair = false;
    private boolean isNeedAnimoji3D = false;
    private boolean isNeedPosterFace = false;
    private boolean isNeedBodySlim = false;
    private Effect mDefaultEffect;//默认道具
    private boolean mIsCreateEGLContext; //是否需要手动创建EGLContext
    private int mInputTextureType = 0; //输入的图像texture类型，Camera提供的默认为EXTERNAL OES
    private int mInputImageFormat = 0;
    private volatile boolean mIsNeedUpdateFaceBeauty = true;
    private float mBodySlimStrength = 0.0f; // 瘦身
    private float mLegSlimStrength = 0.0f; // 长腿
    private float mWaistSlimStrength = 0.0f; // 细腰
    private float mShoulderSlimStrength = 0.5f; // 美肩
    private float mHipSlimStrength = 0.0f; // 美胯
    private float mHeadSlimStrength = 0.0f; // 小头
    private float mLegThinSlimStrength = 0.0f; // 瘦腿

    private int mInputOrientation = 270;
    private int mExternalInputType = EXTERNAL_INPUT_TYPE_NONE;
    private boolean mIsSystemCameraRecord;
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mMaxFaces = 4; // 同时识别的最大人脸数
    private int mMaxHumans = 1; // 同时识别的最大人体数
    // 美发参数
    private float mHairColorStrength = 0.6f;
    private int mHairColorType = HAIR_GRADIENT;
    private int mHairColorIndex = 0;

    // 美妆妆容参数集合
    private Map<String, Object> mMakeupParams = new ConcurrentHashMap<>(16);
    // 轻美妆妆容集合，保证预定义的子妆容顺序，口红要最后上妆，这样才不会被覆盖
    private Map<Integer, LightMakeupItem> mLightMakeupItemMap = new LinkedHashMap<>(16);
    // 美妆组合妆
    private MakeupEntity mMakeupEntity;
    // 美妆子妆句柄集合
    private Map<String, Integer> mMakeupItemHandleMap = new HashMap<>(16);
    // 美妆点位是否镜像
    private boolean mIsMakeupFlipPoints;

    private float[] rotationData = new float[4];
    private float[] faceRectData = new float[4];

    private List<Runnable> mEventQueue;
    private long mGlThreadId;
    private OnBundleLoadCompleteListener mOnBundleLoadCompleteListener;
    private int mCartoonFilterStyle = CartoonFilterParam.NO_FILTER;
    private static boolean sIsInited;
    /* 设备方向 */
    private int mDeviceOrientation = 90;
    /* 人脸识别方向 */
    private int mRotationMode = faceunity.FU_ROTATION_MODE_90;

    private boolean mIsLoadAiGesture;
    private boolean mIsLoadAiHumanProcessor;

    /**
     * 绿幕抠像 用到的参数
     */
    private double[] mKeyColor = new double[]{0, 255, 0};
    private float mChromaThres = 0.45f;
    private float mChromaThresT = 0.3f;
    private float mAlphaL = 0.2f;
    private float mStartX = 0f;
    private float mStartY = 0f;
    private float mEndX = 1f;
    private float mEndY = 1f;
    private boolean mRunBgSegGreen = true;
    private int mBgSegGreenItem;
    private String mSourcePath = "bg_seg_green/classroom.mp4";
    private VideoDecoder mVideoDecoder;

    /**
     * 初始化系统环境，加载底层数据，并进行网络鉴权。
     * 应用使用期间只需要初始化一次，无需释放数据。
     * 必须在SDK其他接口前调用，否则会引起应用崩溃。
     */
    public static void initFURenderer(Context context) {
        if (sIsInited) {
            return;
        }
        // {trace:0, debug:1, info:2, warn:3, error:4, critical:4, off:6}
        faceunity.fuSetLogLevel(3);
        faceunity.fuCreateEGLContext();
        // 获取 Nama SDK 版本信息
        Log.e(TAG, "fu sdk version " + faceunity.fuGetVersion());
        Log.i(TAG, "device info: " + DeviceUtils.retrieveDeviceInfo(context));
        faceunity.fuSetup(new byte[0], authpack.A());
        faceunity.fuReleaseEGLContext();

        // 提前加载人脸检测算法模型，必选
        loadAiModel(context, BUNDLE_AI_MODEL_FACE_PROCESSOR, faceunity.FUAITYPE_FACEPROCESSOR);
        // 提前加载舌头跟踪算法模型，可选
        loadTongueModel(context, BUNDLE_TONGUE);
        boolean isInited = isLibInit();
        sIsInited = isInited;
        Log.i(TAG, "initFURenderer finish. isLibraryInit: " + isInited);
    }

    /**
     * 释放 SDK 占用的内存。如需再次使用，需要调用 fuSetup
     */
    public static void destroyLibData() {
        if (sIsInited) {
            releaseAiModel(faceunity.FUAITYPE_FACEPROCESSOR);
            faceunity.fuDestroyLibData();
            sIsInited = isLibInit();
            Log.d(TAG, "destroyLibData. isLibraryInit: " + (sIsInited ? "yes" : "no"));
        }
    }

    /**
     * SDK 是否初始化。fuSetup 后表示已经初始化，fuDestroyLibData 后表示已经销毁
     *
     * @return 1 inited, 0 not init.
     */
    public static boolean isLibInit() {
        return faceunity.fuIsLibraryInit() == 1;
    }

    /**
     * 加载 AI 模型资源
     *
     * @param context
     * @param bundlePath ai_model.bundle
     * @param type       faceunity.FUAITYPE_XXX
     */
    private static void loadAiModel(Context context, String bundlePath, int type) {
        byte[] buffer = readFile(context, bundlePath);
        if (buffer != null) {
            int isLoaded = faceunity.fuLoadAIModelFromPackage(buffer, type);
            Log.d(TAG, "loadAiModel. type: " + type + ", isLoaded: " + (isLoaded == 1 ? "yes" : "no"));
        }
    }

    /**
     * 释放 AI 模型资源
     *
     * @param type
     */
    private static void releaseAiModel(int type) {
        if (faceunity.fuIsAIModelLoaded(type) == 1) {
            int isReleased = faceunity.fuReleaseAIModel(type);
            Log.d(TAG, "releaseAiModel. type: " + type + ", isReleased: " + (isReleased == 1 ? "yes" : "no"));
        }
    }

    private static void releaseAllAiModel() {
        releaseAiModel(faceunity.FUAITYPE_BACKGROUNDSEGMENTATION);
        releaseAiModel(faceunity.FUAITYPE_HAIRSEGMENTATION);
        releaseAiModel(faceunity.FUAITYPE_HANDGESTURE);
        releaseAiModel(faceunity.FUAITYPE_HUMAN_PROCESSOR);
    }

    /**
     * 加载 bundle 道具，不需要 EGL Context，可以异步执行
     *
     * @param bundlePath bundle 文件路径
     * @return 道具句柄，大于 0 表示加载成功
     */
    private static int loadItem(Context context, String bundlePath) {
        int handle = 0;
        if (!TextUtils.isEmpty(bundlePath)) {
            byte[] buffer = readFile(context, bundlePath);
            if (buffer != null) {
                handle = faceunity.fuCreateItemFromPackage(buffer);
            }
        }
        Log.d(TAG, "loadItem. bundlePath: " + bundlePath + ", itemHandle: " + handle);
        return handle;
    }

    /**
     * 加载舌头跟踪数据包，开启舌头跟踪，仅限 Animoji 模块适用
     * 如果要使用舌头跟踪，请随 fuSetup 一同初始化
     *
     * @param context
     * @param bundlePath tongue.bundle
     */
    private static void loadTongueModel(Context context, String bundlePath) {
        byte[] buffer = readFile(context, bundlePath);
        if (buffer != null) {
            int isLoaded = faceunity.fuLoadTongueModel(buffer);
            Log.d(TAG, "loadTongueModel. isLoaded: " + (isLoaded == 0 ? "no" : "yes"));
        }
    }

    /**
     * 从 assets 文件夹或者本地磁盘读文件
     *
     * @param context
     * @param path
     * @return
     */
    private static byte[] readFile(Context context, String path) {
        InputStream is = null;
        try {
            is = context.getAssets().open(path);
        } catch (IOException e1) {
            Log.w(TAG, "readFile: e1", e1);
            // open assets failed, then try sdcard
            try {
                is = new FileInputStream(path);
            } catch (IOException e2) {
                Log.w(TAG, "readFile: e2", e2);
            }
        }
        if (is != null) {
            try {
                byte[] buffer = new byte[is.available()];
                int length = is.read(buffer);
                Log.v(TAG, "readFile. path: " + path + ", length: " + length + " Byte");
                is.close();
                return buffer;
            } catch (IOException e3) {
                Log.e(TAG, "readFile: e3", e3);
            }
        }
        return null;
    }

    /**
     * 获取 Nama SDK 完整版本号，例如 7.2.0_phy_91ee86c9_451bd41
     *
     * @return full version
     */
    public static String getVersion() {
        return faceunity.fuGetVersion();
    }

    /**
     * 获取证书相关的权限码
     */
    public static int getModuleCode(int index) {
        return faceunity.fuGetModuleCode(index);
    }

    /**
     * FURenderer构造函数
     */
    private FURenderer(Context context, boolean isCreateEGLContext) {
        this.mContext = context;
        this.mIsCreateEGLContext = isCreateEGLContext;
    }

    /**
     * 创建及初始化faceunity相应的资源
     */
    public void onSurfaceCreated() {
        Log.e(TAG, "onSurfaceCreated");
        mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>(16));
        mGlThreadId = Thread.currentThread().getId();
        HandlerThread handlerThread = new HandlerThread("FUItemWorker");
        handlerThread.start();
        Handler fuItemHandler = new FUItemHandler(handlerThread.getLooper());
        mFuItemHandler = fuItemHandler;

        /**
         * fuCreateEGLContext 创建OpenGL环境
         * 适用于没OpenGL环境时调用
         * 如果调用了fuCreateEGLContext，在销毁时需要调用fuReleaseEGLContext
         */
        if (mIsCreateEGLContext) {
            faceunity.fuCreateEGLContext();
        }

        mFrameId = 0;
        setMaxFaces(mMaxFaces);
        int rotationMode = calculateRotationMode();
        faceunity.fuSetDefaultRotationMode(rotationMode);
        mRotationMode = rotationMode;

        if (mIsLoadAiHumanProcessor) {
            fuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadAiModel(mContext, AI_MODEL_ASSETS_DIR + "ai_human_processor.bundle", faceunity.FUAITYPE_HUMAN_PROCESSOR);
                    setMaxHumans(mMaxHumans);
                }
            });
        }
        if (mIsLoadAiGesture) {
            fuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadAiModel(mContext, AI_MODEL_ASSETS_DIR + "ai_hand_processor.bundle", faceunity.FUAITYPE_HANDGESTURE);
                }
            });
        }
        if (isNeedFaceBeauty) {
            fuItemHandler.sendEmptyMessage(ITEM_ARRAYS_FACE_BEAUTY_INDEX);
        }
        if (isNeedBeautyHair) {
            fuItemHandler.sendEmptyMessage(ITEM_ARRAYS_BEAUTY_HAIR_INDEX);
        }
        if (isNeedAnimoji3D) {
            fuItemHandler.sendEmptyMessage(ITEM_ARRAYS_ABIMOJI_3D_INDEX);
        }
        if (isNeedBodySlim) {
            fuItemHandler.sendEmptyMessage(ITEM_ARRAYS_BEAUTIFY_BODY);
        }
        if (isNeedPosterFace) {
            mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX] = loadItem(mContext, BUNDLE_CHANGE_FACE);
        }

        // 设置动漫滤镜
        int cartoonFilterStyle = mCartoonFilterStyle;
        mCartoonFilterStyle = CartoonFilterParam.NO_FILTER;
        setCartoonFilter(cartoonFilterStyle);

        // 异步加载默认道具，放在加载 animoji 3D 和动漫滤镜之后
        if (mDefaultEffect != null) {
            Message.obtain(fuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect).sendToTarget();
        }

        // 恢复美妆的参数值
        if (mMakeupEntity != null) {
            Message.obtain(fuItemHandler, ITEM_ARRAYS_FACE_MAKEUP_INDEX, new MakeupEntity(mMakeupEntity)).sendToTarget();
        }

        // 恢复轻美妆的参数值
        if (mLightMakeupItemMap.size() > 0) {
            Collection<LightMakeupItem> makeupItems = mLightMakeupItemMap.values();
            onLightMakeupCombinationSelected(new ArrayList<>(makeupItems));
        }
    }

    /**
     * 单输入接口(fuRenderToNV21Image)
     *
     * @param img NV21数据
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCameraFacing != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= faceunity.FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToNV21Image(img, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 单输入接口 (fuRenderToRgbaImage)
     *
     * @param img RGBA 数据
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameRgba(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCameraFacing != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= faceunity.FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToRgbaImage(img, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 单输入接口(fuRenderToNV21Image)，自定义画面数据需要回写到的byte[]
     *
     * @param img         NV21数据
     * @param w
     * @param h
     * @param readBackImg 画面数据需要回写到的byte[]
     * @param readBackW
     * @param readBackH
     * @return
     */
    public int onDrawFrame(byte[] img, int w, int h, byte[] readBackImg, int readBackW, int readBackH) {
        if (img == null || w <= 0 || h <= 0 || readBackImg == null || readBackW <= 0 || readBackH <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCameraFacing != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= faceunity.FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToNV21Image(img, w, h, mFrameId++, mItemsArray, flags,
                readBackW, readBackH, readBackImg);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 双输入接口(fuDualInputToTexture)(处理后的画面数据并不会回写到数组)，由于省去相应的数据拷贝性能相对最优，推荐使用。
     *
     * @param img NV21数据
     * @param tex 纹理ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h) {
        if (tex <= 0 || img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType | mInputImageFormat;
        if (mCameraFacing != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= faceunity.FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, flags, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 双输入接口(fuDualInputToTexture)，自定义画面数据需要回写到的byte[]
     *
     * @param img         NV21数据
     * @param tex         纹理ID
     * @param w
     * @param h
     * @param readBackImg 画面数据需要回写到的byte[]
     * @param readBackW
     * @param readBackH
     * @return
     */
    public int onDrawFrame(byte[] img, int tex, int w, int h, byte[] readBackImg, int readBackW, int readBackH) {
        if (tex <= 0 || img == null || w <= 0 || h <= 0 || readBackImg == null || readBackW <= 0 || readBackH <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType | mInputImageFormat;
        if (mCameraFacing != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= faceunity.FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, flags, w, h, mFrameId++, mItemsArray,
                readBackW, readBackH, readBackImg);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 单输入接口(fuRenderToTexture)
     *
     * @param tex 纹理ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrame(int tex, int w, int h) {
        if (tex <= 0 || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType;
        if (mCameraFacing != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= faceunity.FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToTexture(tex, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 单美颜接口(fuBeautifyImage)，将输入的图像数据，送入SDK流水线进行全图美化，并输出处理之后的图像数据。
     * 该接口仅执行图像层面的美化处 理（包括滤镜、美肤），不执行人脸跟踪及所有人脸相关的操作（如美型）。
     * 由于功能集中，相比 fuDualInputToTexture 接口执行美颜道具，该接口所需计算更少，执行效率更高。
     *
     * @param tex 纹理ID
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameBeautify(int tex, int w, int h) {
        if (tex <= 0 || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType;

        if (mNeedBenchmark)
            mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuBeautifyImage(tex, flags, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark)
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 销毁faceunity相关的资源
     */
    public void onSurfaceDestroyed() {
        Log.e(TAG, "onSurfaceDestroyed");
        if (mFuItemHandler != null) {
            mFuItemHandler.removeCallbacksAndMessages(null);
            mFuItemHandler.getLooper().quit();
            mFuItemHandler = null;
        }
        if (mEventQueue != null) {
            mEventQueue.clear();
            mEventQueue = null;
        }
        if (mVideoDecoder != null) {
            mVideoDecoder.release();
            mVideoDecoder = null;
        }
        mBgSegGreenItem = 0;
        mGlThreadId = 0;
        if (mItemsArray.length > PTA_BIND_ITEM_COUNT) {
            int posterIndex = mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX];
            if (posterIndex > 0) {
                faceunity.fuDeleteTexForItem(posterIndex, "tex_input");
                faceunity.fuDeleteTexForItem(posterIndex, "tex_template");
            }

            int lightMakeupIndex = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
            if (lightMakeupIndex > 0) {
                Set<Integer> makeupTypes = mLightMakeupItemMap.keySet();
                for (Integer makeupType : makeupTypes) {
                    faceunity.fuDeleteTexForItem(lightMakeupIndex, MakeupParamHelper.getMakeupTextureKeyByType(makeupType));
                }
            }

            int faceMakeupIndex = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
            if (faceMakeupIndex > 0) {
                if (mMakeupEntity != null && mMakeupEntity.getItemHandle() > 0) {
                    faceunity.fuUnBindItems(faceMakeupIndex, new int[]{mMakeupEntity.getItemHandle()});
                    faceunity.fuDestroyItem(mMakeupEntity.getItemHandle());
                    mMakeupEntity.setItemHandle(0);
                }
                int size = mMakeupItemHandleMap.size();
                if (size > 0) {
                    Iterator<Integer> iterator = mMakeupItemHandleMap.values().iterator();
                    int[] itemHandles = new int[size];
                    for (int i = 0; iterator.hasNext(); ) {
                        itemHandles[i++] = iterator.next();
                    }
                    faceunity.fuUnBindItems(faceMakeupIndex, itemHandles);
                    for (int itemHandle : itemHandles) {
                        if (itemHandle > 0) {
                            faceunity.fuDestroyItem(itemHandle);
                        }
                    }
                    mMakeupItemHandleMap.clear();
                }
            }
        }

        mFrameId = 0;
        mIsNeedUpdateFaceBeauty = true;
        resetTrackStatus();
        releaseAllAiModel();
        destroyControllerRelated();
        for (int item : mItemsArray) {
            if (item > 0) {
                faceunity.fuDestroyItem(item);
            }
        }
        Arrays.fill(mItemsArray, 0);
        faceunity.fuDestroyAllItems();
        faceunity.fuDone();
        faceunity.fuOnDeviceLost();
        if (mIsCreateEGLContext) {
            faceunity.fuReleaseEGLContext();
        }
    }

    //************************** PTA 相关功能 ******************************

    private static final String PTA_ASSETS_DIR = "pta/";
    private static final String PTA_BOY_DIR = "boy/";
    private static final String PTA_GIRL_DIR = "girl/";
    private static final String[] PTA_BOY_BUNDLES = {"head.bundle", "midBody_male.bundle",
            "male_hair_5.bundle", "toushi_7.bundle", "peishi_erding_2.bundle", "waitao_3.bundle",
            "kuzi_changku_5.bundle", "xiezi_tuoxie_2.bundle"};
    private static final String[] PTA_GIRL_BUNDLES = {"head.bundle", "midBody_female.bundle",
            "female_hair_23.bundle", "toushi_5.bundle", "taozhuang_12.bundle", "facemakeup_3.bundle",
            "xiezi_danxie.bundle"};
    private static final String[] GESTURE_BIND_BUNDLES = {"anim_idle.bundle", "anim_eight.bundle", "anim_fist.bundle",
            "anim_greet.bundle", "anim_gun.bundle", "anim_heart.bundle", "anim_hold.bundle", "anim_korheart.bundle",
            "anim_merge.bundle", "anim_ok.bundle", "anim_one.bundle", "anim_palm.bundle", "anim_rock.bundle",
            "anim_six.bundle", "anim_thumb.bundle", "anim_two.bundle"};
    private static final int PTA_BIND_ITEM_COUNT = 2; // PTA 默认绑定两项：config 和 bg
    private static final int PTA_ITEM_COUNT = 2;
    private static final int PTA_ALWAYS_BIND_ITEM_COUNT = PTA_BIND_ITEM_COUNT + GESTURE_BIND_BUNDLES.length; // PTA 始终绑定的 config、bg、anim
    /**
     * 人体跟踪模式，全身或者半身
     */
    public static final int HUMAN_TRACK_SCENE_FULL = 1;
    public static final int HUMAN_TRACK_SCENE_HALF = 0;
    private faceunity.RotatedImage mRotatedImage = new faceunity.RotatedImage();
    private int[] mControllerBoundItems;
    private int mHumanTrackScene = HUMAN_TRACK_SCENE_FULL;

    public int onDrawFramePta(byte[] img, int tex, int w, int h) {
        if (img == null || tex <= 0 || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrame data is invalid");
            return 0;
        }
        prepareDrawFrame();
        if (mNeedBenchmark) {
            mFuCallStartTime = System.nanoTime();
        }
        rotateImage(img, w, h);
        int fuTex = faceunity.fuRenderBundlesWithCamera(mRotatedImage.mData, tex, faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE,
                mRotatedImage.mWidth, mRotatedImage.mHeight, mFrameId++, mItemsArray);
        if (mNeedBenchmark) {
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        }
        return fuTex;
    }

    private void loadPtaController() {
        mFuItemHandler.post(new Runnable() {
            @Override
            public void run() {
                final int controllerItem = loadItem(mContext, GRAPHICS_ASSETS_DIR + "controller.bundle");
                if (controllerItem <= 0) {
                    return;
                }
                final int controllerConfigItem = loadItem(mContext, PTA_ASSETS_DIR + "controller_config.bundle");
                final int[] defaultItems = new int[PTA_BIND_ITEM_COUNT];
                defaultItems[0] = controllerConfigItem;
                if (controllerConfigItem > 0) {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            faceunity.fuBindItems(controllerItem, new int[]{controllerConfigItem});
                            Log.d(TAG, "run: controller bind config");
                        }
                    });
                }
                final int bgItem = loadItem(mContext, PTA_ASSETS_DIR + "default_bg.bundle");
                defaultItems[1] = bgItem;
                if (bgItem > 0) {
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            faceunity.fuBindItems(controllerItem, new int[]{bgItem});
                            Log.d(TAG, "run: controller bind default bg");
                        }
                    });
                }
                final int fxaaItem = loadItem(mContext, GRAPHICS_ASSETS_DIR + "fxaa.bundle");
                int[] gestureItems = new int[GESTURE_BIND_BUNDLES.length];
                for (int i = 0; i < GESTURE_BIND_BUNDLES.length; i++) {
                    int item = loadItem(mContext, PTA_ASSETS_DIR + "gesture/" + GESTURE_BIND_BUNDLES[i]);
                    gestureItems[i] = item;
                }
                final int[] validGestureItems = validateItems(gestureItems);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        faceunity.fuBindItems(controllerItem, validGestureItems);
                        Log.d(TAG, "run: bind gesture " + Arrays.toString(validGestureItems));
                    }
                });
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        // 关闭CNN面部追踪
                        faceunity.fuItemSetParam(controllerItem, "close_face_capture", 1.0);
                        // 关闭 DDE
                        faceunity.fuItemSetParam(controllerItem, "is_close_dde", 1.0);
                        // 进入身体追踪模式
                        faceunity.fuItemSetParam(controllerItem, "enable_human_processor", 1.0);
                        mRotationMode = faceunity.fuGetCurrentRotationMode();
                        // 设置 rotationMode 0，因为输入图像是转正的
                        faceunity.fuSetDefaultRotationMode(0);

                        int[] items = new int[PTA_ITEM_COUNT];
                        items[0] = controllerItem;
                        items[1] = fxaaItem;
                        mItemsArray = items;
                        int[] validDefaultItems = validateItems(defaultItems);
                        int[] controllerBoundItems = new int[validDefaultItems.length + validGestureItems.length];
                        System.arraycopy(validDefaultItems, 0, controllerBoundItems, 0, validDefaultItems.length);
                        System.arraycopy(validGestureItems, 0, controllerBoundItems, validDefaultItems.length, validGestureItems.length);
                        mControllerBoundItems = controllerBoundItems;
                        Log.d(TAG, "run: controller default bind " + Arrays.toString(controllerBoundItems));
                        setHumanTrackScene(mHumanTrackScene);
                    }
                });
            }
        });
    }

    public void selectPtaItem(final String path) {
        if (mItemsArray[0] <= 0) {
            loadPtaController();
        }
        mFuItemHandler.post(new Runnable() {
            @Override
            public void run() {
                String[] ptaBundles = null;
                switch (path) {
                    case PTA_GIRL_DIR:
                        ptaBundles = PTA_GIRL_BUNDLES;
                        break;
                    case PTA_BOY_DIR:
                        ptaBundles = PTA_BOY_BUNDLES;
                        break;
                    default:
                }
                if (ptaBundles == null) {
                    return;
                }
                int[] bindItems = new int[ptaBundles.length];
                for (int i = 0; i < ptaBundles.length; i++) {
                    int item = loadItem(mContext, PTA_ASSETS_DIR + path + ptaBundles[i]);
                    bindItems[i] = item;
                }
                final int[] validBindItems = validateItems(bindItems);
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        //  follow sequence: unbind --> bind --> destroy
                        int controllerItem = mItemsArray[0];
                        int[] validUnbindItems = null;
                        int[] controllerBoundItems = mControllerBoundItems;
                        int[] toUnbindItems = new int[controllerBoundItems.length];
                        int validItemCount = 0;
                        for (int i = controllerBoundItems.length - 1; i >= PTA_ALWAYS_BIND_ITEM_COUNT; i--) {
                            if (controllerBoundItems[i] > 0) {
                                toUnbindItems[validItemCount++] = controllerBoundItems[i];
                            }
                        }
                        validUnbindItems = Arrays.copyOfRange(toUnbindItems, 0, validItemCount);
                        faceunity.fuUnBindItems(controllerItem, validUnbindItems);
                        Log.d(TAG, "run: controller unbind " + Arrays.toString(validUnbindItems));

                        faceunity.fuBindItems(controllerItem, validBindItems);
                        Log.d(TAG, "run: controller bind " + Arrays.toString(validBindItems));
                        if (validUnbindItems != null) {
                            for (int validItem : validUnbindItems) {
                                faceunity.fuDestroyItem(validItem);
                            }
                            Log.d(TAG, "run: controller destroy " + Arrays.toString(validUnbindItems));
                        }
                        int[] newControllerBoundItems = new int[PTA_ALWAYS_BIND_ITEM_COUNT + validBindItems.length];
                        System.arraycopy(controllerBoundItems, 0, newControllerBoundItems, 0, PTA_ALWAYS_BIND_ITEM_COUNT);
                        System.arraycopy(validBindItems, 0, newControllerBoundItems, PTA_ALWAYS_BIND_ITEM_COUNT, validBindItems.length);
                        mControllerBoundItems = newControllerBoundItems;
                        Log.i(TAG, "run: final controller bind " + Arrays.toString(newControllerBoundItems));
                    }
                });
            }
        });
    }

    private void destroyControllerRelated() {
        if (mControllerBoundItems != null && mControllerBoundItems[0] > 0) {
            int controllerItem = mItemsArray[0];
            faceunity.fuItemSetParam(controllerItem, "enable_human_processor", 0.0);
            int[] controllerBoundItems = validateItems(mControllerBoundItems);
            Log.d(TAG, "destroyControllerRelated: unbind " + Arrays.toString(controllerBoundItems));
            faceunity.fuUnBindItems(controllerItem, controllerBoundItems);
            for (int i = controllerBoundItems.length - 1; i >= 0; i--) {
                faceunity.fuDestroyItem(controllerBoundItems[i]);
            }
            Arrays.fill(controllerBoundItems, 0);
            mControllerBoundItems = null;
        }
    }

    private int[] validateItems(int[] input) {
        int[] output = new int[input.length];
        int count = 0;
        for (int i : input) {
            if (i > 0) {
                output[count++] = i;
            }
        }
        return Arrays.copyOfRange(output, 0, count);
    }

    public void setHumanTrackScene(final int humanTrackScene) {
        Log.d(TAG, "setHumanTrackScene() called with: humanTrackScene = [" + humanTrackScene + "]");
        mHumanTrackScene = humanTrackScene;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int item = mItemsArray[0];
                if (item > 0) {
                    boolean isFullScene = humanTrackScene == HUMAN_TRACK_SCENE_FULL;
                    if (isFullScene) {
                        faceunity.fuItemSetParam(item, "target_position", new double[]{0.0, 58.14, -618.94});
                        faceunity.fuItemSetParam(item, "target_angle", 0.0);
                        faceunity.fuItemSetParam(item, "reset_all", 3.0);
                    } else {
                        faceunity.fuItemSetParam(item, "target_position", new double[]{0.0, 11.76, -183.89});
                        faceunity.fuItemSetParam(item, "target_angle", 0);
                        faceunity.fuItemSetParam(item, "reset_all", 6);
                    }
                    faceunity.fuItemSetParam(item, "human_3d_track_set_scene", humanTrackScene);
                }
            }
        });
    }

    private void rotateImage(byte[] img, int width, int height) {
        boolean isFrontCam = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT;
        int rotateMode = isFrontCam ? faceunity.FU_ROTATION_MODE_270 : faceunity.FU_ROTATION_MODE_90;
        int flipX = isFrontCam ? 1 : 0;
        int flipY = 0;
        faceunity.fuRotateImage(mRotatedImage, img, faceunity.FU_FORMAT_NV21_BUFFER, width, height, rotateMode, flipX, flipY);
        faceunity.fuSetInputCameraMatrix(flipX, flipY, rotateMode);
    }

    //************************** PTA 相关功能 ******************************

    /**
     * 获取 rotation
     *
     * @return
     */
    public float[] getRotationData() {
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        return rotationData;
    }

    /**
     * 获取 landmarks 点位
     *
     * @param faceId    0,1...
     * @param landmarks float array
     */
    public void getLandmarksData(int faceId, float[] landmarks) {
        int isTracking = faceunity.fuIsTracking();
        if (isTracking > 0) {
            faceunity.fuGetFaceInfo(faceId, LANDMARKS, landmarks);
        }
    }

    /**
     * 获取检测到的人脸数量
     *
     * @return
     */
    public int getTrackedFaceCount() {
        return faceunity.fuIsTracking();
    }

    public int trackFace(byte[] img, int w, int h, int rotMode) {
        if (img == null || w <= 0 || h <= 0) {
            return 0;
        }
        faceunity.fuOnCameraChange();
        int currRotMode = faceunity.fuGetCurrentRotationMode();
        faceunity.fuSetDefaultRotationMode(rotMode);
        for (int i = 0; i < MAX_TRACK_COUNT; i++) {
            faceunity.fuTrackFace(img, faceunity.FU_FORMAT_NV21_BUFFER, w, h);
        }
        faceunity.fuSetDefaultRotationMode(currRotMode);
        return faceunity.fuIsTracking();
    }

    public float[] getFaceRectData(int i, int rotMode) {
        int currRotMode = faceunity.fuGetCurrentRotationMode();
        faceunity.fuSetDefaultRotationMode(rotMode);
        faceunity.fuGetFaceInfo(i, "face_rect", faceRectData);
        faceunity.fuSetDefaultRotationMode(currRotMode);
        return faceRectData;
    }

    //--------------------------------------对外可使用的接口----------------------------------------

    /**
     * 类似 GLSurfaceView 的 queueEvent 机制
     *
     * @param r
     */
    public void queueEvent(Runnable r) {
        if (Thread.currentThread().getId() == mGlThreadId) {
            r.run();
        } else {
            if (mEventQueue != null) {
                mEventQueue.add(r);
            }
        }
    }

    /**
     * 设置需要识别的人脸个数
     *
     * @param maxFaces
     */
    public void setMaxFaces(final int maxFaces) {
        if (maxFaces > 0) {
            mMaxFaces = maxFaces;
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "setMaxFaces() called with: maxFaces = [" + maxFaces + "]");
                    faceunity.fuSetMaxFaces(maxFaces);
                }
            });
        }
    }

    /**
     * 设置需要识别的人体个数
     *
     * @param maxHumans
     */
    public void setMaxHumans(final int maxHumans) {
        if (maxHumans > 0) {
            mMaxHumans = maxHumans;
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "setMaxHumans() called with: maxHumans = [" + maxHumans + "]");
                    faceunity.fuHumanProcessorSetMaxHumans(maxHumans);
                }
            });
        }
    }

    /**
     * 设置锯齿优化参数，优化捏脸模型的效果
     *
     * @param samples 推荐设置为 4 ，设置 0 表示关闭。
     */
    public void setMultiSamples(final int samples) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuSetMultiSamples(samples);
            }
        });
    }

    /**
     * 每帧处理画面时被调用
     */
    private void prepareDrawFrame() {
        //计算FPS等数据
        benchmarkFPS();
        // 人脸、人体、手势检测
        if (mIsLoadAiHumanProcessor) {
            int trackHumans = faceunity.fuHumanProcessorGetNumResults();
            if (mOnTrackingStatusChangedListener != null && mTrackHumanStatus != trackHumans) {
                mTrackHumanStatus = trackHumans;
                mOnTrackingStatusChangedListener.onTrackStatusChanged(TRACK_TYPE_HUMAN, trackHumans);
            }
        } else if (mIsLoadAiGesture) {
            int trackGesture = faceunity.fuHandDetectorGetResultNumHands();
            if (mOnTrackingStatusChangedListener != null && mTrackGestureStatus != trackGesture) {
                mTrackGestureStatus = trackGesture;
                mOnTrackingStatusChangedListener.onTrackStatusChanged(TRACK_TYPE_GESTURE, trackGesture);
            }
        } else {
            int trackFace = faceunity.fuIsTracking();
            if (mOnTrackingStatusChangedListener != null && mTrackFaceStatus != trackFace) {
                mTrackFaceStatus = trackFace;
                mOnTrackingStatusChangedListener.onTrackStatusChanged(TRACK_TYPE_FACE, trackFace);
            }
        }

        // 获取 SDK 错误信息，并调用回调接口
        int error = faceunity.fuGetSystemError();
        if (error != 0) {
            String errorMessage = faceunity.fuGetSystemErrorString(error);
            Log.e(TAG, "system error code: " + error + ", error message: " + errorMessage);
            if (mOnSystemErrorListener != null) {
                mOnSystemErrorListener.onSystemError(errorMessage);
            }
        }

        //修改美颜参数
        if (mIsNeedUpdateFaceBeauty && mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] > 0) {
            int itemFaceBeauty = mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX];

//            Log.v(TAG, "prepareDrawFrame: face beauty params: isBeautyOn:" + sIsBeautyOn + ", filterName:"
//                    + sFilterName + ", filterLevel:" + mFilterLevel + ", blurType:" + mBlurType
//                    + ", blurLevel:" + mBlurLevel + ", colorLevel:" + mColorLevel + ", redLevel:" + mRedLevel
//                    + ", eyeBright:" + mEyeBright + ", toothWhiten:" + mToothWhiten + ", faceShapeLevel:"
//                    + mFaceShapeLevel + ", faceShape:" + mFaceShape + ", eyeEnlarging:" + mEyeEnlarging
//                    + ", cheekThinning:" + mCheekThinning + ", cheekNarrow:" + mCheekNarrow + ", cheekSmall:"
//                    + mCheekSmall + ", cheekV:" + mCheekV + ", intensityNose:" + mIntensityNose + ", intensityChin:"
//                    + mIntensityChin + ", intensityForehead:" + mIntensityForehead + ", intensityMouth:"
//                    + mIntensityMouth + ", microPouch:" + sMicroPouch + ", microNasolabialFolds:"
//                    + sMicroNasolabialFolds + ", microSmile:" + sMicroSmile + ", microCanthus:"
//                    + sMicroCanthus + ", microPhiltrum:" + sMicroPhiltrum + ", microLongNose:"
//                    + sMicroLongNose + ", microEyeSpace:" + sMicroEyeSpace + ", eyeRotate:" + sMicroEyeRotate);

            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.IS_BEAUTY_ON, sIsBeautyOn);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FILTER_NAME, sFilterName);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FILTER_LEVEL, mFilterLevel);

            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.HEAVY_BLUR, 0.0);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.BLUR_TYPE, mBlurType);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.BLUR_LEVEL, 6.0 * mBlurLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.COLOR_LEVEL, mColorLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.SHARPEN, mSharpen);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.RED_LEVEL, mRedLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.EYE_BRIGHT, mEyeBright);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.TOOTH_WHITEN, mToothWhiten);

            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FACE_SHAPE_LEVEL, mFaceShapeLevel);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.FACE_SHAPE, mFaceShape);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.EYE_ENLARGING, mEyeEnlarging);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_THINNING, mCheekThinning);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_NARROW, mCheekNarrow);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_SMALL, mCheekSmall);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.CHEEK_V, mCheekV);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_CHEEKBONES, mIntensityCheekbones);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_LOW_JAW, mIntensityLowerJaw);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_NOSE, mIntensityNose);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_CHIN, mIntensityChin);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_FOREHEAD, mIntensityForehead);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_MOUTH, mIntensityMouth);

            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.REMOVE_POUCH_STRENGTH, sMicroPouch);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.REMOVE_NASOLABIAL_FOLDS_STRENGTH, sMicroNasolabialFolds);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_SMILE, sMicroSmile);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_CANTHUS, sMicroCanthus);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_PHILTRUM, sMicroPhiltrum);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_LONG_NOSE, sMicroLongNose);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_EYE_SPACE, sMicroEyeSpace);
            faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.INTENSITY_EYE_ROTATE, sMicroEyeRotate);

            mIsNeedUpdateFaceBeauty = false;
        }

        //queueEvent的Runnable在此处被调用
        while (!mEventQueue.isEmpty()) {
            mEventQueue.remove(0).run();
        }
    }

    public void cameraChanged() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
                resetTrackStatus();
            }
        });
    }

    private void resetTrackStatus() {
        faceunity.fuOnCameraChange();
        faceunity.fuHumanProcessorReset();
    }

    /**
     * camera切换时需要调用
     *
     * @param cameraFacing     前后置摄像头ID
     * @param inputOrientation
     */
    public void onCameraChange(final int cameraFacing, final int inputOrientation) {
        Log.d(TAG, "onCameraChange. cameraFacing: " + cameraFacing + ", inputOrientation:" + inputOrientation);
        if (mVideoDecoder != null) {
            mVideoDecoder.setFrontCam(cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
                mCameraFacing = cameraFacing;
                mInputOrientation = inputOrientation;
                resetTrackStatus();
                mRotationMode = calculateRotationMode();
                if (mItemsArray.length <= PTA_ITEM_COUNT) {
                    faceunity.fuSetDefaultRotationMode(0);
                } else {
                    faceunity.fuSetDefaultRotationMode(mRotationMode);
                    setBeautyBodyOrientation();
                    updateEffectItemParams(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                }
            }
        });
    }

    /**
     * 设置识别方向
     *
     * @param rotation
     */
    public void setTrackOrientation(final int rotation) {
        if (mDeviceOrientation != rotation) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mDeviceOrientation = rotation;
                    if (mItemsArray.length <= PTA_ITEM_COUNT) {
                        return;
                    }
                    // 人像分割 Animoji 表情识别 人像驱动 手势识别，转动手机时，重置人脸识别
                    if (mDefaultEffect != null && (mDefaultEffect.getType() == Effect.EFFECT_TYPE_PORTRAIT_SEGMENT
                            || mDefaultEffect.getType() == Effect.EFFECT_TYPE_ANIMOJI
                            || mDefaultEffect.getType() == Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION
                            || mDefaultEffect.getType() == Effect.EFFECT_TYPE_GESTURE_RECOGNITION
                            || mDefaultEffect.getType() == Effect.EFFECT_TYPE_PORTRAIT_DRIVE)) {
                        resetTrackStatus();
                    }
                    int rotationMode = calculateRotationMode();
                    faceunity.fuSetDefaultRotationMode(rotationMode);
                    mRotationMode = rotationMode;
                    Log.d(TAG, "setTrackOrientation. deviceOrientation: " + mDeviceOrientation + ", rotationMode: " + mRotationMode);
                    if (mDefaultEffect != null && mDefaultEffect.getType() != Effect.EFFECT_TYPE_ACTION_RECOGNITION) {
                        setEffectRotationMode(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                    }
                    setBeautyBodyOrientation();
                }
            });
        }
    }

    /**
     * 默认的视频模式下，不保证每帧都检测出人脸；对于图片场景，要设置图片模式
     *
     * @param mode 0 图片模式, 1 视频模式, 默认 1
     */
    public void setFaceProcessorDetectMode(final int mode) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuSetFaceProcessorDetectMode(mode);
                Log.d(TAG, "fuSetFaceProcessorDetectMode: " + mode);
            }
        });
    }

    /**
     * 美妆功能点位镜像，0为关闭，1为开启
     *
     * @param isFlipPoints     是否镜像点位
     * @param isSetImmediately 是否立即设置
     */
    public void setIsMakeupFlipPoints(final boolean isFlipPoints, boolean isSetImmediately) {
        if (mIsMakeupFlipPoints == isFlipPoints) {
            return;
        }
        Log.d(TAG, "setIsMakeupFlipPoints() isFlipPoints = [" + isFlipPoints + "], isSetImmediately = [" + isSetImmediately + "]");
        mIsMakeupFlipPoints = isFlipPoints;
        if (isSetImmediately) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    int item = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                    if (item > 0) {
                        faceunity.fuItemSetParam(item, MakeupParamHelper.MakeupParam.IS_FLIP_POINTS, isFlipPoints ? 1.0 : 0.0);
                    }
                }
            });
        }
    }

    private int calculateRotModeLagacy() {
        int mode;
        if (mInputOrientation == 270) {
            if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mode = mDeviceOrientation / 90;
            } else {
                mode = (mDeviceOrientation - 180) / 90;
            }
        } else {
            if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mode = (mDeviceOrientation + 180) / 90;
            } else {
                mode = mDeviceOrientation / 90;
            }
        }
        return mode;
    }

    /**
     * 计算 RotationMode
     *
     * @return rotationMode
     */
    private int calculateRotationMode() {
        if (mExternalInputType == EXTERNAL_INPUT_TYPE_IMAGE) {
            // 外部图片
            return faceunity.FU_ROTATION_MODE_0;
        } else if (mExternalInputType == EXTERNAL_INPUT_TYPE_VIDEO) {
            // 外部视频
            switch (mInputOrientation) {
                case 90:
                    return faceunity.FU_ROTATION_MODE_270;
                case 270:
                    return faceunity.FU_ROTATION_MODE_90;
                case 180:
//                    return faceunity.FU_ROTATION_MODE_180;
                case 0:
                default:
                    return faceunity.FU_ROTATION_MODE_0;
            }
        } else {
            // 相机数据
            int rotMode = faceunity.FU_ROTATION_MODE_0;
            if (mInputOrientation == 270) {
                if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    rotMode = mDeviceOrientation / 90;
                } else {
                    if (mDeviceOrientation == 180) {
                        rotMode = faceunity.FU_ROTATION_MODE_0;
                    } else if (mDeviceOrientation == 0) {
                        rotMode = faceunity.FU_ROTATION_MODE_180;
                    } else {
                        rotMode = mDeviceOrientation / 90;
                    }
                }
            } else if (mInputOrientation == 90) {
                if (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    if (mDeviceOrientation == 90) {
                        rotMode = faceunity.FU_ROTATION_MODE_270;
                    } else if (mDeviceOrientation == 270) {
                        rotMode = faceunity.FU_ROTATION_MODE_90;
                    } else {
                        rotMode = mDeviceOrientation / 90;
                    }
                } else {
                    if (mDeviceOrientation == 0) {
                        rotMode = faceunity.FU_ROTATION_MODE_180;
                    } else if (mDeviceOrientation == 90) {
                        rotMode = faceunity.FU_ROTATION_MODE_270;
                    } else if (mDeviceOrientation == 180) {
                        rotMode = faceunity.FU_ROTATION_MODE_0;
                    } else {
                        rotMode = faceunity.FU_ROTATION_MODE_90;
                    }
                }
            }
            return rotMode;
        }
    }

    public void setVideoParams(int inputOrientation, boolean isSystemCameraRecord) {
        mInputOrientation = inputOrientation;
        mIsSystemCameraRecord = isSystemCameraRecord;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int rotationMode = calculateRotationMode();
                faceunity.fuSetDefaultRotationMode(rotationMode);
                mRotationMode = rotationMode;
            }
        });
    }

    public void setDefaultEffect(Effect defaultEffect) {
        mDefaultEffect = defaultEffect;
    }

    //--------------------------------------美颜参数与道具回调----------------------------------------

    @Override
    public void onMusicFilterTime(final long time) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int itemEffect = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (itemEffect > 0) {
                    faceunity.fuItemSetParam(itemEffect, "music_time", time);
                }
            }
        });
    }

    @Override
    public void onEffectSelected(Effect effect) {
        if (effect == null || effect == mDefaultEffect) {
            return;
        }
        mDefaultEffect = effect;
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.removeMessages(ITEM_ARRAYS_EFFECT_INDEX);
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect));
                }
            });
        } else {
            mFuItemHandler.removeMessages(ITEM_ARRAYS_EFFECT_INDEX);
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect));
        }
    }

    @Override
    public void setBeautificationOn(boolean isOn) {
        float isBeautyOn = isOn ? 1.0F : 0.0F;
        if (sIsBeautyOn == isBeautyOn) {
            return;
        }
        sIsBeautyOn = isBeautyOn;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int itemFaceBeauty = mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX];
                if (itemFaceBeauty > 0) {
                    faceunity.fuItemSetParam(itemFaceBeauty, BeautificationParam.IS_BEAUTY_ON, sIsBeautyOn);
                }
            }
        });
    }

    @Override
    public void onFilterLevelSelected(float level) {
        mFilterLevel = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onFilterNameSelected(String name) {
        sFilterName = name;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onHairSelected(final int type, final int index, final float strength) {
        if (mHairColorType == type) {
            onHairStrengthSelected(index, strength);
        } else {
            mHairColorType = type;
            mHairColorIndex = index;
            mHairColorStrength = strength;
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_BEAUTY_HAIR_INDEX);
        }
    }

    @Override
    public void onHairStrengthSelected(final int index, final float strength) {
        mHairColorIndex = index;
        mHairColorStrength = strength;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int itemHair = mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX];
                if (itemHair > 0) {
                    faceunity.fuItemSetParam(itemHair, HairParam.INDEX, mHairColorIndex);
                    faceunity.fuItemSetParam(itemHair, HairParam.STRENGTH, mHairColorStrength);
                }
            }
        });
    }

    @Override
    public void onBlurTypeSelected(float type) {
        mBlurType = type;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onBlurLevelSelected(float level) {
        mBlurLevel = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onSharpenLevelSelected(float level) {
        mSharpen = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onColorLevelSelected(float level) {
        mColorLevel = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onRedLevelSelected(float level) {
        mRedLevel = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onEyeBrightSelected(float level) {
        mEyeBright = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onToothWhitenSelected(float level) {
        mToothWhiten = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onEyeEnlargeSelected(float level) {
        mEyeEnlarging = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekThinningSelected(float level) {
        mCheekThinning = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekNarrowSelected(float level) {
        // 窄脸参数上限为0.5
        mCheekNarrow = level / 2;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekSmallSelected(float level) {
        // 小脸参数上限为0.5
        mCheekSmall = level / 2;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekVSelected(float level) {
        mCheekV = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setCheekbonesIntensity(float intensity) {
        mIntensityCheekbones = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setLowerJawIntensity(float intensity) {
        mIntensityLowerJaw = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onIntensityChinSelected(float level) {
        mIntensityChin = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onIntensityForeheadSelected(float level) {
        mIntensityForehead = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onIntensityNoseSelected(float level) {
        mIntensityNose = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onIntensityMouthSelected(float level) {
        mIntensityMouth = level;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onPosterTemplateSelected(final int tempWidth, final int tempHeight, final byte[] temp, final float[] landmark) {
        Log.v(TAG, "onPosterTemplateSelected() called with: tempWidth = [" + tempWidth + "], tempHeight = [" + tempHeight + "], temp = [" + temp + "], landmark = [" + Arrays.toString(landmark) + "]");
        int item = mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX];
        if (item > 0) {
            double[] landmarks = floatArrayToDouble(landmark);
            // 模板图片的宽
            faceunity.fuItemSetParam(item, "template_width", tempWidth);
            // 模板图片的高
            faceunity.fuItemSetParam(item, "template_height", tempHeight);
            // 图片的特征点，75个点
            faceunity.fuItemSetParam(item, "template_face_points", landmarks);
            // 模板图片的 RGBA byte数组
            faceunity.fuCreateTexForItem(item, "tex_template", temp, tempWidth, tempHeight);
        }
    }

    @Override
    public void onPosterInputPhoto(final int inputWidth, final int inputHeight, final byte[] input, final float[] landmark) {
        Log.v(TAG, "onPosterInputPhoto() called with: inputWidth = [" + inputWidth + "], inputHeight = [" + inputHeight + "], input = [" + input + "], landmark = [" + Arrays.toString(landmark) + "]");
        int item = mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX];
        if (item > 0) {
            double[] landmarks = floatArrayToDouble(landmark);
            // 输入图片的宽
            faceunity.fuItemSetParam(item, "input_width", inputWidth);
            // 输入图片的高
            faceunity.fuItemSetParam(item, "input_height", inputHeight);
            // 输入图片的特征点，75个点
            faceunity.fuItemSetParam(item, "input_face_points", landmarks);
            // 输入图片的 RGBA byte 数组
            faceunity.fuCreateTexForItem(item, "tex_input", input, inputWidth, inputHeight);
        }
    }

    @Override
    public void setMakeupItemParam(final Map<String, Object> paramMap) {
        if (paramMap == null) {
            return;
        }

        mMakeupParams.putAll(paramMap);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int makeupHandle = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                if (makeupHandle <= 0) {
                    return;
                }
                Set<Map.Entry<String, Object>> entries = paramMap.entrySet();
                faceunity.fuItemSetParam(makeupHandle, MakeupParamHelper.MakeupParam.IS_CLEAR_MAKEUP, 0.0);
                for (Map.Entry<String, Object> entry : entries) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof String && ((String) value).endsWith(".bundle")) {
                        int newItemHandle = loadItem(mContext, (String) value);
                        if (mMakeupItemHandleMap.containsKey(key)) {
                            int oldItemHandle = mMakeupItemHandleMap.get(key);
                            if (oldItemHandle > 0) {
                                faceunity.fuUnBindItems(makeupHandle, new int[]{oldItemHandle});
                                faceunity.fuDestroyItem(oldItemHandle);
                                Log.d(TAG, "makeup: unbind and destroy old child item: " + oldItemHandle);
                            }
                        }
                        if (newItemHandle > 0) {
                            faceunity.fuBindItems(makeupHandle, new int[]{newItemHandle});
                            Log.d(TAG, "makeup: bind new child item: " + newItemHandle);
                            mMakeupItemHandleMap.put(key, newItemHandle);
                        }
                    } else if (value instanceof double[]) {
                        double[] temp = (double[]) value;
                        faceunity.fuItemSetParam(makeupHandle, key, temp);
                        Log.d(TAG, "makeup: set param key: " + key + ", value: " + Arrays.toString(temp));
                    } else if (value instanceof Double) {
                        faceunity.fuItemSetParam(makeupHandle, key, (Double) value);
                        Log.d(TAG, "makeup: set param key: " + key + ", value: " + value);
                    }
                }
            }
        });
    }

    @Override
    public void setMakeupItemIntensity(final String name, final double density) {
        mMakeupParams.put(name, density);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int makeupHandle = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                if (makeupHandle > 0) {
                    faceunity.fuItemSetParam(makeupHandle, name, density);
                    Log.v(TAG, "makeup: set param key: " + name + ", value: " + density);
                }
            }
        });
    }

    @Override
    public void setMakeupItemColor(final String name, final double[] colors) {
        if (colors == null) {
            return;
        }
        mMakeupParams.put(name, colors);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int makeupHandle = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                if (makeupHandle > 0) {
                    faceunity.fuItemSetParam(makeupHandle, name, colors);
                    Log.v(TAG, "makeup: set param key: " + name + ", value: " + Arrays.toString(colors));
                }
            }
        });
    }

    @Override
    public void selectMakeup(final MakeupEntity makeupEntity, final Map<String, Object> paramMap) {
        mMakeupParams.clear();
        if (paramMap != null) {
            mMakeupParams.putAll(paramMap);
        }
        if (mFuItemHandler != null) {
            mFuItemHandler.removeMessages(ITEM_ARRAYS_FACE_MAKEUP_INDEX);
            Message.obtain(mFuItemHandler, ITEM_ARRAYS_FACE_MAKEUP_INDEX, makeupEntity).sendToTarget();
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.removeMessages(ITEM_ARRAYS_FACE_MAKEUP_INDEX);
                    Message.obtain(mFuItemHandler, ITEM_ARRAYS_FACE_MAKEUP_INDEX, makeupEntity).sendToTarget();
                }
            });
        }
    }

    @Override
    public void setBodySlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mBodySlimStrength = intensity;
                int itemBody = mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY];
                if (itemBody > 0) {
                    faceunity.fuItemSetParam(itemBody, BodySlimParam.BODY_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setLegSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mLegSlimStrength = intensity;
                int itemBody = mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY];
                if (itemBody > 0) {
                    faceunity.fuItemSetParam(itemBody, BodySlimParam.LEG_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setWaistSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mWaistSlimStrength = intensity;
                int itemBody = mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY];
                if (itemBody > 0) {
                    faceunity.fuItemSetParam(itemBody, BodySlimParam.WAIST_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setShoulderSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mShoulderSlimStrength = intensity;
                int itemBody = mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY];
                if (itemBody > 0) {
                    faceunity.fuItemSetParam(itemBody, BodySlimParam.SHOULDER_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setHipSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mHipSlimStrength = intensity;
                int itemBody = mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY];
                if (itemBody > 0) {
                    faceunity.fuItemSetParam(itemBody, BodySlimParam.HIP_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setHeadSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mHeadSlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BodySlimParam.HEAD_SLIM, intensity);
                }
            }
        });
    }

    @Override
    public void setLegThinSlimIntensity(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mLegThinSlimStrength = intensity;
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BodySlimParam.LEG_SLIM, intensity);
                }
            }
        });
    }

    @Override
    public void setRemovePouchStrength(float strength) {
        sMicroPouch = strength;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setRemoveNasolabialFoldsStrength(float strength) {
        sMicroNasolabialFolds = strength;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setSmileIntensity(float intensity) {
        sMicroSmile = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setCanthusIntensity(float intensity) {
        sMicroCanthus = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setPhiltrumIntensity(float intensity) {
        sMicroPhiltrum = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setLongNoseIntensity(float intensity) {
        sMicroLongNose = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setEyeSpaceIntensity(float intensity) {
        sMicroEyeSpace = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setEyeRotateIntensity(float intensity) {
        sMicroEyeRotate = intensity;
        mIsNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onLightMakeupCombinationSelected(List<LightMakeupItem> makeupItems) {
        Set<Integer> keySet = mLightMakeupItemMap.keySet();
        for (final Integer integer : keySet) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    int item = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
                    if (item > 0) {
                        faceunity.fuItemSetParam(item, MakeupParamHelper.getMakeupIntensityKeyByType(integer), 0.0);
                    }
                }
            });
        }
        mLightMakeupItemMap.clear();

        if (makeupItems != null && makeupItems.size() > 0) {
            for (int i = 0, size = makeupItems.size(); i < size; i++) {
                LightMakeupItem makeupItem = makeupItems.get(i);
                onLightMakeupSelected(makeupItem, makeupItem.getLevel());
            }
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    int item = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
                    if (item > 0) {
                        faceunity.fuItemSetParam(item, MakeupParamHelper.MakeupParam.IS_MAKEUP_ON, 0.0);
                    }
                }
            });
        }
    }

    @Override
    public void onLightMakeupItemLevelChanged(LightMakeupItem makeupItem) {
        final int type = makeupItem.getType();
        LightMakeupItem item = mLightMakeupItemMap.get(type);
        if (item != null) {
            item.setLevel(makeupItem.getLevel());
        } else {
            mLightMakeupItemMap.put(type, makeupItem.cloneSelf());
        }
        final float level = makeupItem.getLevel();
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int item = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, MakeupParamHelper.getMakeupIntensityKeyByType(type), level);
                }
            }
        });
    }

    @Override
    public void setCartoonFilter(final int style) {
        if (mCartoonFilterStyle == style) {
            return;
        }
        mCartoonFilterStyle = style;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                boolean isValid = mCartoonFilterStyle > CartoonFilterParam.NO_FILTER;
                int item = mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX];
                if (item > 0) {
                    if (isValid) {
                        faceunity.fuItemSetParam(item, CartoonFilterParam.STYLE, style);
                    } else {
                        faceunity.fuDestroyItem(item);
                        mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] = 0;
                    }
                } else if (isValid) {
                    mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_CARTOON_FILTER_INDEX);
                }
            }
        });
    }


    @Override
    public void setKeyColor(final double[] rgb) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mKeyColor = rgb;
                int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, "key_color", rgb);
//                    Log.d(TAG, "setKeyColor " + Arrays.toString(rgb));
                }
            }
        });
    }

    @Override
    public void setChromaThres(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mChromaThres = intensity;
                int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, "chroma_thres", intensity);
                }
            }
        });
    }

    @Override
    public void setChromaThresT(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mChromaThresT = intensity;
                int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, "chroma_thres_T", intensity);
                }
            }
        });
    }

    @Override
    public void setAlphaL(final float intensity) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mAlphaL = intensity;
                int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, "alpha_L", intensity);
                }
            }
        });
    }

    @Override
    public void setTransform(final float startX, final float startY, final float endX, final float endY) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mStartX = startX;
                mStartY = startY;
                mEndX = endX;
                mEndY = endY;
                int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, "start_x", startX);
                    faceunity.fuItemSetParam(item, "start_y", startY);
                    faceunity.fuItemSetParam(item, "end_x", endX);
                    faceunity.fuItemSetParam(item, "end_y", endY);
                }
            }
        });
    }

    @Override
    public void setTexBgSource(String filePath) {
        mSourcePath = filePath;
        if (mVideoDecoder == null) {
            return;
        }
        if (filePath == null) {
            mVideoDecoder.stop();
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                    if (item > 0) {
                        faceunity.fuDeleteTexForItem(item, "tex_bg");
                    }
                }
            });
        } else {
            File file = new File(FileUtils.getExternalFileDir(mContext), filePath);
            mVideoDecoder.start(file.getAbsolutePath());
        }
    }

    @Override
    public void setRunBgSegGreen(final boolean run) {
        if (mRunBgSegGreen == run) {
            return;
        }
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRunBgSegGreen = run;
                mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = run ? mBgSegGreenItem : 0;
            }
        });
    }

    private VideoDecoder.OnReadPixelListener mOnReadPixelListener = new VideoDecoder.OnReadPixelListener() {

        @Override
        public void onReadPixel(final int width, final int height, final byte[] rgba) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    int item = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                    if (item > 0) {
                        faceunity.fuDeleteTexForItem(item, "tex_bg");
                        faceunity.fuCreateTexForItem(item, "tex_bg", rgba, width, height);
//                        Log.v(TAG, "fuCreateTexForItem: tex_bg. rgba: " + rgba + ", width: " + width + ", height: " + height);
                    }
                }
            });
        }
    };

    /**
     * 海报换脸，输入人脸五官，自动变形调整
     *
     * @param value 范围 [0-1]，0 为关闭
     */
    public void fixPosterFaceParam(final float value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int item = mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX];
                if (item > 0) {
                    faceunity.fuItemSetParam(item, "warp_intensity", value);
                }
            }
        });
    }

    private void onLightMakeupSelected(final LightMakeupItem makeupItem, final float level) {
        int type = makeupItem.getType();
        LightMakeupItem item = mLightMakeupItemMap.get(type);
        if (item != null) {
            item.setLevel(level);
        } else {
            // 复制一份
            mLightMakeupItemMap.put(type, makeupItem.cloneSelf());
        }
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIGHT_MAKEUP_INDEX, makeupItem));
                }
            });
        } else {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIGHT_MAKEUP_INDEX, makeupItem));
        }
    }

    private void setBeautyBodyOrientation() {
        int itemBody = mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY];
        if (itemBody > 0) {
            int bodyOrientation = calculateRotationMode();
            faceunity.fuItemSetParam(itemBody, BodySlimParam.ORIENTATION, bodyOrientation);
        }
    }

    private double[] floatArrayToDouble(float[] input) {
        if (input == null) {
            return null;
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }
        return output;
    }

    //--------------------------------------IsTracking（人脸识别回调相关定义）----------------------------------------

    private int mTrackHumanStatus = -1;
    private int mTrackFaceStatus = -1;
    private int mTrackGestureStatus = -1;

    public interface OnTrackingStatusChangedListener {
        /**
         * 检测状态发生变化
         *
         * @param type
         * @param status
         */
        void onTrackStatusChanged(int type, int status);
    }

    private OnTrackingStatusChangedListener mOnTrackingStatusChangedListener;

    //--------------------------------------FaceUnitySystemError（faceunity错误信息回调相关定义）----------------------------------------

    public interface OnSystemErrorListener {
        void onSystemError(String error);
    }

    private OnSystemErrorListener mOnSystemErrorListener;

    //--------------------------------------FPS（FPS相关定义）----------------------------------------

    private static final int NANO_IN_ONE_MILLI_SECOND = 1_000_000;
    private static final int NANO_IN_ONE_NANO_SECOND = 1_000_000_000;
    private static final int TIME = 10;
    private int mCurrentFrameCnt = 0;
    private long mLastOneHundredFrameTimeStamp = 0;
    private long mOneHundredFrameFUTime = 0;
    private boolean mNeedBenchmark = true;
    private long mFuCallStartTime = 0;

    private OnFUDebugListener mOnFUDebugListener;

    public interface OnFUDebugListener {
        void onFpsChange(double fps, double renderTime);
    }

    private void benchmarkFPS() {
        if (!mNeedBenchmark) {
            return;
        }
        if (++mCurrentFrameCnt == TIME) {
            mCurrentFrameCnt = 0;
            double fps = ((float) TIME * NANO_IN_ONE_NANO_SECOND / (System.nanoTime() - mLastOneHundredFrameTimeStamp));
            double renderTime = (float) mOneHundredFrameFUTime / TIME / NANO_IN_ONE_MILLI_SECOND;
            mLastOneHundredFrameTimeStamp = System.nanoTime();
            mOneHundredFrameFUTime = 0;

            if (mOnFUDebugListener != null) {
                mOnFUDebugListener.onFpsChange(fps, renderTime);
            }
        }
    }

    //--------------------------------------道具（异步加载道具）----------------------------------------

    public interface OnBundleLoadCompleteListener {
        /**
         * bundle 加载完成
         *
         * @param what
         */
        void onBundleLoadComplete(int what);
    }

    /**
     * 动作识别设置边缘距离，主要是用于适配界面顶部的 UI 元素
     *
     * @param distance
     */
    public void setEdgeDistance(final float distance) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                int itemEffect = mItemsArray[ITEM_ARRAYS_EFFECT_INDEX];
                if (itemEffect > 0) {
                    Log.d(TAG, "setEdgeDistance: " + distance);
                    faceunity.fuItemSetParam(itemEffect, "edge_distance", distance);
                }
            }
        });
    }

    /**
     * 设置对道具设置相应的参数
     *
     * @param itemHandle
     */
    private void updateEffectItemParams(Effect effect, final int itemHandle) {
        if (effect == null || itemHandle == 0) {
            return;
        }
        mRotationMode = calculateRotationMode();
        Log.d(TAG, "updateEffectItemParams: mRotationMode=" + mRotationMode);
        if (mExternalInputType == EXTERNAL_INPUT_TYPE_IMAGE) {
            faceunity.fuItemSetParam(itemHandle, "isAndroid", 0.0);
        } else if (mExternalInputType == EXTERNAL_INPUT_TYPE_VIDEO) {
            faceunity.fuItemSetParam(itemHandle, "isAndroid", mIsSystemCameraRecord ? 1.0 : 0.0);
        } else {
            faceunity.fuItemSetParam(itemHandle, "isAndroid", 1.0);
        }
        int effectType = effect.getType();
        if (effectType == Effect.EFFECT_TYPE_STICKER || effectType == Effect.EFFECT_TYPE_EXPRESSION_RECOGNITION) {
            //rotationAngle 参数是用于旋转普通道具
            faceunity.fuItemSetParam(itemHandle, "rotationAngle", mRotationMode * 90);
        }
        int back = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0;
        if (effectType == Effect.EFFECT_TYPE_ANIMOJI || effectType == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            // 镜像顶点
            faceunity.fuItemSetParam(itemHandle, "is3DFlipH", back);
            // 镜像表情
            faceunity.fuItemSetParam(itemHandle, "isFlipExpr", back);
            //这两句代码用于识别人脸默认方向的修改，主要针对animoji道具的切换摄像头倒置问题
            faceunity.fuItemSetParam(itemHandle, "camera_change", 1.0);
        }

        if (effectType == Effect.EFFECT_TYPE_GESTURE_RECOGNITION) {
            //loc_y_flip与loc_x_flip 参数是用于对手势识别道具的镜像
            faceunity.fuItemSetParam(itemHandle, "is3DFlipH", back);
            faceunity.fuItemSetParam(itemHandle, "loc_y_flip", back);
            faceunity.fuItemSetParam(itemHandle, "loc_x_flip", back);
        }
        setEffectRotationMode(effect, itemHandle);
        if (effectType == Effect.EFFECT_TYPE_ANIMOJI) {
            // 镜像跟踪（位移和旋转）
            faceunity.fuItemSetParam(itemHandle, "isFlipTrack", back);
            // 镜像灯光
            faceunity.fuItemSetParam(itemHandle, "isFlipLight ", back);
            // 设置 Animoji 跟随人脸
            faceunity.fuItemSetParam(itemHandle, "{\"thing\":\"<global>\",\"param\":\"follow\"}", 1);
        }
        setMaxFaces(effect.getMaxFace());
    }

    private void setEffectRotationMode(Effect effect, int itemHandle) {
        int rotMode;
        if (effect.getType() == Effect.EFFECT_TYPE_GESTURE_RECOGNITION && effect.getBundleName().startsWith("ctrl")) {
            rotMode = calculateRotModeLagacy();
        } else {
            rotMode = mRotationMode;
        }
        faceunity.fuItemSetParam(itemHandle, "rotMode", rotMode);
        faceunity.fuItemSetParam(itemHandle, "rotationMode", rotMode);
        // for green_screen bundle
        faceunity.fuItemSetParam(itemHandle, "rotation_mode", rotMode);
    }

    /*----------------------------------Builder---------------------------------------*/

    /**
     * FURenderer Builder
     */
    public static class Builder {
        private boolean createEGLContext = false;
        private Effect defaultEffect;
        private int maxFaces = 4;
        private int maxHumans = 1;
        private Context context;
        private int inputTextureType = 0;
        private int inputImageFormat = 0;
        private int inputOrientation = 270;
        private int externalInputType = EXTERNAL_INPUT_TYPE_NONE;
        private boolean isNeedFaceBeauty = true;
        private boolean isNeedAnimoji3D = false;
        private boolean isNeedBeautyHair = false;
        private boolean isNeedPosterFace = false;
        private boolean isNeedBodySlim = false;
        private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        private OnBundleLoadCompleteListener onBundleLoadCompleteListener;
        private OnFUDebugListener onFUDebugListener;
        private OnTrackingStatusChangedListener onTrackingStatusChangedListener;
        private OnSystemErrorListener onSystemErrorListener;

        private boolean mIsLoadAiGesture;
        private boolean mIsLoadAiHumanProcessor;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        /**
         * 是否加载手势识别 AI 模型
         *
         * @param loadAiGesture
         * @return
         */
        public Builder setLoadAiGesture(boolean loadAiGesture) {
            mIsLoadAiGesture = loadAiGesture;
            return this;
        }

        /**
         * 是否加载身体分割 AI 模型
         *
         * @param loadAiHumanProcessor
         * @return
         */
        public Builder setLoadAiHumanProcessor(boolean loadAiHumanProcessor) {
            mIsLoadAiHumanProcessor = loadAiHumanProcessor;
            return this;
        }

        /**
         * 是否需要美颜效果
         *
         * @param needFaceBeauty
         * @return
         */
        public Builder setNeedFaceBeauty(boolean needFaceBeauty) {
            isNeedFaceBeauty = needFaceBeauty;
            return this;
        }

        /**
         * 是否需要美体效果
         *
         * @param needBodySlim
         * @return
         */
        public Builder setNeedBodySlim(boolean needBodySlim) {
            isNeedBodySlim = needBodySlim;
            return this;
        }

        /**
         * 是否需要自己创建EGLContext
         *
         * @param createEGLContext
         * @return
         */
        public Builder createEGLContext(boolean createEGLContext) {
            this.createEGLContext = createEGLContext;
            return this;
        }

        /**
         * 是否需要立即加载道具
         *
         * @param defaultEffect
         * @return
         */
        public Builder defaultEffect(Effect defaultEffect) {
            this.defaultEffect = defaultEffect;
            return this;
        }

        /**
         * 外部输入的类型，INPUT_TYPE_IMAGE or INPUT_TYPE_VIDEO
         *
         * @param externalInputType
         * @return
         */
        public Builder setExternalInputType(int externalInputType) {
            this.externalInputType = externalInputType;
            return this;
        }

        /**
         * 识别最大人脸数
         *
         * @param maxFaces
         * @return
         */
        public Builder maxFaces(int maxFaces) {
            this.maxFaces = maxFaces;
            return this;
        }

        /**
         * 识别最大人体数
         *
         * @param maxHumans
         * @return
         */
        public Builder maxHumans(int maxHumans) {
            this.maxHumans = maxHumans;
            return this;
        }

        /**
         * 传入纹理的类型（传入数据没有纹理则无需调用）
         * camera OES纹理：1
         * 普通2D纹理：0
         *
         * @param textureType
         * @return
         */
        public Builder inputTextureType(int textureType) {
            this.inputTextureType = textureType;
            return this;
        }

        /**
         * 输入的byte[]数据类型
         *
         * @param inputImageFormat
         * @return
         */
        public Builder inputImageFormat(int inputImageFormat) {
            this.inputImageFormat = inputImageFormat;
            return this;
        }

        /**
         * 输入的画面数据方向
         *
         * @param inputOrientation
         * @return
         */
        public Builder inputImageOrientation(int inputOrientation) {
            this.inputOrientation = inputOrientation;
            return this;
        }

        /**
         * 是否需要3D道具的抗锯齿功能
         *
         * @param needAnimoji3D
         * @return
         */
        public Builder setNeedAnimoji3D(boolean needAnimoji3D) {
            this.isNeedAnimoji3D = needAnimoji3D;
            return this;
        }

        /**
         * 是否需要美发功能
         *
         * @param needBeautyHair
         * @return
         */
        public Builder setNeedBeautyHair(boolean needBeautyHair) {
            isNeedBeautyHair = needBeautyHair;
            return this;
        }

        /**
         * 是否需要海报换脸
         *
         * @param needPosterFace
         * @return
         */
        public Builder setNeedPosterFace(boolean needPosterFace) {
            isNeedPosterFace = needPosterFace;
            return this;
        }

        /**
         * 相机方向，前置或后置
         *
         * @param cameraFacing
         * @return
         */
        public Builder setCameraFacing(int cameraFacing) {
            this.cameraFacing = cameraFacing;
            return this;
        }

        /**
         * 设置debug数据回调
         *
         * @param onFUDebugListener
         * @return
         */
        public Builder setOnFUDebugListener(OnFUDebugListener onFUDebugListener) {
            this.onFUDebugListener = onFUDebugListener;
            return this;
        }

        /**
         * 设置是否检查到人脸的回调
         *
         * @param onTrackingStatusChangedListener
         * @return
         */
        public Builder setOnTrackingStatusChangedListener(OnTrackingStatusChangedListener onTrackingStatusChangedListener) {
            this.onTrackingStatusChangedListener = onTrackingStatusChangedListener;
            return this;
        }

        /**
         * 设置bundle加载完成回调
         *
         * @param onBundleLoadCompleteListener
         * @return
         */
        public Builder setOnBundleLoadCompleteListener(OnBundleLoadCompleteListener onBundleLoadCompleteListener) {
            this.onBundleLoadCompleteListener = onBundleLoadCompleteListener;
            return this;
        }


        /**
         * 设置SDK使用错误回调
         *
         * @param onSystemErrorListener
         * @return
         */
        public Builder setOnSystemErrorListener(OnSystemErrorListener onSystemErrorListener) {
            this.onSystemErrorListener = onSystemErrorListener;
            return this;
        }

        public FURenderer build() {
            FURenderer fuRenderer = new FURenderer(context, createEGLContext);
            fuRenderer.mMaxFaces = maxFaces;
            fuRenderer.mMaxHumans = maxHumans;
            fuRenderer.mInputTextureType = inputTextureType;
            fuRenderer.mInputImageFormat = inputImageFormat;
            fuRenderer.mInputOrientation = inputOrientation;
            fuRenderer.mExternalInputType = externalInputType;
            fuRenderer.mDefaultEffect = defaultEffect;
            fuRenderer.isNeedFaceBeauty = isNeedFaceBeauty;
            fuRenderer.isNeedBodySlim = isNeedBodySlim;
            fuRenderer.isNeedAnimoji3D = isNeedAnimoji3D;
            fuRenderer.isNeedBeautyHair = isNeedBeautyHair;
            fuRenderer.isNeedPosterFace = isNeedPosterFace;
            fuRenderer.mCameraFacing = cameraFacing;
            fuRenderer.mOnFUDebugListener = onFUDebugListener;
            fuRenderer.mOnTrackingStatusChangedListener = onTrackingStatusChangedListener;
            fuRenderer.mOnSystemErrorListener = onSystemErrorListener;
            fuRenderer.mOnBundleLoadCompleteListener = onBundleLoadCompleteListener;
            fuRenderer.mIsLoadAiGesture = mIsLoadAiGesture;
            fuRenderer.mIsLoadAiHumanProcessor = mIsLoadAiHumanProcessor;
            return fuRenderer;
        }
    }

//--------------------------------------Builder----------------------------------------

    class FUItemHandler extends Handler {

        FUItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //加载普通道具 bundle
                case ITEM_ARRAYS_EFFECT_INDEX: {
                    final Effect effect = (Effect) msg.obj;
                    if (effect == null) {
                        return;
                    }
                    boolean isNone = effect.getType() == Effect.EFFECT_TYPE_NONE;
                    final int itemEffect = isNone ? 0 : loadItem(mContext, effect.getBundlePath());
                    if (!isNone && itemEffect <= 0) {
                        Log.w(TAG, "create effect item failed: " + itemEffect);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                                mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = itemEffect;

                            if (effect.getType() == Effect.EFFECT_TYPE_BG_SEG_GREEN) {
                                mBgSegGreenItem = itemEffect;
                                setKeyColor(mKeyColor);
                                setChromaThres(mChromaThres);
                                setChromaThresT(mChromaThresT);
                                setAlphaL(mAlphaL);
                                setTransform(mStartX, mStartY, mEndX, mEndY);
                                if (mVideoDecoder == null) {
                                    mVideoDecoder = new VideoDecoder();
                                    boolean external = mExternalInputType == EXTERNAL_INPUT_TYPE_IMAGE ||
                                            mExternalInputType == EXTERNAL_INPUT_TYPE_VIDEO;
                                    mVideoDecoder.create(EGL14.eglGetCurrentContext(), !external && mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);
                                    mVideoDecoder.setOnReadPixelListener(mOnReadPixelListener);
                                }
                                setTexBgSource(mSourcePath);
                                setRunBgSegGreen(mRunBgSegGreen);
                            }
                            updateEffectItemParams(effect, itemEffect);
                        }
                    });
                }
                break;
                // 加载美颜 bundle
                case ITEM_ARRAYS_FACE_BEAUTY_INDEX: {
                    final int itemBeauty = loadItem(mContext, BUNDLE_FACE_BEAUTIFICATION);
                    if (itemBeauty <= 0) {
                        Log.w(TAG, "create face beauty item failed: " + itemBeauty);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX]);
                                mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = itemBeauty;
                            mIsNeedUpdateFaceBeauty = true;
                        }
                    });
                }
                break;
                // 加载轻美妆 bundle
                case ITEM_ARRAYS_LIGHT_MAKEUP_INDEX: {
                    if (!(msg.obj instanceof LightMakeupItem)) {
                        return;
                    }
                    final LightMakeupItem makeupItem = (LightMakeupItem) msg.obj;
                    String path = makeupItem.getPath();
                    if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] <= 0) {
                        int itemLightMakeup = loadItem(mContext, BUNDLE_LIGHT_MAKEUP);
                        if (itemLightMakeup <= 0) {
                            Log.w(TAG, "create light makeup item failed: " + itemLightMakeup);
                            return;
                        }
                        mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] = itemLightMakeup;
                    }
                    if (!TextUtils.isEmpty(path)) {
                        MakeupParamHelper.TextureImage textureImage = null;
                        double[] lipStickColor = null;
                        if (makeupItem.getType() == LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK) {
                            lipStickColor = MakeupParamHelper.readRgbaColor(mContext, path);
                        } else {
                            textureImage = MakeupParamHelper.createTextureImage(mContext, path);
                        }
                        Log.d(TAG, "light makeup. textureImage:" + textureImage + ", lipStick:" + Arrays.toString(lipStickColor));
                        final MakeupParamHelper.TextureImage finalTextureImage = textureImage;
                        final double[] finalLipStickColor = lipStickColor;
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                int itemHandle = mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX];
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.IS_MAKEUP_ON, 1.0);
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY, 1.0);
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.REVERSE_ALPHA, 1.0);
                                faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.getMakeupIntensityKeyByType(makeupItem.getType()), makeupItem.getLevel());
                                if (finalLipStickColor != null) {
                                    if (makeupItem.getType() == LightMakeupItem.FACE_MAKEUP_TYPE_LIPSTICK) {
                                        faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_LIP_COLOR, finalLipStickColor);
                                        faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_LIP_MASK, 1.0);
                                    }
                                } else {
                                    faceunity.fuItemSetParam(itemHandle, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY_LIP, 0.0);
                                }
                                if (finalTextureImage != null) {
                                    String key = MakeupParamHelper.getMakeupTextureKeyByType(makeupItem.getType());
                                    faceunity.fuCreateTexForItem(itemHandle, key, finalTextureImage.getBytes(), finalTextureImage.getWidth(), finalTextureImage.getHeight());
                                }
                            }
                        });
                    } else {
                        // 卸某个妆
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] > 0) {
                                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX],
                                            MakeupParamHelper.getMakeupIntensityKeyByType(makeupItem.getType()), 0.0);
                                }
                            }
                        });
                    }
                }
                break;
                // 加载美妆 bundle
                case ITEM_ARRAYS_FACE_MAKEUP_INDEX: {
                    if (msg.obj == null) {
                        return;
                    }
                    int itemMakeup;
                    if (mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] <= 0) {
                        itemMakeup = loadItem(mContext, BUNDLE_FACE_MAKEUP);
                        if (itemMakeup <= 0) {
                            Log.w(TAG, "create face makeup item failed: " + itemMakeup);
                            return;
                        }
                        mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] = itemMakeup;
                    } else {
                        itemMakeup = mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX];
                    }
                    final int finalItemMakeup = itemMakeup;
                    MakeupEntity obj = msg.obj instanceof MakeupEntity ? (MakeupEntity) msg.obj : null;
                    if (obj == null) {
                        return;
                    }
                    final MakeupEntity makeupEntity = new MakeupEntity(obj);
                    makeupEntity.setItemHandle(loadItem(mContext, makeupEntity.getBundlePath()));
                    Set<Map.Entry<String, Object>> makeupParamEntries = mMakeupParams.entrySet();
                    final Map<String, Integer> makeupItemHandleMap = new HashMap<>(16);
                    for (Map.Entry<String, Object> entry : makeupParamEntries) {
                        Object value = entry.getValue();
                        if (value instanceof String && ((String) value).endsWith(".bundle")) {
                            int handle = loadItem(mContext, (String) value);
                            if (handle > 0) {
                                makeupItemHandleMap.put(entry.getKey(), handle);
                            }
                        }
                    }

                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            // cleanup
                            int size = mMakeupItemHandleMap.size();
                            if (size > 0) {
                                int[] oldItemHandles = new int[size];
                                Iterator<Integer> iterator = mMakeupItemHandleMap.values().iterator();
                                for (int i = 0; iterator.hasNext(); ) {
                                    oldItemHandles[i++] = iterator.next();
                                }
                                faceunity.fuUnBindItems(finalItemMakeup, oldItemHandles);
                                for (int oldItemHandle : oldItemHandles) {
                                    faceunity.fuDestroyItem(oldItemHandle);
                                }
                                Log.d(TAG, "makeup: unbind and destroy old child item: " + Arrays.toString(oldItemHandles));
                                mMakeupItemHandleMap.clear();
                            }
                            if (mMakeupEntity != null) {
                                int itemHandle = mMakeupEntity.getItemHandle();
                                if (itemHandle > 0) {
                                    faceunity.fuUnBindItems(finalItemMakeup, new int[]{itemHandle});
                                    faceunity.fuDestroyItem(itemHandle);
                                    Log.d(TAG, "makeup: unbind and destroy old parent item: " + itemHandle);
                                    mMakeupEntity.setItemHandle(0);
                                }
                            }

                            // bind item
                            if (makeupEntity.getItemHandle() > 0) {
                                faceunity.fuBindItems(finalItemMakeup, new int[]{makeupEntity.getItemHandle()});
                                Log.d(TAG, "makeup: bind new parent item: " + makeupEntity.getItemHandle());
                            }
                            size = makeupItemHandleMap.size();
                            if (size > 0) {
                                int[] itemHandles = new int[size];
                                Iterator<Integer> iterator = makeupItemHandleMap.values().iterator();
                                for (int i = 0; iterator.hasNext(); ) {
                                    itemHandles[i++] = iterator.next();
                                }
                                faceunity.fuBindItems(finalItemMakeup, itemHandles);
                                Log.d(TAG, "makeup: bind new child item: " + Arrays.toString(itemHandles));
                                mMakeupItemHandleMap.putAll(makeupItemHandleMap);
                            }
                            // set param
                            Set<Map.Entry<String, Object>> makeupParamEntries = mMakeupParams.entrySet();
                            for (Map.Entry<String, Object> entry : makeupParamEntries) {
                                Object value = entry.getValue();
                                String key = entry.getKey();
                                if (value instanceof double[]) {
                                    double[] val = (double[]) value;
                                    faceunity.fuItemSetParam(finalItemMakeup, key, val);
                                    Log.d(TAG, "makeup: set param key: " + key + ", value: " + Arrays.toString(val));
                                } else if (value instanceof Double) {
                                    Double val = (Double) value;
                                    faceunity.fuItemSetParam(finalItemMakeup, key, val);
                                    Log.d(TAG, "makeup: set param key: " + key + ", value: " + val);
                                }
                            }

                            if (mExternalInputType == EXTERNAL_INPUT_TYPE_IMAGE || mExternalInputType == EXTERNAL_INPUT_TYPE_VIDEO) {
                                mIsMakeupFlipPoints = !mIsMakeupFlipPoints;
                            }
                            Log.d(TAG, "makeup: flip points: " + mIsMakeupFlipPoints);
                            faceunity.fuItemSetParam(finalItemMakeup, MakeupParamHelper.MakeupParam.IS_FLIP_POINTS, mIsMakeupFlipPoints ? 1.0 : 0.0);
                            faceunity.fuItemSetParam(finalItemMakeup, MakeupParamHelper.MakeupParam.MAKEUP_LIP_MASK, 1.0);
                            faceunity.fuItemSetParam(finalItemMakeup, MakeupParamHelper.MakeupParam.MAKEUP_INTENSITY, 1.0);
                            faceunity.fuItemSetParam(finalItemMakeup, MakeupParamHelper.MakeupParam.IS_CLEAR_MAKEUP, 1.0);
                            Log.i(TAG, "bind makeup:" + makeupEntity + ", unbind makeup:" + mMakeupEntity);
                            mMakeupEntity = makeupEntity;
                        }
                    });
                }
                break;
                //加载美发 bundle
                case ITEM_ARRAYS_BEAUTY_HAIR_INDEX: {
                    int itemHandle = 0;
                    if (mHairColorType == HAIR_NORMAL) {
                        itemHandle = loadItem(mContext, BUNDLE_HAIR_NORMAL);
                    } else if (mHairColorType == HAIR_GRADIENT) {
                        itemHandle = loadItem(mContext, BUNDLE_HAIR_GRADIENT);
                    }
                    final int itemHair = itemHandle;
                    if (itemHair <= 0) {
                        Log.w(TAG, "create hair item failed: " + itemHair);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX]);
                                mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX] = 0;
                            }
                            faceunity.fuItemSetParam(itemHair, HairParam.INDEX, mHairColorIndex);
                            faceunity.fuItemSetParam(itemHair, HairParam.STRENGTH, mHairColorStrength);
                            mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX] = itemHair;
                        }
                    });
                }
                break;
                // 加载 Animoji 风格滤镜 bundle
                case ITEM_ARRAYS_CARTOON_FILTER_INDEX: {
                    final int itemCartoonFilter = loadItem(mContext, BUNDLE_CARTOON_FILTER);
                    if (itemCartoonFilter <= 0) {
                        Log.w(TAG, "create cartoon filter item failed: " + itemCartoonFilter);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            faceunity.fuItemSetParam(itemCartoonFilter, CartoonFilterParam.STYLE, mCartoonFilterStyle);
                            int glMajorVersion = GlUtil.getGlMajorVersion();
                            Log.i(TAG, "cartoon filter. glMajorVersion: " + glMajorVersion);
                            faceunity.fuItemSetParam(itemCartoonFilter, CartoonFilterParam.GLVER, glMajorVersion);
                            mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] = itemCartoonFilter;
                        }
                    });
                }
                break;
                // 加载美体 bundle
                case ITEM_ARRAYS_BEAUTIFY_BODY: {
                    final int itemBeautifyBody = loadItem(mContext, BUNDLE_BEAUTIFY_BODY);
                    if (itemBeautifyBody <= 0) {
                        Log.w(TAG, "create beautify body item failed: " + itemBeautifyBody);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY]);
                                mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] = 0;
                            }
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.BODY_SLIM_STRENGTH, mBodySlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.LEG_SLIM_STRENGTH, mLegSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.WAIST_SLIM_STRENGTH, mWaistSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.SHOULDER_SLIM_STRENGTH, mShoulderSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.HIP_SLIM_STRENGTH, mHipSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.HEAD_SLIM, mHeadSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.LEG_SLIM, mLegThinSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BodySlimParam.DEBUG, 0.0);
                            mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] = itemBeautifyBody;
                            setBeautyBodyOrientation();
                        }
                    });
                }
                break;
                // 加载 Animoji 道具3D抗锯齿 bundle
                case ITEM_ARRAYS_ABIMOJI_3D_INDEX: {
                    final int itemAnimoji3D = loadItem(mContext, BUNDLE_FXAA);
                    if (itemAnimoji3D <= 0) {
                        Log.w(TAG, "create Animoji3D item failed: " + itemAnimoji3D);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX]);
                                mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_ABIMOJI_3D_INDEX] = itemAnimoji3D;
                        }
                    });
                }
                break;
                default:
            }
            if (mOnBundleLoadCompleteListener != null) {
                mOnBundleLoadCompleteListener.onBundleLoadComplete(msg.what);
            }
        }
    }
}
