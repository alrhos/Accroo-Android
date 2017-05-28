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

public class RegisterActivity extends AppCompatActivity {

    private EditText emailAddress, loginPassword, confirmLoginPassword, dataPassword, confirmDataPassword;
    private Button register;
    private char[] loginPwd, dataPwd;
    private ArrayList<GeneralCategory> generalCategories;
    private ArrayList<SubCategory> subCategories;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;

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
                try {

                    String token = dataReceiver[0].get("token").toString();

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.TOKEN_KEY, token);

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.USERNAME_KEY, user.getEmailAddress());

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
            protected void onFailure(String errorMessage) {
                if (errorMessage != null) {
                    System.out.println("ERROR: " + errorMessage);
                } else {
                    System.out.println("COMMS ERROR");
                }
                Arrays.fill(loginPwd, '\u0000');
                Arrays.fill(dataPwd, '\u0000');
            }
        };

        System.out.println(user.toJSON());
        System.out.println("-----------------------------");

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
                System.out.println("GENERAL CATEGORIES CREATED");
                System.out.println(dataReceiver[0].toString());

                // Process the returned general categories

                generalCategories = new ArrayList<>();

                try {
                    JSONArray categories = dataReceiver[0].getJSONArray("categories");
                    for (int i = 0; i < categories.length(); i++) {
                        GeneralCategory gc = new GeneralCategory((JSONObject) categories.get(i));
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
                System.out.println("GENERAL CATEGORY ERROR");
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
                System.out.println("SUB CATEGORIES CREATED");
                System.out.println(dataReceiver[0].toString());

                // Process the returned general categories

                subCategories = new ArrayList<>();

                try {
                    JSONArray categories = dataReceiver[0].getJSONArray("categories");
                    for (int i = 0; i < categories.length(); i++) {
                        SubCategory sc = new SubCategory((JSONObject) categories.get(i));
                        subCategories.add(sc);
                    }

                    // SUCCESS! - Continue to main activity


                } catch (JSONException | UnsupportedEncodingException e) {
                    // TODO: error handling
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailure(String errorMessage) {
                System.out.println("SUB CATEGORY ERROR");
                System.out.println(errorMessage);
                retryCount++;
                if (retryCount > MAX_RETRIES) {
                    // ABORT AND PROCEED TO MAIN APP SCREEN WITHOUT ANY DEFAULT CATEGORIES
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
                    //sc.setGeneralCategoryID(101);
                    break;
                }
            }
        }

        for (GeneralCategory g : generalCategories) {
            System.out.println(g.toString());
        }
        for (SubCategory s : categories) {
            System.out.println(s.toString());
        }


        try {
            JSONArray jsonArray = new JSONArray();
            for (SubCategory category : categories) {
                jsonArray.put(category.encrypt());
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("categories", jsonArray);

            System.out.println(jsonObject.toString());

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
