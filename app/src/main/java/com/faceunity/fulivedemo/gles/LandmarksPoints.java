/*
 * Copyright (C) 2011 The Android Open Source Project
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
package com.faceunity.fulivedemo.gles;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.Matrix;

/**
 *
 */
public class LandmarksPoints {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "uniform float uPointSize;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "  gl_PointSize = uPointSize;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mPointSizeHandle;

    private float mPointSize = 6.0f;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 2;

    public float pointsCoords[] = new float[150];
    private final int vertexCount = pointsCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    static float[] originMtx;
    static float[] flipMtx;
    static {
        originMtx = GlUtil.IDENTITY_MATRIX;
        flipMtx = Arrays.copyOf(originMtx, originMtx.length);
        //Matrix.scaleM(flipMtx, 0, 1, -1, 1);
    }

    ByteBuffer bb;
    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public LandmarksPoints() {
        // initialize vertex byte buffer for shape coordinates
        bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                pointsCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(pointsCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = GlUtil.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = GlUtil.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    public void setPointSize(float pointSize) {
        mPointSize = pointSize;
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     */
    public void draw() {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GlUtil.checkGlError("glGetUniformLocation");

        mPointSizeHandle = GLES20.glGetUniformLocation(mProgram, "uPointSize");
        GlUtil.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, originMtx, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        GLES20.glUniform1f(mPointSizeHandle, mPointSize);
        GlUtil.checkGlError("glUniform1f");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void refresh(float[] landmarksData, int fullWidth, int fullHeight, float topClipRatio, float heightClipRatio, boolean isFlip) {
        for (int i = 0; i < 150; i++) pointsCoords[i] = landmarksData[i];
        //adjust to get the coords
        for (int i = 0; i < landmarksData.length; i += 2) {
            float x, y;
            x = (isFlip ? (fullWidth - pointsCoords[i]) : pointsCoords[i]) / fullWidth;
            y = (pointsCoords[i + 1]) / fullHeight;

            //adjust corresponds to clip to camera preview and show only top left (0.4, 0.4 * 0.8)
            x = (x - topClipRatio) / heightClipRatio;
            x = x * 0.64f + 0.36f;
            y = y * 0.8f + 0.2f;

            pointsCoords[i] = -y * 1.0f;
            pointsCoords[i + 1] = x * 1.0f;
        }

        vertexBuffer.put(pointsCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
    }

    /**
     * if (mGLCameraPreviewRenderer != null) {
     float[] landmarksData = mGLCameraPreviewRenderer.getLandmarksData();
     //double sum = 0;
     //for (int i = 0; i < 150; i++) {
     //  sum += landmarksData[i];
     //}
     if (mPoints != null) {
     for (int i = 0; i < 150; i++) {
     mPoints.pointsCoords[i] = landmarksData[i];
     }
     //if (VERBOSE_LOG) {
     //  String tmp = "";
     //for (int i = 0; i < 150; i++) {
     //  tmp += landmarksData[i] + " ";
     //}
     //Logger.d(TAG, "faceinfo landmarks " + tmp);
     //}
     //adjust to get the coords
     //String landmarkRes = "";
     for (int i = 0; i < landmarksData.length; i += 2) {
     float x, y;
     x = (mPoints.pointsCoords[i]) / (CAMERA_PREVIEW_WIDTH);
     y = (mPoints.pointsCoords[i + 1]) / CAMERA_PREVIEW_HEIGHT;
     x = (x - topClipRatio) / heightClipRatio; //adjust corresponds to clip to camera preview
     x = x * 2.0f - 1.0f;
     y = y * 2.0f - 1.0f;
     mPoints.pointsCoords[i] = -y * 1.0f;
     mPoints.pointsCoords[i + 1] = x * 1.0f;
     if (VERBOSE_LOG) {
     //landmarkRes += " x " + mPoints.pointsCoords[i] + " y " + mPoints.pointsCoords[i + 1];
     //Logger.d(TAG, "adjust landmarks " + landmarkRes);
     }
     }
     }
     if (mPoints != null) {
     //MiscUtil.Logger(TAG, "draw landmarks", false);
     mPoints.draw(mCurrentCameraType != Camera.CameraInfo.CAMERA_FACING_FRONT);
     }
     }
     */
}
