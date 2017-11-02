package com.paleskyline.accroo.database;

/**
 * Created by oscar on 30/04/17.
 */

public final class DBContract {

    // TODO: review hardcoded string variables

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

    protected abstract class Icon {
        protected static final String TABLE_NAME = "Icon";
        protected static final String COLUMN_ICON_NAME = "Name";
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

    protected static final String CREATE_ICONS =
            "CREATE TABLE " + Icon.TABLE_NAME + " (" +
                    Icon.COLUMN_ICON_NAME + " TEXT);";

    protected static final String POPULATE_GENERAL_CATEGORY =
            "INSERT INTO " + GeneralCategory.TABLE_NAME + " (" +
            GeneralCategory.COLUMN_CATEGORY_NAME + ", " +
            GeneralCategory.COLUMN_CATEGORY_ICON + ", " +
            GeneralCategory.COLUMN_ROOT_CATEGORY + ") VALUES " +
            "('Wages', 'i30', 'Income'), " +
            "('Food and Drink', 'i4', 'Expense'), " +
            "('Transport', 'i6', 'Expense'), " +
            "('Pets', 'i18', 'Expense'), " +
            "('Health', 'i29', 'Expense'), " +
            "('Miscellaneous Income', 'i9', 'Income'), " +
            "('Sport', 'i56', 'Expense'), " +
            "('Investments', 'i49', 'Income'), " +
            "('Holidays', 'i41', 'Expense'), " +
            "('Entertainment', 'i51', 'Expense'), " +
            "('Education', 'i34', 'Expense'), " +
            "('Miscellaneous Expenses', 'i7', 'Expense'), " +
            "('Accommodation', 'i21', 'Expense'), " +
            "('Personal', 'i27', 'Expense');";


    protected static final String POPULATE_SUB_CATEGORY =
            "INSERT INTO " + SubCategory.TABLE_NAME + " (" +
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

    protected static final String POPULATE_ICON =
            "INSERT INTO " + Icon.TABLE_NAME + " (" +
            Icon.COLUMN_ICON_NAME + ") VALUES " +
            "('i1'), ('i2'), ('i3'), ('i4'), ('i5'), ('i6'), ('i7'), ('i8'), ('i9'), " +
            "('i10'), ('i11'), ('i12'), ('i13'), ('i14'), ('i15'), ('i16'), ('i17'), " +
            "('i18'), ('i19'), ('i20'), ('i21'), ('i22'), ('i23'), ('i24'), ('i25'), " +
            "('i26'), ('i27'), ('i28'), ('i29'), ('i30'), ('i31'), ('i32'), ('i33'), " +
            "('i34'), ('i35'), ('i36'), ('i37'), ('i38'), ('i39'), ('i40'), ('i41'), " +
            "('i42'), ('i43'), ('i44'), ('i45'), ('i46'), ('i47'), ('i48'), ('i49'), " +
            "('i50'), ('i51'), ('i52'), ('i53'), ('i54'), ('i55'), ('i56'), ('i57');";

}
