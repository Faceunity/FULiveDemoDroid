package com.faceunity.fulivedemo;

import android.app.Application;

import com.faceunity.fulivedemo.core.FURenderer;

/**
 * Created by tujh on 2018/3/30.
 */
public class FUApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FURenderer.initFURenderer(this);
    }

}
