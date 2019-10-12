package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.safetynet.SafetyNetStatusCodes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;

import io.accroo.android.R;
import io.accroo.android.other.Constants;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class LaunchActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private final static int CREATE_ACCOUNT = 1;
    private final static int SIGN_IN = 2;

    private DateTime startDate, endDate;
    public static boolean initialized = false;
    private ApiService apiService;
    private ProgressBar progressBar;
    private TextInputLayout inputEmailAddress;
    private EditText emailAddress;
    private Button createAccount, signIn;
    private int action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        initialized = true;
        startUp();
    }

    private void startUp() {
        setContentView(R.layout.activity_launch_loading);
        apiService = new ApiService(this, getApplicationContext());
        if (apiService.userLoggedIn()) {
            // Set dates from first day of calendar month to the end of the current day
            endDate = new DateTime().withTime(23, 59, 59, 999);
            startDate = new DateTime(endDate.getYear(), endDate.getMonthOfYear(), 1,
                    0, 0, 0, 0);
            apiService.getDefaultData(startDate, endDate);
        } else {
            initLayout();
        }
    }

    private void initLayout() {
        setContentView(R.layout.activity_launch);
        progressBar = findViewById(R.id.progress_bar);
        inputEmailAddress = findViewById(R.id.input_email);
        inputEmailAddress.setError(" ");
        emailAddress = findViewById(R.id.email);
        createAccount = findViewById(R.id.create_account);
        createAccount.setOnClickListener(createAccountListener);
        signIn = findViewById(R.id.sign_in);
        signIn.setOnClickListener(signInListener);
        TextView acceptTerms = findViewById(R.id.accept_terms);
        acceptTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onSuccess(int requestType) {
        if (requestType == ApiService.GET_ANONYMOUS_TOKEN) {
            String email = emailAddress.getText().toString().trim();
            if (action == CREATE_ACCOUNT) {
                apiService.checkEmailAvailability(email);
            } else if (action == SIGN_IN) {
                apiService.getVerificationCode(email);
            }
        } else if (requestType == ApiService.CHECK_EMAIL_AVAILABILITY) {
            // Email is already being used - HTTP 200
            progressBar.setVisibility(View.INVISIBLE);
            createAccount.setOnClickListener(createAccountListener);
            signIn.setOnClickListener(signInListener);
            inputEmailAddress.setError(getResources().getString(R.string.email_in_use));
        } else if (requestType == ApiService.GET_VERIFICATION_CODE) {
            progressBar.setVisibility(View.INVISIBLE);
            Utils.hideSoftKeyboard(LaunchActivity.this);
            createAccount.setOnClickListener(createAccountListener);
            signIn.setOnClickListener(signInListener);
            Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
            intent.putExtra("username", emailAddress.getText().toString());
            intent.putExtra("action", VerificationCodeActivity.LOGIN);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (requestType == ApiService.GET_DEFAULT_DATA) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("startDate", startDate.getMillis());
            intent.putExtra("endDate", endDate.getMillis());
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressBar.setVisibility(View.INVISIBLE);
        createAccount.setOnClickListener(createAccountListener);
        signIn.setOnClickListener(signInListener);
        if (errorCode == ApiService.UNAUTHORIZED) {
            initLayout();
        } else if (requestType == ApiService.CHECK_EMAIL_AVAILABILITY && errorCode == ApiService.NOT_FOUND) {
            // Email is not being used
            Utils.hideSoftKeyboard(LaunchActivity.this);
            createAccount.setOnClickListener(createAccountListener);
            signIn.setOnClickListener(signInListener);
            Intent intent = new Intent(getApplicationContext(), ChoosePasswordActivity.class);
            intent.putExtra("action", ChoosePasswordActivity.REGISTER);
            intent.putExtra("username", emailAddress.getText().toString());
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (requestType == ApiService.GET_VERIFICATION_CODE && errorCode == ApiService.NOT_FOUND) {
            inputEmailAddress.setError(getResources().getString(R.string.account_not_found));
        } else if (errorCode == ApiService.CONNECTION_ERROR || errorCode == ApiService.TIMEOUT_ERROR ||
                errorCode == ApiService.TOO_MANY_REQUESTS || errorCode == ApiService.SERVICE_UNAVAILABLE) {
            // TODO: review how this logic works
            if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
                MaintenanceDialog.show(this);
            }
            setContentView(R.layout.activity_no_connection);
            Button tryAgain = findViewById(R.id.try_again);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    startUp();
                }
            });
        } else {
            onError();
        }
    }

    @Override
    public void onError() {
        apiService.logout();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void recaptchaChallenge() {
        SafetyNet.getClient(this).verifyWithRecaptcha(Constants.RECAPTCHA_SITE_KEY)
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            apiService.getAnonymousToken(response.getTokenResult());
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            String message;
                            switch (statusCode) {
                                case SafetyNetStatusCodes.TIMEOUT:
                                    message = getResources().getString(R.string.timeout_error);
                                    break;
                                case SafetyNetStatusCodes.NETWORK_ERROR:
                                    message = getResources().getString(R.string.no_network_connection);
                                    break;
                                default:
                                    message = getResources().getString(R.string.general_error);
                            }
                            inputEmailAddress.setError(message);
                        } else {
                            inputEmailAddress.setError(getResources().getString(R.string.general_error));
                        }
                        createAccount.setOnClickListener(createAccountListener);
                        signIn.setOnClickListener(signInListener);
                    }
                });
    }

    View.OnClickListener createAccountListener = new View.OnClickListener() {
        public void onClick(View view) {
            String email = emailAddress.getText().toString().trim();
            if (email.length() == 0) {
                inputEmailAddress.setError(getResources().getString(R.string.enter_email));
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmailAddress.setError(getResources().getString(R.string.error_invalid_email));
            } else {
                action = CREATE_ACCOUNT;
                inputEmailAddress.setError(" ");
                createAccount.setOnClickListener(null);
                signIn.setOnClickListener(null);
                if (apiService.hasActiveAccessToken()) {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.checkEmailAvailability(email);
                } else {
                    recaptchaChallenge();
                }
            }
        }
    };

    View.OnClickListener signInListener = new View.OnClickListener() {
        public void onClick(View view) {
            String email = emailAddress.getText().toString();
            if (email.length() == 0) {
                inputEmailAddress.setError(getResources().getString(R.string.enter_email));
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmailAddress.setError(getResources().getString(R.string.error_invalid_email));
            } else {
                action = SIGN_IN;
                inputEmailAddress.setError(" ");
                createAccount.setOnClickListener(null);
                signIn.setOnClickListener(null);
                if (apiService.hasActiveAccessToken()) {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.getVerificationCode(email);
                } else {
                    recaptchaChallenge();
                }

            }
        }
    };

}
