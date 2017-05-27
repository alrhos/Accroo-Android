package com.paleskyline.navicash.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;

public class KeyDecryptionActivity extends AppCompatActivity {

    private EditText keyPassword;
    private Button unlockButton;
    private KeyPackage keyPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_decryption);
        keyPassword = (EditText) findViewById(R.id.key_password);
        unlockButton = (Button) findViewById(R.id.unlock_button);

        System.out.println("------------KEY DECRYPTION ACTIVITY-------------");
        keyPackage = getIntent().getParcelableExtra("keyPackage");
        System.out.println(keyPackage.toString());

        addListeners();
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
                    try {
                        CryptoManager.getInstance().decryptMasterKey(password, keyPackage);
                        CryptoManager.getInstance().saveMasterKey(getApplicationContext());
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        // TODO: error handling for incorrect password
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    // TODO: implement logic
    private boolean isValidInput() {
        return true;
    }


}
