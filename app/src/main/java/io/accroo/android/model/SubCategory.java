package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by oscar on 24/03/17.
 */

public class SubCategory implements Relationship, Parcelable {

    private int id;
    private int generalCategoryId;
    @Expose private String categoryName;
    private String generalCategoryName;
    private GeneralCategory parent;
    private ArrayList<Transaction> transactions;
    private static DecimalFormat df = new DecimalFormat("0.00");

    public SubCategory(String categoryName, int generalCategoryId) {
        this.categoryName = categoryName;
        this.generalCategoryId = generalCategoryId;
    }

    public SubCategory(String categoryName, String generalCategoryName) {
        this.categoryName = categoryName;
        this.generalCategoryName = generalCategoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGeneralCategoryId() {
        return generalCategoryId;
    }

    public void setGeneralCategoryId(int generalCategoryId) {
        this.generalCategoryId = generalCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getGeneralCategoryName() {
        return generalCategoryName;
    }

    public void setGeneralCategoryName(String generalCategoryName) {
        this.generalCategoryName = generalCategoryName;
    }

    public ArrayList<Transaction> getTransactions() {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        return transactions;
    }

    public double getTransactionTotal() {
        double total = 0;
        for (Transaction t : transactions) {
            total += t.getAmount();
        }
        return total;
    }

    public String getFormattedTransactionTotal() {
        return "$" + df.format(getTransactionTotal());
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public EncryptedSubCategory encrypt() {
        String categoryJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(categoryJson);
        return new EncryptedSubCategory(this.id, this.generalCategoryId,
                securePayload.getData(), securePayload.getNonce());
    }

    @Override
    public void setParent(Object parent) {
        this.parent = (GeneralCategory) parent;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.generalCategoryId);
        dest.writeString(this.categoryName);
        dest.writeParcelable(this.parent, flags);
    }

    protected SubCategory(Parcel in) {
        this.id = in.readInt();
        this.generalCategoryId = in.readInt();
        this.categoryName = in.readString();
        this.parent = in.readParcelable(GeneralCategory.class.getClassLoader());
    }

    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel source) {
            return new SubCategory(source);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    @Override
    public String toString() {
        return "SubCategory{" +
                "id=" + id +
                ", generalCategoryId=" + generalCategoryId +
                ", categoryName='" + categoryName + '\'' +
                ", generalCategoryName='" + generalCategoryName + '\'' +
                ", parent=" + parent +
                ", transactions=" + transactions +
                '}';
    }
}
