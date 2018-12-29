package io.accroo.android.model;

import com.google.gson.annotations.Expose;

public class EncryptedDefaultSubCategory {

    @Expose private String data;
    @Expose private String nonce;

    public EncryptedDefaultSubCategory(String data, String nonce) {
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
}
