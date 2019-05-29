package com.faceunity.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.faceunity.utils.FileUtils;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LiuQiang on 2018.12.12
 * 保存到数据库的表情动图的数据
 */
@Entity
public class LivePhoto implements Parcelable {

    public static final Creator<LivePhoto> CREATOR = new Creator<LivePhoto>() {
        @Override
        public LivePhoto createFromParcel(Parcel source) {
            return new LivePhoto(source);
        }

        @Override
        public LivePhoto[] newArray(int size) {
            return new LivePhoto[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    // 背景图的宽
    private int width;
    // 背景图的高
    private int height;
    /**
     * 五官贴图的点位，以视图左上角为原点
     */
    @Transient
    private double[] groupPoints;
    @Transient
    private double[] groupType;
    // 五官点位 JSONArray
    private String groupPointsStr;
    // 五官类型 JSONArray
    private String groupTypeStr;
    // 背景图
    private String imagePath;
    // 五官贴纸路径 JSONArray
    private String stickerImagePath;
    // 变换矩阵 3*3，JSONArray
    private String matrix;
    // 调整过的点位，JSONArray
    private String adjustPoints;

    public LivePhoto() {
    }

    public LivePhoto(int width, int height, double[] groupPoints, double[] groupType, String imagePath,
                     String[] stickerImagePaths, float[] matrix, float[] adjustPoints) {
        this.width = width;
        this.height = height;
        this.imagePath = imagePath;
        setGroupType(groupType);
        setGroupPoints(groupPoints);
        setStickerImagePath(stickerImagePaths);
        setMatrixF(matrix);
        setAdjustPointsF(adjustPoints);
    }

    @Generated(hash = 1602637951)
    public LivePhoto(Long id, int width, int height, String groupPointsStr, String groupTypeStr,
                     String imagePath, String stickerImagePath, String matrix, String adjustPoints) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.groupPointsStr = groupPointsStr;
        this.groupTypeStr = groupTypeStr;
        this.imagePath = imagePath;
        this.stickerImagePath = stickerImagePath;
        this.matrix = matrix;
        this.adjustPoints = adjustPoints;
    }

    protected LivePhoto(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.width = in.readInt();
        this.height = in.readInt();
        this.groupPoints = in.createDoubleArray();
        this.groupType = in.createDoubleArray();
        this.groupPointsStr = in.readString();
        this.groupTypeStr = in.readString();
        this.imagePath = in.readString();
        this.stickerImagePath = in.readString();
        this.matrix = in.readString();
        this.adjustPoints = in.readString();
    }

    public static List<LivePhoto> getDefaultLivePhotos(Context context) {
        List<LivePhoto> livePhotos = new ArrayList<>(4);
        File livePhotoDir = FileUtils.getLivePhotoDir(context);
        File[] files = livePhotoDir.listFiles();
        if (files != null) {
            LivePhoto livePhoto;
            for (File file : files) {
                File[] mf = file.listFiles();
                if (mf != null) {
                    livePhoto = new LivePhoto();
                    for (File f : mf) {
                        String name = f.getName();
                        if (name.endsWith(".json")) {
                            try {
                                String s = FileUtils.readStringFromFile(f);
                                JSONObject jsonObject = new JSONObject(s);
                                int width = jsonObject.optInt("width");
                                int height = jsonObject.optInt("height");
                                JSONArray pointsArray = jsonObject.optJSONArray("group_points");
                                int len = pointsArray.length();
                                double[] groupPoints = new double[len];
                                for (int i = 0; i < len; i++) {
                                    groupPoints[i] = pointsArray.optDouble(i);
                                }
                                JSONArray typeArray = jsonObject.optJSONArray("group_type");
                                len = typeArray.length();
                                double[] groupType = new double[len];
                                for (int i = 0; i < len; i++) {
                                    groupType[i] = typeArray.optDouble(i);
                                }
                                livePhoto.setWidth(width);
                                livePhoto.setHeight(height);
                                livePhoto.setGroupPoints(groupPoints);
                                livePhoto.setGroupType(groupType);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (name.endsWith(".jpg") || name.endsWith(".png")) {
                            livePhoto.setImagePath(f.getAbsolutePath());
                        }
                    }
                    livePhotos.add(livePhoto);
                }
            }
        }
        return livePhotos;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double[] getGroupPoints() {
        if (groupPoints == null) {
            if (groupPointsStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(groupPointsStr);
                    int size = jsonArray.length();
                    groupPoints = new double[size];
                    for (int i = 0; i < size; i++) {
                        groupPoints[i] = jsonArray.optDouble(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return groupPoints;
    }

    public void setGroupPoints(double[] groupPoints) {
        if (groupPoints != null) {
            JSONArray jsonArray = new JSONArray();
            for (double groupPoint : groupPoints) {
                try {
                    jsonArray.put(groupPoint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.groupPointsStr = jsonArray.toString();
        }
        this.groupPoints = groupPoints;
    }

    public String[] getStickerImagePaths() {
        if (this.stickerImagePath != null) {
            try {
                JSONArray jsonArray = new JSONArray(this.stickerImagePath);
                int length = jsonArray.length();
                String[] images = new String[length];
                for (int i = 0; i < length; i++) {
                    images[i] = jsonArray.optString(i);
                }
                return images;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public double[] getGroupType() {
        if (this.groupType == null) {
            if (this.groupTypeStr != null) {
                try {
                    JSONArray jsonArray = new JSONArray(this.groupTypeStr);
                    int size = jsonArray.length();
                    this.groupType = new double[size];
                    for (int i = 0; i < size; i++) {
                        this.groupType[i] = jsonArray.optDouble(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.groupType;
    }

    public void setGroupType(double[] groupType) {
        if (groupType != null) {
            JSONArray jsonArray = new JSONArray();
            for (double groupPoint : groupType) {
                try {
                    jsonArray.put(groupPoint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.groupTypeStr = jsonArray.toString();
        }
        this.groupType = groupType;
    }

    public String getMatrix() {
        return this.matrix;
    }

    public void setMatrix(String matrix) {
        this.matrix = matrix;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupPointsStr() {
        return this.groupPointsStr;
    }

    public void setGroupPointsStr(String groupPointsStr) {
        this.groupPointsStr = groupPointsStr;
    }

    public String getGroupTypeStr() {
        return groupTypeStr;
    }

    public void setGroupTypeStr(String groupTypeStr) {
        this.groupTypeStr = groupTypeStr;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public float[] getMatrixF() {
        if (this.matrix != null) {
            try {
                JSONArray jsonArray = new JSONArray(this.matrix);
                int length = jsonArray.length();
                float[] matrix = new float[length];
                for (int i = 0; i < length; i++) {
                    matrix[i] = (float) jsonArray.optDouble(i);
                }
                return matrix;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setMatrixF(float[] matrixF) {
        if (matrixF != null) {
            JSONArray jsonArray = new JSONArray();
            for (float mat : matrixF) {
                try {
                    jsonArray.put(mat);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.matrix = jsonArray.toString();
        }
    }

    public float[] getAdjustPointsF() {
        if (this.adjustPoints != null) {
            try {
                JSONArray jsonArray = new JSONArray(this.adjustPoints);
                int length = jsonArray.length();
                float[] adjustPoints = new float[length];
                for (int i = 0; i < length; i++) {
                    adjustPoints[i] = (float) jsonArray.optDouble(i);
                }
                return adjustPoints;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setAdjustPointsF(float[] adjustPointsF) {
        if (adjustPointsF != null) {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < adjustPointsF.length; i++) {
                try {
                    jsonArray.put(adjustPointsF[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.adjustPoints = jsonArray.toString();
        }
    }

    public String getAdjustPoints() {
        return adjustPoints;
    }

    public void setAdjustPoints(String adjustPoints) {
        this.adjustPoints = adjustPoints;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + width;
        result = 31 * result + height;
        result = 31 * result + Arrays.hashCode(groupPoints);
        result = 31 * result + Arrays.hashCode(groupType);
        result = 31 * result + (imagePath != null ? imagePath.hashCode() : 0);
        return result;
    }

    public String getStickerImagePath() {
        return this.stickerImagePath;
    }

    public void setStickerImagePath(String stickerImagePath) {
        this.stickerImagePath = stickerImagePath;
    }

    public void setStickerImagePath(String[] stickerImagePaths) {
        if (stickerImagePaths != null) {
            JSONArray jsonArray = new JSONArray();
            for (String path : stickerImagePaths) {
                jsonArray.put(path);
            }
            this.stickerImagePath = jsonArray.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LivePhoto that = (LivePhoto) o;

        if (width != that.width) {
            return false;
        }
        if (height != that.height) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (!Arrays.equals(groupPoints, that.groupPoints)) {
            return false;
        }
        if (!Arrays.equals(groupType, that.groupType)) {
            return false;
        }
        return imagePath != null ? imagePath.equals(that.imagePath) : that.imagePath == null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "LivePhoto{" +
                "width=" + width +
                ", height=" + height +
                ", groupTypeStr='" + groupTypeStr + '\'' +
                ", groupPointsStr='" + groupPointsStr + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", stickerImagePath='" + stickerImagePath + '\'' +
                ", matrix='" + matrix + '\'' +
                ", adjustPoints='" + adjustPoints + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeDoubleArray(this.groupPoints);
        dest.writeDoubleArray(this.groupType);
        dest.writeString(this.groupPointsStr);
        dest.writeString(this.groupTypeStr);
        dest.writeString(this.imagePath);
        dest.writeString(this.stickerImagePath);
        dest.writeString(this.matrix);
        dest.writeString(this.adjustPoints);
    }
}