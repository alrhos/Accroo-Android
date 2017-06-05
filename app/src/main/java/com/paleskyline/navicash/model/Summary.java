package com.paleskyline.navicash.model;

import com.paleskyline.navicash.services.DataProvider;

import java.text.DecimalFormat;

/**
 * Created by oscar on 3/06/17.
 */

public class Summary {

    private DecimalFormat df = new DecimalFormat("0.00");

    public Summary() {}

    public String getIncome() {
        double income = DataProvider.getInstance().getRootCategories()[0].getTransactionTotal();
        return "$" + df.format(income);
    }

    public String getExpenses() {
        double expenses = DataProvider.getInstance().getRootCategories()[1].getTransactionTotal();
        return "$" + df.format(expenses);
    }

    public String getSavings() {
        double income = DataProvider.getInstance().getRootCategories()[0].getTransactionTotal();
        double expenses = DataProvider.getInstance().getRootCategories()[1].getTransactionTotal();
        return "$" + df.format(income - expenses);
    }

    @Override
    public String toString() {
        return "SUMMARY OBJECT";
    }
}
