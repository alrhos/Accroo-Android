package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EncryptedDefaultGeneralCategory {

    @Expose private String data;
    @Expose private String nonce;
    @Expose @SerializedName("sub_categories")
    private ArrayList<EncryptedDefaultSubCategory> subCategories = new ArrayList<>();

    public EncryptedDefaultGeneralCategory(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public ArrayList<EncryptedDefaultSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<EncryptedDefaultSubCategory> subCategories) {
        this.subCategories = subCategories;
    }
}
