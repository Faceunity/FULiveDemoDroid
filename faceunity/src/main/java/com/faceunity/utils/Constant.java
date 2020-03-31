package com.faceunity.utils;

import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Created by tujh on 2018/2/7.
 */
public class Constant {

    public static int NANO_IN_ONE_MILLI_SECOND = 1000000;
    public static final String APP_NAME = "FULiveDemo";
    public static final String EXTERNAL_FILE_PATH = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "FaceUnity" + File.separator + APP_NAME + File.separator;

    public static final String DCIM_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final String PHOTO_FILE_PATH;
    public static final String VIDEO_FILE_PATH;

    static {
        if (Build.FINGERPRINT.contains("Flyme")
                || Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("Meizu")
                || Build.MANUFACTURER.contains("MeiZu")) {
            PHOTO_FILE_PATH = DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
            VIDEO_FILE_PATH = DCIM_FILE_PATH + File.separator + "Video" + File.separator;
        } else if (Build.FINGERPRINT.contains("vivo")
                || Pattern.compile("vivo", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("vivo")
                || Build.MANUFACTURER.contains("Vivo")) {
            PHOTO_FILE_PATH = VIDEO_FILE_PATH = Environment.getExternalStoragePublicDirectory("") + File.separator + "相机" + File.separator;
        } else {
            VIDEO_FILE_PATH = PHOTO_FILE_PATH = DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
        }
        MiscUtil.createFile(VIDEO_FILE_PATH);
        MiscUtil.createFile(PHOTO_FILE_PATH);
    }
}
