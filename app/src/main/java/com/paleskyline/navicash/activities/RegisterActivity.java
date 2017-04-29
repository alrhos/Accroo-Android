package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailAddress, loginPassword, confirmLoginPassword, dataPassword, confirmDataPassword;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailAddress = (EditText) findViewById(R.id.email);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        confirmLoginPassword = (EditText) findViewById(R.id.loginPasswordConfirm);
        dataPassword = (EditText) findViewById(R.id.dataPassword);
        confirmDataPassword = (EditText) findViewById(R.id.dataPasswordConfirm);

        register = (Button) findViewById(R.id.register_button);

        addListeners();
    }

    private void addListeners() {
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Add check to ensure email address is valid

                String emailString = emailAddress.getText().toString();

                // TODO: Add check to ensure login password length is sufficient

                if (loginPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
                    // Login passwords match
                   // Toast.makeText(getApplicationContext(), "LOGIN PASSWORDS MATCH", Toast.LENGTH_SHORT).show();
                }

                // TODO: Add check to ensure data password length is sufficient

                if (dataPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
                    // Data passwords match
                    //Toast.makeText(getApplicationContext(), "DATA PASSWORDS MATCH", Toast.LENGTH_SHORT).show();
                }

                if (emailString.length() > 0 && loginPassword.getText().toString().equals(confirmLoginPassword.getText().toString())
                        && dataPassword.getText().toString().equals(confirmDataPassword.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "WE'RE GOOD TO GO!", Toast.LENGTH_SHORT).show();

                    // PROCESS LOGIN PASSWORD

                    int loginPasswordLength = dataPassword.getText().length();
                    char[] loginPwd = new char[loginPasswordLength];
                    dataPassword.getText().getChars(0, loginPasswordLength, loginPwd, 0);

                    // PROCESS DATA PASSWORD

                    int dataPasswordLength = dataPassword.getText().length();
                    char[] dataPwd = new char[dataPasswordLength];
                    dataPassword.getText().getChars(0, dataPasswordLength, dataPwd, 0);

                    System.out.println(String.copyValueOf(dataPwd));

                    KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPwd);
                    User user = new User(emailString, loginPwd, keyPackage);

                    // HIT API ENDPOINT

                    final JSONObject[] dataReceiver = new JSONObject[1];
                    RequestCoordinator coordinator = new RequestCoordinator(RegisterActivity.super.getApplicationContext(),
                            "REGISTER_ACTIVITY_TAG", dataReceiver) {

                        @Override
                        protected void onSuccess() {
                            System.out.println("SUCCESS!!!");
                        }

                        @Override
                        protected void onFailure(JSONObject json) {
                            System.out.println("ERROR: " + json.toString());
                        }
                    };

                    System.out.println(user.toJSON());
                    coordinator.addRequests(RestMethods.post(0, RestMethods.REGISTER, coordinator, user.toJSON()));
                    coordinator.start();
                }
            }
        });
    }
}
