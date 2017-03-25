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

    public static RestRequest registerAccount(final int index, final RequestCoordinator coordinator,
                                             JSONObject json) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + "register";

        RestRequest restRequest = new RestRequest(Request.Method.POST, url, json, responseListener,
                errorListener, RestRequest.NONE, coordinator.getTag());

        return restRequest;
    }

    protected static RestRequest getToken(final RequestCoordinator coordinator) {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    AuthManager.setToken(response.getString("token"));
                    coordinator.tokenRefresh();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                JSONObject json = parseVolleyException(error);
                coordinator.onFailure(json);
            }
        };

        String url = baseURL + "token";

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.BASIC, coordinator.getTag());

        return restRequest;

    }

    public static RestRequest getEncryptionKey(final int index, final RequestCoordinator coordinator) {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                coordinator.done(index, response);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int responseCode = getResponseCode(error);
                JSONObject json = parseVolleyException(error);
                coordinator.receiveError(responseCode, json);
            }
        };

        String url = baseURL + "key";

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.TOKEN, coordinator.getTag());

        return restRequest;

    }

    public static RestRequest addGeneralCategory(final int index, final RequestCoordinator coordinator, JSONObject json) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + "generalcategory";

        RestRequest restRequest = new RestRequest(Request.Method.POST, url, json, responseListener,
                errorListener, RestRequest.TOKEN, coordinator.getTag());

        return restRequest;
    }

    public static RestRequest addSubCategory(final int index, final RequestCoordinator coordinator, JSONObject json) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + "subcategory";

        RestRequest restRequest = new RestRequest(Request.Method.POST, url, json, responseListener,
                errorListener, RestRequest.TOKEN, coordinator.getTag());

        return restRequest;
    }

    public static RestRequest getGeneralCategories(final int index, final RequestCoordinator coordinator) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + "generalcategory";

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null, responseListener,
                errorListener, RestRequest.TOKEN, coordinator.getTag());

        return restRequest;
    }

    public static RestRequest getSubCategories(final int index, final RequestCoordinator coordinator) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + "subcategory";

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null, responseListener,
                errorListener, RestRequest.TOKEN, coordinator.getTag());

        return restRequest;
    }

    private static int getResponseCode(VolleyError error) {
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

    private static Response.Listener<JSONObject> createResponseListener(final int index, final RequestCoordinator coordinator) {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                coordinator.done(index, response);
            }
        };
        return responseListener;
    }

    private static Response.ErrorListener createErrorListener(final RequestCoordinator coordinator) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int responseCode = getResponseCode(error);
                JSONObject json = parseVolleyException(error);
                coordinator.receiveError(responseCode, json);
            }
        };
        return errorListener;
    }

}
