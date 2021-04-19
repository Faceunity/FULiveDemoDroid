package com.faceunity.app.data.source;

import com.faceunity.app.DemoApplication;
import com.faceunity.ui.entity.PosterBean;

import java.io.File;
import java.util.ArrayList;

/**
 * DESC：海报换脸数据构造
 * Created on 2021/3/28
 */
public class PosterChangeFaceSource {

    /**
     * 构造海报换脸数据
     *
     * @return
     */
    public static ArrayList<PosterBean> buildPoster() {
        ArrayList<PosterBean> posterBeans = new ArrayList<>();
        try {
            String[] files = DemoApplication.mApplication.getAssets().list("change_face");
            for (int i = 0; i < files.length; i++) {
                String fileDir = files[i];
                if (fileDir.contains("template_")) {
                    String[] temps = DemoApplication.mApplication.getAssets().list("change_face/" + fileDir);
                    String path = "";
                    String gridIconPath = "";
                    String listIconPath = "";
                    for (int j = 0; j < temps.length; j++) {
                        String temp = temps[j];
                        if (temp.contains("grid")) {
                            gridIconPath = "file:///android_asset/change_face/" + fileDir + File.separator + temp;
                        } else if (temp.contains("list")) {
                            listIconPath = "file:///android_asset/change_face/" + fileDir + File.separator + temp;
                        } else {
                            path = "change_face/" + fileDir + File.separator + temp;
                        }
                    }
                    double intensity = fileDir.contains("template_6.png") ? 0.2 : 0.5;
                    posterBeans.add(new PosterBean(path, gridIconPath, listIconPath, intensity));
                }

            }


        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return posterBeans;
    }

}
