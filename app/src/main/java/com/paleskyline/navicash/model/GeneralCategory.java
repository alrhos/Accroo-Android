package com.paleskyline.navicash.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscar on 4/03/17.
 */

public class GeneralCategory {

    private int id;
    private String categoryName, rootCategory;

    public GeneralCategory(int id, JSONObject json) {
        this.id = id;
        try {
            this.categoryName = json.getString("name");
            this.rootCategory = json.getString("root_category");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GeneralCategory(String categoryName, String rootCategory) {
        this.categoryName = categoryName;
        this.rootCategory = rootCategory;
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

    public String getCategoryDetails() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", categoryName);
            json.put("root_category", rootCategory);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
