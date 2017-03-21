package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.model.EncryptedGeneralCategory;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.network.APIWorker;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final static String tag = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // register();
        //getToken();
        initKey();

        

        GeneralCategory category = new GeneralCategory("Food and Drink", "Expenses");
        EncryptedGeneralCategory encryptedGeneralCategory = category.encryptObject();

        JSONObject json = new JSONObject();
        try {
            json.put("category_details", encryptedGeneralCategory.getPayload().getEncryptedJson());
            json.put("nonce", encryptedGeneralCategory.getPayload().getNonce());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag) {
            @Override
            protected void onSuccess() {
                System.out.println("SUCCESS");
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("FAILED");
            }
        };

        coordinator.addRequests(RestMethods.addGeneralCategory(0, coordinator, json));
        coordinator.start();



        /*

        RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag) {
            @Override
            protected void onSuccess() {
                System.out.println("SUCCESS");

                for (JSONObject json : this.returnRequestResults()) {
                    System.out.println(json.toString());
                    System.out.println("RESULT PARSED");
                }

            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("FAILED");
                System.out.println(json.toString());
            }
        };



        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        char[] dataPassword = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
        String email = "oscar.alston@protonmail.com";
        KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPassword);

        JSONObject json = new JSONObject();
        try {

            json.put("email", email);
            json.put("password", String.copyValueOf(loginPassword));
            json.put("masterkey", keyPackage.getEncryptedMasterKey());
            json.put("salt", keyPackage.getSalt());
            json.put("nonce", keyPackage.getNonce());
            json.put("opslimit", keyPackage.getOpslimit());
            json.put("memlimit", keyPackage.getMemlimit());

            coordinator.addRequests(RestMethods.registerAccount(0, coordinator, json));
            coordinator.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        */

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


    /*
    public void createGeneralCategory() {
        GeneralCategory category = new GeneralCategory("Holidays", "Expenses");
        System.out.println("Value is: " + category.getCategoryDetails());
        SecurePayload sJson = CryptoManager.getInstance().encrypt(category.getCategoryDetails());
        String token = "eyJhbGciOiJIUzI1NiIsImV4cCI6MTQ4ODcwODEzNCwiaWF0IjoxNDg4NzA4MTA0fQ.MQ.Og2C_CmbLqZ8CTDkIloue5Lj7V_ZhKatTfiNjcNRPH4";
        APIWorker.getInstance(this).createGeneralCategory(sJson);
        //APIWorker.getInstance(this).createGeneralCategory(sJson);
    }
    */
}
