package com.faceunity.fulivedemo;

import android.app.Application;
import android.content.Context;

import com.faceunity.fulivedemo.utils.ThreadHelper;
import com.faceunity.greendao.GreenDaoUtils;
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
        ThreadHelper.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 拷贝 assets 资源
                FileUtils.copyAssetsLivePhoto(sContext);
                FileUtils.copyAssetsTemplate(sContext);
                // 初始化数据库，一定在拷贝文件之后
                GreenDaoUtils.initGreenDao(sContext);
            }
        });
    }
}
