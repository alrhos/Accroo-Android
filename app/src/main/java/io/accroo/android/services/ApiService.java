package io.accroo.android.services;

import android.content.Context;
import androidx.annotation.NonNull;

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

import java.util.HashMap;

/**
 * Created by oscar on 4/07/17.
 */

public class ApiService implements PreRequestTask.PreRequestOutcome, PostRequestTask.PostRequestOutcome {

    public final static int GET_DEFAULT_DATA =          1;
    public final static int CREATE_DEFAULT_CATEGORIES = 2;
    public final static int CREATE_TRANSACTION =        3;
    public final static int UPDATE_TRANSACTION =        4;
    public final static int DELETE_TRANSACTION =        5;
    public final static int CREATE_GENERAL_CATEGORY =   6;
    public final static int UPDATE_GENERAL_CATEGORY =   7;
    public final static int DELETE_GENERAL_CATEGORY =   8;
    public final static int CREATE_SUB_CATEGORY =       9;
    public final static int UPDATE_SUB_CATEGORY =       10;
    public final static int DELETE_SUB_CATEGORY =       11;
    public final static int UPDATE_EMAIL =              12;
    public final static int GET_KEY =                   13;
    public final static int UPDATE_PASSWORD =           14;
    public final static int GET_VERIFICATION_CODE =     15;
    public final static int CREATE_ACCOUNT =            16;
    public final static int LOGIN =                     17;
    public final static int UPDATE_PREFERENCES =        18;
    public final static int CREATE_KEY =                19;
    public final static int REAUTHENTICATE =            20;
    public final static int GET_ANONYMOUS_TOKEN =       21;
    public final static int CHECK_EMAIL_AVAILABILITY =  22;

    public final static int GENERIC_ERROR =             1000;
    public final static int TIMEOUT_ERROR =             1001;
    public final static int CONNECTION_ERROR =          1002;
    public final static int UNAUTHORIZED =              1003;
    public final static int CONFLICT =                  1004;
    public final static int TOO_MANY_REQUESTS =         1005;
    public final static int INVALID_REQUEST =           1006;
    public final static int INVALID_DATE_RANGE =        1007;
    public final static int NOT_FOUND =                 1008;
    public final static int SERVICE_UNAVAILABLE =       1009;
    public final static int FORBIDDEN =                 1010;

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

    public boolean hasActiveAccessToken() {
        try {
            String tokenExpiry = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY);
            DateTime tokenExpiryTime = new DateTime(tokenExpiry);
            DateTime currentTime = new DateTime();
            if (Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds() > 60) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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

    public void checkEmailAvailability(String email) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(CHECK_EMAIL_AVAILABILITY);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(CHECK_EMAIL_AVAILABILITY, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("email", email);

        new PreRequestTask(CHECK_EMAIL_AVAILABILITY, this, context, coordinator,
                preRequestVariables).execute();
    }

    public void getVerificationCode(String username) {
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
                preRequestVariables.put("username", CredentialService.getInstance(context)
                        .getEntry(CredentialService.USERNAME_KEY));
            } catch (Exception e) {
                requestOutcome.onFailure(GET_VERIFICATION_CODE, GENERIC_ERROR);
            }
        }

