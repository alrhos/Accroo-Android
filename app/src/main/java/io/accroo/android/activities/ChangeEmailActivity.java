package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class ChangeEmailActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText emailAddress, confirmEmailAddress;
    private Button next;
    private ProgressDialog progressDialog;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_email);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            apiService = new ApiService(this, getApplicationContext());
            progressDialog = new ProgressDialog(ChangeEmailActivity.this);
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            emailAddress = findViewById(R.id.new_email);
            confirmEmailAddress = findViewById(R.id.confirm_new_email);
            next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmailValid()) {
                        return;
                    }
                    progressDialog.show();
                    apiService.getLoginCode(null);
                }
            });

            emailAddress.setFocusableInTouchMode(true);
            emailAddress.requestFocus();
            Utils.showSoftKeyboard(ChangeEmailActivity.this);
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
        Utils.hideSoftKeyboard(ChangeEmailActivity.this);
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
        if (emailAddress.getText().length() < 5) {
            Toast.makeText(getApplicationContext(), R.string.email_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(int requestType) {
        progressDialog.dismiss();
        if (requestType == ApiService.GET_VERIFICATION_CODE) {
            Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
            intent.putExtra("action", VerificationCodeActivity.UPDATE_EMAIL);
            intent.putExtra("email", emailAddress.getText().toString());
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressDialog.dismiss();
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MaintenanceDialog.show(this);
        } else if (errorCode == ApiService.UNAUTHORIZED) {
            Toast.makeText(getApplicationContext(), R.string.login_required, Toast.LENGTH_LONG).show();
            apiService.logout();
            relaunch();
        } else {
            String message;
            switch (errorCode) {
                case ApiService.CONNECTION_ERROR:
                    message = getResources().getString(R.string.connection_error);
                    break;
                case ApiService.TIMEOUT_ERROR:
                    message = getResources().getString(R.string.timeout_error);
                    break;
                case ApiService.TOO_MANY_REQUESTS:
                    message = getResources().getString(R.string.too_many_requests);
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
