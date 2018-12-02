package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.model.Account;
import io.accroo.android.model.Preferences;
import io.accroo.android.model.User;
import io.accroo.android.other.Constants;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class RegistrationActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText emailAddress, confirmEmailAddress, password, confirmPassword;
    private TextView agree;
    private Button register;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private Account account;
    private char[] pwd;
    private boolean displayPasswordWarning = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_registration);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            emailAddress = findViewById(R.id.email);
            confirmEmailAddress = findViewById(R.id.confirm_email);
            password = findViewById(R.id.password);
            confirmPassword = findViewById(R.id.confirm_password);
            register = findViewById(R.id.register);
            agree = findViewById(R.id.accept_terms);
            agree.setMovementMethod(LinkMovementMethod.getInstance());

            progressDialog = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            apiService = new ApiService(this, getApplicationContext());

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmailValid()) {
                        return;
                    }
                    if (!isPasswordValid()) {
                        return;
                    }

                    Utils.hideSoftKeyboard(RegistrationActivity.this);

                    if(displayPasswordWarning) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                        builder.setMessage(R.string.password_warning)
                                .setTitle(R.string.important)
                                .setPositiveButton(R.string.continue_on, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        displayPasswordWarning = false;
                                        createAccount();
//                                        progressDialog.show();
//                                        int passwordLength = password.getText().length();
//                                        pwd = new char[passwordLength];
//                                        password.getText().getChars(0, passwordLength, pwd, 0);
//                                        User user = new User(emailAddress.getText().toString(), pwd, new Preferences());
//                                        apiService.createUser(user);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {}
                                }).create().show();
                    } else {
                        createAccount();
                    }
                }
            });
        }
    }

    private void createAccount() {
        progressDialog.show();
        account = new Account(emailAddress.getText().toString());
        apiService.createAccount(account);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(RegistrationActivity.this);
    }

    private boolean isEmailValid() {
        if (emailAddress.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!emailAddress.getText().toString().equals(confirmEmailAddress.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.email_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordValid() {
        if (password.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.getText().length() > Constants.MAX_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            int passwordLength = password.getText().length();
            pwd = new char[passwordLength];
            password.getText().getChars(0, passwordLength, pwd, 0);
            apiService.createKey(pwd);
        } else if (requestType == ApiService.CREATE_KEY) {
            apiService.updatePreferences(new Preferences());
        } else if (requestType == ApiService.UPDATE_PREFERENCES) {
            apiService.createDefaultCategories();
        } else if (requestType == ApiService.CREATE_DEFAULT_CATEGORIES) {
            progressDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.ORIGIN_UNAVAILABLE || errorCode == ApiService.SERVICE_UNAVAILABLE) {
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
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
