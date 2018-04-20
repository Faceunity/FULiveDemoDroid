package com.faceunity.fulivedemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.faceunity.fulivedemo.encoder.TextureMovieEncoder;
import com.faceunity.fulivedemo.gles.ProgramTexture2d;
import com.faceunity.fulivedemo.gles.ProgramTextureOES;
import com.faceunity.fulivedemo.gles.core.GlUtil;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.Constant;
import com.faceunity.fulivedemo.utils.MiscUtil;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.faceunity.fulivedemo.encoder.TextureMovieEncoder.IN_RECORDING;
import static com.faceunity.fulivedemo.encoder.TextureMovieEncoder.START_RECORDING;


/**
 * Camera相关处理
 * 拍照与录视频
 * Camera.PreviewCallback camera数据回调
 * GLSurfaceView.Renderer GLSurfaceView相应的创建销毁与绘制回调
 * <p>
 * Created by tujh on 2018/3/2.
 */

public class CameraRenderer implements Camera.PreviewCallback, GLSurfaceView.Renderer {
    public final static String TAG = CameraRenderer.class.getSimpleName();

    private Activity mActivity;
    private GLSurfaceView mGLSurfaceView;

    public interface OnCameraRendererStatusListener {
        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight);

        void onSurfaceDestroy();

