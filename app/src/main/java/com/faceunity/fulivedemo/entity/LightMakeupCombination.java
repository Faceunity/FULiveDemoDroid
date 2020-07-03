package com.faceunity.fulivedemo.entity;

import com.faceunity.entity.LightMakeupItem;

import java.util.List;

/**
 * 轻美妆妆容组合
 *
 * @author Richie on 2018.11.15
 */
public class LightMakeupCombination {
    private List<LightMakeupItem> mMakeupItems;
    private int nameId;
    private int iconId;

    public LightMakeupCombination(List<LightMakeupItem> makeupItems, int nameId, int iconId) {
        mMakeupItems = makeupItems;
        this.nameId = nameId;
        this.iconId = iconId;
    }

    public List<LightMakeupItem> getMakeupItems() {
        return mMakeupItems;
    }

    public void setMakeupItems(List<LightMakeupItem> makeupItems) {
        mMakeupItems = makeupItems;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    @Override
    public String toString() {
        return "LightMakeupCombination{" +
                "MakeupItems=" + mMakeupItems +
                ", name='" + nameId + '\'' +
                ", iconId=" + iconId +
                '}';
    }
}
