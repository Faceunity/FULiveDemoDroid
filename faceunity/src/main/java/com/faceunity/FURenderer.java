package com.faceunity;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import com.faceunity.entity.Effect;
import com.faceunity.entity.Filter;
import com.faceunity.wrapper.faceunity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.faceunity.wrapper.faceunity.FU_ADM_FLAG_FLIP_X;

/**
 * 一个基于Faceunity Nama SDK的简单封装，方便简单集成，理论上简单需求的步骤：
 * <p>
 * 1.通过OnEffectSelectedListener在UI上进行交互
 * 2.合理调用FURenderer构造函数
 * 3.对应的时机调用onSurfaceCreated和onSurfaceDestroyed
 * 4.处理图像时调用onDrawFrame
 * <p>
 * 如果您有更高级的定制需求，Nama API文档请参考http://www.faceunity.com/technical/android-api.html
 */
public class FURenderer implements OnFUControlListener {
    private static final String TAG = FURenderer.class.getSimpleName();

    public static final int FU_ADM_FLAG_EXTERNAL_OES_TEXTURE = faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE;

    private Context mContext;

    /**
     * 目录assets下的 *.bundle为程序的数据文件。
     * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
     * face_beautification.bundle：美颜和美型相关的数据文件；
     * anim_model.bundle：优化表情跟踪功能所需要加载的动画数据文件；适用于使用Animoji和avatar功能的用户，如果不是，可不加载
     * ardata_ex.bundle：高精度模式的三维张量数据文件。适用于换脸功能，如果没用该功能可不加载
     * fxaa.bundle：3D绘制抗锯齿数据文件。加载后，会使得3D绘制效果更加平滑。
     * 目录effects下是我们打包签名好的道具
     */
    public static final String BUNDLE_v3 = "v3.bundle";
    public static final String BUNDLE_anim_model = "anim_model.bundle";
    public static final String BUNDLE_face_beautification = "face_beautification.bundle";
    public static final String BUNDLE_ardata_ex = "ardata_ex.bundle";
    public static final String BUNDLE_animoji_3d = "fxaa.bundle";

    //美颜和滤镜的默认参数
    private boolean isNeedUpdateFaceBeauty = true;
    private static float mFilterLevel = 1.0f;//滤镜强度
    private static Filter mFilterName = new Filter("origin", 0, "原图", Filter.FILTER_TYPE_BEAUTY_FILTER);

    private static float mSkinDetect = 1.0f;//精准磨皮
    private static float mHeavyBlur = 0.0f;//美肤类型
    private static float mBlurLevel = 0.7f;//磨皮
    private static float mColorLevel = 0.5f;//美白
    private static float mRedLevel = 0.5f;//红润
    private static float mEyeBright = 0.0f;//亮眼
    private static float mToothWhiten = 0.0f;//美牙

    private static float mFaceShape = 4.0f;//脸型
    private static float mFaceShapeLevel = 1.0f;//程度
    private static float mEyeEnlarging = 0.4f;//大眼
    private static float mCheekThinning = 0.4f;//瘦脸
    private static float mIntensityChin = 0.3f;//下巴
    private static float mIntensityForehead = 0.3f;//额头
    private static float mIntensityNose = 0.5f;//瘦鼻
    private static float mIntensityMouth = 0.4f;//嘴形

    private int mFrameId = 0;

    private static final int ITEM_ARRAYS_FACE_BEAUTY_INDEX = 0;
    private static final int ITEM_ARRAYS_EFFECT = 1;
    private static final int ITEM_ARRAYS_EFFECT_ABIMOJI_3D = 2;
    private static final int ITEM_ARRAYS_COUNT = 3;
    //美颜和其他道具的handle数组
    private final int[] mItemsArray = new int[ITEM_ARRAYS_COUNT];
    //用于和异步加载道具的线程交互
    private HandlerThread mFuItemHandlerThread;
    private Handler mFuItemHandler;

