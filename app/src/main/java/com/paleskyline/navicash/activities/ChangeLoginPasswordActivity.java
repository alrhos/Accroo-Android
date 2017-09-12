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

public class ChangeLoginPasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText existingPasswordField, newPasswordField, confirmNewPasswordField;
    private Button updatePasswordButton;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        apiService = new ApiService(this, getApplicationContext());
        progressDialog = new ProgressDialog(ChangeLoginPasswordActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.submitting));

        existingPasswordField = (EditText) findViewById(R.id.confirm_login_password);
        newPasswordField = (EditText) findViewById(R.id.new_login_password);
        confirmNewPasswordField = (EditText) findViewById(R.id.confirm_new_login_password);
        updatePasswordButton = (Button) findViewById(R.id.update_login_password_button);

        updatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: add validation login

                // Check new passwords are equal
                // Check new password is not the same
                // Length checks etc.

                progressDialog.show();

                int currentPasswordLength = existingPasswordField.getText().length();
                char[] currentPassword = new char[currentPasswordLength];
                existingPasswordField.getText().getChars(0, currentPasswordLength, currentPassword, 0);

                int newPasswordLength = newPasswordField.getText().length();
                char[] newPassword = new char[newPasswordLength];
                newPasswordField.getText().getChars(0, newPasswordLength, newPassword, 0);

                apiService.updateLoginPassword(currentPassword, newPassword);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    public void onFailure(int requestType, int errorCode) {
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

    @Override
    public void onError() {
        progressDialog.dismiss();
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.login_password_updated, Toast.LENGTH_LONG).show();
        finish();
    }

}
