package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.joda.time.DateTime;

import io.accroo.android.R;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class LaunchActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private DateTime startDate, endDate;
    public static boolean initialized = false;
    private ApiService apiService;
    private ProgressBar progressBar;
    private TextInputLayout inputEmailAddress;
    private EditText emailAddress;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        initialized = true;
        autoLogin();
    }

    private void autoLogin() {
        setContentView(R.layout.activity_launch_loading);
        apiService = new ApiService(this, getApplicationContext());
        if (apiService.userLoggedIn()) {
            // Set dates from first day of calendar month to the end of the current day
            endDate = new DateTime().withTime(23, 59, 59, 999);
            startDate = new DateTime(endDate.getYear(), endDate.getMonthOfYear(), 1,
                    0, 0, 0, 0);
            apiService.getDefaultData(startDate, endDate);
        } else {
            initLayout();
        }
    }

    private void initLayout() {
        setContentView(R.layout.new_launch_activity);
        progressBar = findViewById(R.id.progress_bar);
        inputEmailAddress = findViewById(R.id.input_verification_code);
        inputEmailAddress.setError(" ");
        emailAddress = findViewById(R.id.verification_code);
        signIn = findViewById(R.id.next);
        signIn.setOnClickListener(signInListener);

        Button createAccount = findViewById(R.id.resend_code);
        createAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // TODO: submit request to check if email address is already in use. If not proceed.
                //startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        TextView acceptTerms = findViewById(R.id.accept_terms);
        acceptTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onSuccess(int requestType) {
        progressBar.setVisibility(View.INVISIBLE);
        if (requestType == ApiService.GET_VERIFICATION_CODE) {
            Utils.hideSoftKeyboard(LaunchActivity.this);
            signIn.setOnClickListener(signInListener);
            Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
            intent.putExtra("username", emailAddress.getText().toString());
            intent.putExtra("action", VerificationCodeActivity.LOGIN);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (requestType == ApiService.GET_DEFAULT_DATA) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("startDate", startDate.getMillis());
            intent.putExtra("endDate", endDate.getMillis());
            startActivity(intent);
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        if (errorCode == ApiService.UNAUTHORIZED) {
            initLayout();
        } else if (requestType == ApiService.GET_VERIFICATION_CODE && errorCode == ApiService.NOT_FOUND) {
            progressBar.setVisibility(View.INVISIBLE);
            signIn.setOnClickListener(signInListener);
            inputEmailAddress.setError(getResources().getString(R.string.account_not_found));
        } else if (errorCode == ApiService.CONNECTION_ERROR || errorCode == ApiService.TIMEOUT_ERROR ||
                errorCode == ApiService.TOO_MANY_REQUESTS || errorCode == ApiService.SERVICE_UNAVAILABLE) {
            if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
                MaintenanceDialog.show(this);
            }
            setContentView(R.layout.activity_no_connection);
            Button tryAgain = findViewById(R.id.try_again);
            tryAgain.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    autoLogin();
                }
            });
        } else {
            onError();
        }
    }

    @Override
    public void onError() {
        apiService.logout();
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    View.OnClickListener signInListener = new View.OnClickListener() {
        public void onClick(View view) {
            String email = emailAddress.getText().toString();
            if (email.length() == 0) {
                inputEmailAddress.setError(getResources().getString(R.string.enter_email));
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                inputEmailAddress.setError(getResources().getString(R.string.error_invalid_email));
            } else {
                inputEmailAddress.setError(" ");
                progressBar.setVisibility(View.VISIBLE);
                signIn.setOnClickListener(null);
                apiService.getLoginCode(email);
            }

            //startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    };

}
