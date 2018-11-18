package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.joda.time.DateTime;

import io.accroo.android.R;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.services.ApiService;

import java.util.Calendar;
import java.util.Date;

public class LaunchActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    //private Calendar calendar;
    private DateTime startDate, endDate;
    public static boolean initialized = false;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialized = true;
        autoLogin();
    }

    private void autoLogin() {
        setContentView(R.layout.activity_launch_loading);
        apiService = new ApiService(this, getApplicationContext());

        if (apiService.userLoggedIn()) {
//            calendar = Calendar.getInstance();
//            endDate = calendar.getTime();
//
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.HOUR_OF_DAY, 0);
//            calendar.set(Calendar.MINUTE, 0);
//            calendar.set(Calendar.SECOND, 0);
//            calendar.set(Calendar.MILLISECOND, 0);
//
//            startDate = calendar.getTime();

            endDate = new DateTime(); // TODO the time should probably be set to 23:59:59.999
            startDate = new DateTime(endDate.getYear(), endDate.getMonthOfYear(), 1,
                    0, 0, 0, 0);


            System.out.println(startDate.toDateTime());
            System.out.println(endDate.toDateTime());

            apiService.getDefaultData(startDate, endDate);
        } else {
            initLayout();
        }
    }

    private void initLayout() {
        setContentView(R.layout.activity_launch);

        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }

    @Override
    public void onSuccess(int requestType) {
        Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("startDate", startDate.getTime());
//        intent.putExtra("endDate", endDate.getTime());
        intent.putExtra("startDate", startDate.getMillis());
        intent.putExtra("endDate", endDate.getMillis());
        startActivity(intent);
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        if (errorCode == ApiService.UNAUTHORIZED) {
            initLayout();
        } else if (errorCode == ApiService.CONNECTION_ERROR || errorCode == ApiService.TIMEOUT_ERROR ||
                errorCode == ApiService.TOO_MANY_REQUESTS || errorCode == ApiService.ORIGIN_UNAVAILABLE) {
            if (errorCode == ApiService.ORIGIN_UNAVAILABLE) {
                MaintenanceDialog.show(this);
            }
            setContentView(R.layout.activity_no_connection);
            Button tryAgain = findViewById(R.id.try_again);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    autoLogin();
                }
            });
        } else {
            onError();
        }
    }

    @Override
    public void onError() {
        apiService.logout();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
