package io.accroo.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by oscar on 15/03/17.
 */

public interface Securable {
    JSONObject encrypt() throws JSONException;
    void decrypt(JSONObject json) throws JSONException, UnsupportedEncodingException;
}
