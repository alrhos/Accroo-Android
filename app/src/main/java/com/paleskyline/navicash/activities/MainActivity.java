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

        //register();
        //getToken();
        getKey();

    }

    public void register() {
        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        char[] dataPassword = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
        String email = "oscar.alston@protonmail.com";

        System.out.println("VARIABLES DECLARED");

        KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPassword);

        System.out.println("KEYPACKAGE CREATED");

        User user = new User(email, loginPassword, keyPackage);

        APIWorker.getInstance(this).registerAccount(user);

        System.out.println("DONE");
    }

    public void getToken() {
        String email = "oscar.alston@protonmail.com";
        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        User user = new User(email, loginPassword);
        APIWorker.getInstance(this).getToken(email, String.copyValueOf(loginPassword));
    }

    public void getKey() {
        String token = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4ODYxMjA1NCwiaWF0IjoxNDg4NjA2MDU0fQ.MQ.VnzySMLP8sA4wJ4cLe_dnQlP8enDsyGYB2DqokNCs6c";
        APIWorker.getInstance(this).getEncryptionKey(token);
    }
}
