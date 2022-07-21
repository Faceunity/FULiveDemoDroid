package com.faceunity.app;

import android.app.Application;
import android.os.Environment;

import com.faceunity.app.utils.FileUtils;
import com.faceunity.app.utils.FuDeviceUtils;

import java.io.File;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class DemoConfig {

    /************************** 算法Model  ******************************/
    // 人脸识别
    public static String BUNDLE_AI_FACE = "model" + File.separator + "ai_face_processor.bundle";
    // 手势
    public static String BUNDLE_AI_HAND = "model" + File.separator + "ai_hand_processor.bundle";

    //获取人体bundle
    public static String getAIHumanBundle() {
        if (DemoConfig.DEVICE_LEVEL > FuDeviceUtils.DEVICE_LEVEL_MID)
            return BUNDLE_AI_HUMAN_GPU;
        else
            return BUNDLE_AI_HUMAN;
    }

    // 人体
    public static String BUNDLE_AI_HUMAN = "model" + File.separator + "ai_human_processor.bundle";
    // 人体
    public static String BUNDLE_AI_HUMAN_GPU = "model" + File.separator + "ai_human_processor_gpu.bundle";
    // 头发
    public static String BUNDLE_AI_HAIR_SEG = "model" + File.separator + "ai_hairseg.bundle";
    // 舌头
    public static String BUNDLE_AI_TONGUE = "graphics" + File.separator + "tongue.bundle";

    /************************** 业务道具存储  ******************************/
    // 美颜
    public static String BUNDLE_FACE_BEAUTIFICATION = "graphics" + File.separator + "face_beautification.bundle";

    // 美妆
    public static String BUNDLE_FACE_MAKEUP = "graphics" + File.separator + "face_makeup.bundle";
    // 美妆根目录
    private static String MAKEUP_RESOURCE_DIR = "makeup" + File.separator;
    //美妆单项颜色组合文件
    public static String MAKEUP_RESOURCE_COLOR_SETUP_JSON = MAKEUP_RESOURCE_DIR + "color_setup.json";
    // 美妆参数配置文件目录
    public static String MAKEUP_RESOURCE_JSON_DIR = MAKEUP_RESOURCE_DIR + "config_json" + File.separator;
    //美妆组合妆句柄文件目录
    public static String MAKEUP_RESOURCE_COMBINATION_BUNDLE_DIR = MAKEUP_RESOURCE_DIR + "combination_bundle" + File.separator;//
    //美妆妆容单项句柄文件目录
    public static String MAKEUP_RESOURCE_ITEM_BUNDLE_DIR = MAKEUP_RESOURCE_DIR + "item_bundle" + File.separator;

    // 美体
    public static String BUNDLE_BODY_BEAUTY = "graphics" + File.separator + "body_slim.bundle";

    //动漫滤镜
    public static String BUNDLE_ANIMATION_FILTER = "graphics" + File.separator + "fuzzytoonfilter.bundle";

    // 美发正常色
    public static String BUNDLE_HAIR_NORMAL = "hair_seg" + File.separator + "hair_normal.bundle";
    // 美发渐变色
    public static String BUNDLE_HAIR_GRADIENT = "hair_seg" + File.separator + "hair_gradient.bundle";
    // 轻美妆
    public static String BUNDLE_LIGHT_MAKEUP = "light_makeup" + File.separator + "light_makeup.bundle";

    // 海报换脸
    public static String BUNDLE_POSTER_CHANGE_FACE = "change_face" + File.separator + "change_face.bundle";

    // 绿幕抠像
    public static String BUNDLE_BG_SEG_GREEN = "bg_seg_green" + File.separator + "green_screen.bundle";

    // 3D抗锯齿
    public static String BUNDLE_ANTI_ALIASING = "graphics" + File.separator + "fxaa.bundle";

    // 人像分割
    public static String BUNDLE_BG_SEG_CUSTOM = "effect" + File.separator + "segment" + File.separator + "bg_segment.bundle";

    //mask bundle
    public static String BUNDLE_LANDMARKS = "effect" + File.separator + "landmarks.bundle";

    //设备等级默认为中级
    public static int DEVICE_LEVEL = FuDeviceUtils.DEVICE_LEVEL_MID;

    //人脸置信度 标准
    public static float FACE_CONFIDENCE_SCORE = 0.95f;

    //测试使用 -> 是否开启人脸点位，目前仅在美颜，美妆 情况下使用
    public static boolean IS_OPEN_LAND_MARK = false;

    //设备名称
    public static String DEVICE_NAME = "";

    //是否开启日志重定向到文件
    public static boolean OPEN_FILE_LOG = false;
    //TAG
    public static final String APP_NAME = "KotlinFaceUnityDemo";
    //文件夹路径
    public static String OPEN_FILE_PATH = Environment.getExternalStoragePublicDirectory("") + File.separator + "FaceUnity" + File.separator + APP_NAME + File.separator;
    //文件夹名称
    public static String OPEN_FILE_NAME = "openFile.txt";
    //文件大小
    public static int OPEN_FILE_MAX_SIZE = 100 * 1024 * 1024;
    //文件数量
    public static int OPEN_FILES = 100;

    //timeProfile是否开启
    public static boolean OPEN_TIME_PROFILE_LOG = false;
    //timeProfile文件夹路径
    public static String OPEN_TIME_PROFILE_PATH = Environment.getExternalStoragePublicDirectory("") + File.separator + "FaceUnity" + File.separator + APP_NAME + File.separator;

    //是否开启美颜序列化到磁盘
    public static boolean OPEN_FACE_BEAUTY_TO_FILE = true;

    //获取缓存路径
    public static String cacheFilePath(Application application){
        return FileUtils.getCacheFileDir(application).getPath() + File.separator + "attribute";
    }

    //绿幕背景切换的时候跳过的帧数
    public static final int BG_GREEN_FILTER_FRAME = 1;

    //测试用是否展示效果还原按钮
    public static final boolean IS_SHOW_RESET_BUTTON = false;
}
