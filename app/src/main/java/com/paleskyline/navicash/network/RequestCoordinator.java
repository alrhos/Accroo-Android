package com.paleskyline.navicash.network;

import android.content.Context;
import android.util.Base64;

import com.paleskyline.navicash.crypto.AuthManager;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by oscar on 11/03/17.
 */

public abstract class RequestCoordinator {

    private int doneCount = 0;
    private Context context;
    private Object tag;
    private ArrayList<RestRequest> requests;
    private JSONObject[] dataReceiver;

    private ArrayList<RestRequest> retryRequests;

    public RequestCoordinator(Context context, Object tag, JSONObject[] dataReceiver) {
        this.context = context;
        this.tag = tag;
        this.dataReceiver = dataReceiver;
        requests = new ArrayList<>();
        retryRequests = new ArrayList<>();
    }

    protected Object getTag() {
        return tag;
    }

    public void addRequests(RestRequest... requests) {
        for (RestRequest request : requests) {
            try {
                request.setTag(tag);
                setAuthHeader(request);
                this.requests.add(request);

                // TODO: this needs to be changed. Cloning the request here will mean that the retry
                // requests will have the same auth headers so they will fail if the token is expired
                // because the token value will not be updated.

                retryRequests.add((RestRequest) request.clone());
            } catch (Exception e) {
                // TODO: review exception handling here
                e.printStackTrace();
            }
        }
    }

    private void setAuthHeader(RestRequest request) throws Exception {
        switch(request.getAuthType()) {
            case RestRequest.BASIC:
                String username = AuthManager.getInstance(context).getEntry(AuthManager.USERNAME_KEY);
                // TODO: review password security
                String password = AuthManager.getInstance(context).getEntry(AuthManager.PASSWORD_KEY);
                String credentials = username + ":" + password;
                request.setHeader(Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP));
                break;
            case RestRequest.TOKEN:
                String token = AuthManager.getInstance(context).getEntry(AuthManager.TOKEN_KEY);
                request.setHeader(token);
                break;
            case RestRequest.NONE:
                // TODO: review how headers without auth are handled
                request.setHeader(RestRequest.NONE);
        }
    }

    public void start() {
        if (!requests.isEmpty()) {
            for (RestRequest request : requests) {
                VolleyManager.getInstance(context).addRequest(request);
            }
        }
    }

    protected synchronized void done(int index, JSONObject data) {
        dataReceiver[index] = data;
        doneCount++;
        if (doneCount == requests.size()) {
            onSuccess();
        }
    }

    protected synchronized void tokenRefresh(String token) {
        try {
            AuthManager.getInstance(context).saveEntry(AuthManager.TOKEN_KEY, token);
        } catch (Exception e) {
            // TODO: error handling
            e.printStackTrace();
        }
    }

    protected synchronized void retry() {
        doneCount = 0;
        // TODO: could iterate through the requestQueue, clone each request, set the authHeader then add it to the volley queue
        // WE MIGHT NOT EVEN NEED THE RETRYREQUESTS ARRAY LIST IF WE'RE GRABBING EVERYTHING FROM THE ORIGINAL QUEUE
        if (!retryRequests.isEmpty()) {
            for (RestRequest request : retryRequests) {
                VolleyManager.getInstance(context).addRequest(request);
            }
        }
        // TODO: consider an else statement to call onSuccess() if there are no retry requests.
    }

    protected synchronized void receiveError(int responseCode, JSONObject json) {
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // Remove all requests from the request queue
            VolleyManager.getInstance(context).flushRequests(tag);
            // Create a request for a new token
            VolleyManager.getInstance(context).addRequest(RestMethods.getToken(this));
        } else {
            // Remove any existing requests from the queue
            VolleyManager.getInstance(context).flushRequests(tag);
            onFailure(json);
        }
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(JSONObject json);

}
