package com.paleskyline.navicash.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by oscar on 11/03/17.
 */

public class VolleyManager {

    private RequestQueue requestQueue;
    private static VolleyManager instance = null;
    private static Context context;

    private VolleyManager(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized VolleyManager getInstance(Context context) {
        if (instance == null) {
            instance = new VolleyManager(context);
        }
        return instance;
    }

    public void addRequest(RestRequest restRequest) {
        if (requestQueue != null) {
            requestQueue.add(restRequest);
        }
    }

    public void flushRequests(String tag) {
        requestQueue.cancelAll(tag);
    }



}
