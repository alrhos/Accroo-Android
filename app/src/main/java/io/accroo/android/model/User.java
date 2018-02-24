package io.accroo.android.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscar on 4/03/17.
 */

public class User {

    private String email;
    private char[] password;
    private KeyPackage keyPackage;
    private Preferences preferences;

    public User(String email, char[] password, Preferences preferences) {
        this.email = email;
        this.password = password;
        this.preferences = preferences;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("keyPackage", keyPackage.toJSON());
            json.put("preferences", preferences.encrypt());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

}
