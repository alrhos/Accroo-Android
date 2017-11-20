package com.paleskyline.accroo.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.paleskyline.accroo.crypto.AuthManager;
import com.paleskyline.accroo.services.ApiService;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by oscar on 11/03/17.
 */

public class RequestBuilder {

    private final static String baseURL = "http://apidev.accroo.io:/";

    public final static String REFRESH_TOKEN = "token/refresh";
    public final static String ACCESS_TOKEN = "token/access";
    public final static String REGISTER = "register";
    public final static String EMAIL = "email";
    public final static String LOGIN_PASSWORD = "password";
    public final static String FORGOT_PASSWORD = "password/forgot";
    public final static String DATA_PASSWORD = "key";
    public final static String CATEGORY = "category";
    public final static String GENERAL_CATEGORY = "category/general";
    public final static String SUB_CATEGORY = "category/sub";
    public final static String TRANSACTION = "transaction";

    protected final static int BASIC_AUTH = 0;
    protected final static int REFRESH_TOKEN_AUTH = 1;
    protected final static int ACCESS_TOKEN_AUTH = 2;
    protected final static int NO_AUTH = 3;

    private RequestBuilder() {}

    protected static void updateRequestAccessToken(RestRequest restRequest, Context context) throws Exception {
        String accessToken = AuthManager.getInstance(context).getEntry(AuthManager.ACCESS_TOKEN_KEY);
        restRequest.setAuthHeader(accessToken);
    }

    public static RestRequest basicAuth(int index, final RequestCoordinator coordinator,
                                        int method, JSONObject json, String endpoint,
                                        String username, char[] password, Context context) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator, BASIC_AUTH);

        String credentials = username + ":" + String.copyValueOf(password);
        String authValue = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        String url = baseURL + endpoint;

        // TODO: password security

        return new RestRequest(method, url, json, responseListener,
                errorListener, RestRequest.BASIC, authValue, context);
    }

    protected static RestRequest getAccessToken(final RequestCoordinator coordinator,
                                                final Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String accessToken = response.getString("token");
                    coordinator.receiveAccessToken(accessToken);
                } catch (Exception e) {
                    coordinator.abort(ApiService.GENERIC_ERROR);
                }
            }
        };

        Response.ErrorListener errorListener = createErrorListener(coordinator, REFRESH_TOKEN_AUTH);

        String authValue = AuthManager.getInstance(context).getEntry(AuthManager.REFRESH_TOKEN_KEY);

        String url = baseURL + ACCESS_TOKEN;

        return new RestRequest(Request.Method.POST, url, null, responseListener,
                errorListener, RestRequest.TOKEN, authValue, context);
    }


//    public static RestRequest deleteRefreshToken(int index, RequestCoordinator coordinator,
//                                                 Context context) throws Exception {
//
//
//        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
//        Response.ErrorListener errorListener = createErrorListener(coordinator, REFRESH_TOKEN_AUTH);
//
//        String authValue = AuthManager.getInstance(context).getEntry(AuthManager.REFRESH_TOKEN_KEY);
//
//        String url = baseURL + REFRESH_TOKEN;
//
//        return new RestRequest(Request.Method.DELETE, url, null, responseListener,
//                errorListener, RestRequest.TOKEN, authValue, context);
//
//    }


    public static RestRequest accessTokenAuth(int index, RequestCoordinator coordinator, int method,
                                              String endpoint, String parameters, JSONObject json,
                                              Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator, ACCESS_TOKEN_AUTH);

        String url = baseURL + endpoint;

        if (parameters != null) {
            url += parameters;
        }

        String authValue = AuthManager.getInstance(context).getEntry(AuthManager.ACCESS_TOKEN_KEY);

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.TOKEN, authValue, context);
    }


    public static RestRequest noAuth(int index, RequestCoordinator coordinator, int method,
                                        String endpoint, JSONObject json,
                                        Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator, NO_AUTH);

        String url = baseURL + endpoint;

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.NONE, null, context);
    }


    private static int getResponseCode(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            return response.statusCode;
        }
        return 0;
    }


    // TODO: see if code can be cleaned up
