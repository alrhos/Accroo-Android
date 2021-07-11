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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oscar on 11/03/17.
 */

public class RequestBuilder {

    private final static String BASE_URL =               "https://api-dev.accroo.io/v2/";
    private final static String CLIENT_VERSION_KEY =     "Accroo-Client";
    private final static String CLIENT_VERSION_VALUE =   "Android " + BuildConfig.VERSION_NAME;
    private final static String RECAPTCHA_TOKEN_KEY =    "Recaptcha-Token";

    private final static String USERS = "users";
    private final static String USER = "users/<id>";
    private final static String EMAIL = "users/<id>/email";
    private final static String EMAIL_SEARCH = "users/email/<email>";
    private final static String KEY = "users/<id>/key";
    private final static String PREFERENCES = "users/<id>/preferences";
    private final static String VISITOR_TOKENS = "visitor-tokens";
    private final static String VERIFICATION_TOKENS = "verification-tokens";
    private final static String SESSIONS = "sessions";
    private final static String SESSION = "sessions/<id>";
    private final static String SESSION_REFRESH = "sessions/<id>/refresh";
    private final static String SESSION_AUTHENTICATION = "sessions/<id>/authenticate";
    private final static String SESSION_INVALIDATION = "sessions/<id>/invalidate";
    private final static String CATEGORIES = "categories";
    private final static String GENERAL_CATEGORIES = "categories/general";
    private final static String GENERAL_CATEGORY = "categories/general/<id>";
    private final static String SUB_CATEGORIES = "categories/sub";
    private final static String SUB_CATEGORY = "categories/sub/<id>";
    private final static String TRANSACTIONS = "transactions";
    private final static String TRANSACTION = "transactions/<id>";

    private final static DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(20000,
            0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private RequestBuilder() {}

    public static JsonObjectRequest postVisitorToken(int index, final RequestCoordinator coordinator,
                                                     final String recaptchaToken) {
        String url = BASE_URL + VISITOR_TOKENS;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
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

    public static JsonObjectRequest headEmail(int index, final RequestCoordinator coordinator,
                                              final String accessToken, final String email) throws UnsupportedEncodingException {
        String url = BASE_URL + EMAIL_SEARCH;
        url = url.replace("<email>", URLEncoder.encode(email, "UTF-8"));

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.HEAD, url, null,
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
                // Success response returns 200 - no content
                return Response.success(null, HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        request.setRetryPolicy(retryPolicy);
        return request;
    }

    public static JsonObjectRequest postAccount(int index, final RequestCoordinator coordinator,
                                                String json, final String accessToken) throws JSONException {
        String url = BASE_URL + USERS;
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

    public static JsonObjectRequest postVerificationToken(int index, final RequestCoordinator coordinator,
                                                          final String accessToken, String json) throws JSONException {
        String url = BASE_URL + VERIFICATION_TOKENS;
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

    public static JsonObjectRequest postSession(int index, final RequestCoordinator coordinator,
                                                String json) throws JSONException {
        String url = BASE_URL + SESSIONS;
        JSONObject object = new JSONObject(json);
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

    public static JsonObjectRequest putSession(int index, final RequestCoordinator coordinator,
                                               String sessionId, final String accessToken, String json) throws JSONException {
        String url = BASE_URL + SESSION;
        url = url.replace("<id>", sessionId);
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

    public static JsonObjectRequest postSessionRefresh(int index, final RequestCoordinator coordinator,
                                                       String sessionId, final String refreshToken) {
        String url = BASE_URL + SESSION_REFRESH;
        url = url.replace("<id>", sessionId);
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

    public static JsonObjectRequest postSessionAuthentication(int index, final RequestCoordinator coordinator,
                                                              String sessionId, String json) throws JSONException {
        String url = BASE_URL + SESSION_AUTHENTICATION;
        url = url.replace("<id>", sessionId);
        JSONObject object = new JSONObject(json);
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

    public static JsonObjectRequest postSessionInvalidation(int index, final RequestCoordinator coordinator,
                                                            String sessionId, final String accessToken) {
        String url = BASE_URL + SESSION_INVALIDATION;
        url = url.replace("<id>", sessionId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
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

    public static JsonObjectRequest putEmail(int index, final RequestCoordinator coordinator,
                                             final String accessToken, JSONObject json) {
        String url = BASE_URL + EMAIL;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, json,
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
        String url = BASE_URL + KEY;
        url = url.replace("<id>", userId);
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
        String url = BASE_URL + KEY;
        url = url.replace("<id>", userId);
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
        url = url.replace("<id>", userId);
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
                                                         String json, final String accessToken) throws JSONException {
        String url = BASE_URL + CATEGORIES;
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
                                                   final String accessToken) {
        String url = BASE_URL + TRANSACTIONS;
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
                                                        final String accessToken) {
        String url = BASE_URL + GENERAL_CATEGORIES;
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
                                                    final String accessToken) {
        String url = BASE_URL + SUB_CATEGORIES;
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
                                                    String json, final String accessToken) throws JSONException {
        String url = BASE_URL + TRANSACTIONS;
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
                                                   String transactionId, String json,
                                                   final String accessToken) throws JSONException {
        String url = BASE_URL + TRANSACTION;
        url = url.replace("<id>", transactionId);
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
                                                      String transactionId, final String accessToken) {
        String url = BASE_URL + TRANSACTION;
        url = url.replace("<id>", transactionId);
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
                                                        String json, final String accessToken) throws JSONException {
        String url = BASE_URL + GENERAL_CATEGORIES;
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
                                                       String categoryId, String json,
                                                       final String accessToken) throws JSONException {
        String url = BASE_URL + GENERAL_CATEGORY;
        url = url.replace("<id>", categoryId);
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
                                                          String categoryId, final String accessToken) {
        String url = BASE_URL + GENERAL_CATEGORY;
        url = url.replace("<id>", categoryId);
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
                                                    String json, final String accessToken) throws JSONException {
        String url = BASE_URL + SUB_CATEGORIES;
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
                                                   String categoryId, String json,
                                                   final String accessToken) throws JSONException {
        String url = BASE_URL + SUB_CATEGORY;
        url = url.replace("<id>", categoryId);
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
                                                      String categoryId, final String accessToken) {
        String url = BASE_URL + SUB_CATEGORY;
        url = url.replace("<id>", categoryId);
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
                case 410:
                    return ApiService.GONE;
                case 418:
                    return ApiService.IM_A_TEAPOT;
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
        return error -> {
            error.printStackTrace();
            coordinator.abort(parseVolleyException(error));
        };
    }

    private static Response.Listener<JSONObject> createJsonObjectResponseListener(
            final int index, final RequestCoordinator coordinator) {
        return response -> coordinator.done(index, response);
    }

    private static Response.Listener<JSONArray> createJsonArrayResponseListener(
            final int index, final RequestCoordinator coordinator) {
        return response -> coordinator.done(index, response);
    }

}