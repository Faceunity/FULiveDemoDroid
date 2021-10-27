package com.faceunity.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * DESC：
 * Created on 2021/3/12
 */
public class FileUtils {
    public static final String DCIM_FILE_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath();
    public static final String photoFilePath;
    public static final String videoFilePath;
    public static final String exportVideoDir;

    static {
        if (Build.FINGERPRINT.contains("Flyme")
                || Pattern.compile("Flyme", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("Meizu")
                || Build.MANUFACTURER.contains("MeiZu")) {
            photoFilePath = DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
            videoFilePath = DCIM_FILE_PATH + File.separator + "Video" + File.separator;
        } else if (Build.FINGERPRINT.contains("vivo")
                || Pattern.compile("vivo", Pattern.CASE_INSENSITIVE).matcher(Build.DISPLAY).find()
                || Build.MANUFACTURER.contains("vivo")
                || Build.MANUFACTURER.contains("Vivo")) {
            photoFilePath = videoFilePath = Environment.getExternalStoragePublicDirectory("") + File.separator + "相机" + File.separator;
        } else {
            photoFilePath = videoFilePath = DCIM_FILE_PATH + File.separator + "Camera" + File.separator;
        }
        exportVideoDir = DCIM_FILE_PATH + File.separator + "FaceUnity" + File.separator;
        createFileDir(photoFilePath);
        createFileDir(videoFilePath);
        createFileDir(exportVideoDir);
    }

    public static final String IMAGE_FORMAT_JPG = ".jpg";
    public static final String IMAGE_FORMAT_JPEG = ".jpeg";
    public static final String IMAGE_FORMAT_PNG = ".png";
    public static final String VIDEO_FORMAT_MP4 = ".mp4";


    /**
     * 创建文件夹
     *
     * @param path
     */
    public static void createFileDir(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 应用外部文件目录
     *
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
     * 获取当前时间日期
     *
     * @return
     */
    public static String getDateTimeString() {
        GregorianCalendar now = new GregorianCalendar();
        return new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(now.getTime());
    }


    /**
     * 获取视频缓存文件
     *
     * @param context Context
     * @return File
     */
    public static File getCacheVideoFile(Context context) {
        File fileDir = new File(getExternalFileDir(context).getPath() + File.separator + "video");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, getCurrentVideoFileName());
        if (file.exists()) {
            file.delete();
        }
        return file;
    }

    /**
     * 构造视频文件名称
     *
     * @return
     */
    public static String getCurrentVideoFileName() {
        return getDateTimeString() + VIDEO_FORMAT_MP4;
    }


    /**
     * 构造图片文件名称
     *
     * @return
     */
    public static String getCurrentPhotoFileName() {
        return getDateTimeString() + IMAGE_FORMAT_JPG;
    }


    /**
     * Bitmap保存到本地
     *
     * @param context Context
     * @param bitmap  Bitmap
     * @return String?
     */
    public static String addBitmapToExternal(Context context, Bitmap bitmap) {
        if (bitmap == null) return null;
        File fileDir = new File(getExternalFileDir(context).getPath() + File.separator + "photo");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, getCurrentPhotoFileName());
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将Bitmap文件保存到相册
     *
     * @param bitmap Bitmap
     */
    public static String addBitmapToAlbum(Context context, Bitmap bitmap) {
        if (bitmap == null) return null;
        FileOutputStream fos = null;
        File dcimFile;

        File fileDir = new File(exportVideoDir);
        if (fileDir.exists()) {
            dcimFile = new File(exportVideoDir, getCurrentPhotoFileName());
        } else {
            dcimFile = new File(photoFilePath, getCurrentPhotoFileName());
        }
        if (dcimFile.exists()) {
            dcimFile.delete();
        }
        try {
            fos = new FileOutputStream(dcimFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
        return dcimFile.getAbsolutePath();
    }


    /**
     * 将视频文件保存到相册
     *
     * @param videoFile File
     * @return Uri?
     */

    public static String addVideoToAlbum(Context context, File videoFile) {
        if (videoFile == null) return null;
        File fileDir = new File(exportVideoDir);
        File dcimFile;
        if (fileDir.exists()) {
            dcimFile = new File(exportVideoDir, getCurrentVideoFileName());
        } else {
            dcimFile = new File(videoFilePath, getCurrentVideoFileName());
        }
        if (dcimFile.exists()) {
            dcimFile.delete();
        }
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(videoFile));
            bos = new BufferedOutputStream(new FileOutputStream(dcimFile));
            byte[] bytes = new byte[1024 * 10];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(dcimFile)));
        return dcimFile.getAbsolutePath();
    }


