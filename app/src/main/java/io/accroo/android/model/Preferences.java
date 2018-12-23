package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

/**
 * Created by oscar on 25/11/17.
 */

public class Preferences {

    @Expose private String currency = "USD";

    public Preferences() {}

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public EncryptedPreferences encrypt() {
        String preferencesJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(preferencesJson);
        return new EncryptedPreferences(securePayload.getData(), securePayload.getNonce());
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "currency='" + currency + '\'' +
                '}';
    }
}