        void onCameraChange(int currentCameraType, int cameraOrientation);
    }

    private OnCameraRendererStatusListener mOnCameraRendererStatusListener;

    private int mViewWidth = 1280;
    private int mViewHeight = 720;

    private Camera mCamera;
    private static final int PREVIEW_BUFFER_COUNT = 3;
    private byte[][] previewCallbackBuffer;
    private int mCameraOrientation;
    private int mCurrentCameraType = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int mCameraWidth = 1280;
    private int mCameraHeight = 720;

    private byte[] mCameraNV21Byte;
    private SurfaceTexture mSurfaceTexture;
    private int mCameraTextureId;

    private int mFuTextureId;
    private final float[] mtx = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;
    private ProgramTextureOES mTextureOES;

    private TextureMovieEncoder mTextureMovieEncoder;
    private TextureMovieEncoder.OnEncoderStatusUpdateListener mOnEncoderStatusUpdateListener;

    private boolean mTakePicing = false;
    private boolean mIsNeedTakePic = false;

    public CameraRenderer(Activity activity, GLSurfaceView GLSurfaceView, OnCameraRendererStatusListener onCameraRendererStatusListener) {
        mActivity = activity;
        mGLSurfaceView = GLSurfaceView;
        mOnCameraRendererStatusListener = onCameraRendererStatusListener;
    }

    public void onCreate() {
        mGLSurfaceView.onResume();
    }

    public void onResume() {
        openCamera(mCurrentCameraType);
    }

    public void onPause() {
        releaseCamera();
    }

    public void onDestroy() {
        final CountDownLatch count = new CountDownLatch(1);
        mGLSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                onSurfaceDestroy();
                count.countDown();
            }
        });
        try {
            count.await(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mGLSurfaceView.onPause();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mCameraNV21Byte = data;
        mCamera.addCallbackBuffer(data);
        mGLSurfaceView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mFullFrameRectTexture2D = new ProgramTexture2d();
        mTextureOES = new ProgramTextureOES();
        mCameraTextureId = GlUtil.createTextureObject(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        cameraStartPreview();

        mOnCameraRendererStatusListener.onSurfaceCreated(gl, config);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, mViewWidth = width, mViewHeight = height);

        mOnCameraRendererStatusListener.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (mCameraNV21Byte == null) {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, mtx, GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth));
            return;
        }
        try {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mtx);
        } catch (Exception e) {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, mtx, GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth));
            return;
        }
        mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mCameraNV21Byte, mCameraTextureId, mCameraWidth, mCameraHeight);
        //用于屏蔽切换调用SDK处理数据方法导致的绿屏（切换SDK处理数据方法是用于展示，实际使用中无需切换，故无需调用做这个判断,直接使用else分支绘制即可）
        if (mFuTextureId <= 0) {
            mTextureOES.drawFrame(mCameraTextureId, mtx, GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth));
        } else {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, mtx, GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth));
        }
        sendRecordingData(mFuTextureId);
        checkPic();
        mGLSurfaceView.requestRender();
    }

    private void onSurfaceDestroy() {
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }

        if (mCameraTextureId != 0) {
            int[] textures = new int[]{mCameraTextureId};
            GLES20.glDeleteTextures(1, textures, 0);
            mCameraTextureId = 0;
        }

        if (mFullFrameRectTexture2D != null) {
            mFullFrameRectTexture2D.release();
            mFullFrameRectTexture2D = null;
        }

        mOnCameraRendererStatusListener.onSurfaceDestroy();
    }

    @SuppressWarnings("deprecation")
    private void openCamera(int cameraType) {
        Log.e(TAG, "open Camera");

        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = 0;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == cameraType) {
                cameraId = i;
                try {
                    mCamera = Camera.open(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    releaseCamera();
                    openCamera(cameraType);
                    return;
                }
                mCurrentCameraType = cameraType;
                break;
            }
        }

        mCameraOrientation = CameraUtils.getCameraOrientation(cameraId);
        CameraUtils.setCameraDisplayOrientation(mActivity, cameraId, mCamera);

        Camera.Parameters parameters = mCamera.getParameters();

        CameraUtils.setFocusModes(parameters);

        int[] size = CameraUtils.choosePreviewSize(parameters, mCameraWidth, mCameraHeight);
        mCameraWidth = size[0];
        mCameraHeight = size[1];

        mCamera.setParameters(parameters);

        if (mCameraTextureId > 0) {
            cameraStartPreview();
        }

        mOnCameraRendererStatusListener.onCameraChange(mCurrentCameraType, mCameraOrientation);
    }

    private void cameraStartPreview() {
        Log.e(TAG, "handleCameraStartPreview");
        if (mCamera == null) {
            return;
        }
        if (previewCallbackBuffer == null) {
            previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
        }
        mCamera.setPreviewCallbackWithBuffer(this);
        for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
            mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
        try {
            if (mSurfaceTexture != null) {
                mSurfaceTexture.release();
            }
            mCamera.setPreviewTexture(mSurfaceTexture = new SurfaceTexture(mCameraTextureId));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    private void releaseCamera() {
        Log.e(TAG, "release camera");
        mCameraNV21Byte = null;
        if (mCamera != null) {
            try {
                mCamera.stopPreview();
                mCamera.setPreviewTexture(null);
                mCamera.setPreviewCallbackWithBuffer(null);
                mCamera.release();
                mCamera = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void changeCamera() {
        if (mCameraNV21Byte == null) {
            return;
        }
        releaseCamera();
        openCamera(mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    private void sendRecordingData(int fuTex) {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(START_RECORDING)) {
            String videoFileName = "FULiveDemo_" + MiscUtil.getCurrentDate() + ".mp4";
            final File outFile = new File(Constant.cameraFilePath, videoFileName);
            mTextureMovieEncoder.startRecording(new TextureMovieEncoder.EncoderConfig(
                    outFile, mCameraHeight, mCameraWidth,
                    3000000, EGL14.eglGetCurrentContext(), mSurfaceTexture.getTimestamp()
            ));
            mTextureMovieEncoder.setTextureId(mFullFrameRectTexture2D, fuTex, mtx);
            //forbid click until start or stop success
            mTextureMovieEncoder.setOnEncoderStatusUpdateListener(mOnEncoderStatusUpdateListener);
        }

        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
            mTextureMovieEncoder.setTextureId(mFullFrameRectTexture2D, fuTex, mtx);
            mTextureMovieEncoder.frameAvailable(mSurfaceTexture);
        }
    }

    public void startRecording(TextureMovieEncoder.OnEncoderStatusUpdateListener onEncoderStatusUpdateListener) {
        mTextureMovieEncoder = new TextureMovieEncoder();
        mOnEncoderStatusUpdateListener = onEncoderStatusUpdateListener;
    }

    public void stopRecording() {
        if (mTextureMovieEncoder != null && mTextureMovieEncoder.checkRecordingStatus(IN_RECORDING)) {
            mGLSurfaceView.queueEvent(new Runnable() {
                @Override
                public void run() {
                    mTextureMovieEncoder.stopRecording();
                }
            });
        }
    }

    public void takePic() {
        if (mTakePicing) {
            return;
        }
        mIsNeedTakePic = true;
        mTakePicing = true;
    }

    private void checkPic() {
        if (!mIsNeedTakePic) {
            return;
        }
        mIsNeedTakePic = false;
        final int bitmapBuffer[] = new int[mViewWidth * mViewHeight];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        GLES20.glReadPixels(0, 0, mViewWidth, mViewHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final int bitmapSource[] = new int[mViewWidth * mViewHeight];
                int offset1, offset2;
                for (int i = 0; i < mViewHeight; i++) {
                    offset1 = i * mViewWidth;
                    offset2 = (mViewHeight - i - 1) * mViewWidth;
                    for (int j = 0; j < mViewWidth; j++) {
                        int texturePixel = bitmapBuffer[offset1 + j];
                        int blue = (texturePixel >> 16) & 0xff;
                        int red = (texturePixel << 16) & 0x00ff0000;
                        int pixel = (texturePixel & 0xff00ff00) | red | blue;
                        bitmapSource[offset2 + j] = pixel;
                    }
                }
                final Bitmap shotCaptureBitmap = Bitmap.createBitmap(bitmapSource, mViewWidth, mViewHeight, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);

                String name = "FULiveDemo_" + MiscUtil.getCurrentDate() + ".jpg";
                String result = MiscUtil.saveBitmap(shotCaptureBitmap, Constant.photoFilePath, name);
                if (result != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity, "保存照片成功！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    File resultFile = new File(result);
                    // 最后通知图库更新
                    mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(resultFile)));
                }
                mTakePicing = false;
            }
        });
    }

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }
}
