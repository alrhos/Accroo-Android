package com.paleskyline.navicash.model;

import java.util.ArrayList;

/**
 * Created by oscar on 25/03/17.
 */

public class RootCategory {

    private String categoryName;
    private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();

    public RootCategory(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ArrayList<GeneralCategory> getGeneralCategories() {
        return generalCategories;
    }

    public void setGeneralCategories(ArrayList<GeneralCategory> generalCategories) {
        this.generalCategories = generalCategories;
    }
}
