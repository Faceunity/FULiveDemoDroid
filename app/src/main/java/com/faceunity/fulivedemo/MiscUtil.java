package com.faceunity.fulivedemo;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lirui on 2017/3/6.
 */

public class MiscUtil {

    private static boolean isDebug = true;
    private static String TAG = "FU-MiscUtil";

    public static boolean VERBOSE_LOG = false;

    public static float NANO_IN_ONE_MILLI_SECOND = 1000000.0f;

    public static void Logger(String tag, String msg, boolean isImportant) {
        if (isImportant || isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void checkPermission(Context context, String permission) {
        Logger(TAG, "checkPermission " + permission, false);
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{permission}, 0);
        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private Bitmap processImage(int width, int height, byte[] data) throws IOException {
        // Determine the width/height of the image
        //int width = camera.getParameters().getPictureSize().width;
        //int height = camera.getParameters().getPictureSize().height;

        // Load the bitmap from the byte array
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Rotate and crop the image into a square
        int croppedWidth = (width > height) ? height : width;
        int croppedHeight = (width > height) ? height : width;

        //Matrix matrix = new Matrix();
        //matrix.postRotate(IMAGE_ORIENTATION);
        //Bitmap cropped = Bitmap.createBitmap(bitmap, 0, 0, croppedWidth, croppedHeight, matrix, true);
        //bitmap.recycle();

        // Scale down to the output size
        //Bitmap scaledBitmap = Bitmap.createScaledBitmap(cropped, IMAGE_SIZE, IMAGE_SIZE, true);
        //cropped.recycle();

        //return scaledBitmap;
        return null;
    }

    public static final String filePath = Environment.getExternalStoragePublicDirectory("")
            + File.separator + "FaceUnity" + File.separator + "FULiveDemo" + File.separator;

    public static String createFileName() {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return filePath + getCurrentDate() +
                "_" + System.currentTimeMillis();
    }

    public static String saveDataToFile(String fileName, String fileExtName, final byte[] data) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileFullName = filePath + fileName + "." + fileExtName;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(fileFullName);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return fileFullName;
    }

    public static String saveBitmapToFile(final Bitmap bitmap) {
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String fileName = filePath + getCurrentDate() +
                "_" + System.currentTimeMillis() + ".jpg";
        Logger(TAG, "file : " + fileName, false);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(fileName);
                    FileOutputStream fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.flush();
                    fos.close();
                    bitmap.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return fileName;
    }

    public static Bitmap getBitmapFromPath(String filePath) {
        return BitmapFactory.decodeFile(filePath, new BitmapFactory.Options());
    }

    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        Logger(TAG, "getBitmapFromUri " + uri.toString(), false);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Logger(TAG, "uri FileDescriptor : " + fileDescriptor.toString(), false);
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static void saveInputStreamToFile(InputStream is, FileOutputStream fos, long total) {
        byte[] buf = new byte[2048];
        int len = 0;
        MiscUtil.Logger(TAG, "total------>" + total, false);
        long current = 0;
        try {
            while ((len = is.read(buf)) != -1) {
                current += len;
                fos.write(buf, 0, len);
                MiscUtil.Logger(TAG, "current------>" + current, false);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getFileAbsolutePath(Context context, Uri fileUri) {
        if (context == null || fileUri == null)
            return null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, fileUri)) {
            if (isExternalStorageDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(fileUri)) {
                String id = DocumentsContract.getDocumentId(fileUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(fileUri)) {
                String docId = DocumentsContract.getDocumentId(fileUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(fileUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(fileUri))
                return fileUri.getLastPathSegment();
            return getDataColumn(context, fileUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(fileUri.getScheme())) {
            return fileUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.equals("");
    }

    public static int getScreenWidth(Context context) {
        //WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //return wm.getDefaultDisplay().getWidth();
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    public static String getMediaLatestPictureThumbnailPath(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{
                        MediaStore.Images.Thumbnails.IMAGE_ID,
                        MediaStore.Images.Thumbnails.DATA
                },
                null,
                null,
                null
        );
        cursor.moveToLast();
        int imageId = cursor.getInt(0);
        String imagePath = cursor.getString(1);
        /*cursor = cr.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.Media.DATA
                },
                MediaStore.Audio.Media._ID + "=" + imageId
                )*/
        if (VERBOSE_LOG) {
            MiscUtil.Logger(TAG, "latest media thumbnail path " + imagePath, false);
        }
        return imagePath;
    }

    public static void toast(final Context context, String msg) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, "creating body head bundle from package error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
