package io.accroo.android.services;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.database.DataAccess;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.KeyPackage;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.User;
import io.accroo.android.network.RequestBuilder;
import io.accroo.android.network.RequestCoordinator;
import io.accroo.android.network.RestRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by oscar on 4/07/17.
 */

public class PreRequestTask extends AsyncTask<Void, Boolean, Boolean> {

    private ArrayList<RestRequest> requests;
    private HashMap<String, Object> requestVariables;
    private String uri;
    private int requestType;
    private JSONObject json;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private SubCategory subCategory;
    private PreRequestOutcome preRequestOutcome;
    private Context context;
    private RequestCoordinator coordinator;
    private String username, newEmail;
    private String loginCode;
    private char[] newPassword;

    public PreRequestTask(int requestType, PreRequestOutcome preRequestOutcome, Context context,
                          RequestCoordinator coordinator, HashMap<String, Object> requestVariables) {

        this.requestType = requestType;
        this.preRequestOutcome = preRequestOutcome;
        this.context = context;
        this.coordinator = coordinator;
        requests = new ArrayList<>();
        this.requestVariables = requestVariables;
    }

    public interface PreRequestOutcome {
        void onPreRequestTaskSuccess(RestRequest... requests);
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

                    username = (String) requestVariables.get("username");
                    loginCode = (String) requestVariables.get("loginCode");

                    requests.add(RequestBuilder.basicAuth(0, coordinator, Request.Method.POST,
                            null, RequestBuilder.DEVICE_TOKEN, username, loginCode));

                    return true;

                case ApiService.GET_DEFAULT_DATA:

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.GET,
                            RequestBuilder.CATEGORY, null, context));
                    requests.add(RequestBuilder.deviceTokenAuth(1, coordinator, Request.Method.GET,
                            RequestBuilder.TRANSACTION, null, context));

                    return true;

                case ApiService.CREATE_USER:

                    User user = (User) requestVariables.get("user");

                    KeyPackage keyPackage = CryptoManager.getInstance().generateNewKey(user.getPassword());
                    user.setKeyPackage(keyPackage);

                    requests.add(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.REGISTER, user.toJSON()));

                    return true;

                case ApiService.CREATE_DEFAULT_CATEGORIES:

                    ArrayList<GeneralCategory> generalCategories = DataAccess.getInstance(context).getGeneralCategories();
                    ArrayList<SubCategory> subCategories = DataAccess.getInstance(context).getSubCategories();

                    // Shuffle items so that each user's categories are inserted in a different
                    // order making it more difficult for sysadmins to guess a certain category
                    // given the cipher text length or order of records in db table.

                    Collections.shuffle(generalCategories);
                    Collections.shuffle(subCategories);

                    for (SubCategory subCategory : subCategories) {
                        for (GeneralCategory generalCategory : generalCategories) {
                            if (subCategory.getGeneralCategoryName().equals(generalCategory.getCategoryName())) {
                                generalCategory.getSubCategories().add(subCategory);
                                break;
                            }
                        }
                    }

                    JSONArray generalCategoriesArray = new JSONArray();

                    for (GeneralCategory generalCategory : generalCategories) {
                        JSONArray subCategoriesArray = new JSONArray();
                        for (SubCategory subCategory : generalCategory.getSubCategories()) {
                            subCategoriesArray.put(subCategory.encrypt());
                        }

                        JSONObject category = generalCategory.encrypt();
                        category.put("subCategories", subCategoriesArray);
                        generalCategoriesArray.put(category);
                    }

                    JSONObject categories = new JSONObject();
                    categories.put("categories", generalCategoriesArray);

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.CATEGORY, categories, context));

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    json = ((Transaction) requestVariables.get("transaction")).encrypt();
                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.TRANSACTION, json, context));

                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    transaction = (Transaction) requestVariables.get("transaction");
                    json = transaction.encrypt();
                    uri = RequestBuilder.TRANSACTION + "/" + transaction.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.PUT,
                            uri, json, context));

                    return true;

                case ApiService.DELETE_TRANSACTION:

                    transaction = (Transaction) requestVariables.get("transaction");
                    uri = RequestBuilder.TRANSACTION + "/" + transaction.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, context));

                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    json = ((GeneralCategory) requestVariables.get("generalCategory")).encrypt();
                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.GENERAL_CATEGORY, json, context));

                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    json = generalCategory.encrypt();
                    uri = RequestBuilder.GENERAL_CATEGORY + "/" + generalCategory.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.PUT,
                            uri, json, context));

                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) requestVariables.get("generalCategory");
                    uri = RequestBuilder.GENERAL_CATEGORY + "/" + generalCategory.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, context));

                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    json = ((SubCategory) requestVariables.get("subCategory")).encrypt();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.SUB_CATEGORY, json, context));

                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    json = subCategory.encrypt();
                    uri = RequestBuilder.SUB_CATEGORY + "/" + subCategory.getId();

                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.PUT,
                            uri, json, context));

                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    subCategory = (SubCategory) requestVariables.get("subCategory");
                    uri = RequestBuilder.SUB_CATEGORY + "/" + subCategory.getId();

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

                case ApiService.GET_KEY_PACKAGE:

                    username = CredentialService.getInstance(context).getEntry(CredentialService.USERNAME_KEY);
                    requests.add(RequestBuilder.deviceTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.ENCRYPTION_KEY, null, context));

                    return true;

                case ApiService.UPDATE_PASSWORD:

                    loginCode = (String) requestVariables.get("loginCode");
                    newPassword = (char[]) requestVariables.get("newPassword");
                    username = CredentialService.getInstance(context).getEntry(CredentialService.USERNAME_KEY);
                    KeyPackage newKeyPackage = CryptoManager.getInstance().encryptMasterKey(newPassword, context);

                    requests.add(RequestBuilder.basicAuth(0, coordinator, Request.Method.PUT,
                            newKeyPackage.toJSON(), RequestBuilder.ENCRYPTION_KEY, username, loginCode));

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
            preRequestOutcome.onPreRequestTaskSuccess(requests.toArray(new RestRequest[requests.size()]));
        } else {
            preRequestOutcome.onPreRequestTaskFailure();
        }
    }
}
