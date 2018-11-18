package io.accroo.android.other;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.lang.reflect.Type;
import java.util.Date;

public class GsonUtil {
    private static GsonUtil instance = null;
    private Gson gson;

    private GsonUtil() {
        gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .disableHtmlEscaping()
                .registerTypeAdapter(Date.class, new DateLongFormatTypeAdapter())
                .create();
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

    public Object objectFromJson(@NonNull String json, @NonNull Class className) {
        return gson.fromJson(json, className);
    }

//    public ArrayList<Object> fromJson(String json, Object[] objects) {
//        return Arrays.asList(gson.fromJson(json, objects.getClass()));
//        //return gson.fromJson(json, new TypeToken<List<type>>(){}.getType());
//    }

    public <T> T fromJson(@NonNull final String json, @NonNull Class<T> className) {
        return gson.fromJson(json, className);
    }

    public <E> ArrayList<E> listFromJson(@NonNull final String json, @NonNull final Class<E> className) {
        return gson.fromJson(json, new ListType<>(className));
    }

    private static class ListType<E> implements ParameterizedType {

        private Class<?> wrapped;

        private ListType(Class<E> wrapped) {
            this.wrapped = wrapped;
        }

        public Type[] getActualTypeArguments() {
            return new Type[]{wrapped};
        }

        public Type getRawType() {
            return ArrayList.class;
        }

        public Type getOwnerType() {
            return null;
        }
    }

}