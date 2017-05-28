package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.model.Transaction;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;
import com.paleskyline.navicash.services.DataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_loading);
        autoLogin();
    }

    private void autoLogin() {
        try {
            // Check that credentials exist
            AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.USERNAME_KEY);
            AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.PASSWORD_KEY);
            // Init master key
            CryptoManager.getInstance().initMasterKey(getApplicationContext());

            loadUserData();
        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
            initLayout();
        }
    }

    private void loadUserData() {
        final JSONObject[] dataReceiver = new JSONObject[3];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                System.out.println("LOAD DATA SUCCESS");
                System.out.println(dataReceiver[0].toString());
                System.out.println(dataReceiver[1].toString());
                System.out.println(dataReceiver[2].toString());

                new DecryptData().execute(dataReceiver);

            }

            @Override
            protected void onFailure(String errorMessage) {
                System.out.println("ERROR");
                System.out.println(errorMessage);
                initLayout();
            }
        };

        // TODO: get system date, lookup id in local db and add to transaction request.

        try {

            coordinator.addRequests(
                    RestMethods.get(0, RestMethods.GENERAL_CATEGORY, null, coordinator, RestRequest.TOKEN, getApplicationContext()),
                    RestMethods.get(1, RestMethods.SUB_CATEGORY, null, coordinator, RestRequest.TOKEN, getApplicationContext()),
                    RestMethods.get(2, RestMethods.TRANSACTION, "1", coordinator, RestRequest.TOKEN, getApplicationContext()));

            coordinator.start();
        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
        }
    }

    private void initLayout() {
        setContentView(R.layout.activity_launch);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    class DecryptData extends AsyncTask<JSONObject[], Boolean, Boolean> {

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if (success) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                // TODO: error handling
            }

        }

        @Override
        protected Boolean doInBackground(JSONObject[]... jsonObjects) {
            try {
                JSONArray generalCategories = jsonObjects[0][0].getJSONArray("categories");
                for (int i = 0; i < generalCategories.length(); i++) {
                    GeneralCategory generalCategory = new GeneralCategory(generalCategories.getJSONObject(i));
                    DataProvider.getInstance().getGeneralCategories().add(generalCategory);
                }
                JSONArray subCategories = jsonObjects[0][1].getJSONArray("categories");
                for (int j = 0; j < subCategories.length(); j++) {
                    SubCategory subCategory = new SubCategory(subCategories.getJSONObject(j));
                    DataProvider.getInstance().getSubCategories().add(subCategory);
                }
                JSONArray transactions = jsonObjects[0][2].getJSONArray("transactions");
                for (int k = 0; k < transactions.length(); k++) {
                    Transaction transaction = new Transaction(transactions.getJSONObject(k));
                    DataProvider.getInstance().getTransactions().add(transaction);
                }
                return true;
            } catch (JSONException | UnsupportedEncodingException e) {
                // TODO: error handling
                e.printStackTrace();
                return false;
            }
        }
    }

}
