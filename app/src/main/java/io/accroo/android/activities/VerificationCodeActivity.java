package io.accroo.android.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import io.accroo.android.R;
import io.accroo.android.model.Account;
import io.accroo.android.other.MaintenanceDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class VerificationCodeActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    public static final int LOGIN = 1;
    public static final int UPDATE_EMAIL = 2;
    public static final int UPDATE_PASSWORD = 3;
    private static final String ACCROO_SUPPORT = "support@accroo.io";

    private int action;
    private EditText verificationCodeField;
    private TextInputLayout verificationCodeInput;
    private Button next, resendCode;
    private ProgressBar progressBar;
    private ApiService apiService;
    private String username, email;
    private char[] password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_verification_code);
            action = getIntent().getIntExtra("action", 0);
            username = getIntent().getStringExtra("username");
            email = getIntent().getStringExtra("email");
            password = getIntent().getCharArrayExtra("password");
            progressBar = findViewById(R.id.progress_bar);
            verificationCodeInput = findViewById(R.id.input_password);
            verificationCodeInput.setError(" ");

            apiService = new ApiService(this, getApplicationContext());

            TextView emailAddress = findViewById(R.id.email);
            emailAddress.setText(username);
            next = findViewById(R.id.next);
            resendCode = findViewById(R.id.resend_code);

            TextView noCode = findViewById(R.id.no_code);
            verificationCodeField = findViewById(R.id.verification_code);
            resendCode.setOnClickListener(resendCodeListener);
            next.setOnClickListener(nextListener);

            noCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideSoftKeyboard(VerificationCodeActivity.this);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", ACCROO_SUPPORT, null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Not receiving verification codes");
                    try {
                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.email_chooser)));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), R.string.no_email_client, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(VerificationCodeActivity.this);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        if (requestType == ApiService.GET_VERIFICATION_CODE) {
            verificationCodeField.setText("");
            progressBar.setVisibility(View.INVISIBLE);
            resendCode.setOnClickListener(resendCodeListener);
            next.setOnClickListener(nextListener);
            verificationCodeInput.setError(getResources().getString(R.string.new_verification_code_sent));
        } else if (requestType == ApiService.LOGIN) {
            apiService.getKey();
        } else if (requestType == ApiService.GET_KEY) {
            verificationCodeField.getText().clear();
            progressBar.setVisibility(View.INVISIBLE);
            resendCode.setOnClickListener(resendCodeListener);
            next.setOnClickListener(nextListener);
            Intent intent = new Intent(getApplicationContext(), KeyDecryptionActivity.class);
            intent.putExtra("username", username);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
        } else if (requestType == ApiService.REAUTHENTICATE) {
            if (action == UPDATE_EMAIL) {
                apiService.updateEmail(email);
            } else if (action == UPDATE_PASSWORD) {
                apiService.updatePassword(password);
            }
        } else if (requestType == ApiService.UPDATE_EMAIL) {
            verificationCodeField.getText().clear();
            progressBar.setVisibility(View.INVISIBLE);
            AlertDialog.Builder builder = new AlertDialog.Builder(VerificationCodeActivity.this);
            builder.setTitle(R.string.email_updated_title)
                    .setMessage(R.string.email_updated_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            relaunch();
                        }
                    }).create().show();
        } else if (requestType == ApiService.UPDATE_PASSWORD) {
            Toast.makeText(getApplicationContext(), R.string.password_updated, Toast.LENGTH_LONG).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressBar.setVisibility(View.INVISIBLE);
        resendCode.setOnClickListener(resendCodeListener);
        next.setOnClickListener(nextListener);
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MaintenanceDialog.show(this);
        } else if (requestType == ApiService.UPDATE_EMAIL && errorCode == ApiService.CONFLICT) {
            // TODO: come back and review if this scenario can be handled better
            AlertDialog.Builder builder = new AlertDialog.Builder(VerificationCodeActivity.this);
            builder.setMessage(R.string.email_in_use)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }).create().show();
        } else {
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
                case ApiService.TOO_MANY_REQUESTS:
                    message = getResources().getString(R.string.too_many_requests);
                    break;
                default:
                    message = getResources().getString(R.string.general_error);
            }
            verificationCodeInput.setError(message);
        }
    }

    @Override
    public void onError() {
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

    View.OnClickListener nextListener = new View.OnClickListener() {
        public void onClick(View view) {
            String verificationCode = verificationCodeField.getText().toString();
            if (verificationCode.length() == 0) {
                verificationCodeInput.setError(getResources().getString(R.string.enter_verification_code));
            } else if (verificationCode.length() != 8) {
                verificationCodeInput.setError(getResources().getString(R.string.invalid_verification_code_length));
            } else if (!verificationCode.matches("^[A-Z2-7]{8}$")) {
                // Verification code is a Base32 encoded string
                verificationCodeInput.setError(getResources().getString(R.string.invalid_verification_code_format));
            } else {
                verificationCodeInput.setError(" ");
                progressBar.setVisibility(View.VISIBLE);
                Utils.hideSoftKeyboard(VerificationCodeActivity.this);
                resendCode.setOnClickListener(null);
                next.setOnClickListener(null);
                switch (action) {
                    case LOGIN:
                        Account account = new Account(username, verificationCodeField.getText().toString());
                        apiService.login(account);
                        break;
                    case UPDATE_EMAIL:
                        apiService.reauthenticate(verificationCodeField.getText().toString());
                        break;
                    case UPDATE_PASSWORD:
                        apiService.reauthenticate(verificationCodeField.getText().toString());
                        break;
                }
            }
        }
    };

    View.OnClickListener resendCodeListener = new View.OnClickListener() {
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VerificationCodeActivity.this);
            builder.setMessage(R.string.verification_code_explanation)
                    .setTitle(R.string.where_is_my_code)
                    .setPositiveButton(R.string.resend_code, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            verificationCodeInput.setError(" ");
                            progressBar.setVisibility(View.VISIBLE);
                            Utils.hideSoftKeyboard(VerificationCodeActivity.this);
                            resendCode.setOnClickListener(null);
                            next.setOnClickListener(null);
                            apiService.getLoginCode(username);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {}
                    }).create().show();
        }
    };

}
