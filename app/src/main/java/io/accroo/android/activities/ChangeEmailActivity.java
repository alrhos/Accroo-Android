package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import io.accroo.android.R;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class ChangeEmailActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private TextView currentEmail;
    private String username;
    private Button next;
    private ProgressBar progressBar;
    private TextInputLayout inputEmailAddress;
    private EditText newEmailAddress;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_email);
            username = getIntent().getStringExtra("username");
            currentEmail = findViewById(R.id.current_email);
            currentEmail.setText(username);
            progressBar = findViewById(R.id.progress_bar);
            inputEmailAddress = findViewById(R.id.input_email);
            inputEmailAddress.setError(" ");
            newEmailAddress = findViewById(R.id.new_email);
            next = findViewById(R.id.next);

            newEmailAddress.setFocusableInTouchMode(true);
            newEmailAddress.requestFocus();

            Utils.showSoftKeyboard(ChangeEmailActivity.this);
            apiService = new ApiService(this, getApplicationContext());
            next.setOnClickListener(nextListener);
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

    @Override
    public void onSuccess(int requestType) {
        if (requestType == ApiService.CHECK_EMAIL_AVAILABILITY) {
            // Email is already being used - HTTP 200
            progressBar.setVisibility(View.INVISIBLE);
            inputEmailAddress.setError(getResources().getString(R.string.email_in_use));
            next.setOnClickListener(nextListener);
        } else if (requestType == ApiService.GET_VERIFICATION_CODE) {
            progressBar.setVisibility(View.INVISIBLE);
            Utils.hideSoftKeyboard(ChangeEmailActivity.this);
            next.setOnClickListener(nextListener);
            Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("email", newEmailAddress.getText().toString());
            intent.putExtra("action", VerificationCodeActivity.UPDATE_EMAIL);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        if (requestType == ApiService.CHECK_EMAIL_AVAILABILITY && errorCode == ApiService.NOT_FOUND) {
            // Email is not being used - continue with update process
            apiService.getVerificationCode(username);
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            next.setOnClickListener(nextListener);
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
                inputEmailAddress.setError(message);
            }
        }
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

    View.OnClickListener nextListener = new View.OnClickListener() {
        public void onClick(View view) {
            String email = newEmailAddress.getText().toString();
            if (email.length() == 0) {
                inputEmailAddress.setError(getResources().getString(R.string.enter_email));
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmailAddress.setError(getResources().getString(R.string.error_invalid_email));
            } else if (username.equals(email)) {
                inputEmailAddress.setError(getResources().getString(R.string.email_unchanged));
            } else {
                inputEmailAddress.setError(" ");
                progressBar.setVisibility(View.VISIBLE);
                next.setOnClickListener(null);
                apiService.checkEmailAvailability(email);
            }
        }
    };

}
