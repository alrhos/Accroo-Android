package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.accroo.android.crypto.CryptoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by oscar on 24/03/17.
 */

public class SubCategory implements OldSecurable, Relationship, Parcelable {

    private int id, generalCategoryId;
    private String categoryName, generalCategoryName;
    private GeneralCategory parent;
    private ArrayList<Transaction> transactions = new ArrayList<>();

    private DecimalFormat df = new DecimalFormat("0.00");

    public SubCategory(String categoryName, int generalCategoryId) {
        this.categoryName = categoryName;
        this.generalCategoryId = generalCategoryId;
    }

    public SubCategory(String categoryName, String generalCategoryName) {
        this.categoryName = categoryName;
        this.generalCategoryName = generalCategoryName;
    }

    public SubCategory(JSONObject json) throws JSONException, UnsupportedEncodingException {
        decrypt(json);
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

    public ArrayList<Transaction> getTransactions() {
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

    @Override
    public void setParent(Object parent) {
        this.parent = (GeneralCategory) parent;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    @Override
    public JSONObject encrypt() throws JSONException {
        JSONObject categoryData = new JSONObject();
        categoryData.put("categoryName", categoryName);
        SecurePayload payload = CryptoManager.getInstance().encrypt(categoryData.toString());
        JSONObject json = new JSONObject();

        if (id != 0) {
            json.put("id", id);
        }

        json.put("generalCategoryId", generalCategoryId);
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
        this.generalCategoryId = json.getInt("generalCategoryId");
        this.categoryName = categoryJson.getString("categoryName");
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
        dest.writeSerializable(this.df);
    }

    protected SubCategory(Parcel in) {
        this.id = in.readInt();
        this.generalCategoryId = in.readInt();
        this.categoryName = in.readString();
        this.parent = in.readParcelable(GeneralCategory.class.getClassLoader());
        this.df = (DecimalFormat) in.readSerializable();
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

}
