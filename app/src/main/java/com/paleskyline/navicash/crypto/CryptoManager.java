package com.paleskyline.navicash.crypto;

import android.content.Context;
import android.util.Base64;

import com.paleskyline.navicash.model.KeyPackage;
import com.paleskyline.navicash.model.SecurePayload;

import org.libsodium.jni.NaCl;
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

    private Random               random;
    private byte[]               masterKey;
    private SecretBox            secretBox;
    private static CryptoManager instance = null;

    public static CryptoManager getInstance() {
        if (instance == null) {
            instance = new CryptoManager();
        }
        return instance;
    }

    private CryptoManager() {
        NaCl.sodium();
        if (Sodium.sodium_init() == -1) {
            throw new IllegalStateException("Libsodium could not be initialised");
        }
        this.random = new Random();
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
        return random.randomBytes(nonceSize);
    }

    private String encode(byte[] value) {
        return Base64.encodeToString(value, Base64.NO_WRAP);
    }

    private byte[] decode(String value) {
        return Base64.decode(value, Base64.NO_WRAP);
    }

    private KeyPackage generateKeyPackage(byte[] key, char[] password) {

        System.out.println("Min memory: " + Sodium.crypto_pwhash_memlimit_min());

        // Generate byte array from password

        byte[] passwordBytes = passwordToByteArray(password);

        // Derive key from password byte array

        //int saltSize = Sodium.crypto_pwhash_scryptsalsa208sha256_saltbytes();
        int saltSize = Sodium.crypto_pwhash_saltbytes();
        byte[] salt = random.randomBytes(saltSize);
        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];

//        int opslimit = Sodium.crypto_pwhash_scryptsalsa208sha256_opslimit_interactive();
//        int memlimit = Sodium.crypto_pwhash_scryptsalsa208sha256_memlimit_interactive();

        int algorithm = Sodium.crypto_pwhash_alg_default();
        int opslimit = Sodium.crypto_pwhash_opslimit_interactive();
        int memlimit = Sodium.crypto_pwhash_memlimit_interactive();


//        Sodium.crypto_pwhash_scryptsalsa208sha256(dataPasswordKey, dataPasswordKey.length,
//                passwordBytes, passwordBytes.length, salt, opslimit, memlimit);

        Sodium.crypto_pwhash(dataPasswordKey, dataPasswordKey.length, passwordBytes,
                passwordBytes.length, salt, opslimit, memlimit, algorithm);

        // Encrypt the master key using the key derived from the password

        SecretBox box = new SecretBox(dataPasswordKey);
        int nonceSize = Sodium.crypto_secretbox_xsalsa20poly1305_noncebytes();
        byte[] nonce = random.randomBytes(nonceSize);

        byte[] encryptedMasterKey = box.encrypt(nonce, key);

        // Create a key package to persistently store key data

        String encodedMasterKey = encode(encryptedMasterKey);
        String encodedNonce = encode(nonce);
        String encodedSalt = encode(salt);

        KeyPackage keyPackage = new KeyPackage(encodedMasterKey, encodedNonce,
                encodedSalt, algorithm, opslimit, memlimit);

        // Override values

        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(dataPasswordKey, (byte) 0);

        // TODO: the password char[] should probably also be overridden

        // Initialise the application secret box

        // TODO: The secret box probably doesn't need to be initalised here as it will already have been inited during launch

        //secretBox = new SecretBox(key);

        return keyPackage;

    }

    public KeyPackage generateNewKey(char[] password) {

        masterKey = random.randomBytes(SodiumConstants.SECRETKEY_BYTES);

        // Initialise the application secret box

        secretBox = new SecretBox(masterKey);

        KeyPackage keyPackage = generateKeyPackage(masterKey, password);

        // TODO: review password security

        return  keyPackage;

    }

