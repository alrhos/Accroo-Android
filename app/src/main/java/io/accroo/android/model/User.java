package io.accroo.android.model;

import org.json.JSONObject;

/**
 * Created by oscar on 4/03/17.
 */

public class User {

    private String email;
    private char[] password;
    private Key key;
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

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public void setPreferences(Preferences preferences) {
        this.preferences = preferences;
    }

}
