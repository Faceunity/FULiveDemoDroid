package com.faceunity.app.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.faceunity.app.DemoApplication;
import com.faceunity.app.DemoConfig;
import com.faceunity.app.utils.device.DeviceScoreUtils;
import com.faceunity.core.utils.FULogger;
import com.faceunity.core.utils.FileUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class FuDeviceUtils {

    public static final String TAG = "FuDeviceUtils";

    public static final String DEVICE_LEVEL = "device_level";

    public static final int DEVICE_LEVEL_FOUR = 4;
    public static final int DEVICE_LEVEL_THREE = 3;

    public static final int DEVICE_LEVEL_TWO = 2;
    public static final int DEVICE_LEVEL_ONE = 1;
    public static final int DEVICE_LEVEL_MINUS_ONE = -1;
    public static final int DEVICE_LEVEL_EMPTY = -100;

    public static final double DEVICE_SCORE_FOUR = 85.0;
    public static final double DEVICE_SCORE_THREE = 78.0;
    public static final double DEVICE_SCORE_TWO = 69.0;
    public static final double DEVICE_SCORE_ONE = 64.0;

    public static final String[] levelFourDevices = {};
    public static final String[] levelThreeDevices = {};
    public static final String[] levelTwoDevices = {};
    public static final String[] levelOneDevices = {"PRO 7 Plus"};
    public static final String Nexus_6P = "Nexus 6P";


    /**
     * 判断当前功能，机型是否在黑名单
     *
     * @param function String
     * @return boolean
     */
    public static boolean judgeFunctionInBlackList(String function) {
        HashMap<String, ArrayList<String>> map = getBlackListMap();
        if (map == null || map.size() == 0) {
            return false;
        }
        ArrayList<String> list = map.get(function);
        if (list == null || list.size() == 0) {
            return false;
        }
        return list.contains(getDeviceName());
    }

    public static HashMap<String, ArrayList<String>> getBlackListMap() {
        String blackList = FileUtils.loadStringFromLocal(DemoApplication.mApplication, DemoConfig.BLACK_LIST);
        Gson gson = new Gson();
        return gson.fromJson(blackList, HashMap.class);
    }


    public static int judgeDeviceLevel() {
        return judgeDeviceLevel(false);
    }

    /**
     * 判断设备级别
     *
     * @return int
     */
    public static int judgeDeviceLevel(boolean ignoreCache) {
        if (!ignoreCache) {
            int cacheDeviceLevel = getCacheDeviceLevel();
            if (cacheDeviceLevel > DEVICE_LEVEL_EMPTY) {
                return cacheDeviceLevel;
            }
        }
        //有一些设备不符合下述的判断规则，则走一个机型判断模式
        int specialDevice = judgeDeviceLevelInDeviceName();
        if (specialDevice > DEVICE_LEVEL_EMPTY) {
            return specialDevice;
        }
        double score = DeviceScoreUtils.getDeviceScore();
        int level = DEVICE_LEVEL_MINUS_ONE;
        if (score > DEVICE_SCORE_ONE) {
            level = DEVICE_LEVEL_ONE;
        }
        if (score > DEVICE_SCORE_TWO) {
            level = DEVICE_LEVEL_TWO;
        }
        if (score > DEVICE_SCORE_THREE) {
            level = DEVICE_LEVEL_THREE;
        }
        if (score > DEVICE_SCORE_FOUR) {
            level = DEVICE_LEVEL_FOUR;
        }
        saveCacheDeviceLevel(level);
        FULogger.d(TAG, "CPUName: " + DeviceScoreUtils.CPUName + " GPUName: " + DeviceScoreUtils.GPUName + " score: " + score + " level: " + level);
        return level;
    }

    /**
     * -1 不是特定的高低端机型
     *
     * @return
     */
    private static int judgeDeviceLevelInDeviceName() {
        String currentDeviceName = getDeviceName();
        for (String deviceName : levelFourDevices) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_FOUR;
            }
        }
        for (String deviceName : levelThreeDevices) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_THREE;
            }
        }
        for (String deviceName : levelTwoDevices) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_TWO;
            }
        }
        for (String deviceName : levelOneDevices) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_ONE;
            }
        }
        return DEVICE_LEVEL_EMPTY;
    }

    /**
     * 获取设备名
     *
     * @return String
     */
    public static String getDeviceName() {
        String deviceName = "";
        if (Build.MODEL != null) {
            deviceName = Build.MODEL;
        }
        Log.d(TAG, "deviceName: " + deviceName);
        return deviceName;
    }

    /**
     * 缓存设备等级
     *
     * @param level int
     */
    public static void saveCacheDeviceLevel(int level) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(DEVICE_LEVEL, MODE_PRIVATE);
        sp.edit().putInt(DEVICE_LEVEL, level).apply();
    }

    /**
     * 获取设备等级
     *
     * @return int
     */
    public static int getCacheDeviceLevel() {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(DEVICE_LEVEL, MODE_PRIVATE);
        return sp.getInt(DEVICE_LEVEL, DEVICE_LEVEL_EMPTY);
    }
}
