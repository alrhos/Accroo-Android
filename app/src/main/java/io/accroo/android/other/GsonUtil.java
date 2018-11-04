package io.accroo.android.other;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GsonUtil {
    private static GsonUtil instance = null;
    private Gson gson;

    private GsonUtil() {
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .disableHtmlEscaping().create();
    }

    public static GsonUtil getInstance() {
        if (instance == null) {
            instance = new GsonUtil();
        }
        return instance;
    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public String toJson(ArrayList<Object> objects) {
        return gson.toJson(objects);
    }

    public Object fromJson(String json, Class className) {
        return gson.fromJson(json, className);
    }

}