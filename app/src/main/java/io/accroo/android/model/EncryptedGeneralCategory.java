package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedGeneralCategory {

    @Expose(serialize = false) private int id;
    @Expose private String data;
    @Expose private String nonce;
    @Expose (serialize = false) private ArrayList<EncryptedSubCategory> subCategories = new ArrayList<>();

    public EncryptedGeneralCategory(int id, String data, String nonce) {
        this.id = id;
        this.data = data;
        this.nonce = nonce;
    }

    public EncryptedGeneralCategory(String data, String nonce) {
        this.data = data;
        this.nonce = nonce;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public ArrayList<EncryptedSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<EncryptedSubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public GeneralCategory decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(this.data, this.nonce);
        String categoryString = CryptoManager.getInstance().decrypt(securePayload);
//        ArrayList<SubCategory> decryptedSubCategories = new ArrayList<>();
//        for (EncryptedSubCategory encryptedSubCategory: this.subCategories) {
//            SubCategory decryptedCategory = CryptoManager.getInstance().decrypt()
//            decryptedSubCategories.add(CryptoManager.)
//        }
        GeneralCategory generalCategory = GsonUtil.getInstance().fromJson(categoryString, GeneralCategory.class);
        generalCategory.setId(this.id);
        return generalCategory;
    }

    @Override
    public String toString() {
        return "EncryptedGeneralCategory{" +
                "id=" + id +
                ", data='" + data + '\'' +
                ", nonce='" + nonce + '\'' +
                ", subCategories=" + subCategories +
                '}';
    }
}
