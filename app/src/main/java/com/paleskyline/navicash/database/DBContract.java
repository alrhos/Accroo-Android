package com.paleskyline.navicash.database;

/**
 * Created by oscar on 30/04/17.
 */

public final class DBContract {

    private DBContract() {}

    protected abstract class GeneralCategory {
        protected static final String TABLE_NAME = "GeneralCategory";
        protected static final String COLUMN_CATEGORY_NAME = "CategoryName";
        protected static final String COLUMN_CATEGORY_ICON = "CategoryIcon";
        protected static final String COLUMN_ROOT_CATEGORY = "RootCategory";
    }

    protected abstract class SubCategory {
        protected static final String TABLE_NAME = "SubCategory";
        protected static final String COLUMN_CATEGORY_NAME = "CategoryName";
        protected static final String COLUMN_GENERAL_CATEGORY = "GeneralCategory";
    }

    protected static final String CREATE_GENERAL_CATEGORIES =
            "CREATE TABLE " + GeneralCategory.TABLE_NAME + " (" +
            GeneralCategory.COLUMN_CATEGORY_NAME + " TEXT, " +
            GeneralCategory.COLUMN_CATEGORY_ICON + " TEXT, " +
            GeneralCategory.COLUMN_ROOT_CATEGORY + " TEXT);";

    protected static final String CREATE_SUB_CATEGORIES =
            "CREATE TABLE " + SubCategory.TABLE_NAME + " (" +
            SubCategory.COLUMN_CATEGORY_NAME + " TEXT, " +
            SubCategory.COLUMN_GENERAL_CATEGORY + " TEXT);";

    protected static final String POPULATE_GENERAL_CATEGORY =
            "INSERT INTO " + GeneralCategory.TABLE_NAME + "(" +
            GeneralCategory.COLUMN_CATEGORY_NAME + ", " +
            GeneralCategory.COLUMN_CATEGORY_ICON + ", " +
            GeneralCategory.COLUMN_ROOT_CATEGORY + ") VALUES " +
            "('Wages', 'placeholder', 'Income'), " +
            "('Food and Drink', 'placeholder', 'Expenses'), " +
            "('Transport', 'placeholder', 'Expenses'), " +
            "('Pets', 'placeholder', 'Expenses'), " +
            "('Health', 'placeholder', 'Expenses'), " +
            "('Miscellaneous Income', 'placeholder', 'Income'), " +
            "('Sport', 'placeholder', 'Expenses'), " +
            "('Investments', 'placeholder', 'Income'), " +
            "('Holidays', 'placeholder', 'Expenses'), " +
            "('Entertainment', 'placeholder', 'Expenses'), " +
            "('Education', 'placeholder', 'Expenses'), " +
            "('Miscellaneous Expenses', 'placeholder', 'Expenses'), " +
            "('Accommodation', 'placeholder', 'Expenses');";


    protected static final String POPULATE_SUB_CATEGORY =
            "INSERT INTO " + SubCategory.TABLE_NAME + "(" +
            SubCategory.COLUMN_CATEGORY_NAME + ", " +
            SubCategory.COLUMN_GENERAL_CATEGORY + ") VALUES " +
            "('Salary', 'Wages'), ('Groceries', 'Food and Drink')," +
            "('Takeaway', 'Food and Drink');";

}
