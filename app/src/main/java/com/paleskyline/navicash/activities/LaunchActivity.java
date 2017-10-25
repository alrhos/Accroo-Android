package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.services.ApiService;

import java.util.Calendar;
import java.util.Date;

public class LaunchActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private Calendar calendar;
    private Date startDate, endDate;
    public static boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialized = true;
        autoLogin();
    }



    private void autoLogin() {
        setContentView(R.layout.activity_launch_loading);

        ApiService apiService = new ApiService(this, getApplicationContext());

        if (apiService.userLoggedIn()) {
            calendar = Calendar.getInstance();
            endDate = calendar.getTime();

            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            startDate = calendar.getTime();

            apiService.getDefaultData(startDate, endDate);
        } else {
            initLayout();
        }

    }

    private void initLayout() {
        setContentView(R.layout.activity_launch);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterStageOneActivity.class));
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
    public void onFailure(int requestType, int errorCode) {
        if (errorCode == ApiService.UNAUTHORIZED) {
            initLayout();
        } else if (errorCode == ApiService.CONNECTION_ERROR || errorCode == ApiService.TIMEOUT_ERROR) {
            setContentView(R.layout.activity_no_connection);
            Button tryAgain = (Button) findViewById(R.id.try_again);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    autoLogin();
                }
            });
        } else if (errorCode == ApiService.GENERAL_ERROR) {
            // TODO: need to handle this
        }
    }

    @Override
    public void onError() {
        System.out.println("GENERAL ERROR");
    }

}
