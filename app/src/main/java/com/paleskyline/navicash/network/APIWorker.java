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
import com.paleskyline.navicash.model.User;

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

    public void registerAccount(User user) {
        String url = baseURL + "register";
        final JSONObject json = new JSONObject();

        try {
            json.put("email", user.getEmailAddress());
            json.put("password", String.copyValueOf(user.getPassword()));
            json.put("masterkey", user.getKeyPackage().getEncodedEncryptedMasterKey());
            json.put("salt", user.getKeyPackage().getEncodedSalt());
            json.put("nonce", user.getKeyPackage().getEncodedNonce());
            json.put("opslimit", user.getKeyPackage().getOpslimit());
            json.put("memlimit", user.getKeyPackage().getMemlimit());

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
                            NetworkResponse response = error.networkResponse;
                            if (error instanceof ServerError && response != null) {
                                try {
                                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
                                    JSONObject obj = new JSONObject(res);
                                    System.out.println(obj.toString());
                                } catch (UnsupportedEncodingException e1) {
                                    e1.printStackTrace();
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                            }



                        }
                    });

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }



    }
}
