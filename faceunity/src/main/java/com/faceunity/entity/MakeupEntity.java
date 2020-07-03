package com.faceunity.entity;

/**
 * 美妆组合妆容
 *
 * @author Richie on 2019.11.11
 */
public class MakeupEntity {
    private String bundlePath;
    private int itemHandle;
    private boolean isNeedFlipPoints;

    public MakeupEntity(MakeupEntity makeupEntity) {
        this.bundlePath = makeupEntity.bundlePath;
        this.itemHandle = makeupEntity.itemHandle;
        this.isNeedFlipPoints = makeupEntity.isNeedFlipPoints;
    }

    public MakeupEntity(String bundlePath) {
        this.bundlePath = bundlePath;
    }

    public MakeupEntity(String bundlePath, boolean isNeedFlipPoints) {
        this.bundlePath = bundlePath;
        this.isNeedFlipPoints = isNeedFlipPoints;
    }

    public String getBundlePath() {
        return bundlePath;
    }

    public void setBundlePath(String bundlePath) {
        this.bundlePath = bundlePath;
    }

    public int getItemHandle() {
        return itemHandle;
    }

    public void setItemHandle(int itemHandle) {
        this.itemHandle = itemHandle;
    }

    public boolean isNeedFlipPoints() {
        return isNeedFlipPoints;
    }

    public void setNeedFlipPoints(boolean needFlipPoints) {
        isNeedFlipPoints = needFlipPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MakeupEntity that = (MakeupEntity) o;

        if (isNeedFlipPoints != that.isNeedFlipPoints)
            return false;
        return bundlePath != null ? bundlePath.equals(that.bundlePath) : that.bundlePath == null;
    }

    @Override
    public int hashCode() {
        int result = bundlePath != null ? bundlePath.hashCode() : 0;
        result = 31 * result + (isNeedFlipPoints ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MakeupEntity{" +
                "bundlePath='" + bundlePath + '\'' +
                ", itemHandle=" + itemHandle +
                ", isNeedFlipPoints=" + isNeedFlipPoints +
                '}';
    }
}
