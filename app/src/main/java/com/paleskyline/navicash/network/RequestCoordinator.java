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

    // TODO: add functionality to check token expiry and request a new access token if necessary

    public void start() {
        if (!requests.isEmpty()) {
            for (RestRequest request : requests) {
                RequestDispatcher.getInstance(context).addRequest(request);
            }
        }
    }

    // TODO: this will contain the contents of start

    private void dispatchRequests() {

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
        doneCount = 0;
        for (RestRequest request : retryRequests) {
            try {
                RequestBuilder.updateRequestAccessToken(request, context);
                RequestDispatcher.getInstance(context).addRequest(request);
            } catch (Exception e) {
                abort(RestRequest.GENERAL_ERROR);
            }
        }
    }

    // TODO: review if the retryRequired variable is needed

    // TODO: review error message handling here - looks like the RequestBuilder already parses the message so should be fine to just take what is passed back here.

    protected synchronized void receiveError(int authType, int responseCode, String errorMessage) {
        if (authType == RequestBuilder.ACCESS_TOKEN_AUTH && responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            retryRequired = true;
            RequestDispatcher.getInstance(context).flushRequests(tag);
            try {
                RequestDispatcher.getInstance(context).addRequest(RequestBuilder.getAccessToken(this, context));
            } catch (Exception e) {
                abort(RestRequest.GENERAL_ERROR);
            }
        } else {
            System.out.println("ABORTING!!");
            abort(RestRequest.GENERAL_ERROR);
        }
    }


//        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
//            retryRequired = true;
//            RequestDispatcher.getInstance(context).flushRequests(tag);
//            try {
//                RequestDispatcher.getInstance(context).addRequest(RequestBuilder.getAccessToken(this, context));
//            } catch (Exception e) {
//                abort(RestRequest.GENERAL_ERROR);
//            }
//        } else {
//            abort(errorMessage);
//        }

    protected void abort(String errorMessage) {
        RequestDispatcher.getInstance(context).flushRequests(tag);
        onFailure(errorMessage);
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(String errorMessage);

}
