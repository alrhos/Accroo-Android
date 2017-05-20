package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;
import com.paleskyline.navicash.network.RestRequest;

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

                        }

                        @Override
                        protected void onFailure(JSONObject json) {

                        }
                    };

                    coordinator.addRequests(RestMethods.get(0, RestMethods.KEY, null,
                            coordinator, RestRequest.BASIC));
                }
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
            }
        });

    }


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }

}

