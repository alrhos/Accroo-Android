package io.accroo.android.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import io.accroo.android.R;
import io.accroo.android.other.Utils;
import io.accroo.android.services.ApiService;

public class KeyDecryptionActivity extends AppCompatActivity implements ApiService.RequestOutcome {

    private TextInputLayout keyPasswordInput;
    private EditText keyPassword;
    private Button unlock;
    private TextView forgotPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_key_decryption);
            TextView email = findViewById(R.id.email);
            email.setText(getIntent().getStringExtra("username"));
            keyPasswordInput = findViewById(R.id.input_password);
            keyPasswordInput.setError(" ");
            keyPassword = findViewById(R.id.password);
            unlock = findViewById(R.id.unlock);
            forgotPassword = findViewById(R.id.forgot_password);

            unlock.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (keyPassword.getText().length() > 0) {
                        keyPasswordInput.setError(" ");
                        int passwordLength = keyPassword.length();
                        char[] password = new char[passwordLength];
                        keyPassword.getText().getChars(0, passwordLength, password, 0);
                        if (apiService.initializeKey(password)) {
                            Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
                            keyPassword.getText().clear();
                            startActivity(new Intent(getApplicationContext(), LaunchActivity.class));
                        } else {
                            keyPasswordInput.setError(getResources().getString(R.string.incorrect_password));
                        }
                    } else {
                        keyPasswordInput.setError(getResources().getString(R.string.enter_password));
                    }
                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.hideSoftKeyboard(KeyDecryptionActivity.this);
                    Uri uri = Uri.parse("https://accroo.io/forgot-password");
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
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
