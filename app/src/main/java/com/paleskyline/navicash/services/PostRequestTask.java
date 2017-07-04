package com.paleskyline.navicash.services;

import android.content.Context;
import android.os.AsyncTask;

import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.RootCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by oscar on 4/07/17.
 */

public class PostRequestTask extends AsyncTask<JSONObject[], Boolean, Boolean> {

    private int requestType;
    private PostRequestOutcome postRequestOutcome;
    private Context context;
    private String startDate;
    private String endDate;

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome, Context context) {
        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
    }

    public interface PostRequestOutcome {
        void onPostRequestTaskFailure();
        void onPostRequestTaskSuccess(int requestType);
    }

    @Override
    protected Boolean doInBackground(JSONObject[]... dataReceiver) {
        try {
            switch (requestType) {
                case DataServices.GET_DEFAULT_DATA:

                    RootCategory[] rootCategories = {new RootCategory("Income"), new RootCategory("Expenses")};
                    ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
                    ArrayList<SubCategory> subCategories = new ArrayList<>();
                    ArrayList<Transaction> transactions = new ArrayList<>();

                    JSONArray generalCategoriesArray = dataReceiver[0][0].getJSONArray("categories");

                    for (int i = 0; i < generalCategoriesArray.length(); i++) {

                        JSONObject gc = generalCategoriesArray.getJSONObject(i);
                        GeneralCategory generalCategory = new GeneralCategory(gc);

                        JSONArray linkedSubCategories = gc.getJSONArray("subCategories");

                        for (int j = 0; j < linkedSubCategories.length(); j++) {
                            JSONObject sc = linkedSubCategories.getJSONObject(j);
                            SubCategory subCategory = new SubCategory(sc);
                            subCategories.add(subCategory);
                        }

                        generalCategories.add(generalCategory);
                    }

                    JSONArray transactionsArray = dataReceiver[0][1].getJSONArray("transactions");

                    for (int k = 0; k < transactionsArray.length(); k++) {
                        JSONObject t = transactionsArray.getJSONObject(k);
                        Transaction transaction = new Transaction(t);
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

                    for (SubCategory sc : subCategories) {
                        for (GeneralCategory g : generalCategories) {
                            if (sc.getGeneralCategoryID() == g.getId()) {
                                sc.setCategoryIcon(g.getIconFile());
                                g.getSubCategories().add(sc);
                                break;
                            }
                        }
                    }

                    for (GeneralCategory gc : generalCategories) {
                        for (int c = 0; c < rootCategories.length; c++) {
                            if (gc.getRootCategory().equals(rootCategories[c].getCategoryName())) {
                                rootCategories[c].getGeneralCategories().add(gc);
                                break;
                            }
                        }
                    }

                    DataProvider.getInstance().setRootCategories(rootCategories);

                    return true;

                case DataServices.CREATE_USER:

                    String refreshToken = dataReceiver[0][0].get("refreshToken").toString();
                    String accessToken = dataReceiver[0][0].get("accessToken").toString();

                    AuthManager.getInstance(context).saveEntry(AuthManager.REFRESH_TOKEN_KEY, refreshToken);
                    AuthManager.getInstance(context).saveEntry(AuthManager.ACCESS_TOKEN_KEY, accessToken);

                    CryptoManager.getInstance().saveMasterKey(context);

                    return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            postRequestOutcome.onPostRequestTaskSuccess(requestType);
        } else {
            postRequestOutcome.onPostRequestTaskFailure();
        }
    }

}
