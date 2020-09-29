package com.faceunity.fulivedemo;

import android.app.Application;
import android.content.Context;

import com.faceunity.FURenderer;
import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.utils.FileUtils;

import java.io.File;

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
                FileUtils.copyAssetsFileToLocal(sContext, new File(sContext.getExternalFilesDir(null), "bg_seg_green"), "bg_seg_green/sample");
                FileUtils.copyAssetsChangeFaceTemplate(sContext);
            }
        });

    }
}
