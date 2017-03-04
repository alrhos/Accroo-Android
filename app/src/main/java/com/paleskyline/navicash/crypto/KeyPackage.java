package com.paleskyline.navicash.crypto;

/**
 * Created by oscar on 4/03/17.
 */

public class KeyPackage {

    private String encryptedMasterKey;
    private String nonce;
    private String salt;
    private int opslimit;
    private int memlimit;

    public KeyPackage(String encryptedMasterKey, String nonce, String salt, int opslimit, int memlimit) {

        this.encryptedMasterKey = encryptedMasterKey;
        this.nonce = nonce;
        this.salt = salt;
        this.opslimit = opslimit;
        this.memlimit = memlimit;

    }

    public String getEncryptedMasterKey() {
        return encryptedMasterKey;
    }

    public void setEncryptedMasterKey(String encryptedMasterKey) {
        this.encryptedMasterKey = encryptedMasterKey;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getOpslimit() {
        return opslimit;
    }

    public void setOpslimit(int opslimit) {
        this.opslimit = opslimit;
    }

    public int getMemlimit() {
        return memlimit;
    }

    public void setMemlimit(int memlimit) {
        this.memlimit = memlimit;
    }
}
