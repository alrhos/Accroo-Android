package io.accroo.android.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class KeyDecryptionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText keyPassword;
    private Button unlockButton;
    private TextView forgotPassword;
    private ApiService apiService;
    private static final String ACCROO_SUPPORT = "support@accroo.io";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_key_decryption);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            keyPassword = findViewById(R.id.key_password);
            unlockButton = findViewById(R.id.unlock_button);
            forgotPassword = findViewById(R.id.forgot_password_link);

            Utils.showSoftKeyboard(KeyDecryptionActivity.this);

            unlockButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (isValidInput()) {
                        int passwordLength = keyPassword.length();
                        char[] password = new char[passwordLength];
                        keyPassword.getText().getChars(0, passwordLength, password, 0);
                        if (apiService.initializeKey(password)) {
                            Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
                            keyPassword.getText().clear();
                            startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
                        } else {
                            Toast.makeText(KeyDecryptionActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", ACCROO_SUPPORT, null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Forgot password");
                    try {
                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.email_chooser)));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), R.string.no_email_client, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            apiService = new ApiService(this, getApplicationContext());

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
        Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
    }

    private boolean isValidInput() {
        if (keyPassword.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), R.string.enter_password, Toast.LENGTH_SHORT).show();
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
        // Not invoked
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
        // Not invoked
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        relaunch();
    }

}
