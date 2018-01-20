package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

import io.accroo.android.R;
import io.accroo.android.other.Constants;
import io.accroo.android.services.ApiService;

public class ChangePasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText currentPassword, newPassword, confirmNewPassword;
    private Button next;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private char[] currentPwd, newPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_password);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            apiService = new ApiService(this, getApplicationContext());
            progressDialog = new ProgressDialog(ChangePasswordActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            currentPassword = findViewById(R.id.current_password);
            newPassword = findViewById(R.id.new_password);
            confirmNewPassword = findViewById(R.id.confirm_new_password);

            next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isInputValid()) {
                        return;
                    }
                    progressDialog.show();
                    apiService.getKeyPackage();
                }
            });

        }
    }

    private boolean isInputValid() {
        if (currentPassword.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_current_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newPassword.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newPassword.getText().toString().equals(confirmNewPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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

    @Override
    public void onSuccess(int requestType) {
        // Check that current password is correct

        int currentPasswordLength = currentPassword.getText().length();
        currentPwd = new char[currentPasswordLength];
        currentPassword.getText().getChars(0, currentPasswordLength, currentPwd, 0);

        if (apiService.initializeKey(currentPwd)) {

        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), R.string.incorrect_password, Toast.LENGTH_SHORT).show();
        }


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
       // clearPasswords();
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

    private void clearPasswords() {
        Arrays.fill(currentPwd, '\u0000');
        Arrays.fill(newPwd, '\u0000');
    }

}
