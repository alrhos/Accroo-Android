package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.services.ApiService;

import java.util.Calendar;
import java.util.Date;

public class LaunchActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private Calendar calendar;
    private Date startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_loading);
        autoLogin();
    }



    private void autoLogin() {
        try {

            // Check if refresh token exists

            AuthManager.getInstance(getApplicationContext()).getEntry(AuthManager.REFRESH_TOKEN_KEY);

            // Init master key

            CryptoManager.getInstance().initMasterKey(getApplicationContext());

            ApiService apiService = new ApiService(this, getApplicationContext());

            calendar = Calendar.getInstance();

            endDate = calendar.getTime();

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            startDate = calendar.getTime();

            apiService.getDefaultData(startDate, endDate);

        } catch (Exception e) {
            // TODO: exception handling
            e.printStackTrace();
            initLayout();
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
    public void onSuccess(int requestType) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("startDate", startDate.getTime());
        intent.putExtra("endDate", endDate.getTime());
        startActivity(intent);
    }

    @Override
    public void onUnsuccessfulRequest(int requestType, int errorCode) {
        if (errorCode == ApiService.UNAUTHORIZED) {
            initLayout();
        } else if (errorCode == ApiService.CONNECTION_ERROR || errorCode == ApiService.TIMEOUT_ERROR) {
            setContentView(R.layout.activity_no_connection);
        } else if (errorCode == ApiService.GENERAL_ERROR) {
            // TODO: need to handle this
        }
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
