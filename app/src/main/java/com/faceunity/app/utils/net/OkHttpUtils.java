package com.faceunity.app.utils.net;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * OkHttp 工具类
 *
 * @author Richie on 2018.12.19
 */
public final class OkHttpUtils {
    private static final String NETWORK_FAILURE_MESSAGE = "网络访问失败";
    private static final String PARSE_FAILURE_MESSAGE = "数据解析失败";
    private static final String DOWNLOAD_FAILURE_MESSAGE = "文件下载失败";
    private static final String RESPONSE_FAILURE_MESSAGE = "响应错误 ";
    private static final String UPLOAD_FAILURE_MESSAGE = "文件上传失败 ";
    private static final int TIMEOUT = 10;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private OkHttpClient mOkHttpClient;
    private Context mContext;

    private OkHttpUtils() {
    }

    public static OkHttpUtils getInstance() {
        return OkHttpUtilsHolder.INSTANCE;
    }

    /**
     * 初始化 OkHttp
     *
     * @param context
     * @param debug
     */
    public void init(@NonNull Context context, boolean debug) {
        mContext = context.getApplicationContext();
        OkLogger.debug(debug);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT * 5, TimeUnit.SECONDS)
                .cookieJar(new CookieJar() {
                    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                        cookieStore.put(httpUrl.host(), list);
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        List<Cookie> cookies = cookieStore.get(httpUrl.host());
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                });
        if (debug) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(@NotNull String message) {
                    OkLogger.v(message);
                }
            });
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(logging);
        }
