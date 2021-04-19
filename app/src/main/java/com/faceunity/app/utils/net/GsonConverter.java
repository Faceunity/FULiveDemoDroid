package com.faceunity.app.utils.net;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * GSON 转换器
 *
 * @author Richie on 2018.12.22
 */
public final class GsonConverter {
    private final static Gson GSON = new Gson();

    private GsonConverter() {
    }

    /**
     * json to bean
     *
     * @param json     must be JSONObject
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> T jsonToBean(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * json to bean list, without generic erase problem, recommend
     *
     * @param json     must be JSONArray
     * @param classOfT
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> classOfT) {
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        List<T> list = new ArrayList<>(array.size());
        for (JsonElement elem : array) {
            T t = GSON.fromJson(elem, classOfT);
            list.add(t);
        }
        return list;
    }

    /**
     * json to map
     *
     * @param json must be JSONObject
     * @return
     */
    public static <T> Map<String, T> jsonToMap(String json) {
        return GSON.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    /**
     * json to bean list containing map
     *
     * @param json must be JSONObject
     * @return
     */
    public static <T> List<Map<String, T>> jsonToMapList(String json) {
        return GSON.fromJson(json, new TypeToken<List<Map<String, T>>>() {
        }.getType());
    }

    /**
     * bean to json
     *
     * @param src
     * @return
     */
    public static String objectToJson(Object src) {
        return GSON.toJson(src);
    }

}