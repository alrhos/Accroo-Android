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

public class DataServices implements PreRequestTask.PreRequestOutcome, PostRequestTask.PostRequestOutcome {

    public final static int GET_DEFAULT_DATA = 0;
    public final static int CREATE_USER = 1;
    public final static int CREATE_DEFAULT_CATEGORIES = 2;
    public final static int CREATE_TRANSACTION = 3;
    public final static int UPDATE_TRANSACTION = 4;
    public final static int DELETE_TRANSACTION = 5;
    public final static int GET_REFRESH_TOKEN = 6;


    private RequestOutcome requestOutcome;
    private Context context;
    private RequestCoordinator coordinator;
    private JSONObject[] dataReceiver;

    public DataServices(RequestOutcome requestOutcome, Context context) {
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
                new PostRequestTask(GET_REFRESH_TOKEN, DataServices.this, context).execute(dataReceiver);
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
                new PostRequestTask(GET_DEFAULT_DATA, DataServices.this, context).execute(dataReceiver);
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
                new PostRequestTask(CREATE_USER, DataServices.this, context).execute(dataReceiver);
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
                new PostRequestTask(CREATE_DEFAULT_CATEGORIES, DataServices.this, context).execute(dataReceiver);
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
                new PostRequestTask(CREATE_TRANSACTION, DataServices.this, context, transaction).execute(dataReceiver);
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
                new PostRequestTask(UPDATE_TRANSACTION, DataServices.this, context, transaction).execute(dataReceiver);
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
                new PostRequestTask(DELETE_TRANSACTION, DataServices.this, context, transaction).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onUnsuccessfulRequest(errorMessage);
            }
        };

        new PreRequestTask(DELETE_TRANSACTION, this, context, coordinator, transaction).execute();

    }

    public void createGeneralCategory(GeneralCategory generalCategory) {

    }

    public void updateGeneralCategory(GeneralCategory generalCategory) {

    }

    public void deleteGeneralCategory(GeneralCategory generalCategory) {

    }

    public void createSubCategory(SubCategory subCategory) {

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
