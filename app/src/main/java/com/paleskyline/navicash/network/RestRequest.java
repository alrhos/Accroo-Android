package com.paleskyline.navicash.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.paleskyline.navicash.crypto.AuthManager;

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
    private Context context;

    public final static String TIMEOUT_ERROR = "The connection timed out";
    public final static String CONNECTION_ERROR = "Connection error";
    public final static String GENERAL_ERROR = "An error occurred";

//    public RestRequest(int method, String url, JSONObject json,
//                          Response.Listener<JSONObject> listener,
//                          Response.ErrorListener errorListener,
//                          String authType) {
//
//        super(method, url, json, listener, errorListener);
//        this.authType = authType;
//
//        // DefaultRetryPolicy.DEFAULT_TIMEOUT_MS
//
//        DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
//                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//        this.setRetryPolicy(retryPolicy);
//    }

    protected RestRequest(int method, String url, JSONObject json,
                       Response.Listener<JSONObject> listener,
                       Response.ErrorListener errorListener,
                       String authType, Context context) throws Exception {

        super(method, url, json, listener, errorListener);
        this.authType = authType;
        this.context = context;


        this.setRetryPolicy(new DefaultRetryPolicy(5000,
                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        this.setAuthHeader();
    }

//    public String getAuthType() {
//        return authType;
//    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerMap;
    }

//    public void setHeader(String headerValue) {
//        headerMap = new HashMap<>();
//        System.out.println("HEADER VALUE IS: " + headerValue);
//        headerMap.put("Authorization", authType + " " + headerValue);
//    }

    protected void setAuthHeader() throws Exception {
        String headerValue;
        switch (authType) {
            case BASIC:
                String username = AuthManager.getInstance(context).getEntry(AuthManager.USERNAME_KEY);
                // TODO: review password security
                String password = AuthManager.getInstance(context).getEntry(AuthManager.PASSWORD_KEY);
                String credentials = username + ":" + password;
                headerValue = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                break;
            case TOKEN:
                headerValue = AuthManager.getInstance(context).getEntry(AuthManager.TOKEN_KEY);
                break;
            default:
                headerValue = NONE;
                break;
        }
        headerMap = new HashMap<>();
        headerMap.put("Authorization", authType + " " + headerValue);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
