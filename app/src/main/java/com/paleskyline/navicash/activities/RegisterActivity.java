package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.database.DataAccess;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
        // TODO: add proper regex
        String emailRegEx = ".*";
        if (!emailString.matches(emailRegEx)) {
            Toast.makeText(getApplicationContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isLoginPasswordValid() {
        // Check if passwords match
        if (!loginPassword.getText().toString().equals(confirmLoginPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Login passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: Check password length
        if (loginPassword.getText().length() < 1) {
            Toast.makeText(getApplicationContext(), "Login password must be at least 10 characters",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        // Could add another condition here to check password complexity


        // Return true if all tests pass

        return true;

    }

    private boolean isDataPasswordValid() {
        // Check if passwords match
        if (!dataPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Data passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // TODO: Change password length value
        if (dataPassword.getText().length() < 1) {
            Toast.makeText(getApplicationContext(), "Data password must be at least 10 characters",
                    Toast.LENGTH_SHORT).show();
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
                System.out.println("DATA RECEIVED " + dataReceiver[0]);
                try {

                    JSONObject json = dataReceiver[0];
                    String token = json.getString("token");

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.TOKEN_KEY, token);

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.USERNAME_KEY, user.getEmailAddress());

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.PASSWORD_KEY, String.copyValueOf(user.getPassword()));

                    CryptoManager.getInstance().saveMasterKey(getApplicationContext());



                    System.out.println("TOKEN IS: " + AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.TOKEN_KEY));

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
                if (json != null) {
                    System.out.println("ERROR: " + json.toString());
                } else {
                    System.out.println("COMMS ERROR");
                }
                Arrays.fill(loginPwd, '\u0000');
                Arrays.fill(dataPwd, '\u0000');
            }
        };

        System.out.println(user.toJSON());
        System.out.println("-----------------------------");

        coordinator.addRequests(RestMethods.post(0, RestMethods.REGISTER, coordinator,
                user.toJSON(), RestRequest.NONE));
        coordinator.start();
    }

    private void createGeneralCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                System.out.println("GENERAL CATEGORIES CREATED");
                // We need to get the general categories here to get the IDs
                //createSubCategories();
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("GENERAL CATEGORY ERROR");
            }
        };

        ArrayList<GeneralCategory> categories = DataAccess.getInstance(getApplicationContext()).getGeneralCategories();

        // TODO: shuffle categories to make more anonymous

        try {
            JSONArray jsonArray = new JSONArray();
            for (GeneralCategory category : categories) {
                System.out.println(category.toString());
                jsonArray.put(category.encrypt());
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("categories", jsonArray);
            System.out.println(jsonObject.toString());
            coordinator.addRequests(RestMethods.post(0, RestMethods.GENERAL_CATEGORY_BULK,
                    coordinator, jsonObject, RestRequest.TOKEN));
            coordinator.start();
        } catch (JSONException e) {
            // TODO: error handling
            e.printStackTrace();
        }

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
