package com.paleskyline.navicash.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Created by oscar on 27/04/17.
 */

public class KeyStoreManager {

    private KeyStore keyStore;
    private Cipher cipher;
    private SecureRandom secureRandom;
    private static final String ALIAS = "NavicashKey";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String AES = "AES/GCM/NoPadding";



    // CONSIDER ADDING SUPPORT FOR API LEVELS 21-22

    public KeyStoreManager() throws KeyStoreException, NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException,
            IOException, CertificateException, NoSuchPaddingException {

        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        if (!keyStore.containsAlias(ALIAS)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    ANDROID_KEY_STORE);
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT |
                        KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());
            keyGenerator.generateKey();
        }

        cipher = Cipher.getInstance(AES);
        secureRandom = new SecureRandom();
    }

    private SecretKey getSecretKey() {
        return null;
    }

    public void encrypt() {
       // cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, ))
    }

    public void decrypt() {

    }


    /*

    public void generateKey() throws NoSuchAlgorithmException, NoSuchProviderException,
            InvalidAlgorithmParameterException {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore");

        final KeyGenParameterSpec keyGenParameterSpec = new KeyGenParameterSpec.Builder(
                 ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).build();

        keyGenerator.init(keyGenParameterSpec);
        final SecretKey secretKey = keyGenerator.generateKey();
        System.out.println("GENERATED SECRET KEY IS: " + secretKey);
    }

    public void loadKey() throws KeyStoreException, NoSuchAlgorithmException,
            CertificateException, IOException, UnrecoverableEntryException {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);
        final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS, null);
        System.out.println("SECRET KEY ENTRY IS: " + secretKeyEntry);
    }

    */

}