    private boolean isNeedFaceBeauty = true;
    private boolean isNeedAnimoji3D = false;
    private Effect mDefaultEffect;//默认道具（同步加载）
    private int mMaxFaces = 4; //同时识别的最大人脸
    private boolean mIsCreateEGLContext; //是否需要手动创建EGLContext
    private int mInputTextureType = 0; //输入的图像texture类型，Camera提供的默认为EXTERNAL OES
    private int mInputImageFormat = 0;
    private boolean mNeedReadBackImage = false; //将传入的byte[]图像复写为具有道具效果的

    private int mInputImageOrientation = 0;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private float[] landmarksData = new float[150];
    private float[] expressionData = new float[46];
    private float[] rotationData = new float[4];
    private float[] pupilPosData = new float[2];
    private float[] rotationModeData = new float[1];

    private ArrayList<Runnable> mEventQueue = new ArrayList<>();

    /**
     * 全局加载相应的底层数据包
     */
    public static void initFURenderer(Context context) {
        try {
            //获取faceunity SDK版本信息
            Log.e(TAG, "fu sdk version " + faceunity.fuGetVersion());

            /**
             * fuSetup faceunity初始化
             * 其中 v3.bundle：人脸识别数据文件，缺少该文件会导致系统初始化失败；
             *      authpack：用于鉴权证书内存数组。若没有,请咨询support@faceunity.com
             * 首先调用完成后再调用其他FU API
             */
            InputStream v3 = context.getAssets().open(BUNDLE_v3);
            byte[] v3Data = new byte[v3.available()];
            v3.read(v3Data);
            v3.close();
            faceunity.fuSetup(v3Data, null, authpack.A());

            /**
             * 加载优化表情跟踪功能所需要加载的动画数据文件anim_model.bundle；
             * 启用该功能可以使表情系数及avatar驱动表情更加自然，减少异常表情、模型缺陷的出现。该功能对性能的影响较小。
             * 启用该功能时，通过 fuLoadAnimModel 加载动画模型数据，加载成功即可启动。该功能会影响通过fuGetFaceInfo获取的expression表情系数，以及通过表情驱动的avatar模型。
             * 适用于使用Animoji和avatar功能的用户，如果不是，可不加载
             */
            InputStream animModel = context.getAssets().open(BUNDLE_anim_model);
            byte[] animModelData = new byte[animModel.available()];
            animModel.read(animModelData);
            animModel.close();
            faceunity.fuLoadAnimModel(animModelData);

            /**
             * 加载高精度模式的三维张量数据文件ardata_ex.bundle。
             * 适用于换脸功能，如果没用该功能可不加载；如果使用了换脸功能，必须加载，否则会报错
             */
            InputStream ar = context.getAssets().open(BUNDLE_ardata_ex);
            byte[] arDate = new byte[ar.available()];
            ar.read(arDate);
            ar.close();
            faceunity.fuLoadExtendedARData(arDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取faceunity sdk 版本库
     */
    public static String getVersion() {
        return faceunity.fuGetVersion();
    }

    /**
     * 获取证书相关的权限码
     */
    public static int getModuleCode() {
        return faceunity.fuGetModuleCode(0);
    }

    /**
     * FURenderer构造函数
     */
    private FURenderer(Context context, boolean isCreateEGLContext) {
        this.mContext = context;
        this.mIsCreateEGLContext = isCreateEGLContext;

        mFuItemHandlerThread = new HandlerThread("FUItemHandlerThread");
        mFuItemHandlerThread.start();
        mFuItemHandler = new FUItemHandler(mFuItemHandlerThread.getLooper());
    }

    /**
     * 创建及初始化faceunity相应的资源
     */
    public void onSurfaceCreated() {
        Log.e(TAG, "onSurfaceCreated");

        /**
         * fuCreateEGLContext 创建OpenGL环境
         * 适用于没OpenGL环境时调用
         * 如果调用了fuCreateEGLContext，在销毁时需要调用fuReleaseEGLContext
         */
        if (mIsCreateEGLContext) faceunity.fuCreateEGLContext();

        mFrameId = 0;
        /**
         *fuSetExpressionCalibration 控制表情校准功能的开关及不同模式，参数为0时关闭表情校准，2为被动校准。
         * 被动校准：该种模式下会在整个用户使用过程中逐渐进行表情校准，用户对该过程没有明显感觉。
         *
         * 优化后的SDK只支持被动校准功能，即fuSetExpressionCalibration接口只支持0（关闭）或2（被动校准）这两个数字，设置为1时将不再有效果。
         */
        faceunity.fuSetExpressionCalibration(2);
        faceunity.fuSetMaxFaces(mMaxFaces);//设置多脸，目前最多支持8人。

        if (isNeedFaceBeauty) {
            mFuItemHandler.sendEmptyMessage(FUItemHandler.HANDLE_CREATE_BEAUTY_ITEM);
        }

        if (isNeedAnimoji3D) {
            mFuItemHandler.sendEmptyMessage(FUItemHandler.HANDLE_CREATE_ANIMOJI3D_ITEM);
        }

        //加载默认道具
        if (mDefaultEffect != null) {
            mItemsArray[ITEM_ARRAYS_EFFECT] = loadItem(mDefaultEffect);
            faceunity.fuSetMaxFaces(mDefaultEffect.maxFace());
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
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToNV21Image(img, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
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
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToNV21Image(img, w, h, mFrameId++, mItemsArray, flags,
                readBackW, readBackH, readBackImg);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
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
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType | mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, flags, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
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
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType | mInputImageFormat;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuDualInputToTexture(img, tex, flags, w, h, mFrameId++, mItemsArray,
                readBackW, readBackH, readBackImg);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
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
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType;
        if (mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT)
            flags |= FU_ADM_FLAG_FLIP_X;

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuRenderToTexture(tex, w, h, mFrameId++, mItemsArray, flags);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
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
            Log.e(TAG, "onDrawFrame date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputTextureType;

        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        int fuTex = faceunity.fuBeautifyImage(tex, flags, w, h, mFrameId++, mItemsArray);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return fuTex;
    }

    /**
     * 使用 fuTrackFace + fuAvatarToTexture 的方法组合绘制画面，该组合没有camera画面绘制，适用于animoji等相关道具的绘制。
     * fuTrackFace 获取识别到的人脸信息
     * fuAvatarToTexture 依据人脸信息绘制道具
     *
     * @param img 数据格式可由 flags 定义
     * @param w
     * @param h
     * @return
     */
    public int onDrawFrameAvatar(byte[] img, int w, int h) {
        if (img == null || w <= 0 || h <= 0) {
            Log.e(TAG, "onDrawFrameAvatar date null");
            return 0;
        }
        prepareDrawFrame();

        int flags = mInputImageFormat;
        if (mNeedBenchmark) mFuCallStartTime = System.nanoTime();
        faceunity.fuTrackFace(img, flags, w, h);

        /**
         * landmarks 2D人脸特征点，返回值为75个二维坐标，长度75*2
         */
        Arrays.fill(landmarksData, 0.0f);
        faceunity.fuGetFaceInfo(0, "landmarks", landmarksData);

        /**
         *rotation 人脸三维旋转，返回值为旋转四元数，长度4
         */
        Arrays.fill(rotationData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation", rotationData);
        /**
         * expression  表情系数，长度46
         */
        Arrays.fill(expressionData, 0.0f);
        faceunity.fuGetFaceInfo(0, "expression", expressionData);

        /**
         * pupil pos 人脸朝向，0-3分别对应手机四种朝向，长度1
         */
        Arrays.fill(pupilPosData, 0.0f);
        faceunity.fuGetFaceInfo(0, "pupil_pos", pupilPosData);

        /**
         * rotation mode
         */
        Arrays.fill(rotationModeData, 0.0f);
        faceunity.fuGetFaceInfo(0, "rotation_mode", rotationModeData);

        int isTracking = faceunity.fuIsTracking();

        if (isTracking <= 0) {
            rotationData[3] = 1.0f;
            rotationModeData[0] = (360 - mInputImageOrientation) / 90;
        }

        int tex = faceunity.fuAvatarToTexture(pupilPosData, expressionData, rotationData, rotationModeData,
                0, w, h, mFrameId++, mItemsArray, isTracking);
        if (mNeedBenchmark) mOneHundredFrameFUTime += System.nanoTime() - mFuCallStartTime;
        return tex;
    }

    /**
     * 销毁faceunity相关的资源
     */
    public void onSurfaceDestroyed() {
        Log.e(TAG, "onSurfaceDestroyed");
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_ITEM);

        mFrameId = 0;
        isNeedUpdateFaceBeauty = true;
        Arrays.fill(mItemsArray, 0);
        faceunity.fuDestroyAllItems();
        faceunity.fuOnDeviceLost();
        faceunity.fuDone();
        mEventQueue.clear();
        if (mIsCreateEGLContext) faceunity.fuReleaseEGLContext();
    }

    /**
     * 每帧处理画面时被调用
     */
    private void prepareDrawFrame() {
        //计算FPS等数据
        benchmarkFPS();

        //获取人脸是否识别，并调用回调接口
        int isTracking = faceunity.fuIsTracking();
        if (mOnTrackingStatusChangedListener != null && mTrackingStatus != isTracking) {
            mOnTrackingStatusChangedListener.onTrackingStatusChanged(mTrackingStatus = isTracking);
        }

        //获取faceunity错误信息，并调用回调接口
        int error = faceunity.fuGetSystemError();
        if (error != 0)
            Log.e(TAG, "fuGetSystemErrorString " + faceunity.fuGetSystemErrorString(error));
        if (mOnSystemErrorListener != null && error != 0) {
            mOnSystemErrorListener.onSystemError(faceunity.fuGetSystemErrorString(error));
        }

        //修改美颜参数
        if (isNeedUpdateFaceBeauty && mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] != 0) {
            //filter_level 滤镜强度 范围0~1 SDK默认为 1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "filter_level", mFilterLevel);
            //filter_name 滤镜
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "filter_name", mFilterName.filterName());

            //skin_detect 精准美肤 0:关闭 1:开启 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "skin_detect", mSkinDetect);
            //heavy_blur 美肤类型 0:清晰美肤 1:朦胧美肤 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "heavy_blur", mHeavyBlur);
            //blur_level 磨皮 范围0~6 SDK默认为 6
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "blur_level", 6 * mBlurLevel);
            //blur_blend_ratio 磨皮结果和原图融合率 范围0~1 SDK默认为 1
//          faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "blur_blend_ratio", 1);

            //color_level 美白 范围0~1 SDK默认为 1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "color_level", mColorLevel);
            //red_level 红润 范围0~1 SDK默认为 1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "red_level", mRedLevel);
            //eye_bright 亮眼 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "eye_bright", mEyeBright);
            //tooth_whiten 美牙 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "tooth_whiten", mToothWhiten);


            //face_shape_level 美型程度 范围0~1 SDK默认为1
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "face_shape_level", mFaceShapeLevel);
            //face_shape 脸型 0：女神 1：网红 2：自然 3：默认 4：自定义（新版美型） SDK默认为 3
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "face_shape", mFaceShape);
            //eye_enlarging 大眼 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "eye_enlarging", mEyeEnlarging);
            //cheek_thinning 瘦脸 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "cheek_thinning", mCheekThinning);
            //intensity_chin 下巴 范围0~1 SDK默认为 0.5    大于0.5变大，小于0.5变小
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_chin", mIntensityChin);
            //intensity_forehead 额头 范围0~1 SDK默认为 0.5    大于0.5变大，小于0.5变小
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_forehead", mIntensityForehead);
            //intensity_nose 鼻子 范围0~1 SDK默认为 0
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_nose", mIntensityNose);
            //intensity_mouth 嘴型 范围0~1 SDK默认为 0.5   大于0.5变大，小于0.5变小
            faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX], "intensity_mouth", mIntensityMouth);
            isNeedUpdateFaceBeauty = false;
        }

