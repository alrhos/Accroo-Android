package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.SecurePayload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by oscar on 4/03/17.
 */

public class GeneralCategory implements Securable {

    private int id;
    private String categoryName, rootCategory, iconFile;
    private ArrayList<SubCategory> subCategories = new ArrayList<>();

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
        this.categoryName = categoryData.getString("categoryName");
        this.rootCategory = categoryData.getString("rootCategory");
        this.iconFile = categoryData.getString("iconFile");
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

}
