package com.faceunity.entity;

/**
 * 组合妆
 *
 * @author Richie on 2019.11.11
 */
public class MakeupEntity {
    /* 日常妆 */
    public static final int TYPE_DAILY = 0;
    /* 主题妆 */
    public static final int TYPE_THEME = 1;

    private int type;
    private String bundlePath;
    private int handle;

    public MakeupEntity() {
        this.type = -1;
        this.bundlePath = "";
    }

    public MakeupEntity(int type, String bundlePath) {
        this.type = type;
        this.bundlePath = bundlePath;
    }

    public String getBundlePath() {
        return bundlePath;
    }

    public void setBundlePath(String bundlePath) {
        this.bundlePath = bundlePath;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    @Override
    public String toString() {
        return "MakeupEntity{" +
                "type=" + type +
                ", bundlePath='" + bundlePath + '\'' +
                ", handle=" + handle +
                '}';
    }
}
