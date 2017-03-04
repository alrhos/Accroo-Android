package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.KeyPackage;

/**
 * Created by oscar on 4/03/17.
 */

public class User {

    private String emailAddress;
    private char[] password;
    private KeyPackage keyPackage;

    public User(String emailAddress, char[] password, KeyPackage keyPackage) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.keyPackage = keyPackage;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public KeyPackage getKeyPackage() {
        return keyPackage;
    }

    public void setKeyPackage(KeyPackage keyPackage) {
        this.keyPackage = keyPackage;
    }

}
