package com.paleskyline.navicash.network;

import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Created by oscar on 11/03/17.
 */

public abstract class RequestCoordinator {

    private int doneCount = 0;
    private ArrayList<RestRequest> requests;
    //private T[] requestResults;
    private JSONObject[] requestResults;

    public RequestCoordinator() {
        requests = new ArrayList<>();
    }

    public void addRequests(RestRequest... requests) {
        for (RestRequest request : requests) {
            this.requests.add(request);
        }
        requestResults = new JSONObject[this.requests.size()];


    }

    public void start(Context context) {
        if (!requests.isEmpty()) {
            for (RestRequest request : requests) {
                VolleyManager.getInstance(context).addRequest(request);
            }
        }
    }

    protected synchronized void receiveData(int index, JSONObject data) {
        requestResults[index] = data;
    }

    protected synchronized void done() {
        doneCount++;
        if (doneCount == requests.size()) {
            onSuccess();
        }
    }

    public JSONObject getData(int index) {
        if (index >= 0 && index < requestResults.length) {
            return requestResults[index];
        }
        return null;
    }

    public JSONObject[] returnRequestResults() {
        return requestResults;
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(T data);
}