    /**
     * 将Assets文件拷贝到应用作用域存储
     *
     * @param context    Context
     * @param assetsPath String
     * @param fileName   String
     */
    public static String copyAssetsToExternalFilesDir(Context context, String assetsPath, String fileName) {
        File fileDir = new File(getExternalFileDir(context).getPath() + File.separator + "assets");
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File file = new File(fileDir, fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        try {
            InputStream inputStream = context.getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            byte[] byteArray = new byte[1024];
            int bytes = bis.read(byteArray);
            while (bytes > 0) {
                bos.write(byteArray, 0, bytes);
                bos.flush();
                bytes = bis.read(byteArray);
            }
            bos.close();
            fos.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取Uri文件绝对路径
     *
     * @param context: Context
     * @param uri      Uri
     * @return String
     */
    public static String getFilePathByUri(Context context, Uri uri) {
        if (uri == null) return null;
        return Uri2PathUtil.getRealPathFromUri(context, uri);
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }


    /**
     * load本地图片
     *
     * @param path
     * @param screenWidth
     * @return
     */
    public static Bitmap loadBitmapFromExternal(String path, int screenWidth) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picHeight > screenWidth) {
                opt.inSampleSize = picHeight / screenWidth;
            }
        } else {
            if (picWidth > screenWidth) {
                opt.inSampleSize = picWidth / screenWidth;
            }
        }
        opt.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
        int orientation = getPhotoOrientation(path);
        bitmap = rotateBitmap(bitmap, orientation);
        return bitmap;
    }

    /**
     * load本地图片
     *
     * @param path         String
     * @param screenWidth  Int
     * @param screenHeight Int
     * @return Bitmap
     */
    public static Bitmap loadBitmapFromExternal(String path, int screenWidth, int screenHeight) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        int inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picHeight > screenHeight || picWidth > screenWidth) {
            int halfHeight = picHeight / 2;
            int halfWidth = picWidth / 2;
            while (halfHeight / inSampleSize >= screenHeight && halfWidth / inSampleSize >= screenWidth) {
                inSampleSize *= 2;
            }
        }
        opt.inSampleSize = inSampleSize;
        opt.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
        int orientation = getPhotoOrientation(path);
        bitmap = rotateBitmap(bitmap, orientation);
        return bitmap;
    }


    /**
     * 旋转 Bitmap
     *
     * @param bitmap
     * @param orientation
     * @return
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        if (orientation == 90 || orientation == 180 || orientation == 270) {
            Matrix matrix = new Matrix();
            matrix.postRotate((float) orientation);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }


    /**
     * 获取图片的方向
     *
     * @param path
     * @return
     */
    public static int getPhotoOrientation(String path) {
        int orientation = 0;
        int tagOrientation = 0;
        try {
            tagOrientation = new ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (tagOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                orientation = 90;
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                orientation = 180;
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                orientation = 270;
                break;
        }
        return orientation;
    }


    /**
     * 选中图片
     *
     * @param activity Activity
     */
    public static void pickImageFile(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 选中视频
     *
     * @param activity Activity
     *                 回调可参考下方
     */
    public static void pickVideoFile(Activity activity, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("video/*");
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 根据路径获取InputStream
     *
     * @param context Context
     * @param path    String
     * @return InputStream?
     */
    public static InputStream readInputStreamByPath(Context context, String path) {
        if (path == null || path.trim().length() == 0)
            return null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(path);
        } catch (IOException e1) {
            try {
                inputStream = new FileInputStream(path);
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
        return inputStream;
    }

    /**
     * 相机byte转bitmap
     *
     * @param buffer
     * @param width
     * @param height
     * @return
     */
    public static Bitmap bytes2Bitmap(byte[] buffer, int width, int height) {
        YuvImage yuvimage = new YuvImage(buffer, ImageFormat.NV21, width, height, null);//20、20分别是图的宽度与高度
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);//80--JPG图片的质量[0-100],100最高
        byte[] jdata = baos.toByteArray();
        return BitmapFactory.decodeByteArray(baos.toByteArray(), 0, jdata.length);
    }

    /**
     * 遍历一个文件夹获取改文件夹下所有文件名
     * @param path
     * @return
     */
    public static ArrayList<String> getFileList(String path) {
        ArrayList<String> fileList = new ArrayList<>();
        File dir = new File(path);
        // 该文件目录下文件全部放入数组
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // 判断是文件还是文件夹
                if (files[i].isDirectory()) {
                    // 获取文件绝对路径
                    getFileList(files[i].getAbsolutePath());
                    // 判断文件名是否以.jpg结尾
                } else {
                    fileList.add(files[i].getName());
                }
            }
        }
        return fileList;
    }

    /**
     * 校验文件是否是图片
     *
     * @param path String
     * @return Boolean
     */
    public static Boolean checkIsImage(String path) {
        String name = new File(path).getName().toLowerCase();
        return (name.endsWith(IMAGE_FORMAT_PNG) || name.endsWith(IMAGE_FORMAT_JPG)
                || name.endsWith(IMAGE_FORMAT_JPEG));
    }

    /**
     * 校验文件是否是视频
     *
     * @param path String
     * @return Boolean
     */
    public static Boolean checkIsVideo(Context context,String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(context, Uri.fromFile(new File(path)));
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            return "yes".equals(hasVideo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
