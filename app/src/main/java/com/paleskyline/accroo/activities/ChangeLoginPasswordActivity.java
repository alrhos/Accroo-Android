package com.paleskyline.accroo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.other.Constants;
import com.paleskyline.accroo.services.ApiService;

public class ChangeLoginPasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText existingPasswordField, newPasswordField, confirmNewPasswordField;
    private Button updatePasswordButton;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_login_password);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            apiService = new ApiService(this, getApplicationContext());
            progressDialog = new ProgressDialog(ChangeLoginPasswordActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.submitting));

            existingPasswordField = findViewById(R.id.confirm_login_password);
            newPasswordField = findViewById(R.id.new_login_password);
            confirmNewPasswordField = findViewById(R.id.confirm_new_login_password);
            updatePasswordButton = findViewById(R.id.update_login_password_button);

            updatePasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!isPasswordValid()) {
                        return;
                    }

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
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isPasswordValid() {
        if (existingPasswordField.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_current_login_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPasswordField.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPasswordField.getText().length() > Constants.MAX_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPasswordField.getText().toString().equals(confirmNewPasswordField.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        String message;
        switch (errorCode) {
            case ApiService.UNAUTHORIZED:
                message = getResources().getString(R.string.incorrect_password);
                break;
            case ApiService.CONNECTION_ERROR:
                message = getResources().getString(R.string.connection_error);
                break;
            case ApiService.TIMEOUT_ERROR:
                message = getResources().getString(R.string.timeout_error);
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

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.login_password_updated, Toast.LENGTH_SHORT).show();
        finish();
    }

}
