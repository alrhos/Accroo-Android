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

public class LoginStageOneActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText usernameField;
    private Button next;
    private ProgressDialog progressDialog;
    private ApiService apiService;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_login_stage_one);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            usernameField = findViewById(R.id.email);
            next = findViewById(R.id.next);

            progressDialog = new ProgressDialog(LoginStageOneActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            apiService = new ApiService(this, getApplicationContext());

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isValidInput()) {
                        progressDialog.show();
                        username = usernameField.getText().toString().trim();
                        apiService.getLoginCode(username);
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
        if (usernameField.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_email, Toast.LENGTH_SHORT).show();
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
        progressDialog.dismiss();
        //Intent intent = new Intent(getApplicationContext(), LoginStageTwoActivity.class);
        Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
        intent.putExtra("action", VerificationCodeActivity.LOGIN);
        intent.putExtra("email", username);
        startActivity(intent);
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
                message = getResources().getString(R.string.invalid_username_or_password);
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

