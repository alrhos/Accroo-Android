package io.accroo.android.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

public class GsonUtil {
    private static GsonUtil instance = null;
    private Gson gson;

    private GsonUtil() {
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public static GsonUtil getInstance() {
        if (instance == null) {
            instance = new GsonUtil();
        }
        return instance;
    }

    public JSONObject toJson(Object object) {
        try {
            return new JSONObject(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object fromJson(JSONObject json, Class classType) {
        return gson.fromJson(json.toString(), classType);
    }

}