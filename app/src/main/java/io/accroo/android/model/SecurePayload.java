package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.util.UUID;

/**
 * Created by oscar on 4/03/17.
 */

public class SecurePayload {

    @Expose(serialize = false) private UUID id;
    @Expose private String data;
    @Expose private String nonce;

    public SecurePayload(UUID id, String data, String nonce) {
        this.id = id;
        this.data = data;
        this.nonce = nonce;
    }

    public SecurePayload(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    @Override
    public String toString() {
        return "SecurePayload{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", nonce='" + nonce + '\'' +
                '}';
    }

}
