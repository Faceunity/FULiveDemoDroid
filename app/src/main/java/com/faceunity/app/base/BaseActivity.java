package com.faceunity.app.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
}
