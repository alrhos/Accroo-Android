package com.paleskyline.accroo.model;

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
    private Preferences preferences;

    public User(String email, char[] loginPassword, KeyPackage keyPackage) {
        this.email = email;
        this.loginPassword = loginPassword;
        this.keyPackage = keyPackage;
    }

    public User(String email, char[] loginPassword, char[] dataPassword, Preferences preferences) {
        this.email = email;
        this.loginPassword = loginPassword;
        this.dataPassword = dataPassword;
        this.preferences = preferences;
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

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public void clearPasswords() {
        // TODO: clear password arrays
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", String.copyValueOf(loginPassword));
            json.put("keyPackage", keyPackage.toJSON());
            json.put("preferences", preferences.encrypt());
            // TODO: Wipe password array
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("USER REGO: " + json.toString());
        return json;
    }

}
