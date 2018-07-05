package com.faceunity.entity;

import android.text.TextUtils;

/**
 * 本demo中道具的实体类
 * Created by tujh on 2018/2/7.
 */

public class Effect {
    public static final int EFFECT_TYPE_NONE = 0;
    public static final int EFFECT_TYPE_NORMAL = 1;
    public static final int EFFECT_TYPE_AR = 2;
    public static final int EFFECT_TYPE_FACE_CHANGE = 3;
    public static final int EFFECT_TYPE_EXPRESSION = 4;
    public static final int EFFECT_TYPE_BACKGROUND = 5;
    public static final int EFFECT_TYPE_GESTURE = 6;
    public static final int EFFECT_TYPE_PORTRAIT_LIGHT = 7;
    public static final int EFFECT_TYPE_ANIMOJI = 8;
    public static final int EFFECT_TYPE_PORTRAIT_DRIVE = 9;
    public static final int EFFECT_TYPE_FACE_WARP = 10;
    public static final int EFFECT_TYPE_MUSIC_FILTER = 11;

    private String bundleName;
    private int resId;
    private String path;
    private int maxFace;
    private int effectType;
    private String description;

    public Effect(String bundleName, int resId, String path, int maxFace, int effectType, String description) {
        this.bundleName = bundleName;
        this.resId = resId;
        this.path = path;
        this.maxFace = maxFace;
        this.effectType = effectType;
        this.description = description;
    }

    public String bundleName() {
        return bundleName;
    }

    public int resId() {
        return resId;
    }

    public String path() {
        return path;
    }

    public int maxFace() {
        return maxFace;
    }

    public int effectType() {
        return effectType;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Effect effect = (Effect) o;
        return !TextUtils.isEmpty(bundleName) && bundleName.equals(effect.bundleName());
    }

    @Override
    public int hashCode() {
        return !TextUtils.isEmpty(bundleName) ? bundleName.hashCode() : 0;
    }
}
