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
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class LoginActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText usernameField;
    private Button next;
    private String username;
    private ApiService apiService;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_login_stage_one);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            apiService = new ApiService(this, getApplicationContext());

            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            usernameField = findViewById(R.id.email);
            next = findViewById(R.id.next);

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

            usernameField.setFocusableInTouchMode(true);
            usernameField.requestFocus();
            Utils.showSoftKeyboard(LoginActivity.this);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(LoginActivity.this);
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
        Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
        intent.putExtra("action", VerificationCodeActivity.LOGIN);
        intent.putExtra("username", username);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MaintenanceDialog.show(this);
        } else {
            String message;
            switch (errorCode) {
                case ApiService.UNAUTHORIZED:
                    message = getResources().getString(R.string.account_locked);
                    break;
                case ApiService.CONNECTION_ERROR:
                    message = getResources().getString(R.string.connection_error);
                    break;
                case ApiService.TIMEOUT_ERROR:
                    message = getResources().getString(R.string.timeout_error);
                    break;
                case ApiService.TOO_MANY_REQUESTS:
                    message = getResources().getString(R.string.too_many_requests);
                    break;
                case ApiService.NOT_FOUND:
                    message = getResources().getString(R.string.email_not_found);
                    break;
                default:
                    message = getResources().getString(R.string.general_error);
            }
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError() {
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}

