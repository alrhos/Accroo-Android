package com.paleskyline.accroo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by oscar on 30/04/17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Accroo.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    protected DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DBContract.CREATE_GENERAL_CATEGORIES);
        sqLiteDatabase.execSQL(DBContract.CREATE_SUB_CATEGORIES);
        sqLiteDatabase.execSQL(DBContract.CREATE_ICONS);
        sqLiteDatabase.execSQL(DBContract.POPULATE_GENERAL_CATEGORY);
        sqLiteDatabase.execSQL(DBContract.POPULATE_SUB_CATEGORY);
        sqLiteDatabase.execSQL(DBContract.POPULATE_ICON);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }



}
