package com.faceunity.fulivedemo.entity;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.faceunity.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 海报换脸，模板
 *
 * @author Richie on 2018.10.09
 */
public class PosterChangeFaceTemplate implements Comparable<PosterChangeFaceTemplate> {
    private static final String GRID_ITEM = "grid";
    private static final String LIST_ITEM = "list";
    private String path;
    private String description;
    private String gridIconPath;
    private String listIconPath;

    public static int findSelectedIndex(List<PosterChangeFaceTemplate> templates, String path) {
        for (int i = 0, j = templates.size(); i < j; i++) {
            if (TextUtils.equals(templates.get(i).path, path)) {
                return i;
            }
        }
        return -1;
    }

    public static List<PosterChangeFaceTemplate> getPosterTemplates(Context context) {
        List<PosterChangeFaceTemplate> templates = new ArrayList<>();
        File templatesDir = FileUtils.getChangeFaceTemplatesDir(context);
        File[] dirFiles = templatesDir.listFiles();
        PosterChangeFaceTemplate posterTemplate;
        for (File f : dirFiles) {
            if (!f.getName().startsWith(FileUtils.TEMPLATE_PREFIX)) {
                continue;
            }
            File[] tempFiles = f.listFiles();
            posterTemplate = new PosterChangeFaceTemplate();
            for (File tempFile : tempFiles) {
                String path = tempFile.getAbsolutePath();
                if (path.contains(GRID_ITEM)) {
                    posterTemplate.gridIconPath = path;
                } else if (path.contains(LIST_ITEM)) {
                    posterTemplate.listIconPath = path;
                } else {
                    posterTemplate.path = path;
                }
            }
            templates.add(posterTemplate);
        }
        Collections.sort(templates);
        return templates;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGridIconPath() {
        return gridIconPath;
    }

    public void setGridIconPath(String gridIconPath) {
        this.gridIconPath = gridIconPath;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getListIconPath() {
        return listIconPath;
    }

    public void setListIconPath(String listIconPath) {
        this.listIconPath = listIconPath;
    }

    @Override
    public String toString() {
        return "PosterChangeFaceTemplate{" +
                "gridIconPath=" + gridIconPath +
                ", listIconPath=" + listIconPath +
                ", path='" + path + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull PosterChangeFaceTemplate o) {
        return this.path.compareTo(o.path);
    }
}
