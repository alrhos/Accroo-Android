package io.accroo.android.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import io.accroo.android.R;
import io.accroo.android.other.Constants;
import io.accroo.android.other.Utils;

public class ChoosePasswordActivity extends AppCompatActivity {

    private String username;
    private TextInputLayout choosePasswordInput;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!LaunchActivity.initialized) {
            relaunch();
        } else {
            setContentView(R.layout.activity_choose_password);
            username = getIntent().getStringExtra("username");
            TextView emailAddress = findViewById(R.id.email);
            emailAddress.setText(username);
            choosePasswordInput = findViewById(R.id.input_password);
            choosePasswordInput.setError(" ");
            passwordField = findViewById(R.id.password);
            Button next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String password = passwordField.getText().toString();
                    if (password.length() == 0) {
                        choosePasswordInput.setError(getResources().getString(R.string.enter_password));
                    } else if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
                        choosePasswordInput.setError(getResources().getString(R.string.password_too_short));
                    } else {
                        choosePasswordInput.setError(" ");
                        Utils.hideSoftKeyboard(ChoosePasswordActivity.this);
                        Intent intent = new Intent(getApplicationContext(), ConfirmPasswordActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    private void relaunch() {
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
