package com.faceunity.fulivedemo.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Richie
 * 多线程工具类
 */
public final class ThreadHelper {
    private final Handler mMainHandler;
    private final ExecutorService mExecutorService;
    private Handler mWorkHandler;

    private ThreadHelper() {
        mMainHandler = new Handler(Looper.getMainLooper());
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ThreadHelper#".concat(String.valueOf(mCount.getAndIncrement())));
            }
        };
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCount + 1;
        int maxPoolSize = cpuCount * 2 + 1;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(128);
        mExecutorService = new ThreadPoolExecutor(corePoolSize, maxPoolSize, 10, TimeUnit.SECONDS, queue, threadFactory);
    }

    public static ThreadHelper getInstance() {
        return ThreadHelperHolder.instance;
    }

    private synchronized void ensureSubHandler() {
        if (mWorkHandler == null) {
            HandlerThread handlerThread = new HandlerThread("WorkHandler");
            handlerThread.start();
            mWorkHandler = new Handler(handlerThread.getLooper());
        }
    }

    /**
     * 无返回值的异步任务，使用线程池
     *
     * @param r
     */
    public void execute(Runnable r) {
        try {
            mExecutorService.execute(r);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 有返回值的异步任务，使用线程池
     *
     * @param task
     * @param <T>
     * @return
     */
    public <T> Future<T> submit(Callable<T> task) {
        try {
            return mExecutorService.submit(task);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 异步延时任务，使用 HandlerThread
     *
     * @param r
     * @param delayMillis
     * @return
     */
    public boolean postDelayed(Runnable r, long delayMillis) {
        ensureSubHandler();
        return mWorkHandler.postDelayed(r, delayMillis);
    }

    /**
     * 异步定时任务，使用 HandlerThread
     *
     * @param r
     * @param uptimeMillis
     * @return
     */
    public boolean postAtTime(Runnable r, long uptimeMillis) {
        ensureSubHandler();
        return mWorkHandler.postAtTime(r, uptimeMillis);
    }

    /**
     * 主线程任务
     *
     * @param r
     */
    public void runOnUiThread(Runnable r) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            r.run();
        } else {
            mMainHandler.post(r);
        }
    }

    /**
     * 主线程延时任务
     *
     * @param r
     * @param delay
     * @return
     */
    public boolean runOnUiPostDelayed(Runnable r, long delay) {
        return mMainHandler.postDelayed(r, delay);
    }

    /**
     * 主线程定时任务
     *
     * @param r
     * @param uptimeMillis
     * @return
     */
    public boolean runOnUiPostAtTime(Runnable r, long uptimeMillis) {
        return mMainHandler.postAtTime(r, uptimeMillis);
    }

    /**
     * 移除主线程的任务
     *
     * @param r
     */
    public void removeUiCallbacks(Runnable r) {
        mMainHandler.removeCallbacks(r);
    }

    /**
     * 移除异步线程的任务
     *
     * @param r
     */
    public void removeWorkCallbacks(Runnable r) {
        if (mWorkHandler != null) {
            mWorkHandler.removeCallbacks(r);
        }
    }

    /**
     * 结束线程
     */
    public void shutdown() {
        if (!mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
        if (mWorkHandler != null) {
            try {
                mWorkHandler.getLooper().getThread().interrupt();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static class ThreadHelperHolder {
        private static ThreadHelper instance = new ThreadHelper();
    }

}
