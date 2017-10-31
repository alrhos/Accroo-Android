package com.paleskyline.accroo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.services.ApiService;

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
                        Toast.makeText(KeyDecryptionActivity.this, R.string.incorrect_data_password, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }


    @Override
    public void onSuccess(int requestType) {
    }

    @Override
    public void onFailure(int requestType, int errorCode) {
    }

    @Override
    public void onError() {
        Toast.makeText(getApplicationContext(), R.string.general_error, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
