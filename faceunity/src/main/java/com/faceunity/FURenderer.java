package com.faceunity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.entity.CartoonFilter;
import com.faceunity.entity.Effect;
import com.faceunity.entity.Filter;
import com.faceunity.entity.LightMakeupCombination;
import com.faceunity.entity.LightMakeupItem;
import com.faceunity.entity.LivePhoto;
import com.faceunity.entity.MakeupEntity;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.param.BeautificationParam;
import com.faceunity.param.BeautifyBodyParam;
import com.faceunity.param.MakeupParamHelper;
import com.faceunity.wrapper.faceunity;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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
 */
public class FURenderer implements OnFUControlListener {
    private static final String TAG = FURenderer.class.getSimpleName();
    public static final int FU_ADM_FLAG_EXTERNAL_OES_TEXTURE = faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE;
    public static final int FACE_LANDMARKS_75 = faceunity.FUAITYPE_FACELANDMARKS75;
    public static final int FACE_LANDMARKS_239 = faceunity.FUAITYPE_FACELANDMARKS239;

    public static final int EXTERNAL_INPUT_TYPE_NONE = -1;
    public static final int EXTERNAL_INPUT_TYPE_IMAGE = 0;
    public static final int EXTERNAL_INPUT_TYPE_VIDEO = 1;

    private Context mContext;

    // fxaa.bundle：3D绘制抗锯齿数据文件，加载后3D绘制效果更加平滑。
    private static final String BUNDLE_FXAA = "fxaa.bundle";
    // 美颜 bundle
    private static final String BUNDLE_FACE_BEAUTIFICATION = "face_beautification.bundle";
    // AI 模型文件夹
    private static final String AI_MODEL_ASSETS_DIR = "AI_model/";
    // AI 人脸识别模型
    private static final String BUNDLE_AI_MODEL_FACE_PROCESSOR = AI_MODEL_ASSETS_DIR + "ai_face_processor.bundle";
    // 美发正常色 bundle
    private static final String BUNDLE_HAIR_NORMAL = "hair_normal.bundle";
    // 美发渐变色 bundle
    private static final String BUNDLE_HAIR_GRADIENT = "hair_gradient.bundle";
    // Animoji 舌头 bundle
    private static final String BUNDLE_TONGUE = AI_MODEL_ASSETS_DIR + "tongue.bundle";
    // 海报换脸 bundle
    private static final String BUNDLE_CHANGE_FACE = "change_face/change_face.bundle";
    // 动漫滤镜 bundle
    private static final String BUNDLE_CARTOON_FILTER = "cartoon_filter/fuzzytoonfilter.bundle";
    // 轻美妆 bundle
    private static final String BUNDLE_LIGHT_MAKEUP = "light_makeup/light_makeup.bundle";
    // 美妆 bundle
    private static final String BUNDLE_FACE_MAKEUP = "face_makeup.bundle";
    // 表情动图 bundle
    private static final String BUNDLE_LIVE_PHOTO = "live_photo/photolive.bundle";
    // Avatar 捏脸背景 bundle
    private static final String BUNDLE_AVATAR_BACKGROUND = "avatar/avatar_background.bundle";
    // 美体 bundle
    private static final String BUNDLE_BEAUTIFY_BODY = "body_slim.bundle";

    public static final String LANDMARKS = "landmarks";

    private static float sIsBeautyOn = 1.0F;
    private static String sFilterName = Filter.Key.ZIRAN_2;// 滤镜：自然 2
    private static float mFilterLevel = 0.4f;//滤镜强度
    private static float mBlurLevel = 0.7f;//磨皮程度
    private static float mBlurType = 2.0f;//磨皮类型：精细磨皮
    private static float mColorLevel = 0.3f;//美白
    private static float mRedLevel = 0.3f;//红润
    private static float mEyeBright = 0.0f;//亮眼
    private static float mToothWhiten = 0.0f;//美牙
    private static float mFaceShape = 4;//脸型：精细变形
    private static float mFaceShapeLevel = 1.0f;//变形程度
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
    private static final int ITEM_ARRAYS_LIVE_PHOTO_INDEX = 7;
    private static final int ITEM_ARRAYS_FACE_MAKEUP_INDEX = 8;
    public static final int ITEM_ARRAYS_AVATAR_BACKGROUND = 9;
    public static final int ITEM_ARRAYS_AVATAR_HAIR = 10;
    private static final int ITEM_ARRAYS_BEAUTIFY_BODY = 11;
    // 句柄数量
    private static final int ITEM_ARRAYS_COUNT = 12;

    // 海报换脸 track face 50 次，确保检测成功率
    private static final int MAX_TRACK_COUNT = 50;

    // 美发类型
    public static final int HAIR_NORMAL = 0;
    public static final int HAIR_GRADIENT = 1;

    //美颜和其他道具的handle数组
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    //用于和异步加载道具的线程交互
    private Handler mFuItemHandler;

    private boolean isNeedFaceBeauty = true;
    private boolean isNeedBeautyHair = false;
    private boolean isNeedAnimoji3D = false;
    private boolean isNeedPosterFace = false;
    private Effect mDefaultEffect;//默认道具
    private boolean mIsCreateEGLContext; //是否需要手动创建EGLContext
    private int mInputTextureType = 0; //输入的图像texture类型，Camera提供的默认为EXTERNAL OES
    private int mInputImageFormat = 0;
    private volatile boolean isNeedUpdateFaceBeauty = true;
    // 是否启用美体
    private boolean mUseBeautifyBody;
    private float mBodySlimStrength = 0.0f; // 瘦身
    private float mLegSlimStrength = 0.0f; // 长腿
    private float mWaistSlimStrength = 0.0f; // 细腰
    private float mShoulderSlimStrength = 0.5f; // 美肩
    private float mHipSlimStrength = 0.0f; // 美胯

