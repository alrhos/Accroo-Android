package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.services.ApiService;

public class LoginActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText usernameField, passwordField;
    private TextView forgotPassword;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_login);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            usernameField = findViewById(R.id.email);
            passwordField = findViewById(R.id.password);
            loginButton = findViewById(R.id.next);
            forgotPassword = findViewById(R.id.forgot_password);

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            apiService = new ApiService(this, getApplicationContext());

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValidInput()) {
                        progressDialog.show();
                        String username = usernameField.getText().toString().trim();
                        int passwordLength = passwordField.getText().length();
                        char[] password = new char[passwordLength];
                        passwordField.getText().getChars(0, passwordLength, password, 0);
                        apiService.login(username, password);
                    }
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
                }
            });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isValidInput() {
        if (usernameField.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (passwordField.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
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
        progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), KeyDecryptionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        String message;
        switch (errorCode) {
            case ApiService.CONNECTION_ERROR:
                message = getResources().getString(R.string.connection_error);
                break;
            case ApiService.TIMEOUT_ERROR:
                message = getResources().getString(R.string.timeout_error);
                break;
            case ApiService.UNAUTHORIZED:
                message = getResources().getString(R.string.invalid_username_or_password);
                break;
            default:
                message = getResources().getString(R.string.general_error);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}

