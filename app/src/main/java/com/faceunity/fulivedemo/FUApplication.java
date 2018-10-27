package com.faceunity.fulivedemo;

import android.app.Application;
import android.content.Context;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.utils.FileUtils;
import com.faceunity.fulivedemo.utils.ThreadHelper;


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

//        用于解决Uri.fromFile(file)跳转系统拍摄页面的报错
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        builder.detectFileUriExposure();

        sContext = this;
        FURenderer.initFURenderer(this);
        // 拷贝 assets 资源
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.copyAssetsTemplate(sContext);
            }
        });
    }
}
