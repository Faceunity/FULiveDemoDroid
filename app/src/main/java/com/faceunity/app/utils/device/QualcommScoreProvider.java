package com.faceunity.app.utils.device;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;


/**
 * DESC：高通芯片
 * Created on 2021/3/12
 */
public class QualcommScoreProvider implements DeviceScoreProvider {
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
        if (maxFre < 1.9) {
            return 65;
        }
        if (maxFre <= 2.2) {
            return 70 - MathUtils.getScore(maxFre, 2.2, 5, 0.3);
        }
        if (maxFre <= 2.7) {
            return 75 - MathUtils.getScore(maxFre, 2.7, 5, 0.5);
        }
        if (maxFre <= 2.9) {
            return 80 - MathUtils.getScore(maxFre, 2.9, 5, 0.2);
        }
        if (maxFre <= 3.2) {
            return 90 - MathUtils.getScore(maxFre, 3.2, 10, 0.3);
        }
        return 95 + (maxFre - 3.3) / 0.1;
    }

    @Override
    public double getGpuScore(String glRenderer) {
        if (TextUtils.isEmpty(glRenderer) || !glRenderer.startsWith("Adreno")) {
            return 65;
        }
        int GPUVersion;
        String GPUVersionStr = glRenderer.substring(glRenderer.lastIndexOf(" ") + 1);
        try {
            GPUVersion = Integer.parseInt(GPUVersionStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //可能是后面还包含了非数字的东西，那么截取三位
            String GPUVersionStrNew = GPUVersionStr.substring(0, 3);
            GPUVersion = Integer.parseInt(GPUVersionStrNew);
        }
        if (GPUVersion < 506) {
            return 65;
        }
        if (GPUVersion <= 610) {
            return 70 - MathUtils.getScore(GPUVersion, 610, 5, 104);
        }
        if (GPUVersion <= 630) {
            return 75 - MathUtils.getScore(GPUVersion, 630, 5, 20);
        }
        if (GPUVersion <= 730) {
            return 85 - MathUtils.getScore(GPUVersion, 730, 10, 100);
        }
        if (GPUVersion < 740) {
            return 95 - MathUtils.getScore(GPUVersion, 740, 10, 20);
        }
        return 95 + (GPUVersion - 740) / 20;
    }
}
