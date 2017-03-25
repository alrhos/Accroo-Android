package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.SecurePayload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 24/03/17.
 */

public class SubCategory implements Securable {

    private int id, generalCategoryID;
    private String categoryName;

    public SubCategory(String categoryName, int generalCategoryID) {
        this.categoryName = categoryName;
        this.generalCategoryID = generalCategoryID;
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

    public int getGeneralCategoryID() {
        return generalCategoryID;
    }

    public void setGeneralCategoryID(int generalCategoryID) {
        this.generalCategoryID = generalCategoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public JSONObject encrypt() throws JSONException {
        JSONObject categoryData = new JSONObject();
        categoryData.put("categoryName", categoryName);
        SecurePayload payload = CryptoManager.getInstance().encrypt(categoryData.toString());
        JSONObject json = new JSONObject();
        json.put("generalCategoryID", generalCategoryID);
        json.put("payload", payload.getData());
        json.put("nonce", payload.getNonce());
        return json;
    }

    @Override
    public void decrypt(JSONObject json) throws JSONException, UnsupportedEncodingException {
        SecurePayload payload = new SecurePayload(json.getString("payload"), json.getString("nonce"));
        String categoryString = CryptoManager.getInstance().decrypt(payload);
        JSONObject categoryData = new JSONObject(categoryString);
        this.id = json.getInt("id");
        this.generalCategoryID = json.getInt("generalCategoryID");
        this.categoryName = categoryData.getString("categoryName");
    }

    @Override
    public String toString() {
        return "SubCategory{" +
                "id=" + id +
                ", generalCategoryID=" + generalCategoryID +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
