package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.database.DataAccess;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.KeyPackage;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestBuilder;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.services.DataServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class RegisterActivity extends AppCompatActivity implements DataServices.RequestOutcome {

    private EditText emailAddress, loginPassword, confirmLoginPassword, dataPassword, confirmDataPassword;
    private Button register;
    private char[] loginPwd, dataPwd;
    private ArrayList<GeneralCategory> generalCategories;
    private ArrayList<SubCategory> subCategories;

    private DataServices dataServices;

 //   private int retryCount = 0;
  //  private static final int MAX_RETRIES = 3;

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

        dataServices = new DataServices(this, getApplicationContext());

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

                    String refreshToken = dataReceiver[0].get("refreshToken").toString();
                    String accessToken = dataReceiver[0].get("accessToken").toString();

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.REFRESH_TOKEN_KEY, refreshToken);

                    AuthManager.getInstance(getApplicationContext()).saveEntry(
                            AuthManager.ACCESS_TOKEN_KEY, accessToken);

//                    AuthManager.getInstance(getApplicationContext()).saveEntry(
//                            AuthManager.USERNAME_KEY, user.getEmailAddress());
//
//                    AuthManager.getInstance(getApplicationContext()).saveEntry(
//                            AuthManager.PASSWORD_KEY, String.copyValueOf(user.getPassword()));

                    CryptoManager.getInstance().saveMasterKey(getApplicationContext());

                    createCategories();

                 //   createGeneralCategories();

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
//            coordinator.addRequests(RequestBuilder.post(0, RequestBuilder.USER, coordinator,
//                    user.toJSON(), RestRequest.NONE, getApplicationContext()));
            coordinator.addRequests(RequestBuilder.noAuth(0, coordinator, Request.Method.POST,
                    RequestBuilder.USER, user.toJSON(), getApplicationContext()));

            coordinator.start();
        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
        }

    }

    private void createCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                // TODO: need to decrypt and sort the returned data before initalising the application data variable.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            protected void onFailure(String errorMessage) {
                System.out.println("ERROR CREATING CATEGORIES");
            }
        };

        ArrayList<GeneralCategory> generalCategories = DataAccess.getInstance(getApplicationContext()).getGeneralCategories();
        ArrayList<SubCategory> subCategories = DataAccess.getInstance(getApplicationContext()).getSubCategories();

        for (SubCategory subCategory : subCategories) {
            for (GeneralCategory generalCategory : generalCategories) {
                if (subCategory.getGeneralCategoryName().equals(generalCategory.getCategoryName())) {
                    generalCategory.getSubCategories().add(subCategory);
                    break;
                }
            }
        }

//        // TODO: consider setting application category data values here
//
//        RootCategory rootCategories[] = {new RootCategory("Income"), new RootCategory("Expenses")};
//
//        for (int i = 0; i < rootCategories.length; i++) {
//            for (GeneralCategory generalCategory : generalCategories) {
//                if (generalCategory.getRootCategory().equals(rootCategories[i])) {
//                    rootCategories[i].getGeneralCategories().add(generalCategory);
//                }
//            }
//        }
//
//        DataProvider.getInstance().setRootCategories(rootCategories);


        // Shuffle items so that each user's categories are inserted in a different order making
        // for sysadmins to guess a certain category given the ciphertext length.

        Collections.shuffle(generalCategories);

        try {

            JSONArray generalCategoriesArray = new JSONArray();

            for (GeneralCategory generalCategory : generalCategories) {

                JSONArray subCategoriesArray = new JSONArray();

                for (SubCategory subCategory : generalCategory.getSubCategories()) {
                    subCategoriesArray.put(subCategory.encrypt());
                }

                JSONObject category = generalCategory.encrypt();
                category.put("subCategories", subCategoriesArray);

                generalCategoriesArray.put(category);
            }

            JSONObject categories = new JSONObject();
            categories.put("categories", generalCategoriesArray);

            coordinator.addRequests(RequestBuilder.accessTokenAuth(0, coordinator, Request.Method.POST,
                    RequestBuilder.CATEGORY, null, categories, getApplicationContext()));

//            coordinator.addRequests(RequestBuilder.post(0, RequestBuilder.CATEGORY, coordinator, categories,
//                    RestRequest.TOKEN, getApplicationContext()));

            coordinator.start();

        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
        }




    }

