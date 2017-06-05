package com.paleskyline.navicash.model;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by oscar on 25/03/17.
 */

public class RootCategory {

    private String categoryName;
    private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();

    private DecimalFormat df = new DecimalFormat("0.00");

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

    public double getTransactionTotal() {
        double total = 0;
        for (GeneralCategory gc : generalCategories) {
            total += gc.getTransactionTotal();
        }
        return total;
    }

    public String getFormattedTransactionTotal() {
        return "$" + df.format(getTransactionTotal());
    }

    public void setGeneralCategories(ArrayList<GeneralCategory> generalCategories) {
        this.generalCategories = generalCategories;
    }
}
