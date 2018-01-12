package com.faceunity.fulivedemo;

import android.hardware.Camera;

import com.faceunity.wrapper.faceunity;

/**
 * 这个Activity演示了从Camera取数据,用fuDualInputToTexure处理并预览展示
 * 所谓dual input，指从cpu和gpu同时拿数据，
 * cpu拿到的是nv21的byte数组，gpu拿到的是对应的texture
 * <p>
 * Created by lirui on 2016/12/13.
 */

@SuppressWarnings("deprecation")
public class FUDualInputToTextureExampleActivity extends FUExampleActivity {

    @Override
    protected int draw(byte[] cameraNV21Byte, byte[] fuImgNV21Bytes, int cameraTextureId, int cameraWidth, int cameraHeight, int frameId, int[] arrayItems, int currentCameraType) {
        boolean isOESTexture = true; //Tip: camera texture类型是默认的是OES的，和texture 2D不同
        int flags = isOESTexture ? faceunity.FU_ADM_FLAG_EXTERNAL_OES_TEXTURE : 0;
        boolean isNeedReadBack = false; //是否需要写回，如果是，则入参的byte[]会被修改为带有fu特效的；支持写回自定义大小的内存数组中，即readback custom img
        flags = isNeedReadBack ? flags | faceunity.FU_ADM_FLAG_ENABLE_READBACK : flags;
        if (isNeedReadBack) {
            if (fuImgNV21Bytes == null) {
                fuImgNV21Bytes = new byte[cameraNV21Byte.length];
            }
            System.arraycopy(cameraNV21Byte, 0, fuImgNV21Bytes, 0, cameraNV21Byte.length);
        } else {
            fuImgNV21Bytes = cameraNV21Byte;
        }
        flags |= currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X;

            /*
             * 这里拿到fu处理过后的texture，可以对这个texture做后续操作，如硬编、预览。
             */
        return faceunity.fuDualInputToTexture(fuImgNV21Bytes, cameraTextureId, flags,
                cameraWidth, cameraHeight, frameId, arrayItems);
    }
}
