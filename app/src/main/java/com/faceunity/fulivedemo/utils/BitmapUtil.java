package com.faceunity.fulivedemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.opengl.GLES20;
import android.os.AsyncTask;

import com.faceunity.gles.ProgramTexture2d;

import java.io.IOException;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by tujh on 2018/6/28.
 */
public abstract class BitmapUtil {

    /**
     * 读取图片（glReadPixels）
     *
     * @param textureId
     * @param mtx
     * @param mvp
     * @param texWidth
     * @param texHeight
     * @param listener
     */
    public static void glReadBitmap(int textureId, float[] mtx, float[] mvp, final int texWidth, final int texHeight, final OnReadBitmapListener listener) {

        int viewport[] = new int[4];
        GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);
        GLES20.glViewport(0, 0, texWidth, texHeight);

        new ProgramTexture2d().drawFrame(textureId, mtx, mvp);

        final int bitmapBuffer[] = new int[texWidth * texHeight];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        GLES20.glReadPixels(0, 0, texWidth, texHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
        GLES20.glFinish();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final int bitmapSource[] = new int[texWidth * texHeight];
                int offset1, offset2;
                for (int i = 0; i < texHeight; i++) {
                    offset1 = i * texWidth;
                    offset2 = (texHeight - i - 1) * texWidth;
                    for (int j = 0; j < texWidth; j++) {
                        int texturePixel = bitmapBuffer[offset1 + j];
                        int blue = (texturePixel >> 16) & 0xff;
                        int red = (texturePixel << 16) & 0x00ff0000;
                        int pixel = (texturePixel & 0xff00ff00) | red | blue;
                        bitmapSource[offset2 + j] = pixel;
                    }
                }
                final Bitmap shotCaptureBitmap = Bitmap.createBitmap(bitmapSource, texWidth, texHeight, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
                if (listener != null) {
                    listener.onReadBitmapListener(shotCaptureBitmap);
                }
            }
        });

        GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);
    }

    public interface OnReadBitmapListener {
        void onReadBitmapListener(Bitmap bitmap);
    }

    /**
     * load本地图片
     *
     * @param path
     * @param screenWidth
     * @return
     * @throws IOException
     */
    public static Bitmap loadBitmap(String path, int screenWidth) throws IOException {
        int degree = 0;
        int orientation = new ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            degree = 90;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            degree = 180;
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            degree = 270;
        }
        BitmapFactory.Options opt = new BitmapFactory.Options();
        // 这个isjustdecodebounds很重要
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opt);
        // 获取到这个图片的原始宽度和高度
        int picWidth = opt.outWidth;
        int picHeight = opt.outHeight;
        // isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2
        opt.inSampleSize = 1;
        // 根据屏的大小和图片大小计算出缩放比例
        if (picWidth > picHeight) {
            if (picHeight > screenWidth)
                opt.inSampleSize = picHeight / screenWidth;
        } else {
            if (picWidth > screenWidth)
                opt.inSampleSize = picWidth / screenWidth;
        }
        // 这次再真正地生成一个有像素的，经过缩放了的bitmap
        opt.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
        if (degree == 90 || degree == 180 || degree == 270) {
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    /**
     * bitmap 转 NV21 数据
     *
     * @param inputWidth
     * @param inputHeight
     * @param scaled
     * @return
     */
    public static byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {
        int[] argb = new int[inputWidth * inputHeight];
        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);
        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);
        scaled.recycle();
        return yuv;
    }

    /**
     * ARGB 转 NV21 数据
     *
     * @param yuv420sp
     * @param argb
     * @param width
     * @param height
     */
    public static void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }
                index++;
            }
        }
    }
}
