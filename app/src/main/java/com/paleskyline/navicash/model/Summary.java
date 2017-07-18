package com.paleskyline.navicash.model;

import java.text.DecimalFormat;

/**
 * Created by oscar on 3/06/17.
 */

public class Summary {

    private double income = 0;
    private double expenses = 0;
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

//    public Summary(double income, double expenses) {
//        this.income = income;
//        this.expenses = expenses;
//    }

//    public String getIncome() {
////        double income = DataProvider.getInstance().getRootCategories()[0].getTransactionTotal();
//        return "$" + df.format(income);
//    }
//
//    public String getExpenses() {
//     //   double expenses = DataProvider.getInstance().getRootCategories()[1].getTransactionTotal();
//        return "$" + df.format(expenses);
//    }
//
//    public String getSavings() {
//     //   double income = DataProvider.getInstance().getRootCategories()[0].getTransactionTotal();
//     //   double expenses = DataProvider.getInstance().getRootCategories()[1].getTransactionTotal();
//        return "$" + df.format(income - expenses);
//    }

    @Override
    public String toString() {
        return "SUMMARY OBJECT";
    }
}
