package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.services.DataServices;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements DataServices.RequestOutcome {

    private EditText usernameField, passwordField;
    private Button loginButton;
    private DataServices dataServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.login_button);

        dataServices = new DataServices(this, getApplicationContext());
        addListeners();
    }


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }

    private void addListeners() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isValidInput()) {

                    String username = usernameField.getText().toString();
                    int passwordLength = passwordField.getText().length();
                    char[] password = new char[passwordLength];
                    passwordField.getText().getChars(0, passwordLength, password, 0);

                    dataServices.getRefreshToken(username, password);
                }
            }
        });
    }





//                    final JSONObject[] dataReceiver = new JSONObject[1];
//                    RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
//                            this, dataReceiver) {
//
//                        @Override
//                        protected void onSuccess() {
//                            try {
//
//                                String accessToken = dataReceiver[0].getString("accessToken");
//                                String refreshToken = dataReceiver[0].getString("refreshToken");
//
//                                AuthManager.getInstance(getApplicationContext()).saveEntry(AuthManager.ACCESS_TOKEN_KEY, accessToken);
//                                AuthManager.getInstance(getApplicationContext()).saveEntry(AuthManager.REFRESH_TOKEN_KEY, refreshToken);
//
//
//                                JSONObject keyData = dataReceiver[0].getJSONObject("key");
//
//                                String key = keyData.getString("dataKey");
//                                String nonce = keyData.getString("nonce");
//                                String salt = keyData.getString("salt");
//                                int memlimit = keyData.getInt("memLimit");
//                                int opslimit = keyData.getInt("opsLimit");
//
//                                KeyPackage keyPackage = new KeyPackage(key, nonce, salt, opslimit, memlimit);
//
//                                Intent intent = new Intent(getApplicationContext(), KeyDecryptionActivity.class);
//                                intent.putExtra("keyPackage", keyPackage);
//                                startActivity(intent);
//
//                            } catch (Exception e) {
//                                // TODO: exception handling
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        protected void onFailure(String errorMessage) {
//                            System.out.println("AN ERROR OCCURRED!");
//                            System.out.println(errorMessage);
//                        }
//                    };
//
//                    try {
//
//                        // TODO: review password security
//
//                        int passwordLength = password.getText().length();
//                        char[] enteredPassword = new char[passwordLength];
//                        password.getText().getChars(0, passwordLength, enteredPassword, 0);
//
//                        AuthManager.getInstance(getApplicationContext()).saveEntry(AuthManager.USERNAME_KEY, username.getText().toString());
//                        AuthManager.getInstance(getApplicationContext()).saveEntry(AuthManager.PASSWORD_KEY, password.getText().toString());
//
//                        coordinator.addRequests(RequestBuilder.basicAuth(0, coordinator,
//                                Request.Method.GET, RequestBuilder.REFRESH_TOKEN, username.getText().toString(),
//                                enteredPassword, getApplicationContext()));
//
//                    //    coordinator.addRequests(RequestBuilder.getKey(coordinator, 0, getApplicationContext()));
//                        coordinator.start();
//
//                    } catch (Exception e) {
//                        // TODO: Exception handling
//                        e.printStackTrace();
//                    }
//
//                }
//            }


    @Override
    public void onSuccess(int requestType) {
        Intent intent = new Intent(getApplicationContext(), KeyDecryptionActivity.class);
        startActivity(intent);
    }

    @Override
    public void onUnsuccessfulRequest(String errorMessage) {

    }

    @Override
    public void onUnsuccessfulDecryption() {
        System.out.println("DECRYPTION ERROR");
    }

    @Override
    public void onGeneralError() {
        System.out.println("GENERAL ERROR");
    }

}

