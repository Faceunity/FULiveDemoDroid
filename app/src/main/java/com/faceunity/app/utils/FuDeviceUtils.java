package com.faceunity.app.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLES20;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.app.DemoApplication;
import com.faceunity.core.faceunity.OffLineRenderHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;

public class FuDeviceUtils {

    public static final String TAG = "FuDeviceUtils";
    public static final String DEVICE_LEVEL = "device_level";

    public static final int DEVICE_LEVEL_HIGH = 2;
    public static final int DEVICE_LEVEL_MID = 1;
    public static final int DEVICE_LEVEL_LOW = 0;

    /**
     * The default return value of any method in this class when an
     * error occurs or when processing fails (Currently set to -1). Use this to check if
     * the information about the device in question was successfully obtained.
     */
    public static final int DEVICEINFO_UNKNOWN = -1;

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (!Character.isDigit(path.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };


    /**
     * Calculates the total RAM of the device through Android API or /proc/meminfo.
     *
     * @param c - Context object for current running activity.
     * @return Total RAM that the device has, or DEVICEINFO_UNKNOWN = -1 in the event of an error.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMemory(Context c) {
        // memInfo.totalMem not supported in pre-Jelly Bean APIs.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
            ActivityManager am = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
            am.getMemoryInfo(memInfo);
            if (memInfo != null) {
                return memInfo.totalMem;
            } else {
                return DEVICEINFO_UNKNOWN;
            }
        } else {
            long totalMem = DEVICEINFO_UNKNOWN;
            try {
                FileInputStream stream = new FileInputStream("/proc/meminfo");
                try {
                    totalMem = parseFileForValue("MemTotal", stream);
                    totalMem *= 1024;
                } finally {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return totalMem;
        }
    }

    /**
     * Method for reading the clock speed of a CPU core on the device. Will read from either
     * {@code /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq} or {@code /proc/cpuinfo}.
     *
     * @return Clock speed of a core on the device, or -1 in the event of an error.
     */
    public static int getCPUMaxFreqKHz() {
        int maxFreq = DEVICEINFO_UNKNOWN;
        try {
            for (int i = 0; i < getNumberOfCPUCores(); i++) {
                String filename =
                        "/sys/devices/system/cpu/cpu" + i + "/cpufreq/cpuinfo_max_freq";
                File cpuInfoMaxFreqFile = new File(filename);
                if (cpuInfoMaxFreqFile.exists() && cpuInfoMaxFreqFile.canRead()) {
                    byte[] buffer = new byte[128];
                    FileInputStream stream = new FileInputStream(cpuInfoMaxFreqFile);
                    try {
                        stream.read(buffer);
                        int endIndex = 0;
                        //Trim the first number out of the byte buffer.
                        while (Character.isDigit(buffer[endIndex]) && endIndex < buffer.length) {
                            endIndex++;
                        }
                        String str = new String(buffer, 0, endIndex);
                        Integer freqBound = Integer.parseInt(str);
                        if (freqBound > maxFreq) {
                            maxFreq = freqBound;
                        }
                    } catch (NumberFormatException e) {
                        //Fall through and use /proc/cpuinfo.
                    } finally {
                        stream.close();
                    }
                }
            }
            if (maxFreq == DEVICEINFO_UNKNOWN) {
                FileInputStream stream = new FileInputStream("/proc/cpuinfo");
                try {
                    int freqBound = parseFileForValue("cpu MHz", stream);
                    freqBound *= 1024; //MHz -> kHz
                    if (freqBound > maxFreq) maxFreq = freqBound;
                } finally {
                    stream.close();
                }
            }
        } catch (IOException e) {
            maxFreq = DEVICEINFO_UNKNOWN; //Fall through and return unknown.
        }
        return maxFreq;
    }

    /**
     * Reads the number of CPU cores from the first available information from
     * {@code /sys/devices/system/cpu/possible}, {@code /sys/devices/system/cpu/present},
     * then {@code /sys/devices/system/cpu/}.
     *
     * @return Number of CPU cores in the phone, or DEVICEINFO_UKNOWN = -1 in the event of an error.
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            // Gingerbread doesn't support giving a single application access to both cores, but a
            // handful of devices (Atrix 4G and Droid X2 for example) were released with a dual-core
            // chipset and Gingerbread; that can let an app in the background run without impacting
            // the foreground application. But for our purposes, it makes them single core.
            return 1;
        }
        int cores;
        try {
            cores = getCoresFromFileInfo("/sys/devices/system/cpu/possible");
            if (cores == DEVICEINFO_UNKNOWN) {
                cores = getCoresFromFileInfo("/sys/devices/system/cpu/present");
            }
            if (cores == DEVICEINFO_UNKNOWN) {
                cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
            }
        } catch (SecurityException e) {
            cores = DEVICEINFO_UNKNOWN;
        } catch (NullPointerException e) {
            cores = DEVICEINFO_UNKNOWN;
        }
        return cores;
    }

    /**
     * Tries to read file contents from the file location to determine the number of cores on device.
     *
     * @param fileLocation The location of the file with CPU information
     * @return Number of CPU cores in the phone, or DEVICEINFO_UKNOWN = -1 in the event of an error.
     */
    private static int getCoresFromFileInfo(String fileLocation) {
        InputStream is = null;
        try {
            is = new FileInputStream(fileLocation);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));
            String fileContents = buf.readLine();
            buf.close();
            return getCoresFromFileString(fileContents);
        } catch (IOException e) {
            return DEVICEINFO_UNKNOWN;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // Do nothing.
                }
            }
        }
    }

    /**
     * Converts from a CPU core information format to number of cores.
     *
     * @param str The CPU core information string, in the format of "0-N"
     * @return The number of cores represented by this string
     */
    private static int getCoresFromFileString(String str) {
        if (str == null || !str.matches("0-[\\d]+$")) {
            return DEVICEINFO_UNKNOWN;
        }
        return Integer.valueOf(str.substring(2)) + 1;
    }

    /**
     * Helper method for reading values from system files, using a minimised buffer.
     *
     * @param textToMatch - Text in the system files to read for.
     * @param stream      - FileInputStream of the system file being read from.
     * @return A numerical value following textToMatch in specified the system file.
     * -1 in the event of a failure.
     */
    private static int parseFileForValue(String textToMatch, FileInputStream stream) {
        byte[] buffer = new byte[1024];
        try {
            int length = stream.read(buffer);
            for (int i = 0; i < length; i++) {
                if (buffer[i] == '\n' || i == 0) {
                    if (buffer[i] == '\n') i++;
                    for (int j = i; j < length; j++) {
                        int textIndex = j - i;
                        //Text doesn't match query at some point.
                        if (buffer[j] != textToMatch.charAt(textIndex)) {
                            break;
                        }
                        //Text matches query here.
                        if (textIndex == textToMatch.length() - 1) {
                            return extractValue(buffer, j);
                        }
                    }
                }
            }
        } catch (IOException e) {
            //Ignore any exceptions and fall through to return unknown value.
        } catch (NumberFormatException e) {
        }
        return DEVICEINFO_UNKNOWN;
    }

    /**
     * Helper method used by {@link #parseFileForValue(String, FileInputStream) parseFileForValue}. Parses
     * the next available number after the match in the file being read and returns it as an integer.
     *
     * @param index - The index in the buffer array to begin looking.
     * @return The next number on that line in the buffer, returned as an int. Returns
     * DEVICEINFO_UNKNOWN = -1 in the event that no more numbers exist on the same line.
     */
    private static int extractValue(byte[] buffer, int index) {
        while (index < buffer.length && buffer[index] != '\n') {
            if (Character.isDigit(buffer[index])) {
                int start = index;
                index++;
                while (index < buffer.length && Character.isDigit(buffer[index])) {
                    index++;
                }
                String str = new String(buffer, 0, start, index - start);
                return Integer.parseInt(str);
            }
            index++;
        }
        return DEVICEINFO_UNKNOWN;
    }

    /**
     * 获取当前剩余内存(ram)
     *
     * @param context
     * @return
     */
    public static long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem;
    }

    /**
     * 获取厂商信息
     *
     * @return
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 获取手机机型
     *
     * @return
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * 获取硬件信息(cpu型号)
     *
     * @return
     */
    public static String getHardWare() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text;
            String last = "";
            while ((text = br.readLine()) != null) {
                last = text;
            }
            //一般机型的cpu型号都会在cpuinfo文件的最后一行
            if (last.contains("Hardware")) {
                String[] hardWare = last.split(":\\s+", 2);
                return hardWare[1];
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Build.HARDWARE;
    }

    /**
     * Level judgement based on current memory and CPU.
     *
     * @param context - Context object.
     * @return
     */
    public static int judgeDeviceLevel(Context context) {
        //加一个sp读取 和 设置
        int cacheDeviceLevel = getCacheDeviceLevel();
        if (cacheDeviceLevel > -1) {
            return cacheDeviceLevel;
        }

        int level;
        //有一些设备不符合下述的判断规则，则走一个机型判断模式
        int specialDevice = judgeDeviceLevelInDeviceName();
        if (specialDevice >= 0) return specialDevice;

        int ramLevel = judgeMemory(context);
        int cpuLevel = judgeCPU();
        if (ramLevel == 0 || ramLevel == 1 || cpuLevel == 0) {
            level = DEVICE_LEVEL_LOW;
        } else {
            if (cpuLevel > 1) {
                level = DEVICE_LEVEL_HIGH;
            } else {
                level = DEVICE_LEVEL_MID;
            }
        }
        Log.d(TAG,"DeviceLevel: " + level);
        saveCacheDeviceLevel(level);
        return level;
    }

    /**
     * Level judgement based on current GPU.
     * 需要GL环境
     * @return
     */
    public static int judgeDeviceLevelGPU() {
        int cacheDeviceLevel = getCacheDeviceLevel();
        if (cacheDeviceLevel > -1) {
            return cacheDeviceLevel;
        }

        OffLineRenderHandler.getInstance().onResume();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //加一个sp读取 和 设置
        final int[] level = {-1};
        //有一些设备不符合下述的判断规则，则走一个机型判断模式
        OffLineRenderHandler.getInstance().queueEvent(() -> {
            try {
                //高低端名单
                int specialDevice = judgeDeviceLevelInDeviceName();
                level[0] = specialDevice;
                if (specialDevice >= 0) return;

                String glRenderer = GLES20.glGetString(GLES20.GL_RENDERER);      //GPU 渲染器
                String glVendor = GLES20.glGetString(GLES20.GL_VENDOR);          //GPU 供应商
                int GPUVersion;
                if ("Qualcomm".equals(glVendor)) {
                    //高通
                    if (glRenderer != null && glRenderer.startsWith("Adreno")) {
                        //截取后面的数字
                        String GPUVersionStr = glRenderer.substring(glRenderer.lastIndexOf(" ") + 1);
                        try {
                            GPUVersion = Integer.parseInt(GPUVersionStr);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            //可能是后面还包含了非数字的东西，那么截取三位
                            String GPUVersionStrNew = GPUVersionStr.substring(0,3);
                            GPUVersion = Integer.parseInt(GPUVersionStrNew);
                        }

                        if (GPUVersion >= 512) {
                            level[0] = DEVICE_LEVEL_HIGH;
                        } else {
                            level[0] = DEVICE_LEVEL_MID;
                        }
                        countDownLatch.countDown();
                    }
                } else if ("ARM".equals(glVendor)) {
                    //ARM
                    if (glRenderer != null && glRenderer.startsWith("Mali")) {
                        //截取-后面的东西
                        String GPUVersionStr = glRenderer.substring(glRenderer.lastIndexOf("-") + 1);
                        String strStart = GPUVersionStr.substring(0, 1);
                        String strEnd = GPUVersionStr.substring(1);
                        try {
                            GPUVersion = Integer.parseInt(strEnd);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            //可能是后面还包含了非数字的东西，那么截取三位
                            String strEndNew = strEnd.substring(0,2);
                            GPUVersion = Integer.parseInt(strEndNew);
                        }

                        if ("G".equals(strStart)) {
                            if (GPUVersion >= 51) {
                                level[0] = DEVICE_LEVEL_HIGH;
                            } else {
                                level[0] = DEVICE_LEVEL_MID;
                            }
                        } else if ("T".equals(strStart)) {
                            if (GPUVersion > 880) {
                                level[0] = DEVICE_LEVEL_HIGH;
                            } else {
                                level[0] = DEVICE_LEVEL_MID;
                            }
                        }
                        countDownLatch.countDown();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                level[0] = -1;
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await(200,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OffLineRenderHandler.getInstance().onPause();

        //存储设备等级
        saveCacheDeviceLevel(level[0]);
        Log.d(TAG,"DeviceLevel: " + level[0]);
        return level[0];
    }

    /**
     * -1 不是特定的高低端机型
     * @return
     */
    private static int judgeDeviceLevelInDeviceName() {
        String currentDeviceName = getDeviceName();
        for (String deviceName:upscaleDevice) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_HIGH;
            }
        }

        for (String deviceName:middleDevice) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_MID;
            }
        }

        for (String deviceName:lowDevice) {
            if (deviceName.equals(currentDeviceName)) {
                return DEVICE_LEVEL_LOW;
            }
        }
        return -1;
    }

    public static final String[] upscaleDevice = {"MHA-AL00","VKY-AL00","V1838A","EVA-AL00"};
    public static final String[] lowDevice = {};
    public static final String[] middleDevice = {"PRO 6","PRO 7 Plus","V2002A","Pixel"};

    /**
     * 评定内存的等级.
     *
     * @return
     */
    private static int judgeMemory(Context context) {
        long ramMB = getTotalMemory(context) / (1024 * 1024);
        int level = -1;
        if (ramMB <= 2000) { //2G或以下的最低档
            level = 0;
        } else if (ramMB <= 3000) { //2-3G
            level = 1;
        } else if (ramMB <= 4000) { //4G档 2018主流中端机
            level = 2;
        } else if (ramMB <= 6000) { //6G档 高端机
            level = 3;
        } else { //6G以上 旗舰机配置
            level = 4;
        }
        return level;
    }

    /**
     * 评定CPU等级.（按频率和厂商型号综合判断）
     *
     * @return
     */
    private static int judgeCPU() {
        int level = 0;
        String cpuName = getHardWare();
        int freqMHz = getCPUMaxFreqKHz() / 1024;

        //一个不符合下述规律的高级白名单
        //如果可以获取到CPU型号名称 -> 根据不同的名称走不同判定策略
        if (!TextUtils.isEmpty(cpuName)) {
            if (cpuName.contains("qcom") || cpuName.contains("Qualcomm")) { //高通骁龙
                return judgeQualcommCPU(cpuName, freqMHz);
            } else if (cpuName.contains("hi") || cpuName.contains("kirin")) { //海思麒麟
                return judgeSkinCPU(cpuName, freqMHz);
            } else if (cpuName.contains("MT")) {//联发科
                return judgeMTCPU(cpuName, freqMHz);
            }
        }

        //cpu型号无法获取的普通规则
        if (freqMHz <= 1600) { //1.5G 低端
            level = 0;
        } else if (freqMHz <= 1950) { //2GHz 低中端
            level = 1;
        } else if (freqMHz <= 2500) { //2.2 2.3g 中高端
            level = 2;
        } else { //高端
            level = 3;
        }
        return level;
    }

    /**
     * 联发科芯片等级判定
     *
     * @return
     */
    private static int judgeMTCPU(String cpuName, int freqMHz) {
        //P60之前的全是低端机 MT6771V/C
        int level = 0;
        int mtCPUVersion = getMTCPUVersion(cpuName);
        if (mtCPUVersion == -1) {
            //读取不出version 按照一个比较严格的方式来筛选出高端机
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else if (freqMHz <= 2200) { //2GHz 低中端
                level = 1;
            } else if (freqMHz <= 2700) { //2.2 2.3g 中高端
                level = 2;
            } else { //高端
                level = 3;
            }
        } else if (mtCPUVersion < 6771) {
            //均为中低端机
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else { //2GHz 中端
                level = 1;
            }
        } else {
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else if (freqMHz <= 1900) { //2GHz 低中端
                level = 1;
            } else if (freqMHz <= 2500) { //2.2 2.3g 中高端
                level = 2;
            } else { //高端
                level = 3;
            }
        }

        return level;
    }

    /**
     * 通过联发科CPU型号定义 -> 获取cpu version
     *
     * @param cpuName
     * @return
     */
    private static int getMTCPUVersion(String cpuName) {
        //截取MT后面的四位数字
        int cpuVersion = -1;
        if (cpuName.length() > 5) {
            String cpuVersionStr = cpuName.substring(2, 6);
            try {
                cpuVersion = Integer.valueOf(cpuVersionStr);
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
            }
        }

        return cpuVersion;
    }

    /**
     * 高通骁龙芯片等级判定
     *
     * @return
     */
    private static int judgeQualcommCPU(String cpuName, int freqMHz) {
        int level = 0;
        //xxxx inc MSM8937 比较老的芯片
        //7 8 xxx inc SDM710
        if (cpuName.contains("MSM")) {
            //老芯片
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else { //2GHz 低中端
                level = 1;
            }
        } else {
            //新的芯片
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else if (freqMHz <= 2000) { //2GHz 低中端
                level = 1;
            } else if (freqMHz <= 2500) { //2.2 2.3g 中高端
                level = 2;
            } else { //高端
                level = 3;
            }
        }

        return level;
    }

    /**
     * 麒麟芯片等级判定
     *
     * @param freqMHz
     * @return
     */
    private static int judgeSkinCPU(String cpuName, int freqMHz) {
        //型号 -> kirin710之后 & 最高核心频率
        int level = 0;
        if (cpuName.startsWith("hi")) {
            //这个是海思的芯片中低端
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else if (freqMHz <= 2000) { //2GHz 低中端
                level = 1;
            }
        } else {
            //这个是海思麒麟的芯片
            if (freqMHz <= 1600) { //1.5G 低端
                level = 0;
            } else if (freqMHz <= 2000) { //2GHz 低中端
                level = 1;
            } else if (freqMHz <= 2500) { //2.2 2.3g 中高端
                level = 2;
            } else { //高端
                level = 3;
            }
        }

        return level;
    }

    public static final String Nexus_6P = "Nexus 6P";

    /**
     * 获取设备名
     *
     * @return
     */
    public static String getDeviceName() {
        String deviceName = "";
        if (Build.MODEL != null) deviceName = Build.MODEL;
        Log.d(TAG,"deviceName: " + deviceName);
        return deviceName;
    }

    /**
     * 缓存设备等级
     *
     * @param level
     */
    public static void saveCacheDeviceLevel(int level) {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(DEVICE_LEVEL, MODE_PRIVATE);
        sp.edit().putInt(DEVICE_LEVEL, level).apply();
    }

    /**
     * 获取设备等级
     *
     * @return
     */
    public static int getCacheDeviceLevel() {
        SharedPreferences sp = DemoApplication.mApplication.getSharedPreferences(DEVICE_LEVEL, MODE_PRIVATE);
        return sp.getInt(DEVICE_LEVEL, -1);
    }
}
