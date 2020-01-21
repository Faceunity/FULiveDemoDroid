package com.faceunity.fulivedemo.entity;

import android.util.SparseArray;

import com.faceunity.entity.MakeupEntity;

import java.util.Map;

/**
 * 新版美妆妆容组合
 *
 * @author Richie on 2019.06.18
 */
public class MakeupCombination {
    private int nameId;
    private int iconId;
    private MakeupEntity makeupEntity;
    private String jsonPath;
    private Map<String, Object> paramMap;
    // <type, subItem>
    private SparseArray<SubItem> subItems;

    public MakeupCombination(int nameId, int iconId, String jsonPath, MakeupEntity makeupEntity) {
        this.nameId = nameId;
        this.iconId = iconId;
        this.jsonPath = jsonPath;
        this.makeupEntity = makeupEntity;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
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

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    public SparseArray<SubItem> getSubItems() {
        return subItems;
    }

    public void setSubItems(SparseArray<SubItem> subItems) {
        this.subItems = subItems;
    }

    public MakeupEntity getMakeupEntity() {
        return makeupEntity;
    }

    public void setMakeupEntity(MakeupEntity makeupEntity) {
        this.makeupEntity = makeupEntity;
    }

    @Override
    public String toString() {
        return "MakeupCombination{" +
                "makeupEntity=" + makeupEntity +
                ", jsonPath=" + jsonPath +
                ", paramMap=" + paramMap +
                ", subItems=" + subItems +
                '}';
    }

    /**
     * 界面上默认选中的条目
     */
    public static class SubItem {
        private int type;
        private double intensity;
        private int itemPosition;
        private int colorPosition;

        public SubItem(int type, double intensity, int itemPosition, int colorPosition) {
            this.type = type;
            this.intensity = intensity;
            this.itemPosition = itemPosition;
            this.colorPosition = colorPosition;
        }

        public double getIntensity() {
            return intensity;
        }

        public void setIntensity(double intensity) {
            this.intensity = intensity;
        }

        public int getItemPosition() {
            return itemPosition;
        }

        public void setItemPosition(int itemPosition) {
            this.itemPosition = itemPosition;
        }

        public int getColorPosition() {
            return colorPosition;
        }

        public void setColorPosition(int colorPosition) {
            this.colorPosition = colorPosition;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "SubItem{" +
                    "type=" + type +
                    ", intensity=" + intensity +
                    ", itemPosition=" + itemPosition +
                    ", colorPosition=" + colorPosition +
                    '}';
        }
    }
}
