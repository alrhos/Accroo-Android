package com.paleskyline.accroo.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.model.Preferences;
import com.paleskyline.accroo.model.User;
import com.paleskyline.accroo.other.Constants;
import com.paleskyline.accroo.services.ApiService;

public class RegisterStageTwoActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText dataPassword, confirmDataPassword;
    private Button register;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private String email;
    private char[] loginPwd, dataPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_register_stage_two);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            email = getIntent().getStringExtra("email");
            loginPwd = getIntent().getCharArrayExtra("loginPassword");

            apiService = new ApiService(this, getApplicationContext());
            progressDialog = new ProgressDialog(RegisterStageTwoActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            dataPassword = (EditText) findViewById(R.id.data_password);
            confirmDataPassword = (EditText) findViewById(R.id.confirm_data_password);
            register = (Button) findViewById(R.id.register);

            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isPasswordValid()) {
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterStageTwoActivity.this);
                    builder.setMessage(R.string.password_warning)
                            .setTitle(R.string.important)
                            .setPositiveButton(R.string.continue_on, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    registerAccount();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).create().show();
                }
            });
        }
    }

    private void registerAccount() {
        progressDialog.show();
        int dataPasswordLength = dataPassword.getText().length();
        dataPwd = new char[dataPasswordLength];
        dataPassword.getText().getChars(0, dataPasswordLength, dataPwd, 0);
        User user = new User(email, loginPwd, dataPwd, new Preferences());
        apiService.createUser(user);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private boolean isPasswordValid() {
        if (dataPassword.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (dataPassword.getText().length() > Constants.MAX_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!dataPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int requestType) {
        if (requestType == ApiService.CREATE_USER) {
            apiService.createDefaultCategories();
        } else if (requestType == ApiService.CREATE_DEFAULT_CATEGORIES) {
            startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        String message;
        switch (errorCode) {
            case ApiService.CONNECTION_ERROR:
                message = getResources().getString(R.string.connection_error);
                break;
            case ApiService.TIMEOUT_ERROR:
                message = getResources().getString(R.string.timeout_error);
                break;
            case ApiService.CONFLICT:
                message = getResources().getString(R.string.email_in_use);
                break;
            default:
                message = getResources().getString(R.string.general_error);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
