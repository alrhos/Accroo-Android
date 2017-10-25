package com.paleskyline.navicash.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.paleskyline.navicash.R;

public class RegisterStageTwoActivity extends AppCompatActivity {

    private EditText dataPassword, confirmDataPassword;
    private Button register;
    private String email;
    private char[] loginPwd, dataPwd;
    private static final int MIN_PASSWORD_LENGTH = 10;
    private static final int MAX_PASSWORD_LENGTH = 80;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_stage_two);
        email = getIntent().getStringExtra("email");
        loginPwd = getIntent().getCharArrayExtra("loginPassword");
    }
}
