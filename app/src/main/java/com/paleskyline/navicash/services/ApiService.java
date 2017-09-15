package com.paleskyline.navicash.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestRequest;

import org.json.JSONObject;

import java.util.Date;

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
    public final static int DELETE_REFRESH_TOKEN = 13;
    public final static int UPDATE_EMAIL = 14;
    public final static int UPDATE_LOGIN_PASSWORD = 15;
    public final static int GET_KEY_PACKAGE = 16;
    public final static int UPDATE_DATA_PASSWORD = 17;

    public final static int GENERAL_ERROR = 1000;
    public final static int TIMEOUT_ERROR = 1001;
    public final static int CONNECTION_ERROR = 1002;
    public final static int DECRYPTION_ERROR = 1003;
    public final static int INVALID_INPUT = 1004;
    public final static int EMAIL_IN_USE = 1005;
    public final static int NO_TRANSACTION = 1006;
    public final static int NO_CATEGORY = 1007;
    public final static int DATABASE_ERROR = 1008;
    public final static int UNAUTHORIZED = 1009;
    public final static int INVALID_DATE_RANGE = 1100;

    private RequestOutcome requestOutcome;
    private Context context;
    private RequestCoordinator coordinator;
    private JSONObject[] dataReceiver;

    public ApiService(RequestOutcome requestOutcome, Context context) {
        this.requestOutcome = requestOutcome;
        this.context = context;
    }

    public interface RequestOutcome {
        void onSuccess(int requestType);
        void onFailure(int requestType, int errorCode);
        // TODO: ensure that all callback implementations in UI notify user of error and log them out. Also ensure that user data is deleted in ApiService before calling onError.
        void onError();
    }

