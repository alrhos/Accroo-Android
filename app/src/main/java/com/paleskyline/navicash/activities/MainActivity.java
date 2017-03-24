package com.paleskyline.navicash.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.paleskyline.navicash.R;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.CryptoManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.model.GeneralCategory;
import com.paleskyline.navicash.model.SubCategory;
import com.paleskyline.navicash.network.RequestCoordinator;
import com.paleskyline.navicash.network.RestMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private final static String tag = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initKey();
        insertSubCategories();



    }

    public void initKey() {
        CryptoManager.getInstance().decryptMasterKey(AuthManager.DATAPASSWORD, AuthManager.KEYPACKAGE);
    }

    public void register() {
        char[] loginPassword = {'l', 'o', 'g', 'm', 'e', 'i', 'n', '!'};
        char[] dataPassword = {'s', 'e', 'c', 'r', 'e', 't', 's', 'a', 'u', 'c', 'e', '!'};
        String email = "oscar.alston@protonmail.com";
        KeyPackage keyPackage = CryptoManager.getInstance().generateKeyPackage(dataPassword);

        JSONObject json = new JSONObject();
        try {

            json.put("email", email);
            json.put("password", String.copyValueOf(loginPassword));
            json.put("masterKey", keyPackage.getEncryptedMasterKey());
            json.put("salt", keyPackage.getSalt());
            json.put("nonce", keyPackage.getNonce());
            json.put("opslimit", keyPackage.getOpslimit());
            json.put("memlimit", keyPackage.getMemlimit());

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

            coordinator.addRequests(RestMethods.registerAccount(0, coordinator, json));
            coordinator.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertGeneralCategories() {
        GeneralCategory category = new GeneralCategory("Wages", "Income", "moneybag.png");
        GeneralCategory category2 = new GeneralCategory("Food and Drink", "Expenses", "hamburger.png");

        try {

            JSONObject json = category.encrypt();
            JSONObject json2 = category2.encrypt();


            JSONArray array = new JSONArray();
            array.put(json);
            array.put(json2);

            System.out.println(array.toString());

            JSONObject objects = new JSONObject();
            objects.put("categories", array);
            System.out.println(objects.toString());


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

            coordinator.addRequests(RestMethods.addGeneralCategory(0, coordinator, objects));
            coordinator.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertSubCategories() {
        SubCategory category = new SubCategory("Eating out", 2);
        SubCategory category2 = new SubCategory("Take away", 6);

        try {

            JSONObject json = category.encrypt();
            JSONObject json2 = category2.encrypt();




            JSONArray array = new JSONArray();
            array.put(json);
            array.put(json2);

            System.out.println(array.toString());



            JSONObject objects = new JSONObject();
            objects.put("categories", array);
            System.out.println(objects.toString());


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

            coordinator.addRequests(RestMethods.addSubCategory(0, coordinator, objects));
            coordinator.start();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
