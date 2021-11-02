package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.toolbox.JsonRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.database.DataAccess;
import io.accroo.android.model.AuthCredentials;
import io.accroo.android.model.DefaultGeneralCategory;
import io.accroo.android.model.DefaultSubCategory;
import io.accroo.android.model.EncryptedDefaultGeneralCategory;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.Key;
import io.accroo.android.model.Preferences;
import io.accroo.android.model.SessionData;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.network.RequestBuilder;
import io.accroo.android.network.RequestCoordinator;
import io.accroo.android.other.GsonUtil;

/**
 * Created by oscar on 4/07/17.
 */

public class PreRequestTask extends AsyncTask<Void, Boolean, Boolean> {

    private ArrayList<JsonRequest> requests;
    private HashMap<String, Object> requestVariables;
    private int requestType;
    private String userId;
    private String sessionId;
    private String accessToken;
    private String json;
    private AuthCredentials authCredentials;
    private SessionData sessionData;
    private JSONObject emailJson;
    private String accountJson;
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

                case ApiService.GET_VISITOR_TOKEN:

                    String recaptchaToken = (String) requestVariables.get("recaptchaToken");
                    requests.add(RequestBuilder.postVisitorToken(0, coordinator, recaptchaToken));

                    return true;

                case ApiService.CHECK_EMAIL_AVAILABILITY:

                    String email = (String) requestVariables.get("email");
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.headEmail(0, coordinator, accessToken, email));

                    return true;

                case ApiService.GET_VERIFICATION_CODE:

                    username = (String) requestVariables.get("username");
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    emailJson = new JSONObject();
                    emailJson.put("email", username);
                    requests.add(RequestBuilder.postVerificationToken(0, coordinator, accessToken, emailJson));

                    return true;

                case ApiService.LOAD_DEFAULT_DATA:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    sessionId = CredentialService.getInstance(context).getEntry(CredentialService.SESSION_ID_KEY);
                    sessionData = (SessionData) requestVariables.get("sessionData");
                    json = GsonUtil.getInstance().toJson(sessionData.encrypt());
                    requests.add(RequestBuilder.putSession(0, coordinator, sessionId, accessToken, json));
                    requests.add(RequestBuilder.getGeneralCategories(1, coordinator, accessToken));
                    requests.add(RequestBuilder.getSubCategories(2, coordinator, accessToken));
                    requests.add(RequestBuilder.getTransactions(3, coordinator, accessToken));

                    return true;

                case ApiService.CREATE_ACCOUNT:

                    authCredentials = (AuthCredentials) requestVariables.get("authCredentials");
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.postAccount(0, coordinator,
                            GsonUtil.getInstance().toJson(authCredentials), accessToken));

                    return true;

                case ApiService.CREATE_SESSION:

                    authCredentials = (AuthCredentials) requestVariables.get("authCredentials");
                    accountJson = GsonUtil.getInstance().toJson(authCredentials);
                    requests.add(RequestBuilder.postSession(0, coordinator, accountJson));

                    return true;

                case ApiService.REAUTHENTICATE_SESSION:

                    sessionId = CredentialService.getInstance(context).getEntry(CredentialService.SESSION_ID_KEY);
                    authCredentials = (AuthCredentials) requestVariables.get("authCredentials");
                    accountJson = GsonUtil.getInstance().toJson(authCredentials);
                    requests.add(RequestBuilder.postSessionAuthentication(0, coordinator, sessionId, accountJson));

                    return true;

                case ApiService.INVALIDATE_CURRENT_SESSION:

                    sessionId = (String) requestVariables.get("sessionId");
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.postSessionInvalidation(0, coordinator, sessionId, accessToken));

                    return true;

                case ApiService.INVALIDATE_SESSION:

                    sessionData = (SessionData) requestVariables.get("sessionData");
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.postSessionInvalidation(0, coordinator, sessionData.getId().toString(), accessToken));

                    return true;

                case ApiService.GET_SESSIONS:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.getSessions(0, coordinator, accessToken));

                    return true;

                case ApiService.INITIALIZE_ACCOUNT_DATA:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);

                    // Generate and save data encryption key details
                    password = (char[]) requestVariables.get("password");
                    key = CryptoManager.getInstance().generateNewKey(password);
                    CryptoManager.getInstance().saveMasterKey(context);
                    requests.add(RequestBuilder.putKey(0, coordinator,
                            GsonUtil.getInstance().toJson(key), userId, accessToken));

                    // Generate and save default preferences
                    preferences = (Preferences) requestVariables.get("preferences");
                    requests.add(RequestBuilder.putPreferences(1, coordinator,
                            GsonUtil.getInstance().toJson(preferences.encrypt()), userId, accessToken));

                    // Generate and save default categories
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
                    requests.add(RequestBuilder.postDefaultCategories(2, coordinator, json, accessToken));

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    json = GsonUtil.getInstance().toJson(transaction.encrypt());
                    requests.add(RequestBuilder.postTransaction(0, coordinator, json, accessToken));

                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    transactionId = transaction.getId().toString();
                    json = GsonUtil.getInstance().toJson(transaction.encrypt());
                    requests.add(RequestBuilder.putTransaction(0, coordinator, transactionId, json, accessToken));

                    return true;

                case ApiService.DELETE_TRANSACTION:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    transaction = (Transaction) requestVariables.get("transaction");
                    transactionId = transaction.getId().toString();
                    requests.add(RequestBuilder.deleteTransaction(0, coordinator, transactionId, accessToken));

                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    json = GsonUtil.getInstance().toJson(generalCategory.encrypt());
                    requests.add(RequestBuilder.postGeneralCategory(0, coordinator, json, accessToken));

                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    generalCategoryId = generalCategory.getId().toString();
                    json = GsonUtil.getInstance().toJson(generalCategory.encrypt());
                    requests.add(RequestBuilder.putGeneralCategory(0, coordinator, generalCategoryId, json, accessToken));

                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    generalCategoryId = generalCategory.getId().toString();
                    requests.add(RequestBuilder.deleteGeneralCategory(0, coordinator, generalCategoryId, accessToken));

                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    json = GsonUtil.getInstance().toJson(subCategory.encrypt());
                    requests.add(RequestBuilder.postSubCategory(0, coordinator, json, accessToken));

                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    subCategoryId = subCategory.getId().toString();
                    json = GsonUtil.getInstance().toJson(subCategory.encrypt());
                    requests.add(RequestBuilder.putSubCategory(0, coordinator, subCategoryId, json, accessToken));

                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    subCategoryId = subCategory.getId().toString();
                    requests.add(RequestBuilder.deleteSubCategory(0, coordinator, subCategoryId, accessToken));

                    return true;

                case ApiService.GET_KEY:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);
                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    requests.add(RequestBuilder.getKey(0, coordinator, userId, accessToken));

                    return true;

                case ApiService.UPDATE_EMAIL:

                    userId = CredentialService.getInstance(context).getEntry(CredentialService.USER_ID_KEY);

                    accessToken = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_KEY);
                    newEmail = (String) requestVariables.get("newEmail");
                    emailJson = new JSONObject();
                    emailJson.put("email", newEmail);
                    requests.add(RequestBuilder.putEmail(0, coordinator, userId, accessToken, emailJson));

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
