package com.paleskyline.navicash.services;

import android.content.Context;
import android.os.AsyncTask;

import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.KeyPackage;
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
    private Object dataObject;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private int transactionID;

    private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
    private ArrayList<SubCategory> subCategories = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    private String refreshToken, accessToken;


    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome, Context context) {
        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
    }

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome,
                           Context context, Object dataObject) {

        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
        this.dataObject = dataObject;
    }

    public interface PostRequestOutcome {
        void onPostRequestTaskFailure();
        void onPostRequestTaskSuccess(int requestType);
    }

    @Override
    protected Boolean doInBackground(JSONObject[]... dataReceiver) {
        try {
            switch (requestType) {

                case DataServices.GET_REFRESH_TOKEN:

                    accessToken = dataReceiver[0][0].getString("accessToken");
                    refreshToken = dataReceiver[0][0].getString("refreshToken");

                    AuthManager.getInstance(context).saveEntry(AuthManager.ACCESS_TOKEN_KEY, accessToken);
                    AuthManager.getInstance(context).saveEntry(AuthManager.REFRESH_TOKEN_KEY, refreshToken);

                    JSONObject keyData = dataReceiver[0][0].getJSONObject("key");

                    String key = keyData.getString("dataKey");
                    String nonce = keyData.getString("nonce");
                    String salt = keyData.getString("salt");
                    int memlimit = keyData.getInt("memLimit");
                    int opslimit = keyData.getInt("opsLimit");

                    KeyPackage keyPackage = new KeyPackage(key, nonce, salt, opslimit, memlimit);

                    DataProvider.getInstance().setKeyPackage(keyPackage);

                    return true;

                case DataServices.CREATE_USER:

                    refreshToken = dataReceiver[0][0].get("refreshToken").toString();
                    accessToken = dataReceiver[0][0].get("accessToken").toString();

                    AuthManager.getInstance(context).saveEntry(AuthManager.REFRESH_TOKEN_KEY, refreshToken);
                    AuthManager.getInstance(context).saveEntry(AuthManager.ACCESS_TOKEN_KEY, accessToken);

                    CryptoManager.getInstance().saveMasterKey(context);

                    return true;

                case DataServices.CREATE_DEFAULT_CATEGORIES:

                    //processCategories(dataReceiver[0]);
                    //sortData();

                    return true;

                case DataServices.GET_DEFAULT_DATA:

                    processCategories(dataReceiver[0]);
                    processTransactions(dataReceiver[0]);
                    sortData();

                    return true;

                case DataServices.CREATE_TRANSACTION:

                    transactionID = dataReceiver[0][0].getInt("transactionID");
                    transaction = (Transaction) dataObject;
                    transaction.setId(transactionID);

                    DataProvider.getInstance().addTransaction(transaction);

                    return true;

                case DataServices.UPDATE_TRANSACTION:

                    transaction = (Transaction) dataObject;

                    DataProvider.getInstance().updateTransaction(transaction);

                    return true;

                case DataServices.DELETE_TRANSACTION:

                    transaction = (Transaction) dataObject;

                    DataProvider.getInstance().deleteTransaction(transaction);

                    return true;

                case DataServices.CREATE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) dataObject;

                    DataProvider.getInstance().addGeneralCategory(generalCategory);

                    return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private void processCategories(JSONObject[] dataReceiver) throws Exception {

        System.out.println("PROCESSING CATEGORIES");
        System.out.println(dataReceiver[0].toString());

        JSONArray generalCategoriesArray = dataReceiver[0].getJSONArray("categories");

        for (int i = 0; i < generalCategoriesArray.length(); i++) {
            JSONObject gc = generalCategoriesArray.getJSONObject(i);
            GeneralCategory generalCategory = new GeneralCategory(gc);
            System.out.println(generalCategory.toString());
            JSONArray linkedSubCategories = gc.getJSONArray("subCategories");

            for (int j = 0; j < linkedSubCategories.length(); j++) {
                JSONObject sc = linkedSubCategories.getJSONObject(j);
                SubCategory subCategory = new SubCategory(sc);
                System.out.println(subCategory.toString());
                subCategories.add(subCategory);
            }

            generalCategories.add(generalCategory);
        }
    }

    private void processTransactions(JSONObject[] dataReceiver) throws Exception {

        JSONArray transactionsArray = dataReceiver[1].getJSONArray("transactions");

        for (int k = 0; k < transactionsArray.length(); k++) {
            JSONObject t = transactionsArray.getJSONObject(k);
            Transaction transaction = new Transaction(t);
            // TODO: extra condition required here to only let in transactions for the specified date transaction
            transactions.add(transaction);
        }

    }

    /* TODO - this logic should be moved to the data provider */

    private void sortData() {

        RootCategory[] rootCategories = {new RootCategory("Income"), new RootCategory("Expenses")};

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
    }

}