    private int mInputOrientation = 270;
    private int mExternalInputType = EXTERNAL_INPUT_TYPE_NONE;
    private boolean mIsSystemCameraRecord;
    private int mCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mMaxFaces = 4; //同时识别的最大人脸
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

    //    private float[] landmarksData = new float[150];
    //    private float[] expressionData = new float[46];
    private float[] rotationData = new float[4];
    //    private float[] pupilPosData = new float[2];
    private float[] rotationModeData = new float[1];
    private float[] faceRectData = new float[4];
    private double[] posterTemplateLandmark;
    private double[] posterPhotoLandmark;

    private List<Runnable> mEventQueue;
    private long mGlThreadId;
    private OnBundleLoadCompleteListener mOnBundleLoadCompleteListener;
    private int mCartoonFilterStyle = CartoonFilter.NO_FILTER;
    private static boolean sIsInited;
    private static int sFaceBeautyItemHandle;
    /* 设备方向 */
    private volatile int mDeviceOrientation = 90;
    /* 人脸识别方向 */
    private volatile int mRotationMode = faceunity.FU_ROTATION_MODE_90;
    private boolean mIsNeedBackground;

    private boolean mIsLoadAiBgSeg;
    private boolean mIsLoadAiGesture;
    private boolean mIsLoadAiHairSeg;
    private boolean mIsLoadAiHumanPose;

    /**
     * 初始化系统环境，加载底层数据，并进行网络鉴权。
     * 应用使用期间只需要初始化一次，无需释放数据。
     * 必须在SDK其他接口前调用，否则会引起应用崩溃。
     */
    public static void initFURenderer(Context context) {
        if (sIsInited) {
            return;
        }
        // 获取 Nama SDK 版本信息
        Log.e(TAG, "fu sdk version " + faceunity.fuGetVersion());
        int isSetup = faceunity.fuSetup(new byte[0], authpack.A());
        Log.d(TAG, "fuSetup. isSetup: " + (isSetup == 0 ? "no" : "yes"));
        // 提前加载算法数据模型，用于人脸检测
        loadAiModel(context, BUNDLE_AI_MODEL_FACE_PROCESSOR, faceunity.FUAITYPE_FACEPROCESSOR);
        // 提前加载舌头跟踪模型
        loadTongueModel(context, BUNDLE_TONGUE);
        sIsInited = isLibInit();
        Log.i(TAG, "initFURenderer finish. isLibraryInit: " + (sIsInited ? "yes" : "no"));
    }

