package com.paleskyline.navicash.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oscar on 4/03/17.
 */

public class RestRequest extends JsonObjectRequest implements Cloneable {

    private String authType;
    public static final String BASIC = "Basic";
    public static final String TOKEN = "Token";
    public static final String NONE = "None";
    private Map<String, String> headerMap;

    public RestRequest(int method, String url, JSONObject json,
                          Response.Listener<JSONObject> listener,
                          Response.ErrorListener errorListener,
                          String authType) {

        super(method, url, json, listener, errorListener);
        this.authType = authType;

        // DefaultRetryPolicy.DEFAULT_TIMEOUT_MS

        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        this.setRetryPolicy(retryPolicy);
    }

    public String getAuthType() {
        return authType;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerMap;
    }

    /*

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (authType.equals(BASIC)) {
            return createBasicAuthHeader();
        } else if (authType.equals(TOKEN)) {
            return createTokenAuthHeader();
        } else if (authType.equals(NONE)) {
            return createEmptyHeader();
        }
        return null;
    }

    Map<String, String> createBasicAuthHeader() {
        Map<String, String> headerMap = new HashMap<>();
        String credentials = AuthManager.USERNAME + ":" + String.copyValueOf(AuthManager.LOGINPASSWORD);
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + encodedCredentials);
        return headerMap;
    }

    Map<String, String> createTokenAuthHeader() {
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Authorization", "Token " + AuthManager.TOKEN);
        return headerMap;
    }

    Map<String, String> createEmptyHeader() {
        Map<String, String> headerMap = new HashMap<>();
        //return new HashMap<>();
        return headerMap;
    }

    */

    public void setHeader(String headerValue) {
        headerMap = new HashMap<>();
        System.out.println("HEADER VALUE IS: " + headerValue);
        headerMap.put("Authorization", authType + " " + headerValue);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