        new PreRequestTask(GET_VERIFICATION_CODE, this, context,
                coordinator, preRequestVariables).execute();
    }

    public void getAnonymousToken(final String recaptchaToken) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(GET_ANONYMOUS_TOKEN, ApiService.this, context,
                        null).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(GET_ANONYMOUS_TOKEN, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("recaptchaToken", recaptchaToken);

        new PreRequestTask(GET_ANONYMOUS_TOKEN, this, context, coordinator,
                preRequestVariables).execute();
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

    public void reauthenticate(final String password) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(REAUTHENTICATE, ApiService.this, context,
                        null).execute(dataReceiver);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(REAUTHENTICATE, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("loginCode", password);

        new PreRequestTask(REAUTHENTICATE, this, context, coordinator,
                preRequestVariables).execute();
    }

    private void submitRequest() {
        try {
            String refreshToken = CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
            String tokenExpiry = CredentialService.getInstance(context).getEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY);
            DateTime tokenExpiryTime = new DateTime(tokenExpiry);
            DateTime currentTime = new DateTime();
            if (Seconds.secondsBetween(currentTime, tokenExpiryTime).getSeconds() <= 60) {
                // Access token needs to be refreshed
                final String[] accessTokenReceiver = new String[1];
                RequestCoordinator accessTokenCoordinator = new RequestCoordinator(context,
                        this, accessTokenReceiver) {
                    @Override
                    protected void onSuccess() {
                        String response = accessTokenReceiver[0];
                        AccessToken accessToken = GsonUtil.getInstance().fromJson(response, AccessToken.class);

                        // TODO: remove
                        System.out.println(accessToken.getToken());

                        DateTime tokenExpiry = new DateTime(accessToken.getExpiresAt());
                        try {
                            CredentialService.getInstance(context)
                                    .saveEntry(CredentialService.ACCESS_TOKEN_KEY, accessToken.getToken());
                            CredentialService.getInstance(context)
                                    .saveEntry(CredentialService.ACCESS_TOKEN_EXPIRY_KEY, tokenExpiry.toString());
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
                    JsonObjectRequest accessTokenRequest = RequestBuilder.postAccessToken(0,
                            accessTokenCoordinator, refreshToken);
                    accessTokenCoordinator.addRequests(accessTokenRequest);
                    accessTokenCoordinator.start();
                } catch (Exception e) {
                    e.printStackTrace();
                    requestOutcome.onError();
                }
            } else {
                // Current access token can still be used
                preRequestTask.execute();
            }
        } catch (Exception e) {
            // A refresh token wasn't found or the access token expiry couldn't be retrieved
            e.printStackTrace();
            requestOutcome.onError();
        }
    }

    public void logout() {
        try {
            dataReceiver = new String[1];
            coordinator = new RequestCoordinator(context, this, dataReceiver) {
                @Override
                protected void onSuccess() {}

                @Override
                protected void onFailure(int errorCode) {}
            };

            String refreshToken = CredentialService.getInstance(context).getEntry(CredentialService.REFRESH_TOKEN_KEY);
            CredentialService.getInstance(context).clearSavedData();
            JsonObjectRequest deleteRefreshToken = RequestBuilder.deleteRefreshToken(0, coordinator, refreshToken);
            coordinator.addRequests(deleteRefreshToken);
            coordinator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDefaultData(@NonNull final DateTime startDate, @NonNull final DateTime endDate) {
        if (startDate.isBefore(endDate)) {
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
            submitRequest();
        } else {
            requestOutcome.onFailure(GET_DEFAULT_DATA, INVALID_DATE_RANGE);
        }
    }

    public void createAccount(final Account account, final String recaptchaToken) {
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
        preRequestVariables.put("recaptchaToken", recaptchaToken);

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
        submitRequest();
    }

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
        submitRequest();
    }

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
        submitRequest();
    }

    public void createTransaction(final Transaction transaction) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_TRANSACTION, ApiService.this, context,
                        null).execute(dataReceiver);
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
        submitRequest();
    }

    public void updateTransaction(final Transaction transaction) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_TRANSACTION, ApiService.this, context,
                        null).execute(dataReceiver);
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
        submitRequest();
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
        submitRequest();
    }

    public void createGeneralCategory(final GeneralCategory generalCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_GENERAL_CATEGORY, ApiService.this,
                        context, null).execute(dataReceiver);
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
        submitRequest();
    }

    public void updateGeneralCategory(final GeneralCategory generalCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_GENERAL_CATEGORY, ApiService.this,
                        context, null).execute(dataReceiver);
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
        submitRequest();
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
        submitRequest();
    }

    public void createSubCategory(final SubCategory subCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(CREATE_SUB_CATEGORY, ApiService.this, context,
                        null).execute(dataReceiver);
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
        submitRequest();
    }

    public void updateSubCategory(final SubCategory subCategory) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                new PostRequestTask(UPDATE_SUB_CATEGORY, ApiService.this, context,
                        null).execute(dataReceiver);
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
        submitRequest();
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
        submitRequest();
    }

    public void updateEmail(final String newEmail) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                try {
                    CredentialService.getInstance(context).clearSavedData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestOutcome.onSuccess(UPDATE_EMAIL);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_EMAIL, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("newEmail", newEmail);

        preRequestTask = new PreRequestTask(UPDATE_EMAIL, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_EMAIL;
        submitRequest();
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
        submitRequest();
    }

    public void updatePassword(char[] newPassword) {
        dataReceiver = new String[1];
        coordinator = new RequestCoordinator(context, this, dataReceiver) {
            @Override
            protected void onSuccess() {
                requestOutcome.onSuccess(UPDATE_PASSWORD);
            }

            @Override
            protected void onFailure(int errorCode) {
                requestOutcome.onFailure(UPDATE_PASSWORD, errorCode);
            }
        };

        preRequestVariables.clear();
        preRequestVariables.put("newPassword", newPassword);

        preRequestTask = new PreRequestTask(UPDATE_PASSWORD, this, context,
                coordinator, preRequestVariables);
        requestType = UPDATE_PASSWORD;
        submitRequest();
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
