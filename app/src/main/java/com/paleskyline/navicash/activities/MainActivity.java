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
import com.paleskyline.navicash.model.Transaction;
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


        register();

        //initKey();
        //categoryLoader();
        //getGeneralCategories();
        //insertGeneralCategories();
        //insertSubCategories();
        //getSubCategories();
        //insertTransaction();
        //defaultLoader();



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

            final JSONObject[] dataReceiver = new JSONObject[1];
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

            coordinator.addRequests(RestMethods.post(0, RestMethods.REGISTER, coordinator, json));
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


            final JSONObject[] dataReceiver = new JSONObject[1];
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

            coordinator.addRequests(RestMethods.post(0, RestMethods.GENERAL_CATEGORY, coordinator, objects));
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

            final JSONObject[] dataReceiver = new JSONObject[1];
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

            coordinator.addRequests(RestMethods.post(0, RestMethods.SUB_CATEGORY, coordinator, objects));
            coordinator.start();



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void insertTransaction() {

        Transaction t1 = new Transaction(1, "10/02/2017", 15.39, "test description");
        try {
            JSONObject json = t1.encrypt();
            System.out.println(json.toString());
            final JSONObject[] dataReceiver = new JSONObject[1];
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

            coordinator.addRequests(RestMethods.post(0, RestMethods.TRANSACTION, coordinator, json));
            coordinator.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*
    public void getGeneralCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
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

        coordinator.addRequests(RestMethods.get(0, RestMethods.GENERAL_CATEGORY, null, coordinator));
        coordinator.start();
    }


    public void getSubCategories() {
        final JSONObject[] dataReceiver = new JSONObject[1];
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

        coordinator.addRequests(RestMethods.get(0, RestMethods.SUB_CATEGORY, null, coordinator));
        coordinator.start();
    }
    */

    public void defaultLoader() {
        final JSONObject[] dataReceiver = new JSONObject[3];
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

        coordinator.addRequests(RestMethods.get(0, RestMethods.GENERAL_CATEGORY, null, coordinator),
                RestMethods.get(1, RestMethods.SUB_CATEGORY, null, coordinator),
                RestMethods.get(2, RestMethods.TRANSACTION_PARAM, "1", coordinator));
        coordinator.start();
    }

    class DecryptionTask extends AsyncTask<JSONObject[], Void, Void> {

        private ArrayList<GeneralCategory> generalCategories = new ArrayList<>();
        private ArrayList<SubCategory> subCategories = new ArrayList<>();
        private ArrayList<Transaction> transactions = new ArrayList<>();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            for (GeneralCategory gc : generalCategories) {
                System.out.println(gc.toString());
            }
            for (SubCategory sc : subCategories) {
                System.out.println(sc.toString());
            }
            for (Transaction t : transactions) {
                System.out.println(t.toString());
            }
        }

        @Override
        protected Void doInBackground(JSONObject[]... dataReceiver) {
            JSONObject generalCategoriesJson = dataReceiver[0][0];
            JSONObject subCategoriesJson = dataReceiver[0][1];
            JSONObject transactionsJson = dataReceiver[0][2];
            try {
                JSONArray generalCategoriesArray = generalCategoriesJson.getJSONArray("categories");
                for (int i = 0; i < generalCategoriesArray.length(); i++) {
                    GeneralCategory generalCategory = new GeneralCategory(generalCategoriesArray.getJSONObject(i));
                    generalCategories.add(generalCategory);
                }
                JSONArray subCategoriesArray = subCategoriesJson.getJSONArray("categories");
                for (int j = 0; j < subCategoriesArray.length(); j++) {
                    SubCategory subCategory = new SubCategory(subCategoriesArray.getJSONObject(j));
                    subCategories.add(subCategory);
                }
                JSONArray transactionsArray = transactionsJson.getJSONArray("transactions");
                for (int k = 0; k < transactionsArray.length(); k++) {
                    Transaction transaction = new Transaction(transactionsArray.getJSONObject(k));
                    transactions.add(transaction);
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
