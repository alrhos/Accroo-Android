package io.accroo.android.services;

import android.content.Context;
import android.support.annotation.NonNull;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.model.User;
import io.accroo.android.network.RequestCoordinator;
import io.accroo.android.network.RestRequest;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by oscar on 4/07/17.
 */

public class ApiService implements PreRequestTask.PreRequestOutcome, PostRequestTask.PostRequestOutcome {

    public final static int GET_DEFAULT_DATA =          0;
    public final static int CREATE_USER =               1;
    public final static int CREATE_DEFAULT_CATEGORIES = 2;
    public final static int CREATE_TRANSACTION =        3;
    public final static int UPDATE_TRANSACTION =        4;
    public final static int DELETE_TRANSACTION =        5;
    public final static int GET_REFRESH_TOKEN =         6;
    public final static int CREATE_GENERAL_CATEGORY =   7;
    public final static int UPDATE_GENERAL_CATEGORY =   8;
    public final static int DELETE_GENERAL_CATEGORY =   9;
    public final static int CREATE_SUB_CATEGORY =       10;
    public final static int UPDATE_SUB_CATEGORY =       11;
    public final static int DELETE_SUB_CATEGORY =       12;
    public final static int FORGOT_PASSWORD =           13;
    public final static int UPDATE_EMAIL =              14;
    public final static int UPDATE_LOGIN_PASSWORD =     15;
    public final static int GET_KEY_PACKAGE =           16;
    public final static int UPDATE_DATA_PASSWORD =      17;

    public final static int GENERIC_ERROR =             1000;
    public final static int TIMEOUT_ERROR =             1001;
    public final static int CONNECTION_ERROR =          1002;
    public final static int UNAUTHORIZED =              1003;
    public final static int CONFLICT =                  1004;
    public final static int TOO_MANY_REQUESTS =         1005;
    public final static int INVALID_REQUEST =           1006;
    public final static int INVALID_DATE_RANGE =        1007;

    private RequestOutcome                              requestOutcome;
    private Context                                     context;
    private RequestCoordinator                          coordinator;
    private JSONObject[]                                dataReceiver;

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

    public boolean userLoggedIn() {
        try {
            CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
            CryptoManager.getInstance().initMasterKey(context);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean initializeKey(@NonNull char[] password) {
        try {
            CryptoManager.getInstance().decryptMasterKey(password, DataProvider.getKeyPackage());
            CryptoManager.getInstance().saveMasterKey(context);
            Arrays.fill(password, '\u0000');
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(GET_REFRESH_TOKEN, errorCode);
            }
        };

        new PreRequestTask(GET_REFRESH_TOKEN, this, context, coordinator, username, password).execute();
    }

    public void logout() {
        try {
            CredentialService.getInstance(context).clearSavedData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forgotPassword(@NonNull String email) {
        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(FORGOT_PASSWORD);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(FORGOT_PASSWORD, errorCode);
            }
        };

        new PreRequestTask(FORGOT_PASSWORD, this, context, coordinator, email).execute();
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
                protected void onFailure(int errorCode) {
                    requestOutcome.onFailure(GET_DEFAULT_DATA, errorCode);
                }
            };

            new PreRequestTask(GET_DEFAULT_DATA, this, context, coordinator).execute();
        } else {
            requestOutcome.onFailure(GET_DEFAULT_DATA, INVALID_DATE_RANGE);
        }
    }

    public void createUser(final User user) {
        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_USER, ApiService.this, context, user).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_USER, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_DEFAULT_CATEGORIES, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_TRANSACTION, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_TRANSACTION, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(DELETE_TRANSACTION, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_GENERAL_CATEGORY, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_GENERAL_CATEGORY, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(DELETE_GENERAL_CATEGORY, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_SUB_CATEGORY, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_SUB_CATEGORY, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(DELETE_SUB_CATEGORY, errorCode);
            }
        };

        new PreRequestTask(DELETE_SUB_CATEGORY, this, context, coordinator, subCategory).execute();
    }

    public void updateEmail(final String newEmail, char[] password) {
        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_EMAIL, ApiService.this, context, newEmail).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_EMAIL, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_LOGIN_PASSWORD, errorCode);
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
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(GET_KEY_PACKAGE, errorCode);
            }
        };

        new PreRequestTask(GET_KEY_PACKAGE, this, context, coordinator, loginPassword).execute();
    }

    public void updateDataPassword(char[] loginPassword, char[] newDataPassword) {
        dataReceiver = new JSONObject[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(UPDATE_DATA_PASSWORD);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_DATA_PASSWORD, errorCode);
            }
        };

        new PreRequestTask(UPDATE_DATA_PASSWORD, this, context, coordinator, loginPassword, newDataPassword).execute();
    }

    @Override
    public void onPreRequestTaskSuccess(RestRequest... requests) {
        try {
            coordinator.addRequests(requests);
            coordinator.start();
        } catch (Exception e) {
            requestOutcome.onError();
        }
    }

    @Override
    public void onPostRequestTaskSuccess(int requestType) {
        requestOutcome.onSuccess(requestType);
    }

    @Override
    public void onPreRequestTaskFailure() {
        logout();
        requestOutcome.onError();
    }

    @Override
    public void onPostRequestTaskFailure() {
        logout();
        requestOutcome.onError();
    }

}
