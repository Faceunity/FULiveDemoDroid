package com.faceunity.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.faceunity.app.base.BaseActivity;
import com.faceunity.app.utils.FuDeviceUtils;

/**
 * DESC：
 * Created on 2021/3/1
 */
public class SplashActivity extends BaseActivity {
    @Override
    public int getLayoutResID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 设置为无标题(去掉Android自带的标题栏)，(全屏功能与此无关)
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置为全屏模式
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        //检查设备基本情况，并记录在app生命周期内。
        DemoConfig.DEVICE_LEVEL = FuDeviceUtils.judgeDeviceLevelGPU();
        DemoConfig.DEVICE_NAME = FuDeviceUtils.getDeviceName();
    }

    @Override
    public void initData() {
        Intent intent = new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void initView() {

    }

    @Override
    public void bindListener() {
    }
}