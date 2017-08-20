package com.paleskyline.navicash.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.services.ApiService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText usernameField, passwordField;
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

            usernameField = (EditText) findViewById(R.id.username);
            passwordField = (EditText) findViewById(R.id.password);
            loginButton = (Button) findViewById(R.id.login_button);

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));

            apiService = new ApiService(this, getApplicationContext());
            addListeners();
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

    private void addListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isValidInput()) {

                    progressDialog.show();

                    String username = usernameField.getText().toString();
                    int passwordLength = passwordField.getText().length();
                    char[] password = new char[passwordLength];
                    passwordField.getText().getChars(0, passwordLength, password, 0);

                    apiService.getRefreshToken(username, password);
                }
            }
        });
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        Intent intent = new Intent(getApplicationContext(), KeyDecryptionActivity.class);
        startActivity(intent);
    }

//    @Override
//    public void onAuthorizationError() {
//
//    }

    @Override
    public void onUnsuccessfulRequest(int requestType, int errorCode) {
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
                message = getResources().getString(R.string.error_incorrect_password);
                break;
            default:
                message = getResources().getString(R.string.general_error);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUnsuccessfulDecryption() {
        progressDialog.dismiss();
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        progressDialog.dismiss();
        System.out.println("GENERAL ERROR");
    }

}

