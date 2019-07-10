package com.faceunity.fulivedemo.database;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.faceunity.entity.AvatarModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Richie on 2019.05.31
 */
public final class AvatarModelDao {
    private static final String TAG = "AvatarModelDao";
    public static final String TABLE_NAME = "AVATAR_MODEL";

    public static final class ColumnName {
        public static final String ID = "_id";
        public static final String ICON_PATH = "ICON_PATH";
        public static final String PARAM_JSON = "PARAM_JSON";
        public static final String UI_JSON = "UI_JSON";
    }

    public List<AvatarModel> queryAll() {
        List<AvatarModel> avatarModels = new ArrayList<>(8);
        Cursor cursor = null;
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getReadableDatabase();
        try {
            AvatarModel avatarModel;
            String sql = "SELECT * FROM \"" + TABLE_NAME + "\" ORDER BY \"" + ColumnName.ID + "\" ASC";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(ColumnName.ID));
                String iconPath = cursor.getString(cursor.getColumnIndex(ColumnName.ICON_PATH));
                String paramJson = cursor.getString(cursor.getColumnIndex(ColumnName.PARAM_JSON));
                String uiJson = cursor.getString(cursor.getColumnIndex(ColumnName.UI_JSON));
                avatarModel = new AvatarModel(id, iconPath, paramJson, uiJson);
                avatarModels.add(avatarModel);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return avatarModels;
    }

    public void insertOrUpdate(AvatarModel avatarModel) throws SQLException {
        if (avatarModel.getId() < 0) {
            insert(avatarModel);
        } else {
            update(avatarModel);
        }
    }

    public void insert(AvatarModel avatarModel) throws SQLException {
        String sql = "INSERT INTO \"" + TABLE_NAME + "\" (\""
                + ColumnName.ICON_PATH + "\", \"" + ColumnName.PARAM_JSON + "\", \""
                + ColumnName.UI_JSON + "\") VALUES (?, ?, ?);";
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getWritableDatabase();
        db.execSQL(sql, new Object[]{avatarModel.getIconPath(), avatarModel.getParamJson(), avatarModel.getUiJson()});
    }

    public void update(AvatarModel avatarModel) throws SQLException {
        String sql = "UPDATE \"" + TABLE_NAME + "\" SET \"" + ColumnName.ICON_PATH + "\" = ?, \""
                + ColumnName.PARAM_JSON + "\" = ?, \"" + ColumnName.UI_JSON + "\" = ? WHERE \"" + ColumnName.ID + "\" = ?;";
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getWritableDatabase();
        db.execSQL(sql, new Object[]{avatarModel.getIconPath(), avatarModel.getParamJson(),
                avatarModel.getUiJson(), avatarModel.getId()});
    }

    public void delete(AvatarModel avatarModel) throws SQLException {
        String sql = "DELETE FROM \"" + TABLE_NAME + "\" WHERE \"" + ColumnName.ID + "\" = ?;";
        SQLiteDatabase db = DatabaseOpenHelper.getInstance().getWritableDatabase();
        db.execSQL(sql, new Object[]{avatarModel.getId()});
    }

    public List<AvatarModel> delete(List<AvatarModel> avatarModels) {
        List<AvatarModel> failedList = new ArrayList<>(2);
        for (AvatarModel avatarModel : avatarModels) {
            try {
                delete(avatarModel);
            } catch (SQLException e) {
                failedList.add(avatarModel);
                Log.e(TAG, "delete: ", e);
            }
        }
        return failedList;
    }

}
