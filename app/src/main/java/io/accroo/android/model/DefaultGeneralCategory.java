package io.accroo.android.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.other.GsonUtil;

public class DefaultGeneralCategory {

    @Expose private String categoryName;
    @Expose private String rootCategory;
    @Expose private String iconFile;
    @Expose private ArrayList<DefaultSubCategory> subCategories = new ArrayList<>();

    public DefaultGeneralCategory(String categoryName, String rootCategory, String iconFile) {
        this.categoryName = categoryName;
        this.rootCategory = rootCategory;
        this.iconFile = iconFile;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getRootCategory() {
        return rootCategory;
    }

    public void setRootCategory(String rootCategory) {
        this.rootCategory = rootCategory;
    }

    public String getIconFile() {
        return iconFile;
    }

    public void setIconFile(String iconFile) {
        this.iconFile = iconFile;
    }

    public ArrayList<DefaultSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<DefaultSubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public EncryptedDefaultGeneralCategory encrypt() {
        String categoryJson = GsonUtil.getInstance().toJson(this);
        SecurePayload securePayload = CryptoManager.getInstance().encrypt(categoryJson);
        EncryptedDefaultGeneralCategory generalCategory = new EncryptedDefaultGeneralCategory(
                securePayload.getData(), securePayload.getNonce());
        for (DefaultSubCategory subCategory: this.subCategories) {
            generalCategory.getSubCategories().add(subCategory.encrypt());
        }
        return generalCategory;
        //return new EncryptedGeneralCategory(securePayload.getData(), securePayload.getNonce());
    }

}
