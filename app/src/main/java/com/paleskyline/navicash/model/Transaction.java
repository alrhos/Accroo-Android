package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.SecurePayload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 25/03/17.
 */

public class Transaction implements Securable {

    private int id;
    private int subCategoryID;
    private String date;
    private double amount;
    private String description;

    public Transaction(int subCategoryID, String date, double amount, String description) {
        this.subCategoryID = subCategoryID;
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
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

    @Override
    public JSONObject encrypt() throws JSONException {
        JSONObject transactionData = new JSONObject();
        transactionData.put("date", date);
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
        this.date = transactionData.getString("date");
        this.amount = transactionData.getDouble("amount");
        this.description = transactionData.getString("description");
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", subCategoryID=" + subCategoryID +
                ", date='" + date + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                '}';
    }

}
