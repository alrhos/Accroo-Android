package com.paleskyline.accroo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paleskyline.accroo.R;
import com.paleskyline.accroo.other.Constants;

public class RegisterStageOneActivity extends AppCompatActivity {

    private EditText emailAddress, confirmEmailAddress, loginPassword, confirmLoginPassword;
    private TextView agree;
    private Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_stage_one);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailAddress = findViewById(R.id.email);
        confirmEmailAddress = findViewById(R.id.confirm_email);
        loginPassword = findViewById(R.id.login_password);
        confirmLoginPassword = findViewById(R.id.confirm_login_password);
        next =  findViewById(R.id.next);
        agree = findViewById(R.id.accept_terms);
        agree.setMovementMethod(LinkMovementMethod.getInstance());

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmailValid()) {
                    return;
                }
                if (!isPasswordValid()) {
                    return;
                }

                int loginPasswordLength = loginPassword.getText().length();
                char[] loginPwd = new char[loginPasswordLength];
                loginPassword.getText().getChars(0, loginPasswordLength, loginPwd, 0);

                Intent intent = new Intent(getApplicationContext(), RegisterStageTwoActivity.class);
                intent.putExtra("email", emailAddress.getText().toString().trim());
                intent.putExtra("loginPassword", loginPwd);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

    private boolean isPasswordValid() {
        if (loginPassword.getText().length() < Constants.MIN_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_short, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (loginPassword.getText().length() > Constants.MAX_PASSWORD_LENGTH) {
            Toast.makeText(getApplicationContext(), R.string.password_too_long, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!loginPassword.getText().toString().equals(confirmLoginPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), R.string.password_mismatch, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
