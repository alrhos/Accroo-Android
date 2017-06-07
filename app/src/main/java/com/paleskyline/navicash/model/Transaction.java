package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.SecurePayload;

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

public class Transaction implements Securable{

    private int id;
    private int subCategoryID;
    private String subCategoryName;
    private String categoryIcon;
    private String rootCategoryType;
    private String dateString;
    private double amount;
    private String description;

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Transaction(int subCategoryID, String dateString, double amount, String description) {
        this.subCategoryID = subCategoryID;
        this.dateString = dateString;
        this.amount = amount;
        if (description == null) {
            this.description = "";
        } else {
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

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getRootCategoryType() {
        return rootCategoryType;
    }

    public void setRootCategoryType(String rootCategoryType) {
        this.rootCategoryType = rootCategoryType;
    }

    public Date getDate() {
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            return null;
        }

    }

    @Override
    public JSONObject encrypt() throws JSONException {
        JSONObject transactionData = new JSONObject();
        transactionData.put("date", dateString);
        transactionData.put("amount", amount);
        transactionData.put("description", description);
        SecurePayload payload = CryptoManager.getInstance().encrypt(transactionData.toString());
        JSONObject json = new JSONObject();
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
                ", date='" + dateString + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }

}
