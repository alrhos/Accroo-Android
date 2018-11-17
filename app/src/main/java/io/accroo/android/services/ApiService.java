package io.accroo.android.services;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import io.accroo.android.crypto.CryptoManager;
import io.accroo.android.model.AccessToken;
import io.accroo.android.model.Account;
import io.accroo.android.model.GeneralCategory;
import io.accroo.android.model.Preferences;
import io.accroo.android.model.SubCategory;
import io.accroo.android.model.Transaction;
import io.accroo.android.network.RequestBuilder;
import io.accroo.android.network.RequestCoordinator;
import io.accroo.android.other.GsonUtil;

import java.util.Date;
import java.util.HashMap;

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
    public final static int GET_DEVICE_TOKEN =          6;
    public final static int CREATE_GENERAL_CATEGORY =   7;
    public final static int UPDATE_GENERAL_CATEGORY =   8;
    public final static int DELETE_GENERAL_CATEGORY =   9;
    public final static int CREATE_SUB_CATEGORY =       10;
    public final static int UPDATE_SUB_CATEGORY =       11;
    public final static int DELETE_SUB_CATEGORY =       12;
    public final static int UPDATE_EMAIL =              13;
    public final static int GET_KEY =                   14;
    public final static int UPDATE_PASSWORD =           15;
    public final static int GET_VERIFICATION_CODE =     16;
    public final static int CREATE_ACCOUNT =            17;
    public final static int LOGIN =                     18;
    public final static int UPDATE_PREFERENCES =        19;
    public final static int UPDATE_KEY =                20;
    public final static int CREATE_KEY =                21;

    public final static int GENERIC_ERROR =             1000;
    public final static int TIMEOUT_ERROR =             1001;
    public final static int CONNECTION_ERROR =          1002;
    public final static int UNAUTHORIZED =              1003;
    public final static int CONFLICT =                  1004;
    public final static int TOO_MANY_REQUESTS =         1005;
    public final static int INVALID_REQUEST =           1006;
    public final static int INVALID_DATE_RANGE =        1007;
    public final static int ORIGIN_UNAVAILABLE =        1008;
    public final static int NOT_FOUND =                 1009;

    private RequestOutcome                              requestOutcome;
    private Context                                     context;
    private RequestCoordinator                          coordinator;
    private String[]                                    dataReceiver;
    private HashMap<String, Object>                     preRequestVariables;
    private HashMap<String, Object>                     postRequestVariables;
    private PreRequestTask                              preRequestTask;
    private int                                         requestType;

    public ApiService(RequestOutcome requestOutcome, Context context) {
        this.requestOutcome = requestOutcome;
        this.context = context;
        this.preRequestVariables = new HashMap<>();
        this.postRequestVariables = new HashMap<>();
    }

    public interface RequestOutcome {
        void onSuccess(int requestType);
        void onFailure(int requestType, int errorCode);
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
            CryptoManager.getInstance().decryptMasterKey(password, DataProvider.getKey());
            CryptoManager.getInstance().saveMasterKey(context);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getLoginCode(String username) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(GET_VERIFICATION_CODE);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(GET_VERIFICATION_CODE, errorCode);
            }
        };

        preRequestVariables.clear();

        if (username != null) {
            preRequestVariables.put("username", username);
        } else {
            try {
                preRequestVariables.put("username", CredentialService.getInstance(context).getEntry(CredentialService.USERNAME_KEY));
            } catch (Exception e) {
                requestOutcome.onFailure(GET_VERIFICATION_CODE, GENERIC_ERROR);
            }
        }

        new PreRequestTask(GET_VERIFICATION_CODE, this, context,
                coordinator, preRequestVariables).execute();
    }

    public void login(final Account account) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("account", account);
                new PostRequestTask(LOGIN, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(LOGIN, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("account", account);

        new PreRequestTask(LOGIN, this, context, coordinator,
                preRequestVariables).execute();
    }

//    public void login(@NonNull final String username, @NonNull String loginCode) {
//        dataReceiver = new JSONObject[1];
//        coordinator = new RequestCoordinator(context, this, dataReceiver) {
//            @Override
//            protected void onSuccess() {
//                postRequestVariables.clear();
//                postRequestVariables.put("username", username);
//                new PostRequestTask(GET_DEVICE_TOKEN, ApiService.this, context, postRequestVariables).execute(dataReceiver);
//            }
//
//            @Override
//            protected void onFailure(int errorCode) {
//                requestOutcome.onFailure(GET_DEVICE_TOKEN, errorCode);
//            }
//        };
//
//        preRequestVariables.clear();
//        preRequestVariables.put("username", username);
//        preRequestVariables.put("loginCode", loginCode);
//
//        new PreRequestTask(GET_DEVICE_TOKEN, this, context, coordinator, preRequestVariables).execute();
//    }

    private boolean newAccessTokenRequired() {
        try {
            // Check if a refresh token exists
            CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
            String tokenExpiry = CredentialService.getInstance(context)
                    .getEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY);
            DateTime tokenExpiryTime = new DateTime(tokenExpiry);
            System.out.println("TOKEN EXPIRY TIME: " + tokenExpiryTime.toLocalDateTime());
            DateTime currentTime = new DateTime();
            System.out.println("CURRENT TIME: " + currentTime.toLocalDateTime());
            System.out.println("DIFFERENCE: " + Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds());
            return Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds() <= 595; // TODO: change to 60 after testing
        } catch (Exception e) {
            // A refresh token wasn't found or the access token expiry couldn't be retrieved
            e.printStackTrace();
            return false;
            // TODO: check how to better handle this exception
        }
    }

    private void updateAccessToken() {
        final String[] accessTokenReceiver = new String[1];
        RequestCoordinator accessTokenCoordinator = new RequestCoordinator(context,
                this, accessTokenReceiver) {
            @Override
            protected void onSuccess() {
                String response = accessTokenReceiver[0];
                AccessToken accessToken = GsonUtil.getInstance().fromJson(response, AccessToken.class);
                DateTime tokenExpiry = new DateTime(accessToken.getExpiresAt());
                try {
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_KEY, accessToken.getToken());
                    CredentialService.getInstance(context).saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, tokenExpiry.toString());
                    preRequestTask.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    requestOutcome.onError();
                }
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(requestType, errorCode);
            }
        };

        try {
            String refreshToken = CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
            JsonObjectRequest accessTokenRequest = RequestBuilder.postAccessToken(0,
                    accessTokenCoordinator, refreshToken);
            accessTokenCoordinator.addRequests(accessTokenRequest);
            accessTokenCoordinator.start();
        } catch (Exception e) {
            e.printStackTrace();
            requestOutcome.onError();
        }
    }

    public void logout() {
        try {
            CredentialService.getInstance(context).clearSavedData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDefaultData(@NonNull final Date startDate, @NonNull final Date endDate) {
        if (startDate.before(endDate)) {
            DataProvider.setStartDate(startDate);
            DataProvider.setEndDate(endDate);
            dataReceiver = new String[3];
            coordinator = new RequestCoordinator(context, this, dataReceiver) {
                @Override
                protected void onSuccess() {
                    postRequestVariables.clear();
                    postRequestVariables.put("startDate", startDate);
                    postRequestVariables.put("endDate", endDate);
                    new PostRequestTask(GET_DEFAULT_DATA, ApiService.this,
                            context, postRequestVariables).execute(dataReceiver);
                }

                @Override
                protected void onFailure(int errorCode) {
                    requestOutcome.onFailure(GET_DEFAULT_DATA, errorCode);
                }
            };
            preRequestTask = new PreRequestTask(GET_DEFAULT_DATA, this, context,
                    coordinator, null);
            requestType = GET_DEFAULT_DATA;

            if (newAccessTokenRequired()) {
                updateAccessToken();
            } else {
                preRequestTask.execute();
            }
        } else {
            requestOutcome.onFailure(GET_DEFAULT_DATA, INVALID_DATE_RANGE);
        }
    }

    public void createAccount(final Account account) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(CREATE_ACCOUNT);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_ACCOUNT, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("account", account);

        new PreRequestTask(CREATE_ACCOUNT, this, context, coordinator,
                preRequestVariables).execute();
    }

    public void createKey(final char[] password) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(CREATE_KEY);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_KEY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("password", password);

        preRequestTask = new PreRequestTask(CREATE_KEY, this, context, coordinator,
                preRequestVariables);
        requestType = CREATE_KEY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

//    public void updateKey(final char[] password) {
//        dataReceiver = new JSONObject[1];
//        coordinator = new RequestCoordinator(context, this, dataReceiver) {
//            @Override
//            protected void onSuccess() {
//
//            }
//
//            @Override
//            protected void onFailure(int errorCode) {
//                requestOutcome.onFailure(UPDATE_KEY, errorCode);
//            }
//        };
//
//        preRequestVariables.clear();
//        preRequestVariables.put("password", password);
//
//        new PreRequestTask(UPDATE_KEY, this, context, coordinator,
//                preRequestVariables).execute();
//    }

    public void updatePreferences(final Preferences preferences) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(UPDATE_PREFERENCES);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_PREFERENCES, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("preferences", preferences);

        preRequestTask = new PreRequestTask(UPDATE_PREFERENCES, this, context, coordinator,
                preRequestVariables);
        requestType = UPDATE_PREFERENCES;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }


