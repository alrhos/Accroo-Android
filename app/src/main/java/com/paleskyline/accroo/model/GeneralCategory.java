package com.paleskyline.accroo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.paleskyline.accroo.crypto.CryptoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by oscar on 4/03/17.
 */

public class GeneralCategory implements Securable, Parcelable {

    private int id;
    private String categoryName, rootCategory, iconFile;
    private ArrayList<SubCategory> subCategories = new ArrayList<>();

    private DecimalFormat df = new DecimalFormat("0.00");

    public GeneralCategory(String categoryName, String rootCategory, String iconFile) {
        this.categoryName = categoryName;
        this.rootCategory = rootCategory;
        this.iconFile = iconFile;
    }

    public GeneralCategory(JSONObject json) throws JSONException, UnsupportedEncodingException {
        decrypt(json);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    @Override
    public JSONObject encrypt() throws JSONException {
        JSONObject categoryData = new JSONObject();

        categoryData.put("categoryName", categoryName);
        categoryData.put("rootCategory", rootCategory);
        categoryData.put("iconFile", iconFile);
        SecurePayload payload = CryptoManager.getInstance().encrypt(categoryData.toString());

        JSONObject json = new JSONObject();

        if (id != 0) {
            json.put("id", id);
        }

        json.put("data", payload.getData());
        json.put("nonce", payload.getNonce());

        return json;
    }

    @Override
    public void decrypt(JSONObject json) throws JSONException, UnsupportedEncodingException {
        SecurePayload payload = new SecurePayload(json.getString("data"), json.getString("nonce"));
        String categoryString = CryptoManager.getInstance().decrypt(payload);
        JSONObject categoryJson = new JSONObject(categoryString);
        this.id = json.getInt("id");
        this.categoryName = categoryJson.getString("categoryName");
        this.rootCategory = categoryJson.getString("rootCategory");
        this.iconFile = categoryJson.getString("iconFile");
    }

    @Override
    public String toString() {
        return "GeneralCategory{" +
                "id=" + id +
                ", categoryName='" + categoryName + '\'' +
                ", rootCategory='" + rootCategory + '\'' +
                ", iconFile='" + iconFile + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.categoryName);
        dest.writeString(this.rootCategory);
        dest.writeString(this.iconFile);
        dest.writeSerializable(this.df);
    }

    protected GeneralCategory(Parcel in) {
        this.id = in.readInt();
        this.categoryName = in.readString();
        this.rootCategory = in.readString();
        this.iconFile = in.readString();
        this.df = (DecimalFormat) in.readSerializable();
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

}
