package com.paleskyline.navicash.crypto;

import android.util.Base64;

import java.util.Arrays;

/**
 * Created by oscar on 4/03/17.
 */

public class KeyPackage {

    private byte[] encryptedMasterKey;
    private byte[] nonce;
    private byte[] salt;
    private int opslimit;
    private int memlimit;

    public KeyPackage(byte[] encryptedMasterKey, byte[] nonce, byte[] salt, int opslimit, int memlimit) {

        this.encryptedMasterKey = encryptedMasterKey;
        this.nonce = nonce;
        this.salt = salt;
        this.opslimit = opslimit;
        this.memlimit = memlimit;

    }

    protected byte[] getEncryptedMasterKey() {
        return encryptedMasterKey;
    }

    public String getEncodedEncryptedMasterKey() {
        return Base64.encodeToString(encryptedMasterKey, Base64.NO_WRAP);
    }

    protected void setEncryptedMasterKey(byte[] encryptedMasterKey) {
        this.encryptedMasterKey = encryptedMasterKey;
    }

    protected byte[] getNonce() {
        return nonce;
    }

    public String getEncodedNonce() {
        return Base64.encodeToString(nonce, Base64.NO_WRAP);
    }

    protected void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    protected byte[] getSalt() {
        return salt;
    }

    public String getEncodedSalt() {
        return Base64.encodeToString(salt, Base64.NO_WRAP);
    }

    protected void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public int getOpslimit() {
        return opslimit;
    }

    protected void setOpslimit(int opslimit) {
        this.opslimit = opslimit;
    }

    public int getMemlimit() {
        return memlimit;
    }

    protected void setMemlimit(int memlimit) {
        this.memlimit = memlimit;
    }

    @Override
    public String toString() {
        return "KeyPackage{" +
                "encryptedMasterKey=" + Arrays.toString(encryptedMasterKey) +
                ", nonce=" + Arrays.toString(nonce) +
                ", salt=" + Arrays.toString(salt) +
                ", opslimit=" + opslimit +
                ", memlimit=" + memlimit +
                '}';
    }
}