//    private static String parseVolleyExceptionOld(VolleyError error) {
//        error.printStackTrace();
//        if (error instanceof AuthFailureError) {
//            return RestRequest.UNAUTHORIZED;
//        } else if (error instanceof TimeoutError) {
//            return RestRequest.TIMEOUT_ERROR;
//        } else if (error instanceof NoConnectionError) {
//            return RestRequest.CONNECTION_ERROR;
//        } else {
//            try {
//                NetworkResponse networkResponse = error.networkResponse;
//                String serverMessage = new String(networkResponse.data);
//                if (!serverMessage.isEmpty()) {
//                    String jsonString = new String(networkResponse.data);
//                    try {
//                        JSONObject serverResponse = new JSONObject(jsonString);
//                        return serverResponse.getString("message");
//                    } catch (JSONException e) {
//                        return RestRequest.GENERAL_ERROR;
//                    }
//                } else {
//                    return RestRequest.GENERAL_ERROR;
//                }
//            } catch (Exception e) {
//                return RestRequest.GENERAL_ERROR;
//            }
//        }
//    }

    private static int parseVolleyException(VolleyError error) {
        error.printStackTrace();
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
                case 429:
                    return ApiService.TOO_MANY_REQUESTS;
                case 500:
                    return ApiService.GENERIC_ERROR;
            }
        }
        return ApiService.GENERIC_ERROR;
    }

    private static Response.ErrorListener createErrorListener(final RequestCoordinator coordinator,
                                                              final int authType) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //int responseCode = getResponseCode(error);
                //String errorMessage = parseVolleyExceptionOld(error);
               // int errorCode = parseVolleyException(error);
                coordinator.receiveError(authType, parseVolleyException(error));
            }
        };
        return errorListener;
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

}
















//
//    protected static RestRequest getToken(final RequestCoordinator coordinator, final Context context)
//        throws Exception {
//
//        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    coordinator.updateToken(response.getString("token"));
//                } catch (Exception e) {
//                    coordinator.abort(GENERIC_ERROR);
//                }
//            }
//        };
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                coordinator.abort(parseVolleyException(error));
//            }
//        };
//
//        String url = baseURL + "user/token";
//
//        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
//                responseListener, errorListener, RestRequest.BASIC, context);
//
//        return restRequest;
//    }
//
//    public static RestRequest getKey(final RequestCoordinator coordinator, final int index,
//                                     final Context context) throws Exception {
//
//        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                try {
//                    coordinator.updateToken(response.getString("token"));
//                    coordinator.done(index, response);
//                } catch (Exception e) {
//                    coordinator.abort(GENERIC_ERROR);
//                }
//            }
//        };
//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                coordinator.abort(parseVolleyException(error));
//            }
//        };
//
//        String url = baseURL + "user/key";
//
//        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
//                responseListener, errorListener, RestRequest.BASIC, context);
//
//        return restRequest;
//    }
//
//    public static RestRequest get(final int index, final String endpoint, String parameter,
//                                   final RequestCoordinator coordinator, String authType,
//                                   Context context) throws Exception {
//
//        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
//        Response.ErrorListener errorListener = createErrorListener(coordinator);
//        String url = baseURL + endpoint;
//        if (parameter != null) {
//            url += parameter;
//        }
//        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null, responseListener,
//                errorListener, authType, context);
//
//        return restRequest;
//
//    }
//
//    public static RestRequest post(final int index, final String endpoint,
//                                   final RequestCoordinator coordinator,
//                                   JSONObject json, String authType, Context context) throws Exception {
//
//        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
//        Response.ErrorListener errorListener = createErrorListener(coordinator);
//        String url = baseURL + endpoint;
//        RestRequest restRequest = new RestRequest(Request.Method.POST, url, json, responseListener,
//                errorListener, authType, context);
//
//        return restRequest;
//
//    }
//

















    // TODO: this method needs review - sometimes it causes null object references
//    private static JSONObject parseVolleyExceptionOld(VolleyError error) {
//        JSONObject json = null;
//        NetworkResponse response = error.networkResponse;
//        if (error instanceof ServerError && response != null) {
//            try {
//                String responseString = new String(response.data,
//                        HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
//                json = new JSONObject(responseString);
//            } catch (JSONException | UnsupportedEncodingException e) {
//                // TODO: error handling
//                e.printStackTrace();
//            }
//        } else if ((error instanceof TimeoutError) || (error instanceof NoConnectionError)) {
//
//        } else {
//
//        }
//        return json;
//    }
