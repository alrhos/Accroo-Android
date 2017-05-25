package com.paleskyline.navicash.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.KeyPackage;

public class KeyDecryptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_decryption);
        System.out.println("------------KEY DECRYPTION ACTIVITY-------------");
        KeyPackage keyPackage = (KeyPackage)getIntent().getParcelableExtra("keyPackage");
        System.out.println(keyPackage.toString());
    }
}
