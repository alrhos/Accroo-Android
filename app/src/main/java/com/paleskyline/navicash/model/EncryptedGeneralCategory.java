package com.paleskyline.navicash.model;

import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.SecurePayload;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


/**
 * Created by oscar on 21/03/17.
 */

public class EncryptedGeneralCategory extends EncryptedObject {

    private int id;

    public EncryptedGeneralCategory(SecurePayload payload) {
        super(payload);
    }

    public EncryptedGeneralCategory(int id, SecurePayload payload) {
        super(payload);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public GeneralCategory decryptObject() throws UnsupportedEncodingException, JSONException {
        String nonce = super.getPayload().getNonce();
        String cipherText = super.getPayload().getEncryptedJson();
        String jsonString = CryptoManager.getInstance().decrypt(nonce, cipherText);
        JSONObject json = new JSONObject(jsonString);
        GeneralCategory generalCategory = new GeneralCategory(id, json);
        return generalCategory;
    }

}
