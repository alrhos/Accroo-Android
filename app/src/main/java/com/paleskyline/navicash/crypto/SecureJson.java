package com.paleskyline.navicash.crypto;

/**
 * Created by oscar on 4/03/17.
 */

public class SecureJson {

    private String encryptedJson;
    private String nonce;

    public SecureJson(String encryptedJson, String nonce) {
        this.encryptedJson = encryptedJson;
        this.nonce = nonce;
    }

    public String getEncryptedJson() {
        return encryptedJson;
    }

    public void setEncryptedJson(String encryptedJson) {
        this.encryptedJson = encryptedJson;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

}
