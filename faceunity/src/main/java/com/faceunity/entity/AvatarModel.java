package com.faceunity.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

/**
 * @author Richie on 2019.03.20
 */
@Entity
public class AvatarModel implements Parcelable {

    public static final Parcelable.Creator<AvatarModel> CREATOR = new Parcelable.Creator<AvatarModel>() {
        @Override
        public AvatarModel createFromParcel(Parcel source) {
            return new AvatarModel(source);
        }

        @Override
        public AvatarModel[] newArray(int size) {
            return new AvatarModel[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    @Transient
    private int iconId;
    @Transient
    private boolean isDefault;
    private String iconPath;
    // 参数配置
    private String configJson;
    // 界面位置数据
    private String uiJson;

    public AvatarModel(int iconId, boolean isDefault) {
        this.iconId = iconId;
        this.isDefault = isDefault;
    }

    @Generated(hash = 1677474030)
    public AvatarModel() {
    }

    protected AvatarModel(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.iconId = in.readInt();
        this.isDefault = in.readByte() != 0;
        this.iconPath = in.readString();
        this.configJson = in.readString();
        this.uiJson = in.readString();
    }

    @Generated(hash = 2046086955)
    public AvatarModel(Long id, String iconPath, String configJson, String uiJson) {
        this.id = id;
        this.iconPath = iconPath;
        this.configJson = configJson;
        this.uiJson = uiJson;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConfigJson() {
        return this.configJson;
    }

    public void setConfigJson(String configJson) {
        this.configJson = configJson;
    }

    public String getUiJson() {
        return uiJson;
    }

    public void setUiJson(String uiJson) {
        this.uiJson = uiJson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AvatarModel that = (AvatarModel) o;

        if (iconId != that.iconId)
            return false;
        if (iconPath != null ? !iconPath.equals(that.iconPath) : that.iconPath != null)
            return false;
        if (configJson != null ? !configJson.equals(that.configJson) : that.configJson != null)
            return false;
        return uiJson != null ? uiJson.equals(that.uiJson) : that.uiJson == null;
    }

    @Override
    public int hashCode() {
        int result = iconId;
        result = 31 * result + (iconPath != null ? iconPath.hashCode() : 0);
        result = 31 * result + (configJson != null ? configJson.hashCode() : 0);
        result = 31 * result + (uiJson != null ? uiJson.hashCode() : 0);
        return result;
    }

    public AvatarModel cloneIt() {
        AvatarModel avatarModel = new AvatarModel();
        if (this.id != null && this.id > 0) {
            avatarModel.id = this.id;
        }
        avatarModel.iconId = this.iconId;
        avatarModel.configJson = this.configJson + ""; // deep copy string
        avatarModel.uiJson = this.uiJson + "";
        avatarModel.iconPath = this.iconPath + "";
        return avatarModel;
    }

    @Override
    public String toString() {
        return "AvatarModel{" +
                "id=" + id +
                ", iconId=" + iconId +
                ", isDefault=" + isDefault +
                ", iconPath='" + iconPath + '\'' +
                ", configJson='" + configJson + '\'' +
                ", uiJson='" + uiJson + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.iconId);
        dest.writeByte(this.isDefault ? (byte) 1 : (byte) 0);
        dest.writeString(this.iconPath);
        dest.writeString(this.configJson);
        dest.writeString(this.uiJson);
    }
}
