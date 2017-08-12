package com.paleskyline.navicash.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.services.ApiService;

public class ChangeDataPasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText currentDataPasswordField, newDataPasswordField, confirmDataPasswordField, loginPasswordField;
    private Button updateDataPassword;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_data_password);

        apiService = new ApiService(this, getApplicationContext());
        progressDialog = new ProgressDialog(ChangeDataPasswordActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.submitting));

        currentDataPasswordField = (EditText) findViewById(R.id.current_data_password);
        newDataPasswordField = (EditText) findViewById(R.id.new_data_password);
        confirmDataPasswordField = (EditText) findViewById(R.id.confirm_data_password);
        loginPasswordField = (EditText) findViewById(R.id.current_login_password);
        updateDataPassword = (Button) findViewById(R.id.update_data_password_button);
        updateDataPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add validation login

                // Check new passwords are the same
                // Check new password is not the same
                // Check password strength etc.

                progressDialog.show();

                int loginPasswordLength = loginPasswordField.getText().length();
                char[] loginPassword = new char[loginPasswordLength];
                loginPasswordField.getText().getChars(0, loginPasswordLength, loginPassword, 0);

                int dataPasswordLength = currentDataPasswordField.getText().length();
                char[] dataPassword = new char[dataPasswordLength];
                currentDataPasswordField.getText().getChars(0, dataPasswordLength, dataPassword, 0);

            }
        });

    }


    @Override
    public void onUnsuccessfulRequest(int requestType, int errorCode) {
        progressDialog.dismiss();
        String message;
        switch (errorCode) {
            case ApiService.UNAUTHORIZED:
                message = getResources().getString(R.string.error_incorrect_password);
                break;
            case ApiService.CONNECTION_ERROR:
                message = getResources().getString(R.string.connection_error);
                break;
            case ApiService.TIMEOUT_ERROR:
                message = getResources().getString(R.string.timeout_error);
                break;
            case ApiService.EMAIL_IN_USE:
                message = getResources().getString(R.string.email_in_use);
                break;
            default:
                message = getResources().getString(R.string.general_error);
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

//    public void onAuthorizationError() {
//        progressDialog.dismiss();
//        Toast.makeText(getApplicationContext(), R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onUnsuccessfulDecryption() {
        progressDialog.dismiss();
    }

    @Override
    public void onGeneralError() {
        progressDialog.dismiss();
    }

    @Override
    public void onSuccess(int requestType) {
//        progressDialog.dismiss();
//        Toast.makeText(getApplicationContext(), R.string.email_updated, Toast.LENGTH_LONG).show();
//        finish();
    }


}
