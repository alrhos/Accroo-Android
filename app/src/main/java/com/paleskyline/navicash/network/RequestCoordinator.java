package com.paleskyline.navicash.network;

import android.content.Context;

import com.paleskyline.navicash.crypto.AuthManager;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

/**
 * Created by oscar on 11/03/17.
 */

public abstract class RequestCoordinator {

    private int doneCount = 0;
    private boolean retryRequired = false;
    private Context context;
    private Object tag;
    private ArrayList<RestRequest> requests;
    private JSONObject[] dataReceiver;
    private static final String ERROR = "An error occurred";

    private ArrayList<RestRequest> retryRequests;

    public RequestCoordinator(Context context, Object tag, JSONObject[] dataReceiver) {
        this.context = context;
        this.tag = tag;
        this.dataReceiver = dataReceiver;
        requests = new ArrayList<>();
        retryRequests = new ArrayList<>();
    }

    public void addRequests(RestRequest... requests) throws Exception {
        for (RestRequest request : requests) {
            request.setTag(tag);
            this.requests.add(request);
            retryRequests.add((RestRequest) request.clone());
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

    protected void updateToken(String token) {
        try {
            AuthManager.getInstance(context).saveEntry(AuthManager.TOKEN_KEY, token);
            if (retryRequired) {
                retry();
            }
        } catch (Exception e) {
            abort(ERROR);
        }
    }

    private synchronized void retry() {
        doneCount = 0;
        for (RestRequest request : retryRequests) {
            try {
                request.setAuthHeader();
                VolleyManager.getInstance(context).addRequest(request);
            } catch (Exception e) {
                abort(ERROR);
            }
        }
    }

    protected void abort(String errorMessage) {
        VolleyManager.getInstance(context).flushRequests(tag);
        onFailure(errorMessage);
    }

    protected synchronized void receiveError(int responseCode, String errorMessage) {
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            retryRequired = true;
            VolleyManager.getInstance(context).flushRequests(tag);
            try {
                VolleyManager.getInstance(context).addRequest(RestMethods.getToken(this, context));
            } catch (Exception e) {
                abort(ERROR);
            }
        } else {
            abort(errorMessage);
        }
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(String errorMessage);

}
