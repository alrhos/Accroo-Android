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
            //localKey.generateKey();
            //localKey.loadKey();
        } catch (Exception e) {
            System.out.println("KEY EXCEPTION");
        }
    }
}
