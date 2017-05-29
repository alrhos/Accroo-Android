package com.paleskyline.navicash.services;

import com.paleskyline.navicash.model.RootCategory;

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

}
