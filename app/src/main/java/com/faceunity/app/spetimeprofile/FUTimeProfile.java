package com.faceunity.app.spetimeprofile;

public class FUTimeProfile {
    /**
     * 开始耗时统计
     */
    public static void frameTimeProfileStartDefault(){
        frameTimeProfileStart(300,false,"");
    }

    /**
     * 开始耗时统计
     */
    public static void frameTimeProfileStartToFile(String path){
        frameTimeProfileStart(300,true,path);
    }

    /**
     * 开始耗时统计
     */
    public static void frameTimeProfileStart(int time,boolean toFile,String path){
        frameTimeProfileSetEnable(true);
        frameTimeProfileSetReportInterval(time);
        if (toFile) {
            frameTimeProfileSetAutoReportToFile(path);
        } else {
            frameTimeProfileSetAutoReportToConsole();
        }
    }

    /**
     * 结束耗时统计
     */
    public static void frameTimeProfileStop(){
        frameTimeProfileSetEnable(false);
    }

    /**
     * 开始统计日志
     */
    public static void frameTimeProfileRootStart(String TAG) {
        SDKTimeProfileController.frameTimeProfileRootStart(TAG);
    }

    /**
     * 开始统计日志
     */
    public static void frameTimeProfileStackStart(String TAG) {
        SDKTimeProfileController.frameTimeProfileStackStart(TAG);
    }


    /**
     * 结束统计日志
     */
    public static void frameTimeProfileRootStop(String TAG) {
        SDKTimeProfileController.frameTimeProfileRootStop(TAG);
    }


    /**
     * 结束统计日志
     */
    public static void frameTimeProfileStackStop(String TAG) {
        SDKTimeProfileController.frameTimeProfileStackStop(TAG);
    }


    /**
     * 日志开关
     */
    public static void frameTimeProfileSetEnable(boolean enable) {
        SDKTimeProfileController.frameTimeProfileSetEnable(enable? 1 :0);
    }


    /**
     * 日志统计频率
     */
    public static void frameTimeProfileSetReportInterval(int reportInterval) {
        SDKTimeProfileController.frameTimeProfileSetReportInterval(reportInterval);
    }

    /**
     * 日志统计详情
     */
    public static void frameTimeProfileSetReportDetail(int reportDetail) {
        SDKTimeProfileController.frameTimeProfileSetReportDetail(reportDetail);
    }



    /**
     * 日志输出到控制台
     */
    public static void frameTimeProfileSetAutoReportToConsole() {
        SDKTimeProfileController.frameTimeProfileSetAutoReportToConsole(1);
    }


    /**
     * 日志输出到文件
     */
    public static void frameTimeProfileSetAutoReportToFile(String path) {
        SDKTimeProfileController.frameTimeProfileSetAutoReportToFile(1, path);
    }
}
