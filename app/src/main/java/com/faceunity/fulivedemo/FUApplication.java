package com.faceunity.fulivedemo;

import android.app.Application;
import android.content.Context;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.utils.LogUtils;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.utils.FileUtils;

/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {
    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        FURenderer.initFURenderer(FUApplication.this);
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 异步拷贝 assets 资源
                FileUtils.copyAssetsChangeFaceTemplate(sContext);
            }
        });

        LogUtils.config(this);
        LogUtils.i("************* device info *************\n"
                + LogUtils.retrieveDeviceInfo(this));
    }
}
