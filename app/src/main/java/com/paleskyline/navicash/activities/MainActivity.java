package com.paleskyline.navicash.activities;

import android.os.AsyncTask;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static String tag = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //register();

        initKey();
        categoryLoader();
        //getGeneralCategories();
        //insertGeneralCategories();
        //insertSubCategories();
        //getSubCategories();



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

            ArrayList<JSONObject> dataReceiver = new ArrayList<>();
            RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag, dataReceiver) {
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


            ArrayList<JSONObject> dataReceiver = new ArrayList<>();
            RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag, dataReceiver) {
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
        SubCategory category = new SubCategory("Eating out", 3);
        SubCategory category2 = new SubCategory("Take away", 1);

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

            ArrayList<JSONObject> dataReceiver = new ArrayList<>();
            RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag, dataReceiver) {
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

    public void getGeneralCategories() {
        final ArrayList<JSONObject> dataReceiver = new ArrayList<>();
        final RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag, dataReceiver) {
            @Override
            protected void onSuccess() {
                ArrayList<GeneralCategory> categories = new ArrayList<>();
                JSONObject categoriesJson = dataReceiver.get(0);
                try {
                    JSONArray array = categoriesJson.getJSONArray("categories");
                    for (int i = 0; i < array.length(); i++) {
                        GeneralCategory category = new GeneralCategory(array.getJSONObject(i));
                        categories.add(category);
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("FAILED");
            }
        };

        coordinator.addRequests(RestMethods.getGeneralCategories(0, coordinator));
        coordinator.start();
    }

    public void getSubCategories() {
        final ArrayList<JSONObject> dataReceiver = new ArrayList<>();
        final RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag, dataReceiver) {
            @Override
            protected void onSuccess() {
                ArrayList<SubCategory> categories = new ArrayList<>();
                JSONObject categoriesJson = dataReceiver.get(0);
                System.out.println(categoriesJson.toString());
                try {
                    JSONArray array = categoriesJson.getJSONArray("categories");
                    for (int i = 0; i < array.length(); i++) {
                        SubCategory category = new SubCategory(array.getJSONObject(i));
                        categories.add(category);
                    }
                    for (SubCategory c : categories) {
                        System.out.println(c.toString());
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("FAILED");
            }
        };

        coordinator.addRequests(RestMethods.getSubCategories(0, coordinator));
        coordinator.start();
    }

    public void categoryLoader() {
        final ArrayList<JSONObject> dataReceiver = new ArrayList<>();
        final RequestCoordinator coordinator = new RequestCoordinator(this.getApplicationContext(), tag, dataReceiver) {
            @Override
            protected void onSuccess() {
                new DecryptionTask().execute(dataReceiver, null, null);
            }

            @Override
            protected void onFailure(JSONObject json) {
                System.out.println("FAILED");
            }
        };

        coordinator.addRequests(RestMethods.getGeneralCategories(0, coordinator), RestMethods.getSubCategories(1, coordinator));
        coordinator.start();
    }

    class DecryptionTask extends AsyncTask<ArrayList<JSONObject>, Void, Void> {

        private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
        private ArrayList<SubCategory> subCategories = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            System.out.println("ABOUT TO START JOB");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (GeneralCategory gc : generalCategories) {
                System.out.println(gc.toString());
            }
            System.out.println("\n");
            for (SubCategory sc : subCategories) {
                System.out.println(sc.toString());
            }
        }

        @Override
        protected Void doInBackground(ArrayList<JSONObject>... dataReceiver) {
            JSONObject generalCategoriesJson = dataReceiver[0].get(0);
            JSONObject subCategoriesJson = dataReceiver[0].get(1);
            try {
                JSONArray generalCategoriesArray = generalCategoriesJson.getJSONArray("categories");
                for (int i = 0; i < generalCategoriesArray.length(); i++) {
                    GeneralCategory category = new GeneralCategory(generalCategoriesArray.getJSONObject(i));
                    generalCategories.add(category);
                }
                JSONArray subCategoriesArray = subCategoriesJson.getJSONArray("categories");
                for (int j = 0; j < subCategoriesArray.length(); j++) {
                    SubCategory category = new SubCategory(subCategoriesArray.getJSONObject(j));
                    subCategories.add(category);
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
