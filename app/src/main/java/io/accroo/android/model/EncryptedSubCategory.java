package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedSubCategory extends SecurePayload {

    @Expose @SerializedName("general_category_id") private UUID generalCategoryId;

    public EncryptedSubCategory(UUID id, String data, String nonce, UUID generalCategoryId) {
        super(id, data, nonce);
        this.generalCategoryId = generalCategoryId;
    }

    public UUID getGeneralCategoryId() {
        return generalCategoryId;
    }

    public void setGeneralCategoryId(UUID generalCategoryId) {
        this.generalCategoryId = generalCategoryId;
    }

    public SubCategory decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(getId(), getData(), getNonce());
        String categoryString = CryptoManager.getInstance().decrypt(securePayload);
        SubCategory subCategory = GsonUtil.getInstance().fromJson(categoryString, SubCategory.class);
        subCategory.setId(getId());
        subCategory.setGeneralCategoryId(this.generalCategoryId);
        return subCategory;
    }

}
