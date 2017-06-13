package com.faceunity.fulivedemo;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * 本Demo展示了独立使用FU SDK API的用法.
 *
 * FU SDK和任何三方无耦合，
 * FU SDK负责范围本质上只是接收输入的图像，输出处理后的图像,
 * 如果有对接三方SDK如推流的需求，可以参考本Demo对FU SDK API的使用.
 * FU SDK不涉及视频编码，网络，使用者可以自由选择.
 *
 * 本着演示输入输出API的原则，演示Activity为FUDualInputToTextureExampleActivity和FURenderToNV21ImageExampleActivity
 *
 * Tips:
 * 有FU SDK具体问题,参考详细文档https://github.com/Faceunity/FUQiniuDemoDroid
 * 有Android Graphics,OpenGL ES及Camera问题，参考https://github.com/google/grafika
 *
 * Created by lirui on 2016/12/13.
 */

public class MainActivity extends ListActivity {

    //map keys
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String CLASS_NAME = "class_name";

    private static final String[][] EXAMPLES = {
            { "fuDualInputToTexture",
              "示例：双输入，输入摄像头nv21格式内容和surface texture，输出添加美颜和道具后的texture",
              "FUDualInputToTextureExampleActivity"},
            { "fuRenderToNV21Image",
              "示例：单输入，输入摄像头nv21格式内容，输出添加美颜和道具后的nv21 bytes和texture",
              "FURenderToNV21ImageExampleActivity"},
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = 0.7f;
        getWindow().setAttributes(params);

        setListAdapter(new SimpleAdapter(
                this,
                createActivityList(),
                android.R.layout.two_line_list_item,
                new String[] { TITLE, DESCRIPTION },
                new int[] { android.R.id.text1, android.R.id.text2 }
        ));
    }

    /**
     * Creates the list of activities from the string arrays.
     */
    private List<Map<String, Object>> createActivityList() {
        List<Map<String, Object>> testList = new ArrayList<>();

        for (String[] example : EXAMPLES) {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(TITLE, example[0]);
            tmp.put(DESCRIPTION, example[1]);
            Intent intent = new Intent();
            // Do the class name resolution here, so we crash up front rather than when the
            // activity list item is selected if the class name is wrong.
            try {
                Class cls = Class.forName("com.faceunity.fulivedemo." + example[2]);
                intent.setClass(this, cls);
                tmp.put(CLASS_NAME, intent);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Unable to find " + example[2], cnfe);
            }
            testList.add(tmp);
        }
        return testList;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //Meizu behaves wired in requestPermission, just ignore here
        boolean isMeizu = false;
        if (Build.FINGERPRINT.contains("Flyme")
                || Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("Meizu")
                || Build.MANUFACTURER.contains("MeiZu")) {
            Log.i(TAG, "the phone is meizu");
            isMeizu = true;
        }
        if (!isMeizu && (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)) {
            Log.e(TAG, "no permission");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO}, 0);
            return;
        } else {
            Log.e(TAG, "has permission or is Meizu");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>)l.getItemAtPosition(position);
        Intent intent = (Intent) map.get(CLASS_NAME);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //now i just regard it as CAMERA
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "已拥有权限，请再次点击",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "you must permit the camera permission!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }
}
