package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedTransaction {

    @Expose (serialize = false) private UUID id;
    @Expose @SerializedName("sub_category_id") private UUID subCategoryId;
    @Expose private String data;
    @Expose private String nonce;

    public EncryptedTransaction(UUID id, UUID subCategoryId, String data, String nonce) {
        this.id = id;
        this.subCategoryId = subCategoryId;
        this.data = data;
        this.nonce = nonce;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(UUID subCategoryId) {
        this.subCategoryId = subCategoryId;
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

    public Transaction decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(this.data, this.nonce);
        String transactionJson = CryptoManager.getInstance().decrypt(securePayload);
        Transaction transaction = GsonUtil.getInstance().fromJson(transactionJson, Transaction.class);
        transaction.setId(this.id);
        transaction.setSubCategoryId(this.subCategoryId);
        return transaction;
    }

}
