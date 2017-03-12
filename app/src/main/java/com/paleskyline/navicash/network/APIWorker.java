package com.paleskyline.navicash.network;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.paleskyline.navicash.crypto.AuthManager;
import com.paleskyline.navicash.crypto.KeyPackage;
import com.paleskyline.navicash.crypto.SecuredJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

/**
 * Created by oscar on 4/03/17.
 */

public class APIWorker {

    private String baseURL = "http://192.168.1.21:5000/api/";
    private RequestQueue requestQueue;
    private static APIWorker instance = null;
    /*
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.out.println("ERROR!!!");
        }
    };
    */

    public static APIWorker getInstance(Context context) {
        if (instance == null) {
            instance = new APIWorker(context);
        }
        return instance;
    }

    private APIWorker(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void registerAccount(String email, char[] password, KeyPackage keyPackage) {
        String url = baseURL + "register";

        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", String.copyValueOf(password));
            json.put("masterkey", keyPackage.getEncryptedMasterKey());
            json.put("salt", keyPackage.getSalt());
            json.put("nonce", keyPackage.getNonce());
            json.put("opslimit", keyPackage.getOpslimit());
            json.put("memlimit", keyPackage.getMemlimit());

            JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        JSONObject obj = parseVolleyException(error);
                        System.out.println(obj.toString());
                    }
                });

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getToken() {

        String url = baseURL + "token";
        String email = AuthManager.USERNAME;
        String password = String.copyValueOf(AuthManager.LOGINPASSWORD);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
             @Override
             public void onResponse(JSONObject response) {
                 System.out.println(response.toString());
                 // Need to parse response and set token in AuthManager
             }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
                // If error is because of incorrect login details then re-direct
                // user to login screen.
            }
        };

        /*

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.BASIC, email, password, null);

        requestQueue.add(restRequest);

        */
    }

    public void getEncryptionKey(String token) {

        String url = baseURL + "key";

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println(response.toString());
                try {
                    JSONArray obj = response.getJSONArray("key");
                    JSONObject arr = obj.getJSONObject(0);
                    KeyPackage keyPackage = new KeyPackage(
                            arr.getString("MasterKey"),
                            arr.getString("Nonce"),
                            arr.getString("Salt"),
                            arr.getInt("Opslimit"),
                            arr.getInt("Memlimit")
                    );
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("AN ERROR OCCURRED");
            }
        };
        /*
        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.TOKEN, null, null, token);

        requestQueue.add(restRequest);
        */

    }

    public void createGeneralCategory(SecuredJson sJson) {
        /*

        final String url = baseURL + "generalcategory";

        final JSONObject json = new JSONObject();
        try {
            json.put("category_details", sJson.getEncryptedJson());
            json.put("nonce", sJson.getNonce());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("WE'RE HERE!!!!!!!!!!!!!!");
                System.out.println(response.toString());
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RestRequest r2 = new RestRequest(Request.Method.POST, url, json,
                        responseListener, this, RestRequest.TOKEN, null, null, null);
                tokenRefresh(r2);

                //System.out.println("AN ERROR OCCURRED");
                //parseVolleyException(error);
                //error.printStackTrace();
                //System.out.println(parseVolleyException(error).toString());

                // Make request to get new token here and pass sJson as well
                // If the token request succeeds then it should call back to the
                // start of this method again to retry.


            }
        };

        RestRequest restRequest = new RestRequest(Request.Method.POST, url, json,
                responseListener, errorListener, RestRequest.TOKEN, null, null, AuthManager.TOKEN);

        requestQueue.add(restRequest);
        */

    }

    private JSONObject parseVolleyException(VolleyError error) {
        JSONObject obj = null;
        NetworkResponse response = error.networkResponse;
        //if (error instanceof ServerError && response != null) {
        if (response != null && response.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            // Expired or invalid token

            /*
            try {
                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "UTF-8"));
                obj = new JSONObject(res);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            */
        }


        return obj;
    }

    public void tokenRefresh(final RestRequest originalRequest) {
        System.out.println("REFRESH TEST");
        String url = baseURL + "token";
        String email = AuthManager.USERNAME;
        String password = String.copyValueOf(AuthManager.LOGINPASSWORD);

        Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String newToken = response.getString("token");
                    AuthManager.setToken(newToken);
                    //originalRequest.setToken(newToken);
                    System.out.println(newToken);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println("NEW TOKEN OBTAINED...RETRYING ORIGINAL REQUEST");

                requestQueue.add(originalRequest);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("REFRESH ERROR");
                // If error is because of incorrect login details then re-direct
                // user to login screen.
            }
        };

        /*

        RestRequest restRequest = new RestRequest(Request.Method.GET, url, null,
                responseListener, errorListener, RestRequest.BASIC, email, password, null);

        requestQueue.add(restRequest);
        */
    }
}
