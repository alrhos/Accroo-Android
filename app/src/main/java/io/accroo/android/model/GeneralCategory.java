package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

/**
 * Created by oscar on 4/03/17.
 */

public class GeneralCategory implements Parcelable {

    private UUID id;
    @Expose private String categoryName;
    @Expose private String rootCategory;
    @Expose private String iconFile;
    private ArrayList<SubCategory> subCategories;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    public GeneralCategory(String categoryName, String rootCategory, String iconFile) {
        this.categoryName = categoryName;
        this.rootCategory = rootCategory;
        this.iconFile = iconFile;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(String rootCategory) {
        this.rootCategory = rootCategory;
    }

    public String getIconFile() {
        return iconFile;
    }

    public void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }

    public ArrayList<SubCategory> getSubCategories() {
        if (subCategories == null) {
            subCategories = new ArrayList<>();
        }
        return subCategories;
    }

    public double getTransactionTotal() {
        double total = 0;
        for (SubCategory sc : subCategories) {
            total += sc.getTransactionTotal();
        }
        return total;
    }

    public String getFormattedTransactionTotal() {
        return "$" + df.format(getTransactionTotal());
    }

    public void setSubCategories(ArrayList<SubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public EncryptedGeneralCategory encrypt() {
        String categoryJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(categoryJson);
        securePayload.setId(this.id);
        return new EncryptedGeneralCategory(securePayload.getId(), securePayload.getData(), securePayload.getNonce());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.id);
        dest.writeString(this.categoryName);
        dest.writeString(this.rootCategory);
        dest.writeString(this.iconFile);
    }

    protected GeneralCategory(Parcel in) {
        this.id = (UUID) in.readSerializable();
        this.categoryName = in.readString();
        this.rootCategory = in.readString();
        this.iconFile = in.readString();
    }

    public static final Creator<GeneralCategory> CREATOR = new Creator<GeneralCategory>() {
        @Override
        public GeneralCategory createFromParcel(Parcel source) {
            return new GeneralCategory(source);
        }

        @Override
        public GeneralCategory[] newArray(int size) {
            return new GeneralCategory[size];
        }
    };

    @Override
    public String toString() {
        return "GeneralCategory{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", rootCategory='" + rootCategory + '\'' +
                ", iconFile='" + iconFile + '\'' +
                ", subCategories=" + subCategories +
                '}';
    }
}