    /**
     * 释放 SDK 占用的内存。如需再次使用，需要调用 fuSetup
     */
    public static void destroyLibData() {
        if (sIsInited) {
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
        releaseAiModel(faceunity.FUAITYPE_BACKGROUNDSEGMENTATION_GREEN);
        releaseAiModel(faceunity.FUAITYPE_HANDGESTURE);
        releaseAiModel(faceunity.FUAITYPE_HAIRSEGMENTATION);
        releaseAiModel(faceunity.FUAITYPE_HUMANPOSE2D);
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
     * 获取 Nama SDK 完整版本号，例如 6.7.0_tf_phy-f1e36a93-b9e3359-b5f220d
     *
     * @return full version
     */
    public static String getFullVersion() {
        return faceunity.fuGetVersion();
    }

    /**
     * 获取 Nama SDK 主版本号，例如 6.7.0
     *
     * @return major version
     */
    public static String getMajorVersion() {
        String version = faceunity.fuGetVersion();
        String majorVersion = version.substring(0, version.indexOf('_'));
        return majorVersion;
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
        onSurfaceDestroyed();
        mEventQueue = Collections.synchronizedList(new ArrayList<Runnable>(16));
        mGlThreadId = Thread.currentThread().getId();
        HandlerThread handlerThread = new HandlerThread("FUItemWorker");
        handlerThread.start();
        mFuItemHandler = new FUItemHandler(handlerThread.getLooper());

        /**
         * fuCreateEGLContext 创建OpenGL环境
         * 适用于没OpenGL环境时调用
         * 如果调用了fuCreateEGLContext，在销毁时需要调用fuReleaseEGLContext
         */
        if (mIsCreateEGLContext) {
            faceunity.fuCreateEGLContext();
        }

        mFrameId = 0;
        faceunity.fuSetMaxFaces(mMaxFaces);//设置多脸，目前最多支持8人。
        if (mIsNeedBackground) {
            mRotationMode = faceunity.FU_ROTATION_MODE_90;
        } else {
            mRotationMode = calculateRotationMode();
        }
        Log.d(TAG, "onSurfaceCreated: rotationMode:" + mRotationMode);
        faceunity.fuSetDefaultRotationMode(mRotationMode);

        if (mIsLoadAiHumanPose) {
            mFuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadAiModel(mContext, AI_MODEL_ASSETS_DIR + "ai_humanpose.bundle", faceunity.FUAITYPE_HUMANPOSE2D);
                }
            });
        }
        if (mIsLoadAiBgSeg) {
            mFuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadAiModel(mContext, AI_MODEL_ASSETS_DIR + "ai_bgseg.bundle", faceunity.FUAITYPE_BACKGROUNDSEGMENTATION);
                }
            });
        }
        if (mIsLoadAiHairSeg) {
            mFuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadAiModel(mContext, AI_MODEL_ASSETS_DIR + "ai_hairseg.bundle", faceunity.FUAITYPE_HAIRSEGMENTATION);
                }
            });
        }
        if (mIsLoadAiGesture) {
            mFuItemHandler.post(new Runnable() {
                @Override
                public void run() {
                    loadAiModel(mContext, AI_MODEL_ASSETS_DIR + "ai_gesture.bundle", faceunity.FUAITYPE_HANDGESTURE);
                }
            });
        }
        if (isNeedFaceBeauty) {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_FACE_BEAUTY_INDEX);
        }
        if (isNeedBeautyHair) {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_BEAUTY_HAIR_INDEX);
        }
        if (isNeedAnimoji3D) {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_ABIMOJI_3D_INDEX);
        }
        if (isNeedPosterFace) {
            posterPhotoLandmark = new double[150];
            posterTemplateLandmark = new double[150];
            mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX] = loadItem(mContext, BUNDLE_CHANGE_FACE);
        }

        // 设置动漫滤镜
        int cartoonFilterStyle = mCartoonFilterStyle;
        mCartoonFilterStyle = CartoonFilter.NO_FILTER;
        setCartoonFilter(cartoonFilterStyle);

        if (mIsNeedBackground) {
            loadAvatarBackground();
        }

        // 异步加载默认道具，放在加载 animoji 3D 和动漫滤镜之后
        if (mDefaultEffect != null) {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_EFFECT_INDEX, mDefaultEffect));
        }

        // 恢复美妆的参数值
        if (mMakeupEntity != null) {
            Message.obtain(mFuItemHandler, ITEM_ARRAYS_FACE_MAKEUP_INDEX, mMakeupEntity).sendToTarget();
        }

        // 恢复轻美妆的参数值
        if (mLightMakeupItemMap.size() > 0) {
            Collection<LightMakeupItem> makeupItems = mLightMakeupItemMap.values();
            onLightMakeupCombinationSelected(new ArrayList<>(makeupItems));
        }

        // 加载美体道具，并设置美体参数
        if (mUseBeautifyBody) {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_BEAUTIFY_BODY));
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

    public float[] getRotationData() {
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        return rotationData;
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
        mGlThreadId = 0;

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
        int livePhotoPhotoIndex = mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX];
        if (livePhotoPhotoIndex > 0) {
            faceunity.fuDeleteTexForItem(livePhotoPhotoIndex, "tex_input");
        }

        mFrameId = 0;
        isNeedUpdateFaceBeauty = true;
        releaseAllAiModel();
        // do not release face beauty bundle item
        for (int i = 0; i < mItemsArray.length; i++) {
            if (mItemsArray[i] > 0) {
                faceunity.fuDestroyItem(mItemsArray[i]);
            }
        }
        Arrays.fill(mItemsArray, 0);
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        faceunity.fuDone();
        if (mIsCreateEGLContext) {
            faceunity.fuReleaseEGLContext();
        }
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
        int currRotMode = faceunity.fuGetCurrentRotationMode();
        faceunity.fuSetDefaultRotationMode(rotMode);
        for (int i = 0; i < MAX_TRACK_COUNT; i++) {
            faceunity.fuTrackFace(img, 0, w, h);
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
     * 使用 fuTrackFace + fuAvatarToTexture 的方法组合绘制画面，该组合没有camera画面绘制，适用于animoji等相关道具的绘制。
     * fuTrackFace 获取识别到的人脸信息
     * fuAvatarToTexture 依据人脸信息绘制道具
     *
     * @param w
     * @param h
     * @param translation
     * @return
     */
    public int onDrawFrameAvatar(int w, int h, float[] translation) {
        if (w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrameAvatar data null");
            return 0;
        }
        prepareDrawFrame();

        if (mNeedBenchmark) {
            mFuCallStartTime = System.nanoTime();
        }

        rotationModeData[0] = mRotationMode;
        int tex = faceunity.fuAvatarToTexture(AvatarConstant.PUP_POS_DATA, AvatarConstant.EXPRESSIONS,
                AvatarConstant.ROTATION_DATA, rotationModeData, translation, 0, w, h, mFrameId++, mItemsArray,
                AvatarConstant.VALID_DATA);
        if (mNeedBenchmark) {
            mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        }
        return tex;
    }

    /**
     * 进入捏脸状态
     */
    public void enterFaceShape() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "enter_facepup", 1.0);
                }
            }
        });
    }

    /**
     * 清除全部捏脸参数
     */
    public void clearFaceShape() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "clear_facepup", 1.0);
                }
                if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR], "clear_facepup", 1.0);
                }
            }
        });
    }

    /**
     * 保存和退出，二选一即可
     * 直接退出捏脸状态，不保存当前捏脸状态，进入跟踪状态。使用上一次捏脸，进行人脸表情跟踪。
     */
    public void quitFaceup() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "quit_facepup", 1.0);
                }
            }
        });
    }

    /**
     * 触发保存捏脸，并退出捏脸状态，进入跟踪状态。耗时操作，必要时设置。
     */
    public void recomputeFaceup() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "need_recompute_facepup", 1.0);
                }
            }
        });
    }

    /**
     * 设置捏脸属性的权值，范围[0-1]。这里param对应的就是第几个捏脸属性，从1开始。
     *
     * @param key
     * @param value
     */
    public void fuItemSetParamFaceup(final String key, final double value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "{\"name\":\"facepup\",\"param\":\"" + key + "\"}", value);
                }
            }
        });
    }

    /**
     * 设置 avatar 颜色参数
     *
     * @param key
     * @param value [r,g,b] 或 [r,g,b,intensity]
     */
    public void fuItemSetParamFaceColor(final String key, final double[] value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (value.length > 3) {
                    if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR], key, value);
                    }
                } else {
                    if (mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], key, value);
                    }
                }
            }
        });
    }

    /**
     * whether avatar bundle is loaded
     *
     * @return
     */
    public boolean isAvatarLoaded() {
        return mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] > 0;
    }

    /**
     * whether avatar hair and background bundle is loaded
     *
     * @return
     */
    public boolean isAvatarMakeupItemLoaded() {
        return mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0 && mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0;
    }

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
        if (mMaxFaces != maxFaces && maxFaces > 0) {
            mMaxFaces = maxFaces;
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    faceunity.fuSetMaxFaces(mMaxFaces);
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
     * 表情动图，切换相机时设置方向
     *
     * @param isFront 是否为前置相机
     */
    public void setIsFrontCamera(final boolean isFront) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] > 0) {
                    if (isFront && mInputOrientation == 90) {
                        // 解决 Nexus 手机前置相机发生X镜像的问题
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_swap_x", 1.0);
                    } else {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_swap_x", 0.0);
                    }
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_front", isFront ? 1.0 : 0.0);
                }
            }
        });
    }

    /**
     * 每帧处理画面时被调用
     */
    private void prepareDrawFrame() {
        //计算FPS等数据
        benchmarkFPS();

        if (mUseBeautifyBody) {
            //  是否检测到人体
            int hasHuman = (int) faceunity.fuItemGetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], "HasHuman");
            if (mOnTrackingStatusChangedListener != null && mTrackingStatus != hasHuman) {
                mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = hasHuman);
            }
        } else {
            //获取人脸是否识别，并调用回调接口
            int isTracking = faceunity.fuIsTracking();
            if (mOnTrackingStatusChangedListener != null && mTrackingStatus != isTracking) {
                mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = isTracking);
            }
        }

        //获取faceunity错误信息，并调用回调接口
        int error = faceunity.fuGetSystemError();
        if (error != 0) {
            Log.e(TAG, "fuGetSystemErrorString " + faceunity.fuGetSystemErrorString(error));
            if (mOnSystemErrorListener != null) {
                mOnSystemErrorListener.onSystemError(faceunity.fuGetSystemErrorString(error));
            }
        }

        //修改美颜参数
        if (isNeedUpdateFaceBeauty && mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] > 0) {
            int itemFaceBeauty = mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX];

