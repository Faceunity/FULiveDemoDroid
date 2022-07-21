package com.faceunity.app;

import android.app.Application;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.faceunity.app.utils.net.OkHttpUtils;
import com.faceunity.core.callback.OperateCallback;
import com.faceunity.core.faceunity.FURenderManager;
import com.faceunity.core.utils.FULogger;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class DemoApplication extends Application {

    public static Application mApplication;

    private static String TAG = "DemoApplication";


    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        MultiDex.install(this);
        registerFURender();
        CrashReport.initCrashReport(this, "2fe70385ed", true);
        OkHttpUtils.getInstance().init(this, BuildConfig.DEBUG);

//        DoraemonKit.install(this);
    }

    private void registerFURender() {
        FURenderManager.setKitDebug(FULogger.LogLevel.TRACE);
        FURenderManager.setCoreDebug(FULogger.LogLevel.ERROR);
        FURenderManager.registerFURender(mApplication, authpack.A(), new OperateCallback() {
            @Override
            public void onSuccess(int code, String msg) {
                Log.d(TAG, "success:" + msg);
            }

            @Override
            public void onFail(int errCode, String errMsg) {
                Log.e(TAG, "errCode:" + errCode + "   errMsg:" + errMsg);
            }
        });
    }
}