        //queueEvent的Runnable在此处被调用
        while (!mEventQueue.isEmpty()) {
            mEventQueue.remove(0).run();
        }
    }

    //--------------------------------------对外可使用的接口----------------------------------------

    /**
     * 类似GLSurfaceView的queueEvent机制
     */
    public void queueEvent(Runnable r) {
        mEventQueue.add(r);
    }


    /**
     * 设置需要识别的人脸个数
     *
     * @param maxFaces
     */
    public void setMaxFaces(final int maxFaces) {
        if (mMaxFaces != maxFaces && maxFaces > 0) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mMaxFaces = maxFaces;
                    faceunity.fuSetMaxFaces(maxFaces);
                }
            });
        }
    }

    /**
     * camera切换时需要调用
     *
     * @param currentCameraType     前后置摄像头ID
     * @param inputImageOrientation
     */
    public void onCameraChange(final int currentCameraType, final int inputImageOrientation) {
        if (mCurrentCameraType == currentCameraType && mInputImageOrientation == inputImageOrientation)
            return;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                mCurrentCameraType = currentCameraType;
                mInputImageOrientation = inputImageOrientation;
                faceunity.fuOnCameraChange();
                updateEffectItemParams(mDefaultEffect, mItemsArray[ITEM_ARRAYS_EFFECT]);
            }
        });
    }

    private int mDefaultOrientation;

    public void setTrackOrientation(final int rotation) {
        if (mTrackingStatus == 0 && mDefaultOrientation != rotation) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    mDefaultOrientation = rotation;
                    faceunity.fuSetDefaultOrientation(rotation / 90);//设置识别人脸默认方向，能够提高首次识别的速度
                }
            });
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

    public void setDefaultEffect(Effect defaultEffect) {
        mDefaultEffect = defaultEffect;
    }

    //--------------------------------------美颜参数与道具回调----------------------------------------

    @Override
    public void onMusicFilterTime(final long time) {
        queueEvent(new Runnable() {
            @Override
            public void run() {
                faceunity.fuItemSetParam(mItemsArray[ITEM_ARRAYS_EFFECT], "music_time", time);
            }
        });
    }

    @Override
    public void onEffectSelected(Effect effectItemName) {
        createItem(mDefaultEffect = effectItemName);
    }

    @Override
    public void onFilterLevelSelected(float progress) {
        isNeedUpdateFaceBeauty = true;
        mFilterLevel = progress;
    }

    @Override
    public void onFilterNameSelected(Filter filterName) {
        isNeedUpdateFaceBeauty = true;
        this.mFilterName = filterName;
    }

    @Override
    public void onSkinDetectSelected(float isOpen) {
        isNeedUpdateFaceBeauty = true;
        mSkinDetect = isOpen;
    }

    @Override
    public void onHeavyBlurSelected(float isOpen) {
        isNeedUpdateFaceBeauty = true;
        mHeavyBlur = isOpen;
    }

    @Override
    public void onBlurLevelSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mBlurLevel = level;
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
    public void onFaceShapeSelected(float faceShape) {
        isNeedUpdateFaceBeauty = true;
        this.mFaceShape = faceShape;
    }

    @Override
    public void onEyeEnlargeSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mEyeEnlarging = level;
    }

    @Override
    public void onCheekThinningSelected(float level) {
        isNeedUpdateFaceBeauty = true;
        mCheekThinning = level;
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
        if (!mNeedBenchmark) return;
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

    public void createItem(Effect item) {
        if (item == null) return;
        mFuItemHandler.removeMessages(FUItemHandler.HANDLE_CREATE_ITEM);
        mFuItemHandler.sendMessage(Message.obtain(mFuItemHandler, FUItemHandler.HANDLE_CREATE_ITEM, item));
    }

    class FUItemHandler extends Handler {

        static final int HANDLE_CREATE_ITEM = 1;
        static final int HANDLE_CREATE_BEAUTY_ITEM = 2;
        static final int HANDLE_CREATE_ANIMOJI3D_ITEM = 3;

        FUItemHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //加载道具
                case HANDLE_CREATE_ITEM:
                    final Effect effect = (Effect) msg.obj;
                    final int newEffectItem = loadItem(effect);
                    queueEvent(new Runnable() {
                        @Override
                        public void run() {
                            if (mItemsArray[ITEM_ARRAYS_EFFECT] > 0) {
                                faceunity.fuDestroyItem(mItemsArray[ITEM_ARRAYS_EFFECT]);
                            }
                            mItemsArray[ITEM_ARRAYS_EFFECT] = newEffectItem;
                            if (mItemsArray[ITEM_ARRAYS_EFFECT] > 0)
                                setMaxFaces(effect.maxFace());
                        }
                    });
                    break;
                //加载美颜bundle
                case HANDLE_CREATE_BEAUTY_ITEM:
                    try {
                        InputStream beauty = mContext.getAssets().open(BUNDLE_face_beautification);
                        byte[] beautyData = new byte[beauty.available()];
                        beauty.read(beautyData);
                        beauty.close();
                        mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX] = faceunity.fuCreateItemFromPackage(beautyData);
                        isNeedUpdateFaceBeauty = true;
                        Log.e(TAG, "face beauty item handle " + mItemsArray[ITEM_ARRAYS_FACE_BEAUTY_INDEX]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                //加载animoji道具3D抗锯齿bundle
                case HANDLE_CREATE_ANIMOJI3D_ITEM:
                    try {
                        InputStream animoji3D = mContext.getAssets().open(BUNDLE_animoji_3d);
                        byte[] animoji3DData = new byte[animoji3D.available()];
                        animoji3D.read(animoji3DData);
                        animoji3D.close();
                        mItemsArray[ITEM_ARRAYS_EFFECT_ABIMOJI_3D] = faceunity.fuCreateItemFromPackage(animoji3DData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    /**
     * fuCreateItemFromPackage 加载道具
     *
     * @param bundle（Effect本demo定义的道具实体类）
     * @return 大于0时加载成功
     */
    private int loadItem(final Effect bundle) {
        int item = 0;
        try {
            if (bundle.effectType() == Effect.EFFECT_TYPE_NONE) {
                item = 0;
            } else {
                InputStream is = mContext.getAssets().open(bundle.path());
                byte[] itemData = new byte[is.available()];
                int len = is.read(itemData);
                Log.e(TAG, bundle.path() + " len " + len);
                is.close();
                item = faceunity.fuCreateItemFromPackage(itemData);
                final int finalItem = item;
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        updateEffectItemParams(bundle, finalItem);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 设置对道具设置相应的参数
     *
     * @param itemHandle
     */
    private void updateEffectItemParams(Effect effect, final int itemHandle) {
        if (effect == null || itemHandle == 0) return;
        faceunity.fuItemSetParam(itemHandle, "isAndroid", 1.0);

        if (effect.effectType() == Effect.EFFECT_TYPE_NORMAL) {
            //rotationAngle 参数是用于旋转普通道具
            faceunity.fuItemSetParam(itemHandle, "rotationAngle", 360 - mInputImageOrientation);
        }
        if (effect.effectType() == Effect.EFFECT_TYPE_ANIMOJI || effect.effectType() == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            //这两句代码用于识别人脸默认方向的修改，主要针对animoji道具的切换摄像头倒置问题
            faceunity.fuItemSetParam(itemHandle, "camera_change", 1.0);
            faceunity.fuSetDefaultRotationMode((360 - mInputImageOrientation) / 90);
        }
        if (effect.effectType() == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            //is3DFlipH 参数是用于对3D道具的镜像
            faceunity.fuItemSetParam(itemHandle, "is3DFlipH", mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0);
        }
        if (effect.effectType() == Effect.EFFECT_TYPE_PORTRAIT_DRIVE) {
            //isFlipExpr 参数是用于对人像驱动道具的镜像
            faceunity.fuItemSetParam(itemHandle, "isFlipExpr", mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0);
        }
        if (effect.effectType() == Effect.EFFECT_TYPE_GESTURE) {
            //loc_y_flip与loc_x_flip 参数是用于对手势识别道具的镜像
            faceunity.fuItemSetParam(itemHandle, "loc_y_flip", mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0);
            faceunity.fuItemSetParam(itemHandle, "loc_x_flip", mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_BACK ? 1 : 0);
        }
    }

    //--------------------------------------Builder----------------------------------------

    /**
     * FURenderer Builder
     */
    public static class Builder {

        private boolean createEGLContext = false;
        private Effect defaultEffect;
        private int maxFaces = 1;
        private Context context;
        private int inputTextureType = 0;
        private boolean needReadBackImage = false;
        private int inputImageFormat = 0;
        private int inputImageRotation = 90;
        private boolean isNeedAnimoji3D = false;
        private boolean isNeedFaceBeauty = true;
        private int currentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;

        private OnFUDebugListener onFUDebugListener;
        private OnTrackingStatusChangedListener onTrackingStatusChangedListener;
        private OnSystemErrorListener onSystemErrorListener;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        public Builder createEGLContext(boolean createEGLContext) {
            this.createEGLContext = createEGLContext;
            return this;
        }

        public Builder defaultEffect(Effect defaultEffect) {
            this.defaultEffect = defaultEffect;
            return this;
        }

        public Builder maxFaces(int maxFaces) {
            this.maxFaces = maxFaces;
            return this;
        }

        public Builder inputTextureType(int textureType) {
            this.inputTextureType = textureType;
            return this;
        }

        public Builder needReadBackImage(boolean needReadBackImage) {
            this.needReadBackImage = needReadBackImage;
            return this;
        }

        public Builder inputImageFormat(int inputImageFormat) {
            this.inputImageFormat = inputImageFormat;
            return this;
        }

        public Builder inputImageOrientation(int inputImageRotation) {
            this.inputImageRotation = inputImageRotation;
            return this;
        }

        public Builder setNeedAnimoji3D(boolean needAnimoji3D) {
            this.isNeedAnimoji3D = needAnimoji3D;
            return this;
        }

        public Builder setNeedFaceBeauty(boolean needFaceBeauty) {
            isNeedFaceBeauty = needFaceBeauty;
            return this;
        }

        public Builder setCurrentCameraType(int cameraType) {
            currentCameraType = cameraType;
            return this;
        }

        public Builder setOnFUDebugListener(OnFUDebugListener onFUDebugListener) {
            this.onFUDebugListener = onFUDebugListener;
            return this;
        }

        public Builder setOnTrackingStatusChangedListener(OnTrackingStatusChangedListener onTrackingStatusChangedListener) {
            this.onTrackingStatusChangedListener = onTrackingStatusChangedListener;
            return this;
        }

        public Builder setOnSystemErrorListener(OnSystemErrorListener onSystemErrorListener) {
            this.onSystemErrorListener = onSystemErrorListener;
            return this;
        }

        public FURenderer build() {
            FURenderer fuRenderer = new FURenderer(context, createEGLContext);
            fuRenderer.mMaxFaces = maxFaces;
            fuRenderer.mInputTextureType = inputTextureType;
            fuRenderer.mNeedReadBackImage = needReadBackImage;
            fuRenderer.mInputImageFormat = inputImageFormat;
            fuRenderer.mInputImageOrientation = inputImageRotation;
            fuRenderer.mDefaultEffect = defaultEffect;
            fuRenderer.isNeedAnimoji3D = isNeedAnimoji3D;
            fuRenderer.isNeedFaceBeauty = isNeedFaceBeauty;
            fuRenderer.mCurrentCameraType = currentCameraType;

            fuRenderer.mOnFUDebugListener = onFUDebugListener;
            fuRenderer.mOnTrackingStatusChangedListener = onTrackingStatusChangedListener;
            fuRenderer.mOnSystemErrorListener = onSystemErrorListener;
            return fuRenderer;
        }

    }
}
