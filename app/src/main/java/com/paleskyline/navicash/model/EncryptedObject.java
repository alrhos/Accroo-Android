package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.SecurePayload;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 21/03/17.
 */

public abstract class EncryptedObject<T> {

    private SecurePayload payload;

    public EncryptedObject(SecurePayload payload) {
        this.payload = payload;
    }

    public SecurePayload getPayload() {
        return payload;
    }

    public void setPayload(SecurePayload payload) {
        this.payload = payload;
    }

    public abstract T decryptObject() throws UnsupportedEncodingException, JSONException;
}
