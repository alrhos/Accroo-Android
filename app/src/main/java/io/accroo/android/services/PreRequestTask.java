package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.toolbox.JsonRequest;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.database.DataAccess;
import io.accroo.android.model.Account;
import io.accroo.android.model.DefaultGeneralCategory;
import io.accroo.android.model.DefaultSubCategory;
import io.accroo.android.model.EncryptedDefaultGeneralCategory;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.Key;
import io.accroo.android.model.Preferences;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
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
    private int requestType;
    private String userId;
    private String accessToken;
    private String json;
    private Account account;
    private JSONObject emailJson;
    private Preferences preferences;
    private Key key;
    private Transaction transaction;
    private String transactionId;
    private GeneralCategory generalCategory;
    private String generalCategoryId;
    private SubCategory subCategory;
    private String subCategoryId;
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

                    username = (String) requestVariables.get("username");
                    emailJson = new JSONObject();
                    emailJson.put("email", username);
                    requests.add(RequestBuilder.postVerificationToken(0, coordinator, emailJson));

                    return true;

                case ApiService.GET_DEFAULT_DATA:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.getGeneralCategories(0, coordinator, userId, accessToken));
                    requests.add(RequestBuilder.getSubCategories(1, coordinator, userId, accessToken));
                    requests.add(RequestBuilder.getTransactions(2, coordinator, userId, accessToken));

                    return true;

                case ApiService.CREATE_ACCOUNT:

                    account = (Account) requestVariables.get("account");
                    String recaptchaToken = (String) requestVariables.get("recaptchaToken");
                    requests.add(RequestBuilder.postAccount(0, coordinator,
                            GsonUtil.getInstance().toJson(account), recaptchaToken));

                    return true;

                case ApiService.LOGIN:

                    account = (Account) requestVariables.get("account");
                    requests.add(RequestBuilder.postRefreshToken(0, coordinator,
                            account.getEmail(), account.getVerificationToken()));

                    return true;

                case ApiService.REAUTHENTICATE:

                    loginCode = (String) requestVariables.get("loginCode");
                    username = CredentialService.getInstance(context).getEntry(CredentialService.USERNAME_KEY);
                    requests.add(RequestBuilder.postRefreshToken(0, coordinator,
                            username, loginCode));

                    return true;

                case ApiService.CREATE_KEY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    password = (char[]) requestVariables.get("password");
                    key = CryptoManager.getInstance().generateNewKey(password);
                    CryptoManager.getInstance().saveMasterKey(context);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.putKey(0, coordinator,
                            GsonUtil.getInstance().toJson(key), userId, accessToken));

                    return true;

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

                    json = GsonUtil.getInstance().toJson(encryptedCategories);
                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.postDefaultCategories(0, coordinator, json,
                            userId, accessToken));

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    json = GsonUtil.getInstance().toJson(transaction.encrypt());
                    requests.add(RequestBuilder.postTransaction(0, coordinator,
                            userId, json, accessToken));

                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    transactionId = String.valueOf(transaction.getId());
                    json = GsonUtil.getInstance().toJson(transaction.encrypt());
                    requests.add(RequestBuilder.putTransaction(0, coordinator,
                            userId, transactionId, json, accessToken));

                    return true;

                case ApiService.DELETE_TRANSACTION:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    transactionId = String.valueOf(transaction.getId());
                    requests.add(RequestBuilder.deleteTransaction(0, coordinator, userId,
                            transactionId, accessToken));

                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    json = GsonUtil.getInstance().toJson(generalCategory.encrypt());
                    requests.add(RequestBuilder.postGeneralCategory(0, coordinator,
                            userId, json, accessToken));

                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    generalCategoryId = String.valueOf(generalCategory.getId());
                    json = GsonUtil.getInstance().toJson(generalCategory.encrypt());
                    requests.add(RequestBuilder.putGeneralCategory(0, coordinator,
                            userId, generalCategoryId, json, accessToken));

                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    generalCategoryId = String.valueOf(generalCategory.getId());
                    requests.add(RequestBuilder.deleteGeneralCategory(0, coordinator, userId,
                            generalCategoryId, accessToken));

                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    json = GsonUtil.getInstance().toJson(subCategory.encrypt());
                    requests.add(RequestBuilder.postSubCategory(0, coordinator,
                            userId, json, accessToken));

                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    subCategoryId = String.valueOf(subCategory.getId());
                    json = GsonUtil.getInstance().toJson(subCategory.encrypt());
                    requests.add(RequestBuilder.putSubCategory(0, coordinator,
                            userId, subCategoryId, json, accessToken));

                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    subCategoryId = String.valueOf(subCategory.getId());
                    requests.add(RequestBuilder.deleteSubCategory(0, coordinator, userId,
                            subCategoryId, accessToken));

                    return true;

                case ApiService.GET_KEY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.getKey(0, coordinator, userId, accessToken));

                    return true;

                case ApiService.UPDATE_EMAIL:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    newEmail = (String) requestVariables.get("newEmail");
                    emailJson = new JSONObject();
                    emailJson.put("email", newEmail);
                    requests.add(RequestBuilder.putEmail(0, coordinator, accessToken, emailJson));

                    return true;

                case ApiService.UPDATE_PASSWORD:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    newPassword = (char[]) requestVariables.get("newPassword");
                    Key newKey = CryptoManager.getInstance().encryptMasterKey(newPassword, context);
                    requests.add(RequestBuilder.putKey(0, coordinator,
                            GsonUtil.getInstance().toJson(newKey), userId, accessToken));

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
            preRequestOutcome.onPreRequestTaskSuccess(requests.toArray(new JsonRequest[requests.size()]));
        } else {
            preRequestOutcome.onPreRequestTaskFailure();
        }
    }
}
