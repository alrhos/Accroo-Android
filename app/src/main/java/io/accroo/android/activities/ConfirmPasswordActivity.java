package io.accroo.android.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

import io.accroo.android.R;
import io.accroo.android.model.Account;
import io.accroo.android.model.Preferences;
import io.accroo.android.other.Constants;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class ConfirmPasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private String username, password;
    private ProgressBar progressBar;
    private TextInputLayout confirmPasswordField;
    private EditText passwordField;
    private Button next;
    private ApiService apiService;
    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_confirm_password);
            apiService = new ApiService(this, getApplicationContext());
            username = getIntent().getStringExtra("username");
            password = getIntent().getStringExtra("password");
            TextView emailAddress = findViewById(R.id.email);
            emailAddress.setText(username);
            progressBar = findViewById(R.id.progress_bar);
            confirmPasswordField = findViewById(R.id.input_password);
            confirmPasswordField.setError(" ");
            passwordField = findViewById(R.id.password);
            next = findViewById(R.id.next);
            next.setOnClickListener(nextListener);
        }

    }

    private void createAccount() {
        SafetyNet.getClient(this).verifyWithRecaptcha(Constants.RECAPTCHA_SITE_KEY)
                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        if (!response.getTokenResult().isEmpty()) {
                            progressBar.setVisibility(View.VISIBLE);
                            account = new Account(username);
                            apiService.createAccount(account, response.getTokenResult());
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
                            confirmPasswordField.setError(message);
                        } else {
                            confirmPasswordField.setError(getResources().getString(R.string.general_error));
                        }
                    }
                });
    }

    View.OnClickListener nextListener = new View.OnClickListener() {
        public void onClick(View view) {
            String confirmPassword = passwordField.getText().toString();
            if (confirmPassword.length() == 0) {
                confirmPasswordField.setError(getResources().getString(R.string.confirm_your_password));
            } else if (!confirmPassword.equals(password)) {
                confirmPasswordField.setError(getResources().getString(R.string.password_mismatch));
            } else {
                confirmPasswordField.setError(" ");
                Utils.hideSoftKeyboard(ConfirmPasswordActivity.this);
                next.setOnClickListener(null);
                createAccount();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        if (requestType == ApiService.CREATE_ACCOUNT) {
            apiService.login(account);
        } else if (requestType == ApiService.LOGIN) {
            char[] pwd = new char[password.length()];
            password.getChars(0, password.length(), pwd, 0);
            apiService.createKey(pwd);
        } else if (requestType == ApiService.CREATE_KEY) {
            apiService.updatePreferences(new Preferences());
        } else if (requestType == ApiService.UPDATE_PREFERENCES) {
            apiService.createDefaultCategories();
        } else if (requestType == ApiService.CREATE_DEFAULT_CATEGORIES) {
            startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressBar.setVisibility(View.INVISIBLE);
        next.setOnClickListener(nextListener);
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MaintenanceDialog.show(this);
        } else {
            String message;
            switch (errorCode) {
                case ApiService.CONNECTION_ERROR:
                    message = getResources().getString(R.string.connection_error);
                    break;
                case ApiService.TIMEOUT_ERROR:
                    message = getResources().getString(R.string.timeout_error);
                    break;
                case ApiService.TOO_MANY_REQUESTS:
                    message = getResources().getString(R.string.too_many_requests);
                    break;
                case ApiService.CONFLICT:
                    message = getResources().getString(R.string.email_in_use);
                    break;
                default:
                    message = getResources().getString(R.string.general_error);
            }
            confirmPasswordField.setError(message);
        }
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