//            Log.d(TAG, "prepareDrawFrame: face beauty params: isBeautyOn:" + sIsBeautyOn + ", filterName:"
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

            isNeedUpdateFaceBeauty = false;
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
                faceunity.fuOnCameraChange();
            }
        });
    }

    /**
     * camera切换时需要调用
     *
     * @param cameraFacing     前后置摄像头ID
     * @param inputOrientation
     */
    public void onCameraChange(final int cameraFacing, final int inputOrientation) {
        Log.d(TAG, "onCameraChange. cameraFacing: " + cameraFacing + ", inputOrientation:" + inputOrientation);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
                mCameraFacing = cameraFacing;
                mInputOrientation = inputOrientation;
                faceunity.fuOnCameraChange();
                mRotationMode = calculateRotationMode();
                faceunity.fuSetDefaultRotationMode(mRotationMode);
                setBeautyBodyOrientation();
                updateEffectItemParams(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
            }
        });
    }

    public void setRotationMode(int rotationMode) {
        if (mRotationMode != rotationMode) {
            mRotationMode = rotationMode;
            Log.d(TAG, "setRotationMode: " + rotationMode);
            faceunity.fuSetDefaultRotationMode(rotationMode);
        }
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
                    // 背景分割 Animoji 表情识别 人像驱动 手势识别，转动手机时，重置人脸识别
                    if (mDefaultEffect != null && (mDefaultEffect.effectType() == Effect.EFFECT_TYPE_BACKGROUND
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_ANIMOJI
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_EXPRESSION
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_GESTURE
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_PORTRAIT_DRIVE
                            || mDefaultEffect.effectType() == Effect.EFFECT_TYPE_AVATAR)) {
                        faceunity.fuOnCameraChange();
                    }
                    mRotationMode = calculateRotationMode();
                    if (!mIsNeedBackground) {
                        // Avatar 捏脸时不设置 rotationMode
                        faceunity.fuSetDefaultRotationMode(mRotationMode);
                        Log.d(TAG, "setTrackOrientation. deviceOrientation: " + mDeviceOrientation + ", rotationMode: " + mRotationMode);
                    }
                    if (mDefaultEffect != null) {
                        setEffectRotationMode(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT_INDEX]);
                    }
                    setBeautyBodyOrientation();
                }
            });
        }
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
                    if (mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_MAKEUP_INDEX],
                                MakeupParamHelper.MakeupParam.IS_FLIP_POINTS, isFlipPoints ? 1.0 : 0.0);
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
     * 相机方向和 RotationMode 参数对照：
     * - 前置 270：home 下 1，home 右 0，home 上 3，home 左 2
     * - 后置 90： home 下 3，home 右 0，home 上 1，home 左 2
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
                    if (mDeviceOrientation == 90) {
                        rotMode = faceunity.FU_ROTATION_MODE_270;
                    } else if (mDeviceOrientation == 270) {
                        rotMode = faceunity.FU_ROTATION_MODE_90;
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

    public void changeInputType() {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mFrameId = 0;
            }
        });
    }

    public void setVideoParams(int inputOrientation, boolean isSystemCameraRecord) {
        mInputOrientation = inputOrientation;
        mIsSystemCameraRecord = isSystemCameraRecord;
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
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT_INDEX], "music_time", time);
            }
        });
    }

    @Override
    public void onEffectSelected(Effect effect) {
        if (effect == null) {
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
                if (mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], BeautificationParam.IS_BEAUTY_ON, sIsBeautyOn);
                }
            }
        });
    }

    @Override
    public void onFilterLevelSelected(float progress) {
        isNeedUpdateFaceBeauty = true;
        mFilterLevel = progress;
    }

    @Override
    public void onFilterNameSelected(String filterName) {
        isNeedUpdateFaceBeauty = true;
        sFilterName = filterName;
    }

    @Override
    public void onHairSelected(int type, int hairColorIndex, float hairColorLevel) {
        mHairColorIndex = hairColorIndex;
        mHairColorStrength = hairColorLevel;
        final int lastHairType = mHairColorType;
        mHairColorType = type;
        if (mHairColorType == lastHairType) {
            onHairLevelSelected(mHairColorType, mHairColorIndex, mHairColorStrength);
        } else {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_BEAUTY_HAIR_INDEX);
                }
            });
        }
    }

    @Override
    public void onHairLevelSelected(@HairType final int type, final int hairColorIndex, final float hairColorLevel) {
        mHairColorIndex = hairColorIndex;
        mHairColorStrength = hairColorLevel;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX] > 0) {
                    // 美发类型
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX], "Index", mHairColorIndex);
                    // 美发强度
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTY_HAIR_INDEX], "Strength", mHairColorStrength);
                }
            }
        });
    }

    @Override
    public void onBlurTypeSelected(float blurType) {
        mBlurType = blurType;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onBlurLevelSelected(float level) {
        mBlurLevel = level;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onColorLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mColorLevel = level;
    }

    @Override
    public void onRedLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mRedLevel = level;
    }

    @Override
    public void onEyeBrightSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeBright = level;
    }

    @Override
    public void onToothWhitenSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mToothWhiten = level;
    }

    @Override
    public void onEyeEnlargeSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeEnlarging = level;
    }

    @Override
    public void onCheekThinningSelected(float level) {
        mCheekThinning = level;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekNarrowSelected(float level) {
        // 窄脸参数上限为0.5
        mCheekNarrow = level / 2;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekSmallSelected(float level) {
        // 小脸参数上限为0.5
        mCheekSmall = level / 2;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onCheekVSelected(float level) {
        mCheekV = level;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void onIntensityChinSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityChin = level;
    }

    @Override
    public void onIntensityForeheadSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityForehead = level;
    }

    @Override
    public void onIntensityNoseSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityNose = level;
    }

    @Override
    public void onIntensityMouthSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mIntensityMouth = level;
    }

    @Override
    public void onPosterTemplateSelected(final int tempWidth, final int tempHeight, final byte[] temp, final float[] landmark) {
        Arrays.fill(posterTemplateLandmark, 0);
        for (int i = 0; i < landmark.length; i++) {
            posterTemplateLandmark[i] = landmark[i];
        }
        // 模板图片的宽
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "template_width", tempWidth);
        // 模板图片的高
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "template_height", tempHeight);
        // 图片的特征点，75个点
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "template_face_points", posterTemplateLandmark);
        // 模板图片的 RGBA byte数组
        faceunity.fuCreateTexForItem(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "tex_template", temp, tempWidth, tempHeight);
    }

    @Override
    public void onPosterInputPhoto(final int inputWidth, final int inputHeight, final byte[] input, final float[] landmark) {
        Arrays.fill(posterPhotoLandmark, 0);
        for (int i = 0; i < landmark.length; i++) {
            posterPhotoLandmark[i] = landmark[i];
        }
        // 输入图片的宽
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "input_width", inputWidth);
        // 输入图片的高
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "input_height", inputHeight);
        // 输入图片的特征点，75个点
        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "input_face_points", posterPhotoLandmark);
        // 输入图片的 RGBA byte 数组
        faceunity.fuCreateTexForItem(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "tex_input", input, inputWidth, inputHeight);
    }

    @Override
    public void setLivePhoto(final LivePhoto livePhoto) {
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIVE_PHOTO_INDEX, livePhoto));
                }
            });
        } else {
            mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_LIVE_PHOTO_INDEX, livePhoto));
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
//                    Log.d(TAG, "makeup: set param key: " + name + ", value: " + Arrays.toString(colors));
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
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.BODY_SLIM_STRENGTH, intensity);
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
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.LEG_SLIM_STRENGTH, intensity);
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
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.WAIST_SLIM_STRENGTH, intensity);
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
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.SHOULDER_SLIM_STRENGTH, intensity);
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
                if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.HIP_SLIM_STRENGTH, intensity);
                }
            }
        });
    }

    @Override
    public void setRemovePouchStrength(float strength) {
        sMicroPouch = strength;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setRemoveNasolabialFoldsStrength(float strength) {
        sMicroNasolabialFolds = strength;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setSmileIntensity(float intensity) {
        sMicroSmile = intensity;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setCanthusIntensity(float intensity) {
        sMicroCanthus = intensity;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setPhiltrumIntensity(float intensity) {
        sMicroPhiltrum = intensity;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setLongNoseIntensity(float intensity) {
        sMicroLongNose = intensity;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setEyeSpaceIntensity(float intensity) {
        sMicroEyeSpace = intensity;
        isNeedUpdateFaceBeauty = true;
    }

    @Override
    public void setEyeRotateIntensity(float intensity) {
        sMicroEyeRotate = intensity;
        isNeedUpdateFaceBeauty = true;
    }

    private void setBeautyBodyOrientation() {
        if (mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] > 0) {
            int bodyOrientation = calculateRotationMode();
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY], BeautifyBodyParam.ORIENTATION, bodyOrientation);
        }
    }

    public void loadAvatarBackground() {
        mIsNeedBackground = true;
        if (mFuItemHandler == null) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_AVATAR_BACKGROUND);
                }
            });
        } else {
            mFuItemHandler.sendEmptyMessage(ITEM_ARRAYS_AVATAR_BACKGROUND);
        }
    }

    public void unloadAvatarBackground() {
        mIsNeedBackground = false;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0) {
                    faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND]);
                    mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = 0;
                }
            }
        });
    }

    /**
     * 加载头发道具
     *
     * @param path 道具路径，如果为空就销毁
     */
    public void loadAvatarHair(final String path) {
        if (TextUtils.isEmpty(path)) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                        faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
                        mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = 0;
                    }
                }
            });
        } else {
            if (mFuItemHandler != null) {
                Message message = Message.obtain(mFuItemHandler, ITEM_ARRAYS_AVATAR_HAIR, path);
                mFuItemHandler.sendMessage(message);
            } else {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        Message message = Message.obtain(mFuItemHandler, ITEM_ARRAYS_AVATAR_HAIR, path);
                        mFuItemHandler.sendMessage(message);
                    }
                });
            }
        }
    }

    @Override
    public void onLightMakeupCombinationSelected(List<LightMakeupItem> makeupItems) {
        Set<Integer> keySet = mLightMakeupItemMap.keySet();
        for (final Integer integer : keySet) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], MakeupParamHelper.getMakeupIntensityKeyByType(integer), 0.0);
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
                    if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] > 0) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX], MakeupParamHelper.MakeupParam.IS_MAKEUP_ON, 0.0);
                    }
                }
            });
        }
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
                if (mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIGHT_MAKEUP_INDEX],
                            MakeupParamHelper.getMakeupIntensityKeyByType(type), level);
                }
            }
        });
    }

    /**
     * 设置相机滤镜的风格
     *
     * @param style
     */
    @Override
    public void setCartoonFilter(final int style) {
        if (mCartoonFilterStyle == style) {
            return;
        }
        Log.d(TAG, "setCartoonFilter: " + style);
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCartoonFilterStyle = style;
                if (mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] > 0) {
                    if (style > CartoonFilter.NO_FILTER) {
                        faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX], "style", style);
                    } else {
                        faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX]);
                        mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] = 0;
                    }
                } else if (style > CartoonFilter.NO_FILTER) {
                    mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, ITEM_ARRAYS_CARTOON_FILTER_INDEX, style));
                }
            }
        });

    }

    /**
     * 设置表情动图是否使用卡通点位，闭眼效果更好
     *
     * @param isCartoon
     */
    public void setIsCartoon(final boolean isCartoon) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX], "is_use_cartoon", isCartoon ? 1 : 0);
                }
            }
        });
    }

    /**
     * 海报换脸，输入人脸五官，自动变形调整
     *
     * @param value 范围 [0-1]，0 为关闭
     */
    public void fixPosterFaceParam(final float value) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX] > 0) {
                    faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_CHANGE_FACE_INDEX], "warp_intensity", value);
                }
            }
        });
    }

    //--------------------------------------IsTracking（人脸识别回调相关定义）----------------------------------------

    private int mTrackingStatus = 0;

    public interface OnTrackingStatusChangedListener {
        void onTrackingStatusChanged(int status);
    }

    private OnTrackingStatusChangedListener mOnTrackingStatusChangedListener;

    //--------------------------------------FaceUnitySystemError（faceunity错误信息回调相关定义）----------------------------------------

    public interface OnSystemErrorListener {
        void onSystemError(String error);
    }

    private OnSystemErrorListener mOnSystemErrorListener;

    //--------------------------------------FPS（FPS相关定义）----------------------------------------

    private static final float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;
    private static final float TIME = 5f;
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
            long tmp = System.nanoTime();
            double fps = (1000.0f * NANO_IN_ONE_MILLI_SECOND / ((tmp - mLastOneHundredFrameTimeStamp) / TIME));
            mLastOneHundredFrameTimeStamp = tmp;
            double renderTime = mOneHundredFrameFUTime / TIME / NANO_IN_ONE_MILLI_SECOND;
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

    @IntDef(value = {HAIR_NORMAL, HAIR_GRADIENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HairType {
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
        int effectType = effect.effectType();
        if (effectType == Effect.EFFECT_TYPE_NORMAL || effectType == Effect.EFFECT_TYPE_EXPRESSION) {
            //rotationAngle 参数是用于旋转普通道具
            faceunity.fuItemSetParam(itemHandle, "rotationAngle", mRotationMode * 90);
        }
        int back = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0;
        if (effectType == Effect.EFFECT_TYPE_AVATAR) {
            // Avatar 头型和头发镜像
            faceunity.fuItemSetParam(itemHandle, "isFlipExpr", back);
            setAvatarHairParams(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
        }

        if (effectType == Effect.EFFECT_TYPE_ANIMOJI || effectType == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            // 镜像顶点
            faceunity.fuItemSetParam(itemHandle, "is3DFlipH", back);
            // 镜像表情
            faceunity.fuItemSetParam(itemHandle, "isFlipExpr", back);
            //这两句代码用于识别人脸默认方向的修改，主要针对animoji道具的切换摄像头倒置问题
            faceunity.fuItemSetParam(itemHandle, "camera_change", 1.0);
        }

        if (effectType == Effect.EFFECT_TYPE_GESTURE) {
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
        setMaxFaces(effect.maxFace());
    }

    private void setEffectRotationMode(Effect effect, int itemHandle) {
        int rotMode;
        if (effect.effectType() == Effect.EFFECT_TYPE_GESTURE && effect.bundleName().startsWith("ctrl")) {
            rotMode = calculateRotModeLagacy();
        } else {
            rotMode = mRotationMode;
        }
        faceunity.fuItemSetParam(itemHandle, "rotMode", rotMode);
        faceunity.fuItemSetParam(itemHandle, "rotationMode", rotMode);
    }

    private void setAvatarHairParams(int itemAvatarHair) {
        if (itemAvatarHair > 0) {
            int back = mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0;
            faceunity.fuItemSetParam(itemAvatarHair, "is3DFlipH", back);
            faceunity.fuItemSetParam(itemAvatarHair, "isFlipTrack", back);
            faceunity.fuItemSetParam(itemAvatarHair, mIsNeedBackground ? "enter_facepup" : "quit_facepup", 1.0);
        }
    }

    /*----------------------------------Builder---------------------------------------*/

    /**
     * FURenderer Builder
     */
    public static class Builder {
        private boolean createEGLContext = false;
        private Effect defaultEffect;
        private int maxFaces = 1;
        private Context context;
        private int inputTextureType = 0;
        private int inputImageFormat = 0;
        private int inputOrientation = 270;
        private int externalInputType = EXTERNAL_INPUT_TYPE_NONE;
        private boolean isNeedFaceBeauty = true;
        private boolean isNeedAnimoji3D = false;
        private boolean isNeedBeautyHair = false;
        private boolean isNeedPosterFace = false;
        private int cameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        private OnBundleLoadCompleteListener onBundleLoadCompleteListener;
        private OnFUDebugListener onFUDebugListener;
        private OnTrackingStatusChangedListener onTrackingStatusChangedListener;
        private OnSystemErrorListener onSystemErrorListener;
        private boolean useBeautifyBody = false;

        private boolean mIsLoadAiBgSeg;
        private boolean mIsLoadAiGesture;
        private boolean mIsLoadAiHairSeg;
        private boolean mIsLoadAiHumanPose;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * 是否加载背景分割 AI 模型
         *
         * @param loadAiBgSeg
         * @return
         */
        public Builder setLoadAiBgSeg(boolean loadAiBgSeg) {
            mIsLoadAiBgSeg = loadAiBgSeg;
            return this;
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
         * 是否加载头发分割 AI 模型
         *
         * @param loadAiHairSeg
         * @return
         */
        public Builder setLoadAiHairSeg(boolean loadAiHairSeg) {
            mIsLoadAiHairSeg = loadAiHairSeg;
            return this;
        }

        /**
         * 是否加载身体分割 AI 模型
         *
         * @param loadAiHumanPose
         * @return
         */
        public Builder setLoadAiHumanPose(boolean loadAiHumanPose) {
            mIsLoadAiHumanPose = loadAiHumanPose;
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
         * 是否使用美体
         *
         * @param useBeautBody
         * @return
         */
        public Builder setUseBeautifyBody(boolean useBeautBody) {
            this.useBeautifyBody = useBeautBody;
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
            fuRenderer.mInputTextureType = inputTextureType;
            fuRenderer.mInputImageFormat = inputImageFormat;
            fuRenderer.mInputOrientation = inputOrientation;
            fuRenderer.mExternalInputType = externalInputType;
            fuRenderer.mDefaultEffect = defaultEffect;
            fuRenderer.isNeedFaceBeauty = isNeedFaceBeauty;
            fuRenderer.isNeedAnimoji3D = isNeedAnimoji3D;
            fuRenderer.isNeedBeautyHair = isNeedBeautyHair;
            fuRenderer.isNeedPosterFace = isNeedPosterFace;
            fuRenderer.mCameraFacing = cameraFacing;
            fuRenderer.mUseBeautifyBody = useBeautifyBody;
            fuRenderer.mOnFUDebugListener = onFUDebugListener;
            fuRenderer.mOnTrackingStatusChangedListener = onTrackingStatusChangedListener;
            fuRenderer.mOnSystemErrorListener = onSystemErrorListener;
            fuRenderer.mOnBundleLoadCompleteListener = onBundleLoadCompleteListener;

            fuRenderer.mIsLoadAiBgSeg = mIsLoadAiBgSeg;
            fuRenderer.mIsLoadAiGesture = mIsLoadAiGesture;
            fuRenderer.mIsLoadAiHairSeg = mIsLoadAiHairSeg;
            fuRenderer.mIsLoadAiHumanPose = mIsLoadAiHumanPose;
            return fuRenderer;
        }
    }

    /**
     * Avatar 捏脸输入参数，常量值
     */
    static class AvatarConstant {
        public static final int EXPRESSION_LENGTH = 46;
        public static final float[] ROTATION_DATA = new float[]{0f, 0f, 0f, 1f};
        public static final float[] PUP_POS_DATA = new float[]{0f, 0f};
        public static final int VALID_DATA = 1;
        public static final float[] EXPRESSIONS = new float[EXPRESSION_LENGTH];
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
                    boolean isNone = effect.effectType() == Effect.EFFECT_TYPE_NONE;
                    final int itemEffect = isNone ? 0 : loadItem(mContext, effect.path());
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
                            if (mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0 && !mIsNeedBackground) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND]);
                                mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = 0;
                            }
                            if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
                                mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = 0;
                            }
                            if (itemEffect > 0) {
                                updateEffectItemParams(effect, itemEffect);
                            }
                            mItemsArray[ITEM_ARRAYS_EFFECT_INDEX] = itemEffect;
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
                            isNeedUpdateFaceBeauty = true;
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
                        if (makeupItem.getType() == LightMakeupCombination.FACE_MAKEUP_TYPE_LIPSTICK) {
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
                                    if (makeupItem.getType() == LightMakeupCombination.FACE_MAKEUP_TYPE_LIPSTICK) {
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
                    final MakeupEntity makeupEntity = msg.obj instanceof MakeupEntity ? (MakeupEntity) msg.obj : null;
                    if (makeupEntity == null) {
                        return;
                    }
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
                            if (mMakeupEntity != null && mMakeupEntity != makeupEntity && mMakeupEntity.getItemHandle() > 0) {
                                faceunity.fuUnBindItems(finalItemMakeup, new int[]{mMakeupEntity.getItemHandle()});
                                faceunity.fuDestroyItem(mMakeupEntity.getItemHandle());
                                Log.d(TAG, "makeup: unbind and destroy old parent item: " + mMakeupEntity.getItemHandle());
                                mMakeupEntity.setItemHandle(0);
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
                            Log.i(TAG, "bind makeup:" + makeupEntity + ", unbind makeup:" + (mMakeupEntity != makeupEntity ? mMakeupEntity : "null"));
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
                            // 美发类型
                            faceunity.fuItemSetParam(itemHair, "Index", mHairColorIndex);
                            // 美发强度
                            faceunity.fuItemSetParam(itemHair, "Strength", mHairColorStrength);
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
                    final int style = (int) msg.obj;
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            faceunity.fuItemSetParam(itemCartoonFilter, "style", style);
                            int glMajorVersion = GlUtil.getGlMajorVersion();
                            faceunity.fuItemSetParam(itemCartoonFilter, "glVer", glMajorVersion);
                            mItemsArray[ITEM_ARRAYS_CARTOON_FILTER_INDEX] = itemCartoonFilter;
                        }
                    });
                }
                break;
                // 加载表情动图 bundle
                case ITEM_ARRAYS_LIVE_PHOTO_INDEX: {
                    final LivePhoto livePhoto = (LivePhoto) msg.obj;
                    if (livePhoto == null) {
                        return;
                    }
                    if (mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] <= 0) {
                        int itemLivePhoto = loadItem(mContext, BUNDLE_LIVE_PHOTO);
                        if (itemLivePhoto <= 0) {
                            Log.w(TAG, "create live photo item failed: " + itemLivePhoto);
                            return;
                        }
                        mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX] = itemLivePhoto;
                    }
                    setIsFrontCamera(mCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);

                    final MakeupParamHelper.TextureImage textureImage = MakeupParamHelper.createTextureImage(mContext, livePhoto.getTemplateImagePath());
                    if (textureImage != null) {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                int itemHandle = mItemsArray[ITEM_ARRAYS_LIVE_PHOTO_INDEX];
                                // 设置五官类型
                                faceunity.fuItemSetParam(itemHandle, "group_type", livePhoto.getGroupType());
                                // 设置五官点位
                                faceunity.fuItemSetParam(itemHandle, "group_points", livePhoto.getGroupPoints());
                                // 输入图片的宽
                                faceunity.fuItemSetParam(itemHandle, "target_width", textureImage.getWidth());
                                // 输入图片的高
                                faceunity.fuItemSetParam(itemHandle, "target_height", textureImage.getHeight());
                                // 输入图片的 RGBA byte 数组
                                faceunity.fuCreateTexForItem(itemHandle, "tex_input", textureImage.getBytes(), textureImage.getWidth(), textureImage.getHeight());
                                // 设置插值开关
                                faceunity.fuItemSetParam(itemHandle, "use_interpolate2", 0.0);
                                // 设置使用卡通点位运行
                                setIsCartoon(true);
                            }
                        });
                    }
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
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.BODY_SLIM_STRENGTH, mBodySlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.LEG_SLIM_STRENGTH, mLegSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.WAIST_SLIM_STRENGTH, mWaistSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.SHOULDER_SLIM_STRENGTH, mShoulderSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.HIP_SLIM_STRENGTH, mHipSlimStrength);
                            faceunity.fuItemSetParam(itemBeautifyBody, BeautifyBodyParam.DEBUG, 0.0);
                            mItemsArray[ITEM_ARRAYS_BEAUTIFY_BODY] = itemBeautifyBody;
                            setBeautyBodyOrientation();
                        }
                    });
                }
                break;
                // 加载 Avatar 捏脸的头发 bundle
                case ITEM_ARRAYS_AVATAR_HAIR: {
                    String path = (String) msg.obj;
                    if (!TextUtils.isEmpty(path)) {
                        final int itemAvatarHair = loadItem(mContext, path);
                        if (itemAvatarHair <= 0) {
                            Log.w(TAG, "create avatar hair item failed: " + itemAvatarHair);
                            return;
                        }
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                int oldItem = mItemsArray[ITEM_ARRAYS_AVATAR_HAIR];
                                setAvatarHairParams(itemAvatarHair);
                                mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = itemAvatarHair;
                                if (oldItem > 0) {
                                    faceunity.fuDestroyItem(oldItem);
                                }
                            }
                        });
                    } else {
                        queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                if (mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] > 0) {
                                    faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_HAIR]);
                                    mItemsArray[ITEM_ARRAYS_AVATAR_HAIR] = 0;
                                }
                            }
                        });
                    }
                }
                break;
                // 加载 Avatar 捏脸的背景 bundle
                case ITEM_ARRAYS_AVATAR_BACKGROUND: {
                    final int itemAvatarBg = loadItem(mContext, BUNDLE_AVATAR_BACKGROUND);
                    if (itemAvatarBg <= 0) {
                        Log.w(TAG, "create avatar background item failed: " + itemAvatarBg);
                        return;
                    }
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND]);
                                mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = 0;
                            }
                            mItemsArray[ITEM_ARRAYS_AVATAR_BACKGROUND] = itemAvatarBg;
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
