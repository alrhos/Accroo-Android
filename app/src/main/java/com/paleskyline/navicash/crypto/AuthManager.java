package com.paleskyline.navicash.crypto;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by oscar on 5/03/17.
 */

public class AuthManager {

    private static AuthManager instance = null;
    private static KeyStoreManager keyStoreManager;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String APP = "com.paleskyline.navicash";
    public static final String USERNAME_KEY = "usernameKey";
    public static final String PASSWORD_KEY = "passwordKey";
    public static final String ENCRYPTION_KEY = "encryptionKey";
    public static final String TOKEN_KEY = "tokenKey";


    /*
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
    */

    private AuthManager() throws KeyStoreException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException,
            IOException, CertificateException, NoSuchPaddingException {

        keyStoreManager = new KeyStoreManager();
    }

    public static AuthManager getInstance(Context context) throws Exception {
        if (instance == null) {
            try {
                instance = new AuthManager();
            } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException |
                    InvalidAlgorithmParameterException | IOException | CertificateException |
                    NoSuchPaddingException e) {

                e.printStackTrace();
                // TODO: review exception that is thrown here
                throw new Exception("KeyStore exception!");
            }
        }
        sharedPreferences = context.getSharedPreferences(APP, Context.MODE_PRIVATE);
        return instance;
    }

    public void saveEntry(String key, String value) throws Exception {
        try {
            String encryptedValue = keyStoreManager.encrypt(value);
            editor = sharedPreferences.edit();
            editor.putString(key, encryptedValue);
            editor.commit();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException |
                KeyStoreException | InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {

            e.printStackTrace();
            // TODO: review exception that is thrown here
            throw new Exception("Encryption exception!");
        }
    }

    public String getEntry(String key) throws Exception {
        try {
            String encryptedValue = sharedPreferences.getString(key, null);
            if (encryptedValue == null) {
                throw new Exception("Value doesn't exist!");
            }
            return keyStoreManager.decrypt(encryptedValue);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnrecoverableEntryException |
                KeyStoreException | InvalidKeyException | IllegalBlockSizeException |
                BadPaddingException | UnsupportedEncodingException |
                InvalidAlgorithmParameterException e) {

            e.printStackTrace();
            // TODO: review exception that is thrown here
            throw new Exception("Decryption exception!");
        }
    }


}
