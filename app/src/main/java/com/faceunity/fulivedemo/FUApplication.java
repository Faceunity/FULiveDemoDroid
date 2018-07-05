package com.faceunity.fulivedemo;

import android.app.Application;

import com.faceunity.FURenderer;


/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        用于解决Uri.fromFile(file)跳转系统拍摄页面的报错
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//        builder.detectFileUriExposure();

        FURenderer.initFURenderer(this);
    }

}
