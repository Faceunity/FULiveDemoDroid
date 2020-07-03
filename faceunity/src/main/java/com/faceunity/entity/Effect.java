package com.faceunity.entity;

import java.util.Objects;

/**
 * 道具贴纸实体类
 * Created by tujh on 2018/2/7.
 */
public class Effect {
    /**
     * 类型
     */
    public static final int EFFECT_TYPE_NONE = 0;
    public static final int EFFECT_TYPE_STICKER = 1;
    public static final int EFFECT_TYPE_AR_MASK = 2;
    public static final int EFFECT_TYPE_ACTION_RECOGNITION = 3;
    public static final int EFFECT_TYPE_EXPRESSION_RECOGNITION = 4;
    public static final int EFFECT_TYPE_PORTRAIT_SEGMENT = 5;
    public static final int EFFECT_TYPE_GESTURE_RECOGNITION = 6;
    public static final int EFFECT_TYPE_ANIMOJI = 7;
    public static final int EFFECT_TYPE_PORTRAIT_DRIVE = 8;
    public static final int EFFECT_TYPE_FACE_WARP = 9;
    public static final int EFFECT_TYPE_MUSIC_FILTER = 10;
    public static final int EFFECT_TYPE_HAIR_NORMAL = 11;
    public static final int EFFECT_TYPE_HAIR_GRADIENT = 12;
    public static final int EFFECT_TYPE_PTA = 13;
    public static final int EFFECT_TYPE_BIG_HEAD = 14;

    private String bundleName;
    private int iconId;
    private String bundlePath;
    private int maxFace;
    private int type;
    private int descId;

    public Effect(String bundleName, int iconId, String bundlePath, int maxFace, int type, int descId) {
        this.bundleName = bundleName;
        this.iconId = iconId;
        this.bundlePath = bundlePath;
        this.maxFace = maxFace;
        this.type = type;
        this.descId = descId;
    }

    public String getBundleName() {
        return bundleName;
    }

    public int getIconId() {
        return iconId;
    }

    public String getBundlePath() {
        return bundlePath;
    }

    public int getMaxFace() {
        return maxFace;
    }

    public int getType() {
        return type;
    }

    public int getDescId() {
        return descId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Effect effect = (Effect) o;
        return Objects.equals(bundlePath, effect.bundlePath);
    }

    @Override
    public int hashCode() {
        return bundlePath != null ? bundlePath.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Effect{" +
                "bundleName='" + bundleName + '\'' +
                ", iconId=" + iconId +
                ", filePath='" + bundlePath + '\'' +
                ", maxFace=" + maxFace +
                ", type=" + type +
                ", descId=" + descId +
                '}';
    }
}
