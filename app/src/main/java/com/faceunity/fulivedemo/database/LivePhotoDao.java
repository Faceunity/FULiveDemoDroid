package com.faceunity.fulivedemo.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.faceunity.entity.LivePhoto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Richie on 2019.05.31
 */
public final class LivePhotoDao {
    public static final String TABLE_NAME = "LIVE_PHOTO";

    public static final class ColumnName {
        public static final String ID = "_id";
        public static final String WIDTH = "WIDTH";
        public static final String HEIGHT = "HEIGHT";
        public static final String GROUP_POINTS_STR = "GROUP_POINTS_STR";
        public static final String GROUP_TYPE_STR = "GROUP_TYPE_STR";
        public static final String TEMPLATE_IMAGE_PATH = "TEMPLATE_IMAGE_PATH";
        public static final String STICKER_IMAGE_PATH_STR = "STICKER_IMAGE_PATH_STR";
        public static final String TRANSFORM_MATRIX_STR = "TRANSFORM_MATRIX_STR";
        public static final String ADJUSTED_POINTS_STR = "ADJUSTED_POINTS_STR";
    }

    public List<LivePhoto> queryAll() {
        List<LivePhoto> livePhotos = new ArrayList<>(8);
        Cursor cursor = null;
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getReadableDatabase();
        try {
            String sql = "SELECT * FROM \"" + TABLE_NAME + "\" ORDER BY \"" + ColumnName.ID + "\" ASC";
            cursor = db.rawQuery(sql, null);
            LivePhoto livePhoto;
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ColumnName.ID));
                int width = cursor.getInt(cursor.getColumnIndex(ColumnName.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(ColumnName.HEIGHT));
                String groupPointsStr = cursor.getString(cursor.getColumnIndex(ColumnName.GROUP_POINTS_STR));
                String groupTypeStr = cursor.getString(cursor.getColumnIndex(ColumnName.GROUP_TYPE_STR));
                String templateImagePath = cursor.getString(cursor.getColumnIndex(ColumnName.TEMPLATE_IMAGE_PATH));
                String stickerImagePathStr = cursor.getString(cursor.getColumnIndex(ColumnName.STICKER_IMAGE_PATH_STR));
                String transformMatrixStr = cursor.getString(cursor.getColumnIndex(ColumnName.TRANSFORM_MATRIX_STR));
                String adjustedPointsStr = cursor.getString(cursor.getColumnIndex(ColumnName.ADJUSTED_POINTS_STR));
                livePhoto = new LivePhoto(id, width, height, groupPointsStr, groupTypeStr,
                        templateImagePath, stickerImagePathStr, transformMatrixStr, adjustedPointsStr);
                livePhotos.add(livePhoto);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return livePhotos;
    }

    public void insertOrUpdate(LivePhoto livePhoto) throws SQLException {
        if (livePhoto.getId() < 0) {
            insert(livePhoto);
        } else {
            update(livePhoto);
        }
    }

    public void insert(LivePhoto livePhoto) throws SQLException {
        String sql = "INSERT INTO \"" + TABLE_NAME + "\" (\""
                + ColumnName.WIDTH + "\", \"" + ColumnName.HEIGHT + "\", \""
                + ColumnName.GROUP_POINTS_STR + "\", \"" + ColumnName.GROUP_TYPE_STR + "\", \""
                + ColumnName.TEMPLATE_IMAGE_PATH + "\", \"" + ColumnName.STICKER_IMAGE_PATH_STR + "\", \""
                + ColumnName.TRANSFORM_MATRIX_STR + "\", \"" + ColumnName.ADJUSTED_POINTS_STR
                + "\") VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getWritableDatabase();
        db.execSQL(sql, new Object[]{
                livePhoto.getWidth(), livePhoto.getHeight(), livePhoto.getGroupPointsStr(),
                livePhoto.getGroupTypeStr(), livePhoto.getTemplateImagePath(), livePhoto.getStickerImagePathStr(),
                livePhoto.getTransformMatrixStr(), livePhoto.getAdjustedPointsStr()
        });
    }

    public void update(LivePhoto livePhoto) throws SQLException {
        String sql = "UPDATE \"" + TABLE_NAME + "\" SET \"" + ColumnName.WIDTH + "\" = ?, \""
                + ColumnName.HEIGHT + "\" = ?, \"" + ColumnName.GROUP_POINTS_STR + "\" = ?, \""
                + ColumnName.GROUP_TYPE_STR + "\" = ?, \"" + ColumnName.TEMPLATE_IMAGE_PATH + "\" = ?, \""
                + ColumnName.STICKER_IMAGE_PATH_STR + "\" = ?, \"" + ColumnName.TRANSFORM_MATRIX_STR + "\" = ?, \""
                + ColumnName.ADJUSTED_POINTS_STR + "\" = ? WHERE \"" + ColumnName.ID + "\" = ?;";
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getWritableDatabase();
        db.execSQL(sql, new Object[]{livePhoto.getWidth(), livePhoto.getHeight(), livePhoto.getGroupPointsStr(),
                livePhoto.getGroupTypeStr(), livePhoto.getTemplateImagePath(), livePhoto.getStickerImagePathStr(),
                livePhoto.getTransformMatrixStr(), livePhoto.getAdjustedPointsStr(), livePhoto.getId()});
    }

    public void delete(LivePhoto livePhoto) throws SQLException {
        String sql = "DELETE FROM \"" + TABLE_NAME + "\" WHERE \"" + ColumnName.ID + "\" = ?;";
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getWritableDatabase();
        db.execSQL(sql, new Object[]{livePhoto.getId()});
    }

}
