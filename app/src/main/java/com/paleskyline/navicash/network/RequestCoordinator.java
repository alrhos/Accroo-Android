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
                RequestDispatcher.getInstance(context).addRequest(request);
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

    // TODO: modify this method to not use AuthManager to save tokens

    protected void updateToken(String token) {
        try {
            AuthManager.getInstance(context).saveEntry(AuthManager.ACCESS_TOKEN_KEY, token);
            if (retryRequired) {
                retry();
            }
        } catch (Exception e) {
            abort(RestRequest.GENERAL_ERROR);
        }
    }

    protected void receiveAccessToken(String accessToken) {
        try {
            AuthManager.getInstance(context).saveEntry(AuthManager.ACCESS_TOKEN_KEY, accessToken);
            retry();
        } catch (Exception e) {
            abort(RestRequest.GENERAL_ERROR);
        }
    }

    // TODO: modify to call updateAuthHeader for all requests.

    protected synchronized void retry() {
        if (retryRequired) {
            doneCount = 0;
            for (RestRequest request : retryRequests) {
                try {
                    //request.setAuthHeader();
                    RequestDispatcher.getInstance(context).addRequest(request);
                } catch (Exception e) {
                    abort(RestRequest.GENERAL_ERROR);
                }
            }
        }
    }

    protected void abort(String errorMessage) {
        RequestDispatcher.getInstance(context).flushRequests(tag);
        onFailure(errorMessage);
    }

    // TODO: review if the retryRequired variable is needed

    protected synchronized void receiveError(int responseCode, String errorMessage) {
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            retryRequired = true;
            RequestDispatcher.getInstance(context).flushRequests(tag);
            try {
                RequestDispatcher.getInstance(context).addRequest(RequestBuilder.getAccessToken(this, context));
            } catch (Exception e) {
                abort(RestRequest.GENERAL_ERROR);
            }
        } else {
            abort(errorMessage);
        }
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(String errorMessage);

}
