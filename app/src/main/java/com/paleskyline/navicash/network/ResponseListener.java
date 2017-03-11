package com.paleskyline.navicash.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by oscar on 11/03/17.
 */

public class ResponseListener extends Response.Listener<JSONObject> {

    private int index;
    private RequestCoordinator coordinator;

    public ResponseListener(int index, RequestCoordinator coordinator) {
        this.index = index;
        this.coordinator = coordinator;
    }

    @Override
    public void onResponse(JSONObject response) {
        coordinator.receiveData(index, response);
    }

}
