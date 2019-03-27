package io.accroo.android.network;

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
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import io.accroo.android.BuildConfig;
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

    private final static String BASE_URL =               "https://dev.accroo.io/v1/";
    private final static String CLIENT_VERSION_KEY =     "Accroo-Client";
    private final static String CLIENT_VERSION_VALUE =   "Android " + BuildConfig.VERSION_NAME;
    private final static String RECAPTCHA_TOKEN_KEY =    "Recaptcha-Token";
    private final static String ACCOUNT =                "auth/accounts";
    private final static String EMAIL =                  "auth/accounts/email";
    private final static String VERIFICATION_TOKEN =     "auth/verification-tokens";
    private final static String REFRESH_TOKEN =          "auth/refresh-tokens";
    private final static String ACCESS_TOKEN =           "auth/access-tokens";
    private final static String ENCRYPTION_KEY =         "users/<userId>/keys";
    private final static String PREFERENCES =            "users/<userId>/preferences";
    private final static String CATEGORIES =             "users/<userId>/categories";
    private final static String GENERAL_CATEGORIES =     "users/<userId>/categories/general";
    private final static String GENERAL_CATEGORY =       "users/<userId>/categories/general/<categoryId>";
    private final static String SUB_CATEGORIES =         "users/<userId>/categories/sub";
    private final static String SUB_CATEGORY =           "users/<userId>/categories/sub/<categoryId>";
    private final static String TRANSACTIONS =           "users/<userId>/transactions";
    private final static String TRANSACTION =            "users/<userId>/transactions/<transactionId>";

    private final static DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(20000,
            0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private RequestBuilder() {}

    public static JsonObjectRequest postAccount(int index, final RequestCoordinator coordinator,
                                                String json, final String recaptchaToken) throws JSONException {
        String url = BASE_URL + ACCOUNT;
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put(RECAPTCHA_TOKEN_KEY, recaptchaToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postVerificationToken(int index, final RequestCoordinator coordinator,
                                                          JSONObject object) {
        String url = BASE_URL + VERIFICATION_TOKEN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postRefreshToken(int index, final RequestCoordinator coordinator,
                                                     String username, String verificationToken) {
        String url = BASE_URL + REFRESH_TOKEN;
        String credentials = username + ":" + verificationToken;
        final String authHeader = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Basic " + authHeader);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest deleteRefreshToken(int index, final RequestCoordinator coordinator,
                                                       final String refreshToken) {
        String url = BASE_URL + REFRESH_TOKEN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + ACCESS_TOKEN;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + refreshToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest putEmail(int index, final RequestCoordinator coordinator,
                                             final String accessToken, JSONObject object) {
        String url = BASE_URL + EMAIL;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest getKey(int index, final RequestCoordinator coordinator,
                                           String userId, final String accessToken) {
        String url = BASE_URL + ENCRYPTION_KEY;
        url = url.replace("<userId>", userId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest putKey(int index, final RequestCoordinator coordinator,
                                           String json, String userId, final String accessToken) throws JSONException {
        String url = BASE_URL + ENCRYPTION_KEY;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest putPreferences(int index, final RequestCoordinator coordinator,
                                           String json, String userId, final String accessToken) throws JSONException {
        String url = BASE_URL + PREFERENCES;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + CATEGORIES;
        url = url.replace("<userId>", userId);
        JSONArray jsonArray = new JSONArray(json);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, jsonArray,
                createJsonArrayResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonArrayRequest getTransactions(int index, final RequestCoordinator coordinator,
                                                   String userId, final String accessToken) {
        String url = BASE_URL + TRANSACTIONS;
        url = url.replace("<userId>", userId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                createJsonArrayResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonArrayRequest getGeneralCategories(int index, final RequestCoordinator coordinator,
                                                        String userId, final String accessToken) {
        String url = BASE_URL + GENERAL_CATEGORIES;
        url = url.replace("<userId>", userId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                createJsonArrayResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonArrayRequest getSubCategories(int index, final RequestCoordinator coordinator,
                                                    String userId, final String accessToken) {
        String url = BASE_URL + SUB_CATEGORIES;
        url = url.replace("<userId>", userId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                createJsonArrayResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
                headerMap.put("Authorization", "Bearer " + accessToken);
                return headerMap;
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postTransaction(int index, final RequestCoordinator coordinator,
                                                    String userId, String json, final String accessToken) throws JSONException {
        String url = BASE_URL + TRANSACTIONS;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + TRANSACTION;
        url = url.replace("<userId>", userId);
        url = url.replace("<transactionId>", transactionId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + TRANSACTION;
        url = url.replace("<userId>", userId);
        url = url.replace("<transactionId>", transactionId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + GENERAL_CATEGORIES;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + GENERAL_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + GENERAL_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + SUB_CATEGORIES;
        url = url.replace("<userId>", userId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + SUB_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JSONObject object = new JSONObject(json);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, object,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
        String url = BASE_URL + SUB_CATEGORY;
        url = url.replace("<userId>", userId);
        url = url.replace("<categoryId>", categoryId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, null,
                createJsonObjectResponseListener(index, coordinator), createErrorListener(coordinator)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headerMap = new HashMap<>();
                headerMap.put(CLIENT_VERSION_KEY, CLIENT_VERSION_VALUE);
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
                case 502:
                    return ApiService.SERVICE_UNAVAILABLE;
                case 503:
                    return ApiService.SERVICE_UNAVAILABLE;
                case 521:
                    // Cloudflare: origin refused connection
                    return ApiService.SERVICE_UNAVAILABLE;
                case 522:
                    // Cloudflare: connection timed out
                    return ApiService.TIMEOUT_ERROR;
                case 523:
                    // Cloudflare: origin unreachable
                    return ApiService.SERVICE_UNAVAILABLE;
                case 524:
                    // Cloudflare: timeout
                    return ApiService.TIMEOUT_ERROR;
                case 530:
                    // Cloudflare: origin DNS error - tunnel is unavailable
                    return ApiService.SERVICE_UNAVAILABLE;
            }
        }
        return ApiService.GENERIC_ERROR;
    }

    private static Response.ErrorListener createErrorListener(final RequestCoordinator coordinator) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                try {
                    System.out.println(new String(error.networkResponse.data, "UTF-8"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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