package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.crypto.SecuredJson;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.network.APIWorker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // register();
        //getToken();
        initKey();
        createGeneralCategory();

    }

    public void register() {
        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        char[] dataPassword = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
        String email = "oscar.alston89@protonmail.com";
        KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPassword);
        APIWorker.getInstance(this).registerAccount(email, loginPassword, keyPackage);
    }

    public void getToken() {
        String email = "oscar.alston@protonmail.com";
        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        APIWorker.getInstance(this).getToken();
    }

    public void getKey() {
        String token = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4ODcwMjcxMywiaWF0IjoxNDg4Njk2NzEzfQ.MQ._YcUrchJaNzaWEICHK4L9txWRpqUzyi4i_5LT8p5fIE";
        APIWorker.getInstance(this).getEncryptionKey(token);
    }

    public void initKey() {
        CryptoManager.getInstance().decryptMasterKey(AuthManager.DATAPASSWORD, AuthManager.KEYPACKAGE);
    }



    public void createGeneralCategory() {
        GeneralCategory category = new GeneralCategory("Holidays", "Expenses");
        System.out.println("Value is: " + category.getCategoryDetails());
        SecuredJson sJson = CryptoManager.getInstance().encrypt(category.getCategoryDetails());
        String token = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4ODcwODEzNCwiaWF0IjoxNDg4NzA4MTA0fQ.MQ.Og2C_CmbLqZ8CTDkIloue5Lj7V_ZhKatTfiNjcNRPH4";
        APIWorker.getInstance(this).createGeneralCategory(sJson);
    }
}
