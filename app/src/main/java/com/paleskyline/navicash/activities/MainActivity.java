package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.model.User;
import com.paleskyline.navicash.network.APIWorker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        char[] dataPassword = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
        String email = "oscar.alston2@protonmail.com";

        System.out.println("VARIABLES DECLARED");

        KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPassword);

        System.out.println("KEYPACKAGE CREATED");

        User user = new User(email, loginPassword, keyPackage);

        APIWorker.getInstance(this).registerAccount(user);

        System.out.println("DONE");
    }
}
