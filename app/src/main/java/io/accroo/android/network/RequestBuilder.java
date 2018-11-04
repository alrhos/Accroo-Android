package io.accroo.android.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import io.accroo.android.services.CredentialService;
import io.accroo.android.services.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by oscar on 11/03/17.
 */

public class RequestBuilder {

    private final static String baseURL =           "http://192.168.1.15/v1/";
    public final static String ACCOUNT =            "auth/accounts";
    public final static String EMAIL =              "auth/accounts/email";
    public final static String VERIFICATION_TOKEN = "auth/verification-tokens";
    public final static String REFRESH_TOKEN =      "auth/refresh-tokens";
    public final static String ACCESS_TOKEN =       "auth/access-tokens";
    public final static String ENCRYPTION_KEY =     "users/<userId>/keys";
    public final static String PREFERENCES =        "users/<userId>/preferences";
    public final static String CATEGORIES =         "users/<userId>/categories";
    public final static String GENERAL_CATEGORY =   "users/<userId>/categories/general";
    public final static String SUB_CATEGORY =       "users/<userId>/categories/sub";
    public final static String TRANSACTION =        "users/<userId>/transactions";

    private final static DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(20000, 0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private RequestBuilder() {}

    public static JsonObjectRequest postAccount(int index, final RequestCoordinator coordinator,
                                             String json) throws JSONException {
        String url = baseURL + ACCOUNT;
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator));
        request.setRetryPolicy(retryPolicy);
        return request;

    }

    public static JsonObjectRequest postRefreshToken(int index, final RequestCoordinator coordinator,
                                                     String username, String verificationToken) {
        String url = baseURL + REFRESH_TOKEN;
        String credentials = username + ":" + verificationToken;
        final String authHeader = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Basic " + authHeader);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest putKey(int index, final RequestCoordinator coordinator,
                                           String json, String userId, final String accessToken) throws JSONException {
        String url = baseURL + ENCRYPTION_KEY;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        System.out.println(object.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest putPreferences(int index, final RequestCoordinator coordinator,
                                           String json, String userId, final String accessToken) throws JSONException {
        String url = baseURL + PREFERENCES;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        System.out.println(object.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonArrayRequest postDefaultCategories(int index, final RequestCoordinator coordinator,
                                                         String json, String userId,
                                                         final String accessToken) throws JSONException {
        String url = baseURL + CATEGORIES;
        url = url.replace("<userId>", userId);
        System.out.println(json);
        JSONArray jsonArray = new JSONArray(json);
        System.out.println(jsonArray.toString());
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
                createJsonArrayResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }
















    public static RestRequest basicAuth(int index, final RequestCoordinator coordinator,
                                        int method, JSONObject json, String endpoint,
                                        String username, String password) {

        Response.Listener<JSONObject> responseListener = createJsonObjectResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String credentials = username + ":" + password;
        String authValue = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        String url = baseURL + endpoint;

        return new RestRequest(method, url, json, responseListener,
                errorListener, RestRequest.BASIC, authValue);
    }

    public static RestRequest accessTokenAuth(int index, RequestCoordinator coordinator, int method,
                                              String endpoint, String userId, JSONObject json,
                                              Context context) throws Exception {
        Response.Listener<JSONObject> responseListener = createJsonObjectResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;
        url = url.replace("<userId>", userId);
        String authValue = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.TOKEN, authValue);
    }

    public static RestRequest accessTokenAuth(int index, RequestCoordinator coordinator, int method,
                                              String endpoint, String userId, String json,
                                              Context context) throws Exception {
        Response.Listener<JSONObject> responseListener = createJsonObjectResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;
        url = url.replace("<userId>", userId);
        String authValue = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
        JSONObject jsonObject = new JSONObject(json);

        return new RestRequest(method, url, jsonObject, responseListener, errorListener,
                RestRequest.TOKEN, authValue);
    }

    public static RestRequest deviceTokenAuth(int index, RequestCoordinator coordinator, int method,
                                              String endpoint, JSONObject json, Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createJsonObjectResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;
        String authValue = CredentialService.getInstance(context).getEntry(CredentialService.DEVICE_TOKEN_KEY);

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.TOKEN, authValue);
    }

    public static RestRequest noAuth(int index, RequestCoordinator coordinator, int method,
                                        String endpoint, JSONObject json) throws Exception {

        Response.Listener<JSONObject> responseListener = createJsonObjectResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.NONE, null);
    }

    public static RestRequest noAuth(int index, RequestCoordinator coordinator, int method,
                                     String endpoint, String json) throws Exception {

        Response.Listener<JSONObject> responseListener = createJsonObjectResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;
        JSONObject jsonObject = new JSONObject(json);

        return new RestRequest(method, url, jsonObject, responseListener, errorListener,
                RestRequest.NONE, null);
    }

    private static int getResponseCode(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            return response.statusCode;
        }
        return 0;
    }

    private static int parseVolleyException(VolleyError error) {
        if (error instanceof TimeoutError) {
            return ApiService.TIMEOUT_ERROR;
        } else if (error instanceof NoConnectionError) {
            return ApiService.CONNECTION_ERROR;
        } else if (error instanceof AuthFailureError) {
            return ApiService.UNAUTHORIZED;
        } else if (error instanceof ServerError) {
            int statusCode = getResponseCode(error);
            switch (statusCode) {
                case 400:
                    return ApiService.INVALID_REQUEST;
                case 409:
                    return ApiService.CONFLICT;
                case 429:
                    return ApiService.TOO_MANY_REQUESTS;
                case 500:
                    return ApiService.GENERIC_ERROR;
                case 521:
                    return ApiService.ORIGIN_UNAVAILABLE;
                case 522:
                    return ApiService.TIMEOUT_ERROR;
                case 523:
                    return ApiService.CONNECTION_ERROR;
                case 524:
                    return ApiService.TIMEOUT_ERROR;
            }
        }
        return ApiService.GENERIC_ERROR;
    }

    private static Response.ErrorListener createErrorListener(final RequestCoordinator coordinator) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
                error.printStackTrace();
                coordinator.abort(parseVolleyException(error));
            }
        };
        return errorListener;
    }

    private static Response.Listener<JSONObject> createJsonObjectResponseListener(final int index, final RequestCoordinator coordinator) {
        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                coordinator.done(index, response);
            }
        };
        return responseListener;
    }

    private static Response.Listener<JSONArray> createJsonArrayResponseListener(final int index, final RequestCoordinator coordinator) {
        Response.Listener<JSONArray> responseListener = new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                System.out.println("ARRAY REQUEST SUCCESS");
                System.out.println(response.toString());
                coordinator.done(index, response);
            }
        };
        return responseListener;
    }

}