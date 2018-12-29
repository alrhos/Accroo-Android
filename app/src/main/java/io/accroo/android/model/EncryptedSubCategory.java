package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.io.UnsupportedEncodingException;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedSubCategory {

    @Expose (serialize = false) private int id;
    @Expose private int generalCategoryId;
    @Expose private String data;
    @Expose private String nonce;

    public EncryptedSubCategory(int id, int generalCategoryId, String data, String nonce) {
        this.id = id;
        this.generalCategoryId = generalCategoryId;
        this.data = data;
        this.nonce = nonce;
    }

    public EncryptedSubCategory(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
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

    public SubCategory decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(this.data, this.nonce);
        String categoryString = CryptoManager.getInstance().decrypt(securePayload);
        SubCategory subCategory = GsonUtil.getInstance().fromJson(categoryString, SubCategory.class);
        subCategory.setId(this.id);
        subCategory.setGeneralCategoryId(this.generalCategoryId);
        return subCategory;
    }

    @Override
    public String toString() {
        return "EncryptedSubCategory{" +
                "id=" + id +
                ", generalCategoryId=" + generalCategoryId +
                ", data='" + data + '\'' +
                ", nonce='" + nonce + '\'' +
                '}';
    }
}
