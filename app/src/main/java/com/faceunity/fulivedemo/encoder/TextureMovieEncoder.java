/*
 * Copyright 2013 Google Inc. All rights reserved.
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

package com.faceunity.fulivedemo.encoder;

import android.graphics.SurfaceTexture;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.opengl.EGLContext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.faceunity.fulivedemo.gles.EglCore;
import com.faceunity.fulivedemo.gles.FullFrameRect;
import com.faceunity.fulivedemo.gles.Texture2dProgram;
import com.faceunity.fulivedemo.gles.WindowSurface;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

/**
 * Encode a movie from frames rendered from an texture.
 * <p>
 * The object wraps an encoder running on a dedicated thread.  The various control messages
 * may be sent from arbitrary threads (typically the app UI thread).  The encoder thread
 * manages both sides of the encoder (feeding and draining); the only external input is
 * the GL texture.
 * <p>
 * The design is complicated slightly by the need to create an EGL context that shares state
 * with a view that gets restarted if (say) the device orientation changes.  When the view
 * in question is a GLSurfaceView, we don't have full control over the EGL context creation
 * on that side, so we have to bend a bit backwards here.
 * <p>
 * To use:
 * <ul>
 * <li>create TextureMovieEncoder object
 * <li>create an EncoderConfig
 * <li>call TextureMovieEncoder#startRecording() with the config
 * <li>call TextureMovieEncoder#setTextureId() with the texture object that receives frames
 * <li>for each frame, after latching it with SurfaceTexture#updateTexImage(),
 * call TextureMovieEncoder#frameAvailable().
 * </ul>
 * <p>
 * TODO: tweak the API (esp. textureId) so it's less awkward for simple use cases.
 */
public class TextureMovieEncoder {
    private static final String TAG = "TextureMovieEncoder";
    private static final boolean VERBOSE = false;

    private static final int MSG_START_RECORDING = 0;
    private static final int MSG_STOP_RECORDING = 1;
    private static final int MSG_FRAME_AVAILABLE = 2;
    private static final int MSG_SET_TEXTURE_ID = 3;
    private static final int MSG_UPDATE_SHARED_CONTEXT = 4;
    private static final int MSG_QUIT = 5;

    // ----- accessed exclusively by encoder thread -----
    private WindowSurface mInputWindowSurface;
    private EglCore mEglCore;
    private FullFrameRect mFullScreen;
    private int mTextureId;
    private int mFrameNum;
    private VideoEncoderCore mVideoEncoder;
    private AudioEncoderCore mAudioEncoder;
    private MediaMuxerWrapper mMuxer;

    // ----- accessed by multiple threads -----
    private volatile VideoEncoderHandler mHandler;

    private Object mReadyFence = new Object();      // guards ready/running
    private boolean mReady;
    private boolean mRunning;

    public static final int IN_RECORDING = 1;
    public static final int START_RECORDING = 2;
    public static final int STOP_RECORDING = 3;
    public static final int NONE_RECORDING = 4;
    public static final int PREPARE_RECORDING = 5;
    private int mRecordingStatus = NONE_RECORDING;

    private OnEncoderStatusUpdateListener onEncoderStatusUpdateListener;

    private long firstTimeStampBase = 0;
    private long firstNanoTime = 0;

    private int texture;
    private int frameBuffer;

    private int mWidth, mHeight;

    public boolean checkRecordingStatus(int recordingStatus) {
        return mRecordingStatus == recordingStatus;
    }

    public TextureMovieEncoder() {
        mRecordingStatus = START_RECORDING;
    }


    /**
     * Encoder configuration.
     * <p>
     * Object is immutable, which means we can safely pass it between threads without
     * explicit synchronization (and don't need to worry about it getting tweaked out from
     * under us).
     * <p>
     * TODO: make frame rate and iframe interval configurable?  Maybe use builder pattern
     * with reasonable defaults for those and bit rate.
     */
    public static class EncoderConfig {
        final File mOutputFile;
        final int mWidth;
        final int mHeight;
        final int mBitRate;
        final EGLContext mEglContext;
        final long firstTimeStampBase;

