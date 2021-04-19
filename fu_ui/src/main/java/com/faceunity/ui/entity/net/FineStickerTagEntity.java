package com.faceunity.ui.entity.net;

/**
 * Created on 2021/3/31 0031 16:12.
 * Author: xloger
 * Email:phoenix@xloger.com
 */
public class FineStickerTagEntity {
    private String tag;

    public FineStickerTagEntity(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "FineStickerTagEntity{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
