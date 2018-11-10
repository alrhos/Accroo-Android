package io.accroo.android.network;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import io.accroo.android.model.AccessToken;
import io.accroo.android.other.GsonUtil;
import io.accroo.android.services.ApiService;
import io.accroo.android.services.CredentialService;

/**
 * Created by oscar on 11/03/17.
 */

public abstract class RequestCoordinator {

    private int doneCount = 0;
    private Context context;
    private Object tag;
    private ArrayList<JsonRequest> requests;
    private String[] dataReceiver;

    public RequestCoordinator(Context context, Object tag, String[] dataReceiver) {
        this.context = context;
        this.tag = tag;
        this.dataReceiver = dataReceiver;
        requests = new ArrayList<>();
    }

    public void addRequests(JsonRequest... requests) throws Exception {
        for (JsonRequest request : requests) {
            request.setTag(tag);
            this.requests.add(request);
        }
    }

//    public void verifyTokenValidity() throws Exception {
//        // Check if the current access token is valid (with a fairly generous buffer)
//        // and request a new one if it's expired or close to expiring.
//        String tokenExpiry = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY);
//        DateTime tokenExpiryTime = new DateTime(tokenExpiry);
//        System.out.println(tokenExpiryTime.toDateTime());
//        System.out.println(tokenExpiryTime.toLocalDateTime());
//        DateTime currentTime = new DateTime();
//        System.out.println("CURRENT TIME: " + currentTime.toLocalDateTime());
//        System.out.println("DIFFERENCE IS: " + Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds());
//        // Will the token expire in <= 60 seconds?
//        if (Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds() <= 60) {
//            System.out.println("DIFFERENCE IS: " + Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds());
//            System.out.println("TIME TO GRAB A NEW TOKEN");
//        }
//    }

    private boolean getNewAccessToken() {
        try {
            // Check if a refresh token exists
            CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
            String tokenExpiry = CredentialService.getInstance(context)
                    .getEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY);
            DateTime tokenExpiryTime = new DateTime(tokenExpiry);
            DateTime currentTime = new DateTime();
            return Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds() <= 60;
        } catch (Exception e) {
            // A refresh token wasn't found or the access token expiry couldn't be retrieved
            return false;
        }
    }

    protected void submitRequests() {
        for (JsonRequest request : requests) {
            RequestDispatcher.getInstance(context).addRequest(request);
        }
    }

    public void start() {
        if (!requests.isEmpty()) {
            if (getNewAccessToken()) {
                try {
                    String refreshToken = CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
                    JsonObjectRequest accessTokenRequest = RequestBuilder.postAccessToken(this, refreshToken);
                    RequestDispatcher.getInstance(context).addRequest(accessTokenRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    abort(ApiService.GENERIC_ERROR);
                }
            } else {
                submitRequests();
            }
        }
    }

    protected void updateAccessToken(JSONObject json) {
        AccessToken token = GsonUtil.getInstance().fromJson(json.toString(), AccessToken.class);
        DateTime tokenExpiry = new DateTime(token.getExpiresAt());
        // TODO: add proper exception handling
        try {
            CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, token.getToken());
            CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, tokenExpiry.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected synchronized void done(int index, JSONObject data) {
        dataReceiver[index] = data.toString();
        doneCount++;
        if (doneCount == requests.size()) {
            onSuccess();
        }
    }

    protected synchronized void done(int index, JSONArray data) {
        dataReceiver[index] = data.toString();
        doneCount++;
        if (doneCount == requests.size()) {
            onSuccess();
        }
    }

    protected void abort(int errorCode) {
        RequestDispatcher.getInstance(context).flushRequests(tag);
        onFailure(errorCode);
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(int errorCode);

}
