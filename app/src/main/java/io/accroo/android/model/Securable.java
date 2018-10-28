package io.accroo.android.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public interface Securable {
    Object encrypt() throws JSONException;
    Object decrypt(JSONObject json, Class className) throws JSONException, UnsupportedEncodingException;
}
