package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.services.ApiService;

public class KeyDecryptionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private EditText keyPassword;
    private Button unlockButton;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_key_decryption);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            keyPassword = (EditText) findViewById(R.id.key_password);
            unlockButton = (Button) findViewById(R.id.unlock_button);
            apiService = new ApiService(this, getApplicationContext());
            addListeners();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void addListeners() {
        unlockButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isValidInput()) {
                    // Attempt decryption
                    // TODO: review password security
                    int passwordLength = keyPassword.length();
                    char[] password = new char[passwordLength];
                    keyPassword.getText().getChars(0, passwordLength, password, 0);

                    if (apiService.initializeKey(password)) {
                        startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
                    } else {
                        // TODO: incorrect password to decrypt key
                    }
                }
            }
        });
    }


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }

    public void onUnsuccessfulRequest(int requestType, int errorCode) {

    }

    public void onAuthorizationError() {

    }

    public void onUnsuccessfulDecryption() {

    }

    public void onGeneralError() {

    }

    public void onSuccess(int requestType) {

    }


}