//    public void createUser(final User user) {
//        dataReceiver = new JSONObject[1];
//        coordinator = new RequestCoordinator(context, this, dataReceiver) {
//            @Override
//            protected void onSuccess() {
//                postRequestVariables.clear();
//                postRequestVariables.put("user", user);
//                new PostRequestTask(CREATE_USER, ApiService.this, context, postRequestVariables).execute(dataReceiver);
//            }
//
//            @Override
//            protected void onFailure(int errorCode) {
//                requestOutcome.onFailure(CREATE_USER, errorCode);
//            }
//        };
//
//        preRequestVariables.clear();
//        preRequestVariables.put("user", user);
//
//        new PreRequestTask(CREATE_USER, this, context, coordinator, preRequestVariables).execute();
//    }

    public void createDefaultCategories() {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(CREATE_DEFAULT_CATEGORIES);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_DEFAULT_CATEGORIES, errorCode);
            }
        };

        preRequestTask = new PreRequestTask(CREATE_DEFAULT_CATEGORIES, this,
                context, coordinator, null);
        requestType = CREATE_DEFAULT_CATEGORIES;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void createTransaction(final Transaction transaction) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("transaction", transaction);
                new PostRequestTask(CREATE_TRANSACTION, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_TRANSACTION, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("transaction", transaction);

        preRequestTask = new PreRequestTask(CREATE_TRANSACTION, this, context,
                coordinator, preRequestVariables);
        requestType = CREATE_TRANSACTION;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void updateTransaction(final Transaction transaction) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("transaction", transaction);
                new PostRequestTask(UPDATE_TRANSACTION, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_TRANSACTION, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("transaction", transaction);

        preRequestTask = new PreRequestTask(UPDATE_TRANSACTION, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_TRANSACTION;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void deleteTransaction(final Transaction transaction) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("transaction", transaction);
                new PostRequestTask(DELETE_TRANSACTION, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(DELETE_TRANSACTION, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("transaction", transaction);

        preRequestTask = new PreRequestTask(DELETE_TRANSACTION, this, context,
                coordinator, preRequestVariables);
        requestType = DELETE_TRANSACTION;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void createGeneralCategory(final GeneralCategory generalCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("generalCategory", generalCategory);
                new PostRequestTask(CREATE_GENERAL_CATEGORY, ApiService.this,
                        context, postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_GENERAL_CATEGORY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("generalCategory", generalCategory);

        preRequestTask = new PreRequestTask(CREATE_GENERAL_CATEGORY, this, context,
                coordinator, preRequestVariables);
        requestType = CREATE_GENERAL_CATEGORY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void updateGeneralCategory(final GeneralCategory generalCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("generalCategory", generalCategory);
                new PostRequestTask(UPDATE_GENERAL_CATEGORY, ApiService.this,
                        context, postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_GENERAL_CATEGORY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("generalCategory", generalCategory);

        preRequestTask = new PreRequestTask(UPDATE_GENERAL_CATEGORY, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_GENERAL_CATEGORY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void deleteGeneralCategory(final GeneralCategory generalCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("generalCategory", generalCategory);
                new PostRequestTask(DELETE_GENERAL_CATEGORY, ApiService.this,
                        context, postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(DELETE_GENERAL_CATEGORY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("generalCategory", generalCategory);

        preRequestTask = new PreRequestTask(DELETE_GENERAL_CATEGORY, this, context,
                coordinator, preRequestVariables);
        requestType = DELETE_GENERAL_CATEGORY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void createSubCategory(final SubCategory subCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("subCategory", subCategory);
                new PostRequestTask(CREATE_SUB_CATEGORY, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CREATE_SUB_CATEGORY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("subCategory", subCategory);

        preRequestTask = new PreRequestTask(CREATE_SUB_CATEGORY, this, context,
                coordinator, preRequestVariables);
        requestType = CREATE_SUB_CATEGORY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void updateSubCategory(final SubCategory subCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("subCategory", subCategory);
                new PostRequestTask(UPDATE_SUB_CATEGORY, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_SUB_CATEGORY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("subCategory", subCategory);

        preRequestTask = new PreRequestTask(UPDATE_SUB_CATEGORY, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_SUB_CATEGORY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void deleteSubCategory(final SubCategory subCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("subCategory", subCategory);
                new PostRequestTask(DELETE_SUB_CATEGORY, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(DELETE_SUB_CATEGORY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("subCategory", subCategory);

        preRequestTask = new PreRequestTask(DELETE_SUB_CATEGORY, this, context,
                coordinator, preRequestVariables);
        requestType = DELETE_SUB_CATEGORY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void updateEmail(final String newEmail, String loginCode) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                postRequestVariables.clear();
                postRequestVariables.put("newEmail", newEmail);
                new PostRequestTask(UPDATE_EMAIL, ApiService.this, context,
                        postRequestVariables).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_EMAIL, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("newEmail", newEmail);
        preRequestVariables.put("loginCode", loginCode);

        preRequestTask = new PreRequestTask(UPDATE_EMAIL, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_EMAIL;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void getKey() {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(GET_KEY, ApiService.this, context,
                        null).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(GET_KEY, errorCode);
            }
        };

        preRequestTask = new PreRequestTask(GET_KEY, this, context, coordinator,
                null);
        requestType = GET_KEY;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    public void updatePassword(char[] newPassword, String loginCode) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_PASSWORD, ApiService.this, context,
                        null).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_PASSWORD, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("loginCode", loginCode);
        preRequestVariables.put("newPassword", newPassword);

        preRequestTask = new PreRequestTask(UPDATE_PASSWORD, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_PASSWORD;

        if (newAccessTokenRequired()) {
            updateAccessToken();
        } else {
            preRequestTask.execute();
        }
    }

    @Override
    public void onPreRequestTaskSuccess(JsonRequest... requests) {
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
