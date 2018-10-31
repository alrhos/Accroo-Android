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
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    public static GsonUtil getInstance() {
        if (instance == null) {
            instance = new GsonUtil();
        }
        return instance;
    }

//    public JSONObject toJson(Object object) {
//        try {
//            return new JSONObject(gson.toJson(object));
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    public String toJson(Object object) {
        return gson.toJson(object);
    }

    public String toJson(ArrayList<Object> objects) {
        return gson.toJson(objects);
    }

//    public Object fromJson(JSONObject json, Class className) {
//        return gson.fromJson(json.toString(), className);
//    }

    public Object fromJson(String json, Class className) {
        return gson.fromJson(json, className);
    }

}