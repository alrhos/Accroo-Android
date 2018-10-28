package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

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

    public Preferences decrypt() throws JSONException, UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(this.data, this.nonce);
        String preferencesString = CryptoManager.getInstance().decrypt(securePayload);
        JSONObject preferencesJson = new JSONObject(preferencesString);
        return (Preferences) GsonUtil.getInstance().fromJson(preferencesJson, Preferences.class);
    }

}
