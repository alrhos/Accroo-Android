package com.paleskyline.navicash.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscar on 4/03/17.
 */

public class User {

    private String email;
    private char[] loginPassword;
    private char[] dataPassword;
    private KeyPackage keyPackage;

    public User(String email, char[] loginPassword, KeyPackage keyPackage) {
        this.email = email;
        this.loginPassword = loginPassword;
        this.keyPackage = keyPackage;
    }

    public User(String email, char[] loginPassword, char[] dataPassword) {
        this.email = email;
        this.loginPassword = loginPassword;
        this.dataPassword = dataPassword;
    }

    public User(String email, char[] loginPassword) {
        this.email = email;
        this.loginPassword = loginPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPasswordPassword(char[] password) {
        this.loginPassword = password;
    }

    public void setLoginPassword(char[] loginPassword) {
        this.loginPassword = loginPassword;
    }

    public char[] getDataPassword() {
        return dataPassword;
    }

    public void setDataPassword(char[] dataPassword) {
        this.dataPassword = dataPassword;
    }

    public KeyPackage getKeyPackage() {
        return keyPackage;
    }

    public void setKeyPackage(KeyPackage keyPackage) {
        this.keyPackage = keyPackage;
    }

    public void clearPasswords() {
        // TODO: clear password arrays
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", String.copyValueOf(loginPassword));

            JSONObject keyData = new JSONObject();
            keyData.put("key", keyPackage.getEncryptedMasterKey());
            keyData.put("salt", keyPackage.getSalt());
            keyData.put("nonce", keyPackage.getNonce());
            keyData.put("opslimit", keyPackage.getOpslimit());
            keyData.put("memlimit", keyPackage.getMemlimit());

            json.put("keyPackage", keyData);




//            json.put("dataKey", keyPackage.getEncryptedMasterKey());
//            json.put("salt", keyPackage.getSalt());
//            json.put("nonce", keyPackage.getNonce());
//            json.put("opsLimit", keyPackage.getOpslimit());
//            json.put("memLimit", keyPackage.getMemlimit());
            // TODO: Wipe password array
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

//    public JSONObject toJSONLogin() {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("email", email);
//            json.put("password", String.copyValueOf(loginPassword));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }

}
