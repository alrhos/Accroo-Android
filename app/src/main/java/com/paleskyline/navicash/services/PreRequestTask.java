package com.paleskyline.navicash.services;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.paleskyline.navicash.database.DataAccess;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
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
    private int requestType;

    private PreRequestOutcome preRequestOutcome;
    private Context context;
    private RequestCoordinator coordinator;

    private Object dataObject;

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
                case DataServices.GET_DEFAULT_DATA:

                    // Look up transaction id for date

                    requests.add(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.GET,
                            RequestBuilder.CATEGORY, null, null, context));
                    requests.add(RequestBuilder.accessTokenAuth(1, coordinator, Request.Method.GET,
                            RequestBuilder.TRANSACTION, "?transactionid=1", null, context));
                    return true;

                case DataServices.CREATE_USER:

                    User user = (User) dataObject;

                    requests.add(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
                            RequestBuilder.USER, user.toJSON(), context));
                    return true;

                case DataServices.CREATE_DEFAULT_CATEGORIES:

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

                    // Shuffle items so that each user's categories are inserted in a different order
                    // making it difficult for sysadmins to guess a certain category given the ciphertext length.

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

            }

        } catch (Exception e) {
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
        // Pass the built requests to onPreRequestTaskSuccess();
    }
}
