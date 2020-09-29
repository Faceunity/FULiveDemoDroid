package com.faceunity.fulivedemo.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author Richie on 2020.05.06
 */
public final class PermissionUtil {

    public static void checkPermissions(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // 厂商增加的弹窗权限，在首次启动时就授权
            if ("vivo".equalsIgnoreCase(Build.BRAND)) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
                String firstRun = "first_run";
                boolean isFirstRun = sharedPreferences.getBoolean(firstRun, true);
                if (isFirstRun) {
                    sharedPreferences.edit().putBoolean(firstRun, false).apply();
                    Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    camera.release();
                }
            }
        } else {
            checkPermission(context);
        }
    }

    public static void checkPermission(Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

}
