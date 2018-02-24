package io.accroo.android.network;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by oscar on 4/03/17.
 */

public class RestRequest extends JsonObjectRequest implements Cloneable {

    private String authType;
    public static final String BASIC = "Basic";
    public static final String TOKEN = "Bearer";
    public static final String NONE = "None";
    private Map<String, String> headerMap;

    protected RestRequest(int method, String url, JSONObject json, Response.Listener<JSONObject>
                          responseListener, Response.ErrorListener errorListener, String authType,
                          String authValue) {

        super(method, url, json, responseListener, errorListener);
        this.authType = authType;
        setAuthHeader(authValue);
        setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headerMap;
    }

    protected void setAuthHeader(String authValue) {
        headerMap = new HashMap<>();
        if (authType != NONE) {
            headerMap.put("Authorization", authType + " " + authValue);
        }
    }

}