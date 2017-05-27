package com.paleskyline.navicash.services;

import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;

import java.util.ArrayList;

/**
 * Created by oscar on 27/05/17.
 */

public class DataProvider {

    private static DataProvider instance = null;
    private ArrayList<GeneralCategory> generalCategories;
    private ArrayList<SubCategory> subCategories;
    private ArrayList<Transaction> transactions;

    private DataProvider() {
        generalCategories = new ArrayList<>();
        subCategories = new ArrayList<>();
        transactions = new ArrayList<>();
    }

    public static DataProvider getInstance() {
        if (instance == null) {
            instance = new DataProvider();
        }
        return instance;
    }

    public ArrayList<GeneralCategory> getGeneralCategories() {
        return generalCategories;
    }

    public void setGeneralCategories(ArrayList<GeneralCategory> generalCategories) {
        this.generalCategories = generalCategories;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }
}