//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory();
//        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        builder.hostnameVerifier(HttpsUtils.UnSafeHostnameVerifier);
        mOkHttpClient = builder.build();
    }

    public Context getContext() {
        return mContext;
    }

    public void getAsString(@NonNull String url, @NonNull OkHttpCallback<String> callback) {
        getAsString(url, null, callback);
    }

    /**
     * 发送 Get 请求，返回值是字符串
     *
     * @param url
     * @param paramMap
     * @param callback
     */
    public void getAsString(@NonNull String url, Map<String, String> paramMap, @NonNull OkHttpCallback<String> callback) {
        if (paramMap != null) {
            url = HttpUtils.attachHttpGetParams(url, paramMap);
        }
        Request request = buildGetRequest(url);
        newStringCall(callback, request);
    }

    /**
     * 发送 Get 请求，返回值是数据实体
     *
     * @param url
     * @param callback
     * @param <T>
     */
    public <T> void getAsEntity(@NonNull String url, @NonNull final OkHttpCallback<T> callback) {
        getAsEntity(url, null, callback);
    }

    /**
     * 发送 Get 请求，返回值是数据实体
     *
     * @param url
     * @param paramMap
     * @param callback
     * @param <T>
     */
    public <T> void getAsEntity(@NonNull String url, Map<String, String> paramMap, @NonNull final OkHttpCallback<T> callback) {
        if (paramMap != null) {
            url = HttpUtils.attachHttpGetParams(url, paramMap);
        }
        Request request = buildGetRequest(url);
        newEntityCall(callback, request);
    }

    /**
     * 发送 Post 键值对，表单数据，返回字符串
     *
     * @param url
     * @param paramMap
     * @param callback
     */
    public void postKeyValueAsString(@NonNull String url, Map<String, String> paramMap, @NonNull OkHttpCallback<String> callback) {
        Request request = buildPostRequest(url, paramMap);
        newStringCall(callback, request);
    }

    /**
     * 发送 Post 键值对，表单数据，返回数据实体
     *
     * @param url
     * @param paramMap
     * @param callback
     */
    public <T> void postKeyValueAsEntity(@NonNull String url, Map<String, String> paramMap, @NonNull OkHttpCallback<T> callback) {
        Request request = buildPostRequest(url, paramMap);
        newEntityCall(callback, request);
    }

    /**
     * 发送 Post JSON 数据，返回值s字符串
     *
     * @param url
     * @param jsonStr
     * @param callback
     */
    public void postJsonAsString(@NonNull String url, @NonNull String jsonStr, @NonNull OkHttpCallback<String> callback) {
        RequestBody requestBody = RequestBody.create(HttpUtils.MEDIA_TYPE_JSON, jsonStr);
        Request request = buildPostRequest(url, requestBody);
        newStringCall(callback, request);
    }

    /**
     * 发送 Post JSON 数据，返回值是数据实体
     *
     * @param url
     * @param jsonStr
     * @param callback
     */
    public <T> void postJsonAsEntity(@NonNull String url, @NonNull String jsonStr, @NonNull OkHttpCallback<T> callback) {
        RequestBody requestBody = RequestBody.create(HttpUtils.MEDIA_TYPE_JSON, jsonStr);
        Request request = buildPostRequest(url, requestBody);
        newEntityCall(callback, request);
    }

    /**
     * 下载文件，并且回调到主线程
     *
     * @param url
     * @param file
     * @param callback
     */
    public void downloadFile(@NonNull String url, @NonNull final File file, @NonNull final OkHttpCallback<File> callback) {
        realDownloadFile(url, file, callback, true);
    }

    /**
     * 下载文件，回调再同一线程
     *
     * @param url
     * @param file
     * @param callback
     */
    public void downloadFileCallBackExecute(@NonNull String url, @NonNull final File file, @NonNull final OkHttpCallback<File> callback) {
        realDownloadFile(url, file, callback, false);
    }

    /**
     * 下载文件 回调同步线程
     *
     * @param url
     * @param file
     * @param callback
     */
    public void realDownloadFile(@NonNull String url, @NonNull final File file, @NonNull final OkHttpCallback<File> callback, Boolean callBackInUIThread) {
        if (file.exists()) {
            if (callBackInUIThread) {
                runOnUiThread(() -> {
                    callback.onStart();
                    callback.onSuccess(file);
                    callback.onFinish();
                });
            } else {
                callback.onStart();
                callback.onSuccess(file);
                callback.onFinish();
            }
            return;
        }
        Request request = buildGetRequest(url);
        callback.onStart();
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (callBackInUIThread)
                            onRequestFailure(e, callback);
                        else
                            onRequestFailureCallBackExecute(e, callback);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            InputStream is = null;
                            BufferedOutputStream bos = null;
                            try {
                                is = response.body().byteStream();
                                bos = new BufferedOutputStream(new FileOutputStream(file));
                                byte[] bytes = new byte[10240];
                                int len;
                                while ((len = is.read(bytes)) != -1) {
                                    bos.write(bytes, 0, len);
                                }
                                bos.flush();
                                if (callBackInUIThread) {
                                    runOnUiThread(() -> callback.onSuccess(file));
                                } else {
                                    callback.onSuccess(file);
                                }
                            } catch (Exception e) {
                                OkLogger.printStackTrace(e);
                                if (callBackInUIThread) {
                                    runOnUiThread(() -> callback.onFailure(DOWNLOAD_FAILURE_MESSAGE));
                                } else {
                                    callback.onFailure(DOWNLOAD_FAILURE_MESSAGE);
                                }
                            } finally {
                                if (bos != null) {
                                    try {
                                        bos.close();
                                    } catch (IOException e) {
                                        // ignored
                                    }
                                }
                                if (is != null) {
                                    try {
                                        is.close();
                                    } catch (IOException e) {
                                        // ignored
                                    }
                                }
                                callback.onFinish();
                            }
                        } else {
                            if (callBackInUIThread) onResponseFailure(response, callback);
                            else onResponseFailureCallBackExecute(response, callback);
                        }
                    }
                });
    }

    /**
     * 上传文件
     *
     * @param url
     * @param file
     * @param callback
     */
    public void uploadFile(@NonNull String url, @NonNull File file, @NonNull final OkHttpCallback<String> callback) {
        if (!file.exists() || !file.isFile()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onStart();
                    callback.onFailure(UPLOAD_FAILURE_MESSAGE);
                    callback.onFinish();
                }
            });
            return;
        }
        RequestBody fileBody = RequestBody.create(HttpUtils.MEDIA_TYPE_STREAM, file);
        Request request = buildPostRequest(url, fileBody);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onStart();
            }
        });
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                onRequestFailure(e, callback);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (response.isSuccessful()) {
                    try {
                        final String string = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(string);
                            }
                        });
                    } catch (Exception e) {
                        OkLogger.printStackTrace(e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(PARSE_FAILURE_MESSAGE);
                            }
                        });
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFinish();
                            }
                        });
                    }
                } else {
                    onResponseFailure(response, callback);
                }
            }
        });
    }

    /**
     * 带参数上传文件
     *
     * @param url
     * @param name
     * @param file
     * @param paramMap
     * @param callback
     */
    public void uploadFile(@NonNull String url, @NonNull String name, @NonNull File file,
                           @NonNull Map<String, String> paramMap, @NonNull final OkHttpCallback<String> callback) {
        if (!file.exists() || !file.isFile()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callback.onStart();
                    callback.onFailure(UPLOAD_FAILURE_MESSAGE);
                    callback.onFinish();
                }
            });
            return;
        }
        MultipartBody.Builder mbBuilder = new MultipartBody.Builder();
        mbBuilder.setType(MultipartBody.FORM);
        Set<Map.Entry<String, String>> entries = paramMap.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            mbBuilder.addFormDataPart(entry.getKey(), entry.getValue());
        }
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
        String fileName = System.currentTimeMillis() + "." + fileExtension;
        mbBuilder.addFormDataPart(name, fileName, RequestBody.create(HttpUtils.MEDIA_TYPE_STREAM, file));
        Request request = buildPostRequest(url, mbBuilder.build());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onStart();
            }
        });
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onRequestFailure(e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        final String string = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(string);
                            }
                        });
                    } catch (Exception e) {
                        OkLogger.printStackTrace(e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailure(PARSE_FAILURE_MESSAGE);
                            }
                        });
                    } finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFinish();
                            }
                        });
                    }
                } else {
                    onResponseFailure(response, callback);
                }
            }
        });
    }

    /**
     * 取消所有网络请求
     */
    public void cancelAll() {
        List<Call> queuedCalls = mOkHttpClient.dispatcher().queuedCalls();
        for (Call call : queuedCalls) {
            call.cancel();
        }
        List<Call> runningCalls = mOkHttpClient.dispatcher().runningCalls();
        for (Call call : runningCalls) {
            call.cancel();
        }
    }

    /**
     * 根据 tag 取消请求，默认情况下，tag 就是 url
     *
     * @param tag
     */
    public void cancelTag(@NonNull String tag) {
        List<Call> queuedCalls = mOkHttpClient.dispatcher().queuedCalls();
        for (Call call : queuedCalls) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        List<Call> runningCalls = mOkHttpClient.dispatcher().runningCalls();
        for (Call call : runningCalls) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    private <T> void onRequestFailure(IOException e, @NonNull final OkHttpCallback<T> callback) {
        OkLogger.printStackTrace(e);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(NETWORK_FAILURE_MESSAGE);
                callback.onFinish();
            }
        });
    }

    private <T> void onRequestFailureCallBackExecute(IOException e, @NonNull final OkHttpCallback<T> callback) {
        OkLogger.printStackTrace(e);
        callback.onFailure(NETWORK_FAILURE_MESSAGE);
        callback.onFinish();
    }

    @NonNull
    private Request buildGetRequest(@NonNull String url) {
        return new Request.Builder()
                .get()
                .url(url)
                .build();
    }

    @NonNull
    private Request buildPostRequest(@NonNull String url, RequestBody requestBody) {
        return new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
    }

    @NonNull
    private Request buildPostRequest(@NonNull String url, Map<String, String> paramMap) {
        FormBody.Builder builder = new FormBody.Builder();
        if (paramMap != null && paramMap.size() > 0) {
            Set<Map.Entry<String, String>> entries = paramMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        RequestBody formBody = builder.build();
        return new Request.Builder()
                .post(formBody)
                .url(url)
                .build();
    }

    private void newStringCall(@NonNull final OkHttpCallback<String> callback, Request request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onStart();
            }
        });
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        onRequestFailure(e, callback);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            try {
                                final String string = response.body().string();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(string);
                                    }
                                });
                            } catch (Exception e) {
                                OkLogger.printStackTrace(e);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFailure(PARSE_FAILURE_MESSAGE);
                                    }
                                });
                            } finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFinish();
                                    }
                                });
                            }
                        } else {
                            onResponseFailure(response, callback);
                        }
                    }
                });
    }

    private <T> void newEntityCall(@NonNull final OkHttpCallback<T> callback, Request request) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onStart();
            }
        });
        mOkHttpClient.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        onRequestFailure(e, callback);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        if (response.isSuccessful()) {
                            try {
                                String string = response.body().string();
                                Type type = callback.getClass().getGenericSuperclass();
                                Type[] params = ((ParameterizedType) type).getActualTypeArguments();
                                Class<T> responseClass = (Class<T>) params[0];
                                final T t = GsonConverter.jsonToBean(string, responseClass);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onSuccess(t);
                                    }
                                });
                            } catch (Exception e) {
                                OkLogger.printStackTrace(e);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFailure(PARSE_FAILURE_MESSAGE);
                                    }
                                });
                            } finally {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        callback.onFinish();
                                    }
                                });
                            }
                        } else {
                            onResponseFailure(response, callback);
                        }
                    }
                });
    }

    private void runOnUiThread(Runnable runnable) {
        if (Thread.currentThread() == mMainHandler.getLooper().getThread()) {
            runnable.run();
        } else {
            mMainHandler.post(runnable);
        }
    }

    private <T> void onResponseFailure(final Response response, @NonNull final OkHttpCallback<T> callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(RESPONSE_FAILURE_MESSAGE + response.code() + ":" + response.message());
                callback.onFinish();
            }
        });
    }

    private <T> void onResponseFailureCallBackExecute(final Response response, @NonNull final OkHttpCallback<T> callback) {
        callback.onFailure(RESPONSE_FAILURE_MESSAGE + response.code() + ":" + response.message());
        callback.onFinish();
    }

    private static class OkHttpUtilsHolder {
        private static final OkHttpUtils INSTANCE = new OkHttpUtils();
    }

    /**
     * 网络请求回调
     *
     * @param <T>
     */
    public abstract static class OkHttpCallback<T> {
        /**
         * 开始
         */
        protected void onStart() {
        }

        /**
         * 响应成功
         *
         * @param result
         */
        protected abstract void onSuccess(T result);

        /**
         * 响应失败
         *
         * @param errorMsg
         */
        protected abstract void onFailure(String errorMsg);

        /**
         * 结束
         */
        protected void onFinish() {
        }
    }

    public abstract static class ProgressOkHttpCallback extends OkHttpCallback<File> {
        /**
         * 进度
         *
         * @param current
         * @param total
         */
        protected abstract void onProgress(long current, long total);
    }

}
