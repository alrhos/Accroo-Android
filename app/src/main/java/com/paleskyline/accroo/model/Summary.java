package com.paleskyline.accroo.model;

import java.text.DecimalFormat;

/**
 * Created by oscar on 3/06/17.
 */

public class Summary {

    private RootCategory[] rootCategories;
    public static final String INCOME = "Income";
    public static final String EXPENSES = "Expense";
    private DecimalFormat df = new DecimalFormat("0.00");

    public Summary(RootCategory[] rootCategories) {
        this.rootCategories = rootCategories;
    }

    public String getTotal(String type) {
        for (int i = 0; i < rootCategories.length; i++) {
            if (rootCategories[i].getCategoryName().equals(type)) {
                double total = rootCategories[i].getTransactionTotal();
                return "$" + df.format(total);
            }
        }
        return "$" + df.format(0);
    }

    public String getSavings() {
        double income = 0;
        double expenses = 0;
        for (int i = 0; i < rootCategories.length; i++) {
            if (rootCategories[i].getCategoryName().equals(INCOME)) {
                income = rootCategories[i].getTransactionTotal();
            } else if (rootCategories[i].getCategoryName().equals(EXPENSES)) {
                expenses = rootCategories[i].getTransactionTotal();
            }
        }
        return "$" + df.format(income - expenses);
    }

}
