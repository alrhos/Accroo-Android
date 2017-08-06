package com.paleskyline.navicash.services;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.paleskyline.navicash.database.DataAccess;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestBuilder;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by oscar on 4/07/17.
 */

public class PreRequestTask extends AsyncTask<Void, Boolean, Boolean> {

    private ArrayList<RestRequest> requests;
    private String startDate;
    private String endDate;
    private String uri;
    private int requestType;
    private JSONObject json;
    private Transaction transaction;
    private GeneralCategory generalCategory;
    private SubCategory subCategory;

    private PreRequestOutcome preRequestOutcome;
    private Context context;
    private RequestCoordinator coordinator;

    private Object dataObject;

    private String username;
    private char[] password;

    // TODO: also needs to take in model object to encrypt

    public PreRequestTask(int requestType, PreRequestOutcome preRequestOutcome, Context context,
                          RequestCoordinator coordinator) {

        this.requestType = requestType;
        this.preRequestOutcome = preRequestOutcome;
        this.context = context;
        this.coordinator = coordinator;
        requests = new ArrayList<>();

    }

    public PreRequestTask(int requestType, PreRequestOutcome preRequestOutcome, Context context,
                          RequestCoordinator coordinator, String username, char[] password) {

        this.requestType = requestType;
        this.preRequestOutcome = preRequestOutcome;
        this.context = context;
        this.coordinator = coordinator;
        this.username = username;
        this.password = password;
        requests = new ArrayList<>();

    }

    public PreRequestTask(int requestType, PreRequestOutcome preRequestOutcome, Context context,
                          RequestCoordinator coordinator, Object dataObject) {

        this.requestType = requestType;
        this.preRequestOutcome = preRequestOutcome;
        this.context = context;
        this.coordinator = coordinator;
        this.dataObject = dataObject;
        requests = new ArrayList<>();

    }

    public interface PreRequestOutcome {
        void onPreRequestTaskFailure();
        void onPreRequestTaskSuccess(RestRequest... requests);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            switch (requestType) {

                case ApiService.GET_REFRESH_TOKEN:

                    requests.add(RequestBuilder.basicAuth(0, coordinator,
                            RequestBuilder.REFRESH_TOKEN, username, password, context));

                    return true;

                case ApiService.GET_DEFAULT_DATA:

                    // TODO - Look up transaction id for date

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.GET,
                            RequestBuilder.CATEGORY, null, null, context));
                    requests.add(RequestBuilder.accessTokenAuth(1, coordinator, Request.Method.GET,
                            RequestBuilder.TRANSACTION, null, null, context));

                    return true;

                case ApiService.CREATE_USER:

                    User user = (User) dataObject;

                    requests.add(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.USER, user.toJSON(), context));

                    return true;

                case ApiService.CREATE_DEFAULT_CATEGORIES:

                    ArrayList<GeneralCategory> generalCategories = DataAccess.getInstance(context).getGeneralCategories();
                    ArrayList<SubCategory> subCategories = DataAccess.getInstance(context).getSubCategories();

                    for (SubCategory subCategory : subCategories) {
                        for (GeneralCategory generalCategory : generalCategories) {
                            if (subCategory.getGeneralCategoryName().equals(generalCategory.getCategoryName())) {
                                generalCategory.getSubCategories().add(subCategory);
                                break;
                            }
                        }
                    }

                    // Shuffle items so that each user's categories are inserted in a different
                    // order making it difficult for sysadmins to guess a certain category given
                    // the cipher text length.

                    Collections.shuffle(generalCategories);

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

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.CATEGORY, null, categories, context));

                    return true;

                case ApiService.CREATE_TRANSACTION:

                    json = ((Transaction) dataObject).encrypt();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.TRANSACTION, null, json, context));

                    return true;

                case ApiService.UPDATE_TRANSACTION:

                    transaction = (Transaction) dataObject;

                    json = transaction.encrypt();

                    uri = RequestBuilder.TRANSACTION + "/" + transaction.getId();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.PUT,
                            uri, null, json, context));

                    return true;

                case ApiService.DELETE_TRANSACTION:

                    transaction = (Transaction) dataObject;

                    uri = RequestBuilder.TRANSACTION + "/" + transaction.getId();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, null, context));

                    return true;

                case ApiService.CREATE_GENERAL_CATEGORY:

                    json = ((GeneralCategory) dataObject).encrypt();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.GENERAL_CATEGORY, null, json, context));

                    return true;

                case ApiService.UPDATE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) dataObject;

                    json = generalCategory.encrypt();

                    uri = RequestBuilder.GENERAL_CATEGORY + "/" + generalCategory.getId();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.PUT,
                            uri, null, json, context));

                    return true;

                case ApiService.DELETE_GENERAL_CATEGORY:

                    generalCategory = (GeneralCategory) dataObject;

                    uri = RequestBuilder.GENERAL_CATEGORY + "/" + generalCategory.getId();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, null, context));

                    return true;

                case ApiService.CREATE_SUB_CATEGORY:

                    json = ((SubCategory) dataObject).encrypt();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.SUB_CATEGORY, null, json, context));

                    return true;

                case ApiService.UPDATE_SUB_CATEGORY:

                    subCategory = (SubCategory) dataObject;

                    json = subCategory.encrypt();

                    uri = RequestBuilder.SUB_CATEGORY + "/" + subCategory.getId();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.PUT,
                            uri, null, json, context));

                    return true;

                case ApiService.DELETE_SUB_CATEGORY:

                    subCategory = (SubCategory) dataObject;

                    uri = RequestBuilder.SUB_CATEGORY + "/" + subCategory.getId();

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.DELETE,
                            uri, null, null, context));

                    return true;

                case ApiService.DELETE_REFRESH_TOKEN:

                    requests.add(RequestBuilder.deleteRefreshToken(0, coordinator, context));
                    return true;

            }

        } catch (Exception e) {
            // TODO: review error logging
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
