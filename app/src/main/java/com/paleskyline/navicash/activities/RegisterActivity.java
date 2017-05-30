package com.paleskyline.navicash.activities;

import android.content.Intent;
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
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailAddress, loginPassword, confirmLoginPassword, dataPassword, confirmDataPassword;
    private Button register;
    private char[] loginPwd, dataPwd;
    private ArrayList<GeneralCategory> generalCategories;
    private ArrayList<SubCategory> subCategories;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;

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


    private void registerUser(final User user) {

        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                try {

                    // TODO: review password security here

                    String token = dataReceiver[0].get("token").toString();

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.TOKEN_KEY, token);

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.USERNAME_KEY, user.getEmailAddress());

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.PASSWORD_KEY, String.copyValueOf(user.getPassword()));

                    CryptoManager.getInstance().saveMasterKey(getApplicationContext());

                    createGeneralCategories();

                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), CRITICAL_ERROR, Toast.LENGTH_LONG).show();
                } finally {
                    Arrays.fill(loginPwd, '\u0000');
                    Arrays.fill(dataPwd, '\u0000');
                }
            }

            @Override
            protected void onFailure(String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                // TODO: error handling
                Arrays.fill(loginPwd, '\u0000');
                Arrays.fill(dataPwd, '\u0000');
            }
        };

        try {
            coordinator.addRequests(RestMethods.post(0, RestMethods.REGISTER, coordinator,
                    user.toJSON(), RestRequest.NONE, getApplicationContext()));

            coordinator.start();
        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
        }

    }

    private void createGeneralCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                // Process the returned general categories

                generalCategories = new ArrayList<>();

                try {
                    JSONArray categories = dataReceiver[0].getJSONArray("categories");
                    for (int i = 0; i < categories.length(); i++) {
                        GeneralCategory gc = new GeneralCategory(categories.getJSONObject(i));
                        generalCategories.add(gc);
                    }

                    createSubCategories();

                } catch (JSONException | UnsupportedEncodingException e) {
                    // TODO: error handling
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailure(String errorMessage) {
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    // ABORT AND PROCEED TO MAIN APP SCREEN WITHOUT ANY DEFAULT CATEGORIES
                    return;
                } else {
                    createGeneralCategories();
                }
            }
        };

        ArrayList<GeneralCategory> categories = DataAccess.getInstance(getApplicationContext()).getGeneralCategories();

        // TODO: shuffle categories to make more anonymous

        Collections.shuffle(categories);

        try {
            JSONArray jsonArray = new JSONArray();
            for (GeneralCategory category : categories) {
                jsonArray.put(category.encrypt());
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("categories", jsonArray);

            coordinator.addRequests(RestMethods.post(0, RestMethods.GENERAL_CATEGORY_BULK,
                    coordinator, jsonObject, RestRequest.TOKEN, getApplicationContext()));

            coordinator.start();

        } catch (Exception e) {
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

                // TODO: review logic for transitioning to launch activity. It's not necessary to execute main loadData routine as we already have all the category data we need.

                Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
                startActivity(intent);

//                // Process the returned general categories
//
//                subCategories = new ArrayList<>();
//
//                try {
//                    JSONArray categories = dataReceiver[0].getJSONArray("categories");
//                    for (int i = 0; i < categories.length(); i++) {
//                        SubCategory sc = new SubCategory((JSONObject) categories.get(i));
//                        subCategories.add(sc);
//                    }
//
//                    // SUCCESS! - Continue to main activity
//
//
//                } catch (JSONException | UnsupportedEncodingException e) {
//                    // TODO: error handling
//                    e.printStackTrace();
//                }
            }

            @Override
            protected void onFailure(String errorMessage) {
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    // TODO - ABORT AND PROCEED TO MAIN APP SCREEN WITHOUT ANY DEFAULT CATEGORIES
                    return;
                } else {
                    createGeneralCategories();
                }
            }
        };

        ArrayList<SubCategory> categories = DataAccess.getInstance(getApplicationContext()).getSubCategories();

        for (SubCategory sc: categories) {
            for (GeneralCategory gc: generalCategories) {
                if (sc.getGeneralCategoryName().equals(gc.getCategoryName())) {
                    sc.setGeneralCategoryID(gc.getId());
                    break;
                }
            }
        }

        try {

            JSONArray jsonArray = new JSONArray();
            for (SubCategory category : categories) {
                jsonArray.put(category.encrypt());
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("categories", jsonArray);

            coordinator.addRequests(RestMethods.post(0, RestMethods.SUB_CATEGORY_BULK,
                    coordinator, jsonObject, RestRequest.TOKEN, getApplicationContext()));

            coordinator.start();

        } catch (Exception e) {
            // TODO: error handling
            e.printStackTrace();
        }

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

                registerUser(user);
            }
        });
    }

}
