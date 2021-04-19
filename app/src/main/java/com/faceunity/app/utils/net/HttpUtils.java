package com.faceunity.app.utils.net;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;

import androidx.annotation.NonNull;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import okhttp3.MediaType;

/**
 * @author Richie on 2018.12.22
 */
public final class HttpUtils {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain; charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    public static final String HEAD_KEY_USER_AGENT = "User-Agent";
    private static String sUserAgent;

    private HttpUtils() {
    }

    /**
     * 为 HttpGet 的 url 方便的添加键值对参数
     *
     * @param url
     * @param name
     * @param value
     * @return
     */
    public static String attachHttpGetParam(@NonNull String url, @NonNull String name, @NonNull String value) {
        return url + "?" + name + "=" + value;
    }

    /**
     * 为 HttpGet 的 url 方便的添加键值对参数
     *
     * @param url
     * @param params
     * @return
     */
    public static String attachHttpGetParams(@NonNull String url, @NonNull Map<String, String> params) {
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        Set<Map.Entry<String, String>> entries = params.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            try {
                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                OkLogger.printStackTrace(e);
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 通过 ‘？’ 和 ‘/’ 判断文件名
     * http://mavin-manzhan.oss-cn-hangzhou.aliyuncs.com/1486631099150286149.jpg?x-oss-process=image/watermark,image_d2F0ZXJtYXJrXzIwMF81MC5wbmc
     */
    public static String getUrlFileName(String url) {
        String filename = null;
        String[] strings = url.split("/");
        for (String string : strings) {
            if (string.contains("?")) {
                int endIndex = string.indexOf("?");
                if (endIndex != -1) {
                    filename = string.substring(0, endIndex);
                    return filename;
                }
            }
        }
        if (strings.length > 0) {
            filename = strings[strings.length - 1];
        }
        return filename;
    }

    /**
     * 根据文件名获取MIME类型
     */
    public static MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        fileName = fileName.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MEDIA_TYPE_STREAM;
        }
        return MediaType.parse(contentType);
    }

    /**
     * User-Agent: Mozilla/5.0 (Linux; U; Android 5.0.2; zh-cn; Redmi Note 3 Build/LRX22G) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Mobile Safari/537.36
     */
    public static String getUserAgent() {
        if (TextUtils.isEmpty(sUserAgent)) {
            String webUserAgent = null;
            try {
                Class<?> sysResCls = Class.forName("com.android.internal.R$string");
                Field webUserAgentField = sysResCls.getDeclaredField("web_user_agent");
                Integer resId = (Integer) webUserAgentField.get(null);
                webUserAgent = OkHttpUtils.getInstance().getContext().getString(resId);
            } catch (Exception e) {
                // maybe failed on Android P or higher version
                OkLogger.printStackTrace(e);
            }

            if (TextUtils.isEmpty(webUserAgent)) {
                try {
                    webUserAgent = WebSettings.getDefaultUserAgent(OkHttpUtils.getInstance().getContext());
                } catch (Exception e) {
                    OkLogger.printStackTrace(e);
                }
            }

            if (TextUtils.isEmpty(webUserAgent)) {
                try {
                    webUserAgent = System.getProperty("http.agent");
                } catch (Exception e) {
                    OkLogger.printStackTrace(e);
                }
            }

            if (TextUtils.isEmpty(webUserAgent)) {
                webUserAgent = "okhttp/square";
            }

            Locale locale = Locale.getDefault();
            StringBuffer buffer = new StringBuffer();
            // Add version
            final String version = Build.VERSION.RELEASE;
            if (version.length() > 0) {
                buffer.append(version);
            } else {
                // default to "1.0"
                buffer.append("1.0");
            }
            buffer.append("; ");
            final String language = locale.getLanguage();
            if (language != null) {
                buffer.append(language.toLowerCase(locale));
                final String country = locale.getCountry();
                if (!TextUtils.isEmpty(country)) {
                    buffer.append("-");
                    buffer.append(country.toLowerCase(locale));
                }
            } else {
                // default to "en"
                buffer.append("en");
            }
            // add the model for the release build
            if ("REL".equals(Build.VERSION.CODENAME)) {
                final String model = Build.MODEL;
                if (model.length() > 0) {
                    buffer.append("; ");
                    buffer.append(model);
                }
            }
            final String id = Build.ID;
            if (id.length() > 0) {
                buffer.append(" Build/");
                buffer.append(id);
            }
            sUserAgent = String.format(webUserAgent, buffer, "Mobile ");
        }
        return sUserAgent;
    }

}
