package com.faceunity.fulivedemo.gles;

/**
 * Created by lirui on 2017/4/10.
 */

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

/**
 * This class essentially represents a viewport-sized sprite that will be rendered with
 * a texture, usually from an external source like the camera or video decoder.
 */
public class CameraClipFrameRect {

    // Simple vertex shader, used for all programs.
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    // Simple fragment shader for use with "normal" 2D textures.
    private static final String FRAGMENT_SHADER_EXT =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = vec4(texture2D(sTexture, vTextureCoord).rgb, 1.0);\n" +
                    "}\n";

    /**
     * A "full" square, extending from -1 to +1 in both dimensions.  When the model/view/projection
     * matrix is identity, this will exactly cover the viewport.
     * <p>
     * The texture coordinates are Y-inverted relative to RECTANGLE.  (This seems to work out
     * right with external textures from SurfaceTexture.)
     */
    private static final float FULL_RECTANGLE_COORDS[] = {
            -1.0f, -1.0f,   // 0 bottom left
            1.0f, -1.0f,   // 1 bottom right
            -1.0f, 1.0f,   // 2 top left
            1.0f, 1.0f,   // 3 top right
    };

    //FULL_RECTANGLE_COORDS[0] left
    //FULL_RECTANGLE_COORDS[4] left

    //FULL_RECTANGLE_COORDS[2] right
    //FULL_RECTANGLE_COORDS[6] right

    //FULL_RECTANGLE_COORDS[1] bottom
    //FULL_RECTANGLE_COORDS[3] bottom

    //FULL_RECTANGLE_COORDS[5] top
    //FULL_RECTANGLE_COORDS[7] bottom

    static float clipTop = 0.1f;
    static float clipBottom = 0.9f;

    private static final float FULL_RECTANGLE_TEX_COORDS[] = {
            0.0f, clipTop,     // 0 bottom left
            1.0f, clipTop,     // 1 bottom right
            0.0f, clipBottom,     // 2 top left
            1.0f, clipBottom      // 3 top right
    };
    private final FloatBuffer FULL_RECTANGLE_BUF;
    private final FloatBuffer FULL_RECTANGLE_TEX_BUF;

    //private final Drawable2d mRectDrawable = new Drawable2d(Drawable2d.Prefab.FULL_RECTANGLE);
    //private Texture2dProgram mProgram;

    // Handles to the GL program and various components of it.
    private int mProgramHandle;
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;
    private int maPositionLoc;
    private int maTextureCoordLoc;

    private FloatBuffer mVertexArray;
    private FloatBuffer mTexCoordArray;
    private int mVertexCount;
    private int mCoordsPerVertex;
    private int mVertexStride;
    private int mTexCoordStride;

    private static final int SIZEOF_FLOAT = 4;

    private static final String TAG = "CameraClipFrameRect";

    /**
     * Prepares the object.
     */
    public CameraClipFrameRect(float x, float y) {
        Log.d(TAG, "X : " + x + " Y : " + y);
        // prepare shaders and OpenGL program
        int vertexShader = GlUtil.loadShader(
                GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
        int fragmentShader = GlUtil.loadShader(
                GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_EXT);

        mProgramHandle = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgramHandle, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgramHandle, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgramHandle);                  // create OpenGL program executables

        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");

        /**
         * private static final float FULL_RECTANGLE_COORDS[] = {
         -1.0f, -1.0f,   // 0 bottom left
         1.0f, -1.0f,   // 1 bottom right
         -1.0f, 1.0f,   // 2 top left
         1.0f, 1.0f,   // 3 top right
         };
         */
        //left
        //FULL_RECTANGLE_COORDS[0] = FULL_RECTANGLE_COORDS[4] = -1.0f;
        //right, full screen x
        FULL_RECTANGLE_COORDS[2] = FULL_RECTANGLE_COORDS[6] = -1.0f + 2 * x;
        //bottom full screen y
        FULL_RECTANGLE_COORDS[1] = FULL_RECTANGLE_COORDS[3] = 1.0f - 2 * y;
        //top
        //FULL_RECTANGLE_COORDS[5] = FULL_RECTANGLE_COORDS[7] = -1.0f;

        FULL_RECTANGLE_BUF = GlUtil.createFloatBuffer(FULL_RECTANGLE_COORDS);
        FULL_RECTANGLE_TEX_BUF = GlUtil.createFloatBuffer(FULL_RECTANGLE_TEX_COORDS);

        mVertexArray = FULL_RECTANGLE_BUF;
        mTexCoordArray = FULL_RECTANGLE_TEX_BUF;
        mCoordsPerVertex = 2;
        mVertexStride = mCoordsPerVertex * SIZEOF_FLOAT;
        mVertexCount = FULL_RECTANGLE_COORDS.length / mCoordsPerVertex;
    }

    /**
     * Releases resources.
     * <p>
     * This must be called with the appropriate EGL context current (i.e. the one that was
     * current when the constructor was called).  If we're about to destroy the EGL context,
     * there's no value in having the caller make it current just to do this cleanup, so you
     * can pass a flag that will tell this function to skip any EGL-context-specific cleanup.
     */
    public void release(boolean doEglCleanup) {
        /*if (mProgram != null) {
            if (doEglCleanup) {
                mProgram.release();
            }
            mProgram = null;
        }*/
    }

    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId, float[] texMatrix) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.
        GlUtil.checkGlError("draw start");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, GlUtil.IDENTITY_MATRIX, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, mCoordsPerVertex,
                GLES20.GL_FLOAT, false, mVertexStride, mVertexArray);
        GlUtil.checkGlError("glVertexAttribPointer");

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, mTexCoordStride, mTexCoordArray);
        GlUtil.checkGlError("glVertexAttribPointer");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mVertexCount);
        GlUtil.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glUseProgram(0);
    }
}
