package com.faceunity.app.utils.device;

import java.util.ArrayList;
import java.util.Collections;

/**
 * DESC：
 * Created on 2021/3/12
 */
public class DefaultScoreProvider implements DeviceScoreProvider {
    @Override
    public double getCpuScore(String cpuName) {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        ArrayList<Long> list = DeviceCpuUtils.getCPUFrequencies();
        Collections.sort(list);
        Collections.reverse(list);
        DeviceScoreUtils.CPUFrequencies.clear();
        for (int i = 0; i < list.size(); i++) {
            DeviceScoreUtils.CPUFrequencies.add(list.get(i) / 1000 / 1000f + "GHz");
        }
        if (cpuCores <= 4) {
            return 65;
        }
        if (list == null || list.isEmpty()) {
            return 65;
        }
        double maxFre = list.get(0) / 1000 / 1000f;
        if (maxFre < 2.2) {
            return 65;
        }
        if (maxFre <= 2.4) {
            return 70 - MathUtils.getScore(maxFre, 2.4, 5, 0.2);
        }
        if (maxFre <= 2.8) {
            return 75 - MathUtils.getScore(maxFre, 2.8, 5, 0.4);
        }
        if (maxFre < 3.2) {
            return 85 - MathUtils.getScore(maxFre, 3.2, 10, 0.4);
        }
        return 90 + (maxFre - 3.2) / 0.2;
    }

    @Override
    public double getGpuScore(String glRenderer) {
        return 65;
    }
}
