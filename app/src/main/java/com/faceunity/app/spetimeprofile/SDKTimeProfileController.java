package com.faceunity.app.spetimeprofile;

import com.faceunity.core.utils.FULogger;
import com.faceunity.wrapper.timeprofile;

public class SDKTimeProfileController {
    private static final String TAG = "SDKTimeProfileController";
    //TimeProfile
    /**
     * 日志开始跟节点TAG标签
     */
    public static void frameTimeProfileRootStart(String tag){
        timeprofile.fuFrameTimeProfileRootStart(tag);
        FULogger.d(TAG, "frameTimeProfileRootStart   tag: " + tag);
    }

    /**
     * 日志开始子节点TAG标签
     */
    public static void frameTimeProfileStackStart(String tag){
        timeprofile.fuFrameTimeProfileStackStart(tag);
        FULogger.d(TAG, "fuFrameTimeProfileStackStart   tag: " + tag);
    }

    /**
     * 日志结束跟节点TAG标签
     */
    public static void frameTimeProfileRootStop(String tag){
        timeprofile.fuFrameTimeProfileRootStop(tag);
        FULogger.d(TAG, "fuFrameTimeProfileRootStop   tag: " + tag);
    }

    /**
     * 日志结束子节点TAG标签
     */
    public static void frameTimeProfileStackStop(String tag){
        timeprofile.fuFrameTimeProfileStackStop(tag);
        FULogger.d(TAG, "fuFrameTimeProfileStackStop   tag: " + tag);
    }

    /**
     * 统计日志开关
     */
    public static void frameTimeProfileSetEnable(int enable){
        timeprofile.fuFrameTimeProfileSetEnable(enable);
        FULogger.d(TAG, "fuFrameTimeProfileSetEnable   enable: " + enable);
    }

    /**
     * 统计日志频率
     */
    public static void frameTimeProfileSetReportInterval(int reportInterval){
        timeprofile.fuFrameTimeProfileSetReportInterval(reportInterval);
        FULogger.d(TAG, "fuFrameTimeProfileSetReportInterval   reportInterval:" + reportInterval);
    }

    /**
     * 统计日志详情
     */
    public static void frameTimeProfileSetReportDetail(int reportDetail){
        timeprofile.fuFrameTimeProfileSetReportDetail(reportDetail);
        FULogger.d(TAG, "frameTimeProfileSetReportDetail   reportDetail:" + reportDetail);
    }

    /**
     * 输出日志到控制台
     */
    public static void frameTimeProfileSetAutoReportToConsole(int toConsole){
        timeprofile.fuFrameTimeProfileSetAutoReportToConsole(toConsole);
        FULogger.d(TAG, "fuFrameTimeProfileSetAutoReportToConsole   toConsole:" + toConsole);
    }

    /**
     * 输出日志到文件
     */
    public static void frameTimeProfileSetAutoReportToFile(int toFile,String path){
        timeprofile.fuFrameTimeProfileSetAutoReportToFile(toFile,path);
        FULogger.d(TAG, "frameTimeProfileSetAutoReportToFile   toFile:" + toFile+ "path:" + path);
    }
}
