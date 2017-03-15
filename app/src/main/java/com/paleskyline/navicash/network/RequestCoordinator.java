package com.paleskyline.navicash.network;

import android.content.Context;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by oscar on 11/03/17.
 */

public abstract class RequestCoordinator {

    private int doneCount = 0;
    private Context context;
    private String tag;
    private ArrayList<RestRequest> requests;
    private JSONObject[] requestResults;


    private ArrayList<RestRequest> retryRequests;

    public RequestCoordinator(Context context, String tag) {
        this.context = context;
        this.tag = tag;
        requests = new ArrayList<>();
        retryRequests = new ArrayList<>();
    }

    protected String getTag() {
        return tag;
    }

    public void addRequests(RestRequest... requests) {
        for (RestRequest request : requests) {
            this.requests.add(request);
            try {
                retryRequests.add((RestRequest) request.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

        }
        requestResults = new JSONObject[this.requests.size()];
    }

    public void start() {
        if (!requests.isEmpty()) {
            for (RestRequest request : requests) {
                VolleyManager.getInstance(context).addRequest(request);
            }
        }
    }

    protected synchronized void done(int index, JSONObject data) {
        requestResults[index] = data;
        doneCount++;
        if (doneCount == requests.size()) {
            onSuccess();
        }
    }

    protected synchronized void tokenRefresh() {
        doneCount = 0;
        retry();
    }

    protected void retry() {
        if (!retryRequests.isEmpty()) {
            for (RestRequest request : retryRequests) {
                VolleyManager.getInstance(context).addRequest(request);
            }
        }
    }

    protected synchronized void receiveError(int responseCode, JSONObject json) {
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // Remove all requests from the request queue
            VolleyManager.getInstance(context).flushRequests(tag);
            // Create a request for a new token
            RestRequest tokenRequest = RestMethods.getToken(this);
            VolleyManager.getInstance(context).addRequest(tokenRequest);
        } else {
            // Remove any existing requests from the queue
            VolleyManager.getInstance(context).flushRequests(tag);
            onFailure(json);
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

    protected abstract void onFailure(JSONObject json);

}
