package com.paleskyline.navicash.model;

import com.paleskyline.navicash.services.DataProvider;

/**
 * Created by oscar on 3/06/17.
 */

public class Summary {

    public Summary() {}

    public double getIncome() {
        return DataProvider.getInstance().getRootCategories()[0].getTransactionTotal();
    }

    public double getExpenses() {
        return DataProvider.getInstance().getRootCategories()[1].getTransactionTotal();
    }

    public double getSavings() {
        return getIncome() - getExpenses();
    }

    @Override
    public String toString() {
        return "SUMMARY OBJECT";
    }
}
