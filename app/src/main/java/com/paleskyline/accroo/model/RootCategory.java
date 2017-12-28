package com.paleskyline.accroo.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by oscar on 25/03/17.
 */

public class RootCategory implements Parcelable {

    public static final String INCOME = "Income";
    public static final String EXPENSE = "Expense";
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.categoryName);
        dest.writeList(this.generalCategories);
        dest.writeSerializable(this.df);
    }

    protected RootCategory(Parcel in) {
        this.categoryName = in.readString();
        this.generalCategories = new ArrayList<>();
        in.readList(this.generalCategories, GeneralCategory.class.getClassLoader());
        this.df = (DecimalFormat) in.readSerializable();
    }

    public static final Parcelable.Creator<RootCategory> CREATOR = new Parcelable.Creator<RootCategory>() {
        @Override
        public RootCategory createFromParcel(Parcel source) {
            return new RootCategory(source);
        }

        @Override
        public RootCategory[] newArray(int size) {
            return new RootCategory[size];
        }
    };
}
