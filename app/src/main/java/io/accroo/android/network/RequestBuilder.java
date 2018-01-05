package io.accroo.android.network;

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
import io.accroo.android.services.CredentialService;
import io.accroo.android.services.ApiService;

import org.json.JSONObject;


/**
 * Created by oscar on 11/03/17.
 */

public class RequestBuilder {

    private final static String baseURL = "https://apidev23.accroo.io/";
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
        String accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
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

        String authValue = CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);

        String url = baseURL + ACCESS_TOKEN;

        return new RestRequest(Request.Method.POST, url, null, responseListener,
                errorListener, RestRequest.TOKEN, authValue, context);
    }

    public static RestRequest accessTokenAuth(int index, RequestCoordinator coordinator, int method,
                                              String endpoint, String parameters, JSONObject json,
                                              Context context) throws Exception {

        Response.Listener<JSONObject> responseListener = createResponseListener(index, coordinator);
        Response.ErrorListener errorListener = createErrorListener(coordinator, ACCESS_TOKEN_AUTH);

        String url = baseURL + endpoint;

        if (parameters != null) {
            url += parameters;
        }

        String authValue = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);

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
            }
        }
        return ApiService.GENERIC_ERROR;
    }

    private static Response.ErrorListener createErrorListener(final RequestCoordinator coordinator,
                                                              final int authType) {
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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