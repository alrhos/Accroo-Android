package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.model.Account;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.KeyPackage;
import io.accroo.android.model.LoginSession;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.User;
import io.accroo.android.other.GsonUtil;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by oscar on 4/07/17.
 */

public class PostRequestTask extends AsyncTask<JSONObject[], Boolean, Boolean> {

    private PostRequestOutcome postRequestOutcome;
    private Context context;
    private Date startDate, endDate;
    private Account account;
    private LoginSession loginSession;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private SubCategory subCategory;
    private int id, requestType;
    private String username, newEmail, deviceToken;
    private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
    private ArrayList<SubCategory> subCategories = new ArrayList<>();
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private HashMap<String, Object> requestVariables;

    public PostRequestTask(int requestType, PostRequestOutcome postRequestOutcome,
                           Context context, HashMap<String, Object> requestVariables) {

        this.requestType = requestType;
        this.postRequestOutcome = postRequestOutcome;
        this.context = context;
        this.requestVariables = requestVariables;

    }

    public interface PostRequestOutcome {
        void onPostRequestTaskSuccess(int requestType);
        void onPostRequestTaskFailure();
    }

    @Override
    protected Boolean doInBackground(JSONObject[]... dataReceiver) {
        try {
            switch (requestType) {

                case ApiService.GET_DEVICE_TOKEN:

                    username = (String) requestVariables.get("username");
                    deviceToken = dataReceiver[0][0].getString("deviceToken");

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, username);
                    CredentialService.getInstance(context).saveEntry(CredentialService.DEVICE_TOKEN_KEY, deviceToken);

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

                    username = ((User) requestVariables.get("user")).getEmail();
                    deviceToken = dataReceiver[0][0].getString("deviceToken");

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, username);
                    CredentialService.getInstance(context).saveEntry(CredentialService.DEVICE_TOKEN_KEY, deviceToken);
                    CryptoManager.getInstance().saveMasterKey(context);
                    return true;

                case ApiService.LOGIN:

                    account = (Account) requestVariables.get("account");
                    loginSession = (LoginSession) GsonUtil.getInstance().fromJson(dataReceiver[0][0],
                            LoginSession.class);

                    DateTime refreshTokenExpiry = new DateTime(loginSession.getRefreshToken().getExpiresAt());
                    DateTime accessTokenExpiry = new DateTime(loginSession.getAccessToken().getExpiresAt());

                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, account.getEmail());
                    CredentialService.getInstance(context).saveEntry(CredentialService.USER_ID_KEY, loginSession.getUserId());
                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_KEY, loginSession.getRefreshToken().getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.REFRESH_TOKEN_EXPIRY_KEY, refreshTokenExpiry.toString());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, loginSession.getAccessToken().getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, accessTokenExpiry.toString());

                    return true;

                case ApiService.GET_DEFAULT_DATA:

                    JSONArray generalCategoriesArray = dataReceiver[0][0].getJSONArray("categories");

                    startDate = (Date) requestVariables.get("startDate");
                    endDate = (Date) requestVariables.get("endDate");

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
                    transaction = (Transaction) requestVariables.get("transaction");
                    transaction.setId(id);
                    DataProvider.addTransaction(transaction);
                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    transaction = (Transaction) requestVariables.get("transaction");
                    DataProvider.updateTransaction(transaction);
                    return true;

                case ApiService.DELETE_TRANSACTION:

                    transaction = (Transaction) requestVariables.get("transaction");
                    DataProvider.deleteTransaction(transaction);
                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    id = dataReceiver[0][0].getInt("generalCategoryID");
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    generalCategory.setId(id);
                    DataProvider.addGeneralCategory(generalCategory);
                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    DataProvider.updateGeneralCategory(generalCategory);
                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    DataProvider.deleteGeneralCategory(generalCategory);
                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    id = dataReceiver[0][0].getInt("subCategoryID");
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    subCategory.setId(id);
                    DataProvider.addSubCategory(subCategory);
                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    DataProvider.updateSubCategory(subCategory);
                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    DataProvider.deleteSubCategory(subCategory);
                    return true;

                case ApiService.UPDATE_EMAIL:

                    newEmail = (String) requestVariables.get("newEmail");
                    CredentialService.getInstance(context).saveEntry(CredentialService.USERNAME_KEY, newEmail);
                    return true;

                case ApiService.GET_KEY_PACKAGE:

                    JSONObject json = dataReceiver[0][0].getJSONObject("keyPackage");
                    DataProvider.setKeyPackage(new KeyPackage(json));
                    return true;

                case ApiService.UPDATE_PASSWORD:

                    deviceToken = dataReceiver[0][0].getString("deviceToken");
                    CredentialService.getInstance(context).saveEntry(CredentialService.DEVICE_TOKEN_KEY, deviceToken);
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
