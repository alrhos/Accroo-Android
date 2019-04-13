package io.accroo.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by oscar on 30/04/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Accroo.db";
    private static final int DATABASE_VERSION = 3;
    private SQLiteDatabase db;

    protected DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.CREATE_GENERAL_CATEGORIES);
        db.execSQL(DBContract.CREATE_SUB_CATEGORIES);
        db.execSQL(DBContract.CREATE_ICONS);
        db.execSQL(DBContract.POPULATE_GENERAL_CATEGORY);
        db.execSQL(DBContract.POPULATE_SUB_CATEGORY);
        db.execSQL(DBContract.POPULATE_ICON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.DROP_GENERAL_CATEGORIES);
        db.execSQL(DBContract.DROP_SUB_CATEGORIES);
        db.execSQL(DBContract.DROP_ICONS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

}
