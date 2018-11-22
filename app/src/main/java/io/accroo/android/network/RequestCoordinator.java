package io.accroo.android.network;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.annotations.Expose;

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

    public void addRequests(JsonRequest... requests) {
        for (JsonRequest request : requests) {
            request.setTag(tag);
            this.requests.add(request);
        }
    }

    public void start() {
        if (!requests.isEmpty()) {
            for (JsonRequest request : requests) {
                RequestDispatcher.getInstance(context).addRequest(request);
            }
        }
    }

    protected synchronized void done(int index, JSONObject data) {
        dataReceiver[index] = data == null ? null : data.toString();
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
