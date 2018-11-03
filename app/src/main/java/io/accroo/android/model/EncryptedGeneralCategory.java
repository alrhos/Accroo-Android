package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.io.UnsupportedEncodingException;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedGeneralCategory {

    private int id;
    @Expose private String data;
    @Expose private String nonce;

    public EncryptedGeneralCategory(int id, String data, String nonce) {
        this.id = id;
        this.data = data;
        this.nonce = nonce;
    }

    public EncryptedGeneralCategory(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public GeneralCategory decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(this.data, this.nonce);
        String categoryString = CryptoManager.getInstance().decrypt(securePayload);
        GeneralCategory generalCategory = (GeneralCategory) GsonUtil.getInstance()
                .fromJson(categoryString, GeneralCategory.class);
        generalCategory.setId(this.id);
        return generalCategory;
    }

}
