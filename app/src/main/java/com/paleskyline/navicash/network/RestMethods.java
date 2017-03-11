package com.paleskyline.navicash.network;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.paleskyline.navicash.crypto.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 11/03/17.
 */

public class RestMethods {

    private final static String baseURL = "http://192.168.1.21:5000/api/";

    private RestMethods() {}

    public static RestRequest getToken(final RequestCoordinator coordinator, final RestRequest originalRequest) {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AuthManager.setToken(response.getString("token"));
                    if (originalRequest != null) {

                    }
                    //System.out.println(response.getString("token"));
                    System.out.println("TOKEN HAS BEEN AQUIRED");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                coordinator.done();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
                JSONObject json = parseVolleyException(error);
                coordinator.onFailure(json);
                // If error is because of incorrect login details then re-direct
                // user to login screen.
            }
        };

        String url = baseURL + "token";
        String email = AuthManager.USERNAME;
        String password = String.copyValueOf(AuthManager.LOGINPASSWORD);

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.BASIC, email, password, null);

        return restRequest;

    }

    private int getResponseCode(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            return response.statusCode;
        }
        return 0;
    }

    private static JSONObject parseVolleyException(VolleyError error) {
        JSONObject json = null;
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null) {
            try {
                String responseString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
                json = new JSONObject(responseString);
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    public static RestRequest getEncryptionKey(final RequestCoordinator coordinator) {

        String url = baseURL + "key";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                coordinator.done();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
            }
        };

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.TOKEN, null, null, AuthManager.TOKEN);

        return restRequest;

    }

}