//    private void createGeneralCategories() {
//        final JSONObject[] dataReceiver = new JSONObject[1];
//        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
//                this, dataReceiver) {
//
//            @Override
//            protected void onSuccess() {
//                // Process the returned general categories
//
//                generalCategories = new ArrayList<>();
//
//                try {
//                    JSONArray categories = dataReceiver[0].getJSONArray("categories");
//                    for (int i = 0; i < categories.length(); i++) {
//                        GeneralCategory gc = new GeneralCategory(categories.getJSONObject(i));
//                        generalCategories.add(gc);
//                    }
//
//                    createSubCategories();
//
//                } catch (JSONException | UnsupportedEncodingException e) {
//                    // TODO: error handling
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected void onFailure(String errorMessage) {
//                retryCount++;
//                if (retryCount > MAX_RETRIES) {
//                    // ABORT AND PROCEED TO MAIN APP SCREEN WITHOUT ANY DEFAULT CATEGORIES
//                    return;
//                } else {
//                    createGeneralCategories();
//                }
//            }
//        };
//
//        ArrayList<GeneralCategory> categories = DataAccess.getInstance(getApplicationContext()).getGeneralCategories();
//
//        // TODO: shuffle categories to make more anonymous
//
//        Collections.shuffle(categories);
//
//        try {
//            JSONArray jsonArray = new JSONArray();
//            for (GeneralCategory category : categories) {
//                jsonArray.put(category.encrypt());
//            }
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("categories", jsonArray);
//
//            coordinator.addRequests(RequestBuilder.post(0, RequestBuilder.GENERAL_CATEGORY_BULK,
//                    coordinator, jsonObject, RestRequest.TOKEN, getApplicationContext()));
//
//            coordinator.start();
//
//        } catch (Exception e) {
//            // TODO: error handling
//            e.printStackTrace();
//        }
//
//    }
//
//    private void createSubCategories() {
//
//        final JSONObject[] dataReceiver = new JSONObject[1];
//        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
//                this, dataReceiver) {
//
//            @Override
//            protected void onSuccess() {
//
//                // TODO: review logic for transitioning to launch activity. It's not necessary to execute main loadData routine as we already have all the category data we need.
//
//                Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
//                startActivity(intent);
//
////                // Process the returned general categories
////
////                subCategories = new ArrayList<>();
////
////                try {
////                    JSONArray categories = dataReceiver[0].getJSONArray("categories");
////                    for (int i = 0; i < categories.length(); i++) {
////                        SubCategory sc = new SubCategory((JSONObject) categories.get(i));
////                        subCategories.add(sc);
////                    }
////
////                    // SUCCESS! - Continue to main activity
////
////
////                } catch (JSONException | UnsupportedEncodingException e) {
////                    // TODO: error handling
////                    e.printStackTrace();
////                }
//            }
//
//            @Override
//            protected void onFailure(String errorMessage) {
//                retryCount++;
//                if (retryCount > MAX_RETRIES) {
//                    // TODO - ABORT AND PROCEED TO MAIN APP SCREEN WITHOUT ANY DEFAULT CATEGORIES
//                    return;
//                } else {
//                    createGeneralCategories();
//                }
//            }
//        };
//
//        ArrayList<SubCategory> categories = DataAccess.getInstance(getApplicationContext()).getSubCategories();
//
//        for (SubCategory sc: categories) {
//            for (GeneralCategory gc: generalCategories) {
//                if (sc.getGeneralCategoryName().equals(gc.getCategoryName())) {
//                    sc.setGeneralCategoryID(gc.getId());
//                    break;
//                }
//            }
//        }
//
//        try {
//
//            JSONArray jsonArray = new JSONArray();
//            for (SubCategory category : categories) {
//                jsonArray.put(category.encrypt());
//            }
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("categories", jsonArray);
//
//            coordinator.addRequests(RequestBuilder.post(0, RequestBuilder.SUB_CATEGORY_BULK,
//                    coordinator, jsonObject, RestRequest.TOKEN, getApplicationContext()));
//
//            coordinator.start();
//
//        } catch (Exception e) {
//            // TODO: error handling
//            e.printStackTrace();
//        }
//
//    }



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

                dataServices.createUser(user);

                //registerUser(user);
              //  createCategories();
            }
        });
    }

    @Override
    public void onSuccess(int requestType) {
        if (requestType == DataServices.CREATE_USER) {
            dataServices.createDefaultCategories();
        } else if (requestType == DataServices.CREATE_DEFAULT_CATEGORIES) {
            // Proceed to login
        }
    }

    @Override
    public void onUnsuccessfulRequest(String errorMessage) {
        System.out.println(errorMessage);
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
