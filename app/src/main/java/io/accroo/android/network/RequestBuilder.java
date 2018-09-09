package io.accroo.android.network;

import android.content.Context;
import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import io.accroo.android.services.CredentialService;
import io.accroo.android.services.ApiService;

import org.json.JSONObject;


/**
 * Created by oscar on 11/03/17.
 */

public class RequestBuilder {

    private final static String baseURL =           "https://api.accroo.io/";
    public final static String VERIFICATION_TOKEN = "token/verification";
    public final static String DEVICE_TOKEN =       "token/device";
    public final static String REGISTER =           "register";
    public final static String EMAIL =              "email";
    public final static String ENCRYPTION_KEY =     "key";
    public final static String CATEGORY =           "category";
    public final static String GENERAL_CATEGORY =   "category/general";
    public final static String SUB_CATEGORY =       "category/sub";
    public final static String TRANSACTION =        "transaction";

    private RequestBuilder() {}

    public static RestRequest basicAuth(int index, final RequestCoordinator coordinator,
                                        int method, JSONObject json, String endpoint,
                                        String username, String password) {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String credentials = username + ":" + password;
        String authValue = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        String url = baseURL + endpoint;

        return new RestRequest(method, url, json, responseListener,
                errorListener, RestRequest.BASIC, authValue);
    }

    public static RestRequest deviceTokenAuth(int index, RequestCoordinator coordinator, int method,
                                              String endpoint, JSONObject json, Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;
        String authValue = CredentialService.getInstance(context).getEntry(CredentialService.DEVICE_TOKEN_KEY);

        return new RestRequest(method, url, json, responseListener, errorListener,
                RestRequest.TOKEN, authValue);
    }

    public static RestRequest noAuth(int index, RequestCoordinator coordinator, int method,
                                        String endpoint, JSONObject json) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator);

        String url = baseURL + endpoint;

        return new RestRequest(method, url, json, responseListener, errorListener,
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
                    return ApiService.CONNECTION_ERROR;
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
                coordinator.abort(parseVolleyException(error));
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