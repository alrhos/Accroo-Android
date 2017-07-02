package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.network.RequestBuilder;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestRequest;
import com.paleskyline.navicash.services.DecryptData;

import org.json.JSONObject;

public class LaunchActivity extends AppCompatActivity implements DecryptData.OnDecryptionComplete {

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
        final JSONObject[] dataReceiver = new JSONObject[2];
        RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                this, dataReceiver) {

            @Override
            protected void onSuccess() {
                new DecryptData(LaunchActivity.this).execute(dataReceiver);
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
                    RequestBuilder.get(0, RequestBuilder.CATEGORY, null, coordinator, RestRequest.TOKEN, getApplicationContext()),
                    RequestBuilder.get(1, RequestBuilder.TRANSACTION_GET, "1", coordinator, RestRequest.TOKEN, getApplicationContext()));

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

    @Override
    public void onSuccessfulDecryption() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUnsuccessfulDecryption() {
        // TODO - exception handling
        System.out.println("AN ERROR OCCURRED DURING STARTUP DECRYPTION");
    }

}
