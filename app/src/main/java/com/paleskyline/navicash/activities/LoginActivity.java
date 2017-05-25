package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText username, password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isValidInput()) {

                    final JSONObject[] dataReceiver = new JSONObject[1];
                    RequestCoordinator coordinator = new RequestCoordinator(getApplicationContext(),
                            this, dataReceiver) {

                        @Override
                        protected void onSuccess() {
                            try {
                                JSONObject keyData = dataReceiver[0].getJSONObject("key");
                                String key = keyData.getString("dataKey");
                                String nonce = keyData.getString("nonce");
                                String salt = keyData.getString("salt");
                                int memlimit = keyData.getInt("memLimit");
                                int opslimit = keyData.getInt("opsLimit");

                                KeyPackage keyPackage = new KeyPackage(key, nonce, salt, opslimit, memlimit);

                                Intent intent = new Intent(getApplicationContext(), KeyDecryptionActivity.class);
                                intent.putExtra("keyPackage", keyPackage);
                                startActivity(intent);

                            } catch (JSONException e) {
                                // TODO: exception handling
                                e.printStackTrace();
                            }
                        }

                        @Override
                        protected void onFailure(JSONObject json) {
                            System.out.println("AN ERROR OCCURRED!");
                            System.out.println(json.toString());
                        }
                    };

                    try {
                        AuthManager.getInstance(getApplicationContext()).saveEntry(AuthManager.USERNAME_KEY, username.getText().toString());
                        AuthManager.getInstance(getApplicationContext()).saveEntry(AuthManager.PASSWORD_KEY, password.getText().toString());
                    } catch (Exception e) {
                        // TODO: Exception handling
                        e.printStackTrace();
                    }

                    coordinator.addRequests(RestMethods.getKey(coordinator, 0));
                    coordinator.start();
                }
            }
        });

    }


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }

}

