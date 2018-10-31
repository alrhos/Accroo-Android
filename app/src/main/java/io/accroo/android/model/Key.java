package io.accroo.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscar on 4/03/17.
 */

public class Key implements Parcelable {

    @Expose private String encryptedKey;
    @Expose private String nonce;
    @Expose private String salt;
    @Expose private int algorithm;
    @Expose private int opslimit;
    @Expose private int memlimit;

    public Key(String encryptedKey, String nonce, String salt, int algorithm,
               int opslimit, int memlimit) {

        this.encryptedKey = encryptedKey;
        this.nonce = nonce;
        this.salt = salt;
        this.algorithm = algorithm;
        this.opslimit = opslimit;
        this.memlimit = memlimit;

    }

//    public Key(JSONObject json) throws JSONException {
//
//        this.encryptedKey = json.getString("key");
//        this.nonce = json.getString("nonce");
//        this.salt = json.getString("salt");
//        this.algorithm = json.getInt("algorithm");
//        this.opslimit = json.getInt("opslimit");
//        this.memlimit = json.getInt("memlimit");
//
//    }

    public String getEncryptedKey() {
        return encryptedKey;
    }

    public void setEncryptedKey(String encryptedKey) {
        this.encryptedKey = encryptedKey;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(int algorithm) {
        this.algorithm = algorithm;
    }

    public int getOpslimit() {
        return opslimit;
    }

    public void setOpslimit(int opslimit) {
        this.opslimit = opslimit;
    }

    public int getMemlimit() {
        return memlimit;
    }

    public void setMemlimit(int memlimit) {
        this.memlimit = memlimit;
    }

//    public JSONObject toJSON() {
//        JSONObject json = new JSONObject();
//        try {
//            json.put("key", encryptedKey);
//            json.put("nonce", nonce);
//            json.put("salt", salt);
//            json.put("algorithm", algorithm);
//            json.put("opslimit", opslimit);
//            json.put("memlimit", memlimit);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return json;
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.encryptedKey);
        dest.writeString(this.nonce);
        dest.writeString(this.salt);
        dest.writeInt(this.algorithm);
        dest.writeInt(this.opslimit);
        dest.writeInt(this.memlimit);
    }

    protected Key(Parcel in) {
        this.encryptedKey = in.readString();
        this.nonce = in.readString();
        this.salt = in.readString();
        this.algorithm = in.readInt();
        this.opslimit = in.readInt();
        this.memlimit = in.readInt();
    }

    public static final Parcelable.Creator<Key> CREATOR = new Parcelable.Creator<Key>() {
        @Override
        public Key createFromParcel(Parcel source) {
            return new Key(source);
        }

        @Override
        public Key[] newArray(int size) {
            return new Key[size];
        }
    };

}
