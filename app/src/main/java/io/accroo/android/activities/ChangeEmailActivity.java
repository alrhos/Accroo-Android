package io.accroo.android.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.accroo.android.R;
import io.accroo.android.services.ApiService;

public class ChangeEmailActivity extends AppCompatActivity {

    private EditText emailAddress, confirmEmailAddress;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_change_email);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            emailAddress = findViewById(R.id.new_email);
            confirmEmailAddress = findViewById(R.id.confirm_new_email);
            next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isEmailValid()) {
                        return;
                    }
                    Intent intent = new Intent(getApplicationContext(), VerificationCodeActivity.class);
                    intent.putExtra("action", VerificationCodeActivity.UPDATE_EMAIL);
                    intent.putExtra("email", emailAddress.getText().toString());
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        return true;
    }

}
