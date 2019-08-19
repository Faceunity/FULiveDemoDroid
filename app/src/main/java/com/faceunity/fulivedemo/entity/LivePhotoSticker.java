package com.faceunity.fulivedemo.entity;

import java.util.Arrays;

/**
 * 表情动图，五官贴纸
 *
 * @author Richie on 2018.12.13
 */
public class LivePhotoSticker {
    // 类型
    public static final int ORGAN_TYPE_LEFT_EYE = 0;
    public static final int ORGAN_TYPE_RIGHT_EYE = 1;
    public static final int ORGAN_TYPE_NOSE = 2;
    public static final int ORGAN_TYPE_MOUTH = 3;
    public static final int ORGAN_TYPE_LEFT_EYEBROW = 4;
    public static final int ORGAN_TYPE_RIGHT_EYEBROW = 5;
    // 点位坐标 JSON 的 key
    public static final String POINTS_KEY_LEFT_EYE = "leye";
    public static final String POINTS_KEY_RIGHT_EYE = "reye";
    public static final String POINTS_KEY_NOSE = "nose";
    public static final String POINTS_KEY_MOUTH = "mouth";
    public static final String POINTS_KEY_LEFT_BROW = "lbrow";
    public static final String POINTS_KEY_RIGHT_BROW = "rbrow";
    // 数组长度
    private static final int BROW_LENGTH = 12;
    private static final int EYE_LENGTH = 16;
    private static final int NOSE_LENGTH = 24;
    private static final int MOUTH_LENGTH = 36;
    private int iconId;
    private int nameId;
    // landmark 点位
    private float[] points;
    private int organType;
    private String imagePath;

    public LivePhotoSticker() {
    }

    public LivePhotoSticker(int iconId, String imagePath, int nameId, int organType, float[] points) {
        this.iconId = iconId;
        this.imagePath = imagePath;
        this.nameId = nameId;
        this.organType = organType;
        this.points = points;
    }

    public static int getPointsLength(int type) {
        switch (type) {
            case ORGAN_TYPE_LEFT_EYE:
            case ORGAN_TYPE_RIGHT_EYE:
                return EYE_LENGTH;
            case ORGAN_TYPE_MOUTH:
                return MOUTH_LENGTH;
            case ORGAN_TYPE_NOSE:
                return NOSE_LENGTH;
            case NOSE_LENGTH:
                return NOSE_LENGTH;
            case ORGAN_TYPE_LEFT_EYEBROW:
            case ORGAN_TYPE_RIGHT_EYEBROW:
                return BROW_LENGTH;
            default:
                return 0;
        }
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getOrganType() {
        return organType;
    }

    public void setOrganType(int organType) {
        this.organType = organType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float[] getPoints() {
        return points;
    }

    public void setPoints(float[] points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "LivePhotoSticker{" +
                "iconId=" + iconId +
                ", nameId=" + nameId +
                ", organType=" + organType +
                ", imageAssetsPath='" + imagePath + '\'' +
                ", dots=" + Arrays.toString(points) +
                '}';
    }
}
