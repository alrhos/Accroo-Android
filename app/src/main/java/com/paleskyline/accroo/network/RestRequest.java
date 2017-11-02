package com.paleskyline.accroo.network;

import android.content.Context;

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
    public static final String TOKEN = "Bearer";
    public static final String NONE = "None";
    private Map<String, String> headerMap;
    private Context context;

    // TODO: these string variables should be moved to resources file

//    public final static String UNAUTHORIZED = "Authentication failed";
//    public final static String TIMEOUT_ERROR = "The connection timed out";
//    public final static String CONNECTION_ERROR = "Connection error";
//    public final static String GENERAL_ERROR = "An error occurred";
//
//    public final static String INVALID_INPUT = "Invalid or incomplete details provided";
//    public final static String DATABASE_UNAVAILABLE = "Database unavailable";
//
//    public final static String EMAIL_IN_USE = "This email address is already being used";
//    public final static String NO_TRANSACTION = "Transaction does not exist";
//    public final static String NO_CATEGORY = "Category does not exist";


//    protected RestRequest(int method, String url, JSONObject json,
//                       Response.Listener<JSONObject> listener,
//                       Response.ErrorListener errorListener,
//                       String authType, Context context) throws Exception {
//
//        super(method, url, json, listener, errorListener);
//        this.authType = authType;
//        this.context = context;
//        //this.setAuthHeader();
//        this.setRetryPolicy(new DefaultRetryPolicy(5000,
//                0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//    }

    protected RestRequest(int method, String url, JSONObject json, Response.Listener<JSONObject>
                          responseListener, Response.ErrorListener errorListener, String authType,
                          String authValue, Context context) {

        super(method, url, json, responseListener, errorListener);
        this.authType = authType;
        this.context = context;
        setAuthHeader(authValue);
        setRetryPolicy(new DefaultRetryPolicy(5000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerMap;
    }

//    protected void setAuthHeader() throws Exception {
//        String headerValue;
//        switch (authType) {
//            case BASIC:
//                String username = AuthManager.getInstance(context).getEntry(AuthManager.USERNAME_KEY);
//                // TODO: review password security
//                String password = AuthManager.getInstance(context).getEntry(AuthManager.PASSWORD_KEY);
//                String credentials = username + ":" + password;
//                headerValue = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
//                break;
//            case REFRESH_TOKEN:
//                headerValue = AuthManager.getInstance(context).getEntry(AuthManager.REFRESH_TOKEN_KEY);
//                break;
//            case ACCESS_TOKEN:
//                headerValue = AuthManager.getInstance(context).getEntry(AuthManager.ACCESS_TOKEN_KEY);
//                break;
//            default:
//                headerValue = NONE;
//                break;
//        }
//        headerMap = new HashMap<>();
//        headerMap.put("Authorization", authType + " " + headerValue);
//    }

    protected String getAuthType() {
        return authType;
    }

    protected void setAuthHeader(String authValue) {

        // TODO: don't set auth header for requests that don't require authorization

        headerMap = new HashMap<>();
        headerMap.put("Authorization", authType + " " + authValue);
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
