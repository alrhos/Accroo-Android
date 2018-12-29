package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.io.UnsupportedEncodingException;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedPreferences {

    @Expose private String data;
    @Expose private String nonce;

    public EncryptedPreferences(String data, String nonce) {
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

    public Preferences decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(this.data, this.nonce);
        String preferencesString = CryptoManager.getInstance().decrypt(securePayload);
        return GsonUtil.getInstance().fromJson(preferencesString, Preferences.class);
    }

}
