package com.faceunity.fulivedemo.renderer;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AlertDialog;

import com.faceunity.gles.ProgramTexture2d;
import com.faceunity.gles.ProgramTextureOES;
import com.faceunity.gles.core.GlUtil;
import com.faceunity.fulivedemo.utils.CameraUtils;
import com.faceunity.fulivedemo.utils.FPSUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Camera相关处理
 * Camera.PreviewCallback camera数据回调
 * GLSurfaceView.Renderer GLSurfaceView相应的创建销毁与绘制回调
 * <p>
 * Created by tujh on 2018/3/2.
 */

public class CameraRenderer implements Camera.PreviewCallback, GLSurfaceView.Renderer {
    public final static String TAG = CameraRenderer.class.getSimpleName();

    private Activity mActivity;
    private GLSurfaceView mGLSurfaceView;

    public interface OnRendererStatusListener {

        void onSurfaceCreated(GL10 gl, EGLConfig config);

        void onSurfaceChanged(GL10 gl, int width, int height);

        int onDrawFrame(byte[] cameraNV21Byte, int cameraTextureId, int cameraWidth, int cameraHeight, float[] mtx, long timeStamp);

        void onSurfaceDestroy();

        void onCameraChange(int currentCameraType, int cameraOrientation);
    }

    private OnRendererStatusListener mOnCameraRendererStatusListener;

    private int mViewWidth = 1280;
    private int mViewHeight = 720;

    private final Object mCameraLock = new Object();
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
    private float[] mvp = new float[16];
    private ProgramTexture2d mFullFrameRectTexture2D;
    private ProgramTextureOES mTextureOES;

    private FPSUtil mFPSUtil;

    public CameraRenderer(Activity activity, GLSurfaceView GLSurfaceView, OnRendererStatusListener onCameraRendererStatusListener) {
        mActivity = activity;
        mGLSurfaceView = GLSurfaceView;
        mOnCameraRendererStatusListener = onCameraRendererStatusListener;
        mFPSUtil = new FPSUtil();
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
        mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);
        mOnCameraRendererStatusListener.onSurfaceChanged(gl, width, height);
        mFPSUtil.resetLimit();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            mSurfaceTexture.updateTexImage();
            mSurfaceTexture.getTransformMatrix(mtx);
        } catch (Exception e) {
            return;
        }
        if (mCameraNV21Byte == null) {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, mtx, mvp);
            return;
        }
        mFuTextureId = mOnCameraRendererStatusListener.onDrawFrame(mCameraNV21Byte, mCameraTextureId, mCameraWidth, mCameraHeight, mtx, mSurfaceTexture.getTimestamp());
        //用于屏蔽切换调用SDK处理数据方法导致的绿屏（切换SDK处理数据方法是用于展示，实际使用中无需切换，故无需调用做这个判断,直接使用else分支绘制即可）
        if (mFuTextureId <= 0) {
            mTextureOES.drawFrame(mCameraTextureId, mtx, mvp);
        } else {
            mFullFrameRectTexture2D.drawFrame(mFuTextureId, mtx, mvp);
        }

        mFPSUtil.limit();
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
    private void openCamera(final int cameraType) {
        try {
            synchronized (mCameraLock) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                int cameraId = 0;
                int numCameras = Camera.getNumberOfCameras();
                for (int i = 0; i < numCameras; i++) {
                    Camera.getCameraInfo(i, info);
                    if (info.facing == cameraType) {
                        cameraId = i;
                        mCamera = Camera.open(i);
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
                mvp = GlUtil.changeMVPMatrix(GlUtil.IDENTITY_MATRIX, mViewWidth, mViewHeight, mCameraHeight, mCameraWidth);

                mCamera.setParameters(parameters);
            }

            cameraStartPreview();

            mOnCameraRendererStatusListener.onCameraChange(mCurrentCameraType, mCameraOrientation);
            mFPSUtil.resetLimit();
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();
            new AlertDialog.Builder(mActivity)
                    .setTitle("警告")
                    .setMessage("相机权限被禁用或者相机被别的应用占用！")
                    .setNegativeButton("重试", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            openCamera(cameraType);
                        }
                    })
                    .setNeutralButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mActivity.onBackPressed();
                        }
                    })
                    .show();
        }
    }

    private void cameraStartPreview() {
        try {
            if (mCameraTextureId == 0 || mCamera == null) {
                return;
            }
            synchronized (mCameraLock) {
                if (previewCallbackBuffer == null) {
                    previewCallbackBuffer = new byte[PREVIEW_BUFFER_COUNT][mCameraWidth * mCameraHeight * 3 / 2];
                }
                mCamera.setPreviewCallbackWithBuffer(this);
                for (int i = 0; i < PREVIEW_BUFFER_COUNT; i++)
                    mCamera.addCallbackBuffer(previewCallbackBuffer[i]);
                if (mSurfaceTexture != null) {
                    mSurfaceTexture.release();
                }
                mCamera.setPreviewTexture(mSurfaceTexture = new SurfaceTexture(mCameraTextureId));
                mCamera.startPreview();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseCamera() {
        try {
            synchronized (mCameraLock) {
                mCameraNV21Byte = null;
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.setPreviewTexture(null);
                    mCamera.setPreviewCallbackWithBuffer(null);
                    mCamera.release();
                    mCamera = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeCamera() {
        if (mCameraNV21Byte == null) {
            return;
        }
        releaseCamera();
        openCamera(mCurrentCameraType == Camera.CameraInfo.CAMERA_FACING_FRONT ? Camera.CameraInfo.CAMERA_FACING_BACK : Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    public int getCameraWidth() {
        return mCameraWidth;
    }

    public int getCameraHeight() {
        return mCameraHeight;
    }
}
