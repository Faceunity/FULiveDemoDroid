package com.faceunity.app.utils.device;

import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * DESC： 联发科芯片
 * Created on 2021/3/12
 */
public class MTKScoreProvider implements DeviceScoreProvider {

    public static final String[] badMTKGPUDevices = {"Mali-T830","Mali-G51"};
    @Override
    public double getCpuScore(String cpuName) {
        DeviceScoreUtils.CPUFrequencies.clear();
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ArrayList<Long> list = DeviceCpuUtils.getCPUFrequencies();
        if (list.isEmpty()) {
            return 65;
        }
        Collections.sort(list);
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            DeviceScoreUtils.CPUFrequencies.add(list.get(i) / 1000 / 1000f + "GHz");
        }
        double maxFre = list.get(0) / 1000 / 1000f;
        if (cpuCores <= 4) {
            return 65;
        }
        if (!TextUtils.isEmpty(cpuName) && cpuName.startsWith("MT") && cpuName.length() > 7) {
            long version = Long.valueOf(cpuName.substring(2, 6));
            if (version < 6797) {
                return 65;
            }
            if (version <= 6833) {
                return 70 - MathUtils.getScore(version, 6833, 5, 36);
            }
            if (version <= 6895) {
                return 75 - MathUtils.getScore(version, 6895, 5, 60);
            }
            if (version <= 6983) {
                return 80 - MathUtils.getScore(version, 6983, 5, 88);
            }
            if (version < 6985) {
                return 85 - MathUtils.getScore(version, 6985, 5, 2);
            }
            if (version >= 6985) {
                return 90 + (version - 6985) / 3;
            }
        } else {
            if (maxFre < 2.2) {
                return 65;
            }
            if (maxFre <= 2.9) {
                return 75 - MathUtils.getScore(maxFre, 2.9, 10, 0.7);
            }
            if (maxFre < 3.3) {
                return 85 - MathUtils.getScore(maxFre, 3.3, 10, 0.4);
            }
            return 90 + (maxFre - 3.3) / 0.2;
        }
        return 65;
    }

    @Override
    public double getGpuScore(String glRenderer) {
        if (TextUtils.isEmpty(glRenderer)) {
            return 65;
        }
        // 处理个别低端设备
        for (String badDevice : badMTKGPUDevices){
            if (glRenderer.startsWith(badDevice)){
                return 55;
            }
        }
        if (glRenderer.startsWith("Mali")) {
            glRenderer = glRenderer.split(" ")[0];
            String GPUVersionStr = glRenderer.substring(glRenderer.indexOf("-") + 1);
            String strStart = GPUVersionStr.substring(0, 1);
            Integer version = Integer.valueOf(GPUVersionStr.substring(1));
            if (strStart.equals("T")) {
                return 65;
            }
            if (strStart.equals("G")) {
                if (version >= 700 && version < 800) {
                    return 85;
                }
                if (version >= 600 && version < 700) {
                    return 80;
                }
                if (version < 50) {
                    return 65;
                }
                if (version < 60) {
                    return 66 + (version - 50);
                }
                if (version < 70) {
                    return 67 + (version - 60);
                }
                if (version < 80) {
                    return 68 + (version - 70) * 1.5;
                }
            }
        }
        if (glRenderer.startsWith("PowerVR")) {
            return 70;
        }
        if (glRenderer.startsWith("Immortalis")) {
            return 85;
        }
        return 65;
    }

    private boolean isARMv7() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (String abi : Build.SUPPORTED_ABIS) {
                if (abi.equals("armeabi-v7a")) {
                    return true;
                }
            }
        } else {
            // For devices running below API Level 21
            return Build.CPU_ABI.equals("armeabi-v7a") || Build.CPU_ABI2.equals("armeabi-v7a");
        }
        return false;
    }
}
