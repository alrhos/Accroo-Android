package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonRequest;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.database.DataAccess;
import io.accroo.android.model.Account;
import io.accroo.android.model.DefaultGeneralCategory;
import io.accroo.android.model.DefaultSubCategory;
import io.accroo.android.model.EncryptedDefaultGeneralCategory;
import io.accroo.android.model.EncryptedPreferences;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.Key;
import io.accroo.android.model.Preferences;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.User;
import io.accroo.android.network.RequestBuilder;
import io.accroo.android.network.RequestCoordinator;
import io.accroo.android.other.GsonUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by oscar on 4/07/17.
 */

public class PreRequestTask extends AsyncTask<Void, Boolean, Boolean> {

    private ArrayList<JsonRequest> requests;
    private HashMap<String, Object> requestVariables;
    private String uri;
    private int requestType;
    private String userId;
    private String accessToken;
    private JSONObject json;
    // TODO: rename this to json after removing JSONObject
    private String jsonString;
    private Account account;
    private Preferences preferences;
    private EncryptedPreferences encryptedPreferences;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private SubCategory subCategory;
    private PreRequestOutcome preRequestOutcome;
    private Context context;
    private RequestCoordinator coordinator;
    private String username, newEmail;
    private String loginCode;
    private char[] password;
    private char[] newPassword;

    public PreRequestTask(int requestType, PreRequestOutcome preRequestOutcome, Context context,
                          RequestCoordinator coordinator, HashMap<String, Object> requestVariables) {

        this.requestType = requestType;
        this.preRequestOutcome = preRequestOutcome;
        this.context = context;
        this.coordinator = coordinator;
        this.requestVariables = requestVariables;
        requests = new ArrayList<>();
    }

    public interface PreRequestOutcome {
        void onPreRequestTaskSuccess(JsonRequest... requests);
        void onPreRequestTaskFailure();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            switch (requestType) {

                case ApiService.GET_VERIFICATION_CODE:

                    json = new JSONObject();
                    json.put("email", requestVariables.get("username"));

                    requests.add(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.VERIFICATION_TOKEN, json));

                    return true;

                case ApiService.GET_DEVICE_TOKEN:

//                    username = (String) requestVariables.get("username");
//                    loginCode = (String) requestVariables.get("loginCode");
//
//                    requests.add(RequestBuilder.basicAuth(0, coordinator, Request.Method.POST,
//                            null, RequestBuilder.DEVICE_TOKEN, username, loginCode));

                    return true;

                case ApiService.GET_DEFAULT_DATA:

//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.GET,
//                            RequestBuilder.CATEGORIES, null, context));
//                    requests.add(RequestBuilder.deviceTokenAuth(1, coordinator, Request.Method.GET,
//                            RequestBuilder.TRANSACTIONS, null, context));

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.getGeneralCategories(0, coordinator, userId, accessToken));
                    requests.add(RequestBuilder.getSubCategories(1, coordinator, userId, accessToken));
                    requests.add(RequestBuilder.getTransactions(2, coordinator, userId, accessToken));

                    return true;

                case ApiService.CREATE_USER:

                    User user = (User) requestVariables.get("user");

                    Key key = CryptoManager.getInstance().generateNewKey(user.getPassword());
                    user.setKey(key);

                    requests.add(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.ACCOUNT, user.toJSON()));

                    return true;

                case ApiService.CREATE_ACCOUNT:

                    account = (Account) requestVariables.get("account");
                    requests.add(RequestBuilder.postAccount(0, coordinator,
                            GsonUtil.getInstance().toJson(account)));

//                    requests.add(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
//                            RequestBuilder.ACCOUNT, GsonUtil.getInstance().toJson(account)));

                    return true;

                case ApiService.LOGIN:

                    account = (Account) requestVariables.get("account");
                    requests.add(RequestBuilder.postRefreshToken(0, coordinator,
                            account.getEmail(), account.getVerificationToken()));

//                    requests.add(RequestBuilder.basicAuth(0, coordinator, Request.Method.POST,
//                            null, RequestBuilder.REFRESH_TOKEN, account.getEmail(),
//                            account.getVerificationToken()));

                    return true;

                case ApiService.CREATE_KEY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    password = (char[]) requestVariables.get("password");
                    key = CryptoManager.getInstance().generateNewKey(password);
                    CryptoManager.getInstance().saveMasterKey(context);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.putKey(0, coordinator,
                            GsonUtil.getInstance().toJson(key), userId, accessToken));

//                    jsonString = GsonUtil.getInstance().toJson(key);
//                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator,
//                            Request.Method.PUT, RequestBuilder.ENCRYPTION_KEY, userId,
//                            jsonString, context));

                    return true;

