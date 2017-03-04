package com.paleskyline.navicash.crypto;

import android.util.Base64;

import org.libsodium.jni.Sodium;
import org.libsodium.jni.SodiumConstants;
import org.libsodium.jni.crypto.Random;
import org.libsodium.jni.crypto.SecretBox;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;


/**
 * Created by oscar on 4/03/17.
 */

public class CryptoManager {

    private Random random;
    private byte[] masterKey;
    private SecretBox secretBox;

    private static CryptoManager instance = null;

    public static CryptoManager getInstance() {
        if (instance == null) {
            instance = new CryptoManager();
        }
        return instance;
    }

    private CryptoManager() {
        random = new Random();
    }

    private byte[] passwordToByteArray(char[] password) {

        CharBuffer charBuffer = CharBuffer.wrap(password);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);

        byte[] passwordBytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());

        // Override values

        Arrays.fill(password, '\u0000');
        Arrays.fill(charBuffer.array(), '\u0000');
        Arrays.fill(byteBuffer.array(), (byte) 0);

        return passwordBytes;
    }

    private byte[] generateNonce() {
        int nonceSize = Sodium.crypto_secretbox_xsalsa20poly1305_noncebytes();
        byte[] nonce = random.randomBytes(nonceSize);
        return nonce;
    }

    private String encode(byte[] value) {
        return Base64.encodeToString(value, Base64.NO_WRAP);
    }

    private byte[] decode(String value) {
        return Base64.decode(value, Base64.NO_WRAP);
    }

    public KeyPackage generateKeyPackage(char[] password) {

        // Generate a master key

        masterKey = random.randomBytes(SodiumConstants.SECRETKEY_BYTES);

        // Generate byte array from password

        byte[] passwordBytes = passwordToByteArray(password);

        // Derive key from password byte array

        byte[] salt = random.randomBytes(Sodium.crypto_pwhash_scryptsalsa208sha256_saltbytes());
        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];

        int opslimit = Sodium.crypto_pwhash_scryptsalsa208sha256_opslimit_interactive();
        int memlimit = Sodium.crypto_pwhash_scryptsalsa208sha256_memlimit_interactive();

        Sodium.crypto_pwhash_scryptsalsa208sha256(dataPasswordKey, dataPasswordKey.length,
                passwordBytes, passwordBytes.length, salt, opslimit, memlimit);

        // Encrypt the data key using the key derived from the password

        SecretBox box = new SecretBox(dataPasswordKey);
        int nonceSize = Sodium.crypto_secretbox_xsalsa20poly1305_noncebytes();
        byte[] nonce = random.randomBytes(nonceSize);

        byte[] encryptedMasterKey = box.encrypt(nonce, masterKey);

        // Create a key package to persistently store key data

        String encodedMasterKey = encode(encryptedMasterKey);
        String encodedNonce = encode(nonce);
        String encodedSalt = encode(salt);

        KeyPackage keyPackage = new KeyPackage(encodedMasterKey, encodedNonce, encodedSalt, opslimit, memlimit);

        // Override values

        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(dataPasswordKey, (byte) 0);

        // Initialise the application secret box

        secretBox = new SecretBox(masterKey);

        return keyPackage;

    }

    public void decryptMasterKey(char[] password, KeyPackage keyPackage) {

        byte[] passwordBytes = passwordToByteArray(password);
        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];

        byte[] salt = decode(keyPackage.getSalt());
        int opslimit = keyPackage.getOpslimit();
        int memlimit = keyPackage.getMemlimit();

        Sodium.crypto_pwhash_scryptsalsa208sha256(dataPasswordKey, dataPasswordKey.length,
                passwordBytes, passwordBytes.length, salt, opslimit, memlimit);

        byte[] nonce = decode(keyPackage.getNonce());
        byte[] encryptedMasterKey = decode(keyPackage.getEncryptedMasterKey());

        SecretBox box = new SecretBox(dataPasswordKey);
        masterKey = box.decrypt(nonce, encryptedMasterKey);

        // Initialise the application secret box

        secretBox = new SecretBox(masterKey);

        // Override values

        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(dataPasswordKey, (byte) 0);

    }

    public byte[] getMasterKey() {
        return masterKey;
    }


    public String[] encrypt(String plainText) {
        byte[] nonce = generateNonce();
        byte[] cipherText = secretBox.encrypt(nonce, plainText.getBytes());
        String[] values = new String[2];
        values[0] = Base64.encodeToString(nonce, Base64.NO_WRAP);
        values[1] = Base64.encodeToString(cipherText, Base64.NO_WRAP);
        return values;
    }

    public String decrypt(String nonce, String cipherText) {
        byte[] cipherTextBytes = Base64.decode(cipherText, Base64.NO_WRAP);
        byte[] nonceBytes = Base64.decode(nonce, Base64.NO_WRAP);
        byte[] plainText = secretBox.decrypt(nonceBytes, cipherTextBytes);
        String value = null;
        try {
            value = new String(plainText, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return value;
        }
    }

}
