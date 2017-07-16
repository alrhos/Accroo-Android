package com.paleskyline.navicash.services;

/**
 * Created by oscar on 16/07/17.
 */

public class InputServices {

    public static String capitaliseAndTrim(String input) {
        String result = input.substring(0, 1).toUpperCase() + input.substring(1);
        return result.trim();
    }

}
