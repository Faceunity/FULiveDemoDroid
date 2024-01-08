package com.faceunity.app.utils.device;

/**
 * DESC：
 * Created on 2021/3/12
 */
interface DeviceScoreProvider {
    /**
     * 获取CPU分数
     *
     * @param cpuName String
     * @return double
     */
    double getCpuScore(String cpuName);

    /**
     * 获取GPU分数
     *
     * @param glRenderer String GPU名称
     * @return double
     */
    double getGpuScore(String glRenderer);
}
