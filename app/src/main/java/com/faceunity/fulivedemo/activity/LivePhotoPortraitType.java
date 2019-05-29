package com.faceunity.fulivedemo.activity;

import com.faceunity.fulivedemo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Richie on 2019.04.12
 */
public class LivePhotoPortraitType {
    // 人像类型。写实、萌版、漫画男、漫画女
    public static final int PORTRAIT_TYPE_REALISTIC = 0;
    public static final int PORTRAIT_TYPE_CUTE = 1;
    public static final int PORTRAIT_TYPE_COMIC_GIRL = 2;
    public static final int PORTRAIT_TYPE_COMIC_BOY = 3;
    private int nameId;
    private int type;

    public LivePhotoPortraitType(int nameId, int type) {
        this.nameId = nameId;
        this.type = type;
    }

    public static List<LivePhotoPortraitType> getAll() {
        List<LivePhotoPortraitType> livePhotoPortraitTypes = new ArrayList<>(4);
        livePhotoPortraitTypes.add(new LivePhotoPortraitType(R.string.live_photo_type_realistic, PORTRAIT_TYPE_REALISTIC));
        livePhotoPortraitTypes.add(new LivePhotoPortraitType(R.string.live_photo_type_comic_boy, PORTRAIT_TYPE_COMIC_BOY));
        livePhotoPortraitTypes.add(new LivePhotoPortraitType(R.string.live_photo_type_comic_girl, PORTRAIT_TYPE_COMIC_GIRL));
        livePhotoPortraitTypes.add(new LivePhotoPortraitType(R.string.live_photo_type_cute, PORTRAIT_TYPE_CUTE));
        return livePhotoPortraitTypes;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LivePhotoPortraitType{" +
                "nameId=" + nameId +
                ", type=" + type +
                '}';
    }
}
