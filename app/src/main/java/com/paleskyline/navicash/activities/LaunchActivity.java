package com.paleskyline.navicash.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.KeyStoreManager;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        keystoreTest();
    }

    public void keystoreTest() {
        try {
            KeyStoreManager localKey = new KeyStoreManager();
            String cipherText = localKey.encrypt("secret string :)");
            System.out.println("'" + cipherText + "'");
            System.out.println("'" + localKey.decrypt(cipherText) + "'");
            //String c2 = localKey.encrypt("dit");
            //localKey.decrypt(c2);
            //localKey.generateKey();
            //localKey.loadKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
