package io.accroo.android.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class EncryptedTransaction extends SecurePayload {

    @Expose @SerializedName("sub_category_id") private UUID subCategoryId;

    public EncryptedTransaction(UUID id, String data, String nonce, UUID subCategoryId) {
        super(id, data, nonce);
        this.subCategoryId = subCategoryId;
    }

    public UUID getSubCategoryId() {
        return subCategoryId;
    }

    public void setSubCategoryId(UUID subCategoryId) {
        this.subCategoryId = subCategoryId;
    }

    public Transaction decrypt() throws UnsupportedEncodingException {
        SecurePayload securePayload = new SecurePayload(getId(), getData(), getNonce());
        String transactionJson = CryptoManager.getInstance().decrypt(securePayload);
        Transaction transaction = GsonUtil.getInstance().fromJson(transactionJson, Transaction.class);
        transaction.setId(getId());
        transaction.setSubCategoryId(this.subCategoryId);
        return transaction;
    }

}
