package io.accroo.android.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.services.ApiService;

import java.util.Arrays;

public class KeyDecryptionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText keyPassword;
    private Button unlockButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_key_decryption);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            keyPassword = findViewById(R.id.key_password);
            unlockButton = findViewById(R.id.unlock_button);
            unlockButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (isValidInput()) {
                        int passwordLength = keyPassword.length();
                        char[] password = new char[passwordLength];
                        keyPassword.getText().getChars(0, passwordLength, password, 0);

                        if (apiService.initializeKey(password)) {
                            keyPassword.getText().clear();
                            startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
                        } else {
                            Toast.makeText(KeyDecryptionActivity.this, R.string.incorrect_password, Toast.LENGTH_SHORT).show();
                        }
                        Arrays.fill(password, '\u0000');
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
