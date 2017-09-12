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

public class ChangeEmailActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText newEmailField, passwordField;
    private Button updateEmailButton;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiService = new ApiService(this, getApplicationContext());
        progressDialog = new ProgressDialog(ChangeEmailActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.submitting));

        newEmailField = (EditText) findViewById(R.id.new_email);
        passwordField = (EditText) findViewById(R.id.confirm_login_password);
        updateEmailButton = (Button) findViewById(R.id.update_email_button);
        updateEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidEmail()) {
                    return;
                }

                progressDialog.show();

                String newEmail = newEmailField.getText().toString();
                int passwordLength = passwordField.getText().length();
                char[] password = new char[passwordLength];
                passwordField.getText().getChars(0, passwordLength, password, 0);

                apiService.updateEmail(newEmail, password);

            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isValidEmail() {
        return true;
        // TODO: add logic
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

//    public void onAuthorizationError() {
//        progressDialog.dismiss();
//        Toast.makeText(getApplicationContext(), R.string.error_incorrect_password, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onError() {
        progressDialog.dismiss();
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.email_updated, Toast.LENGTH_LONG).show();
        finish();
    }

}
