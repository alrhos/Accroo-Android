package io.accroo.android.network;

import android.content.Context;
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

    public RequestCoordinator(Context context, Object tag, JSONObject[] dataReceiver) {
        this.context = context;
        this.tag = tag;
        this.dataReceiver = dataReceiver;
        requests = new ArrayList<>();
    }

    public void addRequests(RestRequest... requests) throws Exception {
        for (RestRequest request : requests) {
            request.setTag(tag);
            this.requests.add(request);
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

    protected void abort(int errorCode) {
        RequestDispatcher.getInstance(context).flushRequests(tag);
        onFailure(errorCode);
    }

    protected abstract void onSuccess();

    protected abstract void onFailure(int errorCode);

}
