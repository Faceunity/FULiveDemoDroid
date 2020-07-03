package com.faceunity.entity;

/**
 * 轻美妆单项妆容
 *
 * @author Richie on 2018.11.12
 */
public class LightMakeupItem {
    // 无妆
    public static final int FACE_MAKEUP_TYPE_NONE = -1;
    // 口红
    public static final int FACE_MAKEUP_TYPE_LIPSTICK = 0;
    // 腮红
    public static final int FACE_MAKEUP_TYPE_BLUSHER = 1;
    // 眉毛
    public static final int FACE_MAKEUP_TYPE_EYEBROW = 2;
    // 眼影
    public static final int FACE_MAKEUP_TYPE_EYE_SHADOW = 3;
    // 眼线
    public static final int FACE_MAKEUP_TYPE_EYE_LINER = 4;
    // 睫毛
    public static final int FACE_MAKEUP_TYPE_EYELASH = 5;
    // 美瞳
    public static final int FACE_MAKEUP_TYPE_EYE_PUPIL = 6;

    private String name;
    private String path;
    private int type;
    private int iconId;
    private int nameId;
    private float level;
    private float defaultLevel;

    public LightMakeupItem(String name, String path, int type, int nameId, int iconId, float level) {
        this(name, path, type, nameId, iconId, level, level);
    }

    public LightMakeupItem(String name, String path, int type, int nameId, int iconId, float level, float defaultLevel) {
        this.name = name;
        this.path = path;
        this.type = type;
        this.nameId = nameId;
        this.iconId = iconId;
        this.level = level;
        this.defaultLevel = defaultLevel;
    }

    public LightMakeupItem cloneSelf() {
        return new LightMakeupItem(name, path, type, nameId, iconId, level);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public float getDefaultLevel() {
        return defaultLevel;
    }

    public void setDefaultLevel(float defaultLevel) {
        this.defaultLevel = defaultLevel;
    }

    @Override
    public String toString() {
        return "LightMakeupItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", iconId=" + iconId +
                ", nameId=" + nameId +
                ", level=" + level +
                ", defaultLevel=" + defaultLevel +
                '}';
    }
}
