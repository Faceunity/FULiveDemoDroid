package com.faceunity.fulivedemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.faceunity.fulivedemo.entity.PosterTemplate;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author LiuQiang on 2018.08.30
 */
public class FileUtils {
    /**
     * 海报换脸临时生成文件
     */
    public static final String TMP_PHOTO_POSTER_NAME = "photo_poster.webp";
    /**
     * 拍照后的临时保存路径，用于下一步的编辑
     */
    private static final String TMP_PHOTO_NAME = "photo.webp";

    private FileUtils() {
    }

    public static String saveTempBitmap(Bitmap bitmap, File file) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.WEBP;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(file);
            bitmap.compress(format, quality, stream);
            stream.flush();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return file.getAbsolutePath();
    }

    public static Bitmap loadTempBitmap(File file) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return BitmapFactory.decodeStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static File getSavePathFile(Context context) {
        File file = new File(getFileDir(context), TMP_PHOTO_NAME);
        return file;
    }

    public static File getSavePosterPathFile(Context context) {
        File file = new File(context.getFilesDir(), TMP_PHOTO_POSTER_NAME);
        return file;
    }

    public static String getSavePath(Context context) {
        return getSavePathFile(context).getAbsolutePath();
    }

    public static void copyFile(File src, File dest) throws IOException {
        copyFile(new FileInputStream(src), dest);
    }

    public static void copyFile(InputStream is, File dest) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        if (dest.exists()) {
            dest.delete();
        }
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            bos.write(bytes);
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
    }

    public static File getTemplatesDir(Context context) {
        File fileDir = getFileDir(context);
        File templates = new File(fileDir, "templates");
        if (!templates.exists()) {
            templates.mkdirs();
        }
        return templates;
    }

    public static File getFileDir(Context context) {
        File fileDir = context.getExternalFilesDir("");
        if (fileDir == null) {
            fileDir = context.getFilesDir();
        }
        return fileDir;
    }

    public static File getCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static void copyAssetsTemplate(Context context) {
        try {
            String[] paths = context.getAssets().list("");
            List<String> tempPaths = new ArrayList<>(16);
            for (String path : paths) {
                if (path.startsWith(PosterTemplate.DIR_PREFIX)) {
                    tempPaths.add(path);
                }
            }
            for (String tempPath : tempPaths) {
                String[] list = context.getAssets().list(tempPath);
                for (String s : list) {
                    copyTemplate(context, tempPath, tempPath.concat(File.separator).concat(s));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyTemplate(Context context, String dirPath, String assetsPath) {
        String fileName = assetsPath.substring(assetsPath.lastIndexOf("/") + 1, assetsPath.length());
        File dir = new File(FileUtils.getTemplatesDir(context), dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, fileName);
        if (!dest.exists()) {
            try {
                InputStream is = context.getAssets().open(assetsPath);
                FileUtils.copyFile(is, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
