package io.accroo.android.crypto;

import android.content.Context;
import android.util.Base64;

import io.accroo.android.model.Key;
import io.accroo.android.model.SecurePayload;
import io.accroo.android.services.CredentialService;

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

    private Key generateKeyPackage(byte[] key, char[] password) {
        byte[] passwordBytes = passwordToByteArray(password);

        // Derive key from password byte array

        int saltSize = Sodium.crypto_pwhash_saltbytes();
        byte[] salt = random.randomBytes(saltSize);
        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];

        int algorithm = Sodium.crypto_pwhash_alg_default();
        int opslimit = Sodium.crypto_pwhash_opslimit_interactive();
        int memlimit = Sodium.crypto_pwhash_memlimit_interactive();

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

        Key keyPackage = new Key(encodedMasterKey, encodedNonce,
                encodedSalt, algorithm, opslimit, memlimit);

        Arrays.fill(password, '\u0000');
        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(dataPasswordKey, (byte) 0);

        return keyPackage;
    }

    public Key generateNewKey(char[] password) {
        masterKey = random.randomBytes(SodiumConstants.SECRETKEY_BYTES);
        secretBox = new SecretBox(masterKey);
        Key key = generateKeyPackage(masterKey, password);
        Arrays.fill(password, '\u0000');
        return key;
    }

    public Key encryptMasterKey(char[] password, Context context) throws Exception {
        byte[] secretKeyBytes = decode(CredentialService.getInstance(context).getEntry(CredentialService.ENCRYPTION_KEY));
        Key key = generateKeyPackage(secretKeyBytes, password);
        Arrays.fill(password, '\u0000');
        return key;
    }

    public void decryptMasterKey(char[] password, Key key) {
        byte[] passwordBytes = passwordToByteArray(password);
        byte[] dataPasswordKey = new byte[SodiumConstants.SECRETKEY_BYTES];

        byte[] salt = decode(key.getSalt());
        int algorithm = key.getAlgorithm();
        int opslimit = key.getOpslimit();
        int memlimit = key.getMemlimit();

        Sodium.crypto_pwhash(dataPasswordKey, dataPasswordKey.length, passwordBytes,
                passwordBytes.length, salt, opslimit, memlimit, algorithm);

        byte[] nonce = decode(key.getNonce());
        byte[] encryptedMasterKey = decode(key.getEncryptedKey());

        SecretBox box = new SecretBox(dataPasswordKey);
        this.masterKey = box.decrypt(nonce, encryptedMasterKey);
        this.secretBox = new SecretBox(masterKey);

        Arrays.fill(password, '\u0000');
        Arrays.fill(passwordBytes, (byte) 0);
        Arrays.fill(dataPasswordKey, (byte) 0);
    }

    public void saveMasterKey(Context context) throws Exception {
        CredentialService.getInstance(context).saveEntry(CredentialService.ENCRYPTION_KEY, encode(masterKey));
    }

    public void initMasterKey(Context context) throws Exception {
        byte[] secretKeyBytes = decode(CredentialService.getInstance(context).getEntry(CredentialService.ENCRYPTION_KEY));
        secretBox = new SecretBox(secretKeyBytes);
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