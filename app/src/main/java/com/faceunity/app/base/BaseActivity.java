package com.faceunity.app.base;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

    /**
     * 检查是否有劝降没有通过 true均有权限 false 无权限
     * @return
     */
    public boolean checkSelfPermission(String[] permissions){
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 10001);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Boolean hasPermissionDismiss = false; //有权限没有通过
        for (int element : grantResults) {
            if (element == -1) {
                hasPermissionDismiss = true;
            }
        }
        //如果有权限没有被允许
        if (hasPermissionDismiss) {
            checkPermissionResult(false);
            ToastHelper.showNormalToast(this, "缺少必要权限，可能导致应用功能无法使用");
        } else {
            checkPermissionResult(true);
        }
    }

    public void checkPermissionResult(boolean permissionResult) {

    }
}
