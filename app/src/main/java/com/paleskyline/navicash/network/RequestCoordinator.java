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

    protected abstract void onSuccess();

    protected abstract void onFailure(JSONObject json);

}
