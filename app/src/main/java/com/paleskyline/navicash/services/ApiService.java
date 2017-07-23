package com.paleskyline.navicash.services;

import android.content.Context;

import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestRequest;

import org.json.JSONObject;

/**
 * Created by oscar on 4/07/17.
 */

public class ApiService implements PreRequestTask.PreRequestOutcome, PostRequestTask.PostRequestOutcome {

    public final static int GET_DEFAULT_DATA = 0;
    public final static int CREATE_USER = 1;
    public final static int CREATE_DEFAULT_CATEGORIES = 2;
    public final static int CREATE_TRANSACTION = 3;
    public final static int UPDATE_TRANSACTION = 4;
    public final static int DELETE_TRANSACTION = 5;
    public final static int GET_REFRESH_TOKEN = 6;
    public final static int CREATE_GENERAL_CATEGORY = 7;
    public final static int UPDATE_GENERAL_CATEGORY = 8;
    public final static int DELETE_GENERAL_CATEGORY = 9;
    public final static int CREATE_SUB_CATEGORY = 10;
    public final static int UPDATE_SUB_CATEGORY = 11;
    public final static int DELETE_SUB_CATEGORY = 12;

    private RequestOutcome requestOutcome;
    private Context context;
    private RequestCoordinator coordinator;
    private JSONObject[] dataReceiver;

    public ApiService(RequestOutcome requestOutcome, Context context) {
        this.requestOutcome = requestOutcome;
        this.context = context;
    }

    public interface RequestOutcome {
        void onUnsuccessfulRequest(String errorMessage);
        void onUnsuccessfulDecryption();
        void onGeneralError();
        void onSuccess(int requestType);
    }

    public void getRefreshToken(String username, char[] password) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(GET_REFRESH_TOKEN, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(GET_REFRESH_TOKEN, this, context, coordinator, username, password).execute();

    }

    public void getDefaultData(String startDate) {

        dataReceiver = new JSONObject[2];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(GET_DEFAULT_DATA, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(GET_DEFAULT_DATA, this, context, coordinator).execute();

    }

    public void createUser(User user) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_USER, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(CREATE_USER, this, context, coordinator, user).execute();

    }

    public void createDefaultCategories() {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_DEFAULT_CATEGORIES, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(CREATE_DEFAULT_CATEGORIES, this, context, coordinator).execute();

    }

    public void createTransaction(final Transaction transaction) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_TRANSACTION, ApiService.this, context, transaction).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(CREATE_TRANSACTION, this, context, coordinator, transaction).execute();

    }

    public void updateTransaction(final Transaction transaction) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_TRANSACTION, ApiService.this, context, transaction).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(UPDATE_TRANSACTION, this, context, coordinator, transaction).execute();

    }

    public void deleteTransaction(final Transaction transaction) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(DELETE_TRANSACTION, ApiService.this, context, transaction).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(DELETE_TRANSACTION, this, context, coordinator, transaction).execute();

    }

    public void createGeneralCategory(final GeneralCategory generalCategory) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_GENERAL_CATEGORY, ApiService.this, context, generalCategory).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(CREATE_GENERAL_CATEGORY, this, context, coordinator, generalCategory).execute();

    }

    public void updateGeneralCategory(final GeneralCategory generalCategory) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_GENERAL_CATEGORY, ApiService.this, context, generalCategory).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(UPDATE_GENERAL_CATEGORY, this, context, coordinator, generalCategory).execute();

    }

    public void deleteGeneralCategory(final GeneralCategory generalCategory) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(DELETE_GENERAL_CATEGORY, ApiService.this, context, generalCategory).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(DELETE_GENERAL_CATEGORY, this, context, coordinator, generalCategory).execute();

    }

    public void createSubCategory(final SubCategory subCategory) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_SUB_CATEGORY, ApiService.this, context, subCategory).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(CREATE_SUB_CATEGORY, this, context, coordinator, subCategory).execute();

    }

    public void updateSubCategory(SubCategory subCategory) {

    }

    public void deleteSubCategory(SubCategory subCategory) {

    }




    // TODO: add data service methods

    private void syncTransactions() {
        // Run in background after certain data services to get latest transaction data
    }

    @Override
    public void onPreRequestTaskSuccess(RestRequest... requests) {
        try {
            coordinator.addRequests(requests);
            coordinator.start();
        } catch (Exception e) {
            // TODO: review error handling
            requestOutcome.onGeneralError();
            System.out.println("ERROR STARTING COORDINATOR");
        }
    }

    @Override
    public void onPreRequestTaskFailure() {
        requestOutcome.onGeneralError();
    }

    @Override
    public void onPostRequestTaskSuccess(int requestType) {
        requestOutcome.onSuccess(requestType);
    }

    @Override
    public void onPostRequestTaskFailure() {
        requestOutcome.onUnsuccessfulDecryption();
    }

}
