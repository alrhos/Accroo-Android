package com.paleskyline.navicash.crypto;

import javax.crypto.Cipher;

/**
 * Created by oscar on 5/03/17.
 */

public class AuthManager {

    private KeyStoreManager keyStoreManager;
    private Cipher cipher;

    public static final String USERNAME = "oscar.alston@protonmail.com";
    public static final char[] LOGINPASSWORD = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
    public static final char[] DATAPASSWORD = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
    public static String TOKEN = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4OTMwMzg1MiwiaWF0IjoxNDg5MzAzODIyfQ.MQ.-fwSlLMD_u2Qf50ZFb3ZHElRWaRaUrDP24pNIor7DPw";
    public static final KeyPackage KEYPACKAGE = new KeyPackage(
            "q+0qFl4CCmuPurOFCbN+hniO3U/N9/0lB6ykuYzviZMgnJuyqxx0SYwpflnPqyqp",
            "isLbmwq+/yTPak+ydxTBUAh2KlI5iCTw",
            "wUVKkkStud+8rT0Qqyw0zgwKtXXtocv1yK7+4Yl+xtI=",
            524288,
            16777216
    );

    public static synchronized void setToken(String token) {
        TOKEN = token;
    }

    /*
    public AuthManager() throws NoSuchAlgorithmException, NoSuchPaddingException {
        keyStoreManager = new KeyStoreManager();
        cipher = Cipher.getInstance("AES/GCM/NoPadding");

    }
    */

}
