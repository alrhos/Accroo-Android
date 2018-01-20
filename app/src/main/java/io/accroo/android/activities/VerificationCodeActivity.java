package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.services.ApiService;

public class VerificationCodeActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    public static final int LOGIN = 1;
    public static final int UPDATE_EMAIL = 2;
    public static final int UPDATE_PASSWORD = 3;

    private int action;
    private EditText loginCodeField;
    private Button submit;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private String username, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_verification_code);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            action = getIntent().getIntExtra("action", 0);
            username = getIntent().getStringExtra("username");
            email = getIntent().getStringExtra("email");

            apiService = new ApiService(this, getApplicationContext());
            apiService.getLoginCode(username);

            submit = findViewById(R.id.submit);

            switch (action) {
                case LOGIN:
                    submit.setText(R.string.next);
                    break;
                case UPDATE_EMAIL:
                    submit.setText(R.string.submit);
                    break;
                case UPDATE_PASSWORD:
                    submit.setText(R.string.submit);
                    break;
            }

            loginCodeField = findViewById(R.id.login_code);

            progressDialog = new ProgressDialog(VerificationCodeActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValidInput()) {
                        progressDialog.show();
                        switch (action) {
                            case LOGIN:
                                apiService.login(username, loginCodeField.getText().toString());
                                break;
                            case UPDATE_EMAIL:
                                apiService.updateEmail(email, loginCodeField.getText().toString());
                                break;
                            case UPDATE_PASSWORD:
                                break;
                        }
                    }
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isValidInput() {
        if (loginCodeField.getText().length() != 8) {
            Toast.makeText(getApplicationContext(), R.string.invalid_verification_code, Toast.LENGTH_SHORT).show();
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
        if (requestType != ApiService.GET_VERIFICATION_CODE) {
            progressDialog.dismiss();
            switch (action) {
                case LOGIN:
                    startActivity(new Intent(getApplicationContext(), KeyDecryptionActivity.class));
                    break;
                case UPDATE_EMAIL:
                    Toast.makeText(getApplicationContext(), R.string.email_updated, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;
                case UPDATE_PASSWORD:
                    Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    break;
            }
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
            case ApiService.UNAUTHORIZED:
                message = getResources().getString(R.string.incorrect_verification_code);
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
