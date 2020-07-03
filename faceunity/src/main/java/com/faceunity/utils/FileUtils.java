package com.faceunity.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Richie on 2018.08.30
 */
public class FileUtils {
    /**
     * 拍照后的临时保存路径，用于下一步的编辑
     */
    private static final String TMP_PHOTO_NAME = "photo.jpg";
    /**
     * 海报换脸模板文件的文件夹
     */
    public static final String TEMPLATE_PREFIX = "template_";
    private static final String TAG = "FileUtils";

    private FileUtils() {
    }

    public static String saveTempBitmap(Bitmap bitmap, File file) throws IOException {
        if (file.exists()) {
            file.delete();
        }
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        try (FileOutputStream stream = new FileOutputStream(file)) {
            bitmap.compress(format, quality, stream);
            stream.flush();
        }
        return file.getAbsolutePath();
    }

    public static File getSavePathFile(Context context) {
        return new File(getExternalFileDir(context), TMP_PHOTO_NAME);
    }

    public static String getSavePath(Context context) {
        return getSavePathFile(context).getAbsolutePath();
    }

    public static void copyFile(File src, File dest) throws IOException {
        copyFile(new FileInputStream(src), dest);
    }

    public static void copyFile(InputStream is, File dest) throws IOException {
        if (is == null) {
            return;
        }
        if (dest.exists()) {
            dest.delete();
        }
        try (BufferedInputStream bis = new BufferedInputStream(is); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] bytes = new byte[1024 * 10];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            bos.flush();
        }
    }

    /**
     * 海报换脸的素材存储目录
     *
     * @param context
     * @return
     */
    public static File getChangeFaceTemplatesDir(Context context) {
        File fileDir = getExternalFileDir(context);
        File templates = new File(fileDir, "change_face");
        if (!templates.exists()) {
            boolean b = templates.mkdirs();
            if (!b) {
                return fileDir;
            }
        }
        return templates;
    }

    /**
     * 应用外部文件目录
     *
     * @param context
     * @return
     */
    public static File getExternalFileDir(Context context) {
        File fileDir = context.getExternalFilesDir(null);
        if (fileDir == null) {
            fileDir = context.getFilesDir();
        }
        return fileDir;
    }

    /**
     * 应用外部的缓存目录
     *
     * @param context
     * @return
     */
    public static File getExternalCacheDir(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    public static File getThumbnailDir(Context context) {
        File fileDir = getExternalFileDir(context);
        File thumbDir = new File(fileDir, "thumb");
        if (!thumbDir.exists()) {
            thumbDir.mkdirs();
        }
        return thumbDir;
    }

    /**
     * 生成唯一标示
     *
     * @return
     */
    public static String getUUID32() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    /**
     * 计算文件的 MD5
     *
     * @param file
     * @return
     */
    public static String getMd5ByFile(File file) throws Exception {
        try (FileInputStream in = new FileInputStream(file)) {
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        }
    }

    public static String readStringFromAssetsFile(Context context, String path) throws IOException {
        try (InputStream is = context.getAssets().open(path)) {
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes);
        }
    }

    public static void copyAssetsChangeFaceTemplate(Context context) {
        try {
            AssetManager assets = context.getAssets();
            String baseDirPath = "change_face";
            String[] paths = assets.list(baseDirPath);
            List<String> tempPaths = new ArrayList<>(16);
            for (String path : paths) {
                if (path.startsWith(TEMPLATE_PREFIX)) {
                    tempPaths.add(path);
                }
            }
            for (String tempPath : tempPaths) {
                String path = baseDirPath + File.separator + tempPath;
                String[] list = assets.list(path);
                for (String s : list) {
                    File dir = new File(FileUtils.getChangeFaceTemplatesDir(context), tempPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    copyAssetsFile(context, dir, path + File.separator + s);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "copyAssetsChangeFaceTemplate: ", e);
        }
    }

    private static void copyAssetsFile(Context context, File dir, String assetsPath) {
        String fileName = assetsPath.substring(assetsPath.lastIndexOf("/") + 1);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File dest = new File(dir, fileName);
        if (!dest.exists()) {
            try {
                InputStream is = context.getAssets().open(assetsPath);
                FileUtils.copyFile(is, dest);
            } catch (IOException e) {
                Log.e(TAG, "copyAssetsFile: ", e);
            }
        }
    }

    public static String readStringFromFile(File file) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[bis.available()];
            bis.read(bytes);
            return new String(bytes);
        }
    }

    /**
     * 把外部文件拷贝到应用私有目录
     *
     * @param srcFile
     * @param destDir
     * @return
     * @throws IOException
     */
    public static File copyExternalFileToLocal(File srcFile, File destDir) throws IOException {
        if (!srcFile.exists()) {
            throw new IOException("Source file don't exits");
        }
        if (!destDir.exists()) {
            boolean b = destDir.mkdirs();
            if (!b) {
                throw new IOException("Make dest dir failed");
            }
        }
        String name = srcFile.getName();
        String type = name.substring(name.lastIndexOf("."), name.length());
        String md5ByFile = null;
        try {
            md5ByFile = FileUtils.getMd5ByFile(srcFile);
        } catch (Exception e) {
            md5ByFile = FileUtils.getUUID32();
            Log.e(TAG, "copyExternalFileToLocal: ", e);
        }
        File dest = new File(destDir, md5ByFile + type);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile)); BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
            byte[] bytes = new byte[1024 * 10];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            bos.flush();
        }
        return dest;
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
}
