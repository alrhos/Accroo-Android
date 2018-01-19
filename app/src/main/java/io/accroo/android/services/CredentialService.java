package io.accroo.android.services;

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

import io.accroo.android.crypto.KeyStoreManager;

/**
 * Created by oscar on 5/03/17.
 */

public class CredentialService {

    private static CredentialService        instance = null;
    private static KeyStoreManager          keyStoreManager;
    private static SharedPreferences        sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String             APP = "io.accroo.android";
    public static final String              USERNAME_KEY = "usernameKey";
    public static final String              ENCRYPTION_KEY = "encryptionKey";
    public static final String              DEVICE_TOKEN_KEY = "deviceTokenKey";
 //   public static final String              REFRESH_TOKEN_KEY = "refreshTokenKey";
  //  public static final String              ACCESS_TOKEN_KEY = "accessTokenKey";

    private CredentialService() throws KeyStoreException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException,
            IOException, CertificateException, NoSuchPaddingException {

        keyStoreManager = new KeyStoreManager();
    }

    public static CredentialService getInstance(Context context) throws Exception {
        if (instance == null) {
            try {
                instance = new CredentialService();
            } catch (KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException |
                    InvalidAlgorithmParameterException | IOException | CertificateException |
                    NoSuchPaddingException e) {
                e.printStackTrace();
                throw new Exception("The KeyStore could not be initialized");
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
            editor.apply();
        } catch (NoSuchAlgorithmException | UnrecoverableEntryException |
                KeyStoreException | InvalidKeyException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException e) {
            e.printStackTrace();
            throw new Exception(key + " could not be encrypted");
        }
    }

    public String getEntry(String key) throws Exception {
        try {
            String encryptedValue = sharedPreferences.getString(key, null);
            if (encryptedValue == null) {
                throw new Exception("No shared preferences value exists for the key " + key);
            }
            return keyStoreManager.decrypt(encryptedValue);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnrecoverableEntryException |
                KeyStoreException | InvalidKeyException | IllegalBlockSizeException |
                BadPaddingException | UnsupportedEncodingException |
                InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            throw new Exception(key + " could not be decrypted");
        }
    }

    public void clearSavedData() {
        editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

}
