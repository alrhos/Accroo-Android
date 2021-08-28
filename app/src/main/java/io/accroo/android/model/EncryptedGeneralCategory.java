package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedGeneralCategory extends SecurePayload {

    @Expose(serialize = false) @SerializedName("sub_categories")
    private ArrayList<EncryptedSubCategory> subCategories = new ArrayList<>();

    public EncryptedGeneralCategory(UUID id, String data, String nonce) {
        super(id, data, nonce);
    }

    public ArrayList<EncryptedSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<EncryptedSubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public GeneralCategory decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(getId(), getData(), getNonce());
        String categoryString = CryptoManager.getInstance().decrypt(securePayload);
        GeneralCategory generalCategory = GsonUtil.getInstance().fromJson(categoryString, GeneralCategory.class);
        generalCategory.setId(getId());
        return generalCategory;
    }

}
