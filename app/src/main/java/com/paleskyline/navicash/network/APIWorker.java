package com.paleskyline.navicash.network;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paleskyline.navicash.crypto.KeyPackage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 4/03/17.
 */

public class APIWorker {

    private String baseURL = "http://192.168.1.21:5000/api/";
    private RequestQueue requestQueue;
    private static APIWorker instance = null;

    public static APIWorker getInstance(Context context) {
        if (instance == null) {
            instance = new APIWorker(context);
        }
        return instance;
    }

    private APIWorker(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void registerAccount(String email, char[] password, KeyPackage keyPackage) {
        String url = baseURL + "register";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", String.copyValueOf(password));
            json.put("masterkey", keyPackage.getEncryptedMasterKey());
            json.put("salt", keyPackage.getSalt());
            json.put("nonce", keyPackage.getNonce());
            json.put("opslimit", keyPackage.getOpslimit());
            json.put("memlimit", keyPackage.getMemlimit());

            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject obj = parseVolleyException(error);
                        System.out.println(obj.toString());
                    }
                });

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getToken(String email, String password) {

        String url = baseURL + "token";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response) {
                 System.out.println(response.toString());
             }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
            }
        };

        AuthRequest authRequest = new AuthRequest(Request.Method.GET, url, null,
                responseListener, errorListener, AuthRequest.BASIC, email, password, null);

        requestQueue.add(authRequest);
    }

    public void getEncryptionKey(String token) {

        String url = baseURL + "key";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
            }
        };

        AuthRequest authRequest = new AuthRequest(Request.Method.GET, url, null,
                responseListener, errorListener, AuthRequest.TOKEN, null, null, token);

        requestQueue.add(authRequest);

    }

    private JSONObject parseVolleyException(VolleyError error) {
        JSONObject obj = null;
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null) {
            try {
                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
                obj = new JSONObject(res);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return obj;
    }
}
