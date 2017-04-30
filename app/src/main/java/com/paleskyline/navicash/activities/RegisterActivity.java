package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;

import org.json.JSONObject;

import java.util.Arrays;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailAddress, loginPassword, confirmLoginPassword, dataPassword, confirmDataPassword;
    private Button register;
    private char[] loginPwd, dataPwd;

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

        addListeners();
    }

    private boolean isEmailValid() {
        boolean valid = true;
        String emailString = emailAddress.getText().toString();
        String emailRegEx = ".*";
        if (!emailString.matches(emailRegEx)) {
            // TODO: Add toast
            return false;
        }
        return true;
    }

    private boolean isLoginPasswordValid() {
        // Check if passwords match
        if (!loginPassword.getText().toString().equals(confirmLoginPassword.getText().toString())) {
            // TODO: toast that passwords do not match
            return false;
        }

        // Check password length
        if (loginPassword.getText().length() < 10) {
            // TODO: toast that password length is too short
            return false;
        }

        // Could add another condition here to check password complexity


        // Return true if all tests pass

        return true;

    }

    private boolean isDataPasswordValid() {
        // Check if passwords match
        if (!dataPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
            // TODO: toast that passwords do not match
            return false;
        }

        // Check password length
        if (dataPassword.getText().length() < 10) {
            // TODO: toast that password length is too short
            return false;
        }

        // Could add another condition here to check password complexity


        // Return true if all checks pass

        return true;

    }


    private void registerUser(final User user) {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                System.out.println("SUCCESS!!!");
                try {

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.USERNAME, user.getEmailAddress());

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.PASSWORD_KEY, String.copyValueOf(user.getPassword()));

                    CryptoManager.getInstance().saveMasterKey(getApplicationContext());

                    // Populate general categories
                    createGeneralCategories();

                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO: raise toast
                } finally {
                    Arrays.fill(loginPwd, '\u0000');
                    Arrays.fill(dataPwd, '\u0000');
                }
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("ERROR: " + json.toString());
                Arrays.fill(loginPwd, '\u0000');
                Arrays.fill(dataPwd, '\u0000');
            }
        };

        System.out.println(user.toJSON());
        coordinator.addRequests(RestMethods.post(0, RestMethods.REGISTER, coordinator, user.toJSON()));
        coordinator.start();
    }

    private void createGeneralCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                System.out.println("GENERAL CATEGORIES CREATED");
                createSubCategories();
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("GENERAL CATEGORY ERROR");
            }
        };

        // TODO: add requests and start coordinator (needs to get category data from local db)

    }

    private void createSubCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                System.out.println("SUB CATEGORIES CREATED");
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("SUB CATEGORY ERROR");
            }
        };

        // TODO: add requests and start coordinator (needs to get category data from local db)
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

                int loginPasswordLength = loginPassword.getText().length();
                loginPwd = new char[loginPasswordLength];
                loginPassword.getText().getChars(0, loginPasswordLength, loginPwd, 0);

                // PROCESS DATA PASSWORD

                int dataPasswordLength = dataPassword.getText().length();
                dataPwd = new char[dataPasswordLength];
                dataPassword.getText().getChars(0, dataPasswordLength, dataPwd, 0);

                KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPwd);
                User user = new User(emailAddress.getText().toString(), loginPwd, keyPackage);

                registerUser(user);
            }
        });
    }

}
