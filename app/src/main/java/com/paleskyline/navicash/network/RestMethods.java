package com.paleskyline.navicash.network;

import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 11/03/17.
 */

public class RestMethods {

    private final static String baseURL = "http://192.168.1.15:5000/";
    public final static String REGISTER = "user/register";
    public final static String GENERAL_CATEGORY = "category/general";
    public final static String GENERAL_CATEGORY_BULK = "category/general/bulk";
    public final static String SUB_CATEGORY = "category/sub";
    public final static String SUB_CATEGORY_BULK = "category/sub/bulk";
    public final static String TRANSACTION = "transaction";
    public final static String TRANSACTION_PARAM = "transaction?transactionid=";

    private final static String TIMEOUT_ERROR = "The connection timed out";
    private final static String CONNECTION_ERROR = "Connection error";
    private final static String SERVER_ERROR = "An error occurred";

    private RestMethods() {}

    protected static RestRequest getToken(final RequestCoordinator coordinator) {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    coordinator.tokenRefresh(response.getString("token"));
                    coordinator.retry();
                } catch (JSONException e) {
                    // TODO: error handling
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = parseVolleyException(error);
                coordinator.onFailure(errorMessage);
            }
        };

        String url = baseURL + "user/token";

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.BASIC);

        return restRequest;
    }

    public static RestRequest getKey(final RequestCoordinator coordinator, final int index) {

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    coordinator.tokenRefresh(response.getString("token"));
                    coordinator.done(index, response);
                } catch (JSONException e) {
                    // TODO: error handling
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = parseVolleyException(error);
                coordinator.onFailure(errorMessage);
            }
        };

        String url = baseURL + "user/key";

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.BASIC);

        return restRequest;
    }

    /*

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

    */

    public static RestRequest get(final int index, final String endpoint, String parameter,
                                   final RequestCoordinator coordinator, String authType) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);
        String url = baseURL + endpoint;
        if (parameter != null) {
            url += parameter;
        }
        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null, responseListener,
                errorListener, authType);
        return restRequest;

    }

    public static RestRequest post(final int index, final String endpoint,
                                   final RequestCoordinator coordinator,
                                   JSONObject json, String authType) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);
        String url = baseURL + endpoint;
        RestRequest restRequest = new RestRequest(Request.Method.POST, url, json, responseListener,
                errorListener, authType);
        return restRequest;

    }

    private static int getResponseCode(VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if (response != null) {
            return response.statusCode;
        }
        return 0;
    }

    // TODO: this method needs review - sometimes it causes null object references
    private static JSONObject parseVolleyExceptionOld(VolleyError error) {
        error.printStackTrace();
        JSONObject json = null;
        NetworkResponse response = error.networkResponse;
        if (error instanceof ServerError && response != null) {
            System.out.println("INSTANCE OF SERVER ERROR WITH RESPONSE");
            try {
                String responseString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
                json = new JSONObject(responseString);
            } catch (JSONException | UnsupportedEncodingException e) {
                // TODO: error handling
                e.printStackTrace();
            }
        } else if ((error instanceof TimeoutError) || (error instanceof NoConnectionError)) {

        } else {

        }
        return json;
    }

    private static String parseVolleyException(VolleyError error) {
        if (error instanceof TimeoutError) {
            return TIMEOUT_ERROR;
        } else if (error instanceof NoConnectionError) {
            return CONNECTION_ERROR;
        } else {
            return SERVER_ERROR;
        }
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
                int responseCode = getResponseCode(error);









                String errorMessage = parseVolleyException(error);
                coordinator.receiveError(responseCode, errorMessage);
            }
        };
        return errorListener;
    }

}
