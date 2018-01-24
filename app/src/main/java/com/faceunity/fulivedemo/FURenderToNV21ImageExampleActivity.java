package com.faceunity.fulivedemo;

import android.hardware.Camera;

import com.faceunity.wrapper.faceunity;

/**
 * 这个Activity演示了如何通过fuRenderToNV21Image
 * 实现在无GL Context的情况下输入nv21的人脸图像，输出添加道具及美颜后的nv21图像
 * 和dual input对应，可以认为single input
 * <p>
 * FU SDK使用者可以将拿到处理后的nv21图像与自己的原有项目对接
 * 请FU SDK使用者直接参考示例放至代码至对应位置
 * <p>
 * FU SDK与camera无耦合，不关心数据的来源，只要图像内容正确且和宽高吻合即可
 * <p>
 * Created by lirui on 2016/12/13.
 */

@SuppressWarnings("deprecation")
public class FURenderToNV21ImageExampleActivity extends FUExampleActivity {

    @Override
    protected int draw(byte[] cameraNV21Byte, byte[] fuImgNV21Bytes, int cameraTextureId, int cameraWidth, int cameraHeight, int frameId, int[] arrayItems, int currentCameraType) {
        if (fuImgNV21Bytes == null) {
            fuImgNV21Bytes = new byte[cameraNV21Byte.length];
        }
        System.arraycopy(cameraNV21Byte, 0, fuImgNV21Bytes, 0, cameraNV21Byte.length);

        /**
         * 这个函数执行完成后，入参的nv21 byte数组会被改变
         */
        return faceunity.fuRenderToNV21Image(fuImgNV21Bytes, cameraWidth, cameraHeight, frameId,
                arrayItems, currentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? 0 : faceunity.FU_ADM_FLAG_FLIP_X);

    }
}
