package io.accroo.android.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * Created by oscar on 27/04/17.
 */

public class KeyStoreManager {

    private KeyStore            keyStore;
    private static final String ALIAS = "Accroo";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final int    IV_LENGTH = 12;
    private static final int    AUTH_TAG_LENGTH = 128;

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
    }

    private SecretKey getSecretKey() throws NoSuchAlgorithmException,
            UnrecoverableEntryException, KeyStoreException {
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(ALIAS, null);
        return secretKeyEntry.getSecretKey();
    }

    public String encrypt(String plainText) throws NoSuchAlgorithmException, UnrecoverableEntryException,
            KeyStoreException, InvalidKeyException, InvalidAlgorithmParameterException,
            IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {

        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        byte[] iv = cipher.getIV();
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        byte[] record = new byte[IV_LENGTH + cipherText.length];

        System.arraycopy(iv, 0, record, 0, 12);
        System.arraycopy(cipherText, 0, record, 12, cipherText.length);

        return Base64.encodeToString(record, Base64.NO_WRAP);
    }

    public String decrypt(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException,
            UnrecoverableEntryException, KeyStoreException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException,
            InvalidAlgorithmParameterException {

        byte[] stringBytes = Base64.decode(cipherText, Base64.NO_WRAP);
        byte[] cipherTextBytes = new byte[stringBytes.length - IV_LENGTH];

        System.arraycopy(stringBytes, 12, cipherTextBytes, 0, cipherTextBytes.length);

        Cipher cipher = Cipher.getInstance(AES_MODE);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(AUTH_TAG_LENGTH, stringBytes, 0, 12);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), gcmParameterSpec);
        byte[] plainTextBytes = cipher.doFinal(cipherTextBytes);

        return new String(plainTextBytes, "UTF-8");
    }

}
