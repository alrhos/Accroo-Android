package com.paleskyline.navicash.model;

import org.json.JSONObject;

/**
 * Created by oscar on 15/03/17.
 */

public interface Securable<T> {

    JSONObject toJSON();
    T fromJSON(JSONObject json);

}
