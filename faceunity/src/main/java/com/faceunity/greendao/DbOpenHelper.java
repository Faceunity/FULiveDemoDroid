package com.faceunity.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.greenrobot.greendao.database.Database;

/**
 * @author Lq on 2018.12.02
 */
public class DbOpenHelper extends DaoMaster.OpenHelper {
    private static final String TAG = "DbOpenHelper";

    public DbOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        onCreate(db);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Log.d(TAG, "onUpgrade: oldVersion:" + oldVersion + ", newVersion:" + newVersion);
        DaoMaster.dropAllTables(db, true);
    }

}