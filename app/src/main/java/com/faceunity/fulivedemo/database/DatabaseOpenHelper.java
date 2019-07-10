package com.faceunity.fulivedemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Richie on 2019.05.31
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "fulivedemo.db";
    private static final int DB_VERSION = 4;

    private static DatabaseOpenHelper sDatabaseOpenHelper;
    private LivePhotoDao mLivePhotoDao;
    private AvatarModelDao mAvatarModelDao;

    /**
     * Call on Application onCreate method
     *
     * @param context
     */
    public static void register(Context context) {
        sDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseOpenHelper getInstance() {
        return sDatabaseOpenHelper;
    }

    public LivePhotoDao getLivePhotoDao() {
        return mLivePhotoDao;
    }

    public AvatarModelDao getAvatarModelDao() {
        return mAvatarModelDao;
    }

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mLivePhotoDao = new LivePhotoDao();
        mAvatarModelDao = new AvatarModelDao();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableLivePhoto(db);
        createTableAvatarModel(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTable(db, LivePhotoDao.TABLE_NAME);
        dropTable(db, AvatarModelDao.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Creates the LIVE_PHOTO database table.
     *
     * @param db
     */
    private void createTableLivePhoto(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS \"" + LivePhotoDao.TABLE_NAME + "\"(" +
                "\"" + LivePhotoDao.ColumnName.ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT," +
                "\"" + LivePhotoDao.ColumnName.WIDTH + "\" INTEGER NOT NULL," +
                "\"" + LivePhotoDao.ColumnName.HEIGHT + "\" INTEGER NOT NULL," +
                "\"" + LivePhotoDao.ColumnName.GROUP_POINTS_STR + "\" TEXT," +
                "\"" + LivePhotoDao.ColumnName.GROUP_TYPE_STR + "\" TEXT," +
                "\"" + LivePhotoDao.ColumnName.TEMPLATE_IMAGE_PATH + "\" TEXT," +
                "\"" + LivePhotoDao.ColumnName.STICKER_IMAGE_PATH_STR + "\" TEXT," +
                "\"" + LivePhotoDao.ColumnName.TRANSFORM_MATRIX_STR + "\" TEXT," +
                "\"" + LivePhotoDao.ColumnName.ADJUSTED_POINTS_STR + "\" TEXT);");
    }

    /**
     * Creates the AVATAR_MODEL database table.
     *
     * @param db
     */
    public static void createTableAvatarModel(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS \"" + AvatarModelDao.TABLE_NAME + "\" (" +
                "\"" + AvatarModelDao.ColumnName.ID + "\" INTEGER PRIMARY KEY AUTOINCREMENT," +
                "\"" + AvatarModelDao.ColumnName.ICON_PATH + "\" TEXT," +
                "\"" + AvatarModelDao.ColumnName.PARAM_JSON + "\" TEXT," +
                "\"" + AvatarModelDao.ColumnName.UI_JSON + "\" TEXT);");
    }

    /**
     * Drops the database table.
     *
     * @param tableName
     */
    private void dropTable(SQLiteDatabase db, String tableName) {
        String sql = "DROP TABLE IF EXISTS \"" + tableName + "\"";
        db.execSQL(sql);
    }

}
