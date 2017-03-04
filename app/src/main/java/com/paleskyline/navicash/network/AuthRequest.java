package com.paleskyline.navicash.network;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oscar on 4/03/17.
 */

public class AuthRequest extends JsonObjectRequest {

    private String email, password, token, authType;
    public static final String BASIC = "Basic";
    public static final String TOKEN = "Token";

    protected AuthRequest(int method, String url, JSONObject json,
                       Response.Listener<JSONObject> listener,
                       Response.ErrorListener errorListener,
                          String authType, String email,
                          String password, String token) {

        super(method, url, json, listener, errorListener);
        this.authType = authType;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (authType.equals(BASIC)) {
            return createBasicAuthHeader(email, password);
        } else if (authType.equals(TOKEN)) {
            return createTokenAuthHeader(token);
        }
        return null;
    }

    Map<String, String> createBasicAuthHeader(String username, String password) {
        Map<String, String> headerMap = new HashMap<>();
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + encodedCredentials);
        return headerMap;
    }

    Map<String, String> createTokenAuthHeader(String token) {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Token " + token);
        return headerMap;
    }
}
