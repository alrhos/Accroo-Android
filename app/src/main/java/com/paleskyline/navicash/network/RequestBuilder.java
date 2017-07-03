package com.paleskyline.navicash.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.paleskyline.navicash.crypto.AuthManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.paleskyline.navicash.network.RestRequest.GENERAL_ERROR;


/**
 * Created by oscar on 11/03/17.
 */

public class RequestBuilder {

    private final static String baseURL = "http://192.168.1.15:5000/";
    public final static String REFRESH_TOKEN = "token/refresh";
    private final static String ACCESS_TOKEN = "token/access";
    public final static String REGISTER = "user/register";
    public final static String CATEGORY = "category";
    public final static String GENERAL_CATEGORY = "category/general";
    public final static String GENERAL_CATEGORY_BULK = "category/general/bulk";
    public final static String SUB_CATEGORY = "category/sub";
    public final static String SUB_CATEGORY_BULK = "category/sub/bulk";
    public final static String TRANSACTION = "transaction";
    public final static String TRANSACTION_GET = "transaction?transactionid=";

    private RequestBuilder() {}

    protected void updateRequestHeader(RestRequest restRequest) {
        if (restRequest.getAuthType().equals(RestRequest.ACCESS_TOKEN)) {
            // Update header here
        }
    }

    public static RestRequest basicAuth(int index, final RequestCoordinator coordinator,
                                        int method, String endpoint, String username,
                                           char[] password, Context context) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createAbortingErrorListener(coordinator);

        String credentials = username + ":" + String.copyValueOf(password);
        String authValue = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        String url = baseURL + endpoint;

        // TODO: password security

        return new RestRequest(method, url, null, responseListener, errorListener,
                RestRequest.BASIC, authValue, context);
    }

    protected static RestRequest getAccessToken(final RequestCoordinator coordinator,
                                                final Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String accessToken = response.getString("accessToken");
                    coordinator.receiveAccessToken(accessToken);
                } catch (Exception e) {
                    coordinator.abort(GENERAL_ERROR);
                }
            }
        };

        Response.ErrorListener errorListener = createAbortingErrorListener(coordinator);

        String authValue = AuthManager.getInstance(context).getEntry(AuthManager.REFRESH_TOKEN_KEY);

        String url = baseURL + ACCESS_TOKEN;

        return new RestRequest(Request.Method.GET, url, null, responseListener,
                errorListener, RestRequest.REFRESH_TOKEN, authValue, context);
    }


    public static RestRequest tokenAuth(int index, RequestCoordinator coordinator, int method,
                                        String endpoint, String parameters, JSONObject json,
                                        Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;

        if (parameters != null) {
            url += parameters;
        }

        String authValue = AuthManager.getInstance(context).getEntry(AuthManager.ACCESS_TOKEN_KEY);

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.ACCESS_TOKEN, authValue, context);
    }


    public static RestRequest noAuth(int index, RequestCoordinator coordinator, int method,
                                        String endpoint, JSONObject json,
                                        Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.NONE, null, context);
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
//                    coordinator.abort(GENERAL_ERROR);
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
//                    coordinator.abort(GENERAL_ERROR);
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
















    private static int getResponseCode(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            return response.statusCode;
        }
        return 0;
    }

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

    // TODO: see if code can be cleaned up
    private static String parseVolleyException(VolleyError error) {
        try {
            NetworkResponse networkResponse = error.networkResponse;
            String serverMessage = new String(networkResponse.data);
            if (!serverMessage.isEmpty()) {
                String jsonString = new String(networkResponse.data);
                try {
                    JSONObject serverResponse = new JSONObject(jsonString);
                    return serverResponse.getString("message");
                } catch (JSONException e) {
                    if (error instanceof TimeoutError) {
                        return RestRequest.TIMEOUT_ERROR;
                    } else if (error instanceof NoConnectionError) {
                        return RestRequest.CONNECTION_ERROR;
                    } else {
                        return GENERAL_ERROR;
                    }
                }
            } else {
                return GENERAL_ERROR;
            }
        } catch (Exception e) {
            return RestRequest.CONNECTION_ERROR;
        }

    }

    private static Response.ErrorListener createAbortingErrorListener(final RequestCoordinator coordinator) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                coordinator.abort(parseVolleyException(error));
            }
        };
        return errorListener;
    }

    private static Response.Listener<JSONObject> createResponseListener(
            final int index, final RequestCoordinator coordinator) {
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
                System.out.println(error.toString());
                int responseCode = getResponseCode(error);
                String errorMessage = parseVolleyException(error);
                coordinator.receiveError(responseCode, errorMessage);
            }
        };
        return errorListener;
    }

}
