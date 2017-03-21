package com.paleskyline.navicash.model;

/**
 * Created by oscar on 15/03/17.
 */

public interface Securable<T> {

    T encryptObject();
    //T fromJSON(JSONObject json);

}
