package com.faceunity.app.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.faceunity.app.DemoConfig;
import com.faceunity.app.utils.FuDeviceUtils;
import com.faceunity.ui.dialog.ToastHelper;

/**
 * DESC：
 * Created on 2021/4/12
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResID() > 0) {
            setContentView(getLayoutResID());
        }
        //检查设备基本情况，并记录在app生命周期内。
        DemoConfig.DEVICE_LEVEL = FuDeviceUtils.judgeDeviceLevel(BaseActivity.this);
        DemoConfig.DEVICE_NAME = FuDeviceUtils.getDeviceName();
        initData();
        initView();
        bindListener();
    }

    public abstract int getLayoutResID();

    public abstract void initData();

    public abstract void initView();

    public abstract void bindListener();

    @Override
    protected void onPause() {
        super.onPause();
        ToastHelper.dismissToast();
    }

    /**
     * 显示提示描述
     */
    public void showToast(String msg) {
        ToastHelper.showNormalToast(this, msg);
    }

    /**
     * 显示提示描述
     */
    public void showToast(int res) {
        ToastHelper.showWhiteTextToast(this, res);
    }
}
