package com.faceunity.app.utils.device;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * DESC：
 * Created on 2021/3/12
 */
public class DeviceCpuUtils {
    public static ArrayList<Long> getCPUFrequencies() {
        File cpuFolder = new File("/sys/devices/system/cpu/");
        File[] cpuFiles = cpuFolder.listFiles();
        ArrayList<Long> frequencies = new ArrayList<>();
        for (File file : cpuFiles) {
            if (file.getName().startsWith("cpu")) {
                String fileName = file.getName();
                String frequency = readFrequencyFromFile(file.getAbsolutePath() + "/cpufreq/cpuinfo_max_freq");
                if (!TextUtils.isEmpty(frequency)) {
                    frequencies.add(Long.valueOf(frequency));
                }
            }
        }
        return frequencies;
    }

    private static String readFrequencyFromFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