//    public String getUsername() {
//        try {
//            return AuthManager.getInstance(context).getEntry(AuthManager.USERNAME_KEY);
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public boolean userLoggedIn() {
        try {
            AuthManager.getInstance(context).getEntry(AuthManager.REFRESH_TOKEN_KEY);
            CryptoManager.getInstance().initMasterKey(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean initializeKey(@NonNull char[] password) {
        try {
            // TODO: review password security
            CryptoManager.getInstance().decryptMasterKey(password, DataProvider.getKeyPackage());
            CryptoManager.getInstance().saveMasterKey(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean encryptKey(@NonNull char[] password, @NonNull Context context) {
        try {
            CryptoManager.getInstance().encryptMasterKey(password, context);
            CryptoManager.getInstance().saveMasterKey(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void login(@NonNull final String username, @NonNull char[] password) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(GET_REFRESH_TOKEN, ApiService.this, context, username).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                // TODO: double check that the handleFailedRequest is the right approach here
                //handleFailedRequest(GET_REFRESH_TOKEN, errorMessage);
                requestOutcome.onFailure(GET_REFRESH_TOKEN, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(GET_REFRESH_TOKEN, this, context, coordinator, username, password).execute();

    }

    public void getDefaultData(@NonNull final Date startDate, @NonNull final Date endDate) {

        if (startDate.before(endDate)) {

            DataProvider.setStartDate(startDate);
            DataProvider.setEndDate(endDate);

            dataReceiver = new JSONObject[2];
            coordinator = new RequestCoordinator(context, this, dataReceiver) {
                @Override
                protected void onSuccess() {
                    new PostRequestTask(GET_DEFAULT_DATA, ApiService.this, context, startDate, endDate).execute(dataReceiver);
                }

                @Override
                protected void onFailure(String errorMessage) {
                   // handleFailedRequest(GET_DEFAULT_DATA, errorMessage);
                    requestOutcome.onFailure(GET_DEFAULT_DATA, mapErrorMessage(errorMessage));
                }
            };

            new PreRequestTask(GET_DEFAULT_DATA, this, context, coordinator).execute();
        } else {
            requestOutcome.onFailure(GET_DEFAULT_DATA, INVALID_DATE_RANGE);
        }


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
                //handleFailedRequest(CREATE_USER, errorMessage);
                requestOutcome.onFailure(CREATE_USER, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(CREATE_DEFAULT_CATEGORIES, errorMessage);
                requestOutcome.onFailure(CREATE_DEFAULT_CATEGORIES, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(CREATE_TRANSACTION, errorMessage);
                requestOutcome.onFailure(CREATE_TRANSACTION, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(UPDATE_TRANSACTION, errorMessage);
                requestOutcome.onFailure(UPDATE_TRANSACTION, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(DELETE_TRANSACTION, errorMessage);
                requestOutcome.onFailure(DELETE_TRANSACTION, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(CREATE_GENERAL_CATEGORY, errorMessage);
                requestOutcome.onFailure(CREATE_GENERAL_CATEGORY, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(UPDATE_GENERAL_CATEGORY, errorMessage);
                requestOutcome.onFailure(UPDATE_GENERAL_CATEGORY, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(DELETE_GENERAL_CATEGORY, errorMessage);
                requestOutcome.onFailure(DELETE_GENERAL_CATEGORY, mapErrorMessage(errorMessage));
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
                //handleFailedRequest(CREATE_SUB_CATEGORY, errorMessage);
                requestOutcome.onFailure(CREATE_SUB_CATEGORY, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(CREATE_SUB_CATEGORY, this, context, coordinator, subCategory).execute();

    }

    public void updateSubCategory(final SubCategory subCategory) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_SUB_CATEGORY, ApiService.this, context, subCategory).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                //handleFailedRequest(UPDATE_SUB_CATEGORY, errorMessage);
                requestOutcome.onFailure(UPDATE_SUB_CATEGORY, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(UPDATE_SUB_CATEGORY, this, context, coordinator, subCategory).execute();

    }

    public void deleteSubCategory(final SubCategory subCategory) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(DELETE_SUB_CATEGORY, ApiService.this, context, subCategory).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                //handleFailedRequest(DELETE_SUB_CATEGORY, errorMessage);
                requestOutcome.onFailure(DELETE_SUB_CATEGORY, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(DELETE_SUB_CATEGORY, this, context, coordinator, subCategory).execute();

    }


    public void logout() {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(DELETE_REFRESH_TOKEN, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                //handleFailedRequest(DELETE_REFRESH_TOKEN, errorMessage);
                requestOutcome.onFailure(DELETE_REFRESH_TOKEN, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(DELETE_REFRESH_TOKEN, this, context, coordinator).execute();

    }

    public void updateEmail(final String newEmail, char[] password) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_EMAIL, ApiService.this, context, newEmail).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onFailure(UPDATE_EMAIL, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(UPDATE_EMAIL, this, context, coordinator, password, newEmail).execute();

    }

    public void updateLoginPassword(char[] currentPassword, char[] newPassword) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_LOGIN_PASSWORD, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onFailure(UPDATE_LOGIN_PASSWORD, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(UPDATE_LOGIN_PASSWORD, this, context, coordinator, currentPassword, newPassword).execute();

    }

    public void getKeyPackage(char[] loginPassword) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(GET_KEY_PACKAGE, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onFailure(GET_KEY_PACKAGE, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(GET_KEY_PACKAGE, this, context, coordinator, loginPassword).execute();

    }

    public void updateDataKey(char[] loginPassword, char[] newDataPassword) {

        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_DATA_PASSWORD, ApiService.this, context).execute(dataReceiver);
            }

            @Override
            protected void onFailure(String errorMessage) {
                requestOutcome.onFailure(UPDATE_DATA_PASSWORD, mapErrorMessage(errorMessage));
            }
        };

        new PreRequestTask(UPDATE_DATA_PASSWORD, this, context, coordinator, loginPassword, newDataPassword).execute();

    }

    // TODO: maybe a different api method called 'logout' could be called from an activity which could clear saved data'

//    private void handleFailedRequest(int requestType, String errorMessage) {
//        if (errorMessage.equals(RestRequest.UNAUTHORIZED)) {
//            try {
//                AuthManager.getInstance(context).clearSavedData();
//            } catch (Exception e) {
//                // TODO: review error handling
//                e.printStackTrace();
//            }
//            requestOutcome.onAuthorizationError();
//        } else {
//            requestOutcome.onFailure(requestType, mapErrorMessage(errorMessage));
//        }
//    }


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
            // TODO: exception should be logged
            requestOutcome.onError();
            System.out.println("ERROR STARTING COORDINATOR");
        }
    }

    private int mapErrorMessage(String errorMessage) {
        switch (errorMessage) {
            case RestRequest.UNAUTHORIZED:
                // TODO: determine how to handle clearing user data (if it's necessary here)
                return UNAUTHORIZED;
            case RestRequest.CONNECTION_ERROR:
                return CONNECTION_ERROR;
            case RestRequest.TIMEOUT_ERROR:
                return TIMEOUT_ERROR;
            case RestRequest.GENERAL_ERROR:
                return GENERAL_ERROR;
            case RestRequest.DATABASE_UNAVAILABLE:
                return GENERAL_ERROR;
            case RestRequest.INVALID_INPUT:
                return INVALID_INPUT;
            case RestRequest.EMAIL_IN_USE:
                return EMAIL_IN_USE;
            case RestRequest.NO_CATEGORY:
                return NO_CATEGORY;
            case RestRequest.NO_TRANSACTION:
                return NO_TRANSACTION;
        }
        return GENERAL_ERROR;
    }

    @Override
    public void onPostRequestTaskSuccess(int requestType) {
        requestOutcome.onSuccess(requestType);
    }

    @Override
    public void onPreRequestTaskFailure() {
        requestOutcome.onError();
    }

    @Override
    public void onPostRequestTaskFailure() {
        requestOutcome.onError();
    }

}
