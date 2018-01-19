package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.KeyPackage;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by oscar on 4/07/17.
 */

public class PostRequestTask extends AsyncTask<JSONObject[], Boolean, Boolean> {

    private int requestType;
    private PostRequestOutcome postRequestOutcome;
    private Context context;
    private Date startDate;
    private Date endDate;
    private Object dataObject;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private SubCategory subCategory;
    private int id;
    //private String username, refreshToken, accessToken;
    private String username, deviceToken;
    private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
    private ArrayList<SubCategory> subCategories = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome, Context context) {

        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;

    }

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome, Context context,
                           Date startDate, Date endDate) {
        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome,
                           Context context, String username) {

        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
        this.username = username;

    }

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome,
                           Context context, Object dataObject) {

        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
        this.dataObject = dataObject;

    }

    public interface PostRequestOutcome {
        void onPostRequestTaskSuccess(int requestType);
        void onPostRequestTaskFailure();
    }

    @Override
    protected Boolean doInBackground(JSONObject[]... dataReceiver) {
        try {
            switch (requestType) {

                case ApiService.GET_REFRESH_TOKEN:

//                    refreshToken = dataReceiver[0][0].getString("refreshToken");
//                    accessToken = dataReceiver[0][0].getJSONObject("accessToken").getString("token");

                    deviceToken = dataReceiver[0][0].getString("deviceToken");

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, username);
                    CredentialService.getInstance(context).saveEntry(CredentialService.DEVICE_TOKEN_KEY, deviceToken);
//                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, accessToken);
//                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_KEY, refreshToken);

                    JSONObject keyData = dataReceiver[0][0].getJSONObject("keyPackage");

                    String key = keyData.getString("key");
                    String nonce = keyData.getString("nonce");
                    String salt = keyData.getString("salt");
                    int algorithm = keyData.getInt("algorithm");
                    int memlimit = keyData.getInt("memlimit");
                    int opslimit = keyData.getInt("opslimit");

                    KeyPackage keyPackage = new KeyPackage(key, nonce, salt, algorithm, opslimit, memlimit);

                    DataProvider.setKeyPackage(keyPackage);

                    return true;

                case ApiService.CREATE_USER:

                    username = ((User) dataObject).getEmail();
                    deviceToken = dataReceiver[0][0].getString("deviceToken");
//                    refreshToken = dataReceiver[0][0].getString("refreshToken");
//                    accessToken = dataReceiver[0][0].getJSONObject("accessToken").getString("token");

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, username);
                    CredentialService.getInstance(context).saveEntry(CredentialService.DEVICE_TOKEN_KEY, deviceToken);
//                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_KEY, refreshToken);
//                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, accessToken);

                    CryptoManager.getInstance().saveMasterKey(context);

                    return true;

                case ApiService.CREATE_DEFAULT_CATEGORIES:

                    return true;

                case ApiService.GET_DEFAULT_DATA:

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

                        if (!transaction.getDate().before(startDate) && !transaction.getDate().after(endDate)) {
                            transactions.add(transaction);
                        }
                    }

                    DataProvider.loadData(generalCategories, subCategories, transactions);

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    id = dataReceiver[0][0].getInt("transactionID");
                    transaction = (Transaction) dataObject;
                    transaction.setId(id);
                    DataProvider.addTransaction(transaction);
                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    transaction = (Transaction) dataObject;
                    DataProvider.updateTransaction(transaction);
                    return true;

                case ApiService.DELETE_TRANSACTION:

                    transaction = (Transaction) dataObject;
                    DataProvider.deleteTransaction(transaction);
                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    id = dataReceiver[0][0].getInt("generalCategoryID");
                    generalCategory = (GeneralCategory) dataObject;
                    generalCategory.setId(id);
                    DataProvider.addGeneralCategory(generalCategory);
                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) dataObject;
                    DataProvider.updateGeneralCategory(generalCategory);
                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) dataObject;
                    DataProvider.deleteGeneralCategory(generalCategory);
                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    id = dataReceiver[0][0].getInt("subCategoryID");
                    subCategory = (SubCategory) dataObject;
                    subCategory.setId(id);
                    DataProvider.addSubCategory(subCategory);
                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    subCategory = (SubCategory) dataObject;
                    DataProvider.updateSubCategory(subCategory);
                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    subCategory = (SubCategory) dataObject;
                    DataProvider.deleteSubCategory(subCategory);
                    return true;

                case ApiService.UPDATE_EMAIL:

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, username);
                    return true;

//                case ApiService.UPDATE_LOGIN_PASSWORD:
//
//                    refreshToken = dataReceiver[0][0].getString("refreshToken");
//                    accessToken = dataReceiver[0][0].getJSONObject("accessToken").getString("token");
//
//                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_KEY, refreshToken);
//                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, accessToken);
//
//                    return true;

                case ApiService.GET_KEY_PACKAGE:

                    JSONObject json = dataReceiver[0][0].getJSONObject("keyPackage");
                    DataProvider.setKeyPackage(new KeyPackage(json));
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

}