//    public KeyPackage generateNewKey(char[] password) {
//
//        // Generate a master key
//
//        masterKey = random.randomBytes(SodiumConstants.SECRETKEY_BYTES);
//
//        // Generate byte array from password
//
//        byte[] passwordBytes = passwordToByteArray(password);
//
//        // Derive key from password byte array
//
//        int saltSize = Sodium.crypto_pwhash_scryptsalsa208sha256_saltbytes();
//        byte[] salt = random.randomBytes(saltSize);
//        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];
//
//        int opslimit = Sodium.crypto_pwhash_scryptsalsa208sha256_opslimit_interactive();
//        int memlimit = Sodium.crypto_pwhash_scryptsalsa208sha256_memlimit_interactive();
//
//        Sodium.crypto_pwhash_scryptsalsa208sha256(dataPasswordKey, dataPasswordKey.length,
//                passwordBytes, passwordBytes.length, salt, opslimit, memlimit);
//
//        // Encrypt the master key using the key derived from the password
//
//        SecretBox box = new SecretBox(dataPasswordKey);
//        int nonceSize = Sodium.crypto_secretbox_xsalsa20poly1305_noncebytes();
//        byte[] nonce = random.randomBytes(nonceSize);
//
//        byte[] encryptedMasterKey = box.encrypt(nonce, masterKey);
//
//        // Create a key package to persistently store key data
//
//        String encodedMasterKey = encode(encryptedMasterKey);
//        String encodedNonce = encode(nonce);
//        String encodedSalt = encode(salt);
//
//        KeyPackage keyPackage = new KeyPackage(encodedMasterKey, encodedNonce,
//                encodedSalt, opslimit, memlimit);
//
//        // Override values
//
//        Arrays.fill(passwordBytes, (byte) 0);
//        Arrays.fill(dataPasswordKey, (byte) 0);
//
//        // Initialise the application secret box
//
//        secretBox = new SecretBox(masterKey);
//
//        return keyPackage;
//
//    }

    public KeyPackage encryptMasterKey(char[] password, Context context) throws Exception {
        // TODO: review security of password and key array
        byte[] secretKeyBytes = decode(AuthManager.getInstance(context).getEntry(AuthManager.ENCRYPTION_KEY));
        return generateKeyPackage(secretKeyBytes, password);
    }

    public void decryptMasterKey(char[] password, KeyPackage keyPackage) {

        byte[] passwordBytes = passwordToByteArray(password);
        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];

        byte[] salt = decode(keyPackage.getSalt());
        int algorithm = keyPackage.getAlgorithm();
        int opslimit = keyPackage.getOpslimit();
        int memlimit = keyPackage.getMemlimit();

//        Sodium.crypto_pwhash_scryptsalsa208sha256(dataPasswordKey, dataPasswordKey.length,
//                passwordBytes, passwordBytes.length, salt, opslimit, memlimit);


        Sodium.crypto_pwhash(dataPasswordKey, dataPasswordKey.length, passwordBytes,
                passwordBytes.length, salt, opslimit, memlimit, algorithm);

        byte[] nonce = decode(keyPackage.getNonce());
        byte[] encryptedMasterKey = decode(keyPackage.getEncryptedMasterKey());

        SecretBox box = new SecretBox(dataPasswordKey);
        this.masterKey = box.decrypt(nonce, encryptedMasterKey);

        // Initialise the application secret box

        this.secretBox = new SecretBox(masterKey);

        // Override values

        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(dataPasswordKey, (byte) 0);

    }


    // TODO: review this method's use
    public void saveMasterKey(Context context) throws Exception {
        AuthManager.getInstance(context).saveEntry(AuthManager.ENCRYPTION_KEY, encode(masterKey));
    }

    public void initMasterKey(Context context) throws Exception {
        byte[] secretKeyBytes = decode(AuthManager.getInstance(context).getEntry(AuthManager.ENCRYPTION_KEY));
        secretBox = new SecretBox(secretKeyBytes);
        // TODO: review password security here and override secretKeyBytes
    }

    public SecurePayload encrypt(String plainText) {
        byte[] nonce = generateNonce();
        byte[] cipherText = secretBox.encrypt(nonce, plainText.getBytes());
        return new SecurePayload(encode(cipherText), encode(nonce));
    }

    public String decrypt(SecurePayload payload) throws UnsupportedEncodingException {
        byte[] cipherTextBytes = decode(payload.getData());
        byte[] nonceBytes = decode(payload.getNonce());
        byte[] plainText = secretBox.decrypt(nonceBytes, cipherTextBytes);
        return new String(plainText, "UTF-8");
    }

}
