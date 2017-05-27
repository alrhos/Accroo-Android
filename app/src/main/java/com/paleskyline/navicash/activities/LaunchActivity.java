package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;

import org.json.JSONObject;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoLogin();
    }

    private void autoLogin() {
        try {
            // Check that credentials exist
            AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.USERNAME_KEY);
            AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.PASSWORD_KEY);
            AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.ENCRYPTION_KEY);
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

                // DECRYPT DATA

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            protected void onFailure(String errorMessage) {
                System.out.println("ERROR");
                System.out.println(errorMessage);
                initLayout();
            }
        };

        // TODO: get system date, lookup id in local db and add to transaction request.

        coordinator.addRequests(
                RestMethods.get(0, RestMethods.GENERAL_CATEGORY, null, coordinator, RestRequest.TOKEN),
                RestMethods.get(1, RestMethods.SUB_CATEGORY, null, coordinator, RestRequest.TOKEN),
                RestMethods.get(2, RestMethods.TRANSACTION_PARAM, "1", coordinator, RestRequest.TOKEN));

        coordinator.start();
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

}
