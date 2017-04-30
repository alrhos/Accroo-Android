package com.paleskyline.navicash.database;

/**
 * Created by oscar on 30/04/17.
 */

public final class DBContract {

    private DBContract() {}

    private abstract class GeneralCategory {
        private static final String TABLE_NAME = "GeneralCategory";
        private static final String COLUMN_CATEGORY_NAME = "CategoryName";
        private static final String COLUMN_CATEGORY_ICON = "CategoryIcon";
    }

    private abstract class SubCategory {
        private static final String TABLE_NAME = "SubCategory";
        private static final String COLUMN_CATEGORY_NAME = "CategoryName";
        private static final String COLUMN_GENERAL_CATEGORY = "GeneralCategory";
    }

    protected static final String CREATE_GENERAL_CATEGORIES =
            "CREATE TABLE " + GeneralCategory.TABLE_NAME + " (" +
            GeneralCategory.COLUMN_CATEGORY_NAME + " TEXT, " +
            GeneralCategory.COLUMN_CATEGORY_ICON + " TEXT);";

    protected static final String CREATE_SUB_CATEGORIES =
            "CREATE TABLE " + SubCategory.TABLE_NAME + " (" +
            SubCategory.COLUMN_CATEGORY_NAME + " TEXT, " +
            SubCategory.COLUMN_GENERAL_CATEGORY + " TEXT);";

}
