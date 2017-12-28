package com.paleskyline.accroo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.paleskyline.accroo.model.GeneralCategory;
import com.paleskyline.accroo.model.SubCategory;

import java.util.ArrayList;

/**
 * Created by oscar on 30/04/17.
 */

public class DataAccess {

    private SQLiteDatabase db;
    private DBHelper dbHelper;
    private Cursor cursor;

    private static DataAccess instance = null;

    public static DataAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DataAccess(context);
        }
        return instance;
    }

    private DataAccess(Context context) {
        dbHelper = new DBHelper(context);
    }

    public ArrayList<GeneralCategory> getGeneralCategories() {
        db = dbHelper.getReadableDatabase();
        String[] columns = {
            DBContract.GeneralCategory.COLUMN_CATEGORY_NAME,
            DBContract.GeneralCategory.COLUMN_ROOT_CATEGORY,
            DBContract.GeneralCategory.COLUMN_CATEGORY_ICON
        };

        cursor = db.query(
            DBContract.GeneralCategory.TABLE_NAME,
            columns,
            null,
            null,
            null,
            null,
            null
        );

        ArrayList<GeneralCategory> categories = new ArrayList<>();

        while (cursor.moveToNext()) {
            categories.add(new GeneralCategory(
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.GeneralCategory.COLUMN_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.GeneralCategory.COLUMN_ROOT_CATEGORY)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.GeneralCategory.COLUMN_CATEGORY_ICON))
            ));
        }

        close();
        return categories;
    }

    public ArrayList<SubCategory> getSubCategories() {
        db = dbHelper.getReadableDatabase();
        String[] columns = {
            DBContract.SubCategory.COLUMN_CATEGORY_NAME,
            DBContract.SubCategory.COLUMN_GENERAL_CATEGORY,
        };

        cursor = db.query(
            DBContract.SubCategory.TABLE_NAME,
            columns,
            null,
            null,
            null,
            null,
            null
        );

        ArrayList<SubCategory> categories = new ArrayList<>();

        while (cursor.moveToNext()) {
            categories.add(new SubCategory(
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.SubCategory.COLUMN_CATEGORY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(DBContract.SubCategory.COLUMN_GENERAL_CATEGORY))
            ));
        }

        close();
        return categories;
    }

    public ArrayList<String> getIcons() {
        db = dbHelper.getReadableDatabase();
        String[] columns = {
                DBContract.Icon.COLUMN_ICON_NAME
        };

        cursor = db.query(
                DBContract.Icon.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> icons = new ArrayList<>();

        while (cursor.moveToNext()) {
            icons.add(cursor.getString(cursor.getColumnIndexOrThrow(DBContract.Icon.COLUMN_ICON_NAME)));
        }

        close();
        return icons;
    }

    private void close() {
        cursor.close();
        dbHelper.close();
        db.close();
    }

}
