/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.faceunity.fulivedemo.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Camera-related utility functions.
 */
public final class CameraUtils {
    private static final String TAG = CameraUtils.class.getSimpleName();
    public static final int FOCUS_TIME = 2000;
    public static final boolean DEBUG = false;

    /**
     * 是否支持 Camera2
     *
     * @param context
     * @return
     */
    public static boolean hasCamera2(Context context) {
        if (context == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        try {
            CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
            assert manager != null;
            String[] idList = manager.getCameraIdList();
            boolean notNull = true;
            if (idList.length == 0) {
                notNull = false;
            } else {
                for (final String str : idList) {
                    if (str == null || str.trim().isEmpty()) {
                        notNull = false;
                        break;
                    }
                    final CameraCharacteristics characteristics = manager.getCameraCharacteristics(str);
                    Integer iSupportLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
                    if (iSupportLevel != null && iSupportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                        notNull = false;
                        break;
                    }
                }
            }
            return notNull;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * 获取前置相机的方向
     *
     * @return
     */
    public static int getFrontCameraOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraId = -1;
        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        if (cameraId < 0) {
            // no front camera, regard it as back camera
            return 90;
        } else {
            return info.orientation;
        }
    }

    /**
     * 设置相机显示方向
     *
     * @param activity
     * @param cameraId
     * @param camera
     */
    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * 设置对焦模式，优先支持自动对焦
     *
     * @param parameters
     */
    public static void setFocusModes(Camera.Parameters parameters) {
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        if (DEBUG) {
            Log.i(TAG, "setFocusModes: " + parameters.getFocusMode());
        }
    }

    /**
     * 设置相机 FPS，选择尽可能大的范围
     *
     * @param parameters
     */
    public static void chooseFrameRate(Camera.Parameters parameters) {
        List<int[]> supportedPreviewFpsRanges = parameters.getSupportedPreviewFpsRange();
        if (DEBUG) {
            StringBuilder buffer = new StringBuilder();
            buffer.append('[');
            for (Iterator<int[]> it = supportedPreviewFpsRanges.iterator(); it.hasNext(); ) {
                buffer.append(Arrays.toString(it.next()));
                if (it.hasNext()) {
                    buffer.append(", ");
                }
            }
            buffer.append(']');
            Log.d(TAG, "chooseFrameRate: Supported FPS ranges " + buffer.toString());
        }
        // FPS下限小于 7，弱光时能保证足够曝光时间，提高亮度。
        // range 范围跨度越大越好，光源足够时FPS较高，预览更流畅，光源不够时FPS较低，亮度更好。
        int[] bestFrameRate = supportedPreviewFpsRanges.get(0);
        for (int[] fpsRange : supportedPreviewFpsRanges) {
            int thisMin = fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX];
            int thisMax = fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX];
            if (thisMin < 7000) {
                continue;
            }
            if (thisMin <= 15000 && thisMax - thisMin > bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]
                    - bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX]) {
                bestFrameRate = fpsRange;
            }
        }
        if (DEBUG) {
            Log.i(TAG, "setPreviewFpsRange: [" + bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX] + ", " + bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX] + "]");
        }
        parameters.setPreviewFpsRange(bestFrameRate[Camera.Parameters.PREVIEW_FPS_MIN_INDEX], bestFrameRate[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
    }

    /**
     * Attempts to find a preview size that matches the provided width and height (which
     * specify the dimensions of the encoded video).  If it fails to find a match it just
     * uses the default preview size for video.
     * <p>
     * https://github.com/commonsguy/cwac-camera/blob/master/camera/src/com/commonsware/cwac/camera/CameraUtils.java
     */
    public static int[] choosePreviewSize(Camera.Parameters parameters, int width, int height) {
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        if (DEBUG) {
            StringBuilder sb = new StringBuilder("[");
            for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
                sb.append("[").append(supportedPreviewSize.width).append(", ")
                        .append(supportedPreviewSize.height).append("]").append(", ");
            }
            sb.append("]");
            Log.d(TAG, "choosePreviewSize: Supported preview size " + sb.toString());
        }

        for (Camera.Size size : supportedPreviewSizes) {
            if (size.width == width && size.height == height) {
                parameters.setPreviewSize(width, height);
                return new int[]{width, height};
            }
        }

        if (DEBUG) {
            Log.e(TAG, "Unable to set preview size to " + width + "x" + height);
        }
        Camera.Size ppsfv = parameters.getPreferredPreviewSizeForVideo();
        if (ppsfv != null) {
            parameters.setPreviewSize(ppsfv.width, ppsfv.height);
            return new int[]{ppsfv.width, ppsfv.height};
        }
        // else use whatever the default size is
        return new int[]{0, 0};
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
                                         int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                        option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        Comparator<Size> comparator = new Comparator<Size>() {
            @Override
            public int compare(Size lhs, Size rhs) {
                // We cast here to ensure the multiplications won't overflow
                return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                        (long) rhs.getWidth() * rhs.getHeight());
            }
        };
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, comparator);
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, comparator);
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    /**
     * 设置相机视频防抖动
     *
     * @param parameters
     */
    public static void setVideoStabilization(Camera.Parameters parameters) {
        if (parameters.isVideoStabilizationSupported()) {
            if (!parameters.getVideoStabilization()) {
                parameters.setVideoStabilization(true);
                if (DEBUG) {
                    Log.i(TAG, "Enabling video stabilization...");
                }
            }
        } else {
            if (DEBUG) {
                Log.i(TAG, "This device does not support video stabilization");
            }
        }
    }

    /**
     * 获取曝光补偿
     *
     * @param camera
     * @return
     */
    public static float getExposureCompensation(Camera camera) {
        if (camera == null) {
            return 0;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            float value = parameters.getExposureCompensation();
            float min = parameters.getMinExposureCompensation();
            float max = parameters.getMaxExposureCompensation();
            return (value - min) / (max - min);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(TAG, "getExposureCompensation: ", e);
            }
        }
        return 0;
    }

    /**
     * 设置曝光补偿
     *
     * @param camera
     * @param value
     */
    public static void setExposureCompensation(Camera camera, float value) {
        if (camera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            float min = parameters.getMinExposureCompensation();
            float max = parameters.getMaxExposureCompensation();
            int compensation = (int) (value * (max - min) + min);
            parameters.setExposureCompensation(compensation);
            if (DEBUG) {
                Log.d(TAG, "setExposureCompensation: " + compensation);
            }
            camera.setParameters(parameters);
        } catch (Exception e) {
            if (DEBUG) {
                Log.w(TAG, "setExposureCompensation: ", e);
            }
        }
    }

    /**
     * 设置相机参数
     *
     * @param camera
     * @param parameters
     */
    public static void setParameters(Camera camera, Camera.Parameters parameters) {
        if (camera != null && parameters != null) {
            try {
                camera.setParameters(parameters);
            } catch (Exception ex) {
                if (DEBUG) {
                    Log.w(TAG, "setParameters: ", ex);
                }
            }
        }
    }

    /**
     * 点击屏幕时，设置测光和对焦
     *
     * @param camera
     * @param rawX
     * @param rawY
     * @param viewWidth
     * @param viewHeight
     * @param cameraWidth
     * @param cameraHeight
     * @param areaSize
     * @param cameraFacing
     */
    public static void handleFocusMetering(Camera camera, float rawX, float rawY, int viewWidth, int viewHeight,
                                           int cameraWidth, int cameraHeight, int areaSize, int cameraFacing) {
        if (camera == null) {
            return;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
            Rect focusRect = calculateTapArea(rawX / viewWidth * cameraHeight, rawY / viewHeight * cameraWidth,
                    cameraHeight, cameraWidth, areaSize, cameraFacing);
            final String focusMode = parameters.getFocusMode();
            List<Camera.Area> focusAreas = new ArrayList<>();
            focusAreas.add(new Camera.Area(focusRect, 1000));
            List<Camera.Area> meteringAreas = new ArrayList<>();
            meteringAreas.add(new Camera.Area(new Rect(focusRect), 1000));
            if (parameters.getMaxNumFocusAreas() > 0 &&
                    (focusMode.equals(Camera.Parameters.FOCUS_MODE_AUTO) ||
                            focusMode.equals(Camera.Parameters.FOCUS_MODE_MACRO) ||
                            focusMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) ||
                            focusMode.equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                parameters.setFocusAreas(focusAreas);
                if (parameters.getMaxNumMeteringAreas() > 0) {
                    parameters.setMeteringAreas(meteringAreas);
                    if (DEBUG) {
                        Log.d(TAG, "handleFocusMetering: setMeteringAreas 1 " + focusRect);
                    }
                }
                camera.cancelAutoFocus();
                setParameters(camera, parameters);
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, final Camera camera) {
                        if (DEBUG) {
                            Log.d(TAG, "onAutoFocus success:" + success);
                        }
                        resetFocus(camera, focusMode);
                    }
                });
            } else if (parameters.getMaxNumMeteringAreas() > 0) {
                if (!parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    Log.w(TAG, "handleFocusMetering: not support focus");
//                    return; //cannot autoFocus
                }
                parameters.setMeteringAreas(meteringAreas);
                if (DEBUG) {
                    Log.d(TAG, "handleFocusMetering: setMeteringAreas 2 " + focusRect);
                }
                camera.cancelAutoFocus();
                setParameters(camera, parameters);
                camera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (DEBUG) {
                            Log.d(TAG, "onAutoFocus success:" + success);
                        }
                        resetFocus(camera, focusMode);
                    }
                });
            } else {
                camera.autoFocus(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "handleFocusMetering: ", e);
        }
    }

    private static void resetFocus(final Camera camera, final String focusMode) {
        ThreadHelper.getInstance().removeUiAllTasks();
        ThreadHelper.getInstance().runOnUiPostDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    camera.cancelAutoFocus();
                    Camera.Parameters parameter = camera.getParameters();
                    parameter.setFocusMode(focusMode);
                    if (DEBUG) {
                        Log.d(TAG, "resetFocus focusMode:" + focusMode);
                    }
                    parameter.setFocusAreas(null);
                    parameter.setMeteringAreas(null);
                    setParameters(camera, parameter);
                } catch (Exception e) {
                    if (DEBUG) {
                        Log.w(TAG, "resetFocus: ", e);
                    }
                }
            }
        }, FOCUS_TIME);
    }

    private static Rect calculateTapArea(float x, float y, int width, int height, int areaSize, int cameraFacing) {
        int centerX = (int) (x / width * 2000 - 1000);
        int centerY = (int) (y / height * 2000 - 1000);

        int top = clamp(centerX - areaSize / 2);
        int bottom = clamp(top + areaSize);
        int left = clamp(centerY - areaSize / 2);
        int right = clamp(left + areaSize);
        RectF rectF = new RectF(left, top, right, bottom);
        Matrix matrix = new Matrix();
        int flipX = cameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT ? -1 : 1;
        matrix.setScale(flipX, -1);
        matrix.mapRect(rectF);
        return new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
    }

    private static int clamp(int x) {
        return x > 1000 ? 1000 : (x < -1000 ? -1000 : x);
    }

    /**
     * 查询所有相机参数
     *
     * @param camera
     * @return
     */
    public static Map<String, String> getFullCameraParameters(Camera camera) {
        Map<String, String> result = new HashMap<>(64);
        try {
            Class camClass = camera.getClass();

            // Internally, Android goes into native code to retrieve this String
            // of values
            Method getNativeParams = camClass.getDeclaredMethod("native_getParameters");
            getNativeParams.setAccessible(true);

            // Boom. Here's the raw String from the hardware
            String rawParamsStr = (String) getNativeParams.invoke(camera);

            // But let's do better. Here's what Android uses to parse the
            // String into a usable Map -- a simple ';' StringSplitter, followed
            // by splitting on '='
            //
            // Taken from Camera.Parameters unflatten() method
            TextUtils.StringSplitter splitter = new TextUtils.SimpleStringSplitter(';');
            splitter.setString(rawParamsStr);

            for (String kv : splitter) {
                int pos = kv.indexOf('=');
                if (pos == -1) {
                    continue;
                }
                String k = kv.substring(0, pos);
                String v = kv.substring(pos + 1);
                result.put(k, v);
            }

            // And voila, you have a map of ALL supported parameters
            return result;
        } catch (Exception ex) {
            Log.e(TAG, "ex:", ex);
        }

        // If there was any error, just return an empty Map
        Log.e(TAG, "Unable to retrieve parameters from Camera.");
        return result;
    }

}
