package com.paleskyline.navicash.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by oscar on 4/03/17.
 */

public class KeyPackage implements Parcelable {

    private String encryptedMasterKey;
    private String nonce;
    private String salt;
    private int opslimit;
    private int memlimit;

    public KeyPackage(String encryptedMasterKey, String nonce, String salt, int opslimit, int memlimit) {

        this.encryptedMasterKey = encryptedMasterKey;
        this.nonce = nonce;
        this.salt = salt;
        this.opslimit = opslimit;
        this.memlimit = memlimit;

    }

    public KeyPackage(JSONObject json) throws JSONException {

        this.encryptedMasterKey = json.getString("dataKey");
        this.nonce = json.getString("nonce");
        this.salt = json.getString("salt");
        this.opslimit = json.getInt("opsLimit");
        this.memlimit = json.getInt("memLimit");

    }

    public String getEncryptedMasterKey() {
        return encryptedMasterKey;
    }

    public void setEncryptedMasterKey(String encryptedMasterKey) {
        this.encryptedMasterKey = encryptedMasterKey;
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

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("key", encryptedMasterKey);
            json.put("salt", salt);
            json.put("nonce", nonce);
            json.put("opslimit", opslimit);
            json.put("memlimit", memlimit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.encryptedMasterKey);
        dest.writeString(this.nonce);
        dest.writeString(this.salt);
        dest.writeInt(this.opslimit);
        dest.writeInt(this.memlimit);
    }

    protected KeyPackage(Parcel in) {
        this.encryptedMasterKey = in.readString();
        this.nonce = in.readString();
        this.salt = in.readString();
        this.opslimit = in.readInt();
        this.memlimit = in.readInt();
    }

    public static final Parcelable.Creator<KeyPackage> CREATOR = new Parcelable.Creator<KeyPackage>() {
        @Override
        public KeyPackage createFromParcel(Parcel source) {
            return new KeyPackage(source);
        }

        @Override
        public KeyPackage[] newArray(int size) {
            return new KeyPackage[size];
        }
    };

    @Override
    public String toString() {
        return "KeyPackage{" +
                "encryptedMasterKey='" + encryptedMasterKey + '\'' +
                ", nonce='" + nonce + '\'' +
                ", salt='" + salt + '\'' +
                ", opslimit=" + opslimit +
                ", memlimit=" + memlimit +
                '}';
    }
}
