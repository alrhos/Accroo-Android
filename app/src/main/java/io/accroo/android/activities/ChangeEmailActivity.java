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
import io.accroo.android.services.ApiService;

public class ChangeEmailActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText emailAddress, confirmEmailAddress, loginPassword;
    private Button updateEmailButton;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_email);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            apiService = new ApiService(this, getApplicationContext());
            progressDialog = new ProgressDialog(ChangeEmailActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.submitting));

            emailAddress = findViewById(R.id.new_email);
            confirmEmailAddress = findViewById(R.id.confirm_new_email);
            loginPassword = findViewById(R.id.confirm_password);
            updateEmailButton = findViewById(R.id.update_email_button);
            updateEmailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmailValid()) {
                        return;
                    }
                    if (!isPasswordEntered()) {
                        return;
                    }

                    progressDialog.show();

//                    String newEmail = emailAddress.getText().toString().trim();
//                    int passwordLength = loginPassword.getText().length();
//                    char[] password = new char[passwordLength];
//                    loginPassword.getText().getChars(0, passwordLength, password, 0);
//
//                    apiService.updateEmail(newEmail, password);
                }
            });
        }
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

    private boolean isEmailValid() {
        if (emailAddress.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!emailAddress.getText().toString().equals(confirmEmailAddress.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.email_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isPasswordEntered() {
        if (loginPassword.getText().length() < 1) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.email_updated, Toast.LENGTH_SHORT).show();
        finish();
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
