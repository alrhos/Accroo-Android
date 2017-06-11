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
            "('Wages', 'moneybag', 'Income'), " +
            "('Food and Drink', 'burger', 'Expenses'), " +
            "('Transport', 'car', 'Expenses'), " +
            "('Pets', 'dog', 'Expenses'), " +
            "('Health', 'medical_report', 'Expenses'), " +
            "('Miscellaneous Income', 'cash_in_hand', 'Income'), " +
            "('Sport', 'weightlifting', 'Expenses'), " +
            "('Investments', 'stocks', 'Income'), " +
            "('Holidays', 'plane_takeoff', 'Expenses'), " +
            "('Entertainment', 'ticket', 'Expenses'), " +
            "('Education', 'open_book', 'Expenses'), " +
            "('Miscellaneous Expenses', 'card_in_hand', 'Expenses'), " +
            "('Accommodation', 'house1', 'Expenses'), " +
            "('Personal', 'man_and_woman', 'Expenses');";


    protected static final String POPULATE_SUB_CATEGORY =
            "INSERT INTO " + SubCategory.TABLE_NAME + "(" +
            SubCategory.COLUMN_CATEGORY_NAME + ", " +
            SubCategory.COLUMN_GENERAL_CATEGORY + ") VALUES " +
            "('Salary', 'Wages'), ('Groceries', 'Food and Drink'), " +
            "('Takeaway', 'Food and Drink'), ('Textbooks', 'Education'), " +
            "('Tuition fees', 'Education'), ('Concerts', 'Entertainment'), " +
            "('Movies', 'Entertainment'), ('Going out', 'Entertainment'), " +
            "('Dining out', 'Food and Drink'), ('Dentist', 'Health'), " +
            "('GP appointments', 'Health'), ('Health insurance', 'Health'), " +
            "('Medicine', 'Health'), ('Accommodation', 'Holidays'), " +
            "('Transport', 'Holidays'), ('Travel insurance', 'Holidays'), " +
            "('Bank account interest', 'Investments'), ('Shares', 'Investments'), " +
            "('Clothing', 'Personal'), ('Living essentials', 'Personal'), " +
            "('Personal grooming', 'Personal'), ('Subscriptions', 'Personal'), " +
            "('Car insurance', 'Transport'), ('Petrol', 'Transport'), " +
            "('Uber', 'Transport'), ('Vehicle maintenance', 'Transport');";

}
