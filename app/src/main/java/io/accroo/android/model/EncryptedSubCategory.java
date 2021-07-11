package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedSubCategory {

    @Expose (serialize = false) private UUID id;
    @Expose @SerializedName("general_category_id") private UUID generalCategoryId;
    @Expose private String data;
    @Expose private String nonce;

    public EncryptedSubCategory(UUID id, UUID generalCategoryId, String data, String nonce) {
        this.id = id;
        this.generalCategoryId = generalCategoryId;
        this.data = data;
        this.nonce = nonce;
    }

    public EncryptedSubCategory(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGeneralCategoryId() {
        return generalCategoryId;
    }

    public void setGeneralCategoryId(UUID generalCategoryId) {
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