//                case ApiService.UPDATE_KEY:
//
//                    password = (char[]) requestVariables.get("password");
//
//                    return true;

                case ApiService.UPDATE_PREFERENCES:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    preferences = (Preferences) requestVariables.get("preferences");
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.putPreferences(0, coordinator,
                            GsonUtil.getInstance().toJson(preferences.encrypt()), userId, accessToken));

                    return true;

                case ApiService.CREATE_DEFAULT_CATEGORIES:

                    ArrayList<DefaultGeneralCategory> generalCategories = DataAccess.getInstance(context).getDefaultGeneralCategories();
                    ArrayList<DefaultSubCategory> subCategories = DataAccess.getInstance(context).getDefaultSubCategories();

                    // Shuffle items so that each user's categories are inserted in a random
                    // order making it more difficult for sysadmins to guess a certain category
                    // given the cipher text length or order of records in db table.

                    Collections.shuffle(generalCategories);
                    Collections.shuffle(subCategories);

                    for (DefaultSubCategory subCategory : subCategories) {
                        for (DefaultGeneralCategory generalCategory : generalCategories) {
                            if (subCategory.getGeneralCategoryName().equals(generalCategory.getCategoryName())) {
                                generalCategory.getSubCategories().add(subCategory);
                                break;
                            }
                        }
                    }

                    ArrayList<EncryptedDefaultGeneralCategory> encryptedCategories = new ArrayList<>();
                    for (DefaultGeneralCategory category: generalCategories) {
                        encryptedCategories.add(category.encrypt());
                    }

                    jsonString = GsonUtil.getInstance().toJson(encryptedCategories);
                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.postDefaultCategories(0, coordinator, jsonString,
                            userId, accessToken));
//                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.POST,
//                            RequestBuilder.CATEGORIES, userId, jsonString, context));

//                    JSONArray generalCategoriesArray = new JSONArray();
//
//                    for (DefaultGeneralCategory generalCategory : generalCategories) {
//                        JSONArray subCategoriesArray = new JSONArray();
//                        for (SubCategory subCategory : generalCategory.getSubCategories()) {
//                            subCategoriesArray.put(subCategory.encrypt());
//                        }
//
//                        JSONObject category = generalCategory.encrypt();
//                        category.put("subCategories", subCategoriesArray);
//                        generalCategoriesArray.put(category);
//                    }
//
//                    JSONObject categories = new JSONObject();
//                    categories.put("categories", generalCategoriesArray);
//
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
//                            RequestBuilder.CATEGORY, categories, context));

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    System.out.println(transaction.toString());
                    System.out.println(GsonUtil.getInstance().toJson(transaction));
                    jsonString = GsonUtil.getInstance().toJson(transaction.encrypt());
                    requests.add(RequestBuilder.postTransaction(0, coordinator,
                            userId, jsonString, accessToken));

//                    json = ((Transaction) requestVariables.get("transaction")).encrypt();
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
//                            RequestBuilder.TRANSACTIONS, json, context));

                    return true;

                case ApiService.UPDATE_TRANSACTION:

//                    transaction = (Transaction) requestVariables.get("transaction");
//                    json = transaction.encrypt();
//                    uri = RequestBuilder.TRANSACTIONS + "/" + transaction.getId();
//
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.PUT,
//                            uri, json, context));

                    return true;

                case ApiService.DELETE_TRANSACTION:

                    transaction = (Transaction) requestVariables.get("transaction");
                    uri = RequestBuilder.TRANSACTIONS + "/" + transaction.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, context));

                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

//                    json = ((GeneralCategory) requestVariables.get("generalCategory")).encrypt();
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
//                            RequestBuilder.GENERAL_CATEGORIES, json, context));

                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

//                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
//                    json = generalCategory.encrypt();
//                    uri = RequestBuilder.GENERAL_CATEGORIES + "/" + generalCategory.getId();
//
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.PUT,
//                            uri, json, context));

                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    uri = RequestBuilder.GENERAL_CATEGORIES + "/" + generalCategory.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, context));

                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

//                    json = ((SubCategory) requestVariables.get("subCategory")).encrypt();
//
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
//                            RequestBuilder.SUB_CATEGORIES, json, context));

                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

//                    subCategory = (SubCategory) requestVariables.get("subCategory");
//                    json = subCategory.encrypt();
//                    uri = RequestBuilder.SUB_CATEGORIES + "/" + subCategory.getId();
//
//                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.PUT,
//                            uri, json, context));

                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    uri = RequestBuilder.SUB_CATEGORIES + "/" + subCategory.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, context));

                    return true;

                case ApiService.UPDATE_EMAIL:

                    newEmail = (String) requestVariables.get("newEmail");
                    loginCode = (String) requestVariables.get("loginCode");
                    username = CredentialService.getInstance(context).getEntry(CredentialService.USERNAME_KEY);
                    json = new JSONObject();
                    json.put("email", newEmail);

                    requests.add(RequestBuilder.basicAuth(0, coordinator, Request.Method.PUT,
                            json, RequestBuilder.EMAIL, username, loginCode));

                    return true;

                case ApiService.GET_KEY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.getKey(0, coordinator, userId, accessToken));

                    return true;

//                case ApiService.UPDATE_PASSWORD:
//
//                    loginCode = (String) requestVariables.get("loginCode");
//                    newPassword = (char[]) requestVariables.get("newPassword");
//                    username = CredentialService.getInstance(context).getEntry(CredentialService.USERNAME_KEY);
//                    Key newKey = CryptoManager.getInstance().encryptMasterKey(newPassword, context);
//
//                    requests.add(RequestBuilder.basicAuth(0, coordinator, Request.Method.PUT,
//                            newKey.toJSON(), RequestBuilder.ENCRYPTION_KEY, username, loginCode));
//
//                    return true;

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
            preRequestOutcome.onPreRequestTaskSuccess(requests.toArray(new JsonRequest[requests.size()]));
        } else {
            preRequestOutcome.onPreRequestTaskFailure();
        }
    }
}
