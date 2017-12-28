package io.accroo.android.services;

/**
 * Created by oscar on 16/07/17.
 */

public class InputService {

    public static String capitaliseAndTrim(String input) {
        if (input.length() > 0) {
            String result = input.substring(0, 1).toUpperCase() + input.substring(1);
            return result.trim();
        }
        return input;
    }

}
