package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.io.UnsupportedEncodingException;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedTransaction {

    private int id;
    @Expose private int subCategoryId;
    @Expose private String data;
    @Expose private String nonce;

    public EncryptedTransaction(int id, int subCategoryId, String data, String nonce) {
        this.id = id;
        this.subCategoryId = subCategoryId;
        this.data = data;
        this.nonce = nonce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(int subCategoryId) {
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
        Transaction transaction = (Transaction) GsonUtil.getInstance()
                .fromJson(transactionJson, Transaction.class);
        transaction.setId(this.id);
        transaction.setSubCategoryId(this.subCategoryId);
        return transaction;
    }

}