        public EncoderConfig(File outputFile, int width, int height, int bitRate,
                             EGLContext sharedEglContext, long firstTimeStamp) {
            mOutputFile = outputFile;
            mWidth = width;
            mHeight = height;
            mBitRate = bitRate;
            mEglContext = sharedEglContext;
            firstTimeStampBase = firstTimeStamp;
        }

        @Override
        public String toString() {
            return "EncoderConfig: " + mWidth + "x" + mHeight + " @" + mBitRate +
                    " to '" + mOutputFile.toString() + "' ctxt=" + mEglContext;
        }
    }

    /**
     * Tells the video recorder to start recording.  (Call from non-encoder thread.)
     * <p>
     * Creates a new thread, which will create an encoder using the provided configuration.
     * <p>
     * Returns after the recorder thread has started and is ready to accept Messages.  The
     * encoder may not yet be fully configured.
     */
    public void startRecording(EncoderConfig config) {
        mWidth = config.mWidth;
        mHeight = config.mHeight;

        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        texture = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mWidth, mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        int[] framebuffers = new int[1];
        GLES20.glGenFramebuffers(1, framebuffers, 0);

        frameBuffer = framebuffers[0];

        Log.d(TAG, "Encoder: startRecording()");
        mRecordingStatus = PREPARE_RECORDING;
        firstTimeStampBase = config.firstTimeStampBase;
        firstNanoTime = System.nanoTime();

        synchronized (mReadyFence) {
            if (mRunning) {
                Log.w(TAG, "Encoder thread already running");
                return;
            }
            mRunning = true;
            new VideoThread("TextureMovieVideoEncoder").start();
            new AudioThread().start();
            while (!mReady) {
                try {
                    mReadyFence.wait();
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_START_RECORDING, config));
    }

    /**
     * Tells the video recorder to stop recording.  (Call from non-encoder thread.)
     * <p>
     * Returns immediately; the encoder/muxer may not yet be finished creating the movie.
     * <p>
     * TODO: have the encoder thread invoke a callback on the UI thread just before it shuts down
     * so we can provide reasonable status UI (and let the caller know that movie encoding
     * has completed).
     */
    public void stopRecording() {
        GLES20.glDeleteFramebuffers(1, new int[]{frameBuffer}, 0);
        GLES20.glDeleteTextures(1, new int[]{texture}, 0);
        frameBuffer = 0;
        texture = 0;

        mRecordingStatus = NONE_RECORDING;

        mHandler.sendMessage(mHandler.obtainMessage(MSG_STOP_RECORDING));
        mHandler.sendMessage(mHandler.obtainMessage(MSG_QUIT));
        // We don't know when these will actually finish (or even start).  We don't want to
        // delay the UI thread though, so we return immediately.
    }

    /**
     * Returns true if recording has been started.
     */
    public boolean isRecording() {
        synchronized (mReadyFence) {
            return mRunning;
        }
    }

    /**
     * Tells the video recorder to refresh its EGL surface.  (Call from non-encoder thread.)
     */
    public void updateSharedContext(EGLContext sharedContext) {
        mHandler.sendMessage(mHandler.obtainMessage(MSG_UPDATE_SHARED_CONTEXT, sharedContext));
    }

    /**
     * Tells the video recorder that a new frame is available.  (Call from non-encoder thread.)
     * <p>
     * This function sends a message and returns immediately.  This isn't sufficient -- we
     * don't want the caller to latch a new frame until we're done with this one -- but we
     * can get away with it so long1 as the input frame rate is reasonable and the encoder
     * thread doesn't stall.
     * <p>
     * TODO: either block here until the texture has been rendered onto the encoder surface,
     * or have a separate "block if still busy" method that the caller can execute immediately
     * before it calls updateTexImage().  The latter is preferred because we don't want to
     * stall the caller while this thread does work.
     */
    public void frameAvailable(SurfaceTexture st) {
        synchronized (mReadyFence) {
            if (!mReady) {
                return;
            }
        }

        float[] transform = new float[16];      // TODO - avoid alloc every frame
        Matrix.setIdentityM(transform, 0);
        long timestamp = st.getTimestamp();
        if (timestamp == 0) {
            // Seeing this after device is toggled off/on with power button.  The
            // first frame back has a zero timestamp.
            //
            // MPEG4Writer thinks this is cause to abort() in native code, so it's very
            // important that we just ignore the frame.
            Log.w(TAG, "HEY: got SurfaceTexture with timestamp of zero");
            return;
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE,
                (int) (timestamp >> 32), (int) timestamp, transform));
    }

