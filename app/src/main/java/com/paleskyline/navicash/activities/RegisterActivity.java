package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.model.KeyPackage;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.services.ApiService;

public class RegisterActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText emailAddress, loginPassword, confirmLoginPassword, dataPassword, confirmDataPassword;
    private Button register;
    private char[] loginPwd, dataPwd;

    private ApiService apiService;

    private static final String INVALID_EMAIL = "Invalid email address";
    private static final String PASSWORD_MISMATCH = "passwords do not match";
    private static final String PASSWORD_TOO_SHORT = "password must be at least 10 characters";
    private static final String CRITICAL_ERROR = "An error has occurred";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailAddress = (EditText) findViewById(R.id.email);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        confirmLoginPassword = (EditText) findViewById(R.id.loginPasswordConfirm);
        dataPassword = (EditText) findViewById(R.id.dataPassword);
        confirmDataPassword = (EditText) findViewById(R.id.dataPasswordConfirm);
        register = (Button) findViewById(R.id.register_button);

        apiService = new ApiService(this, getApplicationContext());

        addListeners();
    }

    // TODO: add proper validation
    private boolean isEmailValid() {
        boolean valid = true;
        String emailString = emailAddress.getText().toString();
        String emailRegEx = ".*";
        if (!emailString.matches(emailRegEx)) {
            Toast.makeText(getApplicationContext(), INVALID_EMAIL, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isLoginPasswordValid() {
        if (!loginPassword.getText().toString().equals(confirmLoginPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Login " + PASSWORD_MISMATCH, Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: Change password length value
        if (loginPassword.getText().length() < 1) {
            Toast.makeText(getApplicationContext(), "Login " + PASSWORD_TOO_SHORT,
                    Toast.LENGTH_SHORT).show();
            return false;
        }



        return true;
    }

    private boolean isDataPasswordValid() {
        if (!dataPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Data " + PASSWORD_MISMATCH, Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: Change password length value
        if (dataPassword.getText().length() < 1) {
            Toast.makeText(getApplicationContext(), "Data " + PASSWORD_TOO_SHORT,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: add check to ensure password complexity

        return true;
    }

    private void addListeners() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isEmailValid()) {
                    return;
                }

                if (!isLoginPasswordValid()) {
                    return;
                }

                if (!isDataPasswordValid()) {
                    return;
                }

                // All checks passed - register account

                // PROCESS LOGIN PASSWORD

                // TODO: review password security here

                int loginPasswordLength = loginPassword.getText().length();
                loginPwd = new char[loginPasswordLength];
                loginPassword.getText().getChars(0, loginPasswordLength, loginPwd, 0);

                // PROCESS DATA PASSWORD

                int dataPasswordLength = dataPassword.getText().length();
                dataPwd = new char[dataPasswordLength];
                dataPassword.getText().getChars(0, dataPasswordLength, dataPwd, 0);

                KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPwd);
                User user = new User(emailAddress.getText().toString(), loginPwd, keyPackage);

                apiService.createUser(user);

            }
        });
    }

    @Override
    public void onSuccess(int requestType) {
        if (requestType == ApiService.CREATE_USER) {
            apiService.createDefaultCategories();
        } else if (requestType == ApiService.CREATE_DEFAULT_CATEGORIES) {
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAuthorizationError() {

    }

    @Override
    public void onUnsuccessfulRequest(int requestType, int errorCode) {
        System.out.println("ERROR");
    }

    @Override
    public void onUnsuccessfulDecryption() {
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        System.out.println("GENERAL ERROR");
    }

}
