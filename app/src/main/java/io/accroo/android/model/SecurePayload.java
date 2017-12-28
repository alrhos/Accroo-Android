package io.accroo.android.model;

/**
 * Created by oscar on 4/03/17.
 */

public class SecurePayload {

    private String data;
    private String nonce;

    public SecurePayload(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public String getData() {
        return data;
    }

    public void setData(String encryptedJson) {
        this.data = data;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

}
