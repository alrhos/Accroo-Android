package com.paleskyline.navicash.model;

import org.json.JSONException;
import org.json.JSONObject;

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

    public User(String emailAddress, char[] password) {
        this.emailAddress = emailAddress;
        this.password = password;
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

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", emailAddress);
            json.put("password", String.copyValueOf(password));
            json.put("dataKey", keyPackage.getEncryptedMasterKey());
            json.put("salt", keyPackage.getSalt());
            json.put("nonce", keyPackage.getNonce());
            json.put("opsLimit", keyPackage.getOpslimit());
            json.put("memLimit", keyPackage.getMemlimit());
            // TODO: Wipe password array
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONObject toJSONLogin() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", emailAddress);
            json.put("password", String.copyValueOf(password));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
