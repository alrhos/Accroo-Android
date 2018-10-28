package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 25/11/17.
 */

public class Preferences {

    @Expose private String currency = "USD";

    public Preferences() {}

//    public Preferences(JSONObject json) throws JSONException, UnsupportedEncodingException {
//        decrypt(json, EncryptedPreferences.class);
//    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public EncryptedPreferences encrypt() {
        JSONObject preferencesJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(preferencesJson.toString());
        return new EncryptedPreferences(securePayload.getData(), securePayload.getNonce());
    }

//    @Override
//    public JSONObject encrypt() throws JSONException {
//        JSONObject preferences = new JSONObject();
//        preferences.put("currency", currency);
//
//        SecurePayload payload = CryptoManager.getInstance().encrypt(preferences.toString());
//
//        JSONObject json = new JSONObject();
//        json.put("data", payload.getData());
//        json.put("nonce", payload.getNonce());
//
//        return json;
//    }
//
//    @Override
//    public void decrypt(JSONObject json) throws JSONException, UnsupportedEncodingException {
//        SecurePayload payload = new SecurePayload(json.getString("data"), json.getString("nonce"));
//        String preferencesString = CryptoManager.getInstance().decrypt(payload);
//        JSONObject preferencesJson = new JSONObject(preferencesString);
//        this.currency = preferencesJson.getString("currency");
//    }


    @Override
    public String toString() {
        return "Preferences{" +
                "currency='" + currency + '\'' +
                '}';
    }
}
