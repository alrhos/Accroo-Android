package io.accroo.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import io.accroo.android.R;
import io.accroo.android.other.Constants;
import io.accroo.android.other.MessageDialog;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class KeyDecryptionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    public static final int LOGIN = 1;
    public static final int UPDATE_PASSWORD = 2;

    private int action;
    private String username;
    private TextInputLayout keyPasswordInput;
    private ProgressBar progressBar;
    private EditText keyPassword;
    private Button next;
    private ApiService apiService;
    private char[] password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.enter_password);
            action = getIntent().getIntExtra("action", 0);
            username = getIntent().getStringExtra("username");
            TextView email = findViewById(R.id.email);
            email.setText(username);
            TextView passwordMessage = findViewById(R.id.password_message);
            progressBar = findViewById(R.id.progress_bar);
            keyPasswordInput = findViewById(R.id.input_password);
            keyPasswordInput.setHint(getResources().getString(R.string.password));
            keyPasswordInput.setError(" ");
            keyPassword = findViewById(R.id.password);
            next = findViewById(R.id.next);
            TextView forgotPassword = findViewById(R.id.forgot_password);
            forgotPassword.setVisibility(View.VISIBLE);

            if (action == LOGIN) {
                passwordMessage.setText(R.string.key_decryption_message);
                next.setText(R.string.unlock);
            } else if (action == UPDATE_PASSWORD) {
                passwordMessage.setText(R.string.enter_current_password);
                next.setText(R.string.next);
            }

            apiService = new ApiService(this, getApplicationContext());

            next.setOnClickListener(nextListener);
            forgotPassword.setOnClickListener(view -> {
                Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
                Uri uri = Uri.parse(Constants.FORGOT_PASSWORD_URL);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            });
        }
    }

    View.OnClickListener nextListener = new View.OnClickListener() {
        public void onClick(View view) {
            if (keyPassword.getText().length() > 0) {
                next.setOnClickListener(null);
                keyPasswordInput.setError(" ");
                int passwordLength = keyPassword.length();
                password = new char[passwordLength];
                keyPassword.getText().getChars(0, passwordLength, password, 0);
                if (action == LOGIN) {
                    if (apiService.initializeKey(password)) {
                        Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
                        keyPassword.getText().clear();
                        startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
                    } else {
                        keyPasswordInput.setError(getResources().getString(R.string.incorrect_password));
                        next.setOnClickListener(nextListener);
                    }
                } else if (action == UPDATE_PASSWORD) {
                    progressBar.setVisibility(View.VISIBLE);
                    apiService.getKey();
                }
            } else {
                keyPasswordInput.setError(getResources().getString(R.string.enter_your_password));
            }
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (action == LOGIN) {
            overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSuccess(int requestType) {
        progressBar.setVisibility(View.INVISIBLE);
        next.setOnClickListener(nextListener);
        if (requestType == ApiService.GET_KEY) {
            if (apiService.initializeKey(password)) {
                Intent intent = new Intent(getApplicationContext(), ChoosePasswordActivity.class);
                intent.putExtra("action", ChoosePasswordActivity.UPDATE_PASSWORD);
                intent.putExtra("username", username);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            } else {
                keyPasswordInput.setError(getResources().getString(R.string.incorrect_password));
            }
        }
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        progressBar.setVisibility(View.INVISIBLE);
        next.setOnClickListener(nextListener);
        if (errorCode == ApiService.SERVICE_UNAVAILABLE) {
            MessageDialog.show(KeyDecryptionActivity.this,
                    getResources().getString(R.string.maintenance_title),
                    getResources().getString(R.string.maintenance_message));
        } else if (errorCode == ApiService.GONE) {
            MessageDialog.show(KeyDecryptionActivity.this,
                    getResources().getString(R.string.upgrade_required_title),
                    getResources().getString(R.string.upgrade_required_message));
        } else if (errorCode == ApiService.UNAUTHORIZED || errorCode == ApiService.UNPROCESSABLE_ENTITY) {
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
            keyPasswordInput.setError(message);
        }
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
