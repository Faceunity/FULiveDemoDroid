package com.faceunity.entity;

import android.text.TextUtils;

/**
 * Created by tujh on 2018/8/2.
 */
public class Makeup {

    public static final int MAKEUP_TYPE_NONE = -1;
    public static final int MAKEUP_TYPE_LIPSTICK = 0;
    public static final int MAKEUP_TYPE_BLUSHER = 1;
    public static final int MAKEUP_TYPE_EYEBROW = 2;
    public static final int MAKEUP_TYPE_EYE_SHADOW = 3;
    public static final int MAKEUP_TYPE_EYE_LINER = 4;
    public static final int MAKEUP_TYPE_EYELASH = 5;
    public static final int MAKEUP_TYPE_CONTACT_LENS = 6;

    private String bundleName;
    private int resId;
    private String path;
    private int makeupType;
    private int description;
    private float level = 1.0f;

    public Makeup(String bundleName, int resId, String path, int makeupType, int description) {
        this.bundleName = bundleName;
        this.resId = resId;
        this.path = path;
        this.makeupType = makeupType;
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

    public int makeupType() {
        return makeupType;
    }

    public void setMakeupType(int makeupType) {
        this.makeupType = makeupType;
    }

    public int description() {
        return description;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Makeup makeup = (Makeup) o;
        return !TextUtils.isEmpty(path) && path.equals(makeup.path());
    }

    @Override
    public int hashCode() {
        return !TextUtils.isEmpty(path) ? path.hashCode() : 0;
    }
}
