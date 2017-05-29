package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.RootCategory;
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
import java.util.ArrayList;

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
                System.out.println(errorMessage);
                if (errorMessage.equals(RestRequest.CONNECTION_ERROR) || errorMessage.equals(RestRequest.TIMEOUT_ERROR)) {
                    // Redirect to different layout showing connection error
                    setContentView(R.layout.activity_no_connection);
                    Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                } else {
                    initLayout();
                }
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

        // TODO: experiment with passing in activity name and use to when building the intent.

        public DecryptData() {}

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

            RootCategory[] rc = {new RootCategory("Income"), new RootCategory("Expenses")};
            ArrayList<GeneralCategory> gc = new ArrayList<>();
            ArrayList<SubCategory> sc = new ArrayList<>();
            ArrayList<Transaction> t = new ArrayList<>();

            try {

                JSONArray generalCategories = jsonObjects[0][0].getJSONArray("categories");
                for (int i = 0; i < generalCategories.length(); i++) {
                    GeneralCategory generalCategory = new GeneralCategory(generalCategories.getJSONObject(i));
                    System.out.println(generalCategory.toString());
                    gc.add(generalCategory);
                }

                JSONArray subCategories = jsonObjects[0][1].getJSONArray("categories");
                for (int j = 0; j < subCategories.length(); j++) {
                    SubCategory subCategory = new SubCategory(subCategories.getJSONObject(j));
                    sc.add(subCategory);
                }

                JSONArray transactions = jsonObjects[0][2].getJSONArray("transactions");
                for (int k = 0; k < transactions.length(); k++) {
                    Transaction transaction = new Transaction(transactions.getJSONObject(k));
                    t.add(transaction);
                }

                for (SubCategory s : sc) {
                    for (Transaction tx : t) {
                        if (tx.getSubCategoryID() == s.getId()) {
                            s.getTransactions().add(tx);
                        }
                    }
                }

                for (GeneralCategory g : gc) {
                    for (SubCategory s : sc) {
                        if (s.getGeneralCategoryID() == g.getId()) {
                            g.getSubCategories().add(s);
                        }
                    }
                }

                for (int c = 0; c < rc.length; c++) {
                    for (GeneralCategory g : gc) {
                        if (g.getRootCategory().equals(rc[c].getCategoryName())) {
                            rc[c].getGeneralCategories().add(g);
                        }
                    }
                }

                DataProvider.getInstance().setRootCategories(rc);

                return true;

            } catch (JSONException | UnsupportedEncodingException e) {
                // TODO: error handling
                e.printStackTrace();
                return false;
            }
        }
    }

}
