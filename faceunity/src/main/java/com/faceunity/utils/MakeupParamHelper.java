package com.faceunity.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author Richie on 2019.05.27
 */
public class MakeupParamHelper {
    private static final String TAG = "MakeupParamHelper";

    /**
     * 加载美妆资源数据，返回图像的字节数组和宽高。
     *
     * @param resourcePath
     * @return bytes, width and height
     * @throws IOException
     */
    public static Pair<byte[], Pair<Integer, Integer>> loadMakeupResource(Context context, String resourcePath) {
        if (TextUtils.isEmpty(resourcePath)) {
            return null;
        }

        InputStream is = null;
        Bitmap bitmap;
        try {
            is = context.getAssets().open(resourcePath);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            // open assets failed, then try sdcard
            Log.w(TAG, "loadMakeupResource: ", e);
            bitmap = BitmapFactory.decodeFile(resourcePath);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
        if (bitmap != null) {
            int bmpByteCount = bitmap.getByteCount();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            byte[] bitmapBytes = new byte[bmpByteCount];
            ByteBuffer byteBuffer = ByteBuffer.wrap(bitmapBytes);
            bitmap.copyPixelsToBuffer(byteBuffer);
            return Pair.create(bitmapBytes, Pair.create(width, height));
        }
        return null;
    }

    public static final class MakeupParams {
        /**
         * tex_ 开头的参数表示 fuCreateTexForItem 方法的 name 参数
         */
        public static final String TEX_BROW = "tex_brow";
        public static final String TEX_EYE = "tex_eye";
        public static final String TEX_EYE2 = "tex_eye2";
        public static final String TEX_EYE3 = "tex_eye3";
        public static final String TEX_PUPIL = "tex_pupil";
        public static final String TEX_EYE_LASH = "tex_eyeLash";
        public static final String TEX_EYE_LINER = "tex_eyeLiner";
        public static final String TEX_BLUSHER = "tex_blusher";
        public static final String TEX_FOUNDATION = "tex_foundation";
        public static final String TEX_HIGHLIGHT = "tex_highlight";
        public static final String TEX_SHADOW = "tex_shadow";
        public static final String TEX_PREFIX = "tex_";
        /**
         * 是否使用双色口红，1 为开，0 为关
         */
        public static final String IS_TWO_COLOR = "is_two_color";
        /**
         * 口红类型，0 雾面，1 缎面
         */
        public static final String LIP_TYPE = "lip_type";
        /**
         * 嘴唇优化效果开关，1 为开，0 为关
         */
        public static final String MAKEUP_LIP_MASK = "makeup_lip_mask";
        /**
         * alpha 值逆向
         */
        public static final String REVERSE_ALPHA = "reverse_alpha";
        /**
         * 是否使用眉毛变形，1 为开，0 为关
         */
        public static final String BROW_WARP = "brow_warp";
        /**
         * 眉毛变形类型
         */
        public static final String BROW_WARP_TYPE = "brow_warp_type";
        /**
         * 柳叶眉
         */
        public static final double BROW_WARP_TYPE_WILLOW = 0.0;
        /**
         * 一字眉
         */
        public static final double BROW_WARP_TYPE_ONE_WORD = 1.0;
        /**
         * 小山眉
         */
        public static final double BROW_WARP_TYPE_HILL = 2.0;
        /**
         * 标准眉
         */
        public static final double BROW_WARP_TYPE_STANDARD = 3.0;
        /**
         * 扶形眉
         */
        public static final double BROW_WARP_TYPE_SHAPE = 4.0;
        /**
         * 日常风
         */
        public static final double BROW_WARP_TYPE_DAILY = 5.0;
        /**
         * 日系风
         */
        public static final double BROW_WARP_TYPE_JAPAN = 6.0;

        /**
         * 下面是各个妆容的颜色值
         */
        public static final String MAKEUP_EYE_BROW_COLOR = "makeup_eyeBrow_color";
        public static final String MAKEUP_LIP_COLOR = "makeup_lip_color";
        public static final String MAKEUP_LIP_COLOR2 = "makeup_lip_color2";
        public static final String MAKEUP_EYE_COLOR = "makeup_eye_color";
        public static final String MAKEUP_EYE_LINER_COLOR = "makeup_eyeLiner_color";
        public static final String MAKEUP_EYELASH_COLOR = "makeup_eyelash_color";
        public static final String MAKEUP_BLUSHER_COLOR = "makeup_blusher_color";
        public static final String MAKEUP_FOUNDATION_COLOR = "makeup_foundation_color";
        public static final String MAKEUP_HIGHLIGHT_COLOR = "makeup_highlight_color";
        public static final String MAKEUP_SHADOW_COLOR = "makeup_shadow_color";
        public static final String MAKEUP_PUPIL_COLOR = "makeup_pupil_color";
        /**
         * 美妆开关
         */
        public static final String IS_MAKEUP_ON = "is_makeup_on";
        /**
         * 全局妆容强度，范围 [0-1]
         */
        public static final String MAKEUP_INTENSITY = "makeup_intensity";
        /**
         * 下面是各个妆容强度参数，范围 [0-1]
         */
        public static final String MAKEUP_INTENSITY_LIP = "makeup_intensity_lip";
        public static final String MAKEUP_INTENSITY_EYE_LINER = "makeup_intensity_eyeLiner";
        public static final String MAKEUP_INTENSITY_BLUSHER = "makeup_intensity_blusher";
        public static final String MAKEUP_INTENSITY_PUPIL = "makeup_intensity_pupil";
        public static final String MAKEUP_INTENSITY_EYE_BROW = "makeup_intensity_eyeBrow";
        public static final String MAKEUP_INTENSITY_EYE = "makeup_intensity_eye";
        public static final String MAKEUP_INTENSITY_EYELASH = "makeup_intensity_eyelash";
        public static final String MAKEUP_INTENSITY_FOUNDATION = "makeup_intensity_foundation";
        public static final String MAKEUP_INTENSITY_HIGHLIGHT = "makeup_intensity_highlight";
        public static final String MAKEUP_INTENSITY_SHADOW = "makeup_intensity_shadow";
        public static final String MAKEUP_INTENSITY_PREFIX = "makeup_intensity_";

        public static final String[] MAKEUP_INTENSITIES = {MAKEUP_INTENSITY_LIP, MAKEUP_INTENSITY_EYE_LINER,
                MAKEUP_INTENSITY_BLUSHER, MAKEUP_INTENSITY_PUPIL, MAKEUP_INTENSITY_EYE_BROW, MAKEUP_INTENSITY_EYE,
                MAKEUP_INTENSITY_EYELASH, MAKEUP_INTENSITY_FOUNDATION, MAKEUP_INTENSITY_HIGHLIGHT, MAKEUP_INTENSITY_SHADOW};
    }
}
