package com.paleskyline.accroo.network;

import android.content.Context;

import com.paleskyline.accroo.crypto.AuthManager;
import com.paleskyline.accroo.services.ApiService;

import org.json.JSONObject;

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

    protected void receiveAccessToken(String accessToken) {
        try {
            AuthManager.getInstance(context).saveEntry(AuthManager.ACCESS_TOKEN_KEY, accessToken);
            retry();
        } catch (Exception e) {
            abort(ApiService.GENERIC_ERROR);
        }
    }

    protected synchronized void retry() {
        doneCount = 0;
        for (RestRequest request : retryRequests) {
            if (request.getAuthType().equals(RestRequest.TOKEN)) {
                try {
                    RequestBuilder.updateRequestAccessToken(request, context);
                    RequestDispatcher.getInstance(context).addRequest(request);
                } catch (Exception e) {
                    abort(ApiService.GENERIC_ERROR);
                }
            }
        }
    }

    protected synchronized void receiveError(int authType, int errorCode) {
        if (authType == RequestBuilder.ACCESS_TOKEN_AUTH && errorCode == ApiService.UNAUTHORIZED) {
            RequestDispatcher.getInstance(context).flushRequests(tag);
            try {
                RequestDispatcher.getInstance(context).addRequest(RequestBuilder.getAccessToken(this, context));
            } catch (Exception e) {
                abort(ApiService.GENERIC_ERROR);
            }
        } else {
            abort(errorCode);
        }
    }

    protected void abort(int errorCode) {
        RequestDispatcher.getInstance(context).flushRequests(tag);
        onFailure(errorCode);
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(int errorCode);

}
