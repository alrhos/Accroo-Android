package com.paleskyline.navicash.crypto;

/**
 * Created by oscar on 5/03/17.
 */

public class AuthManager {

    public static final String USERNAME = "oscar.alston@protonmail.com";
    public static final char[] LOGINPASSWORD = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
    public static final char[] DATAPASSWORD = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
    public static String TOKEN = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4ODg4MzQ1MywiaWF0IjoxNDg4ODgzNDIzfQ.MQ.FjdmRmA1In4xtXaNH1YvPy0f8VR77sVVYJoSbckVUYs";
    public static final KeyPackage KEYPACKAGE = new KeyPackage(
            "q+0qFl4CCmuPurOFCbN+hniO3U/N9/0lB6ykuYzviZMgnJuyqxx0SYwpflnPqyqp",
            "isLbmwq+/yTPak+ydxTBUAh2KlI5iCTw",
            "wUVKkkStud+8rT0Qqyw0zgwKtXXtocv1yK7+4Yl+xtI=",
            524288,
            16777216
    );

    public static void setToken(String token) {
        TOKEN = token;
    }

}