    /**
     * Tells the video recorder what texture name to use.  This is the external texture that
     * we're receiving camera previews in.  (Call from non-encoder thread.)
     * <p>
     * TODO: do something less clumsy
     */
    public void setTextureId(FullFrameRect mFullScreenFUDisplay, int fuTex, float[] mtx) {
        if (texture != 0) {
            int viewport[] = new int[4];
            GLES20.glGetIntegerv(GLES20.GL_VIEWPORT, viewport, 0);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);

            GLES20.glViewport(0, 0, mWidth, mHeight);

            if (mFullScreenFUDisplay != null) mFullScreenFUDisplay.drawFrame(fuTex, mtx);

            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

            GLES20.glViewport(viewport[0], viewport[1], viewport[2], viewport[3]);

            synchronized (mReadyFence) {
                if (!mReady) {
                    return;
                }
            }
            mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_TEXTURE_ID, texture, 0, null));
        }
    }

    private class VideoThread extends Thread {
        public VideoThread(String name) {
            super(name);
        }

        /**
         * Encoder thread entry point.  Establishes Looper/Handler and waits for messages.
         * <p>
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            // Establish a Looper for this thread, and define a Handler for it.
            Looper.prepare();
            synchronized (mReadyFence) {
                mHandler = new VideoEncoderHandler(TextureMovieEncoder.this);
                mReady = true;
                mReadyFence.notify();
            }
            Looper.loop();

            Log.d(TAG, "Encoder thread exiting");
            synchronized (mReadyFence) {
                mReady = mRunning = false;
                mHandler = null;
            }
        }
    }

    /**
     * Handles encoder state change requests.  The handler is created on the encoder thread.
     */
    private static class VideoEncoderHandler extends Handler {
        private WeakReference<TextureMovieEncoder> mWeakEncoder;

        public VideoEncoderHandler(TextureMovieEncoder encoder) {
            mWeakEncoder = new WeakReference<TextureMovieEncoder>(encoder);
        }

        @Override  // runs on encoder thread
        public void handleMessage(Message inputMessage) {
            int what = inputMessage.what;
            Object obj = inputMessage.obj;

            TextureMovieEncoder encoder = mWeakEncoder.get();
            if (encoder == null) {
                Log.w(TAG, "VideoEncoderHandler.handleMessage: encoder is null");
                return;
            }

            switch (what) {
                case MSG_START_RECORDING:
                    encoder.handleStartRecording((EncoderConfig) obj);
                    break;
                case MSG_STOP_RECORDING:
                    encoder.handleStopRecording();
                    break;
                case MSG_FRAME_AVAILABLE:
                    long timestamp = (((long) inputMessage.arg1) << 32) |
                            (((long) inputMessage.arg2) & 0xffffffffL);
                    encoder.handleFrameAvailable((float[]) obj, timestamp);
                    break;
                case MSG_SET_TEXTURE_ID:
                    encoder.handleSetTexture(inputMessage.arg1);
                    break;
                case MSG_UPDATE_SHARED_CONTEXT:
                    encoder.handleUpdateSharedContext((EGLContext) inputMessage.obj);
                    break;
                case MSG_QUIT:
                    Looper.myLooper().quit();
                    break;
                default:
                    throw new RuntimeException("Unhandled msg what=" + what);
            }
        }
    }

    private EncoderConfig config = null;

    /**
     * Starts recording.
     */
    private void handleStartRecording(EncoderConfig config) {
        Log.d(TAG, "handleStartRecording " + config);
        this.config = config;
        mFrameNum = 0;
        prepareEncoder(config.mEglContext, config.mWidth, config.mHeight, config.mBitRate,
                config.mOutputFile);
        mRequestStop = false;
        if (onEncoderStatusUpdateListener != null) {
            onEncoderStatusUpdateListener.onStartSuccess();
        }
    }

    /**
     * Handles notification of an available frame.
     * <p>
     * The texture is rendered onto the encoder's input surface.
     * <p>
     *
     * @param transform      The texture transform, from SurfaceTexture.
     * @param timestampNanos The frame's timestamp, from SurfaceTexture.
     */
    private void handleFrameAvailable(float[] transform, long timestampNanos) {
        if (texture != 0) {
            if (VERBOSE) Log.e(TAG, "handleFrameAvailable " + timestampNanos);
            if (VERBOSE) Log.d(TAG, "handleFrameAvailable tr=" + transform);
            try {
                mVideoEncoder.drainEncoder(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GLES20.glViewport(0, 0, config.mWidth, config.mHeight);

            synchronized (TextureMovieEncoder.class) {
                mFullScreen.drawFrame(mTextureId, transform);
            }

            mInputWindowSurface.setPresentationTime(getPTSUs() * 1000L);
            mInputWindowSurface.swapBuffers();
        }
    }

    /**
     * Handles a request to stop encoding.
     */
    private void handleStopRecording() {
        Log.d(TAG, "handleStopRecording");
        try {
            mVideoEncoder.drainEncoder(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mRequestStop = true; //Audio stop
        releaseEncoder();
        Log.e(TAG, "handleStopRecording before stop success");
        while (!stopEncoderSuccess) {
            synchronized (stopEncoderFence) {
                try {
                    stopEncoderFence.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        stopEncoderSuccess = false;
        if (onEncoderStatusUpdateListener != null) {
            onEncoderStatusUpdateListener.onStopSuccess();
        }
    }

    /**
     * Sets the texture name that SurfaceTexture will use when frames are received.
     */
    private void handleSetTexture(int id) {
        //Log.d(TAG, "handleSetTexture " + id);
        mTextureId = id;
    }

    /**
     * Tears down the EGL surface and context we've been using to feed the MediaCodec input
     * surface, and replaces it with a new one that shares with the new context.
     * <p>
     * This is useful if the old context we were sharing with went away (maybe a GLSurfaceView
     * that got torn down) and we need to hook up with the new one.
     */
    private void handleUpdateSharedContext(EGLContext newSharedContext) {
        Log.d(TAG, "handleUpdatedSharedContext " + newSharedContext);

        // Release the EGLSurface and EGLContext.
        mInputWindowSurface.releaseEglSurface();
        mFullScreen.release(false);
        mEglCore.release();

        // Create a new EGLContext and recreate the window surface.
        mEglCore = new EglCore(newSharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface.recreate(mEglCore);
        mInputWindowSurface.makeCurrent();

        // Create new programs and such for the new context.
        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
    }

    private final Object prepareEncoderFence = new Object();
    private boolean prepareEncoderReady = false;
    private final Object stopEncoderFence = new Object();
    private boolean stopEncoderSuccess = false;
    /**
     * For drawing texture to hw encode, init egl related.
     *
     * @param sharedContext
     * @param width
     * @param height
     * @param bitRate
     * @param outputFile
     */
    private void prepareEncoder(EGLContext sharedContext, int width, int height, int bitRate,
                                File outputFile) {
        try {
            // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
            // because our MediaFormat doesn't have the Magic Goodies.  These can only be
            // obtained from the encoder after it has started processing data.
            //
            // We're not actually interested in multiplexing audio.  We just want to convert
            // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
            //mMuxer = new MediaMuxer(outputFile.toString(),
            //      MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mMuxer = new MediaMuxerWrapper(outputFile.toString());
            mVideoEncoder = new VideoEncoderCore(width, height, bitRate, mMuxer);
            mAudioEncoder = new AudioEncoderCore(mMuxer);
            synchronized (prepareEncoderFence) {
                prepareEncoderReady = true;
                prepareEncoderFence.notify();
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        mEglCore = new EglCore(sharedContext, EglCore.FLAG_RECORDABLE);
        mInputWindowSurface = new WindowSurface(mEglCore, mVideoEncoder.getInputSurface(), true);
        mInputWindowSurface.makeCurrent();

        mFullScreen = new FullFrameRect(
                new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_2D));
    }

    private void releaseEncoder() {
        mVideoEncoder.release();
        if (mInputWindowSurface != null) {
            mInputWindowSurface.release();
            mInputWindowSurface = null;
        }
        if (mFullScreen != null) {
            mFullScreen.release(false);
            mFullScreen = null;
        }
        if (mEglCore != null) {
            mEglCore.release();
            mEglCore = null;
        }

        //mAudioEncoder.release();
    }

    private boolean mRequestStop = false;

    private static final int[] AUDIO_SOURCES = new int[]{
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
    };


    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private class AudioThread extends Thread {
        @Override
        public void run() {
            if (VERBOSE) {
                Log.e(TAG, "AudioThread run");
            }
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

            int SAMPLE_RATE = 44100;
            int SAMPLES_PER_FRAME = 2048;
            int FRAMES_PER_BUFFER = 24;

            synchronized (prepareEncoderFence) {
                while (!prepareEncoderReady) {
                    try {
                        prepareEncoderFence.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            prepareEncoderReady = false;

            try {
                final int min_buffer_size = AudioRecord.getMinBufferSize(
                        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);
                int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
                if (buffer_size < min_buffer_size)
                    buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;

                AudioRecord audioRecord = null;
                for (final int source : AUDIO_SOURCES) {
                    try {
                        audioRecord = new AudioRecord(
                                source, SAMPLE_RATE,
                                AudioFormat.CHANNEL_IN_MONO,
                                AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                        if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED)
                            audioRecord = null;
                    } catch (final Exception e) {
                        audioRecord = null;
                    }
                    if (audioRecord != null) break;
                }

                if (audioRecord != null) {
                    try {
                        if (VERBOSE) {
                            Log.v(TAG, "AudioThread:start audio recording");
                        }
                        final ByteBuffer buf = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
                        int readBytes;
                        audioRecord.startRecording();
                        mRecordingStatus = IN_RECORDING;
                        try {
                            while (!mRequestStop) {
                                // read audio data from internal mic
                                buf.clear();
                                readBytes = audioRecord.read(buf, SAMPLES_PER_FRAME);
                                if (readBytes > 0) {
                                    // set audio data to encoder
                                    buf.position(readBytes);
                                    buf.flip();
                                    mAudioEncoder.encode(buf, readBytes, getPTSUs());
                                    mAudioEncoder.drainEncoder();
                                }
                            }
                            mAudioEncoder.encode(null, 0, getPTSUs());
                        } finally {
                            audioRecord.stop();
                        }
                    } finally {
                        audioRecord.release();
                        mAudioEncoder.release();
                    }
                } else {
                    Log.e(TAG, "failed to initialize AudioRecord");
                }
            } catch (final Exception e) {
                Log.e(TAG, "AudioThread#run", e);
            }
            if (VERBOSE) {
                Log.v(TAG, "AudioThread:finished");
            }

            synchronized (stopEncoderFence) {
                stopEncoderSuccess = true;
                stopEncoderFence.notify();
            }
        }
    }

    /**
     * previous presentationTimeUs for writing
     */
    private long prevOutputPTSUs = 0;

    /**
     * get next encoding presentationTimeUs
     *
     * @return
     */
    protected long getPTSUs() {
        long result = 0, thisNanoTime = System.nanoTime();

        if (firstTimeStampBase == 0) {
            result = thisNanoTime;
        } else {
            if (firstNanoTime == 0) firstNanoTime = thisNanoTime;
            long elapsedTime = thisNanoTime - firstNanoTime;
            result = firstTimeStampBase + elapsedTime;
        }

        result = result / 1000L;

        if (result < prevOutputPTSUs) {
            result = (prevOutputPTSUs - result) + result;
        }

        if (result == prevOutputPTSUs) {
            result += 100;
        }

        return prevOutputPTSUs = result;
    }

    public interface OnEncoderStatusUpdateListener {
        void onStartSuccess();
        void onStopSuccess();
    }

    public void setOnEncoderStatusUpdateListener(OnEncoderStatusUpdateListener _On_encoderStatusUpdateListener) {
        this.onEncoderStatusUpdateListener = _On_encoderStatusUpdateListener;
    }
}
