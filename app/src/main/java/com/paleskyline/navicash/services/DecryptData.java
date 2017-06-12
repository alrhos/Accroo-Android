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

        RootCategory[] rc = {new RootCategory("Income"), new RootCategory("Expenses")};
        ArrayList<GeneralCategory> gc = new ArrayList<>();
        ArrayList<SubCategory> sc = new ArrayList<>();
        ArrayList<Transaction> t = new ArrayList<>();

        try {

            JSONArray generalCategories = jsonObjects[0][0].getJSONArray("categories");
            for (int i = 0; i < generalCategories.length(); i++) {
                GeneralCategory generalCategory = new GeneralCategory(generalCategories.getJSONObject(i));
                gc.add(generalCategory);
            }

            JSONArray subCategories = jsonObjects[0][1].getJSONArray("categories");
            for (int j = 0; j < subCategories.length(); j++) {
                SubCategory subCategory = new SubCategory(subCategories.getJSONObject(j));
                sc.add(subCategory);
            }

            JSONArray transactions = jsonObjects[0][2].getJSONArray("transactions");
            for (int k = 0; k < transactions.length(); k++) {
                Transaction transaction = new Transaction(transactions.getJSONObject(k));
                // TODO: extra condition required here to only let in transactions for the specified date transaction
                t.add(transaction);
            }

            for (Transaction tx: t) {
                for (SubCategory s : sc) {
                    if (tx.getSubCategoryID() == s.getId()) {
                        for (GeneralCategory g : gc) {
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

            for (SubCategory s : sc) {
                for (GeneralCategory g : gc) {
                    if (s.getGeneralCategoryID() == g.getId()) {
                        s.setCategoryIcon(g.getIconFile());
                        g.getSubCategories().add(s);
                        break;
                    }
                }
            }

            for (GeneralCategory g : gc) {
                for (int c = 0; c < rc.length; c++) {
                    if (g.getRootCategory().equals(rc[c].getCategoryName())) {
                        rc[c].getGeneralCategories().add(g);
                        break;
                    }
                }
            }

            DataProvider.getInstance().setRootCategories(rc);

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
