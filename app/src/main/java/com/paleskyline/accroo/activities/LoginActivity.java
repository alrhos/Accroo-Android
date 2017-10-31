package com.paleskyline.accroo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.services.ApiService;

/**
 * A login screen that offers login via email/password.
 */
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
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_login);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            usernameField = (EditText) findViewById(R.id.email);
            passwordField = (EditText) findViewById(R.id.password);
            loginButton = (Button) findViewById(R.id.next);
            forgotPassword = (TextView) findViewById(R.id.forgot_password);

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            apiService = new ApiService(this, getApplicationContext());

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValidInput()) {

                        progressDialog.show();

                        String username = usernameField.getText().toString();
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


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }

//    private void addListeners() {
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                if (isValidInput()) {
//
//                    progressDialog.show();
//
//                    String username = usernameField.getText().toString();
//                    int passwordLength = passwordField.getText().length();
//                    char[] password = new char[passwordLength];
//                    passwordField.getText().getChars(0, passwordLength, password, 0);
//
//                    apiService.login(username, password);
//                }
//            }
//        });
//    }

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
                message = getResources().getString(R.string.incorrect_password);
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
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}

