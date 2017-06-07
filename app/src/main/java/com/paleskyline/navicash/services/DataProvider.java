package com.paleskyline.navicash.services;

import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.RootCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.model.TransactionComparator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by oscar on 27/05/17.
 */

public class DataProvider {

    private static DataProvider instance = null;
    private RootCategory[] rootCategories;

    private DataProvider() {
        rootCategories = new RootCategory[2];
        rootCategories[0] = new RootCategory("Income");
        rootCategories[1] = new RootCategory("Expenses");
    }

    public static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }

    public RootCategory[] getRootCategories() {
        return rootCategories;
    }

    public void setRootCategories(RootCategory[] rootCategories) {
        this.rootCategories = rootCategories;
    }

    public ArrayList<GeneralCategory> getGeneralCategories() {
        ArrayList<GeneralCategory> categories = new ArrayList<>();
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                categories.add(gc);
            }
        }
        return categories;
    }

    public ArrayList<Transaction> getTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<>();
        for (int i = 0; i < rootCategories.length; i++) {
            for (GeneralCategory gc : rootCategories[i].getGeneralCategories()) {
                for (SubCategory sc: gc.getSubCategories()) {
                    for (Transaction t : sc.getTransactions()) {
                        transactions.add(t);
                    }
                }
            }
        }
        Collections.sort(transactions, Collections.reverseOrder(new TransactionComparator()));
        return transactions;
    }

}
