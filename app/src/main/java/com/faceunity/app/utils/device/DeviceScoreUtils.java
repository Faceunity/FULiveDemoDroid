package com.faceunity.app.utils.device;

import android.opengl.GLES20;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.faceunity.core.faceunity.OffLineRenderHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author changwei on 2023/11/8 15:00
 */
public class DeviceScoreUtils {

    public static final String TAG = "DeviceLevelUtils";

    public static final double CPU_PROPORTION = 0.6;
    public static final double GPU_PROPORTION = 0.4;
    public static final double MEM_PROPORTION = 0.1;

    public static ArrayList<String> CPUFrequencies = new ArrayList<>();
    public static String CPUName;
    public static String GPUName;
    public static double CPUScore;
    public static double GPUScore;


    public static double getDeviceScore() {
        return getCpuScore() * CPU_PROPORTION + getGpuScore() * GPU_PROPORTION;
    }

    public static double getCpuScore() {
        String cpuName = getCpuName();
        CPUName = cpuName;
        DeviceScoreProvider provider = new DefaultScoreProvider();
        if (!TextUtils.isEmpty(cpuName)) {
            //高通骁龙
            if (cpuName.contains("qcom") || cpuName.contains("Qualcomm")) {
                provider = new QualcommScoreProvider();
                //联发科
            } else if (cpuName.contains("MT")) {
                provider = new MTKScoreProvider();
            }
        }
        CPUScore = provider.getCpuScore(cpuName);
        return CPUScore;
    }

    public static double getGpuScore() {
        OffLineRenderHandler.getInstance().onResume();
        final double[] score = new double[1];
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //有一些设备不符合下述的判断规则，则走一个机型判断模式
        OffLineRenderHandler.getInstance().queueEvent(() -> {
            DeviceScoreProvider provider;
            String glRenderer = GLES20.glGetString(GLES20.GL_RENDERER);      //GPU 渲染器
            String glVendor = GLES20.glGetString(GLES20.GL_VENDOR);          //GPU 供应商
            GPUName = glRenderer;
            Log.d(TAG, "glRenderer: " + glRenderer + ",glVendor: " + glVendor);
            countDownLatch.countDown();
            switch (glVendor) {
                case "Qualcomm":
                    provider = new QualcommScoreProvider();
                    break;
                case "ARM":
                    provider = new MTKScoreProvider();
                    break;
                default:
                    provider = new DefaultScoreProvider();
                    break;
            }
            score[0] = provider.getGpuScore(glRenderer);
        });
        try {
            countDownLatch.await(200, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OffLineRenderHandler.getInstance().onPause();
        GPUScore = score[0];
        return score[0];
    }

    public static double getMemScore() {
        long ramMemory = getTotalRam();
        if (ramMemory <= 4) {
            return 65;
        }
        if (ramMemory <= 6) {
            return 75 - MathUtils.getScore(ramMemory, 6, 10, 2);
        }
        if (ramMemory <= 8) {
            return 85 - MathUtils.getScore(ramMemory, 8, 5, 2);
        }
        if (ramMemory <= 12) {
            return 90 - MathUtils.getScore(ramMemory, 12, 5, 4);
        }
        if (ramMemory <= 16) {
            return 95 - MathUtils.getScore(ramMemory, 16, 5, 4);
        }
        return 95 + (ramMemory - 16) / 2;
    }


    /**
     * 获取硬件信息(cpu型号)
     *
     * @return String
     */
    public static String getCpuName() {
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

    public static long getTotalRam() {
        String path = "/proc/meminfo";
        String ramMemorySize = null;
        int totalRam = 0;
        try {
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader, 4096);
            ramMemorySize = br.readLine().split("\\s+")[1];
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ramMemorySize != null) {
            totalRam = (int) Math.ceil((Float.valueOf(Float.parseFloat(ramMemorySize) / (1024 * 1024)).doubleValue()));
        }

        return totalRam;
    }
}
