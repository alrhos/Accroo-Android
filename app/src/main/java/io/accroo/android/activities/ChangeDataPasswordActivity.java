package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.other.Constants;
import io.accroo.android.services.ApiService;

import java.util.Arrays;

public class ChangeDataPasswordActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText currentDataPasswordField, newDataPasswordField, confirmDataPasswordField, loginPasswordField;
    private Button updateDataPassword;
    private ApiService apiService;
    private ProgressDialog progressDialog;
    private char[] loginPassword, dataPassword, newDataPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_data_password);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            apiService = new ApiService(this, getApplicationContext());
            progressDialog = new ProgressDialog(ChangeDataPasswordActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.submitting));
            progressDialog.setCancelable(false);

            currentDataPasswordField = findViewById(R.id.current_data_password);
            newDataPasswordField = findViewById(R.id.new_data_password);
            confirmDataPasswordField = findViewById(R.id.confirm_data_password);
            loginPasswordField = findViewById(R.id.current_login_password);
            updateDataPassword = findViewById(R.id.update_data_password_button);
            updateDataPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isExistingPasswordValid()) {
                        return;
                    }

                    if (!isNewPasswordValid()) {
                        return;
                    }

                    progressDialog.show();

                    int loginPasswordLength = loginPasswordField.getText().length();
                    loginPassword = new char[loginPasswordLength];
                    loginPasswordField.getText().getChars(0, loginPasswordLength, loginPassword, 0);
                    apiService.getKeyPackage(loginPassword);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isExistingPasswordValid() {
        if (loginPasswordField.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_current_login_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentDataPasswordField.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_current_data_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isNewPasswordValid() {
        if (newDataPasswordField.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (newDataPasswordField.getText().length() > Constants.MAX_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!newDataPasswordField.getText().toString().equals(confirmDataPasswordField.getText().toString())) {
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
    public void onSuccess(int requestType) {
        if (requestType == ApiService.GET_KEY_PACKAGE) {
            int dataPasswordLength = currentDataPasswordField.getText().length();
            dataPassword = new char[dataPasswordLength];
            currentDataPasswordField.getText().getChars(0, dataPasswordLength, dataPassword, 0);
            if (apiService.initializeKey(dataPassword)) {
                int newDataPasswordLength = newDataPasswordField.getText().length();
                newDataPassword = new char[newDataPasswordLength];
                newDataPasswordField.getText().getChars(0, newDataPasswordLength, newDataPassword, 0);
                apiService.updatePassword(loginPassword, newDataPassword);
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), R.string.incorrect_password, Toast.LENGTH_SHORT).show();
            }
//        } else if (requestType == ApiService.UPDATE_DATA_PASSWORD) {
//            clearPasswords();
//            progressDialog.dismiss();
//            Toast.makeText(getApplicationContext(), R.string.data_password_updated, Toast.LENGTH_SHORT).show();
//            finish();
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
        clearPasswords();
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

    private void clearPasswords() {
        Arrays.fill(loginPassword, '\u0000');
        Arrays.fill(dataPassword, '\u0000');
        Arrays.fill(newDataPassword, '\u0000');
    }

}
