package com.paleskyline.accroo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.paleskyline.accroo.crypto.CryptoManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by oscar on 25/03/17.
 */

public class Transaction implements Securable, Relationship, Parcelable {

    private int id;
    private int subCategoryID;
//    private String subCategoryName;
//    private String categoryIcon;
//    private String rootCategoryType;
    private String dateString;
    private double amount;
    private String description = "";
    private SubCategory parent;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Transaction(int subCategoryID, String dateString, double amount, String description) {
        this.subCategoryID = subCategoryID;
        this.dateString = dateString;
        this.amount = amount;
        if (description != null) {
            this.description = description;
        }
    }

    public Transaction(JSONObject json) throws JSONException, UnsupportedEncodingException {
        decrypt(json);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubCategoryID() {
        return subCategoryID;
    }

    public void setSubCategoryID(int subCategoryID) {
        this.subCategoryID = subCategoryID;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String date) {
        this.dateString = date;
    }

    public double getAmount() {
        return amount;
    }

    public String getFormattedAmount() {
        return decimalFormat.format(amount);
    }

    public String getFullyFormattedAmount() {
        return "$" + decimalFormat.format(amount);
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public String getSubCategoryName() {
//        return subCategoryName;
//    }
//
//    public void setSubCategoryName(String subCategoryName) {
//        this.subCategoryName = subCategoryName;
//    }

//    public String getCategoryIcon() {
//        return categoryIcon;
//    }
//
//    public void setCategoryIcon(String categoryIcon) {
//        this.categoryIcon = categoryIcon;
//    }
//
//    public String getRootCategoryType() {
//        return rootCategoryType;
//    }
//
//    public void setRootCategoryType(String rootCategoryType) {
//        this.rootCategoryType = rootCategoryType;
//    }

    public Date getDate() {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }

    }

    @Override
    public void setParent(Object parent) {
        this.parent = (SubCategory) parent;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    @Override
    public JSONObject encrypt() throws JSONException {

        JSONObject transactionData = new JSONObject();

        transactionData.put("date", dateString);
        transactionData.put("amount", amount);
        transactionData.put("description", description);

        SecurePayload payload = CryptoManager.getInstance().encrypt(transactionData.toString());
        JSONObject json = new JSONObject();

        if (id != 0) {
            json.put("id", id);
        }

        json.put("subCategoryID", subCategoryID);
        json.put("data", payload.getData());
        json.put("nonce", payload.getNonce());

        return json;
    }

    @Override
    public void decrypt(JSONObject json) throws JSONException, UnsupportedEncodingException {

        SecurePayload payload = new SecurePayload(json.getString("data"), json.getString("nonce"));

        String transactionString = CryptoManager.getInstance().decrypt(payload);
        JSONObject transactionData = new JSONObject(transactionString);

        this.id = json.getInt("id");
        this.subCategoryID = json.getInt("subCategoryID");
        this.dateString = transactionData.getString("date");
        this.amount = transactionData.getDouble("amount");
        this.description = transactionData.getString("description");
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", subCategoryID=" + subCategoryID +
                ", dateString='" + dateString + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", decimalFormat=" + decimalFormat +
                ", dateFormat=" + dateFormat +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.subCategoryID);
        dest.writeString(this.dateString);
        dest.writeDouble(this.amount);
        dest.writeString(this.description);
        dest.writeParcelable(this.parent, flags);
        dest.writeSerializable(this.decimalFormat);
        dest.writeSerializable(this.dateFormat);
    }

    protected Transaction(Parcel in) {
        this.id = in.readInt();
        this.subCategoryID = in.readInt();
        this.dateString = in.readString();
        this.amount = in.readDouble();
        this.description = in.readString();
        this.parent = in.readParcelable(SubCategory.class.getClassLoader());
        this.decimalFormat = (DecimalFormat) in.readSerializable();
        this.dateFormat = (SimpleDateFormat) in.readSerializable();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel source) {
            return new Transaction(source);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };
}
