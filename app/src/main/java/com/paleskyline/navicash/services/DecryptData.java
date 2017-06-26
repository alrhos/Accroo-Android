package com.paleskyline.navicash.services;

import android.os.AsyncTask;

import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.RootCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by oscar on 12/06/17.
 */

public class DecryptData extends AsyncTask<JSONObject[], Boolean, Boolean> {

    private OnDecryptionComplete decryptionListener;

    // TODO: experiment with passing in activity name and use when building the intent.

    public DecryptData(OnDecryptionComplete decryptionListener) {
        this.decryptionListener = decryptionListener;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            decryptionListener.onSuccessfulDecryption();
        } else {
            decryptionListener.onUnsuccessfulDecryption();
        }

    }

    @Override
    protected Boolean doInBackground(JSONObject[]... jsonObjects) {

        RootCategory[] rootCategories = {new RootCategory("Income"), new RootCategory("Expenses")};
        ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
        ArrayList<SubCategory> subCategories = new ArrayList<>();
        ArrayList<Transaction> transactions = new ArrayList<>();

        try {

            JSONArray generalCategoriesArray = jsonObjects[0][0].getJSONArray("categories");

            for (int i = 0; i < generalCategoriesArray.length(); i++) {

                JSONObject gc = generalCategoriesArray.getJSONObject(i);
                GeneralCategory generalCategory = new GeneralCategory(gc);

                JSONArray linkedSubCategories = gc.getJSONArray("subCategories");

                for (int j = 0; j < linkedSubCategories.length(); j++) {
                    SubCategory subCategory = new SubCategory(linkedSubCategories.getJSONObject(j));
                    subCategories.add(subCategory);
                    //subCategory.setCategoryIcon(generalCategory.getIconFile());
                    //generalCategory.getSubCategories().add(subCategory);
                }

                //GeneralCategory generalCategory = new GeneralCategory(generalCategoriesArray.getJSONObject(i));
                generalCategories.add(generalCategory);
            }

//            JSONArray subCategories = jsonObjects[0][1].getJSONArray("categories");
//            for (int j = 0; j < subCategories.length(); j++) {
//                SubCategory subCategory = new SubCategory(subCategories.getJSONObject(j));
//                subCategories.add(subCategory);
//            }

            JSONArray transactionsArray = jsonObjects[0][1].getJSONArray("transactions");

            for (int k = 0; k < transactionsArray.length(); k++) {
                Transaction transaction = new Transaction(transactionsArray.getJSONObject(k));
                // TODO: extra condition required here to only let in transactions for the specified date transaction
                transactions.add(transaction);
            }

            for (Transaction tx: transactions) {
                for (SubCategory s : subCategories) {
                    if (tx.getSubCategoryID() == s.getId()) {
                        for (GeneralCategory g : generalCategories) {
                            if (g.getId() == s.getGeneralCategoryID()) {
                                tx.setCategoryIcon(g.getIconFile());
                                tx.setRootCategoryType(g.getRootCategory());
                            }
                        }
                        tx.setSubCategoryName(s.getCategoryName());
                        s.getTransactions().add(tx);
                        break;
                    }
                }
            }

            for (SubCategory s : subCategories) {
                for (GeneralCategory g : generalCategories) {
                    if (s.getGeneralCategoryID() == g.getId()) {
                        s.setCategoryIcon(g.getIconFile());
                        g.getSubCategories().add(s);
                        break;
                    }
                }
            }

            for (GeneralCategory g : generalCategories) {
                for (int c = 0; c < rootCategories.length; c++) {
                    if (g.getRootCategory().equals(rootCategories[c].getCategoryName())) {
                        rootCategories[c].getGeneralCategories().add(g);
                        break;
                    }
                }
            }

            DataProvider.getInstance().setRootCategories(rootCategories);

            return true;

        } catch (JSONException | UnsupportedEncodingException e) {
            // TODO: error handling
            e.printStackTrace();
            return false;
        }
    }

    public interface OnDecryptionComplete {
        void onSuccessfulDecryption();
        void onUnsuccessfulDecryption();
    }

}
