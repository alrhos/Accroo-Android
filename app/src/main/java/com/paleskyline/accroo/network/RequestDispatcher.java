package com.paleskyline.accroo.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by oscar on 11/03/17.
 */

public class RequestDispatcher {

    private RequestQueue requestQueue;
    private static RequestDispatcher instance = null;

    private RequestDispatcher(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public static synchronized RequestDispatcher getInstance(Context context) {
        if (instance == null) {
            instance = new RequestDispatcher(context);
        }
        return instance;
    }

    public void addRequest(RestRequest restRequest) {
        if (requestQueue != null) {
            requestQueue.add(restRequest);
        }
    }

    public void flushRequests(Object tag) {
        requestQueue.cancelAll(tag);
    }

}
