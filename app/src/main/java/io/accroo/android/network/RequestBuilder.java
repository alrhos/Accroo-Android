package io.accroo.android.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import io.accroo.android.services.CredentialService;
import io.accroo.android.services.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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
    public final static String GENERAL_CATEGORIES = "users/<userId>/categories/general";
    public final static String GENERAL_CATEGORY =   "users/<userId>/categories/general/<categoryId>";
    public final static String SUB_CATEGORIES =     "users/<userId>/categories/sub";
    public final static String SUB_CATEGORY =       "users/<userId>/categories/sub/<categoryId>";
    public final static String TRANSACTIONS =       "users/<userId>/transactions";
    public final static String TRANSACTION =        "users/<userId>/transactions/<transactionId>";

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

    public static JsonObjectRequest deleteRefreshToken(int index, final RequestCoordinator coordinator,
                                                       final String refreshToken) {
        String url = baseURL + REFRESH_TOKEN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + refreshToken);
                return headerMap;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Delete response return 204 - no content
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postAccessToken(int index, final RequestCoordinator coordinator,
                                                       final String refreshToken) {
        String url = baseURL + ACCESS_TOKEN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + refreshToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getKey(int index, final RequestCoordinator coordinator,
                                           String userId, final String accessToken) {
        String url = baseURL + ENCRYPTION_KEY;
        url = url.replace("<userId>", userId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
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

    public static JsonObjectRequest putKey(int index, final RequestCoordinator coordinator,
                                           String json, String userId, final String accessToken) throws JSONException {
        String url = baseURL + ENCRYPTION_KEY;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
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
        JSONArray jsonArray = new JSONArray(json);
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

    public static JsonArrayRequest getTransactions(int index, final RequestCoordinator coordinator,
                                                   String userId, final String accessToken) {
        String url = baseURL + TRANSACTIONS;
        url = url.replace("<userId>", userId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
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

    public static JsonArrayRequest getGeneralCategories(int index, final RequestCoordinator coordinator,
                                                        String userId, final String accessToken) {
        String url = baseURL + GENERAL_CATEGORIES;
        url = url.replace("<userId>", userId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
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

    public static JsonArrayRequest getSubCategories(int index, final RequestCoordinator coordinator,
                                                    String userId, final String accessToken) {
        String url = baseURL + SUB_CATEGORIES;
        url = url.replace("<userId>", userId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
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

    public static JsonObjectRequest postTransaction(int index, final RequestCoordinator coordinator,
                                                    String userId, String json, final String accessToken) throws JSONException {
        String url = baseURL + TRANSACTIONS;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
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

    public static JsonObjectRequest putTransaction(int index, final RequestCoordinator coordinator,
                                                   String userId, String transactionId,
                                                   String json, final String accessToken) throws JSONException {
        String url = baseURL + TRANSACTION;
        url = url.replace("<userId>", userId);
        url = url.replace("<transactionId>", transactionId);
        JSONObject object = new JSONObject(json);
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

    public static JsonObjectRequest deleteTransaction(int index, final RequestCoordinator coordinator,
                                                      String userId, String transactionId,
                                                      final String accessToken) {
        String url = baseURL + TRANSACTION;
        url = url.replace("<userId>", userId);
        url = url.replace("<transactionId>", transactionId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Delete response return 204 - no content
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postGeneralCategory(int index, final RequestCoordinator coordinator,
                                                        String userId, String json, final String accessToken) throws JSONException {
        String url = baseURL + GENERAL_CATEGORIES;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
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

    public static JsonObjectRequest putGeneralCategory(int index, final RequestCoordinator coordinator,
                                                       String userId, String categoryId,
                                                       String json, final String accessToken) throws JSONException {
        String url = baseURL + GENERAL_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JSONObject object = new JSONObject(json);
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

    public static JsonObjectRequest deleteGeneralCategory(int index, final RequestCoordinator coordinator,
                                                          String userId, String categoryId,
                                                          final String accessToken) {
        String url = baseURL + GENERAL_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Delete response return 204 - no content
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postSubCategory(int index, final RequestCoordinator coordinator,
                                                    String userId, String json, final String accessToken) throws JSONException {
        String url = baseURL + SUB_CATEGORIES;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
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

    public static JsonObjectRequest putSubCategory(int index, final RequestCoordinator coordinator,
                                                   String userId, String categoryId,
                                                   String json, final String accessToken) throws JSONException {
        String url = baseURL + SUB_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JSONObject object = new JSONObject(json);
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

    public static JsonObjectRequest deleteSubCategory(int index, final RequestCoordinator coordinator,
                                                      String userId, String categoryId,
                                                      final String accessToken) {
        String url = baseURL + SUB_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                // Delete response return 204 - no content
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
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
                case 404:
                    return ApiService.NOT_FOUND;
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
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
                error.printStackTrace();
                coordinator.abort(parseVolleyException(error));
            }
        };
    }

    private static Response.Listener<JSONObject> createJsonObjectResponseListener(
            final int index, final RequestCoordinator coordinator) {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                coordinator.done(index, response);
            }
        };
    }

    private static Response.Listener<JSONArray> createJsonArrayResponseListener(
            final int index, final RequestCoordinator coordinator) {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                coordinator.done(index, response);
            }
        };
    }

}